package com.beigu.yunbeiuc.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

/** Creative-tab operations spanning the 1.19.3 registry redesign. */
public interface CreativeTabPlatform {
    CreativeTabHandle create(ResourceLocation id, Supplier<ItemStack> icon);

    Item.Properties apply(Item.Properties properties, CreativeTabHandle tab);

    void append(CreativeTabHandle tab, List<Supplier<? extends Item>> items);
}
