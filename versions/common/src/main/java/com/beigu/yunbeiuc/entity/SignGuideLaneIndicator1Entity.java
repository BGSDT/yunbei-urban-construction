package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SignGuideLaneIndicator1Entity extends CustomSignBlockEntity {
    private String arrow1 = "left_turn";
    private String arrow2 = "straight";
    private String arrow3 = "straight";
    private String arrow4 = "right_turn";
    private ArrowDirection direction1 = ArrowDirection.STRAIGHT;
    private ArrowDirection direction2 = ArrowDirection.STRAIGHT;
    private ArrowDirection direction3 = ArrowDirection.STRAIGHT;
    private ArrowDirection direction4 = ArrowDirection.STRAIGHT;

    public SignGuideLaneIndicator1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_LANE_INDICATOR_1_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.arrow1 = NbtCompat.getString(nbt, "direction1");
        this.arrow2 = NbtCompat.getString(nbt, "direction2");
        this.arrow3 = NbtCompat.getString(nbt, "direction3");
        this.arrow4 = NbtCompat.getString(nbt, "direction4");
        this.direction1 = ArrowDirection.fromName(NbtCompat.getString(nbt, "direction1"));
        this.direction2 = ArrowDirection.fromName(NbtCompat.getString(nbt, "direction2"));
        this.direction3 = ArrowDirection.fromName(NbtCompat.getString(nbt, "direction3"));
        this.direction4 = ArrowDirection.fromName(NbtCompat.getString(nbt, "direction4"));
        if (!CustomSignBlockEntity.hasTextLinesData(nbt) && !hasCustomizedTextLines()) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("direction2", this.direction2.getName());
        nbt.putString("direction3", this.direction3.getName());
        nbt.putString("direction4", this.direction4.getName());
        nbt.putString("direction1", this.arrow1);
        nbt.putString("direction2", this.arrow2);
        nbt.putString("direction3", this.arrow3);
        nbt.putString("direction4", this.arrow4);
        super.saveAdditionalCompat(nbt);
    }

    public enum ArrowDirection {
        LEFT_TURN("left_turn"),
        STRAIGHT("straight"),
        RIGHT_TURN("right_turn"),
        STRAIGHT_LEFT_TURN("straight_left_turn"),
        STRAIGHT_RIGHT_TURN("straight_right_turn"),
        LEFT_TURN_AROUND("left_turn_around");

        private final String name;

        ArrowDirection(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static ArrowDirection fromName(String name) {
            for (ArrowDirection dir : values()) {
                if (dir.name.equals(name)) {
                    return dir;
                }
            }
            return STRAIGHT;
        }
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_lane_arrow_{arrow1}.png", -17.5f, 3f, 0.9f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_lane_arrow_{arrow2}.png", -6f, 3f, 0.9f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_lane_arrow_{arrow3}.png", 6f, 3f, 0.9f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_lane_arrow_{arrow4}.png", 17.5f, 3f, 0.9f));
        setTextLines(lines);
    }

    public String getArrow1() {
        return arrow1;
    }

    public void setArrow1(String arrow1) {
        this.arrow1 = arrow1;
        markDirtyAndUpdate();
    }

    public String getArrow2() {
        return arrow2;
    }

    public void setArrow2(String arrow2) {
        this.arrow2 = arrow2;
        markDirtyAndUpdate();
    }

    public String getArrow3() {
        return arrow3;
    }

    public void setArrow3(String arrow3) {
        this.arrow3 = arrow3;
        markDirtyAndUpdate();
    }

    public String getArrow4() {
        return arrow4;
    }

    public void setArrow4(String arrow4) {
        this.arrow4 = arrow4;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "arrow1" -> arrow1;
            case "arrow2" -> arrow2;
            case "arrow3" -> arrow3;
            case "arrow4" -> arrow4;
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "arrow1" -> List.of(new FieldOptionGroup("箭头", "arrow1", List.of(
                    opt("左转", "left_turn", arrow1),
                    opt("直行", "straight", arrow1),
                    opt("右转", "right_turn", arrow1),
                    opt("左转和右转", "left_right_turn", arrow1),
                    opt("左转和掉头", "left_turn_around", arrow1),
                    opt("直行和左转", "straight_left_turn", arrow1),
                    opt("直行和右转", "straight_right_turn", arrow1),
                    opt("掉头", "single_left_turn_around", arrow1))));
            case "arrow2" -> List.of(new FieldOptionGroup("箭头", "arrow2", List.of(
                    opt("左转", "left_turn", arrow2),
                    opt("直行", "straight", arrow2),
                    opt("右转", "right_turn", arrow2),
                    opt("左转和右转", "left_right_turn", arrow2),
                    opt("左转和掉头", "left_turn_around", arrow2),
                    opt("直行和左转", "straight_left_turn", arrow2),
                    opt("直行和右转", "straight_right_turn", arrow2),
                    opt("掉头", "single_left_turn_around", arrow2))));
            case "arrow3" -> List.of(new FieldOptionGroup("箭头", "arrow3", List.of(
                    opt("左转", "left_turn", arrow3),
                    opt("直行", "straight", arrow3),
                    opt("右转", "right_turn", arrow3),
                    opt("左转和右转", "left_right_turn", arrow3),
                    opt("左转和掉头", "left_turn_around", arrow3),
                    opt("直行和左转", "straight_left_turn", arrow3),
                    opt("直行和右转", "straight_right_turn", arrow3),
                    opt("掉头", "single_left_turn_around", arrow3))));
            case "arrow4" -> List.of(new FieldOptionGroup("箭头", "arrow4", List.of(
                    opt("左转", "left_turn", arrow4),
                    opt("直行", "straight", arrow4),
                    opt("右转", "right_turn", arrow4),
                    opt("左转和右转", "left_right_turn", arrow4),
                    opt("左转和掉头", "left_turn_around", arrow4),
                    opt("直行和左转", "straight_left_turn", arrow4),
                    opt("直行和右转", "straight_right_turn", arrow4),
                    opt("掉头", "single_left_turn_around", arrow4))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "arrow1" -> setArrow1(value);
            case "arrow2" -> setArrow2(value);
            case "arrow3" -> setArrow3(value);
            case "arrow4" -> setArrow4(value);
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
