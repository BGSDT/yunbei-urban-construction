package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/** 1.21.1 waterlogged-slab base: keeps a double slab from being waterlogged. */
public abstract class WaterloggedSlabCompat extends BlockCompat implements SimpleWaterloggedBlock {
    protected WaterloggedSlabCompat(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canPlaceLiquid(Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE
                && SimpleWaterloggedBlock.super.canPlaceLiquid(player, level, pos, state, fluid);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return state.getValue(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE
                && SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }
}
