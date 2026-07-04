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

public class ZonesBoardTimeRange2Entity extends BlockEntity {
    private String time1 = "";
    private String time2 = "";
    private String time3 = "";
    private String time4 = "";

    public ZonesBoardTimeRange2Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONES_BOARD_TIME_RANGE_2_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.time1 = nbt.getString("time1");
        this.time2 = nbt.getString("time2");
        this.time3 = nbt.getString("time3");
        this.time4 = nbt.getString("time4");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("time1", this.time1);
        nbt.putString("time2", this.time2);
        nbt.putString("time3", this.time3);
        nbt.putString("time4", this.time4);
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
    public String getTime3() {
        return time3;
    }
    public String getTime4() {
        return time4;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
        markDirtyAndUpdate();
    }
    public void setTime2(String time2) {
        this.time2 = time2;
        markDirtyAndUpdate();
    }
    public void setTime3(String time3) {
        this.time3 = time3;
        markDirtyAndUpdate();
    }
    public void setTime4(String time4) {
        this.time4 = time4;
        markDirtyAndUpdate();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}