package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TickingBlockMapper extends Block {
    protected TickingBlockMapper(BlockBehaviour.Properties properties) { super(properties); }
    @Override public final void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { tickCompat(state, level, pos); }
    protected abstract void tickCompat(BlockState state, ServerLevel level, BlockPos pos);
}
