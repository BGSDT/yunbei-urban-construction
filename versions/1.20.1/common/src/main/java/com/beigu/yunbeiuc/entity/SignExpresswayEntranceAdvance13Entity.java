package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayEntranceAdvance13Entity extends CustomSignBlockEntity {
    private Expressway expressway1 = Expressway.NATIONAL;
    private Expressway expressway2 = Expressway.NATIONAL;
    private String expresswayNumber1 = "";
    private String expresswayNumber2 = "";
    private String logoType1 = "national_logo_1";
    private String logoType2 = "national_logo_1";
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String text4 = "";

    public SignExpresswayEntranceAdvance13Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_13_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.expressway2 = Expressway.fromName(nbt.getString("expressway2"));
        this.expresswayNumber1 = nbt.getString("expresswayNumber1");
        this.expresswayNumber2 = nbt.getString("expresswayNumber2");
        // 旧存档兼容：无 logoTypeN 键时按原逻辑（国/省道 × 编号位数）推导初始值
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1").replace("provicial", "provincial") : legacyLogoType1();
        this.logoType2 = nbt.contains("logoType2") ? nbt.getString("logoType2").replace("provicial", "provincial") : legacyLogoType2();
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.text4 = nbt.getString("text4");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("expressway2", this.expressway2.getName());
        nbt.putString("expresswayNumber1", this.expresswayNumber1);
        nbt.putString("expresswayNumber2", this.expresswayNumber2);
        nbt.putString("logoType1", this.logoType1);
        nbt.putString("logoType2", this.logoType2);
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("text4", this.text4);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignExpresswayEntranceAdvance13EntityRenderer 的固定布局生成默认文本行：
     * renderExpresswayLogo(expressway1, -6, 10, size 0.65) / renderExpresswayLogo(expressway2, 6, 10, size 0.65)
     * → 两个 -texture 行，纹理由 {logo1}/{logo2} 占位符按国道/省道×编号位数选择；
     * renderExpresswayText(expresswayNumber1, -6, 9.5, 0.045, zOffsetDelta 0.002) /
     * renderExpresswayText(expresswayNumber2, 6, 9.5, 0.045, zOffsetDelta 0.002)；
     * renderCenteredText(text1, -7, 1, 0.035) / renderCenteredText(text2, -7, -6, 0.035) /
     * renderCenteredText(text3, 7, 1, 0.035) / renderCenteredText(text4, 7, -6, 0.035)，均 0xFFFFFF。
     * 原单位编号 +1px 自动 X 偏移为动态布局逻辑，默认行不含（一位数编号将偏左约 1 像素）。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", -6f, 10f, 0.65f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo2}.png", 6f, 10f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("G40", -6f, 9.5f, 0.045f, 0xFFFFFF, 0.002f));
        lines.add(SignTextLinesHelper.centeredWithZ("G42", 6f, 9.5f, 0.045f, 0xFFFFFF, 0.002f));
        lines.add(SignTextLinesHelper.centered("南通", -7f, 1f, 0.035f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("苏州", -7f, -6f, 0.035f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("信阳", 7f, 1f, 0.035f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("武汉", 7f, -6f, 0.035f, 0xFFFFFF));
        setTextLines(lines);
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
    public String getText4() { return text4; }
    public void setText4(String text4) {
        this.text4 = text4;
        markDirtyAndUpdate();
    }
    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "expresswayNumber1" -> expresswayNumber1;
            case "expresswayNumber2" -> expresswayNumber2;
            case "text1" -> text1;
            case "text2" -> text2;
            case "text3" -> text3;
            case "text4" -> text4;
            case "logo1" -> logoType1;
            case "logo2" -> logoType2;
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1" -> List.of(new FieldOptionGroup("高速Logo", "logoType1", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logoType1),
                    opt("国家高速公路（1位数）", "national_logo_2", logoType1),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logoType1),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logoType1))));
            case "logo2" -> List.of(new FieldOptionGroup("高速Logo", "logoType2", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logoType2),
                    opt("国家高速公路（1位数）", "national_logo_2", logoType2),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logoType2),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logoType2))));
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

    private static String expresswayLogoTexture(Expressway expressway, String expresswayNumber) {
        boolean hasDigits = expresswayNumber != null && expresswayNumber.matches(".*\\d.*");
        String digits = expresswayNumber == null ? "" : expresswayNumber.replaceAll("[^0-9]", "");
        boolean oneDigit = hasDigits && digits.length() == 1;
        String kind = expressway == Expressway.PROVINCIAL ? "provincial" : "national";
        return kind + "_logo_" + (oneDigit ? "2" : "1");
    }

    /** 旧存档兼容推导：按 expresswayN 国/省道 × expresswayNumberN 编号位数还原 logo 纹理段 */
    private String legacyLogoType1() {
        return expresswayLogoTexture(expressway1, expresswayNumber1);
    }

    private String legacyLogoType2() {
        return expresswayLogoTexture(expressway2, expresswayNumber2);
    }

    public enum Expressway {
        NATIONAL("national"),
        PROVINCIAL("provincial");

        private final String name;

        Expressway(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Expressway fromName(String name) {
            for (Expressway dir : values()) {
                if (dir.name.equals(name)) {
                    return dir;
                }
            }
            return NATIONAL;
        }
    }
}
