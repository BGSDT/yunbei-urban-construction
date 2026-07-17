package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation1Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayDistanceFromLocation1UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayDistanceFromLocation1Screen extends Screen {
    private final BlockPos pos;

    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget text3TextField;
    private TextFieldWidget length1TextField;
    private TextFieldWidget length2TextField;
    private TextFieldWidget length3TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 245;
    private static final int TEXT_INPUT_WIDTH = 185;
    private static final int LENGTH_INPUT_WIDTH = 185;
    private static final int INPUT_HEIGHT = 20;


    public SignExpresswayDistanceFromLocation1Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_1.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";
        String existingText2 = "";
        String existingText3 = "";
        String existingLength1 = "";
        String existingLength2 = "";
        String existingLength3 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignExpresswayDistanceFromLocation1Entity entity) {
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingText3 = entity.getText3();
                existingLength1 = entity.getLength1();
                existingLength2 = entity.getLength2();
                existingLength3 = entity.getLength3();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // 第一行：text1输入框 + length1输入框
        this.text1TextField = createTextInputField(panelX + 10, panelY + 40, existingText1);
        this.length1TextField = createLengthInputField(panelX + 205, panelY + 40, existingLength1);

        // 第二行：text2输入框 + length2输入框
        this.text2TextField = createTextInputField(panelX + 10, panelY + 70, existingText2);
        this.length2TextField = createLengthInputField(panelX + 205, panelY + 70, existingLength2);

        // 第三行：text3输入框 + length3输入框
        this.text3TextField = createTextInputField(panelX + 10, panelY + 100, existingText3);
        this.length3TextField = createLengthInputField(panelX + 205, panelY + 100, existingLength3);

        // 保存和取消按钮
        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_1.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_1.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private TextFieldWidget createTextInputField(int x, int y, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                TEXT_INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_1.content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_1.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private TextFieldWidget createLengthInputField(int x, int y, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                LENGTH_INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_1.content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_1.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);
            String text2 = getTextSafely(this.text2TextField);
            String text3 = getTextSafely(this.text3TextField);
            String length1 = getTextSafely(this.length1TextField);
            String length2 = getTextSafely(this.length2TextField);
            String length3 = getTextSafely(this.length3TextField);

            SignExpresswayDistanceFromLocation1UpdatePacket packet =
                    new SignExpresswayDistanceFromLocation1UpdatePacket(pos, text1, text2, text3, length1, length2, length3);
            RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), this.client.getNetworkHandler().getRegistryManager());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_1, buf);
        }
        this.close();
    }

    private String getTextSafely(TextFieldWidget textField) {
        return textField != null ? textField.getText() : "";
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ScreenRenderUtils.renderBackground(context, this.width, this.height);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_1.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        renderLabel(context, panelX, panelY, "1_name", 31);
        renderLabel(context, panelX, panelY, "2_name", 61);
        renderLabel(context, panelX, panelY, "3_name", 91);

        // 渲染所有文本框
        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length3TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderLabel(DrawContext context, int panelX, int panelY, String suffix, int yOffset) {
        renderLabel(context, panelX, panelY, suffix, yOffset, 10);
    }

    private void renderLabel(DrawContext context, int panelX, int panelY, String suffix, int yOffset, int xOffset) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_1." + suffix),
                panelX + xOffset, panelY + yOffset,
                0xFFAAAAAA
        );
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