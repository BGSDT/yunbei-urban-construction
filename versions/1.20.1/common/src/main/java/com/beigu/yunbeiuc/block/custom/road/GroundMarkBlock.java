package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class GroundMarkBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 0.1, 16);

    public static final EnumProperty<EightDirection> FACING =
            EnumProperty.of("facing", EightDirection.class);

    public GroundMarkBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, EightDirection.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        float yaw = ctx.getPlayerYaw();
        return this.getDefaultState().with(FACING, EightDirection.fromYaw(yaw));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, state.get(FACING).rotate(rotation));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING, state.get(FACING).mirror(mirror));
    }

    public enum EightDirection implements StringIdentifiable {
        NORTH(0, "north"),
        NORTH_EAST(45, "north_east"),
        EAST(90, "east"),
        SOUTH_EAST(135, "south_east"),
        SOUTH(180, "south"),
        SOUTH_WEST(225, "south_west"),
        WEST(270, "west"),
        NORTH_WEST(315, "north_west");

        private final float angle;
        private final String name;

        EightDirection(float angle, String name) {
            this.angle = angle;
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }

        public float getAngle() {
            return angle;
        }

        public boolean isDiagonal() {
            return this == NORTH_EAST || this == SOUTH_EAST ||
                    this == SOUTH_WEST || this == NORTH_WEST;
        }

        public boolean isCardinal() {
            return !isDiagonal();
        }

        public Direction getDirection() {
            return switch (this) {
                case NORTH -> Direction.NORTH;
                case SOUTH -> Direction.SOUTH;
                case EAST -> Direction.EAST;
                case WEST -> Direction.WEST;
                default -> null;
            };
        }

        public static EightDirection fromAngle(float angle) {
            return values()[MathHelper.floor(angle / 45.0 + 0.5) & 7];
        }

        public static EightDirection fromYaw(float yaw) {
            float angle = (yaw % 360 + 360) % 360;
            return fromAngle(angle);
        }

        public EightDirection rotate(BlockRotation rotation) {
            if (isCardinal()) {
                Direction dir = getDirection();
                Direction rotated = rotation.rotate(dir);
                return fromDirection(rotated);
            } else {
                int currentIndex = this.ordinal();
                int rotationSteps = switch (rotation) {
                    case NONE -> 0;
                    case CLOCKWISE_90 -> 2;
                    case CLOCKWISE_180 -> 4;
                    case COUNTERCLOCKWISE_90 -> 6;
                };
                int newIndex = (currentIndex + rotationSteps) % 8;
                return values()[newIndex];
            }
        }

        public EightDirection mirror(BlockMirror mirror) {
            if (mirror == BlockMirror.NONE) {
                return this;
            }

            return switch (mirror) {
                case LEFT_RIGHT -> switch (this) {
                    case NORTH -> NORTH;
                    case SOUTH -> SOUTH;
                    case EAST -> WEST;
                    case WEST -> EAST;
                    case NORTH_EAST -> NORTH_WEST;
                    case NORTH_WEST -> NORTH_EAST;
                    case SOUTH_EAST -> SOUTH_WEST;
                    case SOUTH_WEST -> SOUTH_EAST;
                };
                case FRONT_BACK -> switch (this) {
                    case EAST -> EAST;
                    case WEST -> WEST;
                    case NORTH -> SOUTH;
                    case SOUTH -> NORTH;
                    case NORTH_EAST -> SOUTH_EAST;
                    case SOUTH_EAST -> NORTH_EAST;
                    case NORTH_WEST -> SOUTH_WEST;
                    case SOUTH_WEST -> NORTH_WEST;
                };
                default -> this;
            };
        }

        private static EightDirection fromDirection(Direction direction) {
            return switch (direction) {
                case NORTH -> NORTH;
                case SOUTH -> SOUTH;
                case EAST -> EAST;
                case WEST -> WEST;
                default -> throw new IllegalArgumentException("Unexpected direction: " + direction);
            };
        }

        public Direction toDirection() {
            // 斜向映射到最近的正方向
            return switch (this) {
                case NORTH -> Direction.NORTH;
                case SOUTH -> Direction.SOUTH;
                case WEST -> Direction.WEST;
                case EAST -> Direction.EAST;
                case NORTH_EAST, NORTH_WEST -> Direction.NORTH;
                case SOUTH_EAST, SOUTH_WEST -> Direction.SOUTH;
            };
        }

        public int getModelRotation() {
            return Math.round(angle);
        }
    }
}