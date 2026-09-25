package com.beigu.yunbeiuc.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockEntityPlatformImpl implements BlockEntityPlatform {
    @Override
    public <T extends BlockEntity> BlockEntityType<T> create(Factory<T> factory, Block... blocks) {
        return BlockEntityType.Builder.of(
                () -> factory.create(BlockPos.ZERO, Blocks.AIR.defaultBlockState()), blocks).build(null);
    }
}
