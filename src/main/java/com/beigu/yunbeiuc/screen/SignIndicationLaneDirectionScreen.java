package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignIndicationLaneDirectionBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.UpdateIndicationLaneDirectionPacket;
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
import com.beigu.yunbeiuc.block.custom.sign.data.SignIndicationLaneDirection;

import java.util.ArrayList;
import java.util.List;

public class SignIndicationLaneDirectionScreen extends Screen {
    private final List<LaneDirectionTypeOption> options;
    private LaneDirectionTypeListWidget listWidget;
    private SignIndicationLaneDirection selectedLaneDirectionType;
    private final BlockPos blockPos;

    public SignIndicationLaneDirectionScreen(Text title, BlockPos pos, SignIndicationLaneDirection currentType) {
        super(title);
        this.blockPos = pos;
        this.options = createLaneDirectionTypeOptions();
        this.selectedLaneDirectionType = currentType;
    }

    @Override
    protected void init() {
        super.init();

        // 重新从方块实体读取当前车道方向类型
        if (this.client != null && this.client.world != null) {
            BlockEntity blockEntity = this.client.world.getBlockEntity(this.blockPos);
            if (blockEntity instanceof SignIndicationLaneDirectionBlockEntity laneDirectionBlockEntity) {
                this.selectedLaneDirectionType = laneDirectionBlockEntity.getLaneDirectionType();
            }
        }

        int listWidth = this.width / 2;
        this.listWidget = new LaneDirectionTypeListWidget(
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
                Text.translatable("text.yunbeiuc.sign_indication_lane_direction.title"),
                listAreaLeft + listAreaWidth / 2,
                10,
                0xFFFFFF
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_indication_lane_direction.current_selection", 
                    Text.translatable(getLaneDirectionTypeTranslationKey(selectedLaneDirectionType))),
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
                Text.translatable("text.yunbeiuc.sign_indication_lane_direction.info_panel"),
                panelX + panelWidth / 2,
                panelY + 10,
                0xFFFFFF
        );

        int iconSize = 50;
        int iconX = panelX + 15;
        int iconY = panelY + 35;

        context.fill(iconX - 2, iconY - 2, iconX + iconSize + 2, iconY + iconSize + 2, 0xFF000000);
        context.drawBorder(iconX - 2, iconY - 2, iconSize + 4, iconSize + 4, 0xFFFFFFFF);

        drawLaneDirectionTypeImage(context, iconX, iconY, iconSize);

        int infoX = iconX + iconSize + 20;
        int infoY = panelY + 40;

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_indication_lane_direction.lane_direction_type", 
                    Text.translatable(getLaneDirectionTypeTranslationKey(selectedLaneDirectionType))),
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
                Text.translatable("text.yunbeiuc.sign_indication_lane_direction.save_button"),
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

        String description = getLaneDirectionDescription(selectedLaneDirectionType);

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

    private void drawLaneDirectionTypeImage(DrawContext context, int x, int y, int displaySize) {
        String imagePath = getLaneDirectionTypeImagePath(selectedLaneDirectionType);
        Identifier texture = new Identifier("yunbeiuc", imagePath);

        try {
            context.drawTexture(texture, x, y, displaySize, displaySize, 0, 0, 400, 400, 400, 400);
        } catch (Exception e) {
            context.fill(x, y, x + displaySize, y + displaySize, 0x44FF0000);
            context.drawBorder(x, y, displaySize, displaySize, 0xFFFF0000);

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.sign_indication_lane_direction.load_failed"),
                    x + displaySize/2,
                    y + displaySize/2 - 5,
                    0xFFFFFF
            );
        }
    }

    private String getLaneDirectionTypeImagePath(SignIndicationLaneDirection laneDirectionType) {
        String laneDirectionName = laneDirectionType.asString();
        return "textures/gui/" + laneDirectionName + ".png";
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
            new UpdateIndicationLaneDirectionPacket(blockPos, selectedLaneDirectionType).write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_INDICATION_LANE_DIRECTION, buf);
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            var blockEntity = client.world.getBlockEntity(blockPos);
            if (blockEntity instanceof SignIndicationLaneDirectionBlockEntity) {
                ((SignIndicationLaneDirectionBlockEntity) blockEntity).setLaneDirectionType(selectedLaneDirectionType);
            }
        }
    }

    private List<LaneDirectionTypeOption> createLaneDirectionTypeOptions() {
        List<LaneDirectionTypeOption> options = new ArrayList<>();
        for (SignIndicationLaneDirection laneDirectionType : SignIndicationLaneDirection.values()) {
            options.add(new LaneDirectionTypeOption(laneDirectionType, getLaneDirectionTypeDisplayName(laneDirectionType)));
        }
        return options;
    }

    private String getLaneDirectionTypeDisplayName(SignIndicationLaneDirection laneDirectionType) {
        return Text.translatable(getLaneDirectionTypeTranslationKey(laneDirectionType)).getString();
    }

    private String getLaneDirectionTypeTranslationKey(SignIndicationLaneDirection laneDirectionType) {
        return "text.yunbeiuc.sign_indication_lane_direction." + laneDirectionType.asString();
    }

    private String getLaneDirectionDescription(SignIndicationLaneDirection laneDirectionType) {
        return Text.translatable("text.yunbeiuc.sign_indication_lane_direction.description").getString();
    }

    private String getLaneDirectionDescriptionTranslationKey(SignIndicationLaneDirection laneDirectionType) {
        return "text.yunbeiuc.sign_indication_lane_direction." + laneDirectionType.asString() + ".description";
    }

    public void setSelectedLaneDirectionType(SignIndicationLaneDirection laneDirectionType) {
        this.selectedLaneDirectionType = laneDirectionType;
    }

    private static class LaneDirectionTypeOption {
        private final SignIndicationLaneDirection laneDirectionType;
        private final String displayText;

        public LaneDirectionTypeOption(SignIndicationLaneDirection laneDirectionType, String displayText) {
            this.laneDirectionType = laneDirectionType;
            this.displayText = displayText;
        }

        public SignIndicationLaneDirection getLaneDirectionType() {
            return laneDirectionType;
        }

        public String getDisplayText() {
            return displayText;
        }
    }

    private class LaneDirectionTypeListWidget extends ElementListWidget<LaneDirectionTypeListWidget.Entry> {
        private final List<LaneDirectionTypeOption> options;
        private final int listWidth;

        public LaneDirectionTypeListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            this.listWidth = width;
            this.options = createLaneDirectionTypeOptions();

            for (LaneDirectionTypeOption option : options) {
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
            private final LaneDirectionTypeOption option;

            public Entry(LaneDirectionTypeOption option) {
                this.option = option;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                if (option.getLaneDirectionType() == selectedLaneDirectionType) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);
                } else if (hovered) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x22FFFFFF);
                }

                int thumbnailSize = 16;
                int thumbnailX = x + 5;
                int thumbnailY = y + (entryHeight - thumbnailSize) / 2;

                try {
                    String imagePath = getLaneDirectionTypeImagePath(option.getLaneDirectionType());
                    Identifier texture = new Identifier("yunbeiuc", imagePath);
                    context.drawTexture(texture, thumbnailX, thumbnailY, thumbnailSize, thumbnailSize, 0, 0, 1, 1, 1, 1);
                } catch (Exception e) {
                    context.fill(thumbnailX, thumbnailY, thumbnailX + thumbnailSize, thumbnailY + thumbnailSize, 0x44FF0000);
                    context.drawBorder(thumbnailX, thumbnailY, thumbnailSize, thumbnailSize, 0xFFFF0000);
                }

                int textX = thumbnailX + thumbnailSize + 8;
                context.drawTextWithShadow(
                        textRenderer,
                        Text.translatable(getLaneDirectionTypeTranslationKey(option.getLaneDirectionType())),
                        textX,
                        y + (entryHeight - 8) / 2,
                        0xFFFFFF
                );
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                setSelectedLaneDirectionType(option.getLaneDirectionType());
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