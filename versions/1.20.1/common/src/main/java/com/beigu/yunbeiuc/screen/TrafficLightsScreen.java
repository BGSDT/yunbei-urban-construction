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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TrafficLightsScreen extends Screen {
    private final BlockPos pos;
    private final TrafficLightsBlockEntity blockEntity;
    private final boolean isPavement;  // 是否为人行道红绿灯

    private final List<DirectionOption> options;
    private DirectionListWidget listWidget;
    private DirectionOption selectedOption;

    private List<Integer> phaseIndices;
    private int phaseCount;
    private final List<PhaseSliderWidget> phaseSliders = new ArrayList<>();
    private final List<ButtonWidget> phaseRemoveButtons = new ArrayList<>();
    private ButtonWidget addPhaseButton;
    private int panelX;
    private int panelY;

    private static final int RIGHT_PANEL_WIDTH = 200;
    private static final int RIGHT_PANEL_HEIGHT = 300;
    private static final int MAX_PHASE_SLIDERS = 4;
    private static final int PHASE_SLIDER_START_Y = 80;
    private static final int PHASE_SLIDER_ROW_HEIGHT = 25;
    private static final int PHASE_SLIDER_AREA_HEIGHT = MAX_PHASE_SLIDERS * PHASE_SLIDER_ROW_HEIGHT;
    private static final int PREVIEW_Y_OFFSET = PHASE_SLIDER_START_Y + PHASE_SLIDER_AREA_HEIGHT + 10;
    private static final int PREVIEW_SIZE = 60;
    private static final int BUTTONS_Y_OFFSET = PREVIEW_Y_OFFSET + PREVIEW_SIZE + 15;

    public TrafficLightsScreen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.traffic_lights.title"));
        this.pos = pos;
        this.blockEntity = (TrafficLightsBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);

        // 判断是否为人行道红绿灯
        Block currentBlock = blockEntity.getCachedState().getBlock();
        this.isPavement = currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get() || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get() || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get();

        this.phaseCount = blockEntity.getPhaseCount();
        if (this.phaseCount <= 0) this.phaseCount = 4;
        this.phaseIndices = new ArrayList<>(blockEntity.getPhaseIndices());
        if (this.phaseIndices.isEmpty()) {
            this.phaseIndices.add(0);
        }
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
                List<Integer> existingPhaseIndices = entity.getPhaseIndices();
                if (!existingPhaseIndices.isEmpty()) {
                    this.phaseIndices = new ArrayList<>(existingPhaseIndices);
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
                    this.options,
                    this::setSelectedOption
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

        this.panelX = panelX;
        this.panelY = panelY;

        // 相位滑块
        if (phaseCount > 1) {
            rebuildPhaseSliders();
        }

        // 保存按钮
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights.save"), button -> saveAndClose())
                        .dimensions(panelX + 30, panelY + BUTTONS_Y_OFFSET, 60, 20)
                        .build()
        );

        // 取消按钮
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights.cancel"), button -> this.close())
                        .dimensions(panelX + 110, panelY + BUTTONS_Y_OFFSET, 60, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

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
                        formatPhaseIndicesText(), phaseCount),
                panelX + 20, panelY + 60,
                0xFFFFFF00
        );

        if (!isPavement && selectedOption != null) {
            // 预览区域（仅非人行道显示）
            int previewSize = PREVIEW_SIZE;
            int previewX = panelX + 10;
            int previewY = panelY + PREVIEW_Y_OFFSET;

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
                            formatPhaseIndicesText(), phaseCount),
                    previewX + previewSize + 8,
                    previewY + 30,
                    0xFFAAAAAA
            );
        }

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
            TrafficLightsBlockEntity.DirectionType selectedDirection = selectedOption != null ?
                    selectedOption.getDirectionType() : TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE;

            int[] phaseIndicesArray = new int[phaseIndices.size()];
            for (int i = 0; i < phaseIndices.size(); i++) phaseIndicesArray[i] = phaseIndices.get(i);

            TrafficLightsUpdatePacket packet =
                    new TrafficLightsUpdatePacket(pos, phaseIndicesArray, selectedDirection);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS, buf);
        }
        this.close();
    }

    public void setSelectedOption(DirectionOption option) {
        this.selectedOption = option;
    }

    private String formatPhaseIndicesText() {
        if (phaseIndices.isEmpty()) return "-";
        List<Integer> sorted = new ArrayList<>(phaseIndices);
        Collections.sort(sorted);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(sorted.get(i) + 1);
        }
        return sb.toString();
    }

    // ==================== 相位滑块管理 ====================

    private void rebuildPhaseSliders() {
        for (PhaseSliderWidget slider : phaseSliders) {
            this.remove(slider);
        }
        phaseSliders.clear();
        for (ButtonWidget button : phaseRemoveButtons) {
            this.remove(button);
        }
        phaseRemoveButtons.clear();
        if (addPhaseButton != null) {
            this.remove(addPhaseButton);
            addPhaseButton = null;
        }

        int maxSliders = Math.min(MAX_PHASE_SLIDERS, phaseCount);

        for (int i = 0; i < phaseIndices.size(); i++) {
            final int slotIndex = i;
            int y = panelY + PHASE_SLIDER_START_Y + i * PHASE_SLIDER_ROW_HEIGHT;
            PhaseSliderWidget slider = this.addDrawableChild(
                    new PhaseSliderWidget(panelX + 20, y, 140, 20, phaseIndices.get(i), phaseCount, slotIndex)
            );
            phaseSliders.add(slider);

            if (i > 0) {
                ButtonWidget removeButton = this.addDrawableChild(
                        ButtonWidget.builder(Text.literal("×"), button -> removePhaseSlider(slotIndex))
                                .dimensions(panelX + 165, y, 15, 20)
                                .build()
                );
                phaseRemoveButtons.add(removeButton);
            }
        }

        if (phaseSliders.size() < maxSliders) {
            int firstRowY = panelY + PHASE_SLIDER_START_Y;
            addPhaseButton = this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("+"), button -> addPhaseSlider())
                            .dimensions(panelX + 165, firstRowY, 15, 20)
                            .build()
            );
        }
    }

    private void addPhaseSlider() {
        int maxSliders = Math.min(MAX_PHASE_SLIDERS, phaseCount);
        if (phaseIndices.size() >= maxSliders) return;

        int candidate = -1;
        int startFrom = phaseIndices.isEmpty() ? 0 : Collections.max(phaseIndices) + 1;
        for (int v = startFrom; v < phaseCount; v++) {
            if (!phaseIndices.contains(v)) {
                candidate = v;
                break;
            }
        }
        if (candidate < 0) {
            for (int v = 0; v < phaseCount; v++) {
                if (!phaseIndices.contains(v)) {
                    candidate = v;
                    break;
                }
            }
        }
        if (candidate < 0) return;

        phaseIndices.add(candidate);
        rebuildPhaseSliders();
    }

    private void removePhaseSlider(int slotIndex) {
        if (slotIndex <= 0 || slotIndex >= phaseIndices.size()) return;
        phaseIndices.remove(slotIndex);
        rebuildPhaseSliders();
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
        options.add(new DirectionOption(TrafficLightsBlockEntity.DirectionType.SLOW,
                "text.yunbeiuc.traffic_lights.direction.slow"));
        return options;
    }

    // ==================== 内部类保持不变 ====================

    private class PhaseSliderWidget extends SliderWidget {
        private final int phaseCount;
        private final int slotIndex;
        private int currentPhase;

        public PhaseSliderWidget(int x, int y, int width, int height, int initialPhase, int phaseCount, int slotIndex) {
            super(x, y, width, height,
                    Text.literal("相位: " + (initialPhase + 1) + " / " + phaseCount),
                    (double) initialPhase / Math.max(1, phaseCount - 1));
            this.phaseCount = phaseCount;
            this.slotIndex = slotIndex;
            this.currentPhase = initialPhase;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("相位: " + (currentPhase + 1) + " / " + phaseCount));
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            snapToMouse(mouseX);
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
            snapToMouse(mouseX);
        }

        private void snapToMouse(double mouseX) {
            double raw = MathHelper.clamp((mouseX - (this.getX() + 4)) / (double) (this.getWidth() - 8), 0.0, 1.0);
            int steps = Math.max(1, phaseCount - 1);
            int snapped = Math.max(0, Math.min((int) Math.round(raw * steps), phaseCount - 1));
            this.value = (double) snapped / steps;
            applyValue();
        }

        @Override
        protected void applyValue() {
            int steps = Math.max(1, phaseCount - 1);
            int desired = Math.max(0, Math.min((int) Math.round(this.value * steps), phaseCount - 1));

            int resolved = desired;
            if (isTaken(resolved)) {
                resolved = -1;
                for (int offset = 1; offset < phaseCount; offset++) {
                    int upper = desired + offset;
                    int lower = desired - offset;
                    if (upper < phaseCount && !isTaken(upper)) {
                        resolved = upper;
                        break;
                    }
                    if (lower >= 0 && !isTaken(lower)) {
                        resolved = lower;
                        break;
                    }
                }
                if (resolved < 0) resolved = currentPhase;
            }

            currentPhase = resolved;
            this.value = (double) currentPhase / steps;
            if (slotIndex < phaseIndices.size()) {
                phaseIndices.set(slotIndex, currentPhase);
            }
            updateMessage();
        }

        private boolean isTaken(int candidate) {
            for (int i = 0; i < phaseIndices.size(); i++) {
                if (i != slotIndex && phaseIndices.get(i) == candidate) return true;
            }
            return false;
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
                case STRAIGHT_CIRCLE -> 0x00AA00;
                case STRAIGHT_ARROW -> 0x006600;
                case LEFT_TURN -> 0x0000FF;
                case RIGHT_TURN -> 0xFF6600;
                case TURN_AROUND -> 0xAA00AA;
                case NON_MOTOR_VEHICLES -> 0x00AAAA;
                case NON_MOTOR_VEHICLES_LEFT_TURN -> 0x0088AA;
                case NON_MOTOR_VEHICLES_RIGHT_TURN -> 0x00AA88;
                case SLOW -> 0xFFAA00;
            };
        }
    }

    private class DirectionListWidget extends AbstractOptionListWidget<DirectionOption> {
        public DirectionListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight,
                                    List<DirectionOption> directionOptions, Consumer<DirectionOption> onSelect) {
            super(client, width, height, top, bottom, itemHeight, directionOptions,
                    option -> option == selectedOption, onSelect,
                    option -> Text.translatable(option.getTranslationKey()), DirectionOption::getColor);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
