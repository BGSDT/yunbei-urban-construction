package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayNamingNumberEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayNamingNumberUpdatePacket {
    private final BlockPos pos;
    private final String expresswayNumber;
    private final String expresswayName;

    public SignExpresswayNamingNumberUpdatePacket(BlockPos pos, String expresswayNumber, String expresswayName) {
        this.pos = pos;
        this.expresswayNumber = expresswayNumber;
        this.expresswayName = expresswayName;
    }

    public SignExpresswayNamingNumberUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.expresswayNumber = buf.readUtf();
        this.expresswayName = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(expresswayNumber);
        buf.writeUtf(expresswayName);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayNamingNumberEntity signEntity) {
                signEntity.setExpresswayNumber(expresswayNumber);
                signEntity.setExpresswayName(expresswayName);

                signEntity.setChanged();
            }
        }
    }
}
