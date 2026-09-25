package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.beigu.yunbeiuc.network.CustomSignFieldUpdatePacket;
import com.beigu.yunbeiuc.network.CustomSignUpdatePacket;
import com.beigu.yunbeiuc.render.TextGizmo;
import com.beigu.yunbeiuc.util.GlobalFontSettings;
import com.beigu.yunbeiuc.util.PresetManager;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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
    // 「设置」属性面板内「原版字体 / ABC字体」两个全局选项按钮的宽度（容纳中英文标签 + 选中勾）
    private static final int FONT_OPTION_BTN_WIDTH = 88;
    private static final float SCALE_DISPLAY_FACTOR = 16f;
    private static final int[] COLOR_PALETTE = {0xFFFFFF, 0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF, 0xFFA500, 0x000000};

    private final CustomSignBlockEntity blockEntity;
    private final BlockPos blockPos;
    /** 当前方块是否为 sign 类方块：只有标志牌显示「设置」按钮，构造时确定一次。 */
    private final boolean signBlock;
    private final List<TextLineWidget> textLineWidgets = new ArrayList<>();
    private int selectedIndex = -1;

    private final List<ButtonWidget> textButtons = new ArrayList<>();
    private ButtonWidget addLineButton;

    private TextFieldWidget textField;
    private ButtonWidget xButton, yButton, zButton, fontSizeButton, colorButton;
    private ButtonWidget rxButton, ryButton, rzButton;
    private ButtonWidget sxButton, syButton, szButton;
    private ButtonWidget boldButton, italicButton, underlineButton, shadowButton;
    private ButtonWidget outlineButton, outlineColorButton;
    private ButtonWidget hAlignButton, vAlignButton, clearFormatButton;

    private enum Category { POSITION, ROTATION, SCALE, FONT, ALIGN, SETTINGS }
    private Category activeCategory = Category.POSITION;
    private ButtonWidget posCatButton, rotCatButton, scaleCatButton, fontCatButton, alignCatButton;
    // 图案按钮：紧贴「对齐」分类按钮右侧，点击打开图案与字体选择界面
    private ButtonWidget patternButton;
    // 设置按钮：紧贴图案按钮右侧，点击锁定「设置」分类并在底部属性面板内显示全局字体选项
    private ButtonWidget settingsButton;
    // 全局字体选项：位于「设置」属性面板内，由 addBottomWidgets 布局（与其它分类控件同样跟随面板显示）
    private ButtonWidget fontVanillaButton, fontAbcButton;

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

    // 保存按钮行（属性分类/图案/设置 + 行操作）：整行放不下时按索引分页并显示左右滚动按钮
    private int rowScrollIndex = 0;
    private ButtonWidget rowScrollLeft, rowScrollRight;
    // 底部属性面板控件行：与右侧状态显示区一起放不下时按索引分页并显示左右滚动按钮
    private int bottomRowScrollIndex = 0;
    private ButtonWidget bottomRowScrollLeft, bottomRowScrollRight;

    private int panelTopX, panelTopY, panelTopWidth, panelTopHeight;
    private int panelBottomX, panelBottomY, panelBottomWidth, panelBottomHeight;

    // 选项行（行标签面板上方）：选中行占位符关联可编辑枚举字段时显示按钮组，否则整行隐藏
    private static final int OPTIONS_ROW_HEIGHT = 22;
    private boolean optionsRowVisible = false;
    private final List<ButtonWidget> optionButtons = new ArrayList<>();
    private final List<Object[]> optionGroupTitles = new ArrayList<>();
    // 输入框显示占位符解析值：listener 抑制标志 + 记录占位符原文/解析值（未编辑则保留占位符联动）
    private boolean suppressTextFieldListener = false;
    private String currentPlaceholderText = "";
    private String currentResolvedText = "";

    private int grabbedGizmo = -1;
    private float grabValueStart, grabAxisStart, grabSize0, grabLen0;
    private float grabAnglePrev, grabAccumDeg;

    public TextDisplayScreen(CustomSignBlockEntity blockEntity) {
        super(Text.translatable("gui.yunbeiuc.custom_sign"));
        this.blockEntity = blockEntity;
        this.blockPos = blockEntity.getPos();
        this.signBlock = computeSignBlock(blockEntity);
    }

    /**
     * 判断方块是否为 sign 类（决定「设置」按钮是否显示）。
     *
     * <p>依据方块注册名（{@code yunbeiuc:<path>} 的 path）中是否包含 {@code sign}：
     * <ul>
     *     <li>内建标志牌（{@code AbstractEditableSignBlock} 族的 {@code sign_*}）→ 显示；</li>
     *     <li>自定义标志牌（{@code CustomSignTypeBlock} 的 {@code sign_custom_*}）→ 显示；</li>
     *     <li>路杆文本显示 / 路杆 LED / 龙门架 LED（{@code road_pole_text_display}、{@code road_pole_led}、
     *         {@code gantry_frame_led*}）→ 隐藏；</li>
     *     <li>区域信息板 {@code zones_board_*}（虽属标志牌类族，但注册名不含 sign）→ 隐藏。</li>
     * </ul>
     */
    private static boolean computeSignBlock(CustomSignBlockEntity blockEntity) {
        Identifier id = Registries.BLOCK.getId(blockEntity.getCachedState().getBlock());
        return id.getPath().contains("sign");
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

        // 属性分类按钮靠屏幕左侧依次排列，点击后锁定并在下方显示对应控件
        int catBtnW = 50, catBtnGap = 4;
        int catX = 5;
        int catY = panelTopY + panelTopHeight + 1;
        posCatButton = ButtonWidget.builder(Text.literal("位移"), b -> selectCategory(Category.POSITION)).dimensions(catX, catY, catBtnW, 20).build(); catX += catBtnW + catBtnGap;
        rotCatButton = ButtonWidget.builder(Text.literal("旋转"), b -> selectCategory(Category.ROTATION)).dimensions(catX, catY, catBtnW, 20).build(); catX += catBtnW + catBtnGap;
        scaleCatButton = ButtonWidget.builder(Text.literal("缩放"), b -> selectCategory(Category.SCALE)).dimensions(catX, catY, catBtnW, 20).build(); catX += catBtnW + catBtnGap;
        fontCatButton = ButtonWidget.builder(Text.literal("字体"), b -> selectCategory(Category.FONT)).dimensions(catX, catY, catBtnW, 20).build(); catX += catBtnW + catBtnGap;
        alignCatButton = ButtonWidget.builder(Text.literal("对齐"), b -> selectCategory(Category.ALIGN)).dimensions(catX, catY, catBtnW, 20).build();

        // 图案按钮：与属性分类按钮同行，位置在 recomputeLayout 中跟随「对齐」按钮
        patternButton = ButtonWidget.builder(Text.translatable("yunbeiuc.gui.button.pattern"), btn -> {
            PatternAndFontOverlay.targetScreen = this;
            PatternAndFontOverlay.isVisible = true;
            PatternAndFontOverlay.selectSidebarTop(PatternAndFontOverlay.NAV_TOP_HOME);
        }).dimensions(0, 0, catBtnW, 20).build();

        // 设置按钮：紧贴图案按钮右侧；点击后与属性分类按钮同样锁定，并打开对应的「设置」属性面板
        settingsButton = ButtonWidget.builder(Text.translatable("yunbeiuc.gui.button.settings"), btn -> selectCategory(Category.SETTINGS))
                .dimensions(0, 0, catBtnW, 20).build();
        // 原版字体 / ABC字体：全局字体开关（对新放置方块生效），显示在「设置」属性面板的控件行内
        fontVanillaButton = ButtonWidget.builder(Text.translatable("yunbeiuc.gui.font.vanilla"), btn -> {
            GlobalFontSettings.setAbcMode(false);
            updateFontSettingButtons();
        }).dimensions(0, 0, FONT_OPTION_BTN_WIDTH, 20).build();
        fontVanillaButton.visible = false;
        fontAbcButton = ButtonWidget.builder(Text.translatable("yunbeiuc.gui.font.abc"), btn -> {
            GlobalFontSettings.setAbcMode(true);
            updateFontSettingButtons();
        }).dimensions(0, 0, FONT_OPTION_BTN_WIDTH, 20).build();
        fontAbcButton.visible = false;

        updateCategoryButtonsLocked();

        // 行操作按钮靠屏幕右侧排列，正在编辑文本行时显示
        int lineActionBtnW = 45, lineActionGap = 4;
        int lineActionTotalW = lineActionBtnW * 4 + lineActionGap * 3;
        int lineActionStartX = sw - lineActionTotalW - 4;
        int lineActionY = panelTopY + panelTopHeight + 1;

        copyLineButton = ButtonWidget.builder(Text.literal("复制"), btn -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                clipboardData = textLineWidgets.get(selectedIndex).data.copy();
                refreshTopPanel();
            }
        }).dimensions(lineActionStartX, lineActionY, lineActionBtnW, 20).build();
        copyLineButton.visible = false;

        pasteLineButton = ButtonWidget.builder(Text.literal("粘贴"), btn -> {
            if (clipboardData != null && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                textLineWidgets.get(selectedIndex).data.applyFrom(clipboardData);
                updateBottomPanelDisplay();
                refreshTopPanel(); refreshBottomPanel();
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(lineActionStartX + (lineActionBtnW + lineActionGap), lineActionY, lineActionBtnW, 20).build();
        pasteLineButton.visible = false;
        pasteLineButton.active = false;

        deleteLineButton = ButtonWidget.builder(Text.literal("删除"), btn -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                requestDeleteLine(selectedIndex);
            }
        }).dimensions(lineActionStartX + (lineActionBtnW + lineActionGap) * 2, lineActionY, lineActionBtnW, 20).build();
        deleteLineButton.visible = false;

        formatPainterButton = ButtonWidget.builder(Text.literal("格式刷"), btn -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                formatPainterMode = true;
                formatPainterSourceIndex = selectedIndex;
                refreshTopPanel();
            }
        }).dimensions(lineActionStartX + (lineActionBtnW + lineActionGap) * 3, lineActionY, lineActionBtnW, 20).build();
        formatPainterButton.visible = false;

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
                // 编辑结果与解析值一致则保留占位符原文（维持字段联动），改动后写入纯文本
                textLineWidgets.get(selectedIndex).data.setText(text.equals(currentResolvedText) ? currentPlaceholderText : text);
                refreshTopPanel();
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        });

        xButton = makeXYZButton("X", 0); yButton = makeXYZButton("Y", 1); zButton = makeXYZButton("Z", 4);
        rxButton = makeRotButton("RX", 5); ryButton = makeRotButton("RY", 6); rzButton = makeRotButton("RZ", 7);
        sxButton = makeScaleButton("SX", 8); syButton = makeScaleButton("SY", 9); szButton = makeScaleButton("SZ", 10);
        fontSizeButton = ButtonWidget.builder(Text.literal("S"), button -> {
            if (hasControlDown()) enterPreciseMode(2);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                d.setFontSize(Math.max(0.1f, d.getFontSize() + stepFor(1f/16f, 1f/32f)));
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        colorButton = makeColorCycleButton(3);

        boldButton = makeToggle("B", s -> s.withBold(true), d -> { d.setBold(!d.isBold()); syncAndUpdateClient(); });
        italicButton = makeToggle("I", s -> s.withItalic(true), d -> { d.setItalic(!d.isItalic()); syncAndUpdateClient(); });
        underlineButton = makeToggle("U", s -> s.withUnderline(true), d -> { d.setUnderline(!d.isUnderline()); syncAndUpdateClient(); });
        shadowButton = makeToggle("D", s -> s.withBold(true), d -> { d.setShadow(!d.isShadow()); syncAndUpdateClient(); });
        outlineButton = makeToggle("O", s -> s.withBold(true), d -> { d.setOutline(!d.isOutline()); syncAndUpdateClient(); });
        outlineColorButton = makeColorCycleButton(12);

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
                d.setBold(false); d.setItalic(false); d.setUnderline(false); d.setShadow(false); d.setOutline(false);
                d.setColor(0xFFFFFF); d.setFontSize(1.0f); d.setAlignment(CustomSignBlockEntity.TextAlignment.CENTER_CENTER);
                colorButton.setMessage(colorMsg(0xFFFFFF));
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
                float step = stepFor(1.0f, 0.5f);
                switch (type) { case 0 -> d.setXOffset(d.getXOffset() + step); case 1 -> d.setYOffset(d.getYOffset() + step); case 4 -> d.setZOffset(d.getZOffset() + step); }
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private ButtonWidget makeRotButton(String label, int type) {
        return ButtonWidget.builder(Text.literal(label), button -> {
            if (hasControlDown()) enterPreciseMode(type);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                float step = stepFor(15.0f, 5.0f);
                switch (type) { case 5 -> d.setRotX(d.getRotX() + step); case 6 -> d.setRotY(d.getRotY() + step); case 7 -> d.setRotZ(d.getRotZ() + step); }
                syncAndUpdateClient();
                sendUpdateToServer();
            }
        }).dimensions(0, 0, ROT_BTN_WIDTH, BTN_SIZE).build();
    }

    private ButtonWidget makeScaleButton(String label, int type) {
        return ButtonWidget.builder(Text.literal(label), button -> {
            if (hasControlDown()) enterPreciseMode(type);
            else if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size() && !presetSaveMode && !presetLoadMode) {
                var d = textLineWidgets.get(selectedIndex).data;
                float step = stepFor(1f/16f, 1f/32f);
                switch (type) {
                    case 8 -> d.setScaleX(Math.max(0.1f, d.getScaleX() + step));
                    case 9 -> d.setScaleY(Math.max(0.1f, d.getScaleY() + step));
                    case 10 -> d.setScaleZ(Math.max(0.1f, d.getScaleZ() + step));
                }
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

    private ButtonWidget makeColorCycleButton(final int type) {
        return ButtonWidget.builder(Text.literal("■"), button -> {
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
        MinecraftClient.getInstance().setScreen(new ColorPickerScreen(this, currentColor, c -> {
            applyColor(type, c, d);
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
        applyColor(type, nc, d);
        syncAndUpdateClient();
        sendUpdateToServer();
    }

    private void applyColor(int type, int c, TextLineData d) {
        switch (type) {
            case 3 -> { d.setColor(c); colorButton.setMessage(colorMsg(c)); }
            default -> { d.setOutlineColor(c); outlineColorButton.setMessage(colorMsg(c)); }
        }
    }

    private static Text colorMsg(int c) { return Text.literal("■").styled(s -> s.withColor(c)); }

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
        // 切换分类后控件组不同，底部控件行分页位置归零
        bottomRowScrollIndex = 0;
        releaseGizmo();
        updateCategoryButtonsLocked();
        refreshBottomPanel();
    }

    private int currentGizmoMode() {
        if (PatternAndFontOverlay.isVisible) return -1;
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
        return true;
    }

    private static int axis(int handle) { return TextGizmo.handleAxis(handle); }

    private boolean isOverUiPanel(double mx, double my) {
        // 图案浮层覆盖全屏时视为始终处于 UI 上，避免误触世界中的拖拽手柄
        if (PatternAndFontOverlay.isVisible) return true;
        return my >= (optionsRowVisible ? panelTopY - OPTIONS_ROW_HEIGHT : panelTopY);
    }

    /**
     * 供图案与字体选择界面把内容写入当前编辑行。
     *
     * <p>未选中任何行时新建一行；写入后刷新面板并同步到客户端与服务端。
     *
     * @param text 要写入的文本（可为 -texture / -rect / -json 指令）
     */
    public void insertPatternContent(String text) {
        if (text == null) return;
        // 退出精准输入模式，让底部面板直接显示写入后的内容
        preciseInputMode = false;
        if (textLineWidgets.isEmpty() || selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) {
            TextLineData newData = new TextLineData(text);
            textLineWidgets.add(new TextLineWidget(newData));
            blockEntity.getTextLines().add(newData);
            selectedIndex = textLineWidgets.size() - 1;
            topScrollOffset = Math.max(0, textLineWidgets.size() - MAX_VISIBLE_TABS);
        } else {
            textLineWidgets.get(selectedIndex).data.setText(text);
        }
        refreshTopPanel();
        refreshBottomPanel();
        syncAndUpdateClient();
        sendUpdateToServer();
    }

    private boolean trySelectLine(double mouseX, double mouseY) {
        if (presetSaveMode || presetLoadMode || preciseInputMode || formatPainterMode || presetSelectMode) return false;
        int idx = TextGizmo.pickLine(mouseX, mouseY);
        if (idx < 0 || idx >= textLineWidgets.size()) return false;
        if (idx == selectedIndex) return true;
        selectedIndex = idx;
        releaseGizmo();
        preciseInputMode = false;
        refreshTopPanel();
        refreshBottomPanel();
        updateBottomPanelDisplay();
        return true;
    }

    private void applyGizmoDrag(double mouseX, double mouseY) {
        var d = textLineWidgets.get(selectedIndex).data;
        int kind = TextGizmo.handleKind(grabbedGizmo);
        int axis = TextGizmo.handleAxis(grabbedGizmo);
        switch (kind) {
            case TextGizmo.KIND_AXIS -> {
                Float v = TextGizmo.axisDragValue(axis, mouseX, mouseY);
                if (v != null) {
                    float val = snapToStep(grabValueStart + (v - grabAxisStart), stepFor(1.0f, 0.5f));
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
        syncWidgetsToBlockEntity();
        blockEntity.markDirty();
    }

    private static float sq(float v) { return v * v; }

    private void updateCategoryButtonsLocked() {
        posCatButton.active = activeCategory != Category.POSITION;
        rotCatButton.active = activeCategory != Category.ROTATION;
        scaleCatButton.active = activeCategory != Category.SCALE;
        fontCatButton.active = activeCategory != Category.FONT;
        alignCatButton.active = activeCategory != Category.ALIGN;
        settingsButton.active = activeCategory != Category.SETTINGS;
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
            case SETTINGS -> new String[]{"全局字体", GlobalFontSettings.isAbcMode() ? "ABC字体" : "原版字体"};
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
                var d = textLineWidgets.get(selectedIndex).data;
                try {
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
        recomputeLayout();
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
                String displayText = blockEntity.resolvePlaceholders(textLineWidgets.get(idx).data.getText());
                if (displayText.isEmpty()) displayText = "(empty)";

                ButtonWidget btn = ButtonWidget.builder(Text.literal(displayText), button -> {
                    if (formatPainterMode) {
                        applyFormatPainter(idx);
                    } else if (presetSelectMode && !presetSaveMode && !presetLoadMode) {
                        if (selectedPresetIndices.contains(idx)) selectedPresetIndices.remove(idx);
                        else selectedPresetIndices.add(idx);
                        refreshTopPanel();
                    } else if (!presetSaveMode && !presetLoadMode) {
                        selectedIndex = idx; preciseInputMode = false;
                        refreshBottomPanel(); refreshTopPanel();
                    }
                }).dimensions(startX + i * (btnWidth + spacing), panelTopY + 2, btnWidth, btnHeight).build();
                // 正在编辑的文本行使用原版按钮的禁用外观表示"锁定"，格式刷/多选模式下按钮另有用途，不锁定
                btn.active = formatPainterMode || presetSelectMode || idx != selectedIndex;
                textButtons.add(btn); this.addDrawableChild(btn);
            }
        }
        if (selectedIndex >= textLineWidgets.size()) selectedIndex = textLineWidgets.isEmpty() ? -1 : textLineWidgets.size() - 1;
        savePresetButton.visible = presetSelectMode && !selectedPresetIndices.isEmpty() && !presetSaveMode && !presetLoadMode;
        // 属性分类按钮：非预设/格式刷模式下显示
        boolean showCategoryButtons = isCategoryRowShown();
        posCatButton.visible = showCategoryButtons;
        rotCatButton.visible = showCategoryButtons;
        scaleCatButton.visible = showCategoryButtons;
        fontCatButton.visible = showCategoryButtons;
        alignCatButton.visible = showCategoryButtons;
        patternButton.visible = showCategoryButtons;
        // 设置按钮：仅 sign 类方块显示（全局字体设置对路杆文本显示/LED、龙门架 LED 无意义）
        settingsButton.visible = showCategoryButtons && signBlock;
        updateFontSettingButtons();
        // 正在编辑文本行时显示行操作按钮（靠屏幕右侧）；无剪贴板内容时粘贴按钮锁定
        boolean showLineActions = isLineActionRowShown();
        copyLineButton.visible = showLineActions;
        pasteLineButton.visible = showLineActions;
        pasteLineButton.active = showLineActions && clipboardData != null;
        deleteLineButton.visible = showLineActions;
        formatPainterButton.visible = showLineActions;
        // 分类/行操作按钮的可见性确定后再排版：分页滚动会在这里进一步隐藏放不下的按钮，
        // 必须在上面所有 visible 赋值之后执行，否则会被覆盖成"全部可见"而与滚动按钮重叠
        layoutSaveButtonRow(panelBottomY - SAVE_BTN_ROW_HEIGHT + 1);
    }

    // ==================== 全局字体设置 ====================

    /**
     * 刷新全局字体选项的选中标记：当前生效的模式在标签后打勾。
     *
     * <p>不使用 active=false 表示选中，那是禁用点击会让按钮点不动；按钮的显隐由
     * 「设置」属性面板（addBottomWidgets）统一控制，与此处无关。
     */
    private void updateFontSettingButtons() {
        if (fontVanillaButton == null || fontAbcButton == null) return;
        boolean abc = GlobalFontSettings.isAbcMode();
        Text vanilla = Text.translatable("yunbeiuc.gui.font.vanilla");
        Text abcLabel = Text.translatable("yunbeiuc.gui.font.abc");
        fontVanillaButton.setMessage(abc ? vanilla : vanilla.copy().append(" ✔"));
        fontAbcButton.setMessage(abc ? abcLabel.copy().append(" ✔") : abcLabel);
    }

    // ==================== 选项行布局 ====================

    /** 重算布局：选项行插在行标签面板与保存按钮行之间，可见时行标签面板上移 22px，各段始终紧贴无缝 */
    private void recomputeLayout() {
        optionsRowVisible = computeOptionsRowVisible();
        int optionsH = optionsRowVisible ? OPTIONS_ROW_HEIGHT : 0;
        panelTopY = panelBottomY - SAVE_BTN_ROW_HEIGHT - optionsH - panelTopHeight;
        int rowBtnY = panelBottomY - SAVE_BTN_ROW_HEIGHT + 1;
        savePresetButton.setPosition(width / 2 - 40, rowBtnY);
        // 分类/行操作按钮行的排版放在 refreshTopPanel 末尾（需在 visible 确定之后），此处只定位本行其它控件
        addLineButton.setPosition(panelTopX + panelTopWidth, panelTopY);
        refreshOptionButtons();
    }

    /** 属性分类按钮行是否显示（预设/格式刷等模式下整行隐藏） */
    private boolean isCategoryRowShown() {
        return !presetSelectMode && !presetSaveMode && !presetLoadMode && !formatPainterMode;
    }

    /** 行操作按钮是否显示（需选中文本行，预设/格式刷等模式下隐藏） */
    private boolean isLineActionRowShown() {
        return !textLineWidgets.isEmpty() && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()
                && !formatPainterMode && !presetSelectMode && !presetSaveMode && !presetLoadMode;
    }

    /**
     * 保存按钮行布局：内容顺序为「属性分类 → 图案 → 设置 → 行操作」。
     *
     * <p>宽度足够时保持原有外观（分类按钮靠左依次排列、行操作按钮靠右依次排列）；
     * 放不下时整行合并为一条**按索引分页**的内容：只摆放并显示完整落在内容区内的按钮，
     * 其余按钮隐藏（不绘制、不响应点击），行两端显示 ◀ ▶ 滚动按钮。
     * 与文本行标签行的分页做法一致，因此滚动按钮不会被溢出的内容按钮压住。
     */
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

        // 能否维持原有外观：分类按钮靠左依次排列、行操作按钮靠右依次排列，且两组不重叠
        boolean fits = categories.isEmpty() || lineActions.isEmpty()
                ? leftMargin + categoryW + lineActionW + rightMargin <= width
                : leftMargin + categoryW + 8 <= width - rightMargin - lineActionW;

        if (fits) {
            // 宽度足够：分类按钮靠左、行操作按钮靠右，维持原有布局
            rowScrollIndex = 0;
            int x = leftMargin;
            for (ButtonWidget b : categories) { b.setPosition(x, rowBtnY); x += b.getWidth() + gap; }
            x = width - rightMargin - lineActionW;
            for (ButtonWidget b : lineActions) { b.setPosition(x, rowBtnY); x += b.getWidth() + gap; }
            return;
        }

        // 放不下：整行合并分页滚动，两端留出左右滚动按钮的位置
        List<ButtonWidget> items = new ArrayList<>(categories);
        int splitIndex = items.size();
        items.addAll(lineActions);

        int contentLeft = leftMargin + SCROLL_BTN_WIDTH + 4;
        int contentRight = width - rightMargin - SCROLL_BTN_WIDTH - 4;
        if (contentRight - contentLeft < widestButton(items)) {
            // 极端窄窗口：内容区放不下任何一个按钮，此时显示滚动按钮必然重叠，改为平铺（按钮可能超出屏幕右边缘）
            rowScrollIndex = 0;
            int x = leftMargin;
            for (ButtonWidget b : items) { b.visible = true; b.setPosition(x, rowBtnY); x += b.getWidth() + gap; }
            return;
        }

        PagedRowInfo info = layoutPagedRow(items, splitIndex, rowScrollIndex, contentLeft, contentRight, gap, splitGap, rowBtnY);
        rowScrollIndex = info.start();
        final int lastIndex = items.size() - 1;

        rowScrollLeft = ButtonWidget.builder(Text.literal("◀"), b -> {
            rowScrollIndex = Math.max(0, rowScrollIndex - 1);
            refreshTopPanel();
        }).dimensions(leftMargin, rowBtnY, SCROLL_BTN_WIDTH, 20).build();
        rowScrollLeft.active = info.start() > 0;
        this.addDrawableChild(rowScrollLeft);
        rowScrollRight = ButtonWidget.builder(Text.literal("▶"), b -> {
            rowScrollIndex = Math.min(lastIndex, rowScrollIndex + 1);
            refreshTopPanel();
        }).dimensions(width - rightMargin - SCROLL_BTN_WIDTH, rowBtnY, SCROLL_BTN_WIDTH, 20).build();
        rowScrollRight.active = info.lastShown() < lastIndex;
        this.addDrawableChild(rowScrollRight);
    }

    /** 一行按钮按固定间隔排列后的总宽度（0 个按钮时为 0） */
    private static int rowWidth(List<ButtonWidget> buttons, int gap) {
        if (buttons.isEmpty()) return 0;
        int w = gap * (buttons.size() - 1);
        for (ButtonWidget b : buttons) w += b.getWidth();
        return w;
    }

    /** 一行中单个按钮的最大宽度 */
    private static int widestButton(List<ButtonWidget> buttons) {
        int w = 0;
        for (ButtonWidget b : buttons) w = Math.max(w, b.getWidth());
        return w;
    }

    /**
     * 按给定顺序收集其中当前可见的按钮。
     *
     * <p>排版时会按需设置 {@code visible}（分页隐藏放不下的按钮），因此必须先把本就应当隐藏的按钮
     * 排除在外——否则它会被排版逻辑重新置为可见（例如非 sign 方块上的「设置」按钮）。
     */
    private static void addVisible(List<ButtonWidget> target, ButtonWidget... buttons) {
        for (ButtonWidget b : buttons) {
            if (b.visible) target.add(b);
        }
    }

    /**
     * 按索引分页摆放一行按钮：从第 {@code index} 个开始向右依次排列，只保留完整落在
     * [{@code contentLeft}, {@code contentRight}] 内的按钮可见，其余一律隐藏。
     *
     * <p>隐藏而非整体平移，是滚动按钮不被内容压住的关键：滚动按钮占用了内容区两侧的位置，
     * 溢出的按钮若继续绘制就会与它们重叠。调用方需保证内容区至少能容纳最宽的按钮，
     * 因此本页第一个按钮必定显示。
     *
     * @param splitIndex 两组内容之间的间隔索引（间隔取 {@code splitGap}），没有分组时传 -1
     * @return 本页实际显示的索引区间
     */
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

    /** 分页行本页显示的索引区间（start 为起始索引，lastShown 为最后一个显示的索引） */
    private record PagedRowInfo(int start, int lastShown) {}

    private boolean computeOptionsRowVisible() {
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return false;
        for (String key : CustomSignBlockEntity.extractPlaceholderKeys(textLineWidgets.get(selectedIndex).data.getText())) {
            List<CustomSignBlockEntity.FieldOptionGroup> groups = blockEntity.getFieldOptions(key);
            if (groups != null && !groups.isEmpty()) return true;
        }
        return false;
    }

    /** 重建选项行按钮：按占位符 key 分组，组标题在 render 中绘制，当前值按钮以禁用外观表示选中 */
    private void refreshOptionButtons() {
        for (var b : optionButtons) this.remove(b);
        optionButtons.clear();
        optionGroupTitles.clear();
        if (!optionsRowVisible || selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return;
        String text = textLineWidgets.get(selectedIndex).data.getText();
        int x = 5;
        int y = panelBottomY - SAVE_BTN_ROW_HEIGHT - OPTIONS_ROW_HEIGHT + (OPTIONS_ROW_HEIGHT - 20) / 2;
        for (String key : CustomSignBlockEntity.extractPlaceholderKeys(text)) {
            List<CustomSignBlockEntity.FieldOptionGroup> groups = blockEntity.getFieldOptions(key);
            if (groups == null) continue;
            for (var group : groups) {
                if (x + 40 > width - 10) return;
                optionGroupTitles.add(new Object[]{group.title(), x, y + 6});
                x += textRenderer.getWidth(group.title()) + 6;
                for (var opt : group.options()) {
                    int w = Math.max(20, textRenderer.getWidth(opt.label()) + 8);
                    if (x + w > width - 5) return;
                    ButtonWidget btn = ButtonWidget.builder(Text.literal(opt.label()), b -> applyOption(group.field(), opt.value()))
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

    private void applyOption(String field, String value) {
        blockEntity.applyFieldOption(field, value);
        // 延迟到回调外刷新（占位符解析结果随字段变化），避免在按钮点击处理中改动控件树
        if (this.client != null) this.client.execute(() -> {
            refreshTopPanel();
            updateBottomPanelDisplay();
        });
        var packet = new CustomSignFieldUpdatePacket(blockPos, field, value);
        var buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);
        NetworkManager.sendToServer(UPDATE_CUSTOM_SIGN_FIELD, buf);
    }

    private void refreshBottomPanel() {
        this.remove(textField); this.remove(xButton); this.remove(yButton); this.remove(zButton);
        this.remove(rxButton); this.remove(ryButton); this.remove(rzButton);
        this.remove(sxButton); this.remove(syButton); this.remove(szButton);
        this.remove(fontSizeButton); this.remove(colorButton); this.remove(boldButton); this.remove(italicButton);
        this.remove(underlineButton); this.remove(shadowButton); this.remove(hAlignButton); this.remove(vAlignButton);
        this.remove(clearFormatButton);
        this.remove(outlineButton); this.remove(outlineColorButton);
        this.remove(fontVanillaButton); this.remove(fontAbcButton);
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
                outlineButton, outlineColorButton, fontVanillaButton, fontAbcButton}) {
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
        // 输入框显示占位符解析后的实际文本，避免出现 {text1} 字面量
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
        // 缩短输入框，为右侧当前值状态留出空间
        textField.setWidth(panelBottomWidth - 10 - INFO_PANEL_WIDTH);
        textField.setPosition(panelBottomX + 5, y1);
        this.addDrawableChild(textField);
        // 控件按分类收集（列表顺序即从左到右顺序），位置与溢出滚动统一由 layoutBottomControlRow 处理
        List<ButtonWidget> row = new ArrayList<>();
        switch (activeCategory) {
            case POSITION -> { row.add(xButton); row.add(yButton); row.add(zButton); }
            case ROTATION -> { row.add(rxButton); row.add(ryButton); row.add(rzButton); }
            case SCALE -> { row.add(sxButton); row.add(syButton); row.add(szButton); row.add(fontSizeButton); }
            case FONT -> {
                row.add(colorButton); row.add(boldButton); row.add(italicButton); row.add(underlineButton);
                row.add(shadowButton); row.add(outlineButton); row.add(outlineColorButton); row.add(clearFormatButton);
            }
            case ALIGN -> { row.add(hAlignButton); row.add(vAlignButton); }
            case SETTINGS -> {
                // 全局字体设置：与其它分类控件同一行，选中项在标签后打勾
                updateFontSettingButtons();
                row.add(fontVanillaButton); row.add(fontAbcButton);
            }
        }
        layoutBottomControlRow(row, y2);
    }

    /**
     * 底部属性面板控件行布局：控件靠左依次排列，右端留出 INFO_PANEL_WIDTH 作为当前值状态显示区。
     *
     * <p>放不下时按索引分页：只摆放并显示完整落在内容区内的控件，其余隐藏，行两端显示 ◀ ▶
     * （与文本行标签行一致），因此滚动按钮不会与控件重叠。
     */
    private void layoutBottomControlRow(List<ButtonWidget> items, int rowY) {
        if (bottomRowScrollLeft != null) { this.remove(bottomRowScrollLeft); bottomRowScrollLeft = null; }
        if (bottomRowScrollRight != null) { this.remove(bottomRowScrollRight); bottomRowScrollRight = null; }
        if (items.isEmpty()) { bottomRowScrollIndex = 0; return; }

        for (ButtonWidget b : items) this.addDrawableChild(b);

        final int leftMargin = panelBottomX + 5;
        final int rightLimit = panelBottomX + panelBottomWidth - 5 - INFO_PANEL_WIDTH;

        if (rowWidth(items, BTN_GAP) <= rightLimit - leftMargin) {
            // 放得下：与改动前一致，从左依次排列
            bottomRowScrollIndex = 0;
            int x = leftMargin;
            for (ButtonWidget b : items) { b.visible = true; b.setPosition(x, rowY); x += b.getWidth() + BTN_GAP; }
            return;
        }

        int contentLeft = leftMargin + SCROLL_BTN_WIDTH + 4;
        int contentRight = rightLimit - SCROLL_BTN_WIDTH - 4;
        if (contentRight - contentLeft < widestButton(items)) {
            // 极端窄窗口：内容区放不下任何一个控件，此时显示滚动按钮必然重叠，改为平铺
            bottomRowScrollIndex = 0;
            int x = leftMargin;
            for (ButtonWidget b : items) { b.visible = true; b.setPosition(x, rowY); x += b.getWidth() + BTN_GAP; }
            return;
        }

        PagedRowInfo info = layoutPagedRow(items, -1, bottomRowScrollIndex, contentLeft, contentRight, BTN_GAP, BTN_GAP, rowY);
        bottomRowScrollIndex = info.start();
        final int lastIndex = items.size() - 1;

        bottomRowScrollLeft = ButtonWidget.builder(Text.literal("◀"), b -> {
            bottomRowScrollIndex = Math.max(0, bottomRowScrollIndex - 1);
            refreshBottomPanel();
        }).dimensions(leftMargin, rowY, SCROLL_BTN_WIDTH, 20).build();
        bottomRowScrollLeft.active = info.start() > 0;
        this.addDrawableChild(bottomRowScrollLeft);
        bottomRowScrollRight = ButtonWidget.builder(Text.literal("▶"), b -> {
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
        int columns = 4, rows = 2;
        int perPage = columns * rows;
        int btnW = (panelBottomWidth - 10) / Math.min(columns, names.size());
        int rowGap = 2;
        int btnH = ((panelBottomHeight - 30) - rowGap) / rows;
        int maxOffset = Math.max(0, names.size() - perPage);
        if (presetScrollOffset > maxOffset) presetScrollOffset = maxOffset;

        for (int i = 0; i < Math.min(perPage, names.size() - presetScrollOffset); i++) {
            String name = names.get(presetScrollOffset + i);
            int col = i % columns, row = i / columns;
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
            }).dimensions(panelBottomX + 5 + col * btnW, panelBottomY + 5 + row * (btnH + rowGap), btnW - 2, btnH).build();
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
        // 图案浮层可见时独占键盘：ESC 仅关闭浮层，不影响底层编辑界面
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

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
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
        // 图案浮层可见时独占鼠标点击，底层编辑界面不响应
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (preciseInputMode) { if (backButton != null && backButton.isMouseOver(mouseX, mouseY)) { exitPreciseMode(); return true; } return super.mouseClicked(mouseX, mouseY, button); }
        if (presetSaveMode || presetLoadMode) return super.mouseClicked(mouseX, mouseY, button);
        if (button == 1 && !presetSelectMode) {
            if (xButton != null && xButton.visible && xButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(0); return true; }
            if (yButton != null && yButton.visible && yButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(1); return true; }
            if (zButton != null && zButton.visible && zButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(4); return true; }
            if (rxButton != null && rxButton.visible && rxButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(5); return true; }
            if (ryButton != null && ryButton.visible && ryButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(6); return true; }
            if (rzButton != null && rzButton.visible && rzButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(7); return true; }
            if (sxButton != null && sxButton.visible && sxButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(8); return true; }
            if (syButton != null && syButton.visible && syButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(9); return true; }
            if (szButton != null && szButton.visible && szButton.isMouseOver(mouseX, mouseY)) { adjustXYZ(10); return true; }
            if (fontSizeButton != null && fontSizeButton.visible && fontSizeButton.isMouseOver(mouseX, mouseY)) { adjustFontSize(); return true; }
            if (colorButton != null && colorButton.visible && colorButton.isMouseOver(mouseX, mouseY)) { cycleColorByType(3, -1); return true; }
            if (outlineColorButton != null && outlineColorButton.visible && outlineColorButton.isMouseOver(mouseX, mouseY)) { cycleColorByType(12, -1); return true; }
        }
        boolean consumed = super.mouseClicked(mouseX, mouseY, button);
        if (!consumed && button == 0 && !isOverUiPanel(mouseX, mouseY)) {
            consumed = tryGrabGizmo(mouseX, mouseY);
            if (!consumed) consumed = trySelectLine(mouseX, mouseY);
        }
        return consumed;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (grabbedGizmo >= 0 && currentGizmoMode() >= 0) {
            applyGizmoDrag(mouseX, mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        if (grabbedGizmo >= 0) {
            releaseGizmo();
            sendUpdateToServer();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.mouseScrolled(mouseX, mouseY, amount);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
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

    /**
     * 删除前检查：自带生成的行（系统默认布局 builtin 标记，或含 {占位符}、-texture/-rect/-json 指令）需二次确认——
     * 删除后不会自动恢复，且将失去枚举按钮、字段联动、图片自动更新等功能；其余纯文本行直接删除
     */
    private void requestDeleteLine(int idx) {
        var data = textLineWidgets.get(idx).data;
        String text = data.getText();
        // builtin 标记覆盖"占位符已被用户改写"的系统行；旧存档行无标记，仍按占位符/指令前缀判断
        boolean generated = data.isBuiltin()
                || !CustomSignBlockEntity.extractPlaceholderKeys(text).isEmpty()
                || text.trim().startsWith("-");
        if (!generated) {
            deleteTextLine(idx);
            return;
        }
        MinecraftClient.getInstance().setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) deleteTextLine(idx);
                    MinecraftClient.getInstance().setScreen(this);
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
        if (selectedIndex >= textLineWidgets.size()) selectedIndex = textLineWidgets.isEmpty() ? -1 : textLineWidgets.size() - 1;
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
            var d = textLineWidgets.get(selectedIndex).data;
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
                float step = -stepFor(1.0f, 0.5f);
                switch (type) { case 0 -> d.setXOffset(d.getXOffset() + step); case 1 -> d.setYOffset(d.getYOffset() + step); case 4 -> d.setZOffset(d.getZOffset() + step); }
            }
            syncAndUpdateClient();
            sendUpdateToServer();
        }
    }
    private void adjustFontSize() {
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            var d = textLineWidgets.get(selectedIndex).data;
            d.setFontSize(Math.max(0.1f, d.getFontSize() - stepFor(1f/16f, 1f/32f)));
            syncAndUpdateClient();
            sendUpdateToServer();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 图案与字体选择浮层打开时，隐藏底层文本编辑界面：不绘制任何面板/控件，
        // 并把世界中的编辑高亮与拖拽手柄一并隐藏（置 -1），只渲染浮层
        if (PatternAndFontOverlay.isVisible) {
            blockEntity.setEditingLineIndex(-1);
            blockEntity.setEditingGizmoMode(-1);
            PatternAndFontOverlay.render(context, mouseX, mouseY);
            return;
        }

        blockEntity.setEditingLineIndex(selectedIndex);
        blockEntity.setEditingGizmoMode(currentGizmoMode());

        // 选项行（行标签面板与保存按钮行之间）：行背景 + 组标题（按钮由 super.render 绘制）
        if (optionsRowVisible) {
            int oy = panelBottomY - SAVE_BTN_ROW_HEIGHT - OPTIONS_ROW_HEIGHT;
            context.fill(0, oy, width, panelBottomY - SAVE_BTN_ROW_HEIGHT, 0xAA333333);
            context.drawBorder(0, oy, width, OPTIONS_ROW_HEIGHT, 0xFF888888);
            for (Object[] t : optionGroupTitles) {
                context.drawText(textRenderer, Text.literal((String) t[0]), (Integer) t[1], (Integer) t[2], 0xFF66FFCC, false);
            }
        }

        // 顶部面板
        context.fill(panelTopX, panelTopY, panelTopX + panelTopWidth, panelTopY + panelTopHeight, 0xAA333333);
        context.drawBorder(panelTopX, panelTopY, panelTopWidth, panelTopHeight, 0xFF888888);
        context.fill(panelTopX + panelTopWidth, panelTopY, panelTopX + panelTopWidth + ADD_BUTTON_WIDTH, panelTopY + panelTopHeight, 0xAA444444);
        context.drawBorder(panelTopX + panelTopWidth, panelTopY, ADD_BUTTON_WIDTH, panelTopHeight, 0xFF888888);

        // 保存按钮行（全宽，锚定底部属性面板上缘）
        context.fill(0, panelBottomY - SAVE_BTN_ROW_HEIGHT, width, panelBottomY, 0xAA222233);
        if (formatPainterMode) {
            String h = "格式刷模式：点击目标文本行标签应用格式（不含文字、位移/旋转）";
            context.drawText(textRenderer, Text.literal(h), (width - textRenderer.getWidth(h)) / 2,
                    panelBottomY - SAVE_BTN_ROW_HEIGHT + (SAVE_BTN_ROW_HEIGHT - textRenderer.fontHeight) / 2, 0xFFFFDD55, false);
        }

        // 底部面板
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
                    statusY += textRenderer.fontHeight + 1;
                }
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
            String l = switch (preciseInputType) { case 0 -> "输入 X 坐标"; case 1 -> "输入 Y 坐标"; case 2 -> "输入字号"; case 3 -> "输入颜色 (#RRGGBB)"; case 4 -> "输入 Z 坐标"; case 5 -> "输入 X 轴旋转角度"; case 6 -> "输入 Y 轴旋转角度"; case 7 -> "输入 Z 轴旋转角度"; case 8 -> "输入 X 轴缩放"; case 9 -> "输入 Y 轴缩放"; case 10 -> "输入 Z 轴缩放"; case 12 -> "输入描边颜色 (#RRGGBB)"; default -> ""; };
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
            var d = (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) ? textLineWidgets.get(selectedIndex).data : null;
            List<TooltipEntry> tips = new ArrayList<>();
            if (xButton != null && xButton.visible && xButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("X 坐标", d != null ? String.format("当前值 %.1f", d.getXOffset()) : null, "左键 +1 | 右键 -1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (yButton != null && yButton.visible && yButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Y 坐标", d != null ? String.format("当前值 %.1f", d.getYOffset()) : null, "左键 +1 | 右键 -1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (zButton != null && zButton.visible && zButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Z 坐标", d != null ? String.format("当前值 %.1f", d.getZOffset()) : null, "左键 +1 | 右键 -1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (rxButton != null && rxButton.visible && rxButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("X 轴旋转", d != null ? String.format("当前值 %.1f°", d.getRotX()) : null, "左键 +15° | 右键 -15°", "Alt ±5° | Shift ±60° | Ctrl+点击精准输入"));
            if (ryButton != null && ryButton.visible && ryButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Y 轴旋转", d != null ? String.format("当前值 %.1f°", d.getRotY()) : null, "左键 +15° | 右键 -15°", "Alt ±5° | Shift ±60° | Ctrl+点击精准输入"));
            if (rzButton != null && rzButton.visible && rzButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Z 轴旋转", d != null ? String.format("当前值 %.1f°", d.getRotZ()) : null, "左键 +15° | 右键 -15°", "Alt ±5° | Shift ±60° | Ctrl+点击精准输入"));
            if (sxButton != null && sxButton.visible && sxButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("X 轴缩放", d != null ? String.format("当前值 %.2f", d.getScaleX() * SCALE_DISPLAY_FACTOR) : null, "左键 +1 | 右键 -1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (syButton != null && syButton.visible && syButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Y 轴缩放", d != null ? String.format("当前值 %.2f", d.getScaleY() * SCALE_DISPLAY_FACTOR) : null, "左键 +1 | 右键 -1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (szButton != null && szButton.visible && szButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("Z 轴缩放", d != null ? String.format("当前值 %.2f", d.getScaleZ() * SCALE_DISPLAY_FACTOR) : null, "左键 +1 | 右键 -1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (fontSizeButton != null && fontSizeButton.visible && fontSizeButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("字号", d != null ? String.format("当前值 %.2f", d.getFontSize() * SCALE_DISPLAY_FACTOR) : null, "左键 +1 | 右键 -1", "Alt ±0.5 | Shift ±4 | Ctrl+点击精准输入"));
            if (shadowButton != null && shadowButton.visible && shadowButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("阴影", "原版阴影，点击开关"));
            if (outlineButton != null && outlineButton.visible && outlineButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("描边", "点击开关文字描边（颜色见右侧 ■）"));
            if (outlineColorButton != null && outlineColorButton.visible && outlineColorButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("描边颜色", d != null ? String.format("#%06X", d.getOutlineColor()) : null, "左键切换 | 右键反向切换 | Ctrl+点击打开色盘"));
            if (colorButton != null && colorButton.visible && colorButton.isMouseOver(mouseX, mouseY)) tips.add(new TooltipEntry("颜色", d != null ? String.format("当前值 #%06X", d.getColor()) : null, "点击切换 | Ctrl+点击打开色盘"));
            if (addLineButton != null && addLineButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("添加文本行", "按P加载预设"));
            if (patternButton != null && patternButton.visible && patternButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("图案", "打开图案与字体选择界面", "点击「插入」把图案/字体写入当前文本行"));
            if (settingsButton != null && settingsButton.visible && settingsButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("设置", "点击打开「设置」属性面板", "面板内为全局字体设置：原版字体 / ABC字体"));
            if (fontVanillaButton != null && fontVanillaButton.visible && fontVanillaButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("原版字体", "全局字体设置为原版字体", "对新放置的标识方块生效"));
            if (fontAbcButton != null && fontAbcButton.visible && fontAbcButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("ABC字体", "全局字体设置为 ABC 交通字体", "对新放置的标识方块生效"));
            if (posCatButton != null && posCatButton.visible && posCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("位移", "点击显示 X/Y/Z 坐标按钮", "可在世界中拖拽坐标轴移动（Shift/Alt 调整步长）"));
            if (rotCatButton != null && rotCatButton.visible && rotCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("旋转", "点击显示 RX/RY/RZ 旋转按钮", "可在世界中拖拽圆环旋转（Shift/Alt 调整步长）"));
            if (scaleCatButton != null && scaleCatButton.visible && scaleCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("缩放", "点击显示 SX/SY/SZ 缩放按钮", "可拖拽绿框角点等比缩放、边点单轴缩放（Shift/Alt 调整步长）"));
            if (fontCatButton != null && fontCatButton.visible && fontCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("字体", "点击显示字号/颜色/加粗/斜体/下划线/阴影/清空格式按钮"));
            if (alignCatButton != null && alignCatButton.visible && alignCatButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("对齐", "点击显示水平/垂直对齐按钮"));
            if (copyLineButton != null && copyLineButton.visible && copyLineButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("复制", "复制该文本行的全部属性"));
            if (pasteLineButton != null && pasteLineButton.visible && pasteLineButton.isMouseOver(mouseX, mouseY)) tips.add(clipboardData != null ? TooltipEntry.of("粘贴", "将复制的属性覆盖到该文本行") : TooltipEntry.of("粘贴（已锁定）", "请先点击复制按钮复制一个文本行"));
            if (deleteLineButton != null && deleteLineButton.visible && deleteLineButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("删除", "删除该文本行"));
            if (formatPainterButton != null && formatPainterButton.visible && formatPainterButton.isMouseOver(mouseX, mouseY)) tips.add(TooltipEntry.of("格式刷", "将颜色/对齐/加粗/斜体/下划线/阴影/字号", "复制到另一文本行（不含文字、位移、旋转）"));
            if (!tips.isEmpty()) drawTooltip(context, mouseX, mouseY, tips);
        }
    }

    private void drawToggleBg(DrawContext context, int x, int y, int size, boolean active) {
        if (active) { context.fill(x, y, x + size, y + size, 0xFF6B6BAA); context.drawBorder(x, y, size, size, 0xFFAAAAFF); }
    }

    private void drawTooltip(DrawContext context, int mx, int my, List<TooltipEntry> entries) {
        int lh = textRenderer.fontHeight + 2, mw = 0;
        List<TooltipLine> lines = new ArrayList<>();
        for (var e : entries) {
            if (!e.title.isEmpty()) { lines.add(new TooltipLine(e.title, 0xFFFFFFFF)); mw = Math.max(mw, textRenderer.getWidth(e.title)); }
            if (e.value != null) { String v = "  " + e.value; lines.add(new TooltipLine(v, 0xFFFFD966)); mw = Math.max(mw, textRenderer.getWidth(v)); }
            for (String d : e.descriptions) { lines.add(new TooltipLine("  " + d, 0xFFAAAAAA)); mw = Math.max(mw, textRenderer.getWidth("  " + d)); }
        }
        int th = 4 + lines.size() * lh, tx = Math.min(mx + 12, width - mw - 10), ty = Math.min(my - th - 4, height - th - 4);
        if (ty < 4) ty = my + 12;
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 400);
        context.fill(tx, ty, tx + mw + 8, ty + th, 0xFF1E1E2E);
        context.drawBorder(tx, ty, mw + 8, th, 0xFF6B6B8A);
        int ty2 = ty + 2;
        for (TooltipLine line : lines) { context.drawText(textRenderer, Text.literal(line.text), tx + 4, ty2, line.color, false); ty2 += lh; }
        context.getMatrices().pop();
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
        PatternAndFontOverlay.closeOverlay();
        blockEntity.setEditingLineIndex(-1);
        blockEntity.setEditingGizmoMode(-1);
        TextGizmo.clear();
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

    private record TooltipEntry(String title, String value, String... descriptions) {
        static TooltipEntry of(String title, String... descriptions) { return new TooltipEntry(title, null, descriptions); }
    }
    private record TooltipLine(String text, int color) {}
}