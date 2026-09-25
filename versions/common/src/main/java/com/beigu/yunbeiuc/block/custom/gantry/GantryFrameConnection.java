package com.beigu.yunbeiuc.block.custom.gantry;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class GantryFrameConnection extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<GantryFrameConnectionType> CONNECTION_TYPE =
            EnumProperty.create("connection_type", GantryFrameConnectionType.class);

    private boolean isManualUpdate = false;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public GantryFrameConnection(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(CONNECTION_TYPE, GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONNECTION_TYPE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (isManualUpdate) return state;

        Direction facing = state.getValue(FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        if (direction == left || direction == right || direction == Direction.DOWN) {
            return updateConnectionType(state, world, pos);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        if (!isManualUpdate) {
            world.setBlock(pos, updateConnectionType(state, world, pos), Block.UPDATE_ALL);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = player.getItemInHand(hand).getItem();

        if (item == ModItems.WAND.get()) {
            if (!world.isClientSide) {
                isManualUpdate = true;

                GantryFrameConnectionType currentType = state.getValue(CONNECTION_TYPE);
                GantryFrameConnectionType newType = switch (currentType) {
                    case GANTRY_FRAME_CONNECTION_1 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_3;
                    case GANTRY_FRAME_CONNECTION_2 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_4;
                    case GANTRY_FRAME_CONNECTION_3 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1;
                    case GANTRY_FRAME_CONNECTION_4 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_2;
                };

                world.setBlock(pos, state.setValue(CONNECTION_TYPE, newType), Block.UPDATE_ALL);
                isManualUpdate = false;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private BlockState updateConnectionType(BlockState state, LevelAccessor world, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        boolean hasMainRight = world.getBlockState(pos.relative(right)).is(MunicipalBlocks.GANTRY_FRAME_MAIN.get());
        boolean hasMainLeft = world.getBlockState(pos.relative(left)).is(MunicipalBlocks.GANTRY_FRAME_MAIN.get());
        boolean isSide1 = isBelowSideType1(world, pos);

        GantryFrameConnectionType finalType;

        if (hasMainRight) {
            finalType = isSide1 ? GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_3 : GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_4;
        } else if (hasMainLeft) {
            finalType = isSide1 ? GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1 : GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_2;
        } else {
            finalType = GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1;
        }

        return state.setValue(CONNECTION_TYPE, finalType);
    }

    private boolean isBelowSideType1(LevelAccessor world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.below());
        if (belowState.getBlock() instanceof GantryFrameSide) {
            return belowState.getValue(GantryFrameSide.FRAME_TYPE).getSerializedName().equals("gantry_frame_side_1");
        }
        return false;
    }

    public enum GantryFrameConnectionType implements StringRepresentable {
        GANTRY_FRAME_CONNECTION_1("gantry_frame_connection_1"),
        GANTRY_FRAME_CONNECTION_2("gantry_frame_connection_2"),
        GANTRY_FRAME_CONNECTION_3("gantry_frame_connection_3"),
        GANTRY_FRAME_CONNECTION_4("gantry_frame_connection_4");

        private final String name;

        GantryFrameConnectionType(String name) { this.name = name; }
        @Override public String getSerializedName() { return name; }
    }
}
