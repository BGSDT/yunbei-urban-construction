package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.RoadNameSignBlockUpdatePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class RoadNameSignScreen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget chineseNameTextField;
    private TextFieldWidget englishNameTextField;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 195;

    public RoadNameSignScreen(BlockPos pos) {
        super(new TranslatableText("text.yunbeiuc.road_name_sign.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        String existingChineseName = "";
        String existingEnglishName = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof RoadNameSignBlockEntity entity) {
                existingChineseName = entity.getChineseText();
                existingEnglishName = entity.getEnglishText();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        this.chineseNameTextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 25,
                300, 24,
                new TranslatableText("text.yunbeiuc.road_name_sign.content")
        );
        this.chineseNameTextField.setMaxLength(256);
        this.chineseNameTextField.setText(existingChineseName);
        this.addSelectableChild(this.chineseNameTextField);

        this.englishNameTextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 70,
                300, 24,
                new TranslatableText("text.yunbeiuc.road_name_sign.content")
        );
        this.englishNameTextField.setMaxLength(256);
        this.englishNameTextField.setText(existingEnglishName);
        this.addSelectableChild(this.englishNameTextField);

        int buttonY = panelY + 160;
        this.addDrawableChild(
                new ButtonWidget(panelX + 60, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.road_name_sign.save"), button -> this.saveAndClose())
        );

        this.addDrawableChild(
                new ButtonWidget(panelX + 170, buttonY, 90, 24, new TranslatableText("text.yunbeiuc.road_name_sign.cancel"), button -> this.close())
        );

        this.setFocused(this.chineseNameTextField);
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String chineseText = this.chineseNameTextField.getText();
            String englishText = this.englishNameTextField.getText();

            RoadNameSignBlockUpdatePacket packet = new RoadNameSignBlockUpdatePacket(pos, chineseText, englishText);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_ROAD_NAME_SIGN, buf);
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
                new TranslatableText("text.yunbeiuc.road_name_sign.title"),
                panelX + PANEL_WIDTH / 2, panelY + 12,
                0xFFCCCCCC
        );textRenderer.drawWithShadow(context, 
                new TranslatableText("text.yunbeiuc.road_name_sign.chinese_name"),
                panelX + 10, panelY + 16,
                0xFFAAAAAA
        );

        DrawableHelper.drawTextWithShadow(
                context, 
                textRenderer,
                new TranslatableText("text.yunbeiuc.road_name_sign.english_name"),
                panelX + 10, panelY + 61,
                0xFFAAAAAA
        );

        this.chineseNameTextField.render(context, mouseX, mouseY, delta);
        this.englishNameTextField.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC键
            this.close();
            return true;
        } else if (keyCode == 257 || keyCode == 335) { // 回车键或小键盘回车
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