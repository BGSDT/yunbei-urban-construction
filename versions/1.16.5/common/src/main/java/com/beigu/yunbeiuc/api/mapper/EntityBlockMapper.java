package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Bridges the 1.16.5 no-position block-entity factory to the shared factory. */
public abstract class EntityBlockMapper extends BaseEntityBlock {
    protected EntityBlockMapper(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public final BlockEntity newBlockEntity(BlockGetter level) {
        return newBlockEntityCompat(BlockPos.ZERO, Blocks.AIR.defaultBlockState());
    }

    @Nullable
    protected abstract BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state);
}
