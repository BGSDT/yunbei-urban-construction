package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

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

    public SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readString();
        this.text2 = buf.readString();
        this.cnText3 = buf.readString();
        this.enText3 = buf.readString();
        this.cnText4 = buf.readString();
        this.enText4 = buf.readString();
        this.cnText5 = buf.readString();
        this.enText5 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(text1);
        buf.writeString(text2);
        buf.writeString(cnText3);
        buf.writeString(enText3);
        buf.writeString(cnText4);
        buf.writeString(enText4);
        buf.writeString(cnText5);
        buf.writeString(enText5);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignGuideIntersectionAdvanceWarning1WuhanEntity signEntity) {
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setCnText3(cnText3);
                signEntity.setEnText3(enText3);
                signEntity.setCnText4(cnText4);
                signEntity.setEnText4(enText4);
                signEntity.setCnText5(cnText5);
                signEntity.setEnText5(enText5);

                signEntity.markDirty();
            }
        }
    }
}
