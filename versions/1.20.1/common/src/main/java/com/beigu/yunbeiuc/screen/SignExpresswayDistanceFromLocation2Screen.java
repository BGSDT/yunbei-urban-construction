package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation2Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayDistanceFromLocation2UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class SignExpresswayDistanceFromLocation2Screen extends AbstractSignFormScreen {

    private SignExpresswayDistanceFromLocation2Entity.Expressway expressway1;
    private TextFieldWidget text1TextField;
    private TextFieldWidget expresswayNumberTextField;
    private TextFieldWidget text3TextField;
    private TextFieldWidget length1TextField;
    private TextFieldWidget length2TextField;
    private TextFieldWidget length3TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 245;
    private static final int TEXT_INPUT_WIDTH = 130;
    private static final int LENGTH_INPUT_WIDTH = 70;
    private static final int INPUT_HEIGHT = 20;
    private static final int EXPRESSWAY_BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;

    public SignExpresswayDistanceFromLocation2Screen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_expressway_distance_from_location_2");
    }

    @Override
    protected void init() {
        super.init();

        this.expressway1 = SignExpresswayDistanceFromLocation2Entity.Expressway.NATIONAL;
        String existingText1 = "";
        String existingExpresswayNumber = "";
        String existingText3 = "";
        String existingLength1 = "";
        String existingLength2 = "";
        String existingLength3 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignExpresswayDistanceFromLocation2Entity entity) {
                this.expressway1 = entity.getExpressway1();
                existingText1 = entity.getText1();
                existingExpresswayNumber = entity.getExpresswayNumber();
                existingText3 = entity.getText3();
                existingLength1 = entity.getLength1();
                existingLength2 = entity.getLength2();
                existingLength3 = entity.getLength3();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.text1TextField = createTextField(panelX + 10, panelY + 40, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingText1);
        this.length1TextField = createTextField(panelX + 150, panelY + 40, LENGTH_INPUT_WIDTH, INPUT_HEIGHT, existingLength1);

        this.expresswayNumberTextField = createTextField(panelX + 10, panelY + 70, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingExpresswayNumber);
        this.length2TextField = createTextField(panelX + 150, panelY + 70, LENGTH_INPUT_WIDTH, INPUT_HEIGHT, existingLength2);
        createExpresswayButtons(panelX + 230, panelY + 70, expressway -> this.expressway1 = expressway);

        this.text3TextField = createTextField(panelX + 10, panelY + 100, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingText3);
        this.length3TextField = createTextField(panelX + 150, panelY + 100, LENGTH_INPUT_WIDTH, INPUT_HEIGHT, existingLength3);

        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_2.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_2.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createExpresswayButtons(int x, int y, Consumer<SignExpresswayDistanceFromLocation2Entity.Expressway> expresswayConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.national"), button -> {
                    expresswayConsumer.accept(SignExpresswayDistanceFromLocation2Entity.Expressway.NATIONAL);
                }).dimensions(x, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.provincial"), button -> {
                    expresswayConsumer.accept(SignExpresswayDistanceFromLocation2Entity.Expressway.PROVINCIAL);
                }).dimensions(x + 85, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
    }

    @Override
    protected void saveAndClose() {
        if (this.client != null && this.client.world != null) {

            String text1 = getTextSafely(this.text1TextField);
            String expresswayNumber = getTextSafely(this.expresswayNumberTextField);
            String text3 = getTextSafely(this.text3TextField);
            String length1 = getTextSafely(this.length1TextField);
            String length2 = getTextSafely(this.length2TextField);
            String length3 = getTextSafely(this.length3TextField);

            SignExpresswayDistanceFromLocation2UpdatePacket packet =
                    new SignExpresswayDistanceFromLocation2UpdatePacket(pos, expressway1, text1, expresswayNumber, text3, length1, length2, length3);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
                NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_2, buf);
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

        renderStatus(context, panelX, panelY, 10, 131,
                Text.translatable("text.yunbeiuc.expressway." + expressway1.getName()));

        // 渲染所有文本框
        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumberTextField, context, mouseX, mouseY, delta);
        renderTextField(this.text3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length3TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }
}
