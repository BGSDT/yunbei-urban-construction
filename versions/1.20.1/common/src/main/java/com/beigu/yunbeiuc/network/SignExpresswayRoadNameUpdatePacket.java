package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayRoadNameEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayRoadNameUpdatePacket {
    private final BlockPos pos;
    private final String text1;

    public SignExpresswayRoadNameUpdatePacket(BlockPos pos, String text1) {
        this.pos = pos;
        this.text1 = text1;
    }

    public SignExpresswayRoadNameUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(text1);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayRoadNameEntity signEntity) {
                signEntity.setText1(text1);

                signEntity.markDirty();
            }
        }
    }
}