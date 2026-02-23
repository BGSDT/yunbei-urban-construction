package com.beigu.yunbeiuc.block.custom.railings;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

public class RoadRailingsOblique extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(2.0983495705504476, 0, 9.098349570550447, 18.098349570550447, 20, 15.598349570550447);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(-2.0983495705504476, 0, 0.401650429449553, 13.901650429449553, 20, 6.901650429449553);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(9.098349570550447, 0, -2.0983495705504476, 15.598349570550447, 20, 13.901650429449553);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(0.401650429449553, 0, 2.0983495705504476, 6.901650429449553, 20, 15.598349570550447);

    public RoadRailingsOblique(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
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
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
}