package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class RoadNameSignBlockUpdatePacket {
    private final BlockPos pos;
    private final String chineseText;
    private final String englishText;

    public RoadNameSignBlockUpdatePacket(BlockPos pos, String chineseText, String englishText) {
        this.pos = pos;
        this.chineseText = chineseText;
        this.englishText = englishText;
    }

    public RoadNameSignBlockUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.chineseText = buf.readString();
        this.englishText = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(chineseText);
        buf.writeString(englishText);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof RoadNameSignBlockEntity signEntity) {
                // 更新数据
                signEntity.setChineseText(chineseText);
                signEntity.setEnglishText(englishText);

                // 标记需要保存和同步
                signEntity.markDirty();
            }
        }
    }
}
