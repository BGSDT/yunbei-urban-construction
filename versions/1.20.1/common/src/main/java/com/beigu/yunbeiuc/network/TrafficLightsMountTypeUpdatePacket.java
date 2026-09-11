package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrafficLightsMountTypeUpdatePacket {
    private final BlockPos pos;
    private final TrafficLightsBlock.MountType mountType;

    public TrafficLightsMountTypeUpdatePacket(BlockPos pos, TrafficLightsBlock.MountType mountType) {
        this.pos = pos;
        this.mountType = mountType;
    }

    public TrafficLightsMountTypeUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.mountType = TrafficLightsBlock.MountType.valueOf(buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(mountType.name());
    }

    public void apply(ServerPlayerEntity player) {
        World world = player.getWorld();
        if (world == null) return;

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof TrafficLightsBlock && state.contains(TrafficLightsBlock.TYPE)) {
            world.setBlockState(pos, state.with(TrafficLightsBlock.TYPE, mountType));
        }
    }
}
