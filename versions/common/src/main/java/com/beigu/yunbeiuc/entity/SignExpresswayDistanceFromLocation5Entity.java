package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDistanceFromLocation5Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";
    private Expressway expressway1 = Expressway.PROVINCIAL;
    private Expressway expressway2 = Expressway.PROVINCIAL;
    private Expressway expressway3 = Expressway.NATIONAL;
    private String expresswayNumber1 = "";
    private String expresswayNumber2 = "";
    private String expresswayNumber3 = "";
    private String logoType1 = "provincial_logo_1";
    private String logoType2 = "provincial_logo_1";
    private String logoType3 = "national_logo_2";

    public SignExpresswayDistanceFromLocation5Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_5_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.expressway2 = Expressway.fromName(nbt.getString("expressway2"));
        this.expressway3 = Expressway.fromName(nbt.getString("expressway3"));
        this.expresswayNumber1 = nbt.getString("expresswayNumber1");
        this.expresswayNumber2 = nbt.getString("expresswayNumber2");
        this.expresswayNumber3 = nbt.getString("expresswayNumber3");
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1").replace("provicial", "provincial") : legacyLogoType1();
        this.logoType2 = nbt.contains("logoType2") ? nbt.getString("logoType2").replace("provicial", "provincial") : legacyLogoType2();
        this.logoType3 = nbt.contains("logoType3") ? nbt.getString("logoType3").replace("provicial", "provincial") : legacyLogoType3();
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("length1", this.length1);
        nbt.putString("length2", this.length2);
        nbt.putString("length3", this.length3);
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("expressway2", this.expressway2.getName());
        nbt.putString("expressway3", this.expressway3.getName());
        nbt.putString("expresswayNumber1", this.expresswayNumber1);
        nbt.putString("expresswayNumber2", this.expresswayNumber2);
        nbt.putString("expresswayNumber3", this.expresswayNumber3);
        nbt.putString("logoType1", this.logoType1);
        nbt.putString("logoType2", this.logoType2);
        nbt.putString("logoType3", this.logoType3);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.left("洋后", -14f, 1f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("衢州", -7f, -7f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("福州", -7f, -13f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.right("8", 13.5f, 1f, 0.04f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("112", 13.5f, -7f, 0.04f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("215", 13.5f, -13f, 0.04f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("km", 17.5f, 0.5f, 0.025f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("km", 17.5f, -7.5f, 0.025f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("km", 17.5f, -13.5f, 0.025f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", -7f, 10f, 0.65f, 0f));
        lines.add(SignTextLinesHelper.centeredWithZ("S10", -7f, 9.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo2}.png", 7f, 10f, 0.65f, 0f));
        lines.add(SignTextLinesHelper.centeredWithZ("S52", 7f, 9.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo3}.png", -13f, -9f, 0.65f, 0f));
        lines.add(SignTextLinesHelper.centeredWithZ("G3", -13f, -9.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
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
    public Expressway getExpressway3() { return expressway3; }
    public void setExpressway3(Expressway expressway3) {
        this.expressway3 = expressway3;
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
    public String getExpresswayNumber3() { return expresswayNumber3; }
    public void setExpresswayNumber3(String expresswayNumber3) {
        this.expresswayNumber3 = expresswayNumber3;
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
    public String getLogoType3() { return logoType3; }
    public void setLogoType3(String logoType3) {
        this.logoType3 = logoType3;
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
            case "expresswayNumber3" -> expresswayNumber3;
            case "logo1" -> logoType1;
            case "logo2" -> logoType2;
            case "logo3" -> logoType3;
            case "logo1x", "logo2x", "logo3x" -> "0";
            default -> null;
        };
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
            case "logo3" -> List.of(new FieldOptionGroup("高速Logo", "logoType3", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logoType3),
                    opt("国家高速公路（1位数）", "national_logo_2", logoType3),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logoType3),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logoType3))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logoType1" -> setLogoType1(value);
            case "logoType2" -> setLogoType2(value);
            case "logoType3" -> setLogoType3(value);
        }
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

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
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
