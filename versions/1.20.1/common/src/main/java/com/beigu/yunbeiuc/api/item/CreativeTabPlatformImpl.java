package com.beigu.yunbeiuc.api.item;

import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public final class CreativeTabPlatformImpl implements CreativeTabPlatform {
    @Override public CreativeTabHandle create(ResourceLocation id, Supplier<ItemStack> icon) { return new CreativeTabHandle(CreativeTabRegistry.create(id, icon)); }
    @Override public Item.Properties apply(Item.Properties properties, CreativeTabHandle tab) { return properties; }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void append(CreativeTabHandle tab, List<Supplier<? extends Item>> items) {
        CreativeTabRegistry.TabSupplier supplier = (CreativeTabRegistry.TabSupplier) tab.value();
        for (Supplier<? extends Item> item : items) {
            CreativeTabRegistry.append(supplier, (Supplier) item);
        }
    }
}
