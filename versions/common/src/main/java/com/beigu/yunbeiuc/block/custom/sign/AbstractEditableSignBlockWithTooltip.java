package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractEditableSignBlockWithTooltip extends AbstractEditableSignBlock {
    private final String tooltipKey;

    public AbstractEditableSignBlockWithTooltip(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Text.translatable(tooltipKey));
        super.appendHoverText(stack, world, tooltip, options);
    }
}
