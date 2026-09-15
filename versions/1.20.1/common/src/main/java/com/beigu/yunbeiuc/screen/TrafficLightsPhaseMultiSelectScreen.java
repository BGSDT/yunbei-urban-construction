package com.beigu.yunbeiuc.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TrafficLightsPhaseMultiSelectScreen extends Screen {
    private static final int PANEL_WIDTH = 250;
    private static final int SLIDER_HEIGHT = 28;
    private static final int MAX_SLIDERS = 4;

    private final Screen previousScreen;
    private final int phaseCount;
    private final List<Integer> selectedPhases;
    private final Consumer<List<Integer>> onConfirm;
    private final List<PhaseSliderWidget> sliders = new ArrayList<>();
    private final List<ButtonWidget> removeButtons = new ArrayList<>();
    private ButtonWidget addButton;
    private ButtonWidget confirmButton;
    private ButtonWidget cancelButton;

    public TrafficLightsPhaseMultiSelectScreen(Screen previousScreen, int phaseCount, List<Integer> currentPhases, Consumer<List<Integer>> onConfirm) {
        super(Text.literal("选择相位"));
        this.previousScreen = previousScreen;
        this.phaseCount = phaseCount;
        this.selectedPhases = new ArrayList<>(currentPhases);
        this.onConfirm = onConfirm;
    }

    @Override
    protected void init() {
        super.init();
        rebuildSliders();
    }

    private void rebuildSliders() {
        for (PhaseSliderWidget slider : sliders) {
            this.remove(slider);
        }
        sliders.clear();

        for (ButtonWidget btn : removeButtons) {
            this.remove(btn);
        }
        removeButtons.clear();

        if (addButton != null) {
            this.remove(addButton);
            addButton = null;
        }
        if (confirmButton != null) {
            this.remove(confirmButton);
            confirmButton = null;
        }
        if (cancelButton != null) {
            this.remove(cancelButton);
            cancelButton = null;
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - 300) / 2;
        int sliderStartY = panelY + 40;

        for (int i = 0; i < selectedPhases.size(); i++) {
            int y = sliderStartY + i * SLIDER_HEIGHT;
            int slotIndex = i;
            PhaseSliderWidget slider = this.addDrawableChild(
                new PhaseSliderWidget(panelX + 20, y, 140, 20, selectedPhases.get(i), phaseCount, slotIndex)
            );
            sliders.add(slider);

            if (i > 0) {
                ButtonWidget removeBtn = this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("×"), button -> removePhase(slotIndex))
                        .dimensions(panelX + 165, y, 15, 20)
                        .build()
                );
                removeButtons.add(removeBtn);
            }
        }

        if (sliders.size() < MAX_SLIDERS) {
            addButton = this.addDrawableChild(
                ButtonWidget.builder(Text.literal("+"), button -> addPhase())
                    .dimensions(panelX + 165, sliderStartY, 15, 20)
                    .build()
            );
        }

        int buttonY = sliderStartY + Math.max(1, selectedPhases.size()) * SLIDER_HEIGHT + 20;
        confirmButton = this.addDrawableChild(
            ButtonWidget.builder(Text.literal("确认"), button -> confirm())
                .dimensions(this.width / 2 - 100, buttonY, 80, 20)
                .build()
        );
        cancelButton = this.addDrawableChild(
            ButtonWidget.builder(Text.literal("取消"), button -> this.close())
                .dimensions(this.width / 2 + 20, buttonY, 80, 20)
                .build()
        );
    }

    private void addPhase() {
        if (selectedPhases.size() >= MAX_SLIDERS) return;

        int candidate = -1;
        int startFrom = selectedPhases.isEmpty() ? 0 : Collections.max(selectedPhases) + 1;
        for (int v = startFrom; v < phaseCount; v++) {
            if (!selectedPhases.contains(v)) {
                candidate = v;
                break;
            }
        }
        if (candidate < 0) {
            for (int v = 0; v < phaseCount; v++) {
                if (!selectedPhases.contains(v)) {
                    candidate = v;
                    break;
                }
            }
        }
        if (candidate < 0) return;

        selectedPhases.add(candidate);
        rebuildSliders();
    }

    private void removePhase(int slotIndex) {
        if (slotIndex <= 0 || slotIndex >= selectedPhases.size()) return;
        selectedPhases.remove(slotIndex);
        rebuildSliders();
    }

    private void confirm() {
        if (!selectedPhases.isEmpty()) {
            onConfirm.accept(new ArrayList<>(selectedPhases));
            this.close();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - 300) / 2;

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 300, 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, 300, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, panelY + 12, 0xFFCCCCCC);
        context.drawTextWithShadow(this.textRenderer, Text.literal("已选相位:"), panelX + 20, panelY + 25, 0xFFAAAAAA);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(previousScreen);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

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
            if (slotIndex < selectedPhases.size()) {
                selectedPhases.set(slotIndex, currentPhase);
            }
            updateMessage();
        }

        private boolean isTaken(int candidate) {
            for (int i = 0; i < selectedPhases.size(); i++) {
                if (i != slotIndex && selectedPhases.get(i) == candidate) return true;
            }
            return false;
        }
    }
}



