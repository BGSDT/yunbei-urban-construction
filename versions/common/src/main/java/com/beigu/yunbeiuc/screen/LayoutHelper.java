package com.beigu.yunbeiuc.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

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

    public static int getHomeCardHeight() {
        return SidebarState.isCollapsed() ? UIConstants.NAV_ICON_ROW_H
                : UIConstants.CARD_HEADER_H;
    }

    public static int calculateH2CardHeight(PatternAndFontOverlay.H2Category h2, Font tr) {
        if (SidebarState.isCollapsed()) return UIConstants.NAV_ICON_ROW_H;
        int h = UIConstants.CARD_HEADER_H;
        if (h2.isExpanded && !h2.subCategories.isEmpty()) {
            h += UIConstants.CARD_INNER_TOP;
            h += PatternAndFontOverlay.collectNavRows(h2).size() * UIConstants.CARD_ITEM_H;
            h += UIConstants.CARD_INNER_BOTTOM;
        }
        return h;
    }

    public static int getSidebarTopItemHeight(Component text, Font tr) {
        return getHomeCardHeight();
    }

    public static int getCategoryHeight(Component title, String prefix, int indent, Font tr) {
        return UIConstants.CARD_ITEM_H;
    }

    public static int getH3ItemHeight(Component title, Font tr) {
        return UIConstants.CARD_ITEM_H;
    }

    public static int calculateH2PanelHeight(PatternAndFontOverlay.H2Category h2, Font tr) {
        return calculateH2CardHeight(h2, tr);
    }

    public static int calculateH3Height(PatternAndFontOverlay.H3Category h3, int indent, Font tr) {
        return UIConstants.CARD_ITEM_H;
    }

    public static int calculateH4Height(PatternAndFontOverlay.H4Section section, int indent, Font tr) {
        return UIConstants.CARD_ITEM_H;
    }

    public static int renderH4SectionDynamic(DrawContext ctx, Font tr, double mx, double my,
                                             PatternAndFontOverlay.H4Section section, int indent, int y,
                                             PatternAndFontOverlay.H3Category parentH3) {
        return y + UIConstants.CARD_ITEM_H;
    }

    public static int renderH3CategoryDynamic(DrawContext ctx, Font tr, double mx, double my,
                                              PatternAndFontOverlay.H3Category h3, int indent, int y) {
        return y + UIConstants.CARD_ITEM_H;
    }

    public static Integer handleH3CategoryClick(double mx, double my, PatternAndFontOverlay.H2Category h2,
                                                PatternAndFontOverlay.H3Category h3, int indent, int y, Font tr) {
        return null;
    }

    public static Integer handleH4SectionClick(double mx, double my, PatternAndFontOverlay.H2Category h2,
                                               PatternAndFontOverlay.H3Category parentH3,
                                               PatternAndFontOverlay.H4Section section, int indent, int y, Font tr) {
        return null;
    }

    public static void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) return;
        int r = Math.min(Math.min(w, h) / 2, 6);
        if (r < 1) { ctx.fill(x, y, x + w, y + h, color); return; }

        ctx.fill(x + r, y, x + w - r, y + h, color);
        ctx.fill(x, y + r, x + r, y + h - r, color);
        ctx.fill(x + w - r, y + r, x + w, y + h - r, color);

        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int dx = r - (int) Math.round(Math.sqrt(Math.max(0.0, r * r - dy * dy)));
            ctx.fill(x + dx, y + i, x + w - dx, y + i + 1, color);
            ctx.fill(x + dx, y + h - i - 1, x + w - dx, y + h - i, color);
        }
    }

    public static void drawRoundedBorder(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r < 1) {
            ctx.drawBorder(x, y, w, h, color);
            return;
        }

        ctx.fill(x + r, y, x + w - r, y + 1, color);
        ctx.fill(x + r, y + h - 1, x + w - r, y + h, color);
        ctx.fill(x, y + r, x + 1, y + h - r, color);
        ctx.fill(x + w - 1, y + r, x + w, y + h - r, color);

        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int dx = r - (int) Math.round(Math.sqrt(Math.max(0.0, r * r - dy * dy)));
            ctx.fill(x + dx, y + i, x + dx + 1, y + i + 1, color);
            ctx.fill(x + w - dx - 1, y + i, x + w - dx, y + i + 1, color);
            ctx.fill(x + dx, y + h - i - 1, x + dx + 1, y + h - i, color);
            ctx.fill(x + w - dx - 1, y + h - i - 1, x + w - dx, y + h - i, color);
        }
    }

    public static void drawRoundedPanel(DrawContext ctx, int x, int y, int w, int h, int r,
                                        int fillColor, int borderColor) {
        drawRoundedRect(ctx, x, y, w, h, fillColor);
        drawRoundedBorder(ctx, x, y, w, h, r, borderColor);
    }

    public static void renderScrollbar(DrawContext ctx, int x, int y, int w, int h,
                                       double scrollVal, double maxScroll, int windowH,
                                       double mx, double my) {
        if (maxScroll <= 1) return;

        int thumbW = UIConstants.THUMB_WIDTH;
        int sbX = x + w - thumbW - 3;

        drawRoundedRect(ctx, sbX, y + 2, thumbW, h - 4, UIConstants.CLR_TRACK);

        float trackRatio = (float) windowH / (float) (windowH + maxScroll);
        int thumbH = Math.max(UIConstants.THUMB_MIN_SIZE, (int) ((h - 4) * trackRatio));
        float scrollRatio = maxScroll <= 0 ? 0 : (float) (scrollVal / maxScroll);
        int thumbY = y + 2 + (int) ((h - 4 - thumbH) * scrollRatio);

        boolean isHov = isMouseInRect(mx, my, sbX, thumbY, thumbW, thumbH);
        int thumbClr = isHov ? UIConstants.CLR_SLIDER_HOVER : UIConstants.CLR_SLIDER;
        drawRoundedRect(ctx, sbX, thumbY, thumbW, thumbH, thumbClr);
    }

    public static int getTotalMainContentHeight(int paneW, Font tr) {
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

    private static int getSectionContentHeight(int paneW, Font tr) {
        int h = 0;
        PatternAndFontOverlay.H3Category selH3 = PatternAndFontOverlay.selectedH3;

        if (selH3.headerText != null) {
            h += PatternAndFontOverlay.headerCardHeight(tr, selH3.headerText, paneW)
                    + PatternAndFontOverlay.HEADER_CARD_GAP;
        }

        for (int secIdx = 0; secIdx < selH3.sections.size(); secIdx++) {
            h += getSectionNodeHeight(selH3.sections.get(secIdx), paneW, tr, 0);
            h += (secIdx < selH3.sections.size() - 1) ? 16 : 8;
        }
        return h + 12;
    }

    private static int getSectionNodeHeight(PatternAndFontOverlay.H4Section section, int paneW,
                                            Font tr, int indent) {
        PatternAndFontOverlay.H4Section eff = effective(section);
        int h = 20;

        if (!section.isExpanded) return h;

        if (eff.description != null && !eff.description.getString().isEmpty()) {
            int descMaxW = paneW - 32 - indent * 14 - 10;
            h += tr.split(eff.description, descMaxW).size() * 12 + 4;
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
        } else if (eff.isLabeledMode) {
            h += GridRenderer.labeledHeight(tr, paneW, eff.labeledItems);
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
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public static int getScreenHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }
}
