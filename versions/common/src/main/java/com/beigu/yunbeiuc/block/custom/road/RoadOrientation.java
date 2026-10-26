package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public final class RoadOrientation {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Property<RoadCorner> CORNER = new RoadCornerProperty("corner");
    public static final BooleanProperty MIRRORED = BooleanProperty.create("mirrored");

    private RoadOrientation() {
    }

    public static Direction.Axis axisOf(BlockState state) {
        if (state.hasProperty(AXIS)) {
            return state.getValue(AXIS);
        }
        if (state.hasProperty(FACING)) {
            return state.getValue(FACING).getAxis();
        }
        if (state.hasProperty(CORNER)) {
            return state.getValue(CORNER).facing().getAxis();
        }
        return Direction.Axis.Z;
    }

    public static Direction directionOf(BlockState state) {
        if (state.hasProperty(AXIS) && state.hasProperty(MIRRORED)
            && state.getBlock() instanceof AxisMirrorOriented oriented) {
            return AxisMirrorOriented.decode(state, oriented.axisMirrorMode());
        }
        if (state.hasProperty(FACING)) {
            return state.getValue(FACING);
        }
        if (state.hasProperty(CORNER)) {
            return state.getValue(CORNER).facing();
        }
        return axisOf(state) == Direction.Axis.X ? Direction.EAST : Direction.NORTH;
    }

    public static BlockState align(BlockState state, Direction direction) {
        if (state.hasProperty(AXIS) && state.hasProperty(MIRRORED)
            && state.getBlock() instanceof AxisMirrorOriented oriented) {
            return AxisMirrorOriented.encode(state, oriented.axisMirrorMode(), direction);
        }
        if (state.hasProperty(CORNER)) {
            return state.setValue(CORNER, RoadCorner.of(direction));
        }
        if (state.hasProperty(AXIS)) {
            return state.setValue(AXIS, direction.getAxis());
        }
        if (state.hasProperty(FACING)) {
            return state.setValue(FACING, direction);
        }
        return state;
    }

    public static BlockState align(BlockState state, Direction direction, boolean mirrored) {
        if (state.hasProperty(FACING) && state.hasProperty(MIRRORED)
            && !(state.hasProperty(AXIS) && state.getBlock() instanceof AxisMirrorOriented)) {
            return state.setValue(FACING, direction).setValue(MIRRORED, mirrored);
        }
        return align(state, direction);
    }

    public static BlockState align(Block block, Direction direction, boolean mirrored) {
        return align(block.defaultBlockState(), direction, mirrored);
    }

    public static BlockState align(Block block, Direction direction) {
        return align(block.defaultBlockState(), direction);
    }

    public static BlockState carryOver(BlockState source, BlockState target) {
        if (target.hasProperty(AXIS) && target.hasProperty(MIRRORED)
            && target.getBlock() instanceof AxisMirrorOriented oriented) {
            return AxisMirrorOriented.encode(target, oriented.axisMirrorMode(), directionOf(source));
        }
        if (target.hasProperty(CORNER)) {
            target = target.setValue(CORNER, RoadCorner.of(directionOf(source)));
        }
        if (target.hasProperty(AXIS)) {
            target = target.setValue(AXIS, axisOf(source));
        }
        if (target.hasProperty(FACING)) {
            target = target.setValue(FACING, directionOf(source));
        }
        if (target.hasProperty(MIRRORED) && source.hasProperty(MIRRORED)) {
            target = target.setValue(MIRRORED, source.getValue(MIRRORED));
        }
        return target;
    }

    public static Direction.Axis turn(Direction.Axis axis, boolean quarterTurn) {
        if (!quarterTurn || axis == Direction.Axis.Y) {
            return axis;
        }
        return axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
    }
}
