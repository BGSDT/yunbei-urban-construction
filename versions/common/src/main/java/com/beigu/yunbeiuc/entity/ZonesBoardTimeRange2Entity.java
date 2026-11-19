package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

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
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.time1 = NbtCompat.getString(nbt, "time1");
        this.time2 = NbtCompat.getString(nbt, "time2");
        this.time3 = NbtCompat.getString(nbt, "time3");
        this.time4 = NbtCompat.getString(nbt, "time4");
        if (!CustomSignBlockEntity.hasTextLinesData(nbt) && !hasCustomizedTextLines()) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("time1", this.time1);
        nbt.putString("time2", this.time2);
        nbt.putString("time3", this.time3);
        nbt.putString("time4", this.time4);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("7:00-9:00", 0f, 5f, 0.0143f, 0x000000, "a"));
        lines.add(SignTextLinesHelper.centered("17:00-19:00", 0f, 3.5f, 0.0151f, 0x000000, "a"));
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
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }
}
