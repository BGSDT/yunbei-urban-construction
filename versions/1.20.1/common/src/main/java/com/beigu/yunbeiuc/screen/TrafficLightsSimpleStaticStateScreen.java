package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsStaticStateUpdatePacket;
import com.beigu.yunbeiuc.network.TrafficLightsMountTypeUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TrafficLightsSimpleStaticStateScreen extends Screen {
    private final BlockPos pos;
    private final boolean isPavement;
    private TrafficLightsBlock.LightState selectedColor = TrafficLightsBlock.LightState.RED;

    // 人行道红绿灯专用字段
    private boolean showSeconds = false;
    private TextFieldWidget secondsField;
    private ButtonWidget mountTypeButton;
    private TrafficLightsBlock.MountType pendingMountType;

    private int panelX;
    private int panelY;

    private static final int PANEL_WIDTH = 220;
    private static final int PANEL_HEIGHT = 300;

    public TrafficLightsSimpleStaticStateScreen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.traffic_lights_static_state.title"));
        this.pos = pos;

        // 判断是否为人行道红绿灯，并读取当前颜色和数据
        var blockEntity = MinecraftClient.getInstance().world.getBlockEntity(pos);
        if (blockEntity instanceof TrafficLightsBlockEntity tl) {
            var currentBlock = tl.getCachedState().getBlock();
            this.isPavement = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_GRAY.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLACK.get();
            if (isPavement) {
                this.showSeconds = tl.isShowSeconds();
            }
            // 读取当前颜色
            var state = tl.getCachedState();
            if (state.contains(TrafficLightsBlock.LIGHT_STATE)) {
                this.selectedColor = state.get(TrafficLightsBlock.LIGHT_STATE);
            }
        } else {
            this.isPavement = false;
        }
    }

    @Override
    protected void init() {
        super.init();

        this.panelX = (this.width - PANEL_WIDTH) / 2;
        this.panelY = (this.height - PANEL_HEIGHT) / 2;

        int currentY = panelY + 60;

        // 颜色按钮（人行道只有红/绿，读秒器有红/黄/绿）
        if (isPavement) {
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.red"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.RED)
                            .dimensions(panelX + 30, currentY, 160, 20)
                            .build()
            );
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.green"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.GREEN)
                            .dimensions(panelX + 30, currentY + 25, 160, 20)
                            .build()
            );
            currentY += 55;

            // 显示秒数开关
            ButtonWidget showSecondsButton = this.addDrawableChild(
                    ButtonWidget.builder(
                            Text.literal(showSeconds ? "显示秒数: 开" : "显示秒数: 关"),
                            button -> {
                                showSeconds = !showSeconds;
                                button.setMessage(Text.literal(showSeconds ? "显示秒数: 开" : "显示秒数: 关"));
                                if (secondsField != null) {
                                    secondsField.setEditable(showSeconds);
                                }
                            })
                            .dimensions(panelX + 30, currentY, 160, 20)
                            .build()
            );
            currentY += 40;

            // 固定秒数输入框（不显示秒数时锁定）
            secondsField = new TextFieldWidget(this.textRenderer, panelX + 30, currentY, 160, 20, Text.literal(""));
            secondsField.setMaxLength(3);
            var blockEntity = MinecraftClient.getInstance().world.getBlockEntity(pos);
            if (blockEntity instanceof TrafficLightsBlockEntity tl) {
                secondsField.setText(String.valueOf(tl.getFixedSeconds()));
            } else {
                secondsField.setText("10");
            }
            secondsField.setEditable(showSeconds);
            this.addDrawableChild(secondsField);

        } else {
            // 读秒器：三个颜色按钮
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.red"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.RED)
                            .dimensions(panelX + 30, currentY, 160, 20)
                            .build()
            );
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.yellow"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.YELLOW)
                            .dimensions(panelX + 30, currentY + 25, 160, 20)
                            .build()
            );
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.green"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.GREEN)
                            .dimensions(panelX + 30, currentY + 50, 160, 20)
                            .build()
            );
        }

        // 保存取消按钮
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.save"), button -> saveAndClose())
                        .dimensions(panelX + 40, panelY + PANEL_HEIGHT - 50, 60, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.cancel"), button -> this.close())
                        .dimensions(panelX + 120, panelY + PANEL_HEIGHT - 50, 60, 20)
                        .build()
        );

        Block currentBlock = MinecraftClient.getInstance().world != null &&
                MinecraftClient.getInstance().world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl ?
                tl.getCachedState().getBlock() : null;
        boolean isIntegration = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_GRAY.get()
                || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLACK.get();

        if (!isIntegration) {
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
                            .dimensions(panelX + 30, panelY + PANEL_HEIGHT - 75, 160, 20)
                            .build()
            );
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // 居中标题
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

        // 颜色标签
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights_static_state.color_label"),
                panelX + 30, panelY + 45,
                0xFFAAAAAA
        );

        // 人行道红绿灯额外显示"显示秒数"标签和"固定秒数"标签
        if (isPavement && secondsField != null) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal("固定秒数:"),
                    panelX + 30, secondsField.getY() - 15,
                    0xFFAAAAAA
            );
        }

        // 警告文本
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("§e设置链接组时间序列后数据将被清除"),
                panelX + PANEL_WIDTH / 2,
                panelY + PANEL_HEIGHT - 12,
                0xFFFF00
        );

        super.render(context, mouseX, mouseY, delta);
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            int fixedSeconds = 10;
            boolean sendShowSeconds = false;

            if (isPavement && secondsField != null) {
                sendShowSeconds = showSeconds;
                try {
                    fixedSeconds = Integer.parseInt(secondsField.getText());
                    if (fixedSeconds < 1) fixedSeconds = 1;
                    if (fixedSeconds > 999) fixedSeconds = 999;
                } catch (NumberFormatException e) {
                    fixedSeconds = 10;
                }
            }

            TrafficLightsStaticStateUpdatePacket packet =
                    new TrafficLightsStaticStateUpdatePacket(pos, TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE,
                            selectedColor, sendShowSeconds, fixedSeconds);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_STATIC_STATE, buf);

            // 保存时才应用mountType的更改
            if (pendingMountType != null) {
                TrafficLightsBlockEntity blockEntity = (TrafficLightsBlockEntity) this.client.world.getBlockEntity(pos);
                if (blockEntity != null && blockEntity.getCachedState().contains(TrafficLightsBlock.TYPE)) {
                    TrafficLightsBlock.MountType currentType = blockEntity.getCachedState().get(TrafficLightsBlock.TYPE);
                    if (pendingMountType != currentType) {
                        TrafficLightsMountTypeUpdatePacket mountPacket = new TrafficLightsMountTypeUpdatePacket(pos, pendingMountType);
                        PacketByteBuf mountBuf = new PacketByteBuf(Unpooled.buffer());
                        mountPacket.write(mountBuf);
                        NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_MOUNT_TYPE, mountBuf);
                    }
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