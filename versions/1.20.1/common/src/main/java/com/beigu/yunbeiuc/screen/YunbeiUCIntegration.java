package com.beigu.yunbeiuc.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

/**
 * 「云北城建内置」插入方式说明页。
 *
 * <p>列出告示牌文本行支持的 3 种内置指令（-json / -rect / -texture），点击按钮即可把指令写入当前编辑行。
 *
 * @see PatternAndFontOverlay
 */
public final class YunbeiUCIntegration {

    /** 3 种插入方式的名称国际化 key。 */
    private static final String[] DISPLAY_KEYS = {
            "yunbeiuc.gui.insert.json.display",
            "yunbeiuc.gui.insert.rect.display",
            "yunbeiuc.gui.insert.texture.display"
    };
    /** 3 种插入方式的描述国际化 key。 */
    private static final String[] DESC_KEYS = {
            "yunbeiuc.gui.insert.json.desc",
            "yunbeiuc.gui.insert.rect.desc",
            "yunbeiuc.gui.insert.texture.desc"
    };
    /** 3 种插入方式的命令前缀。 */
    private static final String[] INSERT_PREFIXES = {"-json ", "-rect ", "-texture "};

    /** 当前渲染帧的悬停插入文本。 */
    public static String hoveredInsertText = null;

    private YunbeiUCIntegration() {
    }

    /**
     * 清除上一帧的悬停插入文本状态。
     */
    public static void clearHovered() {
        hoveredInsertText = null;
    }

    /**
     * 渲染插入方式说明页。
     *
     * @param context 绘制上下文
     * @param textRenderer 文本渲染器
     * @param width 屏幕宽度
     * @param height 屏幕高度
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     * @param sidebarWidth 侧边栏宽度
     */
    public static void render(DrawContext context, TextRenderer textRenderer, int width, int height,
                              int mouseX, int mouseY, int sidebarWidth) {
        double scrollY = PatternAndFontOverlay.scrollY;
        int scrollWindowStartY = UIConstants.HEADER_HEIGHT + 1;
        int scrollWindowEndY = height - UIConstants.FOOTER_HEIGHT;
        int contentStartY = scrollWindowStartY + 15 - (int) scrollY;
        int mainWidth = width - sidebarWidth;
        int currentY = contentStartY;

        context.drawText(textRenderer, Text.translatable("yunbeiuc.gui.insert.title"),
                sidebarWidth + 24, currentY, UIConstants.COLOR_SECTION_TITLE, false);
        currentY += 20;

        currentY = renderMethodButtons(context, textRenderer, mouseX, mouseY, width, mainWidth, sidebarWidth,
                currentY, scrollWindowStartY, scrollWindowEndY);

        int scrollWindowHeight = scrollWindowEndY - scrollWindowStartY;
        LayoutHelper.renderScrollbar(context, sidebarWidth, scrollWindowStartY, width - sidebarWidth,
                scrollWindowHeight, PatternAndFontOverlay.scrollY, PatternAndFontOverlay.maxScrollY,
                scrollWindowHeight, mouseX, mouseY);
    }

    /**
     * 处理插入方式按钮的点击事件。
     *
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     * @param width 屏幕宽度
     * @param height 屏幕高度
     * @param sidebarWidth 侧边栏宽度
     * @return 若点击被消费返回 {@code true}
     */
    public static boolean handleClick(int mouseX, int mouseY, int width, int height, int sidebarWidth) {
        if (hoveredInsertText != null) {
            String text = hoveredInsertText;
            hoveredInsertText = null;
            PatternAndFontOverlay.insertToCurrentLine(text);
            return true;
        }
        return false;
    }

    /**
     * 计算插入方式说明页内容总高度。
     *
     * @param mainWidth 主区域宽度
     * @param textRenderer 文本渲染器
     * @return 内容总高度（像素）
     */
    public static int getContentHeight(int mainWidth, TextRenderer textRenderer) {
        int height = 20; // 标题行高 + 标题与按钮区间距
        int btnW = 50;
        int rightMargin = 24 + btnW + 15;
        int availableWidth = mainWidth - 30 - rightMargin;

        for (int i = 0; i < DISPLAY_KEYS.length; i++) {
            Text displayText = Text.translatable(DISPLAY_KEYS[i]);
            Text descText = Text.translatable(DESC_KEYS[i]);
            int displayWidth = textRenderer.getWidth(displayText);
            int descWidth = Math.max(0, availableWidth - displayWidth - 10);
            List<OrderedText> wrappedLines = textRenderer.wrapLines(descText, descWidth);
            int rowHeight = Math.max(24, wrappedLines.size() * 10 + 14);
            height += rowHeight + 8;
        }
        return height;
    }

    /**
     * 渲染 3 种插入方式的按钮列表。
     */
    private static int renderMethodButtons(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                                           int width, int mainWidth, int sidebarWidth, int currentY,
                                           int scrollWindowStartY, int scrollWindowEndY) {
        int btnW = 50;
        int btnH = 16;
        int rightMargin = 24 + btnW + 15;
        int availableWidth = mainWidth - 30 - rightMargin;
        Text insertBtnText = Text.translatable("yunbeiuc.gui.button.insert");

        for (int i = 0; i < DISPLAY_KEYS.length; i++) {
            Text displayText = Text.translatable(DISPLAY_KEYS[i]);
            Text descText = Text.translatable(DESC_KEYS[i]);

            int displayWidth = textRenderer.getWidth(displayText);
            int descWidth = Math.max(0, availableWidth - displayWidth - 10);

            List<OrderedText> wrappedLines = textRenderer.wrapLines(descText, descWidth);
            int rowHeight = Math.max(24, wrappedLines.size() * 10 + 14);

            if (currentY + rowHeight >= scrollWindowStartY && currentY <= scrollWindowEndY) {
                int textStartY = currentY + (rowHeight - wrappedLines.size() * 10) / 2;

                context.drawText(textRenderer, displayText, sidebarWidth + 30, textStartY,
                        UIConstants.COLOR_BTN_TEXT, false);

                int descStartX = sidebarWidth + 30 + displayWidth + 10;
                for (int j = 0; j < wrappedLines.size(); j++) {
                    context.drawText(textRenderer, wrappedLines.get(j), descStartX, textStartY + j * 10,
                            UIConstants.COLOR_DESC_TEXT, false);
                }

                int btnX = width - 24 - btnW;
                int btnY = currentY + (rowHeight - btnH) / 2;
                boolean hov = LayoutHelper.isMouseInRectStatic(mouseX, mouseY, btnX, btnY, btnW, btnH)
                        && mouseY >= scrollWindowStartY && mouseY <= scrollWindowEndY;

                if (hov) {
                    hoveredInsertText = INSERT_PREFIXES[i];
                }

                context.fill(btnX, btnY, btnX + btnW, btnY + btnH,
                        hov ? UIConstants.COLOR_INSERT_BTN_BG_HOVER : UIConstants.COLOR_INSERT_BTN_BG);
                context.drawBorder(btnX, btnY, btnW, btnH, UIConstants.COLOR_INSERT_BTN_BORDER);
                context.drawText(textRenderer, insertBtnText, btnX + (btnW - textRenderer.getWidth(insertBtnText)) / 2,
                        btnY + 3, UIConstants.COLOR_BTN_TEXT, false);
            }

            currentY += rowHeight + 8;
        }
        return currentY;
    }
}
