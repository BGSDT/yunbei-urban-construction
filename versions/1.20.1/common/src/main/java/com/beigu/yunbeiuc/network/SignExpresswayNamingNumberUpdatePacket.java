package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayNamingNumberEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayNamingNumberUpdatePacket {
    private final BlockPos pos;
    private final String expresswayNumber;
    private final String expresswayName;

    public SignExpresswayNamingNumberUpdatePacket(BlockPos pos, String expresswayNumber, String expresswayName) {
        this.pos = pos;
        this.expresswayNumber = expresswayNumber;
        this.expresswayName = expresswayName;
    }

    public SignExpresswayNamingNumberUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.expresswayNumber = buf.readString();
        this.expresswayName = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(expresswayNumber);
        buf.writeString(expresswayName);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayNamingNumberEntity signEntity) {
                signEntity.setExpresswayNumber(expresswayNumber);
                signEntity.setExpresswayName(expresswayName);

                signEntity.markDirty();
            }
        }
    }
}