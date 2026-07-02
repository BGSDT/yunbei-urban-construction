package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning7Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning7UpdatePacket;
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

public class SignGuideIntersectionAdvanceWarning7Screen extends Screen {
    private final BlockPos pos;

    private SignGuideIntersectionAdvanceWarning7Entity.Direction direction1;
    private SignGuideIntersectionAdvanceWarning7Entity.Direction direction2;
    private SignGuideIntersectionAdvanceWarning7Entity.Direction direction3;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget text3TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 270;

    public SignGuideIntersectionAdvanceWarning7Screen(BlockPos pos) {
        super(new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.direction1 = SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT;
        this.direction2 = SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT;
        this.direction3 = SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT;
        String existingText1 = "";
        String existingText2 = "";
        String existingText3 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionAdvanceWarning7Entity entity) {
                this.direction1 = entity.getDirection1();
                this.direction2 = entity.getDirection2();
                this.direction3 = entity.getDirection3();
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingText3 = entity.getText3();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> {  // 改为 LEFT
                    direction1 = SignGuideIntersectionAdvanceWarning7Entity.Direction.LEFT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 58, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> {  // 改为 STRAIGHT
                    direction1 = SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 106, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> {  // 改为 RIGHT
                    direction1 = SignGuideIntersectionAdvanceWarning7Entity.Direction.RIGHT;
                })
        );

        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> {
                    direction2 = SignGuideIntersectionAdvanceWarning7Entity.Direction.LEFT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 58, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> {
                    direction2 = SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 106, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> {
                    direction2 = SignGuideIntersectionAdvanceWarning7Entity.Direction.RIGHT;
                })
        );

        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 90, 45, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> {
                    direction3 = SignGuideIntersectionAdvanceWarning7Entity.Direction.LEFT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 58, panelY + 90, 45, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> {
                    direction3 = SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 106, panelY + 90, 45, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> {
                    direction3 = SignGuideIntersectionAdvanceWarning7Entity.Direction.RIGHT;
                })
        );

        // Line 3: text1 + text2
        this.text1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 130,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.content")
        );
        this.text1TextField.setMaxLength(256);
        this.text1TextField.setText(existingText1);
        this.addSelectableChild(this.text1TextField);

        this.text2TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 205, panelY + 130,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.content")
        );
        this.text2TextField.setMaxLength(256);
        this.text2TextField.setText(existingText2);
        this.addSelectableChild(this.text2TextField);

        this.text3TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 175,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.content")
        );
        this.text3TextField.setMaxLength(256);
        this.text3TextField.setText(existingText3);
        this.addSelectableChild(this.text3TextField);

        // 保存和取消按钮
        int buttonY = panelY + 235;
        this.addDrawableChild(
                new ButtonWidget(panelX + 100, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.save"), button -> this.saveAndClose())
        );

        this.addDrawableChild(
                new ButtonWidget(panelX + 210, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.cancel"), button -> this.close())
        );
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField.getText();
            String text2 = this.text2TextField.getText();
            String text3 = this.text3TextField.getText();

            SignGuideIntersectionAdvanceWarning7UpdatePacket packet =
                    new SignGuideIntersectionAdvanceWarning7UpdatePacket(pos, direction1, direction2, direction3, text1, text2, text3);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7, buf);
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
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.1_name"),
                panelX + 10, panelY + 31,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.2_name"),
                panelX + 10, panelY + 56,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.3_name"),
                panelX + 10, panelY + 81,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.text_1_name"),
                panelX + 10, panelY + 121,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.text_2_name"),
                panelX + 205, panelY + 121,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_7.text_3_name"),
                panelX + 10, panelY + 166,
                0xFFAAAAAA
        );

        // 状态显示
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction1.getName()),
                panelX + 10, panelY + 210,
                0xFFFFFF00
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction2.getName()),
                panelX + 60, panelY + 210,
                0xFFFFFF00
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction3.getName()),
                panelX + 110, panelY + 210,
                0xFFFFFF00
        );

        this.text1TextField.render(context, mouseX, mouseY, delta);
        this.text2TextField.render(context, mouseX, mouseY, delta);
        this.text3TextField.render(context, mouseX, mouseY, delta);

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