package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDistanceFromLocation6Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";
    private Expressway expressway1 = Expressway.NATIONAL;
    private Expressway expressway2 = Expressway.NATIONAL;
    private String expresswayNumber1 = "";
    private String expresswayNumber2 = "";
    private String logoType1 = "national_logo_1";
    private String logoType2 = "national_logo_1";

    public SignExpresswayDistanceFromLocation6Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_6_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.expressway2 = Expressway.fromName(nbt.getString("expressway2"));
        this.expresswayNumber1 = nbt.getString("expresswayNumber1");
        this.expresswayNumber2 = nbt.getString("expresswayNumber2");
        // 旧存档兼容：无 logoTypeN 键时按原逻辑（国/省道 × 编号位数）推导初始值
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1") : legacyLogoType1();
        this.logoType2 = nbt.contains("logoType2") ? nbt.getString("logoType2") : legacyLogoType2();
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("length1", this.length1);
        nbt.putString("length2", this.length2);
        nbt.putString("length3", this.length3);
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("expressway2", this.expressway2.getName());
        nbt.putString("expresswayNumber1", this.expresswayNumber1);
        nbt.putString("expresswayNumber2", this.expresswayNumber2);
        nbt.putString("logoType1", this.logoType1);
        nbt.putString("logoType2", this.logoType2);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignExpresswayDistanceFromLocation6EntityRenderer 的固定布局生成默认文本行：
     * renderLeftAlignedText(text1..3, -7, 8/-3/-10, 0.04)、
     * renderRightAlignedText(length1..3, 13.5, 8/-3/-10, 0.04)、
     * renderRightAlignedText("km", 17.5, 7.5/-3.5/-10.5, 0.025)、
     * renderExpresswayLogo(expressway1, -14, 8, 0.65)、
     * renderExpresswayText(expresswayNumber1, -14, 7.5, 0.045, zOffsetDelta 0.002, CENTER)、
     * renderExpresswayLogo(expressway2, -14, -5.5, 0.65)、
     * renderExpresswayText(expresswayNumber2, -14, -6, 0.045, zOffsetDelta 0.002, CENTER)。
     * 原单位编号 +1px 自动 X 偏移为动态布局逻辑，默认行不含；
     * logo 的 +1px 偏移（宽版 _1 纹理）由 {logo1x}/{logo2x} 占位符动态补充。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.left("{text1}", -7f, 8f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.left("{text2}", -7f, -3f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.left("{text3}", -7f, -10f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length1}", 13.5f, 8f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length2}", 13.5f, -3f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length3}", 13.5f, -10f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 17.5f, 7.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 17.5f, -3.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 17.5f, -10.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", -14f, 8f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("{expresswayNumber1}", -14f, 7.5f, 0.045f, 0xFFFFFF, 0.002f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo2}.png", -14f, -5.5f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("{expresswayNumber2}", -14f, -6f, 0.045f, 0xFFFFFF, 0.002f));
        setTextLines(lines);
    }

    public String getText1() { return text1; }
    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public String getText2() { return text2; }
    public void setText2(String text2) {
        this.text2 = text2;
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
    public Expressway getExpressway1() { return expressway1; }
    public void setExpressway1(Expressway expressway1) {
        this.expressway1 = expressway1;
        markDirtyAndUpdate();
    }
    public Expressway getExpressway2() { return expressway2; }
    public void setExpressway2(Expressway expressway2) {
        this.expressway2 = expressway2;
        markDirtyAndUpdate();
    }
    public String getExpresswayNumber1() { return expresswayNumber1; }
    public void setExpresswayNumber1(String expresswayNumber1) {
        this.expresswayNumber1 = expresswayNumber1;
        markDirtyAndUpdate();
    }
    public String getExpresswayNumber2() { return expresswayNumber2; }
    public void setExpresswayNumber2(String expresswayNumber2) {
        this.expresswayNumber2 = expresswayNumber2;
        markDirtyAndUpdate();
    }
    public String getLogoType1() { return logoType1; }
    public void setLogoType1(String logoType1) {
        this.logoType1 = logoType1;
        markDirtyAndUpdate();
    }
    public String getLogoType2() { return logoType2; }
    public void setLogoType2(String logoType2) {
        this.logoType2 = logoType2;
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
            case "expresswayNumber1" -> expresswayNumber1;
            case "expresswayNumber2" -> expresswayNumber2;
            // 高速盾牌 logo 纹理：national/provicial × logo_1(宽)/logo_2(窄) 四选一枚举字段
            case "logo1" -> logoType1;
            case "logo2" -> logoType2;
            // 对应原 renderExpresswayLogo 的宽版 _1 纹理 +1px X 偏移
            case "logo1x", "logo2x" -> "0"; // 兼容旧存档已保存的行；默认行不再附加偏移，X 由玩家在编辑界面调整
            default -> null;
        };
    }

    // 旧存档兼容推导：原 {logoN} 的派生逻辑——国道/省道 × 编号位数（1 位数字用窄版 logo_2，其余 logo_1）
    private String legacyLogoType1() {
        return expresswayLogoTexture(expressway1, expresswayNumber1);
    }

    private String legacyLogoType2() {
        return expresswayLogoTexture(expressway2, expresswayNumber2);
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1" -> List.of(new FieldOptionGroup("高速Logo", "logoType1", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logoType1),
                    opt("国家高速公路（1位数）", "national_logo_2", logoType1),
                    opt("省级高速公路（2位数）", "provicial_logo_1", logoType1),
                    opt("省级高速公路（1位数）", "provicial_logo_2", logoType1))));
            case "logo2" -> List.of(new FieldOptionGroup("高速Logo", "logoType2", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logoType2),
                    opt("国家高速公路（1位数）", "national_logo_2", logoType2),
                    opt("省级高速公路（2位数）", "provicial_logo_1", logoType2),
                    opt("省级高速公路（1位数）", "provicial_logo_2", logoType2))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logoType1" -> setLogoType1(value);
            case "logoType2" -> setLogoType2(value);
        }
    }

    /** 与原渲染器 switch 一致：省道用 provicial（保留原资源拼写），无数字或多位数字用宽版 _1 */
    private static String expresswayLogoTexture(Expressway expressway, String expresswayNumber) {
        String kind = expressway == Expressway.PROVINCIAL ? "provicial" : "national";
        return kind + "_logo_" + (isNarrowLogo(expresswayNumber) ? "2" : "1");
    }

    /** 原判定：编号为空或不含数字视为宽版（_1）；提取出的数字串长度为 1 视为窄版（_2） */
    private static boolean isNarrowLogo(String expresswayNumber) {
        if (expresswayNumber == null || expresswayNumber.trim().isEmpty() || !expresswayNumber.matches(".*\\d.*")) {
            return false;
        }
        return expresswayNumber.replaceAll("[^0-9]", "").length() == 1;
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
    }
}
