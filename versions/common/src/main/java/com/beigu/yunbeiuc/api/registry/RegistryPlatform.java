package com.beigu.yunbeiuc.api.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Version-sensitive registry keys used by Architectury deferred registers. */
public interface RegistryPlatform {
    DeferredRegister<Block> blocks(String modId);

    DeferredRegister<Item> items(String modId);

    DeferredRegister<BlockEntityType<?>> blockEntityTypes(String modId);
}
