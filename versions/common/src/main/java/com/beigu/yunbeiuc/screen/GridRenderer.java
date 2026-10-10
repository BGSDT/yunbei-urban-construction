package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.text.Text;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class GridRenderer {
    private GridRenderer() {
    }

    // ==================== 图案网格（大缩略图） ====================

    /**
     * 渲染白名单图案网格。
     *
     * @return 网格总高度
     */
    public static int renderWhitelistGrid(DrawContext ctx, Font tr,
                                          double mx, double my,
                                          int paneW, int clipTop, int clipBottom,
                                          List<PatternAndFontOverlay.WhitelistPatternItem> items,
                                          int startX, int startY) {
        if (items.isEmpty()) {
            ctx.drawText(tr, Text.translatable("yunbeiuc.gui.no_images"),
                    startX, startY, UIConstants.CLR_MUTED, false);
            return 30;
        }

        int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
        int gridW = cols * UIConstants.CELL_SIZE + (cols - 1) * UIConstants.CELL_GAP_X;
        int gridX = SidebarState.getEffectiveWidth() + (paneW - gridW) / 2;

        for (int i = 0; i < items.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int tx = gridX + col * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X);
            int ty = startY + row * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y);

            if (ty + UIConstants.CELL_SIZE < clipTop || ty > clipBottom) continue;

            PatternAndFontOverlay.WhitelistPatternItem item = items.get(i);
            drawTile(ctx, tx, ty, item.textureId, mx, my);
        }

        int rows = (int) Math.ceil((double) items.size() / cols);
        return rows * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y) - UIConstants.CELL_GAP_Y;
    }

    /**
     * 渲染缓存纹理图案网格。
     *
     * @return 网格总高度
     */
    public static int renderCachedTextureGrid(DrawContext ctx, Font tr,
                                              double mx, double my,
                                              int paneW, int clipTop, int clipBottom,
                                              List<ResourceLocation> textures,
                                              int startX, int startY) {
        if (textures == null || textures.isEmpty()) {
            ctx.drawText(tr, Text.translatable("yunbeiuc.gui.no_images"),
                    startX, startY, UIConstants.CLR_MUTED, false);
            return 30;
        }

        int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
        int gridW = cols * UIConstants.CELL_SIZE + (cols - 1) * UIConstants.CELL_GAP_X;
        int gridX = SidebarState.getEffectiveWidth() + (paneW - gridW) / 2;

        for (int i = 0; i < textures.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int tx = gridX + col * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X);
            int ty = startY + row * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y);

            if (ty + UIConstants.CELL_SIZE < clipTop || ty > clipBottom) continue;

            drawTile(ctx, tx, ty, textures.get(i), mx, my);
        }

        int rows = (int) Math.ceil((double) textures.size() / cols);
        return rows * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y) - UIConstants.CELL_GAP_Y;
    }

    // 绘制单个大缩略图方块
    private static void drawTile(DrawContext ctx, int x, int y, ResourceLocation texId,
                                 double mx, double my) {
        int s = UIConstants.CELL_SIZE;
        boolean isHov = LayoutHelper.isMouseInRect(mx, my, x, y, s, s);

        // 背景
        ctx.fill(x, y, x + s, y + s, isHov ? UIConstants.CLR_BTN_HOVER : UIConstants.CLR_CELL_FILL);
        ctx.drawBorder(x, y, s, s, isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_CELL_OUTLINE);

        // 纹理：显式开启 alpha 混合，使带透明通道的 PNG 正确叠加到格子背景上
        // （只开不关——界面其余部分的面板底色/边框同样是半透明的，依赖 blend 状态）
        //
        // 关键修复：按源纹理真实像素尺寸绘制。
        // 原先固定传 regionWidth=regionHeight=28，而 blit 会按 256×256 图集换算 UV
        // （28/256），对 64×64 等非 256 纹理取到的是错误区域，表现为「比例截取不对」；
        // 同时强制 28×28 正方形会把非正方形标志拉伸变形。
        // TextureAspectCache.drawFitted 用真实宽高作 region/texture 尺寸，
        // UV 恰好覆盖整张图（0..1），并在格子内等比居中、不拉伸不变形。
        int pad = 4;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int[] fitted = TextureAspectCache.drawFitted(ctx, texId,
                x + pad, y + pad, s - pad * 2, s - pad * 2);
        if (fitted == null) {
            // 尺寸读取失败（纹理缺失/格式异常）时不绘制纹理，
            // 只保留格子底色与边框，避免用猜测的图集尺寸画出错误区域
        }

        // hover 时顶部高亮条
        if (isHov) {
            ctx.fill(x, y, x + s, y + 3, UIConstants.CLR_ACCENT);
        }
    }

    // ==================== 字体卡片列表 ====================

    /**
     * 渲染字体卡片列表。
     *
     * @return 列表总高度
     */
    public static int renderFontList(DrawContext ctx, Font tr,
                                     double mx, double my,
                                     int paneW, int clipTop, int clipBottom,
                                     List<PatternAndFontOverlay.FontItem> fontItems,
                                     int startX, int startY) {
        if (fontItems.isEmpty()) {
            ctx.drawText(tr, Text.translatable("yunbeiuc.gui.no_fonts"),
                    startX, startY, UIConstants.CLR_MUTED, false);
            return 30;
        }

        int cardW = paneW - 60;
        int cardX = SidebarState.getEffectiveWidth() + 30;
        int cardH = UIConstants.FONT_CARD_H;
        int gap = UIConstants.FONT_CARD_GAP;
        int accentW = 4;

        for (int i = 0; i < fontItems.size(); i++) {
            PatternAndFontOverlay.FontItem item = fontItems.get(i);
            int cy = startY + i * (cardH + gap);

            if (cy + cardH < clipTop || cy > clipBottom) continue;

            boolean isHov = LayoutHelper.isMouseInRect(mx, my, cardX, cy, cardW, cardH);

            // 卡片背景
            int bg = isHov ? UIConstants.CLR_BTN_HOVER : UIConstants.CLR_HOME_CARD_BG;
            ctx.fill(cardX, cy, cardX + cardW, cy + cardH, bg);
            ctx.drawBorder(cardX, cy, cardW, cardH,
                    isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_HOME_CARD_STROKE);

            // 左侧蓝色强调条
            ctx.fill(cardX, cy, cardX + accentW, cy + cardH, UIConstants.CLR_ACCENT);

            // 字体名称（用默认字体渲染，保证可读性）
            String name = item.displayName.getString();
            ctx.drawText(tr, name, cardX + accentW + 10, cy + 6,
                    UIConstants.CLR_HEADING, false);

            // 预览文字：用该字体自身渲染，直观展示字形风格
            Component preview = buildFontPreview(item.fontId);
            ctx.drawText(tr, preview, cardX + accentW + 10, cy + 22,
                    UIConstants.CLR_BODY_TEXT, false);

            // 右侧标签
            Component tag = Text.translatable("yunbeiuc.gui.button.insert");
            int tagW = tr.width(tag) + 12;
            int tagX = cardX + cardW - tagW - 8;
            int tagY = cy + (cardH - 14) / 2;
            ctx.fill(tagX, tagY, tagX + tagW, tagY + 14,
                    isHov ? UIConstants.CLR_ACTION_HOVER : UIConstants.CLR_ACTION_FILL);
            ctx.drawBorder(tagX, tagY, tagW, 14,
                    isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_ACTION_STROKE);
            ctx.drawText(tr, tag, tagX + 6, tagY + 3,
                    UIConstants.CLR_BTN_LABEL, false);
        }

        return fontItems.size() * (cardH + gap) - gap;
    }

    /**
     * 构造字体预览文字：套用 {@code fontId} 指定的字体。
     *
     * <p>通过 {@link Style#withFont(ResourceLocation)} 让 Minecraft 的字体系统
     * 自动查找并应用对应字体（如 {@code yunbeiuc:traf_sign_font_a}）。
     * 若该字体未注册 / JSON 缺失，MC 会自动回退到默认字体，不会崩溃。
     */
    private static Component buildFontPreview(String fontId) {
        Component preview = Text.literal("AaBbCc 123");
        if (fontId == null || fontId.isEmpty()) {
            return preview;
        }
        try {
            int colon = fontId.indexOf(':');
            String namespace = colon >= 0 ? fontId.substring(0, colon) : "minecraft";
            String path = colon >= 0 ? fontId.substring(colon + 1) : fontId;
            ResourceLocation id = createResourceLocation(namespace, path);
            return preview.copy().withStyle(Style.EMPTY.withFont(id));
        } catch (Throwable t) {
            // 任何异常（含反射失败、fontId 非法）都退回默认字体
            return preview;
        }
    }

    /**
     * 跨版本创建 {@link ResourceLocation}。
     *
     * <p>1.21+ 构造器私有，须用 {@code fromNamespaceAndPath(namespace, path)}；
     * 1.20.6 及以前构造器公开，直接反射调用 {@code new ResourceLocation(ns, path)}。
     * 这里优先尝试新 API，失败则回退到构造器。
     */
    private static ResourceLocation createResourceLocation(String namespace, String path)
            throws ReflectiveOperationException {
        // 1.21+
        try {
            return (ResourceLocation) ResourceLocation.class
                    .getMethod("fromNamespaceAndPath", String.class, String.class)
                    .invoke(null, namespace, path);
        } catch (NoSuchMethodException ignored) {
            // 低版本没有该静态方法，继续走构造器
        }
        // 1.20.6 及以前
        return (ResourceLocation) ResourceLocation.class
                .getConstructor(String.class, String.class)
                .newInstance(namespace, path);
    }

    public static int getGridItemIndex(double mx, double my,
                                       int paneW, int startY, int clipTop, int clipBottom,
                                       int itemCount) {
        if (my < clipTop || my > clipBottom) return -1;

        int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
        int gridW = cols * UIConstants.CELL_SIZE + (cols - 1) * UIConstants.CELL_GAP_X;
        int gridX = SidebarState.getEffectiveWidth() + (paneW - gridW) / 2;

        int relX = (int) mx - gridX;
        int relY = (int) my - startY;

        if (relX < 0 || relY < 0) return -1;

        int col = relX / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X);
        int row = relY / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y);

        if (col >= cols) return -1;

        // 检查是否在 tile 本身范围内（而非 gap 区域）
        int tileX = col * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X);
        int tileY = row * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y);
        if (relX - tileX > UIConstants.CELL_SIZE) return -1;
        if (relY - tileY > UIConstants.CELL_SIZE) return -1;

        int idx = row * cols + col;
        if (idx >= itemCount) return -1;

        return idx;
    }

    /**
     * 获取字体卡片点击位置的索引。
     *
     * @return 点击的条目索引，未命中返回 -1
     */
    public static int getFontItemIndex(double mx, double my, int paneW,
                                       int startY, int clipTop, int clipBottom,
                                       int itemCount) {
        if (my < clipTop || my > clipBottom) return -1;

        int cardW = paneW - 60;
        int cardX = SidebarState.getEffectiveWidth() + 30;
        int cardH = UIConstants.FONT_CARD_H;
        int gap = UIConstants.FONT_CARD_GAP;

        int relY = (int) my - startY;
        int idx = relY / (cardH + gap);

        if (idx < 0 || idx >= itemCount) return -1;

        int cy = startY + idx * (cardH + gap);
        if (!LayoutHelper.isMouseInRect(mx, my, cardX, cy, cardW, cardH)) return -1;

        return idx;
    }

    // ==================== 标签按钮网格（图案集整合） ====================

    /**
     * 标签按钮布局结果。
     *
     * <p>{@code rects} 每 4 个元素描述一个按钮的 x / y / w / h；{@code totalHeight} 为整块内容高度。
     */
    public static final class LabeledLayout {
        public final int[] rects;
        public final int totalHeight;

        LabeledLayout(int[] rects, int totalHeight) {
            this.rects = rects;
            this.totalHeight = totalHeight;
        }
    }

    /**
     * 计算标签按钮的换行平铺布局。
     *
     * <p>渲染、命中检测、滚动高度预算三处都调用本方法，保证三者永远一致
     * （否则会出现「按钮画在这里、点在那里」或「底部内容滚不到」的错位）。
     *
     * <p>换行只按相对宽度判断，不依赖 {@code startY}，因此高度预算可以在不知道实际起始 Y 的情况下算准。
     */
    public static LabeledLayout layoutLabeled(Font tr, int paneW, int startX, int startY,
                                              List<PatternAndFontOverlay.LabeledItem> items) {
        int avail = Math.max(1, paneW - 48);
        int n = items.size();
        int[] rects = new int[n * 4];

        int rowH = UIConstants.LABEL_BTN_HEIGHT;
        int relX = 0;
        int y = startY;

        for (int i = 0; i < n; i++) {
            Component label = items.get(i).displayName;
            // 单个按钮不超过可用宽度，避免超长标签把行撑破
            int w = Math.min(avail, tr.width(label) + UIConstants.LABEL_BTN_PAD_X * 2);

            if (relX > 0 && relX + w > avail) {
                relX = 0;
                y += rowH + UIConstants.LABEL_BTN_GAP_Y;
            }

            rects[i * 4] = startX + relX;
            rects[i * 4 + 1] = y;
            rects[i * 4 + 2] = w;
            rects[i * 4 + 3] = rowH;

            relX += w + UIConstants.LABEL_BTN_GAP_X;
        }

        return new LabeledLayout(rects, n == 0 ? 0 : (y - startY) + rowH);
    }

    /**
     * 标签按钮区的内容高度（供滚动条预算使用，与 {@link #renderLabeledGrid} 的返回值一致）。
     */
    public static int labeledHeight(Font tr, int paneW, List<PatternAndFontOverlay.LabeledItem> items) {
        if (items == null || items.isEmpty()) return 30;
        return layoutLabeled(tr, paneW, 0, 0, items).totalHeight;
    }

    /**
     * 渲染标签按钮区。
     *
     * @return 内容高度
     */
    public static int renderLabeledGrid(DrawContext ctx, Font tr,
                                        double mx, double my,
                                        int paneW, int clipTop, int clipBottom,
                                        List<PatternAndFontOverlay.LabeledItem> items,
                                        int startX, int startY) {
        if (items == null || items.isEmpty()) {
            ctx.drawText(tr, Text.translatable("yunbeiuc.gui.no_images"),
                    startX, startY, UIConstants.CLR_MUTED, false);
            return 30;
        }

        LabeledLayout layout = layoutLabeled(tr, paneW, startX, startY, items);

        for (int i = 0; i < items.size(); i++) {
            int x = layout.rects[i * 4];
            int y = layout.rects[i * 4 + 1];
            int w = layout.rects[i * 4 + 2];
            int h = layout.rects[i * 4 + 3];

            if (y + h < clipTop || y > clipBottom) continue;

            boolean isHov = LayoutHelper.isMouseInRect(mx, my, x, y, w, h);

            ctx.fill(x, y, x + w, y + h, isHov ? UIConstants.CLR_BTN_HOVER : UIConstants.CLR_BTN_FILL);
            ctx.drawBorder(x, y, w, h, isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_BTN_STROKE);

            Component label = items.get(i).displayName;
            int lw = tr.width(label);
            ctx.drawText(tr, label, x + (w - lw) / 2, y + (h - 8) / 2,
                    isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_BTN_LABEL, false);
        }

        return layout.totalHeight;
    }

    /**
     * 命中检测：返回点击到的标签按钮索引，未命中返回 -1。
     */
    public static int getLabeledItemIndex(Font tr, double mx, double my, int paneW,
                                          int startY, int clipTop, int clipBottom,
                                          List<PatternAndFontOverlay.LabeledItem> items) {
        if (items == null || items.isEmpty()) return -1;
        if (my < clipTop || my > clipBottom) return -1;

        int startX = SidebarState.getEffectiveWidth() + 24;
        LabeledLayout layout = layoutLabeled(tr, paneW, startX, startY, items);

        for (int i = 0; i < items.size(); i++) {
            if (LayoutHelper.isMouseInRect(mx, my,
                    layout.rects[i * 4], layout.rects[i * 4 + 1],
                    layout.rects[i * 4 + 2], layout.rects[i * 4 + 3])) {
                return i;
            }
        }
        return -1;
    }
}