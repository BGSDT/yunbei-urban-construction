package com.beigu.yunbeiuc.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

public abstract class Screen extends net.minecraft.client.gui.screens.Screen {
    public boolean keyPressedCompat(net.minecraft.client.gui.components.events.GuiEventListener widget,
                                    int keyCode, int scanCode, int modifiers) {
        return widget.keyPressed(keyCode, scanCode, modifiers);
    }
    public boolean dispatchWidgetClickCompat(double mouseX, double mouseY, int button) {
        java.util.List<net.minecraft.client.gui.components.events.GuiEventListener> clickChildren =
                new java.util.ArrayList<>(this.children());
        for (int i = clickChildren.size() - 1; i >= 0; i--) {
            net.minecraft.client.gui.components.events.GuiEventListener child = clickChildren.get(i);
            if (child instanceof net.minecraft.client.gui.components.AbstractWidget widget
                    && widget.visible && widget.active && widget.isMouseOver(mouseX, mouseY)) {
                if (child.mouseClicked(mouseX, mouseY, button)) {
                    this.setFocused((net.minecraft.client.gui.components.events.GuiEventListener) child);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTextInputFocusedCompat() {
        for (var child : this.children()) {
            if (child instanceof net.minecraft.client.gui.components.EditBox box && box.isFocused()) return true;
        }
        return false;
    }
    protected Minecraft client;
    protected Font textRenderer;

    protected Screen(Component title) { super(title); }

    @Override
    protected void init() {
        super.init();
        client = super.minecraft;
        textRenderer = super.font;
        onScreenShownCompat();
    }

    public void rebuildWidgetsCompat() {
        this.clearChildren();
        init();
    }
    protected boolean keepMenuBackground() { return true; }
    public void onScreenShownCompat() { }

    @Override
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.client == null) this.client = super.minecraft;
        if (this.textRenderer == null) this.textRenderer = super.font;
        render(new DrawContext(graphics), mouseX, mouseY, delta);
    }

    private static final int BACKDROP_TOP = 0xC0101010;
    private static final int BACKDROP_BOTTOM = 0xD0101010;
    private static final int BACKDROP_GREY = 0xFF202020;

    protected boolean renderBackdrop = false;
    protected float backdropAlpha = 1.0f;

    public void setRenderBackdrop(boolean value) { this.renderBackdrop = value; }

    public void setBackdropAlpha(float alpha) { this.backdropAlpha = Math.max(0f, Math.min(1f, alpha)); }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.renderBackdrop) {
            renderBackground(context);
        }
        GuiGraphics graphics = context.getGraphics();
        for (GuiEventListener element : this.children()) {
            if (element instanceof Renderable renderable) {
                renderable.render(graphics, mouseX, mouseY, delta);
            }
        }
    }

    public void renderBackground(DrawContext context) {
        if (Minecraft.getInstance().level != null) {
            context.fillGradient(0, 0, this.width, this.height,
                    applyAlpha(BACKDROP_TOP), applyAlpha(BACKDROP_BOTTOM));
        } else {
            context.fill(0, 0, this.width, this.height, applyAlpha(BACKDROP_GREY));
        }
    }

    private int applyAlpha(int argb) {
        if (this.backdropAlpha >= 1.0f) {
            return argb;
        }
        int alpha = Math.round(((argb >>> 24) & 0xFF) * this.backdropAlpha);
        return (alpha << 24) | (argb & 0x00FFFFFF);
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
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return mouseScrolledCompat(mouseX, mouseY, scrollY);
    }

    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount, amount);
    }
}
