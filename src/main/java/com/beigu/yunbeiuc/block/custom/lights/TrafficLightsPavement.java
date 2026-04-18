package com.beigu.yunbeiuc.block.custom.lights;

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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrafficLightsPavement extends Block {
    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_lights_pavement.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(4.25, -0.5, 10.25, 11.75, 16.5, 21);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(4.25, -0.5, 4.25, 11.75, 16.5, 21);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(10.25, -0.5, 4.25, 21, 16.5, 11.75);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(4.25, -0.5, 4.25, 21, 16.5, 11.75);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<LightPavementState> LIGHT_PAVEMENT_STATE = EnumProperty.of("light_pavement_state", LightPavementState.class);

    public TrafficLightsPavement(Settings settings) {
        super(settings);
        this.setDefaultState(
                getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(LIGHT_PAVEMENT_STATE, LightPavementState.RED)
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
        builder.add(FACING, LIGHT_PAVEMENT_STATE);
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
        if (heldItem.isOf(ModItems.WAND)) {
            LightPavementState nextState = state.get(LIGHT_PAVEMENT_STATE).next();
            world.setBlockState(pos, state.with(LIGHT_PAVEMENT_STATE, nextState));
            return ActionResult.success(world.isClient());
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public enum LightPavementState implements StringIdentifiable {
        RED("red"),
        GREEN("green");

        private final String name;

        LightPavementState(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        // 状态循环：红→绿→红
        public LightPavementState next() {
            return switch (this) {
                case RED -> GREEN;
                case GREEN -> RED;
            };
        }
    }
}