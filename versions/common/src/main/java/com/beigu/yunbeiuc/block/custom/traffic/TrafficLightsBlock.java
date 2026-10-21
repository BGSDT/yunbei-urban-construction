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

// 红绿灯方块：MOUNT（安装方式，驱动模型与图案/读秒的 z 偏移）、LIGHT_STATE、TYPE 等状态都在这里。
// 对链接魔杖返回 PASS，让物品的 useOn 能接管（方块 use 先于物品 useOn）。
// 敲掉组内任意一盏灯都会解散整组（时间表与轨道绑定一起清空），所以必须给玩家明确提示。

public class TrafficLightsBlock extends TickingEntityBlock {

    private static final VoxelShape SHAPE_N = Block.box(0, 4, 0, 16, 12, 8);
    private static final VoxelShape SHAPE_E = Block.box(8, 4, 0, 16, 12, 16);
    private static final VoxelShape SHAPE_S = Block.box(0, 4, 8, 16, 12, 16);
    private static final VoxelShape SHAPE_W = Block.box(0, 4, 0, 8, 12, 16);

    private static final VoxelShape VERTICAL_N = Block.box(2.75, -4.75, 0, 13.25, 20.75, 7.5);
    private static final VoxelShape VERTICAL_S = Block.box(2.75, -4.75, 8.5, 13.25, 20.75, 16);
    private static final VoxelShape VERTICAL_E = Block.box(8.5, -4.75, 2.75, 16, 20.75, 13.25);
    private static final VoxelShape VERTICAL_W = Block.box(0, -4.75, 2.75, 7.5, 20.75, 13.25);

    private static final VoxelShape HORIZONTAL_N = Block.box(-4.75, 2.75, 0, 20.75, 13.25, 7.5);
    private static final VoxelShape HORIZONTAL_S = Block.box(-4.75, 2.75, 8.5, 20.75, 13.25, 16);
    private static final VoxelShape HORIZONTAL_E = Block.box(8.5, 2.75, -4.75, 16, 13.25, 20.75);
    private static final VoxelShape HORIZONTAL_W = Block.box(0, 2.75, -4.75, 7.5, 13.25, 20.75);

    private static final VoxelShape PAVEMENT_N = Block.box(2.75, -0.25, 0, 13.25, 16.25, 7.5);
    private static final VoxelShape PAVEMENT_S = Block.box(2.75, -0.25, 8.5, 13.25, 16.25, 16);
    private static final VoxelShape PAVEMENT_E = Block.box(8.5, -0.25, 2.75, 16, 16.25, 13.25);
    private static final VoxelShape PAVEMENT_W = Block.box(0, -0.25, 2.75, 7.5, 16.25, 13.25);

    private static final VoxelShape SINGLE_N = Block.box(2.75, 2.75, 0, 13.25, 13.25, 7.5);
    private static final VoxelShape SINGLE_S = Block.box(2.75, 2.75, 8.5, 13.25, 13.25, 16);
    private static final VoxelShape SINGLE_E = Block.box(8.5, 2.75, 2.75, 16, 13.25, 13.25);
    private static final VoxelShape SINGLE_W = Block.box(0, 2.75, 2.75, 7.5, 13.25, 13.25);

    private static final VoxelShape COUNTDOWN_N = Block.box(0, 2, 0, 16, 16, 8.25);
    private static final VoxelShape COUNTDOWN_S = Block.box(0, 2, 7.75, 16, 16, 16);
    private static final VoxelShape COUNTDOWN_E = Block.box(7.75, 2, 0, 16, 16, 16);
    private static final VoxelShape COUNTDOWN_W = Block.box(0, 2, 0, 8.25, 16, 16);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<LightState> LIGHT_STATE = EnumProperty.create("light_state", LightState.class);
    public static final EnumProperty<MountType> TYPE = EnumProperty.create("type", MountType.class);
    public static final EnumProperty<Mount> MOUNT = EnumProperty.create("mount", Mount.class);

    public TrafficLightsBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIGHT_STATE, LightState.RED)
                        .setValue(TYPE, MountType.AUTO).setValue(MOUNT, Mount.WALL)
        );
    }

    @Override
    public void appendHoverTextCompat(ItemStack stack, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_lights.tooltip"));
        tooltip.add(Text.translatable("block.yunbeiuc.traffic_lights.tooltip.auto_detect"));
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
        builder.add(FACING, LIGHT_STATE, TYPE, MOUNT);
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

        if (this == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_FOGGY.get()) {
            state = state.setValue(LIGHT_STATE, LightState.YELLOW);
        }

        Direction facing = state.getValue(FACING);
        BlockPos behindPos = ctx.getClickedPos().relative(facing.getOpposite());
        BlockState behindState = ctx.getLevel().getBlockState(behindPos);

        state = state.setValue(TYPE, MountType.AUTO).setValue(MOUNT, mountOf(behindState));

        return state;
    }

    private static boolean isPoleBlock(Block block) {
        return block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_FOUNDATIONS.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_FOUNDATIONS_SLAB.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LONGITUDINAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_HORIZONTAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_TSHAPE.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_TEXT_DISPLAY.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LED.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_FLAG.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LIGHT_FOUNDATIONS.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LIGHT_FOUNDATIONS_SLAB.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LIGHT_LONGITUDINAL.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LIGHT_BRANCH_1.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LIGHT_BRANCH_2.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_POLE_LIGHT_BRANCH_3.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_LIGHT_HIGH_MAST.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_LIGHT_1.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_LIGHT_2.get()
                || block == com.beigu.yunbeiuc.block.MunicipalBlocks.ROAD_LIGHTING_LAMP.get();
    }

    private static Mount mountOf(BlockState behindState) {
        return behindState != null && isPoleBlock(behindState.getBlock()) ? Mount.POLE : Mount.WALL;
    }

    public static Mount expectedMount(Level world, BlockPos pos, BlockState state) {
        MountType type = state.getValue(TYPE);
        if (type == MountType.SIMPLE) return Mount.WALL;
        if (type == MountType.POLE) return Mount.POLE;
        if (world == null) return state.getValue(MOUNT);
        return mountOf(world.getBlockState(pos.relative(state.getValue(FACING).getOpposite())));
    }

    public static BlockState applyMountType(Level world, BlockPos pos, BlockState state, MountType type) {
        BlockState updated = state.setValue(TYPE, type);
        return updated.setValue(MOUNT, expectedMount(world, pos, updated));
    }

    public static MountType resolveMountType(Level world, BlockPos pos, BlockState state) {
        return state.getValue(MOUNT) == Mount.POLE ? MountType.POLE : MountType.SIMPLE;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction,
                                                BlockState neighborState, LevelAccessor world,
                                                BlockPos pos, BlockPos neighborPos) {
        if (direction == state.getValue(FACING).getOpposite() && state.getValue(TYPE) == MountType.AUTO) {
            Mount mount = mountOf(neighborState);
            if (mount != state.getValue(MOUNT)) {
                return state.setValue(MOUNT, mount);
            }
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new TrafficLightsBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useCompat(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() == ModItems.WAND.get()) {
            if (world .isClientSide) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof TrafficLightsBlockEntity trafficLightsBE) {
                    if (!trafficLightsBE.isInGroup()) {
                        openStaticStateScreen(pos);
                        return InteractionResult.sidedSuccess(true);
                    }
                    if (!trafficLightsBE.hasTimings()) {
                        openTimingScreen(pos);
                        return InteractionResult.sidedSuccess(true);
                    }
                    openDisplayScreen(pos);
                }
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return super.useCompat(state, world, pos, player, hand, hit);
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
                Minecraft.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TrafficLightsSimpleStaticStateScreen(pos));
            } else {
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
        Mount expected = expectedMount(world, pos, state);
        if (state.getValue(MOUNT) != expected) {
            state = state.setValue(MOUNT, expected);
            world.setBlock(pos, state, VersionServices.blocks().updateAll());
        }

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
    public void playerWillDestroyCompat(Level world, BlockPos pos, BlockState state, Player player) {
        TrafficLightsGroupTracker.track(world, pos, player);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            unloadGroupAt(world, pos);
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    public static void unloadGroupAt(Level world, BlockPos pos) {
        if (world == null || world.isClientSide) return;
        TrafficLightsGroupTracker.Pending pending = TrafficLightsGroupTracker.take(pos);
        if (pending == null) return;
        TrafficLightsBlockEntity trafficLightsBE = pending.entity;
        if (trafficLightsBE.getGroupId() == null) return;

        final int members = trafficLightsBE.getGroupPositions() == null
                ? 0 : trafficLightsBE.getGroupPositions().size();
        trafficLightsBE.unloadGroup();

        if (pending.player != null) {
            pending.player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal(
                    "§7这盏红绿灯属于相位组，拆掉它会§c解散整组§7：其余 §6"
                            + Math.max(0, members - 1)
                            + " §7盏灯已退回未分组状态，时间表与轨道绑定一并清空。"
                            + "要恢复请用链接魔杖重新链接相位组。"), false);
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

    public enum Mount implements StringRepresentable {
        WALL("wall"),
        POLE("pole");

        private final String name;

        Mount(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public enum MountType implements StringRepresentable {
        AUTO("auto"),
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

        public String getDisplayName() {
            return switch (this) {
                case POLE -> "路杆模式";
                case AUTO -> "自适应";
                default -> "墙面模式";
            };
        }
    }
}
