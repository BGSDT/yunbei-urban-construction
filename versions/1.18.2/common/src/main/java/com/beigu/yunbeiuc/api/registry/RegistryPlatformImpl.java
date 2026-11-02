package com.beigu.yunbeiuc.api.registry;

import com.beigu.yunbeiuc.api.resource.Id;
import com.beigu.yunbeiuc.api.resource.IdAdapter;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class RegistryPlatformImpl implements RegistryPlatform {
    @Override public DeferredRegister<Block> blocks(String modId) { return DeferredRegister.create(modId, Registry.BLOCK_REGISTRY); }
    @Override public DeferredRegister<Item> items(String modId) { return DeferredRegister.create(modId, Registry.ITEM_REGISTRY); }
    @Override public DeferredRegister<BlockEntityType<?>> blockEntityTypes(String modId) { return DeferredRegister.create(modId, Registry.BLOCK_ENTITY_TYPE_REGISTRY); }
    @Override public Id blockId(Block block) { return IdAdapter.of(Registry.BLOCK.getKey(block)); }
}
