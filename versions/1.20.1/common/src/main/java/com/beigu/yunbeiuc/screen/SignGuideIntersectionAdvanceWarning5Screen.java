package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning5Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning5UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning5Screen extends AbstractSignFormScreen {
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
        super(pos, "text.yunbeiuc.sign_guide_intersection_advance_warning_5");
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

        this.text1TextField = createTextField(panelX + 10, panelY + 40, INPUT_WIDTH, INPUT_HEIGHT, existingText1);
        this.text2TextField = createTextField(panelX + 10, panelY + 85, INPUT_WIDTH, INPUT_HEIGHT, existingText2);
        this.text3TextField = createTextField(panelX + 10, panelY + 130, INPUT_WIDTH, INPUT_HEIGHT, existingText3);
        this.text4TextField = createTextField(panelX + 10, panelY + 175, INPUT_WIDTH, INPUT_HEIGHT, existingText4);

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

    @Override
    protected void saveAndClose() {
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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        renderPanelBackground(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

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

    @FunctionalInterface
    private interface ValueAdjustCallback {
        void onAdjust(boolean isLeftClick);
    }
}