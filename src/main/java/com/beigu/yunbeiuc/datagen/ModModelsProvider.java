package com.beigu.yunbeiuc.datagen;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.RoadBlocks;
import com.beigu.yunbeiuc.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

public class ModModelsProvider extends FabricModelProvider {

    public ModModelsProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // Road Poles
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_POLE_FOUNDATIONS);
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_POLE_FOUNDATIONS_SLAB);
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_POLE_LONGITUDINAL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_POLE_HORIZONTAL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_POLE_TSHAPE);
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_POLE_LIGHT_FOUNDATIONS);
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_POLE_LIGHT_FOUNDATIONS_SLAB);
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_POLE_LIGHT_LONGITUDINAL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_POLE_LIGHT_BRANCH_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_POLE_LIGHT_BRANCH_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_POLE_LIGHT_BRANCH_3);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_SOLAR_PANEL);
// Road Monitoring Equipment
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_DETECTION_CAMERA);

// Road Safety Facilities
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.TRAFFIC_CONE);
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_COLLISION_BARREL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.WATER_SAFETY_BARRIER_RED);

// Road Markings & Barriers
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.VIBRATION_MARKING_LINE);

// Gantry Frames
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.GANTRY_FRAME_LADDER);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.GANTRY_FRAME_LED_MAIN);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.GANTRY_FRAME_DETECTION_CAMERA);

// Warning Network
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.WARNING_NETWORK);

// Anti-Glare Facilities
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ANTI_GLARE_NET);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ANTI_GLARE_VERSION);

// Traffic Barriers
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.TRAFFIC_BARRIER_GRAY_SLANT);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.TRAFFIC_BARRIER_GRAY_SLANT_YELLOW);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.TRAFFIC_BARRIER_GRAY_SLANT_RED);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.TRAFFIC_BARRIER_GRAY_SLANT_OBLIQUE);

// Road Warning Poles
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_WARNING_POLE_RED);
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_WARNING_POLE_YELLOW);
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.ROAD_WARNING_POLE_GREEN);

// Iron Horse Barriers
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.IRON_HORSE_YELLOW);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.IRON_HORSE_RED);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.IRON_HORSE_WHITE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.IRON_HORSE_GRAY);

// Reflective Signs
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.REFLECTIVE_SIGN_YELLOW_ALL_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.REFLECTIVE_SIGN_YELLOW_ALL_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.REFLECTIVE_SIGN_RED_ALL_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.REFLECTIVE_SIGN_RED_ALL_2);

// Instrument Poles
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.INSTRUMENT_POLE_FOUNDATIONS);
        blockStateModelGenerator.registerSimpleState(MunicipalBlocks.INSTRUMENT_POLE_LONGITUDINAL);

// Road Railings
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_IRON);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_IRON_ENDING_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_IRON_ENDING_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_IRON_POLE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_IRON_OBLIQUE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_GREEN);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_GREEN_ENDING_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_GREEN_ENDING_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_GREEN_POLE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_RAILINGS_GREEN_OBLIQUE);

// Road Facilities
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_FLOWER_BOX_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_FLOWER_BOX_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_FLOWER_BOX_2_FENCE);

// Road Closed Barricades
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_2);

// Safety Islands
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_3);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_4);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_GRAY);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_GRAY_OBLIQUE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_3);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_4);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_GRAY_SLAB);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE_OBLIQUE);

        blockStateModelGenerator.registerCubeAllModelTexturePool(RoadBlocks.ROAD_BLOCK);
        blockStateModelGenerator.registerSimpleState(RoadBlocks.ROAD_FULL_OF_WHITE);
        blockStateModelGenerator.registerSimpleState(RoadBlocks.ROAD_FULL_OF_YELLOW);
        blockStateModelGenerator.registerSimpleState(RoadBlocks.ROAD_WHITE_YELLOW);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_CROSS_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_CROSS_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_LEFT_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_RIGHT_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_DOUBLE_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_LEFT_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_YELLOW_RIGHT_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_THREE_SQUARE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_SIX_SQUARE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_WHITE_NINE_SQUARE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_AUTO_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.STRAIGHT_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.STRAIGHT_LEFT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.LEFT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.STRAIGHT_RIGHT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.RIGHT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.STRAIGHT_LEFT_RIGHT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.LEFT_TURN_AROUND_SINGLE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.STRAIGHT_LEFT_TURN_AROUND_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.LEFT_TURN_AROUND_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.LEFT_RIGHT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.LEFT_TURN_MERGE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.RIGHT_TURN_MERGE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.SLOWDOWN_ANNOUNCEMENT_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.DISTANCE_CONFIRMATION_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.TAXI_1_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.TAXI_2_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.VEHICLE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.DISABLED_PEOPLE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.NON_MOTOR_VEHICLES_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.BUS_1_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.BUS_2_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.SCHOOL_BUS_1_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.LARGE_SPEED_BUMP_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.SMALL_SPEED_BUMP_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.WHITE_040_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.WHITE_050_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.WHITE_060_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.WHITE_070_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.WHITE_080_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.WHITE_090_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.WHITE_100_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.WHITE_110_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.WHITE_120_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.YELLOW_040_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.YELLOW_050_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.YELLOW_060_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.YELLOW_070_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.YELLOW_080_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.YELLOW_090_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.YELLOW_100_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.YELLOW_110_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.YELLOW_120_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.ELECTRIC_VEHICLE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.PEDESTRIAN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.STOP_AND_YIELD_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.HOV_1_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.HOV_2_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.HOV_3_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.HOV_4_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.HOV_5_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(RoadBlocks.MANHOLE_COVER);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(MunicipalBlocks.ROAD_POLE_TEXT_DISPLAY);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.WAND, Models.GENERATED);
        itemModelGenerator.register(ModItems.TREE_WAND, Models.GENERATED);
        itemModelGenerator.register(ModItems.WATER_WAND, Models.GENERATED);
        itemModelGenerator.register(ModItems.ROTATED_WAND, Models.GENERATED);
        itemModelGenerator.register(ModItems.LINK_WAND, Models.GENERATED);
    }
}
