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
        super(client, width, bottom - top, top, itemHeight);
        this.client = client;
        this.left = 0;
    }

    public void setLeftPos(int left) {
        this.left = left;
    }

    public void updateSize(int width, int height, int top, int bottom) {
        super.updateSizeAndPosition(width, bottom - top, top);
        this.left = 0;
    }

    private boolean renderSelection = true;
    private boolean renderListBackground = true;

    public void setRenderSelection(boolean visible) { this.renderSelection = visible; }

    @Override
    protected void renderSelection(GuiGraphics graphics, int top, int height, int left, int width, int color) {
        if (!this.renderSelection) return;
        super.renderSelection(graphics, top, height, left, width, color);
    }

    public void setRenderBackground(boolean visible) { this.renderListBackground = visible; }

    @Override
    protected void renderListBackground(GuiGraphics graphics) {
        if (!this.renderListBackground) return;
        super.renderListBackground(graphics);
    }

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
