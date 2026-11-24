package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.api.ApiCompat;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

// 把附加条件应用到当前维度所有已加载的红绿灯。

public class TrafficLightsApplyAllConditionsPacket {
    private final boolean pulseEnabled;
    private final boolean fullDisplay;
    private final int redFrom;
    private final int greenFrom;
    private final int numberMode;
    private final boolean hidePatternOnGreen;
    private final boolean yellowFlashAlways;

    public TrafficLightsApplyAllConditionsPacket(boolean pulseEnabled, boolean fullDisplay,
                                                 int redFrom, int greenFrom, int numberMode,
                                                 boolean hidePatternOnGreen, boolean yellowFlashAlways) {
        this.pulseEnabled = pulseEnabled;
        this.fullDisplay = fullDisplay;
        this.redFrom = redFrom;
        this.greenFrom = greenFrom;
        this.numberMode = numberMode;
        this.hidePatternOnGreen = hidePatternOnGreen;
        this.yellowFlashAlways = yellowFlashAlways;
    }

    public TrafficLightsApplyAllConditionsPacket(FriendlyByteBuf buf) {
        this.pulseEnabled = buf.readBoolean();
        this.fullDisplay = buf.readBoolean();
        this.redFrom = buf.readVarInt();
        this.greenFrom = buf.readVarInt();
        this.numberMode = buf.readVarInt();
        this.hidePatternOnGreen = buf.readBoolean();
        this.yellowFlashAlways = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(pulseEnabled);
        buf.writeBoolean(fullDisplay);
        buf.writeVarInt(redFrom);
        buf.writeVarInt(greenFrom);
        buf.writeVarInt(numberMode);
        buf.writeBoolean(hidePatternOnGreen);
        buf.writeBoolean(yellowFlashAlways);
    }

    public void apply(ServerPlayer player) {
        ServerLevel level = ApiCompat.serverLevel(player);
        if (level == null) return;
        TrafficLightsBlockEntity.applyConditionsToAll(
                level, pulseEnabled, fullDisplay, redFrom, greenFrom, numberMode, hidePatternOnGreen, yellowFlashAlways);
        ApiCompat.message(player, Text.literal("§a已把附加条件应用到全部红绿灯"), true);
    }
}
