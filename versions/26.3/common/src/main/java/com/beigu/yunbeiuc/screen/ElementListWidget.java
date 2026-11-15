package com.beigu.yunbeiuc.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends ObjectSelectionList<E> {
    protected final Minecraft client;
    protected int left;

    private static int lastMouseX;
    private static int lastMouseY;

    protected ElementListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, bottom - top, top, itemHeight);
        this.client = client;
        this.left = 0;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        super.extractWidgetRenderState(extractor, mouseX, mouseY, delta);
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
    protected void extractSelection(GuiGraphicsExtractor extractor, E entry, int index) {
        if (!this.renderSelection) return;
        super.extractSelection(extractor, entry, index);
    }

    public void setRenderBackground(boolean visible) { this.renderListBackground = visible; }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor extractor) {
        if (!this.renderListBackground) return;
        super.extractListBackground(extractor);
    }

    protected int getScrollbarPositionXCompat() { return super.getRowRight() + 4; }

    public void setRenderHorizontalShadows(boolean visible) { }

    @Override
    protected int scrollBarX() { return getScrollbarPositionXCompat(); }

    public abstract static class Entry<E extends Entry<E>> extends ObjectSelectionList.Entry<E> {
        @Override
        public final void extractContent(GuiGraphicsExtractor extractor, int mouseX, int mouseY, boolean hovered, float delta) {
            render(new DrawContext(extractor), 0, getContentY(), getContentX(), getWidth(), getHeight(),
                    mouseX, mouseY, hovered, delta);
        }

        public abstract void render(DrawContext context, int index, int y, int x, int entryWidth,
                                    int entryHeight, int mouseX, int mouseY, boolean hovered, float delta);

        public List<? extends ClickableWidget> children() { return List.of(); }
        public List<? extends Selectable> selectableChildren() { return List.of(); }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            return mouseClicked(event.x(), event.y(), event.button());
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (ClickableWidget child : children()) {
                if (child.mouseClicked(new net.minecraft.client.input.MouseButtonEvent(mouseX, mouseY,
                        new net.minecraft.client.input.MouseButtonInfo(button + 1, 0)), false)) return true;
            }
            return false;
        }

        @Override
        public Component getNarration() { return Component.empty(); }
    }
}
