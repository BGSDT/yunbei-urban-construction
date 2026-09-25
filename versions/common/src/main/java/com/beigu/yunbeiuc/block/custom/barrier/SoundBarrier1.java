package com.beigu.yunbeiuc.block.custom.barrier;

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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SoundBarrier1 extends DirectionalBlock {
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final IntegerProperty LAYER = IntegerProperty.create("layer", 0, 2); // 0=底, 1=中, 2=顶

    private static final VoxelShape SHAPE_N = Block.box(0, 0, 7.6, 16, 16, 8.4);
    private static final VoxelShape SHAPE_S = Block.box(0, 0, 7.6, 16, 16, 8.4);
    private static final VoxelShape SHAPE_E = Block.box(7.6, 0, 0, 8.4, 16, 16);
    private static final VoxelShape SHAPE_W = Block.box(7.6, 0, 0, 8.4, 16, 16);

    public SoundBarrier1(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, BedPart.FOOT).setValue(LAYER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, LAYER);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getHorizontalDirection().getOpposite();
        BlockPos pos = ctx.getClickedPos();
        BlockPos rightPos = pos.relative(direction.getClockWise());

        // 检查右侧方块和中层、顶层方块是否可放置
        Level world = ctx.getLevel();
        if (world.getBlockState(rightPos).canBeReplaced(ctx) &&
                world.getBlockState(pos.above()).canBeReplaced(ctx) &&
                world.getBlockState(rightPos.above()).canBeReplaced(ctx) &&
                world.getBlockState(pos.above(2)).canBeReplaced(ctx) &&
                world.getBlockState(rightPos.above(2)).canBeReplaced(ctx)) {
            return this.defaultBlockState().setValue(FACING, direction).setValue(PART, BedPart.FOOT).setValue(LAYER, 0);
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

            // 放置右侧同层方块 (HEAD)
            world.setBlock(rightPos,
                    state.setValue(PART, BedPart.HEAD).setValue(LAYER, 0),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());

            // 放置 FOOT 和 HEAD 的中层
            world.setBlock(pos.above(),
                    state.setValue(PART, BedPart.FOOT).setValue(LAYER, 1),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());
            world.setBlock(rightPos.above(),
                    state.setValue(PART, BedPart.HEAD).setValue(LAYER, 1),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());

            // 放置 FOOT 和 HEAD 的顶层
            world.setBlock(pos.above(2),
                    state.setValue(PART, BedPart.FOOT).setValue(LAYER, 2),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());
            world.setBlock(rightPos.above(2),
                    state.setValue(PART, BedPart.HEAD).setValue(LAYER, 2),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BedPart part = state.getValue(PART);
        Direction direction = state.getValue(FACING);
        int layer = state.getValue(LAYER);

        if (!world.isClientSide && player.isCreative()) {
            // 破坏其他所有方块
            breakOtherParts(world, pos, part, direction, layer, player);
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    private void breakOtherParts(Level world, BlockPos pos, BedPart part,
                                 Direction direction, int layer, Player player) {
        BlockPos otherPos = (part == BedPart.FOOT) ?
                pos.relative(direction.getClockWise()) :
                pos.relative(direction.getCounterClockWise());

        // 破坏同层对应方块
        breakBlockIfExists(world, otherPos, player);

        // 破坏其他层对应方块
        for (int i = 0; i < 3; i++) {
            if (i != layer) {
                breakBlockIfExists(world,
                        pos.relative(Direction.UP, i - layer), player);
                breakBlockIfExists(world,
                        otherPos.relative(Direction.UP, i - layer), player);
            }
        }
    }

    private void breakBlockIfExists(Level world, BlockPos pos, Player player) {
        BlockState state = world.getBlockState(pos);
        if (state.is(this)) {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateNeighbors());
            world.levelEvent(player, 2001, pos,
                    Block.getId(state));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world,
                                      BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
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
            int layer = state.getValue(LAYER);

            // 检查其他部分是否还存在
            BlockPos otherPos = (part == BedPart.FOOT) ?
                    pos.relative(direction.getClockWise()) :
                    pos.relative(direction.getCounterClockWise());

            if (!world.getBlockState(otherPos).is(this) ||
                    world.getBlockState(otherPos).getValue(LAYER) != layer) {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(),
                        VersionServices.blocks().updateAll() | VersionServices.blocks().updateNeighbors());
            }
        }
    }
}
