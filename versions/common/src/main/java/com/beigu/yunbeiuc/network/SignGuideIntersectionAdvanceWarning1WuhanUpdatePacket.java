package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket {
    private final BlockPos pos;
    private String text1 = "";
    private String text2 = "";
    private String cnText3 = "";
    private String enText3 = "";
    private String cnText4 = "";
    private String enText4 = "";
    private String cnText5 = "";
    private String enText5 = "";

    public SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket(BlockPos pos, String text1, String text2, String cnText3, String enText3, String cnText4, String enText4,  String cnText5, String enText5) {
        this.pos = pos;
        this.text1 = text1;
        this.text2 = text2;
        this.cnText3 = cnText3;
        this.enText3 = enText3;
        this.cnText4 = cnText4;
        this.enText4 = enText4;
        this.cnText5 = cnText5;
        this.enText5 = enText5;
    }

    public SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readUtf();
        this.text2 = buf.readUtf();
        this.cnText3 = buf.readUtf();
        this.enText3 = buf.readUtf();
        this.cnText4 = buf.readUtf();
        this.enText4 = buf.readUtf();
        this.cnText5 = buf.readUtf();
        this.enText5 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(text1);
        buf.writeUtf(text2);
        buf.writeUtf(cnText3);
        buf.writeUtf(enText3);
        buf.writeUtf(cnText4);
        buf.writeUtf(enText4);
        buf.writeUtf(cnText5);
        buf.writeUtf(enText5);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignGuideIntersectionAdvanceWarning1WuhanEntity signEntity) {
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setCnText3(cnText3);
                signEntity.setEnText3(enText3);
                signEntity.setCnText4(cnText4);
                signEntity.setEnText4(enText4);
                signEntity.setCnText5(cnText5);
                signEntity.setEnText5(enText5);

                signEntity.setChanged();
            }
        }
    }
}
