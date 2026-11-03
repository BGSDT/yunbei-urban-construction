package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public enum RoadCorner {
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),
    NORTH_WEST(Direction.NORTH, Direction.WEST),
    NORTH_EAST(Direction.NORTH, Direction.EAST),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST);

    public final Direction side1;
    public final Direction side2;

    RoadCorner(Direction side1, Direction side2) {
        this.side1 = side1;
        this.side2 = side2;
    }

    public static RoadCorner of(Direction side1, Direction side2) {
        for (RoadCorner corner : values()) {
            if ((corner.side1 == side1 && corner.side2 == side2) || (corner.side1 == side2 && corner.side2 == side1)) {
                return corner;
            }
        }
        throw new IllegalArgumentException("There is no horizontal corner direction composed of "
            + side1.getSerializedName() + " " + side2.getSerializedName() + ".");
    }

    public static RoadCorner of(Direction facing) {
        return of(facing, facing.getClockWise());
    }

    public Direction asFacing() {
        return side1.getClockWise() == side2 ? side1 : side2;
    }

    public Direction dirInAxis(Direction.Axis axis) {
        if (side1.getAxis() == axis) {
            return side1;
        }
        if (side2.getAxis() == axis) {
            return side2;
        }
        throw new IllegalStateException("Corner " + this.name() + " has no direction in axis " + axis.getSerializedName() + ".");
    }

    public boolean containsDir(Direction direction) {
        return direction == side1 || direction == side2;
    }

    public RoadCorner mirrored(Mirror mirror) {
        return of(mirror.mirror(side1), mirror.mirror(side2));
    }

    public RoadCorner mirrored(Direction direction) {
        final Mirror mirror = switch (direction.getAxis()) {
            case X -> Mirror.LEFT_RIGHT;
            case Z -> Mirror.FRONT_BACK;
            default -> Mirror.NONE;
        };
        return mirrored(mirror);
    }

    public RoadCorner rotated(Rotation rotation) {
        return of(rotation.rotate(side1), rotation.rotate(side2));
    }
}
