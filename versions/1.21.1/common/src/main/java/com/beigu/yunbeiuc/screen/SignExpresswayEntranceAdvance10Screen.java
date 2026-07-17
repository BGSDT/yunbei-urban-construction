package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance10Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayEntranceAdvance10UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayEntranceAdvance10Screen extends Screen {
    private final BlockPos pos;

    private SignExpresswayEntranceAdvance10Entity.Expressway expressway1;
    private SignExpresswayEntranceAdvance10Entity.Expressway expressway2;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget expresswayNumber1TextField;
    private TextFieldWidget expresswayNumber2TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 230;
    private static final int DIRECTION_BUTTON_WIDTH = 45;
    private static final int EXPRESSWAY_BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;
    private static final int INPUT_WIDTH = 185;
    private static final int INPUT_HEIGHT = 20;

    public SignExpresswayEntranceAdvance10Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_expressway_entrance_advance_10.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();


        this.expressway1 = SignExpresswayEntranceAdvance10Entity.Expressway.NATIONAL;
        this.expressway2 = SignExpresswayEntranceAdvance10Entity.Expressway.NATIONAL;
        String existingText1 = "";
        String existingText2 = "";
        String existingExpresswayNumber1 = "";
        String existingExpresswayNumber2 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignExpresswayEntranceAdvance10Entity entity) {
                this.expressway1 = entity.getExpressway1();
                this.expressway2 = entity.getExpressway2();
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingExpresswayNumber1 = entity.getExpresswayNumber1();
                existingExpresswayNumber2 = entity.getExpresswayNumber2();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        createExpresswayButtons(panelX + 10, panelY + 40, expressway -> this.expressway1 = expressway);
        createExpresswayButtons(panelX + 210, panelY + 40, expressway -> this.expressway2 = expressway);

        this.expresswayNumber1TextField = createTextField(panelX + 10, panelY + 80, existingExpresswayNumber1);
        this.expresswayNumber2TextField = createTextField(panelX + 205, panelY + 80, existingExpresswayNumber2);

        this.text1TextField = createTextField(panelX + 10, panelY + 110, existingText1);
        this.text2TextField = createTextField(panelX + 205, panelY + 110, existingText2);

        int buttonY = panelY + 195;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_entrance_advance_10.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_entrance_advance_10.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createExpresswayButtons(int x, int y, ExpresswayConsumer expresswayConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.national"), button -> {
                    expresswayConsumer.accept(SignExpresswayEntranceAdvance10Entity.Expressway.NATIONAL);
                }).dimensions(x, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.provincial"), button -> {
                    expresswayConsumer.accept(SignExpresswayEntranceAdvance10Entity.Expressway.PROVINCIAL);
                }).dimensions(x + 85, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
    }

    private TextFieldWidget createTextField(int x, int y, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_expressway_entrance_advance_10.content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_expressway_entrance_advance_10.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);
            String text2 = getTextSafely(this.text2TextField);
            String expresswayNumber1 = getTextSafely(this.expresswayNumber1TextField);
            String expresswayNumber2 = getTextSafely(this.expresswayNumber2TextField);

            SignExpresswayEntranceAdvance10UpdatePacket packet =
                    new SignExpresswayEntranceAdvance10UpdatePacket(pos, expressway1, expressway2
                            , expresswayNumber1, expresswayNumber2, text1, text2);
            RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), this.client.getNetworkHandler().getRegistryManager());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_10, buf);
        }
        this.close();
    }

    private String getTextSafely(TextFieldWidget textField) {
        return textField != null ? textField.getText() : "";
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ScreenRenderUtils.renderBackground(context, this.width, this.height);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_expressway_entrance_advance_10.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        renderLabel(context, panelX, panelY, "1_name", 31);
        renderLabel(context, panelX, panelY, "2_name", 31, 210);
        renderLabel(context, panelX, panelY, "expressway_number_1_name", 71);
        renderLabel(context, panelX, panelY, "expressway_number_2_name", 71, 205);
        renderLabel(context, panelX, panelY, "text_1_name", 101);
        renderLabel(context, panelX, panelY, "text_2_name", 101, 205);

        renderStatus(context, panelX, panelY, 10, 141,
                Text.translatable("text.yunbeiuc.expressway." + expressway1.getName()));
        renderStatus(context, panelX, panelY, 80, 141,
                Text.translatable("text.yunbeiuc.expressway." + expressway2.getName()));

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumber1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumber2TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderLabel(DrawContext context, int panelX, int panelY, String suffix, int yOffset) {
        renderLabel(context, panelX, panelY, suffix, yOffset, 10);
    }

    private void renderLabel(DrawContext context, int panelX, int panelY, String suffix, int yOffset, int xOffset) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_expressway_entrance_advance_10." + suffix),
                panelX + xOffset, panelY + yOffset,
                0xFFAAAAAA
        );
    }

    private void renderStatus(DrawContext context, int panelX, int panelY, int xOffset, int yOffset, Text text) {
        context.drawTextWithShadow(
                this.textRenderer,
                text,
                panelX + xOffset, panelY + yOffset,
                0xFFFFFF00
        );
    }

    private void renderTextField(TextFieldWidget textField, DrawContext context, int mouseX, int mouseY, float delta) {
        if (textField != null) {
            textField.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.close();
            return true;
        } else if (keyCode == 257 || keyCode == 335) {
            this.saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @FunctionalInterface
    private interface ExpresswayConsumer {
        void accept(SignExpresswayEntranceAdvance10Entity.Expressway expressway);
    }
}