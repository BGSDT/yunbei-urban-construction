package com.beigu.yunbeiuc.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractOptionListWidget<O> extends ElementListWidget<AbstractOptionListWidget<O>.Entry> {
    private final int listWidth;
    private final Predicate<O> isSelected;
    private final Consumer<O> onSelect;
    private final Function<O, Text> displayTextProvider;
    private final Function<O, Integer> colorProvider;

    protected AbstractOptionListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight,
                                        List<O> options, Predicate<O> isSelected, Consumer<O> onSelect,
                                        Function<O, Text> displayTextProvider, Function<O, Integer> colorProvider) {
        super(client, width, height, top, bottom, itemHeight);
        this.listWidth = width;
        this.isSelected = isSelected;
        this.onSelect = onSelect;
        this.displayTextProvider = displayTextProvider;
        this.colorProvider = colorProvider;
        for (O option : options) this.addEntry(new Entry(option));
    }

    @Override
    public int getRowWidth() {
        return this.listWidth - 25;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.getRowLeft() + this.getRowWidth() + 4;
    }

    @Override
    public int getRowLeft() {
        return this.left + 5;
    }

    @Override
    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    public class Entry extends ElementListWidget.Entry<Entry> {
        private final O option;

        public Entry(O option) {
            this.option = option;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (isSelected.test(option)) {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);
            } else if (hovered) {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x22FFFFFF);
            }

            int colorSize = 16;
            int colorX = x + 5;
            int colorY = y + (entryHeight - colorSize) / 2;
            context.fill(colorX, colorY, colorX + colorSize, colorY + colorSize, 0xFF000000 | colorProvider.apply(option));
            context.drawBorder(colorX, colorY, colorSize, colorSize, 0xFFCCCCCC);

            int textX = colorX + colorSize + 8;
            context.drawTextWithShadow(
                    client.textRenderer,
                    displayTextProvider.apply(option),
                    textX,
                    y + (entryHeight - 8) / 2,
                    0xFFFFFF
            );
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            onSelect.accept(option);
            return true;
        }

        @Override
        public List<ClickableWidget> selectableChildren() {
            return List.of();
        }

        @Override
        public List<ClickableWidget> children() {
            return List.of();
        }
    }
}
