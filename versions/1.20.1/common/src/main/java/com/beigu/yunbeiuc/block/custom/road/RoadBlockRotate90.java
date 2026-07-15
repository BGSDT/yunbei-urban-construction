package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class RoadBlockRotate90 extends Block {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    private final MapColor mapColor;

    public RoadBlockRotate90(Settings settings) {
        this(settings, MapColor.TERRACOTTA_CYAN);
    }

    public RoadBlockRotate90(Settings settings, MapColor mapColor) {
        super(settings);
        this.mapColor = mapColor;
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    public MapColor getMapColor(BlockState state, BlockView world, BlockPos pos) {
        return this.mapColor;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction facing = state.get(FACING);

        boolean isEastWest = (facing == Direction.EAST || facing == Direction.WEST);
        boolean isNorthSouth = (facing == Direction.NORTH || facing == Direction.SOUTH);

        if (isEastWest) {
            // 方块朝东西
            if (mirror == BlockMirror.LEFT_RIGHT) {
                // 点击东/西面 → 向南逆时针旋转90°
                return state.with(FACING, facing.rotateYCounterclockwise());
            } else if (mirror == BlockMirror.FRONT_BACK) {
                // 点击南/北面 → 向北顺时针旋转90°
                return state.with(FACING, facing.rotateYClockwise());
            }
        } else if (isNorthSouth) {
            // 方块朝南北
            if (mirror == BlockMirror.FRONT_BACK) {
                // 点击南/北面 → 向西逆时针旋转90°
                return state.with(FACING, facing.rotateYCounterclockwise());
            } else if (mirror == BlockMirror.LEFT_RIGHT) {
                // 点击东/西面 → 向东顺时针旋转90°
                return state.with(FACING, facing.rotateYClockwise());
            }
        }
        return state;
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }
}