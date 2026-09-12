package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideConfirmation1Entity extends CustomSignBlockEntity {
    private Unit unit1 = Unit.KILOMETRE;
    private Unit unit2 = Unit.KILOMETRE;
    private Unit unit3 = Unit.KILOMETRE;
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";

    public SignGuideConfirmation1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_CONFIRMATION_1_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.unit1 = Unit.fromName(nbt.getString("unit1"));
        this.unit2 = Unit.fromName(nbt.getString("unit2"));
        this.unit3 = Unit.fromName(nbt.getString("unit3"));
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("unit1", this.unit1.getName());
        nbt.putString("unit2", this.unit2.getName());
        nbt.putString("unit3", this.unit3.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("length1", this.length1);
        nbt.putString("length2", this.length2);
        nbt.putString("length3", this.length3);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignGuideConfirmation1EntityRenderer 的固定布局生成默认文本行（按方块分支）：
     * renderLeftAlignedText(text1..3, 文本X, 9/0/-9, 0.04)、
     * renderRightAlignedText(length1..3, 长度X, 9/0/-9, 0.04)、
     * renderRightAlignedText({unitN}, 单位X, 8.5/-0.5/-9.5, 0.025)。
     * 原渲染器分支：SIGN_GUIDE_CONFIRMATION_1 用 文本X=-17/长度X=13/单位X=17，
     * 其余方块整体内收 2px（-15/11/15），此处同样按 getCachedState() 判断。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean main = getCachedState().getBlock() == SignBlocks.SIGN_GUIDE_CONFIRMATION_1.get();
        float textX = main ? -17f : -15f;
        float lengthX = main ? 13f : 11f;
        float unitX = main ? 17f : 15f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.left("{text1}", textX, 9f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.left("{text2}", textX, 0f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.left("{text3}", textX, -9f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length1}", lengthX, 9f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length2}", lengthX, 0f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length3}", lengthX, -9f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{unit1}", unitX, 8.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{unit2}", unitX, -0.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{unit3}", unitX, -9.5f, 0.025f, 0xFFFFFF));
        setTextLines(lines);
    }

    public Unit getUnit1() {
        return unit1;
    }
    public Unit getUnit2() {
        return unit2;
    }
    public Unit getUnit3() {
        return unit3;
    }
    public String getText1() {
        return text1;
    }
    public String getText2() {
        return text2;
    }
    public String getText3() {
        return text3;
    }
    public String getLength1() {
        return length1;
    }
    public String getLength2() {
        return length2;
    }
    public String getLength3() {
        return length3;
    }

    public void setUnit1(Unit unit1) {
        this.unit1 = unit1;
        markDirtyAndUpdate();
    }
    public void setUnit2(Unit unit2) {
        this.unit2 = unit2;
        markDirtyAndUpdate();
    }
    public void setUnit3(Unit unit3) {
        this.unit3 = unit3;
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
    public void setLength1(String length1) {
        this.length1 = length1;
        markDirtyAndUpdate();
    }
    public void setLength2(String length2) {
        this.length2 = length2;
        markDirtyAndUpdate();
    }
    public void setLength3(String length3) {
        this.length3 = length3;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "text2" -> text2;
            case "text3" -> text3;
            case "length1" -> length1;
            case "length2" -> length2;
            case "length3" -> length3;
            // 对应原渲染器：KILOMETRE 显示 "km"，其余显示 "m"
            case "unit1" -> unit1 == Unit.KILOMETRE ? "km" : "m";
            case "unit2" -> unit2 == Unit.KILOMETRE ? "km" : "m";
            case "unit3" -> unit3 == Unit.KILOMETRE ? "km" : "m";
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "unit1" -> List.of(new FieldOptionGroup("单位", "unit1", List.of(
                    opt("公里（km）", "kilometre", unit1.getName()),
                    opt("米（m）", "metre", unit1.getName()))));
            case "unit2" -> List.of(new FieldOptionGroup("单位", "unit2", List.of(
                    opt("公里（km）", "kilometre", unit2.getName()),
                    opt("米（m）", "metre", unit2.getName()))));
            case "unit3" -> List.of(new FieldOptionGroup("单位", "unit3", List.of(
                    opt("公里（km）", "kilometre", unit3.getName()),
                    opt("米（m）", "metre", unit3.getName()))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "unit1" -> setUnit1(Unit.fromName(value));
            case "unit2" -> setUnit2(Unit.fromName(value));
            case "unit3" -> setUnit3(Unit.fromName(value));
        }
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public enum Unit {
        KILOMETRE("kilometre"),
        METRE("metre");

        private final String name;

        Unit(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Unit fromName(String name) {
            for (Unit value : values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return KILOMETRE;
        }
    }
}
