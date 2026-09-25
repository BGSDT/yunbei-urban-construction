package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class RoadNameSignBlockEntity extends BlockEntity {
    private String chineseText = "";
    private String englishText = "";

    public RoadNameSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROAD_NAME_SIGN_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.chineseText = nbt.getString("chineseText");
        this.englishText = nbt.getString("englishText");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putString("chineseText", this.chineseText);
        nbt.putString("englishText", this.englishText);
        super.saveAdditional(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
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
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
    }
}
