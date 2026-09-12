package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDistanceFromLocation1Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";

    public SignExpresswayDistanceFromLocation1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_1_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("length1", this.length1);
        nbt.putString("length2", this.length2);
        nbt.putString("length3", this.length3);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignExpresswayDistanceFromLocation1EntityRenderer 的固定布局生成默认文本行：
     * renderLeftAlignedText(textN, -15, 9/0/-9, 0.04)；
     * renderRightAlignedText(lengthN, 11, 9/0/-9, 0.04)；
     * renderRightAlignedText("km", 15, 8.5/-0.5/-9.5, 0.025)。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        // 左侧文本
        lines.add(SignTextLinesHelper.left("{text1}", -15f, 9f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.left("{text2}", -15f, 0f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.left("{text3}", -15f, -9f, 0.04f, 0xFFFFFF));
        // 右侧数字
        lines.add(SignTextLinesHelper.right("{length1}", 11f, 9f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length2}", 11f, 0f, 0.04f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("{length3}", 11f, -9f, 0.04f, 0xFFFFFF));
        // km 单位
        lines.add(SignTextLinesHelper.right("km", 15f, 8.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 15f, -0.5f, 0.025f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.right("km", 15f, -9.5f, 0.025f, 0xFFFFFF));
        setTextLines(lines);
    }

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
    public String getText3() { return text3; }
    public void setText3(String text3) {
        this.text3 = text3;
        markDirtyAndUpdate();
    }
    public String getLength1() { return length1; }
    public void setLength1(String length1) {
        this.length1 = length1;
        markDirtyAndUpdate();
    }
    public String getLength2() { return length2; }
    public void setLength2(String length2) {
        this.length2 = length2;
        markDirtyAndUpdate();
    }
    public String getLength3() { return length3; }
    public void setLength3(String length3) {
        this.length3 = length3;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "text2" -> text2;
            case "text3" -> text3;
            case "length1" -> length1;
            case "length2" -> length2;
            case "length3" -> length3;
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
