package com.beigu.yunbeiuc.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class Screen extends net.minecraft.client.gui.screens.Screen {
    protected Minecraft client;
    protected Font textRenderer;

    private final List<Widget> extraRenderables = new ArrayList<>();

    protected Screen(Component title) { super(title); }

    @Override
    protected void init() {
        client = super.minecraft;
        textRenderer = super.font;
    }

    @Override
    public final void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (this.client == null) this.client = super.minecraft;
        if (this.textRenderer == null) this.textRenderer = super.font;
        render(new DrawContext(matrices), mouseX, mouseY, delta);
    }

    protected boolean renderBackdrop = false;

    public void setRenderBackdrop(boolean value) { this.renderBackdrop = value; }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.renderBackdrop) {
            super.renderBackground(context.getMatrices());
        }
        for (Widget widget : this.extraRenderables) {
            widget.render(context.getMatrices(), mouseX, mouseY, delta);
        }
        super.render(context.getMatrices(), mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext context) {
        super.renderBackground(context.getMatrices());
    }

    protected <T extends GuiEventListener> T addDrawableChild(T widget) {
        if (widget instanceof AbstractWidget abstractWidget) {
            super.addButton(abstractWidget);
        } else {
            super.addWidget(widget);
            if (widget instanceof Widget renderable) {
                this.extraRenderables.add(renderable);
            }
        }
        return widget;
    }

    protected <T extends GuiEventListener> T addSelectableChild(T widget) {
        return super.addWidget(widget);
    }

    protected void remove(GuiEventListener widget) {
        if (widget == null) return;
        children.remove(widget);
        this.extraRenderables.remove(widget);
        if (widget instanceof AbstractWidget) {
            buttons.remove(widget);
        }
    }

    protected void clearChildren() {
        children.clear();
        buttons.clear();
        this.extraRenderables.clear();
    }

    public void close() { super.onClose(); }

    public boolean shouldPauseCompat() { return false; }

    @Override
    public boolean isPauseScreen() { return shouldPauseCompat(); }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return mouseScrolledCompat(mouseX, mouseY, amount);
    }

    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
