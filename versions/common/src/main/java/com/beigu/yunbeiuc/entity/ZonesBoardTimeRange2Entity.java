package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

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
    public void load(CompoundTag nbt) {
        super.load(nbt);
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
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("time1", this.time1);
        nbt.putString("time2", this.time2);
        nbt.putString("time3", this.time3);
        nbt.putString("time4", this.time4);
        super.saveAdditional(nbt);
    }

    /**
     * 按原 ZonesBoardTimeRange2EntityRenderer 的固定布局生成默认文本行
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("{time1}-{time2}", 0f, 6f, 0.02f, 0x000000));
        lines.add(SignTextLinesHelper.centered("{time3}-{time4}", 0f, 3f, 0.02f, 0x000000));
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
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
    }
}
