package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange2Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.ZonesBoardTimeRange2UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ZonesBoardTimeRange2Screen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget time1TextField;
    private TextFieldWidget time2TextField;
    private TextFieldWidget time3TextField;
    private TextFieldWidget time4TextField;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 235;
    private static final int INPUT_WIDTH = 300;
    private static final int INPUT_HEIGHT = 24;

    public ZonesBoardTimeRange2Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.zones_board_time_range_2.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        String existingTime1 = "";
        String existingTime2 = "";
        String existingTime3 = "";
        String existingTime4 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof ZonesBoardTimeRange2Entity entity) {
                existingTime1 = entity.getTime1();
                existingTime2 = entity.getTime2();
                existingTime3 = entity.getTime3();
                existingTime4 = entity.getTime4();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.time1TextField = createTextField(panelX + 10, panelY + 25, INPUT_WIDTH, existingTime1);
        this.time2TextField = createTextField(panelX + 10, panelY + 70, INPUT_WIDTH, existingTime2);
        this.time3TextField = createTextField(panelX + 10, panelY + 115, INPUT_WIDTH, existingTime3);
        this.time4TextField = createTextField(panelX + 10, panelY + 160, INPUT_WIDTH, existingTime4);

        int buttonY = panelY + 200;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.zones_board_time_range_2.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 60, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.zones_board_time_range_2.cancel"), button -> this.close())
                        .dimensions(panelX + 170, buttonY, 90, 24)
                        .build()
        );

        this.setFocused(this.time1TextField);
    }

    private TextFieldWidget createTextField(int x, int y, int width, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                width, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.zones_board_time_range_2.content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.zones_board_time_range_2.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String time1 = this.time1TextField != null ? this.time1TextField.getText() : "";
            String time2 = this.time2TextField != null ? this.time2TextField.getText() : "";
            String time3 = this.time3TextField != null ? this.time3TextField.getText() : "";
            String time4 = this.time4TextField != null ? this.time4TextField.getText() : "";

            ZonesBoardTimeRange2UpdatePacket packet = new ZonesBoardTimeRange2UpdatePacket(pos, time1, time2, time3, time4);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_ZONES_BOARD_TIME_RANGE_2, buf);
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
                Text.translatable("text.yunbeiuc.zones_board_time_range_2.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_time_range_2.text_1_name"),
                panelX + 10, panelY + 16,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_time_range_2.text_2_name"),
                panelX + 10, panelY + 61,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_time_range_2.text_3_name"),
                panelX + 10, panelY + 106,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_time_range_2.text_4_name"),
                panelX + 10, panelY + 151,
                0xFFAAAAAA
        );

        renderTextField(this.time1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.time2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.time3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.time4TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderTextField(TextFieldWidget textField, DrawContext context, int mouseX, int mouseY, float delta) {
        if (textField != null) {
            textField.render(context, mouseX, mouseY, delta);
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