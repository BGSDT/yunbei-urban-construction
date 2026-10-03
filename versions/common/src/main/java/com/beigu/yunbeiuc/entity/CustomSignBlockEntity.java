package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.BlockEntityMapper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomSignBlockEntity extends BlockEntityMapper {
    private List<TextLineData> textLines = new ArrayList<>();
    private boolean glowingText = false;
    private transient int editingLineIndex = -1;
    private transient int editingGizmoMode = -1;

    /**
     * 构造时传入的方块状态。
     *
     * <p>1.16.5 / 1.17.1 的 {@code BlockEntity} 构造函数不保存方块状态，其
     * {@code getBlockState()} 会去读 {@code level}；而方块实体是"先构造、后 setLevel"，
     * 构造期间 {@code level == null}，此时调用 {@code getBlockState()} 直接空指针。
     * 因此把构造参数缓存下来，供构造期的默认文本行判断使用。
     */
    private final BlockState initialState;

    public CustomSignBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY.get(), pos, state);
    }

    protected CustomSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.initialState = state;
    }

    /**
     * 取方块状态，可在构造期安全调用。
     *
     * <p>{@code level} 未就绪时返回构造参数缓存的方块状态，避免
     * {@code BlockEntity#getBlockState()} 在构造期空指针崩溃；{@code level}
     * 就绪后行为与 {@code getBlockState()} 完全一致。
     */
    protected BlockState blockStateForDefaults() {
        if (level == null && initialState != null) return initialState;
        return getBlockState();
    }

    public List<TextLineData> getTextLines() { return textLines; }

    public void setTextLines(List<TextLineData> textLines) {
        this.textLines = textLines;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean isGlowingText() { return glowingText; }

    public void setGlowingText(boolean glowingText) {
        this.glowingText = glowingText;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public int getEditingLineIndex() { return editingLineIndex; }
    public void setEditingLineIndex(int editingLineIndex) { this.editingLineIndex = editingLineIndex; }

    public int getEditingGizmoMode() { return editingGizmoMode; }
    public void setEditingGizmoMode(int editingGizmoMode) { this.editingGizmoMode = editingGizmoMode; }

    @Override
    protected void saveAdditionalCompat(CompoundTag nbt) {
        super.saveAdditionalCompat(nbt);
        ListTag list = new ListTag();
        for (TextLineData data : textLines) list.add(data.toNbt());
        nbt.put("TextLines", list);
        nbt.putBoolean("GlowingText", glowingText);
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        textLines.clear();
        ListTag list = nbt.getList("TextLines", 10);
        for (int i = 0; i < list.size(); i++) textLines.add(TextLineData.fromNbt(list.getCompound(i)));
        glowingText = nbt.getBoolean("GlowingText");
    }

    @Nullable @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return createUpdatePacket(); }
    private static final java.util.regex.Pattern PLACEHOLDER_PATTERN = java.util.regex.Pattern.compile("\\{(\\w+)\\}");

    /** 按字段名取占位符值；子类 override 提供固定 NBT 字段映射，未知字段返回 null（保留原文） */
    public String getPlaceholderValue(String key) { return null; }

    /** 将文本中的 {字段名} 占位符替换为字段值 */
    public String resolvePlaceholders(String text) {
        if (text == null || text.indexOf('{') < 0) return text;
        java.util.regex.Matcher m = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String v = getPlaceholderValue(m.group(1));
            m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(v != null ? v : m.group(0)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public record FieldOption(String label, String value, boolean current) {}

    public record FieldOptionGroup(String title, String field, List<FieldOption> options) {}

    protected static FieldOption opt(String label, String value, String currentValue) {
        return new FieldOption(label, value, value != null && value.equals(currentValue));
    }

    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) { return null; }

    public void applyFieldOption(String field, String value) { }

    public static List<String> extractPlaceholderKeys(String text) {
        List<String> keys = new ArrayList<>();
        if (text == null) return keys;
        java.util.regex.Matcher m = PLACEHOLDER_PATTERN.matcher(text);
        while (m.find()) keys.add(m.group(1));
        return keys;
    }

    public static class TextLineData {
        private String text;
        private float xOffset, yOffset, zOffset;
        private float rotX, rotY, rotZ;
        private int color;
        private TextAlignment alignment;
        private boolean bold, italic, underline, shadow;
        private boolean outline;
        private int outlineColor = 0x000000;
        /** 该行是否发光（用原版全亮光照渲染，即"荧光"效果） */
        private boolean glowing = false;
        private float fontSize;

        private String abcFont = "";
        private float scaleX, scaleY, scaleZ;

        private boolean builtin = false;

        public TextLineData(String text) {
            this.text = text != null ? text : "";
            this.xOffset = 0; this.yOffset = 0; this.zOffset = 0;
            this.rotX = 0; this.rotY = 0; this.rotZ = 0;
            this.color = 0xFFFFFF; this.alignment = TextAlignment.CENTER_CENTER;
            this.bold = false; this.italic = false; this.underline = false; this.shadow = false;
            this.outline = false; this.outlineColor = 0x000000;
            this.fontSize = 1.0f;
            this.abcFont = "";
            this.scaleX = 1.0f; this.scaleY = 1.0f; this.scaleZ = 1.0f;
        }

        public TextLineData copy() {
            TextLineData c = new TextLineData(text);
            c.xOffset = xOffset; c.yOffset = yOffset; c.zOffset = zOffset;
            c.rotX = rotX; c.rotY = rotY; c.rotZ = rotZ;
            c.color = color; c.alignment = alignment;
            c.bold = bold; c.italic = italic; c.underline = underline; c.shadow = shadow;
            c.outline = outline; c.outlineColor = outlineColor;
            c.glowing = glowing;
            c.fontSize = fontSize;
            c.abcFont = abcFont;
            c.scaleX = scaleX; c.scaleY = scaleY; c.scaleZ = scaleZ;
            c.builtin = builtin;
            return c;
        }

        public void applyFrom(TextLineData other) {
            this.text = other.text;
            this.xOffset = other.xOffset; this.yOffset = other.yOffset; this.zOffset = other.zOffset;
            this.rotX = other.rotX; this.rotY = other.rotY; this.rotZ = other.rotZ;
            this.color = other.color; this.alignment = other.alignment;
            this.bold = other.bold; this.italic = other.italic; this.underline = other.underline; this.shadow = other.shadow;
            this.outline = other.outline; this.outlineColor = other.outlineColor;
            this.glowing = other.glowing;
            this.fontSize = other.fontSize;
            this.abcFont = other.abcFont;
            this.scaleX = other.scaleX; this.scaleY = other.scaleY; this.scaleZ = other.scaleZ;
            this.builtin = other.builtin;
        }

        public void applyFormatFrom(TextLineData other) {
            this.color = other.color; this.alignment = other.alignment;
            this.bold = other.bold; this.italic = other.italic; this.underline = other.underline; this.shadow = other.shadow;
            this.outline = other.outline; this.outlineColor = other.outlineColor;
            this.glowing = other.glowing;
            this.fontSize = other.fontSize;
        }

        public CompoundTag toNbt() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("text", text);
            nbt.putFloat("xOffset", xOffset); nbt.putFloat("yOffset", yOffset); nbt.putFloat("zOffset", zOffset);
            nbt.putFloat("rotX", rotX); nbt.putFloat("rotY", rotY); nbt.putFloat("rotZ", rotZ);
            nbt.putInt("color", color); nbt.putString("alignment", alignment.name());
            nbt.putBoolean("bold", bold); nbt.putBoolean("italic", italic);
            nbt.putBoolean("underline", underline); nbt.putBoolean("shadow", shadow);
            nbt.putBoolean("outline", outline);; nbt.putInt("outlineColor", outlineColor);
            nbt.putBoolean("glowing", glowing);
            nbt.putFloat("fontSize", fontSize);
            nbt.putString("abcFont", abcFont);
            nbt.putFloat("scaleX", scaleX); nbt.putFloat("scaleY", scaleY); nbt.putFloat("scaleZ", scaleZ);
            nbt.putBoolean("builtin", builtin);
            return nbt;
        }

        public static TextLineData fromNbt(CompoundTag nbt) {
            TextLineData data = new TextLineData(nbt.getString("text"));
            data.xOffset = nbt.getFloat("xOffset"); data.yOffset = nbt.getFloat("yOffset");
            data.zOffset = nbt.getFloat("zOffset"); data.color = nbt.getInt("color");
            data.rotX = nbt.getFloat("rotX"); data.rotY = nbt.getFloat("rotY"); data.rotZ = nbt.getFloat("rotZ");
            try { data.alignment = TextAlignment.valueOf(nbt.getString("alignment")); }
            catch (IllegalArgumentException e) { data.alignment = TextAlignment.CENTER_CENTER; }
            data.bold = nbt.getBoolean("bold"); data.italic = nbt.getBoolean("italic");
            data.underline = nbt.getBoolean("underline"); data.shadow = nbt.getBoolean("shadow");
            data.outline = nbt.getBoolean("outline");data.outlineColor = nbt.contains("outlineColor") ? nbt.getInt("outlineColor") : 0x000000;
            data.glowing = nbt.getBoolean("glowing");
            data.fontSize = nbt.contains("fontSize") ? nbt.getFloat("fontSize") : 1.0f;
            // 旧存档无该字段：默认空串（未指定字体）
            data.abcFont = nbt.contains("abcFont") ? nbt.getString("abcFont") : "";
            data.scaleX = nbt.contains("scaleX") ? nbt.getFloat("scaleX") : 1.0f;
            data.scaleY = nbt.contains("scaleY") ? nbt.getFloat("scaleY") : 1.0f;
            data.scaleZ = nbt.contains("scaleZ") ? nbt.getFloat("scaleZ") : 1.0f;
            data.builtin = nbt.getBoolean("builtin");
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

        public String getAbcFont() { return abcFont; } public void setAbcFont(String f) { this.abcFont = f != null ? f : ""; }
        public float getScaleX() { return scaleX; } public void setScaleX(float s) { this.scaleX = s; }
        public float getScaleY() { return scaleY; } public void setScaleY(float s) { this.scaleY = s; }
        public float getScaleZ() { return scaleZ; } public void setScaleZ(float s) { this.scaleZ = s; }
        public boolean isGlowing() { return glowing; } public void setGlowing(boolean g) { this.glowing = g; }
        public boolean isBuiltin() { return builtin; } public void setBuiltin(boolean b) { this.builtin = b; }
    }

    public enum TextAlignment {
        LEFT_TOP(0, 0), LEFT_CENTER(0, 1), LEFT_BOTTOM(0, 2),
        CENTER_TOP(1, 0), CENTER_CENTER(1, 1), CENTER_BOTTOM(1, 2),
        RIGHT_TOP(2, 0), RIGHT_CENTER(2, 1), RIGHT_BOTTOM(2, 2);
        public final int hAlign, vAlign;
        TextAlignment(int hAlign, int vAlign) { this.hAlign = hAlign; this.vAlign = vAlign; }
    }
}
