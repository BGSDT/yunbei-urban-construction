package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CustomSignUpdatePacket {
    private final BlockPos pos;
    private final List<CustomSignBlockEntity.TextLineData> textLines;

    public CustomSignUpdatePacket(BlockPos pos, List<CustomSignBlockEntity.TextLineData> textLines) {
        this.pos = pos;
        this.textLines = textLines;
    }

    public CustomSignUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        int size = buf.readInt();
        this.textLines = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            NbtCompound nbt = buf.readNbt();
            if (nbt != null) {
                this.textLines.add(CustomSignBlockEntity.TextLineData.fromNbt(nbt));
            }
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(textLines.size());
        for (CustomSignBlockEntity.TextLineData data : textLines) {
            buf.writeNbt(data.toNbt());
        }
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof CustomSignBlockEntity signEntity) {
                signEntity.setTextLines(textLines);
                signEntity.markDirty();
            }
        }
    }
}