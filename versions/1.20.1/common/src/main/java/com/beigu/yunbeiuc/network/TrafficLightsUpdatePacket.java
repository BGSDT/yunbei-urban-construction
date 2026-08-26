package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrafficLightsUpdatePacket {
    private final BlockPos pos;
    private final int[] phaseIndices;
    private final TrafficLightsBlockEntity.DirectionType directionType;

    public TrafficLightsUpdatePacket(BlockPos pos, int[] phaseIndices, TrafficLightsBlockEntity.DirectionType directionType) {
        this.pos = pos;
        this.phaseIndices = phaseIndices;
        this.directionType = directionType;
    }

    public TrafficLightsUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.phaseIndices = buf.readIntArray();
        this.directionType = TrafficLightsBlockEntity.DirectionType.fromName(buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeIntArray(phaseIndices);
        buf.writeString(directionType.getName());
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof TrafficLightsBlockEntity entity) {
                List<Integer> indices = Arrays.stream(phaseIndices).boxed().collect(Collectors.toList());
                entity.setPhaseIndices(indices, player);
                entity.setDirectionType(directionType);
                entity.markDirty();
            }
        }
    }
}
