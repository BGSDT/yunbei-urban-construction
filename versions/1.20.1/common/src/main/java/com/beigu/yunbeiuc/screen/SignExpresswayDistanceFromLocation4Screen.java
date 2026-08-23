package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation4Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayDistanceFromLocation4UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class SignExpresswayDistanceFromLocation4Screen extends AbstractSignFormScreen {
    private SignExpresswayDistanceFromLocation4Entity.RoadType roadType1;
    private SignExpresswayDistanceFromLocation4Entity.RoadType roadType2;
    private SignExpresswayDistanceFromLocation4Entity.RoadType roadType3;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
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

    public SignExpresswayDistanceFromLocation4Screen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_expressway_distance_from_location_4");
    }

    @Override
    protected void init() {
        super.init();

        this.roadType1 = SignExpresswayDistanceFromLocation4Entity.RoadType.EXPRESSWAY;
        this.roadType2 = SignExpresswayDistanceFromLocation4Entity.RoadType.EXPRESSWAY;
        this.roadType3 = SignExpresswayDistanceFromLocation4Entity.RoadType.EXPRESSWAY;
        String existingText1 = "";
        String existingText2 = "";
        String existingText3 = "";
        String existingLength1 = "";
        String existingLength2 = "";
        String existingLength3 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignExpresswayDistanceFromLocation4Entity entity) {
                this.roadType1 = entity.getRoadType1();
                this.roadType2 = entity.getRoadType2();
                this.roadType3 = entity.getRoadType3();
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
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
        createButtons(panelX + 230, panelY + 40, roadType -> this.roadType1 = roadType);

        this.text2TextField = createTextField(panelX + 10, panelY + 70, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingText2);
        this.length2TextField = createTextField(panelX + 150, panelY + 70, LENGTH_INPUT_WIDTH, INPUT_HEIGHT, existingLength2);
        createButtons(panelX + 230, panelY + 70, roadType -> this.roadType2 = roadType);

        this.text3TextField = createTextField(panelX + 10, panelY + 100, TEXT_INPUT_WIDTH, INPUT_HEIGHT, existingText3);
        this.length3TextField = createTextField(panelX + 150, panelY + 100, LENGTH_INPUT_WIDTH, INPUT_HEIGHT, existingLength3);
        createButtons(panelX + 230, panelY + 100, roadType -> this.roadType3 = roadType);

        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_4.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_distance_from_location_4.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createButtons(int x, int y, Consumer<SignExpresswayDistanceFromLocation4Entity.RoadType> expresswayConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.road_type.expressway"), button -> {
                    expresswayConsumer.accept(SignExpresswayDistanceFromLocation4Entity.RoadType.EXPRESSWAY);
                }).dimensions(x, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.road_type.ordinary_municipal"), button -> {
                    expresswayConsumer.accept(SignExpresswayDistanceFromLocation4Entity.RoadType.ORDINARY_MUNICIPAL);
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

            SignExpresswayDistanceFromLocation4UpdatePacket packet =
                    new SignExpresswayDistanceFromLocation4UpdatePacket(pos, text1, text2, text3, roadType1, roadType2, roadType3, length1, length2, length3);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
                NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_4, buf);
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
                Text.translatable("text.yunbeiuc.road_type." + roadType1.getName()));
        renderStatus(context, panelX, panelY, 80, 131,
                Text.translatable("text.yunbeiuc.road_type." + roadType2.getName()));

        // 渲染所有文本框
        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.length3TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

}
