package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.util.TrafficLightsPatternCategoryManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.Random;

/**
 * 新建二级菜单（分类）弹窗：名称输入框 + 颜色色块（点击复用 {@link ColorPickerScreen}）。
 * 只用于"新建"，不支持编辑已有分类（固定分类不可改，用户分类目前也不支持改名/改色，
 * 与需求"二级菜单可以新建，可以自定义颜色"一致，未额外扩展）。
 */
public class TrafficLightsPatternCategoryEditorScreen extends Screen {
    private static final Random RANDOM = new Random();
    private static final int PANEL_WIDTH = 220;
    private static final int PANEL_HEIGHT = 130;
    private static final int COLOR_SWATCH_SIZE = 16;

    private final Screen previousScreen;
    private int categoryColor;
    private TextFieldWidget nameField;
    private Text errorMessage = null;

    private int panelX;
    private int panelY;
    private int colorSwatchX;
    private int colorSwatchY;

    public TrafficLightsPatternCategoryEditorScreen(Screen previousScreen) {
        super(Text.literal("新建分类"));
        this.previousScreen = previousScreen;
        this.categoryColor = RANDOM.nextInt(0x1000000);
    }

    @Override
    protected void init() {
        super.init();
        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = (this.height - PANEL_HEIGHT) / 2;
        colorSwatchX = panelX + 20;
        colorSwatchY = panelY + 50;

        nameField = new TextFieldWidget(this.textRenderer, panelX + 20, panelY + 26, PANEL_WIDTH - 40, 20, Text.literal(""));
        nameField.setMaxLength(24);
        nameField.setChangedListener(text -> errorMessage = null);
        this.addDrawableChild(nameField);
        this.setInitialFocus(nameField);

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("保存"), button -> saveAndClose())
                        .dimensions(panelX + 20, panelY + PANEL_HEIGHT - 30, (PANEL_WIDTH - 50) / 2, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("取消"), button -> this.close())
                        .dimensions(panelX + 30 + (PANEL_WIDTH - 50) / 2, panelY + PANEL_HEIGHT - 30, (PANEL_WIDTH - 50) / 2, 20)
                        .build()
        );
    }

    private void saveAndClose() {
        String name = nameField.getText().trim();
        String error = TrafficLightsPatternCategoryManager.addCategory(name, categoryColor);
        if (error != null) {
            errorMessage = Text.literal("§c" + error);
            return;
        }
        MinecraftClient.getInstance().setScreen(previousScreen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA333333);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFFCCCCCC);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, panelX + PANEL_WIDTH / 2, panelY + 8, 0xFFCCCCCC);

        context.drawTextWithShadow(this.textRenderer, Text.literal("颜色:"), colorSwatchX, colorSwatchY + 4, 0xFFAAAAAA);
        context.fill(colorSwatchX + 34, colorSwatchY, colorSwatchX + 34 + COLOR_SWATCH_SIZE, colorSwatchY + COLOR_SWATCH_SIZE, 0xFF000000 | categoryColor);
        context.drawBorder(colorSwatchX + 34, colorSwatchY, COLOR_SWATCH_SIZE, COLOR_SWATCH_SIZE, 0xFF888888);

        if (errorMessage != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, errorMessage, panelX + PANEL_WIDTH / 2, panelY + PANEL_HEIGHT - 48, 0xFFFF5555);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int swatchX = colorSwatchX + 34;
            if (mouseX >= swatchX && mouseX < swatchX + COLOR_SWATCH_SIZE && mouseY >= colorSwatchY && mouseY < colorSwatchY + COLOR_SWATCH_SIZE) {
                MinecraftClient.getInstance().setScreen(new ColorPickerScreen(this, categoryColor, c -> categoryColor = c));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(previousScreen);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
