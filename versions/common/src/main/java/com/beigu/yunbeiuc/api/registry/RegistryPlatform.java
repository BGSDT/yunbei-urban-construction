package com.beigu.yunbeiuc.api.registry;

import com.beigu.yunbeiuc.api.resource.Id;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface RegistryPlatform {
    DeferredRegister<Block> blocks(String modId);

    DeferredRegister<Item> items(String modId);

    DeferredRegister<BlockEntityType<?>> blockEntityTypes(String modId);

    Id blockId(Block block);
}
