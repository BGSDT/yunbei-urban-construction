package com.beigu.yunbeiuc.block.custom.poles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RoadLight extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<TFType> TF_TYPE = EnumProperty.of("tf_type", TFType.class);

    private static final VoxelShape SHAPE_N = VoxelShapes.combineAndSimplify(Block.createCuboidShape(6.5, 6.5, 13.2, 9.5, 9.5, 16), Block.createCuboidShape(4.9, 6.5, 0, 11.1, 9.8, 13.2), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_S = VoxelShapes.combineAndSimplify(Block.createCuboidShape(6.5, 6.5, 0, 9.5, 9.5, 2.8), Block.createCuboidShape(4.9, 6.5, 2.8, 11.1, 9.8, 16), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_E = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 6.5, 6.5, 2.8, 9.5, 9.5), Block.createCuboidShape(2.8, 6.5, 4.9, 16, 9.8, 11.1), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_W = VoxelShapes.combineAndSimplify(Block.createCuboidShape(13.2, 6.5, 6.5, 16, 9.5, 9.5), Block.createCuboidShape(0, 6.5, 4.9, 13.2, 9.8, 11.1), BooleanBiFunction.OR);

    public RoadLight(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH)
                .with(TF_TYPE, TFType.FALSE));
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
        builder.add(FACING, TF_TYPE);
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = player.getStackInHand(hand).getItem();
        if (item == Items.STICK) {
            if (!world.isClient) {
                TFType current = state.get(TF_TYPE);
                TFType next = current.next();
                world.setBlockState(pos, state.with(TF_TYPE, next));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public enum TFType implements StringIdentifiable {
        TRUE("true"),
        FALSE("false");

        private final String name;

        TFType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public TFType next() {
            return switch (this) {
                case TRUE -> FALSE;
                case FALSE -> TRUE;
            };
        }
    }
}