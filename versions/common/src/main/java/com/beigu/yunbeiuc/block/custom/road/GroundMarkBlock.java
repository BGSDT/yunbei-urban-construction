package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

public class GroundMarkBlock extends Block {
    private static final VoxelShape FLAT_SHAPE = Block.box(0, 0, 0, 16, 0.1, 16);
    private static final VoxelShape HANGING_SHAPE = Block.box(0, -8, 0, 16, -7.9, 16);

    public static final BooleanProperty SLAB = BooleanProperty.create("slab");

    public static final EnumProperty<EightDirection> FACING =
            EnumProperty.create("facing", EightDirection.class);

    public GroundMarkBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, EightDirection.NORTH)
            .setValue(SLAB, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SLAB);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(SLAB) ? HANGING_SHAPE : FLAT_SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockState below = ctx.getLevel().getBlockState(ctx.getClickedPos().below());
        return this.defaultBlockState()
            .setValue(FACING, EightDirection.fromYaw(ctx.getRotation()))
            .setValue(SLAB, isBottomSlab(below));
    }

    // 只有下方是「下半砖」时才下沉：bottom 半砖的顶面在 y=8，标记方块本地坐标 -8 正好落在该顶面上。
    // top 半砖与 double 的顶面都在 y=16，用原模型（0~0.1）即可贴合，无需下沉。
    private static boolean isBottomSlab(BlockState below) {
        return below.hasProperty(BlockStateProperties.SLAB_TYPE)
            && below.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            final boolean onSlab = isBottomSlab(neighborState);
            if (onSlab != state.getValue(SLAB)) {
                return state.setValue(SLAB, onSlab);
            }
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, state.getValue(FACING).rotate(rotation));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, state.getValue(FACING).mirror(mirror));
    }

    public enum EightDirection implements StringRepresentable {
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
        public String getSerializedName() {
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
            return values()[Mth.floor(angle / 45.0 + 0.5) & 7];
        }

        public static EightDirection fromYaw(float yaw) {
            float angle = (yaw % 360 + 360) % 360;
            return fromAngle(angle);
        }

        public EightDirection rotate(Rotation rotation) {
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

        public EightDirection mirror(Mirror mirror) {
            if (mirror == Mirror.NONE) {
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
