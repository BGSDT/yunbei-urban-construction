package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKeys;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BES =
            DeferredRegister.create(YunbeiUrbanConstruction.MOD_ID, RegistryKeys.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<RoadPoleTextDisplayEntity>> ROAD_POLE_TEXT_DISPLAY_ENTITY =
            BES.register("road_pole_text_display_entity",
                    () -> BlockEntityType.Builder.create(RoadPoleTextDisplayEntity::new,
                            MunicipalBlocks.ROAD_POLE_TEXT_DISPLAY.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<FlagBlockEntity>> FLAG_BLOCK_ENTITY =
            BES.register("flag_block_entity",
                    () -> BlockEntityType.Builder.create(FlagBlockEntity::new,
                            MunicipalBlocks.ROAD_POLE_FLAG.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<RoadNameSignBlockEntity>> ROAD_NAME_SIGN_BLOCK_ENTITY =
            BES.register("road_name_sign_block_entity",
                    () -> BlockEntityType.Builder.create(RoadNameSignBlockEntity::new,
                            MunicipalBlocks.ROAD_NAME_SIGN_RC.get(),
                            MunicipalBlocks.ROAD_NAME_SIGN_RA.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideIntersectionAdvanceWarning1WuhanEntity>> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_ENTITY =
            BES.register("sign_guide_intersection_advance_warning_1_wuhan_entity",
                    () -> BlockEntityType.Builder.create(SignGuideIntersectionAdvanceWarning1WuhanEntity::new,
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_LEFT.get(),
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_STRAIGHT.get(),
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_RIGHT.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideIntersectionAdvanceWarning1Entity>> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_ENTITY =
            BES.register("sign_guide_intersection_advance_warning_1_entity",
                    () -> BlockEntityType.Builder.create(SignGuideIntersectionAdvanceWarning1Entity::new,
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1.get(),
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideIntersectionAdvanceWarning3Entity>> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_ENTITY =
            BES.register("sign_guide_intersection_advance_warning_3_entity",
                    () -> BlockEntityType.Builder.create(SignGuideIntersectionAdvanceWarning3Entity::new,
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3.get(),
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideIntersectionAdvanceWarning5Entity>> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5_ENTITY =
            BES.register("sign_guide_intersection_advance_warning_5_entity",
                    () -> BlockEntityType.Builder.create(SignGuideIntersectionAdvanceWarning5Entity::new,
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideIntersectionAdvanceWarning6Entity>> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6_ENTITY =
            BES.register("sign_guide_intersection_advance_warning_6_entity",
                    () -> BlockEntityType.Builder.create(SignGuideIntersectionAdvanceWarning6Entity::new,
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6.get(),
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_8.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideIntersectionAdvanceWarning7Entity>> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7_ENTITY =
            BES.register("sign_guide_intersection_advance_warning_7_entity",
                    () -> BlockEntityType.Builder.create(SignGuideIntersectionAdvanceWarning7Entity::new,
                            SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideIntersectionWarning1Entity>> SIGN_GUIDE_INTERSECTION_WARNING_1_ENTITY =
            BES.register("sign_guide_intersection_warning_1_entity",
                    () -> BlockEntityType.Builder.create(SignGuideIntersectionWarning1Entity::new,
                            SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_1.get(),
                            SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_2.get(),
                            SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_3.get(),
                            SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_6.get(),
                            SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_1.get(),
                            SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_2.get(),
                            SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_3.get(),
                            SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_4.get(),
                            SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_5.get(),
                            SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_6.get(),
                            SignBlocks.SIGN_GUIDE_ODOMETER.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideIntersectionWarning4Entity>> SIGN_GUIDE_INTERSECTION_WARNING_4_ENTITY =
            BES.register("sign_guide_intersection_warning_4_entity",
                    () -> BlockEntityType.Builder.create(SignGuideIntersectionWarning4Entity::new,
                            SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_4.get(),
                            SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_5.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideConfirmation1Entity>> SIGN_GUIDE_CONFIRMATION_1_ENTITY =
            BES.register("sign_guide_confirmation_1_entity",
                    () -> BlockEntityType.Builder.create(SignGuideConfirmation1Entity::new,
                            SignBlocks.SIGN_GUIDE_CONFIRMATION_1.get(),
                            SignBlocks.SIGN_GUIDE_CONFIRMATION_2.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideLaneIndicator1Entity>> SIGN_GUIDE_LANE_INDICATOR_1_ENTITY =
            BES.register("sign_guide_lane_indicator_1_entity",
                    () -> BlockEntityType.Builder.create(SignGuideLaneIndicator1Entity::new,
                            SignBlocks.SIGN_GUIDE_LANE_INDICATOR_1.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignGuideRoadsideFacilityOverloadCheckpoint1Entity>> SIGN_GUIDE_ROADSIDE_FACILITY_OVERLOAD_CHECKPOINT_1_ENTITY =
            BES.register("sign_guide_roadside_facility_overload_checkpoint_1_entity",
                    () -> BlockEntityType.Builder.create(SignGuideRoadsideFacilityOverloadCheckpoint1Entity::new,
                            SignBlocks.SIGN_GUIDE_ROADSIDE_FACILITY_OVERLOAD_CHECKPOINT_1.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignExpresswayEntranceAdvance1Entity>> SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_1_ENTITY =
            BES.register("sign_expressway_entrance_advance_1_entity",
                    () -> BlockEntityType.Builder.create(SignExpresswayEntranceAdvance1Entity::new,
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_1.get(),
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_2.get(),
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_3.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignExpresswayEntranceAdvance4Entity>> SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_4_ENTITY =
            BES.register("sign_expressway_entrance_advance_4_entity",
                    () -> BlockEntityType.Builder.create(SignExpresswayEntranceAdvance4Entity::new,
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_4.get(),
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_5.get(),
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_6.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignExpresswayEntranceAdvance7Entity>> SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_7_ENTITY =
            BES.register("sign_expressway_entrance_advance_7_entity",
                    () -> BlockEntityType.Builder.create(SignExpresswayEntranceAdvance7Entity::new,
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_7.get(),
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_8.get(),
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_9.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignExpresswayEntranceAdvance10Entity>> SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_10_ENTITY =
            BES.register("sign_expressway_entrance_advance_10_entity",
                    () -> BlockEntityType.Builder.create(SignExpresswayEntranceAdvance10Entity::new,
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_10.get(),
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_11.get(),
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_12.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignExpresswayEntranceAdvance13Entity>> SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_13_ENTITY =
            BES.register("sign_expressway_entrance_advance_13_entity",
                    () -> BlockEntityType.Builder.create(SignExpresswayEntranceAdvance13Entity::new,
                            SignBlocks.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_13.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<SignExpresswayExit8Entity>> SIGN_EXPRESSWAY_EXIT_8_ENTITY =
            BES.register("sign_expressway_exit_8_entity",
                    () -> BlockEntityType.Builder.create(SignExpresswayExit8Entity::new,
                            SignBlocks.SIGN_EXPRESSWAY_EXIT_8.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ZonesBoard1Entity>> ZONES_BOARD_1_ENTITY =
            BES.register("zones_board_1_entity",
                    () -> BlockEntityType.Builder.create(ZonesBoard1Entity::new,
                            SignBlocks.ZONES_BOARD_RED.get(),
                            SignBlocks.ZONES_BOARD_YELLOW.get(),
                            SignBlocks.ZONES_BOARD_WHITE.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ZonesBoardImageEntity>> ZONES_BOARD_IMAGE_ENTITY =
            BES.register("zones_board_image_entity",
                    () -> BlockEntityType.Builder.create(ZonesBoardImageEntity::new,
                            SignBlocks.ZONES_BOARD_IMAGE.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ZonesBoardTimeRange1Entity>> ZONES_BOARD_TIME_RANGE_1_ENTITY =
            BES.register("zones_board_time_range_1_entity",
                    () -> BlockEntityType.Builder.create(ZonesBoardTimeRange1Entity::new,
                            SignBlocks.ZONES_BOARD_TIME_RANGE_1.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ZonesBoardTimeRange2Entity>> ZONES_BOARD_TIME_RANGE_2_ENTITY =
            BES.register("zones_board_time_range_2_entity",
                    () -> BlockEntityType.Builder.create(ZonesBoardTimeRange2Entity::new,
                            SignBlocks.ZONES_BOARD_TIME_RANGE_2.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ZonesBoardOverWeightEntity>> ZONES_BOARD_OVER_WEIGHT_ENTITY =
            BES.register("zones_board_over_weight_entity",
                    () -> BlockEntityType.Builder.create(ZonesBoardOverWeightEntity::new,
                            SignBlocks.ZONES_BOARD_OVER_WEIGHT.get(),
                            SignBlocks.ZONES_BOARD_TIME_LIMIT.get(),
                            SignBlocks.ZONES_BOARD_SUGGESTED_SPEED.get(),
                            SignBlocks.ZONES_BOARD_LENGTH.get(),
                            SignBlocks.ZONES_BOARD_DISTANCE_LENGTH.get(),
                            SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_LEFT.get(),
                            SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_RIGHT.get()).build(null));

    public static void init() {
        BES.register();
    }
}
