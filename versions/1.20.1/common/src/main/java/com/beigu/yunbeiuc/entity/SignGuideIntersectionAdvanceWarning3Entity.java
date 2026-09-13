package com.beigu.yunbeiuc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarning3Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String cnText2 = "";
    private String enText2 = "";
    private String cnText3 = "";
    private String enText3 = "";
    private String cnText4 = "";
    private String enText4 = "";
    private String cnText5 = "";
    private String enText5 = "";
    private String cnText6 = "";
    private String enText6 = "";
    private String cnText7 = "";
    private String enText7 = "";

    public SignGuideIntersectionAdvanceWarning3Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.text1 = nbt.getString("text1");
        this.cnText2 = nbt.getString("cnText2");
        this.enText2 = nbt.getString("enText2");
        this.cnText3 = nbt.getString("cnText3");
        this.enText3 = nbt.getString("enText3");
        this.cnText4 = nbt.getString("cnText4");
        this.enText4 = nbt.getString("enText4");
        this.cnText5 = nbt.getString("cnText5");
        this.enText5 = nbt.getString("enText5");
        this.cnText6 = nbt.getString("cnText6");
        this.enText6 = nbt.getString("enText6");
        this.cnText7 = nbt.getString("cnText7");
        this.enText7 = nbt.getString("enText7");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("cnText2", this.cnText2);
        nbt.putString("enText2", this.enText2);
        nbt.putString("cnText3", this.cnText3);
        nbt.putString("enText3", this.enText3);
        nbt.putString("cnText4", this.cnText4);
        nbt.putString("enText4", this.enText4);
        nbt.putString("cnText5", this.cnText5);
        nbt.putString("enText5", this.enText5);
        nbt.putString("cnText6", this.cnText6);
        nbt.putString("enText6", this.enText6);
        nbt.putString("cnText7", this.cnText7);
        nbt.putString("enText7", this.enText7);
        super.writeNbt(nbt);
    }

    /**
     * 按原 SignGuideIntersectionAdvanceWarning3EntityRenderer 的固定布局生成默认文本行（颜色均为白色）：
     * 原渲染器按方块身份区分（同一方块类注册了 WARNING_3 / WARNING_4 两个方块）：
     * WARNING_3 → renderCenteredText(cnText2, 0, 8, 0.03) / (enText2, 0, 4, 0.023) / (cnText4, -14, 4, 0.03)
     *             / (enText4, -14, 0, 0.023) / (cnText5, -14, -4, 0.03) / (enText5, -14, -8, 0.023)
     *             / (cnText6, 14, 4, 0.03) / (enText6, 14, 0, 0.023) / (cnText7, 14, -4, 0.03) / (enText7, 14, -8, 0.023)；
     * WARNING_4 → (text1, 0, -9, 0.03) / (cnText2, 0, 6, 0.03) / (enText2, 0, 3, 0.023) / (cnText3, 0, 13, 0.03)
     *             / (enText3, 0, 10, 0.023) / (cnText4, -14, 8, 0.03) / (enText4, -14, 4, 0.023) / (cnText5, -14, 0, 0.03)
     *             / (enText5, -14, -4, 0.023) / (cnText6, 14, 8, 0.03) / (enText6, 14, 4, 0.023) / (cnText7, 14, 0, 0.03)
     *             / (enText7, 14, -4, 0.023)。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean isWarning3 = getCachedState().getBlock() == com.beigu.yunbeiuc.block.SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3.get();
        List<TextLineData> lines = new ArrayList<>();
        if (isWarning3) {
            lines.add(SignTextLinesHelper.centered("新吴路", 0f, 8f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Xinwu Rd.", 0f, 4f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("锡山路", -14f, 4f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Xishan Rd.", -14f, 0f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("梁溪路", -14f, -4f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Liangxi Rd.", -14f, -8f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("滨湖路", 14f, 4f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Binhu Rd.", 14f, 0f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("惠山路", 14f, -4f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("Huishan Rd", 14f, -8f, 0.023f, 0xFFFFFF));
        } else {
            lines.add(SignTextLinesHelper.centered("江苏路", 0f, -9f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("语文路", 0f, 6f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("97/120", 0f, 3f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("数学路", 0f, 13f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("93/120", 0f, 10f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("英语路", -14f, 8f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("121/130", -14f, 4f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("道法路", -14f, 0f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("50/50", -14f, -4f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("历史", 14f, 8f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("45/50", 14f, 4f, 0.023f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("地理路", 14f, 0f, 0.03f, 0xFFFFFF));
            lines.add(SignTextLinesHelper.centered("I don't know", 14f, -4f, 0.023f, 0xFFFFFF));
        }
        setTextLines(lines);
    }

    public String getText1() {
        return text1;
    }
    public String getCnText2() {
        return cnText2;
    }
    public String getEnText2() {
        return enText2;
    }
    public String getCnText3() {
        return cnText3;
    }
    public String getEnText3() {
        return enText3;
    }
    public String getCnText4() {
        return cnText4;
    }
    public String getEnText4() {
        return enText4;
    }
    public String getCnText5() {
        return cnText5;
    }
    public String getEnText5() {
        return enText5;
    }
    public String getCnText6() {
        return cnText6;
    }
    public String getEnText6() {
        return enText6;
    }
    public String getCnText7() {
        return cnText7;
    }
    public String getEnText7() {
        return enText7;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public void setCnText2(String cnText2) {
        this.cnText2 = cnText2;
        markDirtyAndUpdate();
    }
    public void setEnText2(String enText2) {
        this.enText2 = enText2;
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
    public void setCnText6(String cnText6) {
        this.cnText6 = cnText6;
        markDirtyAndUpdate();
    }
    public void setEnText6(String enText6) {
        this.enText6 = enText6;
        markDirtyAndUpdate();
    }
    public void setCnText7(String cnText7) {
        this.cnText7 = cnText7;
        markDirtyAndUpdate();
    }
    public void setEnText7(String enText7) {
        this.enText7 = enText7;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "cnText2" -> cnText2;
            case "enText2" -> enText2;
            case "cnText3" -> cnText3;
            case "enText3" -> enText3;
            case "cnText4" -> cnText4;
            case "enText4" -> enText4;
            case "cnText5" -> cnText5;
            case "enText5" -> enText5;
            case "cnText6" -> cnText6;
            case "enText6" -> enText6;
            case "cnText7" -> cnText7;
            case "enText7" -> enText7;
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
