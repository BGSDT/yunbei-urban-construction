package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsStaticStateUpdatePacket;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TrafficLightsShanghaiStaticStateScreen extends Screen {
    private final BlockPos pos;
    private TrafficLightsBlockEntity.DirectionType selectedDirection = TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE;
    private TrafficLightsBlock.LightState selectedColor = TrafficLightsBlock.LightState.RED;
    private TextFieldWidget secondsField;
    private DirectionListWidget listWidget;
    private boolean shouldShowCountdown = false;

    private int panelX;
    private int panelY;

    private static final int PANEL_WIDTH = 220;
    private static final int PANEL_HEIGHT = 270;

    public TrafficLightsShanghaiStaticStateScreen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.traffic_lights_static_state.title"));
        this.pos = pos;

        TrafficLightsBlockEntity blockEntity = MinecraftClient.getInstance().world != null
                ? (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos) : null;
        if (blockEntity != null) {
            this.selectedDirection = blockEntity.getDirectionType();
            this.shouldShowCountdown = blockEntity.isShowSeconds();
            var state = blockEntity.getCachedState();
            if (state.contains(TrafficLightsBlock.LIGHT_STATE)) {
                this.selectedColor = state.get(TrafficLightsBlock.LIGHT_STATE);
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        int listWidth = this.width / 3;
        this.listWidget = new DirectionListWidget(
                this.client,
                listWidth,
                this.height,
                40,
                this.height - 60,
                30,
                createDirectionOptions(),
                this::setSelectedDirection
        );
        this.addDrawableChild(this.listWidget);

        int rightAreaX = this.width / 3;
        int rightAreaWidth = this.width * 2 / 3;
        this.panelX = rightAreaX + (rightAreaWidth - PANEL_WIDTH) / 2;
        this.panelY = (this.height - PANEL_HEIGHT) / 2;

        TrafficLightsBlockEntity blockEntity = MinecraftClient.getInstance().world != null
                ? (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos) : null;

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

        // 秒数输入框
        secondsField = new TextFieldWidget(this.textRenderer, panelX + 30, panelY + 90, 160, 20, Text.literal(""));
        secondsField.setMaxLength(3);
        secondsField.setText(blockEntity != null ? String.valueOf(blockEntity.getFixedSeconds()) : "10");
        secondsField.setEditable(shouldShowCountdown);
        this.addDrawableChild(secondsField);

        // 颜色按钮 - 红黄绿三色
        int colorButtonY = panelY + 150;
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
                        .dimensions(panelX + 40, panelY + 230, 60, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_static_state.cancel"), button -> this.close())
                        .dimensions(panelX + 120, panelY + 230, 60, 20)
                        .build()
        );
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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int listAreaWidth = this.width / 3;
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                listAreaWidth / 2,
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
                panelX + 30, panelY + 135,
                0xFFAAAAAA
        );

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

            TrafficLightsStaticStateUpdatePacket packet =
                    new TrafficLightsStaticStateUpdatePacket(pos, selectedDirection, selectedColor, shouldShowCountdown, seconds);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_STATIC_STATE, buf);
        }
        this.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void setSelectedDirection(DirectionOption option) {
        this.selectedDirection = option.getDirectionType();
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
                    option -> option.getDirectionType() == selectedDirection, onSelect,
                    option -> Text.translatable(option.getTranslationKey()), DirectionOption::getColor,
                    DirectionOption::getIcon);
        }
    }
}
