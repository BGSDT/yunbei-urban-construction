package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public interface AxisMirrorOriented {
    enum Mode {
        PARALLEL, PERPENDICULAR, CORNER
    }

    Mode axisMirrorMode();

    static boolean perpendicular(Mode mode) {
        return mode != Mode.PARALLEL;
    }

    static BlockState encode(BlockState state, Mode mode, Direction direction) {
        final Direction axisDirection = perpendicular(mode) ? direction.getClockWise() : direction;
        final boolean mirrored = direction == Direction.SOUTH || direction == Direction.WEST;
        return state
            .setValue(RoadOrientation.AXIS, axisDirection.getAxis())
            .setValue(RoadOrientation.MIRRORED, mirrored);
    }

    static Direction decode(BlockState state, Mode mode) {
        final Direction.Axis axis = state.getValue(RoadOrientation.AXIS);
        final boolean mirrored = state.getValue(RoadOrientation.MIRRORED);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            final Direction axisDirection = perpendicular(mode) ? direction.getClockWise() : direction;
            if (axisDirection.getAxis() == axis
                && (direction == Direction.SOUTH || direction == Direction.WEST) == mirrored) {
                return direction;
            }
        }
        return Direction.NORTH;
    }

    static BlockState rotate(BlockState state, Mode mode, Rotation rotation) {
        return encode(state, mode, rotation.rotate(decode(state, mode)));
    }

    static BlockState mirror(BlockState state, Mode mode, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }
        final Direction.Axis axis = state.getValue(RoadOrientation.AXIS);
        final boolean mirrored = state.getValue(RoadOrientation.MIRRORED);
        if (mode == Mode.CORNER) {
            final Direction.Axis flipped = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return state
                .setValue(RoadOrientation.AXIS, flipped)
                .setValue(RoadOrientation.MIRRORED, mirror == Mirror.FRONT_BACK ? mirrored : !mirrored);
        }
        final Direction.Axis mirrorAxis = mirror == Mirror.FRONT_BACK ? Direction.Axis.Z : Direction.Axis.X;
        if (mirrorAxis == axis) {
            return state;
        }
        return state.setValue(RoadOrientation.MIRRORED, !mirrored);
    }

    static BlockState mirrorPreferring(BlockState state, Mode mode, Mirror primary) {
        BlockState result = mirror(state, mode, primary);
        if (result.equals(state)) {
            result = mirror(state, mode, primary == Mirror.FRONT_BACK ? Mirror.LEFT_RIGHT : Mirror.FRONT_BACK);
        }
        return result;
    }

    static Mirror ofAxis(Direction.Axis axis) {
        return axis == Direction.Axis.X ? Mirror.FRONT_BACK : Mirror.LEFT_RIGHT;
    }

    static Direction alignToNeighbor(Direction direction, Mode mode, BlockPlaceContext context) {
        final BlockPos clicked = context.replacingClickedOnBlock()
            ? context.getClickedPos()
            : context.getClickedPos().relative(context.getClickedFace().getOpposite());
        final BlockState neighbor = context.getLevel().getBlockState(clicked);
        if (!neighbor.hasProperty(RoadOrientation.AXIS)) {
            return direction;
        }
        final Direction.Axis wanted = neighbor.getValue(RoadOrientation.AXIS);
        final Direction.Axis current = (perpendicular(mode) ? direction.getClockWise() : direction).getAxis();
        return current == wanted ? direction : direction.getClockWise();
    }
}
