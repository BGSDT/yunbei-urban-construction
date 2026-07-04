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

public class SignExpresswayEntranceAdvance4Entity extends BlockEntity {
    private Direction direction1 = Direction.EAST;
    private Expressway expressway1 = Expressway.NATIONAL;
    private String text1 = "";
    private String expresswayNumber1 = "";

    public SignExpresswayEntranceAdvance4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_4_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.direction1 = Direction.fromName(nbt.getString("direction1"));
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.text1 = nbt.getString("text1");
        this.expresswayNumber1 = nbt.getString("expresswayNumber1");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("expresswayNumber1", this.expresswayNumber1);
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

    public Direction getDirection1() { return direction1; }
    public Expressway getExpressway1() { return expressway1; }
    public String getText1() { return text1; }
    public String getExpresswayNumber1() { return expresswayNumber1; }

    public void setDirection1(Direction direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }
    public void setExpressway1(Expressway expressway1) {
        this.expressway1 = expressway1;
        markDirtyAndUpdate();
    }
    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public void setExpresswayNumber1(String expresswayNumber1) {
        this.expresswayNumber1 = expresswayNumber1;
        markDirtyAndUpdate();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public enum Expressway {
        NATIONAL("national"),
        PROVINCIAL("provincial");

        private final String name;

        Expressway(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Expressway fromName(String name) {
            for (Expressway dir : values()) {
                if (dir.name.equals(name)) {
                    return dir;
                }
            }
            return NATIONAL;
        }
    }

    public enum Direction {
        NORTH("north"),
        SOUTH("south"),
        WEST("west"),
        EAST("east");

        private final String name;

        Direction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Direction fromName(String name) {
            for (Direction dir : values()) {
                if (dir.name.equals(name)) {
                    return dir;
                }
            }
            return EAST;
        }
    }
}