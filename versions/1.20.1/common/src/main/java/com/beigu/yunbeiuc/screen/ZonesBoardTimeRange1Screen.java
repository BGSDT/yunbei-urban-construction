package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange1Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.ZonesBoardTimeRange1UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ZonesBoardTimeRange1Screen extends AbstractSignFormScreen {
    private TextFieldWidget time1TextField;
    private TextFieldWidget time2TextField;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 195;
    private static final int INPUT_WIDTH = 300;
    private static final int INPUT_HEIGHT = 24;

    public ZonesBoardTimeRange1Screen(BlockPos pos) {
        super(pos, "text.yunbeiuc.zones_board_time_range_1");
    }

    @Override
    protected void init() {
        super.init();

        String existingTime1 = "";
        String existingTime2 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof ZonesBoardTimeRange1Entity entity) {
                existingTime1 = entity.getTime1();
                existingTime2 = entity.getTime2();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.time1TextField = createTextField(panelX + 10, panelY + 25, INPUT_WIDTH, INPUT_HEIGHT, existingTime1);
        this.time2TextField = createTextField(panelX + 10, panelY + 70, INPUT_WIDTH, INPUT_HEIGHT, existingTime2);

        int buttonY = panelY + 160;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.zones_board_time_range_1.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 60, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.zones_board_time_range_1.cancel"), button -> this.close())
                        .dimensions(panelX + 170, buttonY, 90, 24)
                        .build()
        );

        this.setFocused(this.time1TextField);
    }

    @Override
    protected void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String time1 = this.time1TextField != null ? this.time1TextField.getText() : "";
            String time2 = this.time2TextField != null ? this.time2TextField.getText() : "";

            ZonesBoardTimeRange1UpdatePacket packet = new ZonesBoardTimeRange1UpdatePacket(pos, time1, time2);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_ZONES_BOARD_TIME_RANGE_1, buf);
        }
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        renderPanelBackground(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_time_range_1.text_1_name"),
                panelX + 10, panelY + 16,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_time_range_1.text_2_name"),
                panelX + 10, panelY + 61,
                0xFFAAAAAA
        );

        renderTextField(this.time1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.time2TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }
}