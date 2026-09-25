package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** Shared implementation hook for scheduled entity-block ticks. */
public abstract class TickingEntityBlock extends TickingEntityBlockMapper {
    protected TickingEntityBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected abstract void tickCompat(BlockState state, ServerLevel level, BlockPos pos);
}
