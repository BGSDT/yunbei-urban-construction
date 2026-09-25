package com.beigu.yunbeiuc.block.custom.anti;

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

public class AntiGlareVersion extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_N = Shapes.join(Block.box(7.25, 0, 10.69508, 8.75, 12, 11.19508), Block.box(7.25, 0, 4.69508, 8.75, 12, 5.19508), BooleanOp.OR);
    private static final VoxelShape SHAPE_S = Shapes.join(Block.box(7.25, 0, 4.80492, 8.75, 12, 5.30492), Block.box(7.25, 0, 10.80492, 8.75, 12, 11.30492), BooleanOp.OR);
    private static final VoxelShape SHAPE_E = Shapes.join(Block.box(4.80492, 0, 7.25, 5.30492, 12, 8.75), Block.box(10.80492, 0, 7.25, 11.30492, 12, 8.75), BooleanOp.OR);
    private static final VoxelShape SHAPE_W = Shapes.join(Block.box(10.69508, 0, 7.25, 11.19508, 12, 8.75), Block.box(4.69508, 0, 7.25, 5.19508, 12, 8.75), BooleanOp.OR);

    public AntiGlareVersion(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
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
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }
}
