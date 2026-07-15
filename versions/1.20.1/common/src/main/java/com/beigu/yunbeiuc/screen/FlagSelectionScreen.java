package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.render.json.CustomFlag;
import com.beigu.yunbeiuc.render.json.FlagLoader;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.FlagUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class FlagSelectionScreen extends Screen {
    private final List<FlagOption> options;
    private FlagListWidget listWidget;
    private CustomFlag selectedFlag;
    private final BlockPos blockPos;

    public FlagSelectionScreen(Text title, BlockPos blockPos) {
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

        // 列表宽度调整为屏幕的1/2
        int listWidth = this.width / 2;
        this.listWidget = new FlagListWidget(
                this.client,
                listWidth,
                this.height,
                40,
                this.height - 60,
                20,
                this.options
        );

        this.addDrawableChild(this.listWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 绘制背景
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
                Text.translatable("text.yunbeiuc.flag_selection.title"),
                listAreaLeft + listAreaWidth / 2,
                10,
                0xFFFFFF
        );

        // 当前选择信息在列表区域内左对齐
        if (selectedFlag != null) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.flag_selection.current_selection", selectedFlag.getName()),
                    listAreaLeft + 10,
                    this.height - 55,
                    0xFFFFFF
            );
        }

        // 在右侧1/2区域添加预览面板
        drawPreviewPanel(context);
    }

    private void drawPreviewPanel(DrawContext context) {
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
                Text.translatable("text.yunbeiuc.flag_selection.preview_panel"),
                panelX + panelWidth / 2,
                panelY + 10,
                0xFFFFFF
        );

        if (selectedFlag != null) {
            // 计算等比例缩放后的预览图尺寸
            int previewSize = 100;
            int[] scaledDimensions = getScaledDimensions(previewSize);
            int scaledWidth = scaledDimensions[0];
            int scaledHeight = scaledDimensions[1];

            // 计算居中位置
            int previewX = panelX + (panelWidth - scaledWidth) / 2;
            int previewY = panelY + 35;

            // 绘制预览图背景框 - 贴合缩放后的图片尺寸
            context.fill(previewX - 2, previewY - 2, previewX + scaledWidth + 2, previewY + scaledHeight + 2, 0xFF000000);
            context.drawBorder(previewX - 2, previewY - 2, scaledWidth + 4, scaledHeight + 4, 0xFFFFFFFF);

            // 绘制旗帜预览图
            drawFlagPreview(context, previewX, previewY, previewSize);

            // 旗帜名称
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal(selectedFlag.getName()),
                    panelX + panelWidth / 2,
                    previewY + scaledHeight + 10,
                    0xFFFFFF
            );

            // 旗帜ID（颜色较浅）
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal("ID: " + selectedFlag.getId()),
                    panelX + panelWidth / 2,
                    previewY + scaledHeight + 25,
                    0xAAAAAA
            );

            // 保存按钮
            int saveButtonWidth = 80;
            int saveButtonHeight = 20;
            int saveButtonX = panelX + (panelWidth - saveButtonWidth) / 2;
            int saveButtonY = panelY + panelHeight - 40;

            // 绘制保存按钮背景
            context.fill(saveButtonX, saveButtonY, saveButtonX + saveButtonWidth, saveButtonY + saveButtonHeight, 0xFF555555);
            context.drawBorder(saveButtonX, saveButtonY, saveButtonWidth, saveButtonHeight, 0xFFFFFFFF);

            // 绘制保存按钮文字
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
        // 原图尺寸
        int originalWidth = 3543;
        int originalHeight = 9449;

        // 计算等比例缩放后的尺寸
        float widthRatio = (float) maxSize / originalWidth;
        float heightRatio = (float) maxSize / originalHeight;
        float scale = Math.min(widthRatio, heightRatio); // 取较小的比例保证完全显示

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        return new int[]{scaledWidth, scaledHeight};
    }

    private void drawFlagPreview(DrawContext context, int x, int y, int maxSize) {
        if (selectedFlag == null) return;

        try {
            // 直接绘制旗帜纹理
            Identifier texture = selectedFlag.getTexture();

            // 原图尺寸
            int originalWidth = 1062;
            int originalHeight = 2834;

            // 计算等比例缩放后的尺
            float widthRatio = (float) maxSize / originalWidth;
            float heightRatio = (float) maxSize / originalHeight;
            float scale = Math.min(widthRatio, heightRatio); // 取较小的比例保证完全显示

            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            // 计算居中位置
            int centeredX = x;
            int centeredY = y;

            // 绘制等比例缩放的图片
            context.drawTexture(texture, centeredX, centeredY, scaledWidth, scaledHeight, 0, 0, originalWidth, originalHeight, originalWidth, originalHeight);

        } catch (Exception e) {
            // 如果图片加载失败，绘制备用矩形
            int[] scaledDimensions = getScaledDimensions(maxSize);
            context.fill(x, y, x + scaledDimensions[0], y + scaledDimensions[1], 0x44FF0000);
            context.drawBorder(x, y, scaledDimensions[0], scaledDimensions[1], 0xFFFF0000);

            // 错误信息
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

            // 保存按钮位置
            int saveButtonWidth = 80;
            int saveButtonHeight = 20;
            int saveButtonX = panelX + (panelWidth - saveButtonWidth) / 2;
            int saveButtonY = panelY + panelHeight - 40;

            // 如果点击了保存按钮区域
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

            // 发送网络数据包到服务器
            if (client != null && client.getNetworkHandler() != null) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                new FlagUpdatePacket(blockPos, selectedFlag.getId()).write(buf);
                NetworkManager.sendToServer(ModMessages.UPDATE_FLAG, buf);
            }

            // 客户端预览（可选，保持即时反馈）
            if (client != null && client.world != null) {
                var blockEntity = client.world.getBlockEntity(blockPos);
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

    private class FlagListWidget extends ElementListWidget<FlagListWidget.Entry> {
        private final int listWidth;

        public FlagListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, List<FlagOption> flagOptions) {
            super(client, width, height, top, bottom, itemHeight);
            this.listWidth = width;

            for (FlagOption option : flagOptions) {
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
            private final FlagOption option;

            public Entry(FlagOption option) {
                this.option = option;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                if (option.getFlag() == selectedFlag) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);
                } else if (hovered) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x22FFFFFF);
                }

                // 绘制颜色方块 (16x16像素)
                int colorSize = 16;
                int colorX = x + 5;
                int colorY = y + (entryHeight - colorSize) / 2;

                // 绘制颜色方块
                context.fill(colorX, colorY, colorX + colorSize, colorY + colorSize, 0xFF000000 | option.getColor());
                context.drawBorder(colorX, colorY, colorSize, colorSize, 0xFFCCCCCC);

                // 绘制文本，向右偏移给颜色方块留出空间
                int textX = colorX + colorSize + 8;
                context.drawTextWithShadow(
                        textRenderer,
                        Text.literal(option.getDisplayName()),
                        textX,
                        y + (entryHeight - 8) / 2,
                        0xFFFFFF
                );
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                setSelectedFlag(option.getFlag());
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