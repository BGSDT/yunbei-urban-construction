package com.beigu.yunbeiuc.block.custom.road;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.mapper.WaterloggedSlabCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRoadSlabBlock extends WaterloggedSlabCompat {
    public enum Kind {
        NONE, AXIS, AXIS_MIRRORED, FACING, CORNER, MIRRORED
    }

    public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape BOTTOM_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    protected static final VoxelShape TOP_SHAPE = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

    private final AxisMirrorOriented.Mode axisMirrorMode;

    protected AbstractRoadSlabBlock(BlockBehaviour.Properties properties) {
        this(properties, AxisMirrorOriented.Mode.PARALLEL);
    }

    protected AbstractRoadSlabBlock(BlockBehaviour.Properties properties, AxisMirrorOriented.Mode axisMirrorMode) {
        super(properties);
        this.axisMirrorMode = axisMirrorMode;
        final Kind kind = kind();
        BlockState state = this.stateDefinition.any().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, false);
        if (kind == Kind.AXIS_MIRRORED) {
            state = AxisMirrorOriented.encode(state, axisMirrorMode, Direction.NORTH);
        } else if (kind == Kind.AXIS) {
            state = state.setValue(RoadOrientation.AXIS, Direction.Axis.Z);
        } else if (kind == Kind.FACING || kind == Kind.MIRRORED) {
            state = state.setValue(RoadOrientation.FACING, Direction.NORTH);
        } else if (kind == Kind.CORNER) {
            state = state.setValue(RoadOrientation.CORNER, RoadCorner.NORTH_EAST);
        }
        if (kind == Kind.MIRRORED) {
            state = state.setValue(RoadOrientation.MIRRORED, false);
        }
        this.registerDefaultState(state);
    }

    protected abstract Kind kind();

    protected AxisMirrorOriented.Mode axisMirrorMode() {
        return axisMirrorMode;
    }

    protected BlockState alignForPlacement(BlockState state, BlockPlaceContext context) {
        final Player player = context.getPlayer();
        final boolean crouching = player != null && player.isCrouching();
        return switch (kind()) {
            case AXIS_MIRRORED -> {
                state = AxisMirrorOriented.encode(state, axisMirrorMode(),
                    AxisMirrorOriented.alignToNeighbor(
                        context.getHorizontalDirection().getOpposite(), axisMirrorMode(), context));
                if (player != null && player.isCrouching()) {
                    state = AxisMirrorOriented.mirrorPreferring(state, axisMirrorMode(),
                        AxisMirrorOriented.ofAxis(player.getDirection().getAxis()));
                }
                yield state;
            }
            case AXIS -> {
                Direction direction = AxisMirrorOriented.alignToNeighbor(
                    context.getHorizontalDirection().getOpposite(), AxisMirrorOriented.Mode.PARALLEL, context);
                yield state.setValue(RoadOrientation.AXIS,
                    RoadOrientation.turn(direction.getAxis(), crouching));
            }
            case FACING -> state.setValue(RoadOrientation.FACING, context.getHorizontalDirection().getOpposite());
            case CORNER -> state.setValue(RoadOrientation.CORNER, RoadCorner.of(context.getHorizontalDirection().getOpposite()));
            case MIRRORED -> state
                .setValue(RoadOrientation.FACING, context.getHorizontalDirection().getOpposite())
                .setValue(RoadOrientation.MIRRORED, crouching);
            default -> state;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, WATERLOGGED);
        switch (kind()) {
            case AXIS -> builder.add(RoadOrientation.AXIS);
            case AXIS_MIRRORED -> builder.add(RoadOrientation.AXIS, RoadOrientation.MIRRORED);
            case FACING, MIRRORED -> builder.add(RoadOrientation.FACING);
            case CORNER -> builder.add(RoadOrientation.CORNER);
            default -> {
            }
        }
        if (kind() == Kind.MIRRORED) {
            builder.add(RoadOrientation.MIRRORED);
        }
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(TYPE) != SlabType.DOUBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(TYPE)) {
            case DOUBLE -> Shapes.block();
            case TOP -> TOP_SHAPE;
            default -> BOTTOM_SHAPE;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final BlockState existing = context.getLevel().getBlockState(pos);
        if (existing.is(this)) {
            return existing.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, false);
        }
        final BlockState placed = this.alignForPlacement(this.defaultBlockState(), context)
            .setValue(WATERLOGGED, VersionServices.blocks().isWater(context.getLevel().getFluidState(pos)));
        final Direction face = context.getClickedFace();
        final boolean upperHalf = face == Direction.UP
            || (face != Direction.DOWN && context.getClickLocation().y - (double) pos.getY() > 0.5);
        return placed.setValue(TYPE, upperHalf ? SlabType.TOP : SlabType.BOTTOM);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        final ItemStack held = context.getItemInHand();
        final SlabType type = state.getValue(TYPE);
        if (type == SlabType.DOUBLE || held.getItem() != this.asItem()) {
            return false;
        }
        if (!context.replacingClickedOnBlock()) {
            return true;
        }
        final boolean upperHalf = context.getClickLocation().y - (double) context.getClickedPos().getY() > 0.5;
        final Direction face = context.getClickedFace();
        if (type == SlabType.BOTTOM) {
            return face == Direction.UP || (upperHalf && face.getAxis().isHorizontal());
        }
        return face == Direction.DOWN || (!upperHalf && face.getAxis().isHorizontal());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        if (state.hasProperty(RoadOrientation.MIRRORED) && state.hasProperty(RoadOrientation.AXIS)) {
            return AxisMirrorOriented.rotate(state, axisMirrorMode(), rotation);
        }
        if (state.hasProperty(RoadOrientation.CORNER)) {
            return state.setValue(RoadOrientation.CORNER, state.getValue(RoadOrientation.CORNER).rotate(rotation));
        }
        if (state.hasProperty(RoadOrientation.AXIS)) {
            final boolean quarterTurn = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
            return state.setValue(RoadOrientation.AXIS, RoadOrientation.turn(state.getValue(RoadOrientation.AXIS), quarterTurn));
        }
        if (state.hasProperty(RoadOrientation.FACING)) {
            return state.setValue(RoadOrientation.FACING, rotation.rotate(state.getValue(RoadOrientation.FACING)));
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (state.hasProperty(RoadOrientation.MIRRORED) && state.hasProperty(RoadOrientation.AXIS)) {
            return AxisMirrorOriented.mirror(state, axisMirrorMode(), mirror);
        }
        if (state.hasProperty(RoadOrientation.CORNER)) {
            state = state.setValue(RoadOrientation.CORNER, state.getValue(RoadOrientation.CORNER).mirror(mirror));
        }
        if (state.hasProperty(RoadOrientation.FACING) && mirror != Mirror.NONE) {
            state = state.setValue(RoadOrientation.FACING, mirror.mirror(state.getValue(RoadOrientation.FACING)));
            if (state.hasProperty(RoadOrientation.MIRRORED)) {
                state = state.setValue(RoadOrientation.MIRRORED, !state.getValue(RoadOrientation.MIRRORED));
            }
        }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            VersionServices.blocks().scheduleFluidTick(level, pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
}
