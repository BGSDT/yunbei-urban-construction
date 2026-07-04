package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FlagBlockEntity extends BlockEntity {
    private String flagId = ""; // 存储旗帜ID

    public FlagBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLAG_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.flagId = nbt.getString("flagId");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("flagId", this.flagId);
        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public String getFlagId() {
        return flagId;
    }

    // 确保有这个setter方法
    public void setFlagId(String flagId) {
        this.flagId = flagId;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}