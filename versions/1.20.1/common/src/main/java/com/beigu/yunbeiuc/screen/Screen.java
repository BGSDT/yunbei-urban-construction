package com.beigu.yunbeiuc.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

/** Maps the shared screen lifecycle onto Minecraft 1.20.1. */
public abstract class Screen extends net.minecraft.client.gui.screens.Screen {
    protected Minecraft client;
    protected Font textRenderer;

    protected Screen(Component title) { super(title); }

    @Override
    protected void init() {
        super.init();
        client = minecraft;
        textRenderer = font;
    }

    @Override
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        render(new DrawContext(graphics), mouseX, mouseY, delta);
    }

    /** 是否在 render 时自动铺底。1.20.1 的 Screen.render 不自动铺底，故默认关闭。 */
    protected boolean renderBackdrop = false;

    /** 具体界面按需开启自动铺底；不调用则完全由界面自己决定要不要调 renderBackground。 */
    public void setRenderBackdrop(boolean value) { this.renderBackdrop = value; }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.renderBackdrop) {
            super.renderBackground(context.getGraphics());
        }
        super.render(context.getGraphics(), mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext context) {
        super.renderBackground(context.getGraphics());
    }

    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addDrawableChild(T widget) {
        return addRenderableWidget(widget);
    }

    protected <T extends GuiEventListener & NarratableEntry> T addSelectableChild(T widget) {
        return addWidget(widget);
    }

    protected void remove(GuiEventListener widget) {
        if (widget != null) removeWidget(widget);
    }

    protected void clearChildren() { clearWidgets(); }

    public void close() { onClose(); }

    public boolean shouldPause() { return false; }

    @Override
    public boolean isPauseScreen() { return shouldPause(); }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return mouseScrolledCompat(mouseX, mouseY, amount);
    }

    /** Version-neutral scroll hook used by the shared screens. */
    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
