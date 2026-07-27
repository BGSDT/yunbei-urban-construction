package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.CustomSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomSignBlockEntity extends BlockEntity {
    private List<TextLineData> textLines = new ArrayList<>();

    public CustomSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY.get(), pos, state);
    }

    public List<TextLineData> getTextLines() {
        return textLines;
    }

    public void setTextLines(List<TextLineData> textLines) {
        this.textLines = textLines;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public void addTextLine(TextLineData data) {
        this.textLines.add(data);
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public void removeTextLine(int index) {
        if (index >= 0 && index < textLines.size()) {
            textLines.remove(index);
            markDirty();
            if (world != null) {
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtList list = new NbtList();
        for (TextLineData data : textLines) {
            list.add(data.toNbt());
        }
        nbt.put("TextLines", list);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        textLines.clear();
        NbtList list = nbt.getList("TextLines", 10);
        for (int i = 0; i < list.size(); i++) {
            textLines.add(TextLineData.fromNbt(list.getCompound(i)));
        }
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

    public static class TextLineData {
        private String text;
        private float xOffset;
        private float yOffset;
        private int color;
        private TextAlignment alignment;
        private boolean bold;
        private boolean italic;
        private boolean underline;
        private boolean shadow;
        private float fontSize;

        public TextLineData(String text) {
            this.text = text != null ? text : "";
            this.xOffset = 0;
            this.yOffset = 0;
            this.color = 0xFFFFFF;
            this.alignment = TextAlignment.CENTER;
            this.bold = false;
            this.italic = false;
            this.underline = false;
            this.shadow = false;
            this.fontSize = 1.0f;
        }

        public NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("text", text);
            nbt.putFloat("xOffset", xOffset);
            nbt.putFloat("yOffset", yOffset);
            nbt.putInt("color", color);
            nbt.putString("alignment", alignment.name());
            nbt.putBoolean("bold", bold);
            nbt.putBoolean("italic", italic);
            nbt.putBoolean("underline", underline);
            nbt.putBoolean("shadow", shadow);
            nbt.putFloat("fontSize", fontSize);
            return nbt;
        }

        public static TextLineData fromNbt(NbtCompound nbt) {
            TextLineData data = new TextLineData(nbt.getString("text"));
            data.xOffset = nbt.getFloat("xOffset");
            data.yOffset = nbt.getFloat("yOffset");
            data.color = nbt.getInt("color");
            data.alignment = TextAlignment.valueOf(nbt.getString("alignment"));
            data.bold = nbt.getBoolean("bold");
            data.italic = nbt.getBoolean("italic");
            data.underline = nbt.getBoolean("underline");
            data.shadow = nbt.getBoolean("shadow");
            data.fontSize = nbt.contains("fontSize") ? nbt.getFloat("fontSize") : 1.0f;
            return data;
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public float getXOffset() { return xOffset; }
        public void setXOffset(float xOffset) { this.xOffset = xOffset; }
        public float getYOffset() { return yOffset; }
        public void setYOffset(float yOffset) { this.yOffset = yOffset; }
        public int getColor() { return color; }
        public void setColor(int color) { this.color = color; }
        public TextAlignment getAlignment() { return alignment; }
        public void setAlignment(TextAlignment alignment) { this.alignment = alignment; }
        public boolean isBold() { return bold; }
        public void setBold(boolean bold) { this.bold = bold; }
        public boolean isItalic() { return italic; }
        public void setItalic(boolean italic) { this.italic = italic; }
        public boolean isUnderline() { return underline; }
        public void setUnderline(boolean underline) { this.underline = underline; }
        public boolean isShadow() { return shadow; }
        public void setShadow(boolean shadow) { this.shadow = shadow; }
        public float getFontSize() { return fontSize; }
        public void setFontSize(float fontSize) { this.fontSize = fontSize; }
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT
    }
}