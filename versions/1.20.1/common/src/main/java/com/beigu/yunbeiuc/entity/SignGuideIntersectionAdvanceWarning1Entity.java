package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarning1Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";

    public SignGuideIntersectionAdvanceWarning1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignGuideIntersectionAdvanceWarning1EntityRenderer 的固定布局生成默认文本行：
     * 原渲染器按方块身份区分（同一方块类注册了 WARNING_1 / WARNING_2 两个方块）：
     * WARNING_2 → renderLeftAlignedText(text1, -17, 10, 0.035) / (text2, 3, -1, 0.035)；
     * WARNING_1 → renderLeftAlignedText(text1, -12, 12, 0.035) / (text2, -5, -7, 0.035)。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean isWarning2 = getCachedState().getBlock() == com.beigu.yunbeiuc.block.SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2.get();
        List<TextLineData> lines = new ArrayList<>();
        if (isWarning2) {
            lines.add(SignTextLinesHelper.left("{text1}", -17f, 10f, 0.035f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.left("{text2}", 3f, -1f, 0.035f, 0xFFFFFF));
        } else {
            lines.add(SignTextLinesHelper.left("{text1}", -12f, 12f, 0.035f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.left("{text2}", -5f, -7f, 0.035f, 0xFFFFFF));
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
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}
