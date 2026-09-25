package com.beigu.yunbeiuc.screen;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPatternPreset;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.TrafficLightsPatternApplyPacket;
import com.beigu.yunbeiuc.util.TrafficLightsPatternPresetLoader;
import com.beigu.yunbeiuc.util.TrafficLightsPatternPresetManager;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * 相位分配预设选择/应用界面（二级菜单）：列出所属分类下已保存的预设，选中后应用到点击的
 * 红绿灯所在链接组。服务端会做"完美嵌入"校验，全部匹配才写入，否则回复失败原因，
 * 不会做任何修改。
 * 入口：一级菜单 {@link TrafficLightsPatternCategorySelectScreen} 选中分类后"进入分类"。
 */
public class TrafficLightsPatternSelectScreen extends Screen {
    private static final int RIGHT_PANEL_WIDTH = 220;
    private static final int RIGHT_PANEL_HEIGHT = 210;

    private final BlockPos pos;
    private final String category;
    private final Screen previousScreen;
    private final List<String> presetNames = new ArrayList<>();
    private String selectedName;
    private Component errorMessage = null;

    private NameListWidget listWidget;
    private ButtonWidget editButton;
    private ButtonWidget deleteButton;
    private ButtonWidget newButton;
    private int panelX;
    private int panelY;

    public TrafficLightsPatternSelectScreen(BlockPos pos, String category, Screen previousScreen) {
        super(new TextComponent("使用相位分配预设 - " + category));
        this.pos = pos;
        this.category = category;
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        super.init();
        presetNames.clear();
        // 判断当前分类是否为资源包内置分类（非用户创建）
        boolean isBuiltInCategory = TrafficLightsPatternPresetLoader.isBuiltInCategory(category);
        if (isBuiltInCategory) {
            // 资源包内置分类：仅从 TrafficLightsPatternPresetLoader 中筛选该分类下的预设
            for (var entry : TrafficLightsPatternPresetLoader.getPresets().entrySet()) {
                if (category.equals(entry.getValue().getCategory())) {
                    presetNames.add(entry.getKey());
                }
            }
        } else {
            // 用户创建分类或固定分类"默认分类"：从 TrafficLightsPatternPresetManager 中筛选
            for (var entry : TrafficLightsPatternPresetManager.getPresets().entrySet()) {
                if (category.equals(entry.getValue().getCategory())) {
                    presetNames.add(entry.getKey());
                }
            }
        }
        if (selectedName != null && !presetNames.contains(selectedName)) {
            selectedName = null;
        }

        int listWidth = this.width / 3;
        this.listWidget = new NameListWidget(
                this.client,
                listWidth,
                this.height,
                40,
                this.height - 60,
                20,
                presetNames
        );
        this.addDrawableChild(this.listWidget);

        int rightAreaX = this.width / 3;
        int rightAreaWidth = this.width * 2 / 3;
        this.panelX = rightAreaX + (rightAreaWidth - RIGHT_PANEL_WIDTH) / 2;
        this.panelY = (this.height - RIGHT_PANEL_HEIGHT) / 2;

        int buttonWidth = RIGHT_PANEL_WIDTH - 40;
        this.addDrawableChild(
                ButtonWidget.builder(new TextComponent("应用到该组"), button -> applyPreset())
                        .dimensions(panelX + 20, panelY + 50, buttonWidth, 20)
                        .build()
        );
        this.editButton = ButtonWidget.builder(new TextComponent("编辑预设"), button -> openPatternEditor(true))
                .dimensions(panelX + 20, panelY + 78, buttonWidth, 20)
                .build();
        this.editButton.active = selectedName != null && !TrafficLightsPatternPresetLoader.isBuiltIn(selectedName);
        this.addDrawableChild(this.editButton);
        this.deleteButton = ButtonWidget.builder(new TextComponent("删除预设"), button -> deletePreset())
                .dimensions(panelX + 20, panelY + 106, buttonWidth, 20)
                .build();
        this.deleteButton.active = selectedName != null && !TrafficLightsPatternPresetLoader.isBuiltIn(selectedName);
        this.addDrawableChild(this.deleteButton);
        // 资源包内置分类只承载资源包内置预设，不允许在此新建用户预设
        this.newButton = ButtonWidget.builder(new TextComponent("新建预设"), button -> openPatternEditor(false))
                .dimensions(panelX + 20, panelY + 134, buttonWidth, 20)
                .build();
        this.newButton.active = !isBuiltInCategory;
        this.addDrawableChild(this.newButton);
        this.addDrawableChild(
                ButtonWidget.builder(new TextComponent("返回"), button -> this.close())
                        .dimensions(panelX + 20, panelY + 162, buttonWidth, 20)
                        .build()
        );
    }

    private void setSelectedName(String name) {
        this.selectedName = name;
        this.errorMessage = null;
        boolean editable = selectedName != null && !TrafficLightsPatternPresetLoader.isBuiltIn(selectedName);
        if (this.deleteButton != null) {
            this.deleteButton.active = editable;
        }
        if (this.editButton != null) {
            this.editButton.active = editable;
        }
    }

    private TrafficLightsPatternPreset findPreset(String name) {
        TrafficLightsPatternPreset preset = TrafficLightsPatternPresetLoader.getPresets().get(name);
        if (preset == null) {
            preset = TrafficLightsPatternPresetManager.getPresets().get(name);
        }
        return preset;
    }

    private void rebuild() {
        this.clearChildren();
        this.init();
    }

    private void applyPreset() {
        if (selectedName == null) {
            errorMessage = new TextComponent("§c请先在左侧选择一个预设！");
            return;
        }
        TrafficLightsPatternPreset preset = findPreset(selectedName);
        if (preset == null) {
            errorMessage = new TextComponent("§c预设不存在！");
            return;
        }
        TrafficLightsPatternApplyPacket packet = new TrafficLightsPatternApplyPacket(pos, preset);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(buf);
        NetworkManager.sendToServer(ModMessages.APPLY_TRAFFIC_LIGHTS_PATTERN, buf);
        this.close();
    }

    private void deletePreset() {
        if (selectedName == null) {
            errorMessage = new TextComponent("§c请先在左侧选择一个预设！");
            return;
        }
        if (TrafficLightsPatternPresetLoader.isBuiltIn(selectedName)) {
            errorMessage = new TextComponent("§c内置预设不可删除！");
            return;
        }
        TrafficLightsPatternPresetManager.removePreset(selectedName);
        selectedName = null;
        rebuild();
    }

    private void openPatternEditor(boolean editSelected) {
        if (editSelected) {
            if (selectedName == null) {
                errorMessage = new TextComponent("§c请先在左侧选择一个预设！");
                return;
            }
            if (TrafficLightsPatternPresetLoader.isBuiltIn(selectedName)) {
                errorMessage = new TextComponent("§c内置预设不可编辑！");
                return;
            }
            TrafficLightsPatternPreset preset = findPreset(selectedName);
            if (preset == null) {
                errorMessage = new TextComponent("§c预设不存在！");
                return;
            }
            Minecraft.getInstance().setScreen(new TrafficLightsPatternEditorScreen(preset, this.pos, this.category, this));
            return;
        }
        Minecraft.getInstance().setScreen(new TrafficLightsPatternEditorScreen(this.pos, this.category, this));
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
                new TextComponent("已选择: " + (selectedName != null ? selectedName : "无")),
                panelX + RIGHT_PANEL_WIDTH / 2,
                panelY + 12,
                0xFFCCCCCC
        );

        if (presetNames.isEmpty()) {
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    new TextComponent("§7该分类暂无预设"),
                    panelX + RIGHT_PANEL_WIDTH / 2,
                    panelY + 32,
                    0xFF888888
            );
        }

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
    public void close() {
        Minecraft.getInstance().setScreen(previousScreen);
    }

    private void setButtonsActive(boolean active) {
        for (var child : this.children()) {
            if (child instanceof ButtonWidget button) {
                button.active = active;
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private class NameListWidget extends AbstractOptionListWidget<String> {
        public NameListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight,
                               List<String> names) {
            super(client, width, height, top, bottom, itemHeight, names,
                    name -> name.equals(selectedName),
                    TrafficLightsPatternSelectScreen.this::setSelectedName,
                    TextComponent::new,
                    name -> {
                        TrafficLightsPatternPreset preset = findPreset(name);
                        return preset != null ? preset.getDisplayColor() : TrafficLightsPatternPreset.hashColor(name);
                    });
        }
    }
}
