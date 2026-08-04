package com.beigu.yunbeiuc.block.custom.road;

import com.beigu.yunbeiuc.block.RoadBlocks;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 具有自动连接功能的道路方块。
 * <p>
 * 在放置、右键点击或邻近方块更新时，检测四周道路的连接状态（颜色、类型、偏移），并将方块状态自动转换为
 * 对应的直线 / 直角 / 斜线 / T 形 / 十字等标线道路方块。
 * <p>
 * 本实现不要求其它道路方块实现任何接口，而是通过注册 ID（{@link Block} 对象）
 * 识别邻居方块，并按各自模型编码推断其连接方向、颜色、类型与偏移。
 *
 * @see RoadWithAutoBevelLine
 * @see RoadWithAutoRightangleLine
 */
public abstract class RoadWithAutoLine extends Block {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoadWithAutoLine.class);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    /** 自动连接的类型，分为直角和 45° 斜线。 */
    protected final RoadAutoLineType type;

    protected RoadWithAutoLine(Settings settings, RoadAutoLineType type) {
        super(settings);
        this.type = type;
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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
            && (blockItem.getBlock() instanceof RoadWithAutoLine || blockItem.getBlock() instanceof RoadSlabWithAutoLine)
            && !Direction.Type.VERTICAL.test(hit.getSide())) {
            // 手持自动标线方块时放行，允许继续放置。
            return ActionResult.PASS;
        }
        world.setBlockState(pos, tryMakeState(this.type, getConnectionStateMap(world, pos), state, pos), 2);
        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        // 屏蔽上下方的更新与因方块消失（变空气）产生的更新。
        if (!sourcePos.equals(pos.up())
            && !sourcePos.equals(pos.down())
            && !(world.getBlockState(sourcePos).getBlock() instanceof AirBlock)) {
            world.setBlockState(pos, tryMakeState(this.type, getConnectionStateMap(world, pos), state, pos), 2);
        }
    }

    // ============================================================
    // 状态转换逻辑
    // ============================================================

    /**
     * 根据附近的连接状态自动产生一个新的方块状态。
     * <p>本方法以普通道路方块作为目标方块；slab 子类可在外部把返回的方块替换为对应的 slab 方块。
     */
    static BlockState makeState(RoadAutoLineType type, EnumMap<Direction, RoadConnectionState> connectionStateMap, BlockState defaultState) {
        int connected = 0;
        for (Map.Entry<Direction, RoadConnectionState> e : connectionStateMap.entrySet()) {
            if (e.getValue().mayConnect()) {
                connected++;
            }
        }
        switch (connected) {
            case 0:
                // 全都不连接的情况。
                return RoadBlocks.ROAD_BLOCK.get().getDefaultState();
            case 4: {
                // 全都连接的情况。至少两侧道路为黄色则返回黄色十字形道路，否则返回白色十字形道路。
                final int sumYellow = connectionStateMap.values().stream().mapToInt(state -> state.lineColor() == LineColor.YELLOW ? 1 : 0).sum();
                final boolean yellow = sumYellow >= 2;
                // 考虑使用双斜线搭配直线的情况。
                for (Direction direction : Direction.Type.HORIZONTAL) {
                    final EightHorizontalDirection direction1 = connectionStateMap.get(direction.rotateYClockwise()).direction();
                    final EightHorizontalDirection direction2 = connectionStateMap.get(direction.rotateYCounterclockwise()).direction();
                    if (direction1.right().map(cornerDirection -> cornerDirection.hasDirection(direction)).orElse(false)
                        && direction2.right().map(cornerDirection -> cornerDirection.hasDirection(direction)).orElse(false)) {
                        return (yellow ? RoadBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE : RoadBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE).get()
                            .getDefaultState().with(FACING, direction);
                    }
                }
                return (yellow ? RoadBlocks.ROAD_WITH_YELLOW_CROSS_LINE : RoadBlocks.ROAD_WITH_WHITE_CROSS_LINE).get().getDefaultState();
            }
            case 2:
                // 仅有两种方向连接：可能相对，也可能相邻。
                for (Map.Entry<Direction, RoadConnectionState> entry : connectionStateMap.entrySet()) {
                    final RoadConnectionState connectionState = entry.getValue();
                    if (!connectionState.mayConnect()) {
                        continue;
                    }
                    final Direction direction = entry.getKey();

                    // 相邻的一个有连接的道路的方向及其连接状态。
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
                        // 两个相对方向都连接了标线，连接成直线。
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
                            // 不确定的情况在后续循环中完成。
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
                        }).get().getDefaultState().with(FACING, direction);
                    } else {
                        // 两个相邻方向都连接了标线，连接成直角或斜线。
                        if (type == RoadAutoLineType.RIGHT_ANGLE) {
                            // 考虑带有偏移的直角的情况。
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
                                return block.getDefaultState().with(FACING, adjacentDirection);
                            } else if (connectionState.lineColor() == LineColor.WHITE && adjacentState.lineColor() == LineColor.WHITE) {
                                if (connectionState.lineType() == LineType.THICK && adjacentState.lineType() == LineType.NORMAL) {
                                    return RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get().getDefaultState().with(FACING, adjacentDirection);
                                } else if (connectionState.lineType() == LineType.NORMAL && adjacentState.lineType() == LineType.THICK) {
                                    return RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get().getDefaultState().with(FACING, direction);
                                } else if (connectionState.lineType() == LineType.NORMAL && adjacentState.lineType() == LineType.NORMAL) {
                                    return RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE.get().getDefaultState().with(FACING, cornerToFacing(HorizontalCornerDirection.fromDirections(direction, adjacentDirection)));
                                }
                            }
                        }

                        if (connectionState.lineColor() == adjacentState.lineColor() || adjacentState.lineColor() == LineColor.UNKNOWN) {
                            final LineType lineType = connectionState.sureConnect() && adjacentState.sureConnect() ? minEnum(connectionState.lineType(), adjacentState.lineType()) : connectionState.sureConnect() ? connectionState.lineType() : adjacentState.lineType();

                            // 先考虑有偏移的情况（双斜线）。
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
                            }).get().getDefaultState().with(FACING, cornerToFacing(HorizontalCornerDirection.fromDirections(direction, adjacentDirection)));
                        } else if (connectionState.lineColor() == LineColor.UNKNOWN) {
                            continue;
                        }

                        // 仍然不能决定时，返回白色标线。
                        return (switch (type) {
                            case BEVEL -> RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE;
                            case RIGHT_ANGLE -> RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE;
                        }).get().getDefaultState().with(FACING, cornerToFacing(HorizontalCornerDirection.fromDirections(direction, adjacentDirection)));
                    }
                }
                return defaultState;
            case 1:
                // 只有一个方向连接，为直线。
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
                        }).get().getDefaultState().with(FACING, entry.getKey());
                    }
                }
                return defaultState;
            case 3:
                for (Map.Entry<Direction, RoadConnectionState> entry : connectionStateMap.entrySet()) {
                    final RoadConnectionState unconnectedState = entry.getValue();
                    if (unconnectedState.mayConnect()) {
                        continue;
                    }

                    // 唯一没有被连接的方向的反方向，即 T 形线朝向的方向。
                    final Direction unconnectedDirection = entry.getKey();
                    final Direction facingDirection = unconnectedDirection.getOpposite();
                    final RoadConnectionState facingState = connectionStateMap.get(facingDirection);
                    if (facingState.direction() == null
                        || facingState.direction().left().isPresent()
                        || type != RoadAutoLineType.BEVEL) {
                        // 朝向的方向是正对方向而非角落方向，通常应连接 T 形线。
                        final RoadConnectionState stateLeft = connectionStateMap.get(facingDirection.rotateYCounterclockwise());
                        final RoadConnectionState stateRight = connectionStateMap.get(facingDirection.rotateYClockwise());

                        // 考虑双斜线的情况。
                        if (type == RoadAutoLineType.BEVEL
                            && stateLeft.direction().right().map(cornerDirection -> cornerDirection.hasDirection(facingDirection)).orElse(false)
                            && stateRight.direction().right().map(cornerDirection -> cornerDirection.hasDirection(facingDirection)).orElse(false)) {
                            if (facingState.lineColor() == LineColor.YELLOW && (stateLeft.lineColor() == LineColor.YELLOW || stateRight.lineColor() == LineColor.YELLOW)) {
                                return RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE.get().getDefaultState().with(FACING, facingDirection);
                            } else {
                                return RoadBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE.get().getDefaultState().with(FACING, facingDirection);
                            }
                        }

                        final LineOffset facingOffset = facingState.lineOffset();
                        if (stateLeft.lineColor() == stateRight.lineColor() && stateLeft.lineType() == stateRight.lineType()) {
                            if (stateLeft.lineColor() != facingState.lineColor() && facingState.lineColor() != LineColor.UNKNOWN) {
                                // 优先考虑混色部分，产生的均为 T 形线。
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
                                // 然后考虑同色。
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
                            // 存在左右两侧标线不等的情况。
                            final Block block = switch (facingState.lineColor()) {
                                case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE.get();
                                case WHITE, UNKNOWN, NONE -> RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE.get();
                            };
                            return composeJointLine(block, facingDirection, facingOffset);
                        }
                    } else if (facingState.direction().right().isPresent()) {
                        // 考虑连接直斜混线。
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
                        return block.getDefaultState().with(FACING, bevelConDirection);
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

    /**
     * 返回一个 T 形线方块状态，并且如果存在对应的偏移，则转化为相应的带偏移的线路。
     */
    private static BlockState composeJointLine(Block block, Direction facingDirection, LineOffset facingOffset) {
        if (facingOffset != null && facingOffset.level() == 2 && getOffsetTRoads().containsKey(block)) {
            final Block offsetSide = getOffsetTRoads().get(block);
            // 用户的偏移 T 形方块：FACING=f 时，侧线在 f 面，偏移方向为 f 顺时针旋转 90°。
            // 要使返回方块的偏移方向与输入偏移方向 facingOffset.offsetDirection() 一致，
            // 应令 FACING = 偏移方向逆时针旋转 90°。
            return offsetSide.getDefaultState().with(FACING, facingOffset.offsetDirection().rotateYCounterclockwise());
        } else {
            return block.getDefaultState().with(FACING, facingDirection);
        }
    }

    /** 不带偏移的 T 形线方块与其带偏移版本的对应关系。 */
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

    private static BlockState composeAngleLineWithOnePartOffset(LineColor lineColor, HorizontalCornerDirection facing, Direction.Axis axis, boolean isInwards) {
        final Block block;
        block = switch (lineColor) {
            case YELLOW -> (isInwards ? RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN.get() : RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT.get());
            default -> (isInwards ? RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN.get() : RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT.get());
        };
        return block.getDefaultState().with(FACING, facing.getDirectionInAxis(axis));
    }

    private static BlockState composeAngleLineWithTwoPartsOffset(LineColor lineColor, HorizontalCornerDirection facing, boolean isInwards, RoadAutoLineType type) {
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
        return block.getDefaultState().with(FACING, cornerToFacing(facing));
    }

    private static BlockState composeOffsetStraightLine(Direction offsetDirection, int offsetLevel, LineColor color) {
        return switch (offsetLevel) {
            case 114514 -> RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE.get().getDefaultState().with(FACING, offsetDirection.rotateYClockwise());
            case 2 -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_OFFSET_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_OFFSET_LINE.get();
                };
                yield block.getDefaultState().with(FACING, offsetDirection.rotateYClockwise());
            }
            case 1 -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE.get();
                };
                yield block.getDefaultState().with(FACING, offsetDirection.rotateYClockwise());
            }
            default -> {
                final Block block = switch (color) {
                    case YELLOW -> RoadBlocks.ROAD_WITH_YELLOW_LINE.get();
                    default -> RoadBlocks.ROAD_WITH_WHITE_LINE.get();
                };
                yield block.getDefaultState().with(FACING, dirForAxis(offsetDirection.rotateYClockwise().getAxis()));
            }
        };
    }

    // ============================================================
    // 连接状态检测（按注册 ID / Block 对象识别邻居）
    // ============================================================

    /**
     * 获取附近各水平方向的连接状态。
     */
    static EnumMap<Direction, RoadConnectionState> getConnectionStateMap(WorldAccess world, BlockPos pos0) {
        final EnumMap<Direction, RoadConnectionState> connectionStateMap = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            RoadConnectionState state = null;
            // 检查毗邻方块及其上下方。
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

    /** 根据某个方块的连接状态描述（函数式接口）。 */
    @FunctionalInterface
    interface RoadLineInfo {
        RoadConnectionState get(BlockState state, Direction direction);
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
        // 自动标线方块本身：不确定的连接。
        put(info, RoadBlocks.ROAD_WITH_AUTO_BEVEL_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_SLAB_WITH_AUTO_BEVEL_LINE.get(), auto());
        put(info, RoadBlocks.ROAD_SLAB_WITH_AUTO_RIGHTANGLE_LINE.get(), auto());
        // 直线。
        put(info, RoadBlocks.ROAD_WITH_WHITE_LINE.get(), straight(LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITE_DOUBLE_LINE.get(), straight(LineColor.WHITE, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_WHITE_THICK_LINE.get(), straight(LineColor.WHITE, LineType.THICK));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_LINE.get(), straight(LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE.get(), straight(LineColor.YELLOW, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_THICK_LINE.get(), straight(LineColor.YELLOW, LineType.THICK));
        // 偏移直线。
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_LINE.get(), offsetStraight(LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_LINE.get(), offsetStraight(LineColor.YELLOW, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE.get(), offsetStraight(LineColor.WHITE, LineType.NORMAL, 1));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE.get(), offsetStraight(LineColor.YELLOW, LineType.NORMAL, 1));
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE.get(), offsetStraight(LineColor.WHITE, LineType.DOUBLE, 114514));
        // 直角 / 斜线（单色）。
        put(info, RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE.get(), corner(LineColor.WHITE, LineType.NORMAL, false));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE.get(), corner(LineColor.YELLOW, LineType.NORMAL, false));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE.get(), corner(LineColor.WHITE, LineType.NORMAL, true));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE.get(), corner(LineColor.WHITE, LineType.DOUBLE, true));
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE.get(), corner(LineColor.WHITE, LineType.THICK, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_LINE.get(), corner(LineColor.YELLOW, LineType.NORMAL, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE.get(), corner(LineColor.YELLOW, LineType.DOUBLE, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE.get(), corner(LineColor.YELLOW, LineType.THICK, true));
        // 直角（两侧不同）。
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.THICK, LineColor.WHITE, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.THICK, LineColor.YELLOW, LineType.NORMAL));
        put(info, RoadBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.NORMAL, LineColor.YELLOW, LineType.DOUBLE));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE.get(), diffAngle(LineColor.WHITE, LineType.THICK, LineColor.YELLOW, LineType.DOUBLE));
        // 直角（一侧偏移）。
        put(info, RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT.get(), angleLineOnePartOffset(LineColor.WHITE, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN.get(), angleLineOnePartOffset(LineColor.WHITE, -2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT.get(), angleLineOnePartOffset(LineColor.YELLOW, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN.get(), angleLineOnePartOffset(LineColor.YELLOW, -2));
        // 直角 / 斜线（两侧偏移）。
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.WHITE, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.WHITE, -2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.YELLOW, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.YELLOW, -2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.WHITE, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.WHITE, -2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.YELLOW, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE.get(), angleLineTwoPartsOffset(LineColor.YELLOW, -2));
        // T 形线。
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
        // T 形线（一侧偏移）。
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.NORMAL, LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(LineColor.YELLOW, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.DOUBLE, LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.THICK, LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.THICK, LineColor.YELLOW, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE.get(), jointOffsetSide(LineColor.YELLOW, LineType.NORMAL, LineColor.WHITE, LineType.NORMAL, 2));
        put(info, RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE.get(), jointOffsetSide(LineColor.WHITE, LineType.NORMAL, LineColor.YELLOW, LineType.NORMAL, 2));
        // 十字形。
        put(info, RoadBlocks.ROAD_WITH_WHITE_CROSS_LINE.get(), cross(LineColor.WHITE));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_CROSS_LINE.get(), cross(LineColor.YELLOW));
        // 双斜线。
        put(info, RoadBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE.get(), biBevel(LineColor.WHITE, false));
        put(info, RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE.get(), biBevel(LineColor.YELLOW, false));
        put(info, RoadBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE.get(), biBevel(LineColor.WHITE, true));
        put(info, RoadBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE.get(), biBevel(LineColor.YELLOW, true));
        // 直线 + 斜线。
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

    /** 自动标线方块：不确定地连接，颜色与类型未知。 */
    private static RoadLineInfo auto() {
        return (state, direction) ->
            new RoadConnectionState(WhetherConnected.MAY_CONNECT, LineColor.UNKNOWN, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
    }

    /** 直线：沿 FACING 的轴连接。 */
    private static RoadLineInfo straight(LineColor color, LineType type) {
        return (state, direction) -> direction.getAxis() == state.get(FACING).getAxis()
            ? conn(color, EightHorizontalDirection.of(direction), type, null)
            : RoadConnectionState.empty();
    }

    /** 偏移直线：沿 FACING 的轴连接，偏移方向为 FACING 逆时针旋转 90°。 */
    private static RoadLineInfo offsetStraight(LineColor color, LineType type, int level) {
        return (state, direction) -> {
            if (direction.getAxis() != state.get(FACING).getAxis()) {
                return RoadConnectionState.empty();
            }
            return conn(color, EightHorizontalDirection.of(direction), type,
                new LineOffset(state.get(FACING).rotateYCounterclockwise(), level));
        };
    }

    /** 直角 / 斜线：连接 FACING 与 FACING 顺时针旋转 90° 两个方向。 */
    private static RoadLineInfo corner(LineColor color, LineType type, boolean isBevel) {
        return (state, direction) -> {
            final Direction facing = state.get(FACING);
            final HorizontalCornerDirection corner = facingToCorner(facing);
            if (!corner.hasDirection(direction)) {
                return RoadConnectionState.empty();
            }
            return conn(color,
                isBevel ? EightHorizontalDirection.of(corner.mirror(direction)) : EightHorizontalDirection.of(direction),
                type, null);
        };
    }

    /** 直角（两侧不同）：FACING 一侧为第二颜色/类型，另一侧为第一颜色/类型。 */
    private static RoadLineInfo diffAngle(LineColor color1, LineType type1, LineColor color2, LineType type2) {
        return (state, direction) -> {
            final Direction facing = state.get(FACING);
            if (direction == facing) {
                return conn(color2, EightHorizontalDirection.of(direction), type2, null);
            }
            if (direction == facing.rotateYClockwise()) {
                return conn(color1, EightHorizontalDirection.of(direction), type1, null);
            }
            return RoadConnectionState.empty();
        };
    }

    /** 直角（一侧偏移）：FACING 一侧居中，FACING 顺时针旋转 90° 一侧偏移。 */
    private static RoadLineInfo angleLineOnePartOffset(LineColor color, int offsetOutwards) {
        return (state, direction) -> {
            final Direction facing = state.get(FACING);
            if (direction == facing) {
                return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
            }
            if (direction == facing.rotateYClockwise()) {
                return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL,
                    LineOffset.of(facing.getOpposite(), offsetOutwards));
            }
            return RoadConnectionState.empty();
        };
    }

    /** 直角 / 斜线（两侧偏移）：两个方向均偏移。 */
    private static RoadLineInfo angleLineTwoPartsOffset(LineColor color, int offsetOutwards) {
        return (state, direction) -> {
            final Direction facing = state.get(FACING);
            final HorizontalCornerDirection corner = facingToCorner(facing);
            if (!corner.hasDirection(direction)) {
                return RoadConnectionState.empty();
            }
            final Direction offsetDir = corner.getDirectionInAxis(direction.rotateYClockwise().getAxis()).getOpposite();
            return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, LineOffset.of(offsetDir, offsetOutwards));
        };
    }

    /** T 形线：连接除 FACING 反方向以外的三个方向，FACING 一侧为侧线。 */
    private static RoadLineInfo joint(LineColor mainColor, LineType mainType, LineColor sideColor, LineType sideType) {
        return (state, direction) -> {
            final Direction facing = state.get(FACING);
            if (direction == facing.getOpposite()) {
                return RoadConnectionState.empty();
            }
            if (direction == facing) {
                return conn(sideColor, EightHorizontalDirection.of(direction), sideType, null);
            }
            return conn(mainColor, EightHorizontalDirection.of(direction), mainType, null);
        };
    }

    /** T 形线（一侧偏移）：侧线带偏移。偏移方向按用户模型为 FACING 顺时针旋转 90°。 */
    private static RoadLineInfo jointOffsetSide(LineColor mainColor, LineType mainType, LineColor sideColor, LineType sideType, int level) {
        return (state, direction) -> {
            final Direction facing = state.get(FACING);
            if (direction == facing.getOpposite()) {
                return RoadConnectionState.empty();
            }
            if (direction == facing) {
                return conn(sideColor, EightHorizontalDirection.of(direction), sideType,
                    new LineOffset(facing.rotateYClockwise(), level));
            }
            return conn(mainColor, EightHorizontalDirection.of(direction), mainType, null);
        };
    }

    /** 十字形：四个方向均连接。 */
    private static RoadLineInfo cross(LineColor color) {
        return (state, direction) -> conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
    }

    /** 双斜线：FACING 为直线，两侧为斜线。 */
    private static RoadLineInfo biBevel(LineColor color, boolean threeLayer) {
        return (state, direction) -> {
            final Direction facing = state.get(FACING);
            if (threeLayer) {
                if (facing == direction || facing == direction.getOpposite()) {
                    return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
                }
                return conn(color, EightHorizontalDirection.of(HorizontalCornerDirection.fromDirections(facing, direction.getOpposite())), LineType.NORMAL, null);
            } else {
                if (facing == direction) {
                    return conn(color, EightHorizontalDirection.of(direction), LineType.NORMAL, null);
                }
                if (facing != direction.getOpposite()) {
                    return conn(color, EightHorizontalDirection.of(HorizontalCornerDirection.fromDirections(facing, direction.getOpposite())), LineType.NORMAL, null);
                }
                return RoadConnectionState.empty();
            }
        };
    }

    /** 直线 + 斜线：FACING 及其反方向为直线，FACING 顺时针旋转 90° 为斜线。 */
    private static RoadLineInfo straightAndAngle(LineColor straightColor, LineType straightType, LineColor bevelColor, LineType bevelType) {
        return (state, direction) -> {
            final Direction facing = state.get(FACING);
            if (direction == facing || direction == facing.getOpposite()) {
                return conn(straightColor, EightHorizontalDirection.of(direction), straightType, null);
            }
            if (direction == facing.rotateYClockwise()) {
                return conn(bevelColor, EightHorizontalDirection.of(facingToCorner(facing).mirror(direction)), bevelType, null);
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

    /** 将角落方向转换为用户方块模型中的 FACING（连接 FACING 与 FACING 顺时针旋转 90° 两个方向）。 */
    private static Direction cornerToFacing(HorizontalCornerDirection corner) {
        if (corner.dir1.rotateYClockwise() == corner.dir2) {
            return corner.dir1;
        }
        return corner.dir2;
    }

    /** 将 FACING 转换为它对应的角落方向。 */
    private static HorizontalCornerDirection facingToCorner(Direction facing) {
        return HorizontalCornerDirection.fromDirections(facing, facing.rotateYClockwise());
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

    // ============================================================
    // 支持类型
    // ============================================================

    /** 道路自动连接的类型。 */
    enum RoadAutoLineType {
        /** 直角。 */
        RIGHT_ANGLE,
        /** 45° 斜线。 */
        BEVEL
    }

    /** 道路标线颜色。 */
    enum LineColor {
        WHITE,
        YELLOW,
        UNKNOWN,
        NONE
    }

    /** 道路标线类型。 */
    enum LineType {
        NORMAL,
        DOUBLE,
        THICK
    }

    /** 偏移直线记录。 */
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

    /** 水平角落方向（偏 45° 的方向）。 */
    enum HorizontalCornerDirection {
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

        HorizontalCornerDirection mirror(Direction direction) {
            final BlockMirror mirror = switch (direction.getAxis()) {
                case X -> BlockMirror.LEFT_RIGHT;
                case Z -> BlockMirror.FRONT_BACK;
                default -> BlockMirror.NONE;
            };
            return fromDirections(mirror.apply(dir1), mirror.apply(dir2));
        }
    }

    /** 八方向（四正方向 + 四角落方向）。 */
    enum EightHorizontalDirection {
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

    enum WhetherConnected {
        NOT_CONNECTED,
        MAY_CONNECT,
        CONNECTED
    }

    /** 表示一个道路在一个方向上的连接状态。 */
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
