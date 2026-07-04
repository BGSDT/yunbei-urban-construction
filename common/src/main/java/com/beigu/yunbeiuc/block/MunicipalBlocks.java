package com.beigu.yunbeiuc.block;

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
import com.beigu.yunbeiuc.block.custom.sound.SoundBarrier1;
import com.beigu.yunbeiuc.block.custom.sound.SoundBarrier2;
import com.beigu.yunbeiuc.block.custom.waring.WarningNetwork;
import com.beigu.yunbeiuc.block.custom.waring.WarningNetworkPole;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.registry.RegistryKeys;
import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class MunicipalBlocks {
public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(YunbeiUrbanConstruction.MOD_ID, RegistryKeys.BLOCK);
    public static final RegistrySupplier<Block> ROAD_POLE_FOUNDATIONS = BLOCKS.register("road_pole_foundations", () -> new RoadPoleFoundations(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_FOUNDATIONS_SLAB = BLOCKS.register("road_pole_foundations_slab", () -> new RoadPoleFoundationsSlab(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_LONGITUDINAL = BLOCKS.register("road_pole_longitudinal", () -> new RoadPoleLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_HORIZONTAL = BLOCKS.register("road_pole_horizontal", () -> new RoadPoleHorizontal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_TSHAPE = BLOCKS.register("road_pole_tshape", () -> new RoadPoleHorizontal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_TEXT_DISPLAY = BLOCKS.register("road_pole_text_display", () -> new RoadPoleTextDisplay(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_FLAG = BLOCKS.register("road_pole_flag", () -> new RoadPoleFlag(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_LIGHT_FOUNDATIONS = BLOCKS.register("road_pole_light_foundations", () -> new RoadPoleLightFoundations(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_LIGHT_FOUNDATIONS_SLAB = BLOCKS.register("road_pole_light_foundations_slab", () -> new RoadPoleLightFoundationsSlab(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_LIGHT_LONGITUDINAL = BLOCKS.register("road_pole_light_longitudinal", () -> new RoadPoleLightLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_LIGHT_BRANCH_1 = BLOCKS.register("road_pole_light_branch_1", () -> new RoadPoleLightBranch(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_LIGHT_BRANCH_2 = BLOCKS.register("road_pole_light_branch_2", () -> new RoadPoleLightBranch(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final RegistrySupplier<Block> ROAD_POLE_LIGHT_BRANCH_3 = BLOCKS.register("road_pole_light_branch_3", () -> new RoadPoleLightBranch(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final RegistrySupplier<Block> ROAD_LIGHT_HIGH_MAST = BLOCKS.register("road_light_high_mast", () -> new RoadLightHighMast(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final RegistrySupplier<Block> ROAD_LIGHT_1 = BLOCKS.register("road_light_1", () -> new RoadLight(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final RegistrySupplier<Block> ROAD_LIGHT_2 = BLOCKS.register("road_light_2", () -> new RoadLight(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final RegistrySupplier<Block> ROAD_SOLAR_PANEL = BLOCKS.register("road_solar_panel", () -> new RoadSolarPanel(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));

    public static final RegistrySupplier<Block> ROAD_DETECTION_CAMERA = BLOCKS.register("road_detection_camera", () -> new RoadDetectionCamera(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ROAD_LIGHTING_LAMP = BLOCKS.register("road_lighting_lamp", () -> new RoadLightingLamp(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ROAD_RADAR_SPEED_DETECTOR = BLOCKS.register("road_radar_speed_detector", () -> new RoadRadarSpeedDetector(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> TRAFFIC_LIGHTS_STRAIGHT = BLOCKS.register("traffic_lights_straight", () -> new TrafficLightsBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).luminance(state -> 15).requiresTool()));
    public static final RegistrySupplier<Block> TRAFFIC_LIGHTS_LEFT = BLOCKS.register("traffic_lights_left", () -> new TrafficLightsBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).luminance(state -> 15).requiresTool()));
    public static final RegistrySupplier<Block> TRAFFIC_LIGHTS_PAVEMENT = BLOCKS.register("traffic_lights_pavement", () -> new TrafficLightsPavement(AbstractBlock.Settings.create().strength(1.25F, 4.2F).luminance(state -> 15).requiresTool()));

    public static final RegistrySupplier<Block> TRAFFIC_CONE = BLOCKS.register("traffic_cone", () -> new TrafficCone(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ROAD_COLLISION_BARREL = BLOCKS.register("road_collision_barrel", () -> new RoadCollisionBarrel(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> WATER_SAFETY_BARRIER_RED = BLOCKS.register("water_safety_barrier_red", () -> new WaterSafetyBarrier(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> SPEED_BUMP = BLOCKS.register("speed_bump", () -> new SpeedBump(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> VIBRATION_MARKING_LINE = BLOCKS.register("vibration_marking_line", () -> new VibrationMarkingLine(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> PARKING_SPACE_BARRIER = BLOCKS.register("parking_space_barrier", () -> new ParkingSpaceBarrier(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> GANTRY_FRAME_SIDE = BLOCKS.register("gantry_frame_side", () -> new GantryFrameSide(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> GANTRY_FRAME_CONNECTION = BLOCKS.register("gantry_frame_connection", () -> new GantryFrameConnection(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> GANTRY_FRAME_MAIN = BLOCKS.register("gantry_frame_main", () -> new GantryFrameMain(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> GANTRY_FRAME_RAILING = BLOCKS.register("gantry_frame_railing", () -> new GantryFrameRailing(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> GANTRY_FRAME_LADDER = BLOCKS.register("gantry_frame_ladder", () -> new GantryFrameLadder(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> GANTRY_FRAME_LED_SIDE = BLOCKS.register("gantry_frame_led_side", () -> new GantryFrameLedSide(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> GANTRY_FRAME_LED_MAIN = BLOCKS.register("gantry_frame_led_main", () -> new GantryFrameLedMain(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> GANTRY_FRAME_LED = BLOCKS.register("gantry_frame_led", () -> new GantryFrameLed(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> GANTRY_FRAME_DETECTION_CAMERA = BLOCKS.register("gantry_frame_detection_camera", () -> new GantryFrameDetectionCamera(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> GANTRY_FRAME_LIGHTING_LAMP = BLOCKS.register("gantry_frame_lighting_lamp", () -> new GantryFrameLightingLamp(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> GANTRY_FRAME_RADAR_SPEED_DETECTOR = BLOCKS.register("gantry_frame_radar_speed_detector", () -> new GantryFrameRadarSpeedDetector(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> WARNING_NETWORK = BLOCKS.register("warning_network", () -> new WarningNetwork(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> WARNING_NETWORK_POLE = BLOCKS.register("warning_network_pole", () -> new WarningNetworkPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final RegistrySupplier<Block> ANTI_GLARE_NET = BLOCKS.register("anti_glare_net", () -> new AntiGlareNet(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ANTI_GLARE_NET_POLE = BLOCKS.register("anti_glare_net_pole", () -> new AntiGlareNetPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ANTI_GLARE_VERSION = BLOCKS.register("anti_glare_version", () -> new AntiGlareVersion(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> TRAFFIC_BARRIER = BLOCKS.register("traffic_barrier", () -> new TrafficBarrierBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_YELLOW_DOUBLE = BLOCKS.register("traffic_barrier_yellow_double", () -> new TrafficBarrierDoubleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_YELLOW = BLOCKS.register("traffic_barrier_yellow", () -> new TrafficBarrierBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_RED = BLOCKS.register("traffic_barrier_red", () -> new TrafficBarrierBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_RED_DOUBLE = BLOCKS.register("traffic_barrier_red_double", () -> new TrafficBarrierDoubleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_OBLIQUE = BLOCKS.register("traffic_barrier_oblique", () -> new TrafficBarrierObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY = BLOCKS.register("traffic_barrier_gray", () -> new TrafficBarrierGrayBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY_OBLIQUE = BLOCKS.register("traffic_barrier_gray_oblique", () -> new TrafficBarrierGrayObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY_RED = BLOCKS.register("traffic_barrier_gray_red", () -> new TrafficBarrierGrayBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY_RED_OBLIQUE = BLOCKS.register("traffic_barrier_gray_red_oblique", () -> new TrafficBarrierObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY_YELLOW = BLOCKS.register("traffic_barrier_gray_yellow", () -> new TrafficBarrierGrayBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY_YELLOW_OBLIQUE = BLOCKS.register("traffic_barrier_gray_yellow_oblique", () -> new TrafficBarrierObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY_SLANT = BLOCKS.register("traffic_barrier_gray_slant", () -> new TrafficBarrierGraySlantBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY_SLANT_YELLOW = BLOCKS.register("traffic_barrier_gray_slant_yellow", () -> new TrafficBarrierGraySlantBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY_SLANT_RED = BLOCKS.register("traffic_barrier_gray_slant_red", () -> new TrafficBarrierGraySlantBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> TRAFFIC_BARRIER_GRAY_SLANT_OBLIQUE = BLOCKS.register("traffic_barrier_gray_slant_oblique", () -> new TrafficBarrierGraySlantBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> REFLECTIVE_SIGN_YELLOW_ALL_1 = BLOCKS.register("reflective_sign_yellow_all_1", () -> new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> REFLECTIVE_SIGN_YELLOW_ALL_2 = BLOCKS.register("reflective_sign_yellow_all_2", () -> new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> REFLECTIVE_SIGN_RED_ALL_1 = BLOCKS.register("reflective_sign_red_all_1", () -> new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> REFLECTIVE_SIGN_RED_ALL_2 = BLOCKS.register("reflective_sign_red_all_2", () -> new ReflectiveSign(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_1 = BLOCKS.register("safety_island_yellow_1", () -> new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_2 = BLOCKS.register("safety_island_yellow_2", () -> new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_3 = BLOCKS.register("safety_island_yellow_3", () -> new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_4 = BLOCKS.register("safety_island_yellow_4", () -> new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_GRAY = BLOCKS.register("safety_island_gray", () -> new SafetyIslandBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_OBLIQUE_1 = BLOCKS.register("safety_island_yellow_oblique_1", () -> new SafetyIslandObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_OBLIQUE_2 = BLOCKS.register("safety_island_yellow_oblique_2", () -> new SafetyIslandObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_GRAY_OBLIQUE = BLOCKS.register("safety_island_gray_oblique", () -> new SafetyIslandObliqueBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_SLAB_EDGE_1 = BLOCKS.register("safety_island_yellow_slab_edge_1", () -> new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_SLAB_EDGE_2 = BLOCKS.register("safety_island_yellow_slab_edge_2", () -> new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_SLAB_EDGE_3 = BLOCKS.register("safety_island_yellow_slab_edge_3", () -> new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_SLAB_EDGE_4 = BLOCKS.register("safety_island_yellow_slab_edge_4", () -> new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_GRAY_SLAB_EDGE = BLOCKS.register("safety_island_gray_slab_edge", () -> new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_1 = BLOCKS.register("safety_island_yellow_slab_edge_oblique_1", () -> new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_2 = BLOCKS.register("safety_island_yellow_slab_edge_oblique_2", () -> new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_GRAY_SLAB = BLOCKS.register("safety_island_gray_slab", () -> new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> SAFETY_ISLAND_GRAY_SLAB_EDGE_OBLIQUE = BLOCKS.register("safety_island_gray_slab_edge_oblique", () -> new SafetyIslandEdgeBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final RegistrySupplier<Block> SOUND_BARRIER_1_WHITE_NORMAL = BLOCKS.register("sound_barrier_1_white_normal", () -> new SoundBarrier1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_1_WHITE_TB = BLOCKS.register("sound_barrier_1_white_tb", () -> new SoundBarrier1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_1_BLUE_NORMAL = BLOCKS.register("sound_barrier_1_blue_normal", () -> new SoundBarrier1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_1_BLUE_TB = BLOCKS.register("sound_barrier_1_blue_tb", () -> new SoundBarrier1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_1_GREEN_NORMAL = BLOCKS.register("sound_barrier_1_green_normal", () -> new SoundBarrier1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_1_GREEN_TB = BLOCKS.register("sound_barrier_1_green_tb", () -> new SoundBarrier1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_2_NORMAL = BLOCKS.register("sound_barrier_2_normal", () -> new SoundBarrier2(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_2_TB = BLOCKS.register("sound_barrier_2_tb", () -> new SoundBarrier2(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_3_WHITE_NORMAL = BLOCKS.register("sound_barrier_3_white_normal", () -> new SoundBarrier2(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_3_WHITE_TB = BLOCKS.register("sound_barrier_3_white_tb", () -> new SoundBarrier2(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_3_BLUE_NORMAL = BLOCKS.register("sound_barrier_3_blue_normal", () -> new SoundBarrier2(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> SOUND_BARRIER_3_BLUE_TB = BLOCKS.register("sound_barrier_3_blue_tb", () -> new SoundBarrier2(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> ROAD_NAME_SIGN_RC = BLOCKS.register("road_name_sign_rc", () -> new RoadNameSignBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ROAD_NAME_SIGN_RA = BLOCKS.register("road_name_sign_ra", () -> new RoadNameSignBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ROAD_NAME_SIGN_POLE = BLOCKS.register("road_name_sign_pole", () -> new InstrumentPolelLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> INSTRUMENT_POLE_FOUNDATIONS = BLOCKS.register("instrument_pole_foundations", () -> new InstrumentPoleFoundations(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> INSTRUMENT_POLE_LONGITUDINAL = BLOCKS.register("instrument_pole_longitudinal", () -> new InstrumentPolelLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> INSTRUMENT_CAMERA = BLOCKS.register("instrument_camera", () -> new InstrumentCamera(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> INSTRUMENT_FEE_DISPLAY = BLOCKS.register("instrument_fee_display", () -> new InstrumentFeeDisplay(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final RegistrySupplier<Block> INSTRUMENT_LANE_INDICATOR = BLOCKS.register("instrument_lane_indicator", () -> new InstrumentLaneIndicator(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));

    public static final RegistrySupplier<Block> ROAD_RAILINGS_IRON = BLOCKS.register("road_railings_iron", () -> new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_RAILINGS_IRON_ENDING_1 = BLOCKS.register("road_railings_iron_ending_1", () -> new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_RAILINGS_IRON_ENDING_2 = BLOCKS.register("road_railings_iron_ending_2", () -> new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_RAILINGS_IRON_POLE = BLOCKS.register("road_railings_iron_pole", () -> new RoadRailingsPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_RAILINGS_IRON_OBLIQUE = BLOCKS.register("road_railings_iron_oblique", () -> new RoadRailingsOblique(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_RAILINGS_GREEN = BLOCKS.register("road_railings_green", () -> new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_RAILINGS_GREEN_ENDING_1 = BLOCKS.register("road_railings_green_ending_1", () -> new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_RAILINGS_GREEN_ENDING_2 = BLOCKS.register("road_railings_green_ending_2", () -> new RoadRailings(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_RAILINGS_GREEN_POLE = BLOCKS.register("road_railings_green_pole", () -> new RoadRailingsPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_RAILINGS_GREEN_OBLIQUE = BLOCKS.register("road_railings_green_oblique", () -> new RoadRailingsOblique(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final RegistrySupplier<Block> ROAD_FLOWER_BOX_1 = BLOCKS.register("road_flower_box_1", () -> new RoadFlowerBox1(AbstractBlock.Settings.create().strength( 1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ROAD_FLOWER_BOX_2 = BLOCKS.register("road_flower_box_2", () -> new DirectionBlock(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ROAD_FLOWER_BOX_2_FENCE = BLOCKS.register("road_flower_box_2_fence", () -> new RoadFlowerBox2Fence(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> RUBBISH_BIN_WHITE = BLOCKS.register("rubbish_bin_white", () -> new RubbishBinWhite(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> RUBBISH_BIN_GRAY_GREEN = BLOCKS.register("rubbish_bin_gray_green", () -> new RubbishBinGrayGreen(AbstractBlock.Settings.create().strength(1.25F, 4.2F).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> ROAD_CLOSED_BARRICADE_GUARDRAIL_1 = BLOCKS.register("road_closed_barricade_guardrail_1", () -> new RoadClosedBarricadeGuardrail1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_CLOSED_BARRICADE_GUARDRAIL_2 = BLOCKS.register("road_closed_barricade_guardrail_2", () -> new RoadClosedBarricadeGuardrail2(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_CLOSED_BARRICADE_GUARDRAIL_3 = BLOCKS.register("road_closed_barricade_guardrail_3", () -> new RoadClosedBarricadeGuardrail3(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final RegistrySupplier<Block> ROAD_CLOSED_BARRICADE_GUARDRAIL_4 = BLOCKS.register("road_closed_barricade_guardrail_4", () -> new RoadClosedBarricadeGuardrail4(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final RegistrySupplier<Block> ROAD_WARNING_POLE_RED = BLOCKS.register("road_warning_pole_red", () -> new RoadWarningPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ROAD_WARNING_POLE_YELLOW = BLOCKS.register("road_warning_pole_yellow", () -> new RoadWarningPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> ROAD_WARNING_POLE_GREEN = BLOCKS.register("road_warning_pole_green", () -> new RoadWarningPole(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> IRON_HORSE_YELLOW = BLOCKS.register("iron_horse_yellow", () -> new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> IRON_HORSE_RED = BLOCKS.register("iron_horse_red", () -> new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> IRON_HORSE_WHITE = BLOCKS.register("iron_horse_white", () -> new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> IRON_HORSE_GRAY = BLOCKS.register("iron_horse_gray", () -> new IronHorse(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static final RegistrySupplier<Block> BARRIER_GATE_1_MAIN = BLOCKS.register("barrier_gate_1_main", () -> new BarrierGate1Main(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> BARRIER_GATE_1_MAIN_SLAB = BLOCKS.register("barrier_gate_1_main_slab", () -> new BarrierGate1MainSlab(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> BARRIER_GATE_1_POLE_HORIZONTAL = BLOCKS.register("barrier_gate_1_pole_horizontal", () -> new BarrierGate1PoleHorizontal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));
    public static final RegistrySupplier<Block> BARRIER_GATE_1_POLE_LONGITUDINAL = BLOCKS.register("barrier_gate_1_pole_longitudinal", () -> new BarrierGate1PoleLongitudinal(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool().nonOpaque()));

    public static void init() {
        BLOCKS.register();
    }
}
