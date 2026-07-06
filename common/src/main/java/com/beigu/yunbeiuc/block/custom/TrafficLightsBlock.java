package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrafficLightsBlock extends BlockWithEntity implements BlockEntityProvider {

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
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_lights.tooltip"));
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
        builder.add(FACING, LIGHT_STATE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrafficLightsBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);

        // 魔杖设置相位
        if (heldItem.isOf(ModItems.WAND.get())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
                if (!world.isClient()) {
                    // 检查是否已设置时间和链接组
                    if (!trafficLightsBE.hasTimings()) {
                        player.sendMessage(Text.literal("§c请先使用命令设置时间！"), true);
                        return ActionResult.FAIL;
                    }
                    if (!trafficLightsBE.isInGroup()) {
                        player.sendMessage(Text.literal("§c此红绿灯未链接到任何组！"), true);
                        return ActionResult.FAIL;
                    }

                    // 循环切换相位：1 -> 2 -> 3 -> ... -> phaseCount -> 1
                    int currentPhase = trafficLightsBE.getPhaseIndex();
                    int nextPhase;
                    if (currentPhase < 0 || currentPhase >= trafficLightsBE.getPhaseCount() - 1) {
                        nextPhase = 0;
                    } else {
                        nextPhase = currentPhase + 1;
                    }

                    trafficLightsBE.setPhaseIndex(nextPhase, player);
                }
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                return ActionResult.success(world.isClient());
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
            trafficLightsBE.tick();
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, 1);
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
                if (trafficLightsBE.getGroupId() != null) {
                    trafficLightsBE.unloadGroup();
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public enum LightState implements StringIdentifiable {
        RED("red"),
        YELLOW("yellow"),
        GREEN("green"),
        GRAY("gray");

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