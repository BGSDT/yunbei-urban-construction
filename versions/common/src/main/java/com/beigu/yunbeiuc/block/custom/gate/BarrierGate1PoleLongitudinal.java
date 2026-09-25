package com.beigu.yunbeiuc.block.custom.gate;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
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

public class BarrierGate1PoleLongitudinal extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<PoleType> POLE_TYPE = EnumProperty.create("pole_type", PoleType.class);

    private static final VoxelShape SHAPE_N = Block.box(7.25, 0, 12.5, 8.75, 16, 14);
    private static final VoxelShape SHAPE_S = Block.box(7.25, 0, 2, 8.75, 16, 4);
    private static final VoxelShape SHAPE_E = Block.box(2, 0, 7.25, 4, 16, 8.75);
    private static final VoxelShape SHAPE_W = Block.box(12, 0, 7.25, 14, 16, 8.75);

    public BarrierGate1PoleLongitudinal(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POLE_TYPE, PoleType.NORMAL).setValue(FACING, Direction.NORTH)
        );
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
        builder.add(POLE_TYPE, FACING);
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
        if (direction == Direction.DOWN) {
            return updatePoleType(state, world, pos);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        world.setBlock(pos, updatePoleType(state, world, pos), Block.UPDATE_ALL);
    }

    private BlockState updatePoleType(BlockState state, LevelAccessor world, BlockPos pos) {
        BlockPos downPos = pos.below();
        BlockState downState = world.getBlockState(downPos);

        boolean isNormalGateBelow =
                downState.is(MunicipalBlocks.BARRIER_GATE_1_MAIN.get());

        return state.setValue(POLE_TYPE, isNormalGateBelow ? PoleType.OFFSET : PoleType.NORMAL);
    }

    // 柱子类型枚举
    public enum PoleType implements StringRepresentable {
        NORMAL("normal"),
        OFFSET("offset");

        private final String name;

        PoleType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
