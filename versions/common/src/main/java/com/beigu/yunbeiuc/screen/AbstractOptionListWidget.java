package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractOptionListWidget<O> extends ElementListWidget<AbstractOptionListWidget<O>.Entry> {
    private static final RenderPlatform RENDER = VersionServices.render();

    /**
     * 列表条目左侧展示的图标（贴图 + 贴图自身的实际像素宽高，用于按原始比例绘制）。
     */
    public record Icon(ResourceLocation texture, int width, int height) {
    }

    private final int listWidth;
    private final Predicate<O> isSelected;
    private final Consumer<O> onSelect;
    private final Function<O, Component> displayTextProvider;
    private final Function<O, Integer> colorProvider;
    private final Function<O, Icon> iconProvider;

    protected AbstractOptionListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight,
                                        List<O> options, Predicate<O> isSelected, Consumer<O> onSelect,
                                        Function<O, Component> displayTextProvider, Function<O, Integer> colorProvider) {
        this(client, width, height, top, bottom, itemHeight, options, isSelected, onSelect, displayTextProvider, colorProvider, null);
    }

    protected AbstractOptionListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight,
                                        List<O> options, Predicate<O> isSelected, Consumer<O> onSelect,
                                        Function<O, Component> displayTextProvider, Function<O, Integer> colorProvider,
                                        Function<O, Icon> iconProvider) {
        super(client, width, height, top, bottom, itemHeight);
        this.listWidth = width;
        this.isSelected = isSelected;
        this.onSelect = onSelect;
        this.displayTextProvider = displayTextProvider;
        this.colorProvider = colorProvider;
        this.iconProvider = iconProvider;
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
            int rowLeft = getRowLeft();
            RENDER.enableScissor(context, rowLeft, y, rowLeft + getRowWidth(), y + entryHeight);

            if (isSelected.test(option)) {
                RENDER.fill(context, x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);
            } else if (hovered) {
                RENDER.fill(context, x, y, x + entryWidth, y + entryHeight, 0x22FFFFFF);
            }

            Icon icon = iconProvider != null ? iconProvider.apply(option) : null;
            int iconAreaX = x + 5;
            int iconAreaSize = 16;
            int iconAreaY = y + (entryHeight - iconAreaSize) / 2;
            if (icon != null) {
                RENDER.drawTexture(context, icon.texture(), iconAreaX, iconAreaY, iconAreaSize, iconAreaSize,
                        0, 0, icon.width(), icon.height(), icon.width(), icon.height());
            } else {
                RENDER.fill(context, iconAreaX, iconAreaY, iconAreaX + iconAreaSize, iconAreaY + iconAreaSize,
                        0xFF000000 | colorProvider.apply(option));
                RENDER.drawBorder(context, iconAreaX, iconAreaY, iconAreaSize, iconAreaSize, 0xFFCCCCCC);
            }

            int textX = iconAreaX + iconAreaSize + 8;
            RENDER.drawText(
                    context,
                    client.font,
                    displayTextProvider.apply(option),
                    textX,
                    y + (entryHeight - 8) / 2,
                    0xFFFFFF,
                    true
            );

            RENDER.disableScissor(context);
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
