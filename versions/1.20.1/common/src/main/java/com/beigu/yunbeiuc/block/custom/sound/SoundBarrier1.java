package com.beigu.yunbeiuc.block.custom.sound;

import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SoundBarrier1 extends HorizontalFacingBlock {
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;
    public static final IntProperty LAYER = IntProperty.of("layer", 0, 2); // 0=底, 1=中, 2=顶

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(0, 0, 7.6, 16, 16, 8.4);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(0, 0, 7.6, 16, 16, 8.4);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(7.6, 0, 0, 8.4, 16, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(7.6, 0, 0, 8.4, 16, 16);

    public SoundBarrier1(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(PART, BedPart.FOOT)
                .with(LAYER, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, LAYER);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
        BlockPos pos = ctx.getBlockPos();
        BlockPos rightPos = pos.offset(direction.rotateYClockwise());

        // 检查右侧方块和中层、顶层方块是否可放置
        World world = ctx.getWorld();
        if (world.getBlockState(rightPos).canReplace(ctx) &&
                world.getBlockState(pos.up()).canReplace(ctx) &&
                world.getBlockState(rightPos.up()).canReplace(ctx) &&
                world.getBlockState(pos.up(2)).canReplace(ctx) &&
                world.getBlockState(rightPos.up(2)).canReplace(ctx)) {
            return this.getDefaultState()
                    .with(FACING, direction)
                    .with(PART, BedPart.FOOT)
                    .with(LAYER, 0);
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

            // 放置右侧同层方块 (HEAD)
            world.setBlockState(rightPos,
                    state.with(PART, BedPart.HEAD).with(LAYER, 0),
                    Block.NOTIFY_ALL | Block.FORCE_STATE);

            // 放置 FOOT 和 HEAD 的中层
            world.setBlockState(pos.up(),
                    state.with(PART, BedPart.FOOT).with(LAYER, 1),
                    Block.NOTIFY_ALL | Block.FORCE_STATE);
            world.setBlockState(rightPos.up(),
                    state.with(PART, BedPart.HEAD).with(LAYER, 1),
                    Block.NOTIFY_ALL | Block.FORCE_STATE);

            // 放置 FOOT 和 HEAD 的顶层
            world.setBlockState(pos.up(2),
                    state.with(PART, BedPart.FOOT).with(LAYER, 2),
                    Block.NOTIFY_ALL | Block.FORCE_STATE);
            world.setBlockState(rightPos.up(2),
                    state.with(PART, BedPart.HEAD).with(LAYER, 2),
                    Block.NOTIFY_ALL | Block.FORCE_STATE);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BedPart part = state.get(PART);
        Direction direction = state.get(FACING);
        int layer = state.get(LAYER);

        if (!world.isClient && player.isCreative()) {
            // 破坏其他所有方块
            breakOtherParts(world, pos, part, direction, layer, player);
        }

        super.onBreak(world, pos, state, player);
    }

    private void breakOtherParts(World world, BlockPos pos, BedPart part,
                                 Direction direction, int layer, PlayerEntity player) {
        BlockPos otherPos = (part == BedPart.FOOT) ?
                pos.offset(direction.rotateYClockwise()) :
                pos.offset(direction.rotateYCounterclockwise());

        // 破坏同层对应方块
        breakBlockIfExists(world, otherPos, player);

        // 破坏其他层对应方块
        for (int i = 0; i < 3; i++) {
            if (i != layer) {
                breakBlockIfExists(world,
                        pos.offset(Direction.UP, i - layer), player);
                breakBlockIfExists(world,
                        otherPos.offset(Direction.UP, i - layer), player);
            }
        }
    }

    private void breakBlockIfExists(World world, BlockPos pos, PlayerEntity player) {
        BlockState state = world.getBlockState(pos);
        if (state.isOf(this)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(),
                    Block.NOTIFY_ALL | Block.SKIP_DROPS);
            world.syncWorldEvent(player, 2001, pos,
                    Block.getRawIdFromState(state));
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
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
            int layer = state.get(LAYER);

            // 检查其他部分是否还存在
            BlockPos otherPos = (part == BedPart.FOOT) ?
                    pos.offset(direction.rotateYClockwise()) :
                    pos.offset(direction.rotateYCounterclockwise());

            if (!world.getBlockState(otherPos).isOf(this) ||
                    world.getBlockState(otherPos).get(LAYER) != layer) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(),
                        Block.NOTIFY_ALL | Block.SKIP_DROPS);
            }
        }
    }
}