package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * S2C：把红绿灯的链接组状态推给客户端。
 *
 * <p>存在的理由（1.16.5 / 1.17.1 专属问题）：这两个版本的
 * {@code ClientboundBlockEntityDataPacket} 用 int 表示类型，客户端
 * {@code ClientPacketListener#handleBlockEntityData} 只对原版白名单类型
 * （刷怪笼 / 告示牌 / 床 …）调用 {@code BlockEntity#load}，而模组方块实体
 * 只能填 {@code -1}，于是更新标签会被客户端**整包丢弃**——客户端方块实体的
 * {@code groupId} / {@code phaseTimes} 永远停在旧值。
 *
 * <p>后果：链接完（shift+右键）后立刻用魔杖右键红绿灯，客户端
 * {@code isInGroup()} 仍为 false，{@code TrafficLightsBlock#useCompat}
 * 于是打开静态状态界面，而不是时间序列界面。
 *
 * <p>1.18.2 起更新包改为携带 {@code BlockEntityType}，客户端按键取值并
 * {@code load}，不存在白名单丢弃问题，因此无需这条通道。
 */
public class TrafficLightsGroupSyncPacket {
    private final BlockPos pos;
    private final String groupId;
    private final List<BlockPos> groupPositions;
    private final int[] phaseTimes;
    private final List<Integer> phaseIndices;
    private final String directionType;
    private final boolean cycleActive;
    private final int currentActivePhase;
    private final int currentTick;
    private final boolean showSeconds;
    private final int fixedSeconds;
    private final int countdownDisplayMode;
    private final int countdownThreshold;

    public TrafficLightsGroupSyncPacket(BlockPos pos, String groupId, List<BlockPos> groupPositions,
                                        int[] phaseTimes, List<Integer> phaseIndices, String directionType,
                                        boolean cycleActive, int currentActivePhase, int currentTick,
                                        boolean showSeconds, int fixedSeconds,
                                        int countdownDisplayMode, int countdownThreshold) {
        this.pos = pos;
        this.groupId = groupId;
        this.groupPositions = groupPositions == null ? new ArrayList<>() : new ArrayList<>(groupPositions);
        this.phaseTimes = phaseTimes;
        this.phaseIndices = phaseIndices == null ? new ArrayList<>() : new ArrayList<>(phaseIndices);
        this.directionType = directionType;
        this.cycleActive = cycleActive;
        this.currentActivePhase = currentActivePhase;
        this.currentTick = currentTick;
        this.showSeconds = showSeconds;
        this.fixedSeconds = fixedSeconds;
        this.countdownDisplayMode = countdownDisplayMode;
        this.countdownThreshold = countdownThreshold;
    }

    public TrafficLightsGroupSyncPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.groupId = buf.readBoolean() ? buf.readUtf() : null;
        int size = buf.readVarInt();
        this.groupPositions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.groupPositions.add(buf.readBlockPos());
        }
        int timingCount = buf.readVarInt();
        if (timingCount > 0) {
            this.phaseTimes = new int[timingCount];
            for (int i = 0; i < timingCount; i++) {
                this.phaseTimes[i] = buf.readVarInt();
            }
        } else {
            this.phaseTimes = null;
        }
        int indexCount = buf.readVarInt();
        this.phaseIndices = new ArrayList<>();
        for (int i = 0; i < indexCount; i++) {
            this.phaseIndices.add(buf.readVarInt());
        }
        this.directionType = buf.readUtf();
        this.cycleActive = buf.readBoolean();
        this.currentActivePhase = buf.readVarInt();
        this.currentTick = buf.readVarInt();
        this.showSeconds = buf.readBoolean();
        this.fixedSeconds = buf.readVarInt();
        this.countdownDisplayMode = buf.readVarInt();
        this.countdownThreshold = buf.readVarInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(groupId != null);
        if (groupId != null) {
            buf.writeUtf(groupId);
        }
        buf.writeVarInt(groupPositions.size());
        for (BlockPos p : groupPositions) {
            buf.writeBlockPos(p);
        }
        buf.writeVarInt(phaseTimes == null ? 0 : phaseTimes.length);
        if (phaseTimes != null) {
            for (int t : phaseTimes) {
                buf.writeVarInt(t);
            }
        }
        buf.writeVarInt(phaseIndices.size());
        for (int idx : phaseIndices) {
            buf.writeVarInt(idx);
        }
        buf.writeUtf(directionType == null
                ? TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE.getName()
                : directionType);
        buf.writeBoolean(cycleActive);
        buf.writeVarInt(currentActivePhase);
        buf.writeVarInt(currentTick);
        buf.writeBoolean(showSeconds);
        buf.writeVarInt(fixedSeconds);
        buf.writeVarInt(countdownDisplayMode);
        buf.writeVarInt(countdownThreshold);
    }

    /** 客户端应用。只改客户端方块实体，不回发服务端。 */
    public void applyClient() {
        Level world = Minecraft.getInstance().level;
        if (world == null) return;
        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl) {
            tl.applyClientGroupSync(groupId, groupPositions, phaseTimes, phaseIndices, directionType,
                    cycleActive, currentActivePhase, currentTick,
                    showSeconds, fixedSeconds, countdownDisplayMode, countdownThreshold);
        }
    }
}
