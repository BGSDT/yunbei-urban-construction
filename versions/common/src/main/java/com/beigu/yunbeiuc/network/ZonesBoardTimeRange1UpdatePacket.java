package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange1Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class ZonesBoardTimeRange1UpdatePacket {
    private final BlockPos pos;
    private String time1 = "";
    private String time2 = "";

    public ZonesBoardTimeRange1UpdatePacket(BlockPos pos, String time1, String time2) {
        this.pos = pos;
        this.time1 = time1;
        this.time2 = time2;
    }

    public ZonesBoardTimeRange1UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.time1 = buf.readUtf();
        this.time2 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(time1);
        buf.writeUtf(time2);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof ZonesBoardTimeRange1Entity signEntity) {
                signEntity.setTime1(time1);
                signEntity.setTime2(time2);

                signEntity.setChanged();
            }
        }
    }
}
