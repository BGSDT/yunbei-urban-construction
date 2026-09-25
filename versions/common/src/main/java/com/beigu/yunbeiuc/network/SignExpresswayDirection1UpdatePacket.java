package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayDirection1Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayDirection1UpdatePacket {
    private final BlockPos pos;
    private final String text1;

    public SignExpresswayDirection1UpdatePacket(BlockPos pos, String text1) {
        this.pos = pos;
        this.text1 = text1;
    }

    public SignExpresswayDirection1UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(text1);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayDirection1Entity signEntity) {
                signEntity.setText1(text1);

                signEntity.setChanged();
            }
        }
    }
}
