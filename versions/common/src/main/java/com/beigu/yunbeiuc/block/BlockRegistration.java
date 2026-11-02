package com.beigu.yunbeiuc.block;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * Wraps block registration so the platform layer can stamp the block id onto its properties
 * before the block is constructed (26.x requires this).
 */
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

    /**
     * Registry name of a block created through this helper, or {@code null} if unknown.
     *
     * <p>Safe to call while the block is still being registered, which matters because 26.x
     * computes collision shapes eagerly during registration.</p>
     */
    public static String nameOf(Block block) {
        return NAMES.get(block);
    }
}
