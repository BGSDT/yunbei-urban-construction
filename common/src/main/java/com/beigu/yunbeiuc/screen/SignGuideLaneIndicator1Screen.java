package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideLaneIndicator1Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideLaneIndicator1UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideLaneIndicator1Screen extends Screen {
    private final BlockPos pos;

    private SignGuideLaneIndicator1Entity.Direction direction1;
    private SignGuideLaneIndicator1Entity.Direction direction2;
    private SignGuideLaneIndicator1Entity.Direction direction3;
    private SignGuideLaneIndicator1Entity.Direction direction4;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 280;
    private static final int DIRECTION_BUTTON_WIDTH = 60;
    private static final int DIRECTION_BUTTON_HEIGHT = 20;
    private static final int DIRECTION_COUNT = 4;

    public SignGuideLaneIndicator1Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_guide_lane_indicator_1.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.direction1 = SignGuideLaneIndicator1Entity.Direction.STRAIGHT;
        this.direction2 = SignGuideLaneIndicator1Entity.Direction.STRAIGHT;
        this.direction3 = SignGuideLaneIndicator1Entity.Direction.STRAIGHT;
        this.direction4 = SignGuideLaneIndicator1Entity.Direction.STRAIGHT;

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideLaneIndicator1Entity entity) {
                this.direction1 = entity.getDirection1();
                this.direction2 = entity.getDirection2();
                this.direction3 = entity.getDirection3();
                this.direction4 = entity.getDirection4();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // Direction buttons
        createDirectionButtons(panelX + 10, panelY + 40, direction -> this.direction1 = direction);
        createDirectionButtons(panelX + 10, panelY + 85, direction -> this.direction2 = direction);
        createDirectionButtons(panelX + 10, panelY + 130, direction -> this.direction3 = direction);
        createDirectionButtons(panelX + 10, panelY + 175, direction -> this.direction4 = direction);

        int buttonY = panelY + 245;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_lane_indicator_1.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_lane_indicator_1.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createDirectionButtons(int x, int y, DirectionConsumer directionConsumer) {
        // 第一行：LEFT_TURN, STRAIGHT, RIGHT_TURN
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.left_turn"), button -> {
                    directionConsumer.accept(SignGuideLaneIndicator1Entity.Direction.LEFT_TURN);
                }).dimensions(x, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.straight"), button -> {
                    directionConsumer.accept(SignGuideLaneIndicator1Entity.Direction.STRAIGHT);
                }).dimensions(x + 65, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.right_turn"), button -> {
                    directionConsumer.accept(SignGuideLaneIndicator1Entity.Direction.RIGHT_TURN);
                }).dimensions(x + 130, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );

        // 第二行：STRAIGHT_LEFT_TURN, STRAIGHT_RIGHT_TURN, LEFT_TURN_AROUND
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.straight_left_turn"), button -> {
                    directionConsumer.accept(SignGuideLaneIndicator1Entity.Direction.STRAIGHT_LEFT_TURN);
                }).dimensions(x, y + 25, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.straight_right_turn"), button -> {
                    directionConsumer.accept(SignGuideLaneIndicator1Entity.Direction.STRAIGHT_RIGHT_TURN);
                }).dimensions(x + 65, y + 25, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.left_turn_around"), button -> {
                    directionConsumer.accept(SignGuideLaneIndicator1Entity.Direction.LEFT_TURN_AROUND);
                }).dimensions(x + 130, y + 25, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            SignGuideLaneIndicator1UpdatePacket packet =
                    new SignGuideLaneIndicator1UpdatePacket(pos, direction1, direction2, direction3, direction4);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_GUIDE_LANE_INDICATOR_1, buf);
        }
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_lane_indicator_1.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        // 渲染标签
        for (int i = 1; i <= DIRECTION_COUNT; i++) {
            renderDirectionLabel(context, panelX, panelY, i);
        }

        // 显示当前选中的方向
        renderDirectionStatus(context, panelX, panelY, 1, direction1);
        renderDirectionStatus(context, panelX, panelY, 2, direction2);
        renderDirectionStatus(context, panelX, panelY, 3, direction3);
        renderDirectionStatus(context, panelX, panelY, 4, direction4);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderDirectionLabel(DrawContext context, int panelX, int panelY, int index) {
        int yOffset = 31 + (index - 1) * 45;
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_lane_indicator_1.direction_" + index),
                panelX + 10, panelY + yOffset,
                0xFFAAAAAA
        );
    }

    private void renderDirectionStatus(DrawContext context, int panelX, int panelY, int index,
                                       SignGuideLaneIndicator1Entity.Direction direction) {
        int yOffset = 46 + (index - 1) * 45;
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.direction." + direction.getName()),
                panelX + 230, panelY + yOffset,
                0xFFFFFF00
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.close();
            return true;
        } else if (keyCode == 257 || keyCode == 335) {
            this.saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @FunctionalInterface
    private interface DirectionConsumer {
        void accept(SignGuideLaneIndicator1Entity.Direction direction);
    }
}