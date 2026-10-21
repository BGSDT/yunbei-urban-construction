package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning4;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionWarning4Entity extends CustomSignBlockEntity {
    private SignTurnDirection direction1 = SignTurnDirection.STRAIGHT;
    private String text1 = "";

    public SignGuideIntersectionWarning4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_WARNING_4_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.direction1 = SignTurnDirection.fromName(nbt.getString("direction1"), SignTurnDirection.STRAIGHT);
        this.text1 = nbt.getString("text1");
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("text1", this.text1);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean isWarning4 = blockStateForDefaults().getBlock() == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_4.get();
        List<TextLineData> lines = new ArrayList<>();
        if (isWarning4) {
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo1}.png", -13f, 0f, 0.4f, 0f));
            float textX = direction1 == SignTurnDirection.RIGHT ? -4f : 4f;
            lines.add(SignTextLinesHelper.centered("林萃路", textX, 0f, 0.04f, 0xFFFFFF, "a"));
        } else {
            lines.add(SignTextLinesHelper.centered("澎湖路", 0f, 0f, 0.035f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_guide_intersection_warning_5_{logo2}.png", 0f, 0f, 1.75f, 0f));
            lines.add(SignTextLinesHelper.centered("{cnLeft}", 16f, 4f, 0.02f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.centered("{cnRight}", -16f, 4f, 0.02f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.centered("{enLeft}", 16f, -4f, 0.02f, 0xFFFFFF, "a"));
            lines.add(SignTextLinesHelper.centered("{enRight}", -16f, -4f, 0.02f, 0xFFFFFF, "a"));
        }
        setTextLines(lines);
    }

    public SignTurnDirection getDirection1() { return direction1; }
    public String getText1() {
        return text1;
    }

    public void setDirection1(SignTurnDirection direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }
    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "logo1" -> direction1.getName();
            case "logo2" -> direction1.getName();
            case "logo1x" -> "0";
            case "cnLeft" -> directionText("cnLeft");
            case "cnRight" -> directionText("cnRight");
            case "enLeft" -> directionText("enLeft");
            case "enRight" -> directionText("enRight");
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1", "logo2" -> List.of(new FieldOptionGroup("转向", "direction1", List.of(
                    opt("左转", "left", direction1.getName()),
                    opt("直行", "straight", direction1.getName()),
                    opt("右转", "right", direction1.getName()))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "direction1" -> setDirection1(SignTurnDirection.fromName(value, SignTurnDirection.STRAIGHT));
        }
    }

    private String directionText(String key) {
        net.minecraft.core.Direction facing = blockStateForDefaults().getValue(SignGuideIntersectionWarning4.FACING);
        boolean left = key.equals("cnLeft") || key.equals("enLeft");
        boolean cn = key.equals("cnLeft") || key.equals("cnRight");
        return switch (facing) {
            case NORTH -> left ? (cn ? "西" : "W") : (cn ? "东" : "E");
            case SOUTH -> left ? (cn ? "东" : "E") : (cn ? "西" : "W");
            case WEST -> left ? (cn ? "南" : "S") : (cn ? "北" : "N");
            case EAST -> left ? (cn ? "北" : "N") : (cn ? "南" : "S");
            default -> "";
        };
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }

}
