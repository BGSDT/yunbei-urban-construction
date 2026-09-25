package com.beigu.yunbeiuc.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

/** Maps the shared screen lifecycle onto Minecraft 1.16.5. */
public abstract class Screen extends net.minecraft.client.gui.screens.Screen {
    protected Minecraft client;
    protected Font textRenderer;

    protected Screen(Component title) { super(title); }

    @Override
    protected void init() {
        client = minecraft;
        textRenderer = font;
    }

    @Override
    public final void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        render(new DrawContext(matrices), mouseX, mouseY, delta);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context.getMatrices(), mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext context) {
        super.renderBackground(context.getMatrices());
    }

    protected <T extends AbstractWidget> T addDrawableChild(T widget) {
        return addButton(widget);
    }

    protected <T extends GuiEventListener> T addSelectableChild(T widget) {
        return addWidget(widget);
    }

    protected void remove(GuiEventListener widget) {
        if (widget == null) return;
        children.remove(widget);
        if (widget instanceof AbstractWidget) {
            buttons.remove(widget);
        }
    }

    protected void clearChildren() {
        children.clear();
        buttons.clear();
    }

    public void close() { onClose(); }

    public boolean shouldPause() { return false; }

    @Override
    public boolean isPauseScreen() { return shouldPause(); }
}
