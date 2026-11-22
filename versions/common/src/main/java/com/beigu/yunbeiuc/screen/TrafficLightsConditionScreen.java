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
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

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

    private ButtonWidget pulseButton;
    private ButtonWidget displayButton;
    private ButtonWidget numberModeButton;
    private TextFieldWidget redField;
    private TextFieldWidget greenField;
    private ButtonWidget hidePatternButton;

    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 300;
    private static final int RIGHT_MARGIN = 15;
    private static final int FIELD_WIDTH = 50;
    private static final int FIELD_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_GAP = 8;

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

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;
        int buttonX = panelX + PANEL_WIDTH - RIGHT_MARGIN - BUTTON_WIDTH;
        int fieldX = panelX + PANEL_WIDTH - RIGHT_MARGIN - FIELD_WIDTH;

        pulseButton = this.addDrawableChild(
                GUI.createButton(
                        Text.literal(pulseEnabled ? "是" : "不是"),
                        buttonX, panelY + 60,
                        BUTTON_WIDTH, 20,
                        button -> togglePulse())
        );

        displayButton = this.addDrawableChild(
                GUI.createButton(
                        Text.literal(fullDisplay ? "全显" : "半显"),
                        buttonX, panelY + 90,
                        BUTTON_WIDTH, 20,
                        button -> toggleFullDisplay())
        );

        numberModeButton = this.addDrawableChild(
                GUI.createButton(
                        Text.literal(numberModeName(numberMode)),
                        buttonX, panelY + 120,
                        BUTTON_WIDTH, 20,
                        button -> cycleNumberMode())
        );
        numberModeButton.visible = fullDisplay;
        numberModeButton.active = fullDisplay;

        redField = this.addDrawableChild(
                GUI.createTextField(
                        this.textRenderer, fieldX, panelY + 114,
                        FIELD_WIDTH, FIELD_HEIGHT,
                        Text.literal("红灯起始秒数")
                )
        );
        GUI.setMaxLength(redField, 3);
        GUI.setText(redField, String.valueOf(redFrom));
        redField.visible = !fullDisplay;
        redField.active = !fullDisplay;

        greenField = this.addDrawableChild(
                GUI.createTextField(
                        this.textRenderer, fieldX, panelY + 140,
                        FIELD_WIDTH, FIELD_HEIGHT,
                        Text.literal("绿灯起始秒数")
                )
        );
        GUI.setMaxLength(greenField, 3);
        GUI.setText(greenField, String.valueOf(greenFrom));
        greenField.visible = !fullDisplay;
        greenField.active = !fullDisplay;

        hidePatternButton = this.addDrawableChild(
                GUI.createButton(
                        Text.literal(hidePatternOnGreen ? "是" : "不是"),
                        buttonX, panelY + 200,
                        BUTTON_WIDTH, 20,
                        button -> toggleHidePatternOnGreen())
        );

        int bottomY = panelY + PANEL_HEIGHT - 32;
        int totalWidth = BUTTON_WIDTH * 3 + BUTTON_GAP * 2;
        int bottomX = panelX + (PANEL_WIDTH - totalWidth) / 2;

        this.addDrawableChild(
                GUI.createButton(
                        Text.translatable("text.yunbeiuc.traffic_lights_timing.cancel"),
                        bottomX, bottomY,
                        BUTTON_WIDTH, 20,
                        button -> back())
        );

        this.addDrawableChild(
                GUI.createButton(
                        Text.literal("保存"),
                        bottomX + BUTTON_WIDTH + BUTTON_GAP, bottomY,
                        BUTTON_WIDTH, 20,
                        button -> saveConditions())
        );

        this.addDrawableChild(
                GUI.createButton(
                        Text.literal("应用到全部"),
                        bottomX + (BUTTON_WIDTH + BUTTON_GAP) * 2, bottomY,
                        BUTTON_WIDTH, 20,
                        button -> applyToAll())
        );
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
                pos, fullDisplay, redFrom, greenFrom, numberMode, hidePatternOnGreen);
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
        TrafficLightsApplyAllConditionsPacket packet = new TrafficLightsApplyAllConditionsPacket(
                pulseEnabled, fullDisplay, redFrom, greenFrom, numberMode, hidePatternOnGreen);
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
        int panelY = (this.height - PANEL_HEIGHT) / 2;

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

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPauseCompat() {
        return false;
    }
}
