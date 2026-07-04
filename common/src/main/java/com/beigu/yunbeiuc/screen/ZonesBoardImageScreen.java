package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.entity.ZonesBoardImageEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.ZonesBoardImageUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ZonesBoardImageScreen extends Screen {
    private final BlockPos pos;
    private final ZonesBoardImageEntity blockEntity;

    private final List<ImageOption> options;
    private ImageListWidget listWidget;
    private ImageOption selectedOption;

    private TextFieldWidget text1TextField;
    private float andX = 0f;
    private float andY = 0f;
    private float andScale = 0f;

    private ValueAdjustButton xValueButton;
    private ValueAdjustButton yValueButton;
    private ValueAdjustButton scaleValueButton;

    private static final int RIGHT_PANEL_WIDTH = 200;
    private static final int RIGHT_PANEL_HEIGHT = 240;

    private static final Identifier RED_TEXTURE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_red_number_logo.png");
    private static final Identifier YELLOW_TEXTURE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_yellow_number_logo.png");
    private static final Identifier WHITE_TEXTURE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_white_number_logo.png");
    private static final Identifier EXPRESSWAY_TEXTURE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_logo.png");

    public ZonesBoardImageScreen(BlockPos pos) {
        super(Text.translatable("text.yunbeiuc.zones_board_image.title"));
        this.pos = pos;
        this.blockEntity = (ZonesBoardImageEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);
        this.andX = blockEntity.getAndX();
        this.andY = blockEntity.getAndY();
        this.andScale = blockEntity.getAndScale();
        this.options = createImageOptions();

        for (ImageOption option : options) {
            if (option.getImage() == blockEntity.getImage()) {
                this.selectedOption = option;
                break;
            }
        }
        if (this.selectedOption == null && !options.isEmpty()) {
            this.selectedOption = options.get(0);
        }
    }

    @Override
    protected void init() {
        super.init();

        String existingText1 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof ZonesBoardImageEntity entity) {
                existingText1 = entity.getText1();
                andX = entity.getAndX();
                andY = entity.getAndY();
                andScale = entity.getAndScale();
            }
        }

        int listWidth = this.width / 3;
        this.listWidget = new ImageListWidget(
                this.client,
                listWidth,
                this.height,
                40,
                this.height - 60,
                30,
                this.options
        );
        this.addDrawableChild(this.listWidget);

        int rightAreaX = this.width / 3;
        int rightAreaWidth = this.width * 2 / 3;
        int panelX = rightAreaX + (rightAreaWidth - RIGHT_PANEL_WIDTH) / 2;
        int panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;

        this.text1TextField = new TextFieldWidget(
                this.textRenderer,
                panelX + 10, panelY + 40,
                130, 24,
                Text.translatable("text.yunbeiuc.zones_board_image.text1_content")
        );
        this.text1TextField.setMaxLength(256);
        this.text1TextField.setText(existingText1);
        this.text1TextField.setPlaceholder(Text.translatable("text.yunbeiuc.zones_board_image.text1_placeholder"));
        this.addSelectableChild(this.text1TextField);

        // X偏移按钮
        this.xValueButton = this.addDrawableChild(
                new ValueAdjustButton(
                        panelX + 10, panelY + 80,
                        24, 24,
                        andX, "X",
                        button -> {},
                        (button, isLeftClick) -> {
                            if (isLeftClick) {
                                andX += 1.0f;
                            } else {
                                andX -= 1.0f;
                            }
                            ((ValueAdjustButton) button).setValue(andX);
                        }
                )
        );

// Y偏移按钮
        this.yValueButton = this.addDrawableChild(
                new ValueAdjustButton(
                        panelX + 44, panelY + 80,
                        24, 24,
                        andY, "Y",
                        button -> {},
                        (button, isLeftClick) -> {
                            if (isLeftClick) {
                                andY += 1.0f;
                            } else {
                                andY -= 1.0f;
                            }
                            ((ValueAdjustButton) button).setValue(andY);
                        }
                )
        );

// 缩放按钮
        this.scaleValueButton = this.addDrawableChild(
                new ValueAdjustButton(
                        panelX + 100, panelY + 80,
                        24, 24,
                        andScale, "S",
                        button -> {},
                        (button, isLeftClick) -> {
                            if (isLeftClick) {
                                andScale += 0.1f;
                            } else {
                                andScale -= 0.1f;
                            }
                            ((ValueAdjustButton) button).setValue(andScale);
                        }
                )
        );

        // 保存按钮
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.zones_board_image.save"), button -> saveAndClose())
                        .dimensions(panelX + 80, panelY + 210, 50, 20)
                        .build()
        );

        // 取消按钮
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.zones_board_image.cancel"), button -> this.close())
                        .dimensions(panelX + 140, panelY + 210, 50, 20)
                        .build()
        );

        this.setFocused(this.text1TextField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int listAreaWidth = this.width / 3;
        int rightAreaX = this.width / 3;
        int rightAreaWidth = this.width * 2 / 3;
        int panelX = rightAreaX + (rightAreaWidth - RIGHT_PANEL_WIDTH) / 2;
        int panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_image.title"),
                listAreaWidth / 2,
                10,
                0xFFFFFF
        );

        if (selectedOption != null) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.zones_board_image.current_selection",
                            Text.translatable(selectedOption.getTranslationKey())),
                    10,
                    this.height - 55,
                    0xFFFFFF
            );
        }

        context.fill(panelX, panelY, panelX + RIGHT_PANEL_WIDTH, panelY + RIGHT_PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, RIGHT_PANEL_WIDTH, RIGHT_PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_image.settings_title"),
                panelX + RIGHT_PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_image.text1_label"),
                panelX + 10, panelY + 28,
                0xFFAAAAAA
        );

        // XY偏移标签 - 放在同一行
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_image.offset_label"),
                panelX + 10, panelY + 68,
                0xFFAAAAAA
        );

        // XY偏移值 - 放在同一行显示
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_image.offset_value",
                        String.format("%.1f", andX), String.format("%.1f", andY)),
                panelX + 10, panelY + 108,
                0xFFFFFF00
        );

        // 缩放标签
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_image.scale_label"),
                panelX + 100, panelY + 68,
                0xFFAAAAAA
        );

        // 缩放值
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.zones_board_image.scale_value", String.format("%.1f", andScale)),
                panelX + 100, panelY + 108,
                0xFFFFFF00
        );

        if (selectedOption != null) {
            Identifier texture = selectedOption.getTexture();
            int previewSize = 60;

            int previewX = panelX + 10;
            int previewY = panelY + 128;

            context.fill(previewX - 2, previewY - 2, previewX + previewSize + 2, previewY + previewSize + 2, 0xFF000000);
            context.drawBorder(previewX - 2, previewY - 2, previewSize + 4, previewSize + 4, 0xFFFFFFFF);

            context.drawTexture(texture, previewX, previewY, previewSize, previewSize, 0, 0, 16, 16, 16, 16);

            // 图片名称
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable(selectedOption.getTranslationKey()),
                    previewX + previewSize + 8,
                    previewY + 10,
                    0xFFFFFF
            );

            // 当前值显示
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.zones_board_image.current_offset",
                            String.format("%.1f", andX), String.format("%.1f", andY)),
                    previewX + previewSize + 8,
                    previewY + 30,
                    0xFFAAAAAA
            );
        }

        this.text1TextField.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var child : this.children()) {
            if (child instanceof ValueAdjustButton yButton && child.isMouseOver(mouseX, mouseY)) {
                if (button == 1) {
                    yButton.onClick(false);
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
            saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String text1 = this.text1TextField.getText();
            ZonesBoardImageEntity.BoardImage selectedImage = selectedOption != null ? selectedOption.getImage() : ZonesBoardImageEntity.BoardImage.RED;

            ZonesBoardImageUpdatePacket packet =
                    new ZonesBoardImageUpdatePacket(pos, text1, selectedImage, andX, andY, andScale);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_ZONES_BOARD_IMAGE, buf);
        }
        this.close();
    }

    public void setSelectedOption(ImageOption option) {
        this.selectedOption = option;
    }

    private List<ImageOption> createImageOptions() {
        List<ImageOption> options = new ArrayList<>();
        options.add(new ImageOption(ZonesBoardImageEntity.BoardImage.RED, RED_TEXTURE,
                "text.yunbeiuc.zones_board_image.road_type.national"));
        options.add(new ImageOption(ZonesBoardImageEntity.BoardImage.YELLOW, YELLOW_TEXTURE,
                "text.yunbeiuc.zones_board_image.road_type.provincial"));
        options.add(new ImageOption(ZonesBoardImageEntity.BoardImage.WHITE, WHITE_TEXTURE,
                "text.yunbeiuc.zones_board_image.road_type.county"));
        options.add(new ImageOption(ZonesBoardImageEntity.BoardImage.EXPRESSWAY, EXPRESSWAY_TEXTURE,
                "text.yunbeiuc.zones_board_image.road_type.expressway"));
        return options;
    }

    // ... 其余代码保持不变 (ImageOption, ImageListWidget, shouldPause)
    private static class ImageOption {
        private final ZonesBoardImageEntity.BoardImage image;
        private final Identifier texture;
        private final String translationKey;

        public ImageOption(ZonesBoardImageEntity.BoardImage image, Identifier texture, String translationKey) {
            this.image = image;
            this.texture = texture;
            this.translationKey = translationKey;
        }

        public ZonesBoardImageEntity.BoardImage getImage() {
            return image;
        }

        public Identifier getTexture() {
            return texture;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public int getColor() {
            return switch (image) {
                case RED -> 0xFF0000;
                case YELLOW -> 0xFFFF00;
                case WHITE -> 0xFFFFFF;
                case EXPRESSWAY -> 0x00AA00;
            };
        }
    }

    private class ImageListWidget extends ElementListWidget<ImageListWidget.Entry> {
        private final int listWidth;

        public ImageListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, List<ImageOption> imageOptions) {
            super(client, width, height, top, bottom, itemHeight);
            this.listWidth = width;

            for (ImageOption option : imageOptions) {
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
            private final ImageOption option;

            public Entry(ImageOption option) {
                this.option = option;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                if (option == selectedOption) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);
                } else if (hovered) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x22FFFFFF);
                }

                int colorSize = 16;
                int colorX = x + 5;
                int colorY = y + (entryHeight - colorSize) / 2;
                context.fill(colorX, colorY, colorX + colorSize, colorY + colorSize, 0xFF000000 | option.getColor());
                context.drawBorder(colorX, colorY, colorSize, colorSize, 0xFFCCCCCC);

                int textX = colorX + colorSize + 8;
                context.drawTextWithShadow(
                        textRenderer,
                        Text.translatable(option.getTranslationKey()),
                        textX,
                        y + (entryHeight - 8) / 2,
                        0xFFFFFF
                );
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                setSelectedOption(option);
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

    @Override
    public boolean shouldPause() {
        return false;
    }
}