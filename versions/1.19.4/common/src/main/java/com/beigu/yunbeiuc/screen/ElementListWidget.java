package com.beigu.yunbeiuc.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class ElementListWidget<E extends ElementListWidget.Entry<E>> extends ObjectSelectionList<E> {
    protected final Minecraft client;
    protected final int left;

    protected ElementListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.client = client;
        this.left = 0;
    }

    protected int getScrollbarPositionX() { return getRowRight() + 4; }
    public void setRenderHorizontalShadows(boolean visible) { setRenderTopAndBottom(visible); }

    @Override
    protected int getScrollbarPosition() { return getScrollbarPositionX(); }

    public abstract static class Entry<E extends Entry<E>> extends ObjectSelectionList.Entry<E> {
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

        @Override
        public Component getNarration() { return Component.empty(); }
    }
}
