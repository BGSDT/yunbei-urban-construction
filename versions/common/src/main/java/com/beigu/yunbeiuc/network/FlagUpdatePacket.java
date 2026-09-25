package com.beigu.yunbeiuc.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class FlagUpdatePacket {
    private final BlockPos pos;
    private final String flagId;

    public FlagUpdatePacket(BlockPos pos, String flagId) {
        this.pos = pos;
        this.flagId = flagId;
    }

    public FlagUpdatePacket(FriendlyByteBuf buf) {
        CompoundTag nbt = buf.readNbt();
        if (nbt != null) {
            this.pos = BlockPos.of(nbt.getLong("pos"));
            this.flagId = nbt.getString("flagId");
        } else {
            this.pos = BlockPos.ZERO;
            this.flagId = "";
        }
    }

    public void write(FriendlyByteBuf buf) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("pos", this.pos.asLong());
        nbt.putString("flagId", this.flagId);
        buf.writeNbt(nbt);
    }

    public void apply(ServerPlayer player) {
        if (player.level.getBlockEntity(this.pos) instanceof com.beigu.yunbeiuc.entity.FlagBlockEntity flagEntity) {
            flagEntity.setFlagId(this.flagId);
        }
    }
}
