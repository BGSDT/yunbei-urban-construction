package com.beigu.yunbeiuc.block.custom.gate;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BarrierGate1PoleHorizontal extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);
    public static final EnumProperty<GateType> GATE_TYPE = EnumProperty.of("gate_type", GateType.class);

    private static final VoxelShape SHAPE_NORMAL_N = Block.createCuboidShape(0, 1.75, 12.5, 16, 3.25, 14);
    private static final VoxelShape SHAPE_NORMAL_S = Block.createCuboidShape(0, 1.75, 2.5, 16, 3.25, 5);
    private static final VoxelShape SHAPE_NORMAL_E = Block.createCuboidShape(2.5, 1.75, 0, 14, 3.25, 16);
    private static final VoxelShape SHAPE_NORMAL_W = Block.createCuboidShape(12.5, 1.75, 0, 14, 3.25, 16);
    private static final VoxelShape SHAPE_SLAB_N = Block.createCuboidShape(0, -6.25, 12.5, 16, -4.75, 14);
    private static final VoxelShape SHAPE_SLAB_S = Block.createCuboidShape(0, -6.25, 2.5, 16, -4.75, 5);
    private static final VoxelShape SHAPE_SLAB_E = Block.createCuboidShape(2.5, -6.25, 0, 14, -4.75, 16);
    private static final VoxelShape SHAPE_SLAB_W = Block.createCuboidShape(12.5, -6.25, 0, 14, -4.75, 16);
    public BarrierGate1PoleHorizontal(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, Type.SINGLE).with(GATE_TYPE, GateType.NORMAL));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.barrier_gate_1_pole_horizontal.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        GateType gateType = state.get(GATE_TYPE);
        Direction facing = state.get(FACING);

        if (gateType == GateType.SLAB) {
            return switch (facing) {
                case WEST -> SHAPE_SLAB_W;
                case SOUTH -> SHAPE_SLAB_S;
                case EAST -> SHAPE_SLAB_E;
                default -> SHAPE_SLAB_N;
            };
        } else {
            return switch (facing) {
                case WEST -> SHAPE_NORMAL_W;
                case SOUTH -> SHAPE_NORMAL_S;
                case EAST -> SHAPE_NORMAL_E;
                default -> SHAPE_NORMAL_N;
            };
        }
    }



    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, GATE_TYPE);
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
        BlockState newState = getRelatedBlockState(state, world, pos, state.get(FACING));
        return checkSlabState(newState, world, pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && player.getStackInHand(hand).isOf(ModItems.WAND.get())) {
            world.setBlockState(pos, state.cycle(GATE_TYPE), 3);
        }
        return ActionResult.SUCCESS;
    }

    private BlockState getRelatedBlockState(BlockState state, WorldAccess world, BlockPos pos, Direction direction) {
        boolean left = isRelatedInDirection(world, pos, direction, true);
        boolean right = isRelatedInDirection(world, pos, direction, false);
        if (left && right){
            return state.with(TYPE, Type.MIDDLE);
        } else if (right) {
            return state.with(TYPE, Type.RIGHT);
        } else if (left) {
            return state.with(TYPE, Type.LEFT);
        }
        return state.with(TYPE, Type.SINGLE);
    }

    private boolean isRelatedInDirection(WorldAccess world, BlockPos pos, Direction direction, boolean counterClockwise) {
        Direction rotated = counterClockwise ? direction.rotateYCounterclockwise() : direction.rotateYClockwise();
        return isRelatedBlock(world, pos, rotated, direction);
    }

    private boolean isRelatedBlock(WorldAccess world, BlockPos pos, Direction rotate, Direction direction) {
        BlockState state = world.getBlockState(pos.offset(rotate));
        if (state.getBlock() == this){
            Direction direction1 = state.get(FACING);
            return direction1.equals(direction);
        }
        return false;
    }

    private BlockState checkSlabState(BlockState state, WorldAccess world, BlockPos pos) {
        Direction facing = state.get(FACING);
        Direction left = facing.rotateYCounterclockwise();
        BlockState leftState = world.getBlockState(pos.offset(left));
        if (leftState.isOf(MunicipalBlocks.BARRIER_GATE_1_POLE_HORIZONTAL.get()) && leftState.get(GATE_TYPE) == GateType.SLAB) {
            return state.with(GATE_TYPE, GateType.SLAB);
        }
        return state;
    }

    public enum Type implements StringIdentifiable {
        SINGLE("single"),
        LEFT("left"),
        MIDDLE("middle"),
        RIGHT("right");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    public enum GateType implements StringIdentifiable {
        NORMAL("normal"),
        SLAB("slab");

        private final String name;

        GateType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}