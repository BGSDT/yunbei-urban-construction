package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignCompassDirection;
import com.beigu.yunbeiuc.entity.SignExpresswayExit8Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayExit8UpdatePacket {
    private final BlockPos pos;
    private final SignCompassDirection direction1;
    private final SignCompassDirection direction2;
    private final SignExpresswayExit8Entity.Expressway expressway1;
    private final SignExpresswayExit8Entity.Expressway expressway2;
    private final String text1;
    private final String text2;
    private final String expresswayNumber1;
    private final String expresswayNumber2;
    private final String exitNumber;

    public SignExpresswayExit8UpdatePacket(BlockPos pos, SignCompassDirection direction1, SignCompassDirection direction2, SignExpresswayExit8Entity.Expressway expressway1, SignExpresswayExit8Entity.Expressway expressway2, String text1, String text2, String expresswayNumber1, String expresswayNumber2, String exitNumber) {
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

    public SignExpresswayExit8UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnum(SignCompassDirection.class);
        this.direction2 = buf.readEnum(SignCompassDirection.class);
        this.expressway1 = buf.readEnum(SignExpresswayExit8Entity.Expressway.class);
        this.expressway2 = buf.readEnum(SignExpresswayExit8Entity.Expressway.class);
        this.text1 = buf.readUtf();
        this.text2 = buf.readUtf();
        this.expresswayNumber1 = buf.readUtf();
        this.expresswayNumber2 = buf.readUtf();
        this.exitNumber = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(direction1);
        buf.writeEnum(direction2);
        buf.writeEnum(expressway1);
        buf.writeEnum(expressway2);
        buf.writeUtf(text1);
        buf.writeUtf(text2);
        buf.writeUtf(expresswayNumber1);
        buf.writeUtf(expresswayNumber2);
        buf.writeUtf(exitNumber);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
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

                signEntity.setChanged();
            }
        }
    }
}
