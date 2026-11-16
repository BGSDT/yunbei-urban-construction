package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarning1Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";

    public SignGuideIntersectionAdvanceWarning1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_ENTITY.get(), pos, state);
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
        boolean isWarning2 = isBlockType(com.beigu.yunbeiuc.block.SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2);
        List<TextLineData> lines = new ArrayList<>();
        if (isWarning2) {
            lines.add(SignTextLinesHelper.left("昌平", -10f, 9f, 0.035f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.left("顺义", -3.0f, 1.5f, 0.035f, 0xFFFFFF, "a"));
        } else {
            lines.add(SignTextLinesHelper.left("昌平", -14f, 10f, 0.035f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.left("顺义", -5f, -7f, 0.035f, 0xFFFFFF, "a"));
        }
        setTextLines(lines);
    }

    public String getText1() {
        return text1;
    }
    public String getText2() {
        return text2;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
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
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }
}
