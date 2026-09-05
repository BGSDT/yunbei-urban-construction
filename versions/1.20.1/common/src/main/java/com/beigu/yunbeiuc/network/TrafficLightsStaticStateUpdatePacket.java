package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrafficLightsStaticStateUpdatePacket {
    private final BlockPos pos;
    private final TrafficLightsBlockEntity.DirectionType directionType;
    private final TrafficLightsBlock.LightState lightState;
    private final boolean showSeconds;
    private final int fixedSeconds;

    public TrafficLightsStaticStateUpdatePacket(BlockPos pos, TrafficLightsBlockEntity.DirectionType directionType,
                                                 TrafficLightsBlock.LightState lightState, boolean showSeconds, int fixedSeconds) {
        this.pos = pos;
        this.directionType = directionType;
        this.lightState = lightState;
        this.showSeconds = showSeconds;
        this.fixedSeconds = fixedSeconds;
    }

    public TrafficLightsStaticStateUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.directionType = TrafficLightsBlockEntity.DirectionType.fromName(buf.readString());
        this.lightState = TrafficLightsBlock.LightState.valueOf(buf.readString());
        this.showSeconds = buf.readBoolean();
        this.fixedSeconds = buf.readInt();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(directionType.getName());
        buf.writeString(lightState.name());
        buf.writeBoolean(showSeconds);
        buf.writeInt(fixedSeconds);
    }

    public void apply(ServerPlayerEntity player) {
        World world = player.getWorld();
        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity entity) {
            entity.setStaticState(directionType, lightState, showSeconds, fixedSeconds, player);
        }
    }
}