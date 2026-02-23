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

public class RoadClosedBarricadeGuardrail1 extends HorizontalFacingBlock {
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;

    // 碰撞箱 - 每个部分占据半个格子
    private static final VoxelShape FOOT_SHAPE_NORTH = Block.createCuboidShape(0, 0, 0, 8, 19, 16);  // 左边
    private static final VoxelShape HEAD_SHAPE_NORTH = Block.createCuboidShape(8, 0, 0, 16, 19, 16); // 右边

    private static final VoxelShape FOOT_SHAPE_SOUTH = Block.createCuboidShape(8, 0, 0, 16, 19, 16);  // 左边
    private static final VoxelShape HEAD_SHAPE_SOUTH = Block.createCuboidShape(0, 0, 0, 8, 19, 16);   // 右边

    private static final VoxelShape FOOT_SHAPE_EAST = Block.createCuboidShape(0, 0, 8, 16, 19, 16);   // 左边
    private static final VoxelShape HEAD_SHAPE_EAST = Block.createCuboidShape(0, 0, 0, 16, 19, 8);    // 右边

    private static final VoxelShape FOOT_SHAPE_WEST = Block.createCuboidShape(0, 0, 0, 16, 19, 8);    // 左边
    private static final VoxelShape HEAD_SHAPE_WEST = Block.createCuboidShape(0, 0, 8, 16, 19, 16);   // 右边

    public RoadClosedBarricadeGuardrail1(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(PART, BedPart.FOOT));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    // --- 核心逻辑 1：放置 ---
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getHorizontalPlayerFacing();
        BlockPos pos = ctx.getBlockPos();
        BlockPos rightPos = pos.offset(direction.rotateYClockwise()); // 右方

        // 检查右方格子是否可以替换
        if (ctx.getWorld().getBlockState(rightPos).canReplace(ctx)) {
            return this.getDefaultState()
                    .with(FACING, direction)
                    .with(PART, BedPart.FOOT);
        }
        return null; // 空间不够，无法放置
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            Direction direction = state.get(FACING);
            BlockPos rightPos = pos.offset(direction.rotateYClockwise());

            // 在右方放置另一半
            world.setBlockState(rightPos,
                    state.with(PART, BedPart.HEAD),
                    Block.NOTIFY_ALL | Block.FORCE_STATE);
        }
    }

    // --- 核心逻辑 2：破坏同步 ---
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BedPart part = state.get(PART);
        Direction direction = state.get(FACING);

        // 计算另一半的位置
        BlockPos otherPos;
        if (part == BedPart.FOOT) {
            // FOOT在左边，HEAD在右边
            otherPos = pos.offset(direction.rotateYClockwise());
        } else {
            // HEAD在右边，FOOT在左边
            otherPos = pos.offset(direction.rotateYCounterclockwise());
        }

        BlockState otherState = world.getBlockState(otherPos);
        // 确保另一半也是这个方块，防止误删
        if (otherState.isOf(this) && otherState.get(PART) != part) {
            world.setBlockState(otherPos, Blocks.AIR.getDefaultState(),
                    Block.NOTIFY_ALL | Block.SKIP_DROPS);
            world.syncWorldEvent(player, 2001, otherPos,
                    Block.getRawIdFromState(otherState));
        }

        super.onBreak(world, pos, state, player);
    }

    // --- 碰撞箱 ---
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        BedPart part = state.get(PART);

        // 根据朝向和部分返回不同的碰撞箱
        if (part == BedPart.FOOT) {
            // FOOT部分（玩家放置的部分）
            return switch (direction) {
                case NORTH -> FOOT_SHAPE_NORTH;  // 面向北时，FOOT在左边（西边）
                case SOUTH -> FOOT_SHAPE_SOUTH;  // 面向南时，FOOT在左边（东边）
                case EAST -> FOOT_SHAPE_EAST;    // 面向东时，FOOT在左边（北边）
                case WEST -> FOOT_SHAPE_WEST;    // 面向西时，FOOT在左边（南边）
                default -> FOOT_SHAPE_NORTH;
            };
        } else {
            // HEAD部分（右方部分）
            return switch (direction) {
                case NORTH -> HEAD_SHAPE_NORTH;  // 面向北时，HEAD在右边（东边）
                case SOUTH -> HEAD_SHAPE_SOUTH;  // 面向南时，HEAD在右边（西边）
                case EAST -> HEAD_SHAPE_EAST;    // 面向东时，HEAD在右边（南边）
                case WEST -> HEAD_SHAPE_WEST;    // 面向西时，HEAD在右边（北边）
                default -> HEAD_SHAPE_NORTH;
            };
        }
    }

    // --- 旋转支持 ---
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    // --- 镜像支持 ---
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    // --- 防止活塞推动 ---
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    // --- 可选：防止方块实体被破坏 ---
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            BedPart part = state.get(PART);
            Direction direction = state.get(FACING);

            // 检查另一半是否存在
            BlockPos otherPos = (part == BedPart.FOOT) ?
                    pos.offset(direction.rotateYClockwise()) :
                    pos.offset(direction.rotateYCounterclockwise());

            BlockState otherState = world.getBlockState(otherPos);

            // 如果另一半不存在或不是同一方块，删除这一半
            if (!otherState.isOf(this)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(),
                        Block.NOTIFY_ALL | Block.SKIP_DROPS);
            }
        }
    }
}