package com.beigu.yunbeiuc.block.custom.gate;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionResult;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BarrierGate1PoleHorizontal extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    public static final EnumProperty<GateType> GATE_TYPE = EnumProperty.create("gate_type", GateType.class);

    private static final VoxelShape SHAPE_NORMAL_N = Block.box(0, 1.75, 12.5, 16, 3.25, 14);
    private static final VoxelShape SHAPE_NORMAL_S = Block.box(0, 1.75, 2.5, 16, 3.25, 5);
    private static final VoxelShape SHAPE_NORMAL_E = Block.box(2.5, 1.75, 0, 14, 3.25, 16);
    private static final VoxelShape SHAPE_NORMAL_W = Block.box(12.5, 1.75, 0, 14, 3.25, 16);
    private static final VoxelShape SHAPE_SLAB_N = Block.box(0, -6.25, 12.5, 16, -4.75, 14);
    private static final VoxelShape SHAPE_SLAB_S = Block.box(0, -6.25, 2.5, 16, -4.75, 5);
    private static final VoxelShape SHAPE_SLAB_E = Block.box(2.5, -6.25, 0, 14, -4.75, 16);
    private static final VoxelShape SHAPE_SLAB_W = Block.box(12.5, -6.25, 0, 14, -4.75, 16);
    public BarrierGate1PoleHorizontal(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.SINGLE).setValue(GATE_TYPE, GateType.NORMAL));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("block.yunbeiuc.barrier_gate_1_pole_horizontal.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        GateType gateType = state.getValue(GATE_TYPE);
        Direction facing = state.getValue(FACING);

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, GATE_TYPE);
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
        BlockState newState = getRelatedBlockState(state, world, pos, state.getValue(FACING));
        return checkSlabState(newState, world, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide && player.getItemInHand(hand).is(ModItems.WAND.get())) {
            world.setBlock(pos, state.cycle(GATE_TYPE), Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }

    private BlockState getRelatedBlockState(BlockState state, LevelAccessor world, BlockPos pos, Direction direction) {
        boolean left = isRelatedInDirection(world, pos, direction, true);
        boolean right = isRelatedInDirection(world, pos, direction, false);
        if (left && right){
            return state.setValue(TYPE, Type.MIDDLE);
        } else if (right) {
            return state.setValue(TYPE, Type.RIGHT);
        } else if (left) {
            return state.setValue(TYPE, Type.LEFT);
        }
        return state.setValue(TYPE, Type.SINGLE);
    }

    private boolean isRelatedInDirection(LevelAccessor world, BlockPos pos, Direction direction, boolean counterClockwise) {
        Direction rotated = counterClockwise ? direction.getCounterClockWise() : direction.getClockWise();
        return isRelatedBlock(world, pos, rotated, direction);
    }

    private boolean isRelatedBlock(LevelAccessor world, BlockPos pos, Direction rotate, Direction direction) {
        BlockState state = world.getBlockState(pos.relative(rotate));
        if (state.getBlock() == this){
            Direction direction1 = state.getValue(FACING);
            return direction1.equals(direction);
        }
        return false;
    }

    private BlockState checkSlabState(BlockState state, LevelAccessor world, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Direction left = facing.getCounterClockWise();
        BlockState leftState = world.getBlockState(pos.relative(left));
        if (leftState.is(MunicipalBlocks.BARRIER_GATE_1_POLE_HORIZONTAL.get()) && leftState.getValue(GATE_TYPE) == GateType.SLAB) {
            return state.setValue(GATE_TYPE, GateType.SLAB);
        }
        return state;
    }

    public enum Type implements StringRepresentable {
        SINGLE("single"),
        LEFT("left"),
        MIDDLE("middle"),
        RIGHT("right");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public enum GateType implements StringRepresentable {
        NORMAL("normal"),
        SLAB("slab");

        private final String name;

        GateType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
