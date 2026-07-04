package com.beigu.yunbeiuc.block.custom.instrument;

import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InstrumentLaneIndicator extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<TFType> TF_TYPE = EnumProperty.of("tf_type", TFType.class);

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(0, 0, 7, 16, 16, 9);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(7, 0, 0, 9, 16, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(7, 0, 0, 9, 16, 16);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(0, 0, 7, 16, 16, 9);

    public InstrumentLaneIndicator(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH)
                .with(TF_TYPE, TFType.ON));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.instrument_lane_indicator"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TF_TYPE);
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
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = player.getStackInHand(hand).getItem();
        if (item == ModItems.WAND.get()) {
            if (!world.isClient) {
                TFType current = state.get(TF_TYPE);
                TFType next = current.next();
                world.setBlockState(pos, state.with(TF_TYPE, next));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public enum TFType implements StringIdentifiable {
        ON("on"),
        OFF("off");

        private final String name;

        TFType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
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