package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning6Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning6UpdatePacket {
    private final BlockPos pos;
    private final SignGuideIntersectionAdvanceWarning6Entity.Direction direction1;
    private final SignGuideIntersectionAdvanceWarning6Entity.Direction direction2;
    private final String text1;
    private final String text2;

    public SignGuideIntersectionAdvanceWarning6UpdatePacket(BlockPos pos, SignGuideIntersectionAdvanceWarning6Entity.Direction direction1, SignGuideIntersectionAdvanceWarning6Entity.Direction direction2, String text1, String text2) {
        this.pos = pos;
        this.direction1 = direction1;
        this.direction2 = direction2;
        this.text1 = text1;
        this.text2 = text2;
    }

    public SignGuideIntersectionAdvanceWarning6UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnumConstant(SignGuideIntersectionAdvanceWarning6Entity.Direction.class);
        this.direction2 = buf.readEnumConstant(SignGuideIntersectionAdvanceWarning6Entity.Direction.class);
        this.text1 = buf.readString();
        this.text2 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(direction1);
        buf.writeEnumConstant(direction2);
        buf.writeString(text1);
        buf.writeString(text2);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignGuideIntersectionAdvanceWarning6Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setDirection2(direction2);
                signEntity.setText1(text1);
                signEntity.setText2(text2);

                signEntity.markDirty();
            }
        }
    }
}