package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning3Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

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

    public SignGuideIntersectionAdvanceWarning3UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readUtf();
        this.cnText2 = buf.readUtf();
        this.enText2 = buf.readUtf();
        this.cnText3 = buf.readUtf();
        this.enText3 = buf.readUtf();
        this.cnText4 = buf.readUtf();
        this.enText4 = buf.readUtf();
        this.cnText5 = buf.readUtf();
        this.enText5 = buf.readUtf();
        this.cnText6 = buf.readUtf();
        this.enText6 = buf.readUtf();
        this.cnText7 = buf.readUtf();
        this.enText7 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(text1);
        buf.writeUtf(cnText2);
        buf.writeUtf(enText2);
        buf.writeUtf(cnText3);
        buf.writeUtf(enText3);
        buf.writeUtf(cnText4);
        buf.writeUtf(enText4);
        buf.writeUtf(cnText5);
        buf.writeUtf(enText5);
        buf.writeUtf(cnText6);
        buf.writeUtf(enText6);
        buf.writeUtf(cnText7);
        buf.writeUtf(enText7);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
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

                signEntity.setChanged();
            }
        }
    }
}
