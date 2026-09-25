package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashSet;
import java.util.Set;

public class CounterfeitRoadblock extends Block {
    public CounterfeitRoadblock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.SINGLE).setValue(ACTIVE, true));
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape SHAPE_OFF = Block.box(0, 0, 0, 16, 0.5, 16);
    private static final VoxelShape SHAPE_ON = Block.box(0, 0, 0, 16, 9.5, 16);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(ACTIVE) ? SHAPE_ON : SHAPE_OFF;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, ACTIVE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(TYPE, getRelatedType(ctx.getLevel(), ctx.getClickedPos(), ctx.getHorizontalDirection().getOpposite()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            return this.getRelatedBlockState(state, world, pos, state.getValue(FACING));
        }
        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        // 检查玩家是否手持 ModItem.Wand
        if (heldItem.is(ModItems.WAND.get()) && !world .isClientSide) {
            // 切换整个连接组的状态
            toggleConnectedGroup(world, pos, state.getValue(FACING), !state.getValue(ACTIVE));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    private void toggleConnectedGroup(Level world, BlockPos startPos, Direction facing, boolean newActiveState) {
        Set<BlockPos> visited = new HashSet<>();
        toggleRecursive(world, startPos, facing, newActiveState, visited);
    }

    private void toggleRecursive(Level world, BlockPos pos, Direction facing, boolean newActiveState, Set<BlockPos> visited) {
        if (visited.contains(pos)) {
            return;
        }
        visited.add(pos);

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) {
            return;
        }

        // 切换当前方块
        if (state.getValue(ACTIVE) != newActiveState) {
            world.setBlock(pos, state.setValue(ACTIVE, newActiveState), Block.UPDATE_ALL);
            world.playSound(null, pos,
                    newActiveState ? SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        // 递归检查左右相邻的方块
        Direction leftDir = facing.getCounterClockWise();
        Direction rightDir = facing.getClockWise();

        BlockPos leftPos = pos.relative(leftDir);
        BlockPos rightPos = pos.relative(rightDir);

        BlockState leftState = world.getBlockState(leftPos);
        BlockState rightState = world.getBlockState(rightPos);

        if (leftState.getBlock() == this && leftState.getValue(FACING) == facing) {
            toggleRecursive(world, leftPos, facing, newActiveState, visited);
        }
        if (rightState.getBlock() == this && rightState.getValue(FACING) == facing) {
            toggleRecursive(world, rightPos, facing, newActiveState, visited);
        }
    }

    private BlockState getRelatedBlockState(BlockState state, LevelAccessor world, BlockPos pos, Direction direction) {
        Type type = getRelatedType(world, pos, direction);
        return state.setValue(TYPE, type);
    }

    private Type getRelatedType(LevelAccessor world, BlockPos pos, Direction direction) {
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

    private boolean isRelatedInDirection(LevelAccessor world, BlockPos pos, Direction direction, boolean counterClockwise) {
        Direction rotated = counterClockwise ? direction.getCounterClockWise() : direction.getClockWise();
        return this.isRelatedBlock(world, pos, rotated, direction);
    }

    private boolean isRelatedBlock(LevelAccessor world, BlockPos pos, Direction rotate, Direction direction) {
        BlockState state = world.getBlockState(pos.relative(rotate));
        if (state.getBlock() == this) {
            Direction direction1 = state.getValue(FACING);
            return direction1.equals(direction);
        }
        return false;
    }

    public enum Type implements StringRepresentable {
        SINGLE("single"),
        LEFT("left"),
        MIDDLE("middle"),
        RIGHT("right");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
