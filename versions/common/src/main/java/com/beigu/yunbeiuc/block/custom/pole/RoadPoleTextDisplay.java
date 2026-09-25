package com.beigu.yunbeiuc.block.custom.pole;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.beigu.yunbeiuc.block.custom.sign.CustomTextDisplayBlock;
import com.beigu.yunbeiuc.entity.RoadPoleTextDisplayEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

public class RoadPoleTextDisplay extends CustomTextDisplayBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_N = Shapes.join(Block.box(5, -4, -8, 11, 6, 24), Block.box(6, 6, 0, 10, 10, 16), BooleanOp.OR);
    private static final VoxelShape SHAPE_E = Shapes.join(Block.box(-8, -4, 5, 24, 6, 11), Block.box(0, 6, 6, 16, 10, 10), BooleanOp.OR);
    private static final VoxelShape SHAPE_S = Shapes.join(Block.box(5, -4, -8, 11, 6, 24), Block.box(6, 6, 0, 10, 10, 16), BooleanOp.OR);
    private static final VoxelShape SHAPE_W = Shapes.join(Block.box(-8, -4, 5, 24, 6, 11), Block.box(0, 6, 6, 16, 10, 10), BooleanOp.OR);

    public RoadPoleTextDisplay(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RoadPoleTextDisplayEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
