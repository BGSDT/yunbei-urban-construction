package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideLaneIndicator1Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideLaneIndicator1UpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SignGuideLaneIndicator1Screen extends Screen {
    private final BlockPos pos;
    
    private SignGuideLaneIndicator1Entity.Direction direction1;
    private SignGuideLaneIndicator1Entity.Direction direction2;
    private SignGuideLaneIndicator1Entity.Direction direction3;
    private SignGuideLaneIndicator1Entity.Direction direction4;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 245;

    public SignGuideLaneIndicator1Screen(BlockPos pos) {
        super(new TranslatableText("text.yunbeiuc.sign_guide_lane_indicator_1.title"));
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

        // Direction 1 buttons
        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 40, 80, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> {
                    direction1 = SignGuideLaneIndicator1Entity.Direction.LEFT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 95, panelY + 40, 80, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> {
                    direction1 = SignGuideLaneIndicator1Entity.Direction.STRAIGHT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 180, panelY + 40, 80, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> {
                    direction1 = SignGuideLaneIndicator1Entity.Direction.RIGHT;
                })
        );

        // Direction 2 buttons
        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 85, 80, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> {
                    direction2 = SignGuideLaneIndicator1Entity.Direction.LEFT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 95, panelY + 85, 80, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> {
                    direction2 = SignGuideLaneIndicator1Entity.Direction.STRAIGHT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 180, panelY + 85, 80, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> {
                    direction2 = SignGuideLaneIndicator1Entity.Direction.RIGHT;
                })
        );

        // Direction 3 buttons
        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 130, 80, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> {
                    direction3 = SignGuideLaneIndicator1Entity.Direction.LEFT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 95, panelY + 130, 80, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> {
                    direction3 = SignGuideLaneIndicator1Entity.Direction.STRAIGHT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 180, panelY + 130, 80, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> {
                    direction3 = SignGuideLaneIndicator1Entity.Direction.RIGHT;
                })
        );

        // Direction 4 buttons
        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 175, 80, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> {
                    direction4 = SignGuideLaneIndicator1Entity.Direction.LEFT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 95, panelY + 175, 80, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> {
                    direction4 = SignGuideLaneIndicator1Entity.Direction.STRAIGHT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 180, panelY + 175, 80, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> {
                    direction4 = SignGuideLaneIndicator1Entity.Direction.RIGHT;
                })
        );

        int buttonY = panelY + 215;
        this.addDrawableChild(
                new ButtonWidget(panelX + 60, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_lane_indicator_1.save"), button -> this.saveAndClose())
        );

        this.addDrawableChild(
                new ButtonWidget(panelX + 170, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_lane_indicator_1.cancel"), button -> this.close())
        );
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            SignGuideLaneIndicator1UpdatePacket packet =
                    new SignGuideLaneIndicator1UpdatePacket(pos, direction1, direction2, direction3, direction4);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_SIGN_GUIDE_LANE_INDICATOR_1, buf);
        }
        this.close();
    }

    @Override
    public void render(MatrixStack context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        DrawableHelper.fill(context, panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);
        DrawableHelper.fill(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        DrawableHelper.drawCenteredText(
                context, 
                this.textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_lane_indicator_1.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_lane_indicator_1.direction_1"),
                panelX + 10, panelY + 31,
                0xFFAAAAAA
        );
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_lane_indicator_1.direction_2"),
                panelX + 10, panelY + 76,
                0xFFAAAAAA
        );
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_lane_indicator_1.direction_3"),
                panelX + 10, panelY + 121,
                0xFFAAAAAA
        );
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_lane_indicator_1.direction_4"),
                panelX + 10, panelY + 166,
                0xFFAAAAAA
        );

        // 显示当前选中的方向
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction1.getName()),
                panelX + 270, panelY + 46,
                0xFFFFFF00
        );
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction2.getName()),
                panelX + 270, panelY + 91,
                0xFFFFFF00
        );
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction3.getName()),
                panelX + 270, panelY + 136,
                0xFFFFFF00
        );
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction4.getName()),
                panelX + 270, panelY + 181,
                0xFFFFFF00
        );

        super.render(context, mouseX, mouseY, delta);
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
}