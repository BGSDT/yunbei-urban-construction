package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.google.gson.*;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

/**
 * 告示牌文本预设管理。
 *
 * <p>预设来源有两处，合并后统一由 {@link #getPresets()} 提供：
 * <ol>
 *   <li><b>资源包</b>：{@code assets/<命名空间>/ui_definitions/cf_<modid>.json} 中的 {@code "presets"} 块
 *       （与图案/字体共用同一目录与接口；读取的三个文件名见
 *       {@link PatternResourceLoader#CUSTOM_UI_FILES}）；</li>
 *   <li><b>用户</b>：游戏目录下的 {@code yunbeiuc_presets.json}（用户新建/删除，持久化到磁盘）。</li>
 * </ol>
 * 同名时用户预设覆盖资源包预设。
 *
 * <p>资源包预设 JSON 结构（与 {@code yunbeiuc_presets.json} 的文本行一致）：
 * <pre>
 * {
 *   "presets": {
 *     "我的预设": [ { "text": "...", "xOffset": 0, "yOffset": 0, "zOffset": 0,
 *                     "color": 16777215, "bold": false, "italic": false, "underline": false,
 *                     "shadow": false, "fontSize": 1.0, "alignment": "CENTER_CENTER" } ]
 *   }
 * }
 * </pre>
 */
public class PresetManager {
    private static final Path PRESET_FILE = Platform.getGameFolder().resolve("yunbeiuc_presets.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String LOG_TAG = "[PresetManager]";

    /** 资源包提供的预设（只读，随资源包重载刷新）。 */
    private static final Map<String, List<TextLineData>> packPresets = new LinkedHashMap<>();
    /** 用户预设（持久化到游戏目录文件）。 */
    private static final Map<String, List<TextLineData>> userPresets = new LinkedHashMap<>();
    /** 合并视图：资源包预设 + 用户预设（用户优先）。 */
    private static final Map<String, List<TextLineData>> presets = new LinkedHashMap<>();

    public static Map<String, List<TextLineData>> getPresets() { return presets; }

    /** 是否为资源包提供的预设（不会随用户删除而写入磁盘）。 */
    public static boolean isPackPreset(String name) { return packPresets.containsKey(name); }

    public static void addPreset(String name, List<TextLineData> lines) {
        userPresets.put(name, lines);
        rebuild();
        save();
    }

    public static void removePreset(String name) {
        boolean changed = userPresets.remove(name) != null;
        // 资源包预设允许在本次会话中移除（不写盘，重载资源包后恢复）
        packPresets.remove(name);
        rebuild();
        if (changed) save();
    }

    // ==================== 用户预设（游戏目录文件） ====================

    public static void load() {
        userPresets.clear();
        File file = PRESET_FILE.toFile();
        if (!file.exists()) {
            rebuild();
            return;
        }
        try (Reader reader = new FileReader(file)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            if (obj != null) {
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    if (!entry.getValue().isJsonArray()) continue;
                    userPresets.put(entry.getKey(), parseLines(entry.getValue().getAsJsonArray()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rebuild();
    }

    public static void save() {
        JsonObject obj = new JsonObject();
        for (Map.Entry<String, List<TextLineData>> entry : userPresets.entrySet()) {
            obj.add(entry.getKey(), linesToJson(entry.getValue()));
        }
        try (Writer writer = new FileWriter(PRESET_FILE.toFile())) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==================== 资源包预设（cf_<modid>.json / patterns.json 的 presets 块） ====================

    /**
     * 从所有命名空间下的 {@code ui_definitions/cf_<modid>.json}（兼容 {@code patterns.json}）
     * 读取 {@code presets} 块并合并。
     * 读取的文件名与 {@link PatternResourceLoader#CUSTOM_UI_FILES} 保持一致。
     *
     * @param manager 资源管理器
     */
    public static void loadFromResourcePacks(ResourceManager manager) {
        packPresets.clear();
        try {
            List<ResourceLocation> files = VersionServices.resources().listResources(manager,
                    PatternResourceLoader.CUSTOM_UI_DIR,
                    id -> PatternResourceLoader.isCustomUiFile(id.getPath()));
            for (ResourceLocation id : files) {
                try (InputStream in = VersionServices.resources().openIfPresent(manager, id)) {
                    if (in == null) continue;
                    try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                        JsonElement root = new JsonParser().parse(reader);
                        if (!root.isJsonObject()) continue;
                        JsonObject obj = root.getAsJsonObject();
                        if (!obj.has("presets") || !obj.get("presets").isJsonObject()) continue;
                        JsonObject presetsObj = obj.getAsJsonObject("presets");
                        for (Map.Entry<String, JsonElement> entry : presetsObj.entrySet()) {
                            if (!entry.getValue().isJsonArray()) continue;
                            packPresets.put(entry.getKey(), parseLines(entry.getValue().getAsJsonArray()));
                        }
                    }
                } catch (Exception inner) {
                    System.err.println(LOG_TAG + " 解析资源包预设失败: " + id + " | " + inner.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println(LOG_TAG + " 读取资源包预设失败: " + e.getMessage());
        }
        rebuild();
    }

    // ==================== 内部 ====================

    private static void rebuild() {
        presets.clear();
        presets.putAll(packPresets);
        presets.putAll(userPresets);
    }

    private static List<TextLineData> parseLines(JsonArray arr) {
        List<TextLineData> lines = new ArrayList<>();
        for (JsonElement elem : arr) {
            if (!elem.isJsonObject()) continue;
            JsonObject d = elem.getAsJsonObject();
            TextLineData data = new TextLineData(d.has("text") ? d.get("text").getAsString() : "");
            data.setXOffset(getFloat(d, "xOffset", 0f));
            data.setYOffset(getFloat(d, "yOffset", 0f));
            data.setZOffset(getFloat(d, "zOffset", 0f));
            data.setColor(getInt(d, "color", 0xFFFFFF));
            data.setBold(getBool(d, "bold", false));
            data.setItalic(getBool(d, "italic", false));
            data.setUnderline(getBool(d, "underline", false));
            data.setShadow(getBool(d, "shadow", false));
            data.setFontSize(getFloat(d, "fontSize", 1f));
            try {
                data.setAlignment(CustomSignBlockEntity.TextAlignment.valueOf(
                        d.has("alignment") ? d.get("alignment").getAsString() : "CENTER_CENTER"));
            } catch (IllegalArgumentException e) {
                data.setAlignment(CustomSignBlockEntity.TextAlignment.CENTER_CENTER);
            }
            lines.add(data);
        }
        return lines;
    }

    private static JsonArray linesToJson(List<TextLineData> lines) {
        JsonArray arr = new JsonArray();
        for (TextLineData data : lines) {
            JsonObject d = new JsonObject();
            d.addProperty("text", data.getText());
            d.addProperty("xOffset", data.getXOffset());
            d.addProperty("yOffset", data.getYOffset());
            d.addProperty("zOffset", data.getZOffset());
            d.addProperty("color", data.getColor());
            d.addProperty("bold", data.isBold());
            d.addProperty("italic", data.isItalic());
            d.addProperty("underline", data.isUnderline());
            d.addProperty("shadow", data.isShadow());
            d.addProperty("fontSize", data.getFontSize());
            d.addProperty("alignment", data.getAlignment().name());
            arr.add(d);
        }
        return arr;
    }

    private static float getFloat(JsonObject o, String k, float def) {
        try { return o.has(k) ? o.get(k).getAsFloat() : def; } catch (Exception e) { return def; }
    }

    private static int getInt(JsonObject o, String k, int def) {
        try { return o.has(k) ? o.get(k).getAsInt() : def; } catch (Exception e) { return def; }
    }

    private static boolean getBool(JsonObject o, String k, boolean def) {
        try { return o.has(k) ? o.get(k).getAsBoolean() : def; } catch (Exception e) { return def; }
    }
}
