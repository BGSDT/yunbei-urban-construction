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

public final class BlockPlatformImpl implements BlockPlatform {
    @Override public boolean isWater(FluidState state) { return state.getType() == Fluids.WATER; }
    @Override public void scheduleBlockTick(LevelAccessor level, BlockPos pos, Block block, int delay) { level.getBlockTicks().scheduleTick(pos, block, delay); }
    @Override public void scheduleFluidTick(LevelAccessor level, BlockPos pos, Fluid fluid, int delay) { level.getLiquidTicks().scheduleTick(pos, fluid, delay); }
    @Override public boolean growOakTree(ServerLevel level, BlockPos pos) {
        return new OakTreeGrower().growTree(level, level.getChunkSource().getGenerator(), pos,
                Blocks.OAK_SAPLING.defaultBlockState(), level.random);
    }
    @Override public BlockBehaviour.Properties color(BlockBehaviour.Properties properties, BlockColor color) {
        // Properties.mapColor(MapColor) is a 1.20+ mutator; on this version the map colour can only
        // be supplied through the static of(Material, MaterialColor) factories, which cannot be
        // applied to an already-built Properties. All callers copy CYAN_TERRACOTTA, so retaining the
        // copied colour is the closest compatible form.
        return properties;
    }
    @Override public int updateAll() { return Block.UPDATE_ALL; }
    @Override public int updateImmediate() { return Block.UPDATE_IMMEDIATE; }
    @Override public int updateNeighbors() { return Block.UPDATE_NEIGHBORS; }
    @Override public int updateClients() { return Block.UPDATE_CLIENTS; }
}
