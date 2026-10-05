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
        // 必须用 super. 限定：未限定的 font / minecraft 会被 javac 记成「本类」的字段引用，
        // jar 重映射到 Yarn 后 font -> textRenderer、minecraft -> client，
        // 与本类同名字段撞车，putfield 退化成自赋值 → textRenderer 恒为 null → 渲染 NPE。
        client = super.minecraft;
        textRenderer = super.font;
    }

    @Override
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // 兜底：字段名与 Yarn 撞车时 init() 可能填不上，这里保证渲染期一定非 null
        if (this.client == null) this.client = super.minecraft;
        if (this.textRenderer == null) this.textRenderer = super.font;
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

    /**
     * 共享界面覆盖本方法来决定「打开时是否暂停游戏」。
     *
     * <p>名字故意带 {@code *Compat} 后缀，<b>不能</b>叫 {@code shouldPause}：
     * {@code shouldPause} 是 {@code method_25421}（即下面 {@code isPauseScreen}）在 Yarn 里的名字。
     * 本类同时 {@code @Override} 了 {@code isPauseScreen}，当 jar 由 intermediary 重映射到 Yarn 时，
     * 该 override 会被改名成 {@code shouldPause}，与同名同签名的方法撞车
     * （Loom 报 "Mapping target name conflicts detected ... unfixable conflicts"）。
     */
    public boolean shouldPauseCompat() { return false; }

    @Override
    public boolean isPauseScreen() { return shouldPauseCompat(); }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return mouseScrolledCompat(mouseX, mouseY, amount);
    }

    /** Version-neutral scroll hook used by the shared screens. */
    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
