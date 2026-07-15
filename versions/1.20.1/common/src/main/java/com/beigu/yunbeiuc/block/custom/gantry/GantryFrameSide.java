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

public class GantryFrameSide extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public static final EnumProperty<GantryFrameType> FRAME_TYPE = EnumProperty.of("frame_type", GantryFrameType.class);

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(6.5, 0, 0, 9.5, 16, 16);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(0, 0, 6.5, 16, 16, 9.5);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(6.5, 0, 0, 9.5, 16, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(0, 0, 6.5, 16, 16, 9.5);

    public GantryFrameSide(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FRAME_TYPE, GantryFrameType.GANTRY_FRAME_SIDE_1).with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case WEST -> SHAPE_W;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FRAME_TYPE).add(FACING);
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
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return this.updateFrameType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        // 更新当前方块和上下相邻的相同方块
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

    private BlockState updateFrameType(BlockState state, WorldAccess world, BlockPos pos) {
        // 找到最底部的方块
        BlockPos bottomPos = findBottomFrame(world, pos);

        // 从底部开始向上计数，计算当前方块的位置
        int positionFromBottom = getPositionFromBottom(world, bottomPos, pos);

        // 从下往上数：位置1、3、5...显示类型1，位置2、4、6...显示类型2
        GantryFrameType newType = (positionFromBottom % 2 == 1) ?
                GantryFrameType.GANTRY_FRAME_SIDE_1 : GantryFrameType.GANTRY_FRAME_SIDE_2;

        return state.with(FRAME_TYPE, newType);
    }

    private BlockPos findBottomFrame(WorldAccess world, BlockPos startPos) {
        BlockPos currentPos = startPos;

        // 向下查找，直到找不到相同的方块
        BlockPos belowPos = currentPos.down();
        while (world.getBlockState(belowPos).getBlock() instanceof GantryFrameSide) {
            currentPos = belowPos;
            belowPos = currentPos.down();
        }

        return currentPos;
    }

    private int getPositionFromBottom(WorldAccess world, BlockPos bottomPos, BlockPos targetPos) {
        int position = 1; // 从1开始计数（底部第一个方块）
        BlockPos currentPos = bottomPos;

        // 从底部向上遍历，直到找到目标位置
        while (!currentPos.equals(targetPos)) {
            currentPos = currentPos.up();
            if (!(world.getBlockState(currentPos).getBlock() instanceof GantryFrameSide)) {
                return 1; // 如果链条断裂，返回默认值
            }
            position++;
        }

        return position;
    }

    private void updateConnectedFrames(World world, BlockPos pos) {
        // 更新当前方块
        BlockState currentState = world.getBlockState(pos);
        if (currentState.getBlock() instanceof GantryFrameSide) {
            world.setBlockState(pos, updateFrameType(currentState, world, pos), Block.NOTIFY_ALL);
        }

        // 更新上方的相同方块
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);
        if (aboveState.getBlock() instanceof GantryFrameSide) {
            world.setBlockState(abovePos, updateFrameType(aboveState, world, abovePos), Block.NOTIFY_ALL);
        }

        // 更新下方的相同方块
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);
        if (belowState.getBlock() instanceof GantryFrameSide) {
            world.setBlockState(belowPos, updateFrameType(belowState, world, belowPos), Block.NOTIFY_ALL);
        }
    }

    public enum GantryFrameType implements StringIdentifiable {
        GANTRY_FRAME_SIDE_1("gantry_frame_side_1"),
        GANTRY_FRAME_SIDE_2("gantry_frame_side_2");

        private final String name;

        GantryFrameType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}