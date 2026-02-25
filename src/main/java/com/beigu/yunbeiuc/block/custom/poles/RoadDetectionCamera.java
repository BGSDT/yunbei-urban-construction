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

import java.util.stream.Stream;

public class RoadDetectionCamera extends Block {
    private static final VoxelShape SHAPE_N = Stream.of(
            Block.createCuboidShape(5.5, 5.5, 0, 10.5, 10, 16),
            Block.createCuboidShape(4.5, 10, 0, 11.5, 10.5, 16),
            Block.createCuboidShape(3.75, 10.5, 5.3, 14.7, 17.85, 10.7)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.createCuboidShape(0, 5.5, 5.5, 16, 10, 10.5),
            Block.createCuboidShape(0, 10, 4.5, 16, 10.5, 11.5),
            Block.createCuboidShape(5.3, 10.5, 3.75, 10.7, 17.85, 14.7)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.createCuboidShape(5.5, 5.5, 0, 10.5, 10, 16),
            Block.createCuboidShape(4.5, 10, 0, 11.5, 10.5, 16),
            Block.createCuboidShape(3.75, 10.5, 5.3, 14.7, 17.85, 10.7)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.createCuboidShape(0, 5.5, 5.5, 16, 10, 10.5),
            Block.createCuboidShape(0, 10, 4.5, 16, 10.5, 11.5),
            Block.createCuboidShape(5.3, 10.5, 3.75, 10.7, 17.85, 14.7)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public RoadDetectionCamera(Settings settings) {
        super(settings);
        this.setDefaultState(
                getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
        );
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case WEST -> SHAPE_W;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
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
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
}