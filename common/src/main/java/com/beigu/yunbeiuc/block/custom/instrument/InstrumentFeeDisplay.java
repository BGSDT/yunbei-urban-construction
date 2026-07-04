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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InstrumentFeeDisplay extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<DirectionType> DIRECTION_TYPE = EnumProperty.of("direction_type", DirectionType.class);

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(0, 0, 6, 16, 15, 10);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(6, 0, 0, 10, 15, 16);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(0, 0, 6, 16, 15, 10);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(6, 0, 0, 10, 15, 16);

    public InstrumentFeeDisplay(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH)
                .with(DIRECTION_TYPE, DirectionType.MIDDLE));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.instrument_fee_display.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case WEST -> SHAPE_W;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DIRECTION_TYPE);
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
                DirectionType current = state.get(DIRECTION_TYPE);
                DirectionType next = current.next();
                world.setBlockState(pos, state.with(DIRECTION_TYPE, next));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public enum DirectionType implements StringIdentifiable {
        MIDDLE("middle"),
        LEFT("left"),
        RIGHT("right");

        private final String name;

        DirectionType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public DirectionType next() {
            return switch (this) {
                case MIDDLE -> LEFT;
                case LEFT -> RIGHT;
                case RIGHT -> MIDDLE;
            };
        }
    }
}