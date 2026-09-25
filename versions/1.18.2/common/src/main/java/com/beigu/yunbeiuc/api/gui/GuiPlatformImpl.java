package com.beigu.yunbeiuc.api.gui;

import com.beigu.yunbeiuc.screen.ButtonWidget;
import com.beigu.yunbeiuc.screen.TextFieldWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/** Widget construction for the Minecraft 1.18.2 GUI API. */
public final class GuiPlatformImpl implements GuiPlatform {
    @Override
    public ButtonWidget createButton(Component label, int x, int y, int width, int height,
                                     Consumer<ButtonWidget> action) {
        return ButtonWidget.builder(label, action).dimensions(x, y, width, height).build();
    }

    @Override
    public TextFieldWidget createTextField(Font font, int x, int y, int width, int height, Component label) {
        return new TextFieldWidget(font, x, y, width, height, label);
    }

    @Override
    public void setPlaceholder(TextFieldWidget textField, Component placeholder) {
        textField.setPlaceholder(placeholder);
    }

    @Override
    public void setText(TextFieldWidget textField, String value) {
        textField.setText(value);
    }

    @Override
    public String getText(TextFieldWidget textField) {
        return textField.getText();
    }

    @Override
    public void setMaxLength(TextFieldWidget textField, int maxLength) {
        textField.setMaxLength(maxLength);
    }

    @Override
    public void setChangedListener(TextFieldWidget textField, Consumer<String> listener) {
        textField.setChangedListener(listener);
    }

    @Override
    public void setTextFieldPosition(TextFieldWidget textField, int x, int y) {
        textField.setPosition(x, y);
    }

    @Override
    public void setButtonPosition(ButtonWidget button, int x, int y) {
        button.setPosition(x, y);
    }

    @Override
    public void setButtonMessage(ButtonWidget button, Component message) {
        button.setMessage(message);
    }
}
