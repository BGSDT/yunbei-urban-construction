package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation4Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayDistanceFromLocation4UpdatePacket {
    private final BlockPos pos;
    private final String text1;
    private final String text2;
    private final String text3;
    private final SignExpresswayDistanceFromLocation4Entity.RoadType roadType1;
    private final SignExpresswayDistanceFromLocation4Entity.RoadType roadType2;
    private final SignExpresswayDistanceFromLocation4Entity.RoadType roadType3;
    private final String length1;
    private final String length2;
    private final String length3;

    public SignExpresswayDistanceFromLocation4UpdatePacket(BlockPos pos, String text1, String text2, String text3, SignExpresswayDistanceFromLocation4Entity.RoadType roadType1, SignExpresswayDistanceFromLocation4Entity.RoadType roadType2, SignExpresswayDistanceFromLocation4Entity.RoadType roadType3, String length1, String length2, String length3) {
        this.pos = pos;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.roadType1 = roadType1;
        this.roadType2 = roadType2;
        this.roadType3 = roadType3;
        this.length1 = length1;
        this.length2 = length2;
        this.length3 = length3;
    }

    public SignExpresswayDistanceFromLocation4UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readUtf();
        this.text2 = buf.readUtf();
        this.text3 = buf.readUtf();
        this.roadType1 = buf.readEnum(SignExpresswayDistanceFromLocation4Entity.RoadType.class);
        this.roadType2 = buf.readEnum(SignExpresswayDistanceFromLocation4Entity.RoadType.class);
        this.roadType3 = buf.readEnum(SignExpresswayDistanceFromLocation4Entity.RoadType.class);
        this.length1 = buf.readUtf();
        this.length2 = buf.readUtf();
        this.length3 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(text1);
        buf.writeUtf(text2);
        buf.writeUtf(text3);
        buf.writeEnum(roadType1);
        buf.writeEnum(roadType2);
        buf.writeEnum(roadType3);
        buf.writeUtf(length1);
        buf.writeUtf(length2);
        buf.writeUtf(length3);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayDistanceFromLocation4Entity signEntity) {
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setText3(text3);
                signEntity.setRoadType1(roadType1);
                signEntity.setRoadType2(roadType2);
                signEntity.setRoadType3(roadType3);
                signEntity.setLength1(length1);
                signEntity.setLength2(length2);
                signEntity.setLength3(length3);

                signEntity.setChanged();
            }
        }
    }
}
