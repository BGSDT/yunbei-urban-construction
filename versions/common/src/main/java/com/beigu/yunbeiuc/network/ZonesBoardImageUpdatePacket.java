package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.ZonesBoardImageEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class ZonesBoardImageUpdatePacket {
    private final BlockPos pos;
    private String text1 = "";
    private ZonesBoardImageEntity.BoardImage image = ZonesBoardImageEntity.BoardImage.RED;
    private float andX = 0f;
    private float andY = 0f;
    private float andScale = 0f;

    public ZonesBoardImageUpdatePacket(BlockPos pos, String text1, ZonesBoardImageEntity.BoardImage image, float andX, float andY, float andScale) {
        this.pos = pos;
        this.text1 = text1;
        this.image = image;
        this.andX = andX;
        this.andY = andY;
        this.andScale = andScale;
    }

    public ZonesBoardImageUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text1 = buf.readUtf();
        this.image = ZonesBoardImageEntity.BoardImage.fromName(buf.readUtf());
        this.andX = buf.readFloat();
        this.andY = buf.readFloat();
        this.andScale = buf.readFloat();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(text1);
        buf.writeUtf(image.getName());
        buf.writeFloat(andX);
        buf.writeFloat(andY);
        buf.writeFloat(andScale);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof ZonesBoardImageEntity signEntity) {
                signEntity.setText1(text1);
                signEntity.setImage(image);
                signEntity.setAndX(andX);
                signEntity.setAndY(andY);
                signEntity.setAndScale(andScale);

                signEntity.setChanged();
            }
        }
    }
}
