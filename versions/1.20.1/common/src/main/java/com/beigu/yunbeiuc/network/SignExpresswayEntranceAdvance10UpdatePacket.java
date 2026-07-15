package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance10Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayEntranceAdvance10UpdatePacket {
    private final BlockPos pos;
    private final SignExpresswayEntranceAdvance10Entity.Expressway expressway1;
    private final SignExpresswayEntranceAdvance10Entity.Expressway expressway2;
    private final String expresswayNumber1;
    private final String expresswayNumber2;
    private final String text1;
    private final String text2;
    public SignExpresswayEntranceAdvance10UpdatePacket(BlockPos pos, SignExpresswayEntranceAdvance10Entity.Expressway expressway1, SignExpresswayEntranceAdvance10Entity.Expressway expressway2, String expresswayNumber1, String expresswayNumber2, String text1, String text2) {
        this.pos = pos;
        this.expressway1 = expressway1;
        this.expressway2 = expressway2;
        this.expresswayNumber1 = expresswayNumber1;
        this.expresswayNumber2 = expresswayNumber2;
        this.text1 = text1;
        this.text2 = text2;
    }

    public SignExpresswayEntranceAdvance10UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.expressway1 = buf.readEnumConstant(SignExpresswayEntranceAdvance10Entity.Expressway.class);
        this.expressway2 = buf.readEnumConstant(SignExpresswayEntranceAdvance10Entity.Expressway.class);
        this.expresswayNumber1 = buf.readString();
        this.expresswayNumber2 = buf.readString();
        this.text1 = buf.readString();
        this.text2 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(expressway1);
        buf.writeEnumConstant(expressway2);
        buf.writeString(expresswayNumber1);
        buf.writeString(expresswayNumber2);
        buf.writeString(text1);
        buf.writeString(text2);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayEntranceAdvance10Entity signEntity) {
                signEntity.setExpressway1(expressway1);
                signEntity.setExpressway2(expressway2);
                signEntity.setExpresswayNumber1(expresswayNumber1);
                signEntity.setExpresswayNumber2(expresswayNumber2);
                signEntity.setText1(text1);
                signEntity.setText2(text2);

                signEntity.markDirty();
            }
        }
    }
}