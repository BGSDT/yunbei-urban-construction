package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDirection1Entity extends CustomSignBlockEntity {
    private String text1 = "";

    public SignExpresswayDirection1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_1_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.text1 = nbt.getString("text1");
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        float x = blockStateForDefaults().getBlock() == com.beigu.yunbeiuc.block.SignBlocks.SIGN_EXPRESSWAY_DIRECTION_2.get() ? -4.5f : 4.5f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("合肥", x, 0f, 0.05f, 0xFFFFFF, "a"));
        setTextLines(lines);
    }

    public String getText1() { return text1; }
    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
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
