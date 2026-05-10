package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning3Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning3UpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning3Screen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget text1TextField;
    private TextFieldWidget cnText2TextField;
    private TextFieldWidget enText2TextField;
    private TextFieldWidget cnText3TextField;
    private TextFieldWidget enText3TextField;
    private TextFieldWidget cnText4TextField;
    private TextFieldWidget enText4TextField;
    private TextFieldWidget cnText5TextField;
    private TextFieldWidget enText5TextField;
    private TextFieldWidget cnText6TextField;
    private TextFieldWidget enText6TextField;
    private TextFieldWidget cnText7TextField;
    private TextFieldWidget enText7TextField;

    private static final int PANEL_WIDTH = 400;  // 缩小面板宽度
    private static final int PANEL_HEIGHT = 245;
    private static final int INPUT_WIDTH = 70;   // 改为70
    private static final int INPUT_HEIGHT = 24;
    private int andInputY;

    public SignGuideIntersectionAdvanceWarning3Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";
        String existingCnText2 = "";
        String existingEnText2 = "";
        String existingCnText3 = "";
        String existingEnText3 = "";
        String existingCnText4 = "";
        String existingEnText4 = "";
        String existingCnText5 = "";
        String existingEnText5 = "";
        String existingCnText6 = "";
        String existingEnText6 = "";
        String existingCnText7 = "";
        String existingEnText7 = "";

        Block currentBlock = null;
        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionAdvanceWarning3Entity entity) {
                existingText1 = entity.getText1();
                existingCnText2 = entity.getCnText2();
                existingEnText2 = entity.getEnText2();
                existingCnText3 = entity.getCnText3();
                existingEnText3 = entity.getEnText3();
                existingCnText4 = entity.getCnText4();
                existingEnText4 = entity.getEnText4();
                existingCnText5 = entity.getCnText5();
                existingEnText5 = entity.getEnText5();
                existingCnText6 = entity.getCnText6();
                existingEnText6 = entity.getEnText6();
                existingCnText7 = entity.getCnText7();
                existingEnText7 = entity.getEnText7();

                currentBlock = entity.getCachedState().getBlock();
                if (currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3) {
                    andInputY = 40;
                }
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;


        // 第一行：text1 居中（保持280宽度，但调整居中位置）
        if (currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4) {
            this.text1TextField = new TextFieldWidget(
                    this.textRenderer,
                    panelX + (PANEL_WIDTH - 280) / 2, panelY + 40,
                    280, INPUT_HEIGHT,
                    Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.content")
            );
            this.text1TextField.setMaxLength(256);
            this.text1TextField.setText(existingText1);
            this.text1TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.placeholder"));
            this.addSelectableChild(this.text1TextField);
        }

        // 第二行：cnText2 | enText2 | cnText3 | enText3（调整间距）
        this.cnText2TextField = createTextField(panelX + 5, panelY + 85 - andInputY, existingCnText2);
        this.enText2TextField = createTextField(panelX + 85, panelY + 85 - andInputY, existingEnText2);
        this.cnText3TextField = createTextField(panelX + 165, panelY + 85 - andInputY, existingCnText3);
        this.enText3TextField = createTextField(panelX + 245, panelY + 85 - andInputY, existingEnText3);

        // 第三行：cnText4 | enText4 | cnText5 | enText5（调整间距）
        this.cnText4TextField = createTextField(panelX + 5, panelY + 130 - andInputY, existingCnText4);
        this.enText4TextField = createTextField(panelX + 85, panelY + 130 - andInputY, existingEnText4);
        this.cnText5TextField = createTextField(panelX + 165, panelY + 130 - andInputY, existingCnText5);
        this.enText5TextField = createTextField(panelX + 245, panelY + 130 - andInputY, existingEnText5);

        // 第四行：cnText6 | enText6 | cnText7 | enText7（调整间距）
        this.cnText6TextField = createTextField(panelX + 5, panelY + 175 - andInputY, existingCnText6);
        this.enText6TextField = createTextField(panelX + 85, panelY + 175 - andInputY, existingEnText6);
        this.cnText7TextField = createTextField(panelX + 165, panelY + 175 - andInputY, existingCnText7);
        this.enText7TextField = createTextField(panelX + 245, panelY + 175 - andInputY, existingEnText7);

        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );

        this.setFocused(this.text1TextField);
    }

    private TextFieldWidget createTextField(int x, int y, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField != null ? this.text1TextField.getText() : "";
            String cnText2 = this.cnText2TextField.getText();
            String enText2 = this.enText2TextField.getText();
            String cnText3 = this.cnText3TextField.getText();
            String enText3 = this.enText3TextField.getText();
            String cnText4 = this.cnText4TextField.getText();
            String enText4 = this.enText4TextField.getText();
            String cnText5 = this.cnText5TextField.getText();
            String enText5 = this.enText5TextField.getText();
            String cnText6 = this.cnText6TextField.getText();
            String enText6 = this.enText6TextField.getText();
            String cnText7 = this.cnText7TextField.getText();
            String enText7 = this.enText7TextField.getText();

            SignGuideIntersectionAdvanceWarning3UpdatePacket packet = new SignGuideIntersectionAdvanceWarning3UpdatePacket(pos,
                    text1, cnText2, enText2, cnText3, enText3, cnText4, enText4,
                    cnText5, enText5, cnText6, enText6, cnText7, enText7);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3, buf);
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
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        // 第一行标签：text1 居中
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.text_1_name"),
                panelX + PANEL_WIDTH / 2, panelY + 31 - andInputY,
                0xFFAAAAAA
        );

        // 第二行标签（调整位置以匹配新的输入框位置）
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.cn_text_2_name"),
                panelX + 5, panelY + 76 - andInputY, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.en_text_2_name"),
                panelX + 85, panelY + 76 - andInputY, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.cn_text_3_name"),
                panelX + 165, panelY + 76 - andInputY, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.en_text_3_name"),
                panelX + 245, panelY + 76 - andInputY, 0xFFAAAAAA);

        // 第三行标签（调整位置）
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.cn_text_4_name"),
                panelX + 5, panelY + 121 - andInputY, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.en_text_4_name"),
                panelX + 85, panelY + 121 - andInputY, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.cn_text_5_name"),
                panelX + 165, panelY + 121 - andInputY, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.en_text_5_name"),
                panelX + 245, panelY + 121 - andInputY, 0xFFAAAAAA);

        // 第四行标签（调整位置）
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.cn_text_6_name"),
                panelX + 5, panelY + 166 - andInputY, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.en_text_6_name"),
                panelX + 85, panelY + 166 - andInputY, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.cn_text_7_name"),
                panelX + 165, panelY + 166 - andInputY, 0xFFAAAAAA);
        context.drawTextWithShadow(this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.en_text_7_name"),
                panelX + 245, panelY + 166 - andInputY, 0xFFAAAAAA);

        // 渲染所有文本框
        if (this.text1TextField != null) {
            this.text1TextField.render(context, mouseX, mouseY, delta);
        }
        this.cnText2TextField.render(context, mouseX, mouseY, delta);
        this.enText2TextField.render(context, mouseX, mouseY, delta);
        this.cnText3TextField.render(context, mouseX, mouseY, delta);
        this.enText3TextField.render(context, mouseX, mouseY, delta);
        this.cnText4TextField.render(context, mouseX, mouseY, delta);
        this.enText4TextField.render(context, mouseX, mouseY, delta);
        this.cnText5TextField.render(context, mouseX, mouseY, delta);
        this.enText5TextField.render(context, mouseX, mouseY, delta);
        this.cnText6TextField.render(context, mouseX, mouseY, delta);
        this.enText6TextField.render(context, mouseX, mouseY, delta);
        this.cnText7TextField.render(context, mouseX, mouseY, delta);
        this.enText7TextField.render(context, mouseX, mouseY, delta);

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