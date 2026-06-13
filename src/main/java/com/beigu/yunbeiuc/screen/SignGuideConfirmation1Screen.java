package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideConfirmation1Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideConfirmation1UpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideConfirmation1Screen extends Screen {
    private final BlockPos pos;

    private SignGuideConfirmation1Entity.Unit unit1;
    private SignGuideConfirmation1Entity.Unit unit2;
    private SignGuideConfirmation1Entity.Unit unit3;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget text3TextField;
    private TextFieldWidget length1TextField;
    private TextFieldWidget length2TextField;
    private TextFieldWidget length3TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 245;

    public SignGuideConfirmation1Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.unit1 = SignGuideConfirmation1Entity.Unit.METRE;
        this.unit2 = SignGuideConfirmation1Entity.Unit.METRE;
        this.unit3 = SignGuideConfirmation1Entity.Unit.METRE;
        String existingText1 = "";
        String existingText2 = "";
        String existingText3 = "";
        String existingLength1 = "";
        String existingLength2 = "";
        String existingLength3 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideConfirmation1Entity entity) {
                this.unit1 = entity.getUnit1();
                this.unit2 = entity.getUnit2();
                this.unit3 = entity.getUnit3();
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

        // 第一行：text1输入框 + length1输入框 + 单位1选择
        this.text1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 40,
                130, 20,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.text_1")
        );
        this.text1TextField.setMaxLength(256);
        this.text1TextField.setText(existingText1);
        this.text1TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.placeholder"));
        this.addSelectableChild(this.text1TextField);

        this.length1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 150, panelY + 40,
                70, 20,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.length_1")
        );
        this.length1TextField.setMaxLength(256);
        this.length1TextField.setText(existingLength1);
        this.length1TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.placeholder"));
        this.addSelectableChild(this.length1TextField);

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.metre"), button -> {
                    unit1 = SignGuideConfirmation1Entity.Unit.METRE;
                }).dimensions(panelX + 230, panelY + 40, 45, 20).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.kilometre"), button -> {
                    unit1 = SignGuideConfirmation1Entity.Unit.KILOMETRE;
                }).dimensions(panelX + 280, panelY + 40, 55, 20).build()
        );

        // 第二行：text2输入框 + length2输入框 + 单位2选择
        this.text2TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 80,
                130, 20,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.text_2")
        );
        this.text2TextField.setMaxLength(256);
        this.text2TextField.setText(existingText2);
        this.text2TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.placeholder"));
        this.addSelectableChild(this.text2TextField);

        this.length2TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 150, panelY + 80,
                70, 20,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.length_2")
        );
        this.length2TextField.setMaxLength(256);
        this.length2TextField.setText(existingLength2);
        this.length2TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.placeholder"));
        this.addSelectableChild(this.length2TextField);

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.metre"), button -> {
                    unit2 = SignGuideConfirmation1Entity.Unit.METRE;
                }).dimensions(panelX + 230, panelY + 80, 45, 20).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.kilometre"), button -> {
                    unit2 = SignGuideConfirmation1Entity.Unit.KILOMETRE;
                }).dimensions(panelX + 280, panelY + 80, 55, 20).build()
        );

        // 第三行：text3输入框 + length3输入框 + 单位3选择
        this.text3TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 120,
                130, 20,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.text_3")
        );
        this.text3TextField.setMaxLength(256);
        this.text3TextField.setText(existingText3);
        this.text3TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.placeholder"));
        this.addSelectableChild(this.text3TextField);

        this.length3TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 150, panelY + 120,
                70, 20,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.length_3")
        );
        this.length3TextField.setMaxLength(256);
        this.length3TextField.setText(existingLength3);
        this.length3TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.placeholder"));
        this.addSelectableChild(this.length3TextField);

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.metre"), button -> {
                    unit3 = SignGuideConfirmation1Entity.Unit.METRE;
                }).dimensions(panelX + 230, panelY + 120, 45, 20).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.kilometre"), button -> {
                    unit3 = SignGuideConfirmation1Entity.Unit.KILOMETRE;
                }).dimensions(panelX + 280, panelY + 120, 55, 20).build()
        );

        // 保存和取消按钮
        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField.getText();
            String text2 = this.text2TextField.getText();
            String text3 = this.text3TextField.getText();
            String length1 = this.length1TextField.getText();
            String length2 = this.length2TextField.getText();
            String length3 = this.length3TextField.getText();

            SignGuideConfirmation1UpdatePacket packet =
                    new SignGuideConfirmation1UpdatePacket(pos, unit1, unit2, unit3, text1, text2, text3, length1, length2, length3);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_SIGN_GUIDE_CONFIRMATION_1, buf);
        }
        this.close();
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
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        // 第一行标签和状态
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.1_name"),
                panelX + 10, panelY + 28,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.unit." + unit1.getName()),
                panelX + 340, panelY + 42,
                0xFFFFFF00
        );

        // 第二行标签和状态
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.2_name"),
                panelX + 10, panelY + 68,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.unit." + unit2.getName()),
                panelX + 340, panelY + 82,
                0xFFFFFF00
        );

        // 第三行标签和状态
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_confirmation_1.3_name"),
                panelX + 10, panelY + 108,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.unit." + unit3.getName()),
                panelX + 340, panelY + 122,
                0xFFFFFF00
        );

        this.text1TextField.render(context, mouseX, mouseY, delta);
        this.text2TextField.render(context, mouseX, mouseY, delta);
        this.text3TextField.render(context, mouseX, mouseY, delta);
        this.length1TextField.render(context, mouseX, mouseY, delta);
        this.length2TextField.render(context, mouseX, mouseY, delta);
        this.length3TextField.render(context, mouseX, mouseY, delta);

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