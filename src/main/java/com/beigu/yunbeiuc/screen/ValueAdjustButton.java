package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;

public class ValueAdjustButton extends ButtonWidget {
    private float value;
    private final String displayText;
    private final BiConsumer<ValueAdjustButton, Boolean> onPressWithButton;

    public ValueAdjustButton(int x, int y, int width, int height, float initialValue,
                             PressAction onPress, BiConsumer<ValueAdjustButton, Boolean> onPressWithButton) {
        this(x, y, width, height, initialValue, "+", onPress, onPressWithButton);
    }

    public ValueAdjustButton(int x, int y, int width, int height, float initialValue, String displayText,
                             PressAction onPress, BiConsumer<ValueAdjustButton, Boolean> onPressWithButton) {
        super(x, y, width, height, Text.literal(displayText), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.value = initialValue;
        this.displayText = displayText;
        this.onPressWithButton = onPressWithButton;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void onClick(boolean isLeftClick) {
        if (this.onPressWithButton != null) {
            this.onPressWithButton.accept(this, isLeftClick);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onClick(true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean bl = this.clicked(mouseX, mouseY);
                if (bl) {
                    this.playDownSound(net.minecraft.client.MinecraftClient.getInstance().getSoundManager());
                    this.onClick(mouseX, mouseY);
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = this.isHovered() ? 0xFF888888 : 0xFF666666;
        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, color);
        context.drawBorder(this.getX(), this.getY(), this.width, this.height, 0xFFAAAAAA);

        context.drawCenteredTextWithShadow(
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                Text.literal(displayText),
                this.getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2,
                0xFFFFFF00
        );
    }
}