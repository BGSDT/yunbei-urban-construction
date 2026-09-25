package com.beigu.yunbeiuc.block.custom.pole;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import java.util.stream.Stream;

public class RoadDetectionCamera extends Block {
    private static final VoxelShape SHAPE_N = Shapes.join(Block.box(5.5, 5.5, 0, 10.5, 10, 16), Block.box(1.75, 10, 5, 14.25, 18, 11), BooleanOp.OR);
    private static final VoxelShape SHAPE_S = Shapes.join(Block.box(5.5, 5.5, 0, 10.5, 10, 16), Block.box(1.75, 10, 5, 14.25, 18, 11), BooleanOp.OR);
    private static final VoxelShape SHAPE_E = Shapes.join(Block.box(0, 5.5, 5.5, 16, 10.5, 10.5), Block.box(5, 10, 1.75, 11, 18, 14.25), BooleanOp.OR);
    private static final VoxelShape SHAPE_W = Shapes.join(Block.box(0, 5.5, 5.5, 16, 10.5, 10.5), Block.box(5, 10, 1.75, 11, 18, 14.25), BooleanOp.OR);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public RoadDetectionCamera(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case WEST -> SHAPE_W;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }
}
