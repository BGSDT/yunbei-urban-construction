package com.beigu.yunbeiuc.fabric.client;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.RoadBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import com.beigu.yunbeiuc.render.*;
import com.beigu.yunbeiuc.util.PresetManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;


public final class YunbeiUrbanConstructionFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.STRAIGHT_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.STRAIGHT_LEFT_TURN_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.LEFT_TURN_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.STRAIGHT_RIGHT_TURN_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.RIGHT_TURN_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.STRAIGHT_LEFT_RIGHT_TURN_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.LEFT_TURN_AROUND_SINGLE_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.STRAIGHT_LEFT_TURN_AROUND_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.LEFT_TURN_AROUND_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.LEFT_RIGHT_TURN_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.LEFT_TURN_MERGE_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.RIGHT_TURN_MERGE_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.SLOWDOWN_ANNOUNCEMENT_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.DISTANCE_CONFIRMATION_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.TAXI_1_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.TAXI_2_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.VEHICLE_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.DISABLED_PEOPLE_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.NON_MOTOR_VEHICLES_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.BUS_1_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.BUS_2_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.SCHOOL_BUS_1_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.LARGE_SPEED_BUMP_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.SMALL_SPEED_BUMP_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.WHITE_040_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.WHITE_050_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.WHITE_060_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.WHITE_070_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.WHITE_080_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.WHITE_090_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.WHITE_100_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.WHITE_110_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.WHITE_120_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.YELLOW_040_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.YELLOW_050_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.YELLOW_060_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.YELLOW_070_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.YELLOW_080_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.YELLOW_090_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.YELLOW_100_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.YELLOW_110_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.YELLOW_120_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.ELECTRIC_VEHICLE_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.PEDESTRIAN_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.STOP_AND_YIELD_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.HOV_1_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.HOV_2_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.HOV_3_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.HOV_4_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.HOV_5_GROUND_MARK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RoadBlocks.MANHOLE_COVER.get(), RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.ROAD_FLOWER_BOX_1.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.ROAD_FLOWER_BOX_2.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.ROAD_FLOWER_BOX_2_FENCE.get(), RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.ANTI_GLARE_NET.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.ANTI_GLARE_NET_POLE.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.WARNING_NETWORK.get(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.WARNING_NETWORK_POLE.get(), RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_1_WHITE_NORMAL.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_1_WHITE_TB.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_1_BLUE_NORMAL.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_1_BLUE_TB.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_1_GREEN_NORMAL.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_1_GREEN_TB.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_2_NORMAL.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_2_TB.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_3_WHITE_NORMAL.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_3_WHITE_TB.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_3_BLUE_NORMAL.get(), RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(MunicipalBlocks.SOUND_BARRIER_3_BLUE_TB.get(), RenderLayer.getTranslucent());

        SignBlocks.BLOCKS.forEach(blockRegistrySupplier -> {
            BlockRenderLayerMap.INSTANCE.putBlock(blockRegistrySupplier.get(), RenderLayer.getCutout());
        });

        BlockEntityRendererFactories.register(ModBlockEntities.ROAD_POLE_TEXT_DISPLAY_ENTITY.get(), RoadPoleTextDisplayBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.FLAG_BLOCK_ENTITY.get(), FlagBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ROAD_NAME_SIGN_BLOCK_ENTITY.get(), RoadNameSignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.TRAFFIC_LIGHTS_BLOCK_ENTITY.get(), TrafficLightsBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_ENTITY.get(), SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_ENTITY.get(), SignGuideIntersectionAdvanceWarning1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_ENTITY.get(), SignGuideIntersectionAdvanceWarning3EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5_ENTITY.get(), SignGuideIntersectionAdvanceWarning5EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6_ENTITY.get(), SignGuideIntersectionAdvanceWarning6EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7_ENTITY.get(), SignGuideIntersectionAdvanceWarning7EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_WARNING_1_ENTITY.get(), SignGuideIntersectionWarning1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_WARNING_4_ENTITY.get(), SignGuideIntersectionWarning4EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_CONFIRMATION_1_ENTITY.get(), SignGuideConfirmation1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_LANE_INDICATOR_1_ENTITY.get(), SignGuideLaneIndicator1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_GUIDE_ROADSIDE_FACILITY_OVERLOAD_CHECKPOINT_1_ENTITY.get(), SignGuideRoadsideFacilityOverloadCheckpoint1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_1_ENTITY.get(), SignExpresswayEntranceAdvance1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_4_ENTITY.get(), SignExpresswayEntranceAdvance4EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_7_ENTITY.get(), SignExpresswayEntranceAdvance7EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_10_ENTITY.get(), SignExpresswayEntranceAdvance10EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_13_ENTITY.get(), SignExpresswayEntranceAdvance13EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_1_ENTITY.get(), SignExpresswayDirection1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_3_ENTITY.get(), SignExpresswayDirection3EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_5_ENTITY.get(), SignExpresswayDirection5EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_NAMING_NUMBER_ENTITY.get(), SignExpresswayNamingNumberEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_ROAD_NAME_ENTITY.get(), SignExpresswayRoadNameEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_1_ENTITY.get(), SignExpresswayDistanceFromLocation1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_2_ENTITY.get(), SignExpresswayDistanceFromLocation2EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_3_ENTITY.get(), SignExpresswayDistanceFromLocation3EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_4_ENTITY.get(), SignExpresswayDistanceFromLocation4EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_5_ENTITY.get(), SignExpresswayDistanceFromLocation5EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_6_ENTITY.get(), SignExpresswayDistanceFromLocation6EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SIGN_EXPRESSWAY_EXIT_8_ENTITY.get(), SignExpresswayExit8EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ZONES_BOARD_1_ENTITY.get(), ZonesBoard1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ZONES_BOARD_IMAGE_ENTITY.get(), ZonesBoardImageEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ZONES_BOARD_TIME_RANGE_1_ENTITY.get(), ZonesBoardTimeRange1EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ZONES_BOARD_TIME_RANGE_2_ENTITY.get(), ZonesBoardTimeRange2EntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ZONES_BOARD_OVER_WEIGHT_ENTITY.get(), ZonesBoardOverWeightEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY.get(), CustomSignBlockEntityRenderer::new);

        PresetManager.load();
    }
}
