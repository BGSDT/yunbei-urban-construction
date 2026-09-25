package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayEntranceAdvance4Entity extends CustomSignBlockEntity {
    private SignCompassDirection direction1 = SignCompassDirection.SOUTH;
    private Expressway expressway1 = Expressway.NATIONAL;
    private String text1 = "";
    private String expresswayNumber1 = "";
    private String logoType1 = "national_logo_1";

    public SignExpresswayEntranceAdvance4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_4_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.direction1 = SignCompassDirection.fromName(nbt.getString("direction1"), SignCompassDirection.EAST);
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.text1 = nbt.getString("text1");
        this.expresswayNumber1 = nbt.getString("expresswayNumber1");
        // 旧存档兼容：无 logoType1 键时按原逻辑（国/省道 × 编号位数）推导初始值
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1").replace("provicial", "provincial") : legacyLogoType1();
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("expresswayNumber1", this.expresswayNumber1);
        nbt.putString("logoType1", this.logoType1);
        super.saveAdditional(nbt);
    }

    /**
     * 按原 SignExpresswayEntranceAdvance4EntityRenderer 的固定布局生成默认文本行：
     * renderExpresswayLogo(expressway1, -3, 7, size 0.65) → -texture 行，纹理由 {logo1} 占位符按国道/省道×编号位数选择；
     * renderDirectionLogo(direction1, 6, 7, size 0.4) → -texture 行，纹理由 {dir1} 占位符按方向枚举选择；
     * renderCenteredText(text1 + "方向", 0, -2, 0.035, 0xFFFFFF)；
     * renderExpresswayText(expresswayNumber1, -3, 6.5, 0.045, zOffsetDelta 0.002)。
     * 原单位编号 +1px 自动 X 偏移为动态布局逻辑，默认行不含（一位数编号将偏左约 1 像素）。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", -3f, 7f, 0.65f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{dir1}.png", 6f, 7f, 0.4f));
        lines.add(SignTextLinesHelper.centered("梅州方向", 0f, -2f, 0.035f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centeredWithZ("G25", -3f, 6.5f, 0.045f, 0xFFFFFF, 0.002f));
        setTextLines(lines);
    }

    public SignCompassDirection getDirection1() { return direction1; }
    public Expressway getExpressway1() { return expressway1; }
    public String getText1() { return text1; }
    public String getExpresswayNumber1() { return expresswayNumber1; }

    public void setDirection1(SignCompassDirection direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }
    public void setExpressway1(Expressway expressway1) {
        this.expressway1 = expressway1;
        markDirtyAndUpdate();
    }
    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public void setExpresswayNumber1(String expresswayNumber1) {
        this.expresswayNumber1 = expresswayNumber1;
        markDirtyAndUpdate();
    }
    public String getLogoType1() { return logoType1; }
    public void setLogoType1(String logoType1) {
        this.logoType1 = logoType1;
        markDirtyAndUpdate();
    }

    /** 旧存档兼容推导：按 expressway1 国/省道 × expresswayNumber1 编号位数还原 logo 纹理段 */
    private String legacyLogoType1() {
        boolean hasDigits = expresswayNumber1 != null && expresswayNumber1.matches(".*\\d.*");
        String digits = expresswayNumber1 == null ? "" : expresswayNumber1.replaceAll("[^0-9]", "");
        boolean oneDigit = hasDigits && digits.length() == 1;
        String kind = expressway1 == Expressway.PROVINCIAL ? "provincial" : "national";
        return kind + "_logo_" + (oneDigit ? "2" : "1");
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "expresswayNumber1" -> expresswayNumber1;
            case "logo1" -> logoType1;
            // 对应原 renderDirectionLogo：方向枚举 → 纹理名段（north/east/south/west）
            case "dir1" -> direction1 == null ? SignCompassDirection.EAST.getName() : direction1.getName();
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
            case "dir1" -> List.of(new FieldOptionGroup("方向", "direction1", List.of(
                    opt("北（N）", "north", direction1.getName()),
                    opt("南（S）", "south", direction1.getName()),
                    opt("西（W）", "west", direction1.getName()),
                    opt("东（E）", "east", direction1.getName()))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logoType1" -> setLogoType1(value);
            case "direction1" -> setDirection1(SignCompassDirection.fromName(value, SignCompassDirection.NORTH));
        }
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
