package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.text.Text;

import com.beigu.yunbeiuc.api.gui.GuiPlatform;
import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsTimingUpdatePacket;
import com.beigu.yunbeiuc.network.TrafficLightsMountTypeUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class TrafficLightsTimingScreen extends Screen {
    private static final GuiPlatform GUI = VersionServices.gui();
    private static final RenderPlatform RENDER = VersionServices.render();

    private final String groupId;
    private final List<BlockPos> positions;

    private int phaseCount = 4;
    private final List<TextFieldWidget> timingFields = new ArrayList<>();
    private final List<String> savedValues = new ArrayList<>();

    private ButtonWidget saveButton;
    private ButtonWidget mountTypeButton;
    private TrafficLightsBlock.MountType pendingMountType;
    private Component errorMessage = null;

    private static final int PANEL_WIDTH = 260;
    private static final int COLUMNS = 4;
    private static final int FIELD_WIDTH = 50;
    private static final int FIELD_HEIGHT = 20;
    private static final int FIELD_GAP_X = 10;
    private static final int FIELD_GAP_Y = 28;
    private static final int FIELDS_START_Y = 62;
    private static final int MIN_PHASE_COUNT = 2;
    private static final int MAX_PHASE_COUNT = 16;

    public TrafficLightsTimingScreen(String groupId, List<BlockPos> positions) {
        super(Text.translatable("text.yunbeiuc.traffic_lights_timing.title"));
        this.groupId = groupId;
        this.positions = positions;
        for (int i = 0; i < MAX_PHASE_COUNT; i++) {
            savedValues.add("40");
        }
    }

    @Override
    protected void init() {
        super.init();
        rebuildLayout();
    }

    private int panelX() {
        return (this.width - PANEL_WIDTH) / 2;
    }

    private int panelY() {
        return (this.height - panelHeight()) / 2;
    }

    private int rows() {
        return (phaseCount + COLUMNS - 1) / COLUMNS;
    }

    private int panelHeight() {
        return FIELDS_START_Y + rows() * FIELD_GAP_Y + 82;
    }

    private void rebuildLayout() {
        for (TextFieldWidget field : timingFields) {
            this.remove(field);
        }
        timingFields.clear();
        this.clearChildren();

        int panelX = panelX();
        int panelY = panelY();

        this.addDrawableChild(
                GUI.createButton(Text.literal("-"), panelX + PANEL_WIDTH / 2 - 45,
                        panelY + 30, 20, 20, button -> changePhaseCount(-1))
        );
        this.addDrawableChild(
                GUI.createButton(Text.literal("+"), panelX + PANEL_WIDTH / 2 + 25,
                        panelY + 30, 20, 20, button -> changePhaseCount(1))
        );

        for (int i = 0; i < phaseCount; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = panelX + 15 + col * (FIELD_WIDTH + FIELD_GAP_X);
            int y = panelY + FIELDS_START_Y + row * FIELD_GAP_Y + 12;

            TextFieldWidget field = GUI.createTextField(
                    this.textRenderer, x, y, FIELD_WIDTH, FIELD_HEIGHT,
                    Text.literal("相位" + (i + 1))
            );
            GUI.setMaxLength(field, 4);
            GUI.setText(field, savedValues.get(i));
            final int index = i;
            GUI.setChangedListener(field, text -> savedValues.set(index, text));
            this.addDrawableChild(field);
            timingFields.add(field);
        }

        // 移除创建相位预设按钮
        // int patternButtonY = panelY + panelHeight() - 60;
        // this.addDrawableChild(
        //         ButtonWidget.builderCompat(Text.literal("创建相位预设"), button -> openPatternEditor())
        //                 .dimensions(panelX + PANEL_WIDTH / 2 - 80, patternButtonY, 160, 20)
        //                 .build()
        // );

        int buttonY = panelY + panelHeight() - 35;
        this.saveButton = this.addDrawableChild(
                GUI.createButton(Text.translatable("text.yunbeiuc.traffic_lights_timing.save"),
                        panelX + 40, buttonY, 80, 20, button -> saveAndClose())
        );
        this.addDrawableChild(
                GUI.createButton(Text.translatable("text.yunbeiuc.traffic_lights_timing.cancel"),
                        panelX + PANEL_WIDTH - 120, buttonY, 80, 20, button -> this.close())
        );

        if (mountTypeButton != null) {
            this.remove(mountTypeButton);
        }
        TrafficLightsBlock.MountType currentMountType = getCurrentMountType();
        if (pendingMountType == null) {
            pendingMountType = currentMountType;
        }
        int mountTypeY = panelY + panelHeight() - 60;
        mountTypeButton = this.addDrawableChild(
                GUI.createButton(
                        Text.literal(pendingMountType == TrafficLightsBlock.MountType.POLE ? "路杆模式" : "墙面模式"),
                        panelX + PANEL_WIDTH / 2 - 70, mountTypeY, 140, 20, button -> toggleMountType())
        );

        this.setFocused(timingFields.isEmpty() ? null : timingFields.get(0));
    }

    private void changePhaseCount(int delta) {
        for (int i = 0; i < timingFields.size(); i++) {
            savedValues.set(i, GUI.getText(timingFields.get(i)));
        }
        int newCount = phaseCount + delta;
        if (newCount < MIN_PHASE_COUNT || newCount > MAX_PHASE_COUNT) return;
        phaseCount = newCount;
        errorMessage = null;
        rebuildLayout();
    }

    private void saveAndClose() {
        int[] timings = new int[phaseCount];
        for (int i = 0; i < phaseCount; i++) {
            String text = GUI.getText(timingFields.get(i));
            try {
                timings[i] = Integer.parseInt(text.trim());
            } catch (NumberFormatException e) {
                errorMessage = Text.literal("§c格式无效！请输入数字。(相位" + (i + 1) + ")");
                return;
            }
            if (timings[i] < 7) {
                errorMessage = Text.literal("§c时间至少需要7秒！(相位" + (i + 1) + ")");
                return;
            }
            if (timings[i] > 300) {
                errorMessage = Text.literal("§c时间不能超过300秒！(相位" + (i + 1) + ")");
                return;
            }
        }

        TrafficLightsTimingUpdatePacket packet = new TrafficLightsTimingUpdatePacket(groupId, positions, timings);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(buf);
        NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_TIMING, buf);

        // 保存时才应用mountType的更改
        if (pendingMountType != null) {
            TrafficLightsBlock.MountType currentType = getCurrentMountType();
            if (pendingMountType != currentType) {
                for (BlockPos pos : positions) {
                    TrafficLightsMountTypeUpdatePacket mountPacket = new TrafficLightsMountTypeUpdatePacket(pos, pendingMountType);
                    FriendlyByteBuf mountBuf = new FriendlyByteBuf(Unpooled.buffer());
                    mountPacket.write(mountBuf);
                    NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_MOUNT_TYPE, mountBuf);
                }
            }
        }

        this.close();
    }

    private void openPatternEditor() {
        Minecraft.getInstance().setScreen(new TrafficLightsPatternEditorScreen());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = panelX();
        int panelY = panelY();
        int panelHeight = panelHeight();

        RENDER.fill(context, panelX, panelY, panelX + PANEL_WIDTH, panelY + panelHeight, 0xAA333333);
        RENDER.drawBorder(context, panelX, panelY, PANEL_WIDTH, panelHeight, 0xFFCCCCCC);

        RENDER.drawCenteredText(
                context,
                this.textRenderer,
                this.title,
                panelX + PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        RENDER.drawCenteredText(
                context,
                this.textRenderer,
                Text.translatable("text.yunbeiuc.traffic_lights_timing.phase_count", phaseCount),
                panelX + PANEL_WIDTH / 2,
                panelY + 36,
                0xFFFFFF00
        );

        for (int i = 0; i < phaseCount; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = panelX + 15 + col * (FIELD_WIDTH + FIELD_GAP_X);
            int y = panelY + FIELDS_START_Y + row * FIELD_GAP_Y;
            RENDER.drawText(context, this.textRenderer, Text.literal("相位" + (i + 1)),
                    x, y, 0xFFAAAAAA, true);
        }

        if (errorMessage != null) {
            RENDER.drawCenteredText(
                    context,
                    this.textRenderer,
                    errorMessage,
                    panelX + PANEL_WIDTH / 2,
                    panelY + panelHeight - 50,
                    0xFFFF5555
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

    @Override
    public boolean shouldPause() {
        return false;
    }

    private TrafficLightsBlock.MountType getCurrentMountType() {
        if (positions.isEmpty()) return TrafficLightsBlock.MountType.SIMPLE;
        BlockPos pos = positions.get(0);
        if (Minecraft.getInstance().level == null) return TrafficLightsBlock.MountType.SIMPLE;
        if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof TrafficLightsBlockEntity blockEntity) {
            if (blockEntity.getBlockState().hasProperty(TrafficLightsBlock.TYPE)) {
                return blockEntity.getBlockState().getValue(TrafficLightsBlock.TYPE);
            }
        }
        return TrafficLightsBlock.MountType.SIMPLE;
    }

    private void toggleMountType() {
        if (positions.isEmpty()) return;

        pendingMountType = pendingMountType == TrafficLightsBlock.MountType.SIMPLE ?
                TrafficLightsBlock.MountType.POLE : TrafficLightsBlock.MountType.SIMPLE;

        if (mountTypeButton != null) {
            GUI.setButtonMessage(mountTypeButton,
                    Text.literal(pendingMountType == TrafficLightsBlock.MountType.POLE ? "路杆模式" : "墙面模式"));
        }
    }
}
