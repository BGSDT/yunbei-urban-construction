package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPatternPreset;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.Gson;
import dev.architectury.platform.Platform;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 相位预设分类的持久化：yunbeiuc_pattern_categories.json 只存用户新建分类的 名称->颜色。
// 默认分类与资源包内置分类始终存在、不可删除、不写入文件。

public class TrafficLightsPatternCategoryManager {
    public static final String DEFAULT_CATEGORY = "默认分类";

    private static final Path CATEGORY_FILE = Platform.getGameFolder().resolve("yunbeiuc_pattern_categories.json");
    private static final Map<String, Integer> userCategories = new LinkedHashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean isFixedCategory(String name) {
        return DEFAULT_CATEGORY.equals(name) || TrafficLightsPatternPresetLoader.isBuiltInCategory(name);
    }

    public static List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        names.addAll(TrafficLightsPatternPresetLoader.getBuiltInCategoryNames());
        names.add(DEFAULT_CATEGORY);
        names.addAll(userCategories.keySet());
        return names;
    }

    public static int getColor(String name) {
        int builtIn = TrafficLightsPatternPresetLoader.getBuiltInCategoryColor(name);
        if (builtIn != -1) {
            return builtIn & 0xFFFFFF;
        }
        Integer explicit = userCategories.get(name);
        if (explicit != null && explicit != -1) {
            return explicit & 0xFFFFFF;
        }
        return TrafficLightsPatternPreset.hashColor(name);
    }

    public static String addCategory(String name, int color) {
        if (name == null || name.trim().isEmpty()) {
            return "分类名称不能为空！";
        }
        String trimmed = name.trim();
        if (isFixedCategory(trimmed) || userCategories.containsKey(trimmed)) {
            return "已存在同名分类！";
        }
        userCategories.put(trimmed, color);
        save();
        return null;
    }

    public static boolean canDelete(String name) {
        if (isFixedCategory(name) || !userCategories.containsKey(name)) {
            return false;
        }
        for (TrafficLightsPatternPreset preset : TrafficLightsPatternPresetManager.getPresets().values()) {
            if (name.equals(preset.getCategory())) {
                return false;
            }
        }
        return true;
    }

    public static void removeCategory(String name) {
        if (!canDelete(name)) {
            return;
        }
        userCategories.remove(name);
        save();
    }

    public static void load() {
        userCategories.clear();
        File file = CATEGORY_FILE.toFile();
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            if (obj == null) return;
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String key = entry.getKey();
                if (isFixedCategory(key)) continue;
                userCategories.put(key, obj.get(key).getAsInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        JsonObject obj = new JsonObject();
        for (Map.Entry<String, Integer> entry : userCategories.entrySet()) {
            obj.addProperty(entry.getKey(), entry.getValue());
        }
        try (Writer writer = new FileWriter(CATEGORY_FILE.toFile())) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
