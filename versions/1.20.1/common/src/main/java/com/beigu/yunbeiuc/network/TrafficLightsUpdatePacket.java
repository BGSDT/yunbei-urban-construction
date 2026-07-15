package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class TrafficLightsUpdatePacket {
    private final BlockPos pos;
    private final int phaseIndex;
    private final TrafficLightsBlockEntity.DirectionType directionType;

    public TrafficLightsUpdatePacket(BlockPos pos, int phaseIndex, TrafficLightsBlockEntity.DirectionType directionType) {
        this.pos = pos;
        this.phaseIndex = phaseIndex;
        this.directionType = directionType;
    }

    public TrafficLightsUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.phaseIndex = buf.readInt();
        this.directionType = TrafficLightsBlockEntity.DirectionType.fromName(buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(phaseIndex);
        buf.writeString(directionType.getName());
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof TrafficLightsBlockEntity entity) {
                entity.setPhaseIndex(phaseIndex, player);
                entity.setDirectionType(directionType);
                entity.markDirty();
            }
        }
    }
}