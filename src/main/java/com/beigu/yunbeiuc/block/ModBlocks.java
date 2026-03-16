package com.beigu.yunbeiuc.block;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.*;
import com.beigu.yunbeiuc.block.custom.anti.AntiGlareNet;
import com.beigu.yunbeiuc.block.custom.anti.AntiGlareNetPoleBlock;
import com.beigu.yunbeiuc.block.custom.anti.AntiGlareVersion;
import com.beigu.yunbeiuc.block.custom.barrier.*;
import com.beigu.yunbeiuc.block.custom.box.RoadFlowerBox1;
import com.beigu.yunbeiuc.block.custom.box.RoadFlowerBox2Fence;
import com.beigu.yunbeiuc.block.custom.gantry.*;
import com.beigu.yunbeiuc.block.custom.guardrail.RoadClosedBarricadeGuardrail2;
import com.beigu.yunbeiuc.block.custom.instrument.InstrumentCamera;
import com.beigu.yunbeiuc.block.custom.instrument.InstrumentPoleFoundations;
import com.beigu.yunbeiuc.block.custom.instrument.InstrumentPolelLongitudinal;
import com.beigu.yunbeiuc.block.custom.island.SafetyIslandBlock;
import com.beigu.yunbeiuc.block.custom.island.SafetyIslandEdgeBlock;
import com.beigu.yunbeiuc.block.custom.island.SafetyIslandObliqueBlock;
import com.beigu.yunbeiuc.block.custom.pole.*;
import com.beigu.yunbeiuc.block.custom.guardrail.RoadClosedBarricadeGuardrail1;
import com.beigu.yunbeiuc.block.custom.railings.RoadRailings;
import com.beigu.yunbeiuc.block.custom.railings.RoadRailingsOblique;
import com.beigu.yunbeiuc.block.custom.railings.RoadRailingsPole;
import com.beigu.yunbeiuc.block.custom.road.GroundMarkBlock;
import com.beigu.yunbeiuc.block.custom.road.ManholeCover;
import com.beigu.yunbeiuc.block.custom.rubbshi.RubbishBinGrayGreen;
import com.beigu.yunbeiuc.block.custom.rubbshi.RubbishBinWhite;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightsBlock;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightsPavement;
import com.beigu.yunbeiuc.block.custom.sign.abandoned.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block LEFT_TURN_GROUND_MARK = register("left_turn_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block STRAIGHT_GROUND_MARK = register("straight_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block RIGHT_TURN_GROUND_MARK = register("right_turn_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block LEFT_TURN_AROUND_GROUND_MARK = register("left_turn_around_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block RIGHT_TURN_AROUND_GROUND_MARK = register("right_turn_around_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block STRAIGHT_LEFT_TURN_GROUND_MARK = register("straight_left_turn_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block STRAIGHT_RIGHT_TURN_GROUND_MARK = register("straight_right_turn_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block STRAIGHT_LEFT_RIGHT_TURN_GROUND_MARK = register("straight_left_right_turn_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block LEFT_TURN_MERGE_GROUND_MARK = register("left_turn_merge_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block RIGHT_TURN_MERGE_GROUND_MARK = register("right_turn_merge_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block LEFT_TURN_AROUND_SINGLE_GROUND_MARK = register("left_turn_around_single_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block RIGHT_TURN_AROUND_SINGLE_GROUND_MARK = register("right_turn_around_single_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block SLOWDOWN_ANNOUNCEMENT_GROUND_MARK = register("slowdown_announcement_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block SLOWDOWN_YIELD_GROUND_MARK = register("slowdown_yield_ground_mark",new GroundMarkBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block MANHOLE_COVER = register("manhole_cover",new ManholeCover(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block TRAFFIC_LIGHTS_STRAIGHT = register("traffic_lights_straight",new TrafficLightsBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).luminance(state -> 15).requiresTool()));
    public static final Block TRAFFIC_LIGHTS_LEFT = register("traffic_lights_left",new TrafficLightsBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).luminance(state -> 15).requiresTool()));
    public static final Block TRAFFIC_LIGHTS_PAVEMENT = register("traffic_lights_pavement",new TrafficLightsPavement(AbstractBlock.Settings.create().strength(1.25F, 4.2F).luminance(state -> 15).requiresTool()));

    public static final Block ROAD_FLOWER_BOX_1 = register("road_flower_box_1",new RoadFlowerBox1(AbstractBlock.Settings.create().strength( 1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_FLOWER_BOX_2 = register("road_flower_box_2",new DirectionBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_FLOWER_BOX_2_FENCE = register("road_flower_box_2_fence",new RoadFlowerBox2Fence(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block ROAD_DETECTION_CAMERA = register("road_detection_camera",new RoadDetectionCamera(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_LIGHTING_LAMP = register("road_lighting_lamp",new RoadLightingLamp(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_RADAR_SPEED_DETECTOR = register("road_radar_speed_detector",new RoadRadarSpeedDetector(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block TRAFFIC_CONE = register("traffic_cone",new TrafficCone(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_COLLISION_BARREL = register("road_collision_barrel",new RoadCollisionBarrel(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block WATER_SAFETY_BARRIER_RED = register("water_safety_barrier_red",new WaterSafetyBarrier(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block RUBBISH_BIN_WHITE = register("rubbish_bin_white",new RubbishBinWhite(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block RUBBISH_BIN_GRAY_GREEN = register("rubbish_bin_gray_green",new RubbishBinGrayGreen(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block SPEED_BUMP = register("speed_bump",new SpeedBump(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block VIBRATION_MARKING_LINE = register("vibration_marking_line",new VibrationMarkingLine(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block PARKING_SPACE_BARRIER = register("parking_space_barrier",new ParkingSpaceBarrier(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block SIGN_SPEED_LIMIT_BLOCK = register("sign_speed_limit_block", new SignSpeedLimitBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_BLOCK = register("sign_cancel_speed_limit_block", new SignCancelSpeedLimitBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_NO_ENTRY_FOR_VEHICLES_BLOCK = register("sign_no_entry_for_vehicles_block", new SignNoEntryForVehiclesBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_NO_DIRECTION_BLOCK = register("sign_no_direction_block", new SignNoDirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_HEIGHT_LIMIT_BLOCK = register("sign_height_limit_block", new SignHeightLimitBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_WIDTH_LIMIT_BLOCK = register("sign_width_limit_block", new SignWidthLimitBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_WEIGHT_LIMIT_BLOCK = register("sign_weight_limit_block", new SignWeightLimitBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_NO_SPECIAL_BLOCK = register("sign_no_special_block", new SignNoSpecialBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_INDICATION_DIRECTION_BLOCK = register("sign_indication_direction_block", new SignIndicationDirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_BLOCK = register("sign_indication_lane_direction_block", new SignIndicationLaneDirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_BLOCK = register("sign_guide_intersection_advance_warning_block", new SignGuideIntersectionAdvanceWarningBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block GANTRY_FRAME_SIDE = register("gantry_frame_side", new GantryFrameSide(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_CONNECTION = register("gantry_frame_connection", new GantryFrameConnection(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_MAIN = register("gantry_frame_main", new GantryFrameMain(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_RAILING = register("gantry_frame_railing", new GantryFrameRailing(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_LADDER = register("gantry_frame_ladder", new GantryFrameLadder(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_LED_SIDE = register("gantry_frame_led_side", new GantryFrameLedSide(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_LED_MAIN = register("gantry_frame_led_main", new GantryFrameLedMain(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block ANTI_GLARE_NET = register("anti_glare_net", new AntiGlareNet(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block ANTI_GLARE_NET_POLE = register("anti_glare_net_pole", new AntiGlareNetPoleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block ANTI_GLARE_VERSION = register("anti_glare_version", new AntiGlareVersion(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block TRAFFIC_BARRIER = register("traffic_barrier", new TrafficBarrierBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_YELLOW_DOUBLE = register("traffic_barrier_yellow_double", new TrafficBarrierBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_YELLOW = register("traffic_barrier_yellow", new TrafficBarrierDoubleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_RED = register("traffic_barrier_red", new TrafficBarrierBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_RED_DOUBLE = register("traffic_barrier_red_double", new TrafficBarrierDoubleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_OBLIQUE = register("traffic_barrier_oblique", new TrafficBarrierObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY = register("traffic_barrier_gray", new TrafficBarrierGrayBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY_OBLIQUE = register("traffic_barrier_gray_oblique", new TrafficBarrierGrayObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY_RED = register("traffic_barrier_gray_red", new TrafficBarrierGrayBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY_RED_OBLIQUE = register("traffic_barrier_gray_red_oblique", new TrafficBarrierObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY_YELLOW = register("traffic_barrier_gray_yellow", new TrafficBarrierGrayBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY_YELLOW_OBLIQUE = register("traffic_barrier_gray_yellow_oblique", new TrafficBarrierObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY_SLANT = register("traffic_barrier_gray_slant", new TrafficBarrierGraySlantBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY_SLANT_YELLOW = register("traffic_barrier_gray_slant_yellow", new TrafficBarrierGraySlantBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY_SLANT_RED = register("traffic_barrier_gray_slant_red", new TrafficBarrierGraySlantBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_GRAY_SLANT_OBLIQUE = register("traffic_barrier_gray_slant_oblique", new TrafficBarrierGraySlantBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block ROAD_WARNING_POLE_RED = register("road_warning_pole_red", new RoadWarningPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block ROAD_WARNING_POLE_YELLOW = register("road_warning_pole_yellow", new RoadWarningPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block ROAD_WARNING_POLE_GREEN = register("road_warning_pole_green", new RoadWarningPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block IRON_HORSE_YELLOW = register("iron_horse_yellow", new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block IRON_HORSE_RED = register("iron_horse_red", new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block IRON_HORSE_WHITE = register("iron_horse_white", new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block IRON_HORSE_GRAY = register("iron_horse_gray", new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block REFLECTIVE_SIGN_YELLOW_ALL_1 = register("reflective_sign_yellow_all_1", new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block REFLECTIVE_SIGN_YELLOW_ALL_2 = register("reflective_sign_yellow_all_2", new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block REFLECTIVE_SIGN_RED_ALL_1 = register("reflective_sign_red_all_1", new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block REFLECTIVE_SIGN_RED_ALL_2 = register("reflective_sign_red_all_2", new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block INSTRUMENT_POLE_FOUNDATIONS = register("instrument_pole_foundations", new InstrumentPoleFoundations(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block INSTRUMENT_POLE_LONGITUDINAL = register("instrument_pole_longitudinal", new InstrumentPolelLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block INSTRUMENT_CAMERA = register("instrument_camera", new InstrumentCamera(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block SAFETY_ISLAND_YELLOW_1 = register("safety_island_yellow_1", new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_2 = register("safety_island_yellow_2", new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_3 = register("safety_island_yellow_3", new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_4 = register("safety_island_yellow_4", new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_GRAY = register("safety_island_gray", new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_OBLIQUE_1 = register("safety_island_yellow_oblique_1", new SafetyIslandObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_OBLIQUE_2 = register("safety_island_yellow_oblique_2", new SafetyIslandObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_GRAY_OBLIQUE = register("safety_island_gray_oblique", new SafetyIslandObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_SLAB_EDGE_1 = register("safety_island_yellow_slab_edge_1", new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_SLAB_EDGE_2 = register("safety_island_yellow_slab_edge_2", new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_SLAB_EDGE_3 = register("safety_island_yellow_slab_edge_3", new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_SLAB_EDGE_4 = register("safety_island_yellow_slab_edge_4", new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_GRAY_SLAB_EDGE = register("safety_island_gray_slab_edge", new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_1 = register("safety_island_yellow_slab_edge_oblique_1", new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_2 = register("safety_island_yellow_slab_edge_oblique_2", new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_GRAY_SLAB = register("safety_island_gray_slab", new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SAFETY_ISLAND_GRAY_SLAB_EDGE_OBLIQUE = register("safety_island_gray_slab_edge_oblique", new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block ROAD_RAILINGS_IRON = register("road_railings_iron", new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_RAILINGS_IRON_ENDING_1 = register("road_railings_iron_ending_1", new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_RAILINGS_IRON_ENDING_2 = register("road_railings_iron_ending_2", new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_RAILINGS_IRON_POLE = register("road_railings_iron_pole", new RoadRailingsPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_RAILINGS_IRON_OBLIQUE = register("road_railings_iron_oblique", new RoadRailingsOblique(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_RAILINGS_GREEN = register("road_railings_green", new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_RAILINGS_GREEN_ENDING_1 = register("road_railings_green_ending_1", new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_RAILINGS_GREEN_ENDING_2 = register("road_railings_green_ending_2", new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_RAILINGS_GREEN_POLE = register("road_railings_green_pole", new RoadRailingsPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_RAILINGS_GREEN_OBLIQUE = register("road_railings_green_oblique", new RoadRailingsOblique(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block ROAD_CLOSED_BARRICADE_GUARDRAIL_1 = register("road_closed_barricade_guardrail_1", new RoadClosedBarricadeGuardrail1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_CLOSED_BARRICADE_GUARDRAIL_2 = register("road_closed_barricade_guardrail_2", new RoadClosedBarricadeGuardrail2(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block ROAD_POLE_FOUNDATIONS = register("road_pole_foundations", new RoadPoleFoundations(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_POLE_LONGITUDINAL = register("road_pole_longitudinal", new RoadPoleLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_POLE_HORIZONTAL = register("road_pole_horizontal",new RoadPoleHorizontal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_POLE_TSHAPE = register("road_pole_tshape",new RoadPoleTshape(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_LIGHT = register("road_light",new RoadLight(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block ROAD_BLOCK = register("road_block", new Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_FULL_OF_WHITE = register("road_full_of_white", new Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_FULL_OF_YELLOW = register("road_full_of_yellow", new Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WHITE_YELLOW = register("road_white_yellow", new Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_LINE = register("road_with_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_DOUBLE_LINE = register("road_with_white_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_THICK_LINE = register("road_with_white_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_LINE = register("road_with_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_DOUBLE_LINE = register("road_with_yellow_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_THICK_LINE = register("road_with_yellow_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE = register("road_with_white_yellow_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_HALF_DOUBLE_LINE = register("road_with_white_half_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_HALF_DOUBLE_LINE = register("road_with_yellow_half_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_LINE = register("road_with_white_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_LINE = register("road_with_yellow_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_RIGHTANGLE_LINE = register("road_with_white_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_RIGHTANGLE_LINE = register("road_with_yellow_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE = register("road_with_white_yellow_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE = register("road_with_whitethick_normal_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE = register("road_with_whitethick_yellow_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE = register("road_with_whitethick_yellowdouble_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE = register("road_with_white_yellowdouble_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_BEVEL_LINE = register("road_with_white_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE = register("road_with_white_bevel_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_BEVEL_THICK_LINE = register("road_with_white_bevel_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE = register("road_with_white_offset_out_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE = register("road_with_white_offset_in_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_BEVEL_LINE = register("road_with_yellow_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE = register("road_with_yellow_bevel_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_BEVEL_THICK_LINE = register("road_with_yellow_bevel_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE = register("road_with_yellow_offset_out_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE = register("road_with_yellow_offset_in_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT = register("road_with_white_rightangle_line_offset_out", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN = register("road_with_white_rightangle_line_offset_in", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE = register("road_with_white_offset_out_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE = register("road_with_white_offset_in_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT = register("road_with_yellow_rightangle_line_offset_out", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN = register("road_with_yellow_rightangle_line_offset_in", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE = register("road_with_yellow_offset_out_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE = register("road_with_yellow_offset_in_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_LINE = register("road_with_white_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_LINE = register("road_with_yellow_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE = register("road_with_white_tshape_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_THICK_LINE = register("road_with_white_tshape_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE = register("road_with_white_double_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_THICK_TSHAPE_LINE = register("road_with_white_thick_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE = register("road_with_white_tshape_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE = register("road_with_yellow_tshape_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE = register("road_with_white_tshape_yellowdouble_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE = register("road_with_white_thick_tshape_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE = register("road_with_whitethick_tshape_yellowdouble_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE = register("road_with_white_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE = register("road_with_yellow_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE = register("road_with_whitedouble_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE = register("road_with_whitethick_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE = register("road_with_whitethick_tshape_offset_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE = register("road_with_yellow_tshape_offset_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE = register("road_with_white_tshape_offset_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_BEVEL_DB_LINE = register("road_with_white_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_BEVEL_DB_LINE = register("road_with_yellow_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE = register("road_with_whitenormal_and_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE = register("road_with_yellownormal_and_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITENORMAL_BEVEL_LINE = register("road_with_whitenormal_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWNORMAL_BEVEL_LINE = register("road_with_yellownormal_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE = register("road_with_whitenormal_bevel_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE = register("road_with_yellownormal_bevel_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_BEVEL_LINE = register("road_with_whitethick_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWTHICK_BEVEL_LINE = register("road_with_yellowthick_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE = register("road_with_whitethick_bevel_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE = register("road_with_yellowthick_bevel_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_CROSS_LINE = register("road_with_white_cross_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_CROSS_LINE = register("road_with_yellow_cross_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_AUTO_BEVEL_LINE = register("road_with_auto_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_AUTO_RIGHTANGLE_LINE = register("road_with_auto_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));

    public static final Block ROAD_POLE_TEXT_DISPLAY = register("road_pole_text_display", new RoadPolesTextDisplay(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_POLE_FLAG = register("road_pole_flag", new FlagBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static Block register(String id, Block block) {
        registerBlockItems(id, block);
        return Registry.register(Registries.BLOCK, new Identifier(YunbeiUrbanConstruction.MOD_ID, id), block);
    }

    public static void registerBlockItems(String id, Block block) {
        Registry.register(Registries.ITEM, new Identifier(YunbeiUrbanConstruction.MOD_ID, id),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {

    }
}