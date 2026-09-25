package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayNamingNumberEntity extends CustomSignBlockEntity {
    private String expresswayNumber = "";
    private String expresswayName = "";

    public SignExpresswayNamingNumberEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_NAMING_NUMBER_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.expresswayNumber = nbt.getString("expresswayNumber");
        this.expresswayName = nbt.getString("expresswayName");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("expresswayNumber", this.expresswayNumber);
        nbt.putString("expresswayName", this.expresswayName);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignExpresswayNamingNumberEntityRenderer 的固定布局生成默认文本行：
     * renderCenteredText(expresswayNumber, 0, 1.5, 0.08)；
     * renderCenteredText(insertSpaceBetweenChars(expresswayName), 0, -6.5, 0.02)，
     * 逐字加空格的变换由 {expresswayNameSpaced} 占位符在渲染时完成。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("G2", 0f, 1.5f, 0.08f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.centered("京 沪 高 速", 0f, -6.5f, 0.02f, 0xFFFFFF, "a"));
        setTextLines(lines);
    }

    public String getExpresswayNumber() { return expresswayNumber; }
    public void setExpresswayNumber(String expresswayNumber) {
        this.expresswayNumber = expresswayNumber;
        markDirtyAndUpdate();
    }
    public String getExpresswayName() { return expresswayName; }
    public void setExpresswayName(String expresswayName) {
        this.expresswayName = expresswayName;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "expresswayNumber" -> expresswayNumber;
            case "expresswayName" -> expresswayName;
            // 对应原 insertSpaceBetweenChars(expresswayName)：逐字之间插入空格
            case "expresswayNameSpaced" -> insertSpaceBetweenChars(expresswayName);
            default -> null;
        };
    }

    // 原 SignExpresswayNamingNumberEntityRenderer 的公共静态方法，迁入实体供占位符解析
    public static String insertSpaceBetweenChars(String str) {
        if (str == null || str.isEmpty() || str.equals(" ")) {
            return str;
        }
        return str.replaceAll(".(?!$)", "$0 ");
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}
