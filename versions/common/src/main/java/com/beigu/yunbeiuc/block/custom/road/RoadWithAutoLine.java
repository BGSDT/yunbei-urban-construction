package com.beigu.yunbeiuc.block.custom.road;
import com.beigu.yunbeiuc.api.mapper.BlockCompat;

import com.beigu.yunbeiuc.block.RoadBlocks;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class RoadWithAutoLine extends BlockCompat {
    private static final Logger LOGGER = LogManager.getLogger(RoadWithAutoLine.class);

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    private static final int PAIRED_COLOR_LEVEL = 3;

    protected final RoadAutoLineType type;

    protected RoadWithAutoLine(BlockBehaviour.Properties properties, RoadAutoLineType type) {
        super(properties);
        this.type = type;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult useCompat(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final InteractionResult result = super.useCompat(state, world, pos, player, hand, hit);
        if (result == InteractionResult.FAIL) {
            return result;
        }
        final Item item = player.getItemInHand(hand).getItem();
        if (item instanceof BlockItem blockItem
            && (blockItem.getBlock() instanceof RoadWithAutoLine || blockItem.getBlock() instanceof RoadSlabWithAutoLine)
            && !Direction.Plane.VERTICAL.test(hit.getDirection())) {
            return InteractionResult.PASS;
        }
        world.setBlock(pos, tryMakeState(this.type, getConnectionStateMap(world, pos), state, pos), 2);
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, world, pos, sourceBlock, sourcePos, notify);
        if (!sourcePos.equals(pos.above())
            && !sourcePos.equals(pos.below())
            && !(world.getBlockState(sourcePos).getBlock() instanceof AirBlock)) {
            world.setBlock(pos, tryMakeState(this.type, getConnectionStateMap(world, pos), state, pos), 2);
        }
    }

    static BlockState makeState(RoadAutoLineType type, EnumMap<Direction, RoadConnectionState> connectionStateMap, BlockState defaultState) {
        int connected = 0;
        for (Map.Entry<Direction, RoadConnectionState> e : connectionStateMap.entrySet()) {
            if (e.getValue().mayConnect()) {
                connected++;
            }
        }
        switch (connected) {
            case 0:
                return RoadBlocks.ROAD_BLOCK.get().defaultBlockState();
            case 4: {
                final int sumYellow = connectionStateMap.values().stream().mapToInt(state -> state.lineColor() == LineColor.YELLOW ? 1 : 0).sum();
                final boolean yellow = sumYellow >= 2;
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    final EightHorizontalDirection direction1 = connectionStateMap.get(direction.getClockWise()).direction();
                    final EightHorizontalDirection direction2 = connectionStateMap.get(direction.getCounterClockWise()).direction();
                    if (direction1.right().map(cornerDirection -> cornerDirection.hasDirection(direction)).orElse(false)
                        && direction2.right().map(cornerDirection -> cornerDirection.hasDirection(direction)).orElse(false)) {
                        return RoadOrientation.align(
                            (yellow ? RoadBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE : RoadBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE).get(),
                            direction);
                    }
                }
                return (yellow ? RoadBlocks.ROAD_WITH_YELLOW_CROSS_LINE : RoadBlocks.ROAD_WITH_WHITE_CROSS_LINE).get().defaultBlockState();
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
                    } else if (connectionStateMap.get(direction.getClockWise()).mayConnect()) {
                        adjacentDirection = direction.getClockWise();
                    } else {
                        adjacentDirection = direction.getCounterClockWise();
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

                        return RoadOrientation.align((switch (color) {
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
                        }).get().defaultBlockState(), direction);
                    } else {
                        if (type == RoadAutoLineType.RIGHT_ANGLE) {
                            if (connectionState.lineColor() == adjacentState.lineColor() || adjacentState.lineColor() == LineColor.UNKNOWN) {
                                if (connectionState.offsetLevel() == 2
                                    && (connectionState.offsetLevel() == adjacentState.offsetLevel() && (connectionState.offsetDirection() == adjacentDirection) == (adjacentState.offsetDirection() == direction) || !adjacentState.sureConnect())) {
                                    return composeAngleLineWithTwoPartsOffset(connectionState.lineColor(), RoadCorner.of(direction, adjacentDirection), connectionState.offsetDirection() == adjacentDirection, type);
                                } else if (connectionState.offsetLevel() == 2 && adjacentState.offsetLevel() != 2) {
                                    return composeAngleLineWithOnePartOffset(connectionState.lineColor(), RoadCorner.of(direction, adjacentDirection), adjacentDirection.getAxis(), connectionState.offsetDirection() == adjacentDirection);
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
                                return RoadOrientation.align(block, adjacentDirection, direction.getClockWise() == adjacentDirection);
                            } else if (connectionState.lineColor() == LineColor.WHITE && adjacentState.lineColor() == LineColor.WHITE) {
                                if (connectionState.lineType() == LineType.THICK && adjacentState.lineType() == LineType.NORMAL) {
                                    return RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get(), adjacentDirection, direction.getClockWise() == adjacentDirection);
                                } else if (connectionState.lineType() == LineType.NORMAL && adjacentState.lineType() == LineType.THICK) {
                                    return RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get(), direction, adjacentDirection.getClockWise() == direction);
                                } else if (connectionState.lineType() == LineType.NORMAL && adjacentState.lineType() == LineType.NORMAL) {
                                    return RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE.get(), cornerToFacing(RoadCorner.of(direction, adjacentDirection)));
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
                                    return composeAngleLineWithTwoPartsOffset(connectionState.lineColor(), RoadCorner.of(direction, adjacentDirection), isInwards, type);
                                }
                            }

                            return RoadOrientation.align((switch (connectionState.lineColor()) {
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
                            }).get(), cornerToFacing(RoadCorner.of(direction, adjacentDirection)));
                        } else if (connectionState.lineColor() == LineColor.UNKNOWN) {
                            continue;
                        }

                        return RoadOrientation.align((switch (type) {
                            case BEVEL -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE;
                            case RIGHT_ANGLE -> RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE;
                        }).get(), cornerToFacing(RoadCorner.of(direction, adjacentDirection)));
                    }
                }
                return defaultState;
            case 1:
                for (Map.Entry<Direction, RoadConnectionState> entry : connectionStateMap.entrySet()) {
                    final RoadConnectionState connectionState = entry.getValue();
                    if (connectionState.mayConnect()) {
                        if (connectionState.offsetLevel() != 0) {
                            return composeOffsetStraightLine(connectionState.offsetDirection(), connectionState.offsetLevel(), connectionState.lineColor());
                        }
                        return RoadOrientation.align((switch (connectionState.lineColor()) {
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
                        }).get(), entry.getKey());
                    }
                }
                return defaultState;
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
                        final RoadConnectionState stateLeft = connectionStateMap.get(facingDirection.getCounterClockWise());
                        final RoadConnectionState stateRight = connectionStateMap.get(facingDirection.getClockWise());

                        if (type == RoadAutoLineType.BEVEL
                            && stateLeft.direction().right().map(cornerDirection -> cornerDirection.hasDirection(facingDirection)).orElse(false)
                            && stateRight.direction().right().map(cornerDirection -> cornerDirection.hasDirection(facingDirection)).orElse(false)) {
                            if (facingState.lineColor() == LineColor.YELLOW && (stateLeft.lineColor() == LineColor.YELLOW || stateRight.lineColor() == LineColor.YELLOW)) {
                                return RoadOrientation.align(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE.get(), facingDirection);
                            } else {
                                return RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE.get(), facingDirection);
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
                                    return composeJointLine(block, facingDirection, facingOffset);
                                }
                                if (stateLeft.lineColor() == LineColor.YELLOW && facingState.lineColor() == LineColor.WHITE) {
                                    return composeJointLine(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE.get(), facingDirection, facingOffset);
                                } else {
                                    return composeJointLine(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get(), facingDirection, facingOffset);
                                }
                            } else {
                                if (facingState.lineColor() == LineColor.YELLOW || (facingState.lineColor() == LineColor.UNKNOWN && stateLeft.lineColor() == LineColor.YELLOW)) {
                                    return composeJointLine(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get(), facingDirection, facingOffset);
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
                                    return composeJointLine(block, facingDirection, facingOffset);
                                }
                            }
                        } else {
                            final Block block = switch (facingState.lineColor()) {
                                case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get();
                                case WHITE, UNKNOWN, NONE -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get();
                            };
                            return composeJointLine(block, facingDirection, facingOffset);
                        }
                    } else if (facingState.direction().right().isPresent()) {
                        final RoadCorner facingStateDirection = facingState.direction().right().get();

                        final Direction bevelConDirection = facingStateDirection.getDirectionInAxis(facingDirection.getClockWise().getAxis());
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
                        return RoadOrientation.align(block, bevelConDirection);
                    }
                }
                return defaultState;
            default:
                throw new IllegalStateException("Illegal connected number: " + connected);
        }
    }

    static BlockState tryMakeState(RoadAutoLineType type, EnumMap<Direction, RoadConnectionState> connectionStateMap, BlockState defaultState, BlockPos pos) {
        try {
            return makeState(type, connectionStateMap, defaultState);
        } catch (Throwable throwable) {
            LOGGER.error("An error was found when converting road block at {}:", pos, throwable);
            return defaultState;
        }
    }

    private static BlockState composeJointLine(Block block, Direction facingDirection, LineOffset facingOffset) {
        if (facingOffset != null && facingOffset.level() == 2 && getOffsetTRoads().containsKey(block)) {
            final Block offsetSide = getOffsetTRoads().get(block);
            return RoadOrientation.align(offsetSide, facingOffset.offsetDirection().getCounterClockWise());
        } else {
            return RoadOrientation.align(block, facingDirection);
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

    private static BlockState composeAngleLineWithOnePartOffset(LineColor lineColor, RoadCorner facing, Direction.Axis axis, boolean isInwards) {
        final Block block;
        block = switch (lineColor) {
            case YELLOW -> (isInwards ? RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN.get() : RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT.get());
            default -> (isInwards ? RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN.get() : RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT.get());
        };
        return RoadOrientation.align(block, facing.getDirectionInAxis(axis));
    }

    private static BlockState composeAngleLineWithTwoPartsOffset(LineColor lineColor, RoadCorner facing, boolean isInwards, RoadAutoLineType type) {
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
        return RoadOrientation.align(block, cornerToFacing(facing));
    }

    private static BlockState composeOffsetStraightLine(Direction offsetDirection, int offsetLevel, LineColor color) {
        return switch (offsetLevel) {
            case PAIRED_COLOR_LEVEL -> RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE.get(), offsetDirection.getClockWise());
            case 2 -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_OFFSET_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_OFFSET_LINE.get();
                };
                yield RoadOrientation.align(block, offsetDirection.getClockWise());
            }
            case 1 -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE.get();
                };
                yield RoadOrientation.align(block, offsetDirection.getClockWise());
            }
            default -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_LINE.get();
                };
                yield RoadOrientation.align(block, dirForAxis(offsetDirection.getClockWise().getAxis()));
            }
        };
    }

    static EnumMap<Direction, RoadConnectionState> getConnectionStateMap(LevelAccessor world, BlockPos pos0) {
        final EnumMap<Direction, RoadConnectionState> connectionStateMap = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            RoadConnectionState state = null;
            for (BlockPos pos : new BlockPos[]{pos0, pos0.above(), pos0.below()}) {
                final BlockState nextState = world.getBlockState(pos.relative(direction, 1));
                final RoadLineInfo info = getBlockInfo().get(nextState.getBlock());
                if (info != null) {
                    state = info.getValue(nextState, direction.getOpposite());
                    break;
                }
            }
            connectionStateMap.put(direction, state == null ? RoadConnectionState.empty() : state);
        }
        return connectionStateMap;
    }

    @FunctionalInterface
    interface RoadLineInfo {
        RoadConnectionState getValue(BlockState state, Direction direction);
    }

    static Map<Block, RoadLineInfo> getBlockInfo() {
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
        put(info, RoadBlocks.ROAD_SLAB_WITH_AUTO_BEVEL_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_SLAB_WITH_AUTO_RIGHTANGLE_LINE.get(), auto());
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
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE.get(), offsetStraight(LineColor.WHITE, LineType.DOUBLE, PAIRED_COLOR_LEVEL));
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
        return (state, direction) -> direction.getAxis() == RoadOrientation.axisOf(state)
            ? conn(color, EightHorizontalDirection.of(direction), type, null)
            : RoadConnectionState.empty();
    }

    private static RoadLineInfo offsetStraight(LineColor color, LineType type, int level) {
        return (state, direction) -> {
            if (direction.getAxis() != RoadOrientation.axisOf(state)) {
                return RoadConnectionState.empty();
            }
            return conn(color, EightHorizontalDirection.of(direction), type,
                new LineOffset(RoadOrientation.directionOf(state).getCounterClockWise(), level));
        };
    }

    private static RoadLineInfo corner(LineColor color, LineType type, boolean isBevel) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            final RoadCorner corner = facingToCorner(facing);
            if (!corner.hasDirection(direction)) {
                return RoadConnectionState.empty();
            }
            return conn(color,
                isBevel ? EightHorizontalDirection.of(corner.mirror(direction)) : EightHorizontalDirection.of(direction),
                type, null);
        };
    }

    private static RoadLineInfo diffAngle(LineColor color1, LineType type1, LineColor color2, LineType type2) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing) {
                return conn(color2, EightHorizontalDirection.of(direction), type2, null);
            }
            if (direction == facing.getClockWise()) {
                return conn(color1, EightHorizontalDirection.of(direction), type1, null);
            }
            return RoadConnectionState.empty();
        };
    }

    private static RoadLineInfo angleLineOnePartOffset(LineColor color, int offsetOutwards) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing) {
                return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
            }
            if (direction == facing.getClockWise()) {
                return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL,
                    LineOffset.of(facing.getOpposite(), offsetOutwards));
            }
            return RoadConnectionState.empty();
        };
    }

    private static RoadLineInfo angleLineTwoPartsOffset(LineColor color, int offsetOutwards) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            final RoadCorner corner = facingToCorner(facing);
            if (!corner.hasDirection(direction)) {
                return RoadConnectionState.empty();
            }
            final Direction offsetDir = corner.getDirectionInAxis(direction.getClockWise().getAxis()).getOpposite();
            return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, LineOffset.of(offsetDir, offsetOutwards));
        };
    }

    private static RoadLineInfo joint(LineColor mainColor, LineType mainType, LineColor sideColor, LineType sideType) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing.getOpposite()) {
                return RoadConnectionState.empty();
            }
            if (direction == facing) {
                return conn(sideColor, EightHorizontalDirection.of(direction), sideType, null);
            }
            return conn(mainColor, EightHorizontalDirection.of(direction), mainType, null);
        };
    }

    private static RoadLineInfo jointOffsetSide(LineColor mainColor, LineType mainType, LineColor sideColor, LineType sideType, int level) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing.getOpposite()) {
                return RoadConnectionState.empty();
            }
            if (direction == facing) {
                return conn(sideColor, EightHorizontalDirection.of(direction), sideType,
                    new LineOffset(facing.getClockWise(), level));
            }
            return conn(mainColor, EightHorizontalDirection.of(direction), mainType, null);
        };
    }

    private static RoadLineInfo cross(LineColor color) {
        return (state, direction) -> conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
    }

    private static RoadLineInfo biBevel(LineColor color, boolean threeLayer) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (threeLayer) {
                if (facing == direction || facing == direction.getOpposite()) {
                    return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
                }
                return conn(color, EightHorizontalDirection.of(RoadCorner.of(facing, direction.getOpposite())), LineType.NORMAL, null);
            } else {
                if (facing == direction) {
                    return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
                }
                if (facing != direction.getOpposite()) {
                    return conn(color, EightHorizontalDirection.of(RoadCorner.of(facing, direction.getOpposite())), LineType.NORMAL, null);
                }
                return RoadConnectionState.empty();
            }
        };
    }

    private static RoadLineInfo straightAndAngle(LineColor straightColor, LineType straightType, LineColor bevelColor, LineType bevelType) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing || direction == facing.getOpposite()) {
                return conn(straightColor, EightHorizontalDirection.of(direction), straightType, null);
            }
            if (direction == facing.getClockWise()) {
                return conn(bevelColor, EightHorizontalDirection.of(facingToCorner(facing).mirror(direction)), bevelType, null);
            }
            return RoadConnectionState.empty();
        };
    }

    private static RoadConnectionState conn(LineColor color, EightHorizontalDirection direction, LineType type, LineOffset offset) {
        return new RoadConnectionState(WhetherConnected.CONNECTED, color, direction, type, offset);
    }

    private static Direction cornerToFacing(RoadCorner corner) {
        if (corner.dir1.getClockWise() == corner.dir2) {
            return corner.dir1;
        }
        return corner.dir2;
    }

    private static RoadCorner facingToCorner(Direction facing) {
        return RoadCorner.of(facing, facing.getClockWise());
    }

    private static Direction dirForAxis(Direction.Axis axis) {
        return axis == Direction.Axis.X ? Direction.EAST : Direction.NORTH;
    }

    private static <E extends Enum<E>> E maxEnum(E a, E b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    private static <E extends Enum<E>> E minEnum(E a, E b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    enum RoadAutoLineType {
        RIGHT_ANGLE,
        BEVEL
    }

    enum LineColor {
        WHITE,
        YELLOW,
        UNKNOWN,
        NONE
    }

    enum LineType {
        NORMAL,
        DOUBLE,
        THICK
    }

    record LineOffset(Direction offsetDirection, int level) {
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

    enum EightHorizontalDirection {
        SOUTH(Direction.SOUTH),
        SOUTH_WEST(RoadCorner.SOUTH_WEST),
        WEST(Direction.WEST),
        NORTH_WEST(RoadCorner.NORTH_WEST),
        NORTH(Direction.NORTH),
        NORTH_EAST(RoadCorner.NORTH_EAST),
        EAST(Direction.EAST),
        SOUTH_EAST(RoadCorner.SOUTH_EAST);

        private final Direction direction;
        private final RoadCorner cornerDirection;
        private final boolean isCorner;

        EightHorizontalDirection(Direction direction) {
            this.direction = direction;
            this.cornerDirection = null;
            this.isCorner = false;
        }

        EightHorizontalDirection(RoadCorner cornerDirection) {
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

        static EightHorizontalDirection of(RoadCorner cornerDirection) {
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

        Optional<RoadCorner> right() {
            return isCorner ? Optional.of(cornerDirection) : Optional.empty();
        }
    }

    enum WhetherConnected {
        NOT_CONNECTED,
        MAY_CONNECT,
        CONNECTED
    }

    record RoadConnectionState(WhetherConnected whetherConnected, LineColor lineColor, EightHorizontalDirection direction, LineType lineType, LineOffset lineOffset) {
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
