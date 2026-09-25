package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomFontRenderer {
    private static final Map<String, CustomFontRenderer> INSTANCES = new HashMap<>();

    private final String fontName;
    private ResourceLocation fontAtlas;
    private final Map<Character, CharInfo> charMap = new HashMap<>();
    private final Set<Character> pendingChars = new HashSet<>();
    private boolean initialized = false;
    private boolean atlasDirty = false;

    private static final int ATLAS_WIDTH = 2048;
    private static final int ATLAS_HEIGHT = 4096;
    private static final int MAX_CHAR_CACHE = 4096;

    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public CustomFontRenderer(String fontName) {
        this.fontName = fontName;
    }

    public static CustomFontRenderer getInstance(String fontName) {
        return INSTANCES.computeIfAbsent(fontName, CustomFontRenderer::new);
    }

    public void initialize() {
        if (initialized) return;
        initialized = true;
    }

    private synchronized void ensureCharacters(String text) {
        if (text == null || text.isEmpty()) return;

        boolean hasNewChars = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!charMap.containsKey(c) && !pendingChars.contains(c)) {
                pendingChars.add(c);
                hasNewChars = true;
            }
        }

        if (hasNewChars) {
            atlasDirty = true;
        }

        if (atlasDirty) {
            rebuildAtlas();
        }
    }

    private void rebuildAtlas() {
        // 如果已存在纹理，先销毁
        if (fontAtlas != null) {
            Minecraft.getInstance().getTextureManager().release(fontAtlas);
            fontAtlas = null;
        }

        Set<Character> allChars = new HashSet<>(charMap.keySet());
        allChars.addAll(pendingChars);
        charMap.clear();

        CustomFontManager fontManager = CustomFontManager.getInstance();
        fontManager.initialize();

        NativeImage atlasImage = new NativeImage(NativeImage.Format.RGBA, ATLAS_WIDTH, ATLAS_HEIGHT, false);
        fillImage(atlasImage, 0);

        int currentX = 0;
        int currentY = 0;
        int maxRowHeight = 0;
        int padding = 2;

        for (char c : allChars) {
            if (charMap.size() >= MAX_CHAR_CACHE) break;

            CustomFontManager.FontTexture glyph =
                    fontManager.getStringTexture(String.valueOf(c), 0xFFFFFFFF, fontName);

            if (glyph == null || glyph.getImage() == null) continue;

            NativeImage glyphImage = glyph.getImage();
            int glyphW = glyph.getWidth();
            int glyphH = glyph.getHeight();

            if (currentX + glyphW + padding > ATLAS_WIDTH) {
                currentX = 0;
                currentY += maxRowHeight + padding;
                maxRowHeight = 0;
            }

            if (currentY + glyphH + padding > ATLAS_HEIGHT) {
                break;
            }

            for (int y = 0; y < glyphH; y++) {
                for (int x = 0; x < glyphW; x++) {
                    atlasImage.setPixelRGBA(currentX + x, currentY + y, glyphImage.getPixelRGBA(x, y));
                }
            }

            float u1 = (float) currentX / ATLAS_WIDTH;
            float v1 = (float) currentY / ATLAS_HEIGHT;
            float u2 = (float) (currentX + glyphW) / ATLAS_WIDTH;
            float v2 = (float) (currentY + glyphH) / ATLAS_HEIGHT;

            charMap.put(c, new CharInfo(u1, v1, u2, v2, glyphW, glyphH));

            currentX += glyphW + padding;
            maxRowHeight = Math.max(maxRowHeight, glyphH);
        }

        // 使用固定名称，不加时间戳
        ResourceLocation id = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID,
                "font_atlas_" + fontName);

        Minecraft.getInstance().getTextureManager()
                .register(id, new DynamicTexture(atlasImage));
        fontAtlas = id;

        pendingChars.clear();
        atlasDirty = false;
    }

    private static void fillImage(NativeImage image, int color) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setPixelRGBA(x, y, color);
            }
        }
    }

    // 保留旧的 centered 参数方法以兼容
    public static void renderText(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            String text,
            int color,
            float x, float y, float z,
            float scale,
            int light,
            boolean centered,
            String fontName
    ) {
        renderText(matrices, vertexConsumers, text, color, x, y, z, scale, light,
                centered ? TextAlignment.CENTER : TextAlignment.LEFT, fontName, 0.0f, 1.0f);
    }

    // 新增 alignment 参数的方法
    public static void renderText(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            String text,
            int color,
            float x, float y, float z,
            float scale,
            int light,
            TextAlignment alignment,
            String fontName
    ) {
        renderText(matrices, vertexConsumers, text, color, x, y, z, scale, light, alignment, fontName, 0.0f, 1.0f);
    }

    public static void renderText(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            String text,
            int color,
            float x, float y, float z,
            float scale,
            int light,
            TextAlignment alignment,
            String fontName,
            float characterSpacing,
            float letterSpacingFactor
    ) {
        if (text == null || text.isEmpty()) return;

        CustomFontRenderer renderer = getInstance(fontName);
        if (!renderer.initialized) {
            renderer.initialize();
        }

        renderer.ensureCharacters(text);

        if (renderer.fontAtlas == null) {
            return;
        }

        int alpha = (color >>> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        if (alpha == 0) alpha = 255;

        float totalWidth = 0;
        float spacingFactor = 0.8f;
        if (letterSpacingFactor != 1.0f) {
            spacingFactor *= letterSpacingFactor;
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            CharInfo info = renderer.charMap.get(c);
            if (info == null) continue;
            totalWidth += info.width * scale * spacingFactor;
            if (i < text.length() - 1) {
                totalWidth += characterSpacing * scale;
            }
        }

        float currentX;
        switch (alignment) {
            case CENTER:
                currentX = x - totalWidth / 2;
                break;
            case RIGHT:
                currentX = x - totalWidth;
                break;
            case LEFT:
            default:
                currentX = x;
                break;
        }

        Matrix4f positionMatrix = matrices.last().pose();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.text(renderer.fontAtlas));

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            CharInfo info = renderer.charMap.get(c);
            if (info == null) {
                currentX += 5 * scale * spacingFactor;
                if (i < text.length() - 1) {
                    currentX += characterSpacing * scale;
                }
                continue;
            }

            float charWidth = info.width * scale;
            float charHeight = info.height * scale;

            vertexConsumer.vertex(positionMatrix, currentX, y + charHeight, z)
                    .color(red, green, blue, alpha).uv(info.u1, info.v2)
                    .uv2(light).endVertex();
            vertexConsumer.vertex(positionMatrix, currentX + charWidth, y + charHeight, z)
                    .color(red, green, blue, alpha).uv(info.u2, info.v2)
                    .uv2(light).endVertex();
            vertexConsumer.vertex(positionMatrix, currentX + charWidth, y, z)
                    .color(red, green, blue, alpha).uv(info.u2, info.v1)
                    .uv2(light).endVertex();
            vertexConsumer.vertex(positionMatrix, currentX, y, z)
                    .color(red, green, blue, alpha).uv(info.u1, info.v1)
                    .uv2(light).endVertex();

            currentX += charWidth * spacingFactor;
            if (i < text.length() - 1) {
                currentX += characterSpacing * scale;
            }
        }
    }

    private static class CharInfo {
        final float u1, v1, u2, v2;
        final int width, height;

        CharInfo(float u1, float v1, float u2, float v2, int width, int height) {
            this.u1 = u1; this.v1 = v1; this.u2 = u2; this.v2 = v2;
            this.width = width; this.height = height;
        }
    }

    public void cleanup() {
        if (fontAtlas != null) {
            Minecraft.getInstance().getTextureManager().release(fontAtlas);
            fontAtlas = null;
        }
        charMap.clear();
        pendingChars.clear();
        atlasDirty = true;
        initialized = false;
    }

    public static void cleanupAll() {
        for (CustomFontRenderer renderer : INSTANCES.values()) {
            renderer.cleanup();
        }
        INSTANCES.clear();
    }
}
