package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayDirection3Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayDirection3UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class SignExpresswayDirection3Screen extends AbstractSignFormScreen {
    private SignExpresswayDirection3Entity.Expressway expressway1;
    private TextFieldWidget text1TextField;
    private TextFieldWidget expresswayNumber1TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 230;
    private static final int EXPRESSWAY_BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;
    private static final int INPUT_WIDTH = 185;
    private static final int INPUT_HEIGHT = 20;

    public SignExpresswayDirection3Screen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_expressway_direction_3");
    }

    @Override
    protected void init() {
        super.init();

        this.expressway1 = SignExpresswayDirection3Entity.Expressway.NATIONAL;
        String existingText1 = "";
        String existingExpresswayNumber1 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignExpresswayDirection3Entity entity) {
                this.expressway1 = entity.getExpressway1();
                existingText1 = entity.getText1();
                existingExpresswayNumber1 = entity.getExpresswayNumber1();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        createExpresswayButtons(panelX + 10, panelY + 40, expressway -> this.expressway1 = expressway);

        this.expresswayNumber1TextField = createTextField(panelX + 205, panelY + 40, INPUT_WIDTH, INPUT_HEIGHT, existingExpresswayNumber1);

        this.text1TextField = createTextField(panelX + 10, panelY + 80, INPUT_WIDTH, INPUT_HEIGHT, existingText1);

        int buttonY = panelY + 195;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_direction_3.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_direction_3.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createExpresswayButtons(int x, int y, Consumer<SignExpresswayDirection3Entity.Expressway> expresswayConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.national"), button -> {
                    expresswayConsumer.accept(SignExpresswayDirection3Entity.Expressway.NATIONAL);
                }).dimensions(x, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.provincial"), button -> {
                    expresswayConsumer.accept(SignExpresswayDirection3Entity.Expressway.PROVINCIAL);
                }).dimensions(x + 85, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
    }

    @Override
    protected void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);
            String expresswayNumber1 = getTextSafely(this.expresswayNumber1TextField);

            SignExpresswayDirection3UpdatePacket packet =
                    new SignExpresswayDirection3UpdatePacket(pos, expressway1, text1, expresswayNumber1);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_EXPRESSWAY_DIRECTION_3, buf);
        }
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        renderPanelBackground(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        renderLabel(context, panelX, panelY, "1_name", 31);
        renderLabel(context, panelX, panelY, "expressway_number_1_name", 31, 205);
        renderLabel(context, panelX, panelY, "text_1_name", 71);

        renderStatus(context, panelX, panelY, 10, 111,
                Text.translatable("text.yunbeiuc.expressway." + expressway1.getName()));

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumber1TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }
}