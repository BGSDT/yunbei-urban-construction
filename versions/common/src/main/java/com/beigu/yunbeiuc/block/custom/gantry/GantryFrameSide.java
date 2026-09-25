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

public class GantryFrameSide extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final EnumProperty<GantryFrameType> FRAME_TYPE = EnumProperty.create("frame_type", GantryFrameType.class);

    private static final VoxelShape SHAPE_N = Block.box(6.5, 0, 0, 9.5, 16, 16);
    private static final VoxelShape SHAPE_E = Block.box(0, 0, 6.5, 16, 16, 9.5);
    private static final VoxelShape SHAPE_S = Block.box(6.5, 0, 0, 9.5, 16, 16);
    private static final VoxelShape SHAPE_W = Block.box(0, 0, 6.5, 16, 16, 9.5);

    public GantryFrameSide(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FRAME_TYPE, GantryFrameType.GANTRY_FRAME_SIDE_1).setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case WEST -> SHAPE_W;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FRAME_TYPE).add(FACING);
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
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return this.updateFrameType(state, world, pos);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        // 更新当前方块和上下相邻的相同方块
        updateConnectedFrames(world, pos);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, world, pos, newState, moved);
        // 当方块被破坏时，更新相邻的相同方块
        if (!state.is(newState.getBlock())) {
            updateConnectedFrames(world, pos);
        }
    }

    private BlockState updateFrameType(BlockState state, LevelAccessor world, BlockPos pos) {
        // 找到最底部的方块
        BlockPos bottomPos = findBottomFrame(world, pos);

        // 从底部开始向上计数，计算当前方块的位置
        int positionFromBottom = getPositionFromBottom(world, bottomPos, pos);

        // 从下往上数：位置1、3、5...显示类型1，位置2、4、6...显示类型2
        GantryFrameType newType = (positionFromBottom % 2 == 1) ?
                GantryFrameType.GANTRY_FRAME_SIDE_1 : GantryFrameType.GANTRY_FRAME_SIDE_2;

        return state.setValue(FRAME_TYPE, newType);
    }

    private BlockPos findBottomFrame(LevelAccessor world, BlockPos startPos) {
        BlockPos currentPos = startPos;

        // 向下查找，直到找不到相同的方块
        BlockPos belowPos = currentPos.below();
        while (world.getBlockState(belowPos).getBlock() instanceof GantryFrameSide) {
            currentPos = belowPos;
            belowPos = currentPos.below();
        }

        return currentPos;
    }

    private int getPositionFromBottom(LevelAccessor world, BlockPos bottomPos, BlockPos targetPos) {
        int position = 1; // 从1开始计数（底部第一个方块）
        BlockPos currentPos = bottomPos;

        // 从底部向上遍历，直到找到目标位置
        while (!currentPos.equals(targetPos)) {
            currentPos = currentPos.above();
            if (!(world.getBlockState(currentPos).getBlock() instanceof GantryFrameSide)) {
                return 1; // 如果链条断裂，返回默认值
            }
            position++;
        }

        return position;
    }

    private void updateConnectedFrames(Level world, BlockPos pos) {
        // 更新当前方块
        BlockState currentState = world.getBlockState(pos);
        if (currentState.getBlock() instanceof GantryFrameSide) {
            world.setBlock(pos, updateFrameType(currentState, world, pos), VersionServices.blocks().updateAll());
        }

        // 更新上方的相同方块
        BlockPos abovePos = pos.above();
        BlockState aboveState = world.getBlockState(abovePos);
        if (aboveState.getBlock() instanceof GantryFrameSide) {
            world.setBlock(abovePos, updateFrameType(aboveState, world, abovePos), VersionServices.blocks().updateAll());
        }

        // 更新下方的相同方块
        BlockPos belowPos = pos.below();
        BlockState belowState = world.getBlockState(belowPos);
        if (belowState.getBlock() instanceof GantryFrameSide) {
            world.setBlock(belowPos, updateFrameType(belowState, world, belowPos), VersionServices.blocks().updateAll());
        }
    }

    public enum GantryFrameType implements StringRepresentable {
        GANTRY_FRAME_SIDE_1("gantry_frame_side_1"),
        GANTRY_FRAME_SIDE_2("gantry_frame_side_2");

        private final String name;

        GantryFrameType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
