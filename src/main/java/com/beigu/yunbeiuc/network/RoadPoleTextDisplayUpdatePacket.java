package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.RoadPoleTextDisplayEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class RoadPoleTextDisplayUpdatePacket {
    private final BlockPos pos;
    private final String text;
    private final int color;

    public RoadPoleTextDisplayUpdatePacket(BlockPos pos, String text, int color) {
        this.pos = pos;
        this.text = text;
        this.color = color;
    }

    public RoadPoleTextDisplayUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.text = buf.readString();
        this.color = buf.readInt();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(text);
        buf.writeInt(color);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof RoadPoleTextDisplayEntity signEntity) {
                // 更新数据
                signEntity.setText(text);
                signEntity.setColor(color);
                signEntity.setFontSize(25); // 固定字体大小

                // 标记需要保存和同步
                signEntity.markDirty();
            }
        }
    }
}