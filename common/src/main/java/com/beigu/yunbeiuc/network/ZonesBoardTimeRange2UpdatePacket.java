package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange2Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ZonesBoardTimeRange2UpdatePacket {
    private final BlockPos pos;
    private String time1 = "";
    private String time2 = "";
    private String time3 = "";
    private String time4 = "";

    public ZonesBoardTimeRange2UpdatePacket(BlockPos pos, String time1, String time2, String time3, String time4) {
        this.pos = pos;
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.time4 = time4;
    }

    public ZonesBoardTimeRange2UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.time1 = buf.readString();
        this.time2 = buf.readString();
        this.time3 = buf.readString();
        this.time4 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(time1);
        buf.writeString(time2);
        buf.writeString(time3);
        buf.writeString(time4);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof ZonesBoardTimeRange2Entity signEntity) {
                signEntity.setTime1(time1);
                signEntity.setTime2(time2);
                signEntity.setTime3(time3);
                signEntity.setTime4(time4);

                signEntity.markDirty();
            }
        }
    }
}