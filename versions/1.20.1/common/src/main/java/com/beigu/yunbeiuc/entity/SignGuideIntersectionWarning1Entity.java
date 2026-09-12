package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionWarning1Entity extends CustomSignBlockEntity {
    private String text1 = "";

    public SignGuideIntersectionWarning1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_WARNING_1_ENTITY.get(), pos, state);
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
     * 按原 SignGuideIntersectionWarning1EntityRenderer 的固定布局生成默认文本行，
     * 原渲染器按方块身份分支（同一方块类注册了 WARNING_1/2/3/6、DISTANCE_TO_TUNNEL_EXIT_1~6、ODOMETER 共 12 个方块）：
     * WARNING_1/WARNING_6 → renderCenteredText(text1, 0, 0, 0.035, 0xFFFFFF)（WARNING_3 相同）；
     * WARNING_2 → renderCenteredText(text1, -2.5, 0, 0.035, 0xFFFFFF)；
     * DISTANCE_TO_TUNNEL_EXIT_1/2/3 → renderRightAlignedText(text1, 5, 0, 0.045, 0x275aa8)
     *                                  + renderRightAlignedText("km", 9, -0.5, 0.03, 0x275aa8)；
     * DISTANCE_TO_TUNNEL_EXIT_4/5/6 → renderRightAlignedText(text1, -0.5, -4, 0.045, 0x275aa8)
     *                                  + renderRightAlignedText("km", 3.5, -4.5, 0.03, 0x275aa8)；
     * ODOMETER → renderCenteredText(text1, 0, 3, 0.055, 0xFFFFFF)。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        Block block = getCachedState().getBlock();
        List<TextLineData> lines = new ArrayList<>();
        if (block == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_1.get() || block == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_6.get()) {
            lines.add(SignTextLinesHelper.centered("{text1}", 0f, 0f, 0.035f, 0xFFFFFF));
        } else if (block == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_2.get()) {
            lines.add(SignTextLinesHelper.centered("{text1}", -2.5f, 0f, 0.035f, 0xFFFFFF));
        } else if (block == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_3.get()) {
            lines.add(SignTextLinesHelper.centered("{text1}", 0f, 0f, 0.035f, 0xFFFFFF));
        } else if (block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_1.get() || block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_2.get() || block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_3.get()) {
            lines.add(SignTextLinesHelper.right("{text1}", 5f, 0f, 0.045f, 0x275aa8));
            lines.add(SignTextLinesHelper.right("km", 9f, -0.5f, 0.03f, 0x275aa8));
        } else if (block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_4.get() || block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_5.get() || block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_6.get()) {
            lines.add(SignTextLinesHelper.right("{text1}", -0.5f, -4f, 0.045f, 0x275aa8));
            lines.add(SignTextLinesHelper.right("km", 3.5f, -4.5f, 0.03f, 0x275aa8));
        } else if (block == SignBlocks.SIGN_GUIDE_ODOMETER.get()) {
            lines.add(SignTextLinesHelper.centered("{text1}", 0f, 3f, 0.055f, 0xFFFFFF));
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
