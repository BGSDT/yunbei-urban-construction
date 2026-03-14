package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignIndicationDirectionBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.UpdateIndicationDirectionPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignIndicationDirection;

import java.util.ArrayList;
import java.util.List;

public class SignIndicationDirectionScreen extends Screen {
    private final List<DirectionTypeOption> options;
    private DirectionTypeListWidget listWidget;
    private SignIndicationDirection selectedDirectionType;
    private final BlockPos blockPos;

    public SignIndicationDirectionScreen(Text title, BlockPos pos, SignIndicationDirection currentType) {
        super(title);
        this.blockPos = pos;
        this.options = createDirectionTypeOptions();
        this.selectedDirectionType = currentType;
    }

    @Override
    protected void init() {
        super.init();

        // 重新从方块实体读取当前方向类型
        if (this.client != null && this.client.world != null) {
            BlockEntity blockEntity = this.client.world.getBlockEntity(this.blockPos);
            if (blockEntity instanceof SignIndicationDirectionBlockEntity directionBlockEntity) {
                this.selectedDirectionType = directionBlockEntity.getDirectionType();
            }
        }

        int listWidth = this.width / 2;
        this.listWidget = new DirectionTypeListWidget(
                this.client,
                listWidth,
                this.height,
                40,
                this.height - 60,
                20
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
                Text.translatable("text.yunbeiuc.sign_indication_direction.title"),
                listAreaLeft + listAreaWidth / 2,
                10,
                0xFFFFFF
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_indication_direction.current_selection", 
                    Text.translatable(getDirectionTypeTranslationKey(selectedDirectionType))),
                listAreaLeft + 10,
                this.height - 55,
                0xFFFFFF
        );

        drawInfoPanel(context);
    }

    private void drawInfoPanel(DrawContext context) {
        int panelWidth = 200;
        int panelHeight = 230;
        int panelX = this.width / 2 + (this.width / 2 - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA333333);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_indication_direction.info_panel"),
                panelX + panelWidth / 2,
                panelY + 10,
                0xFFFFFF
        );

        int iconSize = 50;
        int iconX = panelX + 15;
        int iconY = panelY + 35;

        context.fill(iconX - 2, iconY - 2, iconX + iconSize + 2, iconY + iconSize + 2, 0xFF000000);
        context.drawBorder(iconX - 2, iconY - 2, iconSize + 4, iconSize + 4, 0xFFFFFFFF);

        drawDirectionTypeImage(context, iconX, iconY, iconSize);

        int infoX = iconX + iconSize + 20;
        int infoY = panelY + 40;

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_indication_direction.direction_type", 
                    Text.translatable(getDirectionTypeTranslationKey(selectedDirectionType))),
                infoX,
                infoY,
                0xFFFFFF
        );

        int saveButtonX = infoX;
        int saveButtonY = infoY + 25;
        int saveButtonWidth = 80;
        int saveButtonHeight = 20;

        context.fill(saveButtonX, saveButtonY, saveButtonX + saveButtonWidth, saveButtonY + saveButtonHeight, 0xFF555555);
        context.drawBorder(saveButtonX, saveButtonY, saveButtonWidth, saveButtonHeight, 0xFFFFFFFF);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_indication_direction.save_button"),
                saveButtonX + saveButtonWidth / 2,
                saveButtonY + 6,
                0xFFFFFF
        );

        int infoBoxY = iconY + iconSize + 10;
        drawInfoBox(context, infoBoxY);
    }

    private void drawInfoBox(DrawContext context, int topY) {
        int infoBoxWidth = 180;
        int infoBoxHeight = 90;
        int infoBoxX = this.width / 2 + (this.width / 2 - infoBoxWidth) / 2;
        int infoBoxY = topY;

        context.fill(infoBoxX, infoBoxY, infoBoxX + infoBoxWidth, infoBoxY + infoBoxHeight, 0xAA000000);
        context.drawBorder(infoBoxX, infoBoxY, infoBoxWidth, infoBoxHeight, 0xFFFFFFFF);

        String description = getDirectionDescription(selectedDirectionType);

        int textX = infoBoxX + 8;
        int textY = infoBoxY + 12;
        int maxWidth = infoBoxWidth - 16;

        List<String> lines = wrapText(description, maxWidth);
        for (int i = 0; i < lines.size() && i < 6; i++) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.of(lines.get(i)),
                    textX,
                    textY + i * 10,
                    0xCCCCCC
            );
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split("");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.toString() + word;
            int lineWidth = this.textRenderer.getWidth(testLine);

            if (lineWidth <= maxWidth) {
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                    currentLine = new StringBuilder();
                }
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private void drawDirectionTypeImage(DrawContext context, int x, int y, int displaySize) {
        String imagePath = getDirectionTypeImagePath(selectedDirectionType);
        Identifier texture = new Identifier("yunbeiuc", imagePath);

        try {
            context.drawTexture(texture, x, y, displaySize, displaySize, 0, 0, 400, 400, 400, 400);
        } catch (Exception e) {
            context.fill(x, y, x + displaySize, y + displaySize, 0x44FF0000);
            context.drawBorder(x, y, displaySize, displaySize, 0xFFFF0000);

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.sign_indication_direction.load_failed"),
                    x + displaySize/2,
                    y + displaySize/2 - 5,
                    0xFFFFFF
            );
        }
    }

    private String getDirectionTypeImagePath(SignIndicationDirection directionType) {
        String directionName = directionType.asString();
        return "textures/gui/" + directionName + ".png";
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int panelWidth = 200;
        int panelHeight = 230;
        int panelX = this.width / 2 + (this.width / 2 - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        int iconSize = 50;
        int iconX = panelX + 15;
        int iconY = panelY + 35;

        int infoX = iconX + iconSize + 20;
        int infoY = panelY + 40;
        int saveButtonX = infoX;
        int saveButtonY = infoY + 25;
        int saveButtonWidth = 80;
        int saveButtonHeight = 20;

        if (mouseX >= saveButtonX && mouseX <= saveButtonX + saveButtonWidth &&
                mouseY >= saveButtonY && mouseY <= saveButtonY + saveButtonHeight) {
            saveSelection();
            this.close();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        super.close();
    }

    private void saveSelection() {
        if (client != null && client.getNetworkHandler() != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            new UpdateIndicationDirectionPacket(blockPos, selectedDirectionType).write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_INDICATION_DIRECTION, buf);
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            var blockEntity = client.world.getBlockEntity(blockPos);
            if (blockEntity instanceof SignIndicationDirectionBlockEntity) {
                ((SignIndicationDirectionBlockEntity) blockEntity).setDirectionType(selectedDirectionType);
            }
        }
    }

    private List<DirectionTypeOption> createDirectionTypeOptions() {
        List<DirectionTypeOption> options = new ArrayList<>();
        for (SignIndicationDirection directionType : SignIndicationDirection.values()) {
            options.add(new DirectionTypeOption(directionType, getDirectionTypeDisplayName(directionType)));
        }
        return options;
    }

    private String getDirectionTypeDisplayName(SignIndicationDirection directionType) {
        return Text.translatable(getDirectionTypeTranslationKey(directionType)).getString();
    }

    private String getDirectionTypeTranslationKey(SignIndicationDirection directionType) {
        return "text.yunbeiuc.sign_indication_direction." + directionType.asString();
    }

    private String getDirectionDescription(SignIndicationDirection directionType) {
        return Text.translatable(getDirectionDescriptionTranslationKey(directionType)).getString();
    }

    private String getDirectionDescriptionTranslationKey(SignIndicationDirection directionType) {
        return "text.yunbeiuc.sign_indication_direction." + directionType.asString() + ".description";
    }

    public void setSelectedDirectionType(SignIndicationDirection directionType) {
        this.selectedDirectionType = directionType;
    }

    private static class DirectionTypeOption {
        private final SignIndicationDirection directionType;
        private final String displayText;

        public DirectionTypeOption(SignIndicationDirection directionType, String displayText) {
            this.directionType = directionType;
            this.displayText = displayText;
        }

        public SignIndicationDirection getDirectionType() {
            return directionType;
        }

        public String getDisplayText() {
            return displayText;
        }
    }

    private class DirectionTypeListWidget extends ElementListWidget<DirectionTypeListWidget.Entry> {
        private final List<DirectionTypeOption> options;
        private final int listWidth;

        public DirectionTypeListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            this.listWidth = width;
            this.options = createDirectionTypeOptions();

            for (DirectionTypeOption option : options) {
                this.addEntry(new Entry(option));
            }
        }

        @Override
        public int getRowWidth() {
            return this.listWidth - 25;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.getRowLeft() + this.getRowWidth() + 4;
        }

        @Override
        public int getRowLeft() {
            return this.left + 5;
        }

        @Override
        public int getRowRight() {
            return this.getRowLeft() + this.getRowWidth();
        }

        public class Entry extends ElementListWidget.Entry<Entry> {
            private final DirectionTypeOption option;

            public Entry(DirectionTypeOption option) {
                this.option = option;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                if (option.getDirectionType() == selectedDirectionType) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);
                } else if (hovered) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x22FFFFFF);
                }

                int thumbnailSize = 16;
                int thumbnailX = x + 5;
                int thumbnailY = y + (entryHeight - thumbnailSize) / 2;

                try {
                    String imagePath = getDirectionTypeImagePath(option.getDirectionType());
                    Identifier texture = new Identifier("yunbeiuc", imagePath);
                    context.drawTexture(texture, thumbnailX, thumbnailY, thumbnailSize, thumbnailSize, 0, 0, 1, 1, 1, 1);
                } catch (Exception e) {
                    context.fill(thumbnailX, thumbnailY, thumbnailX + thumbnailSize, thumbnailY + thumbnailSize, 0x44FF0000);
                    context.drawBorder(thumbnailX, thumbnailY, thumbnailSize, thumbnailSize, 0xFFFF0000);
                }

                int textX = thumbnailX + thumbnailSize + 8;
                context.drawTextWithShadow(
                        textRenderer,
                        Text.translatable(getDirectionTypeTranslationKey(option.getDirectionType())),
                        textX,
                        y + (entryHeight - 8) / 2,
                        0xFFFFFF
                );
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                setSelectedDirectionType(option.getDirectionType());
                return true;
            }

            @Override
            public List<ClickableWidget> selectableChildren() {
                return List.of();
            }

            @Override
            public List<ClickableWidget> children() {
                return List.of();
            }
        }
    }
}