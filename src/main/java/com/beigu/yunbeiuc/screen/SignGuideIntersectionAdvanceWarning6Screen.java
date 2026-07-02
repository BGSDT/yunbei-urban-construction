package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning6Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning6UpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning6Screen extends Screen {
    private final BlockPos pos;

    private SignGuideIntersectionAdvanceWarning6Entity.Direction direction1;
    private SignGuideIntersectionAdvanceWarning6Entity.Direction direction2;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 270;

    public SignGuideIntersectionAdvanceWarning6Screen(BlockPos pos) {
        super(new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.direction1 = SignGuideIntersectionAdvanceWarning6Entity.Direction.STRAIGHT;
        this.direction2 = SignGuideIntersectionAdvanceWarning6Entity.Direction.STRAIGHT;
        String existingText1 = "";
        String existingText2 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionAdvanceWarning6Entity entity) {
                this.direction1 = entity.getDirection1();
                this.direction2 = entity.getDirection2();
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // Line 1: Direction 1 buttons - 修改按钮文本和枚举值
        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> {  // 改为 LEFT
                    direction1 = SignGuideIntersectionAdvanceWarning6Entity.Direction.LEFT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 58, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> {  // 改为 STRAIGHT
                    direction1 = SignGuideIntersectionAdvanceWarning6Entity.Direction.STRAIGHT;
                })
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 106, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> {  // 改为 RIGHT
                    direction1 = SignGuideIntersectionAdvanceWarning6Entity.Direction.RIGHT;
                })
        );

        // Line 2: Direction 2 buttons - 同样修改
        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.left"), button -> direction2 = SignGuideIntersectionAdvanceWarning6Entity.Direction.LEFT)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 58, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.straight"), button -> direction2 = SignGuideIntersectionAdvanceWarning6Entity.Direction.STRAIGHT)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 106, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.right"), button -> direction2 = SignGuideIntersectionAdvanceWarning6Entity.Direction.RIGHT)
        );

        // Line 3: text1 + text2
        this.text1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 105,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.content")
        );
        this.text1TextField.setMaxLength(256);
        this.text1TextField.setText(existingText1);
        this.addSelectableChild(this.text1TextField);

        this.text2TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 205, panelY + 105,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.content")
        );
        this.text2TextField.setMaxLength(256);
        this.text2TextField.setText(existingText2);
        this.addSelectableChild(this.text2TextField);

        // 保存和取消按钮
        int buttonY = panelY + 235;
        this.addDrawableChild(
                new ButtonWidget(panelX + 100, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.save"), button -> this.saveAndClose())

        );

        this.addDrawableChild(
                new ButtonWidget(panelX + 210, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.cancel"), button -> this.close())

        );
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField.getText();
            String text2 = this.text2TextField.getText();

            SignGuideIntersectionAdvanceWarning6UpdatePacket packet =
                    new SignGuideIntersectionAdvanceWarning6UpdatePacket(pos, direction1, direction2, text1, text2);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6, buf);
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
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.1_name"),
                panelX + 10, panelY + 31,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.2_name"),
                panelX + 10, panelY + 56,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.text_1_name"),
                panelX + 10, panelY + 96,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_6.text_2_name"),
                panelX + 205, panelY + 96,
                0xFFAAAAAA
        );

        // 状态显示
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction1.getName()),
                panelX + 10, panelY + 150,
                0xFFFFFF00
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction2.getName()),
                panelX + 170, panelY + 150,
                0xFFFFFF00
        );

        this.text1TextField.render(context, mouseX, mouseY, delta);
        this.text2TextField.render(context, mouseX, mouseY, delta);

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