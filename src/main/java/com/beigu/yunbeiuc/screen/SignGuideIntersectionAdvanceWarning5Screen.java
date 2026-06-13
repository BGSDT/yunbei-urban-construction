package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning5Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning5UpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning5Screen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget text3TextField;
    private TextFieldWidget text4TextField;

    private float text1AndY = 0f;
    private float text2AndY = 0f;
    private float text3AndY = 0f;
    private float text4AndY = 0f;

    private ValueAdjustButton text1YButton;
    private ValueAdjustButton text2YButton;
    private ValueAdjustButton text3YButton;
    private ValueAdjustButton text4YButton;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 245;

    public SignGuideIntersectionAdvanceWarning5Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";
        String existingText2 = "";
        String existingText3 = "";
        String existingText4 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionAdvanceWarning5Entity entity) {
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingText3 = entity.getText3();
                existingText4 = entity.getText4();
                text1AndY = entity.getText1AndY();
                text2AndY = entity.getText2AndY();
                text3AndY = entity.getText3AndY();
                text4AndY = entity.getText4AndY();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.text1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 40,
                200, 24,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.content")
        );
        this.text1TextField.setMaxLength(256);
        this.text1TextField.setText(existingText1);
        this.text1TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.placeholder"));
        this.addSelectableChild(this.text1TextField);

        this.text2TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 85,
                200, 24,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.content")
        );
        this.text2TextField.setMaxLength(256);
        this.text2TextField.setText(existingText2);
        this.text2TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.placeholder"));
        this.addSelectableChild(this.text2TextField);

        this.text3TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 130,
                200, 24,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.content")
        );
        this.text3TextField.setMaxLength(256);
        this.text3TextField.setText(existingText3);
        this.text3TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.placeholder"));
        this.addSelectableChild(this.text3TextField);

        this.text4TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 175,
                200, 24,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.content")
        );
        this.text4TextField.setMaxLength(256);
        this.text4TextField.setText(existingText4);
        this.text4TextField.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.placeholder"));
        this.addSelectableChild(this.text4TextField);

        this.text1YButton = this.addDrawableChild(
                new ValueAdjustButton(
                        panelX + 220, panelY + 40,
                        24, 24,
                        text1AndY, "Y",
                        button -> {},
                        (button, isLeftClick) -> {
                            if (isLeftClick) {
                                text1AndY += 1.0f;
                            } else {
                                text1AndY -= 1.0f;
                            }
                            ((ValueAdjustButton) button).setValue(text1AndY);
                        }
                )
        );

        this.text2YButton = this.addDrawableChild(
                new ValueAdjustButton(
                        panelX + 220, panelY + 85,
                        24, 24,
                        text2AndY, "Y",
                        button -> {},
                        (button, isLeftClick) -> {
                            if (isLeftClick) {
                                text2AndY += 1.0f;
                            } else {
                                text2AndY -= 1.0f;
                            }
                            ((ValueAdjustButton) button).setValue(text2AndY);
                        }
                )
        );

        this.text3YButton = this.addDrawableChild(
                new ValueAdjustButton(
                        panelX + 220, panelY + 130,
                        24, 24,
                        text3AndY, "Y",
                        button -> {},
                        (button, isLeftClick) -> {
                            if (isLeftClick) {
                                text3AndY += 1.0f;
                            } else {
                                text3AndY -= 1.0f;
                            }
                            ((ValueAdjustButton) button).setValue(text3AndY);
                        }
                )
        );

        this.text4YButton = this.addDrawableChild(
                new ValueAdjustButton(
                        panelX + 220, panelY + 175,
                        24, 24,
                        text4AndY, "Y",
                        button -> {},
                        (button, isLeftClick) -> {
                            if (isLeftClick) {
                                text4AndY += 1.0f;
                            } else {
                                text4AndY -= 1.0f;
                            }
                            ((ValueAdjustButton) button).setValue(text4AndY);
                        }
                )
        );

        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 60, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.cancel"), button -> this.close())
                        .dimensions(panelX + 170, buttonY, 90, 24)
                        .build()
        );

        this.setFocused(this.text1TextField);
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField.getText();
            String text2 = this.text2TextField.getText();
            String text3 = this.text3TextField.getText();
            String text4 = this.text4TextField.getText();

            SignGuideIntersectionAdvanceWarning5UpdatePacket packet =
                    new SignGuideIntersectionAdvanceWarning5UpdatePacket(pos, text1, text2, text3, text4, text1AndY, text2AndY, text3AndY, text4AndY);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5, buf);
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
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.text_1_name"),
                panelX + 10, panelY + 31,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.text_2_name"),
                panelX + 10, panelY + 76,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.text_3_name"),
                panelX + 10, panelY + 121,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.text_4_name"),
                panelX + 10, panelY + 166,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.y_value", String.format("%.1f", text1AndY)),
                panelX + 250, panelY + 48,
                0xFFFFFF00
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.y_value", String.format("%.1f", text2AndY)),
                panelX + 250, panelY + 93,
                0xFFFFFF00
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.y_value", String.format("%.1f", text3AndY)),
                panelX + 250, panelY + 138,
                0xFFFFFF00
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_5.y_value", String.format("%.1f", text4AndY)),
                panelX + 250, panelY + 183,
                0xFFFFFF00
        );

        this.text1TextField.render(context, mouseX, mouseY, delta);
        this.text2TextField.render(context, mouseX, mouseY, delta);
        this.text3TextField.render(context, mouseX, mouseY, delta);
        this.text4TextField.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var child : this.children()) {
            if (child instanceof ValueAdjustButton valueButton && child.isMouseOver(mouseX, mouseY)) {
                if (button == 1) {
                    valueButton.onClick(false);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
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