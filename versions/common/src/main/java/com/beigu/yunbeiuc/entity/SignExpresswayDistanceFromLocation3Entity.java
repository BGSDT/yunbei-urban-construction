package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDistanceFromLocation3Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";

    public SignExpresswayDistanceFromLocation3Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_3_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        super.saveAdditional(nbt);
    }

    /**
     * 按原 SignExpresswayDistanceFromLocation3EntityRenderer 的固定布局生成默认文本行：
     * 第二行为 text2 + "个出口"，保持默认显示一致
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("苏州城区", 0f, 5.5f, 0.045f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("5个出口", 0f, -5.5f, 0.045f, 0xFFFFFF));
        setTextLines(lines);
    }

    private static String t(String s) { return s == null ? "" : s; }

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

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "text2" -> text2;
            default -> null;
        };
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
