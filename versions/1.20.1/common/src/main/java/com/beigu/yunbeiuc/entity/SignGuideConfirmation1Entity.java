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

public class SignGuideConfirmation1Entity extends BlockEntity {
    private Unit unit1 = Unit.KILOMETRE;
    private Unit unit2 = Unit.KILOMETRE;
    private Unit unit3 = Unit.KILOMETRE;
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";

    public SignGuideConfirmation1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_CONFIRMATION_1_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.unit1 = Unit.fromName(nbt.getString("unit1"));
        this.unit2 = Unit.fromName(nbt.getString("unit2"));
        this.unit3 = Unit.fromName(nbt.getString("unit3"));
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("unit1", this.unit1.getName());
        nbt.putString("unit2", this.unit2.getName());
        nbt.putString("unit3", this.unit3.getName());
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

    public Unit getUnit1() {
        return unit1;
    }
    public Unit getUnit2() {
        return unit2;
    }
    public Unit getUnit3() {
        return unit3;
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
    public String getLength1() {
        return length1;
    }
    public String getLength2() {
        return length2;
    }
    public String getLength3() {
        return length3;
    }

    public void setUnit1(Unit unit1) {
        this.unit1 = unit1;
        markDirtyAndUpdate();
    }
    public void setUnit2(Unit unit2) {
        this.unit2 = unit2;
        markDirtyAndUpdate();
    }
    public void setUnit3(Unit unit3) {
        this.unit3 = unit3;
        markDirtyAndUpdate();
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
    public void setLength1(String length1) {
        this.length1 = length1;
        markDirtyAndUpdate();
    }
    public void setLength2(String length2) {
        this.length2 = length2;
        markDirtyAndUpdate();
    }
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