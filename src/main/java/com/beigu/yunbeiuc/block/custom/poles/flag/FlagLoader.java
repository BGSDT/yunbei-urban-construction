package com.beigu.yunbeiuc.block.custom.poles.flag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class FlagLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // 使用 LinkedHashMap 保持插入顺序
    private static final Map<String, CustomFlag> CUSTOM_FLAGS = new LinkedHashMap<>();

    public static void loadFlags(ResourceManager resourceManager) {
        CUSTOM_FLAGS.clear();

        try {
            Identifier flagFile = new Identifier("yunbeiuc", "flags_yunbeiuc.json");

            // 修复：使用正确的参数名 resourceManager
            for (Resource resource : resourceManager.getAllResources(flagFile)) {
                try (InputStream stream = resource.getInputStream();
                     InputStreamReader reader = new InputStreamReader(stream)) {

                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    JsonObject customFlags = json.getAsJsonObject("custom_flags");

                    // 方法1：直接遍历，LinkedHashMap 会保持插入顺序
                    for (String flagId : customFlags.keySet()) {
                        JsonObject flagData = customFlags.getAsJsonObject(flagId);

                        String name = flagData.get("name").getAsString();
                        String imagePath = flagData.get("image").getAsString();
                        String colorHex = flagData.get("color").getAsString();

                        if (colorHex.startsWith("#")) {
                            colorHex = colorHex.substring(1);
                        }
                        int color = (int) Long.parseLong(colorHex, 16);

                        String[] imageParts = imagePath.split(":");
                        Identifier texture;
                        if (imageParts.length == 2) {
                            texture = new Identifier(imageParts[0], imageParts[1]);
                        } else {
                            texture = new Identifier("yunbeiuc", imagePath);
                        }

                        CustomFlag flag = new CustomFlag(flagId, name, texture, color);
                        CUSTOM_FLAGS.put(flagId, flag);

                        System.out.println("按JSON顺序加载旗帜: " + flagId + " - " + name);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom flags: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Map<String, CustomFlag> getCustomFlags() {
        return new LinkedHashMap<>(CUSTOM_FLAGS);
    }

    public static CustomFlag getFlag(String id) {
        return CUSTOM_FLAGS.get(id);
    }

    // 新增：获取保持顺序的旗帜列表
    public static List<CustomFlag> getFlagsInOrder() {
        return new ArrayList<>(CUSTOM_FLAGS.values());
    }
}