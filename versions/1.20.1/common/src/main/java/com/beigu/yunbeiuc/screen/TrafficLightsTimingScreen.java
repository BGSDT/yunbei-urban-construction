package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsTimingUpdatePacket;
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

public class TrafficLightsTimingScreen extends Screen {
    private final String groupId;
    private final List<BlockPos> positions;

    private int phaseCount = 4;
    private final List<TextFieldWidget> timingFields = new ArrayList<>();
    private final List<String> savedValues = new ArrayList<>();

    private ButtonWidget saveButton;
    private Text errorMessage = null;

    private static final int PANEL_WIDTH = 260;
    private static final int COLUMNS = 4;
    private static final int FIELD_WIDTH = 50;
    private static final int FIELD_HEIGHT = 20;
    private static final int FIELD_GAP_X = 10;
    private static final int FIELD_GAP_Y = 28;
    private static final int FIELDS_START_Y = 70;
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
        return FIELDS_START_Y + rows() * FIELD_GAP_Y + 85;
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
                ButtonWidget.builder(Text.literal("-"), button -> changePhaseCount(-1))
                        .dimensions(panelX + PANEL_WIDTH / 2 - 45, panelY + 30, 20, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("+"), button -> changePhaseCount(1))
                        .dimensions(panelX + PANEL_WIDTH / 2 + 25, panelY + 30, 20, 20)
                        .build()
        );

        for (int i = 0; i < phaseCount; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = panelX + 15 + col * (FIELD_WIDTH + FIELD_GAP_X);
            int y = panelY + FIELDS_START_Y + row * FIELD_GAP_Y + 12;

            TextFieldWidget field = new TextFieldWidget(
                    this.textRenderer, x, y, FIELD_WIDTH, FIELD_HEIGHT,
                    Text.literal("相位" + (i + 1))
            );
            field.setMaxLength(4);
            field.setText(savedValues.get(i));
            final int index = i;
            field.setChangedListener(text -> savedValues.set(index, text));
            this.addDrawableChild(field);
            timingFields.add(field);
        }

        // 移除创建相位预设按钮
        // int patternButtonY = panelY + panelHeight() - 60;
        // this.addDrawableChild(
        //         ButtonWidget.builder(Text.literal("创建相位预设"), button -> openPatternEditor())
        //                 .dimensions(panelX + PANEL_WIDTH / 2 - 80, patternButtonY, 160, 20)
        //                 .build()
        // );

        int buttonY = panelY + panelHeight() - 35;
        this.saveButton = this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_timing.save"), button -> saveAndClose())
                        .dimensions(panelX + 40, buttonY, 80, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.traffic_lights_timing.cancel"), button -> this.close())
                        .dimensions(panelX + PANEL_WIDTH - 120, buttonY, 80, 20)
                        .build()
        );

        this.setFocused(timingFields.isEmpty() ? null : timingFields.get(0));
    }

    private void changePhaseCount(int delta) {
        for (int i = 0; i < timingFields.size(); i++) {
            savedValues.set(i, timingFields.get(i).getText());
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
            String text = timingFields.get(i).getText();
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
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);
        NetworkManager.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_TIMING, buf);
        this.close();
    }

    private void openPatternEditor() {
        MinecraftClient.getInstance().setScreen(new TrafficLightsPatternEditorScreen());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = panelX();
        int panelY = panelY();
        int panelHeight = panelHeight();

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + panelHeight, 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, panelHeight, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                panelX + PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        context.drawCenteredTextWithShadow(
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
            context.drawTextWithShadow(this.textRenderer, Text.literal("相位" + (i + 1)), x, y, 0xFFAAAAAA);
        }

        if (errorMessage != null) {
            context.drawCenteredTextWithShadow(
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
}
