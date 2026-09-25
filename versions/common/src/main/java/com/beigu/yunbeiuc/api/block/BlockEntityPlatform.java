package com.beigu.yunbeiuc.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Block-entity type construction changed between Minecraft 1.16.5 and 1.17. */
public interface BlockEntityPlatform {
    <T extends BlockEntity> BlockEntityType<T> create(Factory<T> factory, Block... blocks);

    @FunctionalInterface
    interface Factory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }
}
