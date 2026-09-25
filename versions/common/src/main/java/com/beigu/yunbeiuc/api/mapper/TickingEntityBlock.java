package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/** Shared implementation hook for scheduled entity-block ticks. */
public abstract class TickingEntityBlock extends TickingEntityBlockMapper {
    protected TickingEntityBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected abstract void tickCompat(BlockState state, ServerLevel level, BlockPos pos);

    @Nullable
    @Override
    protected abstract BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state);
}
