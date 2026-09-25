package com.beigu.yunbeiuc.item;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.api.item.CreativeTabHandle;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class ModItemGroups {
    public static final CreativeTabHandle YUNBEIUC_MUNICIPAL_GROUP = VersionServices.creativeTabs().create(
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "municipal"),
            () -> new ItemStack(ModItems.ROAD_FLOWER_BOX_2.get()));

    public static final CreativeTabHandle YUNBEIUC_ROAD_GROUP = VersionServices.creativeTabs().create(
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "rb"),
            () -> new ItemStack(ModItems.ROAD_WITH_WHITE_DOUBLE_LINE.get()));

    public static final CreativeTabHandle YUNBEIUC_SIGN_GROUP = VersionServices.creativeTabs().create(
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "sign"),
            () -> new ItemStack(ModItems.SIGN_SPEED_LIMIT_005.get()));

    public static final CreativeTabHandle YUNBEIUC_WAND_GROUP = VersionServices.creativeTabs().create(
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "wand"),
            () -> new ItemStack(ModItems.WAND.get()));

    private ModItemGroups() {
    }

    public static void init() {
        VersionServices.creativeTabs().append(YUNBEIUC_MUNICIPAL_GROUP, ModItems.ALL_MUNICIPAL_ITEMS);
        VersionServices.creativeTabs().append(YUNBEIUC_ROAD_GROUP, ModItems.ALL_ROAD_ITEMS);
        VersionServices.creativeTabs().append(YUNBEIUC_SIGN_GROUP, ModItems.ALL_SIGN_ITEMS);
        VersionServices.creativeTabs().append(YUNBEIUC_WAND_GROUP, ModItems.ALL_WAND_ITEMS);
    }
}
