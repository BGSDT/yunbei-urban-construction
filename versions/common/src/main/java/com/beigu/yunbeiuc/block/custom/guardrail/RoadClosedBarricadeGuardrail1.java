package com.beigu.yunbeiuc.block.custom.guardrail;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RoadClosedBarricadeGuardrail1 extends DirectionalBlock {
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

    private static final VoxelShape FOOT_SHAPE_NORTH = Shapes.join(Block.box(0, 1, 7, 14, 19, 8.5), Block.box(5, 0, 2, 6, 1, 14), BooleanOp.OR);
    private static final VoxelShape FOOT_SHAPE_SOUTH = Shapes.join(Block.box(7, 1, 0, 8.5, 19, 14), Block.box(2, 0, 5, 14, 1, 6), BooleanOp.OR);
    private static final VoxelShape FOOT_SHAPE_EAST = Shapes.join(Block.box(0, 1, 5, 14, 19, 8.5), Block.box(2, 0, 7, 6, 1, 14), BooleanOp.OR);
    private static final VoxelShape FOOT_SHAPE_WEST = Shapes.join(Block.box(7, 1, 5, 8.5, 19, 14), Block.box(0, 0, 2, 14, 1, 6), BooleanOp.OR);
    private static final VoxelShape HEAD_SHAPE_NORTH = Shapes.join(Block.box(0, 1, 7, 14, 19, 8.5), Block.box(10, 0, 2, 11, 1, 14), BooleanOp.OR);
    private static final VoxelShape HEAD_SHAPE_SOUTH = Shapes.join(Block.box(7, 1, 0, 8.5, 19, 14), Block.box(7, 0, 2, 14, 1, 6), BooleanOp.OR);
    private static final VoxelShape HEAD_SHAPE_EAST = Shapes.join(Block.box(0, 1, 5, 14, 19, 8.5), Block.box(7, 0, 2, 14, 1, 6), BooleanOp.OR);
    private static final VoxelShape HEAD_SHAPE_WEST = Shapes.join(Block.box(7, 1, 5, 8.5, 19, 14), Block.box(0, 0, 2, 14, 1, 6), BooleanOp.OR);

    public RoadClosedBarricadeGuardrail1(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, BedPart.FOOT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getHorizontalDirection().getOpposite();
        BlockPos pos = ctx.getClickedPos();
        BlockPos rightPos = pos.relative(direction.getClockWise());

        if (ctx.getLevel().getBlockState(rightPos).canBeReplaced(ctx)) {
            return this.defaultBlockState().setValue(FACING, direction).setValue(PART, BedPart.FOOT);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);

        if (!world.isClientSide) {
            Direction direction = state.getValue(FACING);
            BlockPos rightPos = pos.relative(direction.getClockWise());

            world.setBlock(rightPos,
                    state.setValue(PART, BedPart.HEAD),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BedPart part = state.getValue(PART);
        Direction direction = state.getValue(FACING);

        BlockPos otherPos;
        if (part == BedPart.FOOT) {
            otherPos = pos.relative(direction.getClockWise());
        } else {
            otherPos = pos.relative(direction.getCounterClockWise());
        }

        BlockState otherState = world.getBlockState(otherPos);
        if (otherState.is(this) && otherState.getValue(PART) != part) {
            world.setBlock(otherPos, Blocks.AIR.defaultBlockState(),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateNeighbors());
            world.levelEvent(player, 2001, otherPos,
                    Block.getId(otherState));
        }

        super .playerWillDestroy(world, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world,
                                      BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        BedPart part = state.getValue(PART);

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
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos,
                               Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClientSide) {
            BedPart part = state.getValue(PART);
            Direction direction = state.getValue(FACING);

            BlockPos otherPos = (part == BedPart.FOOT) ?
                    pos.relative(direction.getClockWise()) :
                    pos.relative(direction.getCounterClockWise());

            BlockState otherState = world.getBlockState(otherPos);

            if (!otherState.is(this)) {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(),
                        VersionServices.blocks().updateAll() | VersionServices.blocks().updateNeighbors());
            }
        }
    }
}
