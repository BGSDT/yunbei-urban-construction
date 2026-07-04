package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class  ModMessages {

    public static final Identifier UPDATE_ROAD_POLES_TEXT = id("update_road_poles_text");
    public static final Identifier UPDATE_FLAG = id("update_flag");
    public static final Identifier UPDATE_ROAD_NAME_SIGN = id("update_road_name_sign");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN = id("update_sign_guide_intersection_advance_warning_1_wuhan");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1 = id("update_sign_guide_intersection_advance_warning_1");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3 = id("update_sign_guide_intersection_advance_warning_3");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5 = id("update_sign_guide_intersection_advance_warning_5");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6 = id("update_sign_guide_intersection_advance_warning_6");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7 = id("update_sign_guide_intersection_advance_warning_7");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_1 = id("update_sign_guide_intersection_warning_1");
    public static final Identifier UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_4 = id("update_sign_guide_intersection_warning_4");
    public static final Identifier UPDATE_SIGN_GUIDE_CONFIRMATION_1 = id("update_sign_guide_confirmation_1");
    public static final Identifier UPDATE_SIGN_GUIDE_LANE_INDICATOR_1 = id("update_sign_guide_lane_indicator_1");
    public static final Identifier UPDATE_SIGN_GUIDE_ROADSIDE_FACILITY_OVERLOAD_CHECKPOINT_1 = id("update_sign_guide_roadsid_facility_overload_checkpoint_1");
    public static final Identifier UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_1 = id("update_sign_expressway_entrance_advance_1");
    public static final Identifier UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_4 = id("update_sign_expressway_entrance_advance_4");
    public static final Identifier UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_7 = id("update_sign_expressway_entrance_advance_7");
    public static final Identifier UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_10 = id("update_sign_expressway_entrance_advance_10");
    public static final Identifier UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_13 = id("update_sign_expressway_entrance_advance_13");
    public static final Identifier UPDATE_SIGN_EXPRESSWAY_EXIT_8 = id("update_sign_expressway_exit_8");
    public static final Identifier UPDATE_ZONES_BOARD_1 = id("update_zone_board_1");
    public static final Identifier UPDATE_ZONES_BOARD_IMAGE = id("update_zone_board_image");
    public static final Identifier UPDATE_ZONES_BOARD_TIME_RANGE_1 = id("update_zone_board_time_range_1");
    public static final Identifier UPDATE_ZONES_BOARD_TIME_RANGE_2 = id("update_zone_board_time_range_2");
    public static final Identifier UPDATE_ZONES_BOARD_OVER_WEIGHT = id("update_zone_board_over_weight");

    private static Identifier id(String path) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID, path);
    }

    public static void registerC2SPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_ROAD_POLES_TEXT, (buf, context) -> {
            RoadPoleTextDisplayUpdatePacket packet = new RoadPoleTextDisplayUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_FLAG, (buf, context) -> {
            FlagUpdatePacket packet = new FlagUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_ROAD_NAME_SIGN, (buf, context) -> {
            RoadNameSignBlockUpdatePacket packet = new RoadNameSignBlockUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN, (buf, context) -> {
            SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket packet = new SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1, (buf, context) -> {
            SignGuideIntersectionAdvanceWarning1UpdatePacket packet = new SignGuideIntersectionAdvanceWarning1UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3, (buf, context) -> {
            SignGuideIntersectionAdvanceWarning3UpdatePacket packet = new SignGuideIntersectionAdvanceWarning3UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5, (buf, context) -> {
            SignGuideIntersectionAdvanceWarning5UpdatePacket packet = new SignGuideIntersectionAdvanceWarning5UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6, (buf, context) -> {
            SignGuideIntersectionAdvanceWarning6UpdatePacket packet = new SignGuideIntersectionAdvanceWarning6UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7, (buf, context) -> {
            SignGuideIntersectionAdvanceWarning7UpdatePacket packet = new SignGuideIntersectionAdvanceWarning7UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_1, (buf, context) -> {
            SignGuideIntersectionWarning1UpdatePacket packet = new SignGuideIntersectionWarning1UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_4, (buf, context) -> {
            SignGuideIntersectionWarning4UpdatePacket packet = new SignGuideIntersectionWarning4UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_CONFIRMATION_1, (buf, context) -> {
            SignGuideConfirmation1UpdatePacket packet = new SignGuideConfirmation1UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_LANE_INDICATOR_1, (buf, context) -> {
            SignGuideLaneIndicator1UpdatePacket packet = new SignGuideLaneIndicator1UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_GUIDE_ROADSIDE_FACILITY_OVERLOAD_CHECKPOINT_1, (buf, context) -> {
            SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket packet = new SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_1, (buf, context) -> {
            SignExpresswayEntranceAdvance1UpdatePacket packet = new SignExpresswayEntranceAdvance1UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_4, (buf, context) -> {
            SignExpresswayEntranceAdvance4UpdatePacket packet = new SignExpresswayEntranceAdvance4UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_7, (buf, context) -> {
            SignExpresswayEntranceAdvance7UpdatePacket packet = new SignExpresswayEntranceAdvance7UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_10, (buf, context) -> {
            SignExpresswayEntranceAdvance10UpdatePacket packet = new SignExpresswayEntranceAdvance10UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_13, (buf, context) -> {
            SignExpresswayEntranceAdvance13UpdatePacket packet = new SignExpresswayEntranceAdvance13UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_SIGN_EXPRESSWAY_EXIT_8, (buf, context) -> {
            SignExpresswayExit8UpdatePacket packet = new SignExpresswayExit8UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_ZONES_BOARD_1, (buf, context) -> {
            ZonesBoard1UpdatePacket packet = new ZonesBoard1UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_ZONES_BOARD_IMAGE, (buf, context) -> {
            ZonesBoardImageUpdatePacket packet = new ZonesBoardImageUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_ZONES_BOARD_TIME_RANGE_1, (buf, context) -> {
            ZonesBoardTimeRange1UpdatePacket packet = new ZonesBoardTimeRange1UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_ZONES_BOARD_TIME_RANGE_2, (buf, context) -> {
            ZonesBoardTimeRange2UpdatePacket packet = new ZonesBoardTimeRange2UpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_ZONES_BOARD_OVER_WEIGHT, (buf, context) -> {
            ZonesBoardOverWeightUpdatePacket packet = new ZonesBoardOverWeightUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayerEntity) context.getPlayer()));
        });
    }
}
