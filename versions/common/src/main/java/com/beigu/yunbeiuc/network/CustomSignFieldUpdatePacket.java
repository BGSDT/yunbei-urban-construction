package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

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

    public CustomSignFieldUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.field = buf.readUtf(256);
        this.value = buf.readUtf(256);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(field);
        buf.writeUtf(value);
    }

    public void apply(ServerPlayer player) {
        if (!player.level.hasChunkAt(pos)) {
            return;
        }
        BlockEntity be = player.level.getBlockEntity(pos);
        if (be instanceof CustomSignBlockEntity sign) {
            sign.applyFieldOption(field, value);
        }
    }
}
