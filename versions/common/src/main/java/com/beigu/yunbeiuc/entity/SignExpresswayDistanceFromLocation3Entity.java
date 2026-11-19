package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.nbt.NbtCompat;

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
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.text1 = NbtCompat.getString(nbt, "text1");
        this.text2 = NbtCompat.getString(nbt, "text2");
        if (!CustomSignBlockEntity.hasTextLinesData(nbt) && !hasCustomizedTextLines()) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("苏州城区", 0f, 5.5f, 0.045f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("个出口", -6.5f, -5.5f, 0.045f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.right("5", -6f, -6f, 0.045f, 0xFFFFFF, "a"));
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
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), 3);
        }
    }
}
