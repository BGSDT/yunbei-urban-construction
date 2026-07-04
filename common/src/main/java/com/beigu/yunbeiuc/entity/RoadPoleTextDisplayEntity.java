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

public class RoadPoleTextDisplayEntity extends BlockEntity {
    private String text = "";
    private int color = 0x000000; // 默认黑色
    private int fontSize = 25; // 默认字体大小

    public RoadPoleTextDisplayEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROAD_POLE_TEXT_DISPLAY_ENTITY.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text = nbt.getString("text");
        this.color = nbt.getInt("color");
        this.fontSize = nbt.getInt("fontSize");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("text", this.text);
        nbt.putInt("color", this.color);
        nbt.putInt("fontSize", this.fontSize);
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

    // 文本相关方法
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        markDirtyAndUpdate();
    }

    // 颜色相关方法
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        markDirtyAndUpdate();
    }

    // 字体大小相关方法
    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        markDirtyAndUpdate();
    }

    // 辅助方法：标记脏数据并更新客户端
    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    // 获取RGB颜色分量
    public float getRed() {
        return ((color >> 16) & 0xFF) / 255.0f;
    }

    public float getGreen() {
        return ((color >> 8) & 0xFF) / 255.0f;
    }

    public float getBlue() {
        return (color & 0xFF) / 255.0f;
    }

    public float getAlpha() {
        return 1.0f; // 固定不透明度
    }
}