package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance7Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayEntranceAdvance7UpdatePacket {
    private final BlockPos pos;
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";

    public SignExpresswayEntranceAdvance7UpdatePacket(BlockPos pos, String text1, String text2, String text3) {
        this.pos = pos;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
    }

    public SignExpresswayEntranceAdvance7UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readString();
        this.text2 = buf.readString();
        this.text3 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(text1);
        buf.writeString(text2);
        buf.writeString(text3);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayEntranceAdvance7Entity signEntity) {
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setText3(text3);

                signEntity.markDirty();
            }
        }
    }
}