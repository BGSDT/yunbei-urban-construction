package com.beigu.yunbeiuc.api.item;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public final class CreativeTabPlatformImpl implements CreativeTabPlatform {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(YunbeiUrbanConstruction.MOD_ID, Registries.CREATIVE_MODE_TAB);

    private static boolean registered;

    @Override
    public CreativeTabHandle create(ResourceLocation id, Supplier<ItemStack> icon) {
        // 1.21.1 的创造模式物品栏同样由标题 Component 标识；
        // 语言键与资源包 lang 文件中的 itemGroup.<命名空间>_<路径>_group 一一对应。
        Component title = Component.translatable("itemGroup." + id.getNamespace() + "_" + id.getPath() + "_group");
        RegistrySupplier<CreativeModeTab> supplier =
                TABS.register(id.getPath(), () -> CreativeTabRegistry.create(title, icon));
        return new CreativeTabHandle(supplier);
    }

    @Override
    public void registerTabs() {
        if (registered) return;
        registered = true;
        TABS.register();
    }

    @Override
    public Item.Properties apply(Item.Properties properties, CreativeTabHandle tab) {
        return properties;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void append(CreativeTabHandle tab, List<Supplier<? extends Item>> items) {
        if (!(tab.value() instanceof DeferredSupplier<?> raw)) return;
        DeferredSupplier<CreativeModeTab> supplier = (DeferredSupplier<CreativeModeTab>) raw;
        for (Supplier<? extends Item> item : items) {
            CreativeTabRegistry.append(supplier, (Supplier) item);
        }
    }
}
