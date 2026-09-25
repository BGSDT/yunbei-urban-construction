package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class TickingEntityBlockMapper extends BaseEntityBlock {
    protected TickingEntityBlockMapper(BlockBehaviour.Properties properties) { super(properties); }
    @Override public final void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) { tickCompat(state, level, pos); }
    protected abstract void tickCompat(BlockState state, ServerLevel level, BlockPos pos);
    @Nullable @Override public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return newBlockEntityCompat(pos, state); }
    @Nullable protected abstract BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state);
}
