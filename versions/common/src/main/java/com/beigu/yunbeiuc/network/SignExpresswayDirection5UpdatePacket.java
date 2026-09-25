package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignCompassDirection;
import com.beigu.yunbeiuc.entity.SignExpresswayDirection5Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignExpresswayDirection5UpdatePacket {
    private final BlockPos pos;
    private final SignExpresswayDirection5Entity.Expressway expressway1;
    private final String text1;
    private final String expresswayNumber1;
    private final SignCompassDirection direction1;

    public SignExpresswayDirection5UpdatePacket(BlockPos pos, SignExpresswayDirection5Entity.Expressway expressway1, String text1, String expresswayNumber1, SignCompassDirection direction1) {
        this.pos = pos;
        this.expressway1 = expressway1;
        this.text1 = text1;
        this.expresswayNumber1 = expresswayNumber1;
        this.direction1 = direction1;
    }

    public SignExpresswayDirection5UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.expressway1 = buf.readEnum(SignExpresswayDirection5Entity.Expressway.class);
        this.text1 = buf.readUtf();
        this.expresswayNumber1 = buf.readUtf();
        this.direction1 = buf.readEnum(SignCompassDirection.class);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(expressway1);
        buf.writeUtf(text1);
        buf.writeUtf(expresswayNumber1);
        buf.writeEnum(direction1);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignExpresswayDirection5Entity signEntity) {
                signEntity.setExpressway1(expressway1);
                signEntity.setText1(text1);
                signEntity.setExpresswayNumber1(expresswayNumber1);
                signEntity.setDirection1(direction1);

                signEntity.setChanged();
            }
        }
    }
}
