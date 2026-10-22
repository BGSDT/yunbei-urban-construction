package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.Nullable;

public class RoadMirroredBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public RoadMirroredBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(RoadOrientation.FACING, Direction.NORTH)
            .setValue(RoadOrientation.MIRRORED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RoadOrientation.FACING, RoadOrientation.MIRRORED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(RoadOrientation.FACING, rotation.rotate(state.getValue(RoadOrientation.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }
        return state
            .setValue(RoadOrientation.FACING, mirror.mirror(state.getValue(RoadOrientation.FACING)))
            .setValue(RoadOrientation.MIRRORED, !state.getValue(RoadOrientation.MIRRORED));
    }

    private static BlockState mirrorPreferring(BlockState state, Direction.Axis axis) {
        final Mirror primary = AxisMirrorOriented.ofAxis(axis);
        BlockState result = state.mirror(primary);
        if (result.equals(state)) {
            result = state.mirror(primary == Mirror.FRONT_BACK ? Mirror.LEFT_RIGHT : Mirror.FRONT_BACK);
        }
        return result;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final Player player = context.getPlayer();
        BlockState state = this.defaultBlockState()
            .setValue(RoadOrientation.FACING, context.getHorizontalDirection().getOpposite());
        if (player != null && player.isCrouching()) {
            state = mirrorPreferring(state, player.getDirection().getAxis());
        }
        return state;
    }
}
