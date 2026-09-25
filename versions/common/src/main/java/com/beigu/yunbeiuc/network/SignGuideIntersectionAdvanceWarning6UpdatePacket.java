package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignTurnDirection;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning6Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignGuideIntersectionAdvanceWarning6UpdatePacket {
    private final BlockPos pos;
    private final SignTurnDirection direction1;
    private final SignTurnDirection direction2;
    private final String text1;
    private final String text2;

    public SignGuideIntersectionAdvanceWarning6UpdatePacket(BlockPos pos, SignTurnDirection direction1, SignTurnDirection direction2, String text1, String text2) {
        this.pos = pos;
        this.direction1 = direction1;
        this.direction2 = direction2;
        this.text1 = text1;
        this.text2 = text2;
    }

    public SignGuideIntersectionAdvanceWarning6UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnum(SignTurnDirection.class);
        this.direction2 = buf.readEnum(SignTurnDirection.class);
        this.text1 = buf.readUtf();
        this.text2 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(direction1);
        buf.writeEnum(direction2);
        buf.writeUtf(text1);
        buf.writeUtf(text2);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignGuideIntersectionAdvanceWarning6Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setDirection2(direction2);
                signEntity.setText1(text1);
                signEntity.setText2(text2);

                signEntity.setChanged();
            }
        }
    }
}
