package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages {

    public static final Identifier UPDATE_ROAD_POLES_TEXT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_road_poles_text");
    public static final Identifier UPDATE_FLAG = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_flag");
    public static final Identifier UPDATE_ROAD_NAME_SIGN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_road_name_sign");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_intersection_advance_warning_1_wuhan");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_intersection_advance_warning_1");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_intersection_advance_warning_3");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_intersection_advance_warning_5");
    public static final Identifier UPDATE_SIGN_GUIDE_LANE_INDICATOR_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_lane_indicator_1");

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
            RoadNameSignBlockUpdatePacket packet = new RoadNameSignBlockUpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN, (server, player, handler, buf, responseSender) -> {
            SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket packet = new SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1, (server, player, handler, buf, responseSender) -> {
            SignGuideIntersectionAdvanceWarning1UpdatePacket packet = new SignGuideIntersectionAdvanceWarning1UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3, (server, player, handler, buf, responseSender) -> {
            SignGuideIntersectionAdvanceWarning3UpdatePacket packet = new SignGuideIntersectionAdvanceWarning3UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5, (server, player, handler, buf, responseSender) -> {
            SignGuideIntersectionAdvanceWarning5UpdatePacket packet = new SignGuideIntersectionAdvanceWarning5UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_LANE_INDICATOR_1, (server, player, handler, buf, responseSender) -> {
            SignGuideLaneIndicator1UpdatePacket packet = new SignGuideLaneIndicator1UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });
    }

    public static void registerS2CPackets() {

    }
}