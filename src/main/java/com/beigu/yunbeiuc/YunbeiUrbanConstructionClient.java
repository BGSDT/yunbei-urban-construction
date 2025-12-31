package com.beigu.yunbeiuc;

import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import com.beigu.yunbeiuc.render.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;


public class YunbeiUrbanConstructionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_SPEED_LIMIT_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_CANCEL_SPEED_LIMIT_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_NO_ENTRY_FOR_VEHICLES_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_NO_DIRECTION_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_HEIGHT_LIMIT_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_WIDTH_LIMIT_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_WEIGHT_LIMIT_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_NO_SPECIAL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_INDICATION_DIRECTION_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_INDICATION_LANE_DIRECTION_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANTI_GLARE_NET, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANTI_GLARE_NET_POLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_BLOCK, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(ModBlockEntities.SIMPLE_SIGN_ENTITY, SimpleSignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ROAD_POLES_TEXT_DISPLAY_ENTITY, RoadPolesTextDisplayBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.FLAG_BLOCK_ENTITY, FlagBlockEntityRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.CRASH_BARRIER_CONCRETE_ENTITY, CrashBarrierConcreteRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ROAD_RAILINGS_IRON_ENTITY, RoadRailingsIronRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ROAD_CONSTRUCTION_BARRIER_BLUE_ENTITY, RoadConstructionBarrierBlueRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.TRANSFORMABLE_BLOCK_ENTITY, TransformableBlockEntityRenderer::new
        );
    }
}
