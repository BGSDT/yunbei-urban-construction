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

    protected final AutoMarkMode mode;

    protected RoadWithAutoLine(BlockBehaviour.Properties properties, AutoMarkMode mode) {
        super(properties);
        this.mode = mode;
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
        world.setBlock(pos, attemptState(this.mode, collectLinkStates(world, pos), state, pos), 2);
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
        protected void neighborChangedCompat(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChangedCompat(state, world, pos, sourceBlock, sourcePos, notify);
        if (!sourcePos.equals(pos.above())
            && !sourcePos.equals(pos.below())
            && !(world.getBlockState(sourcePos).getBlock() instanceof AirBlock)) {
            world.setBlock(pos, attemptState(this.mode, collectLinkStates(world, pos), state, pos), 2);
        }
    }

    static BlockState buildState(AutoMarkMode mode, EnumMap<Direction, RoadLinkState> linkStates, BlockState defaultState) {
        int connected = 0;
        for (Map.Entry<Direction, RoadLinkState> e : linkStates.entrySet()) {
            if (e.getValue().canLink()) {
                connected++;
            }
        }
        switch (connected) {
            case 0:
                return RoadBlocks.ROAD_BLOCK.get().defaultBlockState();
            case 4: {
                final int sumYellow = linkStates.values().stream().mapToInt(st -> st.markColor() == MarkColor.YELLOW ? 1 : 0).sum();
                final boolean yellow = sumYellow >= 2;
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    final RoadDir8 cwDir = linkStates.get(direction.getClockWise()).dir();
                    final RoadDir8 ccwDir = linkStates.get(direction.getCounterClockWise()).dir();
                    if (cwDir.corner().map(cd -> cd.containsDir(direction)).orElse(false)
                        && ccwDir.corner().map(cd -> cd.containsDir(direction)).orElse(false)) {
                        return RoadOrientation.align(
                            (yellow ? RoadBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE : RoadBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE).get(),
                            direction);
                    }
                }
                return (yellow ? RoadBlocks.ROAD_WITH_YELLOW_CROSS_LINE : RoadBlocks.ROAD_WITH_WHITE_CROSS_LINE).get().defaultBlockState();
            }
            case 2:
                for (Map.Entry<Direction, RoadLinkState> entry : linkStates.entrySet()) {
                    final RoadLinkState linkState = entry.getValue();
                    if (!linkState.canLink()) {
                        continue;
                    }
                    final Direction direction = entry.getKey();

                    final Direction neighborDir;
                    final RoadLinkState neighborState;
                    if (linkStates.get(direction.getOpposite()).canLink()) {
                        neighborDir = direction.getOpposite();
                    } else if (linkStates.get(direction.getClockWise()).canLink()) {
                        neighborDir = direction.getClockWise();
                    } else {
                        neighborDir = direction.getCounterClockWise();
                    }
                    neighborState = linkStates.get(neighborDir);

                    if (neighborDir == direction.getOpposite()) {
                        final MarkColor color;
                        if (linkState.markColor() == neighborState.markColor()) {
                            color = linkState.markColor();
                        } else if (linkState.markColor() == MarkColor.UNKNOWN) {
                            color = neighborState.markColor();
                        } else if (neighborState.markColor() == MarkColor.UNKNOWN) {
                            color = linkState.markColor();
                        } else {
                            color = maxEnum(linkState.markColor(), neighborState.markColor());
                        }
                        final MarkPattern markPattern;
                        if (linkState.markPattern() == neighborState.markPattern()) {
                            markPattern = linkState.markPattern();
                        } else if (linkState.isConfirmed() == neighborState.isConfirmed()) {
                            markPattern = maxEnum(linkState.markPattern(), neighborState.markPattern());
                        } else {
                            markPattern = linkState.isConfirmed() ? linkState.markPattern() : neighborState.markPattern();
                        }

                        if (linkState.shiftAmount() != 0 && (neighborState.shiftAmount() != 0 || !neighborState.isConfirmed())) {
                            if ((linkState.shiftDir() == neighborState.shiftDir() && linkState.shiftAmount() == neighborState.shiftAmount() || !neighborState.isConfirmed())) {
                                return buildShiftedStraight(linkState.shiftDir(), linkState.shiftAmount(), color);
                            }
                        } else if (!linkState.isConfirmed() && neighborState.shiftAmount() != 0) {
                            continue;
                        }

                        return RoadOrientation.align((switch (color) {
                            case YELLOW -> switch (markPattern) {
                                case PAIRED -> RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE;
                                case WIDE -> RoadBlocks.ROAD_WITH_YELLOW_THICK_LINE;
                                default -> RoadBlocks.ROAD_WITH_YELLOW_LINE;
                            };
                            default -> switch (markPattern) {
                                case PAIRED -> RoadBlocks.ROAD_WITH_WHITE_DOUBLE_LINE;
                                case WIDE -> RoadBlocks.ROAD_WITH_WHITE_THICK_LINE;
                                default -> RoadBlocks.ROAD_WITH_WHITE_LINE;
                            };
                        }).get().defaultBlockState(), direction);
                    } else {
                        if (mode == AutoMarkMode.CROSS) {
                            if (linkState.markColor() == neighborState.markColor() || neighborState.markColor() == MarkColor.UNKNOWN) {
                                if (linkState.shiftAmount() == 2
                                    && (linkState.shiftAmount() == neighborState.shiftAmount() && (linkState.shiftDir() == neighborDir) == (neighborState.shiftDir() == direction) || !neighborState.isConfirmed())) {
                                    return buildAngleTwoPartShift(linkState.markColor(), RoadCorner.of(direction, neighborDir), linkState.shiftDir() == neighborDir, mode);
                                } else if (linkState.shiftAmount() == 2 && neighborState.shiftAmount() != 2) {
                                    return buildAngleOnePartShift(linkState.markColor(), RoadCorner.of(direction, neighborDir), neighborDir.getAxis(), linkState.shiftDir() == neighborDir);
                                } else if (linkState.shiftAmount() != 2 && neighborState.shiftAmount() == 2) {
                                    continue;
                                }
                            }

                            if (linkState.markColor() == MarkColor.YELLOW && neighborState.markColor() == MarkColor.WHITE) {
                                continue;
                            } else if (linkState.markColor() == MarkColor.WHITE && neighborState.markColor() == MarkColor.YELLOW) {
                                final Block block;
                                if (linkState.markPattern() == MarkPattern.WIDE && neighborState.markPattern() == MarkPattern.PAIRED) {
                                    block = RoadBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE.get();
                                } else if (neighborState.markPattern() == MarkPattern.PAIRED) {
                                    block = RoadBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE.get();
                                } else if (linkState.markPattern() == MarkPattern.WIDE) {
                                    block = RoadBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE.get();
                                } else {
                                    block = RoadBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE.get();
                                }
                                return RoadOrientation.align(block, neighborDir, direction.getClockWise() == neighborDir);
                            } else if (linkState.markColor() == MarkColor.WHITE && neighborState.markColor() == MarkColor.WHITE) {
                                if (linkState.markPattern() == MarkPattern.WIDE && neighborState.markPattern() == MarkPattern.SOLID) {
                                    return RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get(), neighborDir, direction.getClockWise() == neighborDir);
                                } else if (linkState.markPattern() == MarkPattern.SOLID && neighborState.markPattern() == MarkPattern.WIDE) {
                                    return RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get(), direction, neighborDir.getClockWise() == direction);
                                } else if (linkState.markPattern() == MarkPattern.SOLID && neighborState.markPattern() == MarkPattern.SOLID) {
                                    return RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE.get(), cornerToFacing(RoadCorner.of(direction, neighborDir)));
                                }
                            }
                        }

                        if (linkState.markColor() == neighborState.markColor() || neighborState.markColor() == MarkColor.UNKNOWN) {
                            final MarkPattern markPattern = linkState.isConfirmed() && neighborState.isConfirmed() ? minEnum(linkState.markPattern(), neighborState.markPattern()) : linkState.isConfirmed() ? linkState.markPattern() : neighborState.markPattern();

                            if (linkState.shiftAmount() == 2) {
                                boolean inward = linkState.shiftDir() == neighborDir;
                                if ((neighborState.shiftAmount() == 2 &&
                                    (neighborState.shiftDir() == direction) == inward
                                    || !neighborState.isConfirmed())) {
                                    return buildAngleTwoPartShift(linkState.markColor(), RoadCorner.of(direction, neighborDir), inward, mode);
                                }
                            }

                            return RoadOrientation.align((switch (linkState.markColor()) {
                                case YELLOW -> switch (mode) {
                                    case DIAGONAL -> switch (markPattern) {
                                        case PAIRED -> RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE;
                                        case WIDE -> RoadBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE;
                                        default -> RoadBlocks.ROAD_WITH_YELLOW_BEVEL_LINE;
                                    };
                                    case CROSS -> RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE;
                                };
                                case WHITE, NONE, UNKNOWN -> switch (mode) {
                                    case DIAGONAL -> switch (markPattern) {
                                        case PAIRED -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE;
                                        case WIDE -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE;
                                        default -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE;
                                    };
                                    case CROSS -> RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE;
                                };
                            }).get(), cornerToFacing(RoadCorner.of(direction, neighborDir)));
                        } else if (linkState.markColor() == MarkColor.UNKNOWN) {
                            continue;
                        }

                        return RoadOrientation.align((switch (mode) {
                            case DIAGONAL -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE;
                            case CROSS -> RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE;
                        }).get(), cornerToFacing(RoadCorner.of(direction, neighborDir)));
                    }
                }
                return defaultState;
            case 1:
                for (Map.Entry<Direction, RoadLinkState> entry : linkStates.entrySet()) {
                    final RoadLinkState linkState = entry.getValue();
                    if (linkState.canLink()) {
                        if (linkState.shiftAmount() != 0) {
                            return buildShiftedStraight(linkState.shiftDir(), linkState.shiftAmount(), linkState.markColor());
                        }
                        return RoadOrientation.align((switch (linkState.markColor()) {
                            case YELLOW -> switch (linkState.markPattern()) {
                                case WIDE -> RoadBlocks.ROAD_WITH_YELLOW_THICK_LINE;
                                case PAIRED -> RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE;
                                default -> RoadBlocks.ROAD_WITH_YELLOW_LINE;
                            };
                            default -> switch (linkState.markPattern()) {
                                case WIDE -> RoadBlocks.ROAD_WITH_WHITE_THICK_LINE;
                                case PAIRED -> RoadBlocks.ROAD_WITH_WHITE_DOUBLE_LINE;
                                default -> RoadBlocks.ROAD_WITH_WHITE_LINE;
                            };
                        }).get(), entry.getKey());
                    }
                }
                return defaultState;
            case 3:
                for (Map.Entry<Direction, RoadLinkState> entry : linkStates.entrySet()) {
                    final RoadLinkState noLinkState = entry.getValue();
                    if (noLinkState.canLink()) {
                        continue;
                    }

                    final Direction noLinkDir = entry.getKey();
                    final Direction facingDir = noLinkDir.getOpposite();
                    final RoadLinkState facingState = linkStates.get(facingDir);
                    if (facingState.dir() == null
                        || facingState.dir().cardinal().isPresent()
                        || mode != AutoMarkMode.DIAGONAL) {
                        final RoadLinkState stateLeft = linkStates.get(facingDir.getCounterClockWise());
                        final RoadLinkState stateRight = linkStates.get(facingDir.getClockWise());

                        if (mode == AutoMarkMode.DIAGONAL
                            && stateLeft.dir().corner().map(cd -> cd.containsDir(facingDir)).orElse(false)
                            && stateRight.dir().corner().map(cd -> cd.containsDir(facingDir)).orElse(false)) {
                            if (facingState.markColor() == MarkColor.YELLOW && (stateLeft.markColor() == MarkColor.YELLOW || stateRight.markColor() == MarkColor.YELLOW)) {
                                return RoadOrientation.align(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE.get(), facingDir);
                            } else {
                                return RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE.get(), facingDir);
                            }
                        }

                        final MarkShift facingShift = facingState.markShift();
                        if (stateLeft.markColor() == stateRight.markColor() && stateLeft.markPattern() == stateRight.markPattern()) {
                            if (stateLeft.markColor() != facingState.markColor() && facingState.markColor() != MarkColor.UNKNOWN) {
                                if (stateLeft.markColor() == MarkColor.WHITE && facingState.markColor() == MarkColor.YELLOW) {
                                    final Block block = switch (stateLeft.markPattern()) {
                                        case WIDE -> (facingState.markPattern() == MarkPattern.PAIRED ? RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE.get() : RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE.get());
                                        default -> (facingState.markPattern() == MarkPattern.PAIRED ? RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE.get() : RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE.get());
                                    };
                                    return buildTJunction(block, facingDir, facingShift);
                                }
                                if (stateLeft.markColor() == MarkColor.YELLOW && facingState.markColor() == MarkColor.WHITE) {
                                    return buildTJunction(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE.get(), facingDir, facingShift);
                                } else {
                                    return buildTJunction(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get(), facingDir, facingShift);
                                }
                            } else {
                                if (facingState.markColor() == MarkColor.YELLOW || (facingState.markColor() == MarkColor.UNKNOWN && stateLeft.markColor() == MarkColor.YELLOW)) {
                                    return buildTJunction(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get(), facingDir, facingShift);
                                } else {
                                    final Block block = switch (facingState.markPattern()) {
                                        case PAIRED -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE.get();
                                        case WIDE -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE.get();
                                        default -> switch (stateLeft.markPattern()) {
                                            case PAIRED -> RoadBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE.get();
                                            case WIDE -> RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE.get();
                                            default -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get();
                                        };
                                    };
                                    return buildTJunction(block, facingDir, facingShift);
                                }
                            }
                        } else {
                            final Block block = switch (facingState.markColor()) {
                                case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get();
                                case WHITE, UNKNOWN, NONE -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get();
                            };
                            return buildTJunction(block, facingDir, facingShift);
                        }
                    } else if (facingState.dir().corner().isPresent()) {
                        final RoadCorner facingStateDir = facingState.dir().corner().get();

                        final Direction diagConDir = facingStateDir.dirInAxis(facingDir.getClockWise().getAxis());
                        final RoadLinkState diagConState = linkStates.get(diagConDir);
                        final Direction diagNonDir = diagConDir.getOpposite();
                        final RoadLinkState diagNonState = linkStates.get(diagNonDir);
                        final Block block;
                        if (facingState.markColor() == MarkColor.YELLOW) {
                            if (diagNonState.markColor() == MarkColor.WHITE && diagNonState.markPattern() == MarkPattern.WIDE
                                && (diagConState.markColor() == MarkColor.WHITE && diagConState.markPattern() == MarkPattern.WIDE
                                || diagConState.markColor() == MarkColor.YELLOW)) {
                                block = RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE.get();
                            } else if (diagNonState.markColor() == MarkColor.YELLOW && diagNonState.markPattern() == MarkPattern.WIDE && diagConState.markColor() == MarkColor.YELLOW) {
                                block = RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE.get();
                            } else if (diagNonState.markColor() == MarkColor.WHITE) {
                                block = RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE.get();
                            } else {
                                block = RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE.get();
                            }
                        } else {
                            if (diagNonState.markColor() == MarkColor.YELLOW && diagNonState.markPattern() == MarkPattern.WIDE
                                && (diagConState.markColor() != MarkColor.YELLOW || diagConState.markPattern() == MarkPattern.WIDE)) {
                                block = RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE.get();
                            } else if (diagNonState.markPattern() == MarkPattern.WIDE && diagConState.markColor() != MarkColor.YELLOW) {
                                block = RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE.get();
                            } else if (diagNonState.markColor() == MarkColor.YELLOW) {
                                block = RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE.get();
                            } else {
                                block = RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE.get();
                            }
                        }
                        return RoadOrientation.align(block, diagConDir);
                    }
                }
                return defaultState;
            default:
                throw new IllegalStateException("Illegal connected number: " + connected);
        }
    }

    static BlockState attemptState(AutoMarkMode mode, EnumMap<Direction, RoadLinkState> linkStates, BlockState defaultState, BlockPos pos) {
        try {
            return buildState(mode, linkStates, defaultState);
        } catch (Throwable throwable) {
            LOGGER.error("An error was found when converting road block at {}:", pos, throwable);
            return defaultState;
        }
    }

    private static BlockState buildTJunction(Block block, Direction facingDir, MarkShift facingShift) {
        if (facingShift != null && facingShift.shiftAmount() == 2 && getOffsetTRoads().containsKey(block)) {
            final Block offsetSide = getOffsetTRoads().get(block);
            return RoadOrientation.align(offsetSide, facingShift.shiftDir().getCounterClockWise());
        } else {
            return RoadOrientation.align(block, facingDir);
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

    private static BlockState buildAngleOnePartShift(MarkColor markColor, RoadCorner facing, Direction.Axis axis, boolean inward) {
        final Block block;
        block = switch (markColor) {
            case YELLOW -> (inward ? RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN.get() : RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT.get());
            default -> (inward ? RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN.get() : RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT.get());
        };
        return RoadOrientation.align(block, facing.dirInAxis(axis));
    }

    private static BlockState buildAngleTwoPartShift(MarkColor markColor, RoadCorner facing, boolean inward, AutoMarkMode mode) {
        final Block block;
        if (mode == AutoMarkMode.CROSS) {
            block = switch (markColor) {
                case YELLOW -> (inward ? RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE.get() : RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE.get());
                default -> (inward ? RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE.get() : RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE.get());
            };
        } else {
            block = switch (markColor) {
                case YELLOW -> (inward ? RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get() : RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get());
                default -> (inward ? RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get() : RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get());
            };
        }
        return RoadOrientation.align(block, cornerToFacing(facing));
    }

    private static BlockState buildShiftedStraight(Direction shiftDir, int shiftAmount, MarkColor color) {
        return switch (shiftAmount) {
            case PAIRED_COLOR_LEVEL -> RoadOrientation.align(RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE.get(), shiftDir.getClockWise());
            case 2 -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_OFFSET_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_OFFSET_LINE.get();
                };
                yield RoadOrientation.align(block, shiftDir.getClockWise());
            }
            case 1 -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE.get();
                };
                yield RoadOrientation.align(block, shiftDir.getClockWise());
            }
            default -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_LINE.get();
                };
                yield RoadOrientation.align(block, dirForAxis(shiftDir.getClockWise().getAxis()));
            }
        };
    }

    static EnumMap<Direction, RoadLinkState> collectLinkStates(LevelAccessor world, BlockPos pos0) {
        final EnumMap<Direction, RoadLinkState> linkStates = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            RoadLinkState state = null;
            for (BlockPos pos : new BlockPos[]{pos0, pos0.above(), pos0.below()}) {
                final BlockState nextState = world.getBlockState(pos.relative(direction, 1));
                final RoadBlockInfo info = getBlockInfo().get(nextState.getBlock());
                if (info != null) {
                    state = info.read(nextState, direction.getOpposite());
                    break;
                }
            }
            linkStates.put(direction, state == null ? RoadLinkState.none() : state);
        }
        return linkStates;
    }

    @FunctionalInterface
    interface RoadBlockInfo {
        RoadLinkState read(BlockState state, Direction direction);
    }

    static Map<Block, RoadBlockInfo> getBlockInfo() {
        return BlockInfoHolder.INFO;
    }

    private static final class BlockInfoHolder {
        static final Map<Block, RoadBlockInfo> INFO = buildInfo();
    }

    private static void put(Map<Block, RoadBlockInfo> info, Block block, RoadBlockInfo roadBlockInfo) {
        info.put(block, roadBlockInfo);
    }

    private static Map<Block, RoadBlockInfo> buildInfo() {
        final Map<Block, RoadBlockInfo> info = new HashMap<>();
        put(info, RoadBlocks.ROAD_WITH_AUTO_BEVEL_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_SLAB_WITH_AUTO_BEVEL_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_SLAB_WITH_AUTO_RIGHTANGLE_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_WITH_WHITE_LINE.get(), straight(MarkColor.WHITE, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITE_DOUBLE_LINE.get(), straight(MarkColor.WHITE, MarkPattern.PAIRED));
        put(info, RoadBlocks.ROAD_WITH_WHITE_THICK_LINE.get(), straight(MarkColor.WHITE, MarkPattern.WIDE));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_LINE.get(), straight(MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE.get(), straight(MarkColor.YELLOW, MarkPattern.PAIRED));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_THICK_LINE.get(), straight(MarkColor.YELLOW, MarkPattern.WIDE));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_LINE.get(), offsetStraight(MarkColor.WHITE, MarkPattern.SOLID, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_LINE.get(), offsetStraight(MarkColor.YELLOW, MarkPattern.SOLID, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE.get(), offsetStraight(MarkColor.WHITE, MarkPattern.SOLID, 1));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE.get(), offsetStraight(MarkColor.YELLOW, MarkPattern.SOLID, 1));
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE.get(), offsetStraight(MarkColor.WHITE, MarkPattern.PAIRED, PAIRED_COLOR_LEVEL));
        put(info, RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE.get(), corner(MarkColor.WHITE, MarkPattern.SOLID, false));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE.get(), corner(MarkColor.YELLOW, MarkPattern.SOLID, false));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE.get(), corner(MarkColor.WHITE, MarkPattern.SOLID, true));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE.get(), corner(MarkColor.WHITE, MarkPattern.PAIRED, true));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE.get(), corner(MarkColor.WHITE, MarkPattern.WIDE, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_LINE.get(), corner(MarkColor.YELLOW, MarkPattern.SOLID, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE.get(), corner(MarkColor.YELLOW, MarkPattern.PAIRED, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE.get(), corner(MarkColor.YELLOW, MarkPattern.WIDE, true));
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE.get(), diffAngle(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get(), diffAngle(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.WHITE, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE.get(), diffAngle(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE.get(), diffAngle(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.YELLOW, MarkPattern.PAIRED));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE.get(), diffAngle(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.YELLOW, MarkPattern.PAIRED));
        put(info, RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT.get(), angleLineOnePartShift(MarkColor.WHITE, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN.get(), angleLineOnePartShift(MarkColor.WHITE, -2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT.get(), angleLineOnePartShift(MarkColor.YELLOW, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN.get(), angleLineOnePartShift(MarkColor.YELLOW, -2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE.get(), angleLineTwoPartShift(MarkColor.WHITE, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE.get(), angleLineTwoPartShift(MarkColor.WHITE, -2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE.get(), angleLineTwoPartShift(MarkColor.YELLOW, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE.get(), angleLineTwoPartShift(MarkColor.YELLOW, -2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartShift(MarkColor.WHITE, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartShift(MarkColor.WHITE, -2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartShift(MarkColor.YELLOW, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartShift(MarkColor.YELLOW, -2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get(), joint(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.WHITE, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get(), joint(MarkColor.YELLOW, MarkPattern.SOLID, MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE.get(), joint(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.WHITE, MarkPattern.PAIRED));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE.get(), joint(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.WHITE, MarkPattern.WIDE));
        put(info, RoadBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE.get(), joint(MarkColor.WHITE, MarkPattern.PAIRED, MarkColor.WHITE, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE.get(), joint(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.WHITE, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE.get(), joint(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE.get(), joint(MarkColor.YELLOW, MarkPattern.SOLID, MarkColor.WHITE, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE.get(), joint(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.YELLOW, MarkPattern.PAIRED));
        put(info, RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE.get(), joint(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE.get(), joint(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.YELLOW, MarkPattern.PAIRED));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.WHITE, MarkPattern.SOLID, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(MarkColor.YELLOW, MarkPattern.SOLID, MarkColor.YELLOW, MarkPattern.SOLID, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(MarkColor.WHITE, MarkPattern.PAIRED, MarkColor.WHITE, MarkPattern.SOLID, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.WHITE, MarkPattern.SOLID, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE.get(), jointOffsetSide(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.YELLOW, MarkPattern.SOLID, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE.get(), jointOffsetSide(MarkColor.YELLOW, MarkPattern.SOLID, MarkColor.WHITE, MarkPattern.SOLID, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE.get(), jointOffsetSide(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.YELLOW, MarkPattern.SOLID, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_CROSS_LINE.get(), cross(MarkColor.WHITE));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_CROSS_LINE.get(), cross(MarkColor.YELLOW));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE.get(), biBevel(MarkColor.WHITE, false));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE.get(), biBevel(MarkColor.YELLOW, false));
        put(info, RoadBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE.get(), biBevel(MarkColor.WHITE, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE.get(), biBevel(MarkColor.YELLOW, true));
        put(info, RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE.get(), straightAndAngle(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.WHITE, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE.get(), straightAndAngle(MarkColor.YELLOW, MarkPattern.SOLID, MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE.get(), straightAndAngle(MarkColor.WHITE, MarkPattern.SOLID, MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE.get(), straightAndAngle(MarkColor.YELLOW, MarkPattern.SOLID, MarkColor.WHITE, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE.get(), straightAndAngle(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.WHITE, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_LINE.get(), straightAndAngle(MarkColor.YELLOW, MarkPattern.WIDE, MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE.get(), straightAndAngle(MarkColor.WHITE, MarkPattern.WIDE, MarkColor.YELLOW, MarkPattern.SOLID));
        put(info, RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE.get(), straightAndAngle(MarkColor.YELLOW, MarkPattern.WIDE, MarkColor.WHITE, MarkPattern.SOLID));
        return info;
    }

    private static RoadBlockInfo auto() {
        return (state, direction) ->
            new RoadLinkState(LinkStatus.POSSIBLE, MarkColor.UNKNOWN, RoadDir8.of(direction), MarkPattern.SOLID, null);
    }

    private static RoadBlockInfo straight(MarkColor color, MarkPattern pattern) {
        return (state, direction) -> direction.getAxis() == RoadOrientation.axisOf(state)
            ? linked(color, RoadDir8.of(direction), pattern, null)
            : RoadLinkState.none();
    }

    private static RoadBlockInfo offsetStraight(MarkColor color, MarkPattern pattern, int amount) {
        return (state, direction) -> {
            if (direction.getAxis() != RoadOrientation.axisOf(state)) {
                return RoadLinkState.none();
            }
            return linked(color, RoadDir8.of(direction), pattern,
                new MarkShift(RoadOrientation.directionOf(state).getCounterClockWise(), amount));
        };
    }

    private static RoadBlockInfo corner(MarkColor color, MarkPattern pattern, boolean isBevel) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            final RoadCorner corner = facingToCorner(facing);
            if (!corner.containsDir(direction)) {
                return RoadLinkState.none();
            }
            return linked(color,
                isBevel ? RoadDir8.of(corner.mirrored(direction)) : RoadDir8.of(direction),
                pattern, null);
        };
    }

    private static RoadBlockInfo diffAngle(MarkColor color1, MarkPattern pattern1, MarkColor color2, MarkPattern pattern2) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing) {
                return linked(color2, RoadDir8.of(direction), pattern2, null);
            }
            if (direction == facing.getClockWise()) {
                return linked(color1, RoadDir8.of(direction), pattern1, null);
            }
            return RoadLinkState.none();
        };
    }

    private static RoadBlockInfo angleLineOnePartShift(MarkColor color, int shiftOutward) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing) {
                return linked(color, RoadDir8.of(direction), MarkPattern.SOLID, null);
            }
            if (direction == facing.getClockWise()) {
                return linked(color, RoadDir8.of(direction), MarkPattern.SOLID,
                    MarkShift.of(facing.getOpposite(), shiftOutward));
            }
            return RoadLinkState.none();
        };
    }

    private static RoadBlockInfo angleLineTwoPartShift(MarkColor color, int shiftOutward) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            final RoadCorner corner = facingToCorner(facing);
            if (!corner.containsDir(direction)) {
                return RoadLinkState.none();
            }
            final Direction shiftDir = corner.dirInAxis(direction.getClockWise().getAxis()).getOpposite();
            return linked(color, RoadDir8.of(direction), MarkPattern.SOLID, MarkShift.of(shiftDir, shiftOutward));
        };
    }

    private static RoadBlockInfo joint(MarkColor mainColor, MarkPattern mainPattern, MarkColor sideColor, MarkPattern sidePattern) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing.getOpposite()) {
                return RoadLinkState.none();
            }
            if (direction == facing) {
                return linked(sideColor, RoadDir8.of(direction), sidePattern, null);
            }
            return linked(mainColor, RoadDir8.of(direction), mainPattern, null);
        };
    }

    private static RoadBlockInfo jointOffsetSide(MarkColor mainColor, MarkPattern mainPattern, MarkColor sideColor, MarkPattern sidePattern, int amount) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing.getOpposite()) {
                return RoadLinkState.none();
            }
            if (direction == facing) {
                return linked(sideColor, RoadDir8.of(direction), sidePattern,
                    new MarkShift(facing.getClockWise(), amount));
            }
            return linked(mainColor, RoadDir8.of(direction), mainPattern, null);
        };
    }

    private static RoadBlockInfo cross(MarkColor color) {
        return (state, direction) -> linked(color, RoadDir8.of(direction), MarkPattern.SOLID, null);
    }

    private static RoadBlockInfo biBevel(MarkColor color, boolean threeLayer) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (threeLayer) {
                if (facing == direction || facing == direction.getOpposite()) {
                    return linked(color, RoadDir8.of(direction), MarkPattern.SOLID, null);
                }
                return linked(color, RoadDir8.of(RoadCorner.of(facing, direction.getOpposite())), MarkPattern.SOLID, null);
            } else {
                if (facing == direction) {
                    return linked(color, RoadDir8.of(direction), MarkPattern.SOLID, null);
                }
                if (facing != direction.getOpposite()) {
                    return linked(color, RoadDir8.of(RoadCorner.of(facing, direction.getOpposite())), MarkPattern.SOLID, null);
                }
                return RoadLinkState.none();
            }
        };
    }

    private static RoadBlockInfo straightAndAngle(MarkColor straightColor, MarkPattern straightPattern, MarkColor bevelColor, MarkPattern bevelPattern) {
        return (state, direction) -> {
            final Direction facing = RoadOrientation.directionOf(state);
            if (direction == facing || direction == facing.getOpposite()) {
                return linked(straightColor, RoadDir8.of(direction), straightPattern, null);
            }
            if (direction == facing.getClockWise()) {
                return linked(bevelColor, RoadDir8.of(facingToCorner(facing).mirrored(direction)), bevelPattern, null);
            }
            return RoadLinkState.none();
        };
    }

    private static RoadLinkState linked(MarkColor color, RoadDir8 direction, MarkPattern pattern, MarkShift shift) {
        return new RoadLinkState(LinkStatus.LINKED, color, direction, pattern, shift);
    }

    private static Direction cornerToFacing(RoadCorner corner) {
        if (corner.side1.getClockWise() == corner.side2) {
            return corner.side1;
        }
        return corner.side2;
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

    enum AutoMarkMode {
        CROSS,
        DIAGONAL
    }

    enum MarkColor {
        WHITE,
        YELLOW,
        UNKNOWN,
        NONE
    }

    enum MarkPattern {
        SOLID,
        PAIRED,
        WIDE
    }

    record MarkShift(Direction shiftDir, int shiftAmount) {
        MarkShift {
            if (shiftAmount < 0) {
                throw new IllegalArgumentException();
            }
        }

        static MarkShift of(Direction direction, int amount) {
            if (amount == 0) {
                return null;
            } else if (amount > 0) {
                return new MarkShift(direction, amount);
            } else {
                return new MarkShift(direction.getOpposite(), -amount);
            }
        }
    }

    enum RoadDir8 {
        SOUTH(Direction.SOUTH),
        SOUTH_WEST(RoadCorner.SOUTH_WEST),
        WEST(Direction.WEST),
        NORTH_WEST(RoadCorner.NORTH_WEST),
        NORTH(Direction.NORTH),
        NORTH_EAST(RoadCorner.NORTH_EAST),
        EAST(Direction.EAST),
        SOUTH_EAST(RoadCorner.SOUTH_EAST);

        private final Direction direction;
        private final RoadCorner cornerDir;
        private final boolean isCorner;

        RoadDir8(Direction direction) {
            this.direction = direction;
            this.cornerDir = null;
            this.isCorner = false;
        }

        RoadDir8(RoadCorner cornerDir) {
            this.direction = null;
            this.cornerDir = cornerDir;
            this.isCorner = true;
        }

        static RoadDir8 of(Direction direction) {
            return switch (direction) {
                case NORTH -> NORTH;
                case EAST -> EAST;
                case SOUTH -> SOUTH;
                default -> WEST;
            };
        }

        static RoadDir8 of(RoadCorner cornerDir) {
            return switch (cornerDir) {
                case SOUTH_WEST -> SOUTH_WEST;
                case NORTH_WEST -> NORTH_WEST;
                case NORTH_EAST -> NORTH_EAST;
                case SOUTH_EAST -> SOUTH_EAST;
            };
        }

        Optional<Direction> cardinal() {
            return isCorner ? Optional.empty() : Optional.of(direction);
        }

        Optional<RoadCorner> corner() {
            return isCorner ? Optional.of(cornerDir) : Optional.empty();
        }
    }

    enum LinkStatus {
        UNLINKED,
        POSSIBLE,
        LINKED
    }

    record RoadLinkState(LinkStatus linkStatus, MarkColor markColor, RoadDir8 dir, MarkPattern markPattern, MarkShift markShift) {
        static RoadLinkState none() {
            return new RoadLinkState(LinkStatus.UNLINKED, MarkColor.NONE, null, MarkPattern.SOLID, null);
        }

        boolean canLink() {
            return this.linkStatus.ordinal() >= LinkStatus.POSSIBLE.ordinal();
        }

        boolean isConfirmed() {
            return this.linkStatus == LinkStatus.LINKED;
        }

        Direction shiftDir() {
            return markShift == null ? null : markShift.shiftDir();
        }

        int shiftAmount() {
            return markShift == null ? 0 : markShift.shiftAmount();
        }
    }
}
