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

public class SignGuideIntersectionAdvanceWarning5Entity extends BlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String text4 = "";
    private float text1AndY;
    private float text2AndY;
    private float text3AndY;
    private float text4AndY;

    public SignGuideIntersectionAdvanceWarning5Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.text4 = nbt.getString("text4");
        this.text1AndY = nbt.getFloat("text1AndY");
        this.text2AndY = nbt.getFloat("text2AndY");
        this.text3AndY = nbt.getFloat("text3AndY");
        this.text4AndY = nbt.getFloat("text4AndY");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("text4", this.text4);
        nbt.putFloat("text1AndY", this.text1AndY);
        nbt.putFloat("text2AndY", this.text2AndY);
        nbt.putFloat("text3AndY", this.text3AndY);
        nbt.putFloat("text4AndY", this.text4AndY);
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

    public String getText1() {
        return text1;
    }
    public String getText2() {
        return text2;
    }
    public String getText3() {
        return text3;
    }
    public String getText4() {
        return text4;
    }
    public float getText1AndY() {
        return text1AndY;
    }
    public float getText2AndY() {
        return text2AndY;
    }
    public float getText3AndY() {
        return text3AndY;
    }
    public float getText4AndY() {
        return text4AndY;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public void setText2(String text2) {
        this.text2 = text2;
        markDirtyAndUpdate();
    }
    public void setText3(String text3) {
        this.text3 = text3;
        markDirtyAndUpdate();
    }
    public void setText4(String text4) {
        this.text4 = text4;
        markDirtyAndUpdate();
    }
    public void setText1AndY(float text1AndY) {
        this.text1AndY = text1AndY;
        markDirtyAndUpdate();
    }
    public void setText2AndY(float text2AndY) {
        this.text2AndY = text2AndY;
        markDirtyAndUpdate();
    }
    public void setText3AndY(float text3AndY) {
        this.text3AndY = text3AndY;
        markDirtyAndUpdate();
    }
    public void setText4AndY(float text4AndY) {
        this.text4AndY = text4AndY;
        markDirtyAndUpdate();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}