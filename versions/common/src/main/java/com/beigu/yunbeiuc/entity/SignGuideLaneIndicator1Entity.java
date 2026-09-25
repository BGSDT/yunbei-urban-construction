package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideLaneIndicator1Entity extends CustomSignBlockEntity {
    private ArrowDirection direction1 = ArrowDirection.STRAIGHT;
    private ArrowDirection direction2 = ArrowDirection.STRAIGHT;
    private ArrowDirection direction3 = ArrowDirection.STRAIGHT;
    private ArrowDirection direction4 = ArrowDirection.STRAIGHT;

    public SignGuideLaneIndicator1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_LANE_INDICATOR_1_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.direction1 = ArrowDirection.fromName(nbt.getString("direction1"));
        this.direction2 = ArrowDirection.fromName(nbt.getString("direction2"));
        this.direction3 = ArrowDirection.fromName(nbt.getString("direction3"));
        this.direction4 = ArrowDirection.fromName(nbt.getString("direction4"));
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("direction2", this.direction2.getName());
        nbt.putString("direction3", this.direction3.getName());
        nbt.putString("direction4", this.direction4.getName());
        super.saveAdditional(nbt);
    }

    /**
     * 按原 SignGuideLaneIndicator1EntityRenderer 的固定布局生成默认文本行：
     * renderArrow(direction1..4, -17.5/-6/6/17.5, 3, size 0.9) → 4 条 -texture 行，
     * 纹理由 {arrowN} 占位符按 ArrowDirection 枚举选择（映射与原渲染器 switch 一致，
     * 其中 LEFT_TURN_AROUND 对应 straight_left_turn_around 纹理）。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_lane_arrow_{arrow1}.png", -17.5f, 3f, 0.9f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_lane_arrow_{arrow2}.png", -6f, 3f, 0.9f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_lane_arrow_{arrow3}.png", 6f, 3f, 0.9f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_lane_arrow_{arrow4}.png", 17.5f, 3f, 0.9f));
        setTextLines(lines);
    }

    public ArrowDirection getDirection1() { return direction1; }
    public ArrowDirection getDirection2() { return direction2; }
    public ArrowDirection getDirection3() { return direction3; }
    public ArrowDirection getDirection4() { return direction4; }

    public void setDirection1(ArrowDirection direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }
    public void setDirection2(ArrowDirection direction2) {
        this.direction2 = direction2;
        markDirtyAndUpdate();
    }
    public void setDirection3(ArrowDirection direction3) {
        this.direction3 = direction3;
        markDirtyAndUpdate();
    }
    public void setDirection4(ArrowDirection direction4) {
        this.direction4 = direction4;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            // 对应原 renderArrow 的 switch：箭头纹理文件名段（LEFT_TURN_AROUND 用 straight_left_turn_around）
            case "arrow1" -> arrowTexture(direction1);
            case "arrow2" -> arrowTexture(direction2);
            case "arrow3" -> arrowTexture(direction3);
            case "arrow4" -> arrowTexture(direction4);
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "arrow1" -> List.of(new FieldOptionGroup("箭头", "direction1", List.of(
                    opt("左转", "left_turn", direction1.getName()),
                    opt("直行", "straight", direction1.getName()),
                    opt("右转", "right_turn", direction1.getName()),
                    opt("直行和左转", "straight_left_turn", direction1.getName()),
                    opt("直行和右转", "straight_right_turn", direction1.getName()),
                    opt("掉头", "left_turn_around", direction1.getName()))));
            case "arrow2" -> List.of(new FieldOptionGroup("箭头", "direction2", List.of(
                    opt("左转", "left_turn", direction2.getName()),
                    opt("直行", "straight", direction2.getName()),
                    opt("右转", "right_turn", direction2.getName()),
                    opt("直行和左转", "straight_left_turn", direction2.getName()),
                    opt("直行和右转", "straight_right_turn", direction2.getName()),
                    opt("掉头", "left_turn_around", direction2.getName()))));
            case "arrow3" -> List.of(new FieldOptionGroup("箭头", "direction3", List.of(
                    opt("左转", "left_turn", direction3.getName()),
                    opt("直行", "straight", direction3.getName()),
                    opt("右转", "right_turn", direction3.getName()),
                    opt("直行和左转", "straight_left_turn", direction3.getName()),
                    opt("直行和右转", "straight_right_turn", direction3.getName()),
                    opt("掉头", "left_turn_around", direction3.getName()))));
            case "arrow4" -> List.of(new FieldOptionGroup("箭头", "direction4", List.of(
                    opt("左转", "left_turn", direction4.getName()),
                    opt("直行", "straight", direction4.getName()),
                    opt("右转", "right_turn", direction4.getName()),
                    opt("直行和左转", "straight_left_turn", direction4.getName()),
                    opt("直行和右转", "straight_right_turn", direction4.getName()),
                    opt("掉头", "left_turn_around", direction4.getName()))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "direction1" -> setDirection1(ArrowDirection.fromName(value));
            case "direction2" -> setDirection2(ArrowDirection.fromName(value));
            case "direction3" -> setDirection3(ArrowDirection.fromName(value));
            case "direction4" -> setDirection4(ArrowDirection.fromName(value));
        }
    }

    private static String arrowTexture(ArrowDirection direction) {
        return switch (direction) {
            case LEFT_TURN -> "left_turn";
            case STRAIGHT -> "straight";
            case RIGHT_TURN -> "right_turn";
            case STRAIGHT_LEFT_TURN -> "straight_left_turn";
            case STRAIGHT_RIGHT_TURN -> "straight_right_turn";
            case LEFT_TURN_AROUND -> "straight_left_turn_around";
        };
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
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
}
