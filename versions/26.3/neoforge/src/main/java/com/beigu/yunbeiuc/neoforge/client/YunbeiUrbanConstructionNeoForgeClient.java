package com.beigu.yunbeiuc.neoforge.client;

import com.beigu.yunbeiuc.render.LinkWandRenderer;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import com.beigu.yunbeiuc.render.*;
import com.beigu.yunbeiuc.util.CustomFontManager;
import com.beigu.yunbeiuc.util.FlagLoader;
import com.beigu.yunbeiuc.util.PresetManager;
import com.beigu.yunbeiuc.util.TrafficLightsPatternCategoryManager;
import com.beigu.yunbeiuc.util.TrafficLightsPatternPresetLoader;
import com.beigu.yunbeiuc.util.TrafficLightsPatternPresetManager;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

public class YunbeiUrbanConstructionNeoForgeClient {

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_EXIT_4_ENTITY.get(), SignExpresswayExit4EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.ROAD_POLE_TEXT_DISPLAY_ENTITY.get(), RoadPoleTextDisplayEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.FLAG_BLOCK_ENTITY.get(), FlagBlockEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.ROAD_NAME_SIGN_BLOCK_ENTITY.get(), RoadNameSignBlockEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.TRAFFIC_LIGHTS_BLOCK_ENTITY.get(), TrafficLightsBlockEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLOCK_ENTITY.get(), TrafficLightsPavementIntegrationBlockEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.INSTRUMENT_WARNING_CROSSING_FLUORESCENT_ENTITY.get(), TrafficLightsBlockEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.GANTRY_FRAME_LED_ENTITY.get(), GantryFrameLedEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.ROAD_POLE_LED_ENTITY.get(), RoadPoleLedEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_ENTITY.get(), SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_ENTITY.get(), SignGuideIntersectionAdvanceWarning1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_ENTITY.get(), SignGuideIntersectionAdvanceWarning3EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5_ENTITY.get(), SignGuideIntersectionAdvanceWarning5EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6_ENTITY.get(), SignGuideIntersectionAdvanceWarning6EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7_ENTITY.get(), SignGuideIntersectionAdvanceWarning7EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_WARNING_1_ENTITY.get(), SignGuideIntersectionWarning1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_INTERSECTION_WARNING_4_ENTITY.get(), SignGuideIntersectionWarning4EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_CONFIRMATION_1_ENTITY.get(), SignGuideConfirmation1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_LANE_INDICATOR_1_ENTITY.get(), SignGuideLaneIndicator1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_GUIDE_ROADSIDE_FACILITY_OVERLOAD_CHECKPOINT_1_ENTITY.get(), SignGuideRoadsideFacilityOverloadCheckpoint1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_1_ENTITY.get(), SignExpresswayEntranceAdvance1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_4_ENTITY.get(), SignExpresswayEntranceAdvance4EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_7_ENTITY.get(), SignExpresswayEntranceAdvance7EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_10_ENTITY.get(), SignExpresswayEntranceAdvance10EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_13_ENTITY.get(), SignExpresswayEntranceAdvance13EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_1_ENTITY.get(), SignExpresswayDirection1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_3_ENTITY.get(), SignExpresswayDirection3EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_5_ENTITY.get(), SignExpresswayDirection5EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_NAMING_NUMBER_ENTITY.get(), SignExpresswayNamingNumberEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_ROAD_NAME_ENTITY.get(), SignExpresswayRoadNameEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_1_ENTITY.get(), SignExpresswayDistanceFromLocation1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_2_ENTITY.get(), SignExpresswayDistanceFromLocation2EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_3_ENTITY.get(), SignExpresswayDistanceFromLocation3EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_4_ENTITY.get(), SignExpresswayDistanceFromLocation4EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_5_ENTITY.get(), SignExpresswayDistanceFromLocation5EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_6_ENTITY.get(), SignExpresswayDistanceFromLocation6EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_EXIT_1_ENTITY.get(), SignExpresswayExit1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.SIGN_EXPRESSWAY_EXIT_8_ENTITY.get(), SignExpresswayExit8EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.ZONES_BOARD_1_ENTITY.get(), ZonesBoard1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.ZONES_BOARD_IMAGE_ENTITY.get(), ZonesBoardImageEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.ZONES_BOARD_TIME_RANGE_1_ENTITY.get(), ZonesBoardTimeRange1EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.ZONES_BOARD_TIME_RANGE_2_ENTITY.get(), ZonesBoardTimeRange2EntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.ZONES_BOARD_OVER_WEIGHT_ENTITY.get(), ZonesBoardOverWeightEntityRenderer::new);
            BlockEntityRendererRegistry.register(ModBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY.get(), CustomSignBlockEntityRenderer::new);

            FlagLoader.loadFlags(Minecraft.getInstance().getResourceManager());
            TrafficLightsPatternPresetLoader.loadPresets(Minecraft.getInstance().getResourceManager());
            CustomFontManager.getInstance().onResourceReload();
            PresetManager.load();
            TrafficLightsPatternPresetManager.load();
            TrafficLightsPatternCategoryManager.load();
        });

        NeoForge.EVENT_BUS.addListener(YunbeiUrbanConstructionNeoForgeClient::onAfterTranslucentBlocks);
    }

    private static void onAfterTranslucentBlocks(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        LinkWandRenderer.renderLinkedLightsOutline(event.getPoseStack(), Minecraft.getInstance().gameRenderer.mainCamera());
    }
}
