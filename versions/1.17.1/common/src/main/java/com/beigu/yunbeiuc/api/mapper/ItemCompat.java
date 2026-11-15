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

    protected com.beigu.yunbeiuc.api.ApiCompat.UseResult useCompat(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        return com.beigu.yunbeiuc.api.ApiCompat.UseResult.PASS;
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);
        return switch (useCompat(level, player, hand)) {
            case SUCCESS -> net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            case FAIL -> net.minecraft.world.InteractionResultHolder.fail(stack);
            case PASS -> net.minecraft.world.InteractionResultHolder.pass(stack);
        };
    }

    protected boolean canAttackBlockCompat(net.minecraft.world.level.block.state.BlockState state,
                                           net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos,
                                           net.minecraft.world.entity.player.Player player) {
        return true;
    }

    @Override
    public boolean canAttackBlock(net.minecraft.world.level.block.state.BlockState state,
                                  net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos,
                                  net.minecraft.world.entity.player.Player player) {
        return canAttackBlockCompat(state, world, pos, player);
    }
}
