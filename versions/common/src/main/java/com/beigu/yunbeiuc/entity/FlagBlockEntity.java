package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.api.mapper.BlockEntityMapper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FlagBlockEntity extends BlockEntityMapper {
    private String flagId = ""; // 存储旗帜ID

    public FlagBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLAG_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.flagId = nbt.getString("flagId");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putString("flagId", this.flagId);
        super.saveAdditional(nbt);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return createUpdatePacket();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return createUpdateTag();
    }

    public String getFlagId() {
        return flagId;
    }

    // 确保有这个setter方法
    public void setFlagId(String flagId) {
        this.flagId = flagId;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), VersionServices.blocks().updateAll());
        }
    }
}
