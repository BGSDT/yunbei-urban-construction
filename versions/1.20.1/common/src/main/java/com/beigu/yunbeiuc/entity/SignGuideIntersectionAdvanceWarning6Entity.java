package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarning6Entity extends CustomSignBlockEntity {
    private SignTurnDirection direction1 = SignTurnDirection.STRAIGHT;
    private SignTurnDirection direction2 = SignTurnDirection.RIGHT;
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
     *             其中 adjustedX = (direction == RIGHT) ? 13 : -13（右转整行 X 镜像），迁移为按当前 direction1/2
     *             直接生成行 xOffset，切换转向时由 applyFieldOption 同步更新（见 mirrorWarning6Lines）；
     *             renderTextWithDirectionAdjustment(text1, 6, 6, 0.035) / (text2, 6, -6, 0.035)——原渲染器按方向枚举
     *             镜像行 X（RIGHT 时取反），迁移方式同 logo 行。
     * WARNING_8 → renderDirectionLogo2(direction1, -9, -3, 0.4) / renderDirectionLogo2(direction2, 9, -3, 0.4)（转弯箭头纹理，不镜像），
     *             renderCenteredText(text1, -9, 6, 0.035) / (text2, 9, 6, 0.035)。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean isWarning6 = getCachedState().getBlock() == com.beigu.yunbeiuc.block.SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6.get();
        List<TextLineData> lines = new ArrayList<>();
        if (isWarning6) {
            // 原渲染器右转时整行 X 镜像：logo ±13、文本 ±6（旧存档 direction 已为 RIGHT 时同样生效）
            float logo1X = direction1 == SignTurnDirection.RIGHT ? 13f : -13f;
            float logo2X = direction2 == SignTurnDirection.RIGHT ? 13f : -13f;
            float text1X = direction1 == SignTurnDirection.RIGHT ? -6f : 6f;
            float text2X = direction2 == SignTurnDirection.RIGHT ? -6f : 6f;
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo1}.png", logo1X, 6f, 0.4f));
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo2}.png", logo2X, -6f, 0.4f));
            lines.add(SignTextLinesHelper.centered("大厂", text1X, 6f, 0.035f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.centered("燕郊", text2X, -6f, 0.035f, 0xFFFFFF, "a"));
        } else {
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo1}.png", -9f, -3f, 0.4f));
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo2}.png", 9f, -3f, 0.4f));
            lines.add(SignTextLinesHelper.centered("东菀", -9f, 6f, 0.035f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.centered("深圳", 9f, 6f, 0.035f, 0xFFFFFF, "a"));
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
            case "direction1" -> {
                setDirection1(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
                mirrorWarning6Lines();
            }
            case "direction2" -> {
                setDirection2(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
                mirrorWarning6Lines();
            }
        }
    }

    /**
     * 对应原渲染器 WARNING_6 分支的右转 X 镜像：direction 为 RIGHT 时整行 X 取反
     * （renderDirectionLogo1 adjustedX = (texture==RIGHT) ? -andX : andX，andX=-13 → RIGHT 时 13；
     *   renderTextWithDirectionAdjustment adjustedX = (direction==RIGHT) ? -6 : 6）。
     * logo 行按 {logoN} 占位符定位；文本行默认文本为固定字面量，按 builtin 标记 + 出现顺序
     * 定位（第 1/2 条对应 direction1/2）。UI 新增的自定义行（无 builtin 标记）不触碰；
     * WARNING_8 方块布局固定不镜像。
     */
    private void mirrorWarning6Lines() {
        if (getCachedState().getBlock() != com.beigu.yunbeiuc.block.SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6.get()) return;
        float logo1X = direction1 == SignTurnDirection.RIGHT ? 13f : -13f;
        float logo2X = direction2 == SignTurnDirection.RIGHT ? 13f : -13f;
        float text1X = direction1 == SignTurnDirection.RIGHT ? -6f : 6f;
        float text2X = direction2 == SignTurnDirection.RIGHT ? -6f : 6f;
        boolean changed = false;
        int textIndex = 0;
        for (TextLineData line : getTextLines()) {
            String text = line.getText();
            if (text.trim().startsWith("-texture")) {
                if (text.contains("{logo1}")) { line.setXOffset(logo1X); changed = true; }
                else if (text.contains("{logo2}")) { line.setXOffset(logo2X); changed = true; }
            } else if (line.isBuiltin()) {
                if (textIndex == 0) { line.setXOffset(text1X); changed = true; }
                else if (textIndex == 1) { line.setXOffset(text2X); changed = true; }
                textIndex++;
            }
        }
        if (changed) markDirtyAndUpdate();
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
