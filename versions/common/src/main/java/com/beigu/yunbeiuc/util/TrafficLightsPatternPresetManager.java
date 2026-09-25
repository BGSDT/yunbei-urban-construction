package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPatternPreset;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import dev.architectury.platform.Platform;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 红绿灯相位分配预设的 JSON 存储管理。
 * 文件位于游戏目录：yunbeiuc_pattern_presets.json。
 * 预设随客户端保存（单人即本存档目录），应用时把预设完整数据通过
 * C2S 包发给服务端执行，服务端无需持久化。
 */
public class TrafficLightsPatternPresetManager {
    private static final Path PRESET_FILE = Platform.getGameFolder().resolve("yunbeiuc_pattern_presets.json");
    private static final Map<String, TrafficLightsPatternPreset> presets = new LinkedHashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Map<String, TrafficLightsPatternPreset> getPresets() {
        return presets;
    }

    public static void addPreset(TrafficLightsPatternPreset preset) {
        presets.put(preset.getName(), preset);
        save();
    }

    public static void removePreset(String name) {
        presets.remove(name);
        save();
    }

    public static void load() {
        presets.clear();
        File file = PRESET_FILE.toFile();
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            if (obj == null) return;
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonObject d = obj.getAsJsonObject(key);
                TrafficLightsPatternPreset preset = new TrafficLightsPatternPreset();
                preset.setName(key);
                preset.setPhaseCount(d.has("phaseCount") ? d.get("phaseCount").getAsInt() : 4);
                preset.setColor(d.has("color") ? d.get("color").getAsInt() : -1);
                // 旧存档缺失分类字段时归入"默认分类"，兼容二级菜单功能上线前保存的预设
                preset.setCategory(d.has("category") ? d.get("category").getAsString() : TrafficLightsPatternCategoryManager.DEFAULT_CATEGORY);
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
                presets.put(key, preset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        JsonObject obj = new JsonObject();
        for (var entry : presets.entrySet()) {
            TrafficLightsPatternPreset preset = entry.getValue();
            JsonObject d = new JsonObject();
            d.addProperty("phaseCount", preset.getPhaseCount());
            if (preset.getColor() != -1) {
                d.addProperty("color", preset.getColor());
            }
            d.addProperty("category", preset.getCategory());
            JsonArray arr = new JsonArray();
            for (TrafficLightsPatternPreset.Slot slot : preset.getSlots()) {
                JsonObject s = new JsonObject();
                s.addProperty("direction", slot.getDirection().name());
                s.addProperty("kind", slot.getKind().name());
                s.addProperty("directionType", slot.getDirectionType().getName());
                JsonArray pi = new JsonArray();
                for (Integer index : slot.getPhaseIndices()) {
                    pi.add(index);
                }
                s.add("phaseIndices", pi);
                s.addProperty("order", slot.getOrder());
                arr.add(s);
            }
            d.add("slots", arr);
            obj.add(entry.getKey(), d);
        }
        try (Writer writer = new FileWriter(PRESET_FILE.toFile())) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
