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

public class SignGuideRoadsideFacilityOverloadCheckpoint1Entity extends BlockEntity {
    private Unit unit1 = Unit.KILOMETRE;
    private String length1 = "";

    public SignGuideRoadsideFacilityOverloadCheckpoint1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_ROADSIDE_FACILITY_OVERLOAD_CHECKPOINT_1_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.unit1 = Unit.fromName(nbt.getString("unit1"));
        this.length1 = nbt.getString("length1");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("unit1", this.unit1.getName());
        nbt.putString("length1", this.length1);
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

    public Unit getUnit1() {
        return unit1;
    }
    public String getLength1() {
        return length1;
    }

    public void setUnit1(Unit unit1) {
        this.unit1 = unit1;
        markDirtyAndUpdate();
    }
    public void setLength1(String length1) {
        this.length1 = length1;
        markDirtyAndUpdate();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public enum Unit {
        KILOMETRE("kilometre"),
        METRE("metre");

        private final String name;

        Unit(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Unit fromName(String name) {
            for (Unit value : values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return KILOMETRE;
        }
    }
}