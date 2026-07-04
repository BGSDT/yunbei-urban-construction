package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance4Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayEntranceAdvance4UpdatePacket {
    private final BlockPos pos;
    private final SignExpresswayEntranceAdvance4Entity.Direction direction1;
    private final SignExpresswayEntranceAdvance4Entity.Expressway expressway1;
    private final String text1;
    private final String expresswayNumber1;

    public SignExpresswayEntranceAdvance4UpdatePacket(BlockPos pos, SignExpresswayEntranceAdvance4Entity.Direction direction1, SignExpresswayEntranceAdvance4Entity.Expressway expressway1, String text1, String expresswayNumber1) {
        this.pos = pos;
        this.direction1 = direction1;
        this.expressway1 = expressway1;
        this.text1 = text1;
        this.expresswayNumber1 = expresswayNumber1;
    }

    public SignExpresswayEntranceAdvance4UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnumConstant(SignExpresswayEntranceAdvance4Entity.Direction.class);
        this.expressway1 = buf.readEnumConstant(SignExpresswayEntranceAdvance4Entity.Expressway.class);
        this.text1 = buf.readString();
        this.expresswayNumber1 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(direction1);
        buf.writeEnumConstant(expressway1);
        buf.writeString(text1);
        buf.writeString(expresswayNumber1);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayEntranceAdvance4Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setExpressway1(expressway1);
                signEntity.setText1(text1);
                signEntity.setExpresswayNumber1(expresswayNumber1);

                signEntity.markDirty();
            }
        }
    }
}