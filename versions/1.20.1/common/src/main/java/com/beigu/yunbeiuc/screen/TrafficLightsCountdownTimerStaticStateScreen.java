package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsStaticStateUpdatePacket;
import com.beigu.yunbeiuc.network.TrafficLightsMountTypeUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TrafficLightsCountdownTimerStaticStateScreen extends Screen {
    private final BlockPos pos;
    private TrafficLightsBlock.LightState selectedColor = TrafficLightsBlock.LightState.RED;
    private TextFieldWidget secondsField;
    private boolean shouldShowCountdown = false;
    private ButtonWidget mountTypeButton;
    private TrafficLightsBlock.MountType pendingMountType;

    private int panelX;
    private int panelY;

    private static final int PANEL_WIDTH = 220;
    private static final int PANEL_HEIGHT = 300;

    public TrafficLightsCountdownTimerStaticStateScreen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.traffic_lights_static_state.title"));
        this.pos = pos;

        // 读取当前颜色
        TrafficLightsBlockEntity blockEntity = MinecraftClient.getInstance().world != null
                ? (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos) : null;
        if (blockEntity != null) {
            var state = blockEntity.getCachedState();
            if (state.contains(TrafficLightsBlock.LIGHT_STATE)) {
                this.selectedColor = state.get(TrafficLightsBlock.LIGHT_STATE);
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        this.panelX = (this.width - PANEL_WIDTH) / 2;
        this.panelY = (this.height - PANEL_HEIGHT) / 2;

        TrafficLightsBlockEntity blockEntity = MinecraftClient.getInstance().world != null
                ? (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos) : null;
        if (blockEntity != null) {
            this.shouldShowCountdown = blockEntity.isShowSeconds();
        }

        // 显示读秒开关
        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(shouldShowCountdown ? "显示读秒: 开" : "显示读秒: 关"),
                        button -> {
                            shouldShowCountdown = !shouldShowCountdown;
                            button.setMessage(Text.literal(shouldShowCountdown ? "显示读秒: 开" : "显示读秒: 关"));
                            if (secondsField != null) {
                                secondsField.setEditable(shouldShowCountdown);
                            }
                        })
                        .dimensions(panelX + 30, panelY + 50, 160, 20)
                        .build()
        );

        // 秒数输入框（不显示读秒时锁定）
        secondsField = new TextFieldWidget(this.textRenderer, panelX + 30, panelY + 90, 160, 20, Text.literal(""));
        secondsField.setMaxLength(3);
        secondsField.setText(blockEntity != null ? String.valueOf(blockEntity.getFixedSeconds()) : "10");
        secondsField.setEditable(shouldShowCountdown);
        this.addDrawableChild(secondsField);

        // 颜色按钮 - 三个颜色
        int colorButtonY = panelY + 130;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.red"),
                                button -> this.selectedColor = TrafficLightsBlock.LightState.RED)
                        .dimensions(panelX + 30, colorButtonY, 160, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.yellow"),
                                button -> this.selectedColor = TrafficLightsBlock.LightState.YELLOW)
                        .dimensions(panelX + 30, colorButtonY + 25, 160, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.green"),
                                button -> this.selectedColor = TrafficLightsBlock.LightState.GREEN)
                        .dimensions(panelX + 30, colorButtonY + 50, 160, 20)
                        .build()
        );

        // 保存取消按钮
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.save"), button -> saveAndClose())
                        .dimensions(panelX + 40, panelY + 260, 60, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.cancel"), button -> this.close())
                        .dimensions(panelX + 120, panelY + 260, 60, 20)
                        .build()
        );

        if (mountTypeButton != null) {
            this.remove(mountTypeButton);
        }
        TrafficLightsBlock.MountType currentMountType = getCurrentMountType();
        if (pendingMountType == null) {
            pendingMountType = currentMountType;
        }
        mountTypeButton = this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(pendingMountType == TrafficLightsBlock.MountType.POLE ? "路杆模式" : "墙面模式"),
                        button -> toggleMountType())
                        .dimensions(panelX + 30, panelY + 235, 160, 20)
                        .build()
        );
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                10,
                0xFFFFFF
        );

        // 面板背景
        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        // 面板标题
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights_static_state.settings_title"),
                panelX + PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        // 秒数标签
        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("固定秒数:"),
                panelX + 30, panelY + 75,
                0xFFAAAAAA
        );

        // 颜色标签
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights_static_state.color_label"),
                panelX + 30, panelY + 115,
                0xFFAAAAAA
        );

        // 警告文本 - 移到底部
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("§e设置链接组时间序列后数据将被清除"),
                panelX + PANEL_WIDTH / 2,
                panelY + PANEL_HEIGHT - 12,
                0xFFFF00
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.close();
            return true;
        } else if (keyCode == 257 || keyCode == 335) {
            saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            int seconds = 10;
            try {
                seconds = Integer.parseInt(secondsField.getText());
                if (seconds < 1) seconds = 1;
                if (seconds > 999) seconds = 999;
            } catch (NumberFormatException e) {
                seconds = 10;
            }

            TrafficLightsBlockEntity blockEntity = (TrafficLightsBlockEntity) this.client.world.getBlockEntity(pos);
            TrafficLightsBlockEntity.DirectionType currentDirection = blockEntity != null
                    ? blockEntity.getDirectionType()
                    : TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE;

            TrafficLightsStaticStateUpdatePacket packet =
                    new TrafficLightsStaticStateUpdatePacket(pos, currentDirection, selectedColor, shouldShowCountdown, seconds);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_STATIC_STATE, buf);

            // 保存时才应用mountType的更改
            if (pendingMountType != null && blockEntity != null && blockEntity.getCachedState().contains(TrafficLightsBlock.TYPE)) {
                TrafficLightsBlock.MountType currentType = blockEntity.getCachedState().get(TrafficLightsBlock.TYPE);
                if (pendingMountType != currentType) {
                    TrafficLightsMountTypeUpdatePacket mountPacket = new TrafficLightsMountTypeUpdatePacket(pos, pendingMountType);
                    PacketByteBuf mountBuf = new PacketByteBuf(Unpooled.buffer());
                    mountPacket.write(mountBuf);
                    NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_MOUNT_TYPE, mountBuf);
                }
            }
        }
        this.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private TrafficLightsBlock.MountType getCurrentMountType() {
        if (MinecraftClient.getInstance().world == null) return TrafficLightsBlock.MountType.SIMPLE;
        TrafficLightsBlockEntity blockEntity = (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.getCachedState().contains(TrafficLightsBlock.TYPE)) {
            return blockEntity.getCachedState().get(TrafficLightsBlock.TYPE);
        }
        return TrafficLightsBlock.MountType.SIMPLE;
    }

    private void toggleMountType() {
        pendingMountType = pendingMountType == TrafficLightsBlock.MountType.SIMPLE ?
                TrafficLightsBlock.MountType.POLE : TrafficLightsBlock.MountType.SIMPLE;

        if (mountTypeButton != null) {
            mountTypeButton.setMessage(Text.literal(pendingMountType == TrafficLightsBlock.MountType.POLE ? "路杆模式" : "墙面模式"));
        }
    }

}
