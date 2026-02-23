package com.beigu.yunbeiuc.block.custom.poles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class RoadPolesTshape extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_N = VoxelShapes.combineAndSimplify(Block.createCuboidShape(6, 6, 0, 10, 10, 14.5), Block.createCuboidShape(3.25, 3.25, 14.5, 12.75, 12.75, 27.5), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_S = VoxelShapes.combineAndSimplify(Block.createCuboidShape(6, 6, 0, 10, 10, 14.5), Block.createCuboidShape(3.25, 3.25, 1.5, 12.75, 12.75, 14.5), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_E = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 6, 6, 14.5, 10, 10), Block.createCuboidShape(1.5, 3.25, 3.25, 14.5, 12.75, 12.75), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_W = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 6, 6, 14.5, 10, 10), Block.createCuboidShape(1.5, 3.25, 3.25, 14.5, 12.75, 12.75), BooleanBiFunction.OR);

    public RoadPolesTshape(Settings settings) {
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