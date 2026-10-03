package com.beigu.yunbeiuc.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

/** Maps the shared screen lifecycle onto Minecraft 1.21.1. */
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

    /** 1.20.1 原版底色（有世界时）：上暗、下更暗的半透明渐变，两层各画一次就会明显变黑。 */
    private static final int BACKDROP_TOP = 0xC0101010;
    private static final int BACKDROP_BOTTOM = 0xD0101010;
    /** 没有世界时（主菜单类界面）用的不透明灰底。 */
    private static final int BACKDROP_GREY = 0xFF202020;

    /** 是否在 {@link #render} 时自动铺底。1.20.1 的 {@code Screen.render} 不自动铺底，故默认关闭。 */
    protected boolean renderBackdrop = false;
    /** 底色透明度倍率（1.0 = 原版浓度，0 = 全透明），便于个别界面调浅。 */
    protected float backdropAlpha = 1.0f;

    /** 具体界面按需开启自动铺底；不调用则完全由界面自己决定要不要调 {@link #renderBackground}。 */
    public void setRenderBackdrop(boolean value) { this.renderBackdrop = value; }

    /** 调整底色透明度倍率：{@code 1.0} 为 1.20.1 原版浓度，{@code 0.5} 为半透明。 */
    public void setBackdropAlpha(float alpha) { this.backdropAlpha = Math.max(0f, Math.min(1f, alpha)); }

    /**
     * 不使用 {@code super.render(...)}：1.21 的原版实现在渲染子控件前会先画
     * {@code renderBackground}（高斯模糊 + 暗化背景），而本模组的界面全部自绘背景。
     * 这里按 Yarn {@code Drawable} 语义只遍历子控件渲染。
     */
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 仅当界面显式开启时才自动铺底；默认与 1.20.1 一致（谁要底色谁自己调 renderBackground）
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

    /**
     * 1.20.1 同款灰底，只在被显式调用时画一层（与 1.20.1 的 {@code renderBackground} 语义一致，
     * 来回调两次就会叠加变暗）。关卡内是原版那层暗色半透明渐变（世界仍隐约可见），
     * 没有世界时铺一层不透明灰。
     *
     * <p>1.21 的原版 {@code renderBackground} 会额外叠加高斯模糊，故这里自行绘制、不调 super。
     */
    public void renderBackground(DrawContext context) {
        if (Minecraft.getInstance().level != null) {
            context.fillGradient(0, 0, this.width, this.height,
                    applyAlpha(BACKDROP_TOP), applyAlpha(BACKDROP_BOTTOM));
        } else {
            context.fill(0, 0, this.width, this.height, applyAlpha(BACKDROP_GREY));
        }
    }

    /** 按 {@link #backdropAlpha} 缩放颜色的 alpha 通道。 */
    private int applyAlpha(int argb) {
        if (this.backdropAlpha >= 1.0f) {
            return argb;
        }
        int alpha = Math.round(((argb >>> 24) & 0xFF) * this.backdropAlpha);
        return (alpha << 24) | (argb & 0x00FFFFFF);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return mouseScrolledCompat(mouseX, mouseY, scrollY);
    }

    /** Version-neutral scroll hook used by the shared screens. */
    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount, amount);
    }
}
