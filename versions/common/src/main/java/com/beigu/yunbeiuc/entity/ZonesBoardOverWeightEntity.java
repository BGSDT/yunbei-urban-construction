package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ZonesBoardOverWeightEntity extends CustomSignBlockEntity {
    private String text1 = "";

    public ZonesBoardOverWeightEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONES_BOARD_OVER_WEIGHT_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.text1 = NbtCompat.getString(nbt, "text1");
        if (!CustomSignBlockEntity.hasTextLinesData(nbt) && !hasCustomizedTextLines()) {
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
        List<TextLineData> lines = new ArrayList<>();
        if (isBlockType(SignBlocks.ZONES_BOARD_OVER_WEIGHT) || isBlockType(SignBlocks.ZONES_BOARD_TIME_LIMIT)) {
            lines.add(SignTextLinesHelper.left("{text1}", -5f, 5.5f, 0.03f, 0x000000, "b"));
        } else if (isBlockType(SignBlocks.ZONES_BOARD_SUGGESTED_SPEED)) {
            lines.add(SignTextLinesHelper.left("{text1}", -6f, 0f, 0.04f, 0x000000, "b"));
        } else if (isBlockType(SignBlocks.ZONES_BOARD_LENGTH)) {
            lines.add(SignTextLinesHelper.centered("{text1}", 1f, 0f, 0.04f, 0x000000, "b"));
        } else if (isBlockType(SignBlocks.ZONES_BOARD_DISTANCE_LENGTH)) {
            lines.add(SignTextLinesHelper.centered("{text1}", 0f, 0f, 0.04f, 0x000000, "b"));
        } else if (isBlockType(SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_LEFT)) {
            lines.add(SignTextLinesHelper.centered("{text1}", 0f, 1f, 0.03f, 0x000000, "b"));
        } else if (isBlockType(SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_RIGHT)) {
            lines.add(SignTextLinesHelper.centered("{text1}", -2f, 1f, 0.03f, 0x000000, "b"));
        }
        setTextLines(lines);
    }

    public String getText1() {
        return text1;
    }

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
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }
}
