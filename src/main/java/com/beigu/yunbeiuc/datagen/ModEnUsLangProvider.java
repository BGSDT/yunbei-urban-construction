package com.beigu.yunbeiuc.datagen;

import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModEnUsLangProvider extends FabricLanguageProvider {
    public ModEnUsLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput,"en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.WAND,"Wand");
        translationBuilder.add(ModItems.TREE_WAND,"Tree Wand");
        translationBuilder.add(ModItems.LINK_WAND,"Link Wand");
        translationBuilder.add("item.yunbeiuc.tree_wand.tooltip","Right-click on Grass Block or Dirt to plant an Oak Tree");
        translationBuilder.add("item.yunbeiuc.tree_wand.success","Successfully planted an Oak Tree!");
        translationBuilder.add("item.yunbeiuc.tree_wand.planted_sapling","Planted an Oak Sapling!");
        translationBuilder.add("item.yunbeiuc.tree_wand.failed","Cannot generate Oak Tree here!");
        translationBuilder.add("item.yunbeiuc.tree_wand.no_space","Not enough space to plant a tree!");
        translationBuilder.add("item.yunbeiuc.tree_wand.invalid_block","Cannot plant trees on this block!");
        translationBuilder.add(ModItems.WATER_WAND,"Water Wand");
        translationBuilder.add("item.yunbeiuc.water_wand.tooltip","Right-click to replace a 3*3*3 area with Water Source");
        translationBuilder.add("item.yunbeiuc.water_wand.success","Successfully replaced with Water Source!");
        translationBuilder.add(ModItems.ROTATED_WAND,"Rotation Wand");
        translationBuilder.add("item.yunbeiuc.rotated_wand.tooltip","Right-click on a block to rotate it 90 degrees clockwise");

        // Road Markings & Ground Signs
        translationBuilder.add(ModBlocks.LEFT_TURN_GROUND_MARK, "Left Turn Ground Mark");
        translationBuilder.add(ModBlocks.STRAIGHT_GROUND_MARK, "Straight Ground Mark");
        translationBuilder.add(ModBlocks.RIGHT_TURN_GROUND_MARK, "Right Turn Ground Mark");
        translationBuilder.add(ModBlocks.LEFT_TURN_AROUND_GROUND_MARK, "Left Turn & U-Turn Ground Mark");
        translationBuilder.add(ModBlocks.RIGHT_TURN_AROUND_GROUND_MARK, "Right Turn & U-Turn Ground Mark");
        translationBuilder.add(ModBlocks.STRAIGHT_LEFT_TURN_GROUND_MARK, "Straight & Left Turn Ground Mark");
        translationBuilder.add(ModBlocks.STRAIGHT_RIGHT_TURN_GROUND_MARK, "Straight & Right Turn Ground Mark");
        translationBuilder.add(ModBlocks.STRAIGHT_LEFT_RIGHT_TURN_GROUND_MARK, "Straight, Left & Right Turn Ground Mark");
        translationBuilder.add(ModBlocks.LEFT_TURN_MERGE_GROUND_MARK, "Left Turn Merge Ground Mark");
        translationBuilder.add(ModBlocks.RIGHT_TURN_MERGE_GROUND_MARK, "Right Turn Merge Ground Mark");
        translationBuilder.add(ModBlocks.LEFT_TURN_AROUND_SINGLE_GROUND_MARK, "Left U-Turn Ground Mark");
        translationBuilder.add(ModBlocks.RIGHT_TURN_AROUND_SINGLE_GROUND_MARK, "Right U-Turn Ground Mark");
        translationBuilder.add(ModBlocks.SLOWDOWN_ANNOUNCEMENT_GROUND_MARK, "Slow Down Warning Ground Mark");
        translationBuilder.add(ModBlocks.SLOWDOWN_YIELD_GROUND_MARK, "Slow Down & Yield Ground Mark");
        translationBuilder.add(ModBlocks.MANHOLE_COVER, "Manhole Cover");

// Traffic Lights
        translationBuilder.add(ModBlocks.TRAFFIC_LIGHTS_STRAIGHT, "Straight Traffic Light");
        translationBuilder.add(ModBlocks.TRAFFIC_LIGHTS_LEFT, "Left Turn Traffic Light");
        translationBuilder.add(ModBlocks.TRAFFIC_LIGHTS_PAVEMENT, "Pedestrian Traffic Light");
        translationBuilder.add("block.yunbeiuc.traffic_lights.tooltip","Right-click with a Wand to toggle traffic light state");

// Road Facilities
        translationBuilder.add(ModBlocks.ROAD_FLOWER_BOX_1, "Road Flower Box Type 1");
        translationBuilder.add(ModBlocks.ROAD_FLOWER_BOX_2, "Road Flower Box Type 2");
        translationBuilder.add(ModBlocks.ROAD_FLOWER_BOX_2_FENCE, "Road Flower Box Type 2 Fence");

// Road Monitoring Equipment
        translationBuilder.add(ModBlocks.ROAD_DETECTION_CAMERA, "Road Detection/Speed Camera");
        translationBuilder.add(ModBlocks.ROAD_LIGHTING_LAMP, "Road Supplementary Light");
        translationBuilder.add("block.yunbeiuc.roadway_lighting_lamp.tooltip","Right-click with a Wand to toggle light state");
        translationBuilder.add(ModBlocks.ROAD_RADAR_SPEED_DETECTOR, "Radar Speed Detector");
        translationBuilder.add("block.yunbeiuc.radar_speed_detector.tooltip","Right-click with a Wand to toggle light state");

// Road Safety Facilities
        translationBuilder.add(ModBlocks.TRAFFIC_CONE, "Traffic Cone");
        translationBuilder.add(ModBlocks.ROAD_COLLISION_BARREL, "Road Crash Barrel");
        translationBuilder.add(ModBlocks.WATER_SAFETY_BARRIER_RED, "Red Water Safety Barrier");

// Sanitation Facilities
        translationBuilder.add(ModBlocks.RUBBISH_BIN_WHITE, "White Trash Can");
        translationBuilder.add(ModBlocks.RUBBISH_BIN_GRAY_GREEN, "Gray-Green Trash Can");
        translationBuilder.add("block.yunbeiuc.rubbish_bin.tooltip","Right-click with a Wand to empty the trash can");

// Road Markings & Barriers
        translationBuilder.add(ModBlocks.SPEED_BUMP, "Speed Bump");
        translationBuilder.add(ModBlocks.VIBRATION_MARKING_LINE, "Vibration Marking Line");
        translationBuilder.add(ModBlocks.PARKING_SPACE_BARRIER, "Parking Space Lock");

// Gantry Frames
        translationBuilder.add(ModBlocks.GANTRY_FRAME_SIDE, "Gantry Side Frame");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_CONNECTION, "Gantry Connector");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_MAIN, "Gantry Main Frame");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_RAILING, "Gantry Railing");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_LADDER, "Gantry Ladder");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_LED_SIDE, "Gantry Side LED Screen");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_LED_MAIN, "Gantry Main LED Screen");

// Anti-Glare Facilities
        translationBuilder.add(ModBlocks.ANTI_GLARE_NET, "Anti-Glare Net");
        translationBuilder.add(ModBlocks.ANTI_GLARE_NET_POLE, "Anti-Glare Net Pole");
        translationBuilder.add(ModBlocks.ANTI_GLARE_VERSION, "Anti-Glare Board");

// Traffic Barriers
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER, "Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_YELLOW_DOUBLE, "Yellow Double Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_YELLOW, "Yellow Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_RED, "Red Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_RED_DOUBLE, "Red Double Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_OBLIQUE, "Angled Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY, "Gray Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_OBLIQUE, "Gray Angled Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_RED, "Red-Gray Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_RED_OBLIQUE,"Red-Gray Angled Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_YELLOW, "Yellow-Gray Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_YELLOW_OBLIQUE, "Yellow-Gray Angled Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT, "Gray Slanted Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_YELLOW, "Yellow-Gray Slanted Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_RED, "Red-Gray Slanted Traffic Barrier");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_OBLIQUE, "Gray Slanted Angled Traffic Barrier");

// Road Warning Poles
        translationBuilder.add(ModBlocks.ROAD_WARNING_POLE_RED, "Red Road Warning Pole");
        translationBuilder.add(ModBlocks.ROAD_WARNING_POLE_YELLOW, "Yellow Road Warning Pole");
        translationBuilder.add(ModBlocks.ROAD_WARNING_POLE_GREEN, "Green Road Warning Pole");

// Iron Horse Barriers
        translationBuilder.add(ModBlocks.IRON_HORSE_YELLOW, "Yellow Crowd Control Barrier");
        translationBuilder.add(ModBlocks.IRON_HORSE_RED, "Red Crowd Control Barrier");
        translationBuilder.add(ModBlocks.IRON_HORSE_WHITE, "White Crowd Control Barrier");
        translationBuilder.add(ModBlocks.IRON_HORSE_GRAY, "Gray Crowd Control Barrier");

// Reflective Signs
        translationBuilder.add(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_1, "Yellow Full Reflective Sign Type 1");
        translationBuilder.add(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_2, "Yellow Full Reflective Sign Type 2");
        translationBuilder.add(ModBlocks.REFLECTIVE_SIGN_RED_ALL_1, "Red Full Reflective Sign Type 1");
        translationBuilder.add(ModBlocks.REFLECTIVE_SIGN_RED_ALL_2, "Red Full Reflective Sign Type 2");

// Instrument Poles
        translationBuilder.add(ModBlocks.INSTRUMENT_POLE_FOUNDATIONS, "Instrument Pole Foundation");
        translationBuilder.add(ModBlocks.INSTRUMENT_POLE_LONGITUDINAL, "Longitudinal Instrument Pole");
        translationBuilder.add(ModBlocks.INSTRUMENT_CAMERA, "Instrument Camera");

// Safety Islands
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_1, "Yellow Safety Island Type 1");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_2, "Yellow Safety Island Type 2");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_3, "Yellow Safety Island Type 3");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_4, "Yellow Safety Island Type 4");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY, "Gray Safety Island");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_1, "Yellow Angled Safety Island Type 1");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_2, "Yellow Angled Safety Island Type 2");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY_OBLIQUE, "Gray Angled Safety Island");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_1, "Yellow Safety Island Slab Edge Type 1");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_2, "Yellow Safety Island Slab Edge Type 2");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_3, "Yellow Safety Island Slab Edge Type 3");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_4, "Yellow Safety Island Slab Edge Type 4");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE, "Gray Safety Island Slab Edge");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_1, "Yellow Angled Safety Island Slab Edge Type 1");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_2, "Yellow Angled Safety Island Slab Edge Type 2");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB, "Gray Safety Island Slab");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE_OBLIQUE, "Gray Angled Safety Island Slab Edge");

// Road Railings
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON, "Iron Road Railing");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON_ENDING_1, "Iron Road Railing End Type 1");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON_ENDING_2, "Iron Road Railing End Type 2");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON_POLE, "Iron Road Railing Pole");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON_OBLIQUE, "Angled Iron Road Railing");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN, "Green Road Railing");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN_ENDING_1, "Green Road Railing End Type 1");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN_ENDING_2, "Green Road Railing End Type 2");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN_POLE, "Green Road Railing Pole");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN_OBLIQUE, "Angled Green Road Railing");

// Road Closed Barricades
        translationBuilder.add(ModBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_1, "Road Closed Barricade Type 1");
        translationBuilder.add(ModBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_2, "Road Closed Barricade Type 2");

// Road Poles
        translationBuilder.add(ModBlocks.ROAD_POLE_FOUNDATIONS, "Road Pole Foundation");
        translationBuilder.add(ModBlocks.ROAD_POLE_LONGITUDINAL, "Longitudinal Road Pole");
        translationBuilder.add(ModBlocks.ROAD_POLE_HORIZONTAL, "Horizontal Road Pole");
        translationBuilder.add(ModBlocks.ROAD_POLE_TSHAPE, "T-Shape Road Pole");
        translationBuilder.add(ModBlocks.ROAD_LIGHT, "Road Light");
        translationBuilder.add("block.yunbeiuc.road_light.tooltip","Right-click with a Wand to toggle light state");

// Road Blocks & Markings
        translationBuilder.add(ModBlocks.ROAD_BLOCK, "Road Block");
        translationBuilder.add(ModBlocks.ROAD_FULL_OF_WHITE,"All-White Road");
        translationBuilder.add(ModBlocks.ROAD_FULL_OF_YELLOW,"All-Yellow Road");
        translationBuilder.add(ModBlocks.ROAD_WHITE_YELLOW,"White-Yellow Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_LINE, "White Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_DOUBLE_LINE, "White Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_THICK_LINE, "White Thick Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_LINE, "Yellow Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE, "Yellow Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_THICK_LINE, "Yellow Thick Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE, "White-Yellow Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE, "White Half Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE, "Yellow Half Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_LINE, "White Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_LINE, "Yellow Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE, "White Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE, "Yellow Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE, "White-Yellow Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE, "White Thick + Normal Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE, "White Thick + Yellow Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE, "White Thick + Yellow Double Solid Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE, "White + Yellow Double Solid Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_LINE, "White Bevel Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE, "White Bevel Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE, "White Thick Bevel Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE, "White Outer Offset Bevel Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE, "White Inner Offset Bevel Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_LINE, "Yellow Bevel Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE, "Yellow Bevel Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE, "Yellow Thick Bevel Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE, "Yellow Outer Offset Bevel Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE, "Yellow Inner Offset Bevel Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT, "White Right Angle Outer Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN, "White Right Angle Inner Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE, "White Outer Offset Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE, "White Inner Offset Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT, "Yellow Right Angle Outer Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN, "Yellow Right Angle Inner Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE, "Yellow Outer Offset Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE, "Yellow Inner Offset Right Angle Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_LINE, "White T-Shape Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE, "Yellow T-Shape Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE, "White T-Shape Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE, "White Thick T-Shape Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE, "White Double Solid T-Shape Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE, "White Thick Solid T-Shape Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE, "White T-Shape + Yellow Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE, "Yellow T-Shape + White Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE, "White T-Shape + Yellow Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE, "White Thick T-Shape + Yellow Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE, "White Thick T-Shape + Yellow Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE, "White T-Shape Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE, "Yellow T-Shape Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE, "White Double Solid T-Shape Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE, "White Thick T-Shape Offset Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE, "White Thick T-Shape Offset + Yellow Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE, "Yellow T-Shape Offset + White Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE, "White T-Shape Offset + Yellow Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE, "White Bevel Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE, "Yellow Bevel Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE, "White Normal + Bevel Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE, "Yellow Normal + Bevel Double Solid Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE, "White Normal + Bevel Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE, "Yellow Normal + Bevel Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE, "White Normal + Bevel + Yellow Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE, "Yellow Normal + Bevel + White Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE, "White Thick + Bevel Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_LINE, "Yellow Thick + Bevel Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE, "White Thick + Bevel + Yellow Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE, "Yellow Thick + Bevel + White Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_CROSS_LINE, "White Cross Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_CROSS_LINE, "Yellow Cross Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_AUTO_BEVEL_LINE, "Auto Bevel Line Road");
        translationBuilder.add(ModBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE, "Auto Right Angle Line Road");

// Road Pole Attachments
        translationBuilder.add(ModBlocks.ROAD_POLE_TEXT_DISPLAY, "Road Pole Text Display");
        translationBuilder.add(ModBlocks.ROAD_POLE_FLAG, "Road Pole Flag");
        translationBuilder.add("text.yunbeiuc.flag_selection.title", "Flag Selection");
        translationBuilder.add("text.yunbeiuc.flag_selection.current_selection", "Selected: %s");
        translationBuilder.add("text.yunbeiuc.flag_selection.preview_panel", "Flag Preview");
        translationBuilder.add("text.yunbeiuc.flag_selection.save_button", "Save");
        translationBuilder.add("text.yunbeiuc.flag_selection.load_failed", "Load Failed");

        // Traffic Signs
        translationBuilder.add(SignBlocks.SIGN_STOP, "Stop Sign");
        translationBuilder.add(SignBlocks.SIGN_YIELD, "Yield Sign");
        translationBuilder.add(SignBlocks.SIGN_YIELD_TO_ONCOMING_TRAFFIC, "Yield to Oncoming Traffic Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_ALL, "No Entry Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_ENTRY, "Do Not Enter Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_MOTOR_VEHICLES, "No Motor Vehicles Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_LARGE_BUS, "No Large Buses Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_SMALL_PASSENGER_CAR, "No Small Passenger Cars Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_TRUCK, "No Trucks Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_TRAILER, "No Trailers/Semi-Trailers Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_TRACTOR, "No Tractors Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_THREE_WHEELED_VEHICLE, "No Three-Wheeled Vehicles/Low-Speed Trucks Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_MOTORCYCLE, "No Motorcycles Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_NON_MOTOR_VEHICLES, "No Non-Motor Vehicles Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_ELECTRIC_VEHICLE, "No E-Bikes Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_ANIMAL_DRAWN_CART, "No Animal-Drawn Carts Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_TRICYCLE, "No Tricycles Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_HUMAN_POWERED_PASSENGER_TRICYCLE, "No Pedicabs Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_HUMAN_POWERED_CARGO_TRICYCLE, "No Cargo Tricycles Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_RICKSHAW, "No Rickshaws Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_PEDESTRIAN, "No Pedestrians Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_LEFT_TURN, "No Left Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_STRAIGHT, "No Straight Ahead Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_RIGHT_TURN, "No Right Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_LEFT_RIGHT_TURN, "No Left or Right Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_STRAIGHT_LEFT_TURN, "No Straight or Left Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_STRAIGHT_RIGHT_TURN, "No Straight or Right Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_SINGLE_LEFT_TURN_AROUND, "No U-Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_OVERTAKE, "No Overtaking Sign");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_OVERTAKE, "End of No Overtaking Zone Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_PARKING, "No Parking Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_PARKING_LONG_TIME, "No Long Parking Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_HONK_HORN, "No Honking Sign");
        translationBuilder.add(SignBlocks.SIGN_WIDTH_LIMIT_20, "Width Limit: 2.0m");
        translationBuilder.add(SignBlocks.SIGN_WIDTH_LIMIT_25, "Width Limit: 2.5m");
        translationBuilder.add(SignBlocks.SIGN_WIDTH_LIMIT_30, "Width Limit: 3.0m");
        translationBuilder.add(SignBlocks.SIGN_WIDTH_LIMIT_35, "Width Limit: 3.5m");
        translationBuilder.add(SignBlocks.SIGN_WIDTH_LIMIT_40, "Width Limit: 4.0m");
        translationBuilder.add(SignBlocks.SIGN_WIDTH_LIMIT_45, "Width Limit: 4.5m");
        translationBuilder.add(SignBlocks.SIGN_HEIGHT_LIMIT_20, "Height Limit: 2.0m");
        translationBuilder.add(SignBlocks.SIGN_HEIGHT_LIMIT_25, "Height Limit: 2.5m");
        translationBuilder.add(SignBlocks.SIGN_HEIGHT_LIMIT_30, "Height Limit: 3.0m");
        translationBuilder.add(SignBlocks.SIGN_HEIGHT_LIMIT_35, "Height Limit: 3.5m");
        translationBuilder.add(SignBlocks.SIGN_HEIGHT_LIMIT_40, "Height Limit: 4.0m");
        translationBuilder.add(SignBlocks.SIGN_HEIGHT_LIMIT_45, "Height Limit: 4.5m");
        translationBuilder.add(SignBlocks.SIGN_WEIGHT_LIMIT_10, "Weight Limit: 10t");
        translationBuilder.add(SignBlocks.SIGN_WEIGHT_LIMIT_20, "Weight Limit: 20t");
        translationBuilder.add(SignBlocks.SIGN_WEIGHT_LIMIT_30, "Weight Limit: 30t");
        translationBuilder.add(SignBlocks.SIGN_WEIGHT_LIMIT_40, "Weight Limit: 40t");
        translationBuilder.add(SignBlocks.SIGN_ALEX_WEIGHT_LIMIT_10, "Axle Load Limit: 10t");
        translationBuilder.add(SignBlocks.SIGN_ALEX_WEIGHT_LIMIT_20, "Axle Load Limit: 20t");
        translationBuilder.add(SignBlocks.SIGN_ALEX_WEIGHT_LIMIT_30, "Axle Load Limit: 30t");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_005, "Speed Limit: 5 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_010, "Speed Limit: 10 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_020, "Speed Limit: 20 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_030, "Speed Limit: 30 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_040, "Speed Limit: 40 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_050, "Speed Limit: 50 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_060, "Speed Limit: 60 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_070, "Speed Limit: 70 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_080, "Speed Limit: 80 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_090, "Speed Limit: 90 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_100, "Speed Limit: 100 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_110, "Speed Limit: 110 km/h");
        translationBuilder.add(SignBlocks.SIGN_SPEED_LIMIT_120, "Speed Limit: 120 km/h");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_005, "End of 5 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_010, "End of 10 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_020, "End of 20 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_030, "End of 30 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_040, "End of 40 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_050, "End of 50 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_060, "End of 60 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_070, "End of 70 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_080, "End of 80 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_090, "End of 90 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_100, "End of 100 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_110, "End of 110 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_120, "End of 120 km/h Speed Limit");
        translationBuilder.add(SignBlocks.SIGN_CHECK, "Stop for Inspection Sign");
        translationBuilder.add(SignBlocks.SIGN_PORT_CHECK, "Port Inspection Sign");
        translationBuilder.add(SignBlocks.SIGN_NO_HAZARDOUS_MATERIALS_TRANSPORT_VEHICLE, "No Hazardous Materials Vehicles Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_STRAIGHT, "Straight Ahead Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LEFT_TURN, "Left Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_RIGHT_TURN, "Right Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_STRAIGHT_LEFT_TURN, "Straight or Left Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_STRAIGHT_RIGHT_TURN, "Straight or Right Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LEFT_RIGHT_TURN, "Left or Right Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_RIGHT_SIDE_MEDIAN_STRIP, "Keep Right of Median Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LEFT_SIDE_MEDIAN_STRIP, "Keep Left of Median Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_ROUNDABOUT, "Roundabout Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_ONE_WAY_STREET_LEFT_RIGHT, "One Way (Left/Right) Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_ONE_WAY_STREET_STRAIGHT, "One Way (Straight) Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_HONK_HORN, "Honk Horn Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_HEADLIGHTS, "Turn On Headlights Sign");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_005, "Minimum Speed: 5 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_010, "Minimum Speed: 10 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_020, "Minimum Speed: 20 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_030, "Minimum Speed: 30 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_040, "Minimum Speed: 40 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_050, "Minimum Speed: 50 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_060, "Minimum Speed: 60 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_070, "Minimum Speed: 70 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_080, "Minimum Speed: 80 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_090, "Minimum Speed: 90 km/h");
        translationBuilder.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_100, "Minimum Speed: 100 km/h");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_YIELD_TO_ONCOMING_TRAFFIC, "Priority Over Oncoming Traffic Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_ZEBRA_CROSSING, "Zebra Crossing Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_ZEBRA_CROSSING_FLUORESCENCE, "Fluorescent Zebra Crossing Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_RIGHT_TURN, "Right Turn Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LEFT_TURN, "Left Turn Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_STRAIGHT, "Straight Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_STRAIGHT_RIGHT_TURN, "Straight & Right Turn Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_STRAIGHT_LEFT_TURN, "Straight & Left Turn Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SINGLE_LEFT_TURN_AROUND, "U-Turn Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LEFT_TURN_AROUND, "U-Turn & Left Turn Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_MOTOR_VEHICLES, "Motor Vehicles Only Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES, "Motor Vehicle Lane Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES_STRAIGHT, "Motor Vehicle Lane Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES_LEFT_SIDE_MEDIAN_STRIP, "Motor Vehicle Lane Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES_RIGHT_SIDE_MEDIAN_STRIP, "Motor Vehicle Lane Sign 4");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR, "Small Passenger Car Lane Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR_STRAIGHT, "Small Passenger Car Lane Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR_LEFT_SIDE_MEDIAN_STRIP, "Small Passenger Car Lane Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR_RIGHT_SIDE_MEDIAN_STRIP, "Small Passenger Car Lane Sign 4");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS, "Bus Lane Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_STRAIGHT, "Bus Lane Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_LEFT_SIDE_MEDIAN_STRIP, "Bus Lane Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_RIGHT_SIDE_MEDIAN_STRIP, "Bus Lane Sign 4");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT, "Bus Lane (Text) Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT_STRAIGHT, "Bus Lane (Text) Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT_LEFT_SIDE_MEDIAN_STRIP, "Bus Lane (Text) Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT_RIGHT_SIDE_MEDIAN_STRIP, "Bus Lane (Text) Sign 4");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT, "BRT Lane Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT_STRAIGHT, "BRT Lane Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT_LEFT_SIDE_MEDIAN_STRIP, "BRT Lane Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT_RIGHT_SIDE_MEDIAN_STRIP, "BRT Lane Sign 4");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_TRAM_STRAIGHT, "Tram Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_HOV, "HOV Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_NON_MOTOR_VEHICLES, "Non-Motor Vehicles Only Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES, "Non-Motor Vehicle Lane Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES_STRAIGHT, "Non-Motor Vehicle Lane Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES_LEFT_SIDE_MEDIAN_STRIP, "Non-Motor Vehicle Lane Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES_RIGHT_SIDE_MEDIAN_STRIP, "Non-Motor Vehicle Lane Sign 4");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_ELECTRIC_VEHICLE, "E-Bikes Only Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE, "E-Bike Lane Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE_STRAIGHT, "E-Bike Lane Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE_LEFT_SIDE_MEDIAN_STRIP, "E-Bike Lane Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE_RIGHT_SIDE_MEDIAN_STRIP, "E-Bike Lane Sign 4");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PEDESTRIAN, "Pedestrians Only Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PEDESTRIAN_NON_MOTOR_VEHICLES_1, "Pedestrians & Non-Motor Vehicles Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PEDESTRIAN_NON_MOTOR_VEHICLES_2, "Pedestrians & Non-Motor Vehicles Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PEDESTRIAN_NON_MOTOR_VEHICLES_3, "Pedestrians & Non-Motor Vehicles Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PROMOTION_NON_MOTOR_VEHICLES, "Push Non-Motor Vehicles Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PROMOTION_DRIVE_RIGHT, "Keep Right Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_1, "Parking Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_2, "Parking Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_3, "Parking Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_4, "Parking Sign 4");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_DISABLED, "Disabled Parking Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_SCHOOL_BUS, "School Bus Parking/Stop Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_SCHOOL_BUS_WUXI, "Wuxi School Bus Stop Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_SCHOOL_BUS_FLUORESCENCE, "Fluorescent School Bus Parking/Stop Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_TAXI, "Taxi Parking Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_NON_MOTOR_VEHICLE, "Non-Motor Vehicle Parking Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_BUS, "Bus Parking Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_CHARGING, "Charging Parking Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PARKING_COMPANY, "Private Parking Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_TURN_AROUND, "U-Turn Allowed Sign");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PAVED_SHOULDER_1, "Paved Shoulder Allowed Sign 1");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PAVED_SHOULDER_2, "Paved Shoulder Allowed Sign 2");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_PAVED_SHOULDER_3, "Paved Shoulder Allowed Sign 3");
        translationBuilder.add(SignBlocks.SIGN_INDICATION_OK_TRUCK, "Trucks Allowed Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_1, "Crossroads Warning Sign 1");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_2, "Crossroads Warning Sign 2");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_3, "Crossroads Warning Sign 3");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_4, "Crossroads Warning Sign 4");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_5, "Crossroads Warning Sign 5");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_6, "Crossroads Warning Sign 6");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_7, "Crossroads Warning Sign 7");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_8, "Crossroads Warning Sign 8");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_9, "Crossroads Warning Sign 9");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_10, "Crossroads Warning Sign 10");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSING_11, "Crossroads Warning Sign 11");
        translationBuilder.add(SignBlocks.SIGN_WARNING_SHARP_TURN_1, "Sharp Left Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_SHARP_TURN_2, "Sharp Right Turn Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_REVERSE_DETOUR_1, "Reverse Curve Sign 1");
        translationBuilder.add(SignBlocks.SIGN_WARNING_REVERSE_DETOUR_2, "Reverse Curve Sign 2");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CONTINUOUS_WINDING_ROADS_1, "Winding Road Sign 1");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CONTINUOU_WINDING_ROADS_2, "Winding Road Sign 2");
        translationBuilder.add(SignBlocks.SIGN_WARNING_STEEP_SLOPE_UP, "Steep Upward Slope Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_STEEP_SLOPE_DOWN, "Steep Downward Slope Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CONTINUOUS_DOWNHILL, "Continuous Downhill Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_NARROW_ROAD_DOUBLE, "Road Narrows on Both Sides Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_NARROW_ROAD_LEFT, "Road Narrows on Left Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_NARROW_ROAD_RIGHT, "Road Narrows on Right Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_NARROW_BRIDGE, "Narrow Bridge Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_TWO_WAY_TRAFFIC, "Two-Way Traffic Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_ZEBRA_CROSSING, "Watch for Pedestrians Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_ZEBRA_CROSSING_FLUORESCENCE, "Fluorescent Watch for Pedestrians Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CHILDREN, "Watch for Children Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CHILDREN_FLUORESCENCE, "Fluorescent Watch for Children Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_DISABLED, "Watch for Disabled Persons Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_DISABLED_FLUORESCENCE, "Fluorescent Watch for Disabled Persons Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_NON_MOTOR_VEHICLES, "Watch for Non-Motor Vehicles Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_ELECTRIC_VEHICLE, "Watch for E-Bikes Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_LIVESTOCK, "Watch for Livestock Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_WILDLIFE, "Watch for Wildlife Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_TRAFFIC_LIGHTS, "Watch for Traffic Lights Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_FALLING_ROCKS_LEFT, "Falling Rocks on Left Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_FALLING_ROCKS_RIGHT, "Falling Rocks on Right Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CROSSWIND, "Crosswind Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_SLIPPERY, "Slippery Road Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_PERILOUS_ROAD_ALONG_THE_MOUNTAIN_LEFT, "Mountain Road on Left Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_PERILOUS_ROAD_ALONG_THE_MOUNTAIN_RIGHT, "Mountain Road on Right Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_EMBANKMENT_ROAD_LEFT, "Embankment Road Sign 1");
        translationBuilder.add(SignBlocks.SIGN_WARNING_EMBANKMENT_ROAD_RIGHT, "Embankment Road Sign 2");
        translationBuilder.add(SignBlocks.SIGN_WARNING_VILLAGE, "Village Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_TUNNEL, "Tunnel Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CAMEL_BACK_BRIDGE, "Hump Bridge Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_ROAD_UNEVEN, "Uneven Road Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_SPEED_BUMP, "Speed Bump Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_ROAD_WET, "Flooded Road Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_SOMEONE_GUARDING_THE_RAILWAY_CROSSING, "Railroad Crossing (Guarded) Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_UNMANNED_GUARDING_THE_RAILWAY_CROSSING, "Railroad Crossing (Unguarded) Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_ACCIDENT_PRONE_ROAD, "Accident Prone Area Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_DETOUR_DOUBLE, "Obstacle - Detour Both Sides Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_DETOUR_LEFT, "Obstacle - Detour Left Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_DETOUR_RIGHT, "Obstacle - Detour Right Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_DANGEROUS, "Danger Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CONSTRUCTION, "Road Work Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_ACCIDENT, "Traffic Accident Management Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_TIDAL_LANE, "Watch for Tidal Lane Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_DISTANCE_BETWEEN_VEHICLES, "Maintain Safe Distance Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CONFLUENCE_LEFT, "Merge from Left Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_CONFLUENCE_RIGHT, "Merge from Right Sign");
        translationBuilder.add(SignBlocks.SIGN_WARING_LESS_3_TO_2, "Lane Reduction (3→2) Sign");
        translationBuilder.add(SignBlocks.SIGN_WARING_LESS_4_TO_3, "Lane Reduction (4→3) Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_EMERGENCY_LANE_1, "Escape Ramp Sign 1");
        translationBuilder.add(SignBlocks.SIGN_WARNING_EMERGENCY_LANE_2, "Escape Ramp Sign 2");
        translationBuilder.add(SignBlocks.SIGN_WARNING_ROAD_ICY, "Icy Road Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_WEATHER_RAINY_SNOWY, "Rain/Snow Warning Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_WEATHER_FOGGY, "Fog Warning Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_WEATHER_THUNDER, "Adverse Weather Warning Sign");
        translationBuilder.add(SignBlocks.SIGN_WARNING_VEHICLES_QUEUED_AHEAD, "Queue Ahead Warning Sign");
        translationBuilder.add(SignBlocks.ZONES_BOARD_BUS, "Bus Zone Sign");
        translationBuilder.add(SignBlocks.ZONES_BOARD_CHARGING, "Charging Zone Sign");
        translationBuilder.add(SignBlocks.ZONES_BOARD_COMPANY, "Private Zone Sign");
        translationBuilder.add(SignBlocks.ZONES_BOARD_DISABLED, "Disabled Zone Sign");
        translationBuilder.add(SignBlocks.ZONES_BOARD_NON_MOTOR_VEHICLES, "Non-Motor Vehicle Zone Sign");
        translationBuilder.add(SignBlocks.ZONES_BOARD_SCHOOL, "School Zone Sign");
        translationBuilder.add(SignBlocks.ZONES_BOARD_SCHOOL_BUS_1, "School Bus Zone Sign 1");
        translationBuilder.add(SignBlocks.ZONES_BOARD_SCHOOL_BUS_2, "School Bus Zone Sign 2");
        translationBuilder.add(SignBlocks.ZONES_BOARD_TAXI, "Taxi Zone Sign");

        translationBuilder.add("itemGroup.yunbeiuc_rb_group","Yunbei UC | Road Blocks");
        translationBuilder.add("itemGroup.yunbeiuc_sings_group","Yunbei UC | Road Signs");
    }
}
