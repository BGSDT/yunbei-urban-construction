package com.beigu.yunbeiuc.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ZonesBoardTimeRange2Entity extends CustomSignBlockEntity {
    private String time1 = "";
    private String time2 = "";
    private String time3 = "";
    private String time4 = "";

    public ZonesBoardTimeRange2Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONES_BOARD_TIME_RANGE_2_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.time1 = nbt.getString("time1");
        this.time2 = nbt.getString("time2");
        this.time3 = nbt.getString("time3");
        this.time4 = nbt.getString("time4");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("time1", this.time1);
        nbt.putString("time2", this.time2);
        nbt.putString("time3", this.time3);
        nbt.putString("time4", this.time4);
        super.writeNbt(nbt);
    }

    /**
     * 按原 ZonesBoardTimeRange2EntityRenderer 的固定布局生成默认文本行
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("7:00-9:00", 0f, 6f, 0.02f, 0x000000, "b"));
        lines.add(SignTextLinesHelper.centered("17:00-19:00", 0f, 3f, 0.02f, 0x000000, "b"));
        setTextLines(lines);
    }

    private static String t(String s) { return s == null ? "" : s; }

    public String getTime1() { return time1; }
    public void setTime1(String time1) {
        this.time1 = time1;
        markDirtyAndUpdate();
    }

    public String getTime2() { return time2; }
    public void setTime2(String time2) {
        this.time2 = time2;
        markDirtyAndUpdate();
    }

    public String getTime3() { return time3; }
    public void setTime3(String time3) {
        this.time3 = time3;
        markDirtyAndUpdate();
    }

    public String getTime4() { return time4; }
    public void setTime4(String time4) {
        this.time4 = time4;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "time1" -> time1;
            case "time2" -> time2;
            case "time3" -> time3;
            case "time4" -> time4;
            default -> null;
        };
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
}
