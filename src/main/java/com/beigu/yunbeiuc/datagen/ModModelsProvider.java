package com.beigu.yunbeiuc.datagen;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.block.custom.ParkingSpaceBarrier;
import com.beigu.yunbeiuc.block.custom.SpeedBump;
import com.beigu.yunbeiuc.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.minecraft.data.client.BlockStateModelGenerator.createFenceBlockState;

public class ModModelsProvider extends FabricModelProvider {

    public ModModelsProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_FLOWER_BOX_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_FLOWER_BOX_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_FLOWER_BOX_2_FENCE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_DETECTION_CAMERA);
        blockStateModelGenerator.registerSimpleState(ModBlocks.TRAFFIC_CONE);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ROAD_COLLISION_BARREL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WATER_SAFETY_BARRIER_RED);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.VIBRATION_MARKING_LINE);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.GANTRY_FRAME_LADDER);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.GANTRY_FRAME_LED_MAIN);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.GANTRY_FRAME_DETECTION_CAMERA);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ANTI_GLARE_NET);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ANTI_GLARE_VERSION);


        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_YELLOW);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_RED);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_OBLIQUE);

        blockStateModelGenerator.registerSimpleState(ModBlocks.ROAD_WARNING_POLE_RED);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ROAD_WARNING_POLE_YELLOW);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ROAD_WARNING_POLE_GREEN);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.IRON_HORSE_YELLOW);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.IRON_HORSE_RED);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.IRON_HORSE_WHITE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.IRON_HORSE_GRAY);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.REFLECTIVE_SIGN_RED_ALL_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.REFLECTIVE_SIGN_RED_ALL_2);

        blockStateModelGenerator.registerSimpleState(ModBlocks.INSTRUMENT_POLE_FOUNDATIONS);
        blockStateModelGenerator.registerSimpleState(ModBlocks.INSTRUMENT_POLE_LONGITUDINAL);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_3);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_4);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_GRAY);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_GRAY_OBLIQUE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_3);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_4);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_GRAY_SLAB);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE_OBLIQUE);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_IRON);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_IRON_ENDING_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_IRON_ENDING_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_IRON_POLE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_IRON_OBLIQUE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_GREEN);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_GREEN_ENDING_1);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_GREEN_ENDING_2);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_GREEN_POLE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_RAILINGS_GREEN_OBLIQUE);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WARNING_NETWORK);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_2);

        blockStateModelGenerator.registerSimpleState(ModBlocks.ROAD_POLE_FOUNDATIONS);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ROAD_POLE_LONGITUDINAL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_POLE_HORIZONTAL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_POLE_TSHAPE);

        blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.ROAD_BLOCK);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ROAD_FULL_OF_WHITE);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ROAD_FULL_OF_YELLOW);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ROAD_WHITE_YELLOW);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_TSHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_CROSS_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_CROSS_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_LEFT_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_RIGHT_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_DOUBLE_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_LEFT_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_DOUBLE_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_YELLOW_RIGHT_DIAMOND_SHAPE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_THREE_SQUARE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_SIX_SQUARE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_WHITE_NINE_SQUARE_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_AUTO_BEVEL_LINE);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE);

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.STRAIGHT_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.STRAIGHT_LEFT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.LEFT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.STRAIGHT_RIGHT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.RIGHT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.STRAIGHT_LEFT_RIGHT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.LEFT_TURN_AROUND_SINGLE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.STRAIGHT_LEFT_TURN_AROUND_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.LEFT_TURN_AROUND_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.LEFT_RIGHT_TURN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.LEFT_TURN_MERGE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.RIGHT_TURN_MERGE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SLOWDOWN_ANNOUNCEMENT_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.DISTANCE_CONFIRMATION_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.TAXI_1_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.TAXI_2_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.VEHICLE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.DISABLED_PEOPLE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.NON_MOTOR_VEHICLES_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.BUS_1_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.BUS_2_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SCHOOL_BUS_1_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.LARGE_SPEED_BUMP_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.SMALL_SPEED_BUMP_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WHITE_040_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WHITE_050_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WHITE_060_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WHITE_070_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WHITE_080_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WHITE_090_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WHITE_100_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WHITE_110_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.WHITE_120_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.YELLOW_040_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.YELLOW_050_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.YELLOW_060_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.YELLOW_070_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.YELLOW_080_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.YELLOW_090_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.YELLOW_100_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.YELLOW_110_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.YELLOW_120_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ELECTRIC_VEHICLE_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.PEDESTRIAN_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.STOP_AND_YIELD_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.HOV_1_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.HOV_2_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.HOV_3_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.HOV_4_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.HOV_5_GROUND_MARK);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.MANHOLE_COVER);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_POLE_TEXT_DISPLAY);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.ROAD_POLE_FLAG);
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
