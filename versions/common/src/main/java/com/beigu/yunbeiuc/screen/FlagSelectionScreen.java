package com.beigu.yunbeiuc.screen;
import com.beigu.yunbeiuc.api.network.NetworkCompat;

import com.beigu.yunbeiuc.api.text.Text;

import com.beigu.yunbeiuc.util.CustomFlag;
import com.beigu.yunbeiuc.util.FlagLoader;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.FlagUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FlagSelectionScreen extends Screen {
    private final List<FlagOption> options;
    private FlagListWidget listWidget;
    private CustomFlag selectedFlag;
    private final BlockPos blockPos;

    public FlagSelectionScreen(Component title, BlockPos blockPos) {
        super(title);
        this.blockPos = blockPos;
        this.options = createFlagOptions();

        if (!options.isEmpty()) {
            this.selectedFlag = options.get(0).getFlag();
        }
    }

    @Override
    protected void init() {
        super.init();

        int listWidth = this.width / 2;
        this.listWidget = new FlagListWidget(
                this.client,
                listWidth,
                this.height,
                40,
                this.height - 60,
                20,
                this.options,
                option -> setSelectedFlag(option.getFlag())
        );

        this.addDrawableChild(this.listWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context);

        super.render(context, mouseX, mouseY, delta);

        int listAreaWidth = this.width / 2;

        int listAreaLeft = 0;

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.flag_selection.title"),
                listAreaLeft + listAreaWidth / 2,
                10,
                0xFFFFFF
        );

        if (selectedFlag != null) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.flag_selection.current_selection", selectedFlag.getName()),
                    listAreaLeft + 10,
                    this.height - 55,
                    0xFFFFFF
            );
        }

        drawPreviewPanel(context);
    }

    private void drawPreviewPanel(DrawContext context) {
        int panelWidth = 200;
        int panelHeight = 230;

        int panelX = this.width / 2 + (this.width / 2 - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA333333);

        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.flag_selection.preview_panel"),
                panelX + panelWidth / 2,
                panelY + 10,
                0xFFFFFF
        );

        if (selectedFlag != null) {
            int previewSize = 100;
            int[] scaledDimensions = getScaledDimensions(previewSize);
            int scaledWidth = scaledDimensions[0];
            int scaledHeight = scaledDimensions[1];

            int previewX = panelX + (panelWidth - scaledWidth) / 2;
            int previewY = panelY + 35;

            context.fill(previewX - 2, previewY - 2, previewX + scaledWidth + 2, previewY + scaledHeight + 2, 0xFF000000);
            context.drawBorder(previewX - 2, previewY - 2, scaledWidth + 4, scaledHeight + 4, 0xFFFFFFFF);

            drawFlagPreview(context, previewX, previewY, previewSize);

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal(selectedFlag.getName()),
                    panelX + panelWidth / 2,
                    previewY + scaledHeight + 10,
                    0xFFFFFF
            );

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal("ID: " + selectedFlag.getId()),
                    panelX + panelWidth / 2,
                    previewY + scaledHeight + 25,
                    0xAAAAAA
            );

            int saveButtonWidth = 80;
            int saveButtonHeight = 20;
            int saveButtonX = panelX + (panelWidth - saveButtonWidth) / 2;
            int saveButtonY = panelY + panelHeight - 40;

            context.fill(saveButtonX, saveButtonY, saveButtonX + saveButtonWidth, saveButtonY + saveButtonHeight, 0xFF555555);
            context.drawBorder(saveButtonX, saveButtonY, saveButtonWidth, saveButtonHeight, 0xFFFFFFFF);

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.flag_selection.save_button"),
                    saveButtonX + saveButtonWidth / 2,
                    saveButtonY + 6,
                    0xFFFFFF
            );
        }
    }

    private int[] getScaledDimensions(int maxSize) {
        int originalWidth = 3543;
        int originalHeight = 9449;

        float widthRatio = (float) maxSize / originalWidth;
        float heightRatio = (float) maxSize / originalHeight;
        float scale = Math.min(widthRatio, heightRatio);

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        return new int[]{scaledWidth, scaledHeight};
    }

    private void drawFlagPreview(DrawContext context, int x, int y, int maxSize) {
        if (selectedFlag == null) return;

        try {
            ResourceLocation texture = selectedFlag.getTexture();

            int originalWidth = 1062;
            int originalHeight = 2834;

            float widthRatio = (float) maxSize / originalWidth;
            float heightRatio = (float) maxSize / originalHeight;
            float scale = Math.min(widthRatio, heightRatio);

            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            int centeredX = x;
            int centeredY = y;

            context.drawTexture(texture, centeredX, centeredY, scaledWidth, scaledHeight, 0, 0, originalWidth, originalHeight, originalWidth, originalHeight);

        } catch (Exception e) {
            int[] scaledDimensions = getScaledDimensions(maxSize);
            context.fill(x, y, x + scaledDimensions[0], y + scaledDimensions[1], 0x44FF0000);
            context.drawBorder(x, y, scaledDimensions[0], scaledDimensions[1], 0xFFFF0000);

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.flag_selection.load_failed"),
                    x + scaledDimensions[0] / 2,
                    y + scaledDimensions[1] / 2 - 5,
                    0xFFFFFF
            );

            System.err.println("Failed to load flag texture: " + e.getMessage());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedFlag != null) {
            int panelWidth = 200;
            int panelHeight = 230;
            int panelX = this.width / 2 + (this.width / 2 - panelWidth) / 2;
            int panelY = (this.height - panelHeight) / 2;

            int saveButtonWidth = 80;
            int saveButtonHeight = 20;
            int saveButtonX = panelX + (panelWidth - saveButtonWidth) / 2;
            int saveButtonY = panelY + panelHeight - 40;

            if (mouseX >= saveButtonX && mouseX <= saveButtonX + saveButtonWidth &&
                    mouseY >= saveButtonY && mouseY <= saveButtonY + saveButtonHeight) {
                saveSelection();
                this.close();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void saveSelection() {
        if (selectedFlag != null) {
            System.out.println("保存旗帜选择: " + selectedFlag.getId() + " - " + selectedFlag.getName());

            if (client != null && client.getConnection() != null) {
                FriendlyByteBuf buf = NetworkCompat.newBuffer();
                new FlagUpdatePacket(blockPos, selectedFlag.getId()).write(buf);
                NetworkCompat.sendToServer(ModMessages.UPDATE_FLAG, buf);
            }

            if (client != null && client.level != null) {
                var blockEntity = client.level.getBlockEntity(blockPos);
                if (blockEntity instanceof com.beigu.yunbeiuc.entity.FlagBlockEntity) {
                    ((com.beigu.yunbeiuc.entity.FlagBlockEntity) blockEntity).setFlagId(selectedFlag.getId());
                }
            }
        }
    }

    private List<FlagOption> createFlagOptions() {
        List<FlagOption> options = new ArrayList<>();
        for (CustomFlag flag : FlagLoader.getFlagsInOrder()) {
            options.add(new FlagOption(flag));
        }
        return options;
    }

    public void setSelectedFlag(CustomFlag flag) {
        this.selectedFlag = flag;
    }

    private static class FlagOption {
        private final CustomFlag flag;

        public FlagOption(CustomFlag flag) {
            this.flag = flag;
        }

        public CustomFlag getFlag() {
            return flag;
        }

        public String getDisplayName() {
            return flag.getName();
        }

        public int getColor() {
            return flag.getColor();
        }
    }

    private class FlagListWidget extends AbstractOptionListWidget<FlagOption> {
        public FlagListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight,
                               List<FlagOption> flagOptions, Consumer<FlagOption> onSelect) {
            super(client, width, height, top, bottom, itemHeight, flagOptions,
                    option -> option.getFlag() == selectedFlag, onSelect,
                    option -> Text.literal(option.getDisplayName()), FlagOption::getColor);
        }
    }
}
