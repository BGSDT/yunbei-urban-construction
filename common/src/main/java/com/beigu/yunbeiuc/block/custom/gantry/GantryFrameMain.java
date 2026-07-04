package com.beigu.yunbeiuc.block.custom.gantry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class GantryFrameMain extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<GantryFrameMainType> MAIN_TYPE = EnumProperty.of("main_type", GantryFrameMainType.class);

    public GantryFrameMain(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(MAIN_TYPE, GantryFrameMainType.GANTRY_FRAME_MAIN_1));
    }

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, MAIN_TYPE);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING, mirror.apply(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // 玩家放置时朝向玩家正面
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    // ==================== 核心修复：邻接更新 ====================
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // 东西方向变化才更新，防止无限递归
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return updateMainType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!world.isClient) {
            updateConnectedFrames(world, pos); // 只在服务端更新
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!state.isOf(newState.getBlock()) && !world.isClient) {
            updateConnectedFrames(world, pos); // 破坏时刷新整条链
        }
    }

    // ==================== 核心修复：类型计算逻辑 ====================
    private BlockState updateMainType(BlockState state, WorldAccess world, BlockPos pos) {
        // 找到最左侧的龙门架方块
        BlockPos leftmostPos = findLeftFrame(world, pos);
        // 计算从左数的序号
        int index = getPositionFromLeft(world, leftmostPos, pos);
        // 奇数=1 偶数=2
        GantryFrameMainType type = index % 2 == 1 ?
                GantryFrameMainType.GANTRY_FRAME_MAIN_1 :
                GantryFrameMainType.GANTRY_FRAME_MAIN_2;

        return state.with(MAIN_TYPE, type);
    }

    // 安全查找最左侧方块（防越界）
    private BlockPos findLeftFrame(WorldAccess world, BlockPos startPos) {
        BlockPos current = startPos;
        BlockPos nextWest = current.west();

        while (isSameFrame(world, nextWest)) {
            current = nextWest;
            nextWest = current.west();
        }
        return current;
    }

    // 计算从左到右的位置序号
    private int getPositionFromLeft(WorldAccess world, BlockPos leftPos, BlockPos targetPos) {
        int count = 1;
        BlockPos current = leftPos;

        while (!current.equals(targetPos)) {
            current = current.east();
            if (!isSameFrame(world, current)) break;
            count++;
        }
        return count;
    }

    // 判断是否是同一个龙门架方块（统一判断逻辑）
    private boolean isSameFrame(WorldAccess world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof GantryFrameMain;
    }

    // ==================== 核心修复：连锁更新整条线 ====================
    private void updateConnectedFrames(World world, BlockPos pos) {
        // 先找到最左和最右，更新整条链
        BlockPos leftmost = findLeftFrame(world, pos);
        BlockPos current = leftmost;

        while (isSameFrame(world, current)) {
            BlockState state = world.getBlockState(current);
            BlockState newState = updateMainType(state, world, current);

            // 只有状态不同才更新，避免无限递归
            if (!newState.equals(state)) {
                world.setBlockState(current, newState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
            }

            current = current.east();
        }
    }

    // ==================== 枚举不变 ====================
    public enum GantryFrameMainType implements StringIdentifiable {
        GANTRY_FRAME_MAIN_1("gantry_frame_main_1"),
        GANTRY_FRAME_MAIN_2("gantry_frame_main_2");

        private final String name;
        GantryFrameMainType(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }
}