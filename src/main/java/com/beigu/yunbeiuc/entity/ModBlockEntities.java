package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<RoadPoleTextDisplayEntity> ROAD_POLE_TEXT_DISPLAY_ENTITY;
    public static BlockEntityType<FlagBlockEntity> FLAG_BLOCK_ENTITY;
    public static BlockEntityType<RoadNameSignBlockEntity> ROAD_NAME_SIGN_BLOCK_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning1WuhanEntity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning1Entity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning3Entity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning5Entity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5_ENTITY;
    public static BlockEntityType<SignGuideLaneIndicator1Entity> SIGN_GUIDE_LANE_INDICATOR_1_ENTITY;
    public static void registerBlockEntities() {
        ROAD_POLE_TEXT_DISPLAY_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "road_pole_text_display_entity"),
                FabricBlockEntityTypeBuilder.create(RoadPoleTextDisplayEntity::new, MunicipalBlocks.ROAD_POLE_TEXT_DISPLAY).build()
        );

        FLAG_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "flag_block_entity"),
                FabricBlockEntityTypeBuilder.create(FlagBlockEntity::new, MunicipalBlocks.ROAD_POLE_FLAG).build()
        );

        ROAD_NAME_SIGN_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "road_name_sign_block_entity"),
                FabricBlockEntityTypeBuilder.create(RoadNameSignBlockEntity::new, MunicipalBlocks.ROAD_NAME_SIGN_RC, MunicipalBlocks.ROAD_NAME_SIGN_RA).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_1_wuhan_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning1WuhanEntity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_LEFT, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_STRAIGHT, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_RIGHT).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_1_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning1Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_3_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning3Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_5_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning5Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5).build()
        );

        SIGN_GUIDE_LANE_INDICATOR_1_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_lane_indicator_1_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideLaneIndicator1Entity::new, SignBlocks.SIGN_GUIDE_LANE_INDICATOR_1).build()
        );
    }
}