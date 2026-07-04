package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning3Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning3UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
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

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 245;
    private static final int INPUT_WIDTH = 70;
    private static final int NEW_INPUT_WIDTH = 150;
    private static final int INPUT_HEIGHT = 24;
    private int andInputY;

    private Block currentBlock;
    private boolean isWarning4;

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

        currentBlock = null;
        isWarning4 = false;

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
                isWarning4 = (currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4);
                andInputY = isWarning4 ? 0 : 40;
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.text1TextField = null;
        if (isWarning4) {
            this.text1TextField = createTextField(panelX + (PANEL_WIDTH - 310) / 2, panelY + 40, 310, existingText1);
        }

        if (isWarning4) {
            this.cnText2TextField = createTextField(panelX + 5, panelY + 85 - andInputY, INPUT_WIDTH, existingCnText2);
            this.enText2TextField = createTextField(panelX + 85, panelY + 85 - andInputY, INPUT_WIDTH, existingEnText2);
            this.cnText3TextField = createTextField(panelX + 165, panelY + 85 - andInputY, INPUT_WIDTH, existingCnText3);
            this.enText3TextField = createTextField(panelX + 245, panelY + 85 - andInputY, INPUT_WIDTH, existingEnText3);
        } else {
            this.cnText2TextField = createTextField(panelX + 5, panelY + 85 - andInputY, NEW_INPUT_WIDTH, existingCnText2);
            this.enText2TextField = createTextField(panelX + 165, panelY + 85 - andInputY, NEW_INPUT_WIDTH, existingEnText2);
        }

        this.cnText4TextField = createTextField(panelX + 5, panelY + 130 - andInputY, INPUT_WIDTH, existingCnText4);
        this.enText4TextField = createTextField(panelX + 85, panelY + 130 - andInputY, INPUT_WIDTH, existingEnText4);
        this.cnText5TextField = createTextField(panelX + 165, panelY + 130 - andInputY, INPUT_WIDTH, existingCnText5);
        this.enText5TextField = createTextField(panelX + 245, panelY + 130 - andInputY, INPUT_WIDTH, existingEnText5);

        this.cnText6TextField = createTextField(panelX + 5, panelY + 175 - andInputY, INPUT_WIDTH, existingCnText6);
        this.enText6TextField = createTextField(panelX + 85, panelY + 175 - andInputY, INPUT_WIDTH, existingEnText6);
        this.cnText7TextField = createTextField(panelX + 165, panelY + 175 - andInputY, INPUT_WIDTH, existingCnText7);
        this.enText7TextField = createTextField(panelX + 245, panelY + 175 - andInputY, INPUT_WIDTH, existingEnText7);

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

        if (this.cnText2TextField != null) {
            this.setFocused(this.cnText2TextField);
        }
    }

    private TextFieldWidget createTextField(int x, int y, int width, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                width, INPUT_HEIGHT,
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
            String text1 = getTextSafely(this.text1TextField);
            String cnText2 = getTextSafely(this.cnText2TextField);
            String enText2 = getTextSafely(this.enText2TextField);
            String cnText3 = getTextSafely(this.cnText3TextField);
            String enText3 = getTextSafely(this.enText3TextField);
            String cnText4 = getTextSafely(this.cnText4TextField);
            String enText4 = getTextSafely(this.enText4TextField);
            String cnText5 = getTextSafely(this.cnText5TextField);
            String enText5 = getTextSafely(this.enText5TextField);
            String cnText6 = getTextSafely(this.cnText6TextField);
            String enText6 = getTextSafely(this.enText6TextField);
            String cnText7 = getTextSafely(this.cnText7TextField);
            String enText7 = getTextSafely(this.enText7TextField);

            SignGuideIntersectionAdvanceWarning3UpdatePacket packet = new SignGuideIntersectionAdvanceWarning3UpdatePacket(pos,
                    text1, cnText2, enText2, cnText3, enText3, cnText4, enText4,
                    cnText5, enText5, cnText6, enText6, cnText7, enText7);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3, buf);
        }
        this.close();
    }

    private String getTextSafely(TextFieldWidget textField) {
        return textField != null ? textField.getText() : "";
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

        if (isWarning4) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.text_1_name"),
                    panelX + PANEL_WIDTH / 2, panelY + 31 - andInputY,
                    0xFFAAAAAA
            );

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
        } else {
            context.drawTextWithShadow(this.textRenderer,
                    Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.cn_text_2_name"),
                    panelX + 5, panelY + 76 - andInputY, 0xFFAAAAAA);
            context.drawTextWithShadow(this.textRenderer,
                    Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_3.en_text_2_name"),
                    panelX + 165, panelY + 76 - andInputY, 0xFFAAAAAA);
        }

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

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.cnText2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.enText2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.cnText3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.enText3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.cnText4TextField, context, mouseX, mouseY, delta);
        renderTextField(this.enText4TextField, context, mouseX, mouseY, delta);
        renderTextField(this.cnText5TextField, context, mouseX, mouseY, delta);
        renderTextField(this.enText5TextField, context, mouseX, mouseY, delta);
        renderTextField(this.cnText6TextField, context, mouseX, mouseY, delta);
        renderTextField(this.enText6TextField, context, mouseX, mouseY, delta);
        renderTextField(this.cnText7TextField, context, mouseX, mouseY, delta);
        renderTextField(this.enText7TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderTextField(TextFieldWidget textField, DrawContext context, int mouseX, int mouseY, float delta) {
        if (textField != null) {
            textField.render(context, mouseX, mouseY, delta);
        }
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