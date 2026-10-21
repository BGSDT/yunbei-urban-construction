package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class EntityBlockCompat extends EntityBlockMapper {
    protected EntityBlockCompat(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    protected abstract BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state);
}
