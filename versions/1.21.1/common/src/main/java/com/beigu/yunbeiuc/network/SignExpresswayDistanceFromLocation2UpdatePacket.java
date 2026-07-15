package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation2Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

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

    public SignExpresswayDistanceFromLocation2UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.expressway1 = buf.readEnumConstant(SignExpresswayDistanceFromLocation2Entity.Expressway.class);
        this.text1 = buf.readString();
        this.expresswayNumber = buf.readString();
        this.text3 = buf.readString();
        this.length1 = buf.readString();
        this.length2 = buf.readString();
        this.length3 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(expressway1);
        buf.writeString(text1);
        buf.writeString(expresswayNumber);
        buf.writeString(text3);
        buf.writeString(length1);
        buf.writeString(length2);
        buf.writeString(length3);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayDistanceFromLocation2Entity signEntity) {
                signEntity.setExpressway1(expressway1);
                signEntity.setText1(text1);
                signEntity.setExpresswayNumber(expresswayNumber);
                signEntity.setText3(text3);
                signEntity.setLength1(length1);
                signEntity.setLength2(length2);
                signEntity.setLength3(length3);

                signEntity.markDirty();
            }
        }
    }
}