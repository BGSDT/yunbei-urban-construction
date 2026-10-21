package com.beigu.yunbeiuc.block.custom.gantry;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class GantryFrameMain extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<GantryFrameMainType> MAIN_TYPE = EnumProperty.create("main_type", GantryFrameMainType.class);

    public GantryFrameMain(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(MAIN_TYPE, GantryFrameMainType.GANTRY_FRAME_MAIN_1));
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MAIN_TYPE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            return updateMainType(state, world, pos);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        if (!world.isClientSide) {
            updateConnectedFrames(world, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, world, pos, newState, moved);
        if (!state.is(newState.getBlock()) && !world.isClientSide) {
            updateConnectedFrames(world, pos);
        }
    }

    private BlockState updateMainType(BlockState state, LevelAccessor world, BlockPos pos) {
        BlockPos leftmostPos = findLeftFrame(world, pos);
        int index = getPositionFromLeft(world, leftmostPos, pos);
        GantryFrameMainType type = index % 2 == 1 ?
                GantryFrameMainType.GANTRY_FRAME_MAIN_1 :
                GantryFrameMainType.GANTRY_FRAME_MAIN_2;

        return state.setValue(MAIN_TYPE, type);
    }

    private BlockPos findLeftFrame(LevelAccessor world, BlockPos startPos) {
        BlockPos current = startPos;
        BlockPos nextWest = current.west();

        while (isSameFrame(world, nextWest)) {
            current = nextWest;
            nextWest = current.west();
        }
        return current;
    }

    private int getPositionFromLeft(LevelAccessor world, BlockPos leftPos, BlockPos targetPos) {
        int count = 1;
        BlockPos current = leftPos;

        while (!current.equals(targetPos)) {
            current = current.east();
            if (!isSameFrame(world, current)) break;
            count++;
        }
        return count;
    }

    private boolean isSameFrame(LevelAccessor world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof GantryFrameMain;
    }

    private void updateConnectedFrames(Level world, BlockPos pos) {
        BlockPos leftmost = findLeftFrame(world, pos);
        BlockPos current = leftmost;

        while (isSameFrame(world, current)) {
            BlockState state = world.getBlockState(current);
            BlockState newState = updateMainType(state, world, current);

            if (!newState.equals(state)) {
                world.setBlock(current, newState, VersionServices.blocks().updateClients() | VersionServices.blocks().updateImmediate());
            }

            current = current.east();
        }
    }

    public enum GantryFrameMainType implements StringRepresentable {
        GANTRY_FRAME_MAIN_1("gantry_frame_main_1"),
        GANTRY_FRAME_MAIN_2("gantry_frame_main_2");

        private final String name;
        GantryFrameMainType(String name) { this.name = name; }
        @Override public String getSerializedName() { return name; }
    }
}
