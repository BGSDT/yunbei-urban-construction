package com.beigu.yunbeiuc.block.custom.pole;

import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
import java.util.stream.Stream;

public class RoadRadarSpeedDetector extends Block {
    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.radar_speed_detector.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }
    public static final BooleanProperty LIT = Properties.LIT;
    private static final VoxelShape SHAPE_N = VoxelShapes.combineAndSimplify(Block.createCuboidShape(5.5, 5.5, 0, 10.5, 10, 16), Block.createCuboidShape(4.75, 10, 5, 11.25, 18, 11), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_S = VoxelShapes.combineAndSimplify(Block.createCuboidShape(5.5, 5.5, 0, 10.5, 10, 16), Block.createCuboidShape(4.75, 10, 5, 11.25, 18, 11), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_E = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 5.5, 5.5, 16, 10.5, 10.5), Block.createCuboidShape(5, 10, 4.75, 11, 18, 11.25), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_W = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 5.5, 5.5, 16, 10.5, 10.5), Block.createCuboidShape(5, 10, 4.75, 11, 18, 11.25), BooleanBiFunction.OR);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<LightTFState> LIGHT_TF_STATE = EnumProperty.of("light_tf_state", LightTFState.class);

    public RoadRadarSpeedDetector(Settings settings) {
        super(settings.luminance(state -> state.get(LIT) ? 15 : 0));
        this.setDefaultState(
                getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(LIT, true)
                        .with(LIGHT_TF_STATE, LightTFState.TRUE)
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
        builder.add(FACING,LIT,LIGHT_TF_STATE);
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
        // 直接使用 ModItems.WAND 判断是否为魔杖
        if (!world.isClient()) {
            if (heldItem.isOf(ModItems.WAND.get())) {
                boolean newLitState = !state.get(LIT);
                LightTFState newLightState = state.get(LIGHT_TF_STATE).next();
                world.setBlockState(pos, state.with(LIT, newLitState).with(LIGHT_TF_STATE, newLightState));
            }
        }
        return ActionResult.SUCCESS;
    }

    public enum LightTFState implements StringIdentifiable {
        TRUE("true"),
        FALSE("false");

        private final String name;

        LightTFState(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        // 状态循环： TRUE -> FALSE -> TRUE
        public LightTFState next() {
            return switch (this) {
                case TRUE -> FALSE;
                case FALSE -> TRUE;
            };
        }
    }
}