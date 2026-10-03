package com.beigu.yunbeiuc.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class BlockPlatformImpl implements BlockPlatform {
    @Override public boolean isWater(FluidState state) { return state.getType() == Fluids.WATER; }
    @Override public void scheduleBlockTick(LevelAccessor level, BlockPos pos, Block block, int delay) { level.scheduleTick(pos, block, delay); }
    @Override public void scheduleFluidTick(LevelAccessor level, BlockPos pos, Fluid fluid, int delay) { level.scheduleTick(pos, fluid, delay); }
    @Override public boolean growOakTree(ServerLevel level, BlockPos pos) {
        return TreeGrower.OAK.growTree(level, level.getChunkSource().getGenerator(), pos,
                Blocks.OAK_SAPLING.defaultBlockState(), level.getRandom());
    }
    @Override public BlockBehaviour.Properties copyProperties(Block source) { return BlockBehaviour.Properties.ofFullCopy(source); }
    @Override public BlockBehaviour.Properties color(BlockBehaviour.Properties properties, BlockColor color) {
        return properties.mapColor(mapColor(color));
    }

    private static MapColor mapColor(BlockColor color) {
        return switch (color) {
            case NONE -> MapColor.NONE;
            case GRASS -> MapColor.GRASS;
            case SAND -> MapColor.SAND;
            case WOOL -> MapColor.WOOL;
            case FIRE -> MapColor.FIRE;
            case ICE -> MapColor.ICE;
            case METAL -> MapColor.METAL;
            case PLANT -> MapColor.PLANT;
            case SNOW -> MapColor.SNOW;
            case CLAY -> MapColor.CLAY;
            case DIRT -> MapColor.DIRT;
            case STONE -> MapColor.STONE;
            case WATER -> MapColor.WATER;
            case WOOD -> MapColor.WOOD;
            case QUARTZ -> MapColor.QUARTZ;
            case COLOR_ORANGE -> MapColor.COLOR_ORANGE;
            case COLOR_MAGENTA -> MapColor.COLOR_MAGENTA;
            case COLOR_LIGHT_BLUE -> MapColor.COLOR_LIGHT_BLUE;
            case COLOR_YELLOW -> MapColor.COLOR_YELLOW;
            case COLOR_LIGHT_GREEN -> MapColor.COLOR_LIGHT_GREEN;
            case COLOR_PINK -> MapColor.COLOR_PINK;
            case COLOR_GRAY -> MapColor.COLOR_GRAY;
            case COLOR_LIGHT_GRAY -> MapColor.COLOR_LIGHT_GRAY;
            case COLOR_CYAN -> MapColor.COLOR_CYAN;
            case COLOR_PURPLE -> MapColor.COLOR_PURPLE;
            case COLOR_BLUE -> MapColor.COLOR_BLUE;
            case COLOR_BROWN -> MapColor.COLOR_BROWN;
            case COLOR_GREEN -> MapColor.COLOR_GREEN;
            case COLOR_RED -> MapColor.COLOR_RED;
            case COLOR_BLACK -> MapColor.COLOR_BLACK;
            case GOLD -> MapColor.GOLD;
            case DIAMOND -> MapColor.DIAMOND;
            case LAPIS -> MapColor.LAPIS;
            case EMERALD -> MapColor.EMERALD;
            case PODZOL -> MapColor.PODZOL;
            case NETHER -> MapColor.NETHER;
            case TERRACOTTA_WHITE -> MapColor.TERRACOTTA_WHITE;
            case TERRACOTTA_ORANGE -> MapColor.TERRACOTTA_ORANGE;
            case TERRACOTTA_MAGENTA -> MapColor.TERRACOTTA_MAGENTA;
            case TERRACOTTA_LIGHT_BLUE -> MapColor.TERRACOTTA_LIGHT_BLUE;
            case TERRACOTTA_YELLOW -> MapColor.TERRACOTTA_YELLOW;
            case TERRACOTTA_LIGHT_GREEN -> MapColor.TERRACOTTA_LIGHT_GREEN;
            case TERRACOTTA_PINK -> MapColor.TERRACOTTA_PINK;
            case TERRACOTTA_GRAY -> MapColor.TERRACOTTA_GRAY;
            case TERRACOTTA_LIGHT_GRAY -> MapColor.TERRACOTTA_LIGHT_GRAY;
            case TERRACOTTA_CYAN -> MapColor.TERRACOTTA_CYAN;
            case TERRACOTTA_PURPLE -> MapColor.TERRACOTTA_PURPLE;
            case TERRACOTTA_BLUE -> MapColor.TERRACOTTA_BLUE;
            case TERRACOTTA_BROWN -> MapColor.TERRACOTTA_BROWN;
            case TERRACOTTA_GREEN -> MapColor.TERRACOTTA_GREEN;
            case TERRACOTTA_RED -> MapColor.TERRACOTTA_RED;
            case TERRACOTTA_BLACK -> MapColor.TERRACOTTA_BLACK;
            case CRIMSON_NYLIUM -> MapColor.CRIMSON_NYLIUM;
            case CRIMSON_STEM -> MapColor.CRIMSON_STEM;
            case CRIMSON_HYPHAE -> MapColor.CRIMSON_HYPHAE;
            case WARPED_NYLIUM -> MapColor.WARPED_NYLIUM;
            case WARPED_STEM -> MapColor.WARPED_STEM;
            case WARPED_HYPHAE -> MapColor.WARPED_HYPHAE;
            case WARPED_WART_BLOCK -> MapColor.WARPED_WART_BLOCK;
        };
    }
    @Override public int updateAll() { return Block.UPDATE_ALL; }
    @Override public int updateImmediate() { return Block.UPDATE_IMMEDIATE; }
    @Override public int updateNeighbors() { return Block.UPDATE_NEIGHBORS; }
    @Override public int updateClients() { return Block.UPDATE_CLIENTS; }
}
