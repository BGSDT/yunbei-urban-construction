package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public enum RoadCorner {
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),
    NORTH_WEST(Direction.NORTH, Direction.WEST),
    NORTH_EAST(Direction.NORTH, Direction.EAST),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST);

    public final Direction dir1;
    public final Direction dir2;

    RoadCorner(Direction dir1, Direction dir2) {
        this.dir1 = dir1;
        this.dir2 = dir2;
    }

    public static RoadCorner of(Direction dir1, Direction dir2) {
        for (RoadCorner corner : values()) {
            if ((corner.dir1 == dir1 && corner.dir2 == dir2) || (corner.dir1 == dir2 && corner.dir2 == dir1)) {
                return corner;
            }
        }
        throw new IllegalArgumentException("There is no horizontal corner direction composed of "
            + dir1.getSerializedName() + " " + dir2.getSerializedName() + ".");
    }

    public static RoadCorner of(Direction facing) {
        return of(facing, facing.getClockWise());
    }

    public Direction facing() {
        return dir1.getClockWise() == dir2 ? dir1 : dir2;
    }

    public Direction getDirectionInAxis(Direction.Axis axis) {
        if (dir1.getAxis() == axis) {
            return dir1;
        }
        if (dir2.getAxis() == axis) {
            return dir2;
        }
        throw new IllegalStateException("Corner " + this.name() + " has no direction in axis " + axis.getSerializedName() + ".");
    }

    public boolean hasDirection(Direction direction) {
        return direction == dir1 || direction == dir2;
    }

    public RoadCorner mirror(Mirror mirror) {
        return of(mirror.mirror(dir1), mirror.mirror(dir2));
    }

    public RoadCorner mirror(Direction direction) {
        final Mirror mirror = switch (direction.getAxis()) {
            case X -> Mirror.LEFT_RIGHT;
            case Z -> Mirror.FRONT_BACK;
            default -> Mirror.NONE;
        };
        return mirror(mirror);
    }

    public RoadCorner rotate(Rotation rotation) {
        return of(rotation.rotate(dir1), rotation.rotate(dir2));
    }
}
