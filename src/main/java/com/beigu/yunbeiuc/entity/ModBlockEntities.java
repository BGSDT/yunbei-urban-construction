package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<RoadPoleTextDisplayEntity> ROAD_POLE_TEXT_DISPLAY_ENTITY;
    public static BlockEntityType<FlagBlockEntity> FLAG_BLOCK_ENTITY;
    public static BlockEntityType<RoadNameSignBlockEntity> ROAD_NAME_SIGN_BLOCK_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning1WuhanEntity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning1Entity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning3Entity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning5Entity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning6Entity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6_ENTITY;
    public static BlockEntityType<SignGuideIntersectionAdvanceWarning7Entity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7_ENTITY;
    public static BlockEntityType<SignGuideIntersectionWarning1Entity>  SIGN_GUIDE_INTERSECTION_WARNING_1_ENTITY;
    public static BlockEntityType<SignGuideIntersectionWarning4Entity>  SIGN_GUIDE_INTERSECTION_WARNING_4_ENTITY;
    public static BlockEntityType<SignGuideConfirmation1Entity>  SIGN_GUIDE_CONFIRMATION_1_ENTITY;
    public static BlockEntityType<SignGuideLaneIndicator1Entity> SIGN_GUIDE_LANE_INDICATOR_1_ENTITY;
    public static BlockEntityType<SignExpresswayExit8Entity> SIGN_EXPRESSWAY_EXIT_8_ENTITY;
    public static BlockEntityType<ZonesBoard1Entity> ZONES_BOARD_1_ENTITY;
    public static BlockEntityType<ZonesBoard2Entity> ZONES_BOARD_2_ENTITY;
    public static void registerBlockEntities() {
        ROAD_POLE_TEXT_DISPLAY_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "road_pole_text_display_entity"),
                FabricBlockEntityTypeBuilder.create(RoadPoleTextDisplayEntity::new, MunicipalBlocks.ROAD_POLE_TEXT_DISPLAY).build()
        );

        FLAG_BLOCK_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "flag_block_entity"),
                FabricBlockEntityTypeBuilder.create(FlagBlockEntity::new, MunicipalBlocks.ROAD_POLE_FLAG).build()
        );

        ROAD_NAME_SIGN_BLOCK_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "road_name_sign_block_entity"),
                FabricBlockEntityTypeBuilder.create(RoadNameSignBlockEntity::new, MunicipalBlocks.ROAD_NAME_SIGN_RC, MunicipalBlocks.ROAD_NAME_SIGN_RA).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_1_wuhan_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning1WuhanEntity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_LEFT, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_STRAIGHT, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_RIGHT).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_1_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning1Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_3_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning3Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_5_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning5Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_6_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning6Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_8).build()
        );

        SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_advance_warning_7_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionAdvanceWarning7Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7).build()
        );

        SIGN_GUIDE_INTERSECTION_WARNING_1_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_warning_1_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionWarning1Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_1, SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_2, SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_3, SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_6).build()
        );

        SIGN_GUIDE_INTERSECTION_WARNING_4_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_intersection_warning_4_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideIntersectionWarning4Entity::new, SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_4, SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_5).build()
        );

        SIGN_GUIDE_CONFIRMATION_1_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_confirmation_1_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideConfirmation1Entity::new, SignBlocks.SIGN_GUIDE_CONFIRMATION_1, SignBlocks.SIGN_GUIDE_CONFIRMATION_2).build()
        );

        SIGN_GUIDE_LANE_INDICATOR_1_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_guide_lane_indicator_1_entity"),
                FabricBlockEntityTypeBuilder.create(SignGuideLaneIndicator1Entity::new, SignBlocks.SIGN_GUIDE_LANE_INDICATOR_1).build()
        );

        SIGN_EXPRESSWAY_EXIT_8_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "sign_expressway_exit_8_entity"),
                FabricBlockEntityTypeBuilder.create(SignExpresswayExit8Entity::new, SignBlocks.SIGN_EXPRESSWAY_EXIT_8).build()
        );

        ZONES_BOARD_1_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "zones_board_1_entity"),
                FabricBlockEntityTypeBuilder.create(ZonesBoard1Entity::new, SignBlocks.ZONES_BOARD_RED, SignBlocks.ZONES_BOARD_YELLOW, SignBlocks.ZONES_BOARD_WHITE).build()
        );

        ZONES_BOARD_2_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "zones_board_2_entity"),
                FabricBlockEntityTypeBuilder.create(ZonesBoard2Entity::new, SignBlocks.ZONES_BOARD_IMAGE).build()
        );
    }
}