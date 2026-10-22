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

public class RoadAxisMirrorBlock extends Block implements AxisMirrorOriented {
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    private final Mode mode;

    public RoadAxisMirrorBlock(BlockBehaviour.Properties properties, Mode mode) {
        super(properties);
        this.mode = mode;
        this.registerDefaultState(AxisMirrorOriented.encode(this.stateDefinition.any(), mode, Direction.NORTH));
    }

    @Override
    public Mode axisMirrorMode() {
        return mode;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RoadOrientation.AXIS, RoadOrientation.MIRRORED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return AxisMirrorOriented.rotate(state, mode, rotation);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return AxisMirrorOriented.mirror(state, mode, mirror);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = AxisMirrorOriented.alignToNeighbor(
            context.getHorizontalDirection().getOpposite(), mode, context);
        BlockState state = AxisMirrorOriented.encode(this.defaultBlockState(), mode, direction);
        final Player player = context.getPlayer();
        if (player != null && player.isCrouching()) {
            state = AxisMirrorOriented.mirrorPreferring(state, mode,
                AxisMirrorOriented.ofAxis(player.getDirection().getAxis()));
        }
        return state;
    }
}
