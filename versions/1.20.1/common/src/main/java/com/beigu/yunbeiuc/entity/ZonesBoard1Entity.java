package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ZonesBoard1Entity extends CustomSignBlockEntity {
    private String text1 = "";

    public ZonesBoard1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONES_BOARD_1_ENTITY.get(), pos, state);
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
     * 按原 ZonesBoard1EntityRenderer 的固定布局生成默认文本行：
     * renderCenteredText(text1, 0, 0, 0.04, color)，颜色按方块（红牌白字，其余黑字）
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        int color = getCachedState().getBlock() == SignBlocks.ZONES_BOARD_RED.get() ? 0xFFFFFF : 0x000000;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("{text1}", 0f, 0f, 0.04f, color));
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
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
}
