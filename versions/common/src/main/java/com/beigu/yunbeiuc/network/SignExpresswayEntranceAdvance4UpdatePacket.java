package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignCompassDirection;
import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance4Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayEntranceAdvance4UpdatePacket {
    private final BlockPos pos;
    private final SignCompassDirection direction1;
    private final SignExpresswayEntranceAdvance4Entity.Expressway expressway1;
    private final String text1;
    private final String expresswayNumber1;

    public SignExpresswayEntranceAdvance4UpdatePacket(BlockPos pos, SignCompassDirection direction1, SignExpresswayEntranceAdvance4Entity.Expressway expressway1, String text1, String expresswayNumber1) {
        this.pos = pos;
        this.direction1 = direction1;
        this.expressway1 = expressway1;
        this.text1 = text1;
        this.expresswayNumber1 = expresswayNumber1;
    }

    public SignExpresswayEntranceAdvance4UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.direction1 = buf.readEnum(SignCompassDirection.class);
        this.expressway1 = buf.readEnum(SignExpresswayEntranceAdvance4Entity.Expressway.class);
        this.text1 = buf.readUtf();
        this.expresswayNumber1 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(direction1);
        buf.writeEnum(expressway1);
        buf.writeUtf(text1);
        buf.writeUtf(expresswayNumber1);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayEntranceAdvance4Entity signEntity) {
                signEntity.setDirection1(direction1);
                signEntity.setExpressway1(expressway1);
                signEntity.setText1(text1);
                signEntity.setExpresswayNumber1(expresswayNumber1);

                signEntity.setChanged();
            }
        }
    }
}
