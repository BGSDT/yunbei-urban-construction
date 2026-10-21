package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

// S2C：把红绿灯的链接组状态推给客户端（1.16.5 / 1.17.1 专用，原因见 TrafficLightsGroupSync）。

public class TrafficLightsGroupSyncPacket {
    private final BlockPos pos;
    private final String groupId;
    private final List<BlockPos> groupPositions;
    private final int[] phaseTimes;
    private final List<Integer> phaseIndices;
    private final String directionType;
    private final boolean cycleActive;
    private final int currentActivePhase;
    private final long cycleStartGameTime;
    private final boolean showSeconds;
    private final int fixedSeconds;
    private final int countdownDisplayMode;
    private final int countdownThreshold;

    public TrafficLightsGroupSyncPacket(BlockPos pos, String groupId, List<BlockPos> groupPositions,
                                        int[] phaseTimes, List<Integer> phaseIndices, String directionType,
                                        boolean cycleActive, int currentActivePhase, long cycleStartGameTime,
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
        this.cycleStartGameTime = cycleStartGameTime;
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
        this.cycleStartGameTime = buf.readLong();
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
        buf.writeLong(cycleStartGameTime);
        buf.writeBoolean(showSeconds);
        buf.writeVarInt(fixedSeconds);
        buf.writeVarInt(countdownDisplayMode);
        buf.writeVarInt(countdownThreshold);
    }

    public void applyClient() {
        Level world = Minecraft.getInstance().level;
        if (world == null) return;
        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl) {
            tl.applyClientGroupSync(groupId, groupPositions, phaseTimes, phaseIndices, directionType,
                    cycleActive, currentActivePhase, cycleStartGameTime,
                    showSeconds, fixedSeconds, countdownDisplayMode, countdownThreshold);
        }
    }
}
