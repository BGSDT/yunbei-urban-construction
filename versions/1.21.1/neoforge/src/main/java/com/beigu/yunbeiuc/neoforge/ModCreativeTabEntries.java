package com.beigu.yunbeiuc.neoforge;

import com.beigu.yunbeiuc.item.ModItemGroups;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.item.ItemGroup;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class ModCreativeTabEntries {

    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        ItemGroup tab = event.getTab();

        if (tab == ModItemGroups.YUNBEIUC_MUNICIPAL_GROUP.get()) {
            for (var supplier : ModItems.ALL_MUNICIPAL_ITEMS) {
                event.add(supplier.get(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            }
        } else if (tab == ModItemGroups.YUNBEIUC_ROAD_GROUP.get()) {
            for (var supplier : ModItems.ALL_ROAD_ITEMS) {
                event.add(supplier.get(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            }
        } else if (tab == ModItemGroups.YUNBEIUC_SIGN_GROUP.get()) {
            for (var supplier : ModItems.ALL_SIGN_ITEMS) {
                event.add(supplier.get(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            }
        } else if (tab == ModItemGroups.YUNBEIUC_WAND_GROUP.get()) {
            event.add(ModItems.WAND.get(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            event.add(ModItems.TREE_WAND.get(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            event.add(ModItems.WATER_WAND.get(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            event.add(ModItems.ROTATED_WAND.get(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            event.add(ModItems.LINK_WAND.get(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
