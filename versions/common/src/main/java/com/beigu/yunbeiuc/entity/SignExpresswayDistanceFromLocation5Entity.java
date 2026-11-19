package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDistanceFromLocation5Entity extends CustomSignBlockEntity {
    private String logo1Value = "provincial_logo_1";
    private String logo2Value = "provincial_logo_1";
    private String logo3Value = "national_logo_2";
    private Expressway expressway1 = Expressway.PROVINCIAL;
    private Expressway expressway2 = Expressway.PROVINCIAL;
    private Expressway expressway3 = Expressway.NATIONAL;
    private String logo1 = "provincial_logo_1";
    private String logo2 = "provincial_logo_1";
    private String logo3 = "national_logo_2";
    private String expresswayNumber1 = "";
    private String expresswayNumber2 = "";
    private String expresswayNumber3 = "";

    public SignExpresswayDistanceFromLocation5Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_5_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.logo1Value = NbtCompat.getString(nbt, "logoType1");
        this.logo2Value = NbtCompat.getString(nbt, "logoType2");
        this.logo3Value = NbtCompat.getString(nbt, "logoType3");
        this.expressway1 = Expressway.fromName(NbtCompat.getString(nbt, "expressway1"));
        this.expressway2 = Expressway.fromName(NbtCompat.getString(nbt, "expressway2"));
        this.expressway3 = Expressway.fromName(NbtCompat.getString(nbt, "expressway3"));
        this.logo1 = nbt.contains("logoType1") ? NbtCompat.getString(nbt, "logoType1").replace("provicial", "provincial") : legacyLogoType1();
        this.logo2 = nbt.contains("logoType2") ? NbtCompat.getString(nbt, "logoType2").replace("provicial", "provincial") : legacyLogoType2();
        this.logo3 = nbt.contains("logoType3") ? NbtCompat.getString(nbt, "logoType3").replace("provicial", "provincial") : legacyLogoType3();
        if (!CustomSignBlockEntity.hasTextLinesData(nbt) && !hasCustomizedTextLines()) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("expressway2", this.expressway2.getName());
        nbt.putString("expressway3", this.expressway3.getName());
        nbt.putString("logoType1", this.logo1Value);
        nbt.putString("logoType2", this.logo2Value);
        nbt.putString("logoType3", this.logo3Value);
        super.saveAdditionalCompat(nbt);
    }

    private String legacyLogoType1() {
                return expresswayLogoTexture(expressway1, expresswayNumber1);
            }

    private String legacyLogoType2() {
                return expresswayLogoTexture(expressway2, expresswayNumber2);
            }

    private String legacyLogoType3() {
                return expresswayLogoTexture(expressway3, expresswayNumber3);
            }

    private static String expresswayLogoTexture(Expressway expressway, String expresswayNumber) {
                String kind = expressway == Expressway.PROVINCIAL ? "provincial" : "national";
                return kind + "_logo_" + (isNarrowLogo(expresswayNumber) ? "2" : "1");
            }

    private static boolean isNarrowLogo(String expresswayNumber) {
                if (expresswayNumber == null || expresswayNumber.trim().isEmpty() || !expresswayNumber.matches(".*\\d.*")) {
                    return false;
                }
                return expresswayNumber.replaceAll("[^0-9]", "").length() == 1;
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

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.left("洋后", -14f, 1f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("衢州", -7f, -7f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("福州", -7f, -13f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.right("8", 13.5f, 1f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.right("112", 13.5f, -7f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.right("215", 13.5f, -13f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("km", 13f, 0.5f, 0.025f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("km", 13f, -7.5f, 0.025f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("km", 13f, -13.5f, 0.025f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", -7f, 10f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("S10", -7f, 9.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo2}.png", 7f, 10f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("S52", 7f, 9.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo3}.png", -13f, -9f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("G3", -13f, -9.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
        setTextLines(lines);
    }

    public String getLogo1Value() {
        return logo1Value;
    }

    public void setLogo1Value(String logo1Value) {
        this.logo1Value = logo1Value;
        markDirtyAndUpdate();
    }

    public String getLogo2Value() {
        return logo2Value;
    }

    public void setLogo2Value(String logo2Value) {
        this.logo2Value = logo2Value;
        markDirtyAndUpdate();
    }

    public String getLogo3Value() {
        return logo3Value;
    }

    public void setLogo3Value(String logo3Value) {
        this.logo3Value = logo3Value;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "logo1" -> logo1Value;
            case "logo2" -> logo2Value;
            case "logo3" -> logo3Value;
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1" -> List.of(new FieldOptionGroup("高速Logo", "logo1", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logo1Value),
                    opt("国家高速公路（1位数）", "national_logo_2", logo1Value),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logo1Value),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logo1Value))));
            case "logo2" -> List.of(new FieldOptionGroup("高速Logo", "logo2", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logo2Value),
                    opt("国家高速公路（1位数）", "national_logo_2", logo2Value),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logo2Value),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logo2Value))));
            case "logo3" -> List.of(new FieldOptionGroup("高速Logo", "logo3", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logo3Value),
                    opt("国家高速公路（1位数）", "national_logo_2", logo3Value),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logo3Value),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logo3Value))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logo1" -> setLogo1Value(value);
            case "logo2" -> setLogo2Value(value);
            case "logo3" -> setLogo3Value(value);
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
