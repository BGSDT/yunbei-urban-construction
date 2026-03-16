package com.beigu.yunbeiuc.block.custom.lights;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public class TrafficLightGroup {
    private final UUID groupId;
    private final Map<Direction, Map<Boolean, BlockPos>> lights = new HashMap<>();
    private LightPhase currentPhase = LightPhase.NS_LEFT_GREEN;
    private int phaseTime = 0;
    private boolean active = true;
    private transient World world; // transient 表示不保存到NBT

    public enum LightPhase {
        NS_LEFT_GREEN(540),    // 27秒 = 540 ticks
        NS_LEFT_YELLOW(60),    // 3秒 = 60 ticks
        NS_STRAIGHT_GREEN(540),
        NS_STRAIGHT_YELLOW(60),
        EW_LEFT_GREEN(540),
        EW_LEFT_YELLOW(60),
        EW_STRAIGHT_GREEN(540),
        EW_STRAIGHT_YELLOW(60);

        public final int duration;

        LightPhase(int duration) {
            this.duration = duration;
        }

        public LightPhase next() {
            return switch (this) {
                case NS_LEFT_GREEN -> NS_LEFT_YELLOW;
                case NS_LEFT_YELLOW -> NS_STRAIGHT_GREEN;
                case NS_STRAIGHT_GREEN -> NS_STRAIGHT_YELLOW;
                case NS_STRAIGHT_YELLOW -> EW_LEFT_GREEN;
                case EW_LEFT_GREEN -> EW_LEFT_YELLOW;
                case EW_LEFT_YELLOW -> EW_STRAIGHT_GREEN;
                case EW_STRAIGHT_GREEN -> EW_STRAIGHT_YELLOW;
                case EW_STRAIGHT_YELLOW -> NS_LEFT_GREEN;
            };
        }
    }

    public TrafficLightGroup(UUID groupId) {
        this.groupId = groupId;
        // 初始化每个方向的Map
        for (Direction dir : Direction.Type.HORIZONTAL) {
            lights.put(dir, new HashMap<>());
        }
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void tick(World world) {
        if (!active || world == null || world.isClient) return;

        phaseTime++;

        // 每 tick 检查是否需要切换状态
        if (phaseTime >= currentPhase.duration) {
            currentPhase = currentPhase.next();
            phaseTime = 0;
            updateAllLights(world);
        }
    }

    private void updateAllLights(World world) {
        // 根据当前阶段设置所有红绿灯的状态
        Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> states = new HashMap<>();

        // 初始化所有方向为红灯
        for (Direction dir : Direction.Type.HORIZONTAL) {
            Map<Boolean, TrafficLightsBlock.LightState> dirStates = new HashMap<>();
            dirStates.put(true, TrafficLightsBlock.LightState.RED);
            dirStates.put(false, TrafficLightsBlock.LightState.RED);
            states.put(dir, dirStates);
        }

        // 根据当前阶段设置特定方向的状态
        switch (currentPhase) {
            case NS_LEFT_GREEN:
                states.get(Direction.NORTH).put(true, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.SOUTH).put(true, TrafficLightsBlock.LightState.GREEN);
                break;
            case NS_LEFT_YELLOW:
                states.get(Direction.NORTH).put(true, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.SOUTH).put(true, TrafficLightsBlock.LightState.YELLOW);
                break;
            case NS_STRAIGHT_GREEN:
                states.get(Direction.NORTH).put(false, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.SOUTH).put(false, TrafficLightsBlock.LightState.GREEN);
                break;
            case NS_STRAIGHT_YELLOW:
                states.get(Direction.NORTH).put(false, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.SOUTH).put(false, TrafficLightsBlock.LightState.YELLOW);
                break;
            case EW_LEFT_GREEN:
                states.get(Direction.EAST).put(true, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.WEST).put(true, TrafficLightsBlock.LightState.GREEN);
                break;
            case EW_LEFT_YELLOW:
                states.get(Direction.EAST).put(true, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.WEST).put(true, TrafficLightsBlock.LightState.YELLOW);
                break;
            case EW_STRAIGHT_GREEN:
                states.get(Direction.EAST).put(false, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.WEST).put(false, TrafficLightsBlock.LightState.GREEN);
                break;
            case EW_STRAIGHT_YELLOW:
                states.get(Direction.EAST).put(false, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.WEST).put(false, TrafficLightsBlock.LightState.YELLOW);
                break;
        }

        // 应用状态到所有方块
        applyAllStates(world, states);
    }

    /**
     * 立即更新所有红绿灯的状态，根据当前阶段显示正确颜色
     */
    public void updateAllLightsImmediately(World world) {
        if (world == null) return;

        // 根据当前阶段设置所有红绿灯的状态
        Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> states = new HashMap<>();

        // 初始化所有方向为红灯
        for (Direction dir : Direction.Type.HORIZONTAL) {
            Map<Boolean, TrafficLightsBlock.LightState> dirStates = new HashMap<>();
            dirStates.put(true, TrafficLightsBlock.LightState.RED);
            dirStates.put(false, TrafficLightsBlock.LightState.RED);
            states.put(dir, dirStates);
        }

        // 根据当前阶段设置特定方向的状态
        switch (currentPhase) {
            case NS_LEFT_GREEN:
                states.get(Direction.NORTH).put(true, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.SOUTH).put(true, TrafficLightsBlock.LightState.GREEN);
                break;
            case NS_LEFT_YELLOW:
                states.get(Direction.NORTH).put(true, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.SOUTH).put(true, TrafficLightsBlock.LightState.YELLOW);
                break;
            case NS_STRAIGHT_GREEN:
                states.get(Direction.NORTH).put(false, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.SOUTH).put(false, TrafficLightsBlock.LightState.GREEN);
                break;
            case NS_STRAIGHT_YELLOW:
                states.get(Direction.NORTH).put(false, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.SOUTH).put(false, TrafficLightsBlock.LightState.YELLOW);
                break;
            case EW_LEFT_GREEN:
                states.get(Direction.EAST).put(true, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.WEST).put(true, TrafficLightsBlock.LightState.GREEN);
                break;
            case EW_LEFT_YELLOW:
                states.get(Direction.EAST).put(true, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.WEST).put(true, TrafficLightsBlock.LightState.YELLOW);
                break;
            case EW_STRAIGHT_GREEN:
                states.get(Direction.EAST).put(false, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.WEST).put(false, TrafficLightsBlock.LightState.GREEN);
                break;
            case EW_STRAIGHT_YELLOW:
                states.get(Direction.EAST).put(false, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.WEST).put(false, TrafficLightsBlock.LightState.YELLOW);
                break;
        }

        // 应用状态到所有方块
        applyAllStates(world, states);

        System.out.println("Group " + groupId.toString().substring(0, 8) + " initial state set to: " + currentPhase);
    }

    private void applyAllStates(World world, Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> states) {
        for (Map.Entry<Direction, Map<Boolean, BlockPos>> directionEntry : lights.entrySet()) {
            Direction direction = directionEntry.getKey();
            for (Map.Entry<Boolean, BlockPos> lightEntry : directionEntry.getValue().entrySet()) {
                boolean isLeft = lightEntry.getKey();
                BlockPos pos = lightEntry.getValue();

                if (pos != null && world.getBlockState(pos).getBlock() instanceof TrafficLightsBlock) {
                    TrafficLightsBlock.LightState newState = states.get(direction).get(isLeft);
                    world.setBlockState(pos, world.getBlockState(pos)
                            .with(TrafficLightsBlock.LIGHT_STATE, newState));
                }
            }
        }
    }

    public void addLight(BlockPos pos, Direction direction, boolean isLeft) {
        lights.get(direction).put(isLeft, pos);
    }

    public boolean isValid() {
        // 检查是否所有方向都有左转和直行灯
        for (Direction dir : Direction.Type.HORIZONTAL) {
            Map<Boolean, BlockPos> dirLights = lights.get(dir);
            if (dirLights.get(true) == null || dirLights.get(false) == null) {
                return false;
            }
        }
        return true;
    }

    public int getLightCount() {
        int count = 0;
        for (Direction dir : Direction.Type.HORIZONTAL) {
            count += lights.get(dir).size();
        }
        return count;
    }

    // NBT序列化方法
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("groupId", groupId);
        nbt.putInt("currentPhase", currentPhase.ordinal());
        nbt.putInt("phaseTime", phaseTime);
        nbt.putBoolean("active", active);

        // 保存所有位置
        for (Direction dir : Direction.Type.HORIZONTAL) {
            String dirName = dir.getName();
            Map<Boolean, BlockPos> dirLights = lights.get(dir);

            if (dirLights.get(true) != null) {
                nbt.put(dirName + "_left", NbtHelper.fromBlockPos(dirLights.get(true)));
            }
            if (dirLights.get(false) != null) {
                nbt.put(dirName + "_straight", NbtHelper.fromBlockPos(dirLights.get(false)));
            }
        }

        return nbt;
    }

    public static TrafficLightGroup fromNbt(NbtCompound nbt) {
        TrafficLightGroup group = new TrafficLightGroup(nbt.getUuid("groupId"));
        group.currentPhase = LightPhase.values()[nbt.getInt("currentPhase")];
        group.phaseTime = nbt.getInt("phaseTime");
        group.active = nbt.getBoolean("active");

        // 加载所有位置
        for (Direction dir : Direction.Type.HORIZONTAL) {
            String dirName = dir.getName();

            if (nbt.contains(dirName + "_left")) {
                BlockPos pos = NbtHelper.toBlockPos(nbt.getCompound(dirName + "_left"));
                group.addLight(pos, dir, true);
            }
            if (nbt.contains(dirName + "_straight")) {
                BlockPos pos = NbtHelper.toBlockPos(nbt.getCompound(dirName + "_straight"));
                group.addLight(pos, dir, false);
            }
        }

        return group;
    }

    public UUID getGroupId() {
        return groupId;
    }
}