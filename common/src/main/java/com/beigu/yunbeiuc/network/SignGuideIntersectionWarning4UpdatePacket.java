package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionWarning4UpdatePacket {
    private final BlockPos pos;
    private final SignGuideIntersectionWarning4Entity.Direction direction1;
    private String text1 = "";

    public SignGuideIntersectionWarning4UpdatePacket(BlockPos pos, SignGuideIntersectionWarning4Entity.Direction direction1, String text1) {
        this.pos = pos;
        this.direction1 = direction1;
        this.text1 = text1;
    }

    public SignGuideIntersectionWarning4UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnumConstant(SignGuideIntersectionWarning4Entity.Direction.class);
        this.text1 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(direction1);
        buf.writeString(text1);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignGuideIntersectionWarning4Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setText1(text1);

                signEntity.markDirty();
            }
        }
    }
}