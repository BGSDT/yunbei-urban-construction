package com.beigu.yunbeiuc.block.custom.traffic;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.mapper.TickingEntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.screen.TrafficLightsScreen;
import com.beigu.yunbeiuc.screen.TrafficLightsStaticStateScreen;
import com.beigu.yunbeiuc.screen.TrafficLightsTimingScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrafficLightsBlock extends TickingEntityBlock {

    private static final VoxelShape SHAPE_N = Block.box(0, 4, 0, 16, 12, 8);
    private static final VoxelShape SHAPE_E = Block.box(8, 4, 0, 16, 12, 16);
    private static final VoxelShape SHAPE_S = Block.box(0, 4, 8, 16, 12, 16);
    private static final VoxelShape SHAPE_W = Block.box(0, 4, 0, 8, 12, 16);

    // 普通竖装/黄色竖装：x[2.75,13.25] y[-4.75,20.75]（南向基准 z[8.5,16]）
    private static final VoxelShape VERTICAL_N = Block.box(2.75, -4.75, 0, 13.25, 20.75, 7.5);
    private static final VoxelShape VERTICAL_S = Block.box(2.75, -4.75, 8.5, 13.25, 20.75, 16);
    private static final VoxelShape VERTICAL_E = Block.box(8.5, -4.75, 2.75, 16, 20.75, 13.25);
    private static final VoxelShape VERTICAL_W = Block.box(0, -4.75, 2.75, 7.5, 20.75, 13.25);

    // 横装/台北式：x[-4.75,20.75] y[2.75,13.25]（南向基准 z[8.5,16]）
    private static final VoxelShape HORIZONTAL_N = Block.box(-4.75, 2.75, 0, 20.75, 13.25, 7.5);
    private static final VoxelShape HORIZONTAL_S = Block.box(-4.75, 2.75, 8.5, 20.75, 13.25, 16);
    private static final VoxelShape HORIZONTAL_E = Block.box(8.5, 2.75, -4.75, 16, 13.25, 20.75);
    private static final VoxelShape HORIZONTAL_W = Block.box(0, 2.75, -4.75, 7.5, 13.25, 20.75);

    // 人行道：x[2.75,13.25] y[-0.25,16.25]（南向基准 z[8.5,16]）
    private static final VoxelShape PAVEMENT_N = Block.box(2.75, -0.25, 0, 13.25, 16.25, 7.5);
    private static final VoxelShape PAVEMENT_S = Block.box(2.75, -0.25, 8.5, 13.25, 16.25, 16);
    private static final VoxelShape PAVEMENT_E = Block.box(8.5, -0.25, 2.75, 16, 16.25, 13.25);
    private static final VoxelShape PAVEMENT_W = Block.box(0, -0.25, 2.75, 7.5, 16.25, 13.25);

    // 单灯（横式/竖式）：x[2.75,13.25] y[2.75,13.25]（南向基准 z[8.5,16]）
    private static final VoxelShape SINGLE_N = Block.box(2.75, 2.75, 0, 13.25, 13.25, 7.5);
    private static final VoxelShape SINGLE_S = Block.box(2.75, 2.75, 8.5, 13.25, 13.25, 16);
    private static final VoxelShape SINGLE_E = Block.box(8.5, 2.75, 2.75, 16, 13.25, 13.25);
    private static final VoxelShape SINGLE_W = Block.box(0, 2.75, 2.75, 7.5, 13.25, 13.25);

    // 读秒器：x[0,16] y[2,16]（南向基准 z[7.75,16]）
    private static final VoxelShape COUNTDOWN_N = Block.box(0, 2, 0, 16, 16, 8.25);
    private static final VoxelShape COUNTDOWN_S = Block.box(0, 2, 7.75, 16, 16, 16);
    private static final VoxelShape COUNTDOWN_E = Block.box(7.75, 2, 0, 16, 16, 16);
    private static final VoxelShape COUNTDOWN_W = Block.box(0, 2, 0, 8.25, 16, 16);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<LightState> LIGHT_STATE = EnumProperty.create("light_state", LightState.class);
    public static final EnumProperty<MountType> TYPE = EnumProperty.create("type", MountType.class);

    public TrafficLightsBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIGHT_STATE, LightState.RED).setValue(TYPE, MountType.SIMPLE)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_lights.tooltip"));
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_lights.tooltip.auto_detect"));
        super .appendHoverText(stack, world, tooltip, options);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
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

        return switch (state.getValue(FACING)) {
            case WEST -> west;
            case SOUTH -> south;
            case EAST -> east;
            default -> north;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIGHT_STATE, TYPE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());

        // 雾灯方块默认为黄色
        if (this == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_FOGGY.get()) {
            state = state.setValue(LIGHT_STATE, LightState.YELLOW);
        }

        // 自动检测背后是否有路杆方块
        Direction facing = state.getValue(FACING);
        BlockPos behindPos = ctx.getClickedPos().relative(facing.getOpposite());
        BlockState behindState = ctx.getLevel().getBlockState(behindPos);

        MountType type = determineType(behindState);
        state = state.setValue(TYPE, type);

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
    public BlockState updateShape(BlockState state, Direction direction,
                                                BlockState neighborState, LevelAccessor world,
                                                BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        if (direction == facing.getOpposite()) {
            MountType newType = determineType(neighborState);
            if (newType != state.getValue(TYPE)) {
                return state.setValue(TYPE, newType);
            }
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new TrafficLightsBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        // 魔杖交互
        if (heldItem.getItem() == ModItems.WAND.get()) {
            // Shift + 右键切换 type 状态
            if (player.isShiftKeyDown()) {
                if (!world .isClientSide) {
                    MountType currentType = state.getValue(TYPE);
                    MountType newType = currentType == MountType.SIMPLE ? MountType.POLE : MountType.SIMPLE;
                    world.setBlock(pos, state.setValue(TYPE, newType), VersionServices.blocks().updateAll());
                    player.displayClientMessage(Text.literal("§a已切换至 " + (newType == MountType.POLE ? "§6路杆模式" : "§6简易模式")), true);
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }

            // 普通右键打开设置界面
            if (world .isClientSide) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
                    if (!trafficLightsBE.isInGroup()) {
                        // 未分组：打开静态状态设置界面
                        openStaticStateScreen(pos);
                        return InteractionResult.sidedSuccess(true);
                    }
                    if (!trafficLightsBE.hasTimings()) {
                        // 已分组但未设置时间表：打开时间设置界面
                        openTimingScreen(pos);
                        return InteractionResult.sidedSuccess(true);
                    }
                    // 打开GUI
                    openDisplayScreen(pos);
                }
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return super .use(state, world, pos, player, hand, hit);
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
                // 读秒器：无方向列表，仅选颜色 + 秒数输入 + 是否显示秒数开关
                Minecraft.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsCountdownTimerStaticStateScreen(pos));
            } else if (isShanghai) {
                // 上海红绿灯：有方向列表 + 秒数输入 + 是否显示秒数开关
                Minecraft.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsShanghaiStaticStateScreen(pos));
            } else if (isPavement) {
                // 人行道红绿灯：无方向列表，仅选颜色 + 秒数输入 + 是否显示秒数开关
                Minecraft.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsSimpleStaticStateScreen(pos));
            } else {
                // 普通红绿灯（含单灯横式/竖式）：有方向列表，无秒数相关控件
                Minecraft.getInstance().setScreen(new TrafficLightsStaticStateScreen(pos));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void openTimingScreen(BlockPos pos) {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof TrafficLightsBlockEntity tl) {
            Minecraft.getInstance().setScreen(new TrafficLightsTimingScreen(tl.getGroupId(), tl.getGroupPositions()));
        }
    }

    @Override
    protected void tickCompat(BlockState state, ServerLevel world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
            trafficLightsBE.tick();
            VersionServices.blocks().scheduleBlockTick(world, pos, this, 1);
        }
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world .isClientSide) {
            VersionServices.blocks().scheduleBlockTick(world, pos, this, 1);
        }
        super.onPlace(state, world, pos, oldState, notify);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
                if (trafficLightsBE.getGroupId() != null) {
                    trafficLightsBE.unloadGroup();
                }
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    public enum LightState implements StringRepresentable {
        RED("red"),
        YELLOW("yellow"),
        GREEN("green"),
        GRAY("gray");

        private final String name;

        LightState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public enum MountType implements StringRepresentable {
        SIMPLE("simple"),
        POLE("pole");

        private final String name;

        MountType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
