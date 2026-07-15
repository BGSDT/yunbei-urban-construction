package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayDirection3Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayDirection3UpdatePacket {
    private final BlockPos pos;
    private final SignExpresswayDirection3Entity.Expressway expressway1;
    private final String text1;
    private final String expresswayNumber1;

    public SignExpresswayDirection3UpdatePacket(BlockPos pos, SignExpresswayDirection3Entity.Expressway expressway1, String text1, String expresswayNumber1) {
        this.pos = pos;
        this.expressway1 = expressway1;
        this.text1 = text1;
        this.expresswayNumber1 = expresswayNumber1;
    }

    public SignExpresswayDirection3UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.expressway1 = buf.readEnumConstant(SignExpresswayDirection3Entity.Expressway.class);
        this.text1 = buf.readString();
        this.expresswayNumber1 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(expressway1);
        buf.writeString(text1);
        buf.writeString(expresswayNumber1);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayDirection3Entity signEntity) {
                signEntity.setExpressway1(expressway1);
                signEntity.setText1(text1);
                signEntity.setExpresswayNumber1(expresswayNumber1);

                signEntity.markDirty();
            }
        }
    }
}