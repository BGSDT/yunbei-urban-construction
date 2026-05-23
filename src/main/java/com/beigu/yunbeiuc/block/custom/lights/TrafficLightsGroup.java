package com.beigu.yunbeiuc.block.custom.lights;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public class TrafficLightsGroup {
    private final UUID groupId;
    private final int mode; // 1=十字路口, 2=T字路口
    private final Map<Direction, Map<Boolean, BlockPos>> lights = new HashMap<>();
    private LightPhase currentPhase;
    private int phaseTime = 0;
    private boolean active = true;
    private transient World world;
    private final Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> cachedStates = new HashMap<>();

    // 闪烁相关
    private boolean isBlinking = false;
    private int blinkTick = 0;
    private static final int BLINK_INTERVAL = 10;
    private static final int BLINK_DURATION = 60;

    // 完整性检测相关
    private int integrityCheckTick = 0;
    private static final int INTEGRITY_CHECK_INTERVAL = 20; // 每秒检测一次
    private boolean markedForRemoval = false;

    // ==================== 十字路口相位枚举 ====================
    public enum CrossPhase {
        NS_LEFT_GREEN(480),
        NS_LEFT_BLINK(60),
        NS_LEFT_YELLOW(60),
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

        CrossPhase(int duration) {
            this.duration = duration;
        }

        public CrossPhase next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        public boolean isGreenPhase() {
            return this == NS_LEFT_GREEN || this == NS_STRAIGHT_GREEN ||
                    this == EW_LEFT_GREEN || this == EW_STRAIGHT_GREEN;
        }

        public boolean isBlinkPhase() {
            return this == NS_LEFT_BLINK || this == NS_STRAIGHT_BLINK ||
                    this == EW_LEFT_BLINK || this == EW_STRAIGHT_BLINK;
        }

        public boolean isYellowPhase() {
            return this == NS_LEFT_YELLOW || this == NS_STRAIGHT_YELLOW ||
                    this == EW_LEFT_YELLOW || this == EW_STRAIGHT_YELLOW;
        }
    }

    // ==================== T字路口相位枚举 ====================
    public enum TPhase {
        FIRST_GREEN(480),
        FIRST_BLINK(60),
        FIRST_YELLOW(60),
        SECOND_GREEN(480),
        SECOND_BLINK(60),
        SECOND_YELLOW(60),
        THIRD_GREEN(480),
        THIRD_BLINK(60),
        THIRD_YELLOW(60);

        public final int duration;

        TPhase(int duration) {
            this.duration = duration;
        }

        public TPhase next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        public boolean isGreenPhase() {
            return this == FIRST_GREEN || this == SECOND_GREEN || this == THIRD_GREEN;
        }

        public boolean isBlinkPhase() {
            return this == FIRST_BLINK || this == SECOND_BLINK || this == THIRD_BLINK;
        }

        public boolean isYellowPhase() {
            return this == FIRST_YELLOW || this == SECOND_YELLOW || this == THIRD_YELLOW;
        }
    }

    // ==================== 统一相位接口 ====================
    public enum LightPhase {
        // 十字路口 (0-11)
        NS_LEFT_GREEN(480, true, false, false),
        NS_LEFT_BLINK(60, false, true, false),
        NS_LEFT_YELLOW(60, false, false, true),
        NS_STRAIGHT_GREEN(480, true, false, false),
        NS_STRAIGHT_BLINK(60, false, true, false),
        NS_STRAIGHT_YELLOW(60, false, false, true),
        EW_LEFT_GREEN(480, true, false, false),
        EW_LEFT_BLINK(60, false, true, false),
        EW_LEFT_YELLOW(60, false, false, true),
        EW_STRAIGHT_GREEN(480, true, false, false),
        EW_STRAIGHT_BLINK(60, false, true, false),
        EW_STRAIGHT_YELLOW(60, false, false, true),

        // T字路口 (12-20)
        T_FIRST_GREEN(480, true, false, false),
        T_FIRST_BLINK(60, false, true, false),
        T_FIRST_YELLOW(60, false, false, true),
        T_SECOND_GREEN(480, true, false, false),
        T_SECOND_BLINK(60, false, true, false),
        T_SECOND_YELLOW(60, false, false, true),
        T_THIRD_GREEN(480, true, false, false),
        T_THIRD_BLINK(60, false, true, false),
        T_THIRD_YELLOW(60, false, false, true);

        public final int duration;
        public final boolean isGreen;
        public final boolean isBlink;
        public final boolean isYellow;

        private static final int T_START = 12; // T字路口相位起始索引

        LightPhase(int duration, boolean isGreen, boolean isBlink, boolean isYellow) {
            this.duration = duration;
            this.isGreen = isGreen;
            this.isBlink = isBlink;
            this.isYellow = isYellow;
        }

        public LightPhase nextCross() {
            // 安全取模，确保在十字路口范围内循环
            int index = this.ordinal() % 12;
            CrossPhase cp = CrossPhase.values()[index];
            return LightPhase.values()[cp.next().ordinal()];
        }

        public LightPhase nextT() {
            int offset = this.ordinal() - T_START;
            // 边界保护：如果偏移无效，重置为T_FIRST_GREEN
            if (offset < 0 || offset >= 9) {
                offset = 0;
            }
            TPhase tp = TPhase.values()[offset];
            TPhase next = tp.next();
            return LightPhase.values()[T_START + next.ordinal()];
        }
    }

    // ==================== 构造函数 ====================
    public TrafficLightsGroup(UUID groupId, int mode) {
        this.groupId = groupId;
        this.mode = mode;
        for (Direction dir : Direction.Type.HORIZONTAL) {
            lights.put(dir, new HashMap<>());
            Map<Boolean, TrafficLightsBlock.LightState> dirStates = new HashMap<>();
            dirStates.put(true, TrafficLightsBlock.LightState.RED);
            dirStates.put(false, TrafficLightsBlock.LightState.RED);
            cachedStates.put(dir, dirStates);
        }
        // 根据模式设置初始相位
        if (mode == 1) {
            this.currentPhase = LightPhase.NS_LEFT_GREEN;
        } else {
            this.currentPhase = LightPhase.T_FIRST_GREEN;
        }
    }

    // ==================== 公共方法 ====================
    public void setWorld(World world) {
        this.world = world;
    }

    public void tick(World world) {
        if (!active || world == null || world.isClient) return;

        // 完整性检测
        integrityCheckTick++;
        if (integrityCheckTick >= INTEGRITY_CHECK_INTERVAL) {
            integrityCheckTick = 0;
            if (!checkIntegrity(world)) {
                // 如果检测到损坏，标记为移除并停止所有操作
                markedForRemoval = true;
                active = false;
                cleanupAllLights(world);
                return;
            }
        }

        phaseTime++;

        if (currentPhase.isBlink) {
            blinkTick++;
            if (blinkTick >= BLINK_INTERVAL) {
                blinkTick = 0;
                isBlinking = !isBlinking;
                updateAllLights(world);
            }
        }

        if (phaseTime >= currentPhase.duration) {
            if (mode == 1) {
                currentPhase = currentPhase.nextCross();
            } else {
                currentPhase = currentPhase.nextT();
            }
            phaseTime = 0;
            blinkTick = 0;
            isBlinking = false;
            updateAllLights(world);
        }
    }

    /**
     * 检查组内所有红绿灯是否完好
     * @return true表示所有灯都在，false表示有灯被破坏
     */
    private boolean checkIntegrity(World world) {
        for (Map.Entry<Direction, Map<Boolean, BlockPos>> directionEntry : lights.entrySet()) {
            for (Map.Entry<Boolean, BlockPos> lightEntry : directionEntry.getValue().entrySet()) {
                BlockPos pos = lightEntry.getValue();
                if (pos != null) {
                    BlockState state = world.getBlockState(pos);
                    // 检查该位置的方块是否还是红绿灯方块
                    if (!(state.getBlock() instanceof TrafficLightsBlock)) {
                        // 检查方块是否被移除（空气或其他方块）
                        if (state.isAir() || !(state.getBlock() instanceof TrafficLightsBlock)) {
                            System.out.println("[TrafficLightsGroup] 检测到红绿灯被破坏! 组ID: " + groupId + " 位置: " + pos);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 清理所有红绿灯的状态
     */
    private void cleanupAllLights(World world) {
        System.out.println("[TrafficLightsGroup] 正在清理红绿灯组: " + groupId);

        // 将所有灯重置为关闭状态或默认状态
        for (Map.Entry<Direction, Map<Boolean, BlockPos>> directionEntry : lights.entrySet()) {
            for (Map.Entry<Boolean, BlockPos> lightEntry : directionEntry.getValue().entrySet()) {
                BlockPos pos = lightEntry.getValue();
                if (pos != null) {
                    BlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof TrafficLightsBlock) {
                        // 设置为默认状态（例如关闭或灰色）
                        world.setBlockState(pos, state.with(TrafficLightsBlock.LIGHT_STATE,
                                TrafficLightsBlock.LightState.GRAY));
                    }
                }
            }
        }

        // 清空内部数据结构
        clearAllData();
    }

    /**
     * 清空所有内部数据
     */
    private void clearAllData() {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            Map<Boolean, BlockPos> dirLights = lights.get(dir);
            if (dirLights != null) {
                dirLights.clear();
            }

            Map<Boolean, TrafficLightsBlock.LightState> dirStates = cachedStates.get(dir);
            if (dirStates != null) {
                dirStates.put(true, TrafficLightsBlock.LightState.GRAY);
                dirStates.put(false, TrafficLightsBlock.LightState.GRAY);
            }
        }
    }

    /**
     * 检查是否已标记为移除
     */
    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    /**
     * 强制检查完整性（可用于方块破坏事件）
     */
    public boolean forceIntegrityCheck(World world) {
        if (!checkIntegrity(world)) {
            markedForRemoval = true;
            active = false;
            cleanupAllLights(world);
            return false;
        }
        return true;
    }

    /**
     * 当单个红绿灯方块被破坏时调用
     */
    public void onLightBroken(World world, BlockPos brokenPos) {
        System.out.println("[TrafficLightsGroup] 红绿灯被破坏事件触发! 组ID: " + groupId + " 破坏位置: " + brokenPos);
        markedForRemoval = true;
        active = false;
        cleanupAllLights(world);
    }

    private void updateAllLights(World world) {
        // 所有灯默认红色
        for (Direction dir : Direction.Type.HORIZONTAL) {
            Map<Boolean, TrafficLightsBlock.LightState> dirStates = cachedStates.get(dir);
            dirStates.put(true, TrafficLightsBlock.LightState.RED);
            dirStates.put(false, TrafficLightsBlock.LightState.RED);
        }

        if (currentPhase.isGreen) {
            setGreenStates(cachedStates);
        } else if (currentPhase.isBlink) {
            setBlinkStates(cachedStates);
        } else if (currentPhase.isYellow) {
            setYellowStates(cachedStates);
        }

        applyAllStates(world, cachedStates);
    }

    // ==================== 十字路口状态设置 ====================
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
            // T字路口
            case T_FIRST_GREEN:
            case T_SECOND_GREEN:
            case T_THIRD_GREEN:
                setTGreenStates(states);
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
            case T_FIRST_BLINK:
            case T_SECOND_BLINK:
            case T_THIRD_BLINK:
                setTBlinkStates(states, blinkState);
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
            case T_FIRST_YELLOW:
            case T_SECOND_YELLOW:
            case T_THIRD_YELLOW:
                setTYellowStates(states);
                break;
            default:
                break;
        }
    }

    // ==================== T字路口状态设置 ====================
    private List<Direction> getTOrderedDirections() {
        // 根据实际存储的非空方向动态排序
        List<Direction> dirs = new ArrayList<>();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            if (lights.get(dir).get(false) != null) {
                dirs.add(dir);
            }
        }
        // 按固定顺序排列：NORTH, EAST, SOUTH, WEST
        dirs.sort(Comparator.comparingInt(d -> switch (d) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 4;
        }));
        return dirs;
    }

    private void setTGreenStates(Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> states) {
        List<Direction> dirs = getTOrderedDirections();
        int phaseIndex = currentPhase.ordinal() - LightPhase.T_START;
        int slotIndex = phaseIndex / 3; // 0, 1, 2 对应第一、二、三个方向

        if (slotIndex < dirs.size()) {
            Direction targetDir = dirs.get(slotIndex);
            states.get(targetDir).put(false, TrafficLightsBlock.LightState.GREEN);
        }
    }

    private void setTBlinkStates(Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> states, TrafficLightsBlock.LightState blinkState) {
        List<Direction> dirs = getTOrderedDirections();
        int phaseIndex = currentPhase.ordinal() - LightPhase.T_START;
        int slotIndex = phaseIndex / 3;

        if (slotIndex < dirs.size()) {
            Direction targetDir = dirs.get(slotIndex);
            states.get(targetDir).put(false, blinkState);
        }
    }

    private void setTYellowStates(Map<Direction, Map<Boolean, TrafficLightsBlock.LightState>> states) {
        List<Direction> dirs = getTOrderedDirections();
        int phaseIndex = currentPhase.ordinal() - LightPhase.T_START;
        int slotIndex = phaseIndex / 3;

        if (slotIndex < dirs.size()) {
            Direction targetDir = dirs.get(slotIndex);
            states.get(targetDir).put(false, TrafficLightsBlock.LightState.YELLOW);
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

    public void updateAllLightsImmediately(World world) {
        if (world == null) return;
        updateAllLights(world);
    }

    public void addLight(BlockPos pos, Direction direction, boolean isLeft) {
        lights.get(direction).put(isLeft, pos);
    }

    public boolean isValid() {
        if (mode == 1) {
            // 十字路口：8个灯，每个方向左转+直行
            for (Direction dir : Direction.Type.HORIZONTAL) {
                Map<Boolean, BlockPos> dirLights = lights.get(dir);
                if (dirLights.get(true) == null || dirLights.get(false) == null) {
                    return false;
                }
            }
            return true;
        } else {
            // T字路口：3个灯，3个方向各1个直行
            int count = 0;
            for (Direction dir : Direction.Type.HORIZONTAL) {
                if (lights.get(dir).get(false) != null) {
                    count++;
                }
            }
            return count == 3;
        }
    }

    public int getLightCount() {
        int count = 0;
        for (Direction dir : Direction.Type.HORIZONTAL) {
            for (BlockPos pos : lights.get(dir).values()) {
                if (pos != null) count++;
            }
        }
        return count;
    }

    public int getMode() {
        return mode;
    }

    public List<BlockPos> getAllLightPositions() {
        List<BlockPos> positions = new ArrayList<>();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            for (BlockPos pos : lights.get(dir).values()) {
                if (pos != null) positions.add(pos);
            }
        }
        return positions;
    }

    // ==================== NBT序列化 ====================
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("groupId", groupId);
        nbt.putInt("mode", mode);
        nbt.putInt("currentPhase", currentPhase.ordinal());
        nbt.putInt("phaseTime", phaseTime);
        nbt.putBoolean("active", active);

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

    public static TrafficLightsGroup fromNbt(NbtCompound nbt) {
        int mode = nbt.contains("mode") ? nbt.getInt("mode") : 1; // 默认为十字路口，兼容旧数据
        TrafficLightsGroup group = new TrafficLightsGroup(nbt.getUuid("groupId"), mode);

        int phaseOrdinal = nbt.getInt("currentPhase");
        if (mode == 1) {
            // 十字路口相位必须在0-11范围内
            if (phaseOrdinal < 0 || phaseOrdinal >= 12) {
                phaseOrdinal = 0; // 重置为NS_LEFT_GREEN
            }
        } else {
            // T字路口相位必须在12-20范围内
            if (phaseOrdinal < 12 || phaseOrdinal >= 21) {
                phaseOrdinal = 12; // 重置为T_FIRST_GREEN
            }
        }
        group.currentPhase = LightPhase.values()[phaseOrdinal];
        group.phaseTime = nbt.getInt("phaseTime");
        group.active = nbt.getBoolean("active");

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