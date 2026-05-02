package com.beigu.yunbeiuc.block.custom.sound;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SoundBarrier2 extends HorizontalFacingBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final IntProperty LAYER = IntProperty.of("layer", 0, 2); // 0=底, 1=中, 2=顶

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(0, 0, 6.5, 16, 16, 9.5);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(0, 0, 6.5, 16, 16, 9.5);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(6.5, 0, 0, 9.5, 16, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(6.5, 0, 0, 9.5, 16, 16);

    public SoundBarrier2(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(LAYER, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, LAYER);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();

        // 检查所有三层是否可放置（包括底层）
        if (!world.getBlockState(pos).canReplace(ctx) ||
                !world.getBlockState(pos.up()).canReplace(ctx) ||
                !world.getBlockState(pos.up(2)).canReplace(ctx)) {
            return null;
        }

        // 检查是否超出世界高度限制
        if (pos.getY() > world.getTopY() - 3) {
            return null;
        }

        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(LAYER, 0);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            Direction direction = state.get(FACING);

            // 放置中层
            world.setBlockState(pos.up(),
                    this.getDefaultState()
                            .with(FACING, direction)
                            .with(HALF, DoubleBlockHalf.LOWER)
                            .with(LAYER, 1),
                    Block.NOTIFY_ALL | Block.FORCE_STATE);

            // 放置顶层
            world.setBlockState(pos.up(2),
                    this.getDefaultState()
                            .with(FACING, direction)
                            .with(HALF, DoubleBlockHalf.UPPER)
                            .with(LAYER, 2),
                    Block.NOTIFY_ALL | Block.FORCE_STATE);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        int layer = state.get(LAYER);

        if (!world.isClient) {
            if (player.isCreative()) {
                // 破坏所有层
                breakOtherLayers(world, pos, layer, player);
            } else {
                // 生存模式下也破坏所有关联方块
                breakOtherLayers(world, pos, layer, player);
            }
        }

        super.onBreak(world, pos, state, player);
    }

    private void breakOtherLayers(World world, BlockPos pos, int currentLayer, PlayerEntity player) {
        // 破坏其他层
        for (int i = 0; i < 3; i++) {
            if (i != currentLayer) {
                BlockPos otherPos = pos.offset(Direction.UP, i - currentLayer);
                breakBlockIfExists(world, otherPos, player);
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
            int layer = state.get(LAYER);

            // 如果更新来自同一结构的其他层，忽略
            if (fromPos.getY() == pos.getY() + 1 - layer ||
                    fromPos.getY() == pos.getY() + 2 - layer ||
                    fromPos.getY() == pos.getY() - layer) {
                // 检查是否是同一结构
                if (world.getBlockState(fromPos).isOf(this)) {
                    return; // 同一结构，不处理
                }
            }

            // 检查其他层是否还存在
            for (int i = 0; i < 3; i++) {
                if (i != layer) {
                    BlockPos otherPos = pos.offset(Direction.UP, i - layer);
                    if (!world.getBlockState(otherPos).isOf(this)) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(),
                                Block.NOTIFY_ALL | Block.SKIP_DROPS);
                        break;
                    }
                }
            }
        }
    }
}