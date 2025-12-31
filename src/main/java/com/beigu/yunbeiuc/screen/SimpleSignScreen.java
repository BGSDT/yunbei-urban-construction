// SimpleSignScreen.java
package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SimpleSignEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SimpleSignScreen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget textField;

    public SimpleSignScreen(BlockPos pos) {
        super(Text.literal("Edit Sign Text"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();
        
        // 输入框
        this.textField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 20, 200, 20, Text.literal("Enter text"));
        this.textField.setMaxLength(100);
        this.addSelectableChild(this.textField);
        this.setFocused(this.textField);

        // 确认按钮
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Confirm"), button -> this.saveAndClose())
            .dimensions(this.width / 2 - 100, this.height / 2 + 10, 200, 20)
            .build());
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            // 获取方块实体并设置文本
            if (this.client.world.getBlockEntity(this.pos) instanceof SimpleSignEntity signEntity) {
                signEntity.setText(this.textField.getText());
            }
        }
        this.close();
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        this.textField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }
}