package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public interface BlockCompatHooks {
    default void appendHoverTextCompat(ItemStack stack, List<Component> tooltip, TooltipFlag flag) { }

    default InteractionResult useCompat(BlockState state, Level level, BlockPos pos, Player player,
                                        InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    default void playerWillDestroyCompat(Level level, BlockPos pos, BlockState state, Player player) { }
}
