package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning7Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning7UpdatePacket {
    private final BlockPos pos;
    private final SignGuideIntersectionAdvanceWarning7Entity.Direction direction1;
    private final SignGuideIntersectionAdvanceWarning7Entity.Direction direction2;
    private final SignGuideIntersectionAdvanceWarning7Entity.Direction direction3;
    
    private final String text1;
    private final String text2;
    private final String text3;

    public SignGuideIntersectionAdvanceWarning7UpdatePacket(BlockPos pos, SignGuideIntersectionAdvanceWarning7Entity.Direction direction1, SignGuideIntersectionAdvanceWarning7Entity.Direction direction2, SignGuideIntersectionAdvanceWarning7Entity.Direction direction3,String text1, String text2, String text3) {
        this.pos = pos;
        this.direction1 = direction1;
        this.direction2 = direction2;
        this.direction3 = direction3;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
    }

    public SignGuideIntersectionAdvanceWarning7UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnumConstant(SignGuideIntersectionAdvanceWarning7Entity.Direction.class);
        this.direction2 = buf.readEnumConstant(SignGuideIntersectionAdvanceWarning7Entity.Direction.class);
        this.direction3 = buf.readEnumConstant(SignGuideIntersectionAdvanceWarning7Entity.Direction.class);
        this.text1 = buf.readString();
        this.text2 = buf.readString();
        this.text3 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(direction1);
        buf.writeEnumConstant(direction2);
        buf.writeEnumConstant(direction3);
        buf.writeString(text1);
        buf.writeString(text2);
        buf.writeString(text3);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignGuideIntersectionAdvanceWarning7Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setDirection2(direction2);
                signEntity.setDirection3(direction3);
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setText3(text3);

                signEntity.markDirty();
            }
        }
    }
}