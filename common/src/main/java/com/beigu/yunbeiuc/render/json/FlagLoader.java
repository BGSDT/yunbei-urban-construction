package com.beigu.yunbeiuc.render.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FlagLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, CustomFlag> CUSTOM_FLAGS = new LinkedHashMap<>();

    public static void loadFlags(ResourceManager resourceManager) {
        CUSTOM_FLAGS.clear();

        try {
            // 遍历所有命名空间，直接查找 flags_yunbeiuc.json
            for (String namespace : resourceManager.getAllNamespaces()) {
                Identifier fileId = new Identifier(namespace, "flags_yunbeiuc.json");

                resourceManager.getResource(fileId).ifPresent(resource -> {
                    try (InputStream stream = resource.getInputStream();
                         InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        JsonObject customFlags = json.getAsJsonObject("custom_flags");

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
                                texture = new Identifier(namespace, imagePath);
                            }

                            CustomFlag flag = new CustomFlag(flagId, name, texture, color);
                            CUSTOM_FLAGS.put(flagId, flag);

                            System.out.println("加载旗帜: " + flagId + " | 命名空间: " + namespace + " | 纹理: " + texture);
                        }
                    } catch (Exception e) {
                        System.err.println("加载旗帜文件失败 [" + fileId + "]: " + e.getMessage());
                    }
                });
            }

            System.out.println("旗帜加载完成，共 " + CUSTOM_FLAGS.size() + " 个旗帜");

        } catch (Exception e) {
            System.err.println("旗帜加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 下面的方法完全不用改
    public static Map<String, CustomFlag> getCustomFlags() {
        return Collections.unmodifiableMap(CUSTOM_FLAGS);
    }

    public static CustomFlag getFlag(String id) {
        return CUSTOM_FLAGS.get(id);
    }

    public static List<CustomFlag> getFlagsInOrder() {
        return new ArrayList<>(CUSTOM_FLAGS.values());
    }
}