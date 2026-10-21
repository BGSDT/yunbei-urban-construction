package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation4;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDistanceFromLocation4Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private RoadType roadType1 = RoadType.ORDINARY_MUNICIPAL;
    private RoadType roadType2 = RoadType.ORDINARY_MUNICIPAL;
    private RoadType roadType3 = RoadType.EXPRESSWAY;
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";
    private String logoType1 = "ordinary_municipal_road_logo_1";
    private String logoType2 = "ordinary_municipal_road_logo_1";
    private String logoType3 = "ordinary_municipal_road_logo_1";

    public SignExpresswayDistanceFromLocation4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_4_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.roadType1 = RoadType.fromName(nbt.getString("roadType1"));
        this.roadType2 = RoadType.fromName(nbt.getString("roadType2"));
        this.roadType3 = RoadType.fromName(nbt.getString("roadType3"));
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1").replace("provicial", "provincial") : legacyLogoType1();
        this.logoType2 = nbt.contains("logoType2") ? nbt.getString("logoType2").replace("provicial", "provincial") : legacyLogoType2();
        this.logoType3 = nbt.contains("logoType3") ? nbt.getString("logoType3").replace("provicial", "provincial") : legacyLogoType3();
        if (!nbt.contains("TextLines")) {
            getTextLines().clear();
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("roadType1", this.roadType1.getName());
        nbt.putString("roadType2", this.roadType2.getName());
        nbt.putString("roadType3", this.roadType3.getName());
        nbt.putString("length1", this.length1);
        nbt.putString("length2", this.length2);
        nbt.putString("length3", this.length3);
        nbt.putString("logoType1", this.logoType1);
        nbt.putString("logoType2", this.logoType2);
        nbt.putString("logoType3", this.logoType3);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.addAll(buildRow(roadType1, "人民路", "logo1", 12f, "a"));
        lines.addAll(buildRow(roadType2, "东环路", "logo2", 0f, "a"));
        lines.addAll(buildRow(roadType3, "苏州新区", "logo3", -12f, "a"));
        lines.add(SignTextLinesHelper.right("7", 13f, 12f, 0.04f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("17", 13f, 0f, 0.04f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("30", 13f, -12f, 0.04f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("km", 17f, 11.5f, 0.025f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("km", 17f, -0.5f, 0.025f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("km", 17f, -12.5f, 0.025f, 0xFFFFFF, "b"));
        setTextLines(lines);
    }

    private List<TextLineData> buildRow(RoadType roadType, String textPlaceholder, String logoKey, float y, String abcFont) {
        List<TextLineData> lines = new ArrayList<>();
        if (roadType == RoadType.ORDINARY_MUNICIPAL) {
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_{" + logoKey + "}.png", -8f, y, 1.55f, 0f));
        }
        TextLineData text = SignTextLinesHelper.left(textPlaceholder, -17f, y, 0.04f, 0xFFFFFF, abcFont);
        text.setZOffset(roadTextZOffset(roadType));
        lines.add(text);
        return lines;
    }

    private float roadTextZOffset(RoadType roadType) {
        float baseZ = switch (blockStateForDefaults().getValue(SignExpresswayDistanceFromLocation4.TYPE)) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
        float absoluteZ = switch (roadType) {
            case EXPRESSWAY -> switch (blockStateForDefaults().getValue(SignExpresswayDistanceFromLocation4.TYPE)) {
                case POLE_L -> -0.75f;
                case POLE_H -> -0.79f;
                case NORMAL -> -0.43f;
            };
            case ORDINARY_MUNICIPAL -> switch (blockStateForDefaults().getValue(SignExpresswayDistanceFromLocation4.TYPE)) {
                case POLE_L -> -0.74f;
                case POLE_H -> -0.78f;
                case NORMAL -> -0.43f;
            };
        };
        return (absoluteZ - baseZ) * 16f;
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
    public RoadType getRoadType1() { return roadType1; }
    public void setRoadType1(RoadType roadType1) {
        this.roadType1 = roadType1;
        markDirtyAndUpdate();
    }
    public RoadType getRoadType2() { return roadType2; }
    public void setRoadType2(RoadType roadType2) {
        this.roadType2 = roadType2;
        markDirtyAndUpdate();
    }
    public RoadType getRoadType3() { return roadType3; }
    public void setRoadType3(RoadType roadType3) {
        this.roadType3 = roadType3;
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
            case "logo1" -> roadType1 == RoadType.ORDINARY_MUNICIPAL ? logoType1 : "";
            case "logo2" -> roadType2 == RoadType.ORDINARY_MUNICIPAL ? logoType2 : "";
            case "logo3" -> roadType3 == RoadType.ORDINARY_MUNICIPAL ? logoType3 : "";
            case "logo1x", "logo2x", "logo3x" -> "0";
            default -> null;
        };
    }

    private String legacyLogoType1() {
        return longRoadName(text1) ? "ordinary_municipal_road_logo_2" : "ordinary_municipal_road_logo_1";
    }

    private String legacyLogoType2() {
        return longRoadName(text2) ? "ordinary_municipal_road_logo_2" : "ordinary_municipal_road_logo_1";
    }

    private String legacyLogoType3() {
        return longRoadName(text3) ? "ordinary_municipal_road_logo_2" : "ordinary_municipal_road_logo_1";
    }

    private boolean longRoadName(String text) {
        return text != null && !text.trim().isEmpty() && text.length() > 3;
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "text1", "length1" -> List.of(roadTypeGroup("roadType1", roadType1));
            case "text2", "length2" -> List.of(roadTypeGroup("roadType2", roadType2));
            case "text3", "length3" -> List.of(roadTypeGroup("roadType3", roadType3));
            case "logo1" -> List.of(roadTypeGroup("roadType1", roadType1), municipalLogoGroup("logoType1", logoType1));
            case "logo2" -> List.of(roadTypeGroup("roadType2", roadType2), municipalLogoGroup("logoType2", logoType2));
            case "logo3" -> List.of(roadTypeGroup("roadType3", roadType3), municipalLogoGroup("logoType3", logoType3));
            default -> null;
        };
    }

    private FieldOptionGroup municipalLogoGroup(String field, String current) {
        return new FieldOptionGroup("普通公路Logo", field, List.of(
                opt("普通公路（3字）", "ordinary_municipal_road_logo_1", current),
                opt("普通公路（4字）", "ordinary_municipal_road_logo_2", current)));
    }

    private FieldOptionGroup roadTypeGroup(String field, RoadType current) {
        return new FieldOptionGroup("道路类型", field, List.of(
                opt("高速公路", "expressway", current.getName()),
                opt("普通公路", "ordinary_municipal", current.getName())));
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "roadType1" -> { setRoadType1(RoadType.fromName(value)); rebuildTextLines(); }
            case "roadType2" -> { setRoadType2(RoadType.fromName(value)); rebuildTextLines(); }
            case "roadType3" -> { setRoadType3(RoadType.fromName(value)); rebuildTextLines(); }
            case "logoType1" -> setLogoType1(value);
            case "logoType2" -> setLogoType2(value);
            case "logoType3" -> setLogoType3(value);
        }
    }

    private void rebuildTextLines() {
        getTextLines().clear();
        ensureDefaultTextLines();
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }

    public enum RoadType {
        EXPRESSWAY("expressway"),
        ORDINARY_MUNICIPAL("ordinary_municipal");

        private final String name;
        RoadType(String name) { this.name = name; }
        public String getName() { return name; }
        public static RoadType fromName(String name) {
            for (RoadType dir : values()) {
                if (dir.name.equals(name)) return dir;
            }
            return EXPRESSWAY;
        }
    }}
