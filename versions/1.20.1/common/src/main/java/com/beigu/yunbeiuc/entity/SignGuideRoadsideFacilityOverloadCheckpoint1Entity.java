package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideRoadsideFacilityOverloadCheckpoint1Entity extends CustomSignBlockEntity {
    private Unit unit1 = Unit.KILOMETRE;
    private String length1 = "";

    public SignGuideRoadsideFacilityOverloadCheckpoint1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_ROADSIDE_FACILITY_OVERLOAD_CHECKPOINT_1_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.unit1 = Unit.fromName(nbt.getString("unit1"));
        this.length1 = nbt.getString("length1");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("unit1", this.unit1.getName());
        nbt.putString("length1", this.length1);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignGuideRoadsideFacilityOverloadCheckpoint1EntityRenderer 的固定布局生成默认文本行：
     * renderRightAlignedText(length1, -1.5, -13, 0.045)、
     * renderRightAlignedText({unit1}, 2.5, -13.5, 0.03)。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.right("{length1}", -1.5f, -13f, 0.045f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{unit1}", 2.5f, -13.5f, 0.03f, 0xFFFFFF));
        setTextLines(lines);
    }

    public Unit getUnit1() {
        return unit1;
    }
    public String getLength1() {
        return length1;
    }

    public void setUnit1(Unit unit1) {
        this.unit1 = unit1;
        markDirtyAndUpdate();
    }
    public void setLength1(String length1) {
        this.length1 = length1;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "length1" -> length1;
            // 对应原渲染器：KILOMETRE 显示 "km"，METRE 显示 "m"
            case "unit1" -> unit1 == Unit.KILOMETRE ? "km" : "m";
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "unit1" -> List.of(new FieldOptionGroup("单位", "unit1", List.of(
                    opt("公里（km）", "kilometre", unit1.getName()),
                    opt("米（m）", "metre", unit1.getName()))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "unit1" -> setUnit1(Unit.fromName(value));
        }
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public enum Unit {
        KILOMETRE("kilometre"),
        METRE("metre");

        private final String name;

        Unit(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Unit fromName(String name) {
            for (Unit value : values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return KILOMETRE;
        }
    }
}
