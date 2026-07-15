package com.beigu.yunbeiuc.block.custom.guardrail;

import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RoadClosedBarricadeGuardrail4 extends HorizontalFacingBlock {
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;

    private static final VoxelShape FOOT_SHAPE_NORTH = Block.createCuboidShape(2, 0, 0, 16, 18, 16);
    private static final VoxelShape FOOT_SHAPE_SOUTH = Block.createCuboidShape(2, 0, 0, 16, 18, 16);
    private static final VoxelShape FOOT_SHAPE_EAST = Block.createCuboidShape(0, 0, 5, 16, 18, 16);
    private static final VoxelShape FOOT_SHAPE_WEST = Block.createCuboidShape(0, 0, 5, 16, 18, 16);

    private static final VoxelShape HEAD_SHAPE_NORTH = Block.createCuboidShape(0, 0, 0, 14, 18, 16);
    private static final VoxelShape HEAD_SHAPE_SOUTH = Block.createCuboidShape(0, 0, 0, 14, 18, 16);
    private static final VoxelShape HEAD_SHAPE_EAST = Block.createCuboidShape(0, 0, 5, 16, 18, 16);
    private static final VoxelShape HEAD_SHAPE_WEST = Block.createCuboidShape(0, 0, 5, 16, 18, 16);
    public RoadClosedBarricadeGuardrail4(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(PART, BedPart.FOOT));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
        BlockPos pos = ctx.getBlockPos();
        BlockPos rightPos = pos.offset(direction.rotateYClockwise());

        if (ctx.getWorld().getBlockState(rightPos).canReplace(ctx)) {
            return this.getDefaultState()
                    .with(FACING, direction)
                    .with(PART, BedPart.FOOT);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            Direction direction = state.get(FACING);
            BlockPos rightPos = pos.offset(direction.rotateYClockwise());

            world.setBlockState(rightPos,
                    state.with(PART, BedPart.HEAD),
                    Block.NOTIFY_ALL | Block.FORCE_STATE);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BedPart part = state.get(PART);
        Direction direction = state.get(FACING);

        BlockPos otherPos;
        if (part == BedPart.FOOT) {
            otherPos = pos.offset(direction.rotateYClockwise());
        } else {
            otherPos = pos.offset(direction.rotateYCounterclockwise());
        }

        BlockState otherState = world.getBlockState(otherPos);
        if (otherState.isOf(this) && otherState.get(PART) != part) {
            world.setBlockState(otherPos, Blocks.AIR.getDefaultState(),
                    Block.NOTIFY_ALL | Block.SKIP_DROPS);
            world.syncWorldEvent(player, 2001, otherPos,
                    Block.getRawIdFromState(otherState));
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        BedPart part = state.get(PART);

        if (part == BedPart.FOOT) {
            return switch (direction) {
                case SOUTH -> FOOT_SHAPE_SOUTH;
                case EAST -> FOOT_SHAPE_EAST;
                case WEST -> FOOT_SHAPE_WEST;
                default -> FOOT_SHAPE_NORTH;
            };
        } else {
            return switch (direction) {
                case SOUTH -> HEAD_SHAPE_SOUTH;
                case EAST -> HEAD_SHAPE_EAST;
                case WEST -> HEAD_SHAPE_WEST;
                default -> HEAD_SHAPE_NORTH;
            };
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            BedPart part = state.get(PART);
            Direction direction = state.get(FACING);

            BlockPos otherPos = (part == BedPart.FOOT) ?
                    pos.offset(direction.rotateYClockwise()) :
                    pos.offset(direction.rotateYCounterclockwise());

            BlockState otherState = world.getBlockState(otherPos);

            if (!otherState.isOf(this)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(),
                        Block.NOTIFY_ALL | Block.SKIP_DROPS);
            }
        }
    }
}