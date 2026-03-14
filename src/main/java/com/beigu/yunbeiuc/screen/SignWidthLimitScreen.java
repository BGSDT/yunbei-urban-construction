package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignWidthLimitBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.UpdateWidthLimitPacket;
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
import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignWidthLimit;

import java.util.ArrayList;
import java.util.List;

public class SignWidthLimitScreen extends Screen {
    private final List<WidthLimitOption> options;
    private WidthLimitListWidget listWidget;
    private SignWidthLimit selectedLimit;
    private final BlockPos blockPos;

    public SignWidthLimitScreen(Text title, BlockPos pos, SignWidthLimit currentLimit) {
        super(title);
        this.blockPos = pos;
        this.options = createWidthLimitOptions();

        // 确保使用传入的当前宽度限制
        this.selectedLimit = currentLimit;
        System.out.println("GUI创建：当前宽度限制: " + this.selectedLimit.name());
    }

    @Override
    protected void init() {
        super.init();

        // 在初始化时重新从方块实体读取当前宽度限制
        if (this.client != null && this.client.world != null) {
            BlockEntity blockEntity = this.client.world.getBlockEntity(this.blockPos);
            if (blockEntity instanceof SignWidthLimitBlockEntity widthLimitBlockEntity) {
                this.selectedLimit = widthLimitBlockEntity.getWidthLimit();
                System.out.println("GUI初始化：读取当前宽度限制: " + this.selectedLimit.name());
            }
        }

        // 列表宽度调整为屏幕的1/2
        int listWidth = this.width / 2;
        this.listWidget = new WidthLimitListWidget(
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
                Text.translatable("text.yunbeiuc.sign_width_limit.title"),
                listAreaLeft + listAreaWidth / 2,
                10,
                0xFFFFFF
        );

        // 当前选择信息在列表区域内左对齐
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_width_limit.current_selection", getWidthValue(selectedLimit)),
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
                Text.translatable("text.yunbeiuc.sign_width_limit.info_panel"),
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
        drawWidthLimitImage(context, iconX, iconY, iconSize);

        // 宽度信息移到小图标右侧
        int infoX = iconX + iconSize + 20; // 小图标右侧20像素
        int infoY = panelY + 40;

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_width_limit.width", getWidthValue(selectedLimit)),
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
                Text.translatable("text.yunbeiuc.sign_width_limit.save_button"),
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

        // 绘制长文本内容（自动换行）
        String description = Text.translatable("text.yunbeiuc.sign_width_limit.description").getString();

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

    private void drawWidthLimitImage(DrawContext context, int x, int y, int displaySize) {
        // 根据当前选择的宽度构建图片路径
        String imagePath = getWidthImagePath(selectedLimit);

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
                    Text.translatable("text.yunbeiuc.sign_width_limit.load_failed"),
                    x + displaySize/2,
                    y + displaySize/2 - 5,
                    0xFFFFFF
            );
        }
    }

    private String getWidthImagePath(SignWidthLimit limit) {
        // 根据枚举值返回对应的图片路径
        String widthNumber = limit.asString().replace("sign_width_limit_", "");
        return "textures/gui/sign_width_limit_" + widthNumber + ".png";
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
        System.out.println("保存宽度限制: " + selectedLimit.asString() + " (" + getWidthValue(selectedLimit) + " m)");
        System.out.println("方块位置: " + blockPos);

        // 发送网络数据包到服务器
        if (client != null && client.getNetworkHandler() != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            new UpdateWidthLimitPacket(blockPos, selectedLimit).write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_WIDTH_LIMIT, buf);
        }

        // 客户端预览（可选，保持即时反馈）
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            var blockEntity = client.world.getBlockEntity(blockPos);
            if (blockEntity instanceof SignWidthLimitBlockEntity) {
                ((SignWidthLimitBlockEntity) blockEntity).setWidthLimit(selectedLimit);
            }
        }
    }

    private List<WidthLimitOption> createWidthLimitOptions() {
        List<WidthLimitOption> options = new ArrayList<>();
        for (SignWidthLimit limit : SignWidthLimit.values()) {
            options.add(new WidthLimitOption(limit, getWidthValue(limit)));
        }
        return options;
    }

    private String getWidthValue(SignWidthLimit limit) {
        String numberStr = limit.asString().replace("sign_width_limit_", "");
        // 将数字字符串转换为整数，然后除以10.0得到米数（保留一位小数）
        double widthValue = Integer.parseInt(numberStr) / 10.0;
        return String.format("%.1f", widthValue);
    }

    public void setSelectedLimit(SignWidthLimit limit) {
        this.selectedLimit = limit;
    }

    private static class WidthLimitOption {
        private final SignWidthLimit limit;
        private final String displayText;

        public WidthLimitOption(SignWidthLimit limit, String displayText) {
            this.limit = limit;
            this.displayText = displayText + " m"; // 显示为 "4.5 m" 格式
        }

        public SignWidthLimit getLimit() {
            return limit;
        }

        public String getDisplayText() {
            return displayText;
        }
    }

    private class WidthLimitListWidget extends ElementListWidget<WidthLimitListWidget.Entry> {
        private final List<WidthLimitOption> options;
        private final int listWidth;

        public WidthLimitListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            this.listWidth = width;
            this.options = createWidthLimitOptions();

            for (WidthLimitOption option : options) {
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
            private final WidthLimitOption option;

            public Entry(WidthLimitOption option) {
                this.option = option;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                if (option.getLimit() == selectedLimit) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);
                } else if (hovered) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x22FFFFFF);
                }

                // 绘制小型缩略图 (16x16像素)
                int thumbnailSize = 16; // 缩略图大小
                int thumbnailX = x + 5;
                int thumbnailY = y + (entryHeight - thumbnailSize) / 2;

                try {
                    // 获取对应宽度的图片路径
                    String imagePath = getWidthImagePath(option.getLimit());
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
                        Text.of(option.getDisplayText()),
                        textX,
                        y + (entryHeight - 8) / 2,
                        0xFFFFFF
                );
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                setSelectedLimit(option.getLimit());
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