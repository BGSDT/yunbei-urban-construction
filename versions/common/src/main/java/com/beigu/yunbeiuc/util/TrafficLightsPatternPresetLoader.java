package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPatternPreset;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 内置/资源包预设加载器：遍历所有命名空间的 traffic_lights_yunbeiuc.json 并合并加载。
// 加载出的预设与分类都视为「内置」，不允许在界面里删除。

public class TrafficLightsPatternPresetLoader {
    private static final Map<String, TrafficLightsPatternPreset> BUILT_IN_PRESETS = new LinkedHashMap<>();
    private static final Map<String, List<String>> BUILT_IN_CATEGORY_PRESET_NAMES = new LinkedHashMap<>();
    private static final Map<String, Integer> BUILT_IN_CATEGORY_COLORS = new LinkedHashMap<>();

    public static void loadPresets(ResourceManager resourceManager) {
        BUILT_IN_PRESETS.clear();
        BUILT_IN_CATEGORY_PRESET_NAMES.clear();
        BUILT_IN_CATEGORY_COLORS.clear();

        try {
            for (String namespace : resourceManager.getNamespaces()) {
                ResourceLocation fileId = VersionServices.resources().create(namespace, "traffic_lights_yunbeiuc.json");

                try (InputStream stream = VersionServices.resources().openIfPresent(resourceManager, fileId)) {
                    if (stream == null) continue;
                    try (
                         InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                        JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
                        int count = 0;
                        for (Map.Entry<String, JsonElement> categoryEntry : json.entrySet()) {
                            String categoryName = categoryEntry.getKey();
                            JsonObject categoryObj = json.getAsJsonObject(categoryName);
                            if (categoryObj.has("color") && categoryObj.get("color").isJsonPrimitive()) {
                                BUILT_IN_CATEGORY_COLORS.put(categoryName, categoryObj.get("color").getAsInt());
                            }
                            List<String> names = BUILT_IN_CATEGORY_PRESET_NAMES.computeIfAbsent(categoryName, k -> new ArrayList<>());
                            for (Map.Entry<String, JsonElement> presetEntry : categoryObj.entrySet()) {
                                String name = presetEntry.getKey();
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
                }
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

    public static List<String> getBuiltInCategoryNames() {
        return new ArrayList<>(BUILT_IN_CATEGORY_PRESET_NAMES.keySet());
    }

    public static boolean isBuiltInCategory(String categoryName) {
        return BUILT_IN_CATEGORY_PRESET_NAMES.containsKey(categoryName);
    }

    public static int getBuiltInCategoryColor(String categoryName) {
        Integer color = BUILT_IN_CATEGORY_COLORS.get(categoryName);
        return color != null ? color : -1;
    }
}
