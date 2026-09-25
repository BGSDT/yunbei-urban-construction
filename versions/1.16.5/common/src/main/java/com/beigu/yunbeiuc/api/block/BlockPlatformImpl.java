package com.beigu.yunbeiuc.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

public final class BlockPlatformImpl implements BlockPlatform {
    @Override public boolean isWater(FluidState state) { return state.getType() == Fluids.WATER; }
    @Override public void scheduleBlockTick(LevelAccessor level, BlockPos pos, Block block, int delay) { level.getBlockTicks().scheduleTick(pos, block, delay); }
    @Override public void scheduleFluidTick(LevelAccessor level, BlockPos pos, Fluid fluid, int delay) { level.getLiquidTicks().scheduleTick(pos, fluid, delay); }
    @Override public boolean growOakTree(ServerLevel level, BlockPos pos) {
        return new OakTreeGrower().growTree(level, level.getChunkSource().getGenerator(), pos,
                Blocks.OAK_SAPLING.defaultBlockState(), level.random);
    }
    @Override public BlockBehaviour.Properties color(BlockBehaviour.Properties properties, MaterialColor color) {
        // 1.16.5 only exposes map color when Properties is created. All callers copy
        // CYAN_TERRACOTTA, so retaining the copied color is the closest compatible form.
        return properties;
    }
    @Override public int updateAll() { return 3; }
    @Override public int updateImmediate() { return 8; }
    @Override public int updateNeighbors() { return 1; }
    @Override public int updateClients() { return 2; }
}
