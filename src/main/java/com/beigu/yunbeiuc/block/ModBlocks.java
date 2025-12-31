package com.beigu.yunbeiuc.block;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.*;
import com.beigu.yunbeiuc.block.custom.sign.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block SIGN_SPEED_LIMIT_BLOCK = register("sign_speed_limit_block", new SignSpeedLimitBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_BLOCK = register("sign_cancel_speed_limit_block", new SignCancelSpeedLimitBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_NO_ENTRY_FOR_VEHICLES_BLOCK = register("sign_no_entry_for_vehicles_block", new SignNoEntryForVehiclesBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_NO_DIRECTION_BLOCK = register("sign_no_direction_block", new SignNoDirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_HEIGHT_LIMIT_BLOCK = register("sign_height_limit_block", new SignHeightLimitBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_WIDTH_LIMIT_BLOCK = register("sign_width_limit_block", new SignWidthLimitBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_WEIGHT_LIMIT_BLOCK = register("sign_weight_limit_block", new SignWeightLimitBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_NO_SPECIAL_BLOCK = register("sign_no_special_block", new SignNoSpecialBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_INDICATION_DIRECTION_BLOCK = register("sign_indication_direction_block", new SignIndicationDirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_BLOCK = register("sign_indication_lane_direction_block", new SignIndicationLaneDirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_BLOCK = register("sign_guide_intersection_advance_warning_block", new SignGuideIntersectionAdvanceWarningBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block GANTRY_FRAME_SIDE = register("gantry_frame_side", new GantryFrameSideBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block GANTRY_FRAME_CONNECTION = register("gantry_frame_connection", new GantryFrameConnectionBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block GANTRY_FRAME_MAIN = register("gantry_frame_main", new GantryFrameMainBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block GANTRY_FRAME_RAILING = register("gantry_frame_railing", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block ANTI_GLARE_NET = register("anti_glare_net", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block ANTI_GLARE_NET_POLE = register("anti_glare_net_pole", new AntiGlareNetPoleBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block XXXXX = register("xxxxx", new HorizontalDoubleBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block ROAD_CONSTRUCTION_BARRIER_YELLOW_1 = register("road_construction_barrier_yellow_1", new HorizontalDoubleBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));

    public static final Block CRASH_BARRIER_CONCRETE = registerWithoutItem("crash_barrier_concrete", new CrashBarrierConcrete(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block ROAD_RAILINGS_IRON = registerWithoutItem("road_railings_iron", new RoadRailingsIron(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block ROAD_CONSTRUCTION_BARRIER_BLUE = registerWithoutItem("road_construction_barrier_blue", new RoadConstructionBarrierBlue(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));

    public static final Block TRAIN_STATION_ENTRY_GATE = register("train_station_entry_gate", new EntryGateBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));

    public static final Block ROAD_BLOCK = register("road_block", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_LINE = register("road_with_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_DOUBLE_LINE = register("road_with_white_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_THICK_LINE = register("road_with_white_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_LINE = register("road_with_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_DOUBLE_LINE = register("road_with_yellow_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_THICK_LINE = register("road_with_yellow_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE = register("road_with_white_yellow_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_HALF_DOUBLE_LINE = register("road_with_white_half_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_HALF_DOUBLE_LINE = register("road_with_yellow_half_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_OFFSET_LINE = register("road_with_white_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_OFFSET_LINE = register("road_with_yellow_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_RIGHTANGLE_LINE = register("road_with_white_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_RIGHTANGLE_LINE = register("road_with_yellow_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE = register("road_with_white_yellow_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE = register("road_with_whitethick_normal_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE = register("road_with_whitethick_yellow_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE = register("road_with_whitethick_yellowdouble_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE = register("road_with_white_yellowdouble_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_BEVEL_LINE = register("road_with_white_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE = register("road_with_white_bevel_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_BEVEL_THICK_LINE = register("road_with_white_bevel_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE = register("road_with_white_offset_out_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE = register("road_with_white_offset_in_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_BEVEL_LINE = register("road_with_yellow_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE = register("road_with_yellow_bevel_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_BEVEL_THICK_LINE = register("road_with_yellow_bevel_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE = register("road_with_yellow_offset_out_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE = register("road_with_yellow_offset_in_bevel_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT = register("road_with_white_rightangle_line_offset_out", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN = register("road_with_white_rightangle_line_offset_in", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE = register("road_with_white_offset_out_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE = register("road_with_white_offset_in_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT = register("road_with_yellow_rightangle_line_offset_out", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN = register("road_with_yellow_rightangle_line_offset_in", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE = register("road_with_yellow_offset_out_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE = register("road_with_yellow_offset_in_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_TSHAPE_LINE = register("road_with_white_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_LINE = register("road_with_yellow_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE = register("road_with_white_tshape_double_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_TSHAPE_THICK_LINE = register("road_with_white_tshape_thick_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE = register("road_with_white_double_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_THICK_TSHAPE_LINE = register("road_with_white_thick_tshape_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE = register("road_with_white_tshape_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE = register("road_with_yellow_tshape_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE = register("road_with_white_tshape_yellowdouble_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE = register("road_with_white_thick_tshape_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE = register("road_with_whitethick_tshape_yellowdouble_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE = register("road_with_white_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE = register("road_with_yellow_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE = register("road_with_whitedouble_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE = register("road_with_whitethick_tshape_offset_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE = register("road_with_whitethick_tshape_offset_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE = register("road_with_yellow_tshape_offset_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE = register("road_with_white_tshape_offset_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_BEVEL_DB_LINE = register("road_with_white_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_BEVEL_DB_LINE = register("road_with_yellow_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE = register("road_with_whitenormal_and_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE = register("road_with_yellownormal_and_bevel_db_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITENORMAL_BEVEL_LINE = register("road_with_whitenormal_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOWNORMAL_BEVEL_LINE = register("road_with_yellownormal_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE = register("road_with_whitenormal_bevel_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE = register("road_with_yellownormal_bevel_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITETHICK_BEVEL_LINE = register("road_with_whitethick_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOWTHICK_BEVEL_LINE = register("road_with_yellowthick_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE = register("road_with_whitethick_bevel_yellow_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE = register("road_with_yellowthick_bevel_white_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_WHITE_CROSS_LINE = register("road_with_white_cross_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_YELLOW_CROSS_LINE = register("road_with_yellow_cross_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_AUTO_BEVEL_LINE = register("road_with_auto_bevel_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_WITH_AUTO_RIGHTANGLE_LINE = register("road_with_auto_rightangle_line", new DirectionBlock(AbstractBlock.Settings.copy(Blocks.STONE)));

    public static final Block ROAD_MEDIAN_BARRIER = register("road_median_barrier", new RoadMedianBarrierBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block ROAD_POLES_TEXT_DISPLAY = register("road_poles_text_display", new RoadPolesTextDisplay());
    public static final Block SIMPLE_SIGN_BLOCK = register("simple_sign_block", new SimpleSignBlock());
    public static final Block ROAD_POLES_FLAG = register("road_poles_flag", new FlagBlock());

    public static Block register(String id, Block block) {
        registerBlockItems(id, block);
        return Registry.register(Registries.BLOCK, new Identifier(YunbeiUrbanConstruction.MOD_ID, id), block);
    }

    public static void registerBlockItems(String id, Block block) {
        Registry.register(Registries.ITEM, new Identifier(YunbeiUrbanConstruction.MOD_ID, id),
                new BlockItem(block, new Item.Settings()));
    }

    public static Block registerWithoutItem(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(YunbeiUrbanConstruction.MOD_ID, id), block);
    }

    public static void registerModBlocks() {
        // 注册逻辑已在上面的静态初始化中完成
    }
}