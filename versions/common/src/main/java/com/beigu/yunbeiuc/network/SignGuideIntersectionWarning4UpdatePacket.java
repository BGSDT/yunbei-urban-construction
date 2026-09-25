package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignTurnDirection;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignGuideIntersectionWarning4UpdatePacket {
    private final BlockPos pos;
    private final SignTurnDirection direction1;
    private String text1 = "";

    public SignGuideIntersectionWarning4UpdatePacket(BlockPos pos, SignTurnDirection direction1, String text1) {
        this.pos = pos;
        this.direction1 = direction1;
        this.text1 = text1;
    }

    public SignGuideIntersectionWarning4UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnum(SignTurnDirection.class);
        this.text1 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(direction1);
        buf.writeUtf(text1);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignGuideIntersectionWarning4Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setText1(text1);

                signEntity.setChanged();
            }
        }
    }
}
