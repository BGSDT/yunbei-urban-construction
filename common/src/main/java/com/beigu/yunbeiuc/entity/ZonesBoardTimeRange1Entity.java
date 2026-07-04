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

public class ZonesBoardTimeRange1Entity extends BlockEntity {
    private String time1 = "";
    private String time2 = "";

    public ZonesBoardTimeRange1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONES_BOARD_TIME_RANGE_1_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.time1 = nbt.getString("time1");
        this.time2 = nbt.getString("time2");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("time1", this.time1);
        nbt.putString("time2", this.time2);
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

    public String getTime1() {
        return time1;
    }
    public String getTime2() {
        return time2;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
        markDirtyAndUpdate();
    }
    public void setTime2(String time2) {
        this.time2 = time2;
        markDirtyAndUpdate();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}