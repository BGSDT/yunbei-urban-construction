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

public class SignGuideIntersectionAdvanceWarning7Entity extends BlockEntity {
    private Direction direction1 = Direction.STRAIGHT;
    private Direction direction2 = Direction.STRAIGHT;
    private Direction direction3 = Direction.STRAIGHT;
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";

    public SignGuideIntersectionAdvanceWarning7Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.direction1 = Direction.fromName(nbt.getString("direction1"));
        this.direction2 = Direction.fromName(nbt.getString("direction2"));
        this.direction3 = Direction.fromName(nbt.getString("direction3"));
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("direction2", this.direction2.getName());
        nbt.putString("direction3", this.direction3.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
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
    public Direction getDirection2() { return direction2; }
    public Direction getDirection3() { return direction3; }
    public String getText1() { return text1; }
    public String getText2() { return text2; }
    public String getText3() { return text3;}

    public void setDirection1(Direction direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }
    public void setDirection2(Direction direction2) {
        this.direction2 = direction2;
        markDirtyAndUpdate();
    }
    public void setDirection3(Direction direction3) {
        this.direction3 = direction3;
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

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public enum Direction {
        LEFT("left"),
        STRAIGHT("straight"),
        RIGHT("right");

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
            return STRAIGHT;
        }
    }
}