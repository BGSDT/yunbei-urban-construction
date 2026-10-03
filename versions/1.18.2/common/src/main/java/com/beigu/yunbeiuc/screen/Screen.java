package com.beigu.yunbeiuc.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

/** Maps the shared screen lifecycle onto Minecraft 1.18.2. */
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

    /** 鏄惁鍦?render 鏃惰嚜鍔ㄩ摵搴曘€?.20.1 鐨?Screen.render 涓嶈嚜鍔ㄩ摵搴曪紝鏁呴粯璁ゅ叧闂€?*/
    protected boolean renderBackdrop = false;

    /** 鍏蜂綋鐣岄潰鎸夐渶寮€鍚嚜鍔ㄩ摵搴曪紱涓嶈皟鐢ㄥ垯瀹屽叏鐢辩晫闈㈣嚜宸卞喅瀹氳涓嶈璋?renderBackground銆?*/
    public void setRenderBackdrop(boolean value) { this.renderBackdrop = value; }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.renderBackdrop) {
            super.renderBackground(
context.getMatrices()
);
        }
        super.render(
context.getMatrices()
, mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext context) {
        super.renderBackground(context.getMatrices());
    }

    protected <T extends GuiEventListener & Widget & NarratableEntry> T addDrawableChild(T widget) {
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
