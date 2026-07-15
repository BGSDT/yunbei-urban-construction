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

public class RoadBlockRotate180 extends Block {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    private final MapColor mapColor;

    public RoadBlockRotate180(Settings settings) {
        this(settings, MapColor.TERRACOTTA_CYAN);
    }

    public RoadBlockRotate180(Settings settings, MapColor mapColor) {
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
        // 180°旋转：北→南，东→西
        return state.with(FACING, state.get(FACING).getOpposite());
    }

    // RoadBlockRotateRight.java
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction facing = state.get(FACING);

        // 判断方块朝哪个方向
        boolean isEastWest = (facing == Direction.EAST || facing == Direction.WEST);
        boolean isNorthSouth = (facing == Direction.NORTH || facing == Direction.SOUTH);

        if (isEastWest) {
            // 方块朝东西 → 只能东西镜像(LEFT_RIGHT)有效，南北镜像(FRONT_BACK)无效
            if (mirror == BlockMirror.LEFT_RIGHT) {
                return state.with(FACING, facing.getOpposite());
            }
            // FRONT_BACK → 不变
        } else if (isNorthSouth) {
            // 方块朝南北 → 只能南北镜像(FRONT_BACK)有效，东西镜像(LEFT_RIGHT)无效
            if (mirror == BlockMirror.FRONT_BACK) {
                return state.with(FACING, facing.getOpposite());
            }
            // LEFT_RIGHT → 不变
        }
        return state;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }
}