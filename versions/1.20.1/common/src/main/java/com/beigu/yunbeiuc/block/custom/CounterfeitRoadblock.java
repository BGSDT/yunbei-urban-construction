package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.HashSet;
import java.util.Set;

public class CounterfeitRoadblock extends Block {
    public CounterfeitRoadblock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, Type.SINGLE)
                .with(ACTIVE, false));
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    private static final VoxelShape SHAPE_OFF = Block.createCuboidShape(0, 0, 0, 16, 0.5, 16);
    private static final VoxelShape SHAPE_ON = Block.createCuboidShape(0, 0, 0, 16, 9.5, 16);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(ACTIVE) ? SHAPE_ON : SHAPE_OFF;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, ACTIVE);
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
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(TYPE, getRelatedType(ctx.getWorld(), ctx.getBlockPos(), ctx.getHorizontalPlayerFacing().getOpposite()));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            return this.getRelatedBlockState(state, world, pos, state.get(FACING));
        }
        return state;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);

        // 检查玩家是否手持 ModItem.Wand
        if (heldItem.isOf(ModItems.WAND.get()) && !world.isClient()) {
            // 切换整个连接组的状态
            toggleConnectedGroup(world, pos, state.get(FACING), !state.get(ACTIVE));
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }

    private void toggleConnectedGroup(World world, BlockPos startPos, Direction facing, boolean newActiveState) {
        Set<BlockPos> visited = new HashSet<>();
        toggleRecursive(world, startPos, facing, newActiveState, visited);
    }

    private void toggleRecursive(World world, BlockPos pos, Direction facing, boolean newActiveState, Set<BlockPos> visited) {
        if (visited.contains(pos)) {
            return;
        }
        visited.add(pos);

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) {
            return;
        }

        // 切换当前方块
        if (state.get(ACTIVE) != newActiveState) {
            world.setBlockState(pos, state.with(ACTIVE, newActiveState));
            world.playSound(null, pos,
                    newActiveState ? SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF,
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        // 递归检查左右相邻的方块
        Direction leftDir = facing.rotateYCounterclockwise();
        Direction rightDir = facing.rotateYClockwise();

        BlockPos leftPos = pos.offset(leftDir);
        BlockPos rightPos = pos.offset(rightDir);

        BlockState leftState = world.getBlockState(leftPos);
        BlockState rightState = world.getBlockState(rightPos);

        if (leftState.getBlock() == this && leftState.get(FACING) == facing) {
            toggleRecursive(world, leftPos, facing, newActiveState, visited);
        }
        if (rightState.getBlock() == this && rightState.get(FACING) == facing) {
            toggleRecursive(world, rightPos, facing, newActiveState, visited);
        }
    }

    private BlockState getRelatedBlockState(BlockState state, WorldAccess world, BlockPos pos, Direction direction) {
        Type type = getRelatedType(world, pos, direction);
        return state.with(TYPE, type);
    }

    private Type getRelatedType(WorldAccess world, BlockPos pos, Direction direction) {
        boolean left = isRelatedInDirection(world, pos, direction, true);
        boolean right = isRelatedInDirection(world, pos, direction, false);
        if (left && right) {
            return Type.MIDDLE;
        } else if (right) {
            return Type.RIGHT;
        } else if (left) {
            return Type.LEFT;
        }
        return Type.SINGLE;
    }

    private boolean isRelatedInDirection(WorldAccess world, BlockPos pos, Direction direction, boolean counterClockwise) {
        Direction rotated = counterClockwise ? direction.rotateYCounterclockwise() : direction.rotateYClockwise();
        return this.isRelatedBlock(world, pos, rotated, direction);
    }

    private boolean isRelatedBlock(WorldAccess world, BlockPos pos, Direction rotate, Direction direction) {
        BlockState state = world.getBlockState(pos.offset(rotate));
        if (state.getBlock() == this) {
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