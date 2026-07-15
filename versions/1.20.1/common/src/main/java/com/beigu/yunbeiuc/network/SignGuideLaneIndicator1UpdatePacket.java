package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideLaneIndicator1Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideLaneIndicator1UpdatePacket {
    private final BlockPos pos;
    private final SignGuideLaneIndicator1Entity.Direction direction1;
    private final SignGuideLaneIndicator1Entity.Direction direction2;
    private final SignGuideLaneIndicator1Entity.Direction direction3;
    private final SignGuideLaneIndicator1Entity.Direction direction4;

    public SignGuideLaneIndicator1UpdatePacket(BlockPos pos, SignGuideLaneIndicator1Entity.Direction direction1, SignGuideLaneIndicator1Entity.Direction direction2, SignGuideLaneIndicator1Entity.Direction direction3, SignGuideLaneIndicator1Entity.Direction direction4) {
        this.pos = pos;
        this.direction1 = direction1;
        this.direction2 = direction2;
        this.direction3 = direction3;
        this.direction4 = direction4;
    }

    public SignGuideLaneIndicator1UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnumConstant(SignGuideLaneIndicator1Entity.Direction.class);
        this.direction2 = buf.readEnumConstant(SignGuideLaneIndicator1Entity.Direction.class);
        this.direction3 = buf.readEnumConstant(SignGuideLaneIndicator1Entity.Direction.class);
        this.direction4 = buf.readEnumConstant(SignGuideLaneIndicator1Entity.Direction.class);
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(direction1);
        buf.writeEnumConstant(direction2);
        buf.writeEnumConstant(direction3);
        buf.writeEnumConstant(direction4);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignGuideLaneIndicator1Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setDirection2(direction2);
                signEntity.setDirection3(direction3);
                signEntity.setDirection4(direction4);
                signEntity.markDirty();
            }
        }
    }
}