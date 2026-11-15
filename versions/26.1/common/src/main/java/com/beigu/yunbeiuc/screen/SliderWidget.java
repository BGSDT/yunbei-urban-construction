package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public abstract class SliderWidget extends AbstractSliderButton {
    protected SliderWidget(int x, int y, int width, int height, Component label, double value) {
        super(x, y, width, height, label, value);
    }

    private net.minecraft.client.input.MouseButtonEvent lastClick;

    @Override
    public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        this.lastClick = event;
        onClickCompat(event.x(), event.y());
    }

    protected void onClickCompat(double mouseX, double mouseY) {
        if (this.lastClick != null) {
            super.onClick(this.lastClick, false);
        }
    }

    private net.minecraft.client.input.MouseButtonEvent lastDrag;

    @Override
    public void onDrag(net.minecraft.client.input.MouseButtonEvent event, double deltaX, double deltaY) {
        this.lastDrag = event;
        onDragCompat(event.x(), event.y(), deltaX, deltaY);
    }

    protected void onDragCompat(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (this.lastDrag != null) {
            super.onDrag(this.lastDrag, deltaX, deltaY);
        }
    }
}
