package com.beigu.yunbeiuc.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

/**
 * 「云北城建内置」插入方式说明页 — 海燕蓝主题
 *
 * @see PatternAndFontOverlay
 */
public final class YunbeiUCIntegration {

    private static final String[] DISPLAY_KEYS = {
            "yunbeiuc.gui.insert.json.display",
            "yunbeiuc.gui.insert.rect.display",
            "yunbeiuc.gui.insert.texture.display"
    };
    private static final String[] DESC_KEYS = {
            "yunbeiuc.gui.insert.json.desc",
            "yunbeiuc.gui.insert.rect.desc",
            "yunbeiuc.gui.insert.texture.desc"
    };
    private static final String[] INSERT_PREFIXES = {"-json ", "-rect ", "-texture "};

    public static String hoveredInsertText = null;

    private YunbeiUCIntegration() {
    }

    public static void clearHovered() {
        hoveredInsertText = null;
    }

    /**
     * 渲染插入方式说明页。
     */
    public static void render(DrawContext ctx, TextRenderer tr, int winW, int winH,
                              int mx, int my, int navW) {
        double scrollVal = PatternAndFontOverlay.scrollY;
        int clipTop = UIConstants.TOP_BAR_HEIGHT + 1;
        int clipBottom = winH - UIConstants.BOTTOM_BAR_HEIGHT;
        int contentY = clipTop + 15 - (int) scrollVal;
        int paneW = winW - navW;

        ctx.drawText(tr, Text.translatable("yunbeiuc.gui.insert.title"),
                navW + 24, contentY, UIConstants.CLR_HEADING, false);
        contentY += 22;

        contentY = renderMethodRows(ctx, tr, mx, my, winW, paneW, navW,
                contentY, clipTop, clipBottom);

        int clipH = clipBottom - clipTop;
        LayoutHelper.renderScrollbar(ctx, navW, clipTop, winW - navW,
                clipH, PatternAndFontOverlay.scrollY, PatternAndFontOverlay.maxScrollY,
                clipH, mx, my);
    }

    /**
     * 处理插入方式按钮的点击事件。
     *
     * @return 若点击被消费返回 {@code true}
     */
    public static boolean handleClick(int mx, int my, int winW, int winH, int navW) {
        if (hoveredInsertText != null) {
            String text = hoveredInsertText;
            hoveredInsertText = null;
            PatternAndFontOverlay.insertToCurrentLine(text);
            return true;
        }
        return false;
    }

    /**
     * 计算内容总高度。
     */
    public static int getContentHeight(int paneW, TextRenderer tr) {
        int h = 22;
        int btnW = 50;
        int rightMargin = 24 + btnW + 15;
        int availW = paneW - 30 - rightMargin;

        for (int i = 0; i < DISPLAY_KEYS.length; i++) {
            Text displayText = Text.translatable(DISPLAY_KEYS[i]);
            Text descText = Text.translatable(DESC_KEYS[i]);
            int dw = tr.getWidth(displayText);
            int descW = Math.max(0, availW - dw - 10);
            List<OrderedText> wrapped = tr.wrapLines(descText, descW);
            int rowH = Math.max(24, wrapped.size() * 10 + 14);
            h += rowH + 8;
        }
        return h;
    }

    // 渲染方法行
    private static int renderMethodRows(DrawContext ctx, TextRenderer tr, int mx, int my,
                                         int winW, int paneW, int navW, int curY,
                                         int clipTop, int clipBottom) {
        int btnW = 50;
        int btnH = 16;
        int rightMargin = 24 + btnW + 15;
        int availW = paneW - 30 - rightMargin;
        Text actLabel = Text.translatable("yunbeiuc.gui.button.insert");

        for (int i = 0; i < DISPLAY_KEYS.length; i++) {
            Text displayText = Text.translatable(DISPLAY_KEYS[i]);
            Text descText = Text.translatable(DESC_KEYS[i]);

            int dw = tr.getWidth(displayText);
            int descW = Math.max(0, availW - dw - 10);

            List<OrderedText> wrapped = tr.wrapLines(descText, descW);
            int rowH = Math.max(24, wrapped.size() * 10 + 14);

            if (curY + rowH >= clipTop && curY <= clipBottom) {
                int txtY = curY + (rowH - wrapped.size() * 10) / 2;

                ctx.drawText(tr, displayText, navW + 30, txtY,
                        UIConstants.CLR_BTN_LABEL, false);

                int descX = navW + 30 + dw + 10;
                for (int j = 0; j < wrapped.size(); j++) {
                    ctx.drawText(tr, wrapped.get(j), descX, txtY + j * 10,
                            UIConstants.CLR_BODY_TEXT, false);
                }

                int bx = winW - 24 - btnW;
                int by = curY + (rowH - btnH) / 2;
                boolean hov = LayoutHelper.isMouseInRectStatic(mx, my, bx, by, btnW, btnH)
                        && my >= clipTop && my <= clipBottom;

                if (hov) {
                    hoveredInsertText = INSERT_PREFIXES[i];
                }

                int bg = hov ? UIConstants.CLR_ACTION_HOVER : UIConstants.CLR_ACTION_FILL;
                int stroke = hov ? UIConstants.CLR_ACCENT : UIConstants.CLR_ACTION_STROKE;
                ctx.fill(bx, by, bx + btnW, by + btnH, bg);
                ctx.drawBorder(bx, by, btnW, btnH, stroke);
                ctx.drawText(tr, actLabel, bx + (btnW - tr.getWidth(actLabel)) / 2,
                        by + 3, UIConstants.CLR_BTN_LABEL, false);
            }

            curY += rowH + 8;
        }
        return curY;
    }
}
