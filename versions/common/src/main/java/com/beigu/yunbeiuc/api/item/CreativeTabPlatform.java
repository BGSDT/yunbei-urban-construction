package com.beigu.yunbeiuc.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public interface CreativeTabPlatform {
    CreativeTabHandle create(ResourceLocation id, Supplier<ItemStack> icon);

    void registerTabs();

    Item.Properties apply(Item.Properties properties, CreativeTabHandle tab);

    void append(CreativeTabHandle tab, List<Supplier<? extends Item>> items);
}
