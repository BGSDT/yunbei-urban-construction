package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange2Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

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

    public ZonesBoardTimeRange2UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.time1 = buf.readUtf();
        this.time2 = buf.readUtf();
        this.time3 = buf.readUtf();
        this.time4 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(time1);
        buf.writeUtf(time2);
        buf.writeUtf(time3);
        buf.writeUtf(time4);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof ZonesBoardTimeRange2Entity signEntity) {
                signEntity.setTime1(time1);
                signEntity.setTime2(time2);
                signEntity.setTime3(time3);
                signEntity.setTime4(time4);

                signEntity.setChanged();
            }
        }
    }
}
