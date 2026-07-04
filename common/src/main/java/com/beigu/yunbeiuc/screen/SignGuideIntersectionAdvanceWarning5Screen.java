package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning5Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning5UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning5Screen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget text3TextField;
    private TextFieldWidget text4TextField;

    private float text1AndY = 0f;
    private float text2AndY = 0f;
    private float text3AndY = 0f;
    private float text4AndY = 0f;

    private ValueAdjustButton text1YButton;
    private ValueAdjustButton text2YButton;
    private ValueAdjustButton text3YButton;
    private ValueAdjustButton text4YButton;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 245;
    private static final int INPUT_WIDTH = 200;
    private static final int INPUT_HEIGHT = 24;
    private static final int BUTTON_WIDTH = 24;
    private static final int BUTTON_HEIGHT = 24;

    public SignGuideIntersectionAdvanceWarning5Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";
        String existingText2 = "";
        String existingText3 = "";
        String existingText4 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionAdvanceWarning5Entity entity) {
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingText3 = entity.getText3();
                existingText4 = entity.getText4();
                text1AndY = entity.getText1AndY();
                text2AndY = entity.getText2AndY();
                text3AndY = entity.getText3AndY();
                text4AndY = entity.getText4AndY();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.text1TextField = createTextField(panelX + 10, panelY + 40, existingText1);
        this.text2TextField = createTextField(panelX + 10, panelY + 85, existingText2);
        this.text3TextField = createTextField(panelX + 10, panelY + 130, existingText3);
        this.text4TextField = createTextField(panelX + 10, panelY + 175, existingText4);

        this.text1YButton = createValueAdjustButton(panelX + 220, panelY + 40, text1AndY,
                (isLeftClick) -> text1AndY += isLeftClick ? 1.0f : -1.0f);
        this.text2YButton = createValueAdjustButton(panelX + 220, panelY + 85, text2AndY,
                (isLeftClick) -> text2AndY += isLeftClick ? 1.0f : -1.0f);
        this.text3YButton = createValueAdjustButton(panelX + 220, panelY + 130, text3AndY,
                (isLeftClick) -> text3AndY += isLeftClick ? 1.0f : -1.0f);
        this.text4YButton = createValueAdjustButton(panelX + 220, panelY + 175, text4AndY,
                (isLeftClick) -> text4AndY += isLeftClick ? 1.0f : -1.0f);

        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 60, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.cancel"), button -> this.close())
                        .dimensions(panelX + 170, buttonY, 90, 24)
                        .build()
        );

        this.setFocused(this.text1TextField);
    }

    private TextFieldWidget createTextField(int x, int y, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private ValueAdjustButton createValueAdjustButton(int x, int y, float initialValue, ValueAdjustCallback callback) {
        ValueAdjustButton button = this.addDrawableChild(
                new ValueAdjustButton(
                        x, y,
                        BUTTON_WIDTH, BUTTON_HEIGHT,
                        initialValue, "Y",
                        buttonWidget -> {},
                        (buttonWidget, isLeftClick) -> {
                            callback.onAdjust(isLeftClick);
                            ((ValueAdjustButton) buttonWidget).setValue(getValueForButton(buttonWidget));
                        }
                )
        );
        return button;
    }

    private float getValueForButton(ValueAdjustButton button) {
        if (button == text1YButton) return text1AndY;
        if (button == text2YButton) return text2AndY;
        if (button == text3YButton) return text3AndY;
        if (button == text4YButton) return text4AndY;
        return 0f;
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);
            String text2 = getTextSafely(this.text2TextField);
            String text3 = getTextSafely(this.text3TextField);
            String text4 = getTextSafely(this.text4TextField);

            SignGuideIntersectionAdvanceWarning5UpdatePacket packet =
                    new SignGuideIntersectionAdvanceWarning5UpdatePacket(pos, text1, text2, text3, text4, text1AndY, text2AndY, text3AndY, text4AndY);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5, buf);
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
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        renderLabelAndValue(context, panelX, panelY, 1, 31, 40, 48, text1AndY);
        renderLabelAndValue(context, panelX, panelY, 2, 76, 85, 93, text2AndY);
        renderLabelAndValue(context, panelX, panelY, 3, 121, 130, 138, text3AndY);
        renderLabelAndValue(context, panelX, panelY, 4, 166, 175, 183, text4AndY);

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text4TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderLabelAndValue(DrawContext context, int panelX, int panelY, int index,
                                     int labelY, int fieldY, int valueY, float value) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.text_" + index + "_name"),
                panelX + 10, panelY + labelY,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.y_value", String.format("%.1f", value)),
                panelX + 250, panelY + valueY,
                0xFFFFFF00
        );
    }

    private void renderTextField(TextFieldWidget textField, DrawContext context, int mouseX, int mouseY, float delta) {
        if (textField != null) {
            textField.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var child : this.children()) {
            if (child instanceof ValueAdjustButton valueButton && child.isMouseOver(mouseX, mouseY)) {
                if (button == 1) {
                    valueButton.onClick(false);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
    private interface ValueAdjustCallback {
        void onAdjust(boolean isLeftClick);
    }
}