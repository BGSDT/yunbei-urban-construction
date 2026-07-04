package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning3Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning3UpdatePacket {
    private final BlockPos pos;
    private String text1 = "";
    private String cnText2 = "";
    private String enText2 = "";
    private String cnText3 = "";
    private String enText3 = "";
    private String cnText4 = "";
    private String enText4 = "";
    private String cnText5 = "";
    private String enText5 = "";
    private String cnText6 = "";
    private String enText6 = "";
    private String cnText7 = "";
    private String enText7 = "";

    public SignGuideIntersectionAdvanceWarning3UpdatePacket(BlockPos pos, String text1, String cnText2, String enText2, String cnText3, String enText3, String cnText4, String enText4, String cnText5, String enText5, String cnText6, String enText6, String cnText7, String enText7) {
        this.pos = pos;
        this.text1 = text1;
        this.cnText2 = cnText2;
        this.enText2 = enText2;
        this.cnText3 = cnText3;
        this.enText3 = enText3;
        this.cnText4 = cnText4;
        this.enText4 = enText4;
        this.cnText5 = cnText5;
        this.enText5 = enText5;
        this.cnText6 = cnText6;
        this.enText6 = enText6;
        this.cnText7 = cnText7;
        this.enText7 = enText7;
    }

    public SignGuideIntersectionAdvanceWarning3UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readString();
        this.cnText2 = buf.readString();
        this.enText2 = buf.readString();
        this.cnText3 = buf.readString();
        this.enText3 = buf.readString();
        this.cnText4 = buf.readString();
        this.enText4 = buf.readString();
        this.cnText5 = buf.readString();
        this.enText5 = buf.readString();
        this.cnText6 = buf.readString();
        this.enText6 = buf.readString();
        this.cnText7 = buf.readString();
        this.enText7 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(text1);
        buf.writeString(cnText2);
        buf.writeString(enText2);
        buf.writeString(cnText3);
        buf.writeString(enText3);
        buf.writeString(cnText4);
        buf.writeString(enText4);
        buf.writeString(cnText5);
        buf.writeString(enText5);
        buf.writeString(cnText6);
        buf.writeString(enText6);
        buf.writeString(cnText7);
        buf.writeString(enText7);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignGuideIntersectionAdvanceWarning3Entity signEntity) {
                signEntity.setText1(text1);
                signEntity.setCnText2(cnText2);
                signEntity.setEnText2(enText2);
                signEntity.setCnText3(cnText3);
                signEntity.setEnText3(enText3);
                signEntity.setCnText4(cnText4);
                signEntity.setEnText4(enText4);
                signEntity.setCnText5(cnText5);
                signEntity.setEnText5(enText5);
                signEntity.setCnText6(cnText6);
                signEntity.setEnText6(enText6);
                signEntity.setCnText7(cnText7);
                signEntity.setEnText7(enText7);

                signEntity.markDirty();
            }
        }
    }
}