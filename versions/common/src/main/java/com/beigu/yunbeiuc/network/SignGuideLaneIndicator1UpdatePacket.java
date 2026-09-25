package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideLaneIndicator1Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignGuideLaneIndicator1UpdatePacket {
    private final BlockPos pos;
    private final SignGuideLaneIndicator1Entity.ArrowDirection direction1;
    private final SignGuideLaneIndicator1Entity.ArrowDirection direction2;
    private final SignGuideLaneIndicator1Entity.ArrowDirection direction3;
    private final SignGuideLaneIndicator1Entity.ArrowDirection direction4;

    public SignGuideLaneIndicator1UpdatePacket(BlockPos pos, SignGuideLaneIndicator1Entity.ArrowDirection direction1, SignGuideLaneIndicator1Entity.ArrowDirection direction2, SignGuideLaneIndicator1Entity.ArrowDirection direction3, SignGuideLaneIndicator1Entity.ArrowDirection direction4) {
        this.pos = pos;
        this.direction1 = direction1;
        this.direction2 = direction2;
        this.direction3 = direction3;
        this.direction4 = direction4;
    }

    public SignGuideLaneIndicator1UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnum(SignGuideLaneIndicator1Entity.ArrowDirection.class);
        this.direction2 = buf.readEnum(SignGuideLaneIndicator1Entity.ArrowDirection.class);
        this.direction3 = buf.readEnum(SignGuideLaneIndicator1Entity.ArrowDirection.class);
        this.direction4 = buf.readEnum(SignGuideLaneIndicator1Entity.ArrowDirection.class);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(direction1);
        buf.writeEnum(direction2);
        buf.writeEnum(direction3);
        buf.writeEnum(direction4);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignGuideLaneIndicator1Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setDirection2(direction2);
                signEntity.setDirection3(direction3);
                signEntity.setDirection4(direction4);
                signEntity.setChanged();
            }
        }
    }
}
