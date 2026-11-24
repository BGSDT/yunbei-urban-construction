package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.ApiCompat;
import com.beigu.yunbeiuc.api.gui.GuiPlatform;
import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.network.NetworkCompat;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsApplyAllConditionsPacket;
import com.beigu.yunbeiuc.network.TrafficLightsCountdownUpdatePacket;
import com.beigu.yunbeiuc.network.TrafficLightsPulseUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// 红绿灯附加条件设置界面：一行一个附加条件，左侧文字、右侧开关或输入框。

public class TrafficLightsConditionScreen extends Screen {
    private static final GuiPlatform GUI = VersionServices.gui();
    private static final RenderPlatform RENDER = VersionServices.render();

    private final BlockPos pos;

    private boolean pulseEnabled = false;
    private boolean fullDisplay = false;
    private int redFrom = 15;
    private int greenFrom = 15;
    private int numberMode = 0;
    private boolean hidePatternOnGreen = false;
    private boolean yellowFlashAlways = false;

    private ButtonWidget pulseButton;
    private ButtonWidget displayButton;
    private ButtonWidget numberModeButton;
    private TextFieldWidget redField;
    private TextFieldWidget greenField;
    private ButtonWidget hidePatternButton;
    private ButtonWidget yellowFlashButton;

    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 300;
    private static final int SCROLL_MARGIN = 10;
    private static final int RIGHT_MARGIN = 15;
    private static final int FIELD_WIDTH = 50;
    private static final int FIELD_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_GAP = 8;

    private final List<TrackedWidget> tracked = new ArrayList<>();
    private int scrollOffset = 0;

    public TrafficLightsConditionScreen(BlockPos pos) {
        super(Text.literal("附加条件"));
        this.pos = pos;
        if (Minecraft.getInstance().level != null
                && Minecraft.getInstance().level.getBlockEntity(pos) instanceof TrafficLightsBlockEntity be) {
            this.pulseEnabled = be.isPulseCountdown();
            this.fullDisplay = be.isCountdownFullDisplay();
            this.redFrom = be.getHalfDisplayRedFrom();
            this.greenFrom = be.getHalfDisplayGreenFrom();
            this.numberMode = be.getCountdownNumberMode();
            this.hidePatternOnGreen = be.isHidePatternOnGreen();
            this.yellowFlashAlways = be.isYellowFlashAlways();
        }
    }

    private static String numberModeName(int mode) {
        return switch (mode) {
            case 2 -> "十六进制";
            case 1 -> "百起步十";
            default -> "99";
        };
    }

    @Override
    protected void init() {
        super.init();
        tracked.clear();

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int buttonX = panelX + PANEL_WIDTH - RIGHT_MARGIN - BUTTON_WIDTH;
        int fieldX = panelX + PANEL_WIDTH - RIGHT_MARGIN - FIELD_WIDTH;

        pulseButton = addTrackedButton(
                Text.literal(pulseEnabled ? "是" : "不是"),
                buttonX, 60,
                button -> togglePulse());

        displayButton = addTrackedButton(
                Text.literal(fullDisplay ? "全显" : "半显"),
                buttonX, 90,
                button -> toggleFullDisplay());

        numberModeButton = addTrackedButton(
                Text.literal(numberModeName(numberMode)),
                buttonX, 120,
                button -> cycleNumberMode());
        numberModeButton.visible = fullDisplay;
        numberModeButton.active = fullDisplay;

        redField = this.addDrawableChild(
                GUI.createTextField(
                        this.textRenderer, fieldX, getPanelY() + 114,
                        FIELD_WIDTH, FIELD_HEIGHT,
                        Text.literal("红灯起始秒数")
                )
        );
        tracked.add(TrackedWidget.of(redField, fieldX, 114));
        GUI.setMaxLength(redField, 3);
        GUI.setText(redField, String.valueOf(redFrom));
        redField.visible = !fullDisplay;
        redField.active = !fullDisplay;

        greenField = this.addDrawableChild(
                GUI.createTextField(
                        this.textRenderer, fieldX, getPanelY() + 140,
                        FIELD_WIDTH, FIELD_HEIGHT,
                        Text.literal("绿灯起始秒数")
                )
        );
        tracked.add(TrackedWidget.of(greenField, fieldX, 140));
        GUI.setMaxLength(greenField, 3);
        GUI.setText(greenField, String.valueOf(greenFrom));
        greenField.visible = !fullDisplay;
        greenField.active = !fullDisplay;

        hidePatternButton = addTrackedButton(
                Text.literal(hidePatternOnGreen ? "是" : "不是"),
                buttonX, 200,
                button -> toggleHidePatternOnGreen());

        yellowFlashButton = addTrackedButton(
                Text.literal(yellowFlashAlways ? "是" : "不是"),
                buttonX, 226,
                button -> toggleYellowFlashAlways());

        int bottomY = PANEL_HEIGHT - 32;
        int totalWidth = BUTTON_WIDTH * 3 + BUTTON_GAP * 2;
        int bottomX = panelX + (PANEL_WIDTH - totalWidth) / 2;

        addTrackedButton(
                Text.translatable("text.yunbeiuc.traffic_lights_timing.cancel"),
                bottomX, bottomY,
                button -> back());

        addTrackedButton(
                Text.literal("保存"),
                bottomX + BUTTON_WIDTH + BUTTON_GAP, bottomY,
                button -> saveConditions());

        addTrackedButton(
                Text.literal("应用到全部"),
                bottomX + (BUTTON_WIDTH + BUTTON_GAP) * 2, bottomY,
                button -> applyToAll());
    }

    private ButtonWidget addTrackedButton(Component label, int x, int offsetY, Consumer<ButtonWidget> action) {
        ButtonWidget button = this.addDrawableChild(
                GUI.createButton(label, x, getPanelY() + offsetY, BUTTON_WIDTH, 20, action));
        tracked.add(TrackedWidget.of(button, x, offsetY));
        return button;
    }

    // 内容高于窗口时整页可滚动；窗口足够高时保持居中并禁用滚动。
    private int getPanelY() {
        if (getMaxScroll() <= 0) {
            return (this.height - PANEL_HEIGHT) / 2;
        }
        return SCROLL_MARGIN - scrollOffset;
    }

    private int getMaxScroll() {
        return Math.max(0, PANEL_HEIGHT + SCROLL_MARGIN * 2 - this.height);
    }

    @Override
    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        int max = getMaxScroll();
        if (max <= 0) {
            return super.mouseScrolledCompat(mouseX, mouseY, amount);
        }
        int next = (int) Math.round(scrollOffset - amount * 20);
        next = Math.max(0, Math.min(max, next));
        if (next != scrollOffset) {
            scrollOffset = next;
            int panelY = getPanelY();
            for (TrackedWidget trackedWidget : tracked) {
                trackedWidget.apply(panelY);
            }
        }
        return true;
    }

    private void togglePulse() {
        pulseEnabled = !pulseEnabled;
        if (pulseButton != null) {
            GUI.setButtonMessage(pulseButton, Text.literal(pulseEnabled ? "是" : "不是"));
        }
    }

    private void toggleFullDisplay() {
        fullDisplay = !fullDisplay;
        if (displayButton != null) {
            GUI.setButtonMessage(displayButton, Text.literal(fullDisplay ? "全显" : "半显"));
        }
        if (numberModeButton != null) {
            numberModeButton.visible = fullDisplay;
            numberModeButton.active = fullDisplay;
        }
        if (redField != null) {
            redField.visible = !fullDisplay;
            redField.active = !fullDisplay;
        }
        if (greenField != null) {
            greenField.visible = !fullDisplay;
            greenField.active = !fullDisplay;
        }
    }

    private void cycleNumberMode() {
        numberMode = (numberMode + 1) % 3;
        if (numberModeButton != null) {
            GUI.setButtonMessage(numberModeButton, Text.literal(numberModeName(numberMode)));
        }
    }

    private void toggleHidePatternOnGreen() {
        hidePatternOnGreen = !hidePatternOnGreen;
        if (hidePatternButton != null) {
            GUI.setButtonMessage(hidePatternButton, Text.literal(hidePatternOnGreen ? "是" : "不是"));
        }
    }

    private void toggleYellowFlashAlways() {
        yellowFlashAlways = !yellowFlashAlways;
        if (yellowFlashButton != null) {
            GUI.setButtonMessage(yellowFlashButton, Text.literal(yellowFlashAlways ? "是" : "不是"));
        }
    }

    private int parseSeconds(TextFieldWidget field, int fallback) {
        if (field == null) {
            return fallback;
        }
        String text = GUI.getText(field).trim();
        if (text.isEmpty()) {
            return fallback;
        }
        try {
            return Math.max(0, Math.min(300, Integer.parseInt(text)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void sendCountdownCondition() {
        redFrom = parseSeconds(redField, redFrom);
        greenFrom = parseSeconds(greenField, greenFrom);
        TrafficLightsCountdownUpdatePacket packet = new TrafficLightsCountdownUpdatePacket(
                pos, fullDisplay, redFrom, greenFrom, numberMode, hidePatternOnGreen, yellowFlashAlways);
        FriendlyByteBuf buf = NetworkCompat.newBuffer();
        packet.write(buf);
        NetworkCompat.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_COUNTDOWN, buf);
    }

    private void sendPulseCondition() {
        TrafficLightsPulseUpdatePacket packet = new TrafficLightsPulseUpdatePacket(pos, pulseEnabled);
        FriendlyByteBuf buf = NetworkCompat.newBuffer();
        packet.write(buf);
        NetworkCompat.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_PULSE, buf);
    }

    private void saveConditions() {
        sendPulseCondition();
        sendCountdownCondition();
    }

    private void applyToAll() {
        redFrom = parseSeconds(redField, redFrom);
        greenFrom = parseSeconds(greenField, greenFrom);
        ApiCompat.setScreen(Minecraft.getInstance(), new ConfirmScreen(
                confirmed -> {
                    if (confirmed) {
                        sendApplyAll();
                    }
                    ApiCompat.setScreen(Minecraft.getInstance(), this);
                },
                Text.literal("应用到全部红绿灯"),
                Text.literal("将把当前附加条件应用到当前维度所有已加载的红绿灯，不支持该参数的类型会自动跳过。"),
                Text.literal("确定应用"),
                Text.literal("取消")));
    }

    private void sendApplyAll() {
        TrafficLightsApplyAllConditionsPacket packet = new TrafficLightsApplyAllConditionsPacket(
                pulseEnabled, fullDisplay, redFrom, greenFrom, numberMode, hidePatternOnGreen, yellowFlashAlways);
        FriendlyByteBuf buf = NetworkCompat.newBuffer();
        packet.write(buf);
        NetworkCompat.sendToServer(ModMessages.APPLY_ALL_TRAFFIC_LIGHTS_CONDITIONS, buf);
    }

    private void back() {
        ApiCompat.setScreen(Minecraft.getInstance(), new TrafficLightsScreen(pos));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = getPanelY();

        RENDER.fill(context, panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);
        RENDER.drawBorder(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        RENDER.drawCenteredText(
                context,
                this.textRenderer,
                Text.literal("附加条件"),
                panelX + PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        RENDER.drawText(
                context,
                this.textRenderer,
                Text.literal("第一大类：读秒"),
                panelX + RIGHT_MARGIN,
                panelY + 36,
                0xFFFFFF00,
                true
        );

        RENDER.drawText(
                context,
                this.textRenderer,
                Text.literal("脉冲启动读秒器"),
                panelX + RIGHT_MARGIN,
                panelY + 66,
                0xFFAAAAAA,
                true
        );

        RENDER.drawText(
                context,
                this.textRenderer,
                Text.literal("读秒显示"),
                panelX + RIGHT_MARGIN,
                panelY + 96,
                0xFFAAAAAA,
                true
        );

        if (fullDisplay) {
            RENDER.drawText(
                    context,
                    this.textRenderer,
                    Text.literal("数字显示模式"),
                    panelX + RIGHT_MARGIN + 12,
                    panelY + 126,
                    0xFF888888,
                    true
            );
        } else {
            RENDER.drawText(
                    context,
                    this.textRenderer,
                    Text.literal("半显红灯从（秒）"),
                    panelX + RIGHT_MARGIN + 12,
                    panelY + 120,
                    0xFF888888,
                    true
            );

            RENDER.drawText(
                    context,
                    this.textRenderer,
                    Text.literal("半显绿灯从（秒）"),
                    panelX + RIGHT_MARGIN + 12,
                    panelY + 146,
                    0xFF888888,
                    true
            );
        }

        RENDER.drawText(
                context,
                this.textRenderer,
                Text.literal("第二大类：整体"),
                panelX + RIGHT_MARGIN,
                panelY + 180,
                0xFFFFFF00,
                true
        );

        RENDER.drawText(
                context,
                this.textRenderer,
                Text.literal("绿灯时隐藏图案与读秒"),
                panelX + RIGHT_MARGIN,
                panelY + 206,
                0xFFAAAAAA,
                true
        );

        RENDER.drawText(
                context,
                this.textRenderer,
                Text.literal("黄闪常亮"),
                panelX + RIGHT_MARGIN,
                panelY + 232,
                0xFFAAAAAA,
                true
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPauseCompat() {
        return false;
    }

    // 记录控件的横向位置与相对面板顶部的偏移，滚动时统一重排。
    private static final class TrackedWidget {
        private final ButtonWidget button;
        private final TextFieldWidget field;
        private final int x;
        private final int offsetY;

        private TrackedWidget(ButtonWidget button, TextFieldWidget field, int x, int offsetY) {
            this.button = button;
            this.field = field;
            this.x = x;
            this.offsetY = offsetY;
        }

        static TrackedWidget of(ButtonWidget button, int x, int offsetY) {
            return new TrackedWidget(button, null, x, offsetY);
        }

        static TrackedWidget of(TextFieldWidget field, int x, int offsetY) {
            return new TrackedWidget(null, field, x, offsetY);
        }

        void apply(int panelY) {
            if (button != null) {
                button.setPosition(x, panelY + offsetY);
            }
            if (field != null) {
                field.setPosition(x, panelY + offsetY);
            }
        }
    }
}
