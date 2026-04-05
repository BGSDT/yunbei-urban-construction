package com.beigu.yunbeiuc.block.custom.height;

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

public class HeightLimitBarrierSide extends Block {
    // 水平朝向属性（与原类一致）
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // 替换为你的高度限制栏类型枚举
    public static final EnumProperty<HeightLimitBarrierType> BARRIER_TYPE = EnumProperty.of("barrier_type", HeightLimitBarrierType.class);

    // 碰撞箱形状完全沿用原类（南北/东西形状一致）
    private static final VoxelShape SHAPE_N = Block.createCuboidShape(6.5, 0, 0, 9.5, 16, 16);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(0, 0, 6.5, 16, 16, 9.5);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(6.5, 0, 0, 9.5, 16, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(0, 0, 6.5, 16, 16, 9.5);

    public HeightLimitBarrierSide(Settings settings) {
        super(settings);
        // 设置默认状态：默认类型1 + 默认朝北
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(BARRIER_TYPE, HeightLimitBarrierType.HEIGHT_LIMIT_BARRIER_SIDE_1)
                .with(FACING, Direction.NORTH));
    }

    // 碰撞箱获取逻辑完全一致
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case WEST -> SHAPE_W;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            default -> SHAPE_N;
        };
    }

    // 注册方块状态属性
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BARRIER_TYPE).add(FACING);
    }

    // 旋转/镜像逻辑一致
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    // 放置时朝向玩家反方向
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    // 上下方块更新时刷新类型
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return this.updateBarrierType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    // 方块放置时更新自身及上下方块
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        updateConnectedBarriers(world, pos);
    }

    // 方块破坏时更新相邻方块
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!state.isOf(newState.getBlock())) {
            updateConnectedBarriers(world, pos);
        }
    }

    // ===================== 核心逻辑：交替显示类型1/类型2 =====================
    // 功能与原类完全相同：从最底部开始计数，奇数=类型1，偶数=类型2
    private BlockState updateBarrierType(BlockState state, WorldAccess world, BlockPos pos) {
        BlockPos bottomPos = findBottomBarrier(world, pos);
        int positionFromBottom = getPositionFromBottom(world, bottomPos, pos);

        HeightLimitBarrierType newType = (positionFromBottom % 2 == 1) ?
                HeightLimitBarrierType.HEIGHT_LIMIT_BARRIER_SIDE_1 :
                HeightLimitBarrierType.HEIGHT_LIMIT_BARRIER_SIDE_2;

        return state.with(BARRIER_TYPE, newType);
    }

    // 找到垂直方向最底部的同类方块
    private BlockPos findBottomBarrier(WorldAccess world, BlockPos startPos) {
        BlockPos currentPos = startPos;
        BlockPos belowPos = currentPos.down();

        while (world.getBlockState(belowPos).getBlock() instanceof HeightLimitBarrierSide) {
            currentPos = belowPos;
            belowPos = currentPos.down();
        }

        return currentPos;
    }

    // 计算从底部开始的位置序号
    private int getPositionFromBottom(WorldAccess world, BlockPos bottomPos, BlockPos targetPos) {
        int position = 1;
        BlockPos currentPos = bottomPos;

        while (!currentPos.equals(targetPos)) {
            currentPos = currentPos.up();
            if (!(world.getBlockState(currentPos).getBlock() instanceof HeightLimitBarrierSide)) {
                return 1;
            }
            position++;
        }

        return position;
    }

    // 更新自身 + 上方 + 下方的方块状态
    private void updateConnectedBarriers(World world, BlockPos pos) {
        // 更新当前
        BlockState currentState = world.getBlockState(pos);
        if (currentState.getBlock() instanceof HeightLimitBarrierSide) {
            world.setBlockState(pos, updateBarrierType(currentState, world, pos), Block.NOTIFY_ALL);
        }

        // 更新上方
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);
        if (aboveState.getBlock() instanceof HeightLimitBarrierSide) {
            world.setBlockState(abovePos, updateBarrierType(aboveState, world, abovePos), Block.NOTIFY_ALL);
        }

        // 更新下方
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);
        if (belowState.getBlock() instanceof HeightLimitBarrierSide) {
            world.setBlockState(belowPos, updateBarrierType(belowState, world, belowPos), Block.NOTIFY_ALL);
        }
    }

    // ===================== 你提供的枚举类 =====================
    public enum HeightLimitBarrierType implements StringIdentifiable {
        HEIGHT_LIMIT_BARRIER_SIDE_1("height_limit_barrier_side_1"),
        HEIGHT_LIMIT_BARRIER_SIDE_2("height_limit_barrier_side_2");

        private final String name;

        HeightLimitBarrierType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}