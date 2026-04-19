package com.beigu.yunbeiuc.block;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.*;
import com.beigu.yunbeiuc.block.custom.anti.AntiGlareNet;
import com.beigu.yunbeiuc.block.custom.anti.AntiGlareNetPole;
import com.beigu.yunbeiuc.block.custom.anti.AntiGlareVersion;
import com.beigu.yunbeiuc.block.custom.barrier.*;
import com.beigu.yunbeiuc.block.custom.box.RoadFlowerBox1;
import com.beigu.yunbeiuc.block.custom.box.RoadFlowerBox2Fence;
import com.beigu.yunbeiuc.block.custom.gantry.*;
import com.beigu.yunbeiuc.block.custom.gate.BarrierGate1Main;
import com.beigu.yunbeiuc.block.custom.gate.BarrierGate1MainSlab;
import com.beigu.yunbeiuc.block.custom.gate.BarrierGate1PoleHorizontal;
import com.beigu.yunbeiuc.block.custom.gate.BarrierGate1PoleLongitudinal;
import com.beigu.yunbeiuc.block.custom.guardrail.RoadClosedBarricadeGuardrail2;
import com.beigu.yunbeiuc.block.custom.guardrail.RoadClosedBarricadeGuardrail3;
import com.beigu.yunbeiuc.block.custom.guardrail.RoadClosedBarricadeGuardrail4;
import com.beigu.yunbeiuc.block.custom.instrument.*;
import com.beigu.yunbeiuc.block.custom.island.SafetyIslandBlock;
import com.beigu.yunbeiuc.block.custom.island.SafetyIslandEdgeBlock;
import com.beigu.yunbeiuc.block.custom.island.SafetyIslandObliqueBlock;
import com.beigu.yunbeiuc.block.custom.pole.*;
import com.beigu.yunbeiuc.block.custom.guardrail.RoadClosedBarricadeGuardrail1;
import com.beigu.yunbeiuc.block.custom.railings.RoadRailings;
import com.beigu.yunbeiuc.block.custom.railings.RoadRailingsOblique;
import com.beigu.yunbeiuc.block.custom.railings.RoadRailingsPole;
import com.beigu.yunbeiuc.block.custom.rubbish.RubbishBinGrayGreen;
import com.beigu.yunbeiuc.block.custom.rubbish.RubbishBinWhite;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightsBlock;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightsPavement;
import com.beigu.yunbeiuc.block.custom.waring.WarningNetwork;
import com.beigu.yunbeiuc.block.custom.waring.WarningNetworkPole;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MunicipalBlocks {
    public static final Block ROAD_POLE_FOUNDATIONS = register("road_pole_foundations", new RoadPoleFoundations(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_POLE_LONGITUDINAL = register("road_pole_longitudinal", new RoadPoleLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_POLE_HORIZONTAL = register("road_pole_horizontal",new RoadPoleHorizontal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_POLE_TSHAPE = register("road_pole_tshape",new RoadPoleHorizontal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_LIGHT = register("road_light",new RoadLight(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_POLE_TEXT_DISPLAY = register("road_pole_text_display", new RoadPoleTextDisplay(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_POLE_FLAG = register("road_pole_flag", new RoadPoleFlag(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block ROAD_DETECTION_CAMERA = register("road_detection_camera",new RoadDetectionCamera(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_LIGHTING_LAMP = register("road_lighting_lamp",new RoadLightingLamp(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_RADAR_SPEED_DETECTOR = register("road_radar_speed_detector",new RoadRadarSpeedDetector(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block TRAFFIC_LIGHTS_STRAIGHT = register("traffic_lights_straight",new TrafficLightsBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).luminance(state -> 15).requiresTool()));
    public static final Block TRAFFIC_LIGHTS_LEFT = register("traffic_lights_left",new TrafficLightsBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).luminance(state -> 15).requiresTool()));
    public static final Block TRAFFIC_LIGHTS_PAVEMENT = register("traffic_lights_pavement",new TrafficLightsPavement(AbstractBlock.Settings.create().strength(1.25F, 4.2F).luminance(state -> 15).requiresTool()));

    public static final Block TRAFFIC_CONE = register("traffic_cone",new TrafficCone(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_COLLISION_BARREL = register("road_collision_barrel",new RoadCollisionBarrel(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block WATER_SAFETY_BARRIER_RED = register("water_safety_barrier_red",new WaterSafetyBarrier(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block SPEED_BUMP = register("speed_bump",new SpeedBump(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block VIBRATION_MARKING_LINE = register("vibration_marking_line",new VibrationMarkingLine(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block PARKING_SPACE_BARRIER = register("parking_space_barrier",new ParkingSpaceBarrier(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block GANTRY_FRAME_SIDE = register("gantry_frame_side", new GantryFrameSide(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_CONNECTION = register("gantry_frame_connection", new GantryFrameConnection(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_MAIN = register("gantry_frame_main", new GantryFrameMain(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_RAILING = register("gantry_frame_railing", new GantryFrameRailing(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_LADDER = register("gantry_frame_ladder", new GantryFrameLadder(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_LED_SIDE = register("gantry_frame_led_side", new GantryFrameLedSide(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_LED_MAIN = register("gantry_frame_led_main", new GantryFrameLedMain(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_LED = register("gantry_frame_led", new GantryFrameLed(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block GANTRY_FRAME_DETECTION_CAMERA = register("gantry_frame_detection_camera", new GantryFrameDetectionCamera(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_LIGHTING_LAMP = register("gantry_frame_lighting_lamp", new GantryFrameLightingLamp(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block GANTRY_FRAME_RADAR_SPEED_DETECTOR = register("gantry_frame_radar_speed_detector", new GantryFrameRadarSpeedDetector(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block WARNING_NETWORK = register("warning_network", new WarningNetwork(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WARNING_NETWORK_POLE = register("warning_network_pole", new WarningNetworkPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block ANTI_GLARE_NET = register("anti_glare_net", new AntiGlareNet(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block ANTI_GLARE_NET_POLE = register("anti_glare_net_pole", new AntiGlareNetPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block ANTI_GLARE_VERSION = register("anti_glare_version", new AntiGlareVersion(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block TRAFFIC_BARRIER = register("traffic_barrier", new TrafficBarrierBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_YELLOW_DOUBLE = register("traffic_barrier_yellow_double", new TrafficBarrierDoubleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block TRAFFIC_BARRIER_YELLOW = register("traffic_barrier_yellow", new TrafficBarrierBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
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

    public static final Block REFLECTIVE_SIGN_YELLOW_ALL_1 = register("reflective_sign_yellow_all_1", new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block REFLECTIVE_SIGN_YELLOW_ALL_2 = register("reflective_sign_yellow_all_2", new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block REFLECTIVE_SIGN_RED_ALL_1 = register("reflective_sign_red_all_1", new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block REFLECTIVE_SIGN_RED_ALL_2 = register("reflective_sign_red_all_2", new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

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

    public static final Block INSTRUMENT_POLE_FOUNDATIONS = register("instrument_pole_foundations", new InstrumentPoleFoundations(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block INSTRUMENT_POLE_LONGITUDINAL = register("instrument_pole_longitudinal", new InstrumentPolelLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block INSTRUMENT_CAMERA = register("instrument_camera", new InstrumentCamera(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block INSTRUMENT_FEE_DISPLAY = register("instrument_fee_display", new InstrumentFeeDisplay(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block INSTRUMENT_LANE_INDICATOR = register("instrument_lane_indicator", new InstrumentLaneIndicator(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));

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

    public static final Block ROAD_FLOWER_BOX_1 = register("road_flower_box_1",new RoadFlowerBox1(AbstractBlock.Settings.create().strength( 1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_FLOWER_BOX_2 = register("road_flower_box_2",new DirectionBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block ROAD_FLOWER_BOX_2_FENCE = register("road_flower_box_2_fence",new RoadFlowerBox2Fence(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block RUBBISH_BIN_WHITE = register("rubbish_bin_white",new RubbishBinWhite(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final Block RUBBISH_BIN_GRAY_GREEN = register("rubbish_bin_gray_green",new RubbishBinGrayGreen(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final Block ROAD_CLOSED_BARRICADE_GUARDRAIL_1 = register("road_closed_barricade_guardrail_1", new RoadClosedBarricadeGuardrail1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_CLOSED_BARRICADE_GUARDRAIL_2 = register("road_closed_barricade_guardrail_2", new RoadClosedBarricadeGuardrail2(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_CLOSED_BARRICADE_GUARDRAIL_3 = register("road_closed_barricade_guardrail_3", new RoadClosedBarricadeGuardrail3(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ROAD_CLOSED_BARRICADE_GUARDRAIL_4 = register("road_closed_barricade_guardrail_4", new RoadClosedBarricadeGuardrail4(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block ROAD_WARNING_POLE_RED = register("road_warning_pole_red", new RoadWarningPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block ROAD_WARNING_POLE_YELLOW = register("road_warning_pole_yellow", new RoadWarningPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block ROAD_WARNING_POLE_GREEN = register("road_warning_pole_green", new RoadWarningPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block IRON_HORSE_YELLOW = register("iron_horse_yellow", new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block IRON_HORSE_RED = register("iron_horse_red", new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block IRON_HORSE_WHITE = register("iron_horse_white", new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block IRON_HORSE_GRAY = register("iron_horse_gray", new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final Block BARRIER_GATE_1_MAIN = register("barrier_gate_1_main", new BarrierGate1Main(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block BARRIER_GATE_1_MAIN_SLAB = register("barrier_gate_1_main_slab", new BarrierGate1MainSlab(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block BARRIER_GATE_1_POLE_HORIZONTAL = register("barrier_gate_1_pole_horizontal", new BarrierGate1PoleHorizontal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final Block BARRIER_GATE_1_POLE_LONGITUDINAL = register("barrier_gate_1_pole_longitudinal", new BarrierGate1PoleLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

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