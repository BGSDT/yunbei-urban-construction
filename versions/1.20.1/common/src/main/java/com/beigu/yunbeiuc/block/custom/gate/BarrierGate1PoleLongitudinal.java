package com.beigu.yunbeiuc.block.custom.gate;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
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

public class BarrierGate1PoleLongitudinal extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<PoleType> POLE_TYPE = EnumProperty.of("pole_type", PoleType.class);

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(7.25, 0, 12.5, 8.75, 16, 14);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(7.25, 0, 2, 8.75, 16, 4);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(2, 0, 7.25, 4, 16, 8.75);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(12, 0, 7.25, 14, 16, 8.75);

    public BarrierGate1PoleLongitudinal(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(POLE_TYPE, PoleType.NORMAL)
                .with(FACING, Direction.NORTH)
        );
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
        builder.add(POLE_TYPE, FACING);
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
        if (direction == Direction.DOWN) {
            return updatePoleType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.setBlockState(pos, updatePoleType(state, world, pos), Block.NOTIFY_ALL);
    }

    private BlockState updatePoleType(BlockState state, WorldAccess world, BlockPos pos) {
        BlockPos downPos = pos.down();
        BlockState downState = world.getBlockState(downPos);

        boolean isNormalGateBelow =
                downState.isOf(MunicipalBlocks.BARRIER_GATE_1_MAIN.get());

        return state.with(POLE_TYPE, isNormalGateBelow ? PoleType.OFFSET : PoleType.NORMAL);
    }

    // 柱子类型枚举
    public enum PoleType implements StringIdentifiable {
        NORMAL("normal"),
        OFFSET("offset");

        private final String name;

        PoleType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}