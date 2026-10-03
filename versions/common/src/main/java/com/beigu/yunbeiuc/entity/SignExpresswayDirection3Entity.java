package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDirection3Entity extends CustomSignBlockEntity {
    private Expressway expressway1 = Expressway.NATIONAL;
    private String text1 = "";
    private String expresswayNumber1 = "";
    private String logoType1 = "national_logo_2";

    public SignExpresswayDirection3Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_3_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.text1 = nbt.getString("text1");
        this.expresswayNumber1 = nbt.getString("expresswayNumber1");
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1").replace("provicial", "provincial") : legacyLogoType1();
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("expresswayNumber1", this.expresswayNumber1);
        nbt.putString("logoType1", this.logoType1);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        float x = blockStateForDefaults().getBlock() == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_4.get() ? -4.5f : 4.5f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", x, 7f, 0.85f));
        lines.add(SignTextLinesHelper.centered("天津", x, -7f, 0.05f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.centeredWithZ("G3", x, 6.5f, 0.06f, 0xFFFFFF, 0.002f, "b"));
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
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), 3);
        }
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "expresswayNumber1" -> expresswayNumber1;
            case "logo1" -> logoType1;
            default -> null;
        };
    }

    private String legacyLogoType1() {
        boolean hasDigits = expresswayNumber1 != null && expresswayNumber1.matches(".*\\d.*");
        String digits = expresswayNumber1 == null ? "" : expresswayNumber1.replaceAll("[^0-9]", "");
        boolean oneDigit = hasDigits && digits.length() == 1;
        String kind = expressway1 == Expressway.PROVINCIAL ? "provincial" : "national";
        return kind + "_logo_" + (oneDigit ? "2" : "1");
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
