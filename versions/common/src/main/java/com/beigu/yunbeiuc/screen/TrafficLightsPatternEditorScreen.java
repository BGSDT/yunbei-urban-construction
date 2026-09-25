package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.text.Text;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPatternPreset;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.util.TrafficLightsPatternCategoryManager;
import com.beigu.yunbeiuc.util.TrafficLightsPatternPresetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TrafficLightsPatternEditorScreen extends Screen {
    private static final Random RANDOM = new Random();
    private static final int PANEL_WIDTH = 330;
    private static final int MAX_SLOTS = 32;
    private static final int MIN_PHASE_COUNT = 2;
    private static final int MAX_PHASE_COUNT = 16;
    private static final int MAX_ORDER = 16;
    // 面板高度比原来多留 22px 用于新增的"颜色"行（名称行与方位/类型表头之间）。
    private static final int FIXED_PANEL_HEIGHT = 322;
    private static final int LIST_AREA_HEIGHT = 120;
    private static final int PREVIEW_PANEL_WIDTH = PANEL_WIDTH / 2;
    private static final int PREVIEW_PANEL_HEIGHT = PANEL_WIDTH / 2;
    private static final int PREVIEW_PIXEL_SIZE = 4;
    // 同方位像素簇的最大展开跨度：顺序上限提高到16后，簇内像素按此跨度收紧间距，
    // 数量较多时间距会小于像素尺寸从而重叠，而不是把预览面板撑爆。
    private static final double PREVIEW_CLUSTER_MAX_SPAN = 60;
    // 预览像素（含簇展开后的最大偏移）与预览面板边框之间必须保留的最小间隙。
    private static final int PREVIEW_EDGE_MARGIN = 15;
    private static final int COLOR_SWATCH_SIZE = 16;
    private static final int COLOR_SWATCH_GAP = 3;
    private static final int[] PRESET_COLOR_PALETTE = {
            0xFF5555, 0xFFAA00, 0xFFFF55, 0x55FF55, 0x55FFFF,
            0x5599FF, 0xAA55FF, 0xFF55AA, 0xFFFFFF, 0xAAAAAA
    };

    private final List<TrafficLightsPatternPreset.Slot> slots = new ArrayList<>();
    private int phaseCount = 4;
    // 构造函数中会立即赋值为随机颜色（新建）或已有预设颜色（编辑），此处的-1只是占位。
    private int presetColor = -1;

    private TextFieldWidget nameField;
    private String savedName = "";
    private Component errorMessage = null;

    private int panelX;
    private int panelY;
    private SlotListWidget slotListWidget;
    private int previewPanelX;
    private int previewPanelY;
    private int colorSwatchX;
    private int colorSwatchY;
    private List<PreviewPixel> previewPixels = new ArrayList<>();
    private double savedScrollAmount = 0;
    // 非空时，保存成功后返回到来源的相位预设选择界面；为空则保存后直接关闭（如从计时界面打开时）。
    private final BlockPos returnPos;
    // 保存的预设所归属的分类；从计时界面直接打开（无上下文）时默认归入"默认分类"。
    private final String category;
    private final Screen previousScreen;

    public TrafficLightsPatternEditorScreen() {
        this(null, TrafficLightsPatternCategoryManager.DEFAULT_CATEGORY, null);
    }

    public TrafficLightsPatternEditorScreen(BlockPos returnPos, String category, Screen previousScreen) {
        super(Text.literal("相位分配预设编辑器"));
        this.returnPos = returnPos;
        this.category = category;
        this.previousScreen = previousScreen;
        this.presetColor = randomColor();
    }

    public TrafficLightsPatternEditorScreen(TrafficLightsPatternPreset existing, BlockPos returnPos, String category, Screen previousScreen) {
        super(Text.literal("相位分配预设编辑器"));
        this.returnPos = returnPos;
        this.category = category;
        this.previousScreen = previousScreen;
        this.savedName = existing.getName();
        this.phaseCount = existing.getPhaseCount();
        this.presetColor = existing.getColor() != -1 ? existing.getColor() : randomColor();
        for (TrafficLightsPatternPreset.Slot s : existing.getSlots()) {
            this.slots.add(new TrafficLightsPatternPreset.Slot(
                    s.getDirection(), s.getKind(), s.getDirectionType(),
                    new ArrayList<>(s.getPhaseIndices()), s.getOrder()
            ));
        }
    }

    private static int randomColor() {
        return RANDOM.nextInt(0x1000000);
    }

    @Override
    protected void init() {
        super.init();
        this.panelX = 30;
        this.panelY = (this.height - FIXED_PANEL_HEIGHT) / 2;
        this.previewPanelX = panelX + PANEL_WIDTH + 20;
        this.previewPanelY = panelY + (FIXED_PANEL_HEIGHT - PREVIEW_PANEL_HEIGHT) / 2;
        this.colorSwatchX = panelX + 50;
        this.colorSwatchY = panelY + 54;

        nameField = new TextFieldWidget(this.textRenderer, panelX + 50, panelY + 28, 150, 20, Text.literal(""));
        nameField.setMaxLength(24);
        nameField.setText(savedName);
        nameField.setChangedListener(text -> {
            savedName = text;
            errorMessage = null;
        });
        this.addDrawableChild(nameField);

        this.addDrawableChild(
                ButtonWidget.builderCompat(Text.literal("-"), button -> changePhaseCount(-1))
                        .dimensions(panelX + 210, panelY + 28, 20, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builderCompat(Text.literal("+"), button -> changePhaseCount(1))
                        .dimensions(panelX + 255, panelY + 28, 20, 20)
                        .build()
        );

        int listTop = panelY + 74;
        int listBottom = listTop + LIST_AREA_HEIGHT;
        this.slotListWidget = new SlotListWidget(this.client, PANEL_WIDTH, LIST_AREA_HEIGHT, listTop, listBottom, 22);
        this.slotListWidget.setLeftPos(panelX);
        this.slotListWidget.setRenderBackground(false);
        this.slotListWidget.setRenderHorizontalShadows(false);
        this.slotListWidget.setScrollAmount(savedScrollAmount);
        this.addDrawableChild(this.slotListWidget);

        if (slots.size() < MAX_SLOTS) {
            this.addDrawableChild(
                    ButtonWidget.builderCompat(Text.literal("添加槽位"), button -> addSlot())
                            .dimensions(panelX + 10, listBottom + 4, 80, 20)
                            .build()
            );
        }

        int buttonY = panelY + FIXED_PANEL_HEIGHT - 32;
        this.addDrawableChild(
                ButtonWidget.builderCompat(Text.literal("保存"), button -> saveAndClose())
                        .dimensions(panelX + 40, buttonY, 80, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builderCompat(Text.literal("取消"), button -> this.close())
                        .dimensions(panelX + 160, buttonY, 80, 20)
                        .build()
        );

        rebuildPreview();
    }

    private void rebuild() {
        if (this.slotListWidget != null) {
            this.savedScrollAmount = this.slotListWidget.getScrollAmount();
        }
        this.clearChildren();
        this.init();
    }

    private void cycleDirection(TrafficLightsPatternPreset.Slot slot) {
        TrafficLightsPatternPreset.Direction8[] values = TrafficLightsPatternPreset.Direction8.values();
        slot.setDirection(values[(slot.getDirection().ordinal() + 1) % values.length]);
    }

    private void cycleKind(TrafficLightsPatternPreset.Slot slot) {
        TrafficLightsPatternPreset.MemberKind[] values = TrafficLightsPatternPreset.MemberKind.values();
        slot.setKind(values[(slot.getKind().ordinal() + 1) % values.length]);
    }

    private void cycleDirectionType(TrafficLightsPatternPreset.Slot slot) {
        TrafficLightsBlockEntity.DirectionType[] values = TrafficLightsBlockEntity.DirectionType.values();
        slot.setDirectionType(values[(slot.getDirectionType().ordinal() + 1) % values.length]);
    }

    private String formatPhaseIndices(List<Integer> indices) {
        if (indices.isEmpty()) return "";
        if (indices.size() == 1) return String.valueOf(indices.get(0) + 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(indices.size(), 2); i++) {
            if (i > 0) sb.append(",");
            sb.append(indices.get(i) + 1);
        }
        if (indices.size() > 2) sb.append("...");
        return sb.toString();
    }

    private void openPhaseMultiSelect(TrafficLightsPatternPreset.Slot slot) {
        Minecraft.getInstance().setScreen(
            new TrafficLightsPhaseMultiSelectScreen(
                this,
                phaseCount,
                slot.getPhaseIndices(),
                selected -> {
                    slot.setPhaseIndices(new ArrayList<>(selected));
                    rebuild();
                }
            )
        );
    }

    private void increaseOrder(TrafficLightsPatternPreset.Slot slot) {
        int next = slot.getOrder() + 1;
        if (next > MAX_ORDER) next = 1;
        slot.setOrder(next);
    }

    private void decreaseOrder(TrafficLightsPatternPreset.Slot slot) {
        int next = slot.getOrder() - 1;
        if (next < 1) next = MAX_ORDER;
        slot.setOrder(next);
    }

    private void changePhaseCount(int delta) {
        int newCount = phaseCount + delta;
        if (newCount < MIN_PHASE_COUNT || newCount > MAX_PHASE_COUNT) return;
        phaseCount = newCount;
        for (TrafficLightsPatternPreset.Slot slot : slots) {
            List<Integer> indices = slot.getPhaseIndices();
            for (int i = 0; i < indices.size(); i++) {
                if (indices.get(i) >= phaseCount) {
                    indices.set(i, phaseCount - 1);
                }
            }
        }
        errorMessage = null;
        rebuild();
    }

    private void addSlot() {
        if (slots.size() >= MAX_SLOTS) return;
        slots.add(new TrafficLightsPatternPreset.Slot(
                TrafficLightsPatternPreset.Direction8.N,
                TrafficLightsPatternPreset.MemberKind.NORMAL,
                TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE,
                List.of(0)
        ));
        rebuild();
    }

    /**
     * 按 方位+类型 分桶，找出桶内顺序（order）重复的槽位。
     * 与 {@link TrafficLightsPatternPreset#tryApplyToGroup} 的分桶校验逻辑保持一致，
     * 用于编辑器内实时预览/校验，避免保存后才在应用时才发现冲突。
     */
    private Set<TrafficLightsPatternPreset.Slot> findCollidingSlots() {
        Set<TrafficLightsPatternPreset.Slot> colliding = new HashSet<>();
        Map<String, List<TrafficLightsPatternPreset.Slot>> buckets = new HashMap<>();
        for (TrafficLightsPatternPreset.Slot slot : slots) {
            String key = slot.getDirection().name() + "|" + slot.getKind().name();
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(slot);
        }
        for (List<TrafficLightsPatternPreset.Slot> bucket : buckets.values()) {
            if (bucket.size() < 2) continue;
            Map<Integer, List<TrafficLightsPatternPreset.Slot>> byOrder = new HashMap<>();
            for (TrafficLightsPatternPreset.Slot slot : bucket) {
                byOrder.computeIfAbsent(slot.getOrder(), o -> new ArrayList<>()).add(slot);
            }
            for (List<TrafficLightsPatternPreset.Slot> sameOrder : byOrder.values()) {
                if (sameOrder.size() > 1) {
                    colliding.addAll(sameOrder);
                }
            }
        }
        return colliding;
    }

    private void saveAndClose() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            errorMessage = Text.literal("§c请输入预设名称！");
            return;
        }
        if (slots.isEmpty()) {
            errorMessage = Text.literal("§c至少需要一个槽位！");
            return;
        }
        if (!findCollidingSlots().isEmpty()) {
            errorMessage = Text.literal("§c存在同方位同类型的顺序重复，请先调整顺序！");
            return;
        }
        TrafficLightsPatternPreset preset = new TrafficLightsPatternPreset(name, phaseCount, new ArrayList<>(slots));
        preset.setColor(presetColor);
        preset.setCategory(category);
        TrafficLightsPatternPresetManager.addPreset(preset);
        if (returnPos != null && previousScreen != null) {
            Minecraft.getInstance().setScreen(new TrafficLightsPatternSelectScreen(returnPos, category, previousScreen));
            return;
        }
        this.close();
    }

    private static String directionTypeLabel(TrafficLightsBlockEntity.DirectionType type) {
        return switch (type) {
            case STRAIGHT_CIRCLE -> "直行（圆形）";
            case STRAIGHT_ARROW -> "直行（箭头）";
            case LEFT_TURN -> "左转";
            case RIGHT_TURN -> "右转";
            case TURN_AROUND -> "掉头";
            case NON_MOTOR_VEHICLES -> "非机动车";
            case NON_MOTOR_VEHICLES_LEFT_TURN -> "非机动车（左转）";
            case NON_MOTOR_VEHICLES_RIGHT_TURN -> "非机动车（右转）";
            case COLOR_FLASH -> "色闪";
            case SLOW_FLASH -> "慢闪";
        };
    }

    private void rebuildPreview() {
        previewPixels.clear();
        if (slots.isEmpty()) return;

        int centerX = previewPanelX + PREVIEW_PANEL_WIDTH / 2;
        int centerY = previewPanelY + PREVIEW_PANEL_HEIGHT / 2;
        int radius = 80;
        int maxSpacing = PREVIEW_PIXEL_SIZE + 4;

        // 像素点（左上角坐标）允许出现的范围，保证像素本体与簇内展开都离面板边框至少留出
        // PREVIEW_EDGE_MARGIN，不会紧贴甚至超出边框。
        int minPixelX = previewPanelX + PREVIEW_EDGE_MARGIN;
        int maxPixelX = previewPanelX + PREVIEW_PANEL_WIDTH - PREVIEW_EDGE_MARGIN - PREVIEW_PIXEL_SIZE;
        int minPixelY = previewPanelY + PREVIEW_EDGE_MARGIN;
        int maxPixelY = previewPanelY + PREVIEW_PANEL_HEIGHT - PREVIEW_EDGE_MARGIN - PREVIEW_PIXEL_SIZE;

        // 同方位的槽位作为一组整体居中排列（沿"从左到右"方向对称展开），
        // 而不是从 order=1 开始单向偏移，避免同方位多个槽位时显示歪斜。
        Map<TrafficLightsPatternPreset.Direction8, List<TrafficLightsPatternPreset.Slot>> byDirection = new LinkedHashMap<>();
        for (TrafficLightsPatternPreset.Slot slot : slots) {
            byDirection.computeIfAbsent(slot.getDirection(), d -> new ArrayList<>()).add(slot);
        }

        for (Map.Entry<TrafficLightsPatternPreset.Direction8, List<TrafficLightsPatternPreset.Slot>> entry : byDirection.entrySet()) {
            TrafficLightsPatternPreset.Direction8 dir = entry.getKey();
            List<TrafficLightsPatternPreset.Slot> dirSlots = entry.getValue();
            dirSlots.sort(Comparator.comparingInt(TrafficLightsPatternPreset.Slot::getOrder));

            double baseX = centerX + dir.getOutX() * radius;
            double baseY = centerY + dir.getOutZ() * radius;

            double rightX = -dir.getOutZ();
            double rightZ = dir.getOutX();

            // Direction8 只有 N/E/S/W，outX/outZ 及由其推出的 rightX/rightZ 必然是
            // 0 或 ±1 的轴对齐单位向量（簇要么整体沿 X 展开，要么整体沿 Z 展开，不会斜向），
            // 因此下面按“单轴可用余量”裁剪即可，无需通用的二维线段裁剪。
            baseX = Math.max(minPixelX, Math.min(maxPixelX, baseX));
            baseY = Math.max(minPixelY, Math.min(maxPixelY, baseY));

            int count = dirSlots.size();
            // 序号上限提到16后，同方位像素簇按固定最大跨度收紧间距；
            // 数量较多时间距会小于像素尺寸，使像素点相互重叠而不是撑爆预览面板。
            double spacing = count > 1 ? Math.min(maxSpacing, PREVIEW_CLUSTER_MAX_SPAN / (count - 1)) : maxSpacing;

            double edgeOffset = count > 1 ? (count - 1) / 2.0 * spacing : 0;
            if (edgeOffset > 0) {
                double axisRoom;
                if (rightX != 0) {
                    axisRoom = Math.min(baseX - minPixelX, maxPixelX - baseX);
                } else {
                    axisRoom = Math.min(baseY - minPixelY, maxPixelY - baseY);
                }
                if (axisRoom < 0) axisRoom = 0;
                if (edgeOffset > axisRoom) {
                    spacing *= axisRoom / edgeOffset;
                    edgeOffset = axisRoom;
                }
            }

            for (int i = 0; i < count; i++) {
                double centeredIndex = i - (count - 1) / 2.0;

                int pixelX = (int) Math.round(baseX + rightX * centeredIndex * spacing);
                int pixelY = (int) Math.round(baseY + rightZ * centeredIndex * spacing);

                previewPixels.add(new PreviewPixel(pixelX, pixelY, dirSlots.get(i)));
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + FIXED_PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, FIXED_PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                panelX + PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        context.drawTextWithShadow(this.textRenderer, Text.literal("名称:"), panelX + 12, panelY + 34, 0xFFAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("" + phaseCount), panelX + 240, panelY + 34, 0xFFFFFF00);

        context.drawTextWithShadow(this.textRenderer, Text.literal("颜色:"), panelX + 12, colorSwatchY + 6, 0xFFAAAAAA);
        context.fill(colorSwatchX, colorSwatchY, colorSwatchX + COLOR_SWATCH_SIZE, colorSwatchY + COLOR_SWATCH_SIZE, 0xFF000000 | presetColor);
        context.drawBorder(colorSwatchX, colorSwatchY, COLOR_SWATCH_SIZE, COLOR_SWATCH_SIZE, 0xFF888888);

        context.drawTextWithShadow(this.textRenderer, Text.literal("方位"), panelX + 20, panelY + 84, 0xFF888888);
        context.drawTextWithShadow(this.textRenderer, Text.literal("类型"), panelX + 70, panelY + 84, 0xFF888888);
        context.drawTextWithShadow(this.textRenderer, Text.literal("图案"), panelX + 132, panelY + 84, 0xFF888888);
        context.drawTextWithShadow(this.textRenderer, Text.literal("相位"), panelX + 198, panelY + 84, 0xFF888888);
        context.drawTextWithShadow(this.textRenderer, Text.literal("顺序"), panelX + 245, panelY + 84, 0xFF888888);

        int listBottom = panelY + 96 + LIST_AREA_HEIGHT;
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("方位为相对路口中心的方位；同方位同类型"),
                panelX + PANEL_WIDTH / 2,
                listBottom + 30,
                0xFF777777
        );
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("有多个时用顺序区分（面朝该方位，从左到右为第1、2...个）"),
                panelX + PANEL_WIDTH / 2,
                listBottom + 40,
                0xFF777777
        );

        if (errorMessage != null) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    errorMessage,
                    panelX + PANEL_WIDTH / 2,
                    panelY + FIXED_PANEL_HEIGHT - 52,
                    0xFFFF5555
            );
        }

        context.fill(previewPanelX, previewPanelY, previewPanelX + PREVIEW_PANEL_WIDTH, previewPanelY + PREVIEW_PANEL_HEIGHT, 0xFF000000);
        context.drawBorder(previewPanelX, previewPanelY, PREVIEW_PANEL_WIDTH, PREVIEW_PANEL_HEIGHT, 0xFF888888);

        Set<TrafficLightsPatternPreset.Slot> colliding = findCollidingSlots();
        for (PreviewPixel pixel : previewPixels) {
            int color = colliding.contains(pixel.slot) ? 0xFFFF3333 : 0xFFFFFFFF;
            context.fill(pixel.x, pixel.y, pixel.x + PREVIEW_PIXEL_SIZE, pixel.y + PREVIEW_PIXEL_SIZE, color);
        }

        super.render(context, mouseX, mouseY, delta);

        PreviewPixel hoveredPixel = null;
        for (PreviewPixel pixel : previewPixels) {
            if (mouseX >= pixel.x && mouseX < pixel.x + PREVIEW_PIXEL_SIZE && mouseY >= pixel.y && mouseY < pixel.y + PREVIEW_PIXEL_SIZE) {
                hoveredPixel = pixel;
                break;
            }
        }
        if (hoveredPixel != null) {
            drawSlotTooltip(context, mouseX, mouseY, hoveredPixel.slot, colliding.contains(hoveredPixel.slot));
        }
    }

    private void drawSlotTooltip(DrawContext context, int mx, int my, TrafficLightsPatternPreset.Slot slot, boolean colliding) {
        List<TooltipEntry> tips = new ArrayList<>();
        if (colliding) {
            tips.add(new TooltipEntry(
                    "§c有红绿灯重叠，需调整顺序",
                    null
            ));
        }
        List<Integer> phases = slot.getPhaseIndices();
        StringBuilder phaseStr = new StringBuilder();
        for (int i = 0; i < phases.size(); i++) {
            if (i > 0) phaseStr.append(",");
            phaseStr.append(phases.get(i) + 1);
        }
        tips.add(new TooltipEntry(
                slot.getDirection().getLabel() + " " + slot.getKind().getLabel(),
                directionTypeLabel(slot.getDirectionType()),
                "相位 " + phaseStr,
                "顺序 " + slot.getOrder()
        ));
        drawTooltip(context, mx, my, tips);
    }

    private void drawTooltip(DrawContext context, int mx, int my, List<TooltipEntry> entries) {
        int lh = textRenderer.lineHeight + 2, mw = 0;
        List<TooltipLine> lines = new ArrayList<>();
        for (var e : entries) {
            if (!e.title.isEmpty()) {
                lines.add(new TooltipLine(e.title, 0xFFFFFFFF));
                mw = Math.max(mw, textRenderer.width(e.title));
            }
            if (e.value != null) {
                String v = "  " + e.value;
                lines.add(new TooltipLine(v, 0xFFFFD966));
                mw = Math.max(mw, textRenderer.width(v));
            }
            for (String d : e.descriptions) {
                lines.add(new TooltipLine("  " + d, 0xFFAAAAAA));
                mw = Math.max(mw, textRenderer.width("  " + d));
            }
        }
        int th = 4 + lines.size() * lh, tx = Math.min(mx + 12, width - mw - 10), ty = Math.min(my - th - 4, height - th - 4);
        if (ty < 4) ty = my + 12;
        context.getMatrices().pushPose();
        context.getMatrices().translate(0, 0, 1000);
        context.fill(tx, ty, tx + mw + 8, ty + th, 0xFF1E1E2E);
        context.drawBorder(tx, ty, mw + 8, th, 0xFF6B6B8A);
        int ty2 = ty + 2;
        for (TooltipLine line : lines) {
            context.drawText(textRenderer, Text.literal(line.text), tx + 4, ty2, line.color, false);
            ty2 += lh;
        }
        context.getMatrices().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        Minecraft.getInstance().setScreen(previousScreen);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (mouseX >= colorSwatchX && mouseX < colorSwatchX + COLOR_SWATCH_SIZE && mouseY >= colorSwatchY && mouseY < colorSwatchY + COLOR_SWATCH_SIZE) {
                Minecraft.getInstance().setScreen(new ColorPickerScreen(this, presetColor, c -> presetColor = c));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private class SlotListWidget extends ElementListWidget<SlotListWidget.SlotEntry> {
        public SlotListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            for (TrafficLightsPatternPreset.Slot slot : slots) {
                this.addEntry(new SlotEntry(slot));
            }
        }

        @Override
        public int getRowWidth() {
            return PANEL_WIDTH - 10;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.left + PANEL_WIDTH - 6;
        }

        public class SlotEntry extends ElementListWidget.Entry<SlotEntry> {
            private final TrafficLightsPatternPreset.Slot slot;
            private final ButtonWidget directionButton;
            private final ButtonWidget kindButton;
            private final ButtonWidget directionTypeButton;
            private final ButtonWidget phaseButton;
            private final ButtonWidget orderLeftButton;
            private final ButtonWidget orderRightButton;
            private final ButtonWidget removeButton;
            private final List<ClickableWidget> widgetList;

            public SlotEntry(TrafficLightsPatternPreset.Slot slot) {
                this.slot = slot;

                this.directionButton = ButtonWidget.builderCompat(Text.literal(slot.getDirection().getLabel()), button -> {
                            cycleDirection(slot);
                            rebuild();
                        })
                        .dimensions(0, 0, 45, 20)
                        .build();

                this.kindButton = ButtonWidget.builderCompat(Text.literal(slot.getKind().getLabel()), button -> {
                            cycleKind(slot);
                            rebuild();
                        })
                        .dimensions(0, 0, 52, 20)
                        .build();

                this.directionTypeButton = ButtonWidget.builderCompat(Text.literal(directionTypeLabel(slot.getDirectionType())), button -> {
                            cycleDirectionType(slot);
                            rebuild();
                        })
                        .dimensions(0, 0, 76, 20)
                        .build();

                this.phaseButton = ButtonWidget.builderCompat(Text.literal("相位" + formatPhaseIndices(slot.getPhaseIndices())), button -> {
                            openPhaseMultiSelect(slot);
                        })
                        .dimensions(0, 0, 44, 20)
                        .build();

                this.orderLeftButton = ButtonWidget.builderCompat(Text.literal("←"), button -> {
                            decreaseOrder(slot);
                            rebuild();
                        })
                        .dimensions(0, 0, 14, 20)
                        .build();

                this.orderRightButton = ButtonWidget.builderCompat(Text.literal("→"), button -> {
                            increaseOrder(slot);
                            rebuild();
                        })
                        .dimensions(0, 0, 14, 20)
                        .build();

                this.removeButton = ButtonWidget.builderCompat(Text.literal("×"), button -> {
                            slots.remove(slot);
                            rebuild();
                        })
                        .dimensions(0, 0, 20, 20)
                        .build();

                this.widgetList = List.of(directionButton, kindButton, directionTypeButton, phaseButton, orderLeftButton, orderRightButton, removeButton);
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                directionButton.setPosition(panelX + 10, y);
                kindButton.setPosition(panelX + 57, y);
                directionTypeButton.setPosition(panelX + 111, y);
                phaseButton.setPosition(panelX + 189, y);
                orderLeftButton.setPosition(panelX + 235, y);
                orderRightButton.setPosition(panelX + 265, y);
                removeButton.setPosition(panelX + 281, y);

                TrafficLightsPatternPreset.MemberKind kind = slot.getKind();
                boolean lockDirectionType = (kind == TrafficLightsPatternPreset.MemberKind.PAVEMENT || kind == TrafficLightsPatternPreset.MemberKind.COUNTDOWN_TIMER);
                directionTypeButton.active = !lockDirectionType;

                directionButton.render(context, mouseX, mouseY, tickDelta);
                kindButton.render(context, mouseX, mouseY, tickDelta);
                directionTypeButton.render(context, mouseX, mouseY, tickDelta);
                phaseButton.render(context, mouseX, mouseY, tickDelta);
                orderLeftButton.render(context, mouseX, mouseY, tickDelta);
                context.drawCenteredTextWithShadow(
                        client.font,
                        Text.literal("" + slot.getOrder()),
                        panelX + 256,
                        y + 6,
                        0xFFFFFF00
                );
                orderRightButton.render(context, mouseX, mouseY, tickDelta);
                removeButton.render(context, mouseX, mouseY, tickDelta);
            }

            @Override
            public List<? extends ClickableWidget> children() {
                return widgetList;
            }

            @Override
            public List<? extends Selectable> selectableChildren() {
                return widgetList;
            }
        }
    }

    private static class PreviewPixel {
        final int x;
        final int y;
        final TrafficLightsPatternPreset.Slot slot;

        PreviewPixel(int x, int y, TrafficLightsPatternPreset.Slot slot) {
            this.x = x;
            this.y = y;
            this.slot = slot;
        }
    }

    private record TooltipEntry(String title, String value, String... descriptions) {
        static TooltipEntry of(String title, String... descriptions) {
            return new TooltipEntry(title, null, descriptions);
        }
    }

    private record TooltipLine(String text, int color) {}
}

