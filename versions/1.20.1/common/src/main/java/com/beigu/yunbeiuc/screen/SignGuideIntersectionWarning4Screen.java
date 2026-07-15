package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionWarning4UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionWarning4Screen extends Screen {
    private final BlockPos pos;

    private SignGuideIntersectionWarning4Entity.Direction direction1;
    private TextFieldWidget text1TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 270;
    private static final int DIRECTION_BUTTON_WIDTH = 45;
    private static final int DIRECTION_BUTTON_HEIGHT = 20;
    private static final int INPUT_WIDTH = 185;
    private static final int INPUT_HEIGHT = 20;

    public SignGuideIntersectionWarning4Screen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.sign_guide_intersection_warning_4.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.direction1 = SignGuideIntersectionWarning4Entity.Direction.STRAIGHT;
        String existingText1 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionWarning4Entity entity) {
                this.direction1 = entity.getDirection1();
                existingText1 = entity.getText1();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // Direction 1 buttons
        createDirectionButtons(panelX + 10, panelY + 40, direction -> this.direction1 = direction);

        // Text field
        this.text1TextField = createTextField(panelX + 10, panelY + 80, existingText1);

        // 保存和取消按钮
        int buttonY = panelY + 235;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_warning_4.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_warning_4.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createDirectionButtons(int x, int y, DirectionConsumer directionConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.left"), button -> {
                    directionConsumer.accept(SignGuideIntersectionWarning4Entity.Direction.LEFT);
                }).dimensions(x, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.straight"), button -> {
                    directionConsumer.accept(SignGuideIntersectionWarning4Entity.Direction.STRAIGHT);
                }).dimensions(x + 48, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.right"), button -> {
                    directionConsumer.accept(SignGuideIntersectionWarning4Entity.Direction.RIGHT);
                }).dimensions(x + 96, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
    }

    private TextFieldWidget createTextField(int x, int y, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_warning_4.content")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_intersection_warning_4.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);

            SignGuideIntersectionWarning4UpdatePacket packet =
                    new SignGuideIntersectionWarning4UpdatePacket(pos, direction1, text1);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_WARNING_4, buf);
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
                Text.translatable("text.yunbeiuc.sign_guide_intersection_warning_4.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );

        // 渲染标签
        renderLabel(context, panelX, panelY, "1_name", 31);
        renderLabel(context, panelX, panelY, "text_1_name", 71);

        // 状态显示
        renderDirectionStatus(context, panelX, panelY, direction1);

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderLabel(DrawContext context, int panelX, int panelY, String suffix, int yOffset) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_warning_4." + suffix),
                panelX + 10, panelY + yOffset,
                0xFFAAAAAA
        );
    }

    private void renderDirectionStatus(DrawContext context, int panelX, int panelY,
                                       SignGuideIntersectionWarning4Entity.Direction direction) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.direction." + direction.getName()),
                panelX + 10, panelY + 125,
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
        void accept(SignGuideIntersectionWarning4Entity.Direction direction);
    }
}