package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Version-neutral base for entity blocks whose factory signature changed after 1.16.5. */
public abstract class EntityBlockCompat extends EntityBlockMapper {
    protected EntityBlockCompat(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    protected abstract BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state);
}
