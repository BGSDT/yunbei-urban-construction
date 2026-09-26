package com.beigu.yunbeiuc.api.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class RegistryPlatformImpl implements RegistryPlatform {
    @Override public DeferredRegister<Block> blocks(String modId) { return DeferredRegister.create(modId, Registries.BLOCK); }
    @Override public DeferredRegister<Item> items(String modId) { return DeferredRegister.create(modId, Registries.ITEM); }
    @Override public DeferredRegister<BlockEntityType<?>> blockEntityTypes(String modId) { return DeferredRegister.create(modId, Registries.BLOCK_ENTITY_TYPE); }
}
