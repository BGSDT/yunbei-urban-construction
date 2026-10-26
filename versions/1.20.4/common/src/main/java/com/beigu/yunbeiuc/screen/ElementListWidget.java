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
        // 1.20.2 起 ObjectSelectionList 构造器改为 (Minecraft, width, height, top, itemHeight)，
        // bottom 不再传入，高度取 bottom - top（与 1.21.1 shim 一致）。
        super(client, width, bottom - top, top, itemHeight);
        this.client = client;
        this.left = 0;
    }

    // 1.20.2 起 AbstractSelectionList 继承 AbstractWidget，没有 setLeftPos/updateSize，
    // 分别用 setX / setRectangle(width, height, x, y) 等价实现。
    public void setLeftPos(int left) {
        setX(left);
        this.left = left;
    }

    public void updateSize(int width, int height, int top, int bottom) {
        setRectangle(width, bottom - top, 0, top);
        this.left = 0;
    }

    protected int getScrollbarPositionXCompat() { return super.getRowRight() + 4; }
    public void setRenderHorizontalShadows(boolean visible) { }

    // 1.20.2 起 AbstractSelectionList 删除了 vanilla setRenderSelection(boolean)，
    // 改由子类覆写 renderSelection 自行过滤（共享树 AbstractOptionListWidget 调用 setRenderSelection）。
    private boolean renderSelection = true;

    public void setRenderSelection(boolean visible) { this.renderSelection = visible; }

    @Override
    protected void renderSelection(GuiGraphics graphics, int top, int height, int left, int width, int color) {
        if (!this.renderSelection) return;
        super.renderSelection(graphics, top, height, left, width, color);
    }

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
