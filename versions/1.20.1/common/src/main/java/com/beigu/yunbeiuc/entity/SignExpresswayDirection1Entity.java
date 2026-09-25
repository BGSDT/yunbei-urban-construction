package com.beigu.yunbeiuc.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDirection1Entity extends CustomSignBlockEntity {
    private String text1 = "";

    public SignExpresswayDirection1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_1_ENTITY.get(), pos, state);
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
     * 按原 SignExpresswayDirection1EntityRenderer 的固定布局生成默认文本行：
     * renderCenteredText(text1, ±4.5, 0, 0.05, 0xFFFFFF)，x 按方块（DIRECTION_1→4.5 / DIRECTION_2→-4.5）
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        float x = getCachedState().getBlock() == com.beigu.yunbeiuc.block.SignBlocks.SIGN_EXPRESSWAY_DIRECTION_2.get() ? -4.5f : 4.5f;
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
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
}
