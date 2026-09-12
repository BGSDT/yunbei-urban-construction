package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ZonesBoardOverWeightEntity extends CustomSignBlockEntity {
    private String text1 = "";

    public ZonesBoardOverWeightEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONES_BOARD_OVER_WEIGHT_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        super.writeNbt(nbt);
    }

    /**
     * 按原 ZonesBoardOverWeightEntityRenderer 的固定布局生成默认文本行，
     * 原渲染器按方块身份分支（同一方块类注册了 OVER_WEIGHT/TIME_LIMIT/SUGGESTED_SPEED/LENGTH/
     * DISTANCE_LENGTH/DISTANCE_LENGTH_LEFT/DISTANCE_LENGTH_RIGHT 共 7 个方块）：
     * OVER_WEIGHT/TIME_LIMIT → renderLeftAlignedText(text1, -5, 5.5, 0.03, 0x000000)；
     * SUGGESTED_SPEED → renderLeftAlignedText(text1, -6, 0, 0.04, 0x000000)；
     * LENGTH → renderCenteredText(text1, 1, 0, 0.04, 0x000000)；
     * DISTANCE_LENGTH → renderCenteredText(text1, 0, 0, 0.04, 0x000000)；
     * DISTANCE_LENGTH_LEFT → renderCenteredText(text1, 0, 1, 0.03, 0x000000)；
     * DISTANCE_LENGTH_RIGHT → renderCenteredText(text1, -2, 1, 0.03, 0x000000)。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        Block block = getCachedState().getBlock();
        List<TextLineData> lines = new ArrayList<>();
        if (block == SignBlocks.ZONES_BOARD_OVER_WEIGHT.get() || block == SignBlocks.ZONES_BOARD_TIME_LIMIT.get()) {
            lines.add(SignTextLinesHelper.left("{text1}", -5f, 5.5f, 0.03f, 0x000000));
        } else if (block == SignBlocks.ZONES_BOARD_SUGGESTED_SPEED.get()) {
            lines.add(SignTextLinesHelper.left("{text1}", -6f, 0f, 0.04f, 0x000000));
        } else if (block == SignBlocks.ZONES_BOARD_LENGTH.get()) {
            lines.add(SignTextLinesHelper.centered("{text1}", 1f, 0f, 0.04f, 0x000000));
        } else if (block == SignBlocks.ZONES_BOARD_DISTANCE_LENGTH.get()) {
            lines.add(SignTextLinesHelper.centered("{text1}", 0f, 0f, 0.04f, 0x000000));
        } else if (block == SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_LEFT.get()) {
            lines.add(SignTextLinesHelper.centered("{text1}", 0f, 1f, 0.03f, 0x000000));
        } else if (block == SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_RIGHT.get()) {
            lines.add(SignTextLinesHelper.centered("{text1}", -2f, 1f, 0.03f, 0x000000));
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
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}
