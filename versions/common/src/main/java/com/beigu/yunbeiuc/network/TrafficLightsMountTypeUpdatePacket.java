package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TrafficLightsMountTypeUpdatePacket {
    private final BlockPos pos;
    private final TrafficLightsBlock.MountType mountType;

    public TrafficLightsMountTypeUpdatePacket(BlockPos pos, TrafficLightsBlock.MountType mountType) {
        this.pos = pos;
        this.mountType = mountType;
    }

    public TrafficLightsMountTypeUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.mountType = TrafficLightsBlock.MountType.valueOf(buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(mountType.name());
    }

    public void apply(ServerPlayer player) {
        Level world = player.level;
        if (world == null) return;

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof TrafficLightsBlock && state.hasProperty(TrafficLightsBlock.TYPE)) {
            world.setBlock(pos, state.setValue(TrafficLightsBlock.TYPE, mountType), VersionServices.blocks().updateAll());
        }
    }
}
