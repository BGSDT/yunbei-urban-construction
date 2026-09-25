package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ButtonWidget extends Button implements ClickableWidget {
    private final Consumer<ButtonWidget> action;

    private ButtonWidget(int x, int y, int width, int height, Component label, Consumer<ButtonWidget> action) {
        super(x, y, width, height, label, button -> ((ButtonWidget) button).action.accept((ButtonWidget) button));
        this.action = action;
    }

    public static Builder builderCompat(Component label, Consumer<ButtonWidget> action) {
        return new Builder(label, action);
    }

    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context.getMatrices(), mouseX, mouseY, delta);
    }

    public static final class Builder {
        private final Component label;
        private final Consumer<ButtonWidget> action;
        private int x, y, width, height;

        private Builder(Component label, Consumer<ButtonWidget> action) {
            this.label = label;
            this.action = action;
        }

        public Builder dimensions(int x, int y, int width, int height) {
            this.x = x; this.y = y; this.width = width; this.height = height;
            return this;
        }

        public ButtonWidget build() { return new ButtonWidget(x, y, width, height, label, action); }
    }
}
