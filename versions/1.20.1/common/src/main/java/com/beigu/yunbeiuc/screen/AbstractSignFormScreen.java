package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractSignFormScreen extends Screen {
    protected final BlockPos pos;
    protected final String translationPrefix;

    protected AbstractSignFormScreen(BlockPos pos, String translationPrefix) {
        super(Text.translatable(translationPrefix + ".title"));
        this.pos = pos;
        this.translationPrefix = translationPrefix;
    }

    protected abstract void saveAndClose();

    protected static String getTextSafely(TextFieldWidget textField) {
        return textField != null ? textField.getText() : "";
    }

    protected TextFieldWidget createTextField(int x, int y, int width, int height, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                width, height,
                Text.translatable(translationPrefix + ".content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable(translationPrefix + ".placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    protected void renderTextField(TextFieldWidget textField, DrawContext context, int mouseX, int mouseY, float delta) {
        if (textField != null) {
            textField.render(context, mouseX, mouseY, delta);
        }
    }

    protected void renderPanelBackground(DrawContext context, int panelX, int panelY, int panelWidth, int panelHeight) {
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA333333);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFCCCCCC);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                panelX + panelWidth / 2, panelY + 12,
                0xFFCCCCCC
        );
    }

    protected void renderLabel(DrawContext context, int panelX, int panelY, String suffix, int yOffset) {
        renderLabel(context, panelX, panelY, suffix, yOffset, 10);
    }

    protected void renderLabel(DrawContext context, int panelX, int panelY, String suffix, int yOffset, int xOffset) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable(translationPrefix + "." + suffix),
                panelX + xOffset, panelY + yOffset,
                0xFFAAAAAA
        );
    }

    protected void renderStatus(DrawContext context, int panelX, int panelY, int xOffset, int yOffset, Text text) {
        context.drawTextWithShadow(
                this.textRenderer,
                text,
                panelX + xOffset, panelY + yOffset,
                0xFFFFFF00
        );
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
