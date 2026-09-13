package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDistanceFromLocation2Entity extends CustomSignBlockEntity {
    private Expressway expressway1 = Expressway.NATIONAL;
    private String text1 = "";
    private String expresswayNumber = "";
    private String logoType1 = "national_logo_1";
    private String text3 = "";
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";

    public SignExpresswayDistanceFromLocation2Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_2_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.text1 = nbt.getString("text1");
        this.expresswayNumber = nbt.getString("expresswayNumber");
        // 旧存档兼容：无 logoType1 键时按原逻辑（国/省道 × 编号位数）推导初始值
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1").replace("provicial", "provincial") : legacyLogoType1();
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
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("expresswayNumber", this.expresswayNumber);
        nbt.putString("logoType1", this.logoType1);
        nbt.putString("text3", this.text3);
        nbt.putString("length1", this.length1);
        nbt.putString("length2", this.length2);
        nbt.putString("length3", this.length3);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignExpresswayDistanceFromLocation2EntityRenderer 的固定布局生成默认文本行：
     * renderLeftAlignedText(text1, -15, 11, 0.04)；
     * renderExpresswayLogo(expressway1, -11, 0, size 0.65) → -texture 行，纹理由 {logo1} 占位符选择（logoType1 四选一枚举字段），
     *   原 logo_1（宽版）时 adjustedX = andX + 1f 由 {logo1x} 占位符在渲染时附加；
     * renderExpresswayText(expresswayNumber, -11, -1, 0.045, zOffsetDelta 0.002)；
     * renderLeftAlignedText(text3, -15, -11, 0.04)；
     * renderRightAlignedText(lengthN, 10, 11/0/-11, 0.04) 与 km 单位 (14, 10.5/-0.5/-11.5, 0.025)。
     * 原单位编号 +1px 自动 X 偏移为动态布局逻辑，默认行不含。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.left("璜塘", -15f, 11f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", -11f, 0f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("G42", -11f, -1f, 0.045f, 0xFFFFFF, 0.002f));
        lines.add(SignTextLinesHelper.left("上海", -15f, -11f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("8", 10f, 11f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("17", 10f, 0f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("25", 10f, -11f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 14f, 10.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 14f, -0.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 14f, -11.5f, 0.025f, 0xFFFFFF));
        setTextLines(lines);
    }

    public Expressway getExpressway1() { return expressway1; }
    public void setExpressway1(Expressway expressway1) {
        this.expressway1 = expressway1;
        markDirtyAndUpdate();
    }
    public String getText1() { return text1; }
    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public String getExpresswayNumber() { return expresswayNumber; }
    public void setExpresswayNumber(String expresswayNumber) {
        this.expresswayNumber = expresswayNumber;
        markDirtyAndUpdate();
    }
    public String getLogoType1() { return logoType1; }
    public void setLogoType1(String logoType1) {
        this.logoType1 = logoType1;
        markDirtyAndUpdate();
    }
    public String getText3() { return text3; }
    public void setText3(String text3) {
        this.text3 = text3;
        markDirtyAndUpdate();
    }
    public String getLength1() { return length1; }
    public void setLength1(String length1) {
        this.length1 = length1;
        markDirtyAndUpdate();
    }
    public String getLength2() { return length2; }
    public void setLength2(String length2) {
        this.length2 = length2;
        markDirtyAndUpdate();
    }
    public String getLength3() { return length3; }
    public void setLength3(String length3) {
        this.length3 = length3;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "expresswayNumber" -> expresswayNumber;
            case "text3" -> text3;
            case "length1" -> length1;
            case "length2" -> length2;
            case "length3" -> length3;
            // 高速盾牌 logo 纹理：national/provincial × logo_1(宽)/logo_2(窄) 四选一枚举字段
            case "logo1" -> logoType1;
            // 对应原 adjustedX = andX + 1f：仅宽版 logo_1 附加 +1px X 偏移
            case "logo1x" -> "0"; // 兼容旧存档已保存的行；默认行不再附加偏移，X 由玩家在编辑界面调整
            default -> null;
        };
    }

    // 旧存档兼容推导：原 {logo1} 的派生逻辑——国道/省道 × 编号位数（1 位数字用窄版 logo_2，其余 logo_1）
    private String legacyLogoType1() {
        String kind = expressway1 == Expressway.PROVINCIAL ? "provincial" : "national";
        return kind + "_logo_" + (narrowLogo() ? "2" : "1");
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1" -> List.of(new FieldOptionGroup("高速Logo", "logoType1", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logoType1),
                    opt("国家高速公路（1位数）", "national_logo_2", logoType1),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logoType1),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logoType1))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logoType1" -> setLogoType1(value);
        }
    }

    // 原 renderExpresswayLogo：编号为 1 位数字时用窄版 logo_2，其余用 logo_1
    private boolean narrowLogo() {
        String digits = expresswayNumber == null ? "" : expresswayNumber.replaceAll("[^0-9]", "");
        return expresswayNumber != null && expresswayNumber.matches(".*\\d.*") && digits.length() == 1;
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
        Expressway(String name) { this.name = name; }
        public String getName() { return name; }
        public static Expressway fromName(String name) {
            for (Expressway dir : values()) {
                if (dir.name.equals(name)) return dir;
            }
            return NATIONAL;
        }
    }}
