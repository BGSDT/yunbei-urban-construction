package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation5Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayDistanceFromLocation5UpdatePacket {
    private final BlockPos pos;
    private final String text1;
    private final String text2;
    private final String text3;
    private final String length1;
    private final String length2;
    private final String length3;
    private final SignExpresswayDistanceFromLocation5Entity.Expressway expressway1;
    private final SignExpresswayDistanceFromLocation5Entity.Expressway expressway2;
    private final SignExpresswayDistanceFromLocation5Entity.Expressway expressway3;
    private final String expresswayNumber1;
    private final String expresswayNumber2;
    private final String expresswayNumber3;

    public SignExpresswayDistanceFromLocation5UpdatePacket(BlockPos pos, String text1, String text2, String text3, String length1, String length2, String length3, SignExpresswayDistanceFromLocation5Entity.Expressway expressway1, SignExpresswayDistanceFromLocation5Entity.Expressway expressway2, SignExpresswayDistanceFromLocation5Entity.Expressway expressway3, String expresswayNumber1, String expresswayNumber2, String expresswayNumber3) {
        this.pos = pos;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.length1 = length1;
        this.length2 = length2;
        this.length3 = length3;
        this.expressway1 = expressway1;
        this.expressway2 = expressway2;
        this.expressway3 = expressway3;
        this.expresswayNumber1 = expresswayNumber1;
        this.expresswayNumber2 = expresswayNumber2;
        this.expresswayNumber3 = expresswayNumber3;
    }

    public SignExpresswayDistanceFromLocation5UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readUtf();
        this.text2 = buf.readUtf();
        this.text3 = buf.readUtf();
        this.length1 = buf.readUtf();
        this.length2 = buf.readUtf();
        this.length3 = buf.readUtf();
        this.expressway1 = buf.readEnum(SignExpresswayDistanceFromLocation5Entity.Expressway.class);
        this.expressway2 = buf.readEnum(SignExpresswayDistanceFromLocation5Entity.Expressway.class);
        this.expressway3 = buf.readEnum(SignExpresswayDistanceFromLocation5Entity.Expressway.class);
        this.expresswayNumber1 = buf.readUtf();
        this.expresswayNumber2 = buf.readUtf();
        this.expresswayNumber3 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(text1);
        buf.writeUtf(text2);
        buf.writeUtf(text3);
        buf.writeUtf(length1);
        buf.writeUtf(length2);
        buf.writeUtf(length3);
        buf.writeEnum(expressway1);
        buf.writeEnum(expressway2);
        buf.writeEnum(expressway3);
        buf.writeUtf(expresswayNumber1);
        buf.writeUtf(expresswayNumber2);
        buf.writeUtf(expresswayNumber3);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayDistanceFromLocation5Entity signEntity) {
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setText3(text3);
                signEntity.setLength1(length1);
                signEntity.setLength2(length2);
                signEntity.setLength3(length3);
                signEntity.setExpressway1(expressway1);
                signEntity.setExpressway2(expressway2);
                signEntity.setExpressway3(expressway3);
                signEntity.setExpresswayNumber1(expresswayNumber1);
                signEntity.setExpresswayNumber2(expresswayNumber2);
                signEntity.setExpresswayNumber3(expresswayNumber3);

                signEntity.setChanged();
            }
        }
    }
}
