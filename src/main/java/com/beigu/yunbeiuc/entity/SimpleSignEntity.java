package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SimpleSignEntity extends BlockEntity {
    private String text = ""; // 初始为空

    public SimpleSignEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIMPLE_SIGN_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text = nbt.getString("text");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("text", this.text);
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}