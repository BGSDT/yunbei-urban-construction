package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation2Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayDistanceFromLocation2UpdatePacket {
    private final BlockPos pos;
    private final SignExpresswayDistanceFromLocation2Entity.Expressway expressway1;
    private final String text1;
    private final String expresswayNumber;
    private final String text3;
    private final String length1;
    private final String length2;
    private final String length3;

    public SignExpresswayDistanceFromLocation2UpdatePacket(BlockPos pos, SignExpresswayDistanceFromLocation2Entity.Expressway expressway1, String text1, String expresswayNumber, String text3, String length1, String length2, String length3) {
        this.pos = pos;
        this.expressway1 = expressway1;
        this.text1 = text1;
        this.expresswayNumber = expresswayNumber;
        this.text3 = text3;
        this.length1 = length1;
        this.length2 = length2;
        this.length3 = length3;
    }

    public SignExpresswayDistanceFromLocation2UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.expressway1 = buf.readEnum(SignExpresswayDistanceFromLocation2Entity.Expressway.class);
        this.text1 = buf.readUtf();
        this.expresswayNumber = buf.readUtf();
        this.text3 = buf.readUtf();
        this.length1 = buf.readUtf();
        this.length2 = buf.readUtf();
        this.length3 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(expressway1);
        buf.writeUtf(text1);
        buf.writeUtf(expresswayNumber);
        buf.writeUtf(text3);
        buf.writeUtf(length1);
        buf.writeUtf(length2);
        buf.writeUtf(length3);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayDistanceFromLocation2Entity signEntity) {
                signEntity.setExpressway1(expressway1);
                signEntity.setText1(text1);
                signEntity.setExpresswayNumber(expresswayNumber);
                signEntity.setText3(text3);
                signEntity.setLength1(length1);
                signEntity.setLength2(length2);
                signEntity.setLength3(length3);

                signEntity.setChanged();
            }
        }
    }
}
