package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation4;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDistanceFromLocation4Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private RoadType roadType1 = RoadType.EXPRESSWAY;
    private RoadType roadType2 = RoadType.EXPRESSWAY;
    private RoadType roadType3 = RoadType.EXPRESSWAY;
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";
    // 普通公路盾牌版本（独立枚举：3字/4字），不再按路名长度自动联动
    private String logoType1 = "ordinary_municipal_road_logo_1";
    private String logoType2 = "ordinary_municipal_road_logo_1";
    private String logoType3 = "ordinary_municipal_road_logo_1";

    public SignExpresswayDistanceFromLocation4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_4_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.roadType1 = RoadType.fromName(nbt.getString("roadType1"));
        this.roadType2 = RoadType.fromName(nbt.getString("roadType2"));
        this.roadType3 = RoadType.fromName(nbt.getString("roadType3"));
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
        // 旧存档兼容：无 logoTypeN 键时按路名长度推导盾牌版本（3字/4字）
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1") : legacyLogoType1();
        this.logoType2 = nbt.contains("logoType2") ? nbt.getString("logoType2") : legacyLogoType2();
        this.logoType3 = nbt.contains("logoType3") ? nbt.getString("logoType3") : legacyLogoType3();
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行。
        // logo 行的存在性与文本 z 偏移都依赖 roadType（构造时字段还是默认值 EXPRESSWAY），
        // 故旧存档须先清空构造时生成的行，再按存档字段重建
        if (!nbt.contains("TextLines")) {
            getTextLines().clear();
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
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
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignExpresswayDistanceFromLocation4EntityRenderer 的固定布局生成默认文本行（三行结构相同，y = 12/0/-12）：
     * renderLogo(roadTypeN, -8, y, size 1.55) → 仅 roadType 为 ORDINARY_MUNICIPAL 时有 -texture 行，
     *   纹理由 {logoN} 占位符按路名长度选择（留空或 ≤3 字用 logo_1，否则宽版 logo_2），
     *   宽版 logo_2 时原 adjustedX = andX + 3f 不再自动附加，X 偏移由玩家在编辑界面调整；
     * renderLeftTextWithRoadType(textN, -17, y, 0.04) → 左对齐文本，z 按 roadTypeN 与方块 TYPE 取原绝对 z 再换算；
     * renderRightAlignedText(lengthN, 13, y, 0.04) 与 km 单位 (17, y∓0.5, 0.025)。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.addAll(buildRow(roadType1, "{text1}", "logo1", 12f));
        lines.addAll(buildRow(roadType2, "{text2}", "logo2", 0f));
        lines.addAll(buildRow(roadType3, "{text3}", "logo3", -12f));
        lines.add(SignTextLinesHelper.right("{length1}", 13f, 12f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length2}", 13f, 0f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length3}", 13f, -12f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 17f, 11.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 17f, -0.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 17f, -12.5f, 0.025f, 0xFFFFFF));
        setTextLines(lines);
    }

    private List<TextLineData> buildRow(RoadType roadType, String textPlaceholder, String logoKey, float y) {
        List<TextLineData> lines = new ArrayList<>();
        // 原 renderLogo：仅 ORDINARY_MUNICIPAL（普通公路/省道盾牌）渲染 logo
        if (roadType == RoadType.ORDINARY_MUNICIPAL) {
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_{" + logoKey + "}.png", -8f, y, 1.55f));
        }
        TextLineData text = SignTextLinesHelper.left(textPlaceholder, -17f, y, 0.04f, 0xFFFFFF);
        text.setZOffset(roadTextZOffset(roadType));
        lines.add(text);
        return lines;
    }

    /**
     * 原 renderLeftTextWithRoadType 的绝对 z（EXPRESSWAY -0.75/-0.79/-0.43，ORDINARY_MUNICIPAL -0.74/-0.78/-0.42，
     * 按 POLE_L/POLE_H/NORMAL）换算为相对渲染器 getZOffset（POLE_L -0.74 / POLE_H -0.81 / NORMAL -0.45）的 1/16 单位偏移
     */
    private float roadTextZOffset(RoadType roadType) {
        float baseZ = switch (getCachedState().get(SignExpresswayDistanceFromLocation4.TYPE)) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
        float absoluteZ = switch (roadType) {
            case EXPRESSWAY -> switch (getCachedState().get(SignExpresswayDistanceFromLocation4.TYPE)) {
                case POLE_L -> -0.75f;
                case POLE_H -> -0.79f;
                case NORMAL -> -0.43f;
            };
            case ORDINARY_MUNICIPAL -> switch (getCachedState().get(SignExpresswayDistanceFromLocation4.TYPE)) {
                case POLE_L -> -0.74f;
                case POLE_H -> -0.78f;
                case NORMAL -> -0.42f;
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
            // 对应原 renderLogo：仅普通公路路型渲染 logo（高速公路返回空段使 -texture 行静默跳过）；
            // 盾牌版本为独立枚举字段（3字/4字），旧存档按路名长度推导
            case "logo1" -> roadType1 == RoadType.ORDINARY_MUNICIPAL ? logoType1 : "";
            case "logo2" -> roadType2 == RoadType.ORDINARY_MUNICIPAL ? logoType2 : "";
            case "logo3" -> roadType3 == RoadType.ORDINARY_MUNICIPAL ? logoType3 : "";
            // 对应原 adjustedX = andX + 3f：仅宽版 logo_2 附加 +3px X 偏移
            case "logo1x", "logo2x", "logo3x" -> "0"; // 兼容旧存档已保存的行；默认行不再附加偏移，X 由玩家在编辑界面调整
            default -> null;
        };
    }

    // 旧存档兼容推导（原 renderLogo 逻辑）：路名留空或 ≤3 字用 logo_1（3字），否则 logo_2（4字）
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
            // 同一行组的文本/长度行提供道路类型切换
            case "text1", "length1" -> List.of(roadTypeGroup("roadType1", roadType1));
            case "text2", "length2" -> List.of(roadTypeGroup("roadType2", roadType2));
            case "text3", "length3" -> List.of(roadTypeGroup("roadType3", roadType3));
            // logo 行额外提供普通公路盾牌版本（3字/4字）
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
            // 路型决定 logo 行的存在性与文本行 z，切换后按当前字段重建默认行集
            //（该牌的自定义行布局会重置，固定字段值保留、占位符仍解析出原内容）
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
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
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
