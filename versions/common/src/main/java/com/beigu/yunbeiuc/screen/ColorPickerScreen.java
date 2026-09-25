package com.beigu.yunbeiuc.screen;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.awt.Color;
import java.util.function.Consumer;

/**
 * 通用 HSB 色盘选择界面：饱和度/亮度方块 + 色相条 + 十六进制/RGB 输入框，仿照 MTR 的
 * WidgetColorSelector 设计，使用当前版本适配层提供的原版 GUI API
 * （Screen/ButtonWidget/TextFieldWidget/DrawContext）重新实现——MTR 原版依赖其自有的
 * ButtonMapper/ScreenMapper/Tesselator 等抽象类，本项目未引入，无法直接复用。
 * 关闭方式：点击"确定"才会通过 callback 回传选中颜色（0xRRGGBB）；点击"取消"或按 ESC
 * 直接返回上一界面，不触发 callback，颜色不变。
 */
public class ColorPickerScreen extends Screen {
    private static final int SQUARE_SIZE = 80;
    private static final int SQUARE_STEP = 4;
    private static final int HUE_STRIP_WIDTH = 16;
    private static final int HUE_STRIP_GAP = 8;
    private static final int HUE_STRIP_STEP = 4;
    private static final int PANEL_PADDING = 12;
    private static final int TITLE_HEIGHT = 20;
    private static final int PREVIEW_SIZE = 40;
    private static final int BUTTON_HEIGHT = 18;
    private static final int HEX_FIELD_WIDTH = 70;
    private static final int RIGHT_COL_WIDTH = 70;

    private final Screen previousScreen;
    private final int initialColor;
    private final Consumer<Integer> callback;

    private float hue;
    private float saturation;
    private float brightness;

    private int panelX, panelY, panelWidth, panelHeight;
    private int squareX, squareY;
    private int hueStripX, hueStripY;

    private enum DraggingState { NONE, SATURATION_BRIGHTNESS, HUE }
    private DraggingState draggingState = DraggingState.NONE;

    private TextFieldWidget hexField;
    private boolean syncingFields = false;

    public ColorPickerScreen(Screen previousScreen, int initialColor, Consumer<Integer> callback) {
        super(new TextComponent("选择颜色"));
        this.previousScreen = previousScreen;
        this.initialColor = initialColor & 0xFFFFFF;
        this.callback = callback;
        float[] hsb = Color.RGBtoHSB((this.initialColor >> 16) & 0xFF, (this.initialColor >> 8) & 0xFF, this.initialColor & 0xFF, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    @Override
    protected void init() {
        super.init();
        if (this.client == null) return;

        panelWidth = SQUARE_SIZE + HUE_STRIP_GAP + HUE_STRIP_WIDTH + PANEL_PADDING * 3 + RIGHT_COL_WIDTH;
        panelHeight = TITLE_HEIGHT + PANEL_PADDING + SQUARE_SIZE + PANEL_PADDING + 40;
        panelX = (this.width - panelWidth) / 2;
        panelY = (this.height - panelHeight) / 2;

        squareX = panelX + PANEL_PADDING;
        squareY = panelY + TITLE_HEIGHT + PANEL_PADDING;

        hueStripX = squareX + SQUARE_SIZE + HUE_STRIP_GAP;
        hueStripY = squareY;

        int rightColX = hueStripX + HUE_STRIP_WIDTH + PANEL_PADDING;
        int rightColY = squareY;

        hexField = new TextFieldWidget(this.textRenderer, rightColX, rightColY + 2, HEX_FIELD_WIDTH, 16, new TextComponent(""));
        hexField.setMaxLength(7);
        hexField.setText("#" + String.format("%06X", getCurrentColor()));
        hexField.setChangedListener(text -> {
            if (syncingFields) return;
            if (text.startsWith("#") && text.length() == 7) {
                try {
                    int parsed = Integer.parseInt(text.substring(1), 16);
                    setFromRGB((parsed >> 16) & 0xFF, (parsed >> 8) & 0xFF, parsed & 0xFF);
                } catch (NumberFormatException ignored) {}
            }
        });
        this.addDrawableChild(hexField);

        int btnY = panelY + panelHeight - PANEL_PADDING - BUTTON_HEIGHT;
        int btnWidth = (panelWidth - PANEL_PADDING * 3) / 2;

        ButtonWidget confirmButton = ButtonWidget.builder(new TextComponent("确定"), button -> {
            callback.accept(getCurrentColor());
            Minecraft.getInstance().setScreen(previousScreen);
        }).dimensions(panelX + PANEL_PADDING, btnY, btnWidth, BUTTON_HEIGHT).build();
        this.addDrawableChild(confirmButton);

        ButtonWidget cancelButton = ButtonWidget.builder(new TextComponent("取消"), button -> {
            Minecraft.getInstance().setScreen(previousScreen);
        }).dimensions(panelX + PANEL_PADDING * 2 + btnWidth, btnY, btnWidth, BUTTON_HEIGHT).build();
        this.addDrawableChild(cancelButton);
    }

    private int getCurrentColor() {
        return Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;
    }

    private void setFromRGB(int r, int g, int b) {
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        updateFieldsFromHSB();
    }

    private void updateFieldsFromHSB() {
        syncingFields = true;
        int rgb = getCurrentColor();
        hexField.setText("#" + String.format("%06X", rgb));
        syncingFields = false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client == null) return;

        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA333333);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, panelX + panelWidth / 2, panelY + 6, 0xFFCCCCCC);

        for (int y = 0; y < SQUARE_SIZE; y += SQUARE_STEP) {
            for (int x = 0; x < SQUARE_SIZE; x += SQUARE_STEP) {
                float s = x / (float) SQUARE_SIZE;
                float b = 1.0f - (y / (float) SQUARE_SIZE);
                int color = Color.HSBtoRGB(hue, s, b) & 0xFFFFFF;
                context.fill(squareX + x, squareY + y, squareX + x + SQUARE_STEP, squareY + y + SQUARE_STEP, 0xFF000000 | color);
            }
        }
        context.drawBorder(squareX, squareY, SQUARE_SIZE, SQUARE_SIZE, 0xFF888888);

        int markerX = squareX + (int) (saturation * SQUARE_SIZE);
        int markerY = squareY + (int) ((1.0f - brightness) * SQUARE_SIZE);
        markerX = Mth.clamp(markerX, squareX, squareX + SQUARE_SIZE - 1);
        markerY = Mth.clamp(markerY, squareY, squareY + SQUARE_SIZE - 1);
        context.drawBorder(markerX - 2, markerY - 2, 5, 5, 0xFFFFFFFF);
        context.drawBorder(markerX - 3, markerY - 3, 7, 7, 0xFF000000);

        for (int y = 0; y < SQUARE_SIZE; y += HUE_STRIP_STEP) {
            float hueVal = y / (float) SQUARE_SIZE;
            int color = Color.HSBtoRGB(hueVal, 1.0f, 1.0f) & 0xFFFFFF;
            context.fill(hueStripX, hueStripY + y, hueStripX + HUE_STRIP_WIDTH, hueStripY + y + HUE_STRIP_STEP, 0xFF000000 | color);
        }
        context.drawBorder(hueStripX, hueStripY, HUE_STRIP_WIDTH, SQUARE_SIZE, 0xFF888888);

        int hueMarkerY = hueStripY + (int) (hue * SQUARE_SIZE);
        hueMarkerY = Mth.clamp(hueMarkerY, hueStripY, hueStripY + SQUARE_SIZE - 1);
        context.fill(hueStripX - 1, hueMarkerY - 1, hueStripX + HUE_STRIP_WIDTH + 1, hueMarkerY + 2, 0xFFFFFFFF);

        int rightColX = hueStripX + HUE_STRIP_WIDTH + PANEL_PADDING;
        int rightColY = squareY;
        context.drawTextWithShadow(this.textRenderer, "十六进制", rightColX, rightColY - 10, 0xFFCCCCCC);

        int previewX = rightColX + 2;
        int previewY = rightColY + 22;
        context.fill(previewX, previewY, previewX + PREVIEW_SIZE, previewY + PREVIEW_SIZE, 0xFF000000 | getCurrentColor());
        context.drawBorder(previewX, previewY, PREVIEW_SIZE, PREVIEW_SIZE, 0xFF888888);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (mouseX >= squareX && mouseX < squareX + SQUARE_SIZE && mouseY >= squareY && mouseY < squareY + SQUARE_SIZE) {
                draggingState = DraggingState.SATURATION_BRIGHTNESS;
                updateSaturationBrightness(mouseX, mouseY);
                return true;
            }
            if (mouseX >= hueStripX && mouseX < hueStripX + HUE_STRIP_WIDTH && mouseY >= hueStripY && mouseY < hueStripY + SQUARE_SIZE) {
                draggingState = DraggingState.HUE;
                updateHue(mouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingState == DraggingState.SATURATION_BRIGHTNESS) {
            updateSaturationBrightness(mouseX, mouseY);
            return true;
        }
        if (draggingState == DraggingState.HUE) {
            updateHue(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingState = DraggingState.NONE;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateSaturationBrightness(double mouseX, double mouseY) {
        double relX = Mth.clamp(mouseX - squareX, 0.0, SQUARE_SIZE);
        double relY = Mth.clamp(mouseY - squareY, 0.0, SQUARE_SIZE);
        this.saturation = (float) (relX / SQUARE_SIZE);
        this.brightness = 1.0f - (float) (relY / SQUARE_SIZE);
        updateFieldsFromHSB();
    }

    private void updateHue(double mouseY) {
        double relY = Mth.clamp(mouseY - hueStripY, 0.0, SQUARE_SIZE);
        this.hue = (float) (relY / SQUARE_SIZE);
        updateFieldsFromHSB();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            Minecraft.getInstance().setScreen(previousScreen);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
