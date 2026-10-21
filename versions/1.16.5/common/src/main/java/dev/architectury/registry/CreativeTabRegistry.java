package dev.architectury.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public final class CreativeTabRegistry {
    private CreativeTabRegistry() {}

    public static CreativeModeTab create(ResourceLocation id, Supplier<ItemStack> icon) {
        return me.shedaniel.architectury.registry.CreativeTabs.create(id, icon);
    }
}
