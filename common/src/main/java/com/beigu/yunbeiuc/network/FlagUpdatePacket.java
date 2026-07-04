package com.beigu.yunbeiuc.network;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class FlagUpdatePacket {
    private final BlockPos pos;
    private final String flagId;

    public FlagUpdatePacket(BlockPos pos, String flagId) {
        this.pos = pos;
        this.flagId = flagId;
    }

    public FlagUpdatePacket(PacketByteBuf buf) {
        NbtCompound nbt = buf.readNbt();
        if (nbt != null) {
            this.pos = BlockPos.fromLong(nbt.getLong("pos"));
            this.flagId = nbt.getString("flagId");
        } else {
            this.pos = BlockPos.ORIGIN;
            this.flagId = "";
        }
    }

    public void write(PacketByteBuf buf) {
        NbtCompound nbt = new NbtCompound();
        nbt.putLong("pos", this.pos.asLong());
        nbt.putString("flagId", this.flagId);
        buf.writeNbt(nbt);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().getBlockEntity(this.pos) instanceof com.beigu.yunbeiuc.entity.FlagBlockEntity flagEntity) {
            flagEntity.setFlagId(this.flagId);
        }
    }
}