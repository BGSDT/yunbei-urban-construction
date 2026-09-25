package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarning5Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String text4 = "";
    private float text1AndY;
    private float text2AndY;
    private float text3AndY;
    private float text4AndY;

    public SignGuideIntersectionAdvanceWarning5Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.text4 = nbt.getString("text4");
        this.text1AndY = nbt.getFloat("text1AndY");
        this.text2AndY = nbt.getFloat("text2AndY");
        this.text3AndY = nbt.getFloat("text3AndY");
        this.text4AndY = nbt.getFloat("text4AndY");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("text4", this.text4);
        nbt.putFloat("text1AndY", this.text1AndY);
        nbt.putFloat("text2AndY", this.text2AndY);
        nbt.putFloat("text3AndY", this.text3AndY);
        nbt.putFloat("text4AndY", this.text4AndY);
        super.saveAdditional(nbt);
    }

    /**
     * 按原 SignGuideIntersectionAdvanceWarning5EntityRenderer 的固定布局生成默认文本行：
     * renderCenteredText(text1, -10, 10 + text1AndY, 0.03) / (text2, 10, 10 + text2AndY, 0.03)
     * / (text3, -10, -10 + text3AndY, 0.03) / (text4, 10, -10 + text4AndY, 0.03)。
     * 原渲染器的 textNAndY 为字段驱动的动态 Y 微调；默认行生成时把该字段当前值并入行 yOffset
     * （新放置时字段为 0，旧存档迁移时保留原 Y 微调），迁移后直接在 TextDisplayScreen 中调整行位置。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("白涧", -10f, 10f + text1AndY, 0.03f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("三和", 10f, 10f + text2AndY, 0.03f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("唐山", -10f, -10f + text3AndY, 0.03f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("双塔", 10f, -10f + text4AndY, 0.03f, 0xFFFFFF));
        setTextLines(lines);
    }

    public String getText1() {
        return text1;
    }
    public String getText2() {
        return text2;
    }
    public String getText3() {
        return text3;
    }
    public String getText4() {
        return text4;
    }
    public float getText1AndY() {
        return text1AndY;
    }
    public float getText2AndY() {
        return text2AndY;
    }
    public float getText3AndY() {
        return text3AndY;
    }
    public float getText4AndY() {
        return text4AndY;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public void setText2(String text2) {
        this.text2 = text2;
        markDirtyAndUpdate();
    }
    public void setText3(String text3) {
        this.text3 = text3;
        markDirtyAndUpdate();
    }
    public void setText4(String text4) {
        this.text4 = text4;
        markDirtyAndUpdate();
    }
    public void setText1AndY(float text1AndY) {
        this.text1AndY = text1AndY;
        markDirtyAndUpdate();
    }
    public void setText2AndY(float text2AndY) {
        this.text2AndY = text2AndY;
        markDirtyAndUpdate();
    }
    public void setText3AndY(float text3AndY) {
        this.text3AndY = text3AndY;
        markDirtyAndUpdate();
    }
    public void setText4AndY(float text4AndY) {
        this.text4AndY = text4AndY;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "text2" -> text2;
            case "text3" -> text3;
            case "text4" -> text4;
            default -> null;
        };
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), VersionServices.blocks().updateAll());
        }
    }
}
