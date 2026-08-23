package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation5Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayDistanceFromLocation5UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class SignExpresswayDistanceFromLocation5Screen extends AbstractSignFormScreen {

    private SignExpresswayDistanceFromLocation5Entity.Expressway expressway1;
    private SignExpresswayDistanceFromLocation5Entity.Expressway expressway2;
    private SignExpresswayDistanceFromLocation5Entity.Expressway expressway3;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget text3TextField;
    private TextFieldWidget length1TextField;
    private TextFieldWidget length2TextField;
    private TextFieldWidget length3TextField;
    private TextFieldWidget expresswayNumber1TextField;
    private TextFieldWidget expresswayNumber2TextField;
    private TextFieldWidget expresswayNumber3TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 270;
    private static final int TEXT_INPUT_WIDTH = 130;
    private static final int INPUT_HEIGHT = 20;
    private static final int EXPRESSWAY_BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;

    public SignExpresswayDistanceFromLocation5Screen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_expressway_distance_from_location_5");
    }

    @Override
    protected void init() {
        super.init();

        this.expressway1 = SignExpresswayDistanceFromLocation5Entity.Expressway.NATIONAL;
        this.expressway2 = SignExpresswayDistanceFromLocation5Entity.Expressway.NATIONAL;
        this.expressway3 = SignExpresswayDistanceFromLocation5Entity.Expressway.NATIONAL;
        String existingText1 = "";
        String existingText2 = "";
        String existingText3 = "";
        String existingLength1 = "";
        String existingLength2 = "";
        String existingLength3 = "";
        String existingExpresswayNumber1 = "";
        String existingExpresswayNumber2 = "";
        String existingExpresswayNumber3 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignExpresswayDistanceFromLocation5Entity entity) {
                this.expressway1 = entity.getExpressway1();
                this.expressway2 = entity.getExpressway2();
                this.expressway3 = entity.getExpressway3();
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingText3 = entity.getText3();
                existingLength1 = entity.getLength1();
                existingLength2 = entity.getLength2();
                existingLength3 = entity.getLength3();
                existingExpresswayNumber1 = entity.getExpresswayNumber1();
                existingExpresswayNumber2 = entity.getExpresswayNumber2();
                existingExpresswayNumber3 = entity.getExpresswayNumber3();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        createExpresswayButtons(panelX + 10, panelY + 40, expressway -> this.expressway1 = expressway);
        this.expresswayNumber1TextField = createTextField(panelX + 210, panelY + 40, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingExpresswayNumber1);

        createExpresswayButtons(panelX + 10, panelY + 70, expressway -> this.expressway2 = expressway);
        this.expresswayNumber2TextField = createTextField(panelX + 210, panelY + 70, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingExpresswayNumber2);

        createExpresswayButtons(panelX + 10, panelY + 100, expressway -> this.expressway3 = expressway);
        this.expresswayNumber3TextField = createTextField(panelX + 210, panelY + 100, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingExpresswayNumber3);

        this.text1TextField = createTextField(panelX + 10, panelY + 140, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingText1);
        this.length1TextField = createTextField(panelX + 210, panelY + 140, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingLength1);

        this.text2TextField = createTextField(panelX + 10, panelY + 170, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingText2);
        this.length2TextField = createTextField(panelX + 210, panelY + 170, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingLength2);

        this.text3TextField = createTextField(panelX + 10, panelY + 200, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingText3);
        this.length3TextField = createTextField(panelX + 210, panelY + 200, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingLength3);


        int buttonY = panelY + 240;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_5.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_5.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createExpresswayButtons(int x, int y, Consumer<SignExpresswayDistanceFromLocation5Entity.Expressway> expresswayConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.national"), button -> {
                    expresswayConsumer.accept(SignExpresswayDistanceFromLocation5Entity.Expressway.NATIONAL);
                }).dimensions(x, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.provincial"), button -> {
                    expresswayConsumer.accept(SignExpresswayDistanceFromLocation5Entity.Expressway.PROVINCIAL);
                }).dimensions(x + 85, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
    }

    @Override
    protected void saveAndClose() {
        if (this.client != null && this.client.world != null) {

            String text1 = getTextSafely(this.text1TextField);
            String text2 = getTextSafely(this.text2TextField);
            String text3 = getTextSafely(this.text3TextField);
            String length1 = getTextSafely(this.length1TextField);
            String length2 = getTextSafely(this.length2TextField);
            String length3 = getTextSafely(this.length3TextField);
            String expresswayNumber1 = getTextSafely(this.expresswayNumber1TextField);
            String expresswayNumber2 = getTextSafely(this.expresswayNumber2TextField);
            String expresswayNumber3 = getTextSafely(this.expresswayNumber3TextField);

            SignExpresswayDistanceFromLocation5UpdatePacket packet =
                    new SignExpresswayDistanceFromLocation5UpdatePacket(pos, text1, text2, text3, length1, length2, length3, expressway1, expressway2, expressway3, expresswayNumber1, expresswayNumber2, expresswayNumber3);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
                NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_5, buf);
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
        renderLabel(context, panelX, panelY, "2_name", 61);
        renderLabel(context, panelX, panelY, "3_name", 91);
        renderLabel(context, panelX, panelY, "text_1_name", 131);
        renderLabel(context, panelX, panelY, "length_1_name", 131, 210);
        renderLabel(context, panelX, panelY, "text_2_name", 161);
        renderLabel(context, panelX, panelY, "length_2_name", 161, 210);
        renderLabel(context, panelX, panelY, "text_3_name", 191);
        renderLabel(context, panelX, panelY, "length_3_name", 191, 210);

        renderStatus(context, panelX, panelY, 10, 231,
                Text.translatable("text.yunbeiuc.expressway." + expressway1.getName()));
        renderStatus(context, panelX, panelY, 80, 231,
                Text.translatable("text.yunbeiuc.expressway." + expressway2.getName()));
        renderStatus(context, panelX, panelY, 150, 231,
                Text.translatable("text.yunbeiuc.expressway." + expressway3.getName()));
        // 渲染所有文本框
        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumber1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumber2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumber3TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }
}
