package com.beigu.yunbeiuc.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

/**
 * 布局与渲染辅助工具 — 海燕蓝主题
 *
 * @see PatternAndFontOverlay
 */
public final class LayoutHelper {
    private LayoutHelper() {
    }

    public static PatternAndFontOverlay.H4Section effective(PatternAndFontOverlay.H4Section section) {
        if (section.useStyles && !section.subSections.isEmpty()
                && section.activeStyleIndex >= 0 && section.activeStyleIndex < section.subSections.size()) {
            return section.subSections.get(section.activeStyleIndex);
        }
        return section;
    }

    // ==================== 卡片分组式侧边栏高度 ====================

    /** 侧边栏「主页」卡片高度。 */
    public static int getHomeCardHeight() {
        return SidebarState.isCollapsed() ? UIConstants.NAV_ICON_ROW_H
                : UIConstants.CARD_HEADER_H;
    }

    /** H2 分组卡片高度（含展开的子项）。 */
    public static int calculateH2CardHeight(PatternAndFontOverlay.H2Category h2, TextRenderer tr) {
        if (SidebarState.isCollapsed()) return UIConstants.NAV_ICON_ROW_H;
        int h = UIConstants.CARD_HEADER_H;
        if (h2.isExpanded && !h2.subCategories.isEmpty()) {
            h += UIConstants.CARD_INNER_TOP;
            h += h2.subCategories.size() * UIConstants.CARD_ITEM_H;
            h += UIConstants.CARD_INNER_BOTTOM;
        }
        return h;
    }

    public static int getSidebarTopItemHeight(Text text, TextRenderer tr) {
        return getHomeCardHeight();
    }

    public static int getCategoryHeight(Text title, String prefix, int indent, TextRenderer tr) {
        return UIConstants.CARD_ITEM_H;
    }

    public static int getH3ItemHeight(Text title, TextRenderer tr) {
        return UIConstants.CARD_ITEM_H;
    }

    public static int calculateH2PanelHeight(PatternAndFontOverlay.H2Category h2, TextRenderer tr) {
        return calculateH2CardHeight(h2, tr);
    }

    public static int calculateH3Height(PatternAndFontOverlay.H3Category h3, int indent, TextRenderer tr) {
        return UIConstants.CARD_ITEM_H;
    }

    public static int calculateH4Height(PatternAndFontOverlay.H4Section section, int indent, TextRenderer tr) {
        return UIConstants.CARD_ITEM_H;
    }

    // ==================== 递归渲染（旧接口，新 UI 不再直接使用） ====================

    public static int renderH4SectionDynamic(DrawContext ctx, TextRenderer tr, double mx, double my,
                                             PatternAndFontOverlay.H4Section section, int indent, int y,
                                             PatternAndFontOverlay.H3Category parentH3) {
        return y + UIConstants.CARD_ITEM_H;
    }

    public static int renderH3CategoryDynamic(DrawContext ctx, TextRenderer tr, double mx, double my,
                                              PatternAndFontOverlay.H3Category h3, int indent, int y) {
        return y + UIConstants.CARD_ITEM_H;
    }

    public static Integer handleH3CategoryClick(double mx, double my, PatternAndFontOverlay.H2Category h2,
                                                PatternAndFontOverlay.H3Category h3, int indent, int y, TextRenderer tr) {
        return null;
    }

    public static Integer handleH4SectionClick(double mx, double my, PatternAndFontOverlay.H2Category h2,
                                               PatternAndFontOverlay.H3Category parentH3,
                                               PatternAndFontOverlay.H4Section section, int indent, int y, TextRenderer tr) {
        return null;
    }

    // ==================== 圆角绘制 ====================

    /**
     * 绘制圆角矩形填充。
     *
     * <p>圆角采用逐行扫描：第 i 行（自上而下）对应的圆纵向距离 dy = r - i - 0.5，
     * 该行左右各缩进 dx = r - sqrt(r² - dy²)。i=0 时缩进最大，i→r 时缩进趋近 0，
     * 与下方直边平滑衔接。
     */
    public static void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) return;
        int r = Math.min(Math.min(w, h) / 2, 6);
        if (r < 1) { ctx.fill(x, y, x + w, y + h, color); return; }

        // 中部矩形（避开四角）
        ctx.fill(x + r, y, x + w - r, y + h, color);
        ctx.fill(x, y + r, x + r, y + h - r, color);
        ctx.fill(x + w - r, y + r, x + w, y + h - r, color);

        // 四角逐行补齐
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int dx = r - (int) Math.round(Math.sqrt(Math.max(0.0, r * r - dy * dy)));
            // 上角
            ctx.fill(x + dx, y + i, x + w - dx, y + i + 1, color);
            // 下角（与上角对称）
            ctx.fill(x + dx, y + h - i - 1, x + w - dx, y + h - i, color);
        }
    }

    /**
     * 绘制圆角边框（仅描边）。圆角部分与 {@link #drawRoundedRect} 使用同一套 dy 映射。
     */
    public static void drawRoundedBorder(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r < 1) {
            ctx.drawBorder(x, y, w, h, color);
            return;
        }

        // 直边
        ctx.fill(x + r, y, x + w - r, y + 1, color);
        ctx.fill(x + r, y + h - 1, x + w - r, y + h, color);
        ctx.fill(x, y + r, x + 1, y + h - r, color);
        ctx.fill(x + w - 1, y + r, x + w, y + h - r, color);

        // 四角弧线
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int dx = r - (int) Math.round(Math.sqrt(Math.max(0.0, r * r - dy * dy)));
            // 左上
            ctx.fill(x + dx, y + i, x + dx + 1, y + i + 1, color);
            // 右上
            ctx.fill(x + w - dx - 1, y + i, x + w - dx, y + i + 1, color);
            // 左下
            ctx.fill(x + dx, y + h - i - 1, x + dx + 1, y + h - i, color);
            // 右下
            ctx.fill(x + w - dx - 1, y + h - i - 1, x + w - dx, y + h - i, color);
        }
    }

    /**
     * 绘制圆角面板：填充 + 1px 圆角边框。
     */
    public static void drawRoundedPanel(DrawContext ctx, int x, int y, int w, int h, int r,
                                        int fillColor, int borderColor) {
        drawRoundedRect(ctx, x, y, w, h, fillColor);
        drawRoundedBorder(ctx, x, y, w, h, r, borderColor);
    }

    // ==================== 圆角滚动条 ====================

    /**
     * 渲染圆角滚动条（轨道 + 滑块）。
     */
    public static void renderScrollbar(DrawContext ctx, int x, int y, int w, int h,
                                       double scrollVal, double maxScroll, int windowH,
                                       double mx, double my) {
        if (maxScroll <= 1) return;

        int thumbW = UIConstants.THUMB_WIDTH;
        int sbX = x + w - thumbW - 3;

        // 圆角轨道
        drawRoundedRect(ctx, sbX, y + 2, thumbW, h - 4, UIConstants.CLR_TRACK);

        // 滑块
        float trackRatio = (float) windowH / (float) (windowH + maxScroll);
        int thumbH = Math.max(UIConstants.THUMB_MIN_SIZE, (int) ((h - 4) * trackRatio));
        float scrollRatio = maxScroll <= 0 ? 0 : (float) (scrollVal / maxScroll);
        int thumbY = y + 2 + (int) ((h - 4 - thumbH) * scrollRatio);

        boolean isHov = isMouseInRect(mx, my, sbX, thumbY, thumbW, thumbH);
        int thumbClr = isHov ? UIConstants.CLR_SLIDER_HOVER : UIConstants.CLR_SLIDER;
        drawRoundedRect(ctx, sbX, thumbY, thumbW, thumbH, thumbClr);
    }

    // ==================== 内容高度 ====================

    public static int getTotalMainContentHeight(int paneW, TextRenderer tr) {
        if (PatternAndFontOverlay.isHomeSelected) {
            return HomepageRenderer.getContentHeight(paneW, tr);
        } else if (PatternAndFontOverlay.selectedH3 != null) {
            if (PatternAndFontOverlay.isYunbeiucBuiltinSelected()) {
                return YunbeiUCIntegration.getContentHeight(paneW, tr);
            }
            return getSectionContentHeight(paneW, tr);
        }
        return 0;
    }

    private static int getSectionContentHeight(int paneW, TextRenderer tr) {
        int h = 0;
        PatternAndFontOverlay.H3Category selH3 = PatternAndFontOverlay.selectedH3;

        if (selH3.headerText != null) {
            int descMaxW = paneW - 48;
            int lines = tr.wrapLines(selH3.headerText, descMaxW).size();
            h += lines * 12 + 12 + 16;
        }

        for (int secIdx = 0; secIdx < selH3.sections.size(); secIdx++) {
            h += getSectionNodeHeight(selH3.sections.get(secIdx), paneW, tr, 0);
            h += (secIdx < selH3.sections.size() - 1) ? 16 : 8;
        }
        return h + 12;
    }

    /**
     * 单个分区的内容高度（含子分区），与 {@code PatternAndFontOverlay.paintSectionNode} 的推进保持一致。
     *
     * @param indent 子分区缩进层级，顶层为 0
     */
    private static int getSectionNodeHeight(PatternAndFontOverlay.H4Section section, int paneW,
                                            TextRenderer tr, int indent) {
        PatternAndFontOverlay.H4Section eff = effective(section);
        int h = 20;

        // 收起的分区只占标题行高度
        if (!section.isExpanded) return h;

        if (eff.description != null && !eff.description.getString().isEmpty()) {
            int descMaxW = paneW - 32 - indent * 14 - 10;
            h += tr.wrapLines(eff.description, descMaxW).size() * 12 + 4;
        }

        if (section.useChildSections && !section.childSections.isEmpty()) {
            for (int i = 0; i < section.childSections.size(); i++) {
                h += getSectionNodeHeight(section.childSections.get(i), paneW, tr, indent + 1);
                if (i < section.childSections.size() - 1) h += 10;
            }
            return h + 4;
        }

        if (section.useSubfolders && !section.subFolders.isEmpty()) h += UIConstants.FILTER_PILL_HEIGHT + 8;
        if (section.useStyles && !section.subSections.isEmpty()) h += UIConstants.FILTER_PILL_HEIGHT + 8;

        if (eff.isFontMode) {
            int cardH = UIConstants.FONT_CARD_H + UIConstants.FONT_CARD_GAP;
            h += !eff.fontItems.isEmpty() ? eff.fontItems.size() * cardH : 30;
        } else if (eff.isWhitelistMode) {
            if (!eff.whitelistItems.isEmpty()) {
                int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
                int rows = (int) Math.ceil((double) eff.whitelistItems.size() / cols);
                h += rows * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y) - UIConstants.CELL_GAP_Y + 12;
            } else {
                h += 30;
            }
        } else {
            String tabKey = eff.useSubfolders && !eff.subFolders.isEmpty()
                    ? eff.subFolders.get(eff.activeTabIndex).dirName : "root";
            var textures = eff.cachedTextures.get(tabKey);
            if (textures != null && !textures.isEmpty()) {
                int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
                int rows = (int) Math.ceil((double) textures.size() / cols);
                h += rows * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y) - UIConstants.CELL_GAP_Y;
            } else {
                h += 30;
            }
        }
        return h;
    }

    public static boolean isMouseInRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    public static boolean isMouseInRectStatic(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    public static int getScreenWidth() {
        return MinecraftClient.getInstance().getWindow().getScaledWidth();
    }

    public static int getScreenHeight() {
        return MinecraftClient.getInstance().getWindow().getScaledHeight();
    }
}