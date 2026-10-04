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
import com.beigu.yunbeiuc.api.mapper.VersionServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomFontRenderer {
    private static final Map<String, CustomFontRenderer> INSTANCES = new HashMap<>();

    private final String fontName;
    private ResourceLocation fontAtlas;
    private final Map<Character, CharInfo> charMap = new HashMap<>();
    private final Set<Character> pendingChars = new HashSet<>();
    /** 取不到位图的字符：记下来避免每次渲染都重复触发整张图集重建。 */
    private final Set<Character> unresolvable = new HashSet<>();
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
            if (!charMap.containsKey(c) && !pendingChars.contains(c) && !unresolvable.contains(c)) {
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

    /**
     * 重建字形图集。
     *
     * <p>每次字符集变化都<b>整体重排</b>：先 {@code charMap.clear()}，再按排序后的
     * 字符顺序把每个字形依次落到游标位置，因此每个字形的 UV 必然互不重叠。
     *
     * <p>曾经这里尝试过"已有字形保留原 UV、只为新字形追加空隙"的增量布局，那是错的：
     * 增量布局要正确推进游标，必须按<b>分配顺序</b>回放已有字形；而 {@code charMap} 是
     * {@link HashMap}，{@code keySet()} 的迭代顺序是哈希桶顺序。倒计时数字 '0'-'9'
     * 的哈希桶恰好等于数字值，而引入顺序是"先渲染背景 88、再渲染数字"，于是 charMap 的
     * 迭代顺序变成 [2,5,8] 这类与分配顺序不同的顺序，游标会往回跳，新字形被摆到已有
     * 字形上面 —— 多个数字共用同一块 UV，读秒就会显示成别的数字（看起来像"乱跳"）。
     *
     * <p>重排是安全的：{@link #ensureCharacters} 只在字符集变化时触发重建，
     * 且重建发生在写入顶点之前，同一帧内所有文字用的都是同一份新 UV。
     */
    private void rebuildAtlas() {
        CustomFontManager fontManager = CustomFontManager.getInstance();
        fontManager.initialize();

        // 本次需要存在于图集中的全部字形 = 已分配 + 本次新增
        Set<Character> allChars = new LinkedHashSet<>(charMap.keySet());
        allChars.addAll(pendingChars);

        NativeImage atlasImage = new NativeImage(NativeImage.Format.RGBA, ATLAS_WIDTH, ATLAS_HEIGHT, true);
        fillImage(atlasImage, 0);

        int currentX = 0;
        int currentY = 0;
        int maxRowHeight = 0;
        int padding = 2;

        // 排序后布局：同一批字符在任意机器/任意次运行下都会得到完全相同的图集
        List<Character> ordered = new ArrayList<>(allChars);
        Collections.sort(ordered);

        charMap.clear();

        for (char c : ordered) {
            if (charMap.size() >= MAX_CHAR_CACHE) break;

            CustomFontManager.FontTexture glyph =
                    fontManager.getStringTexture(String.valueOf(c), 0xFFFFFFFF, fontName);

            if (glyph == null || glyph.getImage() == null) {
                unresolvable.add(c);
                continue;
            }

            NativeImage glyphImage = glyph.getImage();
            int glyphW = glyph.getWidth();
            int glyphH = glyph.getHeight();
            if (glyphW <= 0 || glyphH <= 0) {
                unresolvable.add(c);
                continue;
            }

            if (currentX + glyphW + padding > ATLAS_WIDTH) {
                currentX = 0;
                currentY += maxRowHeight + padding;
                maxRowHeight = 0;
            }

            if (currentY + glyphH + padding > ATLAS_HEIGHT) {
                break;
            }

            blitGlyph(atlasImage, glyphImage, currentX, currentY, glyphW, glyphH);

            charMap.put(c, new CharInfo(
                    (float) currentX / ATLAS_WIDTH,
                    (float) currentY / ATLAS_HEIGHT,
                    (float) (currentX + glyphW) / ATLAS_WIDTH,
                    (float) (currentY + glyphH) / ATLAS_HEIGHT,
                    glyphW, glyphH));

            currentX += glyphW + padding;
            maxRowHeight = Math.max(maxRowHeight, glyphH);
        }

        // 使用固定名称，不加时间戳
        ResourceLocation id = VersionServices.resources().create(YunbeiUrbanConstruction.MOD_ID,
                "font_atlas_" + fontName);

        // DynamicTexture 接管 atlasImage 的所有权，不要再单独 close 它。
        DynamicTexture texture = new DynamicTexture(atlasImage);
        texture.setFilter(false, false);
        Minecraft.getInstance().getTextureManager().register(id, texture);
        fontAtlas = id;

        pendingChars.clear();
        atlasDirty = false;
    }

    /** 把单个字形像素拷贝进图集。 */
    private static void blitGlyph(NativeImage atlas, NativeImage glyph,
                                  int dstX, int dstY, int w, int h) {
        for (int y = 0; y < h; y++) {
            int ty = dstY + y;
            if (ty < 0 || ty >= atlas.getHeight()) continue;
            for (int x = 0; x < w; x++) {
                int tx = dstX + x;
                if (tx < 0 || tx >= atlas.getWidth()) continue;
                atlas.setPixelRGBA(tx, ty, glyph.getPixelRGBA(x, y));
            }
        }
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

            // UV 内缩半个纹素：图集里相邻字形仅隔 padding，线性过滤会在边缘
            // 采到邻居的像素，导致数字边缘出现杂色/错位感
            float insetU = 0.5f / ATLAS_WIDTH;
            float insetV = 0.5f / ATLAS_HEIGHT;
            float u1 = info.u1 + insetU;
            float v1 = info.v1 + insetV;
            float u2 = info.u2 - insetU;
            float v2 = info.v2 - insetV;

            VersionServices.render().vertex(vertexConsumer, matrices.last(), currentX, y + charHeight, z, red, green, blue, alpha, u1, v2, 0, light, 0, 0, 1);
            VersionServices.render().vertex(vertexConsumer, matrices.last(), currentX + charWidth, y + charHeight, z, red, green, blue, alpha, u2, v2, 0, light, 0, 0, 1);
            VersionServices.render().vertex(vertexConsumer, matrices.last(), currentX + charWidth, y, z, red, green, blue, alpha, u2, v1, 0, light, 0, 0, 1);
            VersionServices.render().vertex(vertexConsumer, matrices.last(), currentX, y, z, red, green, blue, alpha, u1, v1, 0, light, 0, 0, 1);

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
        unresolvable.clear();
        atlasDirty = true;
        initialized = false;
    }

    /** 资源包重载：清空字形缓存，下次渲染时按新字体重建图集。 */
    public static void cleanupAll() {
        for (CustomFontRenderer renderer : INSTANCES.values()) {
            renderer.cleanup();
        }
        INSTANCES.clear();
    }
}
