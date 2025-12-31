package com.beigu.yunbeiuc.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class HorizontalDoubleBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LEFT = BooleanProperty.of("left");

    // 碰撞箱：全方块大小
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    public HorizontalDoubleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LEFT, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEFT);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getHorizontalPlayerFacing();

        // 获取右侧位置
        BlockPos rightPos = pos.offset(getRightDirection(facing));

        // 检查右侧位置是否可以放置
        if (canPlaceAt(pos, world, facing) && canPlaceAt(rightPos, world, facing)) {
            return this.getDefaultState()
                    .with(FACING, facing.getOpposite())
                    .with(LEFT, true); // 左侧部分
        }

        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            Direction facing = state.get(FACING);
            BlockPos rightPos = pos.offset(getRightDirection(facing));

            // 放置右侧部分
            world.setBlockState(rightPos, state.with(LEFT, false), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            Direction facing = state.get(FACING);
            boolean isLeft = state.get(LEFT);

            // 获取对应的另一个部分的位置
            BlockPos otherPos;
            if (isLeft) {
                // 如果是左侧部分，破坏右侧部分
                otherPos = pos.offset(getRightDirection(facing));
            } else {
                // 如果是右侧部分，破坏左侧部分
                otherPos = pos.offset(getLeftDirection(facing));
            }

            // 防止递归调用
            BlockState otherState = world.getBlockState(otherPos);
            if (otherState.isOf(this)) {
                world.setBlockState(otherPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                world.syncWorldEvent(player, 2001, otherPos, Block.getRawIdFromState(otherState));
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        boolean isLeft = state.get(LEFT);

        // 检查另一个部分是否存在
        BlockPos otherPos = isLeft ?
                pos.offset(getRightDirection(facing)) :
                pos.offset(getLeftDirection(facing));

        BlockState otherState = world.getBlockState(otherPos);
        if (!otherState.isOf(this)) {
            // 如果另一个部分被破坏，破坏这个部分
            return Blocks.AIR.getDefaultState();
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean canPlaceAt(BlockPos pos, World world, Direction facing) {
        // 检查位置是否可放置
        BlockState state = world.getBlockState(pos);
        return state.canReplace(new ItemPlacementContext(
                world, null, null, ItemStack.EMPTY,
                net.minecraft.util.hit.BlockHitResult.createMissed(
                        net.minecraft.util.math.Vec3d.ofCenter(pos),
                        facing.getOpposite(),
                        pos
                )
        ));
    }

    private Direction getRightDirection(Direction facing) {
        return facing.rotateYClockwise(); // 相对于面向方向的右侧
    }

    private Direction getLeftDirection(Direction facing) {
        return facing.rotateYCounterclockwise(); // 相对于面向方向的左侧
    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        // 防止活塞推动
        return PistonBehavior.BLOCK;
    }
}