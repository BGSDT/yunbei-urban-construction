package com.beigu.yunbeiuc.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.server.level.ServerLevel;

/** Stable block operations whose Minecraft method names changed between versions. */
public interface BlockPlatform {
    boolean isWater(FluidState state);

    void scheduleBlockTick(LevelAccessor level, BlockPos pos, Block block, int delay);

    void scheduleFluidTick(LevelAccessor level, BlockPos pos, Fluid fluid, int delay);

    boolean growOakTree(ServerLevel level, BlockPos pos);

}
