package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.network.NetworkCompat;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsGroupSyncPacket;

import com.beigu.yunbeiuc.api.mapper.BlockEntityMapper;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public class TrafficLightsBlockEntity extends BlockEntityMapper {
    private static final int MAX_PHASE_INDICES = 4;

    private List<Integer> phaseIndices = new ArrayList<>();
    private String groupId = null;
    private List<BlockPos> groupPositions = new ArrayList<>();

    private int[] phaseTimes = null;
    private int phaseCount = 0;

    /** 相位内进度（由 {@link #cycleStartGameTime} 与本地游戏刻推算，不再独立自增） */
    private int currentTick = 0;
    /** 当前相位（由 {@link #cycleStartGameTime} 与本地游戏刻推算，不再由"驱动灯"推送） */
    private int currentActivePhase = 0;
    private boolean cycleActive = false;
    /**
     * 整组共享的周期起点（相位 0 开始的游戏刻）。
     *
     * <p>每盏灯都用「本地游戏刻 - cycleStartGameTime」自行推算当前相位与相位内进度，
     * 不再由某一盏"驱动灯"把相位/进度推给组内其它灯。这样做的原因：
     * <ul>
     *   <li>读秒是在客户端方块实体上算出来的，而客户端方块实体不会自己 tick，
     *       只能等服务端每 10 刻推一次方块实体包。若读秒依赖被推来的 currentTick，
     *       客户端拿到的始终是"上一个包"的快照，数字就会一顿一顿地跳。
     *       改成游戏刻推算后，客户端用自己的本地游戏刻逐刻算出读秒，数字连续下降。</li>
     *   <li>每盏灯自己算相位 = "自己分清楚自己属于哪个相位"，不会因为某盏灯区块
     *       未加载、刚加载（旧存档进度）或 tick 顺序变化而把错误相位推给整组。</li>
     * </ul>
     * 只有相位 0 的起点这一个值需要在组内/客户端之间同步，且它每个周期才变一次。
     */
    private long cycleStartGameTime = Long.MIN_VALUE;
    /** 链接组状态（groupId / groupPositions / 时间表）自上次推送后是否又变化过 */
    private boolean groupStateDirty = false;

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

        // 旧存档没有周期起点时，以当前游戏刻作为起点补上（组内同步会把整组拉到同一个值）
        if (cycleStartGameTime == Long.MIN_VALUE) {
            cycleStartGameTime = level.getGameTime();
        }

        int previousPhase = currentActivePhase;
        refreshPhaseProgress();

        // 相位切换必须立刻把新起点推给客户端；其余每 10 刻心跳同步一次，
        // 用来纠正客户端本地游戏刻与服务端的细微漂移（读秒本身是客户端逐刻算的）。
        if (currentActivePhase != previousPhase || level.getGameTime() % 10L == 0L) {
            markDirtyAndUpdate();
        }

        // 组内共享周期起点与时间表；相位/进度由每盏灯自己按同一起点推算，无需逐刻推送。
        syncGroupClock();
        updateLightState();
    }

    /**
     * 按「本地游戏刻 - 周期起点」推算当前相位与相位内进度。
     *
     * <p>这是整个读秒/相位系统的唯一来源：服务端与客户端都调用它，
     * 客户端因此能在渲染时用本地游戏刻算出连续下降的读秒，不必依赖网络包。
     * 组内所有灯共用同一个 {@link #cycleStartGameTime}，所以推出来的相位必然一致。
     */
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
        refreshPhaseProgress();

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
            level.setBlock(worldPosition, currentState.setValue(TrafficLightsBlock.LIGHT_STATE, lightState), VersionServices.blocks().updateAll());
        }
    }

    /**
     * 把整组共享的周期起点与时间表同步给组内其它灯，并立即刷新它们的灯状态。
     *
     * <p>只同步"基准"，不同步"当前相位/进度"：相位与进度由每盏灯自己按
     * {@link #cycleStartGameTime} 与本地游戏刻推算（见 {@link #refreshPhaseProgress()}）。
     * 这样即使某盏灯所在区块未 tick、或刚从旧存档加载进来，它算出的相位也与整组一致，
     * 不会把旧进度推给整组。
     *
     * <p>仍然逐刻刷新成员的灯状态（{@code tl.updateLightState()}）：所在区块未 tick 的成员
     * 也需要持续得到正确的颜色，不能停在旧颜色上。
     *
     * <p>区块未加载的成员只跳过、不拆除链接组：区块未加载不代表方块被破坏
     * （被破坏的成员由 {@code TrafficLightsBlock#unloadGroupAt} 显式清理）。
     */
    private void syncGroupClock() {
        if (groupId == null || groupPositions.isEmpty() || level == null || level .isClientSide) return;

        for (BlockPos pos : groupPositions) {
            if (pos.equals(this.worldPosition)) continue;
            if (!level.isLoaded(pos)) continue;
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TrafficLightsBlockEntity tl) {
                // 时间表与周期起点以整组为准，避免成员残留旧值导致相位/时长不一致
                tl.phaseTimes = this.phaseTimes;
                tl.phaseCount = this.phaseCount;
                tl.cycleActive = this.cycleActive;
                tl.cycleStartGameTime = this.cycleStartGameTime;
                tl.updateLightState();
            } else {
                unloadGroup();
                return;
            }
        }
    }

    // ==================== 时间查询接口（秒数） ====================

    /**
     * 获取绿灯+闪烁的剩余秒数（只算「当前相位」，不跨相位叠加）。
     *
     * <p>本灯被分配到多个相位时，读秒只反映距离最近的那一次相位结束还有多久，
     * 不会把后面连续的绿灯相位时长累加进来。
     *
     * <p>向上取整计算，始终显示 1 到绿灯总秒数的完整序列，不会出现 0。
     */
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
        int yellowStartTick = totalTicks - YELLOW_DURATION;

        // 人行道灯：黄灯时间段视为绿灯闪烁，继续返回剩余秒数
        // 普通灯：黄灯时间段返回-1（由getYellowRemainingSeconds处理）
        if (currentTick >= yellowStartTick && !isPavementLight && !phaseIndices.contains((currentActivePhase + 1) % phaseCount)) {
            return -1;
        }

        // 只按「当前相位」计算：本灯被分配到多个相位时，绿灯剩余时间显示距离最近的那一次
        // 相位结束的时长，不再跨相位叠加。叠加会把后面连续的绿灯相位时间也算进来，
        // 读秒会明显大于当前相位的实际剩余时间（与灯色变化对不上）。
        long remainingTicks = totalTicks - currentTick;

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

        normalizePhaseData();
        refreshPhaseProgress();

        if (!phaseIndices.contains(currentActivePhase)) return -1;

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
     * 获取红灯剩余秒数 —— 距离「本灯所属相位的下一次放行」开始还有多久。
     *
     * <p>红灯读秒必须把「当前相位剩余」加上中间各个相位的时长一起累加，
     * 否则整组红灯都会显示当前相位的同一个剩余值（与各自相位无关）。
     *
     * <p>例：4 个相位各 40 秒，当前处在第 1 相位且还剩 37 秒，
     * 那么第 4 相位的灯应显示 37 + 40 + 40 = 117 秒，而不是 37。
     *
     * <p>本灯未分配到任何相位时保持旧行为（显示当前相位剩余），避免读秒消失。
     *
     * <p>向上取整计算，始终显示 1 到红灯总秒数的完整序列，不会出现 0。
     */
    public int getRedRemainingSeconds() {
        if (phaseTimes == null || phaseCount <= 0 || !cycleActive) return -1;

        normalizePhaseData();
        refreshPhaseProgress();

        if (phaseIndices.contains(currentActivePhase)) return -1;

        // 未分配相位：保留旧行为（当前相位剩余）
        if (phaseIndices.isEmpty()) {
            long fallbackTicks = (long) phaseTimes[currentActivePhase] * 20L - currentTick;
            if (fallbackTicks < 0L) fallbackTicks = 0L;
            return (int) ((fallbackTicks + 19L) / 20L);
        }

        // 从当前相位往后找本灯的下一次放行相位（最多循环一圈）
        int targetPhase = -1;
        for (int step = 1; step < phaseCount; step++) {
            int idx = (currentActivePhase + step) % phaseCount;
            if (phaseIndices.contains(idx)) {
                targetPhase = idx;
                break;
            }
        }
        if (targetPhase < 0) return -1;

        // 当前相位剩余 + 中间各个相位的完整时长
        long remainingTicks = (long) phaseTimes[currentActivePhase] * 20L - currentTick;
        for (int step = 1; step < phaseCount; step++) {
            int idx = (currentActivePhase + step) % phaseCount;
            if (idx == targetPhase) break;
            remainingTicks += (long) phaseTimes[idx] * 20L;
        }
        if (remainingTicks < 0L) remainingTicks = 0L;

        return (int) ((remainingTicks + 19L) / 20L);
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
        markGroupStateDirty();
        return true;
    }

    public void startCycle() {
        if (phaseTimes == null || phaseCount <= 0) return;
        this.cycleActive = true;
        // 整组共用同一个周期起点：setTimings 会在同一游戏刻对所有成员调用本方法，
        // 因此它们拿到的起点完全相同，之后各自推算出的相位必然一致。
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
        directionType = DirectionType.STRAIGHT_CIRCLE;
        cycleStartGameTime = Long.MIN_VALUE;
        markGroupStateDirty();
    }

    // ==================== 设置器 ====================

    public void setTimings(int phaseCount, int[] timings) {
        // phaseCount 以 timings.length 为准，防止两者不一致导致后续数组越界
        this.phaseTimes = timings;
        this.phaseCount = timings != null ? timings.length : 0;
        this.phaseIndices.clear();
        startCycle();
        markGroupStateDirty();
    }

    public void setGroup(String groupId, List<BlockPos> positions) {
        this.groupId = groupId;
        this.groupPositions = new ArrayList<>(positions);
        this.directionType = DirectionType.STRAIGHT_CIRCLE;
        // 重新链接后必须回到"已分组但未设置时间表"状态，否则残留的旧时间表会让
        // TrafficLightsBlock#use 直接打开主界面（含"相位预设"按钮）而不是时间设置界面。
        this.phaseTimes = null;
        this.phaseCount = 0;
        this.phaseIndices.clear();
        this.currentTick = 0;
        this.currentActivePhase = 0;
        this.cycleActive = false;
        this.cycleStartGameTime = Long.MIN_VALUE;
        if (level != null && !level .isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(TrafficLightsBlock.LIGHT_STATE)) {
                level.setBlock(worldPosition, state.setValue(TrafficLightsBlock.LIGHT_STATE, TrafficLightsBlock.LightState.RED), VersionServices.blocks().updateAll());
            }
        }
        markGroupStateDirty();
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
                level.setBlock(worldPosition, state.setValue(TrafficLightsBlock.LIGHT_STATE, color), VersionServices.blocks().updateAll());
            }
        }
        markGroupStateDirty();
        return true;
    }

    // ==================== 获取器 ====================

    public boolean hasTimings() {
        return phaseTimes != null && phaseCount > 0;
    }

    public boolean isInGroup() {
        return groupId != null;
    }

    /**
     * 客户端专用：应用 {@code TrafficLightsGroupSyncPacket} 推来的链接组状态。
     *
     * <p>1.16.5 / 1.17.1 的方块实体更新包会被客户端整包丢弃（详见
     * {@code TrafficLightsGroupSyncPacket}），必须靠这条通道把 {@code groupId}
     * 等状态补到客户端，否则客户端会误判为"未分组"而打开静态状态界面。
     */
    public void applyClientGroupSync(String groupId, List<BlockPos> groupPositions, int[] phaseTimes,
                                     List<Integer> phaseIndices, String directionType,
                                     boolean cycleActive, int currentActivePhase, long cycleStartGameTime,
                                     boolean showSeconds, int fixedSeconds,
                                     int countdownDisplayMode, int countdownThreshold) {
        this.groupId = groupId;
        this.groupPositions = groupPositions == null ? new ArrayList<>() : new ArrayList<>(groupPositions);
        this.phaseTimes = phaseTimes;
        this.phaseCount = phaseTimes != null ? phaseTimes.length : 0;
        this.phaseIndices = phaseIndices == null ? new ArrayList<>() : new ArrayList<>(phaseIndices);
        this.directionType = DirectionType.fromName(directionType);
        this.cycleActive = cycleActive;
        // 客户端拿周期起点，之后按本地游戏刻自行推算相位与读秒（读秒因此逐刻连续变化）
        this.cycleStartGameTime = cycleStartGameTime;
        this.currentActivePhase = currentActivePhase;
        this.currentTick = 0;
        refreshPhaseProgress();
        this.showSeconds = showSeconds;
        this.fixedSeconds = fixedSeconds;
        this.countdownDisplayMode = countdownDisplayMode;
        this.countdownThreshold = countdownThreshold;
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

    // ==================== NBT 读写 ====================

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
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
        // 周期起点（新格式）。旧存档没有该字段时置为未初始化，由 tick/refreshPhaseProgress
        // 以当前游戏刻补上，之后由组内同步（syncGroupClock）把整组拉到同一个值。
        this.cycleStartGameTime = nbt.contains("cycleStartGameTime")
                ? nbt.getLong("cycleStartGameTime")
                : Long.MIN_VALUE;

        if (nbt.contains("groupPositions")) {
            CompoundTag positionsTag = nbt.getCompound("groupPositions");
            int size = positionsTag.getInt("size");
            groupPositions.clear();
            for (int i = 0; i < size; i++) {
                groupPositions.add(readPos(positionsTag, i));
            }
        }

        // 兼容旧存档：校正 phaseCount 与 phaseTimes 长度，并钳制相位索引
        normalizePhaseData();
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

        super.saveAdditionalCompat(nbt);
    }

    /**
     * 版本中立的坐标序列化。
     *
     * <p>不能用 {@code NbtUtils.writeBlockPos}：1.16.5–1.20.1 写的是带 X/Y/Z 的复合标签，
     * 而 1.21 起改写成 {@code IntArrayTag([x,y,z])}，跨版本读写会对不上（坐标全部读成 0,0,0，
     * 链接组因此被判定为"成员已被破坏"）。这里固定使用复合标签格式，与旧存档一致。
     */
    private static CompoundTag writePos(BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("X", pos.getX());
        tag.putInt("Y", pos.getY());
        tag.putInt("Z", pos.getZ());
        return tag;
    }

    /** 读取坐标：兼容 X/Y/Z 复合标签，以及 1.21 上曾被写坏的 {@code IntArrayTag([x,y,z])}。 */
    private static BlockPos readPos(CompoundTag positionsTag, int index) {
        Tag tag = positionsTag.get("pos" + index);
        if (tag instanceof IntArrayTag array) {
            int[] values = array.getAsIntArray();
            if (values.length >= 3) {
                return new BlockPos(values[0], values[1], values[2]);
            }
        }
        CompoundTag compound = positionsTag.getCompound("pos" + index);
        return new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return createUpdatePacket();
    }
    public void markDirtyAndUpdate() {
        setChanged();
        if (level != null && !level .isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), VersionServices.blocks().updateAll());
            syncGroupStateToClients();
        }
    }

    /**
     * 标记链接组状态已变化，下一次 {@link #markDirtyAndUpdate()} 时把完整组状态推给客户端。
     *
     * <p>{@code markDirtyAndUpdate} 在 tick 中每 10 刻也会被调用（读秒同步），
     * 若每次都推组状态会白白占用带宽，因此用脏标记把推送限制在实际变化时。
     */
    public void markGroupStateDirty() {
        this.groupStateDirty = true;
        markDirtyAndUpdate();
    }

    /**
     * 把链接组状态推给正在追踪本区块的客户端。
     *
     * <p>只在 1.16.5 / 1.17.1 需要：这两个版本的方块实体更新包用 int 类型，
     * 模组方块实体只能填 {@code -1}，客户端会整包丢弃，导致
     * {@code groupId} / {@code phaseTimes} 永远同步不到客户端。
     * 1.18.2 起更新包携带 {@code BlockEntityType}，客户端按键取值并 {@code load}，
     * 不存在该问题，此时本方法直接返回，不额外占用带宽。
     */
    private void syncGroupStateToClients() {
        if (!groupStateDirty) return;
        if (!TrafficLightsGroupSync.isRequired()) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.isLoaded(worldPosition)) return;

        groupStateDirty = false;

        TrafficLightsGroupSyncPacket packet = new TrafficLightsGroupSyncPacket(
                worldPosition, groupId, groupPositions, phaseTimes, phaseIndices,
                directionType.getName(), cycleActive, currentActivePhase, cycleStartGameTime,
                showSeconds, fixedSeconds, countdownDisplayMode, countdownThreshold);

        for (ServerPlayer player : serverLevel.players()) {
            // 只发给距离足够近、且客户端已加载本方块所在区块的玩家
            if (player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5) > 96 * 96) {
                continue;
            }
            FriendlyByteBuf buf = NetworkCompat.newBuffer();
            packet.write(buf);
            NetworkCompat.sendToPlayer(player, ModMessages.SYNC_TRAFFIC_LIGHTS_GROUP, buf);
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
