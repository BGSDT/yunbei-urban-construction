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

public class PresetManager {
    private static final Path PRESET_FILE = Platform.getGameFolder().resolve("yunbeiuc_presets.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String LOG_TAG = "[PresetManager]";

    private static final Map<String, List<TextLineData>> packPresets = new LinkedHashMap<>();
    private static final Map<String, List<TextLineData>> userPresets = new LinkedHashMap<>();
    private static final Map<String, List<TextLineData>> presets = new LinkedHashMap<>();

    public static Map<String, List<TextLineData>> getPresets() { return presets; }

    public static boolean isPackPreset(String name) { return packPresets.containsKey(name); }

    public static void addPreset(String name, List<TextLineData> lines) {
        userPresets.put(name, lines);
        rebuild();
        save();
    }

    public static void removePreset(String name) {
        boolean changed = userPresets.remove(name) != null;
        packPresets.remove(name);
        rebuild();
        if (changed) save();
    }

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
