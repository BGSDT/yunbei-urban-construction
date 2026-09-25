package com.beigu.yunbeiuc.api.mapper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Minecraft 1.16.5 block entity compatibility base, modeled after Minecraft-Mappings. */
public abstract class BlockEntityMapper extends BlockEntity {
    protected BlockEntityMapper(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type);
        worldPosition = pos;
    }

    public void load(CompoundTag tag) {
    }

    protected void saveAdditional(CompoundTag tag) {
    }

    @Override
    public final void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        load(tag);
    }

    @Override
    public final CompoundTag save(CompoundTag tag) {
        super.save(tag);
        saveAdditional(tag);
        return tag;
    }

    protected final ClientboundBlockEntityDataPacket createUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, -1, createUpdateTag());
    }

    protected final CompoundTag createUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }
}
