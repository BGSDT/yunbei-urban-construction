package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.BlockEntityMapper;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrafficLightsBlockEntity extends BlockEntityMapper {
    private static final int MAX_PHASE_INDICES = 4;

    private List<Integer> phaseIndices = new ArrayList<>();
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

    private DirectionType directionType = DirectionType.STRAIGHT_CIRCLE;

    // 读秒器显示模式：0=全显，1=半显
    private int countdownDisplayMode = 0;
    // 半显模式阈值（秒）
    private int countdownThreshold = 15;

    // 人行道红绿灯：是否在logo镜像位置显示读秒数字
    private boolean showSeconds = false;
    // 静态状态下的固定秒数（用于人行道红绿灯静态显示）
    private int fixedSeconds = 10;

    private int syncTimer = 0;

    public TrafficLightsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRAFFIC_LIGHTS_BLOCK_ENTITY.get(), pos, state);

        // 雾灯方块默认设置为色闪黄色（COLOR_FLASH）
        Block block = state.getBlock();
        if (block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_FOGGY.get()) {
            this.directionType = DirectionType.COLOR_FLASH;
        }
    }

    // ==================== Tick 逻辑 ====================

    public void tick() {
        if (level == null || level .isClientSide || !cycleActive || groupId == null || phaseTimes == null || phaseCount <= 0) {
            return;
        }

        normalizePhaseData();

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

    /**
     * 校正相位数据的一致性，防止 phaseTimes 数组长度与 phaseCount 不一致时发生数组越界崩溃。
     * 以 phaseTimes.length 为唯一依据；并钳制 currentActivePhase / phaseIndices 到合法范围。
     */
    private void normalizePhaseData() {
        if (phaseTimes != null && phaseTimes.length > 0) {
            phaseCount = phaseTimes.length;
        } else {
            phaseTimes = null;
            phaseCount = 0;
            phaseIndices.clear();
            currentActivePhase = 0;
            cycleActive = false;
            return;
        }
        if (currentActivePhase < 0 || currentActivePhase >= phaseCount) {
            currentActivePhase = 0;
        }

        Set<Integer> deduped = new LinkedHashSet<>();
        for (Integer idx : phaseIndices) {
            if (idx != null && idx >= 0 && idx < phaseCount) {
                deduped.add(idx);
            }
        }
        List<Integer> normalized = new ArrayList<>(deduped);
        int maxAllowed = Math.min(MAX_PHASE_INDICES, phaseCount);
        if (normalized.size() > maxAllowed) {
            normalized = normalized.subList(0, maxAllowed);
        }
        phaseIndices = new ArrayList<>(normalized);
    }

    private void updateLightState() {
        if (level == null || level .isClientSide || phaseTimes == null || phaseCount <= 0) return;

        normalizePhaseData();

        BlockState currentState = getBlockState();
        if (!currentState.hasProperty(TrafficLightsBlock.LIGHT_STATE)) return;

        Block currentBlock = currentState.getBlock();
        boolean isPavementLight = currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLACK.get();

        TrafficLightsBlock.LightState lightState;

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int yellowStartTick = totalTicks - YELLOW_DURATION;
        int flashStartTick = yellowStartTick - FLASH_DURATION;

        boolean continuesGreen = phaseIndices.contains(currentActivePhase)
                && phaseIndices.contains((currentActivePhase + 1) % phaseCount);

        if (phaseIndices.contains(currentActivePhase)) {
            if (continuesGreen) {
                // 下一相位对本灯同样为绿灯，视为连续相位，不出现黄灯/闪烁，直接保持绿灯
                lightState = TrafficLightsBlock.LightState.GREEN;
            } else if (isPavementLight) {
                // 人行道红绿灯：黄灯时间并入绿色，最后3秒闪烁（原黄灯位置闪烁）
                if (currentTick < yellowStartTick) {
                    lightState = TrafficLightsBlock.LightState.GREEN;
                } else {
                    // 黄灯时间改为绿灯闪烁
                    int flashTick = currentTick - yellowStartTick;
                    int flashPhase = flashTick / FLASH_INTERVAL;
                    lightState = (flashPhase % 2 == 0) ? TrafficLightsBlock.LightState.GRAY : TrafficLightsBlock.LightState.GREEN;
                }
            } else {
                // 普通红绿灯：闪烁3秒 + 黄灯3秒
                if (currentTick < flashStartTick) {
                    lightState = TrafficLightsBlock.LightState.GREEN;
                } else if (currentTick < yellowStartTick) {
                    int flashTick = currentTick - flashStartTick;
                    int flashPhase = flashTick / FLASH_INTERVAL;
                    lightState = (flashPhase % 2 == 0) ? TrafficLightsBlock.LightState.GRAY : TrafficLightsBlock.LightState.GREEN;
                } else {
                    lightState = TrafficLightsBlock.LightState.YELLOW;
                }
            }
        } else {
            lightState = TrafficLightsBlock.LightState.RED;
        }

        if (currentState.getValue(TrafficLightsBlock.LIGHT_STATE) != lightState) {
            level.setBlock(worldPosition, currentState.setValue(TrafficLightsBlock.LIGHT_STATE, lightState), net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
    }

    private void updateGroupPhase() {
        if (groupId == null || groupPositions.isEmpty() || level == null || level .isClientSide) return;

        for (BlockPos pos : groupPositions) {
            if (pos.equals(this.worldPosition)) continue;
            BlockEntity be = level.getBlockEntity(pos);
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
     * 获取绿灯+闪烁的总剩余秒数。
     * 向上取整计算，始终显示 1 到绿灯总秒数的完整序列，不会出现 0。
     */
    public int getGreenRemainingSeconds() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;
        if (!phaseIndices.contains(currentActivePhase)) return -1;

        normalizePhaseData();

        Block currentBlock = getBlockState().getBlock();
        boolean isPavementLight = currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLACK.get();

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int yellowStartTick = totalTicks - YELLOW_DURATION;

        // 人行道灯：黄灯时间段视为绿灯闪烁，继续返回剩余秒数
        // 普通灯：黄灯时间段返回-1（由getYellowRemainingSeconds处理）
        if (currentTick >= yellowStartTick && !isPavementLight && !phaseIndices.contains((currentActivePhase + 1) % phaseCount)) {
            return -1;
        }

        // 连续绿灯相位（本相位与下一相位对本灯均为绿灯）时，中间不出现黄灯，
        // 剩余秒数需要跨相位叠加，包含中间本应出现的黄灯时长
        int phase = currentActivePhase;
        long remainingTicks = totalTicks - currentTick;
        for (int i = 1; i < phaseCount; i++) {
            int nextPhase = (phase + i) % phaseCount;
            if (!phaseIndices.contains(nextPhase)) break;
            remainingTicks += phaseTimes[nextPhase] * 20L;
        }

        // 人行道灯：黄灯时间并入绿灯显示，不减去黄灯时长
        // 普通灯：减去黄灯时长（因为黄灯单独显示）
        if (!isPavementLight) {
            remainingTicks -= YELLOW_DURATION;
        }
        if (remainingTicks < 0) remainingTicks = 0;

        return (int) ((remainingTicks + 19) / 20);
    }

    /**
     * 获取黄灯剩余秒数。
     * 向上取整计算，始终显示 1、2、3 的完整序列，不会出现 0。
     */
    public int getYellowRemainingSeconds() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;
        if (!phaseIndices.contains(currentActivePhase)) return -1;

        normalizePhaseData();

        // 与下一相位相接为连续绿灯时，中间不出现黄灯
        if (phaseIndices.contains((currentActivePhase + 1) % phaseCount)) return -1;

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int yellowStartTick = totalTicks - YELLOW_DURATION;

        if (currentTick >= yellowStartTick) {
            int remainingTicks = totalTicks - currentTick;
            return (remainingTicks + 19) / 20;
        }
        return -1;
    }

    /**
     * 获取红灯剩余秒数。
     * 向上取整计算，始终显示 1 到红灯总秒数的完整序列，不会出现 0。
     */
    public int getRedRemainingSeconds() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;
        if (phaseIndices.contains(currentActivePhase)) return -1;

        normalizePhaseData();

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int remainingTicks = totalTicks - currentTick;
        for (int i = 1; i < phaseCount; i++) {
            int nextPhase = (currentActivePhase + i) % phaseCount;
            if (phaseIndices.contains(nextPhase)) break;
            remainingTicks += phaseTimes[nextPhase] * 20;
        }

        return (remainingTicks + 19) / 20;
    }

    /**
     * 获取完整的灯状态信息。
     * 活动阶段与颜色完全由方块状态中的 LIGHT_STATE 决定，
     * 与灯模型（renderLogo）共用同一个权威状态，保证变色与灯状态同 tick 同步。
     */
    public LightTimingInfo getLightTimingInfo() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) {
            return new LightTimingInfo("无", -1, -1, -1, -1);
        }

        BlockState currentState = getBlockState();
        if (!currentState.hasProperty(TrafficLightsBlock.LIGHT_STATE)) {
            return new LightTimingInfo("无", -1, -1, -1, -1);
        }

        TrafficLightsBlock.LightState lightState = currentState.getValue(TrafficLightsBlock.LIGHT_STATE);

        String activeColor;
        int activeRemaining;
        int redSec = -1;
        int yellowSec = -1;
        int greenSec = -1;

        switch (lightState) {
            case RED -> {
                redSec = getRedRemainingSeconds();
                activeColor = "红灯";
                activeRemaining = redSec;
            }
            case YELLOW -> {
                yellowSec = getYellowRemainingSeconds();
                activeColor = "黄灯";
                activeRemaining = yellowSec;
            }
            // GREEN 与 GRAY（闪烁）阶段统一按绿灯剩余处理
            default -> {
                greenSec = getGreenRemainingSeconds();
                activeColor = "绿灯";
                activeRemaining = greenSec;
            }
        }

        return new LightTimingInfo(activeColor, activeRemaining, redSec, yellowSec, greenSec);
    }

    // ==================== 相位控制 ====================

    public boolean setPhaseIndices(List<Integer> indices, Player player) {
        if (phaseTimes == null || phaseCount <= 0) {
            if (player != null && !level .isClientSide) {
                player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c请先使用命令设置时间！"), true);
            }
            return false;
        }
        if (groupId == null) {
            if (player != null && !level .isClientSide) {
                player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c此红绿灯未链接到任何组！"), true);
            }
            return false;
        }
        int maxAllowed = Math.min(MAX_PHASE_INDICES, phaseCount);
        if (indices.size() > maxAllowed) {
            if (player != null && !level .isClientSide) {
                player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c最多只能设置 " + maxAllowed + " 个相位！"), true);
            }
            return false;
        }
        if (new HashSet<>(indices).size() != indices.size()) {
            if (player != null && !level .isClientSide) {
                player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c相位不可以重复！"), true);
            }
            return false;
        }
        for (int index : indices) {
            if (index < 0 || index >= phaseCount) {
                if (player != null && !level .isClientSide) {
                    player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c无效的相位索引！范围：1-" + phaseCount), true);
                }
                return false;
            }
        }
        this.phaseIndices = new ArrayList<>(indices);
        if (player != null && !level .isClientSide) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indices.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(indices.get(i) + 1);
            }
            player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§a相位已设置为 §6" + sb + " §7(共" + phaseCount + "个相位)"), true);
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

        if (level != null && !level .isClientSide && groupPositions != null) {
            for (BlockPos pos : groupPositions) {
                if (pos.equals(this.worldPosition)) continue;
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof TrafficLightsBlockEntity tl && tl != this) {
                    tl.groupId = null;
                    tl.groupPositions.clear();
                    tl.phaseTimes = null;
                    tl.phaseCount = 0;
                    tl.phaseIndices.clear();
                    tl.directionType = DirectionType.STRAIGHT_CIRCLE;
                    tl.stopCycle();
                    tl.markDirtyAndUpdate();
                }
            }
        }

        groupId = null;
        groupPositions.clear();
        phaseTimes = null;
        phaseCount = 0;
        phaseIndices.clear();
        directionType = DirectionType.STRAIGHT_CIRCLE;
        markDirtyAndUpdate();
    }

    // ==================== 设置器 ====================

    public void setTimings(int phaseCount, int[] timings) {
        // phaseCount 以 timings.length 为准，防止两者不一致导致后续数组越界
        this.phaseTimes = timings;
        this.phaseCount = timings != null ? timings.length : 0;
        this.phaseIndices.clear();
        startCycle();
        markDirtyAndUpdate();
    }

    public void setGroup(String groupId, List<BlockPos> positions) {
        this.groupId = groupId;
        this.groupPositions = new ArrayList<>(positions);
        this.directionType = DirectionType.STRAIGHT_CIRCLE;
        if (level != null && !level .isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(TrafficLightsBlock.LIGHT_STATE)) {
                level.setBlock(worldPosition, state.setValue(TrafficLightsBlock.LIGHT_STATE, TrafficLightsBlock.LightState.RED), net.minecraft.world.level.block.Block.UPDATE_ALL);
            }
        }
        markDirtyAndUpdate();
    }

    /**
     * 分组前的静态状态：手动设置图案+颜色并持续保持，直到该红绿灯被加入相位组。
     */
    public boolean setStaticState(DirectionType direction, TrafficLightsBlock.LightState color,
                                   boolean showSeconds, int fixedSeconds, Player player) {
        if (isInGroup()) {
            if (player != null && !level .isClientSide) {
                player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c该红绿灯已加入相位组，无法单独设置静态状态！"), true);
            }
            return false;
        }
        this.directionType = direction;
        this.showSeconds = showSeconds;
        this.fixedSeconds = fixedSeconds;
        if (level != null && !level .isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(TrafficLightsBlock.LIGHT_STATE)) {
                level.setBlock(worldPosition, state.setValue(TrafficLightsBlock.LIGHT_STATE, color), net.minecraft.world.level.block.Block.UPDATE_ALL);
            }
        }
        markDirtyAndUpdate();
        return true;
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

    public List<Integer> getPhaseIndices() {
        return Collections.unmodifiableList(phaseIndices);
    }

    public int[] getPhaseTimes() {
        return phaseTimes;
    }

    public String getGroupId() {
        return groupId;
    }

    public List<BlockPos> getGroupPositions() {
        return Collections.unmodifiableList(groupPositions);
    }

    public DirectionType getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DirectionType directionType) {
        this.directionType = directionType;
        markDirtyAndUpdate();
    }

    public int getCountdownDisplayMode() {
        return countdownDisplayMode;
    }

    public void setCountdownDisplayMode(int mode) {
        this.countdownDisplayMode = mode;
        markDirtyAndUpdate();
    }

    public int getCountdownThreshold() {
        return countdownThreshold;
    }

    public void setCountdownThreshold(int threshold) {
        this.countdownThreshold = threshold;
        markDirtyAndUpdate();
    }

    public boolean isShowSeconds() {
        return showSeconds;
    }

    public void setShowSeconds(boolean showSeconds) {
        this.showSeconds = showSeconds;
        markDirtyAndUpdate();
    }

    public int getFixedSeconds() {
        return fixedSeconds;
    }

    public void setFixedSeconds(int fixedSeconds) {
        this.fixedSeconds = fixedSeconds;
        markDirtyAndUpdate();
    }

    // ==================== NBT 读写 ====================

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("phaseIndices")) {
            int[] arr = nbt.getIntArray("phaseIndices");
            this.phaseIndices = new ArrayList<>();
            for (int v : arr) this.phaseIndices.add(v);
        } else if (nbt.contains("phaseIndex")) {
            int old = nbt.getInt("phaseIndex");
            this.phaseIndices = new ArrayList<>();
            if (old >= 0) this.phaseIndices.add(old);
        } else {
            this.phaseIndices = new ArrayList<>();
        }
        this.groupId = nbt.contains("groupId") ? nbt.getString("groupId") : null;
        this.phaseCount = nbt.getInt("phaseCount");
        this.directionType = DirectionType.fromName(nbt.getString("directionType"));
        this.countdownDisplayMode = nbt.getInt("countdownDisplayMode");
        this.countdownThreshold = nbt.contains("countdownThreshold") ? nbt.getInt("countdownThreshold") : 15;
        this.showSeconds = nbt.getBoolean("showSeconds");
        this.fixedSeconds = nbt.contains("fixedSeconds") ? nbt.getInt("fixedSeconds") : 10;

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
            CompoundTag positionsTag = nbt.getCompound("groupPositions");
            int size = positionsTag.getInt("size");
            groupPositions.clear();
            for (int i = 0; i < size; i++) {
                BlockPos pos = NbtUtils.readBlockPos(positionsTag.getCompound("pos" + i));
                groupPositions.add(pos);
            }
        }

        // 兼容旧存档：校正 phaseCount 与 phaseTimes 长度，并钳制相位索引
        normalizePhaseData();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        int[] indicesArray = new int[phaseIndices.size()];
        for (int i = 0; i < phaseIndices.size(); i++) indicesArray[i] = phaseIndices.get(i);
        nbt.putIntArray("phaseIndices", indicesArray);
        nbt.putString("directionType", this.directionType.getName());
        nbt.putInt("countdownDisplayMode", this.countdownDisplayMode);
        nbt.putInt("countdownThreshold", this.countdownThreshold);
        nbt.putBoolean("showSeconds", this.showSeconds);
        nbt.putInt("fixedSeconds", this.fixedSeconds);

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
            CompoundTag positionsTag = new CompoundTag();
            positionsTag.putInt("size", groupPositions.size());
            for (int i = 0; i < groupPositions.size(); i++) {
                positionsTag.put("pos" + i, NbtUtils.writeBlockPos(groupPositions.get(i)));
            }
            nbt.put("groupPositions", positionsTag);
        }

        super.saveAdditional(nbt);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return createUpdatePacket();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return createUpdateTag();
    }

    public void markDirtyAndUpdate() {
        setChanged();
        if (level != null && !level .isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
    }

    // ==================== 枚举类 ====================

    public enum DirectionType {
        STRAIGHT_CIRCLE("straight"),
        STRAIGHT_ARROW("straight_arrow"),
        LEFT_TURN("left_turn"),
        RIGHT_TURN("right_turn"),
        TURN_AROUND("turn_around"),
        NON_MOTOR_VEHICLES("non_motor_vehicles"),
        NON_MOTOR_VEHICLES_LEFT_TURN("non_motor_vehicles_left_turn"),
        NON_MOTOR_VEHICLES_RIGHT_TURN("non_motor_vehicles_right_turn"),
        COLOR_FLASH("color_flash"),
        SLOW_FLASH("slow_flash");

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
            return STRAIGHT_CIRCLE;
        }
    }

    // ==================== 数据类 ====================

    public static class LightTimingInfo {
        private final String activeColor;
        private final int activeRemaining;
        private final int redRemaining;
        private final int yellowRemaining;
        private final int greenRemaining;

        public LightTimingInfo(String activeColor, int activeRemaining,
                               int redRemaining, int yellowRemaining, int greenRemaining) {
            this.activeColor = activeColor;
            this.activeRemaining = activeRemaining;
            this.redRemaining = redRemaining;
            this.yellowRemaining = yellowRemaining;
            this.greenRemaining = greenRemaining;
        }

        public String getActiveColor() { return activeColor; }
        public int getActiveRemaining() { return activeRemaining; }
        public int getRedRemaining() { return redRemaining; }
        public int getYellowRemaining() { return yellowRemaining; }
        public int getGreenRemaining() { return greenRemaining; }
    }
}
