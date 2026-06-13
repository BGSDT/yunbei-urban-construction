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
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_intersection_advance_warning_6");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_intersection_advance_warning_7");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_intersection_warning_1");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_4 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_intersection_warning_4");
    public static final Identifier UPDATE_SIGN_GUIDE_CONFIRMATION_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_confirmation_1");
    public static final Identifier UPDATE_SIGN_GUIDE_LANE_INDICATOR_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_guide_lane_indicator_1");
    public static final Identifier UPDATE_SIGN_EXPRESSWAY_EXIT_8 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_sign_expressway_exit_8");
    public static final Identifier UPDATE_ZONES_BOARD_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_zone_board_1");
    public static final Identifier UPDATE_ZONES_BOARD_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "update_zone_board_2");

    // register receiver for warning text updates
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_ROAD_POLES_TEXT, (server, player, handler, buf, responseSender) -> {
            RoadPoleTextDisplayUpdatePacket packet = new RoadPoleTextDisplayUpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_FLAG, (server, player, handler, buf, responseSender) -> {
            FlagUpdatePacket packet = new FlagUpdatePacket(buf);
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

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6, (server, player, handler, buf, responseSender) -> {
            SignGuideIntersectionAdvanceWarning6UpdatePacket packet = new SignGuideIntersectionAdvanceWarning6UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7, (server, player, handler, buf, responseSender) -> {
            SignGuideIntersectionAdvanceWarning7UpdatePacket packet = new SignGuideIntersectionAdvanceWarning7UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_1, (server, player, handler, buf, responseSender) -> {
            SignGuideIntersectionWarning1UpdatePacket packet = new SignGuideIntersectionWarning1UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_4, (server, player, handler, buf, responseSender) -> {
            SignGuideIntersectionWarning4UpdatePacket packet = new SignGuideIntersectionWarning4UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_CONFIRMATION_1, (server, player, handler, buf, responseSender) -> {
            SignGuideConfirmation1UpdatePacket packet = new SignGuideConfirmation1UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_GUIDE_LANE_INDICATOR_1, (server, player, handler, buf, responseSender) -> {
            SignGuideLaneIndicator1UpdatePacket packet = new SignGuideLaneIndicator1UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_SIGN_EXPRESSWAY_EXIT_8, (server, player, handler, buf, responseSender) -> {
            SignExpresswayExit8UpdatePacket packet = new SignExpresswayExit8UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_ZONES_BOARD_1, (server, player, handler, buf, responseSender) -> {
            ZonesBoard1UpdatePacket packet = new ZonesBoard1UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_ZONES_BOARD_2, (server, player, handler, buf, responseSender) -> {
            ZonesBoard2UpdatePacket packet = new ZonesBoard2UpdatePacket(buf);
            server.execute(() -> packet.apply(player));
        });
    }

    public static void registerS2CPackets() {

    }
}