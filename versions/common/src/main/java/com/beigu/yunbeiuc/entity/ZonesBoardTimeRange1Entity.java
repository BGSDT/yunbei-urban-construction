package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ZonesBoardTimeRange1Entity extends CustomSignBlockEntity {
    private String time1 = "";
    private String time2 = "";

    public ZonesBoardTimeRange1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONES_BOARD_TIME_RANGE_1_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.time1 = nbt.getString("time1");
        this.time2 = nbt.getString("time2");
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("time1", this.time1);
        nbt.putString("time2", this.time2);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("6:00-20:00", 0f, 5.5f, 0.02f, 0x000000, "b"));
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

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "time1" -> time1;
            case "time2" -> time2;
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
