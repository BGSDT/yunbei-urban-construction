package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionWarning4UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class SignGuideIntersectionWarning4Screen extends AbstractSignFormScreen {
    private SignGuideIntersectionWarning4Entity.Direction direction1;
    private TextFieldWidget text1TextField;

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 270;
    private static final int DIRECTION_BUTTON_WIDTH = 45;
    private static final int DIRECTION_BUTTON_HEIGHT = 20;
    private static final int INPUT_WIDTH = 185;
    private static final int INPUT_HEIGHT = 20;

    public SignGuideIntersectionWarning4Screen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_guide_intersection_warning_4");
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
        this.text1TextField = createTextField(panelX + 10, panelY + 80, INPUT_WIDTH, INPUT_HEIGHT, existingText1);

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

    private void createDirectionButtons(int x, int y, Consumer<SignGuideIntersectionWarning4Entity.Direction> directionConsumer) {
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

    @Override
    protected void saveAndClose() {
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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        renderPanelBackground(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        // 渲染标签
        renderLabel(context, panelX, panelY, "1_name", 31);
        renderLabel(context, panelX, panelY, "text_1_name", 71);

        // 状态显示
        renderDirectionStatus(context, panelX, panelY, direction1);

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
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
}
