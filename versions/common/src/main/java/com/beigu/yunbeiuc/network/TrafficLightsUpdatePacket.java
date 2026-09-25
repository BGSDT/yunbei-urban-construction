package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrafficLightsUpdatePacket {
    private final BlockPos pos;
    private final int[] phaseIndices;
    private final TrafficLightsBlockEntity.DirectionType directionType;
    private final int countdownDisplayMode;
    private final int countdownThreshold;
    private final boolean showSeconds;

    public TrafficLightsUpdatePacket(BlockPos pos, int[] phaseIndices, TrafficLightsBlockEntity.DirectionType directionType, int countdownDisplayMode, int countdownThreshold, boolean showSeconds) {
        this.pos = pos;
        this.phaseIndices = phaseIndices;
        this.directionType = directionType;
        this.countdownDisplayMode = countdownDisplayMode;
        this.countdownThreshold = countdownThreshold;
        this.showSeconds = showSeconds;
    }

    public TrafficLightsUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.phaseIndices = buf.readVarIntArray();
        this.directionType = TrafficLightsBlockEntity.DirectionType.fromName(buf.readUtf());
        this.countdownDisplayMode = buf.readInt();
        this.countdownThreshold = buf.readInt();
        this.showSeconds = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeVarIntArray(phaseIndices);
        buf.writeUtf(directionType.getName());
        buf.writeInt(countdownDisplayMode);
        buf.writeInt(countdownThreshold);
        buf.writeBoolean(showSeconds);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof TrafficLightsBlockEntity entity) {
                List<Integer> indices = Arrays.stream(phaseIndices).boxed().collect(Collectors.toList());
                entity.setPhaseIndices(indices, player);
                entity.setDirectionType(directionType);
                entity.setCountdownDisplayMode(countdownDisplayMode);
                entity.setCountdownThreshold(countdownThreshold);
                entity.setShowSeconds(showSeconds);
                entity.setChanged();
            }
        }
    }
}
