package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.network.NetworkCompat;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.RailPhaseUpdatePacket;
import com.beigu.yunbeiuc.signal.PhaseColors;
import com.beigu.yunbeiuc.signal.RailPhaseBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 轨道相位编辑界面：勾选哪些相位放行这条轨道，保存后发 RailPhaseUpdatePacket。
// 面板固定 200×340，布局常量照抄 TrafficLightsScreen，勿按内容算高度；滑块上限 4。

public class RailPhaseScreen extends Screen {

    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 340;
    private static final int PHASE_SLIDER_START_Y = 68;
    private static final int PHASE_SLIDER_ROW_HEIGHT = 28;
    private static final int MAX_PHASE_SLIDERS = 4;
    private static final int PHASE_SLIDER_AREA_HEIGHT = MAX_PHASE_SLIDERS * PHASE_SLIDER_ROW_HEIGHT;
    private static final int STATE_Y_OFFSET = PHASE_SLIDER_START_Y + PHASE_SLIDER_AREA_HEIGHT + 10;
    private static final int BUTTONS_Y_OFFSET = 255;

    private final BlockPos start;
    private final BlockPos end;
    private final BlockPos groupMember;
    private final int phaseCount;
    private final int[] phaseTimes;

    private final List<Integer> releasedPhases = new ArrayList<>();
    private final List<PhaseSliderWidget> sliders = new ArrayList<>();
    private final List<ButtonWidget> removeButtons = new ArrayList<>();
    private ButtonWidget addButton;
    private ButtonWidget saveButton;
    private ButtonWidget cancelButton;
    private ButtonWidget allButton;
    private ButtonWidget noneButton;

    private int panelX;
    private int panelY;

    public RailPhaseScreen(BlockPos start, BlockPos end, BlockPos groupMember,
                           int phaseCount, int[] phaseTimes, long mask) {
        super(Text.translatable("text.yunbeiuc.rail_phase.title"));
        this.start = start;
        this.end = end;
        this.groupMember = groupMember;
        this.phaseCount = Math.max(0, Math.min(phaseCount, RailPhaseBinding.MAX_PHASES));
        this.phaseTimes = phaseTimes == null ? new int[0] : phaseTimes;

        for (int i = 0; i < this.phaseCount; i++) {
            if ((mask & RailPhaseBinding.bit(i)) != 0L) {
                this.releasedPhases.add(i);
            }
        }
        if (this.releasedPhases.isEmpty() && this.phaseCount > 0) {
            this.releasedPhases.add(0);
        }
    }

    @Override
    protected void init() {
        super.init();

        layoutPanel();

        this.saveButton = this.addDrawableChild(ButtonWidget.builderCompat(
                        Text.translatable("text.yunbeiuc.rail_phase.save"), button -> saveAndClose())
                .dimensions(this.panelX + 30, this.panelY + BUTTONS_Y_OFFSET, 60, 20).build());
        this.cancelButton = this.addDrawableChild(ButtonWidget.builderCompat(
                        Text.translatable("text.yunbeiuc.rail_phase.cancel"), button -> this.close())
                .dimensions(this.panelX + 110, this.panelY + BUTTONS_Y_OFFSET, 60, 20).build());
        this.allButton = this.addDrawableChild(ButtonWidget.builderCompat(
                        Text.translatable("text.yunbeiuc.rail_phase.all"), button -> setAll(true))
                .dimensions(this.panelX + 30, this.panelY + BUTTONS_Y_OFFSET + 25, 65, 20).build());
        this.noneButton = this.addDrawableChild(ButtonWidget.builderCompat(
                        Text.translatable("text.yunbeiuc.rail_phase.none"), button -> setAll(false))
                .dimensions(this.panelX + 105, this.panelY + BUTTONS_Y_OFFSET + 25, 65, 20).build());

        rebuildSliders();
    }

    private void layoutPanel() {
        this.panelX = (this.width - PANEL_WIDTH) / 2;
        this.panelY = (this.height - PANEL_HEIGHT) / 2;
    }

    private void rebuildSliders() {
        for (PhaseSliderWidget slider : this.sliders) {
            this.remove(slider);
        }
        this.sliders.clear();
        for (ButtonWidget button : this.removeButtons) {
            this.remove(button);
        }
        this.removeButtons.clear();
        if (this.addButton != null) {
            this.remove(this.addButton);
            this.addButton = null;
        }

        if (this.phaseCount <= 0) return;

        for (int i = 0; i < this.releasedPhases.size(); i++) {
            final int slotIndex = i;
            final int y = this.panelY + PHASE_SLIDER_START_Y + i * PHASE_SLIDER_ROW_HEIGHT;
            final PhaseSliderWidget slider = this.addDrawableChild(new PhaseSliderWidget(
                    this.panelX + 20, y, 140, 20, this.releasedPhases.get(i), this.phaseCount, slotIndex));
            this.sliders.add(slider);

            if (i > 0) {
                this.removeButtons.add(this.addDrawableChild(
                        ButtonWidget.builderCompat(Text.literal("×"), button -> removePhase(slotIndex))
                                .dimensions(this.panelX + 165, y, 15, 20).build()));
            }
        }

        if (this.releasedPhases.size() < maxSliders()) {
            this.addButton = this.addDrawableChild(
                    ButtonWidget.builderCompat(Text.literal("+"), button -> addPhase())
                            .dimensions(this.panelX + 165, this.panelY + PHASE_SLIDER_START_Y, 15, 20).build());
        }
    }

    private int maxSliders() {
        return Math.min(MAX_PHASE_SLIDERS, this.phaseCount);
    }

    private void addPhase() {
        if (this.releasedPhases.size() >= maxSliders()) return;

        int candidate = -1;
        final int startFrom = this.releasedPhases.isEmpty() ? 0 : Collections.max(this.releasedPhases) + 1;
        for (int v = startFrom; v < this.phaseCount; v++) {
            if (!this.releasedPhases.contains(v)) {
                candidate = v;
                break;
            }
        }
        if (candidate < 0) {
            for (int v = 0; v < this.phaseCount; v++) {
                if (!this.releasedPhases.contains(v)) {
                    candidate = v;
                    break;
                }
            }
        }
        if (candidate < 0) return;

        this.releasedPhases.add(candidate);
        relayout();
    }

    private void removePhase(int slotIndex) {
        if (slotIndex <= 0 || slotIndex >= this.releasedPhases.size()) return;
        this.releasedPhases.remove(slotIndex);
        relayout();
    }

    private void setAll(boolean enabled) {
        this.releasedPhases.clear();
        if (enabled) {
            for (int i = 0; i < maxSliders(); i++) {
                this.releasedPhases.add(i);
            }
        }
        if (this.releasedPhases.isEmpty() && this.phaseCount > 0) {
            this.releasedPhases.add(0);
        }
        relayout();
    }

    private void relayout() {
        rebuildSliders();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.rail_phase.title"), this.width / 2, 10, 0xFFFFFF);

        context.fill(this.panelX, this.panelY,
                this.panelX + PANEL_WIDTH, this.panelY + PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(this.panelX, this.panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights.settings_title"),
                this.panelX + PANEL_WIDTH / 2, this.panelY + 12, 0xFFCCCCCC);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights.phase_label"),
                this.panelX + 20, this.panelY + 42, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.rail_phase.phase_value", formatReleased(), this.phaseCount),
                this.panelX + 20, this.panelY + 52, 0xFFFFFF00);

        if (this.phaseCount <= 0) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.translatable("text.yunbeiuc.rail_phase.empty"),
                    this.panelX + PANEL_WIDTH / 2, this.panelY + PHASE_SLIDER_START_Y, 0xFFFF5555);
        }

        final int activePhase = resolveActivePhase();
        if (activePhase >= 0) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.translatable("text.yunbeiuc.rail_phase.current_phase",
                            Text.literal("相位 " + (activePhase + 1))),
                    this.panelX + PANEL_WIDTH / 2, this.panelY + STATE_Y_OFFSET, resolveStateColor());
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.translatable("text.yunbeiuc.rail_phase.no_cycle"),
                    this.panelX + PANEL_WIDTH / 2, this.panelY + STATE_Y_OFFSET, 0xFFAAAAAA);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.close();
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPauseCompat() {
        return false;
    }

    private String formatReleased() {
        if (this.releasedPhases.isEmpty()) return "-";
        final List<Integer> sorted = new ArrayList<>(this.releasedPhases);
        Collections.sort(sorted);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(sorted.get(i) + 1);
        }
        return sb.toString();
    }

    private int resolveActivePhase() {
        final Level level = Minecraft.getInstance().level;
        if (level == null || this.groupMember == null) return -1;
        if (level.getBlockEntity(this.groupMember) instanceof TrafficLightsBlockEntity group
                && group.isInGroup() && group.isCycleActive()) {
            return group.getActivePhase();
        }
        return -1;
    }

    private int resolveStateColor() {
        final Level level = Minecraft.getInstance().level;
        if (level == null || this.groupMember == null) return PhaseColors.UNKNOWN;
        if (!(level.getBlockEntity(this.groupMember) instanceof TrafficLightsBlockEntity group)
                || !group.isInGroup()) {
            return PhaseColors.UNKNOWN;
        }
        final RailPhaseBinding binding = group.findRailBinding(this.start, this.end);
        if (binding == null) return PhaseColors.UNKNOWN;
        switch (group.getRailLightState(binding)) {
            case GREEN:
            case GRAY:
                return PhaseColors.RELEASED;
            case YELLOW:
                return PhaseColors.WARNING;
            case RED:
            default:
                return PhaseColors.HELD;
        }
    }

    private void saveAndClose() {
        long mask = 0L;
        for (int index : this.releasedPhases) {
            mask |= RailPhaseBinding.bit(index);
        }
        final FriendlyByteBuf buf = NetworkCompat.newBuffer();
        new RailPhaseUpdatePacket(this.start, this.end, this.groupMember, mask).write(buf);
        NetworkCompat.sendToServer(ModMessages.UPDATE_RAIL_PHASE, buf);
        this.close();
    }

    private class PhaseSliderWidget extends SliderWidget {
        private final int slotIndex;
        private int currentPhase;

        PhaseSliderWidget(int x, int y, int width, int height, int initialPhase, int phaseCount, int slotIndex) {
            super(x, y, width, height,
                    Text.literal("相位: " + (initialPhase + 1) + " / " + phaseCount),
                    (double) initialPhase / Math.max(1, phaseCount - 1));
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
            final double raw = Mth.clamp((mouseX - (this.getX() + 4)) / (double) (this.getWidth() - 8), 0.0, 1.0);
            final int steps = Math.max(1, phaseCount - 1);
            final int snapped = Math.max(0, Math.min((int) Math.round(raw * steps), phaseCount - 1));
            this.value = (double) snapped / steps;
            applyValue();
        }

        @Override
        protected void applyValue() {
            final int steps = Math.max(1, phaseCount - 1);
            final int desired = Math.max(0, Math.min((int) Math.round(this.value * steps), phaseCount - 1));

            int resolved = desired;
            if (isTaken(resolved)) {
                resolved = -1;
                for (int offset = 1; offset < phaseCount; offset++) {
                    final int upper = desired + offset;
                    final int lower = desired - offset;
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
            if (slotIndex < releasedPhases.size()) {
                releasedPhases.set(slotIndex, currentPhase);
            }
            updateMessage();
        }

        private boolean isTaken(int candidate) {
            for (int i = 0; i < releasedPhases.size(); i++) {
                if (i != slotIndex && releasedPhases.get(i) == candidate) return true;
            }
            return false;
        }
    }
}
