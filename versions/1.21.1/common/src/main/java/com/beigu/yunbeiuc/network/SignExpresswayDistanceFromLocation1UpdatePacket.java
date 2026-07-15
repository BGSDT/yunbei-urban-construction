package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation1Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayDistanceFromLocation1UpdatePacket {
    private final BlockPos pos;
    private final String text1;
    private final String text2;
    private final String text3;
    private final String length1;
    private final String length2;
    private final String length3;

    public SignExpresswayDistanceFromLocation1UpdatePacket(BlockPos pos, String text1, String text2, String text3, String length1, String length2, String length3) {
        this.pos = pos;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.length1 = length1;
        this.length2 = length2;
        this.length3 = length3;
    }

    public SignExpresswayDistanceFromLocation1UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readString();
        this.text2 = buf.readString();
        this.text3 = buf.readString();
        this.length1 = buf.readString();
        this.length2 = buf.readString();
        this.length3 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(text1);
        buf.writeString(text2);
        buf.writeString(text3);
        buf.writeString(length1);
        buf.writeString(length2);
        buf.writeString(length3);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayDistanceFromLocation1Entity signEntity) {
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setText3(text3);
                signEntity.setLength1(length1);
                signEntity.setLength2(length2);
                signEntity.setLength3(length3);

                signEntity.markDirty();
            }
        }
    }
}