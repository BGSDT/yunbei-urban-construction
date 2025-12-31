package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.RoadPolesTextDisplayEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.RoadPolesTextDisplayUpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class RoadPolesTextDisplayScreen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget textField;
    private TextFieldWidget colorField;

    // 常用颜色
    private final int[] commonColors = {
            0x000000, // 黑色
            0xFF0000, // 红色
            0x00FF00, // 绿色
            0x0000FF, // 蓝色
            0xFFFF00, // 黄色
            0xFF00FF, // 紫色
            0x00FFFF, // 青色
            0xFFFFFF  // 白色
    };

    private final String[] colorNames = {"黑", "红", "绿", "蓝", "黄", "紫", "青", "白"};

    public RoadPolesTextDisplayScreen(BlockPos pos) {
        super(Text.literal("文本显示设置"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        // 获取现有设置
        String existingText = "";
        String existingColor = "000000";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof RoadPolesTextDisplayEntity entity) {
                existingText = entity.getText();
                existingColor = String.format("%06X", entity.getColor() & 0xFFFFFF);
            }
        }

        // 第一行：内容输入
        this.textField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, this.height / 2 - 60, 300, 20, Text.literal("输入文本"));
        this.textField.setMaxLength(256);
        this.textField.setText(existingText);
        this.textField.setPlaceholder(Text.literal("请输入显示文本..."));
        this.addSelectableChild(this.textField);

        // 第二行：颜色设置
        // 颜色输入框
        this.colorField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, this.height / 2 - 30, 120, 20, Text.literal("颜色"));
        this.colorField.setMaxLength(6);
        this.colorField.setText(existingColor);
        this.colorField.setPlaceholder(Text.literal("十六进制颜色"));
        this.addSelectableChild(this.colorField);

        // 常用颜色按钮
        int colorButtonX = this.width / 2 - 20;
        for (int i = 0; i < commonColors.length; i++) {
            final int colorIndex = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(colorNames[i]), button -> {
                        String hexColor = String.format("%06X", commonColors[colorIndex] & 0xFFFFFF);
                        this.colorField.setText(hexColor);
                    })
                    .dimensions(colorButtonX + (i % 4) * 25, this.height / 2 - 30 + (i / 4) * 25, 20, 20)
                    .build());
        }

        // 第三行：保存和取消按钮
        this.addDrawableChild(ButtonWidget.builder(Text.literal("保存"), button -> this.saveAndClose())
                .dimensions(this.width / 2 - 105, this.height / 2 + 10, 100, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("取消"), button -> this.close())
                .dimensions(this.width / 2 + 5, this.height / 2 + 10, 100, 20)
                .build());

        this.setFocused(this.textField);
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            // 设置文本
            String text = this.textField.getText();

            // 设置颜色
            int color;
            try {
                String colorText = this.colorField.getText();
                if (colorText.isEmpty()) {
                    colorText = "000000";
                }
                color = Integer.parseInt(colorText, 16);
            } catch (NumberFormatException e) {
                color = 0x000000; // 默认黑色
            }

            // 创建网络包并发送 - 使用 ModMessages 中的标识符
            RoadPolesTextDisplayUpdatePacket packet = new RoadPolesTextDisplayUpdatePacket(pos, text, color);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);

            // 使用 ModMessages.UPDATE_ROAD_POLES_TEXT
            ClientPlayNetworking.send(ModMessages.UPDATE_ROAD_POLES_TEXT, buf);
        }
        this.close();
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 90, 0xFFFFFF);

        // 绘制标签
        context.drawTextWithShadow(this.textRenderer, "显示内容:", this.width / 2 - 150, this.height / 2 - 75, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "颜色值:", this.width / 2 - 150, this.height / 2 - 45, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "常用颜色:", this.width / 2 - 20, this.height / 2 - 45, 0xFFFFFF);

        // 渲染输入框
        this.textField.render(context, mouseX, mouseY, delta);
        this.colorField.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);

        // 显示当前颜色预览 - 移到所有按钮右边
        int previewX = this.width / 2 - 20 + 100; // 所有按钮右边（4列 * 25px = 100px）
        int previewY = this.height / 2 - 30;
        try {
            String colorText = this.colorField.getText();
            if (!colorText.isEmpty()) {
                int color = Integer.parseInt(colorText, 16);
                // 绘制颜色预览方块
                context.fill(previewX, previewY, previewX + 20, previewY + 20, 0xFF000000 | color);
                // 绘制边框
                context.drawBorder(previewX, previewY, 20, 20, 0xFFAAAAAA);
            }
        } catch (NumberFormatException e) {
            // 绘制默认预览（灰色）
            context.fill(previewX, previewY, previewX + 20, previewY + 20, 0xFF888888);
            context.drawBorder(previewX, previewY, 20, 20, 0xFFAAAAAA);
        }

        // 绘制"预览"标签
        context.drawTextWithShadow(this.textRenderer, "预览", previewX, previewY - 12, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC键
            this.close();
            return true;
        } else if (keyCode == 257) { // 回车键
            this.saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}