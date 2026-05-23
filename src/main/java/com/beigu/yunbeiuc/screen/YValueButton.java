package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;

public class YValueButton extends ButtonWidget {
    private float yValue;
    private final BiConsumer<YValueButton, Boolean> onPressWithButton;

    public YValueButton(int x, int y, int width, int height, float initialYValue,
                        PressAction onPress, BiConsumer<YValueButton, Boolean> onPressWithButton) {
        super(x, y, width, height, Text.literal("Y"), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.yValue = initialYValue;
        this.onPressWithButton = onPressWithButton;
    }

    public void setYValue(float yValue) {
        this.yValue = yValue;
    }

    public float getYValue() {
        return yValue;
    }

    public void onClick(boolean isLeftClick) {
        if (this.onPressWithButton != null) {
            this.onPressWithButton.accept(this, isLeftClick);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        // 左键点击
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
        // 绘制按钮背景
        int color = this.isHovered() ? 0xFF888888 : 0xFF666666;
        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, color);
        context.drawBorder(this.getX(), this.getY(), this.width, this.height, 0xFFAAAAAA);

        // 绘制Y文字
        context.drawCenteredTextWithShadow(
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                Text.literal("Y"),
                this.getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2,
                0xFFFFFF00
        );
    }
}