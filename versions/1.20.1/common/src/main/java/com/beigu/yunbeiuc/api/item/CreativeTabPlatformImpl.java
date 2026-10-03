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
        // 1.20.1 的创造模式物品栏由标题 Component 标识，没有 ResourceLocation。
        // 键名必须与低版本保持一致：1.16.5~1.19.4 走 Architectury 的
        // CreativeTabRegistry.create(id, icon)，其内部固定拼成
        // "itemGroup.<命名空间>.<路径>"，调用方无法覆盖。
        // 因此这里同样使用点号形式，保证全版本语言键统一。
        Component title = Component.translatable("itemGroup." + id.getNamespace() + "." + id.getPath());
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
