package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.ApiCompat;

import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.signal.ClientRailGroupIndex;
import com.beigu.yunbeiuc.signal.RailPhaseBinding;
import com.beigu.yunbeiuc.signal.RailSignalRegistry;

import com.beigu.yunbeiuc.api.mapper.BlockEntityMapper;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// 红绿灯方块实体：相位时间表 + 组内轨道绑定 + 灯色/读秒的唯一数据源。

public class TrafficLightsBlockEntity extends BlockEntityMapper {
    private static final int MAX_PHASE_INDICES = 4;

    // 已加载红绿灯的弱引用索引，用于「应用到全部」。
    private static final Set<TrafficLightsBlockEntity> LOADED_LIGHTS =
            Collections.newSetFromMap(new WeakHashMap<>());

    private List<Integer> phaseIndices = new ArrayList<>();
    private String groupId = null;
    private List<BlockPos> groupPositions = new ArrayList<>();

    private List<RailPhaseBinding> railBindings = new ArrayList<>();

    private int[] phaseTimes = null;
    private int[] phaseDelays = null;
    private int phaseCount = 0;

    private int currentTick = 0;
    private int currentActivePhase = 0;
    private boolean cycleActive = false;
    private long cycleStartGameTime = Long.MIN_VALUE;

    private static final int YELLOW_DURATION = 3 * 20;
    private static final int FLASH_DURATION = 3 * 20;
    private static final int FLASH_INTERVAL = 10;

    private DirectionType directionType = DirectionType.STRAIGHT_CIRCLE;

    private int countdownDisplayMode = 0;
    private int countdownThreshold = 15;

    private boolean countdownFullDisplay = false;
    private int halfDisplayRedFrom = 15;
    private int halfDisplayGreenFrom = 15;
    private int countdownNumberMode = 0;
    private boolean hidePatternOnGreen = false;
    private boolean yellowFlashAlways = false;

    private boolean showSeconds = false;
    private int fixedSeconds = 10;

    private boolean pulseCountdown = false;
    private long pulseStartGameTime = Long.MIN_VALUE;
    private int lastCountdownDisplay = -1;

    public TrafficLightsBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.TRAFFIC_LIGHTS_BLOCK_ENTITY.get(), pos, state);
    }

    public TrafficLightsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        Block block = state.getBlock();
        if (block == com.beigu.yunbeiuc.block.MunicipalBlocks.TRAFFIC_LIGHTS_FOGGY.get()) {
            this.directionType = DirectionType.COLOR_FLASH;
        }
    }

    public void tick() {
        if (level == null || level .isClientSide()) {
            return;
        }

        LOADED_LIGHTS.add(this);

        publishRailSignals();

        // 黄闪常亮：不跟随相位，固定黄灯并闪烁；人行道跳过，继续正常执行。
        if (yellowFlashAlways && !isPavementSignal()) {
            forceYellowFlashState();
            return;
        }

        if (groupId != null && level.getGameTime() % 40L == 0L) {
            verifyGroupIntegrity();
        }

        if (!cycleActive || groupId == null || phaseTimes == null || phaseCount <= 0) {
            return;
        }

        normalizePhaseData();

        if (cycleStartGameTime == Long.MIN_VALUE) {
            cycleStartGameTime = level.getGameTime();
        }

        int previousPhase = currentActivePhase;
        refreshPhaseProgress();

        if (currentActivePhase != previousPhase || level.getGameTime() % 10L == 0L) {
            markDirtyAndUpdate();
        }

        syncGroupClock();
        updateLightState();
    }

    private void refreshPhaseProgress() {
        if (phaseTimes == null || phaseCount <= 0 || level == null) return;
        if (cycleStartGameTime == Long.MIN_VALUE) return;

        long totalCycleTicks = 0L;
        for (int i = 0; i < phaseCount; i++) {
            totalCycleTicks += (long) phaseTimes[i] * 20L;
        }
        if (totalCycleTicks <= 0L) return;

        long elapsed = (level.getGameTime() - cycleStartGameTime) % totalCycleTicks;
        if (elapsed < 0L) elapsed += totalCycleTicks;

        long accumulated = 0L;
        for (int i = 0; i < phaseCount; i++) {
            long duration = (long) phaseTimes[i] * 20L;
            if (elapsed < accumulated + duration) {
                currentActivePhase = i;
                currentTick = (int) (elapsed - accumulated);
                return;
            }
            accumulated += duration;
        }
        currentActivePhase = phaseCount - 1;
        currentTick = 0;
    }

    private void normalizePhaseData() {
        if (phaseTimes != null && phaseTimes.length > 0) {
            phaseCount = phaseTimes.length;
        } else {
            phaseTimes = null;
            phaseDelays = null;
            phaseCount = 0;
            phaseIndices.clear();
            currentActivePhase = 0;
            cycleActive = false;
            return;
        }
        if (phaseDelays == null || phaseDelays.length != phaseCount) {
            int[] normalizedDelays = new int[phaseCount];
            if (phaseDelays != null) {
                System.arraycopy(phaseDelays, 0, normalizedDelays, 0, Math.min(phaseDelays.length, phaseCount));
            }
            phaseDelays = normalizedDelays;
        }
        for (int i = 0; i < phaseCount; i++) {
            phaseDelays[i] = Math.max(0, Math.min(phaseDelays[i], phaseTimes[i]));
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

        if (railBindings != null && !railBindings.isEmpty()) {
            final long valid = maskOf(phaseCount);
            for (RailPhaseBinding binding : railBindings) {
                if ((binding.getPhaseMask() & ~valid) != 0L) {
                    binding.setPhaseMask(binding.getPhaseMask() & valid);
                }
            }
        }
    }

    private void updateLightState() {
        if (level == null || level .isClientSide() || phaseTimes == null || phaseCount <= 0) return;

        normalizePhaseData();
        refreshPhaseProgress();

        BlockState currentState = getBlockState();
        if (!currentState.hasProperty(TrafficLightsBlock.LIGHT_STATE)) return;

        Block currentBlock = currentState.getBlock();
        boolean isPavementLight = currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLACK.get();

        // 黄闪常亮：即使被同组同步调用，也只保持黄灯。
        if (yellowFlashAlways && !isPavementLight) {
            forceYellowFlashState();
            return;
        }

        TrafficLightsBlock.LightState lightState;

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int delayTicks = currentPhaseDelayTicks();
        int phaseEndTick = totalTicks - delayTicks;
        int yellowStartTick = phaseEndTick - YELLOW_DURATION;
        int flashStartTick = yellowStartTick - FLASH_DURATION;

        if (phaseIndices.contains(currentActivePhase)) {
            if (isPavementLight) {
                int walkFlashStartTick = Math.max(0, phaseEndTick - FLASH_DURATION - 2 * YELLOW_DURATION);
                int walkYellowStartTick = Math.max(walkFlashStartTick, phaseEndTick - 2 * YELLOW_DURATION);
                int walkRedStartTick = Math.max(walkYellowStartTick, yellowStartTick);
                if (currentTick < walkFlashStartTick) {
                    lightState = TrafficLightsBlock.LightState.GREEN;
                } else if (currentTick < walkYellowStartTick) {
                    int flashTick = currentTick - walkFlashStartTick;
                    int flashPhase = flashTick / FLASH_INTERVAL;
                    lightState = (flashPhase % 2 == 0) ? TrafficLightsBlock.LightState.GRAY : TrafficLightsBlock.LightState.GREEN;
                } else if (currentTick < walkRedStartTick) {
                    lightState = TrafficLightsBlock.LightState.YELLOW;
                } else {
                    lightState = TrafficLightsBlock.LightState.RED;
                }
            } else {
                if (currentTick >= phaseEndTick) {
                    lightState = TrafficLightsBlock.LightState.RED;
                } else if (currentTick < flashStartTick) {
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
            level.setBlock(worldPosition, currentState.setValue(TrafficLightsBlock.LIGHT_STATE, lightState), VersionServices.blocks().updateAll());
        }
    }

    private int currentPhaseDelayTicks() {
        if (phaseDelays == null || currentActivePhase < 0 || currentActivePhase >= phaseDelays.length) {
            return 0;
        }
        int delay = Math.max(0, Math.min(phaseDelays[currentActivePhase], phaseTimes[currentActivePhase]));
        return delay * 20;
    }

    private void syncGroupClock() {
        if (groupId == null || groupPositions.isEmpty() || level == null || level .isClientSide()) return;

        for (BlockPos pos : groupPositions) {
            if (pos.equals(this.worldPosition)) continue;
            if (!level.isLoaded(pos)) continue;
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TrafficLightsBlockEntity tl) {
                tl.phaseTimes = this.phaseTimes;
                tl.phaseDelays = this.phaseDelays;
                tl.phaseCount = this.phaseCount;
                tl.cycleActive = this.cycleActive;
                tl.cycleStartGameTime = this.cycleStartGameTime;
                tl.railBindings = new ArrayList<>(this.railBindings);
                tl.updateLightState();
            } else {
                unloadGroup();
                return;
            }
        }
    }

    public List<RailPhaseBinding> getRailBindings() {
        return Collections.unmodifiableList(railBindings);
    }

    public BlockPos groupAnchor() {
        return groupPositions.isEmpty() ? worldPosition : groupPositions.get(0);
    }

    public static long maskOf(int phaseCount) {
        if (phaseCount <= 0) return 0L;
        if (phaseCount >= RailPhaseBinding.MAX_PHASES) return -1L;
        return (1L << phaseCount) - 1L;
    }

    public RailPhaseBinding findRailBinding(BlockPos start, BlockPos end) {
        for (RailPhaseBinding binding : railBindings) {
            if (binding.matches(start, end)) {
                return binding;
            }
        }
        return null;
    }

    public boolean addRailBinding(BlockPos start, BlockPos end, Player player) {
        if (level == null || level .isClientSide()) return false;
        if (start.equals(end)) {
            message(player, "§c起点与终点不能是同一个节点！");
            return false;
        }
        if (groupId == null) {
            message(player, "§c此红绿灯未链接到任何组！");
            return false;
        }

        final boolean isNew = findRailBinding(start, end) == null;
        if (isNew) {
            railBindings.add(new RailPhaseBinding(start, end, RailPhaseBinding.bit(0)));
        }

        propagateRailBindings();
        publishRailSignals();
        markGroupStateDirty();
        if (isNew) {
            message(player, "§a已链接线路 §6" + start.toShortString() + " → " + end.toShortString()
                    + " §7(共 " + railBindings.size() + " 条)。瞄准线路右键可编辑放行相位");
        } else {
            message(player, "§7这条线路已在本组中 §7(共 " + railBindings.size() + " 条)");
        }
        return true;
    }

    public boolean setRailBindingMask(BlockPos start, BlockPos end, long phaseMask, Player player) {
        if (level == null || level .isClientSide()) return false;
        final RailPhaseBinding binding = findRailBinding(start, end);
        if (binding == null) {
            message(player, "§c这条线路尚未链接到本组！");
            return false;
        }

        final long clamped = phaseMask & maskOf(phaseCount);
        if (binding.getPhaseMask() == clamped) {
            return true;
        }
        binding.setPhaseMask(clamped);

        propagateRailBindings();
        publishRailSignals();
        markGroupStateDirty();
        message(player, "§a已更新线路 §6" + start.toShortString() + " → " + end.toShortString()
                + " §a放行相位 §6" + describeMask(clamped, phaseCount)
                + " §7(共 " + railBindings.size() + " 条)");
        return true;
    }

    public static String describeMask(long mask, int phaseCount) {
        if (mask == 0L) return "无";
        final StringBuilder sb = new StringBuilder();
        final int limit = Math.min(phaseCount <= 0 ? RailPhaseBinding.MAX_PHASES : phaseCount,
                RailPhaseBinding.MAX_PHASES);
        for (int i = 0; i < limit; i++) {
            if ((mask & RailPhaseBinding.bit(i)) == 0L) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append(i + 1);
        }
        return sb.length() == 0 ? "无" : sb.toString();
    }

    public boolean removeRailBinding(BlockPos start, BlockPos end, Player player) {
        if (level == null || level .isClientSide()) return false;

        boolean removed = railBindings.removeIf(binding -> binding.matches(start, end));
        if (!removed) {
            message(player, "§c这条线路尚未链接到本组！");
            return false;
        }

        propagateRailBindings();
        publishRailSignals();
        markGroupStateDirty();
        message(player, "§a已解除线路 §6" + start.toShortString() + " → " + end.toShortString()
                + " §7(剩余 " + railBindings.size() + " 条)");
        return true;
    }

    public void clearRailBindings(Player player) {
        if (level == null || level .isClientSide()) return;
        final int count = railBindings.size();
        railBindings.clear();
        propagateRailBindings();
        RailSignalRegistry.removeGroup(groupAnchor());
        markGroupStateDirty();
        message(player, "§a已清空本组线路绑定 §7(共 " + count + " 条)");
    }

    private void propagateRailBindings() {
        if (level == null || level .isClientSide() || groupId == null) return;
        for (BlockPos pos : groupPositions) {
            if (pos.equals(this.worldPosition)) continue;
            if (!level.isLoaded(pos)) continue;
            if (level.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl && tl != this) {
                tl.railBindings = new ArrayList<>(this.railBindings);
            }
        }
    }

    private void publishRailSignals() {
        if (level == null || level .isClientSide()) return;
        final BlockPos anchor = groupAnchor();
        if (railBindings.isEmpty()) {
            RailSignalRegistry.removeGroup(anchor);
            return;
        }
        for (RailPhaseBinding binding : railBindings) {
            RailSignalRegistry.publish(binding.railProduct(), anchor, binding.getPhaseMask(),
                    isMaskActive(binding.getPhaseMask()));
        }
    }

    public boolean isMaskActive(long mask) {
        if (mask == 0L) return false;
        if (!cycleActive || phaseTimes == null || phaseCount <= 0) return false;
        if (level == null || cycleStartGameTime == Long.MIN_VALUE) return false;
        normalizePhaseData();
        refreshPhaseProgress();
        return currentActivePhase >= 0
                && currentActivePhase < RailPhaseBinding.MAX_PHASES
                && (mask & RailPhaseBinding.bit(currentActivePhase)) != 0L;
    }

    public boolean isPhaseActive(int phaseIndex) {
        return isMaskActive(RailPhaseBinding.bit(phaseIndex));
    }

    private static void message(Player player, String text) {
        if (player != null) {
            ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal(text), true);
        }
    }

    public int getGreenRemainingSeconds() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;

        normalizePhaseData();
        refreshPhaseProgress();

        if (!phaseIndices.contains(currentActivePhase)) return -1;

        Block currentBlock = getBlockState().getBlock();
        boolean isPavementLight = currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLACK.get();

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int phaseEndTick = totalTicks - currentPhaseDelayTicks();

        if (isPavementLight) {
            int walkGreenEndTick = Math.max(0, phaseEndTick - 2 * YELLOW_DURATION);
            if (currentTick >= walkGreenEndTick) {
                return -1;
            }
            return (walkGreenEndTick - currentTick + 19) / 20;
        }

        int yellowStartTick = phaseEndTick - YELLOW_DURATION;
        if (currentTick >= yellowStartTick) {
            return -1;
        }
        return (yellowStartTick - currentTick + 19) / 20;
    }

    public int getYellowRemainingSeconds() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;

        normalizePhaseData();
        refreshPhaseProgress();

        if (!phaseIndices.contains(currentActivePhase)) return -1;

        Block currentBlock = getBlockState().getBlock();
        boolean isPavementLight = currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLACK.get();

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int phaseEndTick = totalTicks - currentPhaseDelayTicks();

        if (isPavementLight) {
            int walkYellowStartTick = Math.max(0, phaseEndTick - 2 * YELLOW_DURATION);
            int walkRedStartTick = Math.max(walkYellowStartTick, phaseEndTick - YELLOW_DURATION);
            if (currentTick >= walkYellowStartTick && currentTick < walkRedStartTick) {
                return (walkRedStartTick - currentTick + 19) / 20;
            }
            return -1;
        }

        int yellowStartTick = phaseEndTick - YELLOW_DURATION;

        if (currentTick >= yellowStartTick && currentTick < phaseEndTick) {
            return (phaseEndTick - currentTick + 19) / 20;
        }
        return -1;
    }

    public int getRedRemainingSeconds() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;

        normalizePhaseData();
        refreshPhaseProgress();

        if (phaseIndices.isEmpty()) {
            long fallbackTicks = (long) phaseTimes[currentActivePhase] * 20L - currentTick;
            if (fallbackTicks < 0L) fallbackTicks = 0L;
            return (int) ((fallbackTicks + 19L) / 20L);
        }

        int totalTicks = phaseTimes[currentActivePhase] * 20;
        int phaseEndTick = totalTicks - currentPhaseDelayTicks();
        Block currentBlock = getBlockState().getBlock();
        boolean isPavementLight = currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLACK.get();
        int redStartTick = isPavementLight
                ? Math.max(0, phaseEndTick - 2 * YELLOW_DURATION)
                : phaseEndTick;
        if (phaseIndices.contains(currentActivePhase) && currentTick < redStartTick) {
            return -1;
        }

        int targetPhase = -1;
        for (int step = 1; step < phaseCount; step++) {
            int idx = (currentActivePhase + step) % phaseCount;
            if (phaseIndices.contains(idx)) {
                targetPhase = idx;
                break;
            }
        }
        if (targetPhase < 0) return -1;

        long remainingTicks = (long) totalTicks - currentTick;
        for (int step = 1; step < phaseCount; step++) {
            int idx = (currentActivePhase + step) % phaseCount;
            if (idx == targetPhase) break;
            remainingTicks += (long) phaseTimes[idx] * 20L;
        }
        if (remainingTicks < 0L) remainingTicks = 0L;

        return (int) ((remainingTicks + 19L) / 20L);
    }

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
                if (isPavementSignal()) {
                    redSec = getRedRemainingSeconds();
                    activeColor = "红灯";
                    activeRemaining = redSec;
                } else {
                    yellowSec = getYellowRemainingSeconds();
                    activeColor = "黄灯";
                    activeRemaining = yellowSec;
                }
            }
            default -> {
                greenSec = getGreenRemainingSeconds();
                activeColor = "绿灯";
                activeRemaining = greenSec;
            }
        }

        return new LightTimingInfo(activeColor, activeRemaining, redSec, yellowSec, greenSec);
    }

    public boolean setPhaseIndices(List<Integer> indices, Player player) {
        if (phaseTimes == null || phaseCount <= 0) {
            if (player != null && !level .isClientSide()) {
                ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c请先使用命令设置时间！"), true);
            }
            return false;
        }
        if (groupId == null) {
            if (player != null && !level .isClientSide()) {
                ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c此红绿灯未链接到任何组！"), true);
            }
            return false;
        }
        int maxAllowed = Math.min(MAX_PHASE_INDICES, phaseCount);
        if (indices.size() > maxAllowed) {
            if (player != null && !level .isClientSide()) {
                ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c最多只能设置 " + maxAllowed + " 个相位！"), true);
            }
            return false;
        }
        if (new HashSet<>(indices).size() != indices.size()) {
            if (player != null && !level .isClientSide()) {
                ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c相位不可以重复！"), true);
            }
            return false;
        }
        for (int index : indices) {
            if (index < 0 || index >= phaseCount) {
                if (player != null && !level .isClientSide()) {
                    ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c无效的相位索引！范围：1-" + phaseCount), true);
                }
                return false;
            }
        }
        this.phaseIndices = new ArrayList<>(indices);
        if (player != null && !level .isClientSide()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indices.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(indices.get(i) + 1);
            }
            ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§a相位已设置为 §6" + sb + " §7(共" + phaseCount + "个相位)"), true);
        }
        markGroupStateDirty();
        return true;
    }

    public void startCycle() {
        if (phaseTimes == null || phaseCount <= 0) return;
        this.cycleActive = true;
        this.cycleStartGameTime = (level != null) ? level.getGameTime() : Long.MIN_VALUE;
        this.currentTick = 0;
        this.currentActivePhase = 0;
        updateLightState();
        markDirtyAndUpdate();
    }

    public void stopCycle() {
        this.cycleActive = false;
        markDirtyAndUpdate();
    }

    private void verifyGroupIntegrity() {
        if (level == null || level.isClientSide() || groupPositions == null || groupPositions.isEmpty()) {
            return;
        }
        boolean missing = false;
        for (BlockPos pos : groupPositions) {
            if (pos.equals(worldPosition)) continue;
            if (!level.isLoaded(pos)) return;
            final BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock) {
                continue;
            }
            if (state.isAir()) {
                missing = true;
                break;
            }
        }
        if (missing) {
            unloadGroup();
        }
    }

    public void unloadGroup() {
        if (groupId == null) return;

        stopCycle();

        if (level != null && !level .isClientSide()) {
            RailSignalRegistry.removeGroup(groupAnchor());
        }

        if (level != null && !level .isClientSide() && groupPositions != null) {
            for (BlockPos pos : groupPositions) {
                if (pos.equals(this.worldPosition)) continue;
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof TrafficLightsBlockEntity tl && tl != this) {
                    tl.groupId = null;
                    tl.groupPositions.clear();
                    tl.phaseTimes = null;
                    tl.phaseCount = 0;
                    tl.phaseIndices.clear();
                    tl.railBindings.clear();
                    tl.directionType = DirectionType.STRAIGHT_CIRCLE;
                    tl.cycleStartGameTime = Long.MIN_VALUE;
                    tl.stopCycle();
                    tl.markGroupStateDirty();
                }
            }
        }

        groupId = null;
        groupPositions.clear();
        phaseTimes = null;
        phaseCount = 0;
        phaseIndices.clear();
        railBindings.clear();
        directionType = DirectionType.STRAIGHT_CIRCLE;
        cycleStartGameTime = Long.MIN_VALUE;
        markGroupStateDirty();
    }

    public void setTimings(int phaseCount, int[] timings) {
        setTimings(phaseCount, timings, null);
    }

    public void setTimings(int phaseCount, int[] timings, int[] delays) {
        this.phaseTimes = timings;
        this.phaseDelays = delays;
        this.phaseCount = timings != null ? timings.length : 0;
        this.phaseIndices.clear();
        startCycle();
        markGroupStateDirty();
    }

    public int[] getPhaseDelays() {
        return phaseDelays;
    }

    public void setGroup(String groupId, List<BlockPos> positions) {
        this.groupId = groupId;
        this.groupPositions = new ArrayList<>(positions);
        this.directionType = DirectionType.STRAIGHT_CIRCLE;
        this.phaseTimes = null;
        this.phaseDelays = null;
        this.phaseCount = 0;
        this.phaseIndices.clear();
        this.railBindings.clear();
        this.currentTick = 0;
        this.currentActivePhase = 0;
        this.cycleActive = false;
        this.cycleStartGameTime = Long.MIN_VALUE;
        if (level != null && !level .isClientSide()) {
            RailSignalRegistry.removeGroup(groupAnchor());
        }
        if (level != null && !level .isClientSide()) {
            BlockState state = getBlockState();
            if (state.hasProperty(TrafficLightsBlock.LIGHT_STATE)) {
                level.setBlock(worldPosition, state.setValue(TrafficLightsBlock.LIGHT_STATE, TrafficLightsBlock.LightState.RED), VersionServices.blocks().updateAll());
            }
        }
        markGroupStateDirty();
    }

    public boolean setStaticState(DirectionType direction, TrafficLightsBlock.LightState color,
                                   boolean showSeconds, int fixedSeconds, Player player) {
        if (isInGroup()) {
            if (player != null && !level .isClientSide()) {
                ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c该红绿灯已加入相位组，无法单独设置静态状态！"), true);
            }
            return false;
        }
        this.directionType = direction;
        this.showSeconds = showSeconds;
        this.fixedSeconds = fixedSeconds;
        if (level != null && !level .isClientSide()) {
            BlockState state = getBlockState();
            if (state.hasProperty(TrafficLightsBlock.LIGHT_STATE)) {
                level.setBlock(worldPosition, state.setValue(TrafficLightsBlock.LIGHT_STATE, color), VersionServices.blocks().updateAll());
            }
        }
        markGroupStateDirty();
        return true;
    }

    public boolean hasTimings() {
        return phaseTimes != null && phaseCount > 0;
    }

    public boolean isInGroup() {
        return groupId != null;
    }

    public int getPhaseCount() {
        return phaseCount;
    }

    public int getActivePhase() {
        if (phaseTimes == null || phaseCount <= 0) return -1;
        refreshPhaseProgress();
        return currentActivePhase;
    }

    public boolean isCycleActive() {
        return cycleActive;
    }

    public TrafficLightsBlock.LightState getRailLightState(RailPhaseBinding binding) {
        if (binding == null || phaseTimes == null || phaseCount <= 0 || level == null) {
            return TrafficLightsBlock.LightState.RED;
        }
        refreshPhaseProgress();
        if (!cycleActive || currentActivePhase < 0 || currentActivePhase >= phaseCount) {
            return TrafficLightsBlock.LightState.RED;
        }

        if (!binding.isPhaseEnabled(currentActivePhase)) {
            return TrafficLightsBlock.LightState.RED;
        }

        final int totalTicks = phaseTimes[currentActivePhase] * 20;
        final int yellowStartTick = totalTicks - YELLOW_DURATION;
        final int flashStartTick = yellowStartTick - FLASH_DURATION;

        if (currentTick < flashStartTick) {
            return TrafficLightsBlock.LightState.GREEN;
        }
        if (currentTick < yellowStartTick) {
            final int flashPhase = (currentTick - flashStartTick) / FLASH_INTERVAL;
            return (flashPhase % 2 == 0)
                    ? TrafficLightsBlock.LightState.GRAY
                    : TrafficLightsBlock.LightState.GREEN;
        }
        return TrafficLightsBlock.LightState.YELLOW;
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
        markGroupStateDirty();
    }

    public int getCountdownDisplayMode() {
        return countdownDisplayMode;
    }

    public void setCountdownDisplayMode(int mode) {
        this.countdownDisplayMode = mode;
        markGroupStateDirty();
    }

    public int getCountdownThreshold() {
        return countdownThreshold;
    }

    public void setCountdownThreshold(int threshold) {
        this.countdownThreshold = threshold;
        markGroupStateDirty();
    }

    public boolean isShowSeconds() {
        return showSeconds;
    }

    public void setShowSeconds(boolean showSeconds) {
        this.showSeconds = showSeconds;
        markGroupStateDirty();
    }

    public int getFixedSeconds() {
        return fixedSeconds;
    }

    public void setFixedSeconds(int fixedSeconds) {
        this.fixedSeconds = fixedSeconds;
        markGroupStateDirty();
    }

    public boolean isPulseCountdown() {
        return pulseCountdown;
    }

    public void setPulseCountdown(boolean pulseCountdown) {
        this.pulseCountdown = pulseCountdown;
        this.pulseStartGameTime = Long.MIN_VALUE;
        this.lastCountdownDisplay = -1;
        markGroupStateDirty();
    }

    public boolean isPavementSignal() {
        Block block = getBlockState().getBlock();
        return block == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                || block == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || block == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()
                || block == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_GRAY.get()
                || block == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLACK.get();
    }

    private void forceYellowFlashState() {
        BlockState state = getBlockState();
        if (state.hasProperty(TrafficLightsBlock.LIGHT_STATE)
                && state.getValue(TrafficLightsBlock.LIGHT_STATE) != TrafficLightsBlock.LightState.YELLOW) {
            level.setBlock(worldPosition,
                    state.setValue(TrafficLightsBlock.LIGHT_STATE, TrafficLightsBlock.LightState.YELLOW),
                    VersionServices.blocks().updateAll());
        }
    }

    public boolean isYellowFlashAlways() {
        return yellowFlashAlways;
    }

    public void setCountdownCondition(boolean fullDisplay, int redFrom, int greenFrom, int numberMode,
                                      boolean hidePatternOnGreen, boolean yellowFlashAlways) {
        this.countdownFullDisplay = fullDisplay;
        this.halfDisplayRedFrom = Math.max(0, Math.min(300, redFrom));
        this.halfDisplayGreenFrom = Math.max(0, Math.min(300, greenFrom));
        this.countdownNumberMode = Math.max(0, Math.min(2, numberMode));
        this.hidePatternOnGreen = hidePatternOnGreen;
        this.yellowFlashAlways = yellowFlashAlways;
        this.lastCountdownDisplay = -1;
        markGroupStateDirty();
    }

    public boolean isCountdownFullDisplay() {
        return countdownFullDisplay;
    }

    public int getHalfDisplayRedFrom() {
        return halfDisplayRedFrom;
    }

    public int getHalfDisplayGreenFrom() {
        return halfDisplayGreenFrom;
    }

    public int getCountdownNumberMode() {
        return countdownNumberMode;
    }

    public boolean isHidePatternOnGreen() {
        return hidePatternOnGreen;
    }

    // 把附加条件应用到当前维度所有已加载的红绿灯；对方没有对应参数时跳过。
    public static void applyConditionsToAll(ServerLevel serverLevel, boolean pulseEnabled, boolean fullDisplay,
                                            int redFrom, int greenFrom, int numberMode, boolean hidePatternOnGreen,
                                            boolean yellowFlashAlways) {
        List<TrafficLightsBlockEntity> snapshot = new ArrayList<>(LOADED_LIGHTS);
        for (TrafficLightsBlockEntity light : snapshot) {
            if (light == null || light.isRemoved() || light.level != serverLevel) {
                continue;
            }
            light.applySharedConditions(pulseEnabled, fullDisplay, redFrom, greenFrom, numberMode,
                    hidePatternOnGreen, yellowFlashAlways);
        }
    }

    private void applySharedConditions(boolean pulseEnabled, boolean fullDisplay, int redFrom, int greenFrom,
                                       int numberMode, boolean hidePatternOnGreen, boolean yellowFlashAlways) {
        Block block = getBlockState().getBlock();
        boolean pavement = isPavementSignal();
        boolean countdownTimer = block == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get();
        boolean shanghai = block == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get()
                || block == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get();
        boolean taipei = block == MunicipalBlocks.TRAFFIC_LIGHTS_GREEN_TAIPEI.get();
        boolean hasCountdown = pavement || countdownTimer || shanghai || taipei;

        // 绿灯隐藏图案：所有红绿灯都支持。
        this.hidePatternOnGreen = hidePatternOnGreen;

        // 黄闪常亮：人行道与读秒器没有这个参数，跳过。
        if (!pavement && !countdownTimer) {
            this.yellowFlashAlways = yellowFlashAlways;
        }

        // 数字显示模式：所有会显示读秒的灯都支持。
        if (hasCountdown) {
            this.countdownNumberMode = Math.max(0, Math.min(2, numberMode));
        }
        // 全显 / 半显与红绿起始秒：人行道灯没有这些参数，跳过。
        if (hasCountdown && !pavement) {
            this.countdownFullDisplay = fullDisplay;
            this.halfDisplayRedFrom = Math.max(0, Math.min(300, redFrom));
            this.halfDisplayGreenFrom = Math.max(0, Math.min(300, greenFrom));
        }
        // 脉冲只有上海红绿灯支持，其余类型跳过。
        if (shanghai) {
            this.pulseCountdown = pulseEnabled;
            this.pulseStartGameTime = Long.MIN_VALUE;
        }

        this.lastCountdownDisplay = -1;
        markGroupStateDirty();
    }

    // 把剩余秒数格式化成读秒文本（显示屏固定两位）：
    // mode 0 = 十位封顶 99；mode 1 = 百起步十（A0=100, A5=105, B0=110 …）；
    // mode 2 = 十六进制两位、不补前导 0（如 120 -> 78，5 -> 5）。
    public String formatCountdown(int seconds) {
        int value = Math.max(0, seconds);
        if (countdownNumberMode == 2) {
            return Integer.toHexString(Math.min(value, 255)).toUpperCase();
        }
        if (countdownNumberMode == 1) {
            if (value < 100) {
                return String.valueOf(Math.min(value, 99));
            }
            int letterIndex = Math.min((value - 100) / 10, 25);
            char letter = (char) ('A' + letterIndex);
            return "" + letter + (value % 10);
        }
        return String.valueOf(Math.min(value, 99));
    }

    // 解析静态读秒输入：支持纯数字或「字母 + 个位」（A0=100, A5=105, B0=110 …）。
    public static int parseCountdownInput(String text, int fallback) {
        if (text == null) {
            return fallback;
        }
        String value = text.trim().toUpperCase();
        if (value.isEmpty()) {
            return fallback;
        }
        if (value.length() == 2
                && value.charAt(0) >= 'A' && value.charAt(0) <= 'Z'
                && Character.isDigit(value.charAt(1))) {
            int parsed = 100 + (value.charAt(0) - 'A') * 10 + (value.charAt(1) - '0');
            return Math.max(0, Math.min(999, parsed));
        }
        try {
            return Math.max(0, Math.min(999, Integer.parseInt(value)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public boolean shouldHideCountdown(int remaining, boolean isRed) {
        if (isPavementSignal()) {
            return !showSeconds;
        }
        // 黄闪常亮时没有有效读秒，只保留灰色 88。
        if (yellowFlashAlways) {
            return true;
        }
        if (countdownFullDisplay) {
            return false;
        }
        int showFrom = isRed ? halfDisplayRedFrom : halfDisplayGreenFrom;
        return remaining > showFrom;
    }

    public boolean isCountdownPulseHidden() {
        if (!pulseCountdown || level == null || !level.isClientSide()) {
            return false;
        }
        Block block = getBlockState().getBlock();
        if (block != MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get()
                && block != MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()) {
            return false;
        }
        if (!getBlockState().hasProperty(TrafficLightsBlock.LIGHT_STATE)) {
            return false;
        }
        long now = level.getGameTime();
        TrafficLightsBlock.LightState lightState = getBlockState().getValue(TrafficLightsBlock.LIGHT_STATE);
        int displayed;
        if (lightState == TrafficLightsBlock.LightState.GREEN) {
            displayed = getGreenRemainingSeconds();
        } else if (lightState == TrafficLightsBlock.LightState.RED) {
            displayed = getRedRemainingSeconds();
        } else {
            lastCountdownDisplay = -1;
            return false;
        }
        if (displayed != lastCountdownDisplay) {
            if (countdownFullDisplay) {
                // 全显：读秒首次出现时触发一次脉冲。
                if (lastCountdownDisplay < 0 && displayed >= 0) {
                    pulseStartGameTime = now;
                }
            } else {
                // 半显：脉冲跟随半显起始秒数。
                int trigger = lightState == TrafficLightsBlock.LightState.GREEN
                        ? halfDisplayGreenFrom : halfDisplayRedFrom;
                if (displayed == trigger) {
                    pulseStartGameTime = now;
                }
            }
            lastCountdownDisplay = displayed;
        }
        long elapsed = now - pulseStartGameTime;
        return pulseStartGameTime != Long.MIN_VALUE && elapsed >= 0L && elapsed < 5L;
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        if (nbt.contains("phaseIndices")) {
            int[] arr = NbtCompat.getIntArray(nbt, "phaseIndices");
            this.phaseIndices = new ArrayList<>();
            for (int v : arr) this.phaseIndices.add(v);
        } else if (nbt.contains("phaseIndex")) {
            int old = NbtCompat.getInt(nbt, "phaseIndex");
            this.phaseIndices = new ArrayList<>();
            if (old >= 0) this.phaseIndices.add(old);
        } else {
            this.phaseIndices = new ArrayList<>();
        }
        this.groupId = nbt.contains("groupId") ? NbtCompat.getString(nbt, "groupId") : null;
        this.phaseCount = NbtCompat.getInt(nbt, "phaseCount");
        this.directionType = DirectionType.fromName(NbtCompat.getString(nbt, "directionType"));
        this.countdownDisplayMode = NbtCompat.getInt(nbt, "countdownDisplayMode");
        this.countdownThreshold = nbt.contains("countdownThreshold") ? NbtCompat.getInt(nbt, "countdownThreshold") : 15;
        this.showSeconds = NbtCompat.getBoolean(nbt, "showSeconds");
        this.fixedSeconds = nbt.contains("fixedSeconds") ? NbtCompat.getInt(nbt, "fixedSeconds") : 10;
        this.pulseCountdown = NbtCompat.getBoolean(nbt, "pulseCountdown");
        this.countdownFullDisplay = NbtCompat.getBoolean(nbt, "countdownFullDisplay");
        this.halfDisplayRedFrom = nbt.contains("halfDisplayRedFrom") ? NbtCompat.getInt(nbt, "halfDisplayRedFrom") : 15;
        this.halfDisplayGreenFrom = nbt.contains("halfDisplayGreenFrom") ? NbtCompat.getInt(nbt, "halfDisplayGreenFrom") : 15;
        this.countdownNumberMode = nbt.contains("countdownNumberMode")
                ? NbtCompat.getInt(nbt, "countdownNumberMode")
                : (NbtCompat.getBoolean(nbt, "hexCountdown") ? 2 : 0);
        this.hidePatternOnGreen = NbtCompat.getBoolean(nbt, "hidePatternOnGreen");
        this.yellowFlashAlways = NbtCompat.getBoolean(nbt, "yellowFlashAlways");

        if (nbt.contains("phaseTimes")) {
            this.phaseTimes = NbtCompat.getIntArray(nbt, "phaseTimes");
            if (this.phaseTimes.length == 0) {
                this.phaseTimes = null;
            }
        }

        if (nbt.contains("phaseDelays")) {
            this.phaseDelays = NbtCompat.getIntArray(nbt, "phaseDelays");
            if (this.phaseDelays.length == 0) {
                this.phaseDelays = null;
            }
        }

        this.currentActivePhase = NbtCompat.getInt(nbt, "currentActivePhase");
        this.currentTick = NbtCompat.getInt(nbt, "currentTick");
        this.cycleActive = NbtCompat.getBoolean(nbt, "cycleActive");
        this.cycleStartGameTime = nbt.contains("cycleStartGameTime")
                ? NbtCompat.getLong(nbt, "cycleStartGameTime")
                : Long.MIN_VALUE;

        if (nbt.contains("groupPositions")) {
            CompoundTag positionsTag = NbtCompat.getCompound(nbt, "groupPositions");
            int size = NbtCompat.getInt(positionsTag, "size");
            groupPositions.clear();
            for (int i = 0; i < size; i++) {
                groupPositions.add(readPos(positionsTag, i));
            }
        }

        railBindings = new ArrayList<>();
        if (nbt.contains("railBindings")) {
            CompoundTag bindingsTag = NbtCompat.getCompound(nbt, "railBindings");
            int size = NbtCompat.getInt(bindingsTag, "size");
            for (int i = 0; i < size; i++) {
                if (bindingsTag.contains("b" + i)) {
                    railBindings.add(RailPhaseBinding.fromNbt(NbtCompat.getCompound(bindingsTag, "b" + i)));
                }
            }
        }

        normalizePhaseData();

        if (level != null && level .isClientSide() && isInGroup() && !railBindings.isEmpty()) {
            ClientRailGroupIndex.register(level, groupAnchor());
        }
    }

    @Override
    protected void saveAdditionalCompat(CompoundTag nbt) {
        int[] indicesArray = new int[phaseIndices.size()];
        for (int i = 0; i < phaseIndices.size(); i++) indicesArray[i] = phaseIndices.get(i);
        nbt.putIntArray("phaseIndices", indicesArray);
        nbt.putString("directionType", this.directionType.getName());
        nbt.putInt("countdownDisplayMode", this.countdownDisplayMode);
        nbt.putInt("countdownThreshold", this.countdownThreshold);
        nbt.putBoolean("showSeconds", this.showSeconds);
        nbt.putInt("fixedSeconds", this.fixedSeconds);
        nbt.putBoolean("pulseCountdown", this.pulseCountdown);
        nbt.putBoolean("countdownFullDisplay", this.countdownFullDisplay);
        nbt.putInt("halfDisplayRedFrom", this.halfDisplayRedFrom);
        nbt.putInt("halfDisplayGreenFrom", this.halfDisplayGreenFrom);
        nbt.putInt("countdownNumberMode", this.countdownNumberMode);
        nbt.putBoolean("hidePatternOnGreen", this.hidePatternOnGreen);
        nbt.putBoolean("yellowFlashAlways", this.yellowFlashAlways);

        if (groupId != null) {
            nbt.putString("groupId", groupId);
        }

        nbt.putInt("phaseCount", phaseCount);

        if (phaseTimes != null) {
            nbt.putIntArray("phaseTimes", phaseTimes);
        }

        if (phaseDelays != null) {
            nbt.putIntArray("phaseDelays", phaseDelays);
        }

        nbt.putInt("currentActivePhase", currentActivePhase);
        nbt.putInt("currentTick", currentTick);
        nbt.putBoolean("cycleActive", cycleActive);
        if (cycleStartGameTime != Long.MIN_VALUE) {
            nbt.putLong("cycleStartGameTime", cycleStartGameTime);
        }

        if (groupPositions != null && !groupPositions.isEmpty()) {
            CompoundTag positionsTag = new CompoundTag();
            positionsTag.putInt("size", groupPositions.size());
            for (int i = 0; i < groupPositions.size(); i++) {
                positionsTag.put("pos" + i, writePos(groupPositions.get(i)));
            }
            nbt.put("groupPositions", positionsTag);
        }

        if (railBindings != null && !railBindings.isEmpty()) {
            CompoundTag bindingsTag = new CompoundTag();
            bindingsTag.putInt("size", railBindings.size());
            for (int i = 0; i < railBindings.size(); i++) {
                bindingsTag.put("b" + i, railBindings.get(i).toNbt());
            }
            nbt.put("railBindings", bindingsTag);
        }

        super.saveAdditionalCompat(nbt);
    }

    private static CompoundTag writePos(BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("X", pos.getX());
        tag.putInt("Y", pos.getY());
        tag.putInt("Z", pos.getZ());
        return tag;
    }

    private static BlockPos readPos(CompoundTag positionsTag, int index) {
        Tag tag = positionsTag.get("pos" + index);
        if (tag instanceof IntArrayTag array) {
            int[] values = array.getAsIntArray();
            if (values.length >= 3) {
                return new BlockPos(values[0], values[1], values[2]);
            }
        }
        CompoundTag compound = NbtCompat.getCompound(positionsTag, "pos" + index);
        return new BlockPos(NbtCompat.getInt(compound, "X"), NbtCompat.getInt(compound, "Y"), NbtCompat.getInt(compound, "Z"));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return createUpdatePacket();
    }
    public void markDirtyAndUpdate() {
        setChanged();
        if (level != null && !level .isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), VersionServices.blocks().updateAll());
            pushUpdateToNearbyPlayers();
        }
    }

    private void pushUpdateToNearbyPlayers() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.isLoaded(worldPosition)) return;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5) > 96 * 96) {
                continue;
            }
            VersionServices.blocks().sendBlockEntityUpdate(player, serverLevel, worldPosition,
                    createUpdatePacket());
        }
    }

    public void markGroupStateDirty() {
        markDirtyAndUpdate();
    }

    public enum DirectionType {
        STRAIGHT_CIRCLE("straight"),
        STRAIGHT_ARROW("straight_arrow"),
        LEFT_TURN("left_turn"),
        RIGHT_TURN("right_turn"),
        TURN_AROUND("turn_around"),
        NON_MOTOR_VEHICLES("non_motor_vehicles"),
        NON_MOTOR_VEHICLES_LEFT_TURN("non_motor_vehicles_left_turn"),
        NON_MOTOR_VEHICLES_RIGHT_TURN("non_motor_vehicles_right_turn"),
        LANE_BOTTOM("lane_bottom"),
        LANE_BOTTOM_LEFT("lane_bottom_left"),
        LANE_BOTTOM_RIGHT("lane_bottom_right"),
        LANE_CLOSE("lane_close"),
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
