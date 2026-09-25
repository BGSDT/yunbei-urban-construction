package com.beigu.yunbeiuc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 全局字体设置：新放置标识方块的默认行使用「原版字体」还是「ABC 交通字体」。
 *
 * - 持久化到 <游戏目录>/config/yunbeiuc_sign_font.json（跨会话、跨存档生效）；
 * - 供 SignTextLinesHelper 生成默认文本行时查询（单机集成服务器与客户端同进程，可读同一静态值）；
 * - dedicated server 无客户端实例：读取失败或路径不可用一律回退「原版」默认，不影响放置/渲染流程。
 *
 * 注意：本类不标注 @Environment(CLIENT)，因为 SignTextLinesHelper 可能在服务端构造方块实体时调用，
 * 仅靠 MinecraftClient.getInstance() 的 null 兜底保证 dedicated server 安全。
 */
public final class GlobalFontSettings {

    private static final String FILE_NAME = "yunbeiuc_sign_font.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** null = 尚未从文件加载；加载失败亦置 false（回退原版） */
    private static Boolean abcMode = null;

    private GlobalFontSettings() {}

    /** 是否使用 ABC 交通字体作为新放置方块的默认（未填 abcFont 的行兜底 A 型） */
    public static boolean isAbcMode() {
        if (abcMode == null) loadFromFile();
        return Boolean.TRUE.equals(abcMode);
    }

    /** 由 UI 写入：更新内存值并立即落盘 */
    public static void setAbcMode(boolean value) {
        abcMode = value;
        saveToFile();
    }

    private static void loadFromFile() {
        abcMode = false;
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return; // dedicated server：保持原版默认
            Path path = configPath(client);
            if (Files.exists(path)) {
                JsonObject obj = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), JsonObject.class);
                if (obj != null && obj.has("abcMode")) {
                    abcMode = obj.get("abcMode").getAsBoolean();
                }
            }
        } catch (Exception ignored) {
            // 读取失败回退原版默认，不中断渲染/放置流程
            abcMode = false;
        }
    }

    private static void saveToFile() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;
            Path path = configPath(client);
            Files.createDirectories(path.getParent());
            JsonObject obj = new JsonObject();
            obj.addProperty("abcMode", Boolean.TRUE.equals(abcMode));
            Files.writeString(path, GSON.toJson(obj), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            // 写失败静默：本次运行内存值仍生效
        }
    }

    private static Path configPath(MinecraftClient client) {
        return client.runDirectory.toPath().resolve("config").resolve(FILE_NAME);
    }
}
