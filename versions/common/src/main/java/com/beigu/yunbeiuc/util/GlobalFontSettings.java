package com.beigu.yunbeiuc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 全局字体设置（「云北路牌全局设置」页面的持久化数据）。
 *
 * <p>五种取值：
 * <ul>
 *   <li>{@link FontMode#VANILLA}：原版uniform，文本不包裹 -json；</li>
 *   <li>{@link FontMode#ABC_A}/{@link FontMode#ABC_B}/{@link FontMode#ABC_C}：强制使用 A/B/C 型交通字体；</li>
 *   <li>{@link FontMode#ADAPTIVE}：路牌自适应，按方块自带布局记录的字体选择。</li>
 * </ul>
 *
 * <p>仅对**新生成**的文本行有效（随行写入 NBT），已放置方块的文本行不受影响。
 */
public final class GlobalFontSettings {

    private static final String FILE_NAME = "yunbeiuc_sign_font.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public enum FontMode {
        /** 原版uniform：不包裹 -json */
        VANILLA("vanilla"),
        /** ABC 交通字体 A 型 */
        ABC_A("abc_a"),
        /** ABC 交通字体 B 型 */
        ABC_B("abc_b"),
        /** ABC 交通字体 C 型 */
        ABC_C("abc_c"),
        /** 路牌自适应：跟随方块自带布局的字体 */
        ADAPTIVE("adaptive");

        public final String key;

        FontMode(String key) {
            this.key = key;
        }
    }

    /** null = 尚未从文件加载；加载失败亦为 VANILLA（回退原版） */
    private static FontMode mode = null;

    private GlobalFontSettings() {}

    /** 当前全局字体模式 */
    public static FontMode getMode() {
        if (mode == null) loadFromFile();
        return mode;
    }

    /** 由 UI 写入：更新内存值并立即落盘 */
    public static void setMode(FontMode value) {
        mode = value == null ? FontMode.VANILLA : value;
        saveToFile();
    }

    /** 是否走 ABC 字体（含路牌自适应）：true 时新行会包 -json、取消加粗并下移 1/16 补偿基线 */
    public static boolean isAbcMode() {
        return getMode() != FontMode.VANILLA;
    }

    /** 是否按方块自带布局的字体自适应 */
    public static boolean isAdaptive() {
        return getMode() == FontMode.ADAPTIVE;
    }

    /** 非自适应模式强制使用的 ABC 字体标识（"a"/"b"/"c"）；原版uniform 与路牌自适应返回空串 */
    public static String forcedAbcFont() {
        return switch (getMode()) {
            case ABC_A -> "a";
            case ABC_B -> "b";
            case ABC_C -> "c";
            default -> "";
        };
    }

    private static FontMode fromKey(String key) {
        for (FontMode m : FontMode.values()) {
            if (m.key.equals(key)) return m;
        }
        return FontMode.VANILLA;
    }

    private static void loadFromFile() {
        mode = FontMode.VANILLA;
        try {
            Minecraft client = Minecraft.getInstance();
            if (client == null) return; // dedicated server：保持原版默认
            Path path = configPath(client);
            if (Files.exists(path)) {
                JsonObject obj = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), JsonObject.class);
                if (obj != null && obj.has("fontMode")) {
                    mode = fromKey(obj.get("fontMode").getAsString());
                } else if (obj != null && obj.has("abcMode")) {
                    // 兼容旧配置：abcMode=true 等价于 A 型字体
                    mode = obj.get("abcMode").getAsBoolean() ? FontMode.ABC_A : FontMode.VANILLA;
                }
            }
        } catch (Exception ignored) {
            // 读取失败回退原版默认，不中断渲染/放置流程
            mode = FontMode.VANILLA;
        }
    }

    private static void saveToFile() {
        try {
            Minecraft client = Minecraft.getInstance();
            if (client == null) return;
            Path path = configPath(client);
            Files.createDirectories(path.getParent());
            JsonObject obj = new JsonObject();
            obj.addProperty("fontMode", getMode().key);
            Files.writeString(path, GSON.toJson(obj), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            // 写失败静默：本次运行内存值仍生效
        }
    }

    private static Path configPath(Minecraft client) {
        return client.gameDirectory.toPath().resolve("config").resolve(FILE_NAME);
    }
}
