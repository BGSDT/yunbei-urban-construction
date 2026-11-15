package com.beigu.yunbeiuc.block;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

// 26.x requires the block id stamped onto its properties before construction.
public final class BlockRegistration {
    private BlockRegistration() {
    }

    private static final java.util.Map<Block, String> NAMES =
            java.util.Collections.synchronizedMap(new java.util.IdentityHashMap<>());

    public static RegistrySupplier<Block> register(DeferredRegister<Block> registry, String name,
                                                   Supplier<? extends Block> supplier) {
        return registry.register(name, () -> {
            VersionServices.blocks().beginRegistration(name);
            try {
                Block block = supplier.get();
                NAMES.put(block, name);
                return block;
            } finally {
                VersionServices.blocks().endRegistration();
            }
        });
    }

    public static String nameOf(Block block) {
        return NAMES.get(block);
    }
}
