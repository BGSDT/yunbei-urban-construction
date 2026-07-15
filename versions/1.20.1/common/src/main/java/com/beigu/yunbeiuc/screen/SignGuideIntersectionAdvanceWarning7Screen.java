package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning7Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning7UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning7Screen extends Screen {
    private final BlockPos pos;

    private SignGuideIntersectionAdvanceWarning7Entity.Direction direction1;
    private SignGuideIntersectionAdvanceWarning7Entity.Direction direction2;
    private SignGuideIntersectionAdvanceWarning7Entity.Direction direction3;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget text3TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 270;
    private static final int DIRECTION_BUTTON_WIDTH = 45;
    private static final int DIRECTION_BUTTON_HEIGHT = 20;
    private static final int INPUT_WIDTH = 185;
    private static final int INPUT_HEIGHT = 20;

    public SignGuideIntersectionAdvanceWarning7Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_7.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.direction1 = SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT;
        this.direction2 = SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT;
        this.direction3 = SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT;
        String existingText1 = "";
        String existingText2 = "";
        String existingText3 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionAdvanceWarning7Entity entity) {
                this.direction1 = entity.getDirection1();
                this.direction2 = entity.getDirection2();
                this.direction3 = entity.getDirection3();
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingText3 = entity.getText3();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // Line 1: Direction 1 buttons
        createDirectionButtons(panelX + 10, panelY + 40, direction -> this.direction1 = direction);

        // Line 2: Direction 2 buttons
        createDirectionButtons(panelX + 10, panelY + 65, direction -> this.direction2 = direction);

        // Line 3: Direction 3 buttons
        createDirectionButtons(panelX + 10, panelY + 90, direction -> this.direction3 = direction);

        // Text fields
        this.text1TextField = createTextField(panelX + 10, panelY + 130, existingText1);
        this.text2TextField = createTextField(panelX + 205, panelY + 130, existingText2);
        this.text3TextField = createTextField(panelX + 10, panelY + 175, existingText3);

        // 保存和取消按钮
        int buttonY = panelY + 235;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_7.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_7.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createDirectionButtons(int x, int y, DirectionConsumer directionConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.left"), button -> {
                    directionConsumer.accept(SignGuideIntersectionAdvanceWarning7Entity.Direction.LEFT);
                }).dimensions(x, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.straight"), button -> {
                    directionConsumer.accept(SignGuideIntersectionAdvanceWarning7Entity.Direction.STRAIGHT);
                }).dimensions(x + 48, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.right"), button -> {
                    directionConsumer.accept(SignGuideIntersectionAdvanceWarning7Entity.Direction.RIGHT);
                }).dimensions(x + 96, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
    }

    private TextFieldWidget createTextField(int x, int y, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_7.content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_7.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);
            String text2 = getTextSafely(this.text2TextField);
            String text3 = getTextSafely(this.text3TextField);

            SignGuideIntersectionAdvanceWarning7UpdatePacket packet =
                    new SignGuideIntersectionAdvanceWarning7UpdatePacket(pos, direction1, direction2, direction3, text1, text2, text3);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7, buf);
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
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_7.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        // 渲染方向标签
        renderDirectionLabel(context, panelX, panelY, 1, 31);
        renderDirectionLabel(context, panelX, panelY, 2, 56);
        renderDirectionLabel(context, panelX, panelY, 3, 81);

        // 渲染文本框标签
        renderTextFieldLabel(context, panelX, panelY, 1, 121, 10);
        renderTextFieldLabel(context, panelX, panelY, 2, 121, 205);
        renderTextFieldLabel(context, panelX, panelY, 3, 166, 10);

        // 状态显示
        renderDirectionStatus(context, panelX, panelY, 1, direction1);
        renderDirectionStatus(context, panelX, panelY, 2, direction2);
        renderDirectionStatus(context, panelX, panelY, 3, direction3);

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text3TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderDirectionLabel(DrawContext context, int panelX, int panelY, int index, int yOffset) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_7." + index + "_name"),
                panelX + 10, panelY + yOffset,
                0xFFAAAAAA
        );
    }

    private void renderTextFieldLabel(DrawContext context, int panelX, int panelY, int index, int yOffset, int xOffset) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_7.text_" + index + "_name"),
                panelX + xOffset, panelY + yOffset,
                0xFFAAAAAA
        );
    }

    private void renderDirectionStatus(DrawContext context, int panelX, int panelY, int index,
                                       SignGuideIntersectionAdvanceWarning7Entity.Direction direction) {
        int xOffset = 10 + (index - 1) * 50;
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.direction." + direction.getName()),
                panelX + xOffset, panelY + 210,
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
        void accept(SignGuideIntersectionAdvanceWarning7Entity.Direction direction);
    }
}