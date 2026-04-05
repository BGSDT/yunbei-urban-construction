package com.beigu.yunbeiuc.block.custom.height;

import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class HeightLimitBarrierMain extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<HeightLimitBarrierMainType> MAIN_TYPE = EnumProperty.of("main_type", HeightLimitBarrierMainType.class);

    public HeightLimitBarrierMain(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(MAIN_TYPE, HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_1));
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
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return updateMainType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!world.isClient) {
            updateConnectedFrames(world, pos);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!state.isOf(newState.getBlock()) && !world.isClient) {
            updateConnectedFrames(world, pos);
        }
    }

    // ==================== 已修复：点击切换逻辑 ====================
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = player.getStackInHand(hand).getItem();
        if (item != ModItems.WAND) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            boolean hasSideBelow = world.getBlockState(pos.down()).isOf(ModBlocks.HEIGHT_LIMIT_BARRIER_SIDE);
            HeightLimitBarrierMainType current = state.get(MAIN_TYPE);
            HeightLimitBarrierMainType newType = current;

            // 1 ↔ 3 ↔ 4 ↔ 1
            if (current == HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_1 && hasSideBelow) {
                newType = HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_3;
            } else if (current == HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_3 && hasSideBelow) {
                newType = HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_4;
            } else if (current == HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_4 && hasSideBelow) {
                newType = HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_1;
            }

            // 2 ↔ 5 ↔ 6 ↔ 2
            else if (current == HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_2 && hasSideBelow) {
                newType = HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_5;
            } else if (current == HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_5 && hasSideBelow) {
                newType = HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_6;
            } else if (current == HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_6 && hasSideBelow) {
                newType = HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_2;
            }

            world.setBlockState(pos, state.with(MAIN_TYPE, newType), Block.NOTIFY_ALL);
        }
        return ActionResult.SUCCESS;
    }

    private BlockState updateMainType(BlockState state, WorldAccess world, BlockPos pos) {
        BlockPos leftmostPos = findLeftFrame(world, pos);
        int index = getPositionFromLeft(world, leftmostPos, pos);

        HeightLimitBarrierMainType type = index % 2 == 1 ?
                HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_1 :
                HeightLimitBarrierMainType.HEIGHT_LIMIT_BARRIER_MAIN_2;

        return state.with(MAIN_TYPE, type);
    }

    private BlockPos findLeftFrame(WorldAccess world, BlockPos startPos) {
        BlockPos current = startPos;
        BlockPos nextWest = current.west();

        while (isSameFrame(world, nextWest)) {
            current = nextWest;
            nextWest = current.west();
        }
        return current;
    }

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

    private boolean isSameFrame(WorldAccess world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof HeightLimitBarrierMain;
    }

    private void updateConnectedFrames(World world, BlockPos pos) {
        BlockPos leftmost = findLeftFrame(world, pos);
        BlockPos current = leftmost;

        while (isSameFrame(world, current)) {
            BlockState state = world.getBlockState(current);
            BlockState newState = updateMainType(state, world, current);

            if (!newState.equals(state)) {
                world.setBlockState(current, newState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
            }
            current = current.east();
        }
    }

    // ==================== 枚举 1~6 ====================
    public enum HeightLimitBarrierMainType implements StringIdentifiable {
        HEIGHT_LIMIT_BARRIER_MAIN_1("height_limit_barrier_main_1"),
        HEIGHT_LIMIT_BARRIER_MAIN_2("height_limit_barrier_main_2"),
        HEIGHT_LIMIT_BARRIER_MAIN_3("height_limit_barrier_main_3"),
        HEIGHT_LIMIT_BARRIER_MAIN_4("height_limit_barrier_main_4"),
        HEIGHT_LIMIT_BARRIER_MAIN_5("height_limit_barrier_main_5"),
        HEIGHT_LIMIT_BARRIER_MAIN_6("height_limit_barrier_main_6");

        private final String name;
        HeightLimitBarrierMainType(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }
}