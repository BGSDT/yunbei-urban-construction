package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarning6Entity extends CustomSignBlockEntity {
    private SignTurnDirection direction1 = SignTurnDirection.STRAIGHT;
    private SignTurnDirection direction2 = SignTurnDirection.STRAIGHT;
    private String text1 = "";
    private String text2 = "";

    public SignGuideIntersectionAdvanceWarning6Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.direction1 = SignTurnDirection.fromName(nbt.getString("direction1"), SignTurnDirection.STRAIGHT);
        this.direction2 = SignTurnDirection.fromName(nbt.getString("direction2"), SignTurnDirection.STRAIGHT);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("direction2", this.direction2.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignGuideIntersectionAdvanceWarning6EntityRenderer 的固定布局生成默认文本行：
     * 原渲染器按方块身份区分（同一方块类注册了 WARNING_6 / WARNING_8 两个方块）。
     * WARNING_6 → renderDirectionLogo1(direction1, andX -13, 6, size 0.4) / renderDirectionLogo1(direction2, -13, -6, 0.4)，
     *             其中 adjustedX = (direction == RIGHT) ? 13 : -13（右转箭头整行 X 镜像），迁移为基础 xOffset 0 +
     *             xShift 占位符 {logo1x}/{logo2x}（值 = 镜像后的完整 x）；
     *             renderTextWithDirectionAdjustment(text1, 6, 6, 0.035) / (text2, 6, -6, 0.035)——原渲染器按方向枚举
     *             镜像行 X（RIGHT 时 -6），文本行无 xShift 机制，默认行按非镜像方向（LEFT/STRAIGHT）生成静态位置，
     *             RIGHT 方向需手动调整该行 xOffset。
     * WARNING_8 → renderDirectionLogo2(direction1, -9, -3, 0.4) / renderDirectionLogo2(direction2, 9, -3, 0.4)（转弯箭头纹理，不镜像），
     *             renderCenteredText(text1, -9, 6, 0.035) / (text2, 9, 6, 0.035)。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean isWarning6 = getCachedState().getBlock() == com.beigu.yunbeiuc.block.SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6.get();
        List<TextLineData> lines = new ArrayList<>();
        if (isWarning6) {
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo1}.png", -13f, 6f, 0.4f));
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo2}.png", -13f, -6f, 0.4f));
            lines.add(SignTextLinesHelper.centered("{text1}", 6f, 6f, 0.035f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("{text2}", 6f, -6f, 0.035f, 0xFFFFFF));
        } else {
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo1}.png", -9f, -3f, 0.4f));
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo2}.png", 9f, -3f, 0.4f));
            lines.add(SignTextLinesHelper.centered("{text1}", -9f, 6f, 0.035f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("{text2}", 9f, 6f, 0.035f, 0xFFFFFF));
        }
        setTextLines(lines);
    }

    public SignTurnDirection getDirection1() { return direction1; }
    public SignTurnDirection getDirection2() { return direction2; }
    public String getText1() { return text1; }
    public String getText2() { return text2; }

    public void setDirection1(SignTurnDirection direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }
    public void setDirection2(SignTurnDirection direction2) {
        this.direction2 = direction2;
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

    @Override
    public String getPlaceholderValue(String key) {
        boolean isWarning8 = getCachedState().getBlock() == com.beigu.yunbeiuc.block.SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_8.get();
        return switch (key) {
            // 对应原 renderDirectionLogo1（左转/直行/右转直行箭头）与 renderDirectionLogo2（转弯箭头纹理）：
            // WARNING_8 方块用 left_turn/right_turn 纹理，其余用 left/right
            case "logo1" -> logoName(direction1, isWarning8);
            case "logo2" -> logoName(direction2, isWarning8);
            // 对应原 renderDirectionLogo1 的 X 镜像：adjustedX = (direction == RIGHT) ? -andX : andX（andX = -13）
            case "logo1x", "logo2x" -> "0"; // 兼容旧存档已保存的行；默认行不再附加偏移，X 由玩家在编辑界面调整
            case "text1" -> text1;
            case "text2" -> text2;
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
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "direction1" -> setDirection1(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
            case "direction2" -> setDirection2(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
        }
    }

    private static String logoName(SignTurnDirection direction, boolean turnSet) {
        return switch (direction) {
            case LEFT -> turnSet ? "left_turn" : "left";
            case STRAIGHT -> "straight";
            case RIGHT -> turnSet ? "right_turn" : "right";
        };
    }


    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

}
