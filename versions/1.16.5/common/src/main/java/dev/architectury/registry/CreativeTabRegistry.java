package dev.architectury.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/** 1.16.5 bridge for the renamed creative tab helper. */
public final class CreativeTabRegistry {
    private CreativeTabRegistry() {}

    public static CreativeModeTab create(ResourceLocation id, Supplier<ItemStack> icon) {
        return me.shedaniel.architectury.registry.CreativeTabs.create(id, icon);
    }
}
