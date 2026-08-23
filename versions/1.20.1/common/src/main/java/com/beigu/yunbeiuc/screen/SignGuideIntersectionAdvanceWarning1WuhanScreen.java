package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning1WuhanScreen extends AbstractSignFormScreen {
    private TextFieldWidget text1TextField;
    private TextFieldWidget text2TextField;
    private TextFieldWidget cnText3TextField;
    private TextFieldWidget enText3TextField;
    private TextFieldWidget cnText4TextField;
    private TextFieldWidget enText4TextField;
    private TextFieldWidget cnText5TextField;
    private TextFieldWidget enText5TextField;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 245;
    private static final int INPUT_WIDTH = 150;
    private static final int INPUT_HEIGHT = 24;

    public SignGuideIntersectionAdvanceWarning1WuhanScreen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan");
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";
        String existingText2 = "";
        String existingCnText3 = "";
        String existingEnText3 = "";
        String existingCnText4 = "";
        String existingEnText4 = "";
        String existingCnText5 = "";
        String existingEnText5 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideIntersectionAdvanceWarning1WuhanEntity entity) {
                existingText1 = entity.getText1();
                existingText2 = entity.getText2();
                existingCnText3 = entity.getCnText3();
                existingEnText3 = entity.getEnText3();
                existingCnText4 = entity.getCnText4();
                existingEnText4 = entity.getEnText4();
                existingCnText5 = entity.getCnText5();
                existingEnText5 = entity.getEnText5();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.text1TextField = createTextField(panelX + 10, panelY + 40, INPUT_WIDTH, INPUT_HEIGHT, existingText1);
        this.text2TextField = createTextField(panelX + 170, panelY + 40, INPUT_WIDTH, INPUT_HEIGHT, existingText2);
        this.cnText3TextField = createTextField(panelX + 10, panelY + 85, INPUT_WIDTH, INPUT_HEIGHT, existingCnText3);
        this.enText3TextField = createTextField(panelX + 170, panelY + 85, INPUT_WIDTH, INPUT_HEIGHT, existingEnText3);
        this.cnText4TextField = createTextField(panelX + 10, panelY + 130, INPUT_WIDTH, INPUT_HEIGHT, existingCnText4);
        this.enText4TextField = createTextField(panelX + 170, panelY + 130, INPUT_WIDTH, INPUT_HEIGHT, existingEnText4);
        this.cnText5TextField = createTextField(panelX + 10, panelY + 175, INPUT_WIDTH, INPUT_HEIGHT, existingCnText5);
        this.enText5TextField = createTextField(panelX + 170, panelY + 175, INPUT_WIDTH, INPUT_HEIGHT, existingEnText5);

        int buttonY = panelY + 215;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 60, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.cancel"), button -> this.close())
                        .dimensions(panelX + 170, buttonY, 90, 24)
                        .build()
        );

        this.setFocused(this.text1TextField);
    }

    @Override
    protected void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField != null ? this.text1TextField.getText() : "";
            String text2 = this.text2TextField != null ? this.text2TextField.getText() : "";
            String cnText3 = this.cnText3TextField != null ? this.cnText3TextField.getText() : "";
            String enText3 = this.enText3TextField != null ? this.enText3TextField.getText() : "";
            String cnText4 = this.cnText4TextField != null ? this.cnText4TextField.getText() : "";
            String enText4 = this.enText4TextField != null ? this.enText4TextField.getText() : "";
            String cnText5 = this.cnText5TextField != null ? this.cnText5TextField.getText() : "";
            String enText5 = this.enText5TextField != null ? this.enText5TextField.getText() : "";

            SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket packet = new SignGuideIntersectionAdvanceWarning1WuhanUpdatePacket(pos, text1, text2, cnText3, enText3, cnText4, enText4, cnText5, enText5);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN, buf);
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
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.text_1_name"),
                panelX + 10, panelY + 31,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.text_2_name"),
                panelX + 170, panelY + 31,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.cn_text_3_name"),
                panelX + 10, panelY + 76,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.en_text_3_name"),
                panelX + 170, panelY + 76,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.cn_text_4_name"),
                panelX + 10, panelY + 121,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.en_text_4_name"),
                panelX + 170, panelY + 121,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.cn_text_5_name"),
                panelX + 10, panelY + 166,
                0xFFAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning_1_wuhan.en_text_5_name"),
                panelX + 170, panelY + 166,
                0xFFAAAAAA
        );

        renderTextField(this.text1TextField, context, mouseX, mouseY, delta);
        renderTextField(this.text2TextField, context, mouseX, mouseY, delta);
        renderTextField(this.cnText3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.enText3TextField, context, mouseX, mouseY, delta);
        renderTextField(this.cnText4TextField, context, mouseX, mouseY, delta);
        renderTextField(this.enText4TextField, context, mouseX, mouseY, delta);
        renderTextField(this.cnText5TextField, context, mouseX, mouseY, delta);
        renderTextField(this.enText5TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }
}