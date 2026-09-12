package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDirection3Entity extends CustomSignBlockEntity {
    private Expressway expressway1 = Expressway.NATIONAL;
    private String text1 = "";
    private String expresswayNumber1 = "";
    private String logoType1 = "national_logo_1";

    public SignExpresswayDirection3Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_3_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.text1 = nbt.getString("text1");
        this.expresswayNumber1 = nbt.getString("expresswayNumber1");
        // 旧存档兼容：无 logoType1 键时按原逻辑（国/省道 × 编号位数）推导初始值
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1") : legacyLogoType1();
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("expresswayNumber1", this.expresswayNumber1);
        nbt.putString("logoType1", this.logoType1);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignExpresswayDirection3EntityRenderer 的固定布局生成默认文本行（x 按方块镜像：
     * SIGN_EXPRESSWAY_DIRECTION_3 → 4.5 / SIGN_EXPRESSWAY_DIRECTION_4 → -4.5）：
     * renderExpresswayLogo(expressway1, x, 7, size 0.85) → -texture 行，纹理由 {logo1} 占位符选择（logoType1 四选一枚举字段）；
     * renderCenteredText(text1, x, -7, 0.05, 0xFFFFFF)；
     * renderExpresswayText(expresswayNumber1, x, 6.5, 0.06, zOffsetDelta 0.002)。
     * 原单位编号 +1px 自动 X 偏移为动态布局逻辑，默认行不含（一位数编号将偏左约 1 像素）。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        float x = getCachedState().getBlock() == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_4.get() ? -4.5f : 4.5f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", x, 7f, 0.85f));
        lines.add(SignTextLinesHelper.centered("{text1}", x, -7f, 0.05f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centeredWithZ("{expresswayNumber1}", x, 6.5f, 0.06f, 0xFFFFFF, 0.002f));
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
    public String getExpresswayNumber1() { return expresswayNumber1; }
    public void setExpresswayNumber1(String expresswayNumber1) {
        this.expresswayNumber1 = expresswayNumber1;
        markDirtyAndUpdate();
    }
    public String getLogoType1() { return logoType1; }
    public void setLogoType1(String logoType1) {
        this.logoType1 = logoType1;
        markDirtyAndUpdate();
    }
    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "expresswayNumber1" -> expresswayNumber1;
            // 高速盾牌 logo 纹理：national/provicial × logo_1(宽)/logo_2(窄) 四选一枚举字段
            case "logo1" -> logoType1;
            default -> null;
        };
    }

    // 旧存档兼容推导：原 {logo1} 的派生逻辑——国道/省道 × 编号位数（1 位数字用窄版 logo_2，其余 logo_1）
    private String legacyLogoType1() {
        boolean hasDigits = expresswayNumber1 != null && expresswayNumber1.matches(".*\\d.*");
        String digits = expresswayNumber1 == null ? "" : expresswayNumber1.replaceAll("[^0-9]", "");
        boolean oneDigit = hasDigits && digits.length() == 1;
        String kind = expressway1 == Expressway.PROVINCIAL ? "provicial" : "national";
        return kind + "_logo_" + (oneDigit ? "2" : "1");
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1" -> List.of(new FieldOptionGroup("高速Logo", "logoType1", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logoType1),
                    opt("国家高速公路（1位数）", "national_logo_2", logoType1),
                    opt("省级高速公路（2位数）", "provicial_logo_1", logoType1),
                    opt("省级高速公路（1位数）", "provicial_logo_2", logoType1))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logoType1" -> setLogoType1(value);
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
