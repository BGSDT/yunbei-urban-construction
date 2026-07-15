package com.beigu.yunbeiuc.block.custom.gantry;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
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

public class GantryFrameConnection extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<GantryFrameConnectionType> CONNECTION_TYPE =
            EnumProperty.of("connection_type", GantryFrameConnectionType.class);

    private boolean isManualUpdate = false;
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    public GantryFrameConnection(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(CONNECTION_TYPE, GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1));
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

        if (item == ModItems.WAND.get()) {
            if (!world.isClient) {
                isManualUpdate = true;

                GantryFrameConnectionType currentType = state.get(CONNECTION_TYPE);
                GantryFrameConnectionType newType = switch (currentType) {
                    case GANTRY_FRAME_CONNECTION_1 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_3;
                    case GANTRY_FRAME_CONNECTION_2 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_4;
                    case GANTRY_FRAME_CONNECTION_3 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1;
                    case GANTRY_FRAME_CONNECTION_4 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_2;
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

        boolean hasMainRight = world.getBlockState(pos.offset(right)).isOf(MunicipalBlocks.GANTRY_FRAME_MAIN.get());
        boolean hasMainLeft = world.getBlockState(pos.offset(left)).isOf(MunicipalBlocks.GANTRY_FRAME_MAIN.get());
        boolean isSide1 = isBelowSideType1(world, pos);

        GantryFrameConnectionType finalType;

        if (hasMainRight) {
            finalType = isSide1 ? GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_3 : GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_4;
        } else if (hasMainLeft) {
            finalType = isSide1 ? GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1 : GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_2;
        } else {
            finalType = GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1;
        }

        return state.with(CONNECTION_TYPE, finalType);
    }

    private boolean isBelowSideType1(WorldAccess world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.down());
        if (belowState.getBlock() instanceof GantryFrameSide) {
            return belowState.get(GantryFrameSide.FRAME_TYPE).asString().equals("gantry_frame_side_1");
        }
        return false;
    }

    public enum GantryFrameConnectionType implements StringIdentifiable {
        GANTRY_FRAME_CONNECTION_1("gantry_frame_connection_1"),
        GANTRY_FRAME_CONNECTION_2("gantry_frame_connection_2"),
        GANTRY_FRAME_CONNECTION_3("gantry_frame_connection_3"),
        GANTRY_FRAME_CONNECTION_4("gantry_frame_connection_4");

        private final String name;

        GantryFrameConnectionType(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }
}