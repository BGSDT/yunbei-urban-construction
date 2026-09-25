package com.beigu.yunbeiuc.item;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModItemGroups {
    public static final CreativeModeTab YUNBEIUC_MUNICIPAL_GROUP = CreativeTabRegistry.create(
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "municipal"),
            () -> new ItemStack(ModItems.ROAD_FLOWER_BOX_2.get()));

    public static final CreativeModeTab YUNBEIUC_ROAD_GROUP = CreativeTabRegistry.create(
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "rb"),
            () -> new ItemStack(ModItems.ROAD_WITH_WHITE_DOUBLE_LINE.get()));

    public static final CreativeModeTab YUNBEIUC_SIGN_GROUP = CreativeTabRegistry.create(
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "sign"),
            () -> new ItemStack(ModItems.SIGN_SPEED_LIMIT_005.get()));

    public static final CreativeModeTab YUNBEIUC_WAND_GROUP = CreativeTabRegistry.create(
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "wand"),
            () -> new ItemStack(ModItems.WAND.get()));

    private ModItemGroups() {
    }

    public static void init() {
        // Loading this class creates the four 1.18.2 creative tabs.
    }
}
