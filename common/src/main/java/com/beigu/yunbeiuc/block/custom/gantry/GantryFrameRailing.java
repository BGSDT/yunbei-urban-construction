package com.beigu.yunbeiuc.block.custom.gantry;

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
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class GantryFrameRailing extends Block {
    public GantryFrameRailing(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(TYPE, Type.SINGLE));
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_N = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 0, 2, 16, 16, 3), Block.createCuboidShape(0, 0, 13, 16, 16, 14), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_S = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 0, 13, 16, 16, 14), Block.createCuboidShape(0, 0, 2, 16, 16, 3), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_E = VoxelShapes.combineAndSimplify(Block.createCuboidShape(13, 0, 0, 14, 16, 16), Block.createCuboidShape(2, 0, 0, 3, 16, 16), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_W = VoxelShapes.combineAndSimplify(Block.createCuboidShape(2, 0, 0, 3, 16, 16), Block.createCuboidShape(13, 0, 0, 14, 16, 16), BooleanBiFunction.OR);

    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

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
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return this.getRelatedBlockState(state,world,pos,state.get(FACING));
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
        return this.isRelatedBlock(world, pos, rotated, direction);
    }

    private boolean isRelatedBlock(WorldAccess world, BlockPos pos, Direction rotate, Direction direction) {
        BlockState state = world.getBlockState(pos.offset(rotate));
        if (state.getBlock() == this){
            Direction direction1 = state.get(FACING);
            return direction1.equals(direction);
        }
        return false;
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
}
