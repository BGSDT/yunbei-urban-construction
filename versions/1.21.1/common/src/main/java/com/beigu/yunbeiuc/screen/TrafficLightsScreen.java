package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class TrafficLightsScreen extends Screen {
    private final BlockPos pos;
    private final TrafficLightsBlockEntity blockEntity;
    private final boolean isPavement;  // 是否为人行道红绿灯

    private final List<DirectionOption> options;
    private DirectionListWidget listWidget;
    private DirectionOption selectedOption;

    private int phaseIndex;
    private int phaseCount;
    private PhaseSliderWidget phaseSlider;

    private static final int RIGHT_PANEL_WIDTH = 200;
    private static final int RIGHT_PANEL_HEIGHT = 240;

    public TrafficLightsScreen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.traffic_lights.title"));
        this.pos = pos;
        this.blockEntity = (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);

        // 判断是否为人行道红绿灯
        Block currentBlock = blockEntity.getCachedState().getBlock();
        this.isPavement = currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get() || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get() || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get();

        this.phaseIndex = blockEntity.getPhaseIndex();
        this.phaseCount = blockEntity.getPhaseCount();
        if (this.phaseCount <= 0) this.phaseCount = 4;
        if (this.phaseIndex < 0) this.phaseIndex = 0;
        this.options = createDirectionOptions();

        for (DirectionOption option : options) {
            if (option.getDirectionType() == blockEntity.getDirectionType()) {
                this.selectedOption = option;
                break;
            }
        }
        if (this.selectedOption == null && !options.isEmpty()) {
            this.selectedOption = options.get(0);
        }
    }

    @Override
    protected void init() {
        super.init();

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof TrafficLightsBlockEntity entity) {
                int existingPhaseIndex = entity.getPhaseIndex();
                if (existingPhaseIndex >= 0) {
                    this.phaseIndex = existingPhaseIndex;
                }
                this.phaseCount = entity.getPhaseCount();
                if (this.phaseCount <= 0) this.phaseCount = 4;
            }
        }

        int panelX, panelY;

        if (!isPavement) {
            // 非人行道：左侧列表 + 右侧面板
            int listWidth = this.width / 3;
            this.listWidget = new DirectionListWidget(
                    this.client,
                    listWidth,
                    this.height,
                    40,
                    this.height - 60,
                    30,
                    this.options
            );
            this.addDrawableChild(this.listWidget);

            int rightAreaX = this.width / 3;
            int rightAreaWidth = this.width * 2 / 3;
            panelX = rightAreaX + (rightAreaWidth - RIGHT_PANEL_WIDTH) / 2;
            panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;
        } else {
            // 人行道：面板居中
            panelX = (this.width - RIGHT_PANEL_WIDTH) / 2;
            panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;
        }

        // 相位滑块
        if (phaseCount > 1) {
            this.phaseSlider = this.addDrawableChild(
                    new PhaseSliderWidget(
                            panelX + 20, panelY + 80,
                            160, 20,
                            phaseIndex,
                            phaseCount
                    )
            );
        }

        // 保存按钮
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights.save"), button -> saveAndClose())
                        .dimensions(panelX + 30, panelY + 210, 60, 20)
                        .build()
        );

        // 取消按钮
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights.cancel"), button -> this.close())
                        .dimensions(panelX + 110, panelY + 210, 60, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ScreenRenderUtils.renderBackground(context, this.width, this.height);

        int panelX, panelY;

        if (!isPavement) {
            // 非人行道布局
            int listAreaWidth = this.width / 3;
            int rightAreaX = this.width / 3;
            int rightAreaWidth = this.width * 2 / 3;
            panelX = rightAreaX + (rightAreaWidth - RIGHT_PANEL_WIDTH) / 2;
            panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;

            // 标题
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.traffic_lights.title"),
                    listAreaWidth / 2,
                    10,
                    0xFFFFFF
            );

            // 当前选择
            if (selectedOption != null) {
                context.drawTextWithShadow(
                        this.textRenderer,
                        Text.translatable("text.yunbeiuc.traffic_lights.current_selection",
                                Text.translatable(selectedOption.getTranslationKey())),
                        10,
                        this.height - 55,
                        0xFFFFFF
                );
            }
        } else {
            // 人行道布局：面板居中
            panelX = (this.width - RIGHT_PANEL_WIDTH) / 2;
            panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;

            // 居中标题
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.traffic_lights.title"),
                    this.width / 2,
                    10,
                    0xFFFFFF
            );
        }

        // 右侧面板背景
        context.fill(panelX, panelY, panelX + RIGHT_PANEL_WIDTH, panelY + RIGHT_PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, RIGHT_PANEL_WIDTH, RIGHT_PANEL_HEIGHT, 0xFFCCCCCC);

        // 右侧面板标题
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights.settings_title"),
                panelX + RIGHT_PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        // 相位标签
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights.phase_label"),
                panelX + 20, panelY + 50,
                0xFFAAAAAA
        );

        // 当前相位显示
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights.phase_value",
                        phaseIndex + 1, phaseCount),
                panelX + 20, panelY + 60,
                0xFFFFFF00
        );

        if (!isPavement && selectedOption != null) {
            // 预览区域（仅非人行道显示）
            int previewSize = 60;
            int previewX = panelX + 10;
            int previewY = panelY + 120;

            context.fill(previewX - 2, previewY - 2, previewX + previewSize + 2, previewY + previewSize + 2, 0xFF000000);
            context.drawBorder(previewX - 2, previewY - 2, previewSize + 4, previewSize + 4, 0xFFFFFFFF);

            int iconColor = selectedOption.getColor();
            int iconSize = 40;
            int iconX = previewX + (previewSize - iconSize) / 2;
            int iconY = previewY + (previewSize - iconSize) / 2;
            context.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, 0xFF000000 | iconColor);

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable(selectedOption.getTranslationKey()),
                    previewX + previewSize + 8,
                    previewY + 10,
                    0xFFFFFF
            );

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.traffic_lights.current_phase",
                            phaseIndex + 1, phaseCount),
                    previewX + previewSize + 8,
                    previewY + 30,
                    0xFFAAAAAA
            );
        }

        for (Element element : this.children()) {
            if (element instanceof Drawable drawable) {
                drawable.render(context, mouseX, mouseY, delta);
            }
        }
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
            TrafficLightsBlockEntity.DirectionType selectedDirection = selectedOption != null ?
                    selectedOption.getDirectionType() : TrafficLightsBlockEntity.DirectionType.STRAIGHT;

            TrafficLightsUpdatePacket packet =
                    new TrafficLightsUpdatePacket(pos, phaseIndex, selectedDirection);
            RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), this.client.getNetworkHandler().getRegistryManager());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS, buf);
        }
        this.close();
    }

    public void setSelectedOption(DirectionOption option) {
        this.selectedOption = option;
    }

    public void setPhaseIndex(int index) {
        this.phaseIndex = index;
    }

    private List<DirectionOption> createDirectionOptions() {
        List<DirectionOption> options = new ArrayList<>();
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.STRAIGHT,
                "text.yunbeiuc.traffic_lights.direction.straight"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.LEFT_TURN,
                "text.yunbeiuc.traffic_lights.direction.left_turn"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.RIGHT_TURN,
                "text.yunbeiuc.traffic_lights.direction.right_turn"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.TURN_AROUND,
                "text.yunbeiuc.traffic_lights.direction.turn_around"));
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.NON_MOTOR_VEHICLES,
                "text.yunbeiuc.traffic_lights.direction.non_motor_vehicles"));
        return options;
    }

    // ==================== 内部类保持不变 ====================

    private class PhaseSliderWidget extends SliderWidget {
        private final int phaseCount;
        private int currentPhase;

        public PhaseSliderWidget(int x, int y, int width, int height, int initialPhase, int phaseCount) {
            super(x, y, width, height,
                    Text.literal("相位: " + (initialPhase + 1) + " / " + phaseCount),
                    (double) initialPhase / Math.max(1, phaseCount - 1));
            this.phaseCount = phaseCount;
            this.currentPhase = initialPhase;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("相位: " + (currentPhase + 1) + " / " + phaseCount));
        }

        @Override
        protected void applyValue() {
            currentPhase = (int) Math.round(this.value * (phaseCount - 1));
            currentPhase = Math.max(0, Math.min(currentPhase, phaseCount - 1));
            setPhaseIndex(currentPhase);
            updateMessage();
        }
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
                case STRAIGHT -> 0x00AA00;
                case LEFT_TURN -> 0x0000FF;
                case RIGHT_TURN -> 0xFF6600;
                case TURN_AROUND -> 0xAA00AA;
                case NON_MOTOR_VEHICLES -> 0x00AAAA;
            };
        }
    }

    private class DirectionListWidget extends ElementListWidget<DirectionListWidget.Entry> {
        private final int listWidth;

        public DirectionListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, List<DirectionOption> directionOptions) {
            super(client, width, bottom - top, top, itemHeight);
            this.listWidth = width;

            for (DirectionOption option : directionOptions) {
                this.addEntry(new Entry(option));
            }
        }

        @Override
        public int getRowWidth() {
            return this.listWidth - 25;
        }

        @Override
        protected int getScrollbarX() {
            return this.getRowLeft() + this.getRowWidth() + 4;
        }

        @Override
        public int getRowLeft() {
            return this.getX() + 5;
        }

        @Override
        public int getRowRight() {
            return this.getRowLeft() + this.getRowWidth();
        }

        public class Entry extends ElementListWidget.Entry<Entry> {
            private final DirectionOption option;

            public Entry(DirectionOption option) {
                this.option = option;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                if (option == selectedOption) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);
                } else if (hovered) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x22FFFFFF);
                }

                int colorSize = 16;
                int colorX = x + 5;
                int colorY = y + (entryHeight - colorSize) / 2;
                context.fill(colorX, colorY, colorX + colorSize, colorY + colorSize, 0xFF000000 | option.getColor());
                context.drawBorder(colorX, colorY, colorSize, colorSize, 0xFFCCCCCC);

                int textX = colorX + colorSize + 8;
                context.drawTextWithShadow(
                        textRenderer,
                        Text.translatable(option.getTranslationKey()),
                        textX,
                        y + (entryHeight - 8) / 2,
                        0xFFFFFF
                );
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                setSelectedOption(option);
                return true;
            }

            @Override
            public List<ClickableWidget> selectableChildren() {
                return List.of();
            }

            @Override
            public List<ClickableWidget> children() {
                return List.of();
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}