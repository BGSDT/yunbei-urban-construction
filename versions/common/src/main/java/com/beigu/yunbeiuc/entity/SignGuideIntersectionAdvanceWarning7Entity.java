package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarning7Entity extends CustomSignBlockEntity {
    private SignTurnDirection direction1 = SignTurnDirection.LEFT;
    private SignTurnDirection direction2 = SignTurnDirection.LEFT;
    private SignTurnDirection direction3 = SignTurnDirection.RIGHT;
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";

    public SignGuideIntersectionAdvanceWarning7Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
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
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("direction2", this.direction2.getName());
        nbt.putString("direction3", this.direction3.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        super.saveAdditional(nbt);
    }

    /**
     * 按原 SignGuideIntersectionAdvanceWarning7EntityRenderer 的固定布局生成默认文本行：
     * renderDirectionLogo(direction1, andX -13, 12, size 0.4) / renderDirectionLogo(direction2, -13, 0, 0.4)
     * / renderDirectionLogo(direction3, -13, -12, 0.4)，其中 adjustedX = (direction == RIGHT) ? 13 : -13
     * （右转整行 X 镜像），迁移为按当前 direction1/2/3 直接生成行 xOffset，切换转向时由 applyFieldOption
     * 同步更新（见 mirrorLines）；
     * renderTextWithDirectionAdjustment(text1, 6, 12, 0.035) / (text2, 6, 0, 0.035) / (text3, 6, -12, 0.035)——
     * 原渲染器按方向枚举镜像行 X（RIGHT 时取反），迁移方式同 logo 行。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        // 原渲染器右转时整行 X 镜像：logo ±13、文本 ±6（旧存档 direction 已为 RIGHT 时同样生效）
        float logo1X = direction1 == SignTurnDirection.RIGHT ? 13f : -13f;
        float logo2X = direction2 == SignTurnDirection.RIGHT ? 13f : -13f;
        float logo3X = direction3 == SignTurnDirection.RIGHT ? 13f : -13f;
        float text1X = direction1 == SignTurnDirection.RIGHT ? -6f : 6f;
        float text2X = direction2 == SignTurnDirection.RIGHT ? -6f : 6f;
        float text3X = direction3 == SignTurnDirection.RIGHT ? -6f : 6f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo1}.png", logo1X, 12f, 0.4f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo2}.png", logo2X, 0f, 0.4f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo3}.png", logo3X, -12f, 0.4f));
        lines.add(SignTextLinesHelper.centered("学府路", text1X, 12f, 0.035f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("公滨路", text2X, 0f, 0.035f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered("黄河路", text3X, -12f, 0.035f, 0xFFFFFF));
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
            case "direction1" -> {
                setDirection1(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
                mirrorLines();
            }
            case "direction2" -> {
                setDirection2(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
                mirrorLines();
            }
            case "direction3" -> {
                setDirection3(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
                mirrorLines();
            }
        }
    }

    /**
     * 对应原渲染器的右转 X 镜像：direction 为 RIGHT 时整行 X 取反
     * （renderDirectionLogo adjustedX = (texture==RIGHT) ? -andX : andX，andX=-13 → RIGHT 时 13；
     *   renderTextWithDirectionAdjustment adjustedX = (direction==RIGHT) ? -6 : 6）。
     * logo 行按 {logoN} 占位符定位；文本行默认文本为固定字面量，按 builtin 标记 + 出现顺序
     * 定位（第 1/2/3 条对应 direction1/2/3）。UI 新增的自定义行（无 builtin 标记）不触碰。
     */
    private void mirrorLines() {
        float logo1X = direction1 == SignTurnDirection.RIGHT ? 13f : -13f;
        float logo2X = direction2 == SignTurnDirection.RIGHT ? 13f : -13f;
        float logo3X = direction3 == SignTurnDirection.RIGHT ? 13f : -13f;
        float text1X = direction1 == SignTurnDirection.RIGHT ? -6f : 6f;
        float text2X = direction2 == SignTurnDirection.RIGHT ? -6f : 6f;
        float text3X = direction3 == SignTurnDirection.RIGHT ? -6f : 6f;
        boolean changed = false;
        int textIndex = 0;
        for (TextLineData line : getTextLines()) {
            String text = line.getText();
            if (text.trim().startsWith("-texture")) {
                if (text.contains("{logo1}")) { line.setXOffset(logo1X); changed = true; }
                else if (text.contains("{logo2}")) { line.setXOffset(logo2X); changed = true; }
                else if (text.contains("{logo3}")) { line.setXOffset(logo3X); changed = true; }
            } else if (line.isBuiltin()) {
                if (textIndex == 0) { line.setXOffset(text1X); changed = true; }
                else if (textIndex == 1) { line.setXOffset(text2X); changed = true; }
                else if (textIndex == 2) { line.setXOffset(text3X); changed = true; }
                textIndex++;
            }
        }
        if (changed) markDirtyAndUpdate();
    }

    private static String logoName(SignTurnDirection direction) {
        return switch (direction) {
            case LEFT -> "left";
            case STRAIGHT -> "straight";
            case RIGHT -> "right";
        };
    }


    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
    }

}
