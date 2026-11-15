package com.beigu.yunbeiuc.api.mapper;

import static net.minecraft.world.level.block.state.BlockBehaviour.simpleCodec;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.ApiCompat;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
public abstract class DirectionalBlockCompat extends DirectionalBlock implements BlockCompatHooks {
    protected DirectionalBlockCompat(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        return useCompat(state, level, pos, player, hand, hit);
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

    private static final class Fallback extends DirectionalBlock {
        @Override
        protected MapCodec<? extends net.minecraft.world.level.block.DirectionalBlock> codec() {
            return FALLBACK_CODEC;
        }

        private Fallback(BlockBehaviour.Properties properties) {
            super(properties);
        }

    }

    protected BlockState updateShapeCompat(BlockState state, Direction direction, BlockState neighborState,
                                           LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos pos,
                                  Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return updateShapeCompat(state, direction, neighborState, (LevelAccessor) world, pos, neighborPos);
    }

    protected void onRemoveCompat(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
        onRemoveCompat(state, level, pos, state, moved);
    }

    protected void neighborChangedCompat(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block,
                                         BlockPos fromPos, boolean isMoving) {
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block,
                                   net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
        neighborChangedCompat(state, level, pos, block, pos, isMoving);
    }

    private static final MapCodec<DirectionalBlockCompat.Fallback> FALLBACK_CODEC = simpleCodec(DirectionalBlockCompat.Fallback::new);

    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.DirectionalBlock> codec() {
        return FALLBACK_CODEC;
    }
}
