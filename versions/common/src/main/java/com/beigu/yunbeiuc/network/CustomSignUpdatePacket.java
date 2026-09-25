package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CustomSignUpdatePacket {
    private final BlockPos pos;
    private final List<TextLineData> textLines;

    public CustomSignUpdatePacket(BlockPos pos, List<TextLineData> textLines) {
        this.pos = pos;
        this.textLines = textLines;
    }

    public CustomSignUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        int size = buf.readInt();
        this.textLines = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            CompoundTag nbt = buf.readNbt();
            if (nbt != null) this.textLines.add(TextLineData.fromNbt(nbt));
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(textLines != null ? textLines.size() : 0);
        if (textLines != null) {
            for (var d : textLines) {
                buf.writeNbt(d.toNbt());
            }
        }
    }

    public void apply(ServerPlayer player) {

        if (!player.level.hasChunkAt(pos)) {
            return;
        }

        BlockEntity be = player.level.getBlockEntity(pos);
        if (be instanceof CustomSignBlockEntity sign) {
            sign.setTextLines(textLines);
            sign.setChanged();

            // 通知客户端重新读取方块实体数据
            player.level.sendBlockUpdated(pos, sign.getBlockState(), sign.getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);

        }
    }

    public static CustomSignUpdatePacket update(BlockPos pos, List<TextLineData> textLines) {
        return new CustomSignUpdatePacket(pos, textLines);
    }

    public static CustomSignUpdatePacket loadPreset(BlockPos pos, List<TextLineData> loadedLines) {
        return new CustomSignUpdatePacket(pos, loadedLines);
    }
}
