package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideConfirmation1Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideConfirmation1UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideConfirmation1Screen extends Screen {
    private final BlockPos pos;

    private SignGuideConfirmation1Entity.Unit unit1;
    private SignGuideConfirmation1Entity.Unit unit2;
    private SignGuideConfirmation1Entity.Unit unit3;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget text3TextField;
    private TextFieldWidget length1TextField;
    private TextFieldWidget length2TextField;
    private TextFieldWidget length3TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 245;
    private static final int TEXT_INPUT_WIDTH = 130;
    private static final int LENGTH_INPUT_WIDTH = 70;
    private static final int INPUT_HEIGHT = 20;
    private static final int UNIT_METRE_WIDTH = 45;
    private static final int UNIT_KILOMETRE_WIDTH = 55;
    private static final int ROW_COUNT = 3;

    public SignGuideConfirmation1Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.unit1 = SignGuideConfirmation1Entity.Unit.METRE;
        this.unit2 = SignGuideConfirmation1Entity.Unit.METRE;
        this.unit3 = SignGuideConfirmation1Entity.Unit.METRE;
        String existingText1 = "";
        String existingText2 = "";
        String existingText3 = "";
        String existingLength1 = "";
        String existingLength2 = "";
        String existingLength3 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideConfirmation1Entity entity) {
                this.unit1 = entity.getUnit1();
                this.unit2 = entity.getUnit2();
                this.unit3 = entity.getUnit3();
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

        // 第一行：text1输入框 + length1输入框 + 单位1选择
        this.text1TextField = createTextInputField(panelX + 10, panelY + 40, existingText1, 1);
        this.length1TextField = createLengthInputField(panelX + 150, panelY + 40, existingLength1, 1);
        createUnitButtons(panelX + 230, panelY + 40, unit -> this.unit1 = unit);

        // 第二行：text2输入框 + length2输入框 + 单位2选择
        this.text2TextField = createTextInputField(panelX + 10, panelY + 80, existingText2, 2);
        this.length2TextField = createLengthInputField(panelX + 150, panelY + 80, existingLength2, 2);
        createUnitButtons(panelX + 230, panelY + 80, unit -> this.unit2 = unit);

        // 第三行：text3输入框 + length3输入框 + 单位3选择
        this.text3TextField = createTextInputField(panelX + 10, panelY + 120, existingText3, 3);
        this.length3TextField = createLengthInputField(panelX + 150, panelY + 120, existingLength3, 3);
        createUnitButtons(panelX + 230, panelY + 120, unit -> this.unit3 = unit);

        // 保存和取消按钮
        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private TextFieldWidget createTextInputField(int x, int y, String existingText, int index) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                TEXT_INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.text_" + index)
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private TextFieldWidget createLengthInputField(int x, int y, String existingText, int index) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                LENGTH_INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.length_" + index)
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private void createUnitButtons(int x, int y, UnitConsumer unitConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.metre"), button -> {
                    unitConsumer.accept(SignGuideConfirmation1Entity.Unit.METRE);
                }).dimensions(x, y, UNIT_METRE_WIDTH, INPUT_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.kilometre"), button -> {
                    unitConsumer.accept(SignGuideConfirmation1Entity.Unit.KILOMETRE);
                }).dimensions(x + 50, y, UNIT_KILOMETRE_WIDTH, INPUT_HEIGHT).build()
        );
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);
            String text2 = getTextSafely(this.text2TextField);
            String text3 = getTextSafely(this.text3TextField);
            String length1 = getTextSafely(this.length1TextField);
            String length2 = getTextSafely(this.length2TextField);
            String length3 = getTextSafely(this.length3TextField);

            SignGuideConfirmation1UpdatePacket packet =
                    new SignGuideConfirmation1UpdatePacket(pos, unit1, unit2, unit3, text1, text2, text3, length1, length2, length3);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_GUIDE_CONFIRMATION_1, buf);
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
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        // 渲染行标签和状态
        for (int i = 1; i <= ROW_COUNT; i++) {
            renderRowLabel(context, panelX, panelY, i);
        }

        renderUnitStatus(context, panelX, panelY, 1, unit1);
        renderUnitStatus(context, panelX, panelY, 2, unit2);
        renderUnitStatus(context, panelX, panelY, 3, unit3);

        // 渲染所有文本框
        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length3TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderRowLabel(DrawContext context, int panelX, int panelY, int index) {
        int yOffset = 28 + (index - 1) * 40;
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1." + index + "_name"),
                panelX + 10, panelY + yOffset,
                0xFFAAAAAA
        );
    }

    private void renderUnitStatus(DrawContext context, int panelX, int panelY, int index,
                                  SignGuideConfirmation1Entity.Unit unit) {
        int yOffset = 42 + (index - 1) * 40;
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.unit." + unit.getName()),
                panelX + 340, panelY + yOffset,
                0xFFFFFF00
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

    @FunctionalInterface
    private interface UnitConsumer {
        void accept(SignGuideConfirmation1Entity.Unit unit);
    }
}