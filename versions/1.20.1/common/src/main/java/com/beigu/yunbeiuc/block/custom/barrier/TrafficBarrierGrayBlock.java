package com.beigu.yunbeiuc.block.custom.barrier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrafficBarrierGrayBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(0, 0, 1, 16, 16, 15);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(0, 0, 1, 16, 16, 15);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(1, 0, 0, 15, 16, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(1, 0, 0, 15, 16, 16);

    public TrafficBarrierGrayBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(TYPE, Type.BOTTOM));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_barrier.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING,TYPE);
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
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(TYPE, Type.SINGLE);
    }

   @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        boolean top = world.getBlockState(pos.up()).isOf(this);
        boolean bottom = world.getBlockState(pos.down()).isOf(this);
        if (top && bottom) {
            return state.with(TYPE, Type.MIDDLE);
        } else if (top) {
            return state.with(TYPE, Type.BOTTOM);
        } else if (bottom) {
            return state.with(TYPE, Type.UP);
        } else {
            return state.with(TYPE, Type.SINGLE);
        }
    }

    public enum Type implements StringIdentifiable{
        SINGLE("single"),
        UP("up"),
        BOTTOM("bottom"),
        MIDDLE("middle");

        private final String id;

        Type(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }
    }
}