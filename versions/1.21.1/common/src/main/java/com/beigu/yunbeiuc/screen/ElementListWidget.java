package com.beigu.yunbeiuc.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends ObjectSelectionList<E> {
    protected final Minecraft client;
    protected int left;

    protected ElementListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        // 1.21 takes (minecraft, width, height, y, itemHeight): the old top/bottom window becomes y + height.
        super(client, width, bottom - top, top, itemHeight);
        this.client = client;
        this.left = 0;
    }

    /**
     * 同步 Yarn {@code EntryListWidget#setLeftPos} 语义：共享代码通过 {@code left} 计算行位置。
     */
    public void setLeftPos(int left) {
        this.left = left;
    }

    /** Yarn 的 {@code updateSize} 会把 left 归零，这里同步保持两边一致。 */
    public void updateSize(int width, int height, int top, int bottom) {
        super.updateSizeAndPosition(width, bottom - top, top);
        this.left = 0;
    }

    private boolean renderSelection = true;
    private boolean renderListBackground = true;

    /**
     * 1.21 去掉了原版的选中框开关，但原版框仍会按「x0 + (width - rowWidth)/2」居中绘制，
     * 而共享代码的行布局是以 {@code left}（{@link #getRowLeft()}）为基准左对齐的，
     * 两者必然错位（框的左边贴不紧选项左边）。
     *
     * <p>共享代码调用本方法关闭原版框、由 {@code Entry} 自绘选中/悬停底色；
     * 这里用字段承接，并在 {@link #renderSelection} 中真正拦下绘制。
     */
    public void setRenderSelection(boolean visible) { this.renderSelection = visible; }

    @Override
    protected void renderSelection(GuiGraphics graphics, int top, int height, int left, int width, int color) {
        if (!this.renderSelection) return;
        super.renderSelection(graphics, top, height, left, width, color);
    }

    /** 1.21 同样没有列表底色开关，用字段承接共享代码的调用。 */
    public void setRenderBackground(boolean visible) { this.renderListBackground = visible; }

    @Override
    protected void renderListBackground(GuiGraphics graphics) {
        if (!this.renderListBackground) return;
        super.renderListBackground(graphics);
    }

    /**
     * 共享界面覆盖本方法来自定义滚动条位置。
     *
     * <p>名字故意带 {@code *Compat} 后缀，<b>不能</b>叫 {@code getScrollbarPositionX}：
     * 那是 {@code method_25329}（即下面 {@code getScrollbarPosition}）在 Yarn 里的名字。
     * 重映射到 Yarn 时该 override 会被改名成 {@code getScrollbarPositionX}，与同名同签名的方法撞车。
     */
    protected int getScrollbarPositionXCompat() { return super.getRowRight() + 4; }
    public void setRenderHorizontalShadows(boolean visible) { }

    @Override
    protected int getScrollbarPosition() { return getScrollbarPositionXCompat(); }

    public abstract static class Entry<E extends Entry<E>> extends ObjectSelectionList.Entry<E> {
        @Override
        public final void render(GuiGraphics graphics, int index, int y, int x, int entryWidth,
                                 int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            render(new DrawContext(graphics), index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
        }

        public abstract void render(DrawContext context, int index, int y, int x, int entryWidth,
                                    int entryHeight, int mouseX, int mouseY, boolean hovered, float delta);

        public List<? extends ClickableWidget> children() { return List.of(); }
        public List<? extends Selectable> selectableChildren() { return List.of(); }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (ClickableWidget child : children()) {
                if (child.mouseClicked(mouseX, mouseY, button)) return true;
            }
            return false;
        }

        @Override
        public Component getNarration() { return Component.empty(); }
    }
}
