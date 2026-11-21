package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import com.beigu.yunbeiuc.api.mapper.BlockEntityMapper;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomSignBlockEntity extends BlockEntityMapper {
    private static final String NBT_TEXT_LINES = "TextLines";
    private static final String NBT_GLOWING_TEXT = "GlowingText";
    private static final String NBT_TEXT_CUSTOMIZED = "TextLinesCustomized";
    private static final String NBT_COMPAT_WRAPPER = "yunbeiuc_data";

    private List<TextLineData> textLines = new ArrayList<>();
    private boolean glowingText = false;
    private boolean textLinesCustomized = false;
    private boolean textLinesLoaded = false;
    private transient int editingLineIndex = -1;
    private transient int editingGizmoMode = -1;

    private final BlockState initialState;

    public CustomSignBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY.get(), pos, state);
    }

    protected CustomSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.initialState = state;
    }

    protected BlockState blockStateForDefaults() {
        if (level == null && initialState != null) return initialState;
        return getBlockState();
    }

    /**
     * 用注册名判断方块类型。
     *
     * <p>旧版本（尤其 1.16.5）里直接比较方块实例 {@code getBlock() == SignBlocks.XXX.get()}
     * 在部分加载/注册时机下不相等（注册表 supplier 与实际状态里的实例不是同一引用），
     * 导致所有 {@code if (方块类型)} 都落到 else 分支。改用命名空间+注册名比较后跨版本稳定。</p>
     */
    protected boolean isBlockType(dev.architectury.registry.registries.RegistrySupplier<? extends net.minecraft.world.level.block.Block> supplier) {
        if (supplier == null) return false;
        net.minecraft.world.level.block.Block expectedBlock = supplier.get();
        if (expectedBlock == null) return false;
        // blockId(Block) 在所有版本都返回统一的 Id（命名空间 + 注册名），不依赖对象引用。
        com.beigu.yunbeiuc.api.resource.Id expected = VersionServices.registries().blockId(expectedBlock);
        com.beigu.yunbeiuc.api.resource.Id actual =
                VersionServices.registries().blockId(blockStateForDefaults().getBlock());
        return expected != null && actual != null
                && expected.namespace().equals(actual.namespace())
                && expected.path().equals(actual.path());
    }

    public List<TextLineData> getTextLines() { return textLines; }

    public void setTextLines(List<TextLineData> textLines) {
        this.textLines = textLines != null ? new ArrayList<>(textLines) : new ArrayList<>();
        setChanged();
        notifyTextChanged();
    }

    public void applyUserTextLines(List<TextLineData> lines) {
        this.textLinesCustomized = true;
        setTextLines(lines);
        setChanged();
    }

    public boolean hasCustomizedTextLines() { return textLinesCustomized; }

    public void markTextLinesCustomized() {
        this.textLinesCustomized = true;
        setChanged();
        notifyTextChanged();
    }

    public boolean isGlowingText() { return glowingText; }

    public void setGlowingText(boolean glowingText) {
        this.glowingText = glowingText;
        setChanged();
        notifyTextChanged();
    }

    public void notifyTextChanged() {
        if (level == null) return;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.isLoaded(worldPosition)) return;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5) > 96 * 96) {
                continue;
            }
            VersionServices.blocks().sendBlockEntityUpdate(player, serverLevel, worldPosition,
                    createUpdatePacket());
        }
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
        nbt.put(NBT_TEXT_LINES, list);
        nbt.putBoolean(NBT_GLOWING_TEXT, glowingText);
        nbt.putBoolean(NBT_TEXT_CUSTOMIZED, textLinesCustomized);
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        CompoundTag data = textDataTag(nbt);
        if (data == null) {
            if (!textLinesLoaded) {
                textLines.clear();
            }
            setChanged();
            return;
        }
        if (data.contains(NBT_TEXT_LINES)) {
            List<TextLineData> loaded = new ArrayList<>();
            ListTag list = NbtCompat.getList(data, NBT_TEXT_LINES, 10);
            for (int i = 0; i < list.size(); i++) {
                loaded.add(TextLineData.fromNbt(NbtCompat.compoundAt(list, i)));
            }
            this.textLines = loaded;
            this.textLinesLoaded = true;
            this.textLinesCustomized = data.contains(NBT_TEXT_CUSTOMIZED)
                    ? NbtCompat.getBoolean(data, NBT_TEXT_CUSTOMIZED) : !loaded.isEmpty();
        }
        if (data.contains(NBT_GLOWING_TEXT)) {
            glowingText = NbtCompat.getBoolean(data, NBT_GLOWING_TEXT);
        }
    }

    public static boolean hasTextLinesData(CompoundTag nbt) {
        CompoundTag data = textDataTag(nbt);
        return data != null && data.contains(NBT_TEXT_LINES);
    }

    private static CompoundTag textDataTag(CompoundTag nbt) {
        if (nbt == null) return null;
        if (hasAnyTextKey(nbt)) return nbt;
        CompoundTag wrapper = NbtCompat.getCompound(nbt, NBT_COMPAT_WRAPPER);
        return wrapper != null && hasAnyTextKey(wrapper) ? wrapper : null;
    }

    private static boolean hasAnyTextKey(CompoundTag tag) {
        return tag.contains(NBT_TEXT_LINES) || tag.contains(NBT_GLOWING_TEXT)
                || tag.contains(NBT_TEXT_CUSTOMIZED);
    }

    @Nullable @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return createUpdatePacket(); }
    private static final java.util.regex.Pattern PLACEHOLDER_PATTERN = java.util.regex.Pattern.compile("\\{(\\w+)\\}");

    public String getPlaceholderValue(String key) { return null; }

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
        public static final int DEFAULT_GROUP_INTERVAL_MS = 3000;
        public static final int MIN_GROUP_INTERVAL_MS = 200;
        public static final int MAX_GROUP_INTERVAL_MS = 600000;

        /** 容器类型：轮播（按停留时间循环显示组内文本行）。 */
        public static final int CONTAINER_CAROUSEL = 0;
        /** 容器类型：滚动（组内文本行拼接成一条，在容器宽度内循环滚动）。 */
        public static final int CONTAINER_SCROLL = 1;

        public static final float DEFAULT_CONTAINER_WIDTH = 32f;
        public static final float MIN_CONTAINER_WIDTH = 8f;
        public static final float MAX_CONTAINER_WIDTH = 1024f;
        public static final float DEFAULT_CONTAINER_HEIGHT = 32f;
        public static final float MIN_CONTAINER_HEIGHT = 8f;
        public static final float MAX_CONTAINER_HEIGHT = 1024f;

        public static final float DEFAULT_SCROLL_SPEED = 0.75f;
        public static final float MAX_SCROLL_SPEED = 16f;

        public static final int SCROLL_LEFT = 0;
        public static final int SCROLL_RIGHT = 1;
        public static final int SCROLL_UP = 2;
        public static final int SCROLL_DOWN = 3;

        private String text;
        private float xOffset, yOffset, zOffset;
        private float rotX, rotY, rotZ;
        private int color;
        private TextAlignment alignment;
        private boolean bold, italic, underline, shadow;
        private boolean outline;
        private int outlineColor = 0x000000;
        private boolean glowing = false;
        private float fontSize;

        private String abcFont = "";
        private float scaleX, scaleY, scaleZ;

        private boolean builtin = false;

        private int groupId = 0;
        private String groupName = "";
        private int groupIntervalMs = DEFAULT_GROUP_INTERVAL_MS;

        private int containerMode = CONTAINER_CAROUSEL;
        private float containerWidth = DEFAULT_CONTAINER_WIDTH;
        private float containerHeight = DEFAULT_CONTAINER_HEIGHT;
        private float scrollSpeed = DEFAULT_SCROLL_SPEED;
        private int scrollDirection = SCROLL_LEFT;

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
            c.groupId = groupId; c.groupName = groupName; c.groupIntervalMs = groupIntervalMs;
            c.containerMode = containerMode; c.containerWidth = containerWidth;
            c.containerHeight = containerHeight;
            c.scrollSpeed = scrollSpeed; c.scrollDirection = scrollDirection;
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
            nbt.putInt("groupId", groupId);
            nbt.putString("groupName", groupName == null ? "" : groupName);
            nbt.putInt("groupIntervalMs", groupIntervalMs);
            nbt.putInt("containerMode", containerMode);
            nbt.putFloat("containerWidth", containerWidth);
            nbt.putFloat("containerHeight", containerHeight);
            nbt.putFloat("scrollSpeed", scrollSpeed);
            nbt.putInt("scrollDirection", scrollDirection);
            return nbt;
        }

        public static TextLineData fromNbt(CompoundTag nbt) {
            TextLineData data = new TextLineData(NbtCompat.getString(nbt, "text"));
            data.xOffset = NbtCompat.getFloat(nbt, "xOffset"); data.yOffset = NbtCompat.getFloat(nbt, "yOffset");
            data.zOffset = NbtCompat.getFloat(nbt, "zOffset"); data.color = NbtCompat.getInt(nbt, "color");
            data.rotX = NbtCompat.getFloat(nbt, "rotX"); data.rotY = NbtCompat.getFloat(nbt, "rotY"); data.rotZ = NbtCompat.getFloat(nbt, "rotZ");
            try { data.alignment = TextAlignment.valueOf(NbtCompat.getString(nbt, "alignment")); }
            catch (IllegalArgumentException e) { data.alignment = TextAlignment.CENTER_CENTER; }
            data.bold = NbtCompat.getBoolean(nbt, "bold"); data.italic = NbtCompat.getBoolean(nbt, "italic");
            data.underline = NbtCompat.getBoolean(nbt, "underline"); data.shadow = NbtCompat.getBoolean(nbt, "shadow");
            data.outline = NbtCompat.getBoolean(nbt, "outline");data.outlineColor = nbt.contains("outlineColor") ? NbtCompat.getInt(nbt, "outlineColor") : 0x000000;
            data.glowing = NbtCompat.getBoolean(nbt, "glowing");
            data.fontSize = nbt.contains("fontSize") ? NbtCompat.getFloat(nbt, "fontSize") : 1.0f;
            data.abcFont = nbt.contains("abcFont") ? NbtCompat.getString(nbt, "abcFont") : "";
            data.scaleX = nbt.contains("scaleX") ? NbtCompat.getFloat(nbt, "scaleX") : 1.0f;
            data.scaleY = nbt.contains("scaleY") ? NbtCompat.getFloat(nbt, "scaleY") : 1.0f;
            data.scaleZ = nbt.contains("scaleZ") ? NbtCompat.getFloat(nbt, "scaleZ") : 1.0f;
            data.builtin = NbtCompat.getBoolean(nbt, "builtin");
            data.groupId = nbt.contains("groupId") ? NbtCompat.getInt(nbt, "groupId") : 0;
            data.groupName = nbt.contains("groupName") ? NbtCompat.getString(nbt, "groupName") : "";
            data.groupIntervalMs = nbt.contains("groupIntervalMs")
                    ? clampGroupInterval(NbtCompat.getInt(nbt, "groupIntervalMs")) : DEFAULT_GROUP_INTERVAL_MS;
            data.containerMode = nbt.contains("containerMode") ? NbtCompat.getInt(nbt, "containerMode") : CONTAINER_CAROUSEL;
            data.containerWidth = nbt.contains("containerWidth")
                    ? clampContainerWidth(NbtCompat.getFloat(nbt, "containerWidth")) : DEFAULT_CONTAINER_WIDTH;
            data.containerHeight = nbt.contains("containerHeight")
                    ? clampContainerHeight(NbtCompat.getFloat(nbt, "containerHeight")) : DEFAULT_CONTAINER_HEIGHT;
            data.scrollSpeed = nbt.contains("scrollSpeed")
                    ? clampScrollSpeed(NbtCompat.getFloat(nbt, "scrollSpeed")) : DEFAULT_SCROLL_SPEED;
            data.scrollDirection = nbt.contains("scrollDirection")
                    ? clampScrollDirection(NbtCompat.getInt(nbt, "scrollDirection")) : SCROLL_LEFT;
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

        public static int clampGroupInterval(int ms) {
            if (ms <= 0) return DEFAULT_GROUP_INTERVAL_MS;
            return Math.max(MIN_GROUP_INTERVAL_MS, Math.min(MAX_GROUP_INTERVAL_MS, ms));
        }

        public static float clampContainerWidth(float width) {
            if (width <= 0) return DEFAULT_CONTAINER_WIDTH;
            return Math.max(MIN_CONTAINER_WIDTH, Math.min(MAX_CONTAINER_WIDTH, width));
        }

        public static float clampContainerHeight(float height) {
            if (height <= 0) return DEFAULT_CONTAINER_HEIGHT;
            return Math.max(MIN_CONTAINER_HEIGHT, Math.min(MAX_CONTAINER_HEIGHT, height));
        }

        public static float clampScrollSpeed(float speed) {
            if (speed < 0) return 0f;
            return Math.min(MAX_SCROLL_SPEED, speed);
        }

        public static int clampScrollDirection(int dir) {
            return dir < SCROLL_LEFT || dir > SCROLL_DOWN ? SCROLL_LEFT : dir;
        }

        public int getGroupId() { return groupId; }
        public void setGroupId(int id) { this.groupId = Math.max(0, id); }
        public boolean isInGroup() { return groupId > 0; }

        public String getGroupName() { return groupName == null ? "" : groupName; }
        public void setGroupName(String name) { this.groupName = name == null ? "" : name; }

        public int getGroupIntervalMs() { return groupIntervalMs <= 0 ? DEFAULT_GROUP_INTERVAL_MS : groupIntervalMs; }
        public void setGroupIntervalMs(int ms) { this.groupIntervalMs = clampGroupInterval(ms); }

        public void applyGroupInfo(int id, String name, int intervalMs) {
            this.groupId = Math.max(0, id);
            this.groupName = name == null ? "" : name;
            this.groupIntervalMs = clampGroupInterval(intervalMs);
        }

        public int getContainerMode() { return containerMode; }
        public void setContainerMode(int mode) {
            this.containerMode = mode == CONTAINER_SCROLL ? CONTAINER_SCROLL : CONTAINER_CAROUSEL;
        }
        public boolean isScrollContainer() { return containerMode == CONTAINER_SCROLL; }

        public float getContainerWidth() {
            return containerWidth <= 0 ? DEFAULT_CONTAINER_WIDTH : containerWidth;
        }
        public void setContainerWidth(float width) { this.containerWidth = clampContainerWidth(width); }

        public float getContainerHeight() {
            return containerHeight <= 0 ? DEFAULT_CONTAINER_HEIGHT : containerHeight;
        }
        public void setContainerHeight(float height) { this.containerHeight = clampContainerHeight(height); }

        public float getScrollSpeed() { return clampScrollSpeed(scrollSpeed); }
        public void setScrollSpeed(float speed) { this.scrollSpeed = clampScrollSpeed(speed); }

        public int getScrollDirection() { return clampScrollDirection(scrollDirection); }
        public void setScrollDirection(int dir) { this.scrollDirection = clampScrollDirection(dir); }

        /** 新建/追加进容器时，从容器首行复制容器配置。 */
        public void applyContainerInfoFrom(TextLineData leader) {
            if (leader == null) return;
            this.containerMode = leader.containerMode;
            this.containerWidth = leader.getContainerWidth();
            this.containerHeight = leader.getContainerHeight();
            this.scrollSpeed = leader.getScrollSpeed();
            this.scrollDirection = leader.getScrollDirection();
        }
    }

    public enum TextAlignment {
        LEFT_TOP(0, 0), LEFT_CENTER(0, 1), LEFT_BOTTOM(0, 2),
        CENTER_TOP(1, 0), CENTER_CENTER(1, 1), CENTER_BOTTOM(1, 2),
        RIGHT_TOP(2, 0), RIGHT_CENTER(2, 1), RIGHT_BOTTOM(2, 2);
        public final int hAlign, vAlign;
        TextAlignment(int hAlign, int vAlign) { this.hAlign = hAlign; this.vAlign = vAlign; }
    }
}
