package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance1Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayEntranceAdvance1UpdatePacket {
    private final BlockPos pos;
    private final SignExpresswayEntranceAdvance1Entity.Expressway expressway1;
    private final String text1;
    private final String text2;
    private final String expresswayNumber1;

    public SignExpresswayEntranceAdvance1UpdatePacket(BlockPos pos, SignExpresswayEntranceAdvance1Entity.Expressway expressway1, String text1, String text2, String expresswayNumber1) {
        this.pos = pos;
        this.expressway1 = expressway1;
        this.text1 = text1;
        this.text2 = text2;
        this.expresswayNumber1 = expresswayNumber1;
    }

    public SignExpresswayEntranceAdvance1UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.expressway1 = buf.readEnum(SignExpresswayEntranceAdvance1Entity.Expressway.class);
        this.text1 = buf.readUtf();
        this.text2 = buf.readUtf();
        this.expresswayNumber1 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(expressway1);
        buf.writeUtf(text1);
        buf.writeUtf(text2);
        buf.writeUtf(expresswayNumber1);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayEntranceAdvance1Entity signEntity) {
                signEntity.setExpressway1(expressway1);
                signEntity.setText1(text1);
                signEntity.setText2(text2);
                signEntity.setExpresswayNumber1(expresswayNumber1);

                signEntity.setChanged();
            }
        }
    }
}
