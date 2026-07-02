package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket;
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

public class SignGuideIntersectionAdvanceWarning1WuhanScreen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget cnText3TextField;
    private TextFieldWidget enText3TextField;
    private TextFieldWidget cnText4TextField;
    private TextFieldWidget enText4TextField;
    private TextFieldWidget cnText5TextField;
    private TextFieldWidget enText5TextField;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 245;

    public SignGuideIntersectionAdvanceWarning1WuhanScreen(BlockPos pos) {
        super(new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";
        String existingText2 = "";
        String existingCnText3 = "";
        String existingEnText3 = "";
        String existingCnText4 = "";
        String existingEnText4 = "";
        String existingCnText5 = "";
        String existingEnText5 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionAdvanceWarning1WuhanEntity entity) {
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingCnText3 = entity.getCnText3();
                existingEnText3 = entity.getEnText3();
                existingCnText4 = entity.getCnText4();
                existingEnText4 = entity.getEnText4();
                existingCnText5 = entity.getCnText5();
                existingEnText5 = entity.getEnText5();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.text1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 40,
                150, 24,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.content")
        );
        this.text1TextField.setMaxLength(256);
        this.text1TextField.setText(existingText1);
        this.addSelectableChild(this.text1TextField);

        this.text2TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 170, panelY + 40,
                150, 24,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.content")
        );
        this.text2TextField.setMaxLength(256);
        this.text2TextField.setText(existingText2);
        this.addSelectableChild(this.text2TextField);

        this.cnText3TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 85,
                150, 24,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.content")
        );
        this.cnText3TextField.setMaxLength(256);
        this.cnText3TextField.setText(existingCnText3);
        this.addSelectableChild(this.cnText3TextField);

        this.enText3TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 170, panelY + 85,
                150, 24,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.content")
        );
        this.enText3TextField.setMaxLength(256);
        this.enText3TextField.setText(existingEnText3);
        this.addSelectableChild(this.enText3TextField);

        this.cnText4TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 130,
                150, 24,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.content")
        );
        this.cnText4TextField.setMaxLength(256);
        this.cnText4TextField.setText(existingCnText4);
        this.addSelectableChild(this.cnText4TextField);

        this.enText4TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 170, panelY + 130,
                150, 24,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.content")
        );
        this.enText4TextField.setMaxLength(256);
        this.enText4TextField.setText(existingEnText4);
        this.addSelectableChild(this.enText4TextField);

        this.cnText5TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 175,
                150, 24,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.content")
        );
        this.cnText5TextField.setMaxLength(256);
        this.cnText5TextField.setText(existingCnText5);
        this.addSelectableChild(this.cnText5TextField);

        this.enText5TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 170, panelY + 175,
                150, 24,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.content")
        );
        this.enText5TextField.setMaxLength(256);
        this.enText5TextField.setText(existingEnText5);
        this.addSelectableChild(this.enText5TextField);

        int buttonY = panelY + 215;
        this.addDrawableChild(
                new ButtonWidget(panelX + 60, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.save"), button -> this.saveAndClose())

        );

        this.addDrawableChild(
                new ButtonWidget(panelX + 170, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.cancel"), button -> this.close())

        );

        this.setFocused(this.text1TextField);
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField.getText();
            String text2 = this.text2TextField.getText();
            String cnText3 = this.cnText3TextField.getText();
            String enText3 = this.enText3TextField.getText();
            String cnText4 = this.cnText4TextField.getText();
            String enText4 = this.enText4TextField.getText();
            String cnText5 = this.cnText5TextField.getText();
            String enText5 = this.enText5TextField.getText();

            SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket packet = new SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket(pos, text1, text2, cnText3, enText3, cnText4, enText4, cnText5, enText5);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN, buf);
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
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.text_1_name"),
                panelX + 10, panelY + 31,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.text_2_name"),
                panelX + 170, panelY + 31,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.cn_text_3_name"),
                panelX + 10, panelY + 76,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.en_text_3_name"),
                panelX + 170, panelY + 76,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.cn_text_4_name"),
                panelX + 10, panelY + 121,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.en_text_4_name"),
                panelX + 170, panelY + 121,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.cn_text_5_name"),
                panelX + 10, panelY + 166,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.en_text_5_name"),
                panelX + 170, panelY + 166,
                0xFFAAAAAA
        );

        this.text1TextField.render(context, mouseX, mouseY, delta);
        this.text2TextField.render(context, mouseX, mouseY, delta);
        this.cnText3TextField.render(context, mouseX, mouseY, delta);
        this.enText3TextField.render(context, mouseX, mouseY, delta);
        this.cnText4TextField.render(context, mouseX, mouseY, delta);
        this.enText4TextField.render(context, mouseX, mouseY, delta);
        this.cnText5TextField.render(context, mouseX, mouseY, delta);
        this.enText5TextField.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC键
            this.close();
            return true;
        } else if (keyCode == 257 || keyCode == 335) { // 回车键或小键盘回车
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