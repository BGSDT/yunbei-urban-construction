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
    private transient World world;

    // 闪烁相关
    private boolean isBlinking = false;
    private int blinkTick = 0;
    private static final int BLINK_INTERVAL = 10; // 0.5秒 = 10 ticks (20 ticks/秒)
    private static final int BLINK_DURATION = 60; // 3秒 = 60 ticks

    public enum LightPhase {
        NS_LEFT_GREEN(480),      // 24秒 = 480 ticks
        NS_LEFT_BLINK(60),       // 3秒闪烁 = 60 ticks
        NS_LEFT_YELLOW(60),      // 3秒黄灯 = 60 ticks
        NS_STRAIGHT_GREEN(480),
        NS_STRAIGHT_BLINK(60),
        NS_STRAIGHT_YELLOW(60),
        EW_LEFT_GREEN(480),
        EW_LEFT_BLINK(60),
        EW_LEFT_YELLOW(60),
        EW_STRAIGHT_GREEN(480),
        EW_STRAIGHT_BLINK(60),
        EW_STRAIGHT_YELLOW(60);

        public final int duration;

        LightPhase(int duration) {
            this.duration = duration;
        }

        public LightPhase next() {
            return switch (this) {
                // 南北向左转
                case NS_LEFT_GREEN -> NS_LEFT_BLINK;
                case NS_LEFT_BLINK -> NS_LEFT_YELLOW;
                case NS_LEFT_YELLOW -> NS_STRAIGHT_GREEN;

                // 南北向直行
                case NS_STRAIGHT_GREEN -> NS_STRAIGHT_BLINK;
                case NS_STRAIGHT_BLINK -> NS_STRAIGHT_YELLOW;
                case NS_STRAIGHT_YELLOW -> EW_LEFT_GREEN;

                // 东西向左转
                case EW_LEFT_GREEN -> EW_LEFT_BLINK;
                case EW_LEFT_BLINK -> EW_LEFT_YELLOW;
                case EW_LEFT_YELLOW -> EW_STRAIGHT_GREEN;

                // 东西向直行
                case EW_STRAIGHT_GREEN -> EW_STRAIGHT_BLINK;
                case EW_STRAIGHT_BLINK -> EW_STRAIGHT_YELLOW;
                case EW_STRAIGHT_YELLOW -> NS_LEFT_GREEN;
            };
        }

        // 判断当前阶段是否是绿灯阶段
        public boolean isGreenPhase() {
            return this == NS_LEFT_GREEN || this == NS_STRAIGHT_GREEN ||
                    this == EW_LEFT_GREEN || this == EW_STRAIGHT_GREEN;
        }

        // 判断当前阶段是否是闪烁阶段
        public boolean isBlinkPhase() {
            return this == NS_LEFT_BLINK || this == NS_STRAIGHT_BLINK ||
                    this == EW_LEFT_BLINK || this == EW_STRAIGHT_BLINK;
        }

        // 判断当前阶段是否是黄灯阶段
        public boolean isYellowPhase() {
            return this == NS_LEFT_YELLOW || this == NS_STRAIGHT_YELLOW ||
                    this == EW_LEFT_YELLOW || this == EW_STRAIGHT_YELLOW;
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

        // 处理闪烁逻辑
        if (currentPhase.isBlinkPhase()) {
            blinkTick++;
            if (blinkTick >= BLINK_INTERVAL) {
                blinkTick = 0;
                isBlinking = !isBlinking;
                updateAllLights(world); // 更新闪烁状态
            }
        }

        // 检查是否需要切换到下一阶段
        if (phaseTime >= currentPhase.duration) {
            currentPhase = currentPhase.next();
            phaseTime = 0;
            blinkTick = 0;
            isBlinking = false; // 重置闪烁状态
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
        if (currentPhase.isGreenPhase()) {
            // 绿灯阶段
            setGreenStates(states);
        } else if (currentPhase.isBlinkPhase()) {
            // 闪烁阶段：根据isBlinking决定显示绿色还是灰色
            setBlinkStates(states);
        } else if (currentPhase.isYellowPhase()) {
            // 黄灯阶段
            setYellowStates(states);
        }

        // 应用状态到所有方块
        applyAllStates(world, states);
    }

    private void setGreenStates(Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> states) {
        switch (currentPhase) {
            case NS_LEFT_GREEN:
                states.get(Direction.NORTH).put(true, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.SOUTH).put(true, TrafficLightsBlock.LightState.GREEN);
                break;
            case NS_STRAIGHT_GREEN:
                states.get(Direction.NORTH).put(false, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.SOUTH).put(false, TrafficLightsBlock.LightState.GREEN);
                break;
            case EW_LEFT_GREEN:
                states.get(Direction.EAST).put(true, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.WEST).put(true, TrafficLightsBlock.LightState.GREEN);
                break;
            case EW_STRAIGHT_GREEN:
                states.get(Direction.EAST).put(false, TrafficLightsBlock.LightState.GREEN);
                states.get(Direction.WEST).put(false, TrafficLightsBlock.LightState.GREEN);
                break;
            default:
                break;
        }
    }

    private void setBlinkStates(Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> states) {
        TrafficLightsBlock.LightState blinkState = isBlinking ?
                TrafficLightsBlock.LightState.GREEN : TrafficLightsBlock.LightState.GRAY;

        switch (currentPhase) {
            case NS_LEFT_BLINK:
                states.get(Direction.NORTH).put(true, blinkState);
                states.get(Direction.SOUTH).put(true, blinkState);
                break;
            case NS_STRAIGHT_BLINK:
                states.get(Direction.NORTH).put(false, blinkState);
                states.get(Direction.SOUTH).put(false, blinkState);
                break;
            case EW_LEFT_BLINK:
                states.get(Direction.EAST).put(true, blinkState);
                states.get(Direction.WEST).put(true, blinkState);
                break;
            case EW_STRAIGHT_BLINK:
                states.get(Direction.EAST).put(false, blinkState);
                states.get(Direction.WEST).put(false, blinkState);
                break;
            default:
                break;
        }
    }

    private void setYellowStates(Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> states) {
        switch (currentPhase) {
            case NS_LEFT_YELLOW:
                states.get(Direction.NORTH).put(true, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.SOUTH).put(true, TrafficLightsBlock.LightState.YELLOW);
                break;
            case NS_STRAIGHT_YELLOW:
                states.get(Direction.NORTH).put(false, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.SOUTH).put(false, TrafficLightsBlock.LightState.YELLOW);
                break;
            case EW_LEFT_YELLOW:
                states.get(Direction.EAST).put(true, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.WEST).put(true, TrafficLightsBlock.LightState.YELLOW);
                break;
            case EW_STRAIGHT_YELLOW:
                states.get(Direction.EAST).put(false, TrafficLightsBlock.LightState.YELLOW);
                states.get(Direction.WEST).put(false, TrafficLightsBlock.LightState.YELLOW);
                break;
            default:
                break;
        }
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

    /**
     * 立即更新所有红绿灯的状态，根据当前阶段显示正确颜色
     */
    public void updateAllLightsImmediately(World world) {
        if (world == null) return;
        updateAllLights(world);
        System.out.println("Group " + groupId.toString().substring(0, 8) + " initial state set to: " + currentPhase);
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

    public List<BlockPos> getAllLightPositions() {
        List<BlockPos> positions = new ArrayList<>();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            positions.addAll(lights.get(dir).values());
        }
        return positions;
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