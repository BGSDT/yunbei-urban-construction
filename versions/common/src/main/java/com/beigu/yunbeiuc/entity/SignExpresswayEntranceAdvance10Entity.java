package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayEntranceAdvance10Entity extends CustomSignBlockEntity {
    private String logo1 = "national_logo_2";
    private String logo2 = "national_logo_2";
    private Expressway expressway1 = Expressway.NATIONAL;
    private Expressway expressway2 = Expressway.NATIONAL;
    private String expresswayNumber1 = "";
    private String expresswayNumber2 = "";

    public SignExpresswayEntranceAdvance10Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_10_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.logo1 = NbtCompat.getString(nbt, "logoType1");
        this.logo2 = NbtCompat.getString(nbt, "logoType2");
        this.expressway1 = Expressway.fromName(NbtCompat.getString(nbt, "expressway1"));
        this.expressway2 = Expressway.fromName(NbtCompat.getString(nbt, "expressway2"));
        this.logo1 = nbt.contains("logoType1") ? NbtCompat.getString(nbt, "logoType1").replace("provicial", "provincial") : legacyLogoType1();
        this.logo2 = nbt.contains("logoType2") ? NbtCompat.getString(nbt, "logoType2").replace("provicial", "provincial") : legacyLogoType2();
        if (!CustomSignBlockEntity.hasTextLinesData(nbt) && !hasCustomizedTextLines()) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("expressway2", this.expressway2.getName());
        nbt.putString("logoType1", this.logo1);
        nbt.putString("logoType2", this.logo2);
        super.saveAdditionalCompat(nbt);
    }

    private String legacyLogoType1() {
            return expresswayLogoTexture(expressway1, expresswayNumber1);
        }

    private String legacyLogoType2() {
            return expresswayLogoTexture(expressway2, expresswayNumber2);
        }

    private static String expresswayLogoTexture(Expressway expressway, String expresswayNumber) {
            boolean hasDigits = expresswayNumber != null && expresswayNumber.matches(".*\\d.*");
            String digits = expresswayNumber == null ? "" : expresswayNumber.replaceAll("[^0-9]", "");
            boolean oneDigit = hasDigits && digits.length() == 1;
            String kind = expressway == Expressway.PROVINCIAL ? "provincial" : "national";
            return kind + "_logo_" + (oneDigit ? "2" : "1");
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

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", -6f, 7f, 0.65f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo2}.png", 6f, 7f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("G2", -6f, 6.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
        lines.add(SignTextLinesHelper.centeredWithZ("G3", 6f, 6.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
        lines.add(SignTextLinesHelper.centered("天津", -7f, -2f, 0.035f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.centered("济南", 7f, -2f, 0.035f, 0xFFFFFF, "a"));
        setTextLines(lines);
    }

    public String getLogo1() {
        return logo1;
    }

    public void setLogo1(String logo1) {
        this.logo1 = logo1;
        markDirtyAndUpdate();
    }

    public String getLogo2() {
        return logo2;
    }

    public void setLogo2(String logo2) {
        this.logo2 = logo2;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "logo1" -> logo1;
            case "logo2" -> logo2;
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1" -> List.of(new FieldOptionGroup("高速Logo", "logo1", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logo1),
                    opt("国家高速公路（1位数）", "national_logo_2", logo1),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logo1),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logo1))));
            case "logo2" -> List.of(new FieldOptionGroup("高速Logo", "logo2", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logo2),
                    opt("国家高速公路（1位数）", "national_logo_2", logo2),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logo2),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logo2))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logo1" -> setLogo1(value);
            case "logo2" -> setLogo2(value);
            default -> { }
        }
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }
}
