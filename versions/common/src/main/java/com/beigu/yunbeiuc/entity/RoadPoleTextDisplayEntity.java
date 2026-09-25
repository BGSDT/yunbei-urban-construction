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
        // 初始化默认文本行
        if (getTextLines().isEmpty()) {
            TextLineData defaultLine = new TextLineData("");
            defaultLine.setColor(0xFFFFFF);
            defaultLine.setFontSize(DEFAULT_FONT_SIZE);
            // 系统初始化生成的行：删除时 UI 需二次确认
            defaultLine.setBuiltin(true);
            getTextLines().add(defaultLine);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        // 先调用父类读取新格式数据
        super.load(nbt);

        // 如果父类没有读取到数据，尝试读取旧格式
        if (getTextLines().isEmpty() && nbt.contains("text")) {
            // 读取旧数据
            legacyText = nbt.getString("text");
            legacyColor = nbt.getInt("color");
            legacyFontSize = nbt.getInt("fontSize");

            // 转换为新格式
            TextLineData line = new TextLineData(legacyText);
            line.setColor(legacyColor);
            // 将旧的 fontSize (int) 转换为新的 fontSize (float)
            line.setFontSize(legacyFontSize / 100.0f); // 假设25对应0.25f
            line.setAlignment(TextAlignment.CENTER_CENTER);
            // 旧格式迁移生成的行：删除时 UI 需二次确认
            line.setBuiltin(true);
            getTextLines().add(line);
        }
    }

    // 兼容旧 API 的方法
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
            // 系统 API 兜底创建的行：删除时 UI 需二次确认
            line.setBuiltin(true);
            getTextLines().add(line);
        } else {
            getTextLines().get(0).setColor(color);
        }
        setTextLines(getTextLines()); // 触发更新
    }

    public int getFontSize() {
        if (getTextLines().isEmpty()) return 25;
        // 将新的 float fontSize 转换回旧的 int fontSize
        return Math.round(getTextLines().get(0).getFontSize() * 100.0f);
    }

    public void setFontSize(int fontSize) {
        if (getTextLines().isEmpty()) {
            TextLineData line = new TextLineData("");
            line.setFontSize(fontSize / 100.0f);
            // 系统 API 兜底创建的行：删除时 UI 需二次确认
            line.setBuiltin(true);
            getTextLines().add(line);
        } else {
            getTextLines().get(0).setFontSize(fontSize / 100.0f);
        }
        setTextLines(getTextLines()); // 触发更新
    }

    // 获取RGB颜色分量（保持兼容）
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
