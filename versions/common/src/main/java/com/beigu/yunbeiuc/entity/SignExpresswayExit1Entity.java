package com.beigu.yunbeiuc.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayExit1Entity extends CustomSignBlockEntity {
    private String logo1 = "";

    public SignExpresswayExit1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_EXIT_1_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.logo1 = nbt.getString("logo1");
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("logo1", this.logo1);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        TextLineData baseplate = new TextLineData("-rect 36 32");
        baseplate.setXOffset(0f);
        baseplate.setYOffset(0f);
        baseplate.setColor(0x00703C);
        baseplate.setAlignment(TextAlignment.CENTER_CENTER);
        baseplate.setBuiltin(true);
        lines.add(baseplate);
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", 0f, 10f, 0.65f));
        lines.add(SignTextLinesHelper.centered("G204", 0f, 10f, 0.021f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.centered("如皋", -8f, -1f, 0.041f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.centered("海安", 8f, -1f, 0.041f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.centered("2", -9f, -11f, 0.031f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.centered("km", -5f, -12f, 0.021f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_right_side_median_strip.png", 8f, -11f, 0.31f, 180f));
        setTextLines(lines);
    }

    public String getLogo1() { return logo1; }
    public void setLogo1(String logo1) {
        this.logo1 = logo1;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "logo1" -> logo1;
            default -> null;
        };
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), 3);
        }
    }
}
