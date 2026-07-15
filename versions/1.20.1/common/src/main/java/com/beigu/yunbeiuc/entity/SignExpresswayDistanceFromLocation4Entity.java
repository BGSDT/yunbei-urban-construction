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

public class SignExpresswayDistanceFromLocation4Entity extends BlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private RoadType roadType1 = RoadType.EXPRESSWAY;
    private RoadType roadType2 = RoadType.EXPRESSWAY;
    private RoadType roadType3 = RoadType.EXPRESSWAY;
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";

    public SignExpresswayDistanceFromLocation4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_4_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.roadType1 = RoadType.fromName(nbt.getString("roadType1"));
        this.roadType2 = RoadType.fromName(nbt.getString("roadType2"));
        this.roadType3 = RoadType.fromName(nbt.getString("roadType3"));
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("roadType1", this.roadType1.getName());
        nbt.putString("roadType2", this.roadType2.getName());
        nbt.putString("roadType3", this.roadType3.getName());
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
    public RoadType getRoadType1() { return roadType1; }
    public void setRoadType1(RoadType roadType1) {
        this.roadType1 = roadType1;
        markDirtyAndUpdate();
    }
    public RoadType getRoadType2() { return roadType2; }
    public void setRoadType2(RoadType roadType2) {
        this.roadType2 = roadType2;
        markDirtyAndUpdate();
    }
    public RoadType getRoadType3() { return roadType3; }
    public void setRoadType3(RoadType roadType3) {
        this.roadType3 = roadType3;
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

    public enum RoadType {
        EXPRESSWAY("expressway"),
        ORDINARY_MUNICIPAL("ordinary_municipal");

        private final String name;
        RoadType(String name) { this.name = name; }
        public String getName() { return name; }
        public static RoadType fromName(String name) {
            for (RoadType dir : values()) {
                if (dir.name.equals(name)) return dir;
            }
            return EXPRESSWAY;
        }
    }}