package com.beigu.yunbeiuc.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
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
    private boolean glowingText = false;
    // 客户端UI状态：当前正在编辑的文本行索引，不写入NBT
    private transient int editingLineIndex = -1;
    // 客户端UI状态：当前gizmo模式（-1无 0位移 1旋转 2缩放），不写入NBT
    private transient int editingGizmoMode = -1;

    public CustomSignBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY.get(), pos, state);
    }

    protected CustomSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public List<TextLineData> getTextLines() { return textLines; }

    public void setTextLines(List<TextLineData> textLines) {
        this.textLines = textLines;
        markDirty();
        if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    public boolean isGlowingText() { return glowingText; }

    public void setGlowingText(boolean glowingText) {
        this.glowingText = glowingText;
        markDirty();
        if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    public int getEditingLineIndex() { return editingLineIndex; }
    public void setEditingLineIndex(int editingLineIndex) { this.editingLineIndex = editingLineIndex; }

    public int getEditingGizmoMode() { return editingGizmoMode; }
    public void setEditingGizmoMode(int editingGizmoMode) { this.editingGizmoMode = editingGizmoMode; }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtList list = new NbtList();
        for (TextLineData data : textLines) list.add(data.toNbt());
        nbt.put("TextLines", list);
        nbt.putBoolean("GlowingText", glowingText);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        textLines.clear();
        NbtList list = nbt.getList("TextLines", 10);
        for (int i = 0; i < list.size(); i++) textLines.add(TextLineData.fromNbt(list.getCompound(i)));
        glowingText = nbt.getBoolean("GlowingText");
    }

    @Nullable @Override public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt() { return createNbt(); }

    public static class TextLineData {
        private String text;
        private float xOffset, yOffset, zOffset;
        private float rotX, rotY, rotZ;
        private int color;
        private TextAlignment alignment;
        private boolean bold, italic, underline, shadow;
        private boolean outline;
        private int outlineColor = 0x000000;
        private float fontSize;
        private float scaleX, scaleY, scaleZ;

        public TextLineData(String text) {
            this.text = text != null ? text : "";
            this.xOffset = 0; this.yOffset = 0; this.zOffset = 0;
            this.rotX = 0; this.rotY = 0; this.rotZ = 0;
            this.color = 0xFFFFFF; this.alignment = TextAlignment.CENTER_CENTER;
            this.bold = false; this.italic = false; this.underline = false; this.shadow = false;
            this.outline = false; this.outlineColor = 0x000000;
            this.fontSize = 1.0f;
            this.scaleX = 1.0f; this.scaleY = 1.0f; this.scaleZ = 1.0f;
        }

        public TextLineData copy() {
            TextLineData c = new TextLineData(text);
            c.xOffset = xOffset; c.yOffset = yOffset; c.zOffset = zOffset;
            c.rotX = rotX; c.rotY = rotY; c.rotZ = rotZ;
            c.color = color; c.alignment = alignment;
            c.bold = bold; c.italic = italic; c.underline = underline; c.shadow = shadow;
            c.outline = outline; c.outlineColor = outlineColor;
            c.fontSize = fontSize;
            c.scaleX = scaleX; c.scaleY = scaleY; c.scaleZ = scaleZ;
            return c;
        }

        public void applyFrom(TextLineData other) {
            this.text = other.text;
            this.xOffset = other.xOffset; this.yOffset = other.yOffset; this.zOffset = other.zOffset;
            this.rotX = other.rotX; this.rotY = other.rotY; this.rotZ = other.rotZ;
            this.color = other.color; this.alignment = other.alignment;
            this.bold = other.bold; this.italic = other.italic; this.underline = other.underline; this.shadow = other.shadow;
            this.outline = other.outline; this.outlineColor = other.outlineColor;
            this.fontSize = other.fontSize;
            this.scaleX = other.scaleX; this.scaleY = other.scaleY; this.scaleZ = other.scaleZ;
        }

        public void applyFormatFrom(TextLineData other) {
            this.color = other.color; this.alignment = other.alignment;
            this.bold = other.bold; this.italic = other.italic; this.underline = other.underline; this.shadow = other.shadow;
            this.outline = other.outline; this.outlineColor = other.outlineColor;
            this.fontSize = other.fontSize;
        }

        public NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("text", text);
            nbt.putFloat("xOffset", xOffset); nbt.putFloat("yOffset", yOffset); nbt.putFloat("zOffset", zOffset);
            nbt.putFloat("rotX", rotX); nbt.putFloat("rotY", rotY); nbt.putFloat("rotZ", rotZ);
            nbt.putInt("color", color); nbt.putString("alignment", alignment.name());
            nbt.putBoolean("bold", bold); nbt.putBoolean("italic", italic);
            nbt.putBoolean("underline", underline); nbt.putBoolean("shadow", shadow);
            nbt.putBoolean("outline", outline);; nbt.putInt("outlineColor", outlineColor);
            nbt.putFloat("fontSize", fontSize);
            nbt.putFloat("scaleX", scaleX); nbt.putFloat("scaleY", scaleY); nbt.putFloat("scaleZ", scaleZ);
            return nbt;
        }

        public static TextLineData fromNbt(NbtCompound nbt) {
            TextLineData data = new TextLineData(nbt.getString("text"));
            data.xOffset = nbt.getFloat("xOffset"); data.yOffset = nbt.getFloat("yOffset");
            data.zOffset = nbt.getFloat("zOffset"); data.color = nbt.getInt("color");
            data.rotX = nbt.getFloat("rotX"); data.rotY = nbt.getFloat("rotY"); data.rotZ = nbt.getFloat("rotZ");
            try { data.alignment = TextAlignment.valueOf(nbt.getString("alignment")); }
            catch (IllegalArgumentException e) { data.alignment = TextAlignment.CENTER_CENTER; }
            data.bold = nbt.getBoolean("bold"); data.italic = nbt.getBoolean("italic");
            data.underline = nbt.getBoolean("underline"); data.shadow = nbt.getBoolean("shadow");
            data.outline = nbt.getBoolean("outline");data.outlineColor = nbt.contains("outlineColor") ? nbt.getInt("outlineColor") : 0x000000;
            data.fontSize = nbt.contains("fontSize") ? nbt.getFloat("fontSize") : 1.0f;
            data.scaleX = nbt.contains("scaleX") ? nbt.getFloat("scaleX") : 1.0f;
            data.scaleY = nbt.contains("scaleY") ? nbt.getFloat("scaleY") : 1.0f;
            data.scaleZ = nbt.contains("scaleZ") ? nbt.getFloat("scaleZ") : 1.0f;
            return data;
        }

        public String getText() { return text; } public void setText(String text) { this.text = text; }
        public float getXOffset() { return xOffset; } public void setXOffset(float x) { this.xOffset = x; }
        public float getYOffset() { return yOffset; } public void setYOffset(float y) { this.yOffset = y; }
        public float getZOffset() { return zOffset; } public void setZOffset(float z) { this.zOffset = z; }
        public float getRotX() { return rotX; } public void setRotX(float r) { this.rotX = r; }
        public float getRotY() { return rotY; } public void setRotY(float r) { this.rotY = r; }
        public float getRotZ() { return rotZ; } public void setRotZ(float r) { this.rotZ = r; }
        public int getColor() { return color; } public void setColor(int c) { this.color = c; }
        public TextAlignment getAlignment() { return alignment; } public void setAlignment(TextAlignment a) { this.alignment = a; }
        public boolean isBold() { return bold; } public void setBold(boolean b) { this.bold = b; }
        public boolean isItalic() { return italic; } public void setItalic(boolean i) { this.italic = i; }
        public boolean isUnderline() { return underline; } public void setUnderline(boolean u) { this.underline = u; }
        public boolean isShadow() { return shadow; } public void setShadow(boolean s) { this.shadow = s; }
        public boolean isOutline() { return outline; } public void setOutline(boolean o) { this.outline = o; }
        public int getOutlineColor() { return outlineColor; } public void setOutlineColor(int c) { this.outlineColor = c; }
        public float getFontSize() { return fontSize; } public void setFontSize(float s) { this.fontSize = s; }
        public float getScaleX() { return scaleX; } public void setScaleX(float s) { this.scaleX = s; }
        public float getScaleY() { return scaleY; } public void setScaleY(float s) { this.scaleY = s; }
        public float getScaleZ() { return scaleZ; } public void setScaleZ(float s) { this.scaleZ = s; }
    }

    public enum TextAlignment {
        LEFT_TOP(0, 0), LEFT_CENTER(0, 1), LEFT_BOTTOM(0, 2),
        CENTER_TOP(1, 0), CENTER_CENTER(1, 1), CENTER_BOTTOM(1, 2),
        RIGHT_TOP(2, 0), RIGHT_CENTER(2, 1), RIGHT_BOTTOM(2, 2);
        public final int hAlign, vAlign;
        TextAlignment(int hAlign, int vAlign) { this.hAlign = hAlign; this.vAlign = vAlign; }
    }
}