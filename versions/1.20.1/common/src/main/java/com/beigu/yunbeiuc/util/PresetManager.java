package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class PresetManager {
    private static final Path PRESET_FILE = FabricLoader.getInstance().getGameDir().resolve("yunbeiuc_presets.json");
    private static Map<String, List<TextLineData>> presets = new LinkedHashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Map<String, List<TextLineData>> getPresets() { return presets; }

    public static void addPreset(String name, List<TextLineData> lines) {
        presets.put(name, lines);
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
            for (String key : obj.keySet()) {
                JsonArray arr = obj.getAsJsonArray(key);
                List<TextLineData> lines = new ArrayList<>();
                for (JsonElement elem : arr) {
                    JsonObject d = elem.getAsJsonObject();
                    TextLineData data = new TextLineData(d.get("text").getAsString());
                    data.setXOffset(d.get("xOffset").getAsFloat());
                    data.setYOffset(d.get("yOffset").getAsFloat());
                    data.setZOffset(d.get("zOffset").getAsFloat());
                    data.setColor(d.get("color").getAsInt());
                    data.setBold(d.get("bold").getAsBoolean());
                    data.setItalic(d.get("italic").getAsBoolean());
                    data.setUnderline(d.get("underline").getAsBoolean());
                    data.setShadow(d.get("shadow").getAsBoolean());
                    data.setFontSize(d.get("fontSize").getAsFloat());
                    try {
                        data.setAlignment(CustomSignBlockEntity.TextAlignment.valueOf(d.get("alignment").getAsString()));
                    } catch (IllegalArgumentException e) {
                        data.setAlignment(CustomSignBlockEntity.TextAlignment.CENTER_CENTER);
                    }
                    lines.add(data);
                }
                presets.put(key, lines);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        JsonObject obj = new JsonObject();
        for (var entry : presets.entrySet()) {
            JsonArray arr = new JsonArray();
            for (TextLineData data : entry.getValue()) {
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
            obj.add(entry.getKey(), arr);
        }
        try (Writer writer = new FileWriter(PRESET_FILE.toFile())) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}