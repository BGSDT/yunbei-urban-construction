package com.beigu.yunbeiuc.screen;
import com.beigu.yunbeiuc.api.network.NetworkCompat;

import com.beigu.yunbeiuc.api.text.Text;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.beigu.yunbeiuc.entity.SignTextLinesHelper;
import com.beigu.yunbeiuc.util.GlobalFontSettings;
import com.beigu.yunbeiuc.network.CustomSignFieldUpdatePacket;
import com.beigu.yunbeiuc.network.CustomSignUpdatePacket;
import com.beigu.yunbeiuc.render.TextGizmo;
import com.beigu.yunbeiuc.util.PresetManager;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static com.beigu.yunbeiuc.network.ModMessages.UPDATE_CUSTOM_SIGN;
import static com.beigu.yunbeiuc.network.ModMessages.UPDATE_CUSTOM_SIGN_FIELD;

public class TextDisplayScreen extends Screen {
    private static final int PANEL_TOP_HEIGHT = 20;
    private static final int PANEL_BOTTOM_HEIGHT_RATIO = 5;
    private static final int ADD_BUTTON_WIDTH = 20;
    private static final int BTN_SIZE = 20;
    private static final int ROT_BTN_WIDTH = 24;
    private static final int BTN_GAP = 5;
    private static final int MAX_VISIBLE_TABS = 8;
    private static final int SCROLL_BTN_WIDTH = 14;
    private static final int SAVE_BTN_ROW_HEIGHT = 22;
    private static final int INFO_PANEL_WIDTH = 100;
    private static final float SCALE_DISPLAY_FACTOR = 16f;
    private static final float POSITION_STEP = 0.1f;
    private static final float POSITION_ALT_STEP = 0.05f;
    private static final int MAX_LINE_TAB_WIDTH = 300;
    private static final int[] COLOR_PALETTE = {0xFFFFFF, 0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF, 0xFFA500, 0x000000};

    private final CustomSignBlockEntity blockEntity;
    private final BlockPos blockPos;
    private final boolean supportsGlobalFontSetting;
    private final List<TextLineWidget> textLineWidgets = new ArrayList<>();
    private int selectedIndex = -1;
    private final LinkedHashSet<Integer> selectedLineIndices = new LinkedHashSet<>();

    private final List<ButtonWidget> textButtons = new ArrayList<>();
    private ButtonWidget addLineButton;

    private TextFieldWidget textField;
    private ButtonWidget xButton, yButton, zButton, fontSizeButton, colorButton;
    private ButtonWidget rxButton, ryButton, rzButton;
    private ButtonWidget sxButton, syButton, szButton;
    private ButtonWidget boldButton, italicButton, underlineButton, shadowButton;
    private ButtonWidget outlineButton, outlineColorButton;
    private ButtonWidget glowButton;
    private ButtonWidget hAlignButton, vAlignButton, clearFormatButton;

    private enum Category { POSITION, ROTATION, SCALE, FONT, ALIGN }
    private Category activeCategory = Category.POSITION;
    private ButtonWidget posCatButton, rotCatButton, scaleCatButton, fontCatButton, alignCatButton;
    private ButtonWidget patternButton;
    private ButtonWidget settingsButton;

    private boolean preciseInputMode = false;
    private TextFieldWidget preciseInputField;
    private ButtonWidget backButton;
    private int preciseInputType = 0;

    private boolean presetSelectMode = false;
    private boolean presetSaveMode = false;
    private boolean presetLoadMode = false;
    private final Set<Integer> selectedPresetIndices = new HashSet<>();
    private ButtonWidget savePresetButton;

    private TextLineData clipboardData = null;
    private boolean formatPainterMode = false;
    private int formatPainterSourceIndex = -1;
    private ButtonWidget copyLineButton, pasteLineButton, deleteLineButton, formatPainterButton;
    private TextFieldWidget presetNameField;
    private ButtonWidget confirmSaveButton, cancelPresetButton, cancelLoadButton;
    private final List<ButtonWidget> presetButtons = new ArrayList<>();
    private int presetScrollOffset = 0;
    private ButtonWidget presetScrollUp, presetScrollDown;

    private int topScrollOffset = 0;
    private ButtonWidget topScrollLeft, topScrollRight;

    private int rowScrollIndex = 0;
    private ButtonWidget rowScrollLeft, rowScrollRight;
    private int bottomRowScrollIndex = 0;
    private ButtonWidget bottomRowScrollLeft, bottomRowScrollRight;

    private int panelTopX, panelTopY, panelTopWidth, panelTopHeight;
    private int panelBottomX, panelBottomY, panelBottomWidth, panelBottomHeight;

    private static final int OPTIONS_ROW_HEIGHT = 22;
    private boolean optionsRowVisible = false;
    private final List<ButtonWidget> optionButtons = new ArrayList<>();
    private final List<Object[]> optionGroupTitles = new ArrayList<>();

    // ===== 外部模组扩展：自定义按钮 =====
    /** 注入到「底部控制行（位移/旋转/缩放/字体/对齐那一行）」的自定义按钮定义。 */
    private final List<CustomButton> customControlRowButtons = new ArrayList<>();
    /** 注入到「属性选项行」的自定义按钮定义。 */
    private final List<CustomButton> customPropertyButtons = new ArrayList<>();
    /** 每次刷新时创建的底部控制行自定义控件（用于刷新前移除旧控件）。 */
    private final List<ButtonWidget> builtControlCustomWidgets = new ArrayList<>();
    private boolean suppressTextFieldListener = false;
    private String currentPlaceholderText = "";
    private String currentResolvedText = "";

    private int grabbedGizmo = -1;
    private float grabValueStart, grabAxisStart, grabSize0, grabLen0;
    private float grabAnglePrev, grabAccumDeg;
    private final Map<Integer, float[]> gizmoSelectionStart = new HashMap<>();
    private boolean boxSelecting = false;
    private boolean boxSelectionAdditive = false;
    private double boxStartX, boxStartY, boxCurrentX, boxCurrentY;
    private boolean rotatingView = false;

    public TextDisplayScreen(CustomSignBlockEntity blockEntity) {
        super(Text.translatable("gui.yunbeiuc.custom_sign"));
        this.blockEntity = blockEntity;
        this.blockPos = blockEntity.getBlockPos();
        this.supportsGlobalFontSetting = computeSupportsGlobalFontSetting(blockEntity);
    }

    private static boolean computeSupportsGlobalFontSetting(CustomSignBlockEntity blockEntity) {
        ResourceLocation id = VersionServices.registries().blockId(blockEntity.getBlockState().getBlock());
        String path = id.getPath();
        return path.contains("sign") || path.contains("zones");
    }

    private String signAdaptiveAbcFont() {
        for (TextLineData line : blockEntity.getTextLines()) {
            if (!line.isBuiltin()) continue;
            String font = line.getAbcFont();
            if ("a".equals(font) || "b".equals(font) || "c".equals(font)) return font;
        }
        return "a";
    }

    private TextLineData createUserTextLine(String text) {
        TextLineData line = new TextLineData(text == null ? "" : text);
        line.setBuiltin(false);
        if (supportsGlobalFontSetting) {
            SignTextLinesHelper.applyGlobalFontSetting(line, signAdaptiveAbcFont());
        }
        return line;
    }

    private List<Integer> selectedTargets() {
        selectedLineIndices.removeIf(i -> i < 0 || i >= textLineWidgets.size());
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) selectedLineIndices.add(selectedIndex);
        return new ArrayList<>(selectedLineIndices);
    }

    private void selectOnlyLine(int index) {
        selectedLineIndices.clear();
        if (index >= 0 && index < textLineWidgets.size()) {
            selectedLineIndices.add(index);
            selectedIndex = index;
        } else {
            selectedIndex = -1;
        }
    }

    private void toggleLineSelection(int index) {
        if (index < 0 || index >= textLineWidgets.size()) return;
        if (selectedLineIndices.remove(index)) {
            if (selectedIndex == index) {
                selectedIndex = selectedLineIndices.isEmpty() ? -1 : selectedLineIndices.stream().reduce((a, b) -> b).orElse(-1);
            }
        } else {
            selectedLineIndices.add(index);
            selectedIndex = index;
        }
    }

    private void refreshSelectionUi() {
        releaseGizmo();
        preciseInputMode = false;
        refreshTopPanel();
        refreshBottomPanel();
        if (selectedIndex >= 0) updateBottomPanelDisplay();
    }

    private static String lineButtonLabel(String resolvedText) {
        String text = resolvedText == null ? "" : resolvedText.trim();
        if (text.startsWith("-json")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length >= 2) {
                try {
                    Component parsed = VersionServices.text().fromLegacyJson(parts[1]);
                    if (parsed != null) return parsed.getString();
                } catch (Exception ignored) {
                }
            }
            return "";
        }
        if (text.startsWith("-rect")) {
            String[] parts = text.split("\\s+");
            if (parts.length >= 3) return "rect " + parts[1] + "*" + parts[2];
            return "rect";
        }
        if (text.startsWith("-texture")) {
            String[] parts = text.split("\\s+", 3);
            if (parts.length >= 2) {
                String path = parts[1];
                int slash = path.lastIndexOf('/');
                return slash >= 0 ? path.substring(slash + 1) : path;
            }
            return "texture";
        }
        return text;
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

        savePresetButton = ButtonWidget.builderCompat(Text.literal("保存为预设"), btn -> {
            if (selectedPresetIndices.isEmpty()) return;
            presetSaveMode = true; presetSelectMode = false;
            refreshBottomPanel(); refreshTopPanel();
        }).dimensions(sw / 2 - 40, panelTopY + panelTopHeight + 1, 80, 20).build();
        savePresetButton.visible = false;

        int catBtnW = 50, catBtnGap = 4;
        int catX = 5;
        int catY = panelTopY + panelTopHeight + 1;
        posCatButton = ButtonWidget.builderCompat(Text.literal("位移"), b -> selectCategory(Category.POSITION)).dimensions(catX, catY, catBtnW, 20).build(); catX += catBtnW + catBtnGap;
        rotCatButton = ButtonWidget.builderCompat(Text.literal("旋转"), b -> selectCategory(Category.ROTATION)).dimensions(catX, catY, catBtnW, 20).build(); catX += catBtnW + catBtnGap;
        scaleCatButton = ButtonWidget.builderCompat(Text.literal("缩放"), b -> selectCategory(Category.SCALE)).dimensions(catX, catY, catBtnW, 20).build(); catX += catBtnW + catBtnGap;
        fontCatButton = ButtonWidget.builderCompat(Text.literal("字体"), b -> selectCategory(Category.FONT)).dimensions(catX, catY, catBtnW, 20).build(); catX += catBtnW + catBtnGap;
        alignCatButton = ButtonWidget.builderCompat(Text.literal("对齐"), b -> selectCategory(Category.ALIGN)).dimensions(catX, catY, catBtnW, 20).build();

        patternButton = ButtonWidget.builderCompat(Text.translatable("yunbeiuc.gui.button.pattern"), btn -> {
            PatternAndFontOverlay.targetScreen = this;
            PatternAndFontOverlay.isVisible = true;
            PatternAndFontOverlay.selectSidebarTop(PatternAndFontOverlay.NAV_TOP_HOME);
        }).dimensions(0, 0, catBtnW, 20).build();

        settingsButton = ButtonWidget.builderCompat(Text.translatable("yunbeiuc.gui.button.settings"), btn -> SignGlobalSettingsOverlay.open(this))
                .dimensions(0, 0, catBtnW, 20).build();

        updateCategoryButtonsLocked();

        int lineActionBtnW = 45, lineActionGap = 4;
        int lineActionTotalW = lineActionBtnW * 4 + lineActionGap * 3;
        int lineActionStartX = sw - lineActionTotalW - 4;
        int lineActionY = panelTopY + panelTopHeight + 1;

        copyLineButton = ButtonWidget.builderCompat(Text.literal("复制"), btn -> copySelectedLine())
                .dimensions(lineActionStartX, lineActionY, lineActionBtnW, 20).build();
        copyLineButton.visible = false;

        pasteLineButton = ButtonWidget.builderCompat(Text.literal("粘贴"), btn -> pasteSelectedLine())
                .dimensions(lineActionStartX + (lineActionBtnW + lineActionGap), lineActionY, lineActionBtnW, 20).build();
        pasteLineButton.visible = false;
        pasteLineButton.active = false;

        deleteLineButton = ButtonWidget.builderCompat(Text.literal("删除"), btn -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                requestDeleteLine(selectedIndex);
            }
        }).dimensions(lineActionStartX + (lineActionBtnW + lineActionGap) * 2, lineActionY, lineActionBtnW, 20).build();
        deleteLineButton.visible = false;

        formatPainterButton = ButtonWidget.builderCompat(Text.literal("格式刷"), btn -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                formatPainterMode = true;
                formatPainterSourceIndex = selectedIndex;
                refreshTopPanel();
            }
        }).dimensions(lineActionStartX + (lineActionBtnW + lineActionGap) * 3, lineActionY, lineActionBtnW, 20).build();
        formatPainterButton.visible = false;

        addLineButton = ButtonWidget.builderCompat(Text.literal("+"), button -> {
            if (presetSelectMode || presetSaveMode || presetLoadMode) return;
            TextLineData newData = createUserTextLine("Text");
            textLineWidgets.add(new TextLineWidget(newData));
            blockEntity.getTextLines().add(newData);
            selectOnlyLine(textLineWidgets.size() - 1);
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
        this.addDrawableChild(posCatButton);
        this.addDrawableChild(rotCatButton);
        this.addDrawableChild(scaleCatButton);
        this.addDrawableChild(fontCatButton);
        this.addDrawableChild(alignCatButton);
        this.addDrawableChild(patternButton);
        this.addDrawableChild(settingsButton);
        this.addDrawableChild(copyLineButton);
        this.addDrawableChild(pasteLineButton);
        this.addDrawableChild(deleteLineButton);
        this.addDrawableChild(formatPainterButton);
    }

    private void createBottomPanelWidgets() {
        textField = new TextFieldWidget(this.textRenderer, 0, 0, panelBottomWidth - 10 - INFO_PANEL_WIDTH, 16, Text.literal("Text"));
        textField.setMaxLength(Integer.MAX_VALUE);
        textField.setChangedListener(text -> {
            if (suppressTextFieldListener) return;
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                textLineWidgets.get(selectedIndex).data.setText(text.equals(currentResolvedText) ? currentPlaceholderText : text);
                refreshTopPanel();
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        });

        xButton = makeXYZButton("X", 0); yButton = makeXYZButton("Y", 1); zButton = makeXYZButton("Z", 4);
        rxButton = makeRotButton("RX", 5); ryButton = makeRotButton("RY", 6); rzButton = makeRotButton("RZ", 7);
        sxButton = makeScaleButton("SX", 8); syButton = makeScaleButton("SY", 9); szButton = makeScaleButton("SZ", 10);
        fontSizeButton = ButtonWidget.builderCompat(Text.literal("S"), button -> {
            if (hasControlDown()) enterPreciseMode(2);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                float step = stepFor(1f/16f, 1f/32f);
                for (int index : selectedTargets()) {
                    var d = textLineWidgets.get(index).data;
                    d.setFontSize(Math.max(0.1f, d.getFontSize() + step));
                }
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        colorButton = makeColorCycleButton(3);

        boldButton = makeToggle("B", s -> s.withBold(true), d -> { d.setBold(!d.isBold()); syncAndUpdateClient(); });
        italicButton = makeToggle("I", s -> s.withItalic(true), d -> { d.setItalic(!d.isItalic()); syncAndUpdateClient(); });
        underlineButton = makeToggle("U", s -> s.withUnderlined(true), d -> { d.setUnderline(!d.isUnderline()); syncAndUpdateClient(); });
        shadowButton = makeToggle("D", s -> s.withBold(true), d -> { d.setShadow(!d.isShadow()); syncAndUpdateClient(); });
        outlineButton = makeToggle("O", s -> s.withBold(true), d -> { d.setOutline(!d.isOutline()); syncAndUpdateClient(); });
        outlineColorButton = makeColorCycleButton(12);
        // 发光：该行用原版全亮光照渲染（荧光效果）
        glowButton = makeToggle("L", s -> s.withBold(true), d -> { d.setGlowing(!d.isGlowing()); syncAndUpdateClient(); });

        hAlignButton = ButtonWidget.builderCompat(Text.literal("水平居中"), button -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                int nh = (d.getAlignment().hAlign + 1) % 3;
                for (int index : selectedTargets()) {
                    var line = textLineWidgets.get(index).data;
                    line.setAlignment(getAlignment(nh, line.getAlignment().vAlign));
                }
                hAlignButton.setMessage(Text.literal(getHAlignText(nh)));
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE + 40, BTN_SIZE).build();

        vAlignButton = ButtonWidget.builderCompat(Text.literal("垂直居中"), button -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                int nv = (d.getAlignment().vAlign + 1) % 3;
                for (int index : selectedTargets()) {
                    var line = textLineWidgets.get(index).data;
                    line.setAlignment(getAlignment(line.getAlignment().hAlign, nv));
                }
                vAlignButton.setMessage(Text.literal(getVAlignText(nv)));
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE + 40, BTN_SIZE).build();

        clearFormatButton = ButtonWidget.builderCompat(Text.literal("✕"), button -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                for (int index : selectedTargets()) {
                    var d = textLineWidgets.get(index).data;
                    d.setBold(false); d.setItalic(false); d.setUnderline(false); d.setShadow(false); d.setOutline(false);
                    d.setColor(0xFFFFFF); d.setFontSize(1.0f); d.setAlignment(CustomSignBlockEntity.TextAlignment.CENTER_CENTER);
                }
                colorButton.setMessage(colorMsg(0xFFFFFF));
                hAlignButton.setMessage(Text.literal("水平居中")); vAlignButton.setMessage(Text.literal("垂直居中"));
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private ButtonWidget makeXYZButton(String label, int type) {
        return ButtonWidget.builderCompat(Text.literal(label), button -> {
            if (hasControlDown()) enterPreciseMode(type);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                float step = stepFor(POSITION_STEP, POSITION_ALT_STEP);
                for (int index : selectedTargets()) {
                    var d = textLineWidgets.get(index).data;
                    switch (type) { case 0 -> d.setXOffset(d.getXOffset() + step); case 1 -> d.setYOffset(d.getYOffset() + step); case 4 -> d.setZOffset(d.getZOffset() + step); }
                }
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private ButtonWidget makeRotButton(String label, int type) {
        return ButtonWidget.builderCompat(Text.literal(label), button -> {
            if (hasControlDown()) enterPreciseMode(type);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                float step = stepFor(15.0f, 5.0f);
                for (int index : selectedTargets()) {
                    var d = textLineWidgets.get(index).data;
                    switch (type) { case 5 -> d.setRotX(d.getRotX() + step); case 6 -> d.setRotY(d.getRotY() + step); case 7 -> d.setRotZ(d.getRotZ() + step); }
                }
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, ROT_BTN_WIDTH, BTN_SIZE).build();
    }

    private ButtonWidget makeScaleButton(String label, int type) {
        return ButtonWidget.builderCompat(Text.literal(label), button -> {
            if (hasControlDown()) enterPreciseMode(type);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                float step = stepFor(1f/16f, 1f/32f);
                for (int index : selectedTargets()) {
                    var d = textLineWidgets.get(index).data;
                    switch (type) {
                        case 8 -> d.setScaleX(Math.max(0.1f, d.getScaleX() + step));
                        case 9 -> d.setScaleY(Math.max(0.1f, d.getScaleY() + step));
                        case 10 -> d.setScaleZ(Math.max(0.1f, d.getScaleZ() + step));
                    }
                }
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private ButtonWidget makeToggle(String label, java.util.function.UnaryOperator<net.minecraft.network.chat.Style> sf, java.util.function.Consumer<TextLineData> action) {
        return ButtonWidget.builderCompat(Text.literal(label).withStyle(sf), btn -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                for (int index : selectedTargets()) action.accept(textLineWidgets.get(index).data);
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private ButtonWidget makeColorCycleButton(final int type) {
        return ButtonWidget.builderCompat(Text.literal("■"), button -> {
            if (hasControlDown()) {
                if (type == 3 || type == 12) {
                    openColorPickerForType(type);
                } else {
                    enterPreciseMode(type);
                }
                return;
            }
            cycleColorByType(type, 1);
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private void openColorPickerForType(int type) {
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return;
        TextLineWidget w = textLineWidgets.get(selectedIndex);
        TextLineData d = w.data;
        int currentColor = (type == 3) ? d.getColor() : d.getOutlineColor();
        if (currentColor == -1) currentColor = 0xFFFFFF;
        Minecraft.getInstance().setScreen(new ColorPickerScreen(this, currentColor, c -> {
            for (int index : selectedTargets()) applyColor(type, c, textLineWidgets.get(index).data);
            syncAndUpdateClient();
            sendUpdateToServer();
        }));
    }

    private void cycleColorByType(int type, int dir) {
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size() || presetSaveMode || presetLoadMode) return;
        var d = textLineWidgets.get(selectedIndex).data;
        int cur = switch (type) { case 3 -> d.getColor(); default -> d.getOutlineColor(); };
        int ci = -1;
        for (int i = 0; i < COLOR_PALETTE.length; i++) if (COLOR_PALETTE[i] == cur) { ci = i; break; }
        int nc = COLOR_PALETTE[(ci + dir + COLOR_PALETTE.length) % COLOR_PALETTE.length];
        for (int index : selectedTargets()) applyColor(type, nc, textLineWidgets.get(index).data);
        syncAndUpdateClient();
        sendUpdateToServer();
    }

    private void applyColor(int type, int c, TextLineData d) {
        switch (type) {
            case 3 -> { d.setColor(c); colorButton.setMessage(colorMsg(c)); }
            default -> { d.setOutlineColor(c); outlineColorButton.setMessage(colorMsg(c)); }
        }
    }

    private static Component colorMsg(int c) { return Text.literal("■").withStyle(s -> s.withColor(net.minecraft.network.chat.TextColor.fromRgb(c))); }

    private static Integer tryParseHex(String t) {
        String hex = t.replace("#", "").trim();
        if (hex.length() != 6) return null;
        try { return Integer.parseInt(hex, 16); } catch (NumberFormatException e) { return null; }
    }

    private CustomSignBlockEntity.TextAlignment getAlignment(int h, int v) {
        for (var a : CustomSignBlockEntity.TextAlignment.values()) if (a.hAlign == h && a.vAlign == v) return a;
        return CustomSignBlockEntity.TextAlignment.CENTER_CENTER;
    }
    private String getHAlignText(int h) { return switch (h) { case 0 -> "左对齐"; case 1 -> "水平居中"; case 2 -> "右对齐"; default -> "水平居中"; }; }
    private String getVAlignText(int v) { return switch (v) { case 0 -> "顶部对齐"; case 1 -> "垂直居中"; case 2 -> "底部对齐"; default -> "垂直居中"; }; }

    private float stepFor(float base, float altStep) { return hasShiftDown() ? base * 4f : (hasAltDown() ? altStep : base); }

    private void selectCategory(Category c) {
        activeCategory = c;
        preciseInputMode = false;
        bottomRowScrollIndex = 0;
        releaseGizmo();
        updateCategoryButtonsLocked();
        refreshBottomPanel();
    }

    private int currentGizmoMode() {
        if (PatternAndFontOverlay.isVisible || SignGlobalSettingsOverlay.isVisible) return -1;
        if (presetSaveMode || presetLoadMode || preciseInputMode || formatPainterMode) return -1;
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return -1;
        return switch (activeCategory) {
            case POSITION -> TextGizmo.MODE_POSITION;
            case ROTATION -> TextGizmo.MODE_ROTATION;
            case SCALE -> TextGizmo.MODE_SCALE;
            default -> -1;
        };
    }

    private static float snapToStep(float v, float step) { return step <= 0f ? v : Math.round(v / step) * step; }

    private static float clampScaleDisplay(float display) { return Math.max(1.6f, display); }

    private void releaseGizmo() {
        grabbedGizmo = -1;
        gizmoSelectionStart.clear();
        TextGizmo.grabId = -1;
        TextGizmo.hoverId = -1;
    }

    private boolean tryGrabGizmo(double mouseX, double mouseY) {
        if (currentGizmoMode() < 0 || !TextGizmo.isFresh()) return false;
        var d = textLineWidgets.get(selectedIndex).data;
        int h = TextGizmo.pick(mouseX, mouseY, d.getScaleX(), d.getScaleY(), d.getScaleZ());
        if (h < 0) return false;
        grabbedGizmo = h;
        TextGizmo.grabId = h;
        int kind = TextGizmo.handleKind(h);
        if (kind == TextGizmo.KIND_AXIS) {
            Float v = TextGizmo.axisDragValue(axis(h), mouseX, mouseY);
            grabAxisStart = v != null ? v : 0f;
            grabValueStart = switch (axis(h)) { case 0 -> d.getXOffset(); case 1 -> d.getYOffset(); default -> d.getZOffset(); };
        } else if (kind == TextGizmo.KIND_ROT) {
            Float a = TextGizmo.rotationDragAngle(axis(h), mouseX, mouseY);
            grabAnglePrev = a != null ? a : 0f;
            grabAccumDeg = 0f;
            grabValueStart = switch (axis(h)) { case 0 -> d.getRotX(); case 1 -> d.getRotY(); default -> d.getRotZ(); };
        } else if (kind == TextGizmo.KIND_CORNER) {
            grabSize0 = d.getFontSize();
            grabLen0 = (float) Math.sqrt(sq(TextGizmo.halfWidthBlocks() * d.getScaleX())
                    + sq(TextGizmo.halfHeightBlocks() * d.getScaleY()));
        }
        gizmoSelectionStart.clear();
        for (int index : selectedTargets()) {
            TextLineData line = textLineWidgets.get(index).data;
            gizmoSelectionStart.put(index, new float[]{
                    line.getXOffset(), line.getYOffset(), line.getZOffset(),
                    line.getRotX(), line.getRotY(), line.getRotZ(),
                    line.getFontSize(), line.getScaleX(), line.getScaleY(), line.getScaleZ()
            });
        }
        return true;
    }

    private static int axis(int handle) { return TextGizmo.handleAxis(handle); }

    private boolean isOverUiPanel(double mx, double my) {
        if (PatternAndFontOverlay.isVisible || SignGlobalSettingsOverlay.isVisible) return true;
        return my >= (optionsRowVisible ? panelTopY - OPTIONS_ROW_HEIGHT : panelTopY);
    }

    public void insertPatternContent(String text) {
        if (text == null) return;
        preciseInputMode = false;
        if (textLineWidgets.isEmpty() || selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) {
            // 同上：新增行不继承路牌自带属性
            TextLineData newData = createUserTextLine(text);
            textLineWidgets.add(new TextLineWidget(newData));
            blockEntity.getTextLines().add(newData);
            selectOnlyLine(textLineWidgets.size() - 1);
            topScrollOffset = Math.max(0, textLineWidgets.size() - MAX_VISIBLE_TABS);
        } else {
            textLineWidgets.get(selectedIndex).data.setText(text);
        }
        refreshTopPanel();
        refreshBottomPanel();
        syncAndUpdateClient();
        sendUpdateToServer();
    }

    // ==================== 外部模组扩展 API ====================
    //
    // 供其他模组直接调用：操作输入框、向底部控制行 / 属性选项行注入按钮、新增文本行。
    // 标签提供 Component 与 String 两种重载；String 版本签名里不含任何 Minecraft 类型，
    // 跨映射（如 yarn 项目）调用最省事。

    /** 清空底部输入框（同时把当前选中文本行的文字清空）。 */
    public void clearInputField() {
        setInputFieldContent("");
    }

    /**
     * 覆盖式设置底部输入框内容，并写回当前选中文本行。
     *
     * <p>与 {@link #insertPatternContent(String)} 不同：本方法始终覆盖，不追加；
     * 当前没有选中行时会自动新建一行。
     *
     * @param text 新内容；{@code null} 视为空串
     */
    public void setInputFieldContent(String text) {
        String content = text == null ? "" : text;
        preciseInputMode = false;
        if (textLineWidgets.isEmpty() || selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) {
            TextLineData newData = createUserTextLine(content);
            textLineWidgets.add(new TextLineWidget(newData));
            blockEntity.getTextLines().add(newData);
            selectOnlyLine(textLineWidgets.size() - 1);
            topScrollOffset = Math.max(0, textLineWidgets.size() - MAX_VISIBLE_TABS);
        } else {
            textLineWidgets.get(selectedIndex).data.setText(content);
        }
        refreshTopPanel();
        refreshBottomPanel();
        syncAndUpdateClient();
        sendUpdateToServer();
    }

    /**
     * 新增一条文本行并选中它。
     *
     * @param text 行文本；{@code null} 视为空串
     * @return 新增行的数据对象，便于调用方继续设置字体 / 颜色 / 位移等属性
     */
    public TextLineData addTextLine(String text) {
        TextLineData newData = createUserTextLine(text == null ? "" : text);
        textLineWidgets.add(new TextLineWidget(newData));
        blockEntity.getTextLines().add(newData);
        selectOnlyLine(textLineWidgets.size() - 1);
        topScrollOffset = Math.max(0, textLineWidgets.size() - MAX_VISIBLE_TABS);
        refreshTopPanel();
        refreshBottomPanel();
        syncAndUpdateClient();
        sendUpdateToServer();
        return newData;
    }

    /**
     * 在「底部控制行」（位移 / 旋转 / 缩放 / 字体 / 对齐那一行）追加一个自定义按钮。
     *
     * <p>按钮宽度按文字自适应；该行放不下时会自动出现左右滚动按钮。
     *
     * @param label   按钮文字
     * @param onClick 点击回调
     * @return 自定义按钮句柄，可用于 {@link #removeCustomButton(CustomButton)}
     */
    public CustomButton addControlRowButton(Component label, Runnable onClick) {
        return addControlRowButton(label == null ? "" : label.getString(), onClick);
    }

    /** {@link #addControlRowButton(Component, Runnable)} 的 String 版本。 */
    public CustomButton addControlRowButton(String label, Runnable onClick) {
        CustomButton cb = new CustomButton(label, onClick);
        customControlRowButtons.add(cb);
        refreshBottomPanel();
        return cb;
    }

    /**
     * 在「属性选项行」追加一个自定义按钮。
     *
     * <p>属性选项行原本只在当前文本行含占位符属性时出现；一旦加入自定义按钮，该行会始终显示。
     *
     * @param label   按钮文字
     * @param onClick 点击回调
     * @return 自定义按钮句柄，可用于 {@link #removeCustomButton(CustomButton)}
     */
    public CustomButton addPropertyButton(Component label, Runnable onClick) {
        return addPropertyButton(label == null ? "" : label.getString(), onClick);
    }

    /** {@link #addPropertyButton(Component, Runnable)} 的 String 版本。 */
    public CustomButton addPropertyButton(String label, Runnable onClick) {
        CustomButton cb = new CustomButton(label, onClick);
        customPropertyButtons.add(cb);
        refreshTopPanel();
        return cb;
    }

    /** 移除之前注入的自定义按钮（底部控制行 / 属性选项行均可）。 */
    public void removeCustomButton(CustomButton button) {
        if (button == null) return;
        customControlRowButtons.remove(button);
        customPropertyButtons.remove(button);
        refreshTopPanel();
        refreshBottomPanel();
    }

    /**
     * 其他模组注入的自定义按钮定义（标签 + 点击回调）。
     *
     * <p>只使用 {@link String} / {@link Runnable}，签名中不含任何 Minecraft 类型，
     * 因此跨映射（yarn 项目）调用无需重映射。
     */
    public static final class CustomButton {
        public final String label;
        public final Runnable onClick;

        public CustomButton(String label, Runnable onClick) {
            this.label = label == null ? "" : label;
            this.onClick = onClick;
        }
    }

    // 依据自定义按钮定义创建控件；w 为宽度，y 为纵坐标（横坐标由所在行布局决定）
    private ButtonWidget buildCustomButton(CustomButton cb, int w, int y) {
        return ButtonWidget.builderCompat(Text.literal(cb.label), b -> {
            if (cb.onClick != null) cb.onClick.run();
        }).dimensions(0, y, w, BTN_SIZE).build();
    }

    // 自定义按钮自适应宽度
    private int customButtonWidth(CustomButton cb) {
        return Math.max(20, textRenderer.width(Text.literal(cb.label)) + 8);
    }

    private boolean trySelectLine(double mouseX, double mouseY, boolean additive) {
        if (presetSaveMode || presetLoadMode || preciseInputMode || formatPainterMode || presetSelectMode) return false;
        int idx = TextGizmo.pickLine(mouseX, mouseY);
        if (idx < 0 || idx >= textLineWidgets.size()) return false;
        if (additive) toggleLineSelection(idx);
        else selectOnlyLine(idx);
        refreshSelectionUi();
        return true;
    }

    private void beginBoxSelection(double mouseX, double mouseY, boolean additive) {
        boxSelecting = true;
        boxSelectionAdditive = additive;
        boxStartX = boxCurrentX = mouseX;
        boxStartY = boxCurrentY = mouseY;
    }

    private void finishBoxSelection() {
        double dx = boxCurrentX - boxStartX;
        double dy = boxCurrentY - boxStartY;
        if (!boxSelectionAdditive) selectedLineIndices.clear();
        if (dx * dx + dy * dy >= 16.0) {
            for (int index : TextGizmo.pickLinesInRect(boxStartX, boxStartY, boxCurrentX, boxCurrentY)) {
                if (index >= 0 && index < textLineWidgets.size()) selectedLineIndices.add(index);
            }
        }
        selectedIndex = selectedLineIndices.isEmpty() ? -1 : selectedLineIndices.stream().reduce((a, b) -> b).orElse(-1);
        boxSelecting = false;
        refreshSelectionUi();
    }

    private void applyGizmoDrag(double mouseX, double mouseY) {
        var d = textLineWidgets.get(selectedIndex).data;
        int kind = TextGizmo.handleKind(grabbedGizmo);
        int axis = TextGizmo.handleAxis(grabbedGizmo);
        switch (kind) {
            case TextGizmo.KIND_AXIS -> {
                Float v = TextGizmo.axisDragValue(axis, mouseX, mouseY);
                if (v != null) {
                    float val = snapToStep(grabValueStart + (v - grabAxisStart), stepFor(POSITION_STEP, POSITION_ALT_STEP));
                    switch (axis) { case 0 -> d.setXOffset(val); case 1 -> d.setYOffset(val); default -> d.setZOffset(val); }
                }
            }
            case TextGizmo.KIND_ROT -> {
                Float a = TextGizmo.rotationDragAngle(axis, mouseX, mouseY);
                if (a != null) {
                    float stepDeg = a - grabAnglePrev;
                    stepDeg -= 360f * Math.round(stepDeg / 360f);
                    grabAnglePrev = a;
                    grabAccumDeg += stepDeg;
                    float val = snapToStep(grabValueStart + grabAccumDeg, stepFor(15.0f, 5.0f));
                    switch (axis) { case 0 -> d.setRotX(val); case 1 -> d.setRotY(val); default -> d.setRotZ(val); }
                }
            }
            case TextGizmo.KIND_CORNER -> {
                float[] uv = TextGizmo.scaleDragPoint(mouseX, mouseY);
                if (uv != null && grabLen0 > 1e-5f) {
                    float r = (float) Math.sqrt(sq(uv[0]) + sq(uv[1])) / grabLen0;
                    d.setFontSize(clampScaleDisplay(snapToStep(grabSize0 * 16f * r, stepFor(1f, 0.5f))) / 16f);
                }
            }
            case TextGizmo.KIND_EDGE -> {
                float[] uv = TextGizmo.scaleDragPoint(mouseX, mouseY);
                if (uv != null) {
                    float step = stepFor(1f, 0.5f);
                    if (axis == 0) d.setScaleX(clampScaleDisplay(snapToStep(Math.abs(uv[0]) / Math.max(1e-5f, TextGizmo.halfWidthBlocks()) * 16f, step)) / 16f);
                    else d.setScaleY(clampScaleDisplay(snapToStep(Math.abs(uv[1]) / Math.max(1e-5f, TextGizmo.halfHeightBlocks()) * 16f, step)) / 16f);
                }
            }
        }
        float[] activeStart = gizmoSelectionStart.get(selectedIndex);
        if (activeStart != null && gizmoSelectionStart.size() > 1) {
            for (var entry : gizmoSelectionStart.entrySet()) {
                if (entry.getKey() == selectedIndex || entry.getKey() < 0 || entry.getKey() >= textLineWidgets.size()) continue;
                TextLineData line = textLineWidgets.get(entry.getKey()).data;
                float[] start = entry.getValue();
                if (kind == TextGizmo.KIND_AXIS) {
                    if (axis == 0) line.setXOffset(start[0] + d.getXOffset() - activeStart[0]);
                    else if (axis == 1) line.setYOffset(start[1] + d.getYOffset() - activeStart[1]);
                    else line.setZOffset(start[2] + d.getZOffset() - activeStart[2]);
                } else if (kind == TextGizmo.KIND_ROT) {
                    if (axis == 0) line.setRotX(start[3] + d.getRotX() - activeStart[3]);
                    else if (axis == 1) line.setRotY(start[4] + d.getRotY() - activeStart[4]);
                    else line.setRotZ(start[5] + d.getRotZ() - activeStart[5]);
                } else if (kind == TextGizmo.KIND_CORNER && activeStart[6] > 1e-6f) {
                    line.setFontSize(Math.max(0.1f, start[6] * d.getFontSize() / activeStart[6]));
                } else if (kind == TextGizmo.KIND_EDGE) {
                    if (axis == 0 && activeStart[7] > 1e-6f) line.setScaleX(Math.max(0.1f, start[7] * d.getScaleX() / activeStart[7]));
                    else if (axis == 1 && activeStart[8] > 1e-6f) line.setScaleY(Math.max(0.1f, start[8] * d.getScaleY() / activeStart[8]));
                }
            }
        }
        syncWidgetsToBlockEntity();
        blockEntity.setChanged();
    }

    private static float sq(float v) { return v * v; }

    private void updateCategoryButtonsLocked() {
        posCatButton.active = activeCategory != Category.POSITION;
        rotCatButton.active = activeCategory != Category.ROTATION;
        scaleCatButton.active = activeCategory != Category.SCALE;
        fontCatButton.active = activeCategory != Category.FONT;
        alignCatButton.active = activeCategory != Category.ALIGN;
    }

    private String[] getCategoryStatusLines(TextLineData d) {
        if (d == null) return new String[0];
        return switch (activeCategory) {
            case POSITION -> new String[]{String.format("X:%.1f", d.getXOffset()), String.format("Y:%.1f", d.getYOffset()), String.format("Z:%.1f", d.getZOffset())};
            case ROTATION -> new String[]{String.format("RX:%.1f", d.getRotX()), String.format("RY:%.1f", d.getRotY()), String.format("RZ:%.1f", d.getRotZ())};
            case SCALE -> new String[]{String.format("SX:%.2f", d.getScaleX() * SCALE_DISPLAY_FACTOR), String.format("SY:%.2f", d.getScaleY() * SCALE_DISPLAY_FACTOR), String.format("SZ:%.2f", d.getScaleZ() * SCALE_DISPLAY_FACTOR), String.format("S:%.2f", d.getFontSize() * SCALE_DISPLAY_FACTOR)};
            case FONT -> new String[]{String.format("颜色:#%06X", d.getColor()),
                    String.format("阴影:%s", d.isShadow() ? "开" : "关"),
                    String.format("描边:#%06X %s", d.getOutlineColor(), d.isOutline() ? "开" : "关")};
            case ALIGN -> new String[]{getHAlignText(d.getAlignment().hAlign), getVAlignText(d.getAlignment().vAlign)};
        };
    }

    private void enterPreciseMode(int type) { preciseInputMode = true; preciseInputType = type; refreshBottomPanel(); }
    private void exitPreciseMode() { preciseInputMode = false; refreshBottomPanel(); }

    private void createPreciseInputWidgets() {
        preciseInputField = new TextFieldWidget(textRenderer, 0, 0, panelBottomWidth - 60, 16, Text.literal(""));
        preciseInputField.setMaxLength(Integer.MAX_VALUE);
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            var d = textLineWidgets.get(selectedIndex).data;
            preciseInputField.setText(switch (preciseInputType) {
                case 0 -> String.format("%.1f", d.getXOffset()); case 1 -> String.format("%.1f", d.getYOffset());
                case 2 -> String.format("%.2f", d.getFontSize() * SCALE_DISPLAY_FACTOR); case 3 -> String.format("#%06X", d.getColor());
                case 4 -> String.format("%.1f", d.getZOffset());
                case 5 -> String.format("%.1f", d.getRotX()); case 6 -> String.format("%.1f", d.getRotY());
                case 7 -> String.format("%.1f", d.getRotZ());
                case 8 -> String.format("%.2f", d.getScaleX() * SCALE_DISPLAY_FACTOR); case 9 -> String.format("%.2f", d.getScaleY() * SCALE_DISPLAY_FACTOR);
                case 10 -> String.format("%.2f", d.getScaleZ() * SCALE_DISPLAY_FACTOR);
                case 12 -> String.format("#%06X", d.getOutlineColor());
                default -> "0";
            });
        }
        preciseInputField.setChangedListener(text -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                try {
                    for (int index : selectedTargets()) {
                        var d = textLineWidgets.get(index).data;
                        switch (preciseInputType) {
                            case 0 -> d.setXOffset(Float.parseFloat(text)); case 1 -> d.setYOffset(Float.parseFloat(text));
                            case 2 -> d.setFontSize(Math.max(0.1f, Float.parseFloat(text) / SCALE_DISPLAY_FACTOR));
                            case 3 -> { Integer c = tryParseHex(text); if (c != null) applyColor(3, c, d); }
                            case 4 -> d.setZOffset(Float.parseFloat(text));
                            case 5 -> d.setRotX(Float.parseFloat(text)); case 6 -> d.setRotY(Float.parseFloat(text));
                            case 7 -> d.setRotZ(Float.parseFloat(text));
                            case 8 -> d.setScaleX(Math.max(0.1f, Float.parseFloat(text) / SCALE_DISPLAY_FACTOR));
                            case 9 -> d.setScaleY(Math.max(0.1f, Float.parseFloat(text) / SCALE_DISPLAY_FACTOR));
                            case 10 -> d.setScaleZ(Math.max(0.1f, Float.parseFloat(text) / SCALE_DISPLAY_FACTOR));
                            case 12 -> { Integer c = tryParseHex(text); if (c != null) applyColor(12, c, d); }
                        }
                    }
                } catch (NumberFormatException ignored) {}
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        });
        backButton = ButtonWidget.builderCompat(Text.literal("←"), btn -> exitPreciseMode()).dimensions(0, 0, 20, 20).build();
    }

    private void initializeTextLines() {
        textLineWidgets.clear();
        for (var data : blockEntity.getTextLines()) {
            textLineWidgets.add(new TextLineWidget(data));
        }
        if (!textLineWidgets.isEmpty()) { selectOnlyLine(0); updateBottomPanelDisplay(); }
    }

    private void syncAndUpdateClient() {
        List<TextLineData> updatedLines = new ArrayList<>();
        for (TextLineWidget widget : textLineWidgets) {
            updatedLines.add(widget.data);
        }
        blockEntity.getTextLines().clear();
        blockEntity.getTextLines().addAll(updatedLines);

        if (Minecraft.getInstance().level != null) {
            blockEntity.setChanged();
            var state = blockEntity.getBlockState();
            Minecraft.getInstance().level.sendBlockUpdated(blockPos, state, state, 3);
        }
    }

    private boolean isAnyTextFieldFocused() {
        if (textField != null && textField.isFocused()) return true;
        if (preciseInputField != null && preciseInputField.isFocused()) return true;
        if (presetNameField != null && presetNameField.isFocused()) return true;
        return false;
    }

    private void refreshTopPanel() {
        recomputeLayout();
        for (var btn : textButtons) this.remove(btn);
        textButtons.clear();
        if (topScrollLeft != null) { this.remove(topScrollLeft); topScrollLeft = null; }
        if (topScrollRight != null) { this.remove(topScrollRight); topScrollRight = null; }

        if (textLineWidgets.isEmpty()) {
            selectedIndex = -1; selectedLineIndices.clear(); topScrollOffset = 0;
        } else {
            int count = textLineWidgets.size();
            if (topScrollOffset > Math.max(0, count - MAX_VISIBLE_TABS)) topScrollOffset = Math.max(0, count - MAX_VISIBLE_TABS);
            if (topScrollOffset < 0) topScrollOffset = 0;

            int visibleCount = Math.min(MAX_VISIBLE_TABS, count);
            int spacing = 2;
            int availableWidth = panelTopWidth;
            if (count > MAX_VISIBLE_TABS) availableWidth -= SCROLL_BTN_WIDTH * 2 + 8;

            int btnWidth = Math.max(20, (availableWidth - (visibleCount + 1) * spacing) / visibleCount);
            btnWidth = Math.min(btnWidth, MAX_LINE_TAB_WIDTH);
            int btnHeight = panelTopHeight - 4;
            int startX = panelTopX + spacing;

            if (count > MAX_VISIBLE_TABS) {
                startX += SCROLL_BTN_WIDTH + 4;
                topScrollLeft = ButtonWidget.builderCompat(Text.literal("◀"), b -> {
                    if (topScrollOffset > 0) { topScrollOffset--; refreshTopPanel(); }
                }).dimensions(panelTopX + 2, panelTopY + panelTopHeight / 2 - 10, SCROLL_BTN_WIDTH, 20).build();
                this.addDrawableChild(topScrollLeft);
                topScrollRight = ButtonWidget.builderCompat(Text.literal("▶"), b -> {
                    if (topScrollOffset < count - MAX_VISIBLE_TABS) { topScrollOffset++; refreshTopPanel(); }
                }).dimensions(panelTopX + panelTopWidth - SCROLL_BTN_WIDTH - 2, panelTopY + panelTopHeight / 2 - 10, SCROLL_BTN_WIDTH, 20).build();
                this.addDrawableChild(topScrollRight);
            }

            for (int i = 0; i < visibleCount; i++) {
                int idx = topScrollOffset + i;
                if (idx >= count) break;
                String displayText = lineButtonLabel(blockEntity.resolvePlaceholders(textLineWidgets.get(idx).data.getText()));
                if (displayText.isEmpty()) displayText = "(empty)";

                ButtonWidget btn = ButtonWidget.builderCompat(Text.literal(displayText), button -> {
                    if (formatPainterMode) {
                        applyFormatPainter(idx);
                    } else if (presetSelectMode && !presetSaveMode && !presetLoadMode) {
                        if (selectedPresetIndices.contains(idx)) selectedPresetIndices.remove(idx);
                        else selectedPresetIndices.add(idx);
                        refreshTopPanel();
                    } else if (!presetSaveMode && !presetLoadMode) {
                        if (hasShiftDown()) toggleLineSelection(idx);
                        else selectOnlyLine(idx);
                        preciseInputMode = false;
                        refreshBottomPanel(); refreshTopPanel();
                    }
                }).dimensions(startX + i * (btnWidth + spacing), panelTopY + 2, btnWidth, btnHeight).build();
                btn.active = true;
                textButtons.add(btn); this.addDrawableChild(btn);
            }
        }
        selectedLineIndices.removeIf(i -> i < 0 || i >= textLineWidgets.size());
        if (selectedIndex >= textLineWidgets.size()) selectedIndex = textLineWidgets.isEmpty() ? -1 : textLineWidgets.size() - 1;
        if (selectedIndex >= 0) selectedLineIndices.add(selectedIndex);
        savePresetButton.visible = presetSelectMode && !selectedPresetIndices.isEmpty() && !presetSaveMode && !presetLoadMode;
        boolean showCategoryButtons = isCategoryRowShown();
        posCatButton.visible = showCategoryButtons;
        rotCatButton.visible = showCategoryButtons;
        scaleCatButton.visible = showCategoryButtons;
        fontCatButton.visible = showCategoryButtons;
        alignCatButton.visible = showCategoryButtons;
        patternButton.visible = showCategoryButtons;
        settingsButton.visible = showCategoryButtons && supportsGlobalFontSetting;
        boolean showLineActions = isLineActionRowShown();
        copyLineButton.visible = showLineActions;
        pasteLineButton.visible = showLineActions;
        pasteLineButton.active = showLineActions && clipboardData != null;
        deleteLineButton.visible = showLineActions;
        formatPainterButton.visible = showLineActions;
        layoutSaveButtonRow(panelBottomY - SAVE_BTN_ROW_HEIGHT + 1);
    }

    private void recomputeLayout() {
        optionsRowVisible = computeOptionsRowVisible() || !customPropertyButtons.isEmpty();
        int optionsH = optionsRowVisible ? OPTIONS_ROW_HEIGHT : 0;
        panelTopY = panelBottomY - SAVE_BTN_ROW_HEIGHT - optionsH - panelTopHeight;
        int rowBtnY = panelBottomY - SAVE_BTN_ROW_HEIGHT + 1;
        savePresetButton.setPosition(width / 2 - 40, rowBtnY);
        addLineButton.setPosition(panelTopX + panelTopWidth, panelTopY);
        refreshOptionButtons();
    }

    private boolean isCategoryRowShown() {
        return !presetSelectMode && !presetSaveMode && !presetLoadMode && !formatPainterMode;
    }

    private boolean isLineActionRowShown() {
        return !textLineWidgets.isEmpty() && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()
                && !formatPainterMode && !presetSelectMode && !presetSaveMode && !presetLoadMode;
    }

    private void layoutSaveButtonRow(int rowBtnY) {
        if (rowScrollLeft != null) { this.remove(rowScrollLeft); rowScrollLeft = null; }
        if (rowScrollRight != null) { this.remove(rowScrollRight); rowScrollRight = null; }

        boolean showCategory = isCategoryRowShown();
        boolean showLineActions = isLineActionRowShown();
        List<ButtonWidget> categories = new ArrayList<>();
        if (showCategory) addVisible(categories,
                posCatButton, rotCatButton, scaleCatButton, fontCatButton, alignCatButton, patternButton, settingsButton);
        List<ButtonWidget> lineActions = new ArrayList<>();
        if (showLineActions) addVisible(lineActions,
                copyLineButton, pasteLineButton, deleteLineButton, formatPainterButton);
        if (categories.isEmpty() && lineActions.isEmpty()) { rowScrollIndex = 0; return; }

        final int leftMargin = 5, rightMargin = 4, gap = 4, splitGap = 12;
        int categoryW = rowWidth(categories, gap);
        int lineActionW = rowWidth(lineActions, gap);

        boolean fits = categories.isEmpty() || lineActions.isEmpty()
                ? leftMargin + categoryW + lineActionW + rightMargin <= width
                : leftMargin + categoryW + 8 <= width - rightMargin - lineActionW;

        if (fits) {
            rowScrollIndex = 0;
            int x = leftMargin;
            for (ButtonWidget b : categories) { b.setPosition(x, rowBtnY); x += b.getWidth() + gap; }
            x = width - rightMargin - lineActionW;
            for (ButtonWidget b : lineActions) { b.setPosition(x, rowBtnY); x += b.getWidth() + gap; }
            return;
        }

        List<ButtonWidget> items = new ArrayList<>(categories);
        int splitIndex = items.size();
        items.addAll(lineActions);

        int contentLeft = leftMargin + SCROLL_BTN_WIDTH + 4;
        int contentRight = width - rightMargin - SCROLL_BTN_WIDTH - 4;
        if (contentRight - contentLeft < widestButton(items)) {
            rowScrollIndex = 0;
            int x = leftMargin;
            for (ButtonWidget b : items) { b.visible = true; b.setPosition(x, rowBtnY); x += b.getWidth() + gap; }
            return;
        }

        PagedRowInfo info = layoutPagedRow(items, splitIndex, rowScrollIndex, contentLeft, contentRight, gap, splitGap, rowBtnY);
        rowScrollIndex = info.start();
        final int lastIndex = items.size() - 1;

        rowScrollLeft = ButtonWidget.builderCompat(Text.literal("◀"), b -> {
            rowScrollIndex = Math.max(0, rowScrollIndex - 1);
            refreshTopPanel();
        }).dimensions(leftMargin, rowBtnY, SCROLL_BTN_WIDTH, 20).build();
        rowScrollLeft.active = info.start() > 0;
        this.addDrawableChild(rowScrollLeft);
        rowScrollRight = ButtonWidget.builderCompat(Text.literal("▶"), b -> {
            rowScrollIndex = Math.min(lastIndex, rowScrollIndex + 1);
            refreshTopPanel();
        }).dimensions(width - rightMargin - SCROLL_BTN_WIDTH, rowBtnY, SCROLL_BTN_WIDTH, 20).build();
        rowScrollRight.active = info.lastShown() < lastIndex;
        this.addDrawableChild(rowScrollRight);
    }

    private static int rowWidth(List<ButtonWidget> buttons, int gap) {
        if (buttons.isEmpty()) return 0;
        int w = gap * (buttons.size() - 1);
        for (ButtonWidget b : buttons) w += b.getWidth();
        return w;
    }

    private static int widestButton(List<ButtonWidget> buttons) {
        int w = 0;
        for (ButtonWidget b : buttons) w = Math.max(w, b.getWidth());
        return w;
    }

    private static void addVisible(List<ButtonWidget> target, ButtonWidget... buttons) {
        for (ButtonWidget b : buttons) {
            if (b.visible) target.add(b);
        }
    }

    private PagedRowInfo layoutPagedRow(List<ButtonWidget> items, int splitIndex, int index,
                                        int contentLeft, int contentRight, int gap, int splitGap, int rowY) {
        int start = Math.max(0, Math.min(index, items.size() - 1));
        int x = contentLeft;
        int lastShown = start - 1;
        for (int i = start; i < items.size(); i++) {
            if (i > start) x += (i == splitIndex) ? splitGap : gap;
            ButtonWidget b = items.get(i);
            if (i > start && x + b.getWidth() > contentRight) break;
            b.visible = true;
            b.setPosition(x, rowY);
            x += b.getWidth();
            lastShown = i;
        }
        for (int i = 0; i < items.size(); i++) {
            if (i < start || i > lastShown) items.get(i).visible = false;
        }
        return new PagedRowInfo(start, lastShown);
    }

    private record PagedRowInfo(int start, int lastShown) {}

    private boolean computeOptionsRowVisible() {
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return false;
        for (String key : CustomSignBlockEntity.extractPlaceholderKeys(textLineWidgets.get(selectedIndex).data.getText())) {
            List<CustomSignBlockEntity.FieldOptionGroup> groups = blockEntity.getFieldOptions(key);
            if (groups != null && !groups.isEmpty()) return true;
        }
        return false;
    }

    private void refreshOptionButtons() {
        for (var b : optionButtons) this.remove(b);
        optionButtons.clear();
        optionGroupTitles.clear();
        if (!optionsRowVisible) return;

        int x = 5;
        int y = panelBottomY - SAVE_BTN_ROW_HEIGHT - OPTIONS_ROW_HEIGHT + (OPTIONS_ROW_HEIGHT - 20) / 2;

        // 占位符属性选项
        boolean overflow = false;
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            String text = textLineWidgets.get(selectedIndex).data.getText();
            outer:
            for (String key : CustomSignBlockEntity.extractPlaceholderKeys(text)) {
                List<CustomSignBlockEntity.FieldOptionGroup> groups = blockEntity.getFieldOptions(key);
                if (groups == null) continue;
                for (var group : groups) {
                    if (x + 40 > width - 10) { overflow = true; break outer; }
                    optionGroupTitles.add(new Object[]{group.title(), x, y + 6});
                    x += textRenderer.width(group.title()) + 6;
                    for (var opt : group.options()) {
                        int w = Math.max(20, textRenderer.width(opt.label()) + 8);
                        if (x + w > width - 5) { overflow = true; break outer; }
                        ButtonWidget btn = ButtonWidget.builderCompat(Text.literal(opt.label()), b -> applyOption(group.field(), opt.value()))
                                .dimensions(x, y, w, 20).build();
                        btn.active = !opt.current();
                        optionButtons.add(btn);
                        this.addDrawableChild(btn);
                        x += w + 4;
                    }
                    x += 10;
                }
            }
        }

        // 外部模组注入的自定义属性按钮（占位符选项已排满时不再追加）
        if (!overflow) {
            for (CustomButton cb : customPropertyButtons) {
                int w = customButtonWidth(cb);
                if (x + w > width - 5) break;
                ButtonWidget btn = buildCustomButton(cb, w, y);
                optionButtons.add(btn);
                this.addDrawableChild(btn);
                x += w + 4;
            }
        }
    }

    private void applyOption(String field, String value) {
        blockEntity.applyFieldOption(field, value);
        if (this.client != null) this.client.execute(() -> {
            refreshTopPanel();
            updateBottomPanelDisplay();
        });
        var packet = new CustomSignFieldUpdatePacket(blockPos, field, value);
        var buf = NetworkCompat.newBuffer();
        packet.write(buf);
        NetworkCompat.sendToServer(UPDATE_CUSTOM_SIGN_FIELD, buf);
    }

    private void refreshBottomPanel() {
        for (ButtonWidget b : builtControlCustomWidgets) this.remove(b);
        builtControlCustomWidgets.clear();
        this.remove(textField); this.remove(xButton); this.remove(yButton); this.remove(zButton);
        this.remove(rxButton); this.remove(ryButton); this.remove(rzButton);
        this.remove(sxButton); this.remove(syButton); this.remove(szButton);
        this.remove(fontSizeButton); this.remove(colorButton); this.remove(boldButton); this.remove(italicButton);
        this.remove(underlineButton); this.remove(shadowButton); this.remove(hAlignButton); this.remove(vAlignButton);
        this.remove(clearFormatButton);
        this.remove(outlineButton); this.remove(outlineColorButton); this.remove(glowButton);
        if (bottomRowScrollLeft != null) { this.remove(bottomRowScrollLeft); bottomRowScrollLeft = null; }
        if (bottomRowScrollRight != null) { this.remove(bottomRowScrollRight); bottomRowScrollRight = null; }
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
        for (ButtonWidget b : new ButtonWidget[]{xButton, yButton, zButton, rxButton, ryButton, rzButton,
                sxButton, syButton, szButton, fontSizeButton, colorButton, boldButton, italicButton,
                underlineButton, shadowButton, hAlignButton, vAlignButton, clearFormatButton,
                outlineButton, outlineColorButton, glowButton}) {
            b.visible = false;
        }

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
        currentPlaceholderText = d.getText();
        currentResolvedText = blockEntity.resolvePlaceholders(d.getText());
        suppressTextFieldListener = true;
        textField.setText(currentResolvedText);
        suppressTextFieldListener = false;
        colorButton.setMessage(colorMsg(d.getColor()));
        outlineColorButton.setMessage(colorMsg(d.getOutlineColor()));
        hAlignButton.setMessage(Text.literal(getHAlignText(d.getAlignment().hAlign)));
        vAlignButton.setMessage(Text.literal(getVAlignText(d.getAlignment().vAlign)));
    }

    private void addBottomWidgets() {
        int lh = (panelBottomHeight - 10) / 2;
        int y1 = panelBottomY + 5, y2 = panelBottomY + 5 + lh;
        textField.setWidth(panelBottomWidth - 10 - INFO_PANEL_WIDTH);
        textField.setPosition(panelBottomX + 5, y1);
        this.addDrawableChild(textField);
        List<ButtonWidget> row = new ArrayList<>();
        switch (activeCategory) {
            case POSITION -> { row.add(xButton); row.add(yButton); row.add(zButton); }
            case ROTATION -> { row.add(rxButton); row.add(ryButton); row.add(rzButton); }
            case SCALE -> { row.add(sxButton); row.add(syButton); row.add(szButton); row.add(fontSizeButton); }
            case FONT -> {
                row.add(colorButton); row.add(boldButton); row.add(italicButton); row.add(underlineButton);
                row.add(shadowButton); row.add(outlineButton); row.add(outlineColorButton); row.add(glowButton); row.add(clearFormatButton);
            }
            case ALIGN -> { row.add(hAlignButton); row.add(vAlignButton); }
        }
        // 外部模组注入的自定义按钮（排在标准按钮之后）
        for (CustomButton cb : customControlRowButtons) {
            ButtonWidget w = buildCustomButton(cb, customButtonWidth(cb), y2);
            builtControlCustomWidgets.add(w);
            row.add(w);
        }
        layoutBottomControlRow(row, y2);
    }

    private void layoutBottomControlRow(List<ButtonWidget> items, int rowY) {
        if (bottomRowScrollLeft != null) { this.remove(bottomRowScrollLeft); bottomRowScrollLeft = null; }
        if (bottomRowScrollRight != null) { this.remove(bottomRowScrollRight); bottomRowScrollRight = null; }
        if (items.isEmpty()) { bottomRowScrollIndex = 0; return; }

        for (ButtonWidget b : items) this.addDrawableChild(b);

        final int leftMargin = panelBottomX + 5;
        final int rightLimit = panelBottomX + panelBottomWidth - 5 - INFO_PANEL_WIDTH;

        if (rowWidth(items, BTN_GAP) <= rightLimit - leftMargin) {
            bottomRowScrollIndex = 0;
            int x = leftMargin;
            for (ButtonWidget b : items) { b.visible = true; b.setPosition(x, rowY); x += b.getWidth() + BTN_GAP; }
            return;
        }

        int contentLeft = leftMargin + SCROLL_BTN_WIDTH + 4;
        int contentRight = rightLimit - SCROLL_BTN_WIDTH - 4;
        if (contentRight - contentLeft < widestButton(items)) {
            bottomRowScrollIndex = 0;
            int x = leftMargin;
            for (ButtonWidget b : items) { b.visible = true; b.setPosition(x, rowY); x += b.getWidth() + BTN_GAP; }
            return;
        }

        PagedRowInfo info = layoutPagedRow(items, -1, bottomRowScrollIndex, contentLeft, contentRight, BTN_GAP, BTN_GAP, rowY);
        bottomRowScrollIndex = info.start();
        final int lastIndex = items.size() - 1;

        bottomRowScrollLeft = ButtonWidget.builderCompat(Text.literal("◀"), b -> {
            bottomRowScrollIndex = Math.max(0, bottomRowScrollIndex - 1);
            refreshBottomPanel();
        }).dimensions(leftMargin, rowY, SCROLL_BTN_WIDTH, 20).build();
        bottomRowScrollLeft.active = info.start() > 0;
        this.addDrawableChild(bottomRowScrollLeft);
        bottomRowScrollRight = ButtonWidget.builderCompat(Text.literal("▶"), b -> {
            bottomRowScrollIndex = Math.min(lastIndex, bottomRowScrollIndex + 1);
            refreshBottomPanel();
        }).dimensions(rightLimit - SCROLL_BTN_WIDTH, rowY, SCROLL_BTN_WIDTH, 20).build();
        bottomRowScrollRight.active = info.lastShown() < lastIndex;
        this.addDrawableChild(bottomRowScrollRight);
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
        confirmSaveButton = ButtonWidget.builderCompat(Text.literal("保存"), btn -> {
            String name = presetNameField.getText().trim();
            if (name.isEmpty()) return;
            List<TextLineData> lines = new ArrayList<>();
            for (int idx : selectedPresetIndices) lines.add(textLineWidgets.get(idx).data.copy());
            PresetManager.addPreset(name, lines);
            selectedPresetIndices.clear(); presetSaveMode = false;
            refreshTopPanel(); refreshBottomPanel();
        }).dimensions(panelBottomX + panelBottomWidth / 2 - 40, panelBottomY + 35, 35, 20).build();
        this.addDrawableChild(confirmSaveButton);
        cancelPresetButton = ButtonWidget.builderCompat(Text.literal("取消"), btn -> {
            selectedPresetIndices.clear(); presetSaveMode = false; refreshTopPanel(); refreshBottomPanel();
        }).dimensions(panelBottomX + panelBottomWidth / 2 + 5, panelBottomY + 35, 35, 20).build();
        this.addDrawableChild(cancelPresetButton);
    }

    private void addPresetLoadWidgets() {
        var presets = PresetManager.getPresets();
        if (presets.isEmpty()) {
            cancelLoadButton = ButtonWidget.builderCompat(Text.literal("返回"), btn -> { presetLoadMode = false; refreshBottomPanel(); })
                    .dimensions(panelBottomX + panelBottomWidth / 2 - 20, panelBottomY + panelBottomHeight / 2 + 10, 40, 20).build();
            this.addDrawableChild(cancelLoadButton);
            return;
        }
        List<String> names = new ArrayList<>(presets.keySet());
        int rows = 2;
        int rowGap = 2;
        int btnH = ((panelBottomHeight - 30) - rowGap) / rows;

        int maxOffset = Math.max(0, names.size() - 1);
        if (presetScrollOffset > maxOffset) presetScrollOffset = maxOffset;

        int x = panelBottomX + 5;
        int y = panelBottomY + 5;
        for (int i = presetScrollOffset; i < names.size(); i++) {
            String name = names.get(i);
            int btnW = Math.min(MAX_LINE_TAB_WIDTH, Math.max(20, textRenderer.width(name) + 8));

            if (x + btnW > panelBottomX + panelBottomWidth - 5) {
                x = panelBottomX + 5;
                y += btnH + rowGap;
            }
            if (y + btnH > panelBottomY + panelBottomHeight - 5) break;

            ButtonWidget btn = ButtonWidget.builderCompat(Text.literal(name), b -> {
                List<TextLineData> loaded = new ArrayList<>();
                for (var d : presets.get(name)) loaded.add(d.copy());
                blockEntity.getTextLines().addAll(loaded);
                for (var d : loaded) textLineWidgets.add(new TextLineWidget(d));
                selectOnlyLine(textLineWidgets.size() - loaded.size());
                topScrollOffset = Math.max(0, textLineWidgets.size() - MAX_VISIBLE_TABS);
                presetLoadMode = false;
                refreshTopPanel(); refreshBottomPanel();
                syncAndUpdateClient();
                sendUpdateToServer();
            }).dimensions(x, y, btnW, btnH).build();
            presetButtons.add(btn);
            this.addDrawableChild(btn);

            x += btnW + 2;
        }

        if (names.size() > 1) {
            presetScrollUp = ButtonWidget.builderCompat(Text.literal("◀"), b -> { if (presetScrollOffset > 0) { presetScrollOffset--; refreshBottomPanel(); } })
                    .dimensions(panelBottomX + 2, panelBottomY + panelBottomHeight - 25, 12, 20).build();
            presetScrollDown = ButtonWidget.builderCompat(Text.literal("▶"), b -> { if (presetScrollOffset < maxOffset) { presetScrollOffset++; refreshBottomPanel(); } })
                    .dimensions(panelBottomX + panelBottomWidth - 14, panelBottomY + panelBottomHeight - 25, 12, 20).build();
            this.addDrawableChild(presetScrollUp); this.addDrawableChild(presetScrollDown);
        }
        cancelLoadButton = ButtonWidget.builderCompat(Text.literal("返回"), btn -> { presetLoadMode = false; refreshBottomPanel(); })
                .dimensions(panelBottomX + panelBottomWidth / 2 - 20, panelBottomY + panelBottomHeight - 25, 40, 20).build();
        this.addDrawableChild(cancelLoadButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (SignGlobalSettingsOverlay.isVisible) {
            SignGlobalSettingsOverlay.keyPressed(keyCode, modifiers);
            return true;
        }
        if (PatternAndFontOverlay.isVisible) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                PatternAndFontOverlay.isVisible = false;
            }
            return true;
        }
        if (isAnyTextFieldFocused()) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (preciseInputMode && (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER)) {
            exitPreciseMode();
            return true;
        }
        if (presetSaveMode || presetLoadMode) return super.keyPressed(keyCode, scanCode, modifiers);
        if (handleEditorShortcut(keyCode)) return true;
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            presetSelectMode = true;
            formatPainterMode = false; formatPainterSourceIndex = -1;
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

    private boolean handleEditorShortcut(int keyCode) {
        boolean ctrl = hasControlDown();
        if (ctrl && keyCode == GLFW.GLFW_KEY_S) {
            syncAndUpdateClient();
            sendUpdateToServer();
            return true;
        }
        if (ctrl && keyCode == GLFW.GLFW_KEY_C) {
            copySelectedLine();
            return true;
        }
        if (ctrl && keyCode == GLFW.GLFW_KEY_V) {
            pasteSelectedLine();
            return true;
        }
        if (ctrl && keyCode == GLFW.GLFW_KEY_D) {
            duplicateSelectedLine();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DELETE && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            requestDeleteLine(selectedIndex);
            return true;
        }
        if (!ctrl) {
            if (keyCode == GLFW.GLFW_KEY_W) { selectCategory(Category.POSITION); return true; }
            if (keyCode == GLFW.GLFW_KEY_E) { selectCategory(Category.ROTATION); return true; }
            if (keyCode == GLFW.GLFW_KEY_R) { selectCategory(Category.SCALE); return true; }
            if (keyCode == GLFW.GLFW_KEY_F) { selectCategory(Category.FONT); return true; }
            if (keyCode == GLFW.GLFW_KEY_A) { selectCategory(Category.ALIGN); return true; }
        }
        float step = stepFor(POSITION_STEP, POSITION_ALT_STEP);
        if (keyCode == GLFW.GLFW_KEY_LEFT) return nudgeSelectedLine(-step, 0f, 0f);
        if (keyCode == GLFW.GLFW_KEY_RIGHT) return nudgeSelectedLine(step, 0f, 0f);
        if (keyCode == GLFW.GLFW_KEY_UP) return nudgeSelectedLine(0f, step, 0f);
        if (keyCode == GLFW.GLFW_KEY_DOWN) return nudgeSelectedLine(0f, -step, 0f);
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) return nudgeSelectedLine(0f, 0f, step);
        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) return nudgeSelectedLine(0f, 0f, -step);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (SignGlobalSettingsOverlay.isVisible) {
            return true;
        }
        if (PatternAndFontOverlay.isVisible) {
            return true;
        }
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
        if (SignGlobalSettingsOverlay.isVisible) {
            SignGlobalSettingsOverlay.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (preciseInputMode) { if (backButton != null && backButton.isMouseOver(mouseX, mouseY)) { exitPreciseMode(); return true; } return super.mouseClicked(mouseX, mouseY, button); }
        if (presetSaveMode || presetLoadMode) return super.mouseClicked(mouseX, mouseY, button);
        if (button == 1 && !presetSelectMode) {
            rotatingView = true;
            return true;
        }
        boolean consumed = super.mouseClicked(mouseX, mouseY, button);
        if (!consumed && button == 0 && !isOverUiPanel(mouseX, mouseY)) {
            consumed = tryGrabGizmo(mouseX, mouseY);
            if (!consumed) consumed = trySelectLine(mouseX, mouseY, hasShiftDown());
            if (!consumed) {
                beginBoxSelection(mouseX, mouseY, hasShiftDown());
                consumed = true;
            }
        }
        return consumed;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (SignGlobalSettingsOverlay.isVisible) {
            SignGlobalSettingsOverlay.mouseDragged(mouseX, mouseY, button);
            return true;
        }
        if (boxSelecting && button == 0) {
            boxCurrentX = mouseX;
            boxCurrentY = mouseY;
            return true;
        }
        if (rotatingView && button == 1) {
            rotateView(deltaX, deltaY);
            return true;
        }
        if (grabbedGizmo >= 0 && currentGizmoMode() >= 0) {
            applyGizmoDrag(mouseX, mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (SignGlobalSettingsOverlay.isVisible) {
            SignGlobalSettingsOverlay.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        if (button == 1 && rotatingView) {
            rotatingView = false;
            return true;
        }
        if (boxSelecting && button == 0) {
            boxCurrentX = mouseX;
            boxCurrentY = mouseY;
            finishBoxSelection();
            return true;
        }
        if (grabbedGizmo >= 0) {
            releaseGizmo();
            sendUpdateToServer();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        if (SignGlobalSettingsOverlay.isVisible) {
            SignGlobalSettingsOverlay.mouseScrolled(mouseX, mouseY, amount);
            return true;
        }
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.mouseScrolled(mouseX, mouseY, amount);
            return true;
        }
        if (amount != 0.0) {
            List<? extends GuiEventListener> children = this.children();
            for (int i = children.size() - 1; i >= 0; i--) {
                GuiEventListener child = children.get(i);
                if (child instanceof AbstractWidget widget && widget.visible
                        && widget.isMouseOver(mouseX, mouseY)) {
                    if (amount < 0.0 && isDirectionalStepButton(widget)) {
                        adjustDirectionalButton(widget);
                        return true;
                    }
                    widget.mouseClicked(mouseX, mouseY, 0);
                    return true;
                }
            }
        }
        return super.mouseScrolledCompat(mouseX, mouseY, amount);
    }

    private boolean isDirectionalStepButton(AbstractWidget widget) {
        return widget == xButton || widget == yButton || widget == zButton
                || widget == rxButton || widget == ryButton || widget == rzButton
                || widget == sxButton || widget == syButton || widget == szButton
                || widget == fontSizeButton || widget == colorButton || widget == outlineColorButton;
    }

    private void adjustDirectionalButton(AbstractWidget widget) {
        if (widget == xButton) adjustXYZ(0);
        else if (widget == yButton) adjustXYZ(1);
        else if (widget == zButton) adjustXYZ(4);
        else if (widget == rxButton) adjustXYZ(5);
        else if (widget == ryButton) adjustXYZ(6);
        else if (widget == rzButton) adjustXYZ(7);
        else if (widget == sxButton) adjustXYZ(8);
        else if (widget == syButton) adjustXYZ(9);
        else if (widget == szButton) adjustXYZ(10);
        else if (widget == fontSizeButton) adjustFontSize();
        else if (widget == colorButton) cycleColorByType(3, -1);
        else if (widget == outlineColorButton) cycleColorByType(12, -1);
    }

    private void rotateView(double deltaX, double deltaY) {
        if (minecraft == null || minecraft.player == null) return;
        minecraft.player.setYRot(minecraft.player.getYRot() + (float) deltaX * 0.35f);
        minecraft.player.setXRot(Math.max(-90f, Math.min(90f, minecraft.player.getXRot() + (float) deltaY * 0.35f)));
    }

    private void copySelectedLine() {
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return;
        clipboardData = textLineWidgets.get(selectedIndex).data.copy();
        refreshTopPanel();
    }

    private void pasteSelectedLine() {
        if (clipboardData == null || selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return;
        textLineWidgets.get(selectedIndex).data.applyFrom(clipboardData);
        updateBottomPanelDisplay();
        refreshTopPanel();
        refreshBottomPanel();
        syncAndUpdateClient();
        sendUpdateToServer();
    }

    private void duplicateSelectedLine() {
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return;
        TextLineData copy = textLineWidgets.get(selectedIndex).data.copy();
        copy.setBuiltin(false);
        int insertIndex = selectedIndex + 1;
        textLineWidgets.add(insertIndex, new TextLineWidget(copy));
        selectOnlyLine(insertIndex);
        topScrollOffset = Math.max(0, Math.min(selectedIndex, textLineWidgets.size() - MAX_VISIBLE_TABS));
        refreshTopPanel();
        refreshBottomPanel();
        syncAndUpdateClient();
        sendUpdateToServer();
    }

    private boolean nudgeSelectedLine(float dx, float dy, float dz) {
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return false;
        for (int index : selectedTargets()) {
            TextLineData line = textLineWidgets.get(index).data;
            line.setXOffset(line.getXOffset() + dx);
            line.setYOffset(line.getYOffset() + dy);
            line.setZOffset(line.getZOffset() + dz);
        }
        syncAndUpdateClient();
        refreshBottomPanel();
        sendUpdateToServer();
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (grabbedGizmo < 0) {
            if (currentGizmoMode() >= 0 && !isOverUiPanel(mouseX, mouseY)
                    && !textLineWidgets.isEmpty() && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                var d = textLineWidgets.get(selectedIndex).data;
                TextGizmo.hoverId = TextGizmo.pick(mouseX, mouseY, d.getScaleX(), d.getScaleY(), d.getScaleZ());
            } else {
                TextGizmo.hoverId = -1;
            }
        }
        super.mouseMoved(mouseX, mouseY);
    }

    private void requestDeleteLine(int idx) {
        var data = textLineWidgets.get(idx).data;
        String text = data.getText();
        boolean generated = data.isBuiltin()
                || !CustomSignBlockEntity.extractPlaceholderKeys(text).isEmpty()
                || text.trim().startsWith("-");
        if (!generated) {
            deleteTextLine(idx);
            return;
        }
        Minecraft.getInstance().setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) deleteTextLine(idx);
                    Minecraft.getInstance().setScreen(this);
                },
                Text.literal("删除文本行"),
                Text.literal("该行为系统生成或包含占位符/图片指令，删除后不会自动恢复，且将失去枚举按钮、字段联动、图片自动更新等功能。"),
                Text.literal("确定删除"),
                Text.literal("取消")));
    }

    private void deleteTextLine(int actualIdx) {
        releaseGizmo();
        blockEntity.getTextLines().remove(actualIdx); textLineWidgets.remove(actualIdx);
        selectedPresetIndices.remove(actualIdx);
        Set<Integer> newSet = new HashSet<>();
        for (int idx : selectedPresetIndices) newSet.add(idx > actualIdx ? idx - 1 : idx);
        selectedPresetIndices.clear(); selectedPresetIndices.addAll(newSet);
        LinkedHashSet<Integer> remappedSelection = new LinkedHashSet<>();
        for (int idx : selectedLineIndices) {
            if (idx != actualIdx) remappedSelection.add(idx > actualIdx ? idx - 1 : idx);
        }
        selectedLineIndices.clear();
        selectedLineIndices.addAll(remappedSelection);
        if (selectedLineIndices.isEmpty() && !textLineWidgets.isEmpty()) {
            selectedIndex = Math.min(actualIdx, textLineWidgets.size() - 1);
            selectedLineIndices.add(selectedIndex);
        } else {
            selectedIndex = selectedLineIndices.isEmpty() ? -1 : selectedLineIndices.stream().reduce((a, b) -> b).orElse(-1);
        }
        if (formatPainterSourceIndex == actualIdx) { formatPainterMode = false; formatPainterSourceIndex = -1; }
        else if (formatPainterSourceIndex > actualIdx) formatPainterSourceIndex--;
        preciseInputMode = false; refreshTopPanel(); refreshBottomPanel();
        syncAndUpdateClient();
        sendUpdateToServer();
    }

    private void applyFormatPainter(int targetIdx) {
        if (formatPainterSourceIndex < 0 || formatPainterSourceIndex >= textLineWidgets.size()
                || targetIdx < 0 || targetIdx >= textLineWidgets.size()) {
            formatPainterMode = false; formatPainterSourceIndex = -1;
            refreshTopPanel();
            return;
        }
        var src = textLineWidgets.get(formatPainterSourceIndex).data;
        var target = textLineWidgets.get(targetIdx).data;
        target.applyFormatFrom(src);
        formatPainterMode = false; formatPainterSourceIndex = -1;
        if (selectedIndex == targetIdx) updateBottomPanelDisplay();
        refreshTopPanel(); refreshBottomPanel();
        syncAndUpdateClient();
        sendUpdateToServer();
    }

    private void adjustXYZ(int type) {
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            for (int index : selectedTargets()) {
                var d = textLineWidgets.get(index).data;
                if (type == 5 || type == 6 || type == 7) {
                    float rotStep = -stepFor(15.0f, 5.0f);
                    switch (type) { case 5 -> d.setRotX(d.getRotX() + rotStep); case 6 -> d.setRotY(d.getRotY() + rotStep); case 7 -> d.setRotZ(d.getRotZ() + rotStep); }
                } else if (type == 8 || type == 9 || type == 10) {
                    float scaleStep = -stepFor(1f/16f, 1f/32f);
                    switch (type) {
                        case 8 -> d.setScaleX(Math.max(0.1f, d.getScaleX() + scaleStep));
                        case 9 -> d.setScaleY(Math.max(0.1f, d.getScaleY() + scaleStep));
                        case 10 -> d.setScaleZ(Math.max(0.1f, d.getScaleZ() + scaleStep));
                    }
                } else {
                    float step = -stepFor(POSITION_STEP, POSITION_ALT_STEP);
                    switch (type) { case 0 -> d.setXOffset(d.getXOffset() + step); case 1 -> d.setYOffset(d.getYOffset() + step); case 4 -> d.setZOffset(d.getZOffset() + step); }
                }
            }
            syncAndUpdateClient();
            sendUpdateToServer();
        }
    }
    private void adjustFontSize() {
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            float step = stepFor(1f/16f, 1f/32f);
            for (int index : selectedTargets()) {
                var d = textLineWidgets.get(index).data;
                d.setFontSize(Math.max(0.1f, d.getFontSize() - step));
            }
            syncAndUpdateClient();
            sendUpdateToServer();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (SignGlobalSettingsOverlay.isVisible) {
            blockEntity.setEditingLineIndex(-1);
            blockEntity.setEditingGizmoMode(-1);
            SignGlobalSettingsOverlay.render(context, mouseX, mouseY);
            return;
        }
        if (PatternAndFontOverlay.isVisible) {
            blockEntity.setEditingLineIndex(-1);
            blockEntity.setEditingGizmoMode(-1);
            PatternAndFontOverlay.render(context, mouseX, mouseY);
            return;
        }

        blockEntity.setEditingLineIndex(selectedIndex);
        blockEntity.setEditingGizmoMode(selectedIndex >= 0 ? currentGizmoMode() : -2);

        if (optionsRowVisible) {
            int oy = panelBottomY - SAVE_BTN_ROW_HEIGHT - OPTIONS_ROW_HEIGHT;
            context.fill(0, oy, width, panelBottomY - SAVE_BTN_ROW_HEIGHT, 0xAA333333);
            context.drawBorder(0, oy, width, OPTIONS_ROW_HEIGHT, 0xFF888888);
            for (Object[] t : optionGroupTitles) {
                context.drawText(textRenderer, Text.literal((String) t[0]), (Integer) t[1], (Integer) t[2], 0xFF66FFCC, false);
            }
        }

        context.fill(panelTopX, panelTopY, panelTopX + panelTopWidth, panelTopY + panelTopHeight, 0xAA333333);
        context.drawBorder(panelTopX, panelTopY, panelTopWidth, panelTopHeight, 0xFF888888);
        context.fill(panelTopX + panelTopWidth, panelTopY, panelTopX + panelTopWidth + ADD_BUTTON_WIDTH, panelTopY + panelTopHeight, 0xAA444444);
        context.drawBorder(panelTopX + panelTopWidth, panelTopY, ADD_BUTTON_WIDTH, panelTopHeight, 0xFF888888);

        context.fill(0, panelBottomY - SAVE_BTN_ROW_HEIGHT, width, panelBottomY, 0xAA222233);
        if (formatPainterMode) {
            String h = "格式刷模式：点击目标文本行标签应用格式（不含文字、位移/旋转）";
            context.drawText(textRenderer, Text.literal(h), (width - textRenderer.width(h)) / 2,
                    panelBottomY - SAVE_BTN_ROW_HEIGHT + (SAVE_BTN_ROW_HEIGHT - textRenderer.lineHeight) / 2, 0xFFFFDD55, false);
        }

        context.fill(panelBottomX, panelBottomY, panelBottomX + panelBottomWidth, panelBottomY + panelBottomHeight, 0xAA333333);
        context.drawBorder(panelBottomX, panelBottomY, panelBottomWidth, panelBottomHeight, 0xFF888888);

        if (!preciseInputMode && !presetSaveMode && !presetLoadMode) {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                var d = textLineWidgets.get(selectedIndex).data;
                int lh = (panelBottomHeight - 10) / 2, y1 = panelBottomY + 5, y2 = panelBottomY + 5 + lh;
                if (activeCategory == Category.FONT) {
                    int cx = panelBottomX + 5 + (BTN_SIZE + BTN_GAP);
                    drawToggleBg(context, cx, y2, BTN_SIZE, d.isBold()); cx += BTN_SIZE + BTN_GAP;
                    drawToggleBg(context, cx, y2, BTN_SIZE, d.isItalic()); cx += BTN_SIZE + BTN_GAP;
                    drawToggleBg(context, cx, y2, BTN_SIZE, d.isUnderline()); cx += BTN_SIZE + BTN_GAP;
                    drawToggleBg(context, cx, y2, BTN_SIZE, d.isShadow()); cx += BTN_SIZE + BTN_GAP;
                    drawToggleBg(context, cx, y2, BTN_SIZE, d.isOutline());
                }
                int statusY = y1 + 4;
                for (String line : getCategoryStatusLines(d)) {
                    context.drawText(textRenderer, Text.literal(line), panelBottomX + panelBottomWidth - INFO_PANEL_WIDTH + 5, statusY, 0xFF66FFCC, false);
                    statusY += textRenderer.lineHeight + 1;
                }
            }
            if (textLineWidgets.isEmpty()) {
                String h = "点击 + 添加文本, 按P加载预设";
                context.drawText(textRenderer, Text.literal(h), panelTopX + (panelTopWidth - textRenderer.width(h))/2, panelTopY + (panelTopHeight - textRenderer.lineHeight)/2, 0xFFAAAAAA, false);
            }
            if (selectedIndex < 0 && !textLineWidgets.isEmpty()) {
                String h = "选择文本, 按住Ctrl多选保存预设, 按P加载预设";
                context.drawText(textRenderer, Text.literal(h), panelBottomX + (panelBottomWidth - textRenderer.width(h))/2, panelBottomY + (panelBottomHeight - textRenderer.lineHeight)/2, 0xFFAAAAAA, false);
            }
        } else if (presetSaveMode) context.drawText(textRenderer, Text.literal("输入预设名称并保存"), panelBottomX + 5, panelBottomY + 2, 0xFFAAAAAA, false);
        else if (presetLoadMode && PresetManager.getPresets().isEmpty()) {
            String h = "暂无预设，请先保存预设";
            context.drawText(textRenderer, Text.literal(h), panelBottomX + (panelBottomWidth - textRenderer.width(h))/2, panelBottomY + (panelBottomHeight - textRenderer.lineHeight)/2 - 10, 0xFFAAAAAA, false);
        } else if (preciseInputMode) {
            String l = switch (preciseInputType) { case 0 -> "输入 X 坐标"; case 1 -> "输入 Y 坐标"; case 2 -> "输入字号"; case 3 -> "输入颜色 (#RRGGBB)"; case 4 -> "输入 Z 坐标"; case 5 -> "输入 X 轴旋转角度"; case 6 -> "输入 Y 轴旋转角度"; case 7 -> "输入 Z 轴旋转角度"; case 8 -> "输入 X 轴缩放"; case 9 -> "输入 Y 轴缩放"; case 10 -> "输入 Z 轴缩放"; case 12 -> "输入描边颜色 (#RRGGBB)"; default -> ""; };
            context.drawText(textRenderer, Text.literal(l), panelBottomX + 5, panelBottomY + 5, 0xFFAAAAAA, false);
        }

        super.render(context, mouseX, mouseY, delta);

        for (int i = 0; i < textButtons.size(); i++) {
            int actualIdx = topScrollOffset + i;
            if (selectedLineIndices.contains(actualIdx)) {
                var btn = textButtons.get(i);
                context.drawBorder(btn.getX() - 1, btn.getY() - 1, btn.getWidth() + 2, btn.getHeight() + 2,
                        actualIdx == selectedIndex ? 0xFF66FFCC : 0xFF55AAFF);
            }
        }

        for (int index : selectedLineIndices) {
            float[] rect = TextGizmo.lineRect(index);
            if (rect == null) continue;
            int x = Math.round(rect[0]);
            int y = Math.round(rect[1]);
            int w = Math.max(1, Math.round(rect[2] - rect[0]));
            int h = Math.max(1, Math.round(rect[3] - rect[1]));
            context.drawBorder(x, y, w, h, index == selectedIndex ? 0xFF66FFCC : 0xFF55AAFF);
        }

        if (boxSelecting) {
            int x0 = (int) Math.min(boxStartX, boxCurrentX);
            int y0 = (int) Math.min(boxStartY, boxCurrentY);
            int x1 = (int) Math.max(boxStartX, boxCurrentX);
            int y1 = (int) Math.max(boxStartY, boxCurrentY);
            context.fill(x0, y0, x1, y1, 0x3344AAFF);
            context.drawBorder(x0, y0, Math.max(1, x1 - x0), Math.max(1, y1 - y0), 0xFF66CCFF);
        }

        if (selectedLineIndices.size() > 1) {
            String h = "已选 " + selectedLineIndices.size() + " 项 · Shift+左键增减选择 · 拖拽框选 · 右键拖动视角";
            int badgeWidth = textRenderer.width(h) + 10;
            context.fill(5, 5, 5 + badgeWidth, 9 + textRenderer.lineHeight, 0xAA222233);
            context.drawBorder(5, 5, badgeWidth, textRenderer.lineHeight + 4, 0xFF66FFCC);
            context.drawText(textRenderer, Text.literal(h), 10, 7, 0xFF66FFCC, false);
        }

        for (int i = 0; i < textButtons.size(); i++) {
            int actualIdx = topScrollOffset + i;
            if (selectedPresetIndices.contains(actualIdx)) {
                var btn = textButtons.get(i);
                context.fill(btn.getX() - 1, btn.getY() - 1, btn.getX() + btn.getWidth() + 1, btn.getY() + btn.getHeight() + 1, 0x6600FF00);
            }
        }

        if (!presetSaveMode && !presetLoadMode && !preciseInputMode) {
            var d = (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) ? textLineWidgets.get(selectedIndex).data : null;
            List<TooltipEntry> tips = new ArrayList<>();
            if (xButton != null && xButton.visible && xButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("X 坐标", d != null ? String.format("当前值 %.1f", d.getXOffset()) : null, "左键 +0.1 | 滚轮上/下 ±0.1", "Alt ±0.05 | Shift ±0.4 | Ctrl+点击精准输入"));
            if (yButton != null && yButton.visible && yButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Y 坐标", d != null ? String.format("当前值 %.1f", d.getYOffset()) : null, "左键 +0.1 | 滚轮上/下 ±0.1", "Alt ±0.05 | Shift ±0.4 | Ctrl+点击精准输入"));
            if (zButton != null && zButton.visible && zButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Z 坐标", d != null ? String.format("当前值 %.1f", d.getZOffset()) : null, "左键 +0.1 | 滚轮上/下 ±0.1", "Alt ±0.05 | Shift ±0.4 | Ctrl+点击精准输入"));
            if (rxButton != null && rxButton.visible && rxButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("X 轴旋转", d != null ? String.format("当前值 %.1f°", d.getRotX()) : null, "左键 +15° | 滚轮上/下 ±15°", "Alt ±5° | Shift ±60° | Ctrl+点击精准输入"));
            if (ryButton != null && ryButton.visible && ryButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Y 轴旋转", d != null ? String.format("当前值 %.1f°", d.getRotY()) : null, "左键 +15° | 滚轮上/下 ±15°", "Alt ±5° | Shift ±60° | Ctrl+点击精准输入"));
            if (rzButton != null && rzButton.visible && rzButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Z 轴旋转", d != null ? String.format("当前值 %.1f°", d.getRotZ()) : null, "左键 +15° | 滚轮上/下 ±15°", "Alt ±5° | Shift ±60° | Ctrl+点击精准输入"));
            if (sxButton != null && sxButton.visible && sxButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("X 轴缩放", d != null ? String.format("当前值 %.2f", d.getScaleX() * SCALE_DISPLAY_FACTOR) : null, "左键 +1 | 滚轮上/下 ±1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (syButton != null && syButton.visible && syButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Y 轴缩放", d != null ? String.format("当前值 %.2f", d.getScaleY() * SCALE_DISPLAY_FACTOR) : null, "左键 +1 | 滚轮上/下 ±1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (szButton != null && szButton.visible && szButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Z 轴缩放", d != null ? String.format("当前值 %.2f", d.getScaleZ() * SCALE_DISPLAY_FACTOR) : null, "左键 +1 | 滚轮上/下 ±1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (fontSizeButton != null && fontSizeButton.visible && fontSizeButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("字号", d != null ? String.format("当前值 %.2f", d.getFontSize() * SCALE_DISPLAY_FACTOR) : null, "左键 +1 | 滚轮上/下 ±1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (shadowButton != null && shadowButton.visible && shadowButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("阴影", "原版阴影，点击开关"));
            if (outlineButton != null && outlineButton.visible && outlineButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("描边", "点击开关文字描边（颜色见右侧 ■）"));
            if (outlineColorButton != null && outlineColorButton.visible && outlineColorButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("描边颜色", d != null ? String.format("#%06X", d.getOutlineColor()) : null, "左键切换 | 滚轮反向切换 | Ctrl+点击打开色盘"));
            if (colorButton != null && colorButton.visible && colorButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("颜色", d != null ? String.format("当前值 #%06X", d.getColor()) : null, "点击切换 | Ctrl+点击打开色盘"));
            if (addLineButton != null && addLineButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("添加文本行", "按P加载预设"));
            if (patternButton != null && patternButton.visible && patternButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("图案", "打开图案与字体选择界面", "点击「插入」把图案/字体写入当前文本行"));
            if (settingsButton != null && settingsButton.visible && settingsButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("设置", "点击打开「云北路牌全局设置」页面", "Ctrl+S / Enter 保存，Esc 取消"));
            if (posCatButton != null && posCatButton.visible && posCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("位移 · W", "方向键调整 X/Y，PageUp/PageDown 调整 Z", "可在世界中拖拽坐标轴移动（Shift/Alt 调整步长）"));
            if (rotCatButton != null && rotCatButton.visible && rotCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("旋转 · E", "点击显示 RX/RY/RZ 旋转按钮", "可在世界中拖拽圆环旋转（Shift/Alt 调整步长）"));
            if (scaleCatButton != null && scaleCatButton.visible && scaleCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("缩放 · R", "点击显示 SX/SY/SZ 缩放按钮", "可拖拽绿框角点等比缩放、边点单轴缩放（Shift/Alt 调整步长）"));
            if (fontCatButton != null && fontCatButton.visible && fontCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("字体 · F", "点击显示字号/颜色/加粗/斜体/下划线/阴影/清空格式按钮"));
            if (alignCatButton != null && alignCatButton.visible && alignCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("对齐 · A", "点击显示水平/垂直对齐按钮"));
            if (copyLineButton != null && copyLineButton.visible && copyLineButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("复制", "Ctrl+C：复制该文本行的全部属性"));
            if (pasteLineButton != null && pasteLineButton.visible && pasteLineButton.isMouseOver(mouseX, mouseY)) tips.add(clipboardData != null ? TooltipEntry.of("粘贴", "Ctrl+V：将复制的属性覆盖到该文本行") : TooltipEntry.of("粘贴（已锁定）", "请先点击复制按钮或按 Ctrl+C"));
            if (deleteLineButton != null && deleteLineButton.visible && deleteLineButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("删除", "Delete：删除该文本行", "Ctrl+D：复制一份当前文本行"));
            if (formatPainterButton != null && formatPainterButton.visible && formatPainterButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("格式刷", "将颜色/对齐/加粗/斜体/下划线/阴影/字号", "复制到另一文本行（不含文字、位移、旋转）"));
            if (!tips.isEmpty()) drawTooltip(context, mouseX, mouseY, tips);
        }
    }

    private void drawToggleBg(DrawContext context, int x, int y, int size, boolean active) {
        if (active) { context.fill(x, y, x + size, y + size, 0xFF6B6BAA); context.drawBorder(x, y, size, size, 0xFFAAAAFF); }
    }

    private void drawTooltip(DrawContext context, int mx, int my, List<TooltipEntry> entries) {
        int lh = textRenderer.lineHeight + 2, mw = 0;
        List<TooltipLine> lines = new ArrayList<>();
        for (var e : entries) {
            if (!e.title.isEmpty()) { lines.add(new TooltipLine(e.title, 0xFFFFFFFF)); mw = Math.max(mw, textRenderer.width(e.title)); }
            if (e.value != null) { String v = "  " + e.value; lines.add(new TooltipLine(v, 0xFFFFD966)); mw = Math.max(mw, textRenderer.width(v)); }
            for (String d : e.descriptions) { lines.add(new TooltipLine("  " + d, 0xFFAAAAAA)); mw = Math.max(mw, textRenderer.width("  " + d)); }
        }
        int th = 4 + lines.size() * lh, tx = Math.min(mx + 12, width - mw - 10), ty = Math.min(my - th - 4, height - th - 4);
        if (ty < 4) ty = my + 12;
        context.getMatrices().pushPose();
        context.getMatrices().translate(0, 0, 400);
        context.fill(tx, ty, tx + mw + 8, ty + th, 0xFF1E1E2E);
        context.drawBorder(tx, ty, mw + 8, th, 0xFF6B6B8A);
        int ty2 = ty + 2;
        for (TooltipLine line : lines) { context.drawText(textRenderer, Text.literal(line.text), tx + 4, ty2, line.color, false); ty2 += lh; }
        context.getMatrices().popPose();
    }

    public void sendUpdateToServer() {
        syncWidgetsToBlockEntity();
        var packet = CustomSignUpdatePacket.update(blockPos, new ArrayList<>(blockEntity.getTextLines()));
        var buf = NetworkCompat.newBuffer();
        packet.write(buf);
        NetworkCompat.sendToServer(UPDATE_CUSTOM_SIGN, buf);
    }

    private void syncWidgetsToBlockEntity() {
        List<TextLineData> updatedLines = new ArrayList<>();
        for (TextLineWidget widget : textLineWidgets) {
            updatedLines.add(widget.data);
        }
        blockEntity.getTextLines().clear();
        blockEntity.getTextLines().addAll(updatedLines);
    }

    /**
     * 「全局设置」页的「一键转换」：把路牌自带行上的字体标签统一改写为自适应字体或原版字体。
     *
     * <p>只处理路牌自带的行（{@code builtin}），并且跳过 {@code -texture} / {@code -rect}
     * 这类指令行（改写会破坏它们）；用户自己新增的行不受影响。
     *
     * @param adaptive {@code true} = 全部转为自适应字体；{@code false} = 全部转为原版 uniform
     * @return 实际改写的行数
     */
    public int convertBuiltinFontTags(boolean adaptive) {
        int converted = 0;
        for (TextLineData line : blockEntity.getTextLines()) {
            if (line == null || !line.isBuiltin()) continue;
            if (!SignTextLinesHelper.canApplyFont(line.getText())) continue;
            // 每行用它自己该用的字体（方块自带布局写入的 a/b/c，缺失时按内容推断），
            // 而不是统一塞同一个字体——否则中英文混排的牌子会全部变成 a。
            SignTextLinesHelper.forceFont(line, adaptive ? SignTextLinesHelper.lineAbcFont(line) : "");
            converted++;
        }
        // 同步全局字体模式，使本次转换结果与后续新生成的行保持一致
        GlobalFontSettings.setMode(adaptive ? GlobalFontSettings.FontMode.ADAPTIVE
                : GlobalFontSettings.FontMode.VANILLA);
        if (converted > 0) {
            syncAndUpdateClient();
            refreshTopPanel();
            refreshBottomPanel();
            sendUpdateToServer();
        }
        return converted;
    }

    public int applyGlobalFontSettingToAllLines() {
        GlobalFontSettings.FontMode mode = GlobalFontSettings.getMode();
        int changed = 0;
        for (TextLineWidget widget : textLineWidgets) {
            TextLineData line = widget.data;
            if (!SignTextLinesHelper.canApplyFont(line.getText())) continue;
            String font = switch (mode) {
                case VANILLA -> "";
                case ABC_A -> "a";
                case ABC_B -> "b";
                case ABC_C -> "c";
                case ADAPTIVE -> SignTextLinesHelper.lineAbcFont(line);
            };
            SignTextLinesHelper.forceFont(line, font);
            changed++;
        }
        if (changed > 0) {
            syncAndUpdateClient();
            refreshTopPanel();
            refreshBottomPanel();
            sendUpdateToServer();
        }
        return changed;
    }

    @Override
    public void onClose() {
        rotatingView = false;
        PatternAndFontOverlay.closeOverlay();
        SignGlobalSettingsOverlay.close();
        TextGizmo.clear();
        sendUpdateToServer();
        super.onClose();
    }

    @Override
    public void removed() {
        rotatingView = false;
        blockEntity.setEditingLineIndex(-1);
        blockEntity.setEditingGizmoMode(-1);
        super.removed();
    }

    @Override
    public boolean shouldPauseCompat() {
        return false;
    }

    private static class TextLineWidget {
        final TextLineData data;
        TextLineWidget(TextLineData d) {
            this.data = d;
        }
    }

    private record TooltipEntry(String title, String value, String... descriptions) {
        static TooltipEntry of(String title, String... descriptions) { return new TooltipEntry(title, null, descriptions); }
    }
    private record TooltipLine(String text, int color) {}
}
