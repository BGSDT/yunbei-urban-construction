package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.ZonesBoard1Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.ZonesBoard1UpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ZonesBoard1Screen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget text1TextField;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 140;

    public ZonesBoard1Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.zones_board_1.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof ZonesBoard1Entity entity) {
                existingText1 = entity.getText1();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.text1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 25,
                300, 24,
                Text.translatable("text.yunbeiuc.zones_board_1.content")
        );
        this.text1TextField.setMaxLength(256);
        this.text1TextField.setText(existingText1);
        this.text1TextField.setPlaceholder(Text.translatable("text.yunbeiuc.zones_board_1.placeholder"));
        this.addSelectableChild(this.text1TextField);

        int buttonY = panelY + 105;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.zones_board_1.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 60, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.zones_board_1.cancel"), button -> this.close())
                        .dimensions(panelX + 170, buttonY, 90, 24)
                        .build()
        );

        this.setFocused(this.text1TextField);
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField.getText();

            ZonesBoard1UpdatePacket packet = new ZonesBoard1UpdatePacket(pos, text1);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_ZONES_BOARD_1, buf);
        }
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_1.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_1.text_1_name"),
                panelX + 10, panelY + 16,
                0xFFAAAAAA
        );

        this.text1TextField.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
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