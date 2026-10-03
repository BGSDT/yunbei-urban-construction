package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/** Version-neutral base for shared items: routes the vanilla tooltip callback to the compat hook. */
public abstract class ItemCompat extends Item implements BlockCompatHooks {
    protected ItemCompat(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        appendHoverTextCompat(stack, tooltip, flag);
    }

    public static void hurtAndBreakCompat(ItemStack stack, int amount, Player player, InteractionHand hand) {
        stack.hurtAndBreak(amount, player, p -> p.broadcastBreakEvent(hand));
    }

    @org.jetbrains.annotations.Nullable
    public static net.minecraft.nbt.CompoundTag getTagCompat(ItemStack stack) {
        return stack.getTag();
    }

    public static void updateTagCompat(ItemStack stack, java.util.function.Consumer<net.minecraft.nbt.CompoundTag> action) {
        action.accept(stack.getOrCreateTag());
    }
}

