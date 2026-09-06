package com.beigu.yunbeiuc.block.custom.traffic;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.screen.TrafficLightsScreen;
import com.beigu.yunbeiuc.screen.TrafficLightsStaticStateScreen;
import com.beigu.yunbeiuc.screen.TrafficLightsTimingScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class  TrafficLightsBlock extends BlockWithEntity implements BlockEntityProvider {

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(0, 4, 0, 16, 12, 8);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(8, 4, 0, 16, 12, 16);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(0, 4, 8, 16, 12, 16);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(0, 4, 0, 8, 12, 16);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<LightState> LIGHT_STATE = EnumProperty.of("light_state", LightState.class);
    public static final EnumProperty<MountType> TYPE = EnumProperty.of("type", MountType.class);

    public TrafficLightsBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(LIGHT_STATE, LightState.RED)
                        .with(TYPE, MountType.SIMPLE)
        );
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_lights.tooltip"));
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_lights.tooltip.auto_detect"));
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
        builder.add(FACING, LIGHT_STATE, TYPE);
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
        BlockState state = getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());

        // 雾灯方块默认为黄色
        if (this == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_FOGGY.get()) {
            state = state.with(LIGHT_STATE, LightState.YELLOW);
        }

        // 自动检测背后是否有路杆方块
        Direction facing = state.get(FACING);
        BlockPos behindPos = ctx.getBlockPos().offset(facing.getOpposite());
        BlockState behindState = ctx.getWorld().getBlockState(behindPos);

        MountType type = determineType(behindState);
        state = state.with(TYPE, type);

        return state;
    }

    private boolean isPoleBlock(Block block) {
        return block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_FOUNDATIONS.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_FOUNDATIONS_SLAB.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LONGITUDINAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_HORIZONTAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_TSHAPE.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_TEXT_DISPLAY.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LED.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_FLAG.get();
    }

    private MountType determineType(BlockState behindState) {
        Block behindBlock = behindState.getBlock();
        if (isPoleBlock(behindBlock)) {
            return MountType.POLE;
        } else {
            return MountType.SIMPLE;
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
                                                BlockState neighborState, WorldAccess world,
                                                BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        if (direction == facing.getOpposite()) {
            MountType newType = determineType(neighborState);
            if (newType != state.get(TYPE)) {
                return state.with(TYPE, newType);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrafficLightsBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);

        // 魔杖交互
        if (heldItem.isOf(ModItems.WAND.get())) {
            // Shift + 右键切换 type 状态
            if (player.isSneaking()) {
                if (!world.isClient()) {
                    MountType currentType = state.get(TYPE);
                    MountType newType = currentType == MountType.SIMPLE ? MountType.POLE : MountType.SIMPLE;
                    world.setBlockState(pos, state.with(TYPE, newType));
                    player.sendMessage(Text.literal("§a已切换至 " + (newType == MountType.POLE ? "§6路杆模式" : "§6简易模式")), true);
                }
                return ActionResult.success(world.isClient());
            }

            // 普通右键打开设置界面
            if (world.isClient()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
                    if (!trafficLightsBE.isInGroup()) {
                        // 未分组：打开静态状态设置界面
                        openStaticStateScreen(pos);
                        return ActionResult.success(true);
                    }
                    if (!trafficLightsBE.hasTimings()) {
                        // 已分组但未设置时间表：打开时间设置界面
                        openTimingScreen(pos);
                        return ActionResult.success(true);
                    }
                    // 打开GUI
                    openDisplayScreen(pos);
                }
            }
            return ActionResult.success(world.isClient());
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Environment(EnvType.CLIENT)
    private void openDisplayScreen(BlockPos pos) {
        MinecraftClient.getInstance().setScreen(new TrafficLightsScreen(pos));
    }

    @Environment(EnvType.CLIENT)
    private void openStaticStateScreen(BlockPos pos) {
        BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(pos);
        if (blockEntity instanceof TrafficLightsBlockEntity tl) {
            Block currentBlock = tl.getCachedState().getBlock();
            boolean isCountdownTimer = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get();
            boolean isShanghai = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get();
            boolean isPavement = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get();

            if (isCountdownTimer) {
                // 读秒器：无方向列表，仅选颜色 + 秒数输入 + 是否显示秒数开关
                MinecraftClient.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsCountdownTimerStaticStateScreen(pos));
            } else if (isShanghai) {
                // 上海红绿灯：有方向列表 + 秒数输入 + 是否显示秒数开关
                MinecraftClient.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsShanghaiStaticStateScreen(pos));
            } else if (isPavement) {
                // 人行道红绿灯：无方向列表，仅选颜色 + 秒数输入 + 是否显示秒数开关
                MinecraftClient.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsSimpleStaticStateScreen(pos));
            } else {
                // 普通红绿灯（含单灯横式/竖式）：有方向列表，无秒数相关控件
                MinecraftClient.getInstance().setScreen(new TrafficLightsStaticStateScreen(pos));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void openTimingScreen(BlockPos pos) {
        BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(pos);
        if (blockEntity instanceof TrafficLightsBlockEntity tl) {
            MinecraftClient.getInstance().setScreen(new TrafficLightsTimingScreen(tl.getGroupId(), tl.getGroupPositions()));
        }
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

    public enum MountType implements StringIdentifiable {
        SIMPLE("simple"),
        POLE("pole");

        private final String name;

        MountType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}