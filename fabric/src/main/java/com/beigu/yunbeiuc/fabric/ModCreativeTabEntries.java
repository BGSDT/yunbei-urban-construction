package com.beigu.yunbeiuc.fabric;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.RoadBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModCreativeTabEntries {

    public static void register() {
        // Municipal tab
        ItemGroupEvents.modifyEntriesEvent(
                RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(YunbeiUrbanConstruction.MOD_ID, "municipal"))
        ).register(entries -> {
            for (var supplier : ModItems.ALL_MUNICIPAL_ITEMS) {
                entries.add(new ItemStack(supplier.get()));
            }
        });

        // Road tab
        ItemGroupEvents.modifyEntriesEvent(
                RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(YunbeiUrbanConstruction.MOD_ID, "rb"))
        ).register(entries -> {
            for (var supplier : ModItems.ALL_ROAD_ITEMS) {
                entries.add(new ItemStack(supplier.get()));
            }
        });

        ItemGroupEvents.modifyEntriesEvent(
                RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(YunbeiUrbanConstruction.MOD_ID, "sign"))
        ).register(entries -> {
            for (var supplier : ModItems.ALL_SIGN_ITEMS) {
                entries.add(new ItemStack(supplier.get()));
            }
        });

        ItemGroupEvents.modifyEntriesEvent(
                RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(YunbeiUrbanConstruction.MOD_ID, "wand"))
        ).register(entries -> {
            entries.add(new ItemStack(ModItems.WAND.get()));
            entries.add(new ItemStack(ModItems.TREE_WAND.get()));
            entries.add(new ItemStack(ModItems.WATER_WAND.get()));
            entries.add(new ItemStack(ModItems.ROTATED_WAND.get()));
            entries.add(new ItemStack(ModItems.LINK_WAND.get()));
        });
    }
}
