package com.beigu.yunbeiuc.api.mapper;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/** Version-neutral base for shared directional blocks. */
public abstract class DirectionalBlockCompat extends DirectionalBlock implements BlockCompatHooks {
    protected DirectionalBlockCompat(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        appendHoverTextCompat(stack, tooltip, flag);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        return toItemInteractionResult(useCompat(state, level, pos, player, hand, hit), level);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        return useCompat(state, level, pos, player, InteractionHand.MAIN_HAND, hit);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        playerWillDestroyCompat(level, pos, state, player);
        return state;
    }

    protected static ItemInteractionResult toItemInteractionResult(InteractionResult result, Level level) {
        if (result.consumesAction()) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return result == InteractionResult.FAIL
                ? ItemInteractionResult.FAIL
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    private static final MapCodec<DirectionalBlockCompat.Fallback> FALLBACK_CODEC =
            simpleCodec(DirectionalBlockCompat.Fallback::new);

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return FALLBACK_CODEC;
    }

    private static final class Fallback extends DirectionalBlock {
        private Fallback(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        protected MapCodec<? extends DirectionalBlock> codec() {
            return FALLBACK_CODEC;
        }
    }
}
