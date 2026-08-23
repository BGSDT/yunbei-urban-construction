package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayNamingNumberEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayNamingNumberUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayNamingNumberScreen extends AbstractSignFormScreen {
    private TextFieldWidget expresswayNameTextField;
    private TextFieldWidget expresswayNumber1TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 230;
    private static final int INPUT_WIDTH = 185;
    private static final int INPUT_HEIGHT = 20;

    public SignExpresswayNamingNumberScreen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_expressway_naming_number");
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";
        String existingExpresswayNumber1 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignExpresswayNamingNumberEntity entity) {
                existingText1 = entity.getExpresswayName();
                existingExpresswayNumber1 = entity.getExpresswayNumber();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.expresswayNumber1TextField = createTextField(panelX + 10, panelY + 40, INPUT_WIDTH, INPUT_HEIGHT, existingExpresswayNumber1);
        this.expresswayNameTextField = createTextField(panelX + 205, panelY + 40, INPUT_WIDTH, INPUT_HEIGHT, existingText1);

        int buttonY = panelY + 195;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_naming_number.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_naming_number.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    @Override
    protected void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String expresswayName = getTextSafely(this.expresswayNameTextField);
            String expresswayNumber1 = getTextSafely(this.expresswayNumber1TextField);

            SignExpresswayNamingNumberUpdatePacket packet =
                    new SignExpresswayNamingNumberUpdatePacket(pos, expresswayNumber1, expresswayName);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_EXPRESSWAY_NAMING_NUMBER, buf);
        }
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        renderPanelBackground(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        renderLabel(context, panelX, panelY, "expressway_number_1_name", 31);
        renderLabel(context, panelX, panelY, "expressway_number_name_name", 31, 205);

        renderTextField(this.expresswayNameTextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumber1TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }
}