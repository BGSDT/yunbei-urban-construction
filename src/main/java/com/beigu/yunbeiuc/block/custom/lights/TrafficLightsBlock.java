package com.beigu.yunbeiuc.block.custom.lights;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
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

public class TrafficLightsBlock extends Block {
    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_lights.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(4.25, -1.5, 12.25, 11.75, 17.5, 22);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(4.25, -1.5, 0.25, 11.75, 17.5, 8.75);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(0.25, -1.5, 4.25, 8.75, 17.5, 11.75);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(12.25, -1.5, 4.25, 22, 17.5, 11.75);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<LightState> LIGHT_STATE = EnumProperty.of("light_state", LightState.class);

    public TrafficLightsBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(LIGHT_STATE, LightState.RED)
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
        builder.add(FACING, LIGHT_STATE);
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

    public enum LightState implements StringIdentifiable {
        RED("red"),
        YELLOW("yellow"),
        GREEN("green"),
        GRAY("gray");  // 添加灰色状态用于闪烁

        private final String name;

        LightState(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}