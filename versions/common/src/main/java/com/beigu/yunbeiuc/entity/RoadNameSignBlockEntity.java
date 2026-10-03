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

public class RoadNameSignBlockEntity extends BlockEntityMapper {
    private String chineseText = "";
    private String englishText = "";

    public RoadNameSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROAD_NAME_SIGN_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.chineseText = nbt.getString("chineseText");
        this.englishText = nbt.getString("englishText");
    }

    @Override
    protected void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("chineseText", this.chineseText);
        nbt.putString("englishText", this.englishText);
        super.saveAdditionalCompat(nbt);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return createUpdatePacket();
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
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), VersionServices.blocks().updateAll());
        }
    }
}
