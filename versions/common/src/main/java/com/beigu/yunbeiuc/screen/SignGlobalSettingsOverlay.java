package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.util.GlobalFontSettings;
import com.beigu.yunbeiuc.util.GlobalFontSettings.FontMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * 「云北路牌全局设置」覆盖层页面。
 *
 * <p>由文本编辑界面（{@link TextDisplayScreen}）的「设置」按钮打开；页内只有一个选项组
 * 「全局字体（仅对路牌方块新的文本行有效）」，五选一：
 * 原版uniform（不包裹JSON）/ A字体 / B字体 / C字体 / 路牌自适应。
 *
 * <p>与 {@link PatternAndFontOverlay} 相同的浮层契约：静态 {@link #isVisible} 控制显隐，
 * 渲染与输入事件由宿主界面转发；浮层自己持有并绘制按钮，不注册到宿主界面的控件列表
 * （宿主在浮层打开时不绘制自己的控件，也不处理世界中的拖拽手柄）。
 */
public final class SignGlobalSettingsOverlay {

    private static final String TITLE = "云北路牌全局设置";
    private static final String SECTION = "全局字体（仅对新放置路牌或文本行有效）";
    private static final String LABEL_VANILLA = "原版";
    private static final String LABEL_ABC_PREFIX = "交通标志专用字体";
    private static final String LABEL_A = "A字体";
    private static final String LABEL_B = "B字体";
    private static final String LABEL_C = "C字体";
    private static final String LABEL_ADAPTIVE = "自适应字体";
    private static final String LABEL_CLOSE = "关闭";
    private static final String CHECK = " ✔";

    private static final int OPTION_H = 20;
    private static final int OPTION_GAP = 6;
    private static final int ROW_GAP = 8;
    private static final int PADDING = 12;
    private static final int CLOSE_W = 72;

    // 「设置」页面背景与文本编辑界面各面板保持一致：灰色透明底 + 浅色（白）边框
    private static final int CLR_PANEL_FILL = 0xAA333333;
    private static final int CLR_PANEL_BORDER = 0xFF888888;
    private static final int CLR_TITLE = 0xFFFFFFFF;
    private static final int CLR_SECTION = 0xFF66FFCC;
    private static final int CLR_HINT = 0xFFAAAAAA;

    public static boolean isVisible = false;
    public static TextDisplayScreen targetScreen = null;

    private static final List<ButtonWidget> optionButtons = new ArrayList<>();
    private static ButtonWidget vanillaOption;
    private static ButtonWidget closeButton;

    private static int optionWidth = 80;
    private static int panelX, panelY, panelW, panelH;
    private static int titleY, sectionY, row1Y, abcLabelY, row2Y, closeY;

    private SignGlobalSettingsOverlay() {}

    // ==================== 显隐 ====================

    public static void open(TextDisplayScreen screen) {
        targetScreen = screen;
        isVisible = true;
        rebuild();
    }

    public static void close() {
        isVisible = false;
        targetScreen = null;
        optionButtons.clear();
        vanillaOption = null;
        closeButton = null;
    }

    // ==================== 控件构建 ====================

    private static void rebuild() {
        Font tr = Minecraft.getInstance().font;
        optionButtons.clear();
        int w = tr.width(LABEL_A + CHECK) + 20;
        w = Math.max(w, tr.width(LABEL_B + CHECK) + 20);
        w = Math.max(w, tr.width(LABEL_C + CHECK) + 20);
        w = Math.max(w, tr.width(LABEL_ADAPTIVE + CHECK) + 20);
        optionWidth = w;

        vanillaOption = makeOption(FontMode.VANILLA, LABEL_VANILLA, tr.width(LABEL_VANILLA + CHECK) + 20);
        optionButtons.add(vanillaOption);
        optionButtons.add(makeOption(FontMode.ABC_A, LABEL_A, optionWidth));
        optionButtons.add(makeOption(FontMode.ABC_B, LABEL_B, optionWidth));
        optionButtons.add(makeOption(FontMode.ABC_C, LABEL_C, optionWidth));
        optionButtons.add(makeOption(FontMode.ADAPTIVE, LABEL_ADAPTIVE, optionWidth));

        closeButton = ButtonWidget.builderCompat(Text.literal(LABEL_CLOSE), b -> close())
                .dimensions(0, 0, CLOSE_W, OPTION_H).build();

        refreshLabels();
        layout();
    }

    private static ButtonWidget makeOption(FontMode mode, String label, int width) {
        return ButtonWidget.builderCompat(Text.literal(label), b -> {
            GlobalFontSettings.setMode(mode);
            refreshLabels();
        }).dimensions(0, 0, width, OPTION_H).build();
    }

    /** 当前生效的模式在标签后打勾（不使用 active=false 表示选中，那会禁用点击） */
    private static void refreshLabels() {
        FontMode mode = GlobalFontSettings.getMode();
        if (vanillaOption != null) vanillaOption.setMessage(Text.literal(mark(LABEL_VANILLA, mode == FontMode.VANILLA)));
        if (optionButtons.size() < 5) return;
        optionButtons.get(1).setMessage(Text.literal(mark(LABEL_A, mode == FontMode.ABC_A)));
        optionButtons.get(2).setMessage(Text.literal(mark(LABEL_B, mode == FontMode.ABC_B)));
        optionButtons.get(3).setMessage(Text.literal(mark(LABEL_C, mode == FontMode.ABC_C)));
        optionButtons.get(4).setMessage(Text.literal(mark(LABEL_ADAPTIVE, mode == FontMode.ADAPTIVE)));
    }

    private static String mark(String label, boolean selected) {
        return selected ? label + CHECK : label;
    }

    // ==================== 布局 ====================

    private static void layout() {
        Font tr = Minecraft.getInstance().font;
        int sw = LayoutHelper.getScreenWidth();
        int sh = LayoutHelper.getScreenHeight();

        int row2W = 4 * optionWidth + 3 * OPTION_GAP;
        int contentW = Math.max(Math.max(tr.width(TITLE), tr.width(SECTION)), Math.max(vanillaOption.getWidth(), row2W));
        panelW = contentW + PADDING * 2;
        panelH = PADDING * 2 + tr.lineHeight * 3 + OPTION_H * 3 + ROW_GAP * 4;
        panelX = Math.max(0, (sw - panelW) / 2);
        panelY = Math.max(0, (sh - panelH) / 2);

        int y = panelY + PADDING;
        titleY = y; y += tr.lineHeight + ROW_GAP;
        sectionY = y; y += tr.lineHeight + ROW_GAP;
        row1Y = y; y += OPTION_H + ROW_GAP;
        abcLabelY = y; y += tr.lineHeight + ROW_GAP;
        row2Y = y; y += OPTION_H + ROW_GAP;
        closeY = y;

        vanillaOption.setPosition(panelX + PADDING, row1Y);
        int x = panelX + PADDING;
        for (int i = 1; i < optionButtons.size(); i++) {
            optionButtons.get(i).setPosition(x, row2Y);
            x += optionWidth + OPTION_GAP;
        }
        closeButton.setPosition(panelX + panelW - PADDING - CLOSE_W, closeY);
    }

    // ==================== 渲染与输入 ====================

    public static void render(DrawContext ctx, int mouseX, int mouseY) {
        if (!isVisible) return;
        Font tr = Minecraft.getInstance().font;
        layout();

        ctx.fill(panelX, panelY, panelX + panelW, panelY + panelH, CLR_PANEL_FILL);
        ctx.drawBorder(panelX, panelY, panelW, panelH, CLR_PANEL_BORDER);

        ctx.drawCenteredTextWithShadow(tr, TITLE, panelX + panelW / 2, titleY, CLR_TITLE);
        ctx.drawText(tr, SECTION, panelX + PADDING, sectionY, CLR_SECTION, false);
        ctx.drawText(tr, LABEL_ABC_PREFIX, panelX + PADDING, abcLabelY, CLR_HINT, false);

        for (ButtonWidget b : optionButtons) renderButton(ctx, b, mouseX, mouseY);
        renderButton(ctx, closeButton, mouseX, mouseY);
    }

    private static void renderButton(DrawContext ctx, ButtonWidget button, int mouseX, int mouseY) {
        if (button == null || !button.visible) return;
        button.render(ctx, mouseX, mouseY, 0f);
    }

    public static boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isVisible) return false;
        layout();
        for (ButtonWidget b : optionButtons) {
            if (b.mouseClicked(mouseX, mouseY, button)) return true;
        }
        if (closeButton != null) closeButton.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    public static boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isVisible) return false;
        for (ButtonWidget b : optionButtons) {
            if (b.mouseReleased(mouseX, mouseY, button)) return true;
        }
        if (closeButton != null) closeButton.mouseReleased(mouseX, mouseY, button);
        return true;
    }

    public static boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // 浮层内容固定高度，无滚动内容：仅吞掉滚轮，避免影响底层界面
        return isVisible;
    }

    public static boolean keyPressed(int keyCode) {
        if (!isVisible) return false;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
        }
        return true;
    }
}
