package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

public class RoadPoleTextDisplayEntity extends CustomSignBlockEntity {
    // 旧数据字段（仅用于兼容读取）
    private String legacyText = "";
    private int legacyColor = 0x000000;
    private int legacyFontSize = 25;
    private static final float DEFAULT_FONT_SIZE = 1f;

    public RoadPoleTextDisplayEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROAD_POLE_TEXT_DISPLAY_ENTITY.get(), pos, state);
        if (getTextLines().isEmpty()) {
            TextLineData defaultLine = new TextLineData("");
            defaultLine.setColor(0xFFFFFF);
            defaultLine.setFontSize(DEFAULT_FONT_SIZE);
            defaultLine.setBuiltin(true);
            getTextLines().add(defaultLine);
        }
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);

        if (getTextLines().isEmpty() && nbt.contains("text")) {
            legacyText = nbt.getString("text");
            legacyColor = nbt.getInt("color");
            legacyFontSize = nbt.getInt("fontSize");

            TextLineData line = new TextLineData(legacyText);
            line.setColor(legacyColor);
            line.setFontSize(legacyFontSize / 100.0f); // 假设25对应0.25f
            line.setAlignment(TextAlignment.CENTER_CENTER);
            line.setBuiltin(true);
            getTextLines().add(line);
        }
    }

    public String getText() {
        return getTextLines().isEmpty() ? "" : getTextLines().get(0).getText();
    }

    public void setText(String text) {
        if (getTextLines().isEmpty()) {
            TextLineData line = new TextLineData(text);
            line.setFontSize(DEFAULT_FONT_SIZE);
            // 系统 API 兜底创建的行：删除时 UI 需二次确认
            line.setBuiltin(true);
            getTextLines().add(line);
        } else {
            getTextLines().get(0).setText(text);
        }
        setTextLines(getTextLines()); // 触发更新
    }

    public int getColor() {
        return getTextLines().isEmpty() ? 0x000000 : getTextLines().get(0).getColor();
    }

    public void setColor(int color) {
        if (getTextLines().isEmpty()) {
            TextLineData line = new TextLineData("");
            line.setColor(color);
            line.setFontSize(DEFAULT_FONT_SIZE);
            line.setBuiltin(true);
            getTextLines().add(line);
        } else {
            getTextLines().get(0).setColor(color);
        }
        setTextLines(getTextLines()); // 触发更新
    }

    public int getFontSize() {
        if (getTextLines().isEmpty()) return 25;
        return Math.round(getTextLines().get(0).getFontSize() * 100.0f);
    }

    public void setFontSize(int fontSize) {
        if (getTextLines().isEmpty()) {
            TextLineData line = new TextLineData("");
            line.setFontSize(fontSize / 100.0f);
            line.setBuiltin(true);
            getTextLines().add(line);
        } else {
            getTextLines().get(0).setFontSize(fontSize / 100.0f);
        }
        setTextLines(getTextLines()); // 触发更新
    }

    public float getRed() {
        int color = getColor();
        return ((color >> 16) & 0xFF) / 255.0f;
    }

    public float getGreen() {
        int color = getColor();
        return ((color >> 8) & 0xFF) / 255.0f;
    }

    public float getBlue() {
        int color = getColor();
        return (color & 0xFF) / 255.0f;
    }

    public float getAlpha() {
        return 1.0f;
    }
}
