package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation3Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayDistanceFromLocation3UpdatePacket {
    private final BlockPos pos;
    private final String text1;
    private final String text2;

    public SignExpresswayDistanceFromLocation3UpdatePacket(BlockPos pos, String text1, String text2) {
        this.pos = pos;
        this.text1 = text1;
        this.text2 = text2;
    }

    public SignExpresswayDistanceFromLocation3UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readUtf();
        this.text2 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(text1);
        buf.writeUtf(text2);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayDistanceFromLocation3Entity signEntity) {
                signEntity.setText1(text1);
                signEntity.setText2(text2);

                signEntity.setChanged();
            }
        }
    }
}
