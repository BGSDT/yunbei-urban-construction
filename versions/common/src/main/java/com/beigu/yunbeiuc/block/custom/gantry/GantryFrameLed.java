package com.beigu.yunbeiuc.block.custom.gantry;

import com.beigu.yunbeiuc.block.custom.sign.CustomTextDisplayBlock;
import com.beigu.yunbeiuc.entity.GantryFrameLedEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GantryFrameLed extends CustomTextDisplayBlock {
    public GantryFrameLed(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.SINGLE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("block.yunbeiuc.gantry_frame_led.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_N = Block.box(0, 0, 2, 16, 16, 7);
    private static  final VoxelShape SHAPE_W = Block.box(2, 0, 0, 7, 16, 16);
    private static final VoxelShape SHAPE_S = Block.box(0, 0, 2, 16, 16, 7);
    private static final VoxelShape SHAPE_E = Block.box(9, 0, 0, 14, 16, 16);

    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case WEST -> SHAPE_W;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING,TYPE);
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
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return this.getRelatedBlockState(state,world,pos,state.getValue(FACING));
    }

    private BlockState getRelatedBlockState(BlockState state, LevelAccessor world, BlockPos pos, Direction direction) {
        boolean left = isRelatedInDirection(world, pos, direction, true);
        boolean right = isRelatedInDirection(world, pos, direction, false);
        if (left && right){
            return state.setValue(TYPE, Type.MIDDLE);
        } else if (right) {
            return state.setValue(TYPE, Type.RIGHT);
        } else if (left) {
            return state.setValue(TYPE, Type.LEFT);
        }
        return state.setValue(TYPE, Type.SINGLE);
    }

    private boolean isRelatedInDirection(LevelAccessor world, BlockPos pos, Direction direction, boolean counterClockwise) {
        Direction rotated = counterClockwise ? direction.getCounterClockWise() : direction.getClockWise();
        return this.isRelatedBlock(world, pos, rotated, direction);
    }

    private boolean isRelatedBlock(LevelAccessor world, BlockPos pos, Direction rotate, Direction direction) {
        BlockState state = world.getBlockState(pos.relative(rotate));
        if (state.getBlock() == this){
            Direction direction1 = state.getValue(FACING);
            return direction1.equals(direction);
        }
        return false;
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new GantryFrameLedEntity(pos, state);
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
