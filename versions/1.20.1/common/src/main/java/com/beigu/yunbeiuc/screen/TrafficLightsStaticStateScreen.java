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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TrafficLightsStaticStateScreen extends Screen {
    private final BlockPos pos;

    private final List<DirectionOption> options;
    private DirectionListWidget listWidget;
    private DirectionOption selectedOption;
    private TrafficLightsBlock.LightState selectedColor = TrafficLightsBlock.LightState.RED;
    private ButtonWidget mountTypeButton;
    private TrafficLightsBlock.MountType pendingMountType;

    private int panelX;
    private int panelY;

    private static final int RIGHT_PANEL_WIDTH = 200;
    private static final int RIGHT_PANEL_HEIGHT = 270;

    public TrafficLightsStaticStateScreen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.traffic_lights_static_state.title"));
        this.pos = pos;

        this.options = createDirectionOptions();
        TrafficLightsBlockEntity blockEntity = MinecraftClient.getInstance().world != null
                ? (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos) : null;

        if (blockEntity != null) {
            for (DirectionOption option : options) {
                if (option.getDirectionType() == blockEntity.getDirectionType()) {
                    this.selectedOption = option;
                    break;
                }
            }
            // 读取当前颜色
            var state = blockEntity.getCachedState();
            if (state.contains(TrafficLightsBlock.LIGHT_STATE)) {
                this.selectedColor = state.get(TrafficLightsBlock.LIGHT_STATE);
            }
        }
        if (this.selectedOption == null && !options.isEmpty()) {
            this.selectedOption = options.get(0);
        }
    }

    @Override
    protected void init() {
        super.init();

        TrafficLightsBlockEntity blockEntity = MinecraftClient.getInstance().world != null
                ? (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos) : null;
        Block currentBlock = blockEntity != null ? blockEntity.getCachedState().getBlock() : null;

        boolean isPavement = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get();

        if (!isPavement) {
            int listWidth = this.width / 3;
            this.listWidget = new DirectionListWidget(
                    this.client,
                    listWidth,
                    this.height,
                    40,
                    this.height - 60,
                    30,
                    this.options,
                    this::setSelectedOption
            );
            this.addDrawableChild(this.listWidget);

            int rightAreaX = this.width / 3;
            int rightAreaWidth = this.width * 2 / 3;
            this.panelX = rightAreaX + (rightAreaWidth - RIGHT_PANEL_WIDTH) / 2;
            this.panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;
        } else {
            this.panelX = (this.width - RIGHT_PANEL_WIDTH) / 2;
            this.panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;
        }

        int colorButtonY = panelY + 100;

        if (isPavement) {
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.red"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.RED)
                            .dimensions(panelX + 20, colorButtonY, 160, 20)
                            .build()
            );
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.green"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.GREEN)
                            .dimensions(panelX + 20, colorButtonY + 25, 160, 20)
                            .build()
            );
        } else {
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.red"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.RED)
                            .dimensions(panelX + 20, colorButtonY, 160, 20)
                            .build()
            );
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.yellow"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.YELLOW)
                            .dimensions(panelX + 20, colorButtonY + 25, 160, 20)
                            .build()
            );
            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.color.green"),
                                    button -> this.selectedColor = TrafficLightsBlock.LightState.GREEN)
                            .dimensions(panelX + 20, colorButtonY + 50, 160, 20)
                            .build()
            );
        }

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.save"), button -> saveAndClose())
                        .dimensions(panelX + 30, panelY + 210, 60, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.cancel"), button -> this.close())
                        .dimensions(panelX + 110, panelY + 210, 60, 20)
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
                        .dimensions(panelX + 30, panelY + 185, 140, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        TrafficLightsBlockEntity blockEntity = MinecraftClient.getInstance().world != null
                ? (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos) : null;
        Block currentBlock = blockEntity != null ? blockEntity.getCachedState().getBlock() : null;

        boolean isPavement = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get();

        if (!isPavement) {
            int listAreaWidth = this.width / 3;

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    this.title,
                    listAreaWidth / 2,
                    10,
                    0xFFFFFF
            );

            if (selectedOption != null) {
                context.drawTextWithShadow(
                        this.textRenderer,
                        Text.translatable("text.yunbeiuc.traffic_lights_static_state.current_selection",
                                Text.translatable(selectedOption.getTranslationKey())),
                        10,
                        this.height - 55,
                        0xFFFFFF
                );
            }
        } else {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    this.title,
                    this.width / 2,
                    10,
                    0xFFFFFF
            );
        }

        context.fill(panelX, panelY, panelX + RIGHT_PANEL_WIDTH, panelY + RIGHT_PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, RIGHT_PANEL_WIDTH, RIGHT_PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights_static_state.settings_title"),
                panelX + RIGHT_PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights_static_state.color_label"),
                panelX + 20, panelY + 40,
                0xFFAAAAAA
        );

        if (selectedOption != null) {
            int previewSize = 40;
            int previewX = panelX + 20;
            int previewY = panelY + 55;
            int iconColor = colorToRgb(selectedColor);
            context.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, 0xFF000000 | iconColor);
            context.drawBorder(previewX, previewY, previewSize, previewSize, 0xFFFFFFFF);
        }

        // 警告文本 - 移到底部
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("§e设置链接组时间序列后数据将被清除"),
                panelX + RIGHT_PANEL_WIDTH / 2,
                panelY + RIGHT_PANEL_HEIGHT - 12,
                0xFFFF00
        );

        super.render(context, mouseX, mouseY, delta);
    }

    private int colorToRgb(TrafficLightsBlock.LightState state) {
        return switch (state) {
            case RED -> 0xFF0000;
            case YELLOW -> 0xFFFF00;
            case GREEN -> 0x00FF00;
            case GRAY -> 0x888888;
        };
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
            TrafficLightsBlockEntity blockEntity = (TrafficLightsBlockEntity) this.client.world.getBlockEntity(pos);
            Block currentBlock = blockEntity != null ? blockEntity.getCachedState().getBlock() : null;

            boolean isPavement = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get();

            TrafficLightsBlockEntity.DirectionType selectedDirection;
            if (isPavement) {
                selectedDirection = TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE;
            } else if (selectedOption != null) {
                selectedDirection = selectedOption.getDirectionType();
            } else {
                selectedDirection = TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE;
            }

            // 人行道红绿灯保留当前的显示读秒开关和固定秒数
            boolean showSeconds = blockEntity != null ? blockEntity.isShowSeconds() : false;
            int fixedSeconds = blockEntity != null ? blockEntity.getFixedSeconds() : 10;

            TrafficLightsStaticStateUpdatePacket packet =
                    new TrafficLightsStaticStateUpdatePacket(pos, selectedDirection, selectedColor, showSeconds, fixedSeconds);
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

    public void setSelectedOption(DirectionOption option) {
        this.selectedOption = option;
    }

    private List<DirectionOption> createDirectionOptions() {
        List<DirectionOption> options = new ArrayList<>();
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE,
                "text.yunbeiuc.traffic_lights.direction.straight_circle"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.STRAIGHT_ARROW,
                "text.yunbeiuc.traffic_lights.direction.straight_arrow"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.LEFT_TURN,
                "text.yunbeiuc.traffic_lights.direction.left_turn"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.RIGHT_TURN,
                "text.yunbeiuc.traffic_lights.direction.right_turn"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.TURN_AROUND,
                "text.yunbeiuc.traffic_lights.direction.turn_around"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.NON_MOTOR_VEHICLES,
                "text.yunbeiuc.traffic_lights.direction.non_motor_vehicles"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.NON_MOTOR_VEHICLES_LEFT_TURN,
                "text.yunbeiuc.traffic_lights.direction.non_motor_vehicles_left_turn"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.NON_MOTOR_VEHICLES_RIGHT_TURN,
                "text.yunbeiuc.traffic_lights.direction.non_motor_vehicles_right_turn"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.COLOR_FLASH,
                "text.yunbeiuc.traffic_lights.direction.color_flash"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.SLOW_FLASH,
                "text.yunbeiuc.traffic_lights.direction.slow_flash"));
        return options;
    }

    private static class DirectionOption {
        private final TrafficLightsBlockEntity.DirectionType directionType;
        private final String translationKey;

        public DirectionOption(TrafficLightsBlockEntity.DirectionType directionType, String translationKey) {
            this.directionType = directionType;
            this.translationKey = translationKey;
        }

        public TrafficLightsBlockEntity.DirectionType getDirectionType() {
            return directionType;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public int getColor() {
            return switch (directionType) {
                case STRAIGHT_CIRCLE -> 0x00AA00;
                case STRAIGHT_ARROW -> 0x006600;
                case LEFT_TURN -> 0x0000FF;
                case RIGHT_TURN -> 0xFF6600;
                case TURN_AROUND -> 0xAA00AA;
                case NON_MOTOR_VEHICLES -> 0x00AAAA;
                case NON_MOTOR_VEHICLES_LEFT_TURN -> 0x0088AA;
                case NON_MOTOR_VEHICLES_RIGHT_TURN -> 0x00AA88;
                case COLOR_FLASH -> 0xFFAA00;
                case SLOW_FLASH -> 0xFFCC00;
            };
        }

        public AbstractOptionListWidget.Icon getIcon() {
            return com.beigu.yunbeiuc.util.TrafficLightsDirectionIcons.get(directionType);
        }
    }

    private class DirectionListWidget extends AbstractOptionListWidget<DirectionOption> {
        public DirectionListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight,
                                    List<DirectionOption> directionOptions, Consumer<DirectionOption> onSelect) {
            super(client, width, height, top, bottom, itemHeight, directionOptions,
                    option -> option == selectedOption, onSelect,
                    option -> Text.translatable(option.getTranslationKey()), DirectionOption::getColor,
                    DirectionOption::getIcon);
        }
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
