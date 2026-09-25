package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.text.Text;

import com.beigu.yunbeiuc.util.TrafficLightsPatternCategoryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * 相位预设"一级菜单"：列出所有二级菜单（分类），选中后进入该分类查看/管理其中的预设
 * （原本的预设选择界面 {@link TrafficLightsPatternSelectScreen} 现在作为二级菜单使用）。
 * 入口：已完成时间表设置的红绿灯（TrafficLightsScreen）的"使用相位预设"按钮。
 */
public class TrafficLightsPatternCategorySelectScreen extends Screen {
    private static final int RIGHT_PANEL_WIDTH = 220;
    private static final int RIGHT_PANEL_HEIGHT = 170;

    private final BlockPos pos;
    private final List<String> categoryNames = new ArrayList<>();
    private String selectedCategory;
    private Component errorMessage = null;

    private CategoryListWidget listWidget;
    private ButtonWidget enterButton;
    private ButtonWidget deleteButton;
    private int panelX;
    private int panelY;

    public TrafficLightsPatternCategorySelectScreen(BlockPos pos) {
        super(Text.literal("选择相位预设分类"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();
        categoryNames.clear();
        categoryNames.addAll(TrafficLightsPatternCategoryManager.getCategoryNames());
        if (selectedCategory != null && !categoryNames.contains(selectedCategory)) {
            selectedCategory = null;
        }

        int listWidth = this.width / 3;
        this.listWidget = new CategoryListWidget(
                this.client,
                listWidth,
                this.height,
                40,
                this.height - 60,
                20,
                categoryNames
        );
        this.addDrawableChild(this.listWidget);

        int rightAreaX = this.width / 3;
        int rightAreaWidth = this.width * 2 / 3;
        this.panelX = rightAreaX + (rightAreaWidth - RIGHT_PANEL_WIDTH) / 2;
        this.panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;

        int buttonWidth = RIGHT_PANEL_WIDTH - 40;
        this.enterButton = ButtonWidget.builderCompat(Text.literal("进入分类"), button -> enterCategory())
                .dimensions(panelX + 20, panelY + 50, buttonWidth, 20)
                .build();
        this.enterButton.active = selectedCategory != null;
        this.addDrawableChild(this.enterButton);

        this.addDrawableChild(
                ButtonWidget.builderCompat(Text.literal("新建分类"), button ->
                        Minecraft.getInstance().setScreen(new TrafficLightsPatternCategoryEditorScreen(this)))
                        .dimensions(panelX + 20, panelY + 78, buttonWidth, 20)
                        .build()
        );

        this.deleteButton = ButtonWidget.builderCompat(Text.literal("删除分类"), button -> deleteCategory())
                .dimensions(panelX + 20, panelY + 106, buttonWidth, 20)
                .build();
        this.deleteButton.active = selectedCategory != null && TrafficLightsPatternCategoryManager.canDelete(selectedCategory);
        this.addDrawableChild(this.deleteButton);

        this.addDrawableChild(
                ButtonWidget.builderCompat(Text.literal("取消"), button -> this.close())
                        .dimensions(panelX + 20, panelY + 134, buttonWidth, 20)
                        .build()
        );
    }

    private void setSelectedCategory(String name) {
        this.selectedCategory = name;
        this.errorMessage = null;
        if (this.enterButton != null) {
            this.enterButton.active = name != null;
        }
        if (this.deleteButton != null) {
            this.deleteButton.active = name != null && TrafficLightsPatternCategoryManager.canDelete(name);
        }
    }

    private void rebuild() {
        this.clearChildren();
        this.init();
    }

    private void enterCategory() {
        if (selectedCategory == null) {
            errorMessage = Text.literal("§c请先在左侧选择一个分类！");
            return;
        }
        Minecraft.getInstance().setScreen(new TrafficLightsPatternSelectScreen(pos, selectedCategory, this));
    }

    private void setButtonsActive(boolean active) {
        if (this.enterButton != null) this.enterButton.active = active;
        if (this.deleteButton != null) this.deleteButton.active = active;
        for (var child : this.children()) {
            if (child instanceof ButtonWidget button) {
                button.active = active;
            }
        }
    }

    private void deleteCategory() {
        if (selectedCategory == null) {
            errorMessage = Text.literal("§c请先在左侧选择一个分类！");
            return;
        }
        if (!TrafficLightsPatternCategoryManager.canDelete(selectedCategory)) {
            errorMessage = Text.literal("§c该分类不可删除（固定分类或分类内仍有预设）！");
            return;
        }
        TrafficLightsPatternCategoryManager.removeCategory(selectedCategory);
        selectedCategory = null;
        rebuild();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int listAreaWidth = this.width / 3;
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                listAreaWidth / 2,
                10,
                0xFFFFFF
        );

        context.fill(panelX, panelY, panelX + RIGHT_PANEL_WIDTH, panelY + RIGHT_PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, RIGHT_PANEL_WIDTH, RIGHT_PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("已选择: " + (selectedCategory != null ? selectedCategory : "无")),
                panelX + RIGHT_PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        if (errorMessage != null) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    errorMessage,
                    listAreaWidth / 2,
                    this.height - 40,
                    0xFFFF5555
            );
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private class CategoryListWidget extends AbstractOptionListWidget<String> {
        public CategoryListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight,
                                   List<String> names) {
            super(client, width, height, top, bottom, itemHeight, names,
                    name -> name.equals(selectedCategory),
                    TrafficLightsPatternCategorySelectScreen.this::setSelectedCategory,
                    Text::literal,
                    TrafficLightsPatternCategoryManager::getColor);
        }
    }
}
