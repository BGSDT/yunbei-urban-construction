package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarning7Entity extends CustomSignBlockEntity {
    private SignTurnDirection direction1 = SignTurnDirection.STRAIGHT;
    private SignTurnDirection direction2 = SignTurnDirection.STRAIGHT;
    private SignTurnDirection direction3 = SignTurnDirection.STRAIGHT;
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";

    public SignGuideIntersectionAdvanceWarning7Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.direction1 = SignTurnDirection.fromName(nbt.getString("direction1"), SignTurnDirection.STRAIGHT);
        this.direction2 = SignTurnDirection.fromName(nbt.getString("direction2"), SignTurnDirection.STRAIGHT);
        this.direction3 = SignTurnDirection.fromName(nbt.getString("direction3"), SignTurnDirection.STRAIGHT);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("direction2", this.direction2.getName());
        nbt.putString("direction3", this.direction3.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignGuideIntersectionAdvanceWarning7EntityRenderer 的固定布局生成默认文本行：
     * renderDirectionLogo(direction1, andX -13, 12, size 0.4) / renderDirectionLogo(direction2, -13, 0, 0.4)
     * / renderDirectionLogo(direction3, -13, -12, 0.4)，其中 adjustedX = (direction == RIGHT) ? 13 : -13
     * （右转箭头整行 X 镜像），迁移为基础 xOffset 0 + xShift 占位符 {logo1x}/{logo2x}/{logo3x}（值 = 镜像后的完整 x）；
     * renderTextWithDirectionAdjustment(text1, 6, 12, 0.035) / (text2, 6, 0, 0.035) / (text3, 6, -12, 0.035)——
     * 原渲染器按方向枚举镜像行 X（RIGHT 时 -6），文本行无 xShift 机制，默认行按非镜像方向（LEFT/STRAIGHT）
     * 生成静态位置，RIGHT 方向需手动调整该行 xOffset。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo1}.png", -13f, 12f, 0.4f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo2}.png", -13f, 0f, 0.4f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo3}.png", -13f, -12f, 0.4f));
        lines.add(SignTextLinesHelper.centered("{text1}", 6f, 12f, 0.035f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("{text2}", 6f, 0f, 0.035f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("{text3}", 6f, -12f, 0.035f, 0xFFFFFF));
        setTextLines(lines);
    }

    public SignTurnDirection getDirection1() { return direction1; }
    public SignTurnDirection getDirection2() { return direction2; }
    public SignTurnDirection getDirection3() { return direction3; }
    public String getText1() { return text1; }
    public String getText2() { return text2; }
    public String getText3() { return text3;}

    public void setDirection1(SignTurnDirection direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }
    public void setDirection2(SignTurnDirection direction2) {
        this.direction2 = direction2;
        markDirtyAndUpdate();
    }
    public void setDirection3(SignTurnDirection direction3) {
        this.direction3 = direction3;
        markDirtyAndUpdate();
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

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            // 对应原 renderDirectionLogo：方向枚举选择直行箭头纹理（left/straight/right）
            case "logo1" -> logoName(direction1);
            case "logo2" -> logoName(direction2);
            case "logo3" -> logoName(direction3);
            // 对应原 renderDirectionLogo 的 X 镜像：adjustedX = (direction == RIGHT) ? -andX : andX（andX = -13）
            case "logo1x", "logo2x", "logo3x" -> "0"; // 兼容旧存档已保存的行；默认行不再附加偏移，X 由玩家在编辑界面调整
            case "text1" -> text1;
            case "text2" -> text2;
            case "text3" -> text3;
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1" -> List.of(new FieldOptionGroup("转向", "direction1", List.of(
                    opt("左转", "left", direction1.getName()),
                    opt("直行", "straight", direction1.getName()),
                    opt("右转", "right", direction1.getName()))));
            case "logo2" -> List.of(new FieldOptionGroup("转向", "direction2", List.of(
                    opt("左转", "left", direction2.getName()),
                    opt("直行", "straight", direction2.getName()),
                    opt("右转", "right", direction2.getName()))));
            case "logo3" -> List.of(new FieldOptionGroup("转向", "direction3", List.of(
                    opt("左转", "left", direction3.getName()),
                    opt("直行", "straight", direction3.getName()),
                    opt("右转", "right", direction3.getName()))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "direction1" -> setDirection1(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
            case "direction2" -> setDirection2(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
            case "direction3" -> setDirection3(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
        }
    }

    private static String logoName(SignTurnDirection direction) {
        return switch (direction) {
            case LEFT -> "left";
            case STRAIGHT -> "straight";
            case RIGHT -> "right";
        };
    }


    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

}
