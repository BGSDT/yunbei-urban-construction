package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.util.GlobalFontSettings;
import com.beigu.yunbeiuc.util.GlobalFontSettings.FontMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import org.lwjgl.glfw.GLFW;

/**
 * 「云北路牌全局设置」全屏页面。
 *
 * <p>由文本编辑界面（{@link TextDisplayScreen}）的「设置」按钮打开。全屏覆盖，
 * 沿用项目其它界面的方角面板与海燕蓝配色（不使用圆角）。
 *
 * <p>两块内容：
 * <ol>
 *   <li><b>全局字体</b>：原版 uniform / 交通字体 A·B·C / 路牌自适应，决定之后新生成文本行的字体；</li>
 *   <li><b>一键转换</b>：把已放置路牌「自带行」的字体标签统一改写为自适应字体或原版字体
 *       （自适应按每行各自的 a/b/c；原版则加粗）。用户自己新增的行不参与。</li>
 * </ol>
 *
 * <p>内容区比视口高，因此带滚动条：滚轮滚动，也可以按住滚动条拖动。
 *
 * <p>契约与 {@link PatternAndFontOverlay} 相同：静态 {@link #isVisible} 控制显隐，
 * 渲染与输入事件由宿主界面转发；本页自行绘制与命中测试。
 */
public final class SignGlobalSettingsOverlay {

    private static final String TITLE = "云北路牌全局设置";

    private static final String FONT_SECTION = "全局字体";
    private static final String FONT_HINT = "保存后应用到当前路牌文本，并作为后续新文本行的默认字体";

    private static final String CONVERT_SECTION = "一键转换自带标签文本";
    private static final String CONVERT_HINT = "按每行自带的 a/b/c 字体改写；原版字体为加粗纯文本";
    private static final String CONVERT_ADAPTIVE = "全部转为自适应字体";
    private static final String CONVERT_VANILLA = "全部转为原版字体";
    private static final String SAVE_SETTINGS = "保存设置";
    private static final String FOOTER_HINT = "Ctrl+S / Enter 保存，Esc 取消；图片与矩形指令行不受影响";

    private static final String[] OPTION_LABELS = {"原版 uniform", "交通字体 A", "交通字体 B", "交通字体 C", "路牌自适应"};
    private static final String[] OPTION_NOTES = {"加粗纯文本", "traf_sign_font_a", "traf_sign_font_b", "traf_sign_font_c", "按每行自带字体"};
    private static final FontMode[] OPTION_MODES = {FontMode.VANILLA, FontMode.ABC_A, FontMode.ABC_B, FontMode.ABC_C, FontMode.ADAPTIVE};
    private static final int OPTION_COUNT = OPTION_LABELS.length;

    private static final int DIM = 0x9C000000;
    private static final int PAD = 20;
    private static final int HEADER_H = 20;
    private static final int SECTION_TITLE_H = 16;
    private static final int SECTION_HINT_H = 11;
    private static final int OPTION_H = 20;
    private static final int OPTION_GAP = 4;
    private static final int ACTION_H = 22;
    private static final int ACTION_GAP = 10;
    private static final int CLOSE_SIZE = 16;
    private static final int CARD_W_MAX = 560;
    private static final int VIEWPORT_MAX_H = 168;
    private static final int FOOTER_H = 11;
    private static final int ACCENT_BAR_W = 3;
    private static final int SCROLLBAR_RESERVE = 10;

    public static boolean isVisible = false;
    public static TextDisplayScreen targetScreen = null;

    private static String statusMessage = "";
    private static int statusColor = UIConstants.CLR_MUTED;

    private static boolean hoverClose = false;
    private static boolean hoverSave = false;
    private static int hoverOption = -1;
    private static int hoverAction = -1;
    private static FontMode pendingMode = FontMode.VANILLA;

    // 滚动
    private static int scrollOffset = 0;
    private static int maxScroll = 0;
    private static int contentHeight = 0;
    private static int viewportX, viewportY, viewportW, viewportH;
    private static int thumbX, thumbY, thumbW, thumbH;
    private static boolean draggingThumb = false;
    private static int dragStartMouseY = 0;
    private static int dragStartScroll = 0;

    // 布局结果（已含滚动偏移）
    private static int cardX, cardY, cardW, cardH;
    private static int contentX, contentW;
    private static int titleY, closeX, closeY;
    private static int fontSectionY, fontHintY;
    private static final int[] optionY = new int[OPTION_COUNT];
    private static int convertSectionY, convertHintY;
    private static int adaptiveBtnX, adaptiveBtnY, vanillaBtnX, actionBtnW;
    private static int statusY, saveBtnX, saveBtnY, saveBtnW, footerY;

    private SignGlobalSettingsOverlay() {}

    // ==================== 显隐 ====================

    public static void open(TextDisplayScreen screen) {
        targetScreen = screen;
        isVisible = true;
        statusMessage = "";
        statusColor = UIConstants.CLR_MUTED;
        pendingMode = GlobalFontSettings.getMode();
        scrollOffset = 0;
        draggingThumb = false;
        hoverSave = false;
        hoverOption = -1;
        hoverAction = -1;
        hoverClose = false;
    }

    public static void close() {
        isVisible = false;
        targetScreen = null;
        statusMessage = "";
        draggingThumb = false;
        hoverSave = false;
        hoverOption = -1;
        hoverAction = -1;
        hoverClose = false;
    }

    // ==================== 一键转换 ====================

    private static void runConvert(boolean adaptive) {
        if (targetScreen == null) return;
        int converted = targetScreen.convertBuiltinFontTags(adaptive);
        pendingMode = GlobalFontSettings.getMode();
        if (converted > 0) {
            statusMessage = "已转换 " + converted + " 行自带标签文本 → "
                    + (adaptive ? "自适应字体" : "原版字体（加粗）");
            statusColor = UIConstants.CLR_ACCENT;
        } else {
            statusMessage = "没有找到可转换的自带标签文本行";
            statusColor = UIConstants.CLR_MUTED;
        }
    }

    private static void saveAndClose() {
        GlobalFontSettings.setMode(pendingMode);
        if (targetScreen != null) targetScreen.applyGlobalFontSettingToAllLines();
        close();
    }

    // ==================== 布局 ====================

    private static int computeContentHeight() {
        return SECTION_TITLE_H + SECTION_HINT_H + 6
                + OPTION_COUNT * OPTION_H + (OPTION_COUNT - 1) * OPTION_GAP
                + 14
                + SECTION_TITLE_H + SECTION_HINT_H + 6
                + ACTION_H + 8 + 12;
    }

    private static void layout() {
        int sw = LayoutHelper.getScreenWidth();
        int sh = LayoutHelper.getScreenHeight();

        cardW = Math.min(CARD_W_MAX, Math.max(300, sw - 40));
        contentW = cardW - PAD * 2 - SCROLLBAR_RESERVE;
        contentHeight = computeContentHeight();
        viewportH = Math.min(contentHeight, Math.min(VIEWPORT_MAX_H, Math.max(60, sh - 150)));
        maxScroll = Math.max(0, contentHeight - viewportH);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        cardH = PAD + HEADER_H + 8 + 1 + 8 + viewportH + 8 + ACTION_H + 6 + FOOTER_H + PAD;
        cardX = Math.max(10, (sw - cardW) / 2);
        cardY = Math.max(10, (sh - cardH) / 2);
        contentX = cardX + PAD;
        viewportX = contentX;
        viewportY = cardY + PAD + HEADER_H + 8 + 1 + 8;
        viewportW = cardW - PAD * 2;

        titleY = cardY + PAD + (HEADER_H - 8) / 2;
        closeX = cardX + cardW - PAD - CLOSE_SIZE;
        closeY = cardY + PAD + (HEADER_H - CLOSE_SIZE) / 2;

        // 内容区内的相对布局，再减去滚动偏移得到屏幕坐标
        int y = viewportY - scrollOffset;
        fontSectionY = y; y += SECTION_TITLE_H;
        fontHintY = y; y += SECTION_HINT_H + 6;
        for (int i = 0; i < OPTION_COUNT; i++) {
            optionY[i] = y;
            y += OPTION_H + (i < OPTION_COUNT - 1 ? OPTION_GAP : 0);
        }
        y += 14;
        convertSectionY = y; y += SECTION_TITLE_H;
        convertHintY = y; y += SECTION_HINT_H + 6;
        adaptiveBtnY = y;
        actionBtnW = (contentW - ACTION_GAP) / 2;
        adaptiveBtnX = contentX;
        vanillaBtnX = contentX + actionBtnW + ACTION_GAP;
        y += ACTION_H + 8;
        statusY = y;

        saveBtnX = contentX;
        saveBtnY = viewportY + viewportH + 8;
        saveBtnW = viewportW;
        footerY = saveBtnY + ACTION_H + 6;

        // 滚动条滑块
        thumbW = UIConstants.THUMB_WIDTH;
        thumbX = viewportX + viewportW - thumbW - 3;
        if (maxScroll > 0) {
            float ratio = (float) viewportH / (float) contentHeight;
            thumbH = Math.max(UIConstants.THUMB_MIN_SIZE, (int) ((viewportH - 4) * ratio));
            float scrollRatio = (float) scrollOffset / (float) maxScroll;
            thumbY = viewportY + 2 + (int) ((viewportH - 4 - thumbH) * scrollRatio);
        } else {
            thumbH = viewportH - 4;
            thumbY = viewportY + 2;
        }
    }

    private static boolean inViewport(double mx, double my, int y, int h) {
        return LayoutHelper.isMouseInRect(mx, my, viewportX, y, contentW, h)
                && my >= viewportY && my <= viewportY + viewportH;
    }

    // ==================== 渲染 ====================

    public static void render(DrawContext ctx, int mouseX, int mouseY) {
        if (!isVisible) return;
        Font tr = Minecraft.getInstance().font;
        layout();
        updateHover(mouseX, mouseY);

        // 全屏遮罩：本页是全屏页面，背后不再显示文本编辑界面
        ctx.fill(0, 0, LayoutHelper.getScreenWidth(), LayoutHelper.getScreenHeight(), DIM);

        // 卡片（方角）
        ctx.fill(cardX, cardY, cardX + cardW, cardY + cardH, UIConstants.CLR_CONTENT_BG);
        ctx.drawBorder(cardX, cardY, cardW, cardH, UIConstants.CLR_CONTENT_BORDER);

        // 标题 + 关闭
        ctx.fill(contentX, titleY - 1, contentX + ACCENT_BAR_W, titleY + 9, UIConstants.CLR_ACCENT);
        ctx.drawText(tr, TITLE, contentX + ACCENT_BAR_W + 6, titleY, UIConstants.CLR_HEADING, false);

        int closeFill = hoverClose ? UIConstants.CLR_BTN_HOVER : UIConstants.CLR_BTN_FILL;
        ctx.fill(closeX, closeY, closeX + CLOSE_SIZE, closeY + CLOSE_SIZE, closeFill);
        ctx.drawBorder(closeX, closeY, CLOSE_SIZE, CLOSE_SIZE, UIConstants.CLR_BTN_STROKE);
        ctx.drawCenteredTextWithShadow(tr, "×", closeX + CLOSE_SIZE / 2, closeY + (CLOSE_SIZE - 8) / 2,
                UIConstants.CLR_BTN_LABEL);

        int headerDivider = viewportY - 5;
        ctx.fill(contentX, headerDivider, contentX + viewportW, headerDivider + 1, UIConstants.CLR_CONTENT_DIVIDER);

        // ---- 可滚动内容区 ----
        ctx.enableScissor(viewportX, viewportY, viewportX + viewportW, viewportY + viewportH);

        drawSectionTitle(ctx, tr, FONT_SECTION, fontSectionY);
        ctx.drawText(tr, FONT_HINT, contentX, fontHintY, UIConstants.CLR_MUTED, false);

        for (int i = 0; i < OPTION_COUNT; i++) {
            drawOptionRow(ctx, tr, i, optionY[i], OPTION_MODES[i] == pendingMode, hoverOption == i);
        }

        drawSectionTitle(ctx, tr, CONVERT_SECTION, convertSectionY);
        ctx.drawText(tr, CONVERT_HINT, contentX, convertHintY, UIConstants.CLR_MUTED, false);
        drawActionButton(ctx, tr, CONVERT_ADAPTIVE, adaptiveBtnX, adaptiveBtnY, actionBtnW, hoverAction == 0);
        drawActionButton(ctx, tr, CONVERT_VANILLA, vanillaBtnX, adaptiveBtnY, actionBtnW, hoverAction == 1);
        if (!statusMessage.isEmpty()) {
            ctx.drawText(tr, statusMessage, contentX, statusY, statusColor, false);
        }

        ctx.disableScissor();

        drawActionButton(ctx, tr, SAVE_SETTINGS, saveBtnX, saveBtnY, saveBtnW, hoverSave);

        // 滚动条
        if (maxScroll > 0) {
            ctx.fill(thumbX, viewportY + 2, thumbX + thumbW, viewportY + viewportH - 2, UIConstants.CLR_TRACK);
            boolean thumbHover = draggingThumb
                    || LayoutHelper.isMouseInRect(mouseX, mouseY, thumbX, thumbY, thumbW, thumbH);
            ctx.fill(thumbX, thumbY, thumbX + thumbW, thumbY + thumbH,
                    thumbHover ? UIConstants.CLR_SLIDER_HOVER : UIConstants.CLR_SLIDER);
        }

        ctx.drawText(tr, FOOTER_HINT, contentX, footerY, UIConstants.CLR_MUTED, false);
    }

    private static void drawSectionTitle(DrawContext ctx, Font tr, String text, int y) {
        ctx.fill(contentX, y + 3, contentX + ACCENT_BAR_W, y + 12, UIConstants.CLR_ACCENT);
        ctx.drawText(tr, text, contentX + ACCENT_BAR_W + 6, y + 3, UIConstants.CLR_ACCENT, false);
    }

    /** 单行选项卡：选中态用填充 + 左侧强调条，悬停态用浅色底（方角）。 */
    private static void drawOptionRow(DrawContext ctx, Font tr, int index, int y, boolean selected, boolean hovered) {
        int fill = selected ? UIConstants.CLR_NAVItemSelected
                : (hovered ? UIConstants.CLR_NAV_PILL_HOVER : UIConstants.CLR_CELL_FILL);
        ctx.fill(contentX, y, contentX + contentW, y + OPTION_H, fill);
        ctx.drawBorder(contentX, y, contentW, OPTION_H, selected ? UIConstants.CLR_ACCENT : UIConstants.CLR_CELL_OUTLINE);
        if (selected) {
            ctx.fill(contentX, y + 1, contentX + ACCENT_BAR_W, y + OPTION_H - 1, UIConstants.CLR_ACCENT);
        }
        int textY = y + (OPTION_H - 8) / 2;
        ctx.drawText(tr, OPTION_LABELS[index], contentX + 12, textY,
                selected ? UIConstants.CLR_HEADING : UIConstants.CLR_BODY_TEXT, false);
        String note = OPTION_NOTES[index];
        ctx.drawText(tr, note, contentX + contentW - 10 - tr.width(note), textY, UIConstants.CLR_MUTED, false);
    }

    private static void drawActionButton(DrawContext ctx, Font tr, String label, int x, int y, int w, boolean hovered) {
        ctx.fill(x, y, x + w, y + ACTION_H, hovered ? UIConstants.CLR_ACTION_HOVER : UIConstants.CLR_ACTION_FILL);
        ctx.drawBorder(x, y, w, ACTION_H, UIConstants.CLR_ACTION_STROKE);
        ctx.drawCenteredTextWithShadow(tr, label, x + w / 2, y + (ACTION_H - 8) / 2, UIConstants.CLR_BTN_LABEL);
    }

    private static void updateHover(int mouseX, int mouseY) {
        hoverClose = LayoutHelper.isMouseInRect(mouseX, mouseY, closeX, closeY, CLOSE_SIZE, CLOSE_SIZE);
        hoverSave = LayoutHelper.isMouseInRect(mouseX, mouseY, saveBtnX, saveBtnY, saveBtnW, ACTION_H);
        hoverOption = -1;
        for (int i = 0; i < OPTION_COUNT; i++) {
            if (inViewport(mouseX, mouseY, optionY[i], OPTION_H)) {
                hoverOption = i;
                break;
            }
        }
        hoverAction = -1;
        if (inViewport(mouseX, mouseY, adaptiveBtnY, ACTION_H)) {
            if (LayoutHelper.isMouseInRect(mouseX, mouseY, adaptiveBtnX, adaptiveBtnY, actionBtnW, ACTION_H)) {
                hoverAction = 0;
            } else if (LayoutHelper.isMouseInRect(mouseX, mouseY, vanillaBtnX, adaptiveBtnY, actionBtnW, ACTION_H)) {
                hoverAction = 1;
            }
        }
    }

    private static void scrollBy(int amount) {
        scrollOffset = Math.max(0, Math.min(scrollOffset + amount, maxScroll));
    }

    // ==================== 输入 ====================

    public static boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isVisible) return false;
        layout();
        updateHover((int) mouseX, (int) mouseY);
        if (button != 0) return true;

        if (hoverClose) {
            close();
            return true;
        }
        if (maxScroll > 0 && LayoutHelper.isMouseInRect(mouseX, mouseY, thumbX, thumbY, thumbW, thumbH)) {
            draggingThumb = true;
            dragStartMouseY = (int) mouseY;
            dragStartScroll = scrollOffset;
            return true;
        }
        if (hoverOption >= 0) {
            pendingMode = OPTION_MODES[hoverOption];
            statusMessage = "设置已修改，点击“保存设置”后生效";
            statusColor = UIConstants.CLR_MUTED;
            return true;
        }
        if (hoverSave) {
            saveAndClose();
            return true;
        }
        if (hoverAction == 0) {
            runConvert(true);
            return true;
        }
        if (hoverAction == 1) {
            runConvert(false);
            return true;
        }
        // 全屏页面：卡片外的点击也一并吞掉，避免影响底层界面
        return true;
    }

    /** 拖动滚动条滑块。 */
    public static boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (!isVisible) return false;
        if (!draggingThumb || maxScroll <= 0) return true;
        int track = Math.max(1, viewportH - 4 - thumbH);
        int delta = (int) mouseY - dragStartMouseY;
        scrollOffset = Math.max(0, Math.min(dragStartScroll + (int) ((float) delta / track * maxScroll), maxScroll));
        return true;
    }

    public static boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingThumb = false;
        return isVisible;
    }

    public static boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isVisible) return false;
        layout();
        if (maxScroll > 0) scrollBy((int) (-amount * UIConstants.WHEEL_STEP));
        return true;
    }

    public static boolean keyPressed(int keyCode, int modifiers) {
        if (!isVisible) return false;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
        } else if (keyCode == GLFW.GLFW_KEY_ENTER
                || (keyCode == GLFW.GLFW_KEY_S && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0)) {
            saveAndClose();
        }
        return true;
    }
}
