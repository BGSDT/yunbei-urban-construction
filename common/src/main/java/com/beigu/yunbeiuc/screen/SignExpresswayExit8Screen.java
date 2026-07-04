package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayExit8Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayExit8UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayExit8Screen extends Screen {
    private final BlockPos pos;

    private SignExpresswayExit8Entity.Direction direction1;
    private SignExpresswayExit8Entity.Direction direction2;
    private SignExpresswayExit8Entity.Expressway expressway1;
    private SignExpresswayExit8Entity.Expressway expressway2;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget expresswayNumber1TextField;
    private TextFieldWidget expresswayNumber2TextField;
    private TextFieldWidget exitNumberTextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 250;
    private static final int DIRECTION_BUTTON_WIDTH = 45;
    private static final int EXPRESSWAY_BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;
    private static final int INPUT_WIDTH = 185;
    private static final int INPUT_HEIGHT = 20;

    public SignExpresswayExit8Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_expressway_exit_8.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.direction1 = SignExpresswayExit8Entity.Direction.EAST;
        this.direction2 = SignExpresswayExit8Entity.Direction.EAST;
        this.expressway1 = SignExpresswayExit8Entity.Expressway.NATIONAL;
        this.expressway2 = SignExpresswayExit8Entity.Expressway.NATIONAL;
        String existingText1 = "";
        String existingText2 = "";
        String existingExpresswayNumber1 = "";
        String existingExpresswayNumber2 = "";
        String existingExitNumber = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignExpresswayExit8Entity entity) {
                this.direction1 = entity.getDirection1();
                this.direction2 = entity.getDirection2();
                this.expressway1 = entity.getExpressway1();
                this.expressway2 = entity.getExpressway2();
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingExpresswayNumber1 = entity.getExpresswayNumber1();
                existingExpresswayNumber2 = entity.getExpresswayNumber2();
                existingExitNumber = entity.getExitNumber();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        createDirectionButtons(panelX + 10, panelY + 40, direction -> this.direction1 = direction);
        createExpresswayButtons(panelX + 210, panelY + 40, expressway -> this.expressway1 = expressway);

        createDirectionButtons(panelX + 10, panelY + 65, direction -> this.direction2 = direction);
        createExpresswayButtons(panelX + 210, panelY + 65, expressway -> this.expressway2 = expressway);

        this.expresswayNumber1TextField = createTextField(panelX + 10, panelY + 105, existingExpresswayNumber1);
        this.expresswayNumber2TextField = createTextField(panelX + 205, panelY + 105, existingExpresswayNumber2);

        this.text1TextField = createTextField(panelX + 10, panelY + 135, existingText1);
        this.text2TextField = createTextField(panelX + 205, panelY + 135, existingText2);

        this.exitNumberTextField = createTextField(panelX + 10, panelY + 165, existingExitNumber);

        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_exit_8.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_expressway_exit_8.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createDirectionButtons(int x, int y, DirectionConsumer directionConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.north"), button -> {
                    directionConsumer.accept(SignExpresswayExit8Entity.Direction.NORTH);
                }).dimensions(x, y, DIRECTION_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.south"), button -> {
                    directionConsumer.accept(SignExpresswayExit8Entity.Direction.SOUTH);
                }).dimensions(x + 48, y, DIRECTION_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.west"), button -> {
                    directionConsumer.accept(SignExpresswayExit8Entity.Direction.WEST);
                }).dimensions(x + 96, y, DIRECTION_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.east"), button -> {
                    directionConsumer.accept(SignExpresswayExit8Entity.Direction.EAST);
                }).dimensions(x + 144, y, DIRECTION_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
    }

    private void createExpresswayButtons(int x, int y, ExpresswayConsumer expresswayConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.national"), button -> {
                    expresswayConsumer.accept(SignExpresswayExit8Entity.Expressway.NATIONAL);
                }).dimensions(x, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.expressway.provincial"), button -> {
                    expresswayConsumer.accept(SignExpresswayExit8Entity.Expressway.PROVINCIAL);
                }).dimensions(x + 85, y, EXPRESSWAY_BUTTON_WIDTH, BUTTON_HEIGHT).build()
        );
    }

    private TextFieldWidget createTextField(int x, int y, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_expressway_exit_8.content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_expressway_exit_8.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);
            String text2 = getTextSafely(this.text2TextField);
            String expresswayNumber1 = getTextSafely(this.expresswayNumber1TextField);
            String expresswayNumber2 = getTextSafely(this.expresswayNumber2TextField);
            String exitNumber = getTextSafely(this.exitNumberTextField);

            SignExpresswayExit8UpdatePacket packet =
                    new SignExpresswayExit8UpdatePacket(pos, direction1, direction2, expressway1, expressway2,
                            text1, text2, expresswayNumber1, expresswayNumber2, exitNumber);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_EXPRESSWAY_EXIT_8, buf);
        }
        this.close();
    }

    private String getTextSafely(TextFieldWidget textField) {
        return textField != null ? textField.getText() : "";
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
                Text.translatable("text.yunbeiuc.sign_expressway_exit_8.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        // 渲染行标签
        renderLabel(context, panelX, panelY, "1_name", 31);
        renderLabel(context, panelX, panelY, "2_name", 56);
        renderLabel(context, panelX, panelY, "expressway_number_1_name", 96);
        renderLabel(context, panelX, panelY, "expressway_number_2_name", 96, 205);
        renderLabel(context, panelX, panelY, "text_1_name", 126);
        renderLabel(context, panelX, panelY, "text_2_name", 126, 205);
        renderLabel(context, panelX, panelY, "exit_number_name", 156);

        // 状态显示
        renderStatus(context, panelX, panelY, 10, 196,
                Text.translatable("text.yunbeiuc.direction." + direction1.getName()));
        renderStatus(context, panelX, panelY, 80, 196,
                Text.translatable("text.yunbeiuc.expressway." + expressway1.getName()));
        renderStatus(context, panelX, panelY, 170, 196,
                Text.translatable("text.yunbeiuc.direction." + direction2.getName()));
        renderStatus(context, panelX, panelY, 240, 196,
                Text.translatable("text.yunbeiuc.expressway." + expressway2.getName()));

        // 渲染所有文本框
        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumber1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.expresswayNumber2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.exitNumberTextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderLabel(DrawContext context, int panelX, int panelY, String suffix, int yOffset) {
        renderLabel(context, panelX, panelY, suffix, yOffset, 10);
    }

    private void renderLabel(DrawContext context, int panelX, int panelY, String suffix, int yOffset, int xOffset) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_expressway_exit_8." + suffix),
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
    private interface DirectionConsumer {
        void accept(SignExpresswayExit8Entity.Direction direction);
    }

    @FunctionalInterface
    private interface ExpresswayConsumer {
        void accept(SignExpresswayExit8Entity.Expressway expressway);
    }
}