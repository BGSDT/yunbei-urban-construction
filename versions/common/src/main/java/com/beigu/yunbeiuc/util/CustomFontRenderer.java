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

    /**
     * 重建字形图集。
     *
     * <p>关键约束：字形在 UV 空间中的位置一经分配就<b>不再改变</b>。
     *
     * <p>原实现每次新增字符都把已有字形整体重排（{@code charMap.clear()} 后按
     * {@code HashSet} 的不确定顺序重新排布），同时又用固定名称把新纹理注册到
     * 同一个 {@link ResourceLocation} 上。在 1.20.1 及更低版本中，渲染批次会跨帧
     * 持有上一帧的 UV 与 GL 纹理引用，于是每当字符集变化（如倒计时 15→14），
     * 正在绘制的批次就会按新 UV 采样旧图集，数字边缘被切碎、像素错乱。
     *
     * <p>因此这里改为：布局按字符排序保证确定性，且只为<b>尚未分配</b>的字形
     * 追加空隙，已有字形的 UV 保持不变；纹理则整体重新上传。
     */
    private void rebuildAtlas() {
        CustomFontManager fontManager = CustomFontManager.getInstance();
        fontManager.initialize();

        // 只处理从未分配过位置的字形，已有字形保持原 UV 不动
        List<Character> newChars = new ArrayList<>(pendingChars);
        newChars.removeIf(c -> charMap.containsKey(c));
        // 排序保证同一批字符在不同机器/不同次运行下布局一致
        Collections.sort(newChars);

        Set<Character> allChars = new LinkedHashSet<>(charMap.keySet());
        allChars.addAll(newChars);

        NativeImage atlasImage = new NativeImage(NativeImage.Format.RGBA, ATLAS_WIDTH, ATLAS_HEIGHT, true);
        fillImage(atlasImage, 0);

        int currentX = 0;
        int currentY = 0;
        int maxRowHeight = 0;
        int padding = 2;

        // 必须按排序后的顺序布局：先已分配（其位置会被下面的赋值覆盖为一致值），
        // 再追加新字形，保证新字形落在已有字形之后，不会挤占既有 UV。
        for (char c : allChars) {
            if (!charMap.containsKey(c) && charMap.size() >= MAX_CHAR_CACHE) break;

            CustomFontManager.FontTexture glyph =
                    fontManager.getStringTexture(String.valueOf(c), 0xFFFFFFFF, fontName);

            if (glyph == null || glyph.getImage() == null) continue;

            NativeImage glyphImage = glyph.getImage();
            int glyphW = glyph.getWidth();
            int glyphH = glyph.getHeight();
            if (glyphW <= 0 || glyphH <= 0) continue;

            CharInfo existing = charMap.get(c);
            if (existing != null && existing.width == glyphW && existing.height == glyphH) {
                // 已分配且尺寸未变：沿用原 UV，并推进布局指针跳过其占位，
                // 使后续追加的字形不会与它重叠。
                int ex = Math.round(existing.u1 * ATLAS_WIDTH);
                int ey = Math.round(existing.v1 * ATLAS_HEIGHT);
                if (ex + glyphW + padding > ATLAS_WIDTH) {
                    currentX = 0;
                    currentY = ey + glyphH + padding;
                    maxRowHeight = 0;
                } else {
                    currentX = ex + glyphW + padding;
                    if (currentX > ATLAS_WIDTH) {
                        currentX = 0;
                        currentY = ey + glyphH + padding;
                        maxRowHeight = 0;
                    }
                }
                maxRowHeight = Math.max(maxRowHeight, glyphH);
                blitGlyph(atlasImage, glyphImage, ex, ey, glyphW, glyphH);
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

            float u1 = (float) currentX / ATLAS_WIDTH;
            float v1 = (float) currentY / ATLAS_HEIGHT;
            float u2 = (float) (currentX + glyphW) / ATLAS_WIDTH;
            float v2 = (float) (currentY + glyphH) / ATLAS_HEIGHT;

            charMap.put(c, new CharInfo(u1, v1, u2, v2, glyphW, glyphH));

            currentX += glyphW + padding;
            maxRowHeight = Math.max(maxRowHeight, glyphH);
        }

        // 使用固定名称，不加时间戳
        ResourceLocation id = VersionServices.resources().create(YunbeiUrbanConstruction.MOD_ID,
                "font_atlas_" + fontName);

        // DynamicTexture 接管 atlasImage 的所有权，不要再单独 close 它；
        // upload() 会把整张图集重新上传到同一个 GL 纹理对象，
        // 避免释放纹理导致在途批次采样到已删除的纹理。
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
