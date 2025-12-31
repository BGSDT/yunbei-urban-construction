package com.beigu.yunbeiuc.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;

public class RoadMedianBarrierBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    public RoadMedianBarrierBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(TYPE, Type.SINGLE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
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

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        if (direction == facing.rotateYClockwise() || direction == facing.rotateYCounterclockwise()) {
            return getAdvancedRelatedBlockState(state, world, pos, facing);
        }
        return state;
    }

    private BlockState getAdvancedRelatedBlockState(BlockState state, WorldAccess world, BlockPos pos, Direction direction) {
        List<BlockPos> connectedBarriers = findConnectedBarriers(world, pos, direction);
        int totalConnected = connectedBarriers.size();

        if (totalConnected == 1) {
            return state.with(TYPE, Type.SINGLE);
        }

        int currentIndex = findCurrentIndexInSequence(connectedBarriers, pos);
        if (currentIndex == -1) {
            return state.with(TYPE, Type.SINGLE);
        }

        return determineBarrierType(state, currentIndex, totalConnected);
    }

    /**
     * 修复：LEFT和RIGHT方向修正
     */
    private List<BlockPos> findConnectedBarriers(WorldAccess world, BlockPos startPos, Direction direction) {
        List<BlockPos> connected = new ArrayList<>();
        connected.add(startPos);

        // 修正：左侧搜索（逆时针方向）
        searchInDirection(world, startPos, direction, connected, direction.rotateYCounterclockwise());
        // 修正：右侧搜索（顺时针方向）
        searchInDirection(world, startPos, direction, connected, direction.rotateYClockwise());

        return connected;
    }

    private void searchInDirection(WorldAccess world, BlockPos startPos, Direction facing, List<BlockPos> connected, Direction searchDirection) {
        BlockPos currentPos = startPos;

        while (true) {
            currentPos = currentPos.offset(searchDirection);
            BlockState neighborState = world.getBlockState(currentPos);

            if (isValidBarrierConnection(neighborState, facing)) {
                // 修正：根据搜索方向正确确定左右
                if (searchDirection == facing.rotateYCounterclockwise()) {
                    // 逆时针方向是左侧，添加到开头
                    connected.add(0, currentPos);
                } else {
                    // 顺时针方向是右侧，添加到结尾
                    connected.add(currentPos);
                }
            } else {
                break;
            }
        }
    }

    private boolean isValidBarrierConnection(BlockState state, Direction expectedFacing) {
        return state.getBlock() == this && state.get(FACING) == expectedFacing;
    }

    private int findCurrentIndexInSequence(List<BlockPos> connectedBarriers, BlockPos currentPos) {
        for (int i = 0; i < connectedBarriers.size(); i++) {
            if (connectedBarriers.get(i).equals(currentPos)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 修复：LEFT和RIGHT分配逻辑
     */
    private BlockState determineBarrierType(BlockState state, int index, int total) {
        if (total == 1) {
            return state.with(TYPE, Type.SINGLE);
        } else if (total == 2) {
            // 修正：第一个是LEFT，第二个是RIGHT
            return state.with(TYPE, index == 0 ? Type.LEFT : Type.RIGHT);
        } else {
            // 修正：序列中的第一个是LEFT，最后一个是RIGHT
            if (index == 0) {
                return state.with(TYPE, Type.LEFT);
            } else if (index == total - 1) {
                return state.with(TYPE, Type.RIGHT);
            } else if (total % 2 == 1 && index == total / 2) {
                return state.with(TYPE, Type.CENTER);
            } else {
                return state.with(TYPE, Type.MIDDLE);
            }
        }
    }

    public enum Type implements StringIdentifiable {
        SINGLE("single"),
        LEFT("left"),
        RIGHT("right"),
        MIDDLE("middle"),
        CENTER("center");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}