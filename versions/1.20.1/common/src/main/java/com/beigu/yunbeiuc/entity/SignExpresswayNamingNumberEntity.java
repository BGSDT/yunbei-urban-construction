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

public class SignExpresswayNamingNumberEntity extends BlockEntity {
    private String expresswayNumber = "";
    private String expresswayName = "";

    public SignExpresswayNamingNumberEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_NAMING_NUMBER_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.expresswayNumber = nbt.getString("expresswayNumber");
        this.expresswayName = nbt.getString("expresswayName");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("expresswayNumber", this.expresswayNumber);
        nbt.putString("expresswayName", this.expresswayName);
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

    public String getExpresswayNumber() { return expresswayNumber; }
    public void setExpresswayNumber(String expresswayNumber) {
        this.expresswayNumber = expresswayNumber;
        markDirtyAndUpdate();
    }
    public String getExpresswayName() { return expresswayName; }
    public void setExpresswayName(String expresswayName) {
        this.expresswayName = expresswayName;
        markDirtyAndUpdate();
    }
    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}