package com.beigu.yunbeiuc.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

public abstract class Screen extends net.minecraft.client.gui.screens.Screen {
    protected Minecraft client;
    protected Font textRenderer;

    protected Screen(Component title) { super(title); }

    @Override
    protected void init() {
        super.init();
        client = super.minecraft;
        textRenderer = super.font;
    }

    @Override
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.client == null) this.client = super.minecraft;
        if (this.textRenderer == null) this.textRenderer = super.font;
        render(new DrawContext(graphics), mouseX, mouseY, delta);
    }

    protected boolean renderBackdrop = false;

    public void setRenderBackdrop(boolean value) { this.renderBackdrop = value; }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.renderBackdrop) {
            // 1.20.2 起 renderBackground 多带 mouseX/mouseY/partialTick 三个参数（1.20.4 里未被使用）。
            super.renderBackground(context.getGraphics(), mouseX, mouseY, delta);
        }
        super.render(context.getGraphics(), mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext context) {
        super.renderBackground(context.getGraphics(), 0, 0, 0);
    }

    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addDrawableChild(T widget) {
        return super.addRenderableWidget(widget);
    }

    protected <T extends GuiEventListener & NarratableEntry> T addSelectableChild(T widget) {
        return super.addWidget(widget);
    }

    protected void remove(GuiEventListener widget) {
        if (widget != null) super.removeWidget(widget);
    }

    protected void clearChildren() { super.clearWidgets(); }

    public void close() { super.onClose(); }

    public boolean shouldPauseCompat() { return false; }

    @Override
    public boolean isPauseScreen() { return shouldPauseCompat(); }
    // 1.20.2 起 GuiEventListener.mouseScrolled 变为 4 参（scrollX/scrollY 拆分），
    // 与 1.21.1 的 shim 一致；纵向滚动取 scrollY。
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return mouseScrolledCompat(mouseX, mouseY, scrollY);
    }

    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount, amount);
    }
}
