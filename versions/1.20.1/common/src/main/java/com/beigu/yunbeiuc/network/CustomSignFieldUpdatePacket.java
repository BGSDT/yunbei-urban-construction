package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * C2S：TextDisplayScreen 选项行按钮对固定 NBT 字段的修改（field+value），
 * 服务端经 CustomSignBlockEntity.applyFieldOption 写回（子类映射到对应 setter）。
 */
public class CustomSignFieldUpdatePacket {
    private final BlockPos pos;
    private final String field;
    private final String value;

    public CustomSignFieldUpdatePacket(BlockPos pos, String field, String value) {
        this.pos = pos;
        this.field = field;
        this.value = value;
    }

    public CustomSignFieldUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.field = buf.readString(256);
        this.value = buf.readString(256);
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(field);
        buf.writeString(value);
    }

    public void apply(ServerPlayerEntity player) {
        if (!player.getWorld().isChunkLoaded(pos)) {
            return;
        }
        BlockEntity be = player.getWorld().getBlockEntity(pos);
        if (be instanceof CustomSignBlockEntity sign) {
            sign.applyFieldOption(field, value);
        }
    }
}
