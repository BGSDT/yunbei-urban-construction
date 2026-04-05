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

public class HeightLimitBarrierConnection extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<HeightLimitBarrierConnectionType> CONNECTION_TYPE =
            EnumProperty.of("connection_type", HeightLimitBarrierConnectionType.class);

    private boolean isManualUpdate = false;
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    public HeightLimitBarrierConnection(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(CONNECTION_TYPE, HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_1));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONNECTION_TYPE);
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
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (isManualUpdate) return state;

        Direction facing = state.get(FACING);
        Direction left = facing.rotateYCounterclockwise();
        Direction right = facing.rotateYClockwise();

        if (direction == left || direction == right || direction == Direction.DOWN) {
            return updateConnectionType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!isManualUpdate) {
            world.setBlockState(pos, updateConnectionType(state, world, pos), Block.NOTIFY_ALL);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = player.getStackInHand(hand).getItem();

        if (item == ModItems.WAND) {
            if (!world.isClient) {
                isManualUpdate = true;

                HeightLimitBarrierConnectionType currentType = state.get(CONNECTION_TYPE);
                HeightLimitBarrierConnectionType newType = switch (currentType) {
                    case HEIGHT_LIMIT_BARRIER_CONNECTION_1 -> HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_3;
                    case HEIGHT_LIMIT_BARRIER_CONNECTION_2 -> HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_4;
                    case HEIGHT_LIMIT_BARRIER_CONNECTION_3 -> HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_1;
                    case HEIGHT_LIMIT_BARRIER_CONNECTION_4 -> HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_2;
                };

                world.setBlockState(pos, state.with(CONNECTION_TYPE, newType), Block.NOTIFY_ALL);
                isManualUpdate = false;
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private BlockState updateConnectionType(BlockState state, WorldAccess world, BlockPos pos) {
        Direction facing = state.get(FACING);
        Direction left = facing.rotateYCounterclockwise();
        Direction right = facing.rotateYClockwise();

        boolean hasMainRight = world.getBlockState(pos.offset(right)).isOf(ModBlocks.HEIGHT_LIMIT_BARRIER_MAIN);
        boolean hasMainLeft = world.getBlockState(pos.offset(left)).isOf(ModBlocks.HEIGHT_LIMIT_BARRIER_MAIN);
        boolean isSide1 = isBelowSideType1(world, pos);

        HeightLimitBarrierConnectionType finalType;

        if (hasMainRight) {
            finalType = isSide1 ? HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_3 : HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_4;
        } else if (hasMainLeft) {
            finalType = isSide1 ? HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_1 : HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_2;
        } else {
            finalType = HeightLimitBarrierConnectionType.HEIGHT_LIMIT_BARRIER_CONNECTION_1;
        }

        return state.with(CONNECTION_TYPE, finalType);
    }

    // 判断下方的 HeightLimitBarrierSide 是否为类型 1
    private boolean isBelowSideType1(WorldAccess world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.getBlock() instanceof HeightLimitBarrierSide) {
            return belowState.get(HeightLimitBarrierSide.BARRIER_TYPE).asString().equals("height_limit_barrier_side_1");
        }
        return false;
    }

    // 你要求的 1~4 枚举
    public enum HeightLimitBarrierConnectionType implements StringIdentifiable {
        HEIGHT_LIMIT_BARRIER_CONNECTION_1("height_limit_barrier_connection_1"),
        HEIGHT_LIMIT_BARRIER_CONNECTION_2("height_limit_barrier_connection_2"),
        HEIGHT_LIMIT_BARRIER_CONNECTION_3("height_limit_barrier_connection_3"),
        HEIGHT_LIMIT_BARRIER_CONNECTION_4("height_limit_barrier_connection_4");

        private final String name;

        HeightLimitBarrierConnectionType(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }
}