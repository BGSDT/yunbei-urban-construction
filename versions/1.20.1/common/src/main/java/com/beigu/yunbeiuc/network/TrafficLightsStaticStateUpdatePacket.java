package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrafficLightsStaticStateUpdatePacket {
    private final BlockPos pos;
    private final TrafficLightsBlockEntity.DirectionType directionType;
    private final TrafficLightsBlock.LightState lightState;

    public TrafficLightsStaticStateUpdatePacket(BlockPos pos, TrafficLightsBlockEntity.DirectionType directionType, TrafficLightsBlock.LightState lightState) {
        this.pos = pos;
        this.directionType = directionType;
        this.lightState = lightState;
    }

    public TrafficLightsStaticStateUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.directionType = TrafficLightsBlockEntity.DirectionType.fromName(buf.readString());
        this.lightState = TrafficLightsBlock.LightState.valueOf(buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(directionType.getName());
        buf.writeString(lightState.name());
    }

    public void apply(ServerPlayerEntity player) {
        World world = player.getWorld();
        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity entity) {
            entity.setStaticState(directionType, lightState, player);
        }
    }
}
