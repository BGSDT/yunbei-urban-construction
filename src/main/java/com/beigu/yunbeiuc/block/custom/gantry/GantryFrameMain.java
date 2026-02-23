package com.beigu.yunbeiuc.block.custom.gantry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class GantryFrameMain extends Block {
    public static final EnumProperty<GantryFrameMainType> MAIN_TYPE = EnumProperty.of("main_type", GantryFrameMainType.class);

    public GantryFrameMain(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(MAIN_TYPE, GantryFrameMainType.GANTRY_FRAME_MAIN_1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MAIN_TYPE);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return this.updateMainType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        // 更新当前方块和左右相邻的相同方块
        updateConnectedFrames(world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        // 当方块被破坏时，更新相邻的相同方块
        if (!state.isOf(newState.getBlock())) {
            updateConnectedFrames(world, pos);
        }
    }

    private BlockState updateMainType(BlockState state, WorldAccess world, BlockPos pos) {
        // 找到最左边的方块
        BlockPos leftPos = findLeftFrame(world, pos);

        // 从左边开始向右计数，计算当前方块的位置
        int positionFromLeft = getPositionFromLeft(world, leftPos, pos);

        // 从左往右数：位置1、3、5...显示类型1，位置2、4、6...显示类型2
        GantryFrameMainType newType = (positionFromLeft % 2 == 1) ?
                GantryFrameMainType.GANTRY_FRAME_MAIN_1 : GantryFrameMainType.GANTRY_FRAME_MAIN_2;

        return state.with(MAIN_TYPE, newType);
    }

    private BlockPos findLeftFrame(WorldAccess world, BlockPos startPos) {
        BlockPos currentPos = startPos;

        // 向左查找，直到找不到相同的方块
        BlockPos leftPos = currentPos.west();
        while (world.getBlockState(leftPos).getBlock() instanceof GantryFrameMain) {
            currentPos = leftPos;
            leftPos = currentPos.west();
        }

        return currentPos;
    }

    private int getPositionFromLeft(WorldAccess world, BlockPos leftPos, BlockPos targetPos) {
        int position = 1; // 从1开始计数（左边第一个方块）
        BlockPos currentPos = leftPos;

        // 从左边向右遍历，直到找到目标位置
        while (!currentPos.equals(targetPos)) {
            currentPos = currentPos.east();
            if (!(world.getBlockState(currentPos).getBlock() instanceof GantryFrameMain)) {
                return 1; // 如果链条断裂，返回默认值
            }
            position++;
        }

        return position;
    }

    private void updateConnectedFrames(World world, BlockPos pos) {
        // 更新当前方块
        BlockState currentState = world.getBlockState(pos);
        if (currentState.getBlock() instanceof GantryFrameMain) {
            world.setBlockState(pos, updateMainType(currentState, world, pos), Block.NOTIFY_ALL);
        }

        // 更新右边的相同方块
        BlockPos eastPos = pos.east();
        BlockState eastState = world.getBlockState(eastPos);
        if (eastState.getBlock() instanceof GantryFrameMain) {
            world.setBlockState(eastPos, updateMainType(eastState, world, eastPos), Block.NOTIFY_ALL);
        }

        // 更新左边的相同方块
        BlockPos westPos = pos.west();
        BlockState westState = world.getBlockState(westPos);
        if (westState.getBlock() instanceof GantryFrameMain) {
            world.setBlockState(westPos, updateMainType(westState, world, westPos), Block.NOTIFY_ALL);
        }
    }

    public enum GantryFrameMainType implements StringIdentifiable {
        GANTRY_FRAME_MAIN_1("gantry_frame_main_1"),
        GANTRY_FRAME_MAIN_2("gantry_frame_main_2");

        private final String name;

        GantryFrameMainType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}