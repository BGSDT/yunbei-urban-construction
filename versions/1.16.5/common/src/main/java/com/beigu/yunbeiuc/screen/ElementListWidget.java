package com.beigu.yunbeiuc.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;

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

    protected int getScrollbarPositionX() { return getRowRight() + 4; }
    public void setRenderHorizontalShadows(boolean visible) { setRenderTopAndBottom(visible); }

    @Override
    protected int getScrollbarPosition() { return getScrollbarPositionX(); }

    public abstract static class Entry<E extends Entry<E>> extends AbstractSelectionList.Entry<E> {
        @Override
        public final void render(PoseStack matrices, int index, int y, int x, int entryWidth,
                                 int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            render(new DrawContext(matrices), index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
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

    }
}
