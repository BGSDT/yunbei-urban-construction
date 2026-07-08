package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.TrafficLightsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrafficLightsBlockEntity extends BlockEntity {
    private int phaseIndex = -1;
    private String groupId = null;
    private List<BlockPos> groupPositions = new ArrayList<>();

    private int[] phaseTimes = null;
    private int phaseCount = 0;

    private int currentTick = 0;
    private int currentActivePhase = 0;
    private boolean cycleActive = false;

    private static final int YELLOW_DURATION = 3 * 20;
    private static final int FLASH_DURATION = 3 * 20;
    private static final int FLASH_INTERVAL = 10;

    private DirectionType directionType = DirectionType.STRAIGHT;

    private int syncTimer = 0;

    // 标记红灯是否处于过渡阶段
    private boolean redTransition = false;

    public TrafficLightsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRAFFIC_LIGHTS_BLOCK_ENTITY.get(), pos, state);
    }

    // ==================== Tick 逻辑 ====================

    public void tick() {
        if (world == null || world.isClient() || !cycleActive || groupId == null || phaseTimes == null || phaseCount <= 0) {
            return;
        }

        currentTick++;

        int totalTicks = phaseTimes[currentActivePhase] * 20;

        if (currentTick >= totalTicks) {
            currentActivePhase = (currentActivePhase + 1) % phaseCount;
            currentTick = 0;
            updateGroupPhase();
            markDirtyAndUpdate();
        } else {
            syncTimer++;
            if (syncTimer >= 10) {
                syncTimer = 0;
                markDirtyAndUpdate();
            }
        }

        updateLightState();
    }

    private void updateLightState() {
        if (world == null || world.isClient() || phaseTimes == null || phaseCount <= 0) return;

        BlockState currentState = getCachedState();
        if (!currentState.contains(TrafficLightsBlock.LIGHT_STATE)) return;

        TrafficLightsBlock.LightState lightState;

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int yellowStartTick = totalTicks - YELLOW_DURATION;
        int flashStartTick = yellowStartTick - FLASH_DURATION;

        if (phaseIndex == currentActivePhase) {
            if (currentTick < flashStartTick) {
                lightState = TrafficLightsBlock.LightState.GREEN;
            } else if (currentTick < yellowStartTick) {
                int flashTick = currentTick - flashStartTick;
                int flashPhase = flashTick / FLASH_INTERVAL;
                lightState = (flashPhase % 2 == 0) ? TrafficLightsBlock.LightState.GRAY : TrafficLightsBlock.LightState.GREEN;
            } else {
                lightState = TrafficLightsBlock.LightState.YELLOW;
            }
        } else {
            lightState = TrafficLightsBlock.LightState.RED;
        }

        if (currentState.get(TrafficLightsBlock.LIGHT_STATE) != lightState) {
            world.setBlockState(pos, currentState.with(TrafficLightsBlock.LIGHT_STATE, lightState), Block.NOTIFY_ALL);
        }
    }

    private void updateGroupPhase() {
        if (groupId == null || groupPositions.isEmpty() || world == null || world.isClient()) return;

        for (BlockPos pos : groupPositions) {
            if (pos.equals(this.pos)) continue;
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof TrafficLightsBlockEntity tl) {
                tl.currentActivePhase = this.currentActivePhase;
                tl.currentTick = this.currentTick;
                tl.updateLightState();
                tl.markDirtyAndUpdate();
            } else {
                unloadGroup();
                return;
            }
        }
    }

    // ==================== 时间查询接口（秒数） ====================

    /**
     * 获取绿灯+闪烁的总剩余秒数
     * 到0时返回黄灯总秒数(3)
     */
    public int getGreenRemainingSeconds() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;
        if (phaseIndex != currentActivePhase) return -1;

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int yellowStartTick = totalTicks - YELLOW_DURATION;

        if (currentTick < yellowStartTick) {
            int remaining = (yellowStartTick - currentTick) / 20;
            return remaining > 0 ? remaining : YELLOW_DURATION / 20;
        }
        return -1;
    }

    /**
     * 获取黄灯剩余秒数
     * 到0时返回下一个相位的总时间
     */
    public int getYellowRemainingSeconds() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;
        if (phaseIndex != currentActivePhase) return -1;

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int yellowStartTick = totalTicks - YELLOW_DURATION;

        if (currentTick >= yellowStartTick) {
            int remaining = (totalTicks - currentTick) / 20;
            if (remaining > 0) return remaining;
            int nextPhase = (currentActivePhase + 1) % phaseCount;
            return phaseTimes[nextPhase];
        }
        return -1;
    }

    /**
     * 获取红灯剩余秒数
     * 到0时返回自己相位的绿灯时间（设置时间 - 3）
     */
    public int getRedRemainingSeconds() {
        redTransition = false;
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;
        if (phaseIndex == currentActivePhase) return -1;

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int remaining = totalTicks - currentTick;
        for (int i = 1; i < phaseCount; i++) {
            int nextPhase = (currentActivePhase + i) % phaseCount;
            if (nextPhase == phaseIndex) break;
            remaining += phaseTimes[nextPhase] * 20;
        }

        int sec = remaining / 20;
        if (sec > 0) return sec;
        redTransition = true;
        return phaseTimes[phaseIndex] - 3;
    }

    public boolean isRedTransition() {
        return redTransition;
    }

    /**
     * 获取完整的灯状态信息
     */
    public LightTimingInfo getLightTimingInfo() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) {
            return new LightTimingInfo("无", -1, -1, -1, -1, false, "white");
        }

        int greenSec = getGreenRemainingSeconds();
        int yellowSec = getYellowRemainingSeconds();
        int redSec = getRedRemainingSeconds();

        String activeColor;
        int activeRemaining;
        boolean isTransition = false;
        String transitionColor = "white";

        if (greenSec >= 0) {
            activeColor = "绿灯";
            activeRemaining = greenSec;
            // 只有绿灯的最后一个数字(显示3)才变色
            // getGreenRemainingSeconds() 在 remaining<=0 时返回 3
            // 此时 yellowStartTick - currentTick <= 0，即绿灯阶段已结束
            int totalTicks = phaseTimes[currentActivePhase] * 20;
            int yellowStartTick = totalTicks - YELLOW_DURATION;
            if (currentTick >= yellowStartTick - 20 && currentTick < yellowStartTick) {
                isTransition = true;
                transitionColor = "yellow";
            }
        } else if (yellowSec >= 0) {
            activeColor = "黄灯";
            activeRemaining = yellowSec;
            // 只有黄灯的最后一个数字(显示下一个相位总时间)才变色
            int totalTicks = phaseTimes[currentActivePhase] * 20;
            if (currentTick >= totalTicks - 20) {
                isTransition = true;
                transitionColor = "red";
            }
        } else if (redSec >= 0) {
            activeColor = "红灯";
            activeRemaining = redSec;
            // 只有红灯的最后一个数字(显示自己绿灯时间)才变色
            if (isRedTransition()) {
                isTransition = true;
                transitionColor = "green";
            }
        } else {
            activeColor = "无";
            activeRemaining = -1;
        }

        return new LightTimingInfo(activeColor, activeRemaining, redSec, yellowSec, greenSec, isTransition, transitionColor);
    }

    // ==================== 相位控制 ====================

    public boolean setPhaseIndex(int index, PlayerEntity player) {
        if (phaseTimes == null || phaseCount <= 0) {
            if (player != null && !world.isClient()) {
                player.sendMessage(Text.literal("§c请先使用命令设置时间！"), true);
            }
            return false;
        }
        if (groupId == null) {
            if (player != null && !world.isClient()) {
                player.sendMessage(Text.literal("§c此红绿灯未链接到任何组！"), true);
            }
            return false;
        }
        if (index < 0 || index >= phaseCount) {
            if (player != null && !world.isClient()) {
                player.sendMessage(Text.literal("§c无效的相位索引！范围：1-" + phaseCount), true);
            }
            return false;
        }
        this.phaseIndex = index;
        if (player != null && !world.isClient()) {
            player.sendMessage(Text.literal("§a相位已设置为 §6" + (index + 1) + " §7(共" + phaseCount + "个相位)"), true);
        }
        markDirtyAndUpdate();
        return true;
    }

    public void startCycle() {
        if (phaseTimes == null || phaseCount <= 0) return;
        this.cycleActive = true;
        this.currentTick = 0;
        this.currentActivePhase = 0;
        updateLightState();
        markDirtyAndUpdate();
    }

    public void stopCycle() {
        this.cycleActive = false;
        markDirtyAndUpdate();
    }

    public void unloadGroup() {
        if (groupId == null) return;

        stopCycle();

        if (world != null && !world.isClient() && groupPositions != null) {
            for (BlockPos pos : groupPositions) {
                if (pos.equals(this.pos)) continue;
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof TrafficLightsBlockEntity tl && tl != this) {
                    tl.groupId = null;
                    tl.groupPositions.clear();
                    tl.phaseTimes = null;
                    tl.phaseCount = 0;
                    tl.phaseIndex = -1;
                    tl.directionType = DirectionType.STRAIGHT;
                    tl.stopCycle();
                    tl.markDirtyAndUpdate();
                }
            }
        }

        groupId = null;
        groupPositions.clear();
        phaseTimes = null;
        phaseCount = 0;
        phaseIndex = -1;
        directionType = DirectionType.STRAIGHT;
        markDirtyAndUpdate();
    }

    // ==================== 设置器 ====================

    public void setTimings(int phaseCount, int[] timings) {
        this.phaseCount = phaseCount;
        this.phaseTimes = timings;
        this.phaseIndex = -1;
        startCycle();
        markDirtyAndUpdate();
    }

    public void setGroup(String groupId, List<BlockPos> positions) {
        this.groupId = groupId;
        this.groupPositions = new ArrayList<>(positions);
        markDirtyAndUpdate();
    }

    // ==================== 获取器 ====================

    public boolean hasTimings() {
        return phaseTimes != null && phaseCount > 0;
    }

    public boolean isInGroup() {
        return groupId != null;
    }

    public int getPhaseCount() {
        return phaseCount;
    }

    public int getPhaseIndex() {
        return phaseIndex;
    }

    public int[] getPhaseTimes() {
        return phaseTimes;
    }

    public String getGroupId() {
        return groupId;
    }

    public DirectionType getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DirectionType directionType) {
        this.directionType = directionType;
        markDirtyAndUpdate();
    }

    // ==================== NBT 读写 ====================

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.phaseIndex = nbt.getInt("phaseIndex");
        this.groupId = nbt.contains("groupId") ? nbt.getString("groupId") : null;
        this.phaseCount = nbt.getInt("phaseCount");
        this.directionType = DirectionType.fromName(nbt.getString("directionType"));

        if (nbt.contains("phaseTimes")) {
            this.phaseTimes = nbt.getIntArray("phaseTimes");
            if (this.phaseTimes.length == 0) {
                this.phaseTimes = null;
            }
        }

        this.currentActivePhase = nbt.getInt("currentActivePhase");
        this.currentTick = nbt.getInt("currentTick");
        this.cycleActive = nbt.getBoolean("cycleActive");

        if (nbt.contains("groupPositions")) {
            NbtCompound positionsTag = nbt.getCompound("groupPositions");
            int size = positionsTag.getInt("size");
            groupPositions.clear();
            for (int i = 0; i < size; i++) {
                BlockPos pos = NbtHelper.toBlockPos(positionsTag.getCompound("pos" + i));
                groupPositions.add(pos);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("phaseIndex", this.phaseIndex);
        nbt.putString("directionType", this.directionType.getName());

        if (groupId != null) {
            nbt.putString("groupId", groupId);
        }

        nbt.putInt("phaseCount", phaseCount);

        if (phaseTimes != null) {
            nbt.putIntArray("phaseTimes", phaseTimes);
        }

        nbt.putInt("currentActivePhase", currentActivePhase);
        nbt.putInt("currentTick", currentTick);
        nbt.putBoolean("cycleActive", cycleActive);

        if (groupPositions != null && !groupPositions.isEmpty()) {
            NbtCompound positionsTag = new NbtCompound();
            positionsTag.putInt("size", groupPositions.size());
            for (int i = 0; i < groupPositions.size(); i++) {
                positionsTag.put("pos" + i, NbtHelper.fromBlockPos(groupPositions.get(i)));
            }
            nbt.put("groupPositions", positionsTag);
        }

        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void markDirtyAndUpdate() {
        markDirty();
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    // ==================== 枚举类 ====================

    public enum DirectionType {
        STRAIGHT("straight"),
        LEFT_TURN("left_turn"),
        RIGHT_TURN("right_turn"),
        TURN_AROUND("turn_around"),
        NON_MOTOR_VEHICLES("non_motor_vehicles");

        private final String name;

        DirectionType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static DirectionType fromName(String name) {
            for (DirectionType type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return STRAIGHT;
        }
    }

    // ==================== 数据类 ====================

    public static class LightTimingInfo {
        private final String activeColor;
        private final int activeRemaining;
        private final int redRemaining;
        private final int yellowRemaining;
        private final int greenRemaining;
        private final boolean isTransition;
        private final String transitionColor;

        public LightTimingInfo(String activeColor, int activeRemaining,
                               int redRemaining, int yellowRemaining, int greenRemaining,
                               boolean isTransition, String transitionColor) {
            this.activeColor = activeColor;
            this.activeRemaining = activeRemaining;
            this.redRemaining = redRemaining;
            this.yellowRemaining = yellowRemaining;
            this.greenRemaining = greenRemaining;
            this.isTransition = isTransition;
            this.transitionColor = transitionColor;
        }

        public String getActiveColor() { return activeColor; }
        public int getActiveRemaining() { return activeRemaining; }
        public int getRedRemaining() { return redRemaining; }
        public int getYellowRemaining() { return yellowRemaining; }
        public int getGreenRemaining() { return greenRemaining; }
        public boolean isTransition() { return isTransition; }
        public String getTransitionColor() { return transitionColor; }
    }
}