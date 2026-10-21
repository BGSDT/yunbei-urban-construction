package com.beigu.yunbeiuc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GlobalFontSettings {

    private static final String FILE_NAME = "yunbeiuc_sign_font.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public enum FontMode {
        VANILLA("vanilla"),
        ABC_A("abc_a"),
        ABC_B("abc_b"),
        ABC_C("abc_c"),
        ADAPTIVE("adaptive");

        public final String key;

        FontMode(String key) {
            this.key = key;
        }
    }

    private static FontMode mode = null;

    private GlobalFontSettings() {}

    public static FontMode getMode() {
        if (mode == null) loadFromFile();
        return mode;
    }

    public static void setMode(FontMode value) {
        mode = value == null ? FontMode.VANILLA : value;
        saveToFile();
    }

    public static boolean isAbcMode() {
        return getMode() != FontMode.VANILLA;
    }

    public static boolean isAdaptive() {
        return getMode() == FontMode.ADAPTIVE;
    }

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
            Path path = configPath();
            if (path == null) return;
            if (Files.exists(path)) {
                JsonObject obj = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), JsonObject.class);
                if (obj != null && obj.has("fontMode")) {
                    mode = fromKey(obj.get("fontMode").getAsString());
                } else if (obj != null && obj.has("abcMode")) {
                    mode = obj.get("abcMode").getAsBoolean() ? FontMode.ABC_A : FontMode.VANILLA;
                }
            }
        } catch (Exception | LinkageError ignored) {
            mode = FontMode.VANILLA;
        }
    }

    private static void saveToFile() {
        try {
            Path path = configPath();
            if (path == null) return;
            Files.createDirectories(path.getParent());
            JsonObject obj = new JsonObject();
            obj.addProperty("fontMode", getMode().key);
            Files.writeString(path, GSON.toJson(obj), StandardCharsets.UTF_8);
        } catch (Exception | LinkageError ignored) {
        }
    }

    private static Path configPath() {
        try {
            Path gameFolder = Platform.getGameFolder();
            return gameFolder == null ? null : gameFolder.resolve("config").resolve(FILE_NAME);
        } catch (Exception | LinkageError e) {
            return null;
        }
    }
}
