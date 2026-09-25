package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class RoadNameSignBlockUpdatePacket {
    private final BlockPos pos;
    private final String chineseText;
    private final String englishText;

    public RoadNameSignBlockUpdatePacket(BlockPos pos, String chineseText, String englishText) {
        this.pos = pos;
        this.chineseText = chineseText;
        this.englishText = englishText;
    }

    public RoadNameSignBlockUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.chineseText = buf.readUtf();
        this.englishText = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(chineseText);
        buf.writeUtf(englishText);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof RoadNameSignBlockEntity signEntity) {
                // 更新数据
                signEntity.setChineseText(chineseText);
                signEntity.setEnglishText(englishText);

                // 标记需要保存和同步
                signEntity.setChanged();
            }
        }
    }
}
