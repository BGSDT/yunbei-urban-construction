package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignExpresswayExit8Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignExpresswayExit8UpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
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
    private TextFieldWidget exitNumberTextField;  // 新增

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 270;

    public SignExpresswayExit8Screen(BlockPos pos) {
        super(new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.title"));
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
        String existingExitNumber = "";  // 新增

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
                existingExitNumber = entity.getExitNumber();  // 新增
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // Line 1: Direction + Expressway buttons
        // Direction 1 buttons (north/south/west/east)
        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.north"), button -> direction1 = SignExpresswayExit8Entity.Direction.NORTH)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 58, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.south"), button -> direction1 = SignExpresswayExit8Entity.Direction.SOUTH)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 106, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.west"), button -> direction1 = SignExpresswayExit8Entity.Direction.WEST)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 154, panelY + 40, 45, 20, new TranslatableText("text.yunbeiuc.direction.east"), button -> direction1 = SignExpresswayExit8Entity.Direction.EAST)
        );

        // Expressway 1 buttons (national/provincial)
        this.addDrawableChild(
                new ButtonWidget(panelX + 210, panelY + 40, 80, 20, new TranslatableText("text.yunbeiuc.expressway.national"), button -> expressway1 = SignExpresswayExit8Entity.Expressway.NATIONAL)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 295, panelY + 40, 80, 20, new TranslatableText("text.yunbeiuc.expressway.provincial"), button -> expressway1 = SignExpresswayExit8Entity.Expressway.PROVINCIAL)
        );

        // Line 2: Direction + Expressway buttons
        // Direction 2 buttons
        this.addDrawableChild(
                new ButtonWidget(panelX + 10, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.north"), button -> direction2 = SignExpresswayExit8Entity.Direction.NORTH)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 58, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.south"), button -> direction2 = SignExpresswayExit8Entity.Direction.SOUTH)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 106, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.west"), button -> direction2 = SignExpresswayExit8Entity.Direction.WEST)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 154, panelY + 65, 45, 20, new TranslatableText("text.yunbeiuc.direction.east"), button -> direction2 = SignExpresswayExit8Entity.Direction.EAST)
        );

        // Expressway 2 buttons
        this.addDrawableChild(
                new ButtonWidget(panelX + 210, panelY + 65, 80, 20, new TranslatableText("text.yunbeiuc.expressway.national"), button -> expressway2 = SignExpresswayExit8Entity.Expressway.NATIONAL)
        );
        this.addDrawableChild(
                new ButtonWidget(panelX + 295, panelY + 65, 80, 20, new TranslatableText("text.yunbeiuc.expressway.provincial"), button -> expressway2 = SignExpresswayExit8Entity.Expressway.PROVINCIAL)
        );

        // Line 3: expresswayNumber1 + expresswayNumber2 (一行两个)
        this.expresswayNumber1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 105,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.content")
        );
        this.expresswayNumber1TextField.setMaxLength(256);
        this.expresswayNumber1TextField.setText(existingExpresswayNumber1);
        this.addSelectableChild(this.expresswayNumber1TextField);

        this.expresswayNumber2TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 205, panelY + 105,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.content")
        );
        this.expresswayNumber2TextField.setMaxLength(256);
        this.expresswayNumber2TextField.setText(existingExpresswayNumber2);
        this.addSelectableChild(this.expresswayNumber2TextField);

        // Line 4: text1 + text2 (一行两个)
        this.text1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 135,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.content")
        );
        this.text1TextField.setMaxLength(256);
        this.text1TextField.setText(existingText1);
        this.addSelectableChild(this.text1TextField);

        this.text2TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 205, panelY + 135,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.content")
        );
        this.text2TextField.setMaxLength(256);
        this.text2TextField.setText(existingText2);
        this.addSelectableChild(this.text2TextField);

        // Line 5: exitNumber (新增)
        this.exitNumberTextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 165,
                185, 20,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.content")
        );
        this.exitNumberTextField.setMaxLength(256);
        this.exitNumberTextField.setText(existingExitNumber);
        this.addSelectableChild(this.exitNumberTextField);

        int buttonY = panelY + 235;
        this.addDrawableChild(
                new ButtonWidget(panelX + 100, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.save"), button -> this.saveAndClose())
        );

        this.addDrawableChild(
                new ButtonWidget(panelX + 210, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.cancel"), button -> this.close())
        );
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField.getText();
            String text2 = this.text2TextField.getText();
            String expresswayNumber1 = this.expresswayNumber1TextField.getText();
            String expresswayNumber2 = this.expresswayNumber2TextField.getText();
            String exitNumber = this.exitNumberTextField.getText();  // 新增

            SignExpresswayExit8UpdatePacket packet =
                    new SignExpresswayExit8UpdatePacket(pos, direction1, direction2, expressway1, expressway2, text1, text2, expresswayNumber1, expresswayNumber2, exitNumber);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_SIGN_EXPRESSWAY_EXIT_8, buf);
        }
        this.close();
    }

    @Override
    public void render(MatrixStack context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        DrawableHelper.fill(context, panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);
        DrawableHelper.fill(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        DrawableHelper.drawCenteredText(
                context, 
                this.textRenderer,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.1_name"),
                panelX + 10, panelY + 31,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.2_name"),
                panelX + 10, panelY + 56,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.expressway_number_1_name"),
                panelX + 10, panelY + 96,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.expressway_number_2_name"),
                panelX + 205, panelY + 96,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.text_1_name"),
                panelX + 10, panelY + 126,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.text_2_name"),
                panelX + 205, panelY + 126,
                0xFFAAAAAA
        );

        // 新增 exitNumber 标签
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.sign_expressway_exit_8.exit_number_name"),
                panelX + 10, panelY + 156,
                0xFFAAAAAA
        );

        // 调整了Y坐标，将状态显示下移
        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction1.getName()),
                panelX + 10, panelY + 195,
                0xFFFFFF00
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.expressway." + expressway1.getName()),
                panelX + 80, panelY + 195,
                0xFFFFFF00
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.direction." + direction2.getName()),
                panelX + 170, panelY + 195,
                0xFFFFFF00
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.expressway." + expressway2.getName()),
                panelX + 240, panelY + 195,
                0xFFFFFF00
        );

        this.text1TextField.render(context, mouseX, mouseY, delta);
        this.text2TextField.render(context, mouseX, mouseY, delta);
        this.expresswayNumber1TextField.render(context, mouseX, mouseY, delta);
        this.expresswayNumber2TextField.render(context, mouseX, mouseY, delta);
        this.exitNumberTextField.render(context, mouseX, mouseY, delta);  // 新增

        super.render(context, mouseX, mouseY, delta);
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
}