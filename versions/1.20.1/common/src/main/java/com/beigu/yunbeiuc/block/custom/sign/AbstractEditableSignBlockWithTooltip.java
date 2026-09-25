package com.beigu.yunbeiuc.block.custom.sign;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractEditableSignBlockWithTooltip extends AbstractEditableSignBlock {
    private final String tooltipKey;

    public AbstractEditableSignBlockWithTooltip(Settings settings, String tooltipKey) {
        super(settings);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable(tooltipKey));
        super.appendTooltip(stack, world, tooltip, options);
    }
}
