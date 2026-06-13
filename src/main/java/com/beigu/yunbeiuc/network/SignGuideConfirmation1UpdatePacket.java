package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideConfirmation1Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideConfirmation1UpdatePacket {
    private final BlockPos pos;
    private final SignGuideConfirmation1Entity.Unit unit1;
    private final SignGuideConfirmation1Entity.Unit unit2;
    private final SignGuideConfirmation1Entity.Unit unit3;
    private final String text1;
    private final String text2;
    private final String text3;
    private final String length1;
    private final String length2;
    private final String length3;

    public SignGuideConfirmation1UpdatePacket(BlockPos pos, SignGuideConfirmation1Entity.Unit unit1, SignGuideConfirmation1Entity.Unit unit2, SignGuideConfirmation1Entity.Unit unit3, String text1, String text2, String text3, String length1, String length2, String length3) {
        this.pos = pos;
        this.unit1 = unit1;
        this.unit2 = unit2;
        this.unit3 = unit3;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.length1 = length1;
        this.length2 = length2;
        this.length3 = length3;
    }

    public SignGuideConfirmation1UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.unit1 = buf.readEnumConstant(SignGuideConfirmation1Entity.Unit.class);
        this.unit2 = buf.readEnumConstant(SignGuideConfirmation1Entity.Unit.class);
        this.unit3 = buf.readEnumConstant(SignGuideConfirmation1Entity.Unit.class);
        this.text1 = buf.readString();
        this.text2 = buf.readString();
        this.text3 = buf.readString();
        this.length1 = buf.readString();
        this.length2 = buf.readString();
        this.length3 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(unit1);
        buf.writeEnumConstant(unit2);
        buf.writeEnumConstant(unit3);
        buf.writeString(text1);
        buf.writeString(text2);
        buf.writeString(text3);
        buf.writeString(length1);
        buf.writeString(length2);
        buf.writeString(length3);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignGuideConfirmation1Entity signEntity) {
                signEntity.setUnit1(unit1);
                signEntity.setUnit2(unit2);
                signEntity.setUnit3(unit3);
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setText3(text3);
                signEntity.setLength1(length1);
                signEntity.setLength2(length2);
                signEntity.setLength3(length3);

                signEntity.markDirty();
            }
        }
    }
}