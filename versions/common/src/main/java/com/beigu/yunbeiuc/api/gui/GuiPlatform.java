package com.beigu.yunbeiuc.api.gui;

import com.beigu.yunbeiuc.screen.ButtonWidget;
import com.beigu.yunbeiuc.screen.TextFieldWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/** Version-sensitive widget construction. Each Minecraft version supplies an implementation. */
public interface GuiPlatform {
    ButtonWidget createButton(Component label, int x, int y, int width, int height,
                              Consumer<ButtonWidget> action);

    TextFieldWidget createTextField(Font font, int x, int y, int width, int height, Component label);

    void setPlaceholder(TextFieldWidget textField, Component placeholder);

    void setText(TextFieldWidget textField, String value);

    String getText(TextFieldWidget textField);

    void setMaxLength(TextFieldWidget textField, int maxLength);

    void setChangedListener(TextFieldWidget textField, Consumer<String> listener);

    void setTextFieldPosition(TextFieldWidget textField, int x, int y);

    void setButtonPosition(ButtonWidget button, int x, int y);

    void setButtonMessage(ButtonWidget button, Component message);
}
