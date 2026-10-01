package com.beigu.yunbeiuc.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

/** Creative-tab operations spanning the 1.19.3 registry redesign. */
public interface CreativeTabPlatform {
    CreativeTabHandle create(ResourceLocation id, Supplier<ItemStack> icon);

    /** 把 create 阶段登记的物品栏一次性写入注册表；必须在 append 之前调用一次。 */
    void registerTabs();

    Item.Properties apply(Item.Properties properties, CreativeTabHandle tab);

    void append(CreativeTabHandle tab, List<Supplier<? extends Item>> items);
}
