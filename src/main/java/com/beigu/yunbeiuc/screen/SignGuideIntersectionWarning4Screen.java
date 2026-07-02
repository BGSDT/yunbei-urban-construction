package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionWarning4UpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionWarning4Screen extends Screen {
    private final BlockPos pos;

    private SignGuideIntersectionWarning4Entity.Direction direction1;
    private TextFieldWidget text1TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 270;

    public SignGuideIntersectionWarning4Screen(BlockPos pos) {
        super(new TranslatableText("text.yunbeiuc.sign_guide_intersection_warning_4.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.direction1 = SignGuideIntersectionWarning4Entity.Direction.STRAIGHT;
        String existingText1 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionWarning4Entity entity) {
                this.direction1 = entity.getDirection1();
                existingText1 = entity.getText1();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> {  // 改为 LEFT
                    direction1 = SignGuideIntersectionWarning4Entity.Direction.LEFT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 58, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> {  // 改为 STRAIGHT
                    direction1 = SignGuideIntersectionWarning4Entity.Direction.STRAIGHT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 106, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> {  // 改为 RIGHT
                    direction1 = SignGuideIntersectionWarning4Entity.Direction.RIGHT;
                })
        );

        this.text1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 80,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_warning_4.content")
        );
        this.text1TextField.setMaxLength(256);
        this.text1TextField.setText(existingText1);
        this.addSelectableChild(this.text1TextField);

        int buttonY = panelY + 235;
        this.addDrawableChild(
                new ButtonWidget(panelX + 100, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_intersection_warning_4.save"), button -> this.saveAndClose())
        );

        this.addDrawableChild(
                new ButtonWidget(panelX + 210, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_intersection_warning_4.cancel"), button -> this.close())
        );
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField.getText();

            SignGuideIntersectionWarning4UpdatePacket packet =
                    new SignGuideIntersectionWarning4UpdatePacket(pos, direction1, text1);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_4, buf);
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
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_warning_4.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );textRenderer.drawWithShadow(context, 
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_warning_4.1_name"),
                panelX + 10, panelY + 31,
                0xFFAAAAAA
        );textRenderer.drawWithShadow(context, 
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_warning_4.text_1_name"),
                panelX + 10, panelY + 71,
                0xFFAAAAAA
        );

        // 状态显示
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction1.getName()),
                panelX + 10, panelY + 125,
                0xFFFFFF00
        );

        this.text1TextField.render(context, mouseX, mouseY, delta);

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