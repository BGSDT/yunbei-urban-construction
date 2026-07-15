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

public class RoadNameSignBlockEntity extends BlockEntity {
    private String chineseText = "";
    private String englishText = "";

    public RoadNameSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROAD_NAME_SIGN_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.chineseText = nbt.getString("chineseText");
        this.englishText = nbt.getString("englishText");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("chineseText", this.chineseText);
        nbt.putString("englishText", this.englishText);
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

    public String getChineseText() {
        return chineseText;
    }

    public String getEnglishText() {
        return englishText;
    }

    public void setChineseText(String chineseText) {
        this.chineseText = chineseText;
        markDirtyAndUpdate();
    }

    public void setEnglishText(String englishText) {
        this.englishText = englishText;
        markDirtyAndUpdate();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}
