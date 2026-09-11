package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning6Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning6UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class SignGuideIntersectionAdvanceWarning6Screen extends AbstractSignFormScreen {
    private SignGuideIntersectionAdvanceWarning6Entity.Direction direction1;
    private SignGuideIntersectionAdvanceWarning6Entity.Direction direction2;
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 270;
    private static final int DIRECTION_BUTTON_WIDTH = 45;
    private static final int DIRECTION_BUTTON_HEIGHT = 20;
    private static final int INPUT_WIDTH = 185;
    private static final int INPUT_HEIGHT = 20;

    public SignGuideIntersectionAdvanceWarning6Screen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_guide_intersection_advance_warning_6");
    }

    @Override
    protected void init() {
        super.init();

        this.direction1 = SignGuideIntersectionAdvanceWarning6Entity.Direction.STRAIGHT;
        this.direction2 = SignGuideIntersectionAdvanceWarning6Entity.Direction.STRAIGHT;
        String existingText1 = "";
        String existingText2 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionAdvanceWarning6Entity entity) {
                this.direction1 = entity.getDirection1();
                this.direction2 = entity.getDirection2();
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // Line 1: Direction 1 buttons
        createDirectionButtons(panelX + 10, panelY + 40, direction -> this.direction1 = direction);

        // Line 2: Direction 2 buttons
        createDirectionButtons(panelX + 10, panelY + 65, direction -> this.direction2 = direction);

        // Line 3: text1 + text2
        this.text1TextField = createTextField(panelX + 10, panelY + 105, INPUT_WIDTH, INPUT_HEIGHT, existingText1);
        this.text2TextField = createTextField(panelX + 205, panelY + 105, INPUT_WIDTH, INPUT_HEIGHT, existingText2);

        // 保存和取消按钮
        int buttonY = panelY + 235;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_6.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_6.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private void createDirectionButtons(int x, int y, Consumer<SignGuideIntersectionAdvanceWarning6Entity.Direction> directionConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.left_turn"), button -> {
                    directionConsumer.accept(SignGuideIntersectionAdvanceWarning6Entity.Direction.LEFT);
                }).dimensions(x, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.straight"), button -> {
                    directionConsumer.accept(SignGuideIntersectionAdvanceWarning6Entity.Direction.STRAIGHT);
                }).dimensions(x + 48, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.direction.right_turn"), button -> {
                    directionConsumer.accept(SignGuideIntersectionAdvanceWarning6Entity.Direction.RIGHT);
                }).dimensions(x + 96, y, DIRECTION_BUTTON_WIDTH, DIRECTION_BUTTON_HEIGHT).build()
        );
    }

    @Override
    protected void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = getTextSafely(this.text1TextField);
            String text2 = getTextSafely(this.text2TextField);

            SignGuideIntersectionAdvanceWarning6UpdatePacket packet =
                    new SignGuideIntersectionAdvanceWarning6UpdatePacket(pos, direction1, direction2, text1, text2);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6, buf);
        }
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        renderPanelBackground(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_6.1_name"),
                panelX + 10, panelY + 31,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_6.2_name"),
                panelX + 10, panelY + 56,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_6.text_1_name"),
                panelX + 10, panelY + 96,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_6.text_2_name"),
                panelX + 205, panelY + 96,
                0xFFAAAAAA
        );

        // 状态显示
        renderDirectionStatus(context, panelX, panelY, 1, direction1);
        renderDirectionStatus(context, panelX, panelY, 2, direction2);

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderDirectionStatus(DrawContext context, int panelX, int panelY, int index,
                                       SignGuideIntersectionAdvanceWarning6Entity.Direction direction) {
        int xOffset = (index == 1) ? 10 : 170;
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.direction." + direction.getName()),
                panelX + xOffset, panelY + 150,
                0xFFFFFF00
        );
    }
}
