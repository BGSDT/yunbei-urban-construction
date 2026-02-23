package com.beigu.yunbeiuc.block.custom.instrument;

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

public class InstrumentCamera extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<DirectionType> DIRECTION_TYPE = EnumProperty.of("direction_type", DirectionType.class);

    private static final VoxelShape SHAPE_MIDDLE_N = Block.createCuboidShape(5.9, -9.536743128535363e-8, 1.9624997138977047, 10.1, 5.649999904632569, 11.962499713897706);
    private static final VoxelShape SHAPE_MIDDLE_S = Block.createCuboidShape(5.9, -9.536743128535363e-8, 4.037500286102295, 10.1, 5.649999904632569, 14.037500286102297);
    private static final VoxelShape SHAPE_MIDDLE_E = Block.createCuboidShape(4.037500286102295, -9.536743128535363e-8, 5.9, 14.037500286102297, 5.649999904632569, 10.1);
    private static final VoxelShape SHAPE_MIDDLE_W = Block.createCuboidShape(1.9624997138977047, -9.536743128535363e-8, 5.9, 11.962499713897706, 5.649999904632569, 10.1);
    private static final VoxelShape SHAPE_LEFT_N = Block.createCuboidShape(5.9, -9.536743128535363e-8, 1.9624997138977047, 10.1, 5.649999904632569, 11.962499713897706);
    private static final VoxelShape SHAPE_LEFT_S = Block.createCuboidShape(5.9, -9.536743128535363e-8, 4.037500286102295, 10.1, 5.649999904632569, 14.037500286102297);
    private static final VoxelShape SHAPE_LEFT_E = Block.createCuboidShape(4.037500286102295, -9.536743128535363e-8, 5.9, 14.037500286102297, 5.649999904632569, 10.1);
    private static final VoxelShape SHAPE_LEFT_W = Block.createCuboidShape(1.9624997138977047, -9.536743128535363e-8, 5.9, 11.962499713897706, 5.649999904632569, 10.1);
    private static final VoxelShape SHAPE_RIGHT_N = Block.createCuboidShape(5.9, -9.536743128535363e-8, 1.9624997138977047, 10.1, 5.649999904632569, 11.962499713897706);
    private static final VoxelShape SHAPE_RIGHT_S = Block.createCuboidShape(5.9, -9.536743128535363e-8, 4.037500286102295, 10.1, 5.649999904632569, 14.037500286102297);
    private static final VoxelShape SHAPE_RIGHT_E = Block.createCuboidShape(4.037500286102295, -9.536743128535363e-8, 5.9, 14.037500286102297, 5.649999904632569, 10.1);
    private static final VoxelShape SHAPE_RIGHT_W = Block.createCuboidShape(1.9624997138977047, -9.536743128535363e-8, 5.9, 11.962499713897706, 5.649999904632569, 10.1);

    public InstrumentCamera(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH)
                .with(DIRECTION_TYPE, DirectionType.MIDDLE));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        DirectionType type = state.get(DIRECTION_TYPE);
        return switch (type) {
            case MIDDLE -> switch (facing) {
                case SOUTH -> SHAPE_MIDDLE_S;
                case EAST -> SHAPE_MIDDLE_E;
                case WEST -> SHAPE_MIDDLE_W;
                default -> SHAPE_MIDDLE_N;
            };
            case LEFT -> switch (facing) {
                case SOUTH -> SHAPE_LEFT_S;
                case EAST -> SHAPE_LEFT_E;
                case WEST -> SHAPE_LEFT_W;
                default -> SHAPE_LEFT_N;
            };
            case RIGHT -> switch (facing) {
                case SOUTH -> SHAPE_RIGHT_S;
                case EAST -> SHAPE_RIGHT_E;
                case WEST -> SHAPE_RIGHT_W;
                default -> SHAPE_RIGHT_N;
            };
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DIRECTION_TYPE);
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
                DirectionType current = state.get(DIRECTION_TYPE);
                DirectionType next = current.next();
                world.setBlockState(pos, state.with(DIRECTION_TYPE, next));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public enum DirectionType implements StringIdentifiable {
        MIDDLE("middle"),
        LEFT("left"),
        RIGHT("right");

        private final String name;

        DirectionType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public DirectionType next() {
            return switch (this) {
                case MIDDLE -> LEFT;
                case LEFT -> RIGHT;
                case RIGHT -> MIDDLE;
            };
        }
    }
}