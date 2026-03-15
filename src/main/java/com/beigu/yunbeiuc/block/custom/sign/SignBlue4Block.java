package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.block.ModBlocks;
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

public class SignBlue4Block extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    private static final VoxelShape SHAPE_POLE_L_N = Block.createCuboidShape(-2, -2, 20.1, 18, 18, 21);
    private static final VoxelShape SHAPE_POLE_L_E = Block.createCuboidShape(-5.1, -2, -2, -4, 18, 18);
    private static final VoxelShape SHAPE_POLE_L_S = Block.createCuboidShape(-2, -2, -5.1, 18, 18, -4);
    private static final VoxelShape SHAPE_POLE_L_W = Block.createCuboidShape(21, -2, -2, 22.1, 18, 18);
    private static final VoxelShape SHAPE_POLE_H_N = Block.createCuboidShape(-2, -2, 21.1, 18, 18, 22);
    private static final VoxelShape SHAPE_POLE_H_E = Block.createCuboidShape(-5.1, 2, -2, -4, 18, 18);
    private static final VoxelShape SHAPE_POLE_H_S = Block.createCuboidShape(-2, -2, -5.1, 18, 18, -4);
    private static final VoxelShape SHAPE_POLE_H_W = Block.createCuboidShape(21, -2, -2, 22.1, 18, 18);
    private static final VoxelShape SHAPE_NORMAL_N = Block.createCuboidShape(-2, -2, 15.1, 18, 18, 18);
    private static final VoxelShape SHAPE_NORMAL_E = Block.createCuboidShape(-5.1, -2, -2, -4, 18, 18);
    private static final VoxelShape SHAPE_NORMAL_S = Block.createCuboidShape(-2, -2, -5.1, 18, 18, -4);
    private static final VoxelShape SHAPE_NORMAL_W = Block.createCuboidShape(21, -2, -2, 22.1, 18, 18);

    public SignBlue4Block(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH)
                .with(TYPE, Type.NORMAL));
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

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
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
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        BlockPos backPos = pos.offset(facing);
        BlockState backBlockState = world.getBlockState(backPos);
        Block backBlock = backBlockState.getBlock();

        Type type;
        if (backBlock == ModBlocks.ROAD_POLE_HORIZONTAL) {
            type = Type.POLE_H;
        } else if (backBlock == ModBlocks.ROAD_POLE_LONGITUDINAL) {
            type = Type.POLE_L;
        } else {
            type = Type.NORMAL;
        }

        return this.getDefaultState().with(FACING, facing).with(TYPE, type);
    }

    public enum Type implements StringIdentifiable {
        POLE_L("pole_l"),
        POLE_H("pole_h"),
        NORMAL("normal");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}