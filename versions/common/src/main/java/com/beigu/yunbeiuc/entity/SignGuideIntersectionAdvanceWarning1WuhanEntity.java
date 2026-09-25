package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarning1WuhanEntity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String cnText3 = "";
    private String enText3 = "";
    private String cnText4 = "";
    private String enText4 = "";
    private String cnText5 = "";
    private String enText5 = "";

    public SignGuideIntersectionAdvanceWarning1WuhanEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.cnText3 = nbt.getString("cnText3");
        this.enText3 = nbt.getString("enText3");
        this.cnText4 = nbt.getString("cnText4");
        this.enText4 = nbt.getString("enText4");
        this.cnText5 = nbt.getString("cnText5");
        this.enText5 = nbt.getString("enText5");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("cnText3", this.cnText3);
        nbt.putString("enText3", this.enText3);
        nbt.putString("cnText4", this.cnText4);
        nbt.putString("enText4", this.enText4);
        nbt.putString("cnText5", this.cnText5);
        nbt.putString("enText5", this.enText5);
        super.saveAdditional(nbt);
    }

    /**
     * 按原 SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer 的固定布局生成默认文本行：
     * 原渲染器按方块身份区分（同一方块类注册了 WUHAN_LEFT / WUHAN_STRAIGHT / WUHAN_RIGHT 三个方块）：
     * WUHAN_RIGHT → renderCenteredText(text1, 16.5, 12, 0.023, 0x275aa8) / (text2, 16.5, -12, 0.023, 0x275aa8)，
     *               (cnText3, -6, 12, 0.03) / (enText3, -6, 8, 0.023) / (cnText4, -6, 1, 0.03) / (enText4, -6, -3, 0.023)
     *               / (cnText5, -6, -10, 0.03) / (enText5, -6, -14, 0.023)，其余白色；
     * 其余（LEFT/STRAIGHT）→ 各行 x 取反（text1/text2 为 -16.5，其余为 6）。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean isRight = getBlockState().getBlock() == com.beigu.yunbeiuc.block.SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_RIGHT.get();
        List<TextLineData> lines = new ArrayList<>();
        if (isRight) {
            lines.add(SignTextLinesHelper.centered("沌阳大道", 16.5f, 12f, 0.023f, 0x275aa8));
            lines.add(SignTextLinesHelper.centered("东风大道", 16.5f, -12f, 0.023f, 0x275aa8));
            lines.add(SignTextLinesHelper.centered("枫树六路", -6f, 12f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Fengshu Rd.(No.6)", -6f, 8f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("枫树三路", -6f, 1f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Fengshu Rd.(No.3)", -6f, -3f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("车城南路", -6f, -10f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Checheng Rd.(S)", -6f, -14f, 0.023f, 0xFFFFFF));
        } else {
            lines.add(SignTextLinesHelper.centered("沌阳大道", -16.5f, 12f, 0.023f, 0x275aa8));
            lines.add(SignTextLinesHelper.centered("东风大道", -16.5f, -12f, 0.023f, 0x275aa8));
            lines.add(SignTextLinesHelper.centered("枫树六路", 6f, 12f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Fengshu Rd.(No.6)", 6f, 8f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("枫树三路", 6f, 1f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Fengshu Rd.(No.3)", 6f, -3f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("车城南路", 6f, -10f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Checheng Rd.(S)", 6f, -14f, 0.023f, 0xFFFFFF));
        }
        setTextLines(lines);
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }

    public String getCnText3() {return cnText3;}

    public String getEnText3() {return enText3;}

    public String getCnText4() {return cnText4;}

    public String getEnText4() {return enText4;}

    public String getCnText5() {return cnText5;}

    public String getEnText5() {return enText5;}

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }

    public void setText2(String text2) {
        this.text2 = text2;
        markDirtyAndUpdate();
    }

    public void setCnText3(String cnText3) {
        this.cnText3 = cnText3;
        markDirtyAndUpdate();
    }

    public void setEnText3(String enText3) {
        this.enText3 = enText3;
        markDirtyAndUpdate();
    }

    public void setCnText4(String cnText4) {
        this.cnText4 = cnText4;
        markDirtyAndUpdate();
    }

    public void setEnText4(String enText4) {
        this.enText4 = enText4;
        markDirtyAndUpdate();
    }

    public void setCnText5(String cnText5) {
        this.cnText5 = cnText5;
        markDirtyAndUpdate();
    }

    public void setEnText5(String enText5) {
        this.enText5 = enText5;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "text2" -> text2;
            case "cnText3" -> cnText3;
            case "enText3" -> enText3;
            case "cnText4" -> cnText4;
            case "enText4" -> enText4;
            case "cnText5" -> cnText5;
            case "enText5" -> enText5;
            default -> null;
        };
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
    }
}
