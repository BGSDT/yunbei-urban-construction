package com.beigu.yunbeiuc.block.custom.traffic;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.entity.TrafficLightsPavementIntegrationBlockEntity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.screen.TrafficLightsSimpleStaticStateScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TrafficLightsPavementIntegrationBlock extends TrafficLightsBlock {

    public static final EnumProperty<TriplePart> PART = EnumProperty.of("part", TriplePart.class);

    public TrafficLightsPavementIntegrationBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIGHT_STATE, TYPE, PART);
    }

    public static final VoxelShape SHAPE_N = Block.createCuboidShape(3.5, 0, 5, 12.5, 16, 11);
    public static final VoxelShape SHAPE_E = Block.createCuboidShape(5, 0, 3.5, 11, 16, 12.5);
    public static final VoxelShape SHAPE_S = Block.createCuboidShape(3.5, 0, 5, 12.5, 16, 11);
    public static final VoxelShape SHAPE_W = Block.createCuboidShape(5, 0, 3.5, 11, 16, 12.5);

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        BlockPos middlePos = pos.up();
        BlockPos topPos = pos.up(2);

        if (ctx.getWorld().getBlockState(middlePos).canReplace(ctx) &&
            ctx.getWorld().getBlockState(topPos).canReplace(ctx) &&
            pos.getY() < ctx.getWorld().getTopY() - 2) {
            return this.getDefaultState()
                    .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                    .with(PART, TriplePart.BOTTOM);
        }
        return null;
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
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            TriplePart part = state.get(PART);

            if (part == TriplePart.BOTTOM) {
                Direction direction = state.get(FACING);
                LightState lightState = state.get(LIGHT_STATE);
                BlockPos middlePos = pos.up();
                BlockPos topPos = pos.up(2);

                world.setBlockState(middlePos,
                        state.with(PART, TriplePart.MIDDLE).with(FACING, direction).with(LIGHT_STATE, lightState),
                        Block.NOTIFY_ALL | Block.FORCE_STATE);

                world.setBlockState(topPos,
                        state.with(PART, TriplePart.TOP).with(FACING, direction).with(LIGHT_STATE, lightState),
                        Block.NOTIFY_ALL | Block.FORCE_STATE);
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            TriplePart part = state.get(PART);

            BlockPos bottomPos = switch (part) {
                case BOTTOM -> pos;
                case MIDDLE -> pos.down();
                case TOP -> pos.down(2);
            };

            for (int i = 0; i < 3; i++) {
                BlockPos currentPos = bottomPos.up(i);
                BlockState currentState = world.getBlockState(currentPos);

                if (currentState.isOf(this) && !currentPos.equals(pos)) {
                    world.setBlockState(currentPos, Blocks.AIR.getDefaultState(),
                            Block.NOTIFY_ALL | Block.SKIP_DROPS);
                    world.syncWorldEvent(player, 2001, currentPos,
                            Block.getRawIdFromState(currentState));
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(PART) == TriplePart.BOTTOM) {
            return new TrafficLightsPavementIntegrationBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        TriplePart part = state.get(PART);
        BlockPos bottomPos = switch (part) {
            case BOTTOM -> pos;
            case MIDDLE -> pos.down();
            case TOP -> pos.down(2);
        };

        ItemStack heldItem = player.getStackInHand(hand);

        if (heldItem.isOf(ModItems.LINK_WAND.get())) {
            return ActionResult.PASS;
        }

        if (heldItem.isOf(ModItems.WAND.get())) {
            if (world.isClient()) {
                BlockEntity blockEntity = world.getBlockEntity(bottomPos);
                if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
                    if (!trafficLightsBE.isInGroup()) {
                        // 未分组：打开静态状态设置界面
                        MinecraftClient.getInstance().setScreen(new TrafficLightsSimpleStaticStateScreen(bottomPos));
                        return ActionResult.success(true);
                    }
                    if (!trafficLightsBE.hasTimings()) {
                        // 已分组但未设置时间表：打开时间设置界面
                        MinecraftClient.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsTimingScreen(
                                trafficLightsBE.getGroupId(), trafficLightsBE.getGroupPositions()));
                        return ActionResult.success(true);
                    }
                    // 已分组且已设置时间：打开显示界面
                    MinecraftClient.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsScreen(bottomPos));
                }
            }
            return ActionResult.success(world.isClient());
        }

        return ActionResult.SUCCESS;
    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    public enum TriplePart implements StringIdentifiable {
        BOTTOM("bottom"),
        MIDDLE("middle"),
        TOP("top");

        private final String name;

        TriplePart(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
