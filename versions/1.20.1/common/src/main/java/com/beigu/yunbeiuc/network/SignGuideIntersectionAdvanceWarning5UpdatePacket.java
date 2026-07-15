package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning5Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning5UpdatePacket {
    private final BlockPos pos;
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String text4 = "";
    private float text1AndY = 0f;
    private float text2AndY = 0f;
    private float text3AndY = 0f;
    private float text4AndY = 0f;

    public SignGuideIntersectionAdvanceWarning5UpdatePacket(BlockPos pos, String text1, String text2, String text3, String text4, float text1AndY, float text2AndY, float text3AndY, float text4AndY) {
        this.pos = pos;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
        this.text1AndY = text1AndY;
        this.text2AndY = text2AndY;
        this.text3AndY = text3AndY;
        this.text4AndY = text4AndY;
    }

    public SignGuideIntersectionAdvanceWarning5UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readString();
        this.text2 = buf.readString();
        this.text3 = buf.readString();
        this.text4 = buf.readString();
        this.text1AndY = buf.readFloat();
        this.text2AndY = buf.readFloat();
        this.text3AndY = buf.readFloat();
        this.text4AndY = buf.readFloat();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(text1);
        buf.writeString(text2);
        buf.writeString(text3);
        buf.writeString(text4);
        buf.writeFloat(text1AndY);
        buf.writeFloat(text2AndY);
        buf.writeFloat(text3AndY);
        buf.writeFloat(text4AndY);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignGuideIntersectionAdvanceWarning5Entity signEntity) {
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setText3(text3);
                signEntity.setText4(text4);
                signEntity.setText1AndY(text1AndY);
                signEntity.setText2AndY(text2AndY);
                signEntity.setText3AndY(text3AndY);
                signEntity.setText4AndY(text4AndY);

                signEntity.markDirty();
            }
        }
    }
}