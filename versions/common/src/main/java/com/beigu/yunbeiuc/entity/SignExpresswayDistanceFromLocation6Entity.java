package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDistanceFromLocation6Entity extends CustomSignBlockEntity {
    private String logo1 = "national_logo_1";
    private String logo2 = "national_logo_1";
    private Expressway expressway1 = Expressway.NATIONAL;
    private Expressway expressway2 = Expressway.NATIONAL;
    private String expresswayNumber1 = "";
    private String expresswayNumber2 = "";

    public SignExpresswayDistanceFromLocation6Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_6_ENTITY.get(), pos, state);
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
        lines.add(SignTextLinesHelper.left("南通", -7f, 8f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("扬州", -7f, -3f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("南京", -7f, -10f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.right("25", 13.5f, 8f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.right("92", 13.5f, -3f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.right("115", 13.5f, -10f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("km", 13f, 7.5f, 0.025f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("km", 13f, -3.5f, 0.025f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("km", 13f, -10.5f, 0.025f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", -14f, 8f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("G15", -14f, 7.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo2}.png", -14f, -5.5f, 0.65f));
        lines.add(SignTextLinesHelper.centeredWithZ("G25", -14f, -6f, 0.045f, 0xFFFFFF, 0.002f, "b"));
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
