package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPatternPreset;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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

/**
 * 相位预设"二级菜单"（分类）的持久化管理。
 * 文件位于游戏目录：yunbeiuc_pattern_categories.json，只存用户新建分类的 名称->颜色。
 * 一个固定分类（默认分类）始终存在、不写入文件、不可删除：
 * - DEFAULT_CATEGORY：承载旧存档中没有分类字段、或用户未指定分类时归入的预设。
 * 资源包内置分类（通过 {@link TrafficLightsPatternPresetLoader#getBuiltInCategoryNames()} 获取）
 * 同样不可删除、不持久化，但数量和名称由资源包 JSON 动态决定。
 */
public class TrafficLightsPatternCategoryManager {
    public static final String DEFAULT_CATEGORY = "默认分类";

    private static final Path CATEGORY_FILE = Platform.getGameFolder().resolve("yunbeiuc_pattern_categories.json");
    // 用户新建分类：名称 -> 显式颜色（-1 表示未设置，展示时按名称哈希派生）
    private static final Map<String, Integer> userCategories = new LinkedHashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 是否为固定分类或资源包内置分类（始终存在，不可删除，不持久化）。
     */
    public static boolean isFixedCategory(String name) {
        return DEFAULT_CATEGORY.equals(name) || TrafficLightsPatternPresetLoader.isBuiltInCategory(name);
    }

    /**
     * 全部分类名称：资源包内置分类在前，固定分类"默认分类"次之，用户新建分类按创建顺序在后。
     */
    public static List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        names.addAll(TrafficLightsPatternPresetLoader.getBuiltInCategoryNames());
        names.add(DEFAULT_CATEGORY);
        names.addAll(userCategories.keySet());
        return names;
    }

    /**
     * 分类在列表中展示的颜色：资源包内置分类若在 JSON 中显式指定 "color" 则优先使用；
     * 用户分类已显式设置则直接返回；否则（含"默认分类"）按名称哈希派生固定的伪随机颜色。
     */
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

    /**
     * 新建分类。名称不可为空、不可与已有分类（含固定分类）重名。
     *
     * @return null 表示新建成功；非 null 时为失败原因
     */
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

    /**
     * 用户分类可删除的条件：不是固定分类或资源包内置分类，且该分类下已没有任何用户预设。
     */
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
            for (String key : obj.keySet()) {
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
