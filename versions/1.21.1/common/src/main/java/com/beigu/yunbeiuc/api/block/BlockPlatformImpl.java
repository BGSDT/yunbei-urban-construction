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
    @Override public void scheduleBlockTick(LevelAccessor level, BlockPos pos, Block block, int delay) { level.scheduleTick(pos, block, delay); }
    @Override public void scheduleFluidTick(LevelAccessor level, BlockPos pos, Fluid fluid, int delay) { level.scheduleTick(pos, fluid, delay); }
    @Override public boolean growOakTree(ServerLevel level, BlockPos pos) {
        return new OakTreeGrower().growTree(level, level.getChunkSource().getGenerator(), pos,
                Blocks.OAK_SAPLING.defaultBlockState(), level.getRandom());
    }
    @Override public BlockBehaviour.Properties color(BlockBehaviour.Properties properties, MaterialColor color) { return properties.mapColor(color); }
    @Override public int updateAll() { return Block.UPDATE_ALL; }
    @Override public int updateImmediate() { return Block.UPDATE_IMMEDIATE; }
    @Override public int updateNeighbors() { return Block.UPDATE_NEIGHBORS; }
    @Override public int updateClients() { return Block.UPDATE_CLIENTS; }
}
