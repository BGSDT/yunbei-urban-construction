package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.api.ApiCompat;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TrafficLightsPulseUpdatePacket {
    private final BlockPos pos;
    private final boolean enabled;

    public TrafficLightsPulseUpdatePacket(BlockPos pos, boolean enabled) {
        this.pos = pos;
        this.enabled = enabled;
    }

    public TrafficLightsPulseUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.enabled = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(enabled);
    }

    public void apply(ServerPlayer player) {
        Level world = ApiCompat.level(player);
        if (world == null) return;
        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl) {
            tl.setPulseCountdown(enabled);
        }
    }
}
