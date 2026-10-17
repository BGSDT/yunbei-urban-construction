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

/**
 * 轨道相位编辑界面：手持**普通魔杖**
 * （{@code com.beigu.yunbeiuc.item.custom.PhaseWand}）右键一条已链接的轨道打开。
 *
 * <p>只做一件事——选择「哪些相位放行本轨道」。一条轨道可以同时被多个相位放行
 * （例如直行与左转共用一段轨道），所以用一组相位滑块，而不是单选。
 *
 * <h3>版式与其它红绿灯页面完全一致</h3>
 * 控件与布局照搬 {@code TrafficLightsScreen}，且**面板尺寸固定**——
 * {@value #PANEL_WIDTH}×{@value #PANEL_HEIGHT} 居中，滑块区固定占
 * {@code MAX_PHASE_SLIDERS * 28} 像素，所以加减槽位时面板不会改变大小、也不会移动：
 * <ul>
 *   <li>顶部居中白色标题（{@code y = 10}，{@code 0xFFFFFF}）；</li>
 *   <li>居中 {@value #PANEL_WIDTH} 宽面板：{@code 0xAA333333} 半透明底 + {@code 0xFFCCCCCC} 边框；</li>
 *   <li>面板内 {@code panelY + 12} 处一行 {@code 0xFFCCCCCC} 面板标题，下面标签 {@code 0xFFAAAAAA}
 *       / 数值 {@code 0xFFFFFF00}；</li>
 *   <li>相位行是 {@code SliderWidget}（{@code 140×20}，起点 {@code panelY + 68}，行距 {@code 28}），
 *       右侧 {@code ×} 删除槽位、首行右侧 {@code +} 新增槽位；</li>
 *   <li>{@code panelY + 255} 处 {@code 保存} / {@code 取消}（{@code 60×20}），
 *       下一行 {@code 全选} / {@code 全不选}（{@code 65×20}）。</li>
 * </ul>
 *
 * <p>滑块槽位上限 {@value #MAX_PHASE_SLIDERS} 个，与红绿灯页面一致。
 *
 * <h3>颜色语义与红绿灯一致（见 {@link PhaseColors}）</h3>
 * <b>在滑块里 = 该相位放行这条轨道（绿）</b>，不在 = 不放行（红）。
 * 世界里这条轨道的高亮线与悬浮标签用的是同一套判断，两边能直接对上号；
 * 当前正在运行的相位那一行滑块边框会加亮成白色。
 *
 * <p>结果以位掩码保存，通过 {@link RailPhaseUpdatePacket} 发回服务端；
 * 服务端会裁剪掩码范围并重新发布信号，列车在未放行的相位里就会在轨道前方停车。
 */
public class RailPhaseScreen extends Screen {

    /** 面板宽度，与 {@code TrafficLightsScreen.RIGHT_PANEL_WIDTH} 一致。 */
    private static final int PANEL_WIDTH = 200;
    /** 面板高度，与 {@code TrafficLightsScreen.RIGHT_PANEL_HEIGHT} 一致。**固定值**，不随槽位增删变化。 */
    private static final int PANEL_HEIGHT = 340;
    /** 相位滑块区起点（相对面板），与红绿灯页面一致。 */
    private static final int PHASE_SLIDER_START_Y = 68;
    /** 相位滑块行距，与红绿灯页面一致。 */
    private static final int PHASE_SLIDER_ROW_HEIGHT = 28;
    /** 滑块槽位上限，与红绿灯页面一致。 */
    private static final int MAX_PHASE_SLIDERS = 4;
    /**
     * 滑块区**固定**占用的高度：按槽位上限算，而不是按当前槽位数算。
     *
     * <p>这是「布局跟红绿灯页面一致」的关键。{@code TrafficLightsScreen} 用的就是
     * {@code MAX_PHASE_SLIDERS * PHASE_SLIDER_ROW_HEIGHT} 这个定值，
     * 所以加减槽位时面板尺寸、位置以及下方按钮都不会跟着上下跳。
     */
    private static final int PHASE_SLIDER_AREA_HEIGHT = MAX_PHASE_SLIDERS * PHASE_SLIDER_ROW_HEIGHT;
    /** 「当前相位」状态行的 y 偏移（相对面板）：紧贴滑块区下方，位置同红绿灯页面的内容区。 */
    private static final int STATE_Y_OFFSET = PHASE_SLIDER_START_Y + PHASE_SLIDER_AREA_HEIGHT + 10;
    /** 保存 / 取消 那一行的 y 偏移（相对面板），与红绿灯页面一致。 */
    private static final int BUTTONS_Y_OFFSET = 255;

    private final BlockPos start;
    private final BlockPos end;
    private final BlockPos groupMember;
    private final int phaseCount;
    private final int[] phaseTimes;

    /** 已勾选放行的相位索引（0 起）。保存时转成位掩码。 */
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
            // 与红绿灯页面一样：至少留一个槽位，否则玩家没有可拖的东西
            this.releasedPhases.add(0);
        }
    }

    // ------------------------------------------------------------------ 布局

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

    /**
     * 面板是**固定尺寸**并居中，跟 {@code TrafficLightsScreen} 一模一样：
     * 加减滑块槽位时面板不会改变大小、也不会移动。
     */
    private void layoutPanel() {
        this.panelX = (this.width - PANEL_WIDTH) / 2;
        this.panelY = (this.height - PANEL_HEIGHT) / 2;
    }

    /** 与红绿灯页面同一套：先撤掉旧控件，再按当前槽位重建。 */
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

    /**
     * 槽位数量变了 → 只需要重建滑块。
     *
     * <p>面板是**固定尺寸**、按钮是**固定位置**（都跟红绿灯页面一致），
     * 所以这里不重新定位任何控件，界面不会因为加减槽位而抖动。
     *
     * <p>也不把整个控件列表清空重建：这些回调是在按钮自己的 {@code mouseClicked} 里触发的，
     * 而那一层正在遍历子控件列表，整体清空会有并发修改风险
     * （{@code TrafficLightsScreen} 也是只重建滑块，同一套做法）。
     */
    private void relayout() {
        rebuildSliders();
    }

    // ------------------------------------------------------------------ 渲染

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 与其它红绿灯页面一致：先压暗世界
        this.renderBackground(context);

        // 顶部居中标题（红绿灯页面也是画在这里，而不是画在面板里）
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.rail_phase.title"), this.width / 2, 10, 0xFFFFFF);

        // 面板（固定尺寸，与红绿灯页面同宽同高）
        context.fill(this.panelX, this.panelY,
                this.panelX + PANEL_WIDTH, this.panelY + PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(this.panelX, this.panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        // 面板标题 / 标签 / 数值：位置与配色都对齐 TrafficLightsScreen
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

        // 当前正在运行的相位：这一行用「这条轨道此刻放不放行」的颜色写出来，
        // 与世界里轨道高亮线 / 悬浮标签是同一个判断（绿=放行 / 黄=即将变红 / 红=不放行）
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

        // 滑块等控件由 super 统一渲染（它们会自己画在当前相位那一行上）
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

    // ------------------------------------------------------------------ 数据

    /** 已选相位的中文列表，例如 {@code "1, 3"}；一个都没有时是 {@code "-"}。 */
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

    /**
     * 从客户端的方块实体读当前活动相位；读不到返回 -1。
     *
     * <p>相位是按周期起点 + 本地游戏刻现算的，所以客户端拿到的值与服务端一致。
     */
    private int resolveActivePhase() {
        final Level level = Minecraft.getInstance().level;
        if (level == null || this.groupMember == null) return -1;
        if (level.getBlockEntity(this.groupMember) instanceof TrafficLightsBlockEntity group
                && group.isInGroup() && group.isCycleActive()) {
            return group.getActivePhase();
        }
        return -1;
    }

    /**
     * 这条轨道此刻的红绿灯状态色（绿=放行 / 黄=黄灯段 / 红=不放行）。
     *
     * <p>直接问客户端的相位组方块实体，与世界里高亮线用的是同一个方法，
     * 所以界面上的「当前相位」颜色与轨道上的颜色必然一致。
     */
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
            case GRAY:   // 绿闪：仍然算放行
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

    // ------------------------------------------------------------------ 滑块

    /**
     * 相位滑块：拖动选择相位序号，同一组内不允许重复。
     *
     * <p>与 {@code TrafficLightsScreen} 里的同名内部类同一套逻辑，
     * 只是写进的是本界面的 {@link #releasedPhases}。
     */
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
