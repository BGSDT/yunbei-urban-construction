package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityMapper extends BlockEntity {
    private static final String COMPAT_KEY = "yunbeiuc_data";

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
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        input.read(COMPAT_KEY, CompoundTag.CODEC).ifPresent(this::loadCompat);
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        saveAdditionalCompat(tag);
        if (!tag.isEmpty()) {
            output.store(COMPAT_KEY, CompoundTag.CODEC, tag);
        }
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
