package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TextFieldWidget extends EditBox {
    public TextFieldWidget(Font font, int x, int y, int width, int height, Component label) {
        super(font, x, y, width, height, label);
    }

    public String getText() { return getValue(); }
    public void setText(String value) { setValue(value); }
    public void setChangedListener(Consumer<String> listener) { setResponder(listener); }
    public void setPlaceholder(Component placeholder) { setHint(placeholder); }
    public void setPosition(int x, int y) { setX(x); setY(y); }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context.getMatrices(), mouseX, mouseY, delta);
    }
}
