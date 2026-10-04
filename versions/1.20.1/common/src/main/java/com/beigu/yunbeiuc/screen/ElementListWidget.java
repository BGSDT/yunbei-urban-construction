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
        super(client, width, height, top, bottom, itemHeight);
        this.client = client;
        this.left = 0;
    }

    /**
     * 同步 Yarn {@code EntryListWidget#setLeftPos} 语义：Mojang 的 {@code AbstractSelectionList}
     * 用它设置裁剪窗口的 x0/x1，而共享代码通过 {@code left} 计算行位置，两者必须保持一致。
     */
    @Override
    public void setLeftPos(int left) {
        super.setLeftPos(left);
        this.left = left;
    }

    /** Yarn 的 {@code updateSize} 会把 left 归零，这里同步保持两边一致。 */
    @Override
    public void updateSize(int width, int height, int top, int bottom) {
        super.updateSize(width, height, top, bottom);
        this.left = 0;
    }

    /**
     * 共享界面覆盖本方法来自定义滚动条位置。
     *
     * <p>名字故意带 {@code *Compat} 后缀，<b>不能</b>叫 {@code getScrollbarPositionX}：
     * 那是 {@code method_25329}（即下面 {@code getScrollbarPosition}）在 Yarn 里的名字。
     * 重映射到 Yarn 时该 override 会被改名成 {@code getScrollbarPositionX}，与同名同签名的方法撞车。
     */
    protected int getScrollbarPositionXCompat() { return super.getRowRight() + 4; }
    public void setRenderHorizontalShadows(boolean visible) { super.setRenderTopAndBottom(visible); }

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
