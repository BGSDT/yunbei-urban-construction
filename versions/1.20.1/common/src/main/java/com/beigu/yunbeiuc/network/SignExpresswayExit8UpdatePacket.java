package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayExit8Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayExit8UpdatePacket {
    private final BlockPos pos;
    private final SignExpresswayExit8Entity.Direction direction1;
    private final SignExpresswayExit8Entity.Direction direction2;
    private final SignExpresswayExit8Entity.Expressway expressway1;
    private final SignExpresswayExit8Entity.Expressway expressway2;
    private final String text1;
    private final String text2;
    private final String expresswayNumber1;
    private final String expresswayNumber2;
    private final String exitNumber;

    public SignExpresswayExit8UpdatePacket(BlockPos pos, SignExpresswayExit8Entity.Direction direction1, SignExpresswayExit8Entity.Direction direction2, SignExpresswayExit8Entity.Expressway expressway1, SignExpresswayExit8Entity.Expressway expressway2, String text1, String text2, String expresswayNumber1, String expresswayNumber2, String exitNumber) {
        this.pos = pos;
        this.direction1 = direction1;
        this.direction2 = direction2;
        this.expressway1 = expressway1;
        this.expressway2 = expressway2;
        this.text1 = text1;
        this.text2 = text2;
        this.expresswayNumber1 = expresswayNumber1;
        this.expresswayNumber2 = expresswayNumber2;
        this.exitNumber = exitNumber;
    }

    public SignExpresswayExit8UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnumConstant(SignExpresswayExit8Entity.Direction.class);
        this.direction2 = buf.readEnumConstant(SignExpresswayExit8Entity.Direction.class);
        this.expressway1 = buf.readEnumConstant(SignExpresswayExit8Entity.Expressway.class);
        this.expressway2 = buf.readEnumConstant(SignExpresswayExit8Entity.Expressway.class);
        this.text1 = buf.readString();
        this.text2 = buf.readString();
        this.expresswayNumber1 = buf.readString();
        this.expresswayNumber2 = buf.readString();
        this.exitNumber = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(direction1);
        buf.writeEnumConstant(direction2);
        buf.writeEnumConstant(expressway1);
        buf.writeEnumConstant(expressway2);
        buf.writeString(text1);
        buf.writeString(text2);
        buf.writeString(expresswayNumber1);
        buf.writeString(expresswayNumber2);
        buf.writeString(exitNumber);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayExit8Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setDirection2(direction2);
                signEntity.setExpressway1(expressway1);
                signEntity.setExpressway2(expressway2);
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setExpresswayNumber1(expresswayNumber1);
                signEntity.setExpresswayNumber2(expresswayNumber2);
                signEntity.setExitNumber(exitNumber);

                signEntity.markDirty();
            }
        }
    }
}