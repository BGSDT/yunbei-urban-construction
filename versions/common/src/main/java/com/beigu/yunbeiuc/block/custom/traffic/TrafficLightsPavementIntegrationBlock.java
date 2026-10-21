package com.beigu.yunbeiuc.block.custom.traffic;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.entity.TrafficLightsPavementIntegrationBlockEntity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.screen.TrafficLightsScreen;
import com.beigu.yunbeiuc.screen.TrafficLightsSimpleStaticStateScreen;
import com.beigu.yunbeiuc.screen.TrafficLightsStaticStateScreen;
import com.beigu.yunbeiuc.screen.TrafficLightsTimingScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

// 人行道一体化红绿灯方块：相位/图案逻辑与 TrafficLightsBlock 同一套，只是模型与贴图不同。

public class TrafficLightsPavementIntegrationBlock extends TrafficLightsBlock {

    public static final EnumProperty<TriplePart> PART = EnumProperty.create("part", TriplePart.class);

    public TrafficLightsPavementIntegrationBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIGHT_STATE, TYPE, MOUNT, PART);
    }

    public static final VoxelShape SHAPE_N = Block.box(3.5, 0, 5, 12.5, 16, 11);
    public static final VoxelShape SHAPE_E = Block.box(5, 0, 3.5, 11, 16, 12.5);
    public static final VoxelShape SHAPE_S = Block.box(3.5, 0, 5, 12.5, 16, 11);
    public static final VoxelShape SHAPE_W = Block.box(5, 0, 3.5, 11, 16, 12.5);

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        BlockPos middlePos = pos.above();
        BlockPos topPos = pos.above(2);

        if (ctx.getLevel().getBlockState(middlePos).canBeReplaced(ctx) &&
            ctx.getLevel().getBlockState(topPos).canBeReplaced(ctx) &&
            pos.getY() < ctx.getLevel().getMaxBuildHeight() - 2) {
            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(PART, TriplePart.BOTTOM);
        }
        return null;
    }

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
    public void setPlacedBy(Level world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);

        if (!world.isClientSide) {
            TriplePart part = state.getValue(PART);

            if (part == TriplePart.BOTTOM) {
                Direction direction = state.getValue(FACING);
                LightState lightState = state.getValue(LIGHT_STATE);
                BlockPos middlePos = pos.above();
                BlockPos topPos = pos.above(2);

                world.setBlock(middlePos,
                        state.setValue(PART, TriplePart.MIDDLE).setValue(FACING, direction).setValue(LIGHT_STATE, lightState),
                        VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());

                world.setBlock(topPos,
                        state.setValue(PART, TriplePart.TOP).setValue(FACING, direction).setValue(LIGHT_STATE, lightState),
                        VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());
            }
        }
    }

    @Override
    public void playerWillDestroyCompat(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            TriplePart part = state.getValue(PART);

            BlockPos bottomPos = switch (part) {
                case BOTTOM -> pos;
                case MIDDLE -> pos.below();
                case TOP -> pos.below(2);
            };

            TrafficLightsGroupTracker.track(world, bottomPos, player);

            for (int i = 0; i < 3; i++) {
                BlockPos currentPos = bottomPos.above(i);
                BlockState currentState = world.getBlockState(currentPos);

                if (currentState.is(this) && !currentPos.equals(pos)) {
                    world.setBlock(currentPos, Blocks.AIR.defaultBlockState(),
                            VersionServices.blocks().updateAll() | VersionServices.blocks().updateNeighbors());
                    world.levelEvent(player, 2001, currentPos,
                            Block.getId(currentState));
                }
            }
        }
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        if (state.hasProperty(PART) && state.getValue(PART) != TriplePart.BOTTOM) {
            return null;
        }
        return new TrafficLightsPavementIntegrationBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useCompat(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        TriplePart part = state.getValue(PART);
        BlockPos bottomPos = switch (part) {
            case BOTTOM -> pos;
            case MIDDLE -> pos.below();
            case TOP -> pos.below(2);
        };

        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() == ModItems.LINK_WAND.get()) {
            return InteractionResult.PASS;
        }

        if (heldItem.getItem() == ModItems.WAND.get()) {
            if (world .isClientSide) {
                BlockEntity blockEntity = world.getBlockEntity(bottomPos);
                if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
                    if (!trafficLightsBE.isInGroup()) {
                        openStaticStateScreen(bottomPos);
                        return InteractionResult.sidedSuccess(true);
                    }
                    if (!trafficLightsBE.hasTimings()) {
                        openTimingScreen(bottomPos);
                        return InteractionResult.sidedSuccess(true);
                    }
                    openDisplayScreen(bottomPos);
                }
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return InteractionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void openDisplayScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new TrafficLightsScreen(pos));
    }

    @Environment(EnvType.CLIENT)
    private void openStaticStateScreen(BlockPos pos) {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof TrafficLightsBlockEntity tl) {
            Block currentBlock = tl.getBlockState().getBlock();
            boolean isCountdownTimer = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get();
            boolean isShanghai = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GREEN_TAIPEI.get();
            boolean isPavement = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get();

            if (isCountdownTimer) {
                Minecraft.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsCountdownTimerStaticStateScreen(pos));
            } else if (isShanghai) {
                Minecraft.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsShanghaiStaticStateScreen(pos));
            } else if (isPavement) {
                Minecraft.getInstance().setScreen(new TrafficLightsSimpleStaticStateScreen(pos));
            } else {
                Minecraft.getInstance().setScreen(new TrafficLightsStaticStateScreen(pos));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void openTimingScreen(BlockPos pos) {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof TrafficLightsBlockEntity tl) {
            Minecraft.getInstance().setScreen(new TrafficLightsTimingScreen(
                    tl.getGroupId(), tl.getGroupPositions()));
        }
    }

    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public enum TriplePart implements StringRepresentable {
        BOTTOM("bottom"),
        MIDDLE("middle"),
        TOP("top");

        private final String name;

        TriplePart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
