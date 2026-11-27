package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.api.ApiCompat;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

// 左转待行区相位图案：按相位序号保存每个相位显示的动画图案。
public class TrafficLightsWaitingAreaUpdatePacket {
    private final BlockPos pos;
    private final int[] patterns;

    public TrafficLightsWaitingAreaUpdatePacket(BlockPos pos, int[] patterns) {
        this.pos = pos;
        this.patterns = patterns;
    }

    public TrafficLightsWaitingAreaUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.patterns = buf.readVarIntArray();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeVarIntArray(patterns);
    }

    public void apply(ServerPlayer player) {
        Level world = ApiCompat.level(player);
        if (world == null) return;
        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity entity) {
            entity.setWaitingAreaPatterns(patterns);
        }
    }
}
