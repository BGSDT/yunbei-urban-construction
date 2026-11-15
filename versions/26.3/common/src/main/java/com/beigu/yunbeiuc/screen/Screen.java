package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.input.Keys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

public abstract class Screen extends net.minecraft.client.gui.screens.Screen {
    public boolean keyPressedCompat(net.minecraft.client.gui.components.events.GuiEventListener widget,
                                    int keyCode, int scanCode, int modifiers) {
        return widget.keyPressed(new net.minecraft.client.input.KeyEvent(keyCode, scanCode, modifiers));
    }
    public boolean dispatchWidgetClickCompat(double mouseX, double mouseY, int button) {
        java.util.List<net.minecraft.client.gui.components.events.GuiEventListener> clickChildren =
                new java.util.ArrayList<>(this.children());
        for (int i = clickChildren.size() - 1; i >= 0; i--) {
            net.minecraft.client.gui.components.events.GuiEventListener child = clickChildren.get(i);
            if (child instanceof net.minecraft.client.gui.components.AbstractWidget widget
                    && widget.visible && widget.active && widget.isMouseOver(mouseX, mouseY)) {
                if (child.mouseClicked(new net.minecraft.client.input.MouseButtonEvent(mouseX, mouseY,
                        new net.minecraft.client.input.MouseButtonInfo(legacyButton(button) + 1, 0)), false)) {
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

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        onScreenShownCompat();
    }

    @Override
    public void added() {
        super.added();
        onScreenShownCompat();
    }

    public void rebuildWidgetsCompat() {
        super.clearWidgets();
        init();
    }
    public void onScreenShownCompat() { }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        if (this.client == null) this.client = super.minecraft;
        if (this.textRenderer == null) this.textRenderer = super.font;
        render(new DrawContext(extractor), mouseX, mouseY, delta);
    }

    private static final int BACKDROP_TOP = 0xC0101010;
    private static final int BACKDROP_BOTTOM = 0xD0101010;
    private static final int BACKDROP_GREY = 0xFF202020;

    @Override
    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        if (!keepMenuBackground()) return;
        super.extractBackground(extractor, mouseX, mouseY, delta);
    }

    protected boolean keepMenuBackground() { return true; }

    protected boolean renderBackdrop = false;
    protected float backdropAlpha = 1.0f;

    public void setRenderBackdrop(boolean value) { this.renderBackdrop = value; }

    public void setBackdropAlpha(float alpha) { this.backdropAlpha = Math.max(0f, Math.min(1f, alpha)); }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.renderBackdrop) {
            renderBackground(context);
        }
        super.extractRenderState(context.getGraphics(), mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext context) {
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
    public boolean keyPressed(KeyEvent event) {
        return keyPressed(event.key(), 0, event.modifiers());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        return keyReleased(event.key(), 0, event.modifiers());
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(new KeyEvent(keyCode, scanCode, modifiers));
    }

    private static int legacyButton(int button) {
        return Math.max(0, button);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean result = mouseClicked(event.x(), event.y(), legacyButton(event.button() - 1));
        return result;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(legacyButton(button) + 1, 0)), false);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return mouseReleased(event.x(), event.y(), legacyButton(event.button() - 1));
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(legacyButton(button) + 1, 0)));
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return mouseDragged(event.x(), event.y(), legacyButton(event.button() - 1), deltaX, deltaY);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(legacyButton(button) + 1, 0)), deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return mouseScrolledCompat(mouseX, mouseY, scrollY);
    }

    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount, amount);
    }

    public static boolean hasShiftDown() {
        return isKeyDown(Keys.LEFT_SHIFT)
                || isKeyDown(Keys.RIGHT_SHIFT);
    }

    public static boolean hasControlDown() {
        return isKeyDown(Keys.LEFT_CONTROL)
                || isKeyDown(Keys.RIGHT_CONTROL);
    }

    public static boolean hasAltDown() {
        return isKeyDown(Keys.LEFT_ALT)
                || isKeyDown(Keys.RIGHT_ALT);
    }

    private static boolean isKeyDown(int key) {
        return com.mojang.blaze3d.platform.InputConstants.isKeyDown(key);
    }
}
