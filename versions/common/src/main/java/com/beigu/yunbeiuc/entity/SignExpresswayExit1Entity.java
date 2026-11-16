package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayExit1Entity extends CustomSignBlockEntity {
    private String logo = "red_number_logo";

    public SignExpresswayExit1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_EXIT_1_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.logo = NbtCompat.getString(nbt, "image");
        if (!CustomSignBlockEntity.hasTextLinesData(nbt) && !hasCustomizedTextLines()) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("image", this.logo);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        String signVariantId = BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()).getPath();
        switch (signVariantId) {
            case "sign_expressway_exit_3" -> {
                lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_{logo}.png", 0f, 10f, 0.8f));
                TextLineData line1 = new TextLineData("G204");
                line1.setXOffset(0f);
                line1.setYOffset(9.75f);
                line1.setZOffset(0.032f);
                line1.setRotX(0f);
                line1.setRotY(0f);
                line1.setRotZ(0f);
                line1.setColor(0xFFFFFF);
                line1.setAlignment(TextAlignment.CENTER_CENTER);
                line1.setBold(false);
                line1.setItalic(false);
                line1.setUnderline(false);
                line1.setShadow(false);
                line1.setOutline(false);
                line1.setGlowing(false);
                line1.setFontSize(0.7f);
                line1.setAbcFont("b");
                line1.setScaleX(1f);
                line1.setScaleY(1f);
                line1.setScaleZ(1f);
                line1.setBuiltin(true);
                lines.add(line1);
                lines.add(SignTextLinesHelper.centered("如皋", -8f, -1f, 0.041f, 0xFFFFFF, "a"));
                lines.add(SignTextLinesHelper.centered("海安", 8f, -1f, 0.041f, 0xFFFFFF, "a"));
                lines.add(SignTextLinesHelper.right("2", -7f, -12f, 0.041f, 0xFFFFFF, "a"));
                lines.add(SignTextLinesHelper.centered("km", -5f, -12.5f, 0.031f, 0xFFFFFF, "b"));
                lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_right_side_median_strip.png", 8f, -11f, 0.3592f, 90f));
                lines.add(SignTextLinesHelper.centered("145", -7.5f, 18.75f, 0.03f, 0x2D9B47, "b"));
            }
            default -> {
                lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_{logo}.png", 0f, 10f, 0.8f));
                TextLineData line1 = new TextLineData("G204");
                line1.setXOffset(0f);
                line1.setYOffset(9.75f);
                line1.setZOffset(0.032f);
                line1.setRotX(0f);
                line1.setRotY(0f);
                line1.setRotZ(0f);
                line1.setColor(0xFFFFFF);
                line1.setAlignment(TextAlignment.CENTER_CENTER);
                line1.setBold(false);
                line1.setItalic(false);
                line1.setUnderline(false);
                line1.setShadow(false);
                line1.setOutline(false);
                line1.setGlowing(false);
                line1.setFontSize(0.7f);
                line1.setAbcFont("b");
                line1.setScaleX(1f);
                line1.setScaleY(1f);
                line1.setScaleZ(1f);
                line1.setBuiltin(true);
                lines.add(line1);
                lines.add(SignTextLinesHelper.centered("如皋", -8f, -1f, 0.041f, 0xFFFFFF, "a"));
                lines.add(SignTextLinesHelper.centered("海安", 8f, -1f, 0.041f, 0xFFFFFF, "a"));
                lines.add(SignTextLinesHelper.right("2", -7f, -12f, 0.041f, 0xFFFFFF, "a"));
                lines.add(SignTextLinesHelper.centered("km", -5f, -12.5f, 0.031f, 0xFFFFFF, "b"));
                lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_right_side_median_strip.png", 8f, -11f, 0.3592f, 90f));
                lines.add(SignTextLinesHelper.centered("145", 13.5f, 18.75f, 0.03f, 0x2D9B47, "b"));
            }
        }
        setTextLines(lines);
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "logo" -> logo;
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo" -> List.of(new FieldOptionGroup("底色", "logo", List.of(
                    opt("国道", "red_number_logo", logo),
                    opt("省道", "yellow_number_logo", logo),
                    opt("县、乡道", "white_number_logo", logo),
                    opt("快速路", "expressway_logo", logo))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logo" -> setLogo(value);
            default -> { }
        }
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }
}
