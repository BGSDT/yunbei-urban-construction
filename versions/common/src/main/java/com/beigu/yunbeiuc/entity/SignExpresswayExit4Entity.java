package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.SignBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayExit4Entity extends CustomSignBlockEntity {
    public SignExpresswayExit4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_EXIT_4_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        if (!CustomSignBlockEntity.hasTextLinesData(nbt) && !hasCustomizedTextLines()) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        if (isBlockType(SignBlocks.SIGN_EXPRESSWAY_EXIT_6)) {
            lines.add(SignTextLinesHelper.centered("前进路", 0f, 9.75f, 0.045f, 0x2D9B47, "a"));
            lines.add(SignTextLinesHelper.centered("广州", -8f, -1f, 0.041f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.centered("番禺", 8f, -1f, 0.041f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.right("2", 9f, -12f, 0.041f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.centered("km", 11f, -12f, 0.031f, 0xFFFFFF, "b"));
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_right_side_median_strip.png", -8f, -11f, 0.3592f, 180f));
            lines.add(SignTextLinesHelper.centered("32", -7.5f, 18.75f, 0.03f, 0x2D9B47, "b"));
        } else {
            lines.add(SignTextLinesHelper.centered("前进路", 0f, 9.75f, 0.045f, 0x2D9B47, "a"));
            lines.add(SignTextLinesHelper.centered("广州", -8f, -1f, 0.041f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.centered("番禺", 8f, -1f, 0.041f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.right("2", -7f, -12f, 0.041f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.centered("km", -5f, -12.5f, 0.031f, 0xFFFFFF, "b"));
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_right_side_median_strip.png", 8f, -11f, 0.3592f, 90f));
            lines.add(SignTextLinesHelper.centered("32", 13.5f, 18.75f, 0.03f, 0x2D9B47, "b"));
        }
        setTextLines(lines);
    }
}
