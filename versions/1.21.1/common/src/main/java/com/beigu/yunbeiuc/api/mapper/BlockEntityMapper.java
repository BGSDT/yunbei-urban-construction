package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Minecraft 1.21.1 block entity compatibility base, modeled after Minecraft-Mappings. */
public abstract class BlockEntityMapper extends BlockEntity {
    protected BlockEntityMapper(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected final ClientboundBlockEntityDataPacket createUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    protected final CompoundTag createUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadCompat(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveAdditionalCompat(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return createUpdateTag(registries);
    }

    protected void loadCompat(CompoundTag tag) {
    }

    protected void saveAdditionalCompat(CompoundTag tag) {
    }
}