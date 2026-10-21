package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayNamingNumberEntity extends CustomSignBlockEntity {
    private String expresswayNumber = "";
    private String expresswayName = "";

    public SignExpresswayNamingNumberEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_NAMING_NUMBER_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.expresswayNumber = nbt.getString("expresswayNumber");
        this.expresswayName = nbt.getString("expresswayName");
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("expresswayNumber", this.expresswayNumber);
        nbt.putString("expresswayName", this.expresswayName);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered("G2", 0f, 1.5f, 0.08f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.centered("京 沪 高 速", 0f, -6.5f, 0.02f, 0xFFFFFF, "a"));
        setTextLines(lines);
    }

    public String getExpresswayNumber() { return expresswayNumber; }
    public void setExpresswayNumber(String expresswayNumber) {
        this.expresswayNumber = expresswayNumber;
        markDirtyAndUpdate();
    }
    public String getExpresswayName() { return expresswayName; }
    public void setExpresswayName(String expresswayName) {
        this.expresswayName = expresswayName;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "expresswayNumber" -> expresswayNumber;
            case "expresswayName" -> expresswayName;
            case "expresswayNameSpaced" -> insertSpaceBetweenChars(expresswayName);
            default -> null;
        };
    }

    public static String insertSpaceBetweenChars(String str) {
        if (str == null || str.isEmpty() || str.equals(" ")) {
            return str;
        }
        return str.replaceAll(".(?!$)", "$0 ");
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }
}
