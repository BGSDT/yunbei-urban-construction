package com.beigu.yunbeiuc.client.screen;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.beigu.yunbeiuc.network.CustomSignUpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static com.beigu.yunbeiuc.network.ModMessages.UPDATE_CUSTOM_SIGN;

public class CustomSignScreen extends Screen {
    private static final int MAX_TEXT_LINES = 10;
    private static final int PANEL_TOP_HEIGHT_RATIO = 6;
    private static final int PANEL_BOTTOM_HEIGHT_RATIO = 5;
    private static final int ADD_BUTTON_WIDTH = 20;
    private static final int BTN_SIZE = 20;
    private static final int BTN_GAP = 5;

    private final CustomSignBlockEntity blockEntity;
    private final BlockPos blockPos;
    private final List<TextLineWidget> textLineWidgets = new ArrayList<>();
    private int selectedIndex = -1;

    private final List<ButtonWidget> textButtons = new ArrayList<>();
    private ButtonWidget addLineButton;

    private TextFieldWidget textField;
    private ButtonWidget xButton;
    private ButtonWidget yButton;
    private ButtonWidget fontSizeButton;
    private ButtonWidget colorButton;
    private ButtonWidget boldButton;
    private ButtonWidget italicButton;
    private ButtonWidget underlineButton;
    private ButtonWidget shadowButton;
    private ButtonWidget alignButton;
    private ButtonWidget clearFormatButton;

    private boolean preciseInputMode = false;
    private TextFieldWidget preciseInputField;
    private ButtonWidget backButton;
    private int preciseInputType = 0;

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

        int screenWidth = this.width;
        int screenHeight = this.height;

        panelTopHeight = screenHeight / PANEL_TOP_HEIGHT_RATIO;
        panelTopX = 0;
        panelTopY = 0;
        panelTopWidth = screenWidth - ADD_BUTTON_WIDTH;

        panelBottomHeight = screenHeight / PANEL_BOTTOM_HEIGHT_RATIO;
        panelBottomWidth = screenWidth;
        panelBottomX = 0;
        panelBottomY = screenHeight - panelBottomHeight;

        addLineButton = ButtonWidget.builder(Text.literal("+"), button -> {
            if (textLineWidgets.size() < MAX_TEXT_LINES) {
                CustomSignBlockEntity.TextLineData newData = new CustomSignBlockEntity.TextLineData("Text " + (textLineWidgets.size() + 1));
                textLineWidgets.add(new TextLineWidget(newData));
                blockEntity.getTextLines().add(newData);
                selectedIndex = textLineWidgets.size() - 1;
                refreshTopPanel();
                refreshBottomPanel();
                sendUpdateToServer();
            }
        }).dimensions(panelTopX + panelTopWidth, panelTopY, ADD_BUTTON_WIDTH, panelTopHeight).build();

        createBottomPanelWidgets();
        initializeTextLines();
        refreshTopPanel();
        refreshBottomPanel();

        this.addDrawableChild(addLineButton);
    }

    private void createBottomPanelWidgets() {
        textField = new TextFieldWidget(this.textRenderer, 0, 0, panelBottomWidth - 10, 16, Text.literal("Text"));
        textField.setChangedListener(text -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                textLineWidgets.get(selectedIndex).data.setText(text);
                refreshTopPanel();
                sendUpdateToServer();
            }
        });

        xButton = ButtonWidget.builder(Text.literal("X"), button -> {
            if (hasControlDown()) {
                enterPreciseMode(0);
            } else {
                if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                    CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                    float step = hasAltDown() ? 0.5f : 1.0f;
                    data.setXOffset(data.getXOffset() + step);
                    sendUpdateToServer();
                }
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        yButton = ButtonWidget.builder(Text.literal("Y"), button -> {
            if (hasControlDown()) {
                enterPreciseMode(1);
            } else {
                if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                    CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                    float step = hasAltDown() ? 0.5f : 1.0f;
                    data.setYOffset(data.getYOffset() + step);
                    sendUpdateToServer();
                }
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        fontSizeButton = ButtonWidget.builder(Text.literal("S"), button -> {
            if (hasControlDown()) {
                enterPreciseMode(2);
            } else {
                if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                    CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                    float step = hasAltDown() ? 1f / 32f : 1f / 16f;
                    data.setFontSize(Math.max(0.5f, Math.min(3.0f, data.getFontSize() + step)));
                    sendUpdateToServer();
                }
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        int[] colors = {0xFFFFFF, 0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF, 0xFFA500, 0x000000};
        colorButton = ButtonWidget.builder(
                Text.literal("■"),
                button -> {
                    if (hasControlDown()) {
                        enterPreciseMode(3);
                    } else {
                        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                            CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                            int currentIndex = -1;
                            for (int i = 0; i < colors.length; i++) {
                                if (colors[i] == data.getColor()) {
                                    currentIndex = i;
                                    break;
                                }
                            }
                            data.setColor(colors[(currentIndex + 1) % colors.length]);
                            colorButton.setMessage(Text.literal("■").styled(style -> style.withColor(data.getColor())));
                            sendUpdateToServer();
                        }
                    }
                }
        ).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        boldButton = ButtonWidget.builder(
                Text.literal("B").styled(style -> style.withBold(true)),
                btn -> {
                    if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                        CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                        data.setBold(!data.isBold());
                        sendUpdateToServer();
                    }
                }
        ).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        italicButton = ButtonWidget.builder(
                Text.literal("I").styled(style -> style.withItalic(true)),
                btn -> {
                    if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                        CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                        data.setItalic(!data.isItalic());
                        sendUpdateToServer();
                    }
                }
        ).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        underlineButton = ButtonWidget.builder(
                Text.literal("U").styled(style -> style.withUnderline(true)),
                btn -> {
                    if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                        CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                        data.setUnderline(!data.isUnderline());
                        sendUpdateToServer();
                    }
                }
        ).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        shadowButton = ButtonWidget.builder(
                Text.literal("D").styled(style -> style.withBold(true)),
                btn -> {
                    if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                        CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                        data.setShadow(!data.isShadow());
                        sendUpdateToServer();
                    }
                }
        ).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();

        alignButton = ButtonWidget.builder(
                Text.literal("居中"),
                button -> {
                    if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                        CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                        CustomSignBlockEntity.TextAlignment[] alignments = CustomSignBlockEntity.TextAlignment.values();
                        int currentOrdinal = data.getAlignment().ordinal();
                        data.setAlignment(alignments[(currentOrdinal + 1) % alignments.length]);
                        alignButton.setMessage(Text.literal(getAlignText(data.getAlignment())));
                        sendUpdateToServer();
                    }
                }
        ).dimensions(0, 0, BTN_SIZE + 24, BTN_SIZE).build();

        clearFormatButton = ButtonWidget.builder(Text.literal("✕"), button -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                data.setBold(false);
                data.setItalic(false);
                data.setUnderline(false);
                data.setShadow(false);
                data.setColor(0xFFFFFF);
                data.setFontSize(1.0f);
                colorButton.setMessage(Text.literal("■").styled(style -> style.withColor(0xFFFFFF)));
                alignButton.setMessage(Text.literal("居中"));
                sendUpdateToServer();
            }
        }).dimensions(0, 0, BTN_SIZE, BTN_SIZE).build();
    }

    private void enterPreciseMode(int type) {
        preciseInputMode = true;
        preciseInputType = type;
        refreshBottomPanel();
    }

    private void exitPreciseMode() {
        preciseInputMode = false;
        refreshBottomPanel();
    }

    private void createPreciseInputWidgets() {
        String label = switch (preciseInputType) {
            case 0 -> "X";
            case 1 -> "Y";
            case 2 -> "S";
            case 3 -> "Color";
            default -> "";
        };

        preciseInputField = new TextFieldWidget(this.textRenderer, 0, 0, panelBottomWidth - 60, 16, Text.literal(label));
        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
            String currentValue = switch (preciseInputType) {
                case 0 -> String.format("%.1f", data.getXOffset());
                case 1 -> String.format("%.1f", data.getYOffset());
                case 2 -> String.format("%.2f", data.getFontSize());
                case 3 -> String.format("#%06X", data.getColor());
                default -> "0";
            };
            preciseInputField.setText(currentValue);
        }
        preciseInputField.setChangedListener(text -> {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                switch (preciseInputType) {
                    case 0 -> {
                        try {
                            data.setXOffset(Float.parseFloat(text));
                        } catch (NumberFormatException ignored) {}
                    }
                    case 1 -> {
                        try {
                            data.setYOffset(Float.parseFloat(text));
                        } catch (NumberFormatException ignored) {}
                    }
                    case 2 -> {
                        try {
                            data.setFontSize(Math.max(0.5f, Math.min(3.0f, Float.parseFloat(text))));
                        } catch (NumberFormatException ignored) {}
                    }
                    case 3 -> {
                        try {
                            String hex = text.replace("#", "").trim();
                            if (hex.length() == 6) {
                                data.setColor(Integer.parseInt(hex, 16));
                                colorButton.setMessage(Text.literal("■").styled(style -> style.withColor(data.getColor())));
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
                sendUpdateToServer();
            }
        });

        backButton = ButtonWidget.builder(Text.literal("←"), button -> {
            exitPreciseMode();
        }).dimensions(0, 0, 20, 20).build();
    }

    private void initializeTextLines() {
        textLineWidgets.clear();
        List<CustomSignBlockEntity.TextLineData> dataList = blockEntity.getTextLines();
        for (CustomSignBlockEntity.TextLineData data : dataList) {
            textLineWidgets.add(new TextLineWidget(data));
        }
        if (!textLineWidgets.isEmpty()) {
            selectedIndex = 0;
            updateBottomPanelDisplay();
        }
    }

    private void refreshTopPanel() {
        for (ButtonWidget btn : textButtons) {
            this.remove(btn);
        }
        textButtons.clear();

        if (textLineWidgets.isEmpty()) {
            selectedIndex = -1;
        } else {
            int count = textLineWidgets.size();
            int spacing = 5;
            int totalSpacing = (count + 1) * spacing;
            int btnWidth = (panelTopWidth - totalSpacing) / count;
            int btnHeight = panelTopHeight - 10;

            for (int i = 0; i < count; i++) {
                final int idx = i;
                TextLineWidget widget = textLineWidgets.get(i);
                String displayText = widget.data.getText().isEmpty() ? "(empty)" : widget.data.getText();
                if (displayText.length() > 10) {
                    displayText = displayText.substring(0, 10) + "...";
                }

                ButtonWidget btn = ButtonWidget.builder(
                        Text.literal(displayText),
                        button -> {
                            selectedIndex = idx;
                            preciseInputMode = false;
                            refreshBottomPanel();
                        }
                ).dimensions(
                        panelTopX + spacing + idx * (btnWidth + spacing),
                        panelTopY + 5,
                        btnWidth,
                        btnHeight
                ).build();

                textButtons.add(btn);
                this.addDrawableChild(btn);
            }
        }

        if (selectedIndex >= textLineWidgets.size()) {
            selectedIndex = textLineWidgets.isEmpty() ? -1 : textLineWidgets.size() - 1;
        }
    }

    private void refreshBottomPanel() {
        this.remove(textField);
        this.remove(xButton);
        this.remove(yButton);
        this.remove(fontSizeButton);
        this.remove(colorButton);
        this.remove(boldButton);
        this.remove(italicButton);
        this.remove(underlineButton);
        this.remove(shadowButton);
        this.remove(alignButton);
        this.remove(clearFormatButton);
        if (preciseInputField != null) {
            this.remove(preciseInputField);
        }
        if (backButton != null) {
            this.remove(backButton);
        }

        if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
            if (preciseInputMode) {
                addPreciseInputWidgets();
            } else {
                addBottomWidgets();
                updateBottomPanelDisplay();
            }
        }
    }

    private void updateBottomPanelDisplay() {
        if (selectedIndex < 0 || selectedIndex >= textLineWidgets.size()) return;
        CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;

        textField.setText(data.getText());
        colorButton.setMessage(Text.literal("■").styled(style -> style.withColor(data.getColor())));
        alignButton.setMessage(Text.literal(getAlignText(data.getAlignment())));
    }

    private String getAlignText(CustomSignBlockEntity.TextAlignment alignment) {
        return switch (alignment) {
            case LEFT -> "左对齐";
            case CENTER -> "居中";
            case RIGHT -> "右对齐";
        };
    }

    private void addBottomWidgets() {
        int lineHeight = (panelBottomHeight - 10) / 2;

        int yRow1 = panelBottomY + 5;
        textField.setWidth(panelBottomWidth - 10);
        textField.setPosition(panelBottomX + 5, yRow1);
        this.addDrawableChild(textField);

        int yRow2 = panelBottomY + 5 + lineHeight;
        int curX = panelBottomX + 5;

        xButton.setPosition(curX, yRow2);
        this.addDrawableChild(xButton);
        curX += BTN_SIZE + BTN_GAP;

        yButton.setPosition(curX, yRow2);
        this.addDrawableChild(yButton);
        curX += BTN_SIZE + BTN_GAP;

        fontSizeButton.setPosition(curX, yRow2);
        this.addDrawableChild(fontSizeButton);
        curX += BTN_SIZE + BTN_GAP;

        colorButton.setPosition(curX, yRow2);
        this.addDrawableChild(colorButton);
        curX += BTN_SIZE + BTN_GAP;

        boldButton.setPosition(curX, yRow2);
        this.addDrawableChild(boldButton);
        curX += BTN_SIZE + BTN_GAP;

        italicButton.setPosition(curX, yRow2);
        this.addDrawableChild(italicButton);
        curX += BTN_SIZE + BTN_GAP;

        underlineButton.setPosition(curX, yRow2);
        this.addDrawableChild(underlineButton);
        curX += BTN_SIZE + BTN_GAP;

        shadowButton.setPosition(curX, yRow2);
        this.addDrawableChild(shadowButton);
        curX += BTN_SIZE + BTN_GAP;

        alignButton.setPosition(curX, yRow2);
        this.addDrawableChild(alignButton);
        curX += BTN_SIZE + 24 + BTN_GAP;

        clearFormatButton.setPosition(curX, yRow2);
        this.addDrawableChild(clearFormatButton);
    }

    private void addPreciseInputWidgets() {
        createPreciseInputWidgets();

        int yRow1 = panelBottomY + (panelBottomHeight - 20) / 2;
        preciseInputField.setPosition(panelBottomX + 30, yRow1);
        this.addDrawableChild(preciseInputField);

        backButton.setPosition(panelBottomX + 5, yRow1);
        this.addDrawableChild(backButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (preciseInputMode && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            exitPreciseMode();
            return true;
        }
        if (preciseInputMode && keyCode == GLFW.GLFW_KEY_ENTER) {
            exitPreciseMode();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (preciseInputMode) {
            if (backButton != null && backButton.isMouseOver(mouseX, mouseY)) {
                exitPreciseMode();
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (button == 1) {
            if (xButton != null && xButton.isMouseOver(mouseX, mouseY)) {
                if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                    CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                    float step = hasAltDown() ? 0.5f : 1.0f;
                    data.setXOffset(data.getXOffset() - step);
                    sendUpdateToServer();
                    return true;
                }
            }
            if (yButton != null && yButton.isMouseOver(mouseX, mouseY)) {
                if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                    CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                    float step = hasAltDown() ? 0.5f : 1.0f;
                    data.setYOffset(data.getYOffset() - step);
                    sendUpdateToServer();
                    return true;
                }
            }
            if (fontSizeButton != null && fontSizeButton.isMouseOver(mouseX, mouseY)) {
                if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                    CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                    float step = hasAltDown() ? 1f / 32f : 1f / 16f;
                    data.setFontSize(Math.max(0.5f, Math.min(3.0f, data.getFontSize() - step)));
                    sendUpdateToServer();
                    return true;
                }
            }
            for (int i = 0; i < textButtons.size(); i++) {
                ButtonWidget btn = textButtons.get(i);
                if (mouseX >= btn.getX() && mouseX < btn.getX() + btn.getWidth()
                        && mouseY >= btn.getY() && mouseY < btn.getY() + btn.getHeight()) {
                    blockEntity.getTextLines().remove(i);
                    textLineWidgets.remove(i);
                    if (selectedIndex >= textLineWidgets.size()) {
                        selectedIndex = textLineWidgets.isEmpty() ? -1 : textLineWidgets.size() - 1;
                    }
                    preciseInputMode = false;
                    refreshTopPanel();
                    refreshBottomPanel();
                    sendUpdateToServer();
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(panelTopX, panelTopY, panelTopX + panelTopWidth, panelTopY + panelTopHeight, 0xAA333333);
        context.drawBorder(panelTopX, panelTopY, panelTopWidth, panelTopHeight, 0xFF888888);

        context.fill(panelTopX + panelTopWidth, panelTopY, panelTopX + panelTopWidth + ADD_BUTTON_WIDTH, panelTopY + panelTopHeight, 0xAA444444);
        context.drawBorder(panelTopX + panelTopWidth, panelTopY, ADD_BUTTON_WIDTH, panelTopHeight, 0xFF888888);

        context.fill(panelBottomX, panelBottomY, panelBottomX + panelBottomWidth, panelBottomY + panelBottomHeight, 0xAA333333);
        context.drawBorder(panelBottomX, panelBottomY, panelBottomWidth, panelBottomHeight, 0xFF888888);

        if (!preciseInputMode) {
            if (selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                CustomSignBlockEntity.TextLineData data = textLineWidgets.get(selectedIndex).data;
                int lineHeight = (panelBottomHeight - 10) / 2;
                int yRow2 = panelBottomY + 5 + lineHeight;

                int curX = panelBottomX + 5 + (BTN_SIZE + BTN_GAP) * 4;

                drawToggleBg(context, curX, yRow2, BTN_SIZE, data.isBold());
                curX += BTN_SIZE + BTN_GAP;

                drawToggleBg(context, curX, yRow2, BTN_SIZE, data.isItalic());
                curX += BTN_SIZE + BTN_GAP;

                drawToggleBg(context, curX, yRow2, BTN_SIZE, data.isUnderline());
                curX += BTN_SIZE + BTN_GAP;

                drawToggleBg(context, curX, yRow2, BTN_SIZE, data.isShadow());

                String infoText = String.format("X:%.1f Y:%.1f S:%.2f C:#%06X", data.getXOffset(), data.getYOffset(), data.getFontSize(), data.getColor());
                context.drawText(
                        this.textRenderer,
                        Text.literal(infoText),
                        panelBottomX + panelBottomWidth - this.textRenderer.getWidth(infoText) - 5,
                        yRow2 + 2,
                        0xFFAAAAAA,
                        false
                );
            }

            if (textLineWidgets.isEmpty()) {
                String hint = "点击 + 添加文本";
                int textWidth = this.textRenderer.getWidth(hint);
                context.drawText(
                        this.textRenderer,
                        Text.literal(hint),
                        panelTopX + (panelTopWidth - textWidth) / 2,
                        panelTopY + (panelTopHeight - this.textRenderer.fontHeight) / 2,
                        0xFFAAAAAA,
                        false
                );
            }

            if (selectedIndex < 0 && !textLineWidgets.isEmpty()) {
                String hint = "选择一个文本";
                int textWidth = this.textRenderer.getWidth(hint);
                context.drawText(
                        this.textRenderer,
                        Text.literal(hint),
                        panelBottomX + (panelBottomWidth - textWidth) / 2,
                        panelBottomY + (panelBottomHeight - this.textRenderer.fontHeight) / 2,
                        0xFFAAAAAA,
                        false
                );
            }
        } else {
            String label = switch (preciseInputType) {
                case 0 -> "输入 X 坐标";
                case 1 -> "输入 Y 坐标";
                case 2 -> "输入字号";
                case 3 -> "输入颜色 (#RRGGBB)";
                default -> "";
            };
            context.drawText(
                    this.textRenderer,
                    Text.literal(label),
                    panelBottomX + 5,
                    panelBottomY + 5,
                    0xFFAAAAAA,
                    false
            );
        }

        super.render(context, mouseX, mouseY, delta);

        if (!preciseInputMode) {
            List<TooltipEntry> tooltips = new ArrayList<>();
            if (xButton != null && xButton.isMouseOver(mouseX, mouseY)) {
                tooltips.add(new TooltipEntry("X 坐标", "左键 +1 | 右键 -1", "Alt + 左键 +0.5 | Alt + 右键 -0.5", "Ctrl + 点击精准输入"));
            }
            if (yButton != null && yButton.isMouseOver(mouseX, mouseY)) {
                tooltips.add(new TooltipEntry("Y 坐标", "左键 +1 | 右键 -1", "Alt + 左键 +0.5 | Alt + 右键 -0.5", "Ctrl + 点击精准输入"));
            }
            if (fontSizeButton != null && fontSizeButton.isMouseOver(mouseX, mouseY)) {
                tooltips.add(new TooltipEntry("字号", "左键 +1/16 | 右键 -1/16", "Alt + 左键 +1/32 | Alt + 右键 -1/32", "Ctrl + 点击精准输入"));
            }
            if (colorButton != null && colorButton.isMouseOver(mouseX, mouseY)) {
                tooltips.add(new TooltipEntry("颜色", "点击切换颜色", "Ctrl + 点击精准输入"));
            }
            if (boldButton != null && boldButton.isMouseOver(mouseX, mouseY) && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                boolean active = textLineWidgets.get(selectedIndex).data.isBold();
                tooltips.add(new TooltipEntry("加粗", "点击切换", active ? "当前：开启" : "当前：关闭"));
            }
            if (italicButton != null && italicButton.isMouseOver(mouseX, mouseY) && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                boolean active = textLineWidgets.get(selectedIndex).data.isItalic();
                tooltips.add(new TooltipEntry("斜体", "点击切换", active ? "当前：开启" : "当前：关闭"));
            }
            if (underlineButton != null && underlineButton.isMouseOver(mouseX, mouseY) && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                boolean active = textLineWidgets.get(selectedIndex).data.isUnderline();
                tooltips.add(new TooltipEntry("下划线", "点击切换", active ? "当前：开启" : "当前：关闭"));
            }
            if (shadowButton != null && shadowButton.isMouseOver(mouseX, mouseY) && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                boolean active = textLineWidgets.get(selectedIndex).data.isShadow();
                tooltips.add(new TooltipEntry("阴影", "点击切换", active ? "当前：开启" : "当前：关闭"));
            }
            if (alignButton != null && alignButton.isMouseOver(mouseX, mouseY) && selectedIndex >= 0 && selectedIndex < textLineWidgets.size()) {
                String currentAlign = getAlignText(textLineWidgets.get(selectedIndex).data.getAlignment());
                tooltips.add(new TooltipEntry("对齐方式", "点击切换", "当前：" + currentAlign));
            }
            if (clearFormatButton != null && clearFormatButton.isMouseOver(mouseX, mouseY)) {
                tooltips.add(new TooltipEntry("清除格式", "保留文本和对齐方式"));
            }
            if (addLineButton != null && addLineButton.isMouseOver(mouseX, mouseY)) {
                tooltips.add(new TooltipEntry("添加文本行", "最多 " + MAX_TEXT_LINES + " 行"));
            }
            for (ButtonWidget btn : textButtons) {
                if (btn.isMouseOver(mouseX, mouseY)) {
                    tooltips.add(new TooltipEntry("文本标签", "左键选择", "右键删除"));
                    break;
                }
            }

            if (!tooltips.isEmpty()) {
                drawTooltip(context, mouseX, mouseY, tooltips);
            }
        }
    }

    private void drawToggleBg(DrawContext context, int x, int y, int size, boolean active) {
        if (active) {
            context.fill(x, y, x + size, y + size, 0xFF6B6BAA);
            context.drawBorder(x, y, size, size, 0xFFAAAAFF);
        }
    }

    private void drawTooltip(DrawContext context, int mouseX, int mouseY, List<TooltipEntry> entries) {
        int lineHeight = this.textRenderer.fontHeight + 2;
        int maxWidth = 0;
        int totalHeight = 4;

        List<String> lines = new ArrayList<>();
        for (TooltipEntry entry : entries) {
            if (!entry.title.isEmpty()) {
                lines.add(entry.title);
                maxWidth = Math.max(maxWidth, this.textRenderer.getWidth(entry.title));
            }
            for (String desc : entry.descriptions) {
                lines.add("  " + desc);
                maxWidth = Math.max(maxWidth, this.textRenderer.getWidth("  " + desc));
            }
        }
        totalHeight += lines.size() * lineHeight;

        int tooltipX = Math.min(mouseX + 12, this.width - maxWidth - 10);
        int tooltipY = Math.min(mouseY - totalHeight - 4, this.height - totalHeight - 4);
        if (tooltipY < 4) tooltipY = mouseY + 12;

        context.fill(tooltipX, tooltipY, tooltipX + maxWidth + 8, tooltipY + totalHeight, 0xCC1E1E2E);
        context.drawBorder(tooltipX, tooltipY, maxWidth + 8, totalHeight, 0xFF6B6B8A);

        int textY = tooltipY + 2;
        for (String line : lines) {
            int color = line.startsWith("  ") ? 0xFFAAAAAA : 0xFFFFFFFF;
            context.drawText(this.textRenderer, Text.literal(line), tooltipX + 4, textY, color, false);
            textY += lineHeight;
        }
    }

    public void sendUpdateToServer() {
        List<CustomSignBlockEntity.TextLineData> dataList = new ArrayList<>();
        for (TextLineWidget widget : textLineWidgets) {
            dataList.add(widget.data);
        }

        CustomSignUpdatePacket packet = new CustomSignUpdatePacket(blockPos, dataList);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);
        NetworkManager.sendToServer(UPDATE_CUSTOM_SIGN, buf);
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
        private final CustomSignBlockEntity.TextLineData data;

        public TextLineWidget(CustomSignBlockEntity.TextLineData data) {
            this.data = data;
        }
    }

    private record TooltipEntry(String title, String... descriptions) {}
}