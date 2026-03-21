package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.network.abandoned.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier UPDATE_SPEED_LIMIT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_speed_limit");
    public static final Identifier UPDATE_CANCEL_SPEED_LIMIT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_cancel_speed_limit");
    public static final Identifier UPDATE_VEHICLE_TYPE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_vehicle_type");
    public static final Identifier UPDATE_DIRECTION_TYPE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_direction_type");
    public static final Identifier UPDATE_HEIGHT_LIMIT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_height_limit");
    public static final Identifier UPDATE_WIDTH_LIMIT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_width_limit");
    public static final Identifier UPDATE_WEIGHT_LIMIT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_weight_limit");
    public static final Identifier UPDATE_NO_SPECIAL_TYPE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_no_special_type");
    public static final Identifier UPDATE_INDICATION_DIRECTION = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_indication_direction");
    public static final Identifier UPDATE_INDICATION_LANE_DIRECTION = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_indication_lane_direction");
    public static final Identifier UPDATE_ROAD_POLES_TEXT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_road_poles_text");
    public static final Identifier UPDATE_FLAG = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_flag");
    public static final Identifier ENTITY_CONVERSION = new Identifier(YunbeiUrbanConstruction.MOD_ID, "entity_conversion");
    public static final Identifier TRANSFORM_UPDATE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "transform_update");
    public static final Identifier UPDATE_WARNING_TYPE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_warning_type");

    // register receiver for warning text updates
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SPEED_LIMIT, (server, player, handler, buf, responseSender) -> {
            UpdateSpeedLimitPacket packet = new UpdateSpeedLimitPacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_CANCEL_SPEED_LIMIT, (server, player, handler, buf, responseSender) -> {
            UpdateCancelSpeedLimitPacket packet = new UpdateCancelSpeedLimitPacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_VEHICLE_TYPE, (server, player, handler, buf, responseSender) -> {
            UpdateVehicleTypePacket packet = new UpdateVehicleTypePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_DIRECTION_TYPE, (server, player, handler, buf, responseSender) -> {
            UpdateDirectionTypePacket packet = new UpdateDirectionTypePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_HEIGHT_LIMIT, (server, player, handler, buf, responseSender) -> {
            UpdateHeightLimitPacket packet = new UpdateHeightLimitPacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_WIDTH_LIMIT, (server, player, handler, buf, responseSender) -> {
            UpdateWidthLimitPacket packet = new UpdateWidthLimitPacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_WEIGHT_LIMIT, (server, player, handler, buf, responseSender) -> {
            UpdateWeightLimitPacket packet = new UpdateWeightLimitPacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_NO_SPECIAL_TYPE, (server, player, handler, buf, responseSender) -> {
            UpdateNoSpecialTypePacket packet = new UpdateNoSpecialTypePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_INDICATION_DIRECTION, (server, player, handler, buf, responseSender) -> {
            UpdateIndicationDirectionPacket packet = new UpdateIndicationDirectionPacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_INDICATION_LANE_DIRECTION, (server, player, handler, buf, responseSender) -> {
            UpdateIndicationLaneDirectionPacket packet = new UpdateIndicationLaneDirectionPacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_WARNING_TYPE, (server, player, handler, buf, responseSender) -> {
            UpdateWarningTypePacket packet = new UpdateWarningTypePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_ROAD_POLES_TEXT, (server, player, handler, buf, responseSender) -> {
            RoadPoleTextDisplayUpdatePacket packet = new RoadPoleTextDisplayUpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_FLAG, (server, player, handler, buf, responseSender) -> {
            UpdateFlagPacket packet = new UpdateFlagPacket(buf);
            server.execute(() -> packet.apply(player));
        });
    }

    public static void registerS2CPackets() {

    }
}