package com.beigu.yunbeiuc.item;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup YUNBEIUC_ROAD_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(YunbeiUrbanConstruction.MOD_ID, "road"),
            ItemGroup.create(null, -1)
                    .displayName(Text.translatable("itemGroup.yunbeicu.road_blocks"))
                    .icon(() -> new ItemStack(ModBlocks.ROAD_WITH_WHITE_DOUBLE_LINE))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.ROAD_BLOCK);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_CROSS_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_CROSS_LINE);
                        entries.add(ModBlocks.ROAD_WITH_AUTO_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE);
                    }).build());

    public static void registerGroups() {

    }
}