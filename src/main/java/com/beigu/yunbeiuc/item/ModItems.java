package com.beigu.yunbeiuc.item;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.item.custom.LinkWand;
import com.beigu.yunbeiuc.item.custom.RotatedWand;
import com.beigu.yunbeiuc.item.custom.TreeWand;
import com.beigu.yunbeiuc.item.custom.WaterWand;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item WAND = registerItems("wand", new Item(new Item.Settings()
            .maxCount(1)));

    public static final Item TREE_WAND = registerItems("tree_wand", new TreeWand(new Item.Settings()
            .maxCount(1).maxDamage(9)));

    public static final Item WATER_WAND = registerItems("water_wand", new WaterWand(new Item.Settings()
            .maxCount(1).maxDamage(9)));

    public static final Item ROTATED_WAND = registerItems("rotated_wand", new RotatedWand(new Item.Settings()
            .maxCount(1)));

    public static final Item LINK_WAND = registerItems("link_wand", new LinkWand(new Item.Settings()
            .maxCount(1)));

    public static Item registerItems(String id, Item item) {
        return Registry.register(Registries.ITEM, RegistryKey.of(Registries.ITEM.getKey(), new Identifier(YunbeiUrbanConstruction.MOD_ID, id)), item);
    }

    public static Item registerItem(String id, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(YunbeiUrbanConstruction.MOD_ID, id), item);
    }


    public static Item register(String id, Item item) {
        return register(new Identifier(YunbeiUrbanConstruction.MOD_ID, id), item);
    }

    public static Item register(Identifier id, Item item) {
        return register(RegistryKey.of(Registries.ITEM.getKey(), id), item);
    }

    public static Item register(RegistryKey<Item> key, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return Registry.register(Registries.ITEM, key, item);
    }


    public static void registerModItems(){

    }

    public static void registerItems() {

    }
}
