package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<SignSpeedLimitBlockEntity> SIGN_SPEED_LIMIT_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignSpeedLimitBlockEntity::new, ModBlocks.SIGN_SPEED_LIMIT_BLOCK).build(null);

    public static final BlockEntityType<SignCancelSpeedLimitBlockEntity> SIGN_CANCEL_SPEED_LIMIT_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignCancelSpeedLimitBlockEntity::new, ModBlocks.SIGN_CANCEL_SPEED_LIMIT_BLOCK).build(null);

    public static final BlockEntityType<SignNoEntryForVehiclesBlockEntity> SIGN_NO_ENTRY_FOR_VEHICLES_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignNoEntryForVehiclesBlockEntity::new, ModBlocks.SIGN_NO_ENTRY_FOR_VEHICLES_BLOCK).build(null);

    public static final BlockEntityType<SignNoDirectionBlockEntity> SIGN_NO_DIRECTION_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignNoDirectionBlockEntity::new, ModBlocks.SIGN_NO_DIRECTION_BLOCK).build(null);

    public static final BlockEntityType<SignHeightLimitBlockEntity> SIGN_HEIGHT_LIMIT_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignHeightLimitBlockEntity::new, ModBlocks.SIGN_HEIGHT_LIMIT_BLOCK).build(null);

    public static final BlockEntityType<SignWidthLimitBlockEntity> SIGN_WIDTH_LIMIT_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignWidthLimitBlockEntity::new, ModBlocks.SIGN_WIDTH_LIMIT_BLOCK).build(null);

    public static final BlockEntityType<SignWeightLimitBlockEntity> SIGN_WEIGHT_LIMIT_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignWeightLimitBlockEntity::new, ModBlocks.SIGN_WEIGHT_LIMIT_BLOCK).build(null);

    public static final BlockEntityType<SignNoSpecialBlockEntity> SIGN_NO_SPECIAL_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignNoSpecialBlockEntity::new, ModBlocks.SIGN_NO_SPECIAL_BLOCK).build(null);

    public static final BlockEntityType<SignIndicationDirectionBlockEntity> SIGN_INDICATION_DIRECTION_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignIndicationDirectionBlockEntity::new, ModBlocks.SIGN_INDICATION_DIRECTION_BLOCK).build(null);

    public static final BlockEntityType<SignIndicationLaneDirectionBlockEntity> SIGN_INDICATION_LANE_DIRECTION_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignIndicationLaneDirectionBlockEntity::new, ModBlocks.SIGN_INDICATION_LANE_DIRECTION_BLOCK).build(null);

    public static final BlockEntityType<SignGuideIntersectionAdvanceWarningBlockEntity> SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_BLOCK_ENTITY =
            BlockEntityType.Builder.create(SignGuideIntersectionAdvanceWarningBlockEntity::new, ModBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_BLOCK).build(null);

    public static BlockEntityType<SimpleSignEntity> SIMPLE_SIGN_ENTITY;
    public static BlockEntityType<RoadPolesTextDisplayEntity> ROAD_POLES_TEXT_DISPLAY_ENTITY;
    public static BlockEntityType<FlagBlockEntity> FLAG_BLOCK_ENTITY;
    public static BlockEntityType<CrashBarrierConcreteEntity> CRASH_BARRIER_CONCRETE_ENTITY;
    public static BlockEntityType<RoadRailingsIronEntity> ROAD_RAILINGS_IRON_ENTITY;
    public static BlockEntityType<RoadConstructionBarrierBlueEntity> ROAD_CONSTRUCTION_BARRIER_BLUE_ENTITY;
    public static BlockEntityType<TransformableBlockEntity> TRANSFORMABLE_BLOCK_ENTITY;
    public static void registerBlockEntities() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_speed_limit_block_entity"),
                SIGN_SPEED_LIMIT_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_cancel_speed_limit_block_entity"),
                SIGN_CANCEL_SPEED_LIMIT_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_no_entry_for_vehicles_block_entity"),
                SIGN_NO_ENTRY_FOR_VEHICLES_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_no_direction_block_entity"),
                SIGN_NO_DIRECTION_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_height_limit_block_entity"),
                SIGN_HEIGHT_LIMIT_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_width_limit_block_entity"),
                SIGN_WIDTH_LIMIT_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_weight_limit_block_entity"),
                SIGN_WEIGHT_LIMIT_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_special_block_entity"),
                SIGN_NO_SPECIAL_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_indication_direction_block_entity"),
                SIGN_INDICATION_DIRECTION_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_indication_lane_direction_block_entity"),
                SIGN_INDICATION_LANE_DIRECTION_BLOCK_ENTITY);

        Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign_guide_intersection_advance_warning_block_entity"),
                SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_BLOCK_ENTITY);

        SIMPLE_SIGN_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "simple_sign_entity"),
                FabricBlockEntityTypeBuilder.create(SimpleSignEntity::new, ModBlocks.SIMPLE_SIGN_BLOCK).build()
        );

        ROAD_POLES_TEXT_DISPLAY_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "road_poles_text_display_entity"),
                FabricBlockEntityTypeBuilder.create(RoadPolesTextDisplayEntity::new, ModBlocks.ROAD_POLES_TEXT_DISPLAY).build()
        );

        FLAG_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "flag_block_entity"),
                FabricBlockEntityTypeBuilder.create(FlagBlockEntity::new, ModBlocks.ROAD_POLES_FLAG).build()
        );

        CRASH_BARRIER_CONCRETE_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "crash_barrier_concrete_entity"),
                FabricBlockEntityTypeBuilder.create(CrashBarrierConcreteEntity::new, ModBlocks.CRASH_BARRIER_CONCRETE).build()
        );

        ROAD_RAILINGS_IRON_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "road_railings_iron_entity"),
                FabricBlockEntityTypeBuilder.create(RoadRailingsIronEntity::new, ModBlocks.ROAD_RAILINGS_IRON).build()
        );

        ROAD_CONSTRUCTION_BARRIER_BLUE_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("yunbeiuc", "road_construction_barrier_blue_entity"),
                FabricBlockEntityTypeBuilder.create(RoadConstructionBarrierBlueEntity::new, ModBlocks.ROAD_CONSTRUCTION_BARRIER_BLUE).build()
        );

        TRANSFORMABLE_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(YunbeiUrbanConstruction.MOD_ID, "transformable_block_entity"),
                FabricBlockEntityTypeBuilder.create(TransformableBlockEntity::new).build()
        );
    }
}