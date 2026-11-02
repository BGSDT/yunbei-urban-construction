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
    protected Minecraft client;
    protected Font textRenderer;

    protected Screen(Component title) { super(title); }

    @Override
    protected void init() {
        super.init();
        client = super.minecraft;
        textRenderer = super.font;
    }

    // 26.x renamed the draw pass: screens now fill in a GuiGraphicsExtractor instead of
    // drawing straight into a GuiGraphics.
    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        if (this.client == null) this.client = super.minecraft;
        if (this.textRenderer == null) this.textRenderer = super.font;
        render(new DrawContext(extractor), mouseX, mouseY, delta);
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
        GuiGraphicsExtractor extractor = context.getGraphics();
        for (GuiEventListener element : this.children()) {
            if (element instanceof Renderable renderable) {
                renderable.extractRenderState(extractor, mouseX, mouseY, delta);
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

    // --- input translation -------------------------------------------------
    // 26.x passes input event records; the shared screens still override the classic
    // (keyCode, scanCode, modifiers) / (mouseX, mouseY, button) signatures.

    @Override
    public boolean keyPressed(KeyEvent event) {
        return keyPressed(event.key(), event.scancode(), event.modifiers());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        return keyReleased(event.key(), event.scancode(), event.modifiers());
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(new KeyEvent(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return mouseClicked(event.x(), event.y(), event.button());
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), false);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return mouseReleased(event.x(), event.y(), event.button());
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)));
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return mouseDragged(event.x(), event.y(), event.button(), deltaX, deltaY);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return mouseScrolledCompat(mouseX, mouseY, scrollY);
    }

    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount, amount);
    }

    // 26.x dropped the Screen modifier helpers; the shared screens still call them unqualified.
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
        return com.mojang.blaze3d.platform.InputConstants.isKeyDown(
                Minecraft.getInstance().getWindow(), key);
    }
}
