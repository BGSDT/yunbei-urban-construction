package com.beigu.yunbeiuc.block.custom.road;

import com.beigu.yunbeiuc.block.RoadBlocks;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 具有自动连接功能的道路 Slab 方块（无 FACING 版本）。
 */
public abstract class RoadSlabWithAutoLine extends Block implements Waterloggable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoadSlabWithAutoLine.class);

    public static final EnumProperty<SlabType> TYPE = Properties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape FULL_SHAPE = VoxelShapes.fullCube();

    /** 自动连接的类型，分为直角和 45° 斜线。 */
    protected final RoadAutoLineType type;

    protected RoadSlabWithAutoLine(Settings settings, RoadAutoLineType type) {
        super(settings);
        this.type = type;
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(TYPE, SlabType.BOTTOM)
                .with(WATERLOGGED, false));
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return state.get(TYPE) != SlabType.DOUBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        SlabType slabType = state.get(TYPE);
        switch (slabType) {
            case DOUBLE:
                return FULL_SHAPE;
            case TOP:
                return TOP_SHAPE;
            default:
                return BOTTOM_SHAPE;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TYPE, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);

        if (blockState.isOf(this)) {
            return blockState.with(TYPE, SlabType.DOUBLE)
                    .with(WATERLOGGED, false);
        } else {
            BlockState blockState2 = this.getDefaultState()
                    .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

            Direction direction = ctx.getSide();
            if (direction != Direction.DOWN && (direction == Direction.UP ||
                    !(ctx.getHitPos().y - (double) blockPos.getY() > 0.5))) {
                return blockState2.with(TYPE, SlabType.BOTTOM);
            } else {
                return blockState2.with(TYPE, SlabType.TOP);
            }
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        SlabType slabType = state.get(TYPE);

        if (slabType != SlabType.DOUBLE && itemStack.isOf(this.asItem())) {
            if (context.canReplaceExisting()) {
                boolean bl = context.getHitPos().y - (double) context.getBlockPos().getY() > 0.5;
                Direction direction = context.getSide();

                if (slabType == SlabType.BOTTOM) {
                    return direction == Direction.UP || (bl && direction.getAxis().isHorizontal());
                } else {
                    return direction == Direction.DOWN || (!bl && direction.getAxis().isHorizontal());
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return state.get(TYPE) != SlabType.DOUBLE ? Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState) : false;
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return state.get(TYPE) != SlabType.DOUBLE ? Waterloggable.super.canFillWithFluid(world, pos, state, fluid) : false;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                  WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    // ============================================================
    // 触发转换
    // ============================================================

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final ActionResult result = super.onUse(state, world, pos, player, hand, hit);
        if (result == ActionResult.FAIL) {
            return result;
        }
        final Item item = player.getStackInHand(hand).getItem();
        if (item instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof RoadSlabWithAutoLine
                && !Direction.Type.VERTICAL.test(hit.getSide())) {
            return ActionResult.PASS;
        }
        // 转换时保留 TYPE 和 WATERLOGGED 属性
        BlockState newState = tryMakeState(getConnectionStateMap(world, pos), pos);
        newState = newState.with(TYPE, state.get(TYPE)).with(WATERLOGGED, state.get(WATERLOGGED));
        world.setBlockState(pos, newState, 2);
        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (!sourcePos.equals(pos.up())
                && !sourcePos.equals(pos.down())
                && !(world.getBlockState(sourcePos).getBlock() instanceof AirBlock)) {
            BlockState newState = tryMakeState(getConnectionStateMap(world, pos), pos);
            newState = newState.with(TYPE, state.get(TYPE)).with(WATERLOGGED, state.get(WATERLOGGED));
            world.setBlockState(pos, newState, 2);
        }
    }

    // ============================================================
    // 状态转换逻辑（与原始 RoadWithAutoLine 一致）
    // ============================================================

    private BlockState makeState(EnumMap<Direction, RoadConnectionState> connectionStateMap) {
        int connected = 0;
        for (Map.Entry<Direction, RoadConnectionState> e : connectionStateMap.entrySet()) {
            if (e.getValue().mayConnect()) {
                connected++;
            }
        }
        switch (connected) {
            case 0:
                return RoadBlocks.ROAD_BLOCK.get().getDefaultState();
            case 4: {
                final int sumYellow = connectionStateMap.values().stream().mapToInt(state -> state.lineColor() == LineColor.YELLOW ? 1 : 0).sum();
                final boolean yellow = sumYellow >= 2;
                for (Direction direction : Direction.Type.HORIZONTAL) {
                    final EightHorizontalDirection direction1 = connectionStateMap.get(direction.rotateYClockwise()).direction();
                    final EightHorizontalDirection direction2 = connectionStateMap.get(direction.rotateYCounterclockwise()).direction();
                    if (direction1.right().map(cornerDirection -> cornerDirection.hasDirection(direction)).orElse(false)
                            && direction2.right().map(cornerDirection -> cornerDirection.hasDirection(direction)).orElse(false)) {
                        return (yellow ? RoadBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE : RoadBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE).get()
                                .getDefaultState();
                    }
                }
                return (yellow ? RoadBlocks.ROAD_WITH_YELLOW_CROSS_LINE : RoadBlocks.ROAD_WITH_WHITE_CROSS_LINE).get().getDefaultState();
            }
            case 2:
                for (Map.Entry<Direction, RoadConnectionState> entry : connectionStateMap.entrySet()) {
                    final RoadConnectionState connectionState = entry.getValue();
                    if (!connectionState.mayConnect()) {
                        continue;
                    }
                    final Direction direction = entry.getKey();

                    final Direction adjacentDirection;
                    final RoadConnectionState adjacentState;
                    if (connectionStateMap.get(direction.getOpposite()).mayConnect()) {
                        adjacentDirection = direction.getOpposite();
                    } else if (connectionStateMap.get(direction.rotateYClockwise()).mayConnect()) {
                        adjacentDirection = direction.rotateYClockwise();
                    } else {
                        adjacentDirection = direction.rotateYCounterclockwise();
                    }
                    adjacentState = connectionStateMap.get(adjacentDirection);

                    if (adjacentDirection == direction.getOpposite()) {
                        final LineColor color;
                        if (connectionState.lineColor() == adjacentState.lineColor()) {
                            color = connectionState.lineColor();
                        } else if (connectionState.lineColor() == LineColor.UNKNOWN) {
                            color = adjacentState.lineColor();
                        } else if (adjacentState.lineColor() == LineColor.UNKNOWN) {
                            color = connectionState.lineColor();
                        } else {
                            color = maxEnum(connectionState.lineColor(), adjacentState.lineColor());
                        }
                        final LineType lineType;
                        if (connectionState.lineType() == adjacentState.lineType()) {
                            lineType = connectionState.lineType();
                        } else if (connectionState.sureConnect() == adjacentState.sureConnect()) {
                            lineType = maxEnum(connectionState.lineType(), adjacentState.lineType());
                        } else {
                            lineType = connectionState.sureConnect() ? connectionState.lineType() : adjacentState.lineType();
                        }

                        if (connectionState.offsetLevel() != 0 && (adjacentState.offsetLevel() != 0 || !adjacentState.sureConnect())) {
                            if ((connectionState.offsetDirection() == adjacentState.offsetDirection() && connectionState.offsetLevel() == adjacentState.offsetLevel() || !adjacentState.sureConnect())) {
                                return composeOffsetStraightLine(connectionState.offsetDirection(), connectionState.offsetLevel(), color);
                            }
                        } else if (!connectionState.sureConnect() && adjacentState.offsetLevel() != 0) {
                            continue;
                        }

                        return (switch (color) {
                            case YELLOW -> switch (lineType) {
                                case DOUBLE -> RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE;
                                case THICK -> RoadBlocks.ROAD_WITH_YELLOW_THICK_LINE;
                                default -> RoadBlocks.ROAD_WITH_YELLOW_LINE;
                            };
                            default -> switch (lineType) {
                                case DOUBLE -> RoadBlocks.ROAD_WITH_WHITE_DOUBLE_LINE;
                                case THICK -> RoadBlocks.ROAD_WITH_WHITE_THICK_LINE;
                                default -> RoadBlocks.ROAD_WITH_WHITE_LINE;
                            };
                        }).get().getDefaultState();
                    } else {
                        if (type == RoadAutoLineType.RIGHT_ANGLE) {
                            if (connectionState.lineColor() == adjacentState.lineColor() || adjacentState.lineColor() == LineColor.UNKNOWN) {
                                if (connectionState.offsetLevel() == 2
                                        && (connectionState.offsetLevel() == adjacentState.offsetLevel() && (connectionState.offsetDirection() == adjacentDirection) == (adjacentState.offsetDirection() == direction) || !adjacentState.sureConnect())) {
                                    return composeAngleLineWithTwoPartsOffset(connectionState.lineColor(), HorizontalCornerDirection.fromDirections(direction, adjacentDirection), connectionState.offsetDirection() == adjacentDirection, type);
                                } else if (connectionState.offsetLevel() == 2 && adjacentState.offsetLevel() != 2) {
                                    return composeAngleLineWithOnePartOffset(connectionState.lineColor(), HorizontalCornerDirection.fromDirections(direction, adjacentDirection), adjacentDirection.getAxis(), connectionState.offsetDirection() == adjacentDirection);
                                } else if (connectionState.offsetLevel() != 2 && adjacentState.offsetLevel() == 2) {
                                    continue;
                                }
                            }

                            if (connectionState.lineColor() == LineColor.YELLOW && adjacentState.lineColor() == LineColor.WHITE) {
                                continue;
                            } else if (connectionState.lineColor() == LineColor.WHITE && adjacentState.lineColor() == LineColor.YELLOW) {
                                final Block block;
                                if (connectionState.lineType() == LineType.THICK && adjacentState.lineType() == LineType.DOUBLE) {
                                    block = RoadBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE.get();
                                } else if (adjacentState.lineType() == LineType.DOUBLE) {
                                    block = RoadBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE.get();
                                } else if (connectionState.lineType() == LineType.THICK) {
                                    block = RoadBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE.get();
                                } else {
                                    block = RoadBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE.get();
                                }
                                return block.getDefaultState();
                            } else if (connectionState.lineColor() == LineColor.WHITE && adjacentState.lineColor() == LineColor.WHITE) {
                                if (connectionState.lineType() == LineType.THICK && adjacentState.lineType() == LineType.NORMAL) {
                                    return RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get().getDefaultState();
                                } else if (connectionState.lineType() == LineType.NORMAL && adjacentState.lineType() == LineType.THICK) {
                                    return RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get().getDefaultState();
                                } else if (connectionState.lineType() == LineType.NORMAL && adjacentState.lineType() == LineType.NORMAL) {
                                    return RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE.get().getDefaultState();
                                }
                            }
                        }

                        if (connectionState.lineColor() == adjacentState.lineColor() || adjacentState.lineColor() == LineColor.UNKNOWN) {
                            final LineType lineType = connectionState.sureConnect() && adjacentState.sureConnect() ? minEnum(connectionState.lineType(), adjacentState.lineType()) : connectionState.sureConnect() ? connectionState.lineType() : adjacentState.lineType();

                            if (connectionState.offsetLevel() == 2) {
                                boolean isInwards = connectionState.offsetDirection() == adjacentDirection;
                                if ((adjacentState.offsetLevel() == 2 &&
                                        (adjacentState.offsetDirection() == direction) == isInwards
                                        || !adjacentState.sureConnect())) {
                                    return composeAngleLineWithTwoPartsOffset(connectionState.lineColor(), HorizontalCornerDirection.fromDirections(direction, adjacentDirection), isInwards, type);
                                }
                            }

                            return (switch (connectionState.lineColor()) {
                                case YELLOW -> switch (type) {
                                    case BEVEL -> switch (lineType) {
                                        case DOUBLE -> RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE;
                                        case THICK -> RoadBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE;
                                        default -> RoadBlocks.ROAD_WITH_YELLOW_BEVEL_LINE;
                                    };
                                    case RIGHT_ANGLE -> RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE;
                                };
                                case WHITE, NONE, UNKNOWN -> switch (type) {
                                    case BEVEL -> switch (lineType) {
                                        case DOUBLE -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE;
                                        case THICK -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE;
                                        default -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE;
                                    };
                                    case RIGHT_ANGLE -> RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE;
                                };
                            }).get().getDefaultState();
                        } else if (connectionState.lineColor() == LineColor.UNKNOWN) {
                            continue;
                        }

                        return (switch (type) {
                            case BEVEL -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE;
                            case RIGHT_ANGLE -> RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE;
                        }).get().getDefaultState();
                    }
                }
                return RoadBlocks.ROAD_BLOCK.get().getDefaultState();
            case 1:
                for (Map.Entry<Direction, RoadConnectionState> entry : connectionStateMap.entrySet()) {
                    final RoadConnectionState connectionState = entry.getValue();
                    if (connectionState.mayConnect()) {
                        if (connectionState.offsetLevel() != 0) {
                            return composeOffsetStraightLine(connectionState.offsetDirection(), connectionState.offsetLevel(), connectionState.lineColor());
                        }
                        return (switch (connectionState.lineColor()) {
                            case YELLOW -> switch (connectionState.lineType()) {
                                case THICK -> RoadBlocks.ROAD_WITH_YELLOW_THICK_LINE;
                                case DOUBLE -> RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE;
                                default -> RoadBlocks.ROAD_WITH_YELLOW_LINE;
                            };
                            default -> switch (connectionState.lineType()) {
                                case THICK -> RoadBlocks.ROAD_WITH_WHITE_THICK_LINE;
                                case DOUBLE -> RoadBlocks.ROAD_WITH_WHITE_DOUBLE_LINE;
                                default -> RoadBlocks.ROAD_WITH_WHITE_LINE;
                            };
                        }).get().getDefaultState();
                    }
                }
                return RoadBlocks.ROAD_BLOCK.get().getDefaultState();
            case 3:
                for (Map.Entry<Direction, RoadConnectionState> entry : connectionStateMap.entrySet()) {
                    final RoadConnectionState unconnectedState = entry.getValue();
                    if (unconnectedState.mayConnect()) {
                        continue;
                    }

                    final Direction unconnectedDirection = entry.getKey();
                    final Direction facingDirection = unconnectedDirection.getOpposite();
                    final RoadConnectionState facingState = connectionStateMap.get(facingDirection);
                    if (facingState.direction() == null
                            || facingState.direction().left().isPresent()
                            || type != RoadAutoLineType.BEVEL) {
                        final RoadConnectionState stateLeft = connectionStateMap.get(facingDirection.rotateYCounterclockwise());
                        final RoadConnectionState stateRight = connectionStateMap.get(facingDirection.rotateYClockwise());

                        if (type == RoadAutoLineType.BEVEL
                                && stateLeft.direction().right().map(cornerDirection -> cornerDirection.hasDirection(facingDirection)).orElse(false)
                                && stateRight.direction().right().map(cornerDirection -> cornerDirection.hasDirection(facingDirection)).orElse(false)) {
                            if (facingState.lineColor() == LineColor.YELLOW && (stateLeft.lineColor() == LineColor.YELLOW || stateRight.lineColor() == LineColor.YELLOW)) {
                                return RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE.get().getDefaultState();
                            } else {
                                return RoadBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE.get().getDefaultState();
                            }
                        }

                        final LineOffset facingOffset = facingState.lineOffset();
                        if (stateLeft.lineColor() == stateRight.lineColor() && stateLeft.lineType() == stateRight.lineType()) {
                            if (stateLeft.lineColor() != facingState.lineColor() && facingState.lineColor() != LineColor.UNKNOWN) {
                                if (stateLeft.lineColor() == LineColor.WHITE && facingState.lineColor() == LineColor.YELLOW) {
                                    final Block block = switch (stateLeft.lineType()) {
                                        case THICK -> (facingState.lineType() == LineType.DOUBLE ? RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE.get() : RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE.get());
                                        default -> (facingState.lineType() == LineType.DOUBLE ? RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE.get() : RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE.get());
                                    };
                                    return composeJointLine(block, facingOffset);
                                }
                                if (stateLeft.lineColor() == LineColor.YELLOW && facingState.lineColor() == LineColor.WHITE) {
                                    return composeJointLine(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE.get(), facingOffset);
                                } else {
                                    return composeJointLine(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get(), facingOffset);
                                }
                            } else {
                                if (facingState.lineColor() == LineColor.YELLOW || (facingState.lineColor() == LineColor.UNKNOWN && stateLeft.lineColor() == LineColor.YELLOW)) {
                                    return composeJointLine(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get(), facingOffset);
                                } else {
                                    final Block block = switch (facingState.lineType()) {
                                        case DOUBLE -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE.get();
                                        case THICK -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE.get();
                                        default -> switch (stateLeft.lineType()) {
                                            case DOUBLE -> RoadBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE.get();
                                            case THICK -> RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE.get();
                                            default -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get();
                                        };
                                    };
                                    return composeJointLine(block, facingOffset);
                                }
                            }
                        } else {
                            final Block block = switch (facingState.lineColor()) {
                                case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get();
                                case WHITE, UNKNOWN, NONE -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get();
                            };
                            return composeJointLine(block, facingOffset);
                        }
                    } else if (facingState.direction().right().isPresent()) {
                        final HorizontalCornerDirection facingStateDirection = facingState.direction().right().get();

                        final Direction bevelConDirection = facingStateDirection.getDirectionInAxis(facingDirection.rotateYClockwise().getAxis());
                        final RoadConnectionState bevelConState = connectionStateMap.get(bevelConDirection);
                        final Direction bevelNonDirection = bevelConDirection.getOpposite();
                        final RoadConnectionState bevelNonState = connectionStateMap.get(bevelNonDirection);
                        final Block block;
                        if (facingState.lineColor() == LineColor.YELLOW) {
                            if (bevelNonState.lineColor() == LineColor.WHITE && bevelNonState.lineType() == LineType.THICK
                                    && (bevelConState.lineColor() == LineColor.WHITE && bevelConState.lineType() == LineType.THICK
                                    || bevelConState.lineColor() == LineColor.YELLOW)) {
                                block = RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE.get();
                            } else if (bevelNonState.lineColor() == LineColor.YELLOW && bevelNonState.lineType() == LineType.THICK && bevelConState.lineColor() == LineColor.YELLOW) {
                                block = RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE.get();
                            } else if (bevelNonState.lineColor() == LineColor.WHITE) {
                                block = RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE.get();
                            } else {
                                block = RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE.get();
                            }
                        } else {
                            if (bevelNonState.lineColor() == LineColor.YELLOW && bevelNonState.lineType() == LineType.THICK
                                    && (bevelConState.lineColor() != LineColor.YELLOW || bevelConState.lineType() == LineType.THICK)) {
                                block = RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE.get();
                            } else if (bevelNonState.lineType() == LineType.THICK && bevelConState.lineColor() != LineColor.YELLOW) {
                                block = RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE.get();
                            } else if (bevelNonState.lineColor() == LineColor.YELLOW) {
                                block = RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE.get();
                            } else {
                                block = RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE.get();
                            }
                        }
                        return block.getDefaultState();
                    }
                }
                return RoadBlocks.ROAD_BLOCK.get().getDefaultState();
            default:
                throw new IllegalStateException("Illegal connected number: " + connected);
        }
    }

    private BlockState tryMakeState(EnumMap<Direction, RoadConnectionState> connectionStateMap, BlockPos pos) {
        try {
            return makeState(connectionStateMap);
        } catch (Throwable throwable) {
            LOGGER.error("An error was found when converting road block at {}:", pos, throwable);
            return RoadBlocks.ROAD_BLOCK.get().getDefaultState();
        }
    }

    private BlockState composeJointLine(Block block, LineOffset facingOffset) {
        if (facingOffset != null && facingOffset.level() == 2 && getOffsetTRoads().containsKey(block)) {
            final Block offsetSide = getOffsetTRoads().get(block);
            return offsetSide.getDefaultState();
        } else {
            return block.getDefaultState();
        }
    }

    private static Map<Block, Block> getOffsetTRoads() {
        return OffsetTRoadsHolder.ROADS;
    }

    private static final class OffsetTRoadsHolder {
        static final Map<Block, Block> ROADS = Map.of(
                RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get(), RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE.get(),
                RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get(), RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE.get(),
                RoadBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE.get(), RoadBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE.get(),
                RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE.get(), RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE.get(),
                RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE.get(), RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE.get(),
                RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE.get(), RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE.get(),
                RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE.get(), RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE.get()
        );
    }

    private BlockState composeAngleLineWithOnePartOffset(LineColor lineColor, HorizontalCornerDirection facing, Direction.Axis axis, boolean isInwards) {
        final Block block;
        block = switch (lineColor) {
            case YELLOW -> (isInwards ? RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN.get() : RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT.get());
            default -> (isInwards ? RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN.get() : RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT.get());
        };
        return block.getDefaultState();
    }

    private BlockState composeAngleLineWithTwoPartsOffset(LineColor lineColor, HorizontalCornerDirection facing, boolean isInwards, RoadAutoLineType type) {
        final Block block;
        if (type == RoadAutoLineType.RIGHT_ANGLE) {
            block = switch (lineColor) {
                case YELLOW -> (isInwards ? RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE.get() : RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE.get());
                default -> (isInwards ? RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE.get() : RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE.get());
            };
        } else {
            block = switch (lineColor) {
                case YELLOW -> (isInwards ? RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get() : RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get());
                default -> (isInwards ? RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get() : RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get());
            };
        }
        return block.getDefaultState();
    }

    private static BlockState composeOffsetStraightLine(Direction offsetDirection, int offsetLevel, LineColor color) {
        return switch (offsetLevel) {
            case 114514 -> RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE.get().getDefaultState();
            case 2 -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_OFFSET_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_OFFSET_LINE.get();
                };
                yield block.getDefaultState();
            }
            case 1 -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE.get();
                };
                yield block.getDefaultState();
            }
            default -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_LINE.get();
                };
                yield block.getDefaultState();
            }
        };
    }

    // ============================================================
    // 连接状态检测（按注册 ID / Block 对象识别邻居）
    // ============================================================

    private EnumMap<Direction, RoadConnectionState> getConnectionStateMap(WorldAccess world, BlockPos pos0) {
        final EnumMap<Direction, RoadConnectionState> connectionStateMap = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            RoadConnectionState state = null;
            for (BlockPos pos : new BlockPos[]{pos0, pos0.up(), pos0.down()}) {
                final BlockState nextState = world.getBlockState(pos.offset(direction, 1));
                final RoadLineInfo info = getBlockInfo().get(nextState.getBlock());
                if (info != null) {
                    state = info.get(nextState, direction.getOpposite());
                    break;
                }
            }
            connectionStateMap.put(direction, state == null ? RoadConnectionState.empty() : state);
        }
        return connectionStateMap;
    }

    @FunctionalInterface
    private interface RoadLineInfo {
        RoadConnectionState get(BlockState state, Direction direction);
    }

    private static Map<Block, RoadLineInfo> getBlockInfo() {
        return BlockInfoHolder.INFO;
    }

    private static final class BlockInfoHolder {
        static final Map<Block, RoadLineInfo> INFO = buildInfo();
    }

    private static void put(Map<Block, RoadLineInfo> info, Block block, RoadLineInfo roadLineInfo) {
        info.put(block, roadLineInfo);
    }

    private static Map<Block, RoadLineInfo> buildInfo() {
        final Map<Block, RoadLineInfo> info = new HashMap<>();
        put(info, RoadBlocks.ROAD_WITH_AUTO_BEVEL_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_WITH_WHITE_LINE.get(), straight(LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITE_DOUBLE_LINE.get(), straight(LineColor.WHITE, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_WHITE_THICK_LINE.get(), straight(LineColor.WHITE, LineType.THICK));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_LINE.get(), straight(LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE.get(), straight(LineColor.YELLOW, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_THICK_LINE.get(), straight(LineColor.YELLOW, LineType.THICK));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_LINE.get(), offsetStraight(LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_LINE.get(), offsetStraight(LineColor.YELLOW, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE.get(), offsetStraight(LineColor.WHITE, LineType.NORMAL, 1));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE.get(), offsetStraight(LineColor.YELLOW, LineType.NORMAL, 1));
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE.get(), offsetStraight(LineColor.WHITE, LineType.DOUBLE, 114514));
        put(info, RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE.get(), corner(LineColor.WHITE, LineType.NORMAL, false));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE.get(), corner(LineColor.YELLOW, LineType.NORMAL, false));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE.get(), corner(LineColor.WHITE, LineType.NORMAL, true));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE.get(), corner(LineColor.WHITE, LineType.DOUBLE, true));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE.get(), corner(LineColor.WHITE, LineType.THICK, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_LINE.get(), corner(LineColor.YELLOW, LineType.NORMAL, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE.get(), corner(LineColor.YELLOW, LineType.DOUBLE, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE.get(), corner(LineColor.YELLOW, LineType.THICK, true));
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.THICK, LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.THICK, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.NORMAL, LineColor.YELLOW, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.THICK, LineColor.YELLOW, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT.get(), angleLineOnePartOffset(LineColor.WHITE, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN.get(), angleLineOnePartOffset(LineColor.WHITE, -2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT.get(), angleLineOnePartOffset(LineColor.YELLOW, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN.get(), angleLineOnePartOffset(LineColor.YELLOW, -2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.WHITE, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.WHITE, -2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.YELLOW, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.YELLOW, -2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.WHITE, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.WHITE, -2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.YELLOW, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.YELLOW, -2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get(), joint(LineColor.WHITE, LineType.NORMAL, LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get(), joint(LineColor.YELLOW, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE.get(), joint(LineColor.WHITE, LineType.NORMAL, LineColor.WHITE, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE.get(), joint(LineColor.WHITE, LineType.NORMAL, LineColor.WHITE, LineType.THICK));
        put(info, RoadBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE.get(), joint(LineColor.WHITE, LineType.DOUBLE, LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE.get(), joint(LineColor.WHITE, LineType.THICK, LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE.get(), joint(LineColor.WHITE, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE.get(), joint(LineColor.YELLOW, LineType.NORMAL, LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE.get(), joint(LineColor.WHITE, LineType.NORMAL, LineColor.YELLOW, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE.get(), joint(LineColor.WHITE, LineType.THICK, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE.get(), joint(LineColor.WHITE, LineType.THICK, LineColor.YELLOW, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.NORMAL, LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(LineColor.YELLOW, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.DOUBLE, LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.THICK, LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.THICK, LineColor.YELLOW, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE.get(), jointOffsetSide(LineColor.YELLOW, LineType.NORMAL, LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_CROSS_LINE.get(), cross(LineColor.WHITE));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_CROSS_LINE.get(), cross(LineColor.YELLOW));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE.get(), biBevel(LineColor.WHITE, false));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE.get(), biBevel(LineColor.YELLOW, false));
        put(info, RoadBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE.get(), biBevel(LineColor.WHITE, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE.get(), biBevel(LineColor.YELLOW, true));
        put(info, RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE.get(), straightAndAngle(LineColor.WHITE, LineType.NORMAL, LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE.get(), straightAndAngle(LineColor.YELLOW, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE.get(), straightAndAngle(LineColor.WHITE, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE.get(), straightAndAngle(LineColor.YELLOW, LineType.NORMAL, LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE.get(), straightAndAngle(LineColor.WHITE, LineType.THICK, LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_LINE.get(), straightAndAngle(LineColor.YELLOW, LineType.THICK, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE.get(), straightAndAngle(LineColor.WHITE, LineType.THICK, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE.get(), straightAndAngle(LineColor.YELLOW, LineType.THICK, LineColor.WHITE, LineType.NORMAL));
        return info;
    }

    private static RoadLineInfo auto() {
        return (state, direction) ->
                new RoadConnectionState(WhetherConnected.MAY_CONNECT, LineColor.UNKNOWN, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
    }

    private static RoadLineInfo straight(LineColor color, LineType type) {
        return (state, direction) -> conn(color, EightHorizontalDirection.of(direction), type, null);
    }

    private static RoadLineInfo offsetStraight(LineColor color, LineType type, int level) {
        return (state, direction) -> conn(color, EightHorizontalDirection.of(direction), type,
                new LineOffset(direction.rotateYCounterclockwise(), level));
    }

    private static RoadLineInfo corner(LineColor color, LineType type, boolean isBevel) {
        return (state, direction) -> conn(color,
                isBevel ? EightHorizontalDirection.of(HorizontalCornerDirection.fromDirections(direction, direction.rotateYCounterclockwise())) : EightHorizontalDirection.of(direction),
                type, null);
    }

    private static RoadLineInfo diffAngle(LineColor color1, LineType type1, LineColor color2, LineType type2) {
        return (state, direction) -> conn(
                direction.getAxis() == Direction.Axis.Z ? color1 : color2,
                EightHorizontalDirection.of(direction),
                direction.getAxis() == Direction.Axis.Z ? type1 : type2,
                null);
    }

    private static RoadLineInfo angleLineOnePartOffset(LineColor color, int offsetOutwards) {
        return (state, direction) -> conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL,
                direction.getAxis() == Direction.Axis.X ? LineOffset.of(direction.rotateYCounterclockwise(), offsetOutwards) : null);
    }

    private static RoadLineInfo angleLineTwoPartsOffset(LineColor color, int offsetOutwards) {
        return (state, direction) -> {
            final Direction offsetDir = direction.rotateYClockwise();
            return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, LineOffset.of(offsetDir, offsetOutwards));
        };
    }

    private static RoadLineInfo joint(LineColor mainColor, LineType mainType, LineColor sideColor, LineType sideType) {
        return (state, direction) -> {
            if (direction.getAxis() == Direction.Axis.X) {
                return conn(mainColor, EightHorizontalDirection.of(direction), mainType, null);
            }
            return conn(sideColor, EightHorizontalDirection.of(direction), sideType, null);
        };
    }

    private static RoadLineInfo jointOffsetSide(LineColor mainColor, LineType mainType, LineColor sideColor, LineType sideType, int level) {
        return (state, direction) -> {
            if (direction.getAxis() == Direction.Axis.X) {
                return conn(mainColor, EightHorizontalDirection.of(direction), mainType, null);
            }
            return conn(sideColor, EightHorizontalDirection.of(direction), sideType,
                    new LineOffset(direction.rotateYClockwise(), level));
        };
    }

    private static RoadLineInfo cross(LineColor color) {
        return (state, direction) -> conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
    }

    private static RoadLineInfo biBevel(LineColor color, boolean threeLayer) {
        return (state, direction) -> {
            if (threeLayer) {
                if (direction.getAxis() == Direction.Axis.Z) {
                    return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
                }
                return conn(color, EightHorizontalDirection.of(HorizontalCornerDirection.fromDirections(Direction.NORTH, direction.getOpposite())), LineType.NORMAL, null);
            } else {
                if (direction == Direction.NORTH) {
                    return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
                }
                if (direction != Direction.SOUTH) {
                    return conn(color, EightHorizontalDirection.of(HorizontalCornerDirection.fromDirections(Direction.NORTH, direction.getOpposite())), LineType.NORMAL, null);
                }
                return RoadConnectionState.empty();
            }
        };
    }

    private static RoadLineInfo straightAndAngle(LineColor straightColor, LineType straightType, LineColor bevelColor, LineType bevelType) {
        return (state, direction) -> {
            if (direction.getAxis() == Direction.Axis.Z) {
                return conn(straightColor, EightHorizontalDirection.of(direction), straightType, null);
            }
            if (direction == Direction.EAST) {
                return conn(bevelColor, EightHorizontalDirection.of(HorizontalCornerDirection.NORTH_EAST), bevelType, null);
            }
            return RoadConnectionState.empty();
        };
    }

    private static RoadConnectionState conn(LineColor color, EightHorizontalDirection direction, LineType type, LineOffset offset) {
        return new RoadConnectionState(WhetherConnected.CONNECTED, color, direction, type, offset);
    }

    // ============================================================
    // 方向转换辅助
    // ============================================================

    private static <E extends Enum<E>> E maxEnum(E a, E b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    private static <E extends Enum<E>> E minEnum(E a, E b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    // ============================================================
    // 支持类型
    // ============================================================

    protected enum RoadAutoLineType {
        RIGHT_ANGLE,
        BEVEL
    }

    private enum LineColor {
        WHITE,
        YELLOW,
        UNKNOWN,
        NONE
    }

    private enum LineType {
        NORMAL,
        DOUBLE,
        THICK
    }

    private record LineOffset(Direction offsetDirection, int level) {
        LineOffset {
            if (level < 0) {
                throw new IllegalArgumentException();
            }
        }

        static LineOffset of(Direction direction, int level) {
            if (level == 0) {
                return null;
            } else if (level > 0) {
                return new LineOffset(direction, level);
            } else {
                return new LineOffset(direction.getOpposite(), -level);
            }
        }
    }

    private enum HorizontalCornerDirection {
        SOUTH_WEST(Direction.SOUTH, Direction.WEST),
        NORTH_WEST(Direction.NORTH, Direction.WEST),
        NORTH_EAST(Direction.NORTH, Direction.EAST),
        SOUTH_EAST(Direction.SOUTH, Direction.EAST);

        public final Direction dir1;
        public final Direction dir2;

        HorizontalCornerDirection(Direction dir1, Direction dir2) {
            this.dir1 = dir1;
            this.dir2 = dir2;
        }

        static HorizontalCornerDirection fromDirections(Direction dir1, Direction dir2) {
            for (HorizontalCornerDirection direction : values()) {
                if ((direction.dir1 == dir1 && direction.dir2 == dir2) || (direction.dir1 == dir2 && direction.dir2 == dir1)) {
                    return direction;
                }
            }
            throw new IllegalArgumentException("There is no horizontal corner direction composed of " + dir1.asString() + " " + dir2.asString() + ".");
        }

        Direction getDirectionInAxis(Direction.Axis axis) {
            if (dir1.getAxis() == axis) {
                return dir1;
            }
            if (dir2.getAxis() == axis) {
                return dir2;
            }
            throw new IllegalStateException("Direction " + this.name() + " has no direction in axis " + axis.asString() + "!");
        }

        boolean hasDirection(Direction direction) {
            return direction == dir1 || direction == dir2;
        }
    }

    private enum EightHorizontalDirection {
        SOUTH(Direction.SOUTH),
        SOUTH_WEST(HorizontalCornerDirection.SOUTH_WEST),
        WEST(Direction.WEST),
        NORTH_WEST(HorizontalCornerDirection.NORTH_WEST),
        NORTH(Direction.NORTH),
        NORTH_EAST(HorizontalCornerDirection.NORTH_EAST),
        EAST(Direction.EAST),
        SOUTH_EAST(HorizontalCornerDirection.SOUTH_EAST);

        private final Direction direction;
        private final HorizontalCornerDirection cornerDirection;
        private final boolean isCorner;

        EightHorizontalDirection(Direction direction) {
            this.direction = direction;
            this.cornerDirection = null;
            this.isCorner = false;
        }

        EightHorizontalDirection(HorizontalCornerDirection cornerDirection) {
            this.direction = null;
            this.cornerDirection = cornerDirection;
            this.isCorner = true;
        }

        static EightHorizontalDirection of(Direction direction) {
            return switch (direction) {
                case NORTH -> NORTH;
                case EAST -> EAST;
                case SOUTH -> SOUTH;
                default -> WEST;
            };
        }

        static EightHorizontalDirection of(HorizontalCornerDirection cornerDirection) {
            return switch (cornerDirection) {
                case SOUTH_WEST -> SOUTH_WEST;
                case NORTH_WEST -> NORTH_WEST;
                case NORTH_EAST -> NORTH_EAST;
                case SOUTH_EAST -> SOUTH_EAST;
            };
        }

        Optional<Direction> left() {
            return isCorner ? Optional.empty() : Optional.of(direction);
        }

        Optional<HorizontalCornerDirection> right() {
            return isCorner ? Optional.of(cornerDirection) : Optional.empty();
        }
    }

    private enum WhetherConnected {
        NOT_CONNECTED,
        MAY_CONNECT,
        CONNECTED
    }

    private record RoadConnectionState(WhetherConnected whetherConnected, LineColor lineColor, EightHorizontalDirection direction, LineType lineType, LineOffset lineOffset) {
        static RoadConnectionState empty() {
            return new RoadConnectionState(WhetherConnected.NOT_CONNECTED, LineColor.NONE, null, LineType.NORMAL, null);
        }

        boolean mayConnect() {
            return this.whetherConnected.ordinal() >= WhetherConnected.MAY_CONNECT.ordinal();
        }

        boolean sureConnect() {
            return this.whetherConnected == WhetherConnected.CONNECTED;
        }

        Direction offsetDirection() {
            return lineOffset == null ? null : lineOffset.offsetDirection();
        }

        int offsetLevel() {
            return lineOffset == null ? 0 : lineOffset.level();
        }
    }
}