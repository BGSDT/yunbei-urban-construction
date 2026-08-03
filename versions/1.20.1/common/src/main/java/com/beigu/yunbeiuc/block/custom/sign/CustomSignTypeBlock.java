package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.block.custom.pole.RoadPoleHorizontal;
import com.beigu.yunbeiuc.block.custom.pole.RoadPoleLongitudinal;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CustomSignTypeBlock extends CustomSignBlock {
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    public enum Type implements StringIdentifiable {
        POLE_L("pole_l"),
        POLE_H("pole_h"),
        NORMAL("normal");

        private final String name;
        Type(String name) { this.name = name; }
        @Override
        public String asString() { return this.name; }
    }

    private static final VoxelShape SHAPE_POLE_L_N = Block.createCuboidShape(0, 0, 19.1, 16, 16, 20);
    private static final VoxelShape SHAPE_POLE_L_E = Block.createCuboidShape(-2.1, 0, 0, -1, 16, 16);
    private static final VoxelShape SHAPE_POLE_L_S = Block.createCuboidShape(0, 0, -2.1, 16, 16, -1);
    private static final VoxelShape SHAPE_POLE_L_W = Block.createCuboidShape(20, 0, 0, 21.1, 16, 16);

    private static final VoxelShape SHAPE_POLE_H_N = Block.createCuboidShape(0, 0, 20.1, 16, 16, 21);
    private static final VoxelShape SHAPE_POLE_H_E = Block.createCuboidShape(-3.1, 0, 0, -2, 16, 16);
    private static final VoxelShape SHAPE_POLE_H_S = Block.createCuboidShape(0, 0, -3.1, 16, 16, -2);
    private static final VoxelShape SHAPE_POLE_H_W = Block.createCuboidShape(20, 0, 0, 21.1, 16, 16);

    private static final VoxelShape SHAPE_NORMAL_N = Block.createCuboidShape(0, 0, 15.1, 16, 16, 16);
    private static final VoxelShape SHAPE_NORMAL_S = Block.createCuboidShape(0, 0, 0, 16, 16, 0.9);
    private static final VoxelShape SHAPE_NORMAL_E = Block.createCuboidShape(0, 0, 0, 0.9, 16, 16);
    private static final VoxelShape SHAPE_NORMAL_W = Block.createCuboidShape(15.1, 0, 0, 16, 16, 16);

    public CustomSignTypeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, Type.NORMAL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        Type type = state.get(TYPE);
        return switch (type) {
            case POLE_L -> switch (facing) {
                case SOUTH -> SHAPE_POLE_L_S;
                case EAST -> SHAPE_POLE_L_E;
                case WEST -> SHAPE_POLE_L_W;
                default -> SHAPE_POLE_L_N;
            };
            case POLE_H -> switch (facing) {
                case SOUTH -> SHAPE_POLE_H_S;
                case EAST -> SHAPE_POLE_H_E;
                case WEST -> SHAPE_POLE_H_W;
                default -> SHAPE_POLE_H_N;
            };
            case NORMAL -> switch (facing) {
                case SOUTH -> SHAPE_NORMAL_S;
                case EAST -> SHAPE_NORMAL_E;
                case WEST -> SHAPE_NORMAL_W;
                default -> SHAPE_NORMAL_N;
            };
        };
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();

        BlockPos behindPos = pos.offset(facing.getOpposite());
        BlockState behindState = world.getBlockState(behindPos);

        Type type = determineType(behindState);

        return this.getDefaultState()
                .with(FACING, facing)
                .with(TYPE, type);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
                                                BlockState neighborState, WorldAccess world,
                                                BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        if (direction == facing.getOpposite()) {
            Type newType = determineType(neighborState);
            if (newType != state.get(TYPE)) {
                return state.with(TYPE, newType);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private Type determineType(BlockState behindState) {
        Block behindBlock = behindState.getBlock();
        if (behindBlock instanceof RoadPoleHorizontal) return Type.POLE_H;
        else if (behindBlock instanceof RoadPoleLongitudinal) return Type.POLE_L;
        else return Type.NORMAL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CustomSignBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        return super.onUse(state, world, pos, player, hand, hit);
    }
}