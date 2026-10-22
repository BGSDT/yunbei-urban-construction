package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RoadAxisBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public RoadAxisBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(RoadOrientation.AXIS, Direction.Axis.Z));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RoadOrientation.AXIS);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        final boolean quarterTurn = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
        return state.setValue(RoadOrientation.AXIS, RoadOrientation.turn(state.getValue(RoadOrientation.AXIS), quarterTurn));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final Direction direction = AxisMirrorOriented.alignToNeighbor(
            context.getHorizontalDirection(), AxisMirrorOriented.Mode.PARALLEL, context);
        final boolean quarterTurn = context.getPlayer() != null && context.getPlayer().isCrouching();
        return this.defaultBlockState()
            .setValue(RoadOrientation.AXIS, RoadOrientation.turn(direction.getAxis(), quarterTurn));
    }
}
