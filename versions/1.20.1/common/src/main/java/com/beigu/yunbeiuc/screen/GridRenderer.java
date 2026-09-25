package com.beigu.yunbeiuc.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * 网格/列表渲染器 — 海燕蓝主题
 *
 * <p>图案使用小缩略图网格（点击直接插入），字体使用卡片式列表。
 *
 * @see PatternAndFontOverlay
 */
public final class GridRenderer {
    private GridRenderer() {
    }

    // ==================== 图案网格（大缩略图） ====================

    /**
     * 渲染白名单图案网格。
     *
     * @return 网格总高度
     */
    public static int renderWhitelistGrid(DrawContext ctx, TextRenderer tr,
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
    public static int renderCachedTextureGrid(DrawContext ctx, TextRenderer tr,
                                               double mx, double my,
                                               int paneW, int clipTop, int clipBottom,
                                               List<Identifier> textures,
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
    private static void drawTile(DrawContext ctx, int x, int y, Identifier texId,
                                  double mx, double my) {
        int s = UIConstants.CELL_SIZE;
        boolean isHov = LayoutHelper.isMouseInRect(mx, my, x, y, s, s);

        // 背景
        ctx.fill(x, y, x + s, y + s, isHov ? UIConstants.CLR_BTN_HOVER : UIConstants.CLR_CELL_FILL);
        ctx.drawBorder(x, y, s, s, isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_CELL_OUTLINE);

        // 纹理：显式开启 alpha 混合，使带透明通道的 PNG 正确叠加到格子背景上
        // （只开不关——界面其余部分的面板底色/边框同样是半透明的，依赖 blend 状态）
        int pad = 4;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ctx.drawTexture(texId, x + pad, y + pad, 0.0F, 0.0F, s - pad * 2, s - pad * 2, s - pad * 2, s - pad * 2);

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
    public static int renderFontList(DrawContext ctx, TextRenderer tr,
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

            // 字体名称（加粗）
            String name = item.displayName.getString();
            ctx.drawText(tr, name, cardX + accentW + 10, cy + 6,
                    UIConstants.CLR_HEADING, false);

            // 预览文字
            Text preview = Text.literal("AaBbCc 123");
            ctx.drawText(tr, preview, cardX + accentW + 10, cy + 22,
                    UIConstants.CLR_BODY_TEXT, false);

            // 右侧标签
            Text tag = Text.translatable("yunbeiuc.gui.button.insert");
            int tagW = tr.getWidth(tag) + 12;
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

    // ==================== 点击判定 ====================

    /**
     * 获取图案网格点击位置的索引（点击整个方块即触发）。
     *
     * @return 点击的条目索引，未命中返回 -1
     */
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
}
