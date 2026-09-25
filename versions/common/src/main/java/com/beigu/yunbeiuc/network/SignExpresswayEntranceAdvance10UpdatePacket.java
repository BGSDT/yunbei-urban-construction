package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance10Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayEntranceAdvance10UpdatePacket {
    private final BlockPos pos;
    private final SignExpresswayEntranceAdvance10Entity.Expressway expressway1;
    private final SignExpresswayEntranceAdvance10Entity.Expressway expressway2;
    private final String expresswayNumber1;
    private final String expresswayNumber2;
    private final String text1;
    private final String text2;
    public SignExpresswayEntranceAdvance10UpdatePacket(BlockPos pos, SignExpresswayEntranceAdvance10Entity.Expressway expressway1, SignExpresswayEntranceAdvance10Entity.Expressway expressway2, String expresswayNumber1, String expresswayNumber2, String text1, String text2) {
        this.pos = pos;
        this.expressway1 = expressway1;
        this.expressway2 = expressway2;
        this.expresswayNumber1 = expresswayNumber1;
        this.expresswayNumber2 = expresswayNumber2;
        this.text1 = text1;
        this.text2 = text2;
    }

    public SignExpresswayEntranceAdvance10UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.expressway1 = buf.readEnum(SignExpresswayEntranceAdvance10Entity.Expressway.class);
        this.expressway2 = buf.readEnum(SignExpresswayEntranceAdvance10Entity.Expressway.class);
        this.expresswayNumber1 = buf.readUtf();
        this.expresswayNumber2 = buf.readUtf();
        this.text1 = buf.readUtf();
        this.text2 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(expressway1);
        buf.writeEnum(expressway2);
        buf.writeUtf(expresswayNumber1);
        buf.writeUtf(expresswayNumber2);
        buf.writeUtf(text1);
        buf.writeUtf(text2);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayEntranceAdvance10Entity signEntity) {
                signEntity.setExpressway1(expressway1);
                signEntity.setExpressway2(expressway2);
                signEntity.setExpresswayNumber1(expresswayNumber1);
                signEntity.setExpresswayNumber2(expresswayNumber2);
                signEntity.setText1(text1);
                signEntity.setText2(text2);

                signEntity.setChanged();
            }
        }
    }
}
