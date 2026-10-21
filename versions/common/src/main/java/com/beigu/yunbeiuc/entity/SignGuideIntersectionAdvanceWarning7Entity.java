package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

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
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.direction1 = SignTurnDirection.fromName(nbt.getString("direction1"), SignTurnDirection.STRAIGHT);
        this.direction2 = SignTurnDirection.fromName(nbt.getString("direction2"), SignTurnDirection.STRAIGHT);
        this.direction3 = SignTurnDirection.fromName(nbt.getString("direction3"), SignTurnDirection.STRAIGHT);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("direction2", this.direction2.getName());
        nbt.putString("direction3", this.direction3.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        float logo1X = direction1 == SignTurnDirection.RIGHT ? 13f : -13f;
        float logo2X = direction2 == SignTurnDirection.RIGHT ? 13f : -13f;
        float logo3X = direction3 == SignTurnDirection.RIGHT ? 13f : -13f;
        float text1X = direction1 == SignTurnDirection.RIGHT ? -6f : 6f;
        float text2X = direction2 == SignTurnDirection.RIGHT ? -6f : 6f;
        float text3X = direction3 == SignTurnDirection.RIGHT ? -6f : 6f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo1}.png", logo1X, 12f, 0.4f, 0f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo2}.png", logo2X, 0f, 0.4f, 0f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_indication_{logo3}.png", logo3X, -12f, 0.4f, 0f));
        lines.add(SignTextLinesHelper.centered("学府路", text1X, 12f, 0.035f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.centered("公滨路", text2X, 0f, 0.035f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.centered("黄河路", text3X, -12f, 0.035f, 0xFFFFFF, "a"));
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
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }

}
