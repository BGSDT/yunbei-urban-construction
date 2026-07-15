package com.beigu.yunbeiuc.block.custom.rubbish;

import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
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

public class RubbishBinGrayGreen extends Block {
    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.rubbish_bin.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    private static final VoxelShape SHAPE_N = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 4.25, 16, 14.5, 11.75),
            Block.createCuboidShape(7, 14.5, 4.25, 9, 16, 11.75),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_E = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(4.25, 0, 0, 11.75, 14.5, 16),
            Block.createCuboidShape(4.25, 14.5, 7, 11.75, 16, 9),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_S = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 4.25, 16, 14.5, 11.75),
            Block.createCuboidShape(7, 14.5, 4.25, 9, 16, 11.75),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_W = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(4.25, 0, 0, 11.75, 14.5, 16),
            Block.createCuboidShape(4.25, 14.5, 7, 11.75, 16, 9),
            BooleanBiFunction.OR
    );
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<NumberState> NUMBER_STATE = EnumProperty.of("number_state", NumberState.class);

    public RubbishBinGrayGreen(Settings settings) {
        super(settings);
        this.setDefaultState(
                getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(NUMBER_STATE, NumberState.ZERO)
        );
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
        builder.add(FACING, NUMBER_STATE);
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
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        NumberState currentState = state.get(NUMBER_STATE);
        if (heldItem.isOf(ModItems.WAND.get())) {
            world.setBlockState(pos, state.with(NUMBER_STATE, NumberState.ZERO));
            return ActionResult.success(world.isClient());
        }else {
            if (currentState == NumberState.NINE) {
                player.sendMessage(Text.translatable("block.yunbeiuc.rubbish_bin.full"), true);
                return ActionResult.success(world.isClient());
            } else if (!heldItem.isEmpty()) {
                // 移除手中的物品（设置为空栈）
                player.setStackInHand(hand, ItemStack.EMPTY);
                NumberState nextState = state.get(NUMBER_STATE).next();
                world.setBlockState(pos, state.with(NUMBER_STATE, nextState));
                return ActionResult.success(world.isClient());
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public enum NumberState implements StringIdentifiable {
        ZERO("zero"),
        ONE("one"),
        TWO("two"),
        THREE("three"),
        FOUR("four"),
        FIVE("five"),
        SIX("six"),
        SEVEN("seven"),
        EIGHT("eight"),
        NINE("nine");

        private final String name;

        NumberState(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        // 状态循环：红→绿→红
        public NumberState next() {
            return switch (this) {
                case ZERO -> ONE;
                case ONE -> TWO;
                case TWO -> THREE;
                case THREE -> FOUR;
                case FOUR -> FIVE;
                case FIVE -> SIX;
                case SIX -> SEVEN;
                case SEVEN -> EIGHT;
                case EIGHT -> NINE;
                case NINE -> ZERO;
            };
        }
    }
}