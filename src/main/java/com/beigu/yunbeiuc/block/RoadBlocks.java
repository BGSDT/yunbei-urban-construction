package com.beigu.yunbeiuc.block;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.DirectionBlock;
import com.beigu.yunbeiuc.block.custom.road.GroundMark1Block;
import com.beigu.yunbeiuc.block.custom.road.GroundMark2Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RoadBlocks {
    public static final Block ROAD_BLOCK = register("road_block", new Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_FULL_OF_WHITE = register("road_full_of_white", new Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_FULL_OF_YELLOW = register("road_full_of_yellow", new Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WHITE_YELLOW = register("road_white_yellow", new Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_LINE = register("road_with_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_DOUBLE_LINE = register("road_with_white_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_THICK_LINE = register("road_with_white_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_LINE = register("road_with_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_DOUBLE_LINE = register("road_with_yellow_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_THICK_LINE = register("road_with_yellow_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE = register("road_with_white_yellow_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_HALF_DOUBLE_LINE = register("road_with_white_half_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_HALF_DOUBLE_LINE = register("road_with_yellow_half_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_LINE = register("road_with_white_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_LINE = register("road_with_yellow_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_RIGHTANGLE_LINE = register("road_with_white_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_RIGHTANGLE_LINE = register("road_with_yellow_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE = register("road_with_white_yellow_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE = register("road_with_whitethick_normal_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE = register("road_with_whitethick_yellow_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE = register("road_with_whitethick_yellowdouble_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE = register("road_with_white_yellowdouble_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_BEVEL_LINE = register("road_with_white_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE = register("road_with_white_bevel_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_BEVEL_THICK_LINE = register("road_with_white_bevel_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE = register("road_with_white_offset_out_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE = register("road_with_white_offset_in_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_BEVEL_LINE = register("road_with_yellow_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE = register("road_with_yellow_bevel_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_BEVEL_THICK_LINE = register("road_with_yellow_bevel_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE = register("road_with_yellow_offset_out_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE = register("road_with_yellow_offset_in_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT = register("road_with_white_rightangle_line_offset_out", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN = register("road_with_white_rightangle_line_offset_in", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE = register("road_with_white_offset_out_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE = register("road_with_white_offset_in_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT = register("road_with_yellow_rightangle_line_offset_out", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN = register("road_with_yellow_rightangle_line_offset_in", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE = register("road_with_yellow_offset_out_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE = register("road_with_yellow_offset_in_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_LINE = register("road_with_white_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_LINE = register("road_with_yellow_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE = register("road_with_white_tshape_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_THICK_LINE = register("road_with_white_tshape_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE = register("road_with_white_double_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_THICK_TSHAPE_LINE = register("road_with_white_thick_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE = register("road_with_white_tshape_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE = register("road_with_yellow_tshape_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE = register("road_with_white_tshape_yellowdouble_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE = register("road_with_white_thick_tshape_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE = register("road_with_whitethick_tshape_yellowdouble_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE = register("road_with_white_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE = register("road_with_yellow_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE = register("road_with_whitedouble_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE = register("road_with_whitethick_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE = register("road_with_whitethick_tshape_offset_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE = register("road_with_yellow_tshape_offset_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE = register("road_with_white_tshape_offset_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_BEVEL_DB_LINE = register("road_with_white_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_BEVEL_DB_LINE = register("road_with_yellow_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE = register("road_with_whitenormal_and_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE = register("road_with_yellownormal_and_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITENORMAL_BEVEL_LINE = register("road_with_whitenormal_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWNORMAL_BEVEL_LINE = register("road_with_yellownormal_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE = register("road_with_whitenormal_bevel_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE = register("road_with_yellownormal_bevel_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_BEVEL_LINE = register("road_with_whitethick_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWTHICK_BEVEL_LINE = register("road_with_yellowthick_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE = register("road_with_whitethick_bevel_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE = register("road_with_yellowthick_bevel_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_CROSS_LINE = register("road_with_white_cross_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_CROSS_LINE = register("road_with_yellow_cross_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_LEFT_DIAMOND_SHAPE_LINE = register("road_with_white_left_diamond_shape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_RIGHT_DIAMOND_SHAPE_LINE = register("road_with_white_right_diamond_shape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_DOUBLE_DIAMOND_SHAPE_LINE = register("road_with_white_double_diamond_shape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_LEFT_DIAMOND_SHAPE_LINE = register("road_with_yellow_left_diamond_shape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_DOUBLE_DIAMOND_SHAPE_LINE = register("road_with_yellow_double_diamond_shape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_YELLOW_RIGHT_DIAMOND_SHAPE_LINE = register("road_with_yellow_right_diamond_shape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_THREE_SQUARE_LINE = register("road_with_white_three_square_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_SIX_SQUARE_LINE = register("road_with_white_six_square_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_WHITE_NINE_SQUARE_LINE = register("road_with_white_nine_square_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_AUTO_BEVEL_LINE = register("road_with_auto_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));
    public static final Block ROAD_WITH_AUTO_RIGHTANGLE_LINE = register("road_with_auto_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).requiresTool()));

    public static final Block STRAIGHT_GROUND_MARK = register("straight_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block STRAIGHT_LEFT_TURN_GROUND_MARK = register("straight_left_turn_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block LEFT_TURN_GROUND_MARK = register("left_turn_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block STRAIGHT_RIGHT_TURN_GROUND_MARK = register("straight_right_turn_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block RIGHT_TURN_GROUND_MARK = register("right_turn_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block STRAIGHT_LEFT_RIGHT_TURN_GROUND_MARK = register("straight_left_right_turn_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block LEFT_TURN_AROUND_SINGLE_GROUND_MARK = register("left_turn_around_single_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block STRAIGHT_LEFT_TURN_AROUND_GROUND_MARK = register("straight_left_turn_around_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block LEFT_TURN_AROUND_GROUND_MARK = register("left_turn_around_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block LEFT_RIGHT_TURN_GROUND_MARK = register("left_right_turn_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block LEFT_TURN_MERGE_GROUND_MARK = register("left_turn_merge_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block RIGHT_TURN_MERGE_GROUND_MARK = register("right_turn_merge_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SLOWDOWN_ANNOUNCEMENT_GROUND_MARK = register("slowdown_announcement_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block DISTANCE_CONFIRMATION_GROUND_MARK = register("distance_confirmation_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block TAXI_1_GROUND_MARK = register("taxi_1_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block TAXI_2_GROUND_MARK = register("taxi_2_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block VEHICLE_GROUND_MARK = register("vehicle_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block DISABLED_PEOPLE_GROUND_MARK = register("disabled_people_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block NON_MOTOR_VEHICLES_GROUND_MARK = register("non_motor_vehicles_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block BUS_1_GROUND_MARK = register("bus_1_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block BUS_2_GROUND_MARK = register("bus_2_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SCHOOL_BUS_1_GROUND_MARK = register("school_bus_1_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block LARGE_SPEED_BUMP_GROUND_MARK = register("large_speed_bump_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SMALL_SPEED_BUMP_GROUND_MARK = register("small_speed_bump_ground_mark", new GroundMark2Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WHITE_040_GROUND_MARK = register("white_040_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WHITE_050_GROUND_MARK = register("white_050_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WHITE_060_GROUND_MARK = register("white_060_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WHITE_070_GROUND_MARK = register("white_070_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WHITE_080_GROUND_MARK = register("white_080_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WHITE_090_GROUND_MARK = register("white_090_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WHITE_100_GROUND_MARK = register("white_100_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WHITE_110_GROUND_MARK = register("white_110_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block WHITE_120_GROUND_MARK = register("white_120_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block YELLOW_040_GROUND_MARK = register("yellow_040_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block YELLOW_050_GROUND_MARK = register("yellow_050_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block YELLOW_060_GROUND_MARK = register("yellow_060_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block YELLOW_070_GROUND_MARK = register("yellow_070_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block YELLOW_080_GROUND_MARK = register("yellow_080_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block YELLOW_090_GROUND_MARK = register("yellow_090_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block YELLOW_100_GROUND_MARK = register("yellow_100_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block YELLOW_110_GROUND_MARK = register("yellow_110_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block YELLOW_120_GROUND_MARK = register("yellow_120_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ELECTRIC_VEHICLE_GROUND_MARK = register("electric_vehicle_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block PEDESTRIAN_GROUND_MARK = register("pedestrian_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block STOP_AND_YIELD_GROUND_MARK = register("stop_and_yield_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block HOV_1_GROUND_MARK = register("hov_1_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block HOV_2_GROUND_MARK = register("hov_2_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block HOV_3_GROUND_MARK = register("hov_3_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block HOV_4_GROUND_MARK = register("hov_4_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block HOV_5_GROUND_MARK = register("hov_5_ground_mark", new GroundMark1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block MANHOLE_COVER = register("manhole_cover", new GroundMark2Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static Block register(String id, Block block) {
        registerBlockItems(id, block);
        return Registry.register(Registries.BLOCK, new Identifier(YunbeiUrbanConstruction.MOD_ID, id), block);
    }

    public static void registerBlockItems(String id, Block block) {
        Registry.register(Registries.ITEM, new Identifier(YunbeiUrbanConstruction.MOD_ID, id),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerRoadBlocks() {

    }
}
