package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange1Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ZonesBoardTimeRange1UpdatePacket {
    private final BlockPos pos;
    private String time1 = "";
    private String time2 = "";

    public ZonesBoardTimeRange1UpdatePacket(BlockPos pos, String time1, String time2) {
        this.pos = pos;
        this.time1 = time1;
        this.time2 = time2;
    }

    public ZonesBoardTimeRange1UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.time1 = buf.readString();
        this.time2 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(time1);
        buf.writeString(time2);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof ZonesBoardTimeRange1Entity signEntity) {
                signEntity.setTime1(time1);
                signEntity.setTime2(time2);

                signEntity.markDirty();
            }
        }
    }
}