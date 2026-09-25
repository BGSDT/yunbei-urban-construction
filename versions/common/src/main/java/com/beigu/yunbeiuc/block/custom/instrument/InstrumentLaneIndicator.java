package com.beigu.yunbeiuc.block.custom.instrument;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionResult;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InstrumentLaneIndicator extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<TFType> TF_TYPE = EnumProperty.create("tf_type", TFType.class);

    private static final VoxelShape SHAPE_N = Block.box(0, 0, 7, 16, 16, 9);
    private static final VoxelShape SHAPE_E = Block.box(7, 0, 0, 9, 16, 16);
    private static final VoxelShape SHAPE_W = Block.box(7, 0, 0, 9, 16, 16);
    private static final VoxelShape SHAPE_S = Block.box(0, 0, 7, 16, 16, 9);

    public InstrumentLaneIndicator(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TF_TYPE, TFType.ON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("block.yunbeiuc.instrument_lane_indicator.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TF_TYPE);
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = player.getItemInHand(hand).getItem();
        if (item == ModItems.WAND.get()) {
            if (!world.isClientSide) {
                TFType current = state.getValue(TF_TYPE);
                TFType next = current.next();
                world.setBlock(pos, state.setValue(TF_TYPE, next), Block.UPDATE_ALL);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public enum TFType implements StringRepresentable {
        ON("on"),
        OFF("off");

        private final String name;

        TFType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public TFType next() {
            return switch (this) {
                case ON -> OFF;
                case OFF -> ON;
            };
        }
    }
}
