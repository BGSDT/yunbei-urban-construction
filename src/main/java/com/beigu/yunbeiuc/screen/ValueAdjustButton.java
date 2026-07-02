package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

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
        super(x, y, width, height, new LiteralText(displayText), onPress);
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

    protected void renderWidget(MatrixStack context, int mouseX, int mouseY, float delta) {
        int color = this.isHovered() ? 0xFF888888 : 0xFF666666;
        DrawableHelper.fill(context, this.x, this.y, this.x + this.width, this.y + this.height, color);
        DrawableHelper.fill(context, this.x, this.y, this.width, this.height, 0xFFAAAAAA);

        DrawableHelper.drawCenteredText(
                context, 
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                new LiteralText(displayText),
                this.x + this.width / 2,
                this.y + (this.height - 8) / 2,
                0xFFFFFF00
        );
    }
}