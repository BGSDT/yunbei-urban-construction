package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                net.minecraft.world.item.component.TooltipDisplay display,
                                java.util.function.Consumer<Component> tooltip, TooltipFlag flag) {
        List<Component> lines = new java.util.ArrayList<>();
        appendHoverTextCompat(stack, lines, flag);
        lines.forEach(tooltip);
    }

    public static void hurtAndBreakCompat(ItemStack stack, int amount, Player player, InteractionHand hand) {
        stack.hurtAndBreak(amount, player,
                hand == InteractionHand.MAIN_HAND
                        ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                        : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
    }

    @org.jetbrains.annotations.Nullable
    public static net.minecraft.nbt.CompoundTag getTagCompat(ItemStack stack) {
        net.minecraft.world.item.component.CustomData data =
                stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return data == null ? null : data.copyTag();
    }

    public static void updateTagCompat(ItemStack stack, java.util.function.Consumer<net.minecraft.nbt.CompoundTag> action) {
        net.minecraft.world.item.component.CustomData.update(
                net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, action);
    }

    protected com.beigu.yunbeiuc.api.ApiCompat.UseResult useCompat(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        return com.beigu.yunbeiuc.api.ApiCompat.UseResult.PASS;
    }

    @Override
    public net.minecraft.world.InteractionResult use(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        return switch (useCompat(level, player, hand)) {
            case SUCCESS -> com.beigu.yunbeiuc.api.ApiCompat.sidedSuccess(level.isClientSide());
            case FAIL -> net.minecraft.world.InteractionResult.FAIL;
            case PASS -> net.minecraft.world.InteractionResult.PASS;
        };
    }

    protected boolean canAttackBlockCompat(net.minecraft.world.level.block.state.BlockState state,
                                           net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos,
                                           net.minecraft.world.entity.player.Player player) {
        return true;
    }
}
