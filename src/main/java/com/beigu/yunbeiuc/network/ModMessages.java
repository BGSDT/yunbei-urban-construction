package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages {

    public static final Identifier UPDATE_ROAD_POLES_TEXT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_road_poles_text");
    public static final Identifier UPDATE_FLAG = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_flag");
    public static final Identifier UPDATE_ROAD_NAME_SIGN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_road_name_sign");

    // register receiver for warning text updates
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_ROAD_POLES_TEXT, (server, player, handler, buf, responseSender) -> {
            RoadPoleTextDisplayUpdatePacket packet = new RoadPoleTextDisplayUpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_FLAG, (server, player, handler, buf, responseSender) -> {
            UpdateFlagPacket packet = new UpdateFlagPacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_ROAD_NAME_SIGN, (server, player, handler, buf, responseSender) -> {
            RoadNameSignUpdatePacket packet = new RoadNameSignUpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });
    }

    public static void registerS2CPackets() {

    }
}