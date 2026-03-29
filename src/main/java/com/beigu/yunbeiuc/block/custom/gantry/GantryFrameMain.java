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
        this.setDefaultState(this.getDefaultState()
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
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    // ===========================
    // 🔥 修复 1：朝向正确（去掉 getOpposite！）
    // ===========================
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // 原来错误：ctx.getHorizontalPlayerFacing().getOpposite()
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }


    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        Direction left = facing.rotateYCounterclockwise();
        Direction right = facing.rotateYClockwise();

        if (direction == left || direction == right) {
            return updateMainType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        updateEntireConnection(world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!state.isOf(newState.getBlock())) {
            updateEntireConnection(world, pos);
        }
    }


    private BlockState updateMainType(BlockState state, WorldAccess world, BlockPos pos) {
        Direction facing = state.get(FACING);
        BlockPos leftEnd = findLeftEnd(world, pos, facing);
        int index = getIndexFromLeft(world, leftEnd, pos, facing);

        GantryFrameMainType type = (index % 2 == 1)
                ? GantryFrameMainType.GANTRY_FRAME_MAIN_1
                : GantryFrameMainType.GANTRY_FRAME_MAIN_2;

        return state.with(MAIN_TYPE, type);
    }


    private BlockPos findLeftEnd(WorldAccess world, BlockPos start, Direction facing) {
        Direction left = facing.rotateYCounterclockwise();
        BlockPos current = start;

        for (int i = 0; i < 32; i++) {
            BlockPos next = current.offset(left);
            if (isSameFrame(world, next, facing)) {
                current = next;
            } else {
                break;
            }
        }
        return current;
    }


    private int getIndexFromLeft(WorldAccess world, BlockPos leftEnd, BlockPos target, Direction facing) {
        Direction right = facing.rotateYClockwise();
        BlockPos current = leftEnd;
        int index = 1;

        while (!current.equals(target)) {
            current = current.offset(right);
            if (!isSameFrame(world, current, facing)) {
                return 1;
            }
            index++;
        }
        return index;
    }


    private void updateEntireConnection(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof GantryFrameMain)) return;

        Direction facing = state.get(FACING);
        Direction left = facing.rotateYCounterclockwise();
        Direction right = facing.rotateYClockwise();

        // 刷新左侧
        BlockPos curr = pos;
        for (int i = 0; i < 32; i++) {
            if (!isSameFrame(world, curr, facing)) break;
            world.setBlockState(curr, updateMainType(world.getBlockState(curr), world, curr), 3);
            curr = curr.offset(left);
        }

        // 刷新右侧
        curr = pos.offset(right);
        for (int i = 0; i < 32; i++) {
            if (!isSameFrame(world, curr, facing)) break;
            world.setBlockState(curr, updateMainType(world.getBlockState(curr), world, curr), 3);
            curr = curr.offset(right);
        }
    }


    private boolean isSameFrame(WorldAccess world, BlockPos pos, Direction facing) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof GantryFrameMain
                && state.get(FACING) == facing;
    }

    public enum GantryFrameMainType implements StringIdentifiable {
        GANTRY_FRAME_MAIN_1("gantry_frame_main_1"),
        GANTRY_FRAME_MAIN_2("gantry_frame_main_2");

        private final String name;
        GantryFrameMainType(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }
}