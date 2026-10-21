package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextAlignment;
import com.beigu.yunbeiuc.util.GlobalFontSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public final class SignTextLinesHelper {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private SignTextLinesHelper() {}

    public static TextLineData centered(String text, float andX, float andY, float scale, int color, String abcFont) {
        TextLineData line = base(text, andX, andY, scale, color, abcFont);
        line.setAlignment(TextAlignment.CENTER_CENTER);
        return line;
    }

    public static TextLineData left(String text, float andX, float andY, float scale, int color, String abcFont) {
        TextLineData line = base(text, andX, andY, scale, color, abcFont);
        line.setAlignment(TextAlignment.LEFT_CENTER);
        return line;
    }

    public static TextLineData right(String text, float andX, float andY, float scale, int color, String abcFont) {
        TextLineData line = base(text, andX, andY, scale, color, abcFont);
        line.setAlignment(TextAlignment.RIGHT_CENTER);
        return line;
    }

    public static TextLineData centeredWithZ(String text, float andX, float andY, float scale, int color, float zOffsetDelta, String abcFont) {
        TextLineData line = centered(text, andX, andY, scale, color, abcFont);
        line.setZOffset(zOffsetDelta * 16f);
        return line;
    }

    public static TextLineData logo(String texturePath, float andX, float andY, float size) {
        return logo(texturePath, andX, andY, size, 0f);
    }

    public static TextLineData logo(String texturePath, float andX, float andY, float size, float rotZ) {
        TextLineData line = new TextLineData("-texture " + texturePath);
        line.setXOffset(andX);
        line.setYOffset(andY);
        line.setZOffset(0);
        line.setRotZ(rotZ);
        line.setColor(0xFFFFFF);
        line.setAlignment(TextAlignment.CENTER_CENTER);
        line.setBold(false);
        line.setFontSize(size / 0.4f);
        line.setBuiltin(true);
        return line;
    }

    private static TextLineData base(String text, float andX, float andY, float scale, int color, String abcFont) {
        TextLineData line = new TextLineData(text);
        line.setXOffset(andX);
        line.setYOffset(andY);
        line.setZOffset(0);
        line.setColor(color);
        line.setAlignment(TextAlignment.CENTER_CENTER);
        String effFont = resolveFont(abcFont);
        if (!effFont.isEmpty()) {
            line.setText("-json " + wrapJson(text, fontId(effFont)));
            line.setYOffset(andY);
        }
        line.setBold(!GlobalFontSettings.isAbcMode());
        line.setFontSize(scale * 20f);
        line.setAbcFont(normalizeFontOrEmpty(abcFont));
        line.setBuiltin(true);
        return line;
    }

    private static String normalizeFontOrEmpty(String font) {
        if (font == null) return "";
        return ("a".equals(font) || "b".equals(font) || "c".equals(font)) ? font : "";
    }

    public static TextLineData applyGlobalFontSetting(TextLineData line, String signAbcFont) {
        if (line == null) {
            return null;
        }
        String effFont = resolveFont(signAbcFont);
        if (effFont.isEmpty()) {
            line.setAbcFont("");
            return line;
        }
        String current = line.getText() == null ? "" : line.getText();
        if (current.trim().startsWith("-")) {
            return line;
        }
        line.setText("-json " + wrapJson(current, fontId(effFont)));
        line.setBold(false);
        line.setAbcFont(effFont);
        return line;
    }

    private static String resolveFont(String lineAbcFont) {
        return switch (GlobalFontSettings.getMode()) {
            case VANILLA -> "";
            case ABC_A -> "a";
            case ABC_B -> "b";
            case ABC_C -> "c";
            case ADAPTIVE -> normalizeFont(lineAbcFont);
        };
    }

    private static final String FONT_PREFIX = "yunbeiuc:traf_sign_font_";

    public static String plainText(String raw) {
        if (raw == null) return "";
        if (!raw.trim().startsWith("-json")) return raw;
        JsonObject obj = parseJsonArg(raw);
        if (obj != null && obj.has("text")) {
            try {
                return obj.get("text").getAsString();
            } catch (Exception ignored) {
            }
        }
        return raw;
    }

    private static JsonObject parseJsonArg(String raw) {
        String t = raw == null ? "" : raw.trim();
        int sp = t.indexOf(' ');
        if (sp < 0) return null;
        try {
            return GSON.fromJson(t.substring(sp + 1).trim(), JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String trafficFontOf(String raw) {
        if (raw == null || !raw.trim().startsWith("-json")) return null;
        JsonObject obj = parseJsonArg(raw);
        if (obj == null || !obj.has("font")) return null;
        try {
            String font = obj.get("font").getAsString();
            if (!font.startsWith(FONT_PREFIX)) return null;
            String letter = font.substring(FONT_PREFIX.length());
            if ("a".equals(letter) || "b".equals(letter) || "c".equals(letter)) return letter;
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean canApplyFont(String raw) {
        if (raw == null || raw.trim().isEmpty()) return false;
        String t = raw.trim();
        if (!t.startsWith("-")) return true;
        return trafficFontOf(t) != null;
    }

    public static void forceFont(TextLineData line, String abcFont) {
        if (line == null) return;
        String text = plainText(line.getText());
        if (text.trim().startsWith("-")) return;
        if (abcFont == null || abcFont.isEmpty()) {
            line.setText(text);
            line.setBold(true);
            return;
        }
        line.setText("-json " + wrapJson(text, fontId(abcFont)));
        line.setAbcFont(abcFont);
        line.setBold(false);
    }

    public static String lineAbcFont(TextLineData line) {
        if (line == null) return "a";
        String recorded = normalizeFontOrEmpty(line.getAbcFont());
        if (!recorded.isEmpty()) return recorded;
        String tag = trafficFontOf(line.getText());
        if (tag != null) return tag;
        return inferAbcFont(line.getText());
    }

    public static String inferAbcFont(String raw) {
        String text = plainText(raw);
        for (int i = 0; i < text.length(); i++) {
            if (isCjk(text.charAt(i))) return "a";
        }
        return "b";
    }

    private static boolean isCjk(char c) {
        Character.UnicodeBlock b = Character.UnicodeBlock.of(c);
        return b == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || b == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || b == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || b == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || b == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    private static String normalizeFont(String font) {
        return "b".equals(font) || "c".equals(font) ? font : "a";
    }

    private static String fontId(String effFont) {
        return switch (effFont) {
            case "b" -> "yunbeiuc:traf_sign_font_b";
            case "c" -> "yunbeiuc:traf_sign_font_c";
            default -> "yunbeiuc:traf_sign_font_a";
        };
    }

    private static String wrapJson(String text, String fontId) {
        JsonObject obj = new JsonObject();
        obj.addProperty("text", text);
        obj.addProperty("font", fontId);
        return GSON.toJson(obj);
    }
}
