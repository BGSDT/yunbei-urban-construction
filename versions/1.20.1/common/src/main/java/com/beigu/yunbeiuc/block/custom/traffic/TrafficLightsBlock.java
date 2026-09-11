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

    // 普通竖装/黄色竖装：x[2.75,13.25] y[-4.75,20.75]（南向基准 z[8.5,16]）
    private static final VoxelShape VERTICAL_N = Block.createCuboidShape(2.75, -4.75, 0, 13.25, 20.75, 7.5);
    private static final VoxelShape VERTICAL_S = Block.createCuboidShape(2.75, -4.75, 8.5, 13.25, 20.75, 16);
    private static final VoxelShape VERTICAL_E = Block.createCuboidShape(8.5, -4.75, 2.75, 16, 20.75, 13.25);
    private static final VoxelShape VERTICAL_W = Block.createCuboidShape(0, -4.75, 2.75, 7.5, 20.75, 13.25);

    // 横装/台北式：x[-4.75,20.75] y[2.75,13.25]（南向基准 z[8.5,16]）
    private static final VoxelShape HORIZONTAL_N = Block.createCuboidShape(-4.75, 2.75, 0, 20.75, 13.25, 7.5);
    private static final VoxelShape HORIZONTAL_S = Block.createCuboidShape(-4.75, 2.75, 8.5, 20.75, 13.25, 16);
    private static final VoxelShape HORIZONTAL_E = Block.createCuboidShape(8.5, 2.75, -4.75, 16, 13.25, 20.75);
    private static final VoxelShape HORIZONTAL_W = Block.createCuboidShape(0, 2.75, -4.75, 7.5, 13.25, 20.75);

    // 人行道：x[2.75,13.25] y[-0.25,16.25]（南向基准 z[8.5,16]）
    private static final VoxelShape PAVEMENT_N = Block.createCuboidShape(2.75, -0.25, 0, 13.25, 16.25, 7.5);
    private static final VoxelShape PAVEMENT_S = Block.createCuboidShape(2.75, -0.25, 8.5, 13.25, 16.25, 16);
    private static final VoxelShape PAVEMENT_E = Block.createCuboidShape(8.5, -0.25, 2.75, 16, 16.25, 13.25);
    private static final VoxelShape PAVEMENT_W = Block.createCuboidShape(0, -0.25, 2.75, 7.5, 16.25, 13.25);

    // 单灯（横式/竖式）：x[2.75,13.25] y[2.75,13.25]（南向基准 z[8.5,16]）
    private static final VoxelShape SINGLE_N = Block.createCuboidShape(2.75, 2.75, 0, 13.25, 13.25, 7.5);
    private static final VoxelShape SINGLE_S = Block.createCuboidShape(2.75, 2.75, 8.5, 13.25, 13.25, 16);
    private static final VoxelShape SINGLE_E = Block.createCuboidShape(8.5, 2.75, 2.75, 16, 13.25, 13.25);
    private static final VoxelShape SINGLE_W = Block.createCuboidShape(0, 2.75, 2.75, 7.5, 13.25, 13.25);

    // 读秒器：x[0,16] y[2,16]（南向基准 z[7.75,16]）
    private static final VoxelShape COUNTDOWN_N = Block.createCuboidShape(0, 2, 0, 16, 16, 8.25);
    private static final VoxelShape COUNTDOWN_S = Block.createCuboidShape(0, 2, 7.75, 16, 16, 16);
    private static final VoxelShape COUNTDOWN_E = Block.createCuboidShape(7.75, 2, 0, 16, 16, 16);
    private static final VoxelShape COUNTDOWN_W = Block.createCuboidShape(0, 2, 0, 8.25, 16, 16);

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
        VoxelShape north = SHAPE_N, south = SHAPE_S, east = SHAPE_E, west = SHAPE_W;
        Block block = state.getBlock();

        if (block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_VERTICAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_VERTICAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_YELLOW_VERTICAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()) {
            north = VERTICAL_N;
            south = VERTICAL_S;
            east = VERTICAL_E;
            west = VERTICAL_W;
        } else if (block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_HORIZONTAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_HORIZONTAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GREEN_TAIPEI.get()) {
            north = HORIZONTAL_N;
            south = HORIZONTAL_S;
            east = HORIZONTAL_E;
            west = HORIZONTAL_W;
        } else if (block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()) {
            north = PAVEMENT_N;
            south = PAVEMENT_S;
            east = PAVEMENT_E;
            west = PAVEMENT_W;
        } else if (block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SINGLE_HORIZONTAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SINGLE_HORIZONTAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SINGLE_VERTICAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SINGLE_VERTICAL.get()) {
            north = SINGLE_N;
            south = SINGLE_S;
            east = SINGLE_E;
            west = SINGLE_W;
        } else if (block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get()) {
            north = COUNTDOWN_N;
            south = COUNTDOWN_S;
            east = COUNTDOWN_E;
            west = COUNTDOWN_W;
        }

        return switch (state.get(FACING)) {
            case WEST -> west;
            case SOUTH -> south;
            case EAST -> east;
            default -> north;
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
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_GREEN_TAIPEI.get();
            boolean isPavement = currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                    || currentBlock == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get();

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