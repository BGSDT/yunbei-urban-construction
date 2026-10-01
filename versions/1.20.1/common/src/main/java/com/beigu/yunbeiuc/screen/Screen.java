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

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
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
}
