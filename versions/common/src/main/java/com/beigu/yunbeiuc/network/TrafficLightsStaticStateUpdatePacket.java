package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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

    public TrafficLightsStaticStateUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.directionType = TrafficLightsBlockEntity.DirectionType.fromName(buf.readUtf());
        this.lightState = TrafficLightsBlock.LightState.valueOf(buf.readUtf());
        this.showSeconds = buf.readBoolean();
        this.fixedSeconds = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(directionType.getName());
        buf.writeUtf(lightState.name());
        buf.writeBoolean(showSeconds);
        buf.writeInt(fixedSeconds);
    }

    public void apply(ServerPlayer player) {
        Level world = player.level;
        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity entity) {
            entity.setStaticState(directionType, lightState, showSeconds, fixedSeconds, player);
        }
    }
}
