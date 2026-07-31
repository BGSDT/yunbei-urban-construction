package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CustomSignUpdatePacket {
    private final BlockPos pos;
    private final List<TextLineData> textLines;

    public CustomSignUpdatePacket(BlockPos pos, List<TextLineData> textLines) {
        this.pos = pos;
        this.textLines = textLines;
    }

    public CustomSignUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        int size = buf.readInt();
        this.textLines = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            NbtCompound nbt = buf.readNbt();
            if (nbt != null) this.textLines.add(TextLineData.fromNbt(nbt));
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(textLines != null ? textLines.size() : 0);
        if (textLines != null) {
            for (var d : textLines) {
                buf.writeNbt(d.toNbt());
            }
        }
    }

    public void apply(ServerPlayerEntity player) {

        if (!player.getWorld().isChunkLoaded(pos)) {
            return;
        }

        BlockEntity be = player.getWorld().getBlockEntity(pos);
        if (be instanceof CustomSignBlockEntity sign) {
            sign.setTextLines(textLines);
            sign.markDirty();

            // 强制同步到客户端
            if (player.getWorld().getChunk(pos) != null) {
                player.getWorld().getChunk(pos).setNeedsSaving(true);
            }

            // 发送更新包给所有客户端
            sign.toUpdatePacket();

        }
    }

    public static CustomSignUpdatePacket update(BlockPos pos, List<TextLineData> textLines) {
        return new CustomSignUpdatePacket(pos, textLines);
    }

    public static CustomSignUpdatePacket loadPreset(BlockPos pos, List<TextLineData> loadedLines) {
        return new CustomSignUpdatePacket(pos, loadedLines);
    }
}