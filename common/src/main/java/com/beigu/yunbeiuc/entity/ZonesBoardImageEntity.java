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

public class ZonesBoardImageEntity extends BlockEntity {
    private String text1 = "";
    private BoardImage image = BoardImage.RED;
    private float andX;
    private float andY;
    private float andScale;

    public ZonesBoardImageEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONES_BOARD_IMAGE_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.image = BoardImage.fromName(nbt.getString("image"));
        this.andX = nbt.getFloat("andX");
        this.andY = nbt.getFloat("andY");
        this.andScale = nbt.getFloat("andScale");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("image", this.image.getName());
        nbt.putFloat("andX", this.andX);
        nbt.putFloat("andY", this.andY);
        nbt.putFloat("andScale", this.andScale);
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

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }

    public BoardImage getImage() {
        return image;
    }

    public void setImage(BoardImage image) {
        this.image = image;
        markDirtyAndUpdate();
    }

    public float getAndX() {
        return andX;
    }

    public void setAndX(float andX) {
        this.andX = andX;
        markDirtyAndUpdate();
    }

    public float getAndY() {
        return andY;
    }

    public void setAndY(float andY) {
        this.andY = andY;
        markDirtyAndUpdate();
    }

    public float getAndScale() { return andScale; }
    public void setAndScale(float andScale) {
        this.andScale = andScale;
        markDirtyAndUpdate();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public enum BoardImage {
        RED("red"),
        YELLOW("yellow"),
        WHITE("white"),
        EXPRESSWAY("expressway");

        private final String name;

        BoardImage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static BoardImage fromName(String name) {
            for (BoardImage img : values()) {
                if (img.name.equals(name)) {
                    return img;
                }
            }
            return RED;
        }
    }
}