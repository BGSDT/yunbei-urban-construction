package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignNoDirectionBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.UpdateDirectionTypePacket;
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
import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignNoDirection;

import java.util.ArrayList;
import java.util.List;

public class SignNoDirectionScreen extends Screen {
    private final List<DirectionTypeOption> options;
    private DirectionTypeListWidget listWidget;
    private SignNoDirection selectedDirectionType;
    private final BlockPos blockPos;

    public SignNoDirectionScreen(Text title, BlockPos pos, SignNoDirection currentType) {
        super(title);
        this.blockPos = pos;
        this.options = createDirectionTypeOptions();

        // 确保使用传入的当前方向类型
        this.selectedDirectionType = currentType;
        System.out.println("GUI创建：当前禁止方向类型: " + this.selectedDirectionType.name());
    }

    @Override
    protected void init() {
        super.init();

        // 在初始化时重新从方块实体读取当前方向类型
        if (this.client != null && this.client.world != null) {
            BlockEntity blockEntity = this.client.world.getBlockEntity(this.blockPos);
            if (blockEntity instanceof SignNoDirectionBlockEntity directionBlockEntity) {
                this.selectedDirectionType = directionBlockEntity.getDirectionType();
                System.out.println("GUI初始化：读取当前禁止方向类型: " + this.selectedDirectionType.name());
            }
        }

        // 列表宽度调整为屏幕的1/2
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
        // 直接绘制背景，不调用父类的renderBackground
        super.renderBackground(context);

        // 渲染所有子部件
        super.render(context, mouseX, mouseY, delta);

        // 列表区域宽度（屏幕的1/2）
        int listAreaWidth = this.width / 2;

        // 列表区域左侧起始位置
        int listAreaLeft = 0;

        // 标题在列表区域内居中
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_no_direction.title"),
                listAreaLeft + listAreaWidth / 2,
                10,
                0xFFFFFF
        );

        // 当前选择信息在列表区域内左对齐
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_no_direction.current_selection", 
                    Text.translatable(getDirectionTypeTranslationKey(selectedDirectionType))),
                listAreaLeft + 10,
                this.height - 55,
                0xFFFFFF
        );

        // 在右侧1/2区域添加信息面板
        drawInfoPanel(context);
    }

    private void drawInfoPanel(DrawContext context) {
        int panelWidth = 200;
        int panelHeight = 230;

        // 面板位置：右侧1/2区域的中心
        int panelX = this.width / 2 + (this.width / 2 - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        // 绘制面板底色（深灰色半透明）
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA333333);

        // 绘制面板边框（浅灰色）
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFCCCCCC);

        // 面板标题
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_no_direction.info_panel"),
                panelX + panelWidth / 2,
                panelY + 10,
                0xFFFFFF
        );

        // 小图标预览移到面板上方左侧
        int iconSize = 50;
        int iconX = panelX + 15;
        int iconY = panelY + 35;

        // 绘制小图标背景框
        context.fill(iconX - 2, iconY - 2, iconX + iconSize + 2, iconY + iconSize + 2, 0xFF000000);
        context.drawBorder(iconX - 2, iconY - 2, iconSize + 4, iconSize + 4, 0xFFFFFFFF);

        // 绘制小图标
        drawDirectionTypeImage(context, iconX, iconY, iconSize);

        // 方向类型信息移到小图标右侧
        int infoX = iconX + iconSize + 20; // 小图标右侧20像素
        int infoY = panelY + 40;

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_no_direction.direction_type", 
                    Text.translatable(getDirectionTypeTranslationKey(selectedDirectionType))),
                infoX,
                infoY,
                0xFFFFFF
        );

        // 保存按钮移到小图标右侧下方
        int saveButtonX = infoX;
        int saveButtonY = infoY + 25;
        int saveButtonWidth = 80;
        int saveButtonHeight = 20;

        // 绘制保存按钮背景
        context.fill(saveButtonX, saveButtonY, saveButtonX + saveButtonWidth, saveButtonY + saveButtonHeight, 0xFF555555);
        context.drawBorder(saveButtonX, saveButtonY, saveButtonWidth, saveButtonHeight, 0xFFFFFFFF);

        // 绘制保存按钮文字
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_no_direction.save_button"),
                saveButtonX + saveButtonWidth / 2,
                saveButtonY + 6,
                0xFFFFFF
        );

        // 信息框移到缩略图下方10px
        int infoBoxY = iconY + iconSize + 10; // 缩略图底部 + 10px
        drawInfoBox(context, infoBoxY);
    }

    private void drawInfoBox(DrawContext context, int topY) {
        int infoBoxWidth = 180;
        int infoBoxHeight = 90; // 固定高度

        // 计算水平位置
        int infoBoxX = this.width / 2 + (this.width / 2 - infoBoxWidth) / 2;

        // 使用传入的topY作为信息框顶部位置
        int infoBoxY = topY;

        // 绘制背景和边框
        context.fill(infoBoxX, infoBoxY, infoBoxX + infoBoxWidth, infoBoxY + infoBoxHeight, 0xAA000000);
        context.drawBorder(infoBoxX, infoBoxY, infoBoxWidth, infoBoxHeight, 0xFFFFFFFF);

        // 使用当前选择方向类型的特定描述
        String description = getDirectionDescription(selectedDirectionType);

        int textX = infoBoxX + 8;
        int textY = infoBoxY + 12;
        int maxWidth = infoBoxWidth - 16;

        // 使用原来的文本换行处理
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
            // 测试添加字符后的宽度
            String testLine = currentLine.toString() + word;
            int lineWidth = this.textRenderer.getWidth(testLine);

            if (lineWidth <= maxWidth) {
                currentLine.append(word);
            } else {
                // 当前行已满，添加到行列表
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // 单个字符就超过行宽，强制分割
                    lines.add(word);
                    currentLine = new StringBuilder();
                }
            }
        }

        // 添加最后一行
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private void drawDirectionTypeImage(DrawContext context, int x, int y, int displaySize) {
        // 根据当前选择的方向类型构建图片路径
        String imagePath = getDirectionTypeImagePath(selectedDirectionType);

        // 获取纹理标识符
        Identifier texture = new Identifier("yunbeiuc", imagePath);

        try {
            // 绘制纹理，将400x400的原图缩放显示为50x50
            context.drawTexture(texture, x, y, displaySize, displaySize, 0, 0, 400, 400, 400, 400);
        } catch (Exception e) {
            // 如果图片加载失败，绘制一个备用矩形和错误信息
            context.fill(x, y, x + displaySize, y + displaySize, 0x44FF0000);
            context.drawBorder(x, y, displaySize, displaySize, 0xFFFF0000);

            // 错误信息
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.sign_no_direction.load_failed"),
                    x + displaySize/2,
                    y + displaySize/2 - 5,
                    0xFFFFFF
            );
        }
    }

    private String getDirectionTypeImagePath(SignNoDirection directionType) {
        // 根据枚举值返回对应的图片路径
        String directionName = directionType.asString();
        return "textures/gui/" + directionName + ".png";
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int panelWidth = 200;
        int panelHeight = 230;
        int panelX = this.width / 2 + (this.width / 2 - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        // 小图标位置
        int iconSize = 50;
        int iconX = panelX + 15;
        int iconY = panelY + 35;

        // 保存按钮新位置
        int infoX = iconX + iconSize + 20;
        int infoY = panelY + 40;
        int saveButtonX = infoX;
        int saveButtonY = infoY + 25;
        int saveButtonWidth = 80;
        int saveButtonHeight = 20;

        // 如果点击了保存按钮区域
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
        // 移除ESC自动保存功能，只在点击保存按钮时保存
    }

    private void saveSelection() {
        System.out.println("保存禁止方向类型: " + selectedDirectionType.asString() + " (" + getDirectionTypeDisplayName(selectedDirectionType) + ")");
        System.out.println("方块位置: " + blockPos);

        // 发送网络数据包到服务器
        if (client != null && client.getNetworkHandler() != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            new UpdateDirectionTypePacket(blockPos, selectedDirectionType).write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_DIRECTION_TYPE, buf);
        }

        // 客户端预览（可选，保持即时反馈）
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            var blockEntity = client.world.getBlockEntity(blockPos);
            if (blockEntity instanceof SignNoDirectionBlockEntity) {
                ((SignNoDirectionBlockEntity) blockEntity).setDirectionType(selectedDirectionType);
            }
        }
    }

    private List<DirectionTypeOption> createDirectionTypeOptions() {
        List<DirectionTypeOption> options = new ArrayList<>();
        for (SignNoDirection directionType : SignNoDirection.values()) {
            options.add(new DirectionTypeOption(directionType, getDirectionTypeDisplayName(directionType)));
        }
        return options;
    }

    private String getDirectionTypeDisplayName(SignNoDirection directionType) {
        // 使用翻译键获取显示名称
        return Text.translatable(getDirectionTypeTranslationKey(directionType)).getString();
    }

    private String getDirectionTypeTranslationKey(SignNoDirection directionType) {
        // 为每个方向类型生成对应的翻译键
        return "text.yunbeiuc.sign_no_direction." + directionType.asString();
    }

    private String getDirectionDescription(SignNoDirection directionType) {
        // 使用翻译键获取方向类型的详细描述
        return Text.translatable(getDirectionDescriptionTranslationKey(directionType)).getString();
    }

    private String getDirectionDescriptionTranslationKey(SignNoDirection directionType) {
        // 使用翻译键格式：text.yunbeiuc.sign_no_direction.xxx.description
        return "text.yunbeiuc.sign_no_direction." + directionType.asString() + ".description";
    }

    public void setSelectedDirectionType(SignNoDirection directionType) {
        this.selectedDirectionType = directionType;
    }

    private static class DirectionTypeOption {
        private final SignNoDirection directionType;
        private final String displayText;

        public DirectionTypeOption(SignNoDirection directionType, String displayText) {
            this.directionType = directionType;
            this.displayText = displayText;
        }

        public SignNoDirection getDirectionType() {
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

                // 绘制小型缩略图 (16x16像素)
                int thumbnailSize = 16; // 缩略图大小
                int thumbnailX = x + 5;
                int thumbnailY = y + (entryHeight - thumbnailSize) / 2;

                try {
                    // 获取对应方向类型的图片路径
                    String imagePath = getDirectionTypeImagePath(option.getDirectionType());
                    Identifier texture = new Identifier("yunbeiuc", imagePath);

                    // 绘制1x1像素缩放的缩略图
                    context.drawTexture(texture, thumbnailX, thumbnailY, thumbnailSize, thumbnailSize, 0, 0, 1, 1, 1, 1);
                } catch (Exception e) {
                    // 如果图片加载失败，绘制备用矩形
                    context.fill(thumbnailX, thumbnailY, thumbnailX + thumbnailSize, thumbnailY + thumbnailSize, 0x44FF0000);
                    context.drawBorder(thumbnailX, thumbnailY, thumbnailSize, thumbnailSize, 0xFFFF0000);
                }

                // 绘制文本，向右偏移给缩略图留出空间
                int textX = thumbnailX + thumbnailSize + 8; // 缩略图右侧留出8像素间距
                context.drawTextWithShadow(
                        textRenderer,
                        // 使用翻译文本
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