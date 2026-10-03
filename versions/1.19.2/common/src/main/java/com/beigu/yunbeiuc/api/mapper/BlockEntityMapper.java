package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Minecraft 1.19.2 block entity compatibility base, modeled after Minecraft-Mappings. */
public abstract class BlockEntityMapper extends BlockEntity {
    protected BlockEntityMapper(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected final ClientboundBlockEntityDataPacket createUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    protected final CompoundTag createUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadCompat(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveAdditionalCompat(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return createUpdateTag();
    }

    protected void loadCompat(CompoundTag tag) {
    }

    protected void saveAdditionalCompat(CompoundTag tag) {
    }
}