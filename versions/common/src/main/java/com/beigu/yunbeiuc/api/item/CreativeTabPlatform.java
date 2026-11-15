package com.beigu.yunbeiuc.api.item;

import com.beigu.yunbeiuc.api.resource.Id;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public interface CreativeTabPlatform {
    CreativeTabHandle create(Id id, Supplier<ItemStack> icon);

    void registerTabs();

    Item.Properties apply(Item.Properties properties, CreativeTabHandle tab);

    default Item.Properties blockDescription(Item.Properties properties) {
        return properties;
    }

    void append(CreativeTabHandle tab, List<Supplier<? extends Item>> items);
}
