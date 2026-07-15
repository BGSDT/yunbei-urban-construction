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

public class SignExpresswayDistanceFromLocation1Entity extends BlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";

    public SignExpresswayDistanceFromLocation1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_1_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("length1", this.length1);
        nbt.putString("length2", this.length2);
        nbt.putString("length3", this.length3);
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

    public String getText1() { return text1; }
    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public String getText2() { return text2; }
    public void setText2(String text2) {
        this.text2 = text2;
        markDirtyAndUpdate();
    }
    public String getText3() { return text3; }
    public void setText3(String text3) {
        this.text3 = text3;
        markDirtyAndUpdate();
    }
    public String getLength1() { return length1; }
    public void setLength1(String length1) {
        this.length1 = length1;
        markDirtyAndUpdate();
    }
    public String getLength2() { return length2; }
    public void setLength2(String length2) {
        this.length2 = length2;
        markDirtyAndUpdate();
    }
    public String getLength3() { return length3; }
    public void setLength3(String length3) {
        this.length3 = length3;
        markDirtyAndUpdate();
    }
    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}