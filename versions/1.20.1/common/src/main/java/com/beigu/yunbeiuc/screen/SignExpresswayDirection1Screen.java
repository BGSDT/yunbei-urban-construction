package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayDirection1Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayDirection1UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayDirection1Screen extends AbstractSignFormScreen {
    private TextFieldWidget text1TextField;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 195;
    private static final int INPUT_WIDTH = 300;
    private static final int INPUT_HEIGHT = 24;

    public SignExpresswayDirection1Screen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_expressway_direction_1");
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignExpresswayDirection1Entity entity) {
                existingText1 = entity.getText1();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.text1TextField = createTextField(panelX + 10, panelY + 25, INPUT_WIDTH, INPUT_HEIGHT, existingText1);

        int buttonY = panelY + 160;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_direction_1.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 60, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_direction_1.cancel"), button -> this.close())
                        .dimensions(panelX + 170, buttonY, 90, 24)
                        .build()
        );

        this.setFocused(this.text1TextField);
    }

    @Override
    protected void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);

            SignExpresswayDirection1UpdatePacket packet = new SignExpresswayDirection1UpdatePacket(pos, text1);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_EXPRESSWAY_DIRECTION_1, buf);
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
                Text.translatable("text.yunbeiuc.sign_expressway_direction_1.text_1_name"),
                panelX + 10, panelY + 16,
                0xFFAAAAAA
        );

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }
}