package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPatternPreset;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 内置/资源包提供的红绿灯相位分配预设加载器，扫描方式与 FlagLoader 一致：
 * 遍历所有命名空间查找 traffic_lights_yunbeiuc.json 并合并加载。
 * JSON 格式为 {分类名: {预设名: {预设数据}, ...}, ...}，一个资源包可以同时内置多个
 * 分类（二级菜单）。这里加载出的预设视为"内置预设"，不允许在编辑器界面删除；
 * 加载出的分类视为"内置分类"，不允许在分类界面删除。
 */
public class TrafficLightsPatternPresetLoader {
    private static final Map<String, TrafficLightsPatternPreset> BUILT_IN_PRESETS = new LinkedHashMap<>();
    private static final Map<String, List<String>> BUILT_IN_CATEGORY_PRESET_NAMES = new LinkedHashMap<>();
    // 分类自身的显示颜色：JSON 中分类对象内的 "color" 键（与预设条目同级，键名 "color" 不作为预设名解析）
    private static final Map<String, Integer> BUILT_IN_CATEGORY_COLORS = new LinkedHashMap<>();

    public static void loadPresets(ResourceManager resourceManager) {
        BUILT_IN_PRESETS.clear();
        BUILT_IN_CATEGORY_PRESET_NAMES.clear();
        BUILT_IN_CATEGORY_COLORS.clear();

        try {
            // 遍历所有命名空间，直接查找 traffic_lights_yunbeiuc.json
            for (String namespace : resourceManager.getAllNamespaces()) {
                Identifier fileId = new Identifier(namespace, "traffic_lights_yunbeiuc.json");

                resourceManager.getResource(fileId).ifPresent(resource -> {
                    try (InputStream stream = resource.getInputStream();
                         InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        int count = 0;
                        for (String categoryName : json.keySet()) {
                            JsonObject categoryObj = json.getAsJsonObject(categoryName);
                            if (categoryObj.has("color") && categoryObj.get("color").isJsonPrimitive()) {
                                BUILT_IN_CATEGORY_COLORS.put(categoryName, categoryObj.get("color").getAsInt());
                            }
                            List<String> names = BUILT_IN_CATEGORY_PRESET_NAMES.computeIfAbsent(categoryName, k -> new ArrayList<>());
                            for (String name : categoryObj.keySet()) {
                                // "color" 是分类自身的颜色字段，不是预设名
                                if ("color".equals(name)) continue;
                                JsonObject d = categoryObj.getAsJsonObject(name);
                                TrafficLightsPatternPreset preset = parsePreset(name, categoryName, d);
                                BUILT_IN_PRESETS.put(name, preset);
                                names.add(name);
                                count++;
                            }
                        }

                        System.out.println("加载内置红绿灯相位预设: 命名空间 " + namespace + " | 共 " + count + " 个");
                    } catch (Exception e) {
                        System.err.println("加载红绿灯相位预设文件失败 [" + fileId + "]: " + e.getMessage());
                    }
                });
            }

            System.out.println("红绿灯相位预设加载完成，共 " + BUILT_IN_PRESETS.size() + " 个内置预设");

        } catch (Exception e) {
            System.err.println("红绿灯相位预设加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static TrafficLightsPatternPreset parsePreset(String name, String categoryName, JsonObject d) {
        TrafficLightsPatternPreset preset = new TrafficLightsPatternPreset();
        preset.setName(name);
        preset.setPhaseCount(d.has("phaseCount") ? d.get("phaseCount").getAsInt() : 4);
        preset.setColor(d.has("color") ? d.get("color").getAsInt() : -1);
        // 资源包内置预设的分类由 JSON 中所在的外层键决定
        preset.setCategory(categoryName);

        List<TrafficLightsPatternPreset.Slot> slots = new ArrayList<>();
        if (d.has("slots") && d.get("slots").isJsonArray()) {
            for (JsonElement elem : d.getAsJsonArray("slots")) {
                JsonObject s = elem.getAsJsonObject();
                TrafficLightsPatternPreset.Slot slot = new TrafficLightsPatternPreset.Slot();
                try {
                    slot.setDirection(TrafficLightsPatternPreset.Direction8.valueOf(s.has("direction") ? s.get("direction").getAsString() : "N"));
                } catch (IllegalArgumentException e) {
                    slot.setDirection(TrafficLightsPatternPreset.Direction8.N);
                }
                try {
                    slot.setKind(TrafficLightsPatternPreset.MemberKind.valueOf(s.has("kind") ? s.get("kind").getAsString() : "NORMAL"));
                } catch (IllegalArgumentException e) {
                    slot.setKind(TrafficLightsPatternPreset.MemberKind.NORMAL);
                }
                slot.setDirectionType(TrafficLightsBlockEntity.DirectionType.fromName(
                        s.has("directionType") ? s.get("directionType").getAsString() : "straight"));
                List<Integer> phaseIndices = new ArrayList<>();
                if (s.has("phaseIndices") && s.get("phaseIndices").isJsonArray()) {
                    for (JsonElement pi : s.getAsJsonArray("phaseIndices")) {
                        phaseIndices.add(pi.getAsInt());
                    }
                }
                if (phaseIndices.isEmpty()) phaseIndices.add(0);
                slot.setPhaseIndices(phaseIndices);
                slot.setOrder(s.has("order") ? s.get("order").getAsInt() : 1);
                slots.add(slot);
            }
        }
        preset.setSlots(slots);
        return preset;
    }

    public static Map<String, TrafficLightsPatternPreset> getPresets() {
        return Collections.unmodifiableMap(BUILT_IN_PRESETS);
    }

    public static boolean isBuiltIn(String name) {
        return BUILT_IN_PRESETS.containsKey(name);
    }

    /**
     * 获取资源包中定义的所有内置分类名称（从 JSON 第一层键读取）。
     */
    public static List<String> getBuiltInCategoryNames() {
        return new ArrayList<>(BUILT_IN_CATEGORY_PRESET_NAMES.keySet());
    }

    /**
     * 检查给定名称是否为资源包中定义的内置分类。
     */
    public static boolean isBuiltInCategory(String categoryName) {
        return BUILT_IN_CATEGORY_PRESET_NAMES.containsKey(categoryName);
    }

    /**
     * 获取资源包内置分类在 JSON 中显式指定的颜色。
     *
     * @return 显式颜色（0xRRGGBB），未指定时返回 -1
     */
    public static int getBuiltInCategoryColor(String categoryName) {
        Integer color = BUILT_IN_CATEGORY_COLORS.get(categoryName);
        return color != null ? color : -1;
    }
}
