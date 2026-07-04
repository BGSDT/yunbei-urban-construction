package com.beigu.yunbeiuc.forge;

import com.beigu.yunbeiuc.item.ModItemGroups;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCreativeTabEntries {

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        ItemGroup tab = event.getTab();

        if (tab == ModItemGroups.YUNBEIUC_MUNICIPAL_GROUP.get()) {
            for (var supplier : ModItems.ALL_MUNICIPAL_ITEMS) {
                event.accept(supplier);
            }
        } else if (tab == ModItemGroups.YUNBEIUC_ROAD_GROUP.get()) {
            for (var supplier : ModItems.ALL_ROAD_ITEMS) {
                event.accept(supplier);
            }
        } else if (tab == ModItemGroups.YUNBEIUC_SIGN_GROUP.get()) {
            for (var supplier : ModItems.ALL_SIGN_ITEMS) {
                event.accept(supplier);
            }
        } else if (tab == ModItemGroups.YUNBEIUC_WAND_GROUP.get()) {
            event.accept(ModItems.WAND);
            event.accept(ModItems.TREE_WAND);
            event.accept(ModItems.WATER_WAND);
            event.accept(ModItems.ROTATED_WAND);
            event.accept(ModItems.LINK_WAND);
        }
    }
}
