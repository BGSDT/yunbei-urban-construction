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

public class SignGuideIntersectionWarning4Entity extends BlockEntity {
    private Direction direction1 = Direction.STRAIGHT;
    private String text1 = "";

    public SignGuideIntersectionWarning4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_WARNING_4_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.direction1 = Direction.fromName(nbt.getString("direction1"));
        this.text1 = nbt.getString("text1");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("text1", this.text1);
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
    public String getText1() {
        return text1;
    }

    public void setDirection1(Direction direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }
    public void setText1(String text1) {
        this.text1 = text1;
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