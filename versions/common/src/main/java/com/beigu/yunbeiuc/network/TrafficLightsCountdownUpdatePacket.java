package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.api.ApiCompat;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TrafficLightsCountdownUpdatePacket {
    private final BlockPos pos;
    private final boolean fullDisplay;
    private final int redFrom;
    private final int greenFrom;
    private final int numberMode;
    private final boolean hidePatternOnGreen;
    private final boolean yellowFlashAlways;

    public TrafficLightsCountdownUpdatePacket(BlockPos pos, boolean fullDisplay, int redFrom, int greenFrom,
                                              int numberMode, boolean hidePatternOnGreen, boolean yellowFlashAlways) {
        this.pos = pos;
        this.fullDisplay = fullDisplay;
        this.redFrom = redFrom;
        this.greenFrom = greenFrom;
        this.numberMode = numberMode;
        this.hidePatternOnGreen = hidePatternOnGreen;
        this.yellowFlashAlways = yellowFlashAlways;
    }

    public TrafficLightsCountdownUpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.fullDisplay = buf.readBoolean();
        this.redFrom = buf.readVarInt();
        this.greenFrom = buf.readVarInt();
        this.numberMode = buf.readVarInt();
        this.hidePatternOnGreen = buf.readBoolean();
        this.yellowFlashAlways = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(fullDisplay);
        buf.writeVarInt(redFrom);
        buf.writeVarInt(greenFrom);
        buf.writeVarInt(numberMode);
        buf.writeBoolean(hidePatternOnGreen);
        buf.writeBoolean(yellowFlashAlways);
    }

    public void apply(ServerPlayer player) {
        Level world = ApiCompat.level(player);
        if (world == null) return;
        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl) {
            tl.setCountdownCondition(fullDisplay, redFrom, greenFrom, numberMode, hidePatternOnGreen, yellowFlashAlways);
        }
    }
}
