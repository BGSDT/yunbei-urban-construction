package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning4;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionWarning4Entity extends CustomSignBlockEntity {
    private SignTurnDirection direction1 = SignTurnDirection.STRAIGHT;
    private String text1 = "";

    public SignGuideIntersectionWarning4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_WARNING_4_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.direction1 = SignTurnDirection.fromName(nbt.getString("direction1"), SignTurnDirection.STRAIGHT);
        this.text1 = nbt.getString("text1");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("text1", this.text1);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignGuideIntersectionWarning4EntityRenderer 的固定布局生成默认文本行，
     * 原渲染器按方块身份分支（同一方块类注册了 WARNING_4 / WARNING_5 两个方块）：
     * WARNING_4 → renderDirectionLogo(direction1, andX -13, 0, size 0.4)——adjustedX = (direction == RIGHT) ? 13 : -13
     *             （右转箭头整行 X 镜像）不再自动附加，默认按非镜像方向（左转/直行）生成静态 X，右转时由玩家调整；
     *             renderText1(text1, andX 4, 0, 0.04)——x = (direction == RIGHT) ? -4 : 4，文本行无 xShift 机制，
     *             默认行按生成时的 direction1 取静态位置，之后改动 direction1 需手动调整该行 xOffset；
     * WARNING_5 → renderCenteredText(text1, 0, 0, 0.035)（先绘制，位于背景 logo 之下）→
     *             renderBackgroundLogo(direction1, 0, 0, size 1.75)（warning_5 系列纹理）→
     *             renderDirectionText 四条方位文本（按方块朝向取中/英文，cn 在 y=4、en 在 y=-4，
     *             左侧 x=16、右侧 x=-16，迁移为 {cnLeft}/{cnRight}/{enLeft}/{enRight} 占位符，渲染时按当前朝向解析）。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean isWarning4 = getCachedState().getBlock() == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_4.get();
        List<TextLineData> lines = new ArrayList<>();
        if (isWarning4) {
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo1}.png", -13f, 0f, 0.4f));
            float textX = direction1 == SignTurnDirection.RIGHT ? -4f : 4f;
            lines.add(SignTextLinesHelper.centered("{text1}", textX, 0f, 0.04f, 0xFFFFFF));
        } else {
            lines.add(SignTextLinesHelper.centered("{text1}", 0f, 0f, 0.035f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_intersection_warning_5_{logo2}.png", 0f, 0f, 1.75f));
            lines.add(SignTextLinesHelper.centered("{cnLeft}", 16f, 4f, 0.02f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("{cnRight}", -16f, 4f, 0.02f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("{enLeft}", 16f, -4f, 0.02f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("{enRight}", -16f, -4f, 0.02f, 0xFFFFFF));
        }
        setTextLines(lines);
    }

    public SignTurnDirection getDirection1() { return direction1; }
    public String getText1() {
        return text1;
    }

    public void setDirection1(SignTurnDirection direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }
    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            // 对应原 renderDirectionLogo：左转/直行/右转箭头纹理文件名段
            case "logo1" -> direction1.getName();
            // 对应原 renderBackgroundLogo：warning_5 系列背景纹理文件名段
            case "logo2" -> direction1.getName();
            // 对应原 renderDirectionLogo 的 X 镜像：x = (direction == RIGHT) ? -andX : andX（andX = -13）
            case "logo1x" -> "0"; // 兼容旧存档已保存的行；默认行不再附加偏移，X 由玩家在编辑界面调整
            // 对应原 renderDirectionText：按方块朝向取左右方位中/英文
            case "cnLeft" -> directionText("cnLeft");
            case "cnRight" -> directionText("cnRight");
            case "enLeft" -> directionText("enLeft");
            case "enRight" -> directionText("enRight");
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            // logo1（WARNING_4 箭头）与 logo2（WARNING_5 背景）均由 direction1 决定，两组映射同一字段
            case "logo1", "logo2" -> List.of(new FieldOptionGroup("转向", "direction1", List.of(
                    opt("左转", "left", direction1.getName()),
                    opt("直行", "straight", direction1.getName()),
                    opt("右转", "right", direction1.getName()))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "direction1" -> setDirection1(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
        }
    }

    /** 与原渲染器 DIRECTION_MAP 一致：按方块朝向取面板左/右侧的中/英文方位名 */
    private String directionText(String key) {
        net.minecraft.util.math.Direction facing = getCachedState().get(SignGuideIntersectionWarning4.FACING);
        boolean left = key.equals("cnLeft") || key.equals("enLeft");
        boolean cn = key.equals("cnLeft") || key.equals("cnRight");
        return switch (facing) {
            case NORTH -> left ? (cn ? "西" : "W") : (cn ? "东" : "E");
            case SOUTH -> left ? (cn ? "东" : "E") : (cn ? "西" : "W");
            case WEST -> left ? (cn ? "南" : "S") : (cn ? "北" : "N");
            case EAST -> left ? (cn ? "北" : "N") : (cn ? "南" : "S");
            default -> "";
        };
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

}
