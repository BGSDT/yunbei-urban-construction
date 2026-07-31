package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.beigu.yunbeiuc.network.CustomSignUpdatePacket;
import com.beigu.yunbeiuc.util.PresetManager;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static com.beigu.yunbeiuc.network.ModMessages.UPDATE_CUSTOM_SIGN;

public class CustomSignScreen extends Screen {
    private static final int PANEL_TOP_HEIGHT = 20;
    private static final int PANEL_BOTTOM_HEIGHT_RATIO = 5;
    private static final int ADD_BUTTON_WIDTH = 20;
    private static final int BTN_SIZE = 20;
    private static final int BTN_GAP = 5;
    private static final int MAX_VISIBLE_TABS = 8;
    private static final int SCROLL_BTN_WIDTH = 14;
    private static final int SAVE_BTN_ROW_HEIGHT = 22;

    private final CustomSignBlockEntity blockEntity;
    private final BlockPos blockPos;
    private final List<TextLineWidget> textLineWidgets = new ArrayList<>();
    private int selectedIndex = -1;

    private final List<ButtonWidget> textButtons = new ArrayList<>();
    private ButtonWidget addLineButton;

    private TextFieldWidget textField;
    private ButtonWidget xButton, yButton, zButton, fontSizeButton, colorButton;
    private ButtonWidget boldButton, italicButton, underlineButton, shadowButton;
    private ButtonWidget hAlignButton, vAlignButton, clearFormatButton;

    private boolean preciseInputMode = false;
    private TextFieldWidget preciseInputField;
    private ButtonWidget backButton;
    private int preciseInputType = 0;

    private boolean presetSelectMode = false;
    private boolean presetSaveMode = false;
    private boolean presetLoadMode = false;
    private final Set<Integer> selectedPresetIndices = new HashSet<>();
    private ButtonWidget savePresetButton;
    private TextFieldWidget presetNameField;
    private ButtonWidget confirmSaveButton, cancelPresetButton, cancelLoadButton;
    private final List<ButtonWidget> presetButtons = new ArrayList<>();
    private int presetScrollOffset = 0;
    private ButtonWidget presetScrollUp, presetScrollDown;

    private int topScrollOffset = 0;
    private ButtonWidget topScrollLeft, topScrollRight;

    private int panelTopX, panelTopY, panelTopWidth, panelTopHeight;
    private int panelBottomX, panelBottomY, panelBottomWidth, panelBottomHeight;

    public CustomSignScreen(CustomSignBlockEntity blockEntity) {
        super(Text.translatable("gui.yunbeiuc.custom_sign"));
        this.blockEntity = blockEntity;
        this.blockPos = blockEntity.getPos();
    }

    @Override
    protected void init() {
        super.init();

        int sw = this.width, sh = this.height;

        panelBottomHeight = sh / PANEL_BOTTOM_HEIGHT_RATIO;
        panelBottomWidth = sw;
        panelBottomX = 0;
        panelBottomY = sh - panelBottomHeight;

        panelTopHeight = PANEL_TOP_HEIGHT;
        panelTopX = 0;
        panelTopY = panelBottomY - panelTopHeight - SAVE_BTN_ROW_HEIGHT;
        panelTopWidth = sw - ADD_BUTTON_WIDTH;

        savePresetButton = ButtonWidget.builder(Text.literal("保存为预设"), btn -> {
            if (selectedPresetIndices.isEmpty()) return;
            presetSaveMode = true; presetSelectMode = false;
            refreshBottomPanel(); refreshTopPanel();
        }).dimensions(sw / 2 - 40, panelTopY + panelTopHeight + 1, 80, 20).build();
        savePresetButton.visible = false;

        addLineButton = ButtonWidget.builder(Text.literal("+"), button -> {
            if (presetSelectMode || presetSaveMode || presetLoadMode) return;
            TextLineData newData = new TextLineData("Text");
            textLineWidgets.add(new TextLineWidget(newData));
            blockEntity.getTextLines().add(newData);
            selectedIndex = textLineWidgets.size() - 1;
            topScrollOffset = Math.max(0, textLineWidgets.size() - MAX_VISIBLE_TABS);
            refreshTopPanel(); refreshBottomPanel();
            syncAndUpdateClient();
            sendUpdateToServer();
        }).dimensions(panelTopX + panelTopWidth, panelTopY, ADD_BUTTON_WIDTH, panelTopHeight).build();

        createBottomPanelWidgets();
        initializeTextLines();
        refreshTopPanel(); refreshBottomPanel();
        this.addDrawableChild(addLineButton);
        this.addDrawableChild(savePresetButton);
    }

    private void createBottomPanelWidgets() {
        textField = new TextFieldWidget(this.textRenderer, 0, 0, panelBottomWidth - 10, 16, Text.literal("Text"));
        textField.setMaxLength(Integer.MAX_VALUE);
        textField.setChangedListener(text -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                textLineWidgets.get(selectedIndex).data.setText(text);
                refreshTopPanel();
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        });

        xButton = makeXYZButton("X", 0); yButton = makeXYZButton("Y", 1); zButton = makeXYZButton("Z", 4);
        fontSizeButton = ButtonWidget.builder(Text.literal("S"), button -> {
            if (hasControlDown()) enterPreciseMode(2);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                d.setFontSize(Math.max(0.1f, d.getFontSize() + (hasAltDown() ? 1f/32f : 1f/16f)));
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        int[] colors = {0xFFFFFF, 0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF, 0xFFA500, 0x000000};
        colorButton = ButtonWidget.builder(Text.literal("■"), button -> {
            if (hasControlDown()) enterPreciseMode(3);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                int ci = -1; for (int i = 0; i < colors.length; i++) if (colors[i] == d.getColor()) { ci = i; break; }
                d.setColor(colors[(ci + 1) % colors.length]);
                colorButton.setMessage(Text.literal("■").styled(s -> s.withColor(d.getColor())));
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        boldButton = makeToggle("B", s -> s.withBold(true), d -> { d.setBold(!d.isBold()); syncAndUpdateClient(); });
        italicButton = makeToggle("I", s -> s.withItalic(true), d -> { d.setItalic(!d.isItalic()); syncAndUpdateClient(); });
        underlineButton = makeToggle("U", s -> s.withUnderline(true), d -> { d.setUnderline(!d.isUnderline()); syncAndUpdateClient(); });
        shadowButton = makeToggle("D", s -> s.withBold(true), d -> { d.setShadow(!d.isShadow()); syncAndUpdateClient(); });

        hAlignButton = ButtonWidget.builder(Text.literal("水平居中"), button -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                int nh = (d.getAlignment().hAlign + 1) % 3;
                d.setAlignment(getAlignment(nh, d.getAlignment().vAlign));
                hAlignButton.setMessage(Text.literal(getHAlignText(nh)));
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE + 40, BTN_SIZE).build();

        vAlignButton = ButtonWidget.builder(Text.literal("垂直居中"), button -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                int nv = (d.getAlignment().vAlign + 1) % 3;
                d.setAlignment(getAlignment(d.getAlignment().hAlign, nv));
                vAlignButton.setMessage(Text.literal(getVAlignText(nv)));
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE + 40, BTN_SIZE).build();

        clearFormatButton = ButtonWidget.builder(Text.literal("✕"), button -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                d.setBold(false); d.setItalic(false); d.setUnderline(false); d.setShadow(false);
                d.setColor(0xFFFFFF); d.setFontSize(1.0f); d.setAlignment(CustomSignBlockEntity.TextAlignment.CENTER_CENTER);
                colorButton.setMessage(Text.literal("■").styled(s -> s.withColor(0xFFFFFF)));
                hAlignButton.setMessage(Text.literal("水平居中")); vAlignButton.setMessage(Text.literal("垂直居中"));
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private ButtonWidget makeXYZButton(String label, int type) {
        return ButtonWidget.builder(Text.literal(label), button -> {
            if (hasControlDown()) enterPreciseMode(type);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                float step = hasAltDown() ? 0.5f : 1.0f;
                switch (type) { case 0 -> d.setXOffset(d.getXOffset() + step); case 1 -> d.setYOffset(d.getYOffset() + step); case 4 -> d.setZOffset(d.getZOffset() + step); }
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private ButtonWidget makeToggle(String label, java.util.function.UnaryOperator<net.minecraft.text.Style> sf, java.util.function.Consumer<TextLineData> action) {
        return ButtonWidget.builder(Text.literal(label).styled(sf), btn -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                action.accept(textLineWidgets.get(selectedIndex).data);
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private CustomSignBlockEntity.TextAlignment getAlignment(int h, int v) {
        for (var a : CustomSignBlockEntity.TextAlignment.values()) if (a.hAlign == h && a.vAlign == v) return a;
        return CustomSignBlockEntity.TextAlignment.CENTER_CENTER;
    }
    private String getHAlignText(int h) { return switch (h) { case 0 -> "左对齐"; case 1 -> "水平居中"; case 2 -> "右对齐"; default -> "水平居中"; }; }
    private String getVAlignText(int v) { return switch (v) { case 0 -> "顶部对齐"; case 1 -> "垂直居中"; case 2 -> "底部对齐"; default -> "垂直居中"; }; }

    private void enterPreciseMode(int type) { preciseInputMode = true; preciseInputType = type; refreshBottomPanel(); }
    private void exitPreciseMode() { preciseInputMode = false; refreshBottomPanel(); }

    private void createPreciseInputWidgets() {
        preciseInputField = new TextFieldWidget(textRenderer, 0, 0, panelBottomWidth - 60, 16, Text.literal(""));
        preciseInputField.setMaxLength(Integer.MAX_VALUE);
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            var d = textLineWidgets.get(selectedIndex).data;
            preciseInputField.setText(switch (preciseInputType) {
                case 0 -> String.format("%.1f", d.getXOffset()); case 1 -> String.format("%.1f", d.getYOffset());
                case 2 -> String.format("%.2f", d.getFontSize()); case 3 -> String.format("#%06X", d.getColor());
                case 4 -> String.format("%.1f", d.getZOffset()); default -> "0";
            });
        }
        preciseInputField.setChangedListener(text -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                var d = textLineWidgets.get(selectedIndex).data;
                try {
                    switch (preciseInputType) {
                        case 0 -> d.setXOffset(Float.parseFloat(text)); case 1 -> d.setYOffset(Float.parseFloat(text));
                        case 2 -> d.setFontSize(Math.max(0.1f, Float.parseFloat(text)));
                        case 3 -> { String hex = text.replace("#", "").trim(); if (hex.length() == 6) { d.setColor(Integer.parseInt(hex, 16)); colorButton.setMessage(Text.literal("■").styled(s -> s.withColor(d.getColor()))); } }
                        case 4 -> d.setZOffset(Float.parseFloat(text));
                    }
                } catch (NumberFormatException ignored) {}
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        });
        backButton = ButtonWidget.builder(Text.literal("←"), btn -> exitPreciseMode()).dimensions(0, 0, 20, 20).build();
    }

    private void initializeTextLines() {
        textLineWidgets.clear();
        for (var data : blockEntity.getTextLines()) {
            textLineWidgets.add(new TextLineWidget(data));
        }
        if (!textLineWidgets.isEmpty()) { selectedIndex = 0; updateBottomPanelDisplay(); }
    }

    private void syncAndUpdateClient() {
        List<TextLineData> updatedLines = new ArrayList<>();
        for (TextLineWidget widget : textLineWidgets) {
            updatedLines.add(widget.data);
        }
        blockEntity.getTextLines().clear();
        blockEntity.getTextLines().addAll(updatedLines);

        if (MinecraftClient.getInstance().world != null) {
            blockEntity.markDirty();
            MinecraftClient.getInstance().worldRenderer.updateBlock(
                    null, blockPos, null, null, 0
            );
        }
    }

    private boolean isAnyTextFieldFocused() {
        if (textField != null && textField.isFocused()) return true;
        if (preciseInputField != null && preciseInputField.isFocused()) return true;
        if (presetNameField != null && presetNameField.isFocused()) return true;
        return false;
    }

    private void refreshTopPanel() {
        for (var btn : textButtons) this.remove(btn);
        textButtons.clear();
        if (topScrollLeft != null) { this.remove(topScrollLeft); topScrollLeft = null; }
        if (topScrollRight != null) { this.remove(topScrollRight); topScrollRight = null; }

        if (textLineWidgets.isEmpty()) {
            selectedIndex = -1; topScrollOffset = 0;
        } else {
            int count = textLineWidgets.size();
            if (topScrollOffset > Math.max(0, count - MAX_VISIBLE_TABS)) topScrollOffset = Math.max(0, count - MAX_VISIBLE_TABS);
            if (topScrollOffset < 0) topScrollOffset = 0;

            int visibleCount = Math.min(MAX_VISIBLE_TABS, count);
            int spacing = 2;
            int availableWidth = panelTopWidth;
            if (count > MAX_VISIBLE_TABS) availableWidth -= SCROLL_BTN_WIDTH * 2 + 8;

            int btnWidth = Math.max(20, (availableWidth - (visibleCount + 1) * spacing) / visibleCount);
            int btnHeight = panelTopHeight - 4;
            int startX = panelTopX + spacing;

            if (count > MAX_VISIBLE_TABS) {
                startX += SCROLL_BTN_WIDTH + 4;
                topScrollLeft = ButtonWidget.builder(Text.literal("◀"), b -> {
                    if (topScrollOffset > 0) { topScrollOffset--; refreshTopPanel(); }
                }).dimensions(panelTopX + 2, panelTopY + panelTopHeight / 2 - 10, SCROLL_BTN_WIDTH, 20).build();
                this.addDrawableChild(topScrollLeft);
                topScrollRight = ButtonWidget.builder(Text.literal("▶"), b -> {
                    if (topScrollOffset < count - MAX_VISIBLE_TABS) { topScrollOffset++; refreshTopPanel(); }
                }).dimensions(panelTopX + panelTopWidth - SCROLL_BTN_WIDTH - 2, panelTopY + panelTopHeight / 2 - 10, SCROLL_BTN_WIDTH, 20).build();
                this.addDrawableChild(topScrollRight);
            }

            for (int i = 0; i < visibleCount; i++) {
                int idx = topScrollOffset + i;
                if (idx >= count) break;
                String displayText = textLineWidgets.get(idx).data.getText();
                if (displayText.isEmpty()) displayText = "(empty)";

                ButtonWidget btn = ButtonWidget.builder(Text.literal(displayText), button -> {
                    if (presetSelectMode && !presetSaveMode && !presetLoadMode) {
                        if (selectedPresetIndices.contains(idx)) selectedPresetIndices.remove(idx);
                        else selectedPresetIndices.add(idx);
                        refreshTopPanel();
                    } else if (!presetSaveMode && !presetLoadMode) {
                        selectedIndex = idx; preciseInputMode = false; refreshBottomPanel();
                    }
                }).dimensions(startX + i * (btnWidth + spacing), panelTopY + 2, btnWidth, btnHeight).build();
                textButtons.add(btn); this.addDrawableChild(btn);
            }
        }
        if (selectedIndex >= textLineWidgets.size()) selectedIndex = textLineWidgets.isEmpty() ? -1 : textLineWidgets.size() - 1;
        savePresetButton.visible = presetSelectMode && !selectedPresetIndices.isEmpty() && !presetSaveMode && !presetLoadMode;
    }

    private void refreshBottomPanel() {
        this.remove(textField); this.remove(xButton); this.remove(yButton); this.remove(zButton);
        this.remove(fontSizeButton); this.remove(colorButton); this.remove(boldButton); this.remove(italicButton);
        this.remove(underlineButton); this.remove(shadowButton); this.remove(hAlignButton); this.remove(vAlignButton);
        this.remove(clearFormatButton);
        if (preciseInputField != null) this.remove(preciseInputField);
        if (backButton != null) this.remove(backButton);
        if (presetNameField != null) this.remove(presetNameField);
        if (confirmSaveButton != null) this.remove(confirmSaveButton);
        if (cancelPresetButton != null) this.remove(cancelPresetButton);
        if (cancelLoadButton != null) this.remove(cancelLoadButton);
        for (var btn : presetButtons) this.remove(btn);
        presetButtons.clear();
        if (presetScrollUp != null) this.remove(presetScrollUp);
        if (presetScrollDown != null) this.remove(presetScrollDown);

        if (presetSaveMode) addPresetSaveWidgets();
        else if (presetLoadMode) addPresetLoadWidgets();
        else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            if (preciseInputMode) { addPreciseInputWidgets(); }
            else { addBottomWidgets(); updateBottomPanelDisplay(); }
        }
    }

    private void updateBottomPanelDisplay() {
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return;
        var d = textLineWidgets.get(selectedIndex).data;
        textField.setText(d.getText());
        colorButton.setMessage(Text.literal("■").styled(s -> s.withColor(d.getColor())));
        hAlignButton.setMessage(Text.literal(getHAlignText(d.getAlignment().hAlign)));
        vAlignButton.setMessage(Text.literal(getVAlignText(d.getAlignment().vAlign)));
    }

    private void addBottomWidgets() {
        int lh = (panelBottomHeight - 10) / 2;
        int y1 = panelBottomY + 5, y2 = panelBottomY + 5 + lh, cx = panelBottomX + 5;
        textField.setWidth(panelBottomWidth - 10); textField.setPosition(cx, y1); this.addDrawableChild(textField);
        cx = panelBottomX + 5;
        xButton.setPosition(cx, y2); this.addDrawableChild(xButton); cx += BTN_SIZE + BTN_GAP;
        yButton.setPosition(cx, y2); this.addDrawableChild(yButton); cx += BTN_SIZE + BTN_GAP;
        zButton.setPosition(cx, y2); this.addDrawableChild(zButton); cx += BTN_SIZE + BTN_GAP;
        fontSizeButton.setPosition(cx, y2); this.addDrawableChild(fontSizeButton); cx += BTN_SIZE + BTN_GAP;
        colorButton.setPosition(cx, y2); this.addDrawableChild(colorButton); cx += BTN_SIZE + BTN_GAP;
        boldButton.setPosition(cx, y2); this.addDrawableChild(boldButton); cx += BTN_SIZE + BTN_GAP;
        italicButton.setPosition(cx, y2); this.addDrawableChild(italicButton); cx += BTN_SIZE + BTN_GAP;
        underlineButton.setPosition(cx, y2); this.addDrawableChild(underlineButton); cx += BTN_SIZE + BTN_GAP;
        shadowButton.setPosition(cx, y2); this.addDrawableChild(shadowButton); cx += BTN_SIZE + BTN_GAP;
        hAlignButton.setPosition(cx, y2); this.addDrawableChild(hAlignButton); cx += BTN_SIZE + 40 + BTN_GAP;
        vAlignButton.setPosition(cx, y2); this.addDrawableChild(vAlignButton); cx += BTN_SIZE + 40 + BTN_GAP;
        clearFormatButton.setPosition(cx, y2); this.addDrawableChild(clearFormatButton);
    }

    private void addPreciseInputWidgets() {
        createPreciseInputWidgets();
        int y = panelBottomY + (panelBottomHeight - 20) / 2;
        preciseInputField.setPosition(panelBottomX + 30, y); this.addDrawableChild(preciseInputField);
        backButton.setPosition(panelBottomX + 5, y); this.addDrawableChild(backButton);
    }

    private void addPresetSaveWidgets() {
        presetNameField = new TextFieldWidget(textRenderer, panelBottomX + 5, panelBottomY + 10, panelBottomWidth - 10, 16, Text.literal("预设名称"));
        presetNameField.setMaxLength(Integer.MAX_VALUE);
        this.addDrawableChild(presetNameField);
        confirmSaveButton = ButtonWidget.builder(Text.literal("保存"), btn -> {
            String name = presetNameField.getText().trim();
            if (name.isEmpty()) return;
            List<TextLineData> lines = new ArrayList<>();
            for (int idx : selectedPresetIndices) lines.add(textLineWidgets.get(idx).data.copy());
            PresetManager.addPreset(name, lines);
            selectedPresetIndices.clear(); presetSaveMode = false;
            refreshTopPanel(); refreshBottomPanel();
        }).dimensions(panelBottomX + panelBottomWidth / 2 - 40, panelBottomY + 35, 35, 20).build();
        this.addDrawableChild(confirmSaveButton);
        cancelPresetButton = ButtonWidget.builder(Text.literal("取消"), btn -> {
            selectedPresetIndices.clear(); presetSaveMode = false; refreshTopPanel(); refreshBottomPanel();
        }).dimensions(panelBottomX + panelBottomWidth / 2 + 5, panelBottomY + 35, 35, 20).build();
        this.addDrawableChild(cancelPresetButton);
    }

    private void addPresetLoadWidgets() {
        var presets = PresetManager.getPresets();
        if (presets.isEmpty()) {
            cancelLoadButton = ButtonWidget.builder(Text.literal("返回"), btn -> { presetLoadMode = false; refreshBottomPanel(); })
                    .dimensions(panelBottomX + panelBottomWidth / 2 - 20, panelBottomY + panelBottomHeight / 2 + 10, 40, 20).build();
            this.addDrawableChild(cancelLoadButton);
            return;
        }
        List<String> names = new ArrayList<>(presets.keySet());
        int perPage = 4;
        int btnW = (panelBottomWidth - 10) / Math.min(perPage, names.size());
        int btnH = panelBottomHeight - 30;
        int maxOffset = Math.max(0, names.size() - perPage);
        if (presetScrollOffset > maxOffset) presetScrollOffset = maxOffset;

        for (int i = 0; i < Math.min(perPage, names.size() - presetScrollOffset); i++) {
            String name = names.get(presetScrollOffset + i);
            ButtonWidget btn = ButtonWidget.builder(Text.literal(name), b -> {
                List<TextLineData> loaded = new ArrayList<>();
                for (var d : presets.get(name)) loaded.add(d.copy());
                blockEntity.getTextLines().addAll(loaded);
                for (var d : loaded) textLineWidgets.add(new TextLineWidget(d));
                selectedIndex = textLineWidgets.size() - loaded.size();
                topScrollOffset = Math.max(0, textLineWidgets.size() - MAX_VISIBLE_TABS);
                presetLoadMode = false;
                refreshTopPanel(); refreshBottomPanel();
                syncAndUpdateClient();
                sendUpdateToServer();
            }).dimensions(panelBottomX + 5 + i * btnW, panelBottomY + 5, btnW - 2, btnH).build();
            presetButtons.add(btn); this.addDrawableChild(btn);
        }

        if (names.size() > perPage) {
            presetScrollUp = ButtonWidget.builder(Text.literal("◀"), b -> { if (presetScrollOffset > 0) { presetScrollOffset--; refreshBottomPanel(); } })
                    .dimensions(panelBottomX + 2, panelBottomY + panelBottomHeight - 25, 12, 20).build();
            presetScrollDown = ButtonWidget.builder(Text.literal("▶"), b -> { if (presetScrollOffset < maxOffset) { presetScrollOffset++; refreshBottomPanel(); } })
                    .dimensions(panelBottomX + panelBottomWidth - 14, panelBottomY + panelBottomHeight - 25, 12, 20).build();
            this.addDrawableChild(presetScrollUp); this.addDrawableChild(presetScrollDown);
        }
        cancelLoadButton = ButtonWidget.builder(Text.literal("返回"), btn -> { presetLoadMode = false; refreshBottomPanel(); })
                .dimensions(panelBottomX + panelBottomWidth / 2 - 20, panelBottomY + panelBottomHeight - 25, 40, 20).build();
        this.addDrawableChild(cancelLoadButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isAnyTextFieldFocused()) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (preciseInputMode && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER)) {
            exitPreciseMode();
            return true;
        }
        if (presetSaveMode || presetLoadMode) return super.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            presetSelectMode = true;
            refreshTopPanel();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_P) {
            presetLoadMode = true;
            presetScrollOffset = 0;
            refreshBottomPanel();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (isAnyTextFieldFocused()) {
            return super.keyReleased(keyCode, scanCode, modifiers);
        }

        if ((keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) && !presetSaveMode) {
            presetSelectMode = false; selectedPresetIndices.clear(); refreshTopPanel();
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (preciseInputMode) { if (backButton != null && backButton.isMouseOver(mouseX, mouseY)) { exitPreciseMode(); return true; } return super.mouseClicked(mouseX, mouseY, button); }
        if (presetSaveMode || presetLoadMode) return super.mouseClicked(mouseX, mouseY, button);
        if (button == 1 && !presetSelectMode) {
            if (xButton != null && xButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(0); return true; }
            if (yButton != null && yButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(1); return true; }
            if (zButton != null && zButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(4); return true; }
            if (fontSizeButton != null && fontSizeButton.isMouseOver(mouseX, mouseY)) { adjustFontSize(); return true; }
            for (int i = 0; i < textButtons.size(); i++) {
                var btn = textButtons.get(i);
                if (mouseX >= btn.getX() && mouseX < btn.getX() + btn.getWidth() && mouseY >= btn.getY() && mouseY < btn.getY() + btn.getHeight()) {
                    int actualIdx = topScrollOffset + i;
                    if (actualIdx < textLineWidgets.size()) {
                        blockEntity.getTextLines().remove(actualIdx); textLineWidgets.remove(actualIdx);
                        selectedPresetIndices.remove(actualIdx);
                        Set<Integer> newSet = new HashSet<>();
                        for (int idx : selectedPresetIndices) newSet.add(idx > actualIdx ? idx - 1 : idx);
                        selectedPresetIndices.clear(); selectedPresetIndices.addAll(newSet);
                        if (selectedIndex >= textLineWidgets.size()) selectedIndex = textLineWidgets.isEmpty() ? -1 : textLineWidgets.size() - 1;
                        preciseInputMode = false; refreshTopPanel(); refreshBottomPanel();
                        syncAndUpdateClient();
                        sendUpdateToServer();
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void adjustXYZ(int type) {
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            var d = textLineWidgets.get(selectedIndex).data;
            float step = -(hasAltDown() ? 0.5f : 1.0f);
            switch (type) { case 0 -> d.setXOffset(d.getXOffset() + step); case 1 -> d.setYOffset(d.getYOffset() + step); case 4 -> d.setZOffset(d.getZOffset() + step); }
            syncAndUpdateClient();
            sendUpdateToServer();
        }
    }
    private void adjustFontSize() {
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            var d = textLineWidgets.get(selectedIndex).data;
            d.setFontSize(Math.max(0.1f, d.getFontSize() - (hasAltDown() ? 1f/32f : 1f/16f)));
            syncAndUpdateClient();
            sendUpdateToServer();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 顶部面板
        context.fill(panelTopX, panelTopY, panelTopX + panelTopWidth, panelTopY + panelTopHeight, 0xAA333333);
        context.drawBorder(panelTopX, panelTopY, panelTopWidth, panelTopHeight, 0xFF888888);
        context.fill(panelTopX + panelTopWidth, panelTopY, panelTopX + panelTopWidth + ADD_BUTTON_WIDTH, panelTopY + panelTopHeight, 0xAA444444);
        context.drawBorder(panelTopX + panelTopWidth, panelTopY, ADD_BUTTON_WIDTH, panelTopHeight, 0xFF888888);

        // 保存按钮行（全宽）
        context.fill(0, panelTopY + panelTopHeight, width, panelTopY + panelTopHeight + SAVE_BTN_ROW_HEIGHT, 0xAA222233);

        // 底部面板
        context.fill(panelBottomX, panelBottomY, panelBottomX + panelBottomWidth, panelBottomY + panelBottomHeight, 0xAA333333);
        context.drawBorder(panelBottomX, panelBottomY, panelBottomWidth, panelBottomHeight, 0xFF888888);

        if (!preciseInputMode && !presetSaveMode && !presetLoadMode) {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                var d = textLineWidgets.get(selectedIndex).data;
                int lh = (panelBottomHeight - 10) / 2, y2 = panelBottomY + 5 + lh;
                int cx = panelBottomX + 5 + (BTN_SIZE + BTN_GAP) * 5;
                drawToggleBg(context, cx, y2, BTN_SIZE, d.isBold()); cx += BTN_SIZE + BTN_GAP;
                drawToggleBg(context, cx, y2, BTN_SIZE, d.isItalic()); cx += BTN_SIZE + BTN_GAP;
                drawToggleBg(context, cx, y2, BTN_SIZE, d.isUnderline()); cx += BTN_SIZE + BTN_GAP;
                drawToggleBg(context, cx, y2, BTN_SIZE, d.isShadow());

                String line1 = String.format("X:%.1f Y:%.1f Z:%.1f", d.getXOffset(), d.getYOffset(), d.getZOffset());
                String line2 = String.format("S:%.2f C:#%06X", d.getFontSize(), d.getColor());
                int infoX = panelBottomX + panelBottomWidth - Math.max(textRenderer.getWidth(line1), textRenderer.getWidth(line2)) - 5;
                context.drawText(textRenderer, Text.literal(line1), infoX, y2 + 2, 0xFFAAAAAA, false);
                context.drawText(textRenderer, Text.literal(line2), infoX, y2 + 2 + textRenderer.fontHeight + 3, 0xFFAAAAAA, false);
            }
            if (textLineWidgets.isEmpty()) {
                String h = "点击 + 添加文本, 按P加载预设";
                context.drawText(textRenderer, Text.literal(h), panelTopX + (panelTopWidth - textRenderer.getWidth(h))/2, panelTopY + (panelTopHeight - textRenderer.fontHeight)/2, 0xFFAAAAAA, false);
            }
            if (selectedIndex < 0 && !textLineWidgets.isEmpty()) {
                String h = "选择文本, 按住Ctrl多选保存预设, 按P加载预设";
                context.drawText(textRenderer, Text.literal(h), panelBottomX + (panelBottomWidth - textRenderer.getWidth(h))/2, panelBottomY + (panelBottomHeight - textRenderer.fontHeight)/2, 0xFFAAAAAA, false);
            }
        } else if (presetSaveMode) context.drawText(textRenderer, Text.literal("输入预设名称并保存"), panelBottomX + 5, panelBottomY + 2, 0xFFAAAAAA, false);
        else if (presetLoadMode && PresetManager.getPresets().isEmpty()) {
            String h = "暂无预设，请先保存预设";
            context.drawText(textRenderer, Text.literal(h), panelBottomX + (panelBottomWidth - textRenderer.getWidth(h))/2, panelBottomY + (panelBottomHeight - textRenderer.fontHeight)/2 - 10, 0xFFAAAAAA, false);
        } else if (preciseInputMode) {
            String l = switch (preciseInputType) { case 0 -> "输入 X 坐标"; case 1 -> "输入 Y 坐标"; case 2 -> "输入字号"; case 3 -> "输入颜色 (#RRGGBB)"; case 4 -> "输入 Z 坐标"; default -> ""; };
            context.drawText(textRenderer, Text.literal(l), panelBottomX + 5, panelBottomY + 5, 0xFFAAAAAA, false);
        }

        super.render(context, mouseX, mouseY, delta);

        for (int i = 0; i < textButtons.size(); i++) {
            int actualIdx = topScrollOffset + i;
            if (selectedPresetIndices.contains(actualIdx)) {
                var btn = textButtons.get(i);
                context.fill(btn.getX() - 1, btn.getY() - 1, btn.getX() + btn.getWidth() + 1, btn.getY() + btn.getHeight() + 1, 0x6600FF00);
            }
        }

        if (!presetSaveMode && !presetLoadMode && !preciseInputMode) {
            List<TooltipEntry> tips = new ArrayList<>();
            if (xButton != null && xButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("X 坐标", "左键 +1 | 右键 -1", "Alt ±0.5 | Ctrl+点击精准输入"));
            if (yButton != null && yButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Y 坐标", "左键 +1 | 右键 -1", "Alt ±0.5 | Ctrl+点击精准输入"));
            if (zButton != null && zButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Z 坐标", "左键 +1 | 右键 -1", "Alt ±0.5 | Ctrl+点击精准输入"));
            if (fontSizeButton != null && fontSizeButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("字号", "左键 +1/16 | 右键 -1/16", "Alt ±1/32 | Ctrl+点击精准输入"));
            if (colorButton != null && colorButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("颜色", "点击切换 | Ctrl+点击精准输入"));
            if (addLineButton != null && addLineButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("添加文本行", "按P加载预设"));
            if (!tips.isEmpty()) drawTooltip(context, mouseX, mouseY, tips);
        }
    }

    private void drawToggleBg(DrawContext context, int x, int y, int size, boolean active) {
        if (active) { context.fill(x, y, x + size, y + size, 0xFF6B6BAA); context.drawBorder(x, y, size, size, 0xFFAAAAFF); }
    }

    private void drawTooltip(DrawContext context, int mx, int my, List<TooltipEntry> entries) {
        int lh = textRenderer.fontHeight + 2, mw = 0;
        List<String> lines = new ArrayList<>();
        for (var e : entries) {
            if (!e.title.isEmpty()) { lines.add(e.title); mw = Math.max(mw, textRenderer.getWidth(e.title)); }
            for (String d : e.descriptions) { lines.add("  " + d); mw = Math.max(mw, textRenderer.getWidth("  " + d)); }
        }
        int th = 4 + lines.size() * lh, tx = Math.min(mx + 12, width - mw - 10), ty = Math.min(my - th - 4, height - th - 4);
        if (ty < 4) ty = my + 12;
        context.fill(tx, ty, tx + mw + 8, ty + th, 0xCC1E1E2E); context.drawBorder(tx, ty, mw + 8, th, 0xFF6B6B8A);
        int ty2 = ty + 2;
        for (String line : lines) { context.drawText(textRenderer, Text.literal(line), tx + 4, ty2, line.startsWith("  ") ? 0xFFAAAAAA : 0xFFFFFFFF, false); ty2 += lh; }
    }

    public void sendUpdateToServer() {
        syncWidgetsToBlockEntity();
        var packet = CustomSignUpdatePacket.update(blockPos, new ArrayList<>(blockEntity.getTextLines()));
        var buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);
        NetworkManager.sendToServer(UPDATE_CUSTOM_SIGN, buf);
    }

    private void syncWidgetsToBlockEntity() {
        List<TextLineData> updatedLines = new ArrayList<>();
        for (TextLineWidget widget : textLineWidgets) {
            updatedLines.add(widget.data);
        }
        blockEntity.getTextLines().clear();
        blockEntity.getTextLines().addAll(updatedLines);
    }

    @Override
    public void close() {
        sendUpdateToServer();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static class TextLineWidget {
        final TextLineData data;
        TextLineWidget(TextLineData d) {
            this.data = d;
        }
    }

    private record TooltipEntry(String title, String... descriptions) {}
}