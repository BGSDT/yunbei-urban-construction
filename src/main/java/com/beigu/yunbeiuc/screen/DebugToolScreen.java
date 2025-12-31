package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.TransformableBlockEntity;
import com.beigu.yunbeiuc.network.EntityConversionPacket;
import com.beigu.yunbeiuc.network.TransformUpdatePacket;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugToolScreen extends Screen {

    private final BlockPos pos;
    private BlockState state;
    private final MinecraftClient client = MinecraftClient.getInstance();

    private final List<String> propertyNames = new ArrayList<>();
    private final Map<String, List<String>> propertyValues = new HashMap<>();
    private final Map<String, Property<?>> propertyObjects = new HashMap<>();

    private int currentPropertyIndex = 0;
    private int currentValueIndex = 0;

    // 实体模式相关
    private boolean entityMode = false;
    private BlockState originalState;

    // 实体变换数据
    private float posX = 0f, posY = 0f, posZ = 0f;
    private float rotX = 0f, rotY = 0f, rotZ = 0f;
    private float scale = 1.0f;

    // layout constants
    private final int arrowW = 20;
    private final int labelW = 50;
    private final int btnH = 20;
    private final int spacing = 5;
    private final int panelPadding = 6;

    // 右侧面板尺寸
    private final int rightPanelWidth = 120;
    private final int rightPanelX = 150;

    // scrolling state for long texts
    private int propScrollIndex = 0;
    private int valScrollIndex = 0;
    private long propLastShift = 0L;
    private long valLastShift = 0L;
    private final long scrollDelayMs = 250L;

    // 实体控制按钮
    private ButtonWidget toggleEntityBtn;
    private ButtonWidget posXMinusBtn, posXPlusBtn;
    private ButtonWidget posYMinusBtn, posYPlusBtn;
    private ButtonWidget posZMinusBtn, posZPlusBtn;
    private ButtonWidget rotXMinusBtn, rotXPlusBtn;
    private ButtonWidget rotYMinusBtn, rotYPlusBtn;
    private ButtonWidget rotZMinusBtn, rotZPlusBtn;
    private ButtonWidget scaleMinusBtn, scalePlusBtn;
    private ButtonWidget resetBtn;

    public DebugToolScreen(BlockState state, BlockPos pos) {
        super(Text.literal("Debug Tool"));
        this.state = state;
        this.pos = pos;
        this.originalState = state;

        // 初始化方块属性
        StateManager<?, ?> manager = state.getBlock().getStateManager();
        for (Property<?> prop : manager.getProperties()) {
            String name = prop.getName();
            propertyNames.add(name);
            propertyObjects.put(name, prop);
            List<String> vals = new ArrayList<>();
            for (Object v : prop.getValues()) {
                vals.add(v.toString());
            }
            propertyValues.put(name, vals);
        }

        if (!propertyNames.isEmpty()) {
            String curPropName = propertyNames.get(0);
            Property<?> p = propertyObjects.get(curPropName);
            if (p != null) {
                Comparable<?> curVal = state.get(p);
                List<String> vals = propertyValues.get(curPropName);
                if (vals != null) {
                    currentValueIndex = Math.max(0, vals.indexOf(curVal.toString()));
                }
            }
        }

        // 检查是否已经是实体模式
        checkEntityMode();
    }

    private void checkEntityMode() {
        if (client.world != null) {
            var blockEntity = client.world.getBlockEntity(pos);
            if (blockEntity instanceof TransformableBlockEntity transformable) {
                entityMode = transformable.isEntityMode();
                if (entityMode) {
                    // 加载实体变换数据
                    posX = transformable.getPosX();
                    posY = transformable.getPosY();
                    posZ = transformable.getPosZ();
                    rotX = transformable.getRotX();
                    rotY = transformable.getRotY();
                    rotZ = transformable.getRotZ();
                    scale = transformable.getScale();
                    originalState = transformable.getOriginalState();
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        // 左侧属性控制面板
        initPropertyControls();

        // 右侧实体控制面板
        initEntityControls();
    }

    private void initPropertyControls() {
        int panelX = 10;
        int panelHeight = panelPadding * 2 + btnH * 2 + spacing;
        int panelY = this.height - panelHeight;

        int propX = panelX + panelPadding;
        int propY = panelY + panelPadding;
        int valY = propY + btnH + spacing;

        // property prev
        ButtonWidget propPrev = ButtonWidget.builder(Text.literal("‹"), button -> {
            if (propertyNames.isEmpty()) return;
            currentPropertyIndex = (currentPropertyIndex - 1 + propertyNames.size()) % propertyNames.size();
            String newProp = getCurrentPropertyName();
            Property<?> p = propertyObjects.get(newProp);
            if (p != null) {
                Comparable<?> curVal = state.get(p);
                List<String> vals = propertyValues.get(newProp);
                if (vals != null) {
                    currentValueIndex = Math.max(0, vals.indexOf(curVal.toString()));
                } else currentValueIndex = 0;
            }
            propScrollIndex = 0;
            propLastShift = 0L;
            updateButtonLabels();
        }).dimensions(propX, propY, arrowW, btnH).build();

        // property next
        ButtonWidget propNext = ButtonWidget.builder(Text.literal("›"), button -> {
            if (propertyNames.isEmpty()) return;
            currentPropertyIndex = (currentPropertyIndex + 1) % propertyNames.size();
            String newProp = getCurrentPropertyName();
            Property<?> p = propertyObjects.get(newProp);
            if (p != null) {
                Comparable<?> curVal = state.get(p);
                List<String> vals = propertyValues.get(newProp);
                if (vals != null) {
                    currentValueIndex = Math.max(0, vals.indexOf(curVal.toString()));
                } else currentValueIndex = 0;
            }
            propScrollIndex = 0;
            propLastShift = 0L;
            updateButtonLabels();
        }).dimensions(propX + arrowW + labelW, propY, arrowW, btnH).build();

        // value prev
        ButtonWidget valPrev = ButtonWidget.builder(Text.literal("‹"), button -> {
            if (entityMode) return; // 实体模式下禁用属性修改
            String curProp = getCurrentPropertyName();
            List<String> vals = propertyValues.get(curProp);
            if (vals == null || vals.isEmpty()) return;
            currentValueIndex = (currentValueIndex - 1 + vals.size()) % vals.size();
            applyCurrentSelectionToWorld();
            valScrollIndex = 0;
            valLastShift = 0L;
            updateButtonLabels();
        }).dimensions(propX, valY, arrowW, btnH).build();

        // value next
        ButtonWidget valNext = ButtonWidget.builder(Text.literal("›"), button -> {
            if (entityMode) return; // 实体模式下禁用属性修改
            String curProp = getCurrentPropertyName();
            List<String> vals = propertyValues.get(curProp);
            if (vals == null || vals.isEmpty()) return;
            currentValueIndex = (currentValueIndex + 1) % vals.size();
            applyCurrentSelectionToWorld();
            valScrollIndex = 0;
            valLastShift = 0L;
            updateButtonLabels();
        }).dimensions(propX + arrowW + labelW, valY, arrowW, btnH).build();

        this.addDrawableChild(propPrev);
        this.addDrawableChild(propNext);
        this.addDrawableChild(valPrev);
        this.addDrawableChild(valNext);
    }

    private void initEntityControls() {
        int startY = 50;
        int buttonWidth = 20;
        int valueWidth = 60;
        int spacing = 5;

        // 切换实体模式按钮
        toggleEntityBtn = ButtonWidget.builder(
                Text.literal(entityMode ? "恢复方块" : "转为实体"),
                this::toggleEntityMode
        ).dimensions(rightPanelX, startY, rightPanelWidth, btnH).build();
        this.addDrawableChild(toggleEntityBtn);

        startY += btnH + spacing * 2;

        // 位置控制
        addControlRow("X位置:", rightPanelX, startY, buttonWidth, valueWidth,
                (btn) -> adjustPosition(0, -0.1f),
                (btn) -> adjustPosition(0, 0.1f));
        startY += btnH + spacing;

        addControlRow("Y位置:", rightPanelX, startY, buttonWidth, valueWidth,
                (btn) -> adjustPosition(1, -0.1f),
                (btn) -> adjustPosition(1, 0.1f));
        startY += btnH + spacing;

        addControlRow("Z位置:", rightPanelX, startY, buttonWidth, valueWidth,
                (btn) -> adjustPosition(2, -0.1f),
                (btn) -> adjustPosition(2, 0.1f));
        startY += btnH + spacing * 2;

        // 旋转控制
        addControlRow("X旋转:", rightPanelX, startY, buttonWidth, valueWidth,
                (btn) -> adjustRotation(0, -15f),
                (btn) -> adjustRotation(0, 15f));
        startY += btnH + spacing;

        addControlRow("Y旋转:", rightPanelX, startY, buttonWidth, valueWidth,
                (btn) -> adjustRotation(1, -15f),
                (btn) -> adjustRotation(1, 15f));
        startY += btnH + spacing;

        addControlRow("Z旋转:", rightPanelX, startY, buttonWidth, valueWidth,
                (btn) -> adjustRotation(2, -15f),
                (btn) -> adjustRotation(2, 15f));
        startY += btnH + spacing * 2;

        // 缩放控制
        addControlRow("缩放:", rightPanelX, startY, buttonWidth, valueWidth,
                (btn) -> adjustScale(-0.1f),
                (btn) -> adjustScale(0.1f));
        startY += btnH + spacing * 2;

        // 重置按钮
        resetBtn = ButtonWidget.builder(Text.literal("重置变换"), this::resetTransforms)
                .dimensions(rightPanelX, startY, rightPanelWidth, btnH).build();
        this.addDrawableChild(resetBtn);

        updateEntityControlsVisibility();
    }

    private void addControlRow(String label, int x, int y, int btnWidth, int valueWidth,
                               ButtonWidget.PressAction minusAction, ButtonWidget.PressAction plusAction) {
        // 减号按钮
        ButtonWidget minusBtn = ButtonWidget.builder(Text.literal("-"), minusAction)
                .dimensions(x, y, btnWidth, btnH).build();

        // 加号按钮
        ButtonWidget plusBtn = ButtonWidget.builder(Text.literal("+"), plusAction)
                .dimensions(x + btnWidth + valueWidth, y, btnWidth, btnH).build();

        this.addDrawableChild(minusBtn);
        this.addDrawableChild(plusBtn);

        // 根据label类型存储按钮引用
        switch (label) {
            case "X位置:" -> { posXMinusBtn = minusBtn; posXPlusBtn = plusBtn; }
            case "Y位置:" -> { posYMinusBtn = minusBtn; posYPlusBtn = plusBtn; }
            case "Z位置:" -> { posZMinusBtn = minusBtn; posZPlusBtn = plusBtn; }
            case "X旋转:" -> { rotXMinusBtn = minusBtn; rotXPlusBtn = plusBtn; }
            case "Y旋转:" -> { rotYMinusBtn = minusBtn; rotYPlusBtn = plusBtn; }
            case "Z旋转:" -> { rotZMinusBtn = minusBtn; rotZPlusBtn = plusBtn; }
            case "缩放:" -> { scaleMinusBtn = minusBtn; scalePlusBtn = plusBtn; }
        }
    }

    private void toggleEntityMode(ButtonWidget button) {
        entityMode = !entityMode;

        if (entityMode) {
            // 转换为实体模式
            convertToEntity();
            toggleEntityBtn.setMessage(Text.literal("恢复方块"));
        } else {
            // 恢复为普通方块
            revertToBlock();
            toggleEntityBtn.setMessage(Text.literal("转为实体"));
        }

        updateEntityControlsVisibility();
    }

    private void convertToEntity() {
        System.out.println("=== 开始转换为实体模式 ===");
        checkEntityStatus(); // 转换前检查

        // 发送网络包转换实体
        sendEntityConversionPacket(true);

        // 延迟检查，等待网络同步
        client.execute(() -> {
            try {
                Thread.sleep(100); // 等待100ms让网络包处理
                System.out.println("转换后检查:");
                checkEntityStatus();

                // 在这里更新本地状态，确保服务器已处理
                if (client.world != null) {
                    var blockEntity = client.world.getBlockEntity(pos);
                    if (blockEntity instanceof TransformableBlockEntity transformable) {
                        // 加载已有的变换数据
                        posX = transformable.getPosX();
                        posY = transformable.getPosY();
                        posZ = transformable.getPosZ();
                        rotX = transformable.getRotX();
                        rotY = transformable.getRotY();
                        rotZ = transformable.getRotZ();
                        scale = transformable.getScale();
                        System.out.println("已加载变换数据");
                    } else {
                        System.out.println("转换后仍未找到 TransformableBlockEntity");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void revertToBlock() {
        // 发送网络包恢复方块
        sendEntityConversionPacket(false);

        // 重置变换数据
        resetTransforms();
    }

    private void adjustPosition(int axis, float amount) {
        switch (axis) {
            case 0 -> posX += amount;
            case 1 -> posY += amount;
            case 2 -> posZ += amount;
        }
        updateEntityTransform();
    }

    private void adjustRotation(int axis, float amount) {
        switch (axis) {
            case 0 -> rotX = (rotX + amount) % 360f;
            case 1 -> rotY = (rotY + amount) % 360f;
            case 2 -> rotZ = (rotZ + amount) % 360f;
        }
        updateEntityTransform();
    }

    private void adjustScale(float amount) {
        scale = Math.max(0.1f, scale + amount);
        updateEntityTransform();
    }

    private void resetTransforms(ButtonWidget button) {
        resetTransforms();
    }

    private void resetTransforms() {
        posX = posY = posZ = 0f;
        rotX = rotY = rotZ = 0f;
        scale = 1.0f;
        updateEntityTransform();
    }

    private void updateEntityTransform() {
        if (!entityMode) return;
        sendTransformUpdatePacket();
    }

    private void sendEntityConversionPacket(boolean toEntity) {
        EntityConversionPacket.send(pos, toEntity, originalState);
    }

    private void sendTransformUpdatePacket() {
        TransformUpdatePacket.send(pos, posX, posY, posZ, rotX, rotY, rotZ, scale);
    }

    private void updateEntityControlsVisibility() {
        boolean visible = entityMode;

        // 设置所有实体控制按钮的可见性
        if (posXMinusBtn != null) posXMinusBtn.visible = visible;
        if (posXPlusBtn != null) posXPlusBtn.visible = visible;
        if (posYMinusBtn != null) posYMinusBtn.visible = visible;
        if (posYPlusBtn != null) posYPlusBtn.visible = visible;
        if (posZMinusBtn != null) posZMinusBtn.visible = visible;
        if (posZPlusBtn != null) posZPlusBtn.visible = visible;
        if (rotXMinusBtn != null) rotXMinusBtn.visible = visible;
        if (rotXPlusBtn != null) rotXPlusBtn.visible = visible;
        if (rotYMinusBtn != null) rotYMinusBtn.visible = visible;
        if (rotYPlusBtn != null) rotYPlusBtn.visible = visible;
        if (rotZMinusBtn != null) rotZMinusBtn.visible = visible;
        if (rotZPlusBtn != null) rotZPlusBtn.visible = visible;
        if (scaleMinusBtn != null) scaleMinusBtn.visible = visible;
        if (scalePlusBtn != null) scalePlusBtn.visible = visible;
        if (resetBtn != null) resetBtn.visible = visible;
    }

    private String getCurrentPropertyName() {
        if (propertyNames.isEmpty()) return "<no-prop>";
        if (currentPropertyIndex < 0 || currentPropertyIndex >= propertyNames.size()) currentPropertyIndex = 0;
        return propertyNames.get(currentPropertyIndex);
    }

    private String getCurrentValueString() {
        String prop = getCurrentPropertyName();
        List<String> vals = propertyValues.get(prop);
        if (vals == null || vals.isEmpty()) return "<no-val>";
        if (currentValueIndex < 0 || currentValueIndex >= vals.size()) currentValueIndex = 0;
        return vals.get(currentValueIndex);
    }

    private void updateButtonLabels() {
        // 更新按钮标签的逻辑（如果需要）
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void applyCurrentSelectionToWorld() {
        if (client.world == null || entityMode) return; // 实体模式下不修改方块状态

        String propName = getCurrentPropertyName();
        Property<?> prop = propertyObjects.get(propName);
        if (prop == null) return;
        List<String> vals = propertyValues.get(propName);
        if (vals == null || vals.isEmpty()) return;
        String target = vals.get(currentValueIndex);

        Comparable chosen = null;
        for (Object o : prop.getValues()) {
            if (o.toString().equals(target)) {
                chosen = (Comparable) o;
                break;
            }
        }
        if (chosen == null) return;

        try {
            BlockState newState = state.with((Property) prop, chosen);
            client.world.setBlockState(pos, newState, 3);
            this.state = newState;
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 绘制背景
        renderBackground(context);

        // 绘制左侧属性控制面板
        renderPropertyPanel(context);

        // 绘制右侧实体控制面板
        renderEntityPanel(context);

        // 渲染子组件
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderPropertyPanel(DrawContext context) {
        int panelX = 10;
        int panelHeight = panelPadding * 2 + btnH * 2 + spacing;
        int panelY = this.height - panelHeight;
        int panelWidth = arrowW + labelW + arrowW + panelPadding * 2;

        // 面板背景
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA000000);

        // 绘制标签文本
        int propLabelLeftX = panelX + panelPadding + arrowW;
        int propLabelY = panelY + panelPadding + (btnH - 8) / 2;
        int valLabelLeftX = propLabelLeftX;
        int valLabelY = panelY + panelPadding + btnH + spacing + (btnH - 8) / 2;

        String propText = getCurrentPropertyName();
        String valText = getCurrentValueString();

        int maxPixelWidth = labelW - 4;
        int propWidth = this.client.textRenderer.getWidth(propText);
        if (propWidth <= maxPixelWidth) {
            int x = propLabelLeftX + (labelW - propWidth) / 2;
            context.drawTextWithShadow(this.client.textRenderer, Text.literal(propText), x, propLabelY, 0xFFFFFF);
        } else {
            long now = System.currentTimeMillis();
            if (propLastShift == 0L) propLastShift = now;
            if (now - propLastShift >= scrollDelayMs) {
                propScrollIndex = (propScrollIndex + 1) % propText.length();
                propLastShift = now;
            }
            String wrapped = propText.substring(propScrollIndex) + "   " + propText;
            String shown = this.client.textRenderer.trimToWidth(wrapped, maxPixelWidth);
            int x = propLabelLeftX + 2;
            context.drawTextWithShadow(this.client.textRenderer, Text.literal(shown), x, propLabelY, 0xFFFFFF);
        }

        int valWidth = this.client.textRenderer.getWidth(valText);
        if (valWidth <= maxPixelWidth) {
            int x = valLabelLeftX + (labelW - valWidth) / 2;
            context.drawTextWithShadow(this.client.textRenderer, Text.literal(valText), x, valLabelY, 0xFFFFFF);
        } else {
            long now = System.currentTimeMillis();
            if (valLastShift == 0L) valLastShift = now;
            if (now - valLastShift >= scrollDelayMs) {
                valScrollIndex = (valScrollIndex + 1) % valText.length();
                valLastShift = now;
            }
            String wrapped = valText.substring(valScrollIndex) + "   " + valText;
            String shown = this.client.textRenderer.trimToWidth(wrapped, maxPixelWidth);
            int x = valLabelLeftX + 2;
            context.drawTextWithShadow(this.client.textRenderer, Text.literal(shown), x, valLabelY, 0xFFFFFF);
        }
    }

    private void renderEntityPanel(DrawContext context) {
        if (entityMode) {
            // 添加实时预览说明
            int previewY = 180;
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal("✓ 实时预览已启用"),
                    rightPanelX, previewY, 0x55FF55);
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal("调整参数立即看到效果"),
                    rightPanelX, previewY + 12, 0xAAAAAA);

            // 显示当前变换值
            int infoY = 200;
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal(String.format("位置: %.1f, %.1f, %.1f", posX, posY, posZ)),
                    rightPanelX, infoY, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal(String.format("旋转: %.1f, %.1f, %.1f", rotX, rotY, rotZ)),
                    rightPanelX, infoY + 12, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal(String.format("缩放: %.1f", scale)),
                    rightPanelX, infoY + 24, 0xAAAAAA);

            // 绘制控制标签
            drawControlLabels(context);
        }

        // 实体面板背景
        int entityPanelHeight = this.height - 80;
        context.fill(rightPanelX - 10, 30, rightPanelX + rightPanelWidth + 10, entityPanelHeight, 0xAA333333);

        // 标题
        context.drawTextWithShadow(client.textRenderer, Text.literal("实体精确控制"), rightPanelX, 35, 0xFFFFFF);

        // 当前模式状态
        String modeText = entityMode ? "实体模式" : "方块模式";
        context.drawTextWithShadow(client.textRenderer, Text.literal("模式: " + modeText), rightPanelX, 50,
                entityMode ? 0x55FF55 : 0xFF5555);

        // 显示当前变换值
        if (entityMode) {
            int infoY = 200;
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal(String.format("位置: %.1f, %.1f, %.1f", posX, posY, posZ)),
                    rightPanelX, infoY, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal(String.format("旋转: %.1f, %.1f, %.1f", rotX, rotY, rotZ)),
                    rightPanelX, infoY + 12, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer,
                    Text.literal(String.format("缩放: %.1f", scale)),
                    rightPanelX, infoY + 24, 0xAAAAAA);

            // 绘制控制标签
            drawControlLabels(context);
        }

        int debugY = 160;
        if (entityMode) {
            // 检查方块实体是否存在
            boolean hasBlockEntity = false;
            TransformableBlockEntity blockEntity = null;

            if (client.world != null) {
                var be = client.world.getBlockEntity(pos);
                if (be instanceof TransformableBlockEntity) {
                    hasBlockEntity = true;
                    blockEntity = (TransformableBlockEntity) be;
                }
            }

            context.drawTextWithShadow(client.textRenderer,
                    Text.literal("实体状态: " + (hasBlockEntity ? "✓ 已加载" : "✗ 未找到")),
                    rightPanelX, debugY, hasBlockEntity ? 0x55FF55 : 0xFF5555);

            if (hasBlockEntity && blockEntity != null) {
                context.drawTextWithShadow(client.textRenderer,
                        Text.literal("原始方块: " + blockEntity.getOriginalState().getBlock().getName().getString()),
                        rightPanelX, debugY + 12, 0xAAAAAA);
            }
        }
    }

    private void drawControlLabels(DrawContext context) {
        int startY = 85;
        int buttonWidth = 20;
        int valueWidth = 60;

        String[] labels = {"X位置:", "Y位置:", "Z位置:", "X旋转:", "Y旋转:", "Z旋转:", "缩放:"};

        for (String label : labels) {
            context.drawTextWithShadow(client.textRenderer, Text.literal(label),
                    rightPanelX + buttonWidth + 5, startY + 5, 0xFFFFFF);
            startY += btnH + spacing;

            if (label.equals("Z位置:") || label.equals("Z旋转:")) {
                startY += spacing; // 额外间距
            }
        }
    }

    private void checkEntityStatus() {
        if (client.world != null) {
            var blockEntity = client.world.getBlockEntity(pos);
            System.out.println("=== 实体状态检查 ===");
            System.out.println("位置: " + pos);
            System.out.println("方块实体: " + blockEntity);
            System.out.println("类型: " + (blockEntity != null ? blockEntity.getClass().getSimpleName() : "null"));

            if (blockEntity instanceof TransformableBlockEntity transformable) {
                System.out.println("实体模式: " + transformable.isEntityMode());
                System.out.println("原始方块: " + transformable.getOriginalState());
                System.out.println("变换数据: " + transformable.getPosX() + ", " + transformable.getPosY() + ", " + transformable.getPosZ());
            }
        }
    }
}