package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.ApiCompat;
import com.beigu.yunbeiuc.api.network.NetworkCompat;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsWaitingAreaUpdatePacket;
import com.beigu.yunbeiuc.util.WaitingAreaTurnLeftAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

// 左转待行区专用：按相位单独设置图案（全红 / 可进入不可左转 / 全绿 / 自动）。
public class TrafficLightsWaitingAreaScreen extends Screen {
    private static final int PANEL_WIDTH = 300;
    private static final int ROW_HEIGHT = 26;
    private static final int MAX_PHASES = 16;

    private final BlockPos pos;
    private final int phaseCount;
    private final int[] patterns;
    private final List<ButtonWidget> patternButtons = new ArrayList<>();

    private int panelX;
    private int panelY;
    private Component errorMessage = null;

    public TrafficLightsWaitingAreaScreen(BlockPos pos) {
        super(Text.literal("左转待行区相位图案"));
        this.pos = pos;

        TrafficLightsBlockEntity entity = null;
        if (Minecraft.getInstance().level != null
                && Minecraft.getInstance().level.getBlockEntity(pos) instanceof TrafficLightsBlockEntity be) {
            entity = be;
        }

        int count = entity != null ? entity.getPhaseCount() : 0;
        if (count <= 0) {
            count = 3;
        }
        this.phaseCount = Math.min(count, MAX_PHASES);

        this.patterns = new int[this.phaseCount];
        int[] existing = entity != null ? entity.getWaitingAreaPatterns() : new int[0];
        for (int i = 0; i < this.phaseCount; i++) {
            this.patterns[i] = i < existing.length ? existing[i] : WaitingAreaTurnLeftAnimation.PATTERN_AUTO;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.panelX = (this.width - PANEL_WIDTH) / 2;
        int panelHeight = getPanelHeight();
        this.panelY = Math.max(20, (this.height - panelHeight) / 2);

        patternButtons.clear();
        int firstRowY = panelY + 44;
        for (int i = 0; i < phaseCount; i++) {
            final int index = i;
            ButtonWidget button = this.addDrawableChild(
                    ButtonWidget.builderCompat(Text.literal(WaitingAreaTurnLeftAnimation.patternLabel(patterns[i])), b -> {
                                patterns[index] = WaitingAreaTurnLeftAnimation.nextPattern(patterns[index]);
                                b.setMessage(Text.literal(WaitingAreaTurnLeftAnimation.patternLabel(patterns[index])));
                            })
                            .dimensions(panelX + 90, firstRowY + i * ROW_HEIGHT, 190, 20)
                            .build()
            );
            patternButtons.add(button);
        }

        int buttonY = panelY + 44 + phaseCount * ROW_HEIGHT + 18;
        this.addDrawableChild(
                ButtonWidget.builderCompat(Text.literal("保存"), b -> saveAndClose())
                        .dimensions(panelX + 40, buttonY, 90, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builderCompat(Text.literal("取消"), b -> this.close())
                        .dimensions(panelX + 170, buttonY, 90, 20)
                        .build()
        );
    }

    private int getPanelHeight() {
        return 44 + phaseCount * ROW_HEIGHT + 18 + 20 + 30;
    }

    private void saveAndClose() {
        int[] toSave = new int[phaseCount];
        System.arraycopy(patterns, 0, toSave, 0, phaseCount);

        TrafficLightsWaitingAreaUpdatePacket packet = new TrafficLightsWaitingAreaUpdatePacket(pos, toSave);
        FriendlyByteBuf buf = NetworkCompat.newBuffer();
        packet.write(buf);
        NetworkCompat.sendToServer(ModMessages.UPDATE_TRAFFIC_LIGHTS_WAITING_AREA, buf);

        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + getPanelHeight(), 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, getPanelHeight(), 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, panelX + PANEL_WIDTH / 2, panelY + 12, 0xFFCCCCCC);

        int firstRowY = panelY + 44;
        for (int i = 0; i < phaseCount; i++) {
            context.drawTextWithShadow(this.textRenderer, Text.literal("相位 " + (i + 1)),
                    panelX + 24, firstRowY + i * ROW_HEIGHT + 6, 0xFFAAAAAA);
        }

        if (errorMessage != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, errorMessage,
                    panelX + PANEL_WIDTH / 2, panelY + getPanelHeight() - 16, 0xFFFF5555);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPauseCompat() {
        return false;
    }
}
