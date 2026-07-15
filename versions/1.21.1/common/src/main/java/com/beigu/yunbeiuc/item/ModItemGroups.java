package com.beigu.yunbeiuc.item;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.RoadBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.item.ModItems;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class ModItemGroups {

    public static final DeferredRegister<ItemGroup> TABS =
            DeferredRegister.create(YunbeiUrbanConstruction.MOD_ID, RegistryKeys.ITEM_GROUP);

    public static final RegistrySupplier<ItemGroup> YUNBEIUC_MUNICIPAL_GROUP = TABS.register("municipal",
            () -> CreativeTabRegistry.create(
                    Text.translatable("itemGroup.yunbeiuc_municipal_group"),
                    () -> new ItemStack(ModItems.ROAD_FLOWER_BOX_2.get())
            ));

    public static final RegistrySupplier<ItemGroup> YUNBEIUC_ROAD_GROUP = TABS.register("rb",
            () -> CreativeTabRegistry.create(
                    Text.translatable("itemGroup.yunbeiuc_rb_group"),
                    () -> new ItemStack(ModItems.ROAD_WITH_WHITE_DOUBLE_LINE.get())
            ));

    public static final RegistrySupplier<ItemGroup> YUNBEIUC_SIGN_GROUP = TABS.register("sign",
            () -> CreativeTabRegistry.create(
                    Text.translatable("itemGroup.yunbeiuc_sign_group"),
                    () -> new ItemStack(ModItems.SIGN_SPEED_LIMIT_005.get())
            ));
    public static final RegistrySupplier<ItemGroup> YUNBEIUC_WAND_GROUP = TABS.register("wand",
            () -> CreativeTabRegistry.create(
                    Text.translatable("itemGroup.yunbeiuc_wand_group"),
                    () -> new ItemStack(ModItems.WAND.get())
            ));

    public static void init() {
        TABS.register();
    }
}
