package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarningBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.UpdateWarningTypePacket;
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
import com.beigu.yunbeiuc.block.custom.sign.data.SignGuideIntersectionAdvanceWarning;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionAdvanceWarningScreen extends Screen {
    private final List<WarningTypeOption> options;
    private WarningTypeListWidget listWidget;
    private SignGuideIntersectionAdvanceWarning selectedWarningType;
    private final BlockPos blockPos;
    private String selectedDirection = "north";
    private boolean directionDropdownOpen = false;
    private final String[] directions = {"north", "south", "east", "west"};

    public SignGuideIntersectionAdvanceWarningScreen(Text title, BlockPos pos, SignGuideIntersectionAdvanceWarning currentType) {
        super(title);
        this.blockPos = pos;
        this.options = createWarningTypeOptions();
        this.selectedWarningType = currentType;

        // 从当前类型中提取方向
        String typeName = selectedWarningType.asString();
        if (typeName.endsWith("_north")) selectedDirection = "north";
        else if (typeName.endsWith("_south")) selectedDirection = "south";
        else if (typeName.endsWith("_east")) selectedDirection = "east";
        else if (typeName.endsWith("_west")) selectedDirection = "west";
    }

    @Override
    protected void init() {
        super.init();

        if (this.client != null && this.client.world != null) {
            BlockEntity blockEntity = this.client.world.getBlockEntity(this.blockPos);
            if (blockEntity instanceof SignGuideIntersectionAdvanceWarningBlockEntity warningBlockEntity) {
                this.selectedWarningType = warningBlockEntity.getWarningType();
                // 从当前类型中提取方向
                String typeName = selectedWarningType.asString();
                if (typeName.endsWith("_north")) selectedDirection = "north";
                else if (typeName.endsWith("_south")) selectedDirection = "south";
                else if (typeName.endsWith("_east")) selectedDirection = "east";
                else if (typeName.endsWith("_west")) selectedDirection = "west";
            }
        }

        int listWidth = this.width / 2;
        this.listWidget = new WarningTypeListWidget(
                this.client,
                listWidth,
                this.height,
                40,
                this.height - 60,
                20
        );

        this.addDrawableChild(this.listWidget);

        // 文本编辑功能已移除 —— 不再显示 "编辑文字" 按钮
    }

    private void updateSelectedTypeWithDirection() {
        String currentName = selectedWarningType.asString();
        if (currentName.startsWith("sign_guide_intersection_advance_warning_3") ||
                currentName.startsWith("sign_guide_intersection_advance_warning_4")) {

            // 获取基础类型（去掉方向后缀）
            String baseType = getBaseTypeFromFullType(currentName);
            String newTypeName = baseType + "_" + selectedDirection;

            for (SignGuideIntersectionAdvanceWarning type : SignGuideIntersectionAdvanceWarning.values()) {
                if (type.asString().equals(newTypeName)) {
                    selectedWarningType = type;
                    break;
                }
            }
        }
    }

    private String getBaseTypeFromFullType(String fullType) {
        if (fullType.startsWith("sign_guide_intersection_advance_warning_3_")) {
            return "sign_guide_intersection_advance_warning_3";
        } else if (fullType.startsWith("sign_guide_intersection_advance_warning_4_")) {
            return "sign_guide_intersection_advance_warning_4";
        }
        return fullType;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        int listAreaWidth = this.width / 2;
        int listAreaLeft = 0;

        // 标题在左边列表上方居中
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning.title"),
                listAreaLeft + listAreaWidth / 2,
                10,
                0xFFFFFF
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning.current_selection",
                        Text.translatable(getWarningTypeTranslationKey(selectedWarningType))),
                listAreaLeft + 10,
                this.height - 55,
                0xFFFFFF
        );

        drawInfoPanel(context, mouseX, mouseY);
    }

    private void drawInfoPanel(DrawContext context, int mouseX, int mouseY) {
        int panelWidth = 200;
        int panelHeight = 230;

        int panelX = this.width / 2 + (this.width / 2 - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA333333);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning.info_panel"),
                panelX + panelWidth / 2,
                panelY + 10,
                0xFFFFFF
        );

        int iconSize = 50;
        int iconX = panelX + 15;
        int iconY = panelY + 35;

        context.fill(iconX - 2, iconY - 2, iconX + iconSize + 2, iconY + iconSize + 2, 0xFF000000);
        context.drawBorder(iconX - 2, iconY - 2, iconSize + 4, iconSize + 4, 0xFFFFFFFF);

        drawWarningTypeImage(context, iconX, iconY, iconSize);

        int infoX = iconX + iconSize + 20;
        int infoY = panelY + 40;

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning.warning_type",
                        Text.translatable(getWarningTypeTranslationKey(selectedWarningType))),
                infoX,
                infoY,
                0xFFFFFF
        );

        int infoBoxY = iconY + iconSize + 15;
        drawInfoBox(context, infoBoxY);

        // 方向选择器（只在需要时显示）
        if (needsDirectionSelection()) {
            int directionY = infoY + 25;
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning.select_direction"),
                    infoX,
                    directionY,
                    0xFFFFFF
            );

            // 绘制下拉框背景
            int dropdownX = infoX;
            int dropdownY = directionY + 15;
            int dropdownWidth = 100;
            int dropdownHeight = 20;

            context.fill(dropdownX, dropdownY, dropdownX + dropdownWidth, dropdownY + dropdownHeight, 0xFF333333);
            context.drawBorder(dropdownX, dropdownY, dropdownWidth, dropdownHeight, 0xFFFFFFFF);

            // 绘制当前选中的方向
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.direction." + selectedDirection),
                    dropdownX + 5,
                    dropdownY + 6,
                    0xFFFFFF
            );

            // 绘制下拉箭头
            int arrowX = dropdownX + dropdownWidth - 15;
            int arrowY = dropdownY + 6;
            context.fill(arrowX, arrowY, arrowX + 8, arrowY + 1, 0xFFFFFFFF);
            context.fill(arrowX + 1, arrowY + 1, arrowX + 7, arrowY + 2, 0xFFFFFFFF);
            context.fill(arrowX + 2, arrowY + 2, arrowX + 6, arrowY + 3, 0xFFFFFFFF);
            context.fill(arrowX + 3, arrowY + 3, arrowX + 5, arrowY + 4, 0xFFFFFFFF);

            // 如果下拉框打开，绘制选项列表
            if (directionDropdownOpen) {
                int optionsY = dropdownY + dropdownHeight;
                int optionsHeight = directions.length * 20;

                // 确保下拉菜单不会超出屏幕底部
                if (optionsY + optionsHeight > this.height) {
                    optionsY = dropdownY - optionsHeight;
                }

                // 绘制下拉菜单背景
                context.fill(dropdownX, optionsY, dropdownX + dropdownWidth, optionsY + optionsHeight, 0xFF222222);
                context.drawBorder(dropdownX, optionsY, dropdownWidth, optionsHeight, 0xFFFFFFFF);

                // 绘制选项
                for (int i = 0; i < directions.length; i++) {
                    int optionY = optionsY + i * 20;

                    // 鼠标悬停效果
                    if (mouseX >= dropdownX && mouseX <= dropdownX + dropdownWidth &&
                            mouseY >= optionY && mouseY < optionY + 20) {
                        context.fill(dropdownX, optionY, dropdownX + dropdownWidth, optionY + 20, 0x44FFFFFF);
                    }

                    context.drawTextWithShadow(
                            this.textRenderer,
                            Text.translatable("text.yunbeiuc.direction." + directions[i]),
                            dropdownX + 5,
                            optionY + 6,
                            0xFFFFFF
                    );
                }
            }
        }

        // 保存按钮
        int saveButtonWidth = 80;
        int saveButtonHeight = 20;
        int saveButtonX = panelX + (panelWidth - saveButtonWidth) / 2;
        int saveButtonY = panelY + panelHeight - 35;

        context.fill(saveButtonX, saveButtonY, saveButtonX + saveButtonWidth, saveButtonY + saveButtonHeight, 0xFF555555);
        context.drawBorder(saveButtonX, saveButtonY, saveButtonWidth, saveButtonHeight, 0xFFFFFFFF);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning.save_button"),
                saveButtonX + saveButtonWidth / 2,
                saveButtonY + 6,
                0xFFFFFF
        );
    }

    private boolean needsDirectionSelection() {
        String typeName = selectedWarningType.asString();
        return typeName.startsWith("sign_guide_intersection_advance_warning_3") ||
                typeName.startsWith("sign_guide_intersection_advance_warning_4");
    }

    private void drawInfoBox(DrawContext context, int topY) {
        int panelWidth = 200;
        int panelX = this.width / 2 + (this.width / 2 - panelWidth) / 2;

        int infoBoxWidth = 180;
        int infoBoxHeight = 80;
        int infoBoxX = panelX + (panelWidth - infoBoxWidth) / 2;
        int infoBoxY = topY;

        context.fill(infoBoxX, infoBoxY, infoBoxX + infoBoxWidth, infoBoxY + infoBoxHeight, 0xAA000000);
        context.drawBorder(infoBoxX, infoBoxY, infoBoxWidth, infoBoxHeight, 0xFFFFFFFF);

        String description = Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning.sign_guide_intersection_advance_warning.description").getString();
        List<String> lines = wrapText(description, infoBoxWidth - 16);
        for (int i = 0; i < lines.size() && i < 6; i++) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.of(lines.get(i)),
                    infoBoxX + 8,
                    infoBoxY + 12 + i * 10,
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

    private void drawWarningTypeImage(DrawContext context, int x, int y, int displaySize) {
        String imagePath = getWarningTypeImagePath(selectedWarningType);
        Identifier texture = new Identifier("yunbeiuc", imagePath);

        try {
            context.drawTexture(texture, x, y, displaySize, displaySize, 0, 0, 400, 400, 400, 400);
        } catch (Exception e) {
            context.fill(x, y, x + displaySize, y + displaySize, 0x44FF0000);
            context.drawBorder(x, y, displaySize, displaySize, 0xFFFF0000);
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning.load_failed"),
                    x + displaySize/2,
                    y + displaySize/2 - 5,
                    0xFFFFFF
            );
        }
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

        // 保存按钮位置
        int saveButtonWidth = 80;
        int saveButtonHeight = 20;
        int saveButtonX = panelX + (panelWidth - saveButtonWidth) / 2;
        int saveButtonY = panelY + panelHeight - 35;

        // 如果点击了保存按钮区域
        if (mouseX >= saveButtonX && mouseX <= saveButtonX + saveButtonWidth &&
                mouseY >= saveButtonY && mouseY <= saveButtonY + saveButtonHeight) {
            saveSelection();
            this.close();
            return true;
        }

        // 方向下拉框点击处理
        if (needsDirectionSelection()) {
            int directionY = infoY + 25;
            int dropdownX = infoX;
            int dropdownY = directionY + 15;
            int dropdownWidth = 100;
            int dropdownHeight = 20;

            // 点击下拉框
            if (mouseX >= dropdownX && mouseX <= dropdownX + dropdownWidth &&
                    mouseY >= dropdownY && mouseY <= dropdownY + dropdownHeight) {
                directionDropdownOpen = !directionDropdownOpen;
                return true;
            }

            // 点击下拉选项
            if (directionDropdownOpen) {
                int optionsY = dropdownY + dropdownHeight;
                int optionsHeight = directions.length * 20;

                // 检查是否超出屏幕底部
                if (optionsY + optionsHeight > this.height) {
                    optionsY = dropdownY - optionsHeight;
                }

                for (int i = 0; i < directions.length; i++) {
                    int optionY = optionsY + i * 20;
                    if (mouseX >= dropdownX && mouseX <= dropdownX + dropdownWidth &&
                            mouseY >= optionY && mouseY < optionY + 20) {
                        selectedDirection = directions[i];
                        directionDropdownOpen = false;
                        updateSelectedTypeWithDirection();
                        return true;
                    }
                }
            }
        }

        // 点击其他地方关闭下拉框
        if (directionDropdownOpen) {
            directionDropdownOpen = false;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // 保存按钮逻辑，保存到BlockEntity
    private void saveSelection() {
        System.out.println("保存路口预告类型: " + selectedWarningType.asString());

        if (client != null && client.getNetworkHandler() != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            new UpdateWarningTypePacket(blockPos, selectedWarningType).write(buf);
            ClientPlayNetworking.send(ModMessages.UPDATE_WARNING_TYPE, buf);
        }

        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.world != null) {
            var blockEntity = client.world.getBlockEntity(blockPos);
            if (blockEntity instanceof com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarningBlockEntity) {
                com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarningBlockEntity entity = (com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarningBlockEntity) blockEntity;
                entity.setWarningType(selectedWarningType);
                // 文本编辑功能已移除：不再修改文本/颜色/字号
             }
         }
    }

    private List<WarningTypeOption> createWarningTypeOptions() {
        List<WarningTypeOption> options = new ArrayList<>();

        // 添加基础类型（不包含方向变体）
        options.add(new WarningTypeOption(SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1, "sign_guide_intersection_advance_warning_1"));
        options.add(new WarningTypeOption(SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2, "sign_guide_intersection_advance_warning_2"));
        // 类型3只显示基础类型
        options.add(new WarningTypeOption(SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_NORTH, "sign_guide_intersection_advance_warning_3"));
        // 类型4只显示基础类型
        options.add(new WarningTypeOption(SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4_NORTH, "sign_guide_intersection_advance_warning_4"));
        options.add(new WarningTypeOption(SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5, "sign_guide_intersection_advance_warning_5"));
        options.add(new WarningTypeOption(SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6, "sign_guide_intersection_advance_warning_6"));
        options.add(new WarningTypeOption(SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7, "sign_guide_intersection_advance_warning_7"));
        options.add(new WarningTypeOption(SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_8, "sign_guide_intersection_advance_warning_8"));

        return options;
    }

    private String getWarningTypeDisplayName(SignGuideIntersectionAdvanceWarning warningType) {
        return Text.translatable(getWarningTypeTranslationKey(warningType)).getString();
    }

    private String getWarningTypeTranslationKey(SignGuideIntersectionAdvanceWarning warningType) {
        // 对于类型3和4，使用基础类型的翻译键
        String typeName = warningType.asString();
        if (typeName.startsWith("sign_guide_intersection_advance_warning_3_")) {
            return "text.yunbeiuc.sign_guide_intersection_advance_warning.sign_guide_intersection_advance_warning_3";
        } else if (typeName.startsWith("sign_guide_intersection_advance_warning_4_")) {
            return "text.yunbeiuc.sign_guide_intersection_advance_warning.sign_guide_intersection_advance_warning_4";
        }
        return "text.yunbeiuc.sign_guide_intersection_advance_warning." + typeName;
    }

    private String getWarningDescriptionTranslationKey(SignGuideIntersectionAdvanceWarning warningType) {
        // 对于类型3和4，使用基础类型的描述
        String typeName = warningType.asString();
        if (typeName.startsWith("sign_guide_intersection_advance_warning_3_")) {
            return "text.yunbeiuc.sign_guide_intersection_advance_warning.sign_guide_intersection_advance_warning_3.description";
        } else if (typeName.startsWith("sign_guide_intersection_advance_warning_4_")) {
            return "text.yunbeiuc.sign_guide_intersection_advance_warning.sign_guide_intersection_advance_warning_4.description";
        }
        return "text.yunbeiuc.sign_guide_intersection_advance_warning." + typeName + ".description";
    }

    private String getWarningTypeImagePath(SignGuideIntersectionAdvanceWarning warningType) {
        String vehicleName = warningType.asString();
        return "textures/gui/" + vehicleName + ".png";
    }

    public void setSelectedWarningType(SignGuideIntersectionAdvanceWarning warningType) {
        this.selectedWarningType = warningType;

        // 更新方向选择
        String typeName = warningType.asString();
        if (typeName.endsWith("_north")) selectedDirection = "north";
        else if (typeName.endsWith("_south")) selectedDirection = "south";
        else if (typeName.endsWith("_east")) selectedDirection = "east";
        else if (typeName.endsWith("_west")) selectedDirection = "west";
    }

    private static class WarningTypeOption {
        private final SignGuideIntersectionAdvanceWarning warningType;
        private final String baseTypeName;

        public WarningTypeOption(SignGuideIntersectionAdvanceWarning warningType, String baseTypeName) {
            this.warningType = warningType;
            this.baseTypeName = baseTypeName;
        }

        public SignGuideIntersectionAdvanceWarning getWarningType() {
            return warningType;
        }

        public String getBaseTypeName() {
            return baseTypeName;
        }
    }

    private class WarningTypeListWidget extends ElementListWidget<WarningTypeListWidget.Entry> {
        private final List<WarningTypeOption> options;
        private final int listWidth;

        public WarningTypeListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            this.listWidth = width;
            this.options = createWarningTypeOptions();

            for (WarningTypeOption option : options) {
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
            private final WarningTypeOption option;

            public Entry(WarningTypeOption option) {
                this.option = option;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                // 检查是否为当前选择的类型（考虑方向变体）
                boolean isSelected = isCurrentTypeSelected(option.getWarningType());

                if (isSelected) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x33FFFFFF);
                } else if (hovered) {
                    context.fill(x, y, x + entryWidth, y + entryHeight, 0x22FFFFFF);
                }

                int thumbnailSize = 16;
                int thumbnailX = x + 5;
                int thumbnailY = y + (entryHeight - thumbnailSize) / 2;

                try {
                    String imagePath = getWarningTypeImagePath(option.getWarningType());
                    Identifier texture = new Identifier("yunbeiuc", imagePath);
                    context.drawTexture(texture, thumbnailX, thumbnailY, thumbnailSize, thumbnailSize, 0, 0, 1, 1, 1, 1);
                } catch (Exception e) {
                    context.fill(thumbnailX, thumbnailY, thumbnailX + thumbnailSize, thumbnailY + thumbnailSize, 0x44FF0000);
                    context.drawBorder(thumbnailX, thumbnailY, thumbnailSize, thumbnailSize, 0xFFFF0000);
                }

                int textX = thumbnailX + thumbnailSize + 8;
                context.drawTextWithShadow(
                        textRenderer,
                        Text.translatable("text.yunbeiuc.sign_guide_intersection_advance_warning." + option.getBaseTypeName()),
                        textX,
                        y + (entryHeight - 8) / 2,
                        0xFFFFFF
                );
            }

            private boolean isCurrentTypeSelected(SignGuideIntersectionAdvanceWarning optionType) {
                String currentBaseType = getBaseTypeFromFullType(selectedWarningType.asString());
                String optionBaseType = getBaseTypeFromFullType(optionType.asString());
                return currentBaseType.equals(optionBaseType);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                setSelectedWarningType(option.getWarningType());
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
