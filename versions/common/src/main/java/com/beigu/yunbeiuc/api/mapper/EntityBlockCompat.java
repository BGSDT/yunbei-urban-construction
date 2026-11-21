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

    /** 创建本方块对应的方块实体（供跨版本区块/世界迁移使用）。 */
    public final BlockEntity createBlockEntityCompat(BlockPos pos, BlockState state) {
        return newBlockEntityCompat(pos, state);
    }

    @Nullable
    @Override
    protected abstract BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state);
}
