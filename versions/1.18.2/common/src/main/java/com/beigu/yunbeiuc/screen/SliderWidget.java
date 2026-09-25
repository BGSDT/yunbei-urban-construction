package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public abstract class SliderWidget extends AbstractSliderButton {
    protected SliderWidget(int x, int y, int width, int height, Component label, double value) {
        super(x, y, width, height, label, value);
    }

    public int getX() { return x; }
}
