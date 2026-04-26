package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.RoadPoleTextDisplayEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.RoadPoleTextDisplayUpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class RoadPoleTextDisplayScreen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget textField;
    private TextFieldWidget colorField;

    // 常用颜色
    private final int[] commonColors = {
            0x000000, // 黑色
            0xFF5555, // 红色
            0x55FF55, // 绿色
            0x5555FF, // 蓝色
            0xFFFF55, // 黄色
            0xFF55FF, // 紫色
            0x55FFFF, // 青色
            0xFFFFFF  // 白色
    };

    private final String[] colorTranslationKeys = {
            "text.yunbeiuc.road_pole_text_display.color.black",
            "text.yunbeiuc.road_pole_text_display.color.red",
            "text.yunbeiuc.road_pole_text_display.color.green",
            "text.yunbeiuc.road_pole_text_display.color.blue",
            "text.yunbeiuc.road_pole_text_display.color.yellow",
            "text.yunbeiuc.road_pole_text_display.color.purple",
            "text.yunbeiuc.road_pole_text_display.color.cyan",
            "text.yunbeiuc.road_pole_text_display.color.white"
    };

    private final int[] colorDisplayColors = {
            0x000000, 0xFF0000, 0x00AA00, 0x0000FF,
            0xFFAA00, 0xAA00AA, 0x00AAAA, 0xCCCCCC
    };

    // 面板尺寸常量
    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 195;

    public RoadPoleTextDisplayScreen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.road_pole_text_display.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        // 获取现有设置
        String existingText = "";
        String existingColor = "FFFFFF";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof RoadPoleTextDisplayEntity entity) {
                existingText = entity.getText();
                if (entity.getColor() != 0) {
                    existingColor = String.format("%06X", entity.getColor() & 0xFFFFFF);
                }
            }
        }

        // 计算面板位置（垂直居中）
        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // === 文本输入区域 ===
        this.textField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 25,
                300, 24,
                Text.translatable("text.yunbeiuc.road_pole_text_display.content")
        );
        this.textField.setMaxLength(256);
        this.textField.setText(existingText);
        this.textField.setPlaceholder(Text.translatable("text.yunbeiuc.road_pole_text_display.placeholder"));
        this.addSelectableChild(this.textField);

        // === 颜色输入区域 ===
        this.colorField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 70,
                140, 22,
                Text.translatable("text.yunbeiuc.road_pole_text_display.color")
        );
        this.colorField.setMaxLength(6);
        this.colorField.setText(existingColor);
        this.colorField.setPlaceholder(Text.translatable("text.yunbeiuc.road_pole_text_display.color_placeholder"));
        this.addSelectableChild(this.colorField);

        // === 常用颜色按钮 ===
        int colorStartX = panelX + 10;
        int colorStartY = panelY + 100;
        for (int i = 0; i < commonColors.length; i++) {
            final int colorIndex = i;
            this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("■"), button -> {
                                String hexColor = String.format("%06X", commonColors[colorIndex] & 0xFFFFFF);
                                this.colorField.setText(hexColor);
                            })
                            .dimensions(colorStartX + (i % 4) * 76, colorStartY + (i / 4) * 28, 72, 24)
                            .build()
            );
        }

        // === 底部按钮 ===
        int buttonY = panelY + 160;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.road_pole_text_display.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 60, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.road_pole_text_display.cancel"), button -> this.close())
                        .dimensions(panelX + 170, buttonY, 90, 24)
                        .build()
        );

        this.setFocused(this.textField);
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text = this.textField.getText();

            int color = 0xFFFFFF;
            try {
                String colorText = this.colorField.getText();
                if (!colorText.isEmpty()) {
                    color = Integer.parseInt(colorText, 16);
                }
            } catch (NumberFormatException e) {
                color = 0xFFFFFF;
            }

            RoadPoleTextDisplayUpdatePacket packet = new RoadPoleTextDisplayUpdatePacket(pos, text, color);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_ROAD_POLES_TEXT, buf);
        }
        this.close();
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        // 背景
        this.renderBackground(context);

        // 计算面板位置（垂直居中）
        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // 绘制面板底色（深灰色半透明）
        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);

        // 绘制面板边框（浅灰色）
        context.drawBorder(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        // === 标题 ===
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.road_pole_text_display.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        // === 标签 ===
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.road_pole_text_display.content"),
                panelX + 10, panelY + 16,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.road_pole_text_display.color"),
                panelX + 10, panelY + 61,
                0xFFAAAAAA
        );

        // === 颜色预览 ===
        int previewX = panelX + 180;
        int previewY = panelY + 70;
        int previewSize = 22;

        int previewColor = 0xFFFFFFFF;
        try {
            String colorText = this.colorField.getText();
            if (!colorText.isEmpty()) {
                int parsedColor = Integer.parseInt(colorText, 16);
                previewColor = 0xFF000000 | parsedColor;
            }
        } catch (NumberFormatException ignored) {}

        // 预览方块背景阴影
        context.fill(previewX + 1, previewY + 1, previewX + previewSize + 1, previewY + previewSize + 1, 0x40000000);
        // 预览方块
        context.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, previewColor);
        // 预览边框
        context.drawBorder(previewX, previewY, previewSize, previewSize, 0xFF888888);
        // 预览标签
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.road_pole_text_display.preview"),
                previewX, previewY - 11,
                0xFFAAAAAA
        );

        // === 渲染输入框 ===
        this.textField.render(context, mouseX, mouseY, delta);
        this.colorField.render(context, mouseX, mouseY, delta);

        // === 渲染按钮 ===
        super.render(context, mouseX, mouseY, delta);

        // 覆盖渲染颜色按钮上的色块和文本
        int colorStartX = panelX + 10;
        int colorStartY = panelY + 100;
        for (int i = 0; i < commonColors.length; i++) {
            int bx = colorStartX + (i % 4) * 76;
            int by = colorStartY + (i / 4) * 28;

            // 色块
            context.fill(bx + 6, by + 6, bx + 20, by + 20, 0xFF000000 | colorDisplayColors[i]);
            context.drawBorder(bx + 6, by + 6, 14, 14, 0xFF888888);

            // 颜色名称文本
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable(colorTranslationKeys[i]),
                    bx + 26, by + 8,
                    0xFFCCCCCC
            );
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC键
            this.close();
            return true;
        } else if (keyCode == 257 || keyCode == 335) { // 回车键或小键盘回车
            this.saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}