package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.ApiCompat;

import com.beigu.yunbeiuc.api.input.Keys;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;

import java.util.List;

// 「轮播容器 / 滚动容器」设置页面。
// 轮播容器：设置名称、每行停留时间（切换时间），并可增删容器内文本行。
// 滚动容器：设置名称、容器宽度、滚动速度、滚动方向，容器内文本行按顺序拼接后循环滚动。
public class TextGroupSettingsScreen extends Screen {

    private static final int PANEL_MAX_WIDTH = 360;
    private static final int PANEL_MIN_WIDTH = 300;
    private static final int PADDING = 14;
    private static final int TITLE_H = 20;
    private static final int ROW_H = 20;
    private static final int ROW_GAP = 8;
    private static final int LABEL_W = 78;
    private static final int MAX_PREVIEW_ROWS = 5;

    private final TextDisplayScreen parent;
    private final int groupId;
    private final boolean scrollMode;

    private TextFieldWidget nameField;
    private TextFieldWidget intervalField;
    private TextFieldWidget widthField;
    private TextFieldWidget speedField;
    private ButtonWidget directionButton;
    private int direction;

    private int panelX, panelY, panelWidth, panelHeight;
    private int contentX, contentW;
    private int nameRowY, intervalRowY, widthRowY, speedRowY, directionRowY, hintY, previewY, actionRowY, confirmRowY;

    public TextGroupSettingsScreen(TextDisplayScreen parent, int groupId) {
        super(Text.literal(parent.getGroupContainerMode(groupId) == TextLineData.CONTAINER_SCROLL
                ? "滚动容器设置" : "轮播容器设置"));
        this.parent = parent;
        this.groupId = groupId;
        this.scrollMode = parent.getGroupContainerMode(groupId) == TextLineData.CONTAINER_SCROLL;
        this.direction = parent.getGroupScrollDirection(groupId);
    }

    @Override
    protected void init() {
        super.init();

        int fieldRows = scrollMode ? 4 : 2;
        int previewRows = Math.min(MAX_PREVIEW_ROWS, Math.max(1, lineTexts().size()));
        panelHeight = PADDING + TITLE_H + 10
                + fieldRows * (ROW_H + ROW_GAP)
                + (scrollMode ? 12 : 0)
                + 12 + previewRows * 12 + 8
                + ROW_H + ROW_GAP
                + ROW_H + PADDING;
        panelWidth = Math.max(PANEL_MIN_WIDTH, Math.min(PANEL_MAX_WIDTH, this.width - 40));
        contentW = panelWidth - PADDING * 2;
        panelX = Math.max(6, (this.width - panelWidth) / 2);
        panelY = Math.max(6, (this.height - panelHeight) / 2);
        contentX = panelX + PADDING;

        int y = panelY + PADDING + TITLE_H + 10;
        nameRowY = y; y += ROW_H + ROW_GAP;
        if (scrollMode) {
            widthRowY = y; y += ROW_H + ROW_GAP;
            speedRowY = y; y += ROW_H + ROW_GAP;
            directionRowY = y; y += ROW_H + ROW_GAP;
            hintY = y; y += 12;
        } else {
            intervalRowY = y; y += ROW_H + ROW_GAP;
        }
        previewY = y + 12; y = previewY + previewRows * 12 + 8;
        actionRowY = y; y += ROW_H + ROW_GAP;
        confirmRowY = y;

        int fieldX = contentX + LABEL_W;
        int fieldW = contentW - LABEL_W;

        nameField = new TextFieldWidget(this.textRenderer, fieldX, nameRowY, fieldW, 18, Text.literal("容器名称"));
        nameField.setMaxLength(64);
        nameField.setText(parent.getGroupName(groupId));
        this.addDrawableChild(nameField);

        if (scrollMode) {
            widthField = new TextFieldWidget(this.textRenderer, fieldX, widthRowY, Math.min(80, fieldW), 18, Text.literal("容器宽度"));
            widthField.setMaxLength(12);
            widthField.setText(formatNumber(parent.getGroupContainerWidth(groupId)));
            this.addDrawableChild(widthField);

            speedField = new TextFieldWidget(this.textRenderer, fieldX, speedRowY, Math.min(80, fieldW), 18, Text.literal("滚动速度"));
            speedField.setMaxLength(12);
            speedField.setText(formatNumber(parent.getGroupScrollSpeed(groupId)));
            this.addDrawableChild(speedField);

            directionButton = ButtonWidget.builderCompat(Text.literal(directionLabel()), b -> {
                direction = (direction + 1) % 4;
                b.setMessage(Text.literal(directionLabel()));
            }).dimensions(fieldX, directionRowY, Math.min(110, fieldW), ROW_H).build();
            this.addDrawableChild(directionButton);
        } else {
            intervalField = new TextFieldWidget(this.textRenderer, fieldX, intervalRowY, Math.min(80, fieldW), 18, Text.literal("切换时间"));
            intervalField.setMaxLength(12);
            intervalField.setText(formatSeconds(parent.getGroupIntervalMs(groupId)));
            this.addDrawableChild(intervalField);
        }

        int halfW = (contentW - ROW_GAP) / 2;

        ButtonWidget addLine = ButtonWidget.builderCompat(Text.literal("添加文本行"), b -> {
            applyFields();
            parent.addLineToGroup(groupId);
            reopen();
        }).dimensions(contentX, actionRowY, halfW, ROW_H).build();
        this.addDrawableChild(addLine);

        ButtonWidget deleteGroup = ButtonWidget.builderCompat(Text.literal("删除整个容器"), b -> confirmDelete())
                .dimensions(contentX + halfW + ROW_GAP, actionRowY, halfW, ROW_H).build();
        this.addDrawableChild(deleteGroup);

        ButtonWidget confirm = ButtonWidget.builderCompat(Text.literal("确定"), b -> {
            applyFields();
            ApiCompat.setScreen(Minecraft.getInstance(), parent);
        }).dimensions(contentX, confirmRowY, halfW, ROW_H).build();
        this.addDrawableChild(confirm);

        ButtonWidget cancel = ButtonWidget.builderCompat(Text.literal("取消"), b ->
                ApiCompat.setScreen(Minecraft.getInstance(), parent))
                .dimensions(contentX + halfW + ROW_GAP, confirmRowY, halfW, ROW_H).build();
        this.addDrawableChild(cancel);
    }

    private List<String> lineTexts() {
        return parent.getGroupLineTexts(groupId);
    }

    private void reopen() {
        ApiCompat.setScreen(Minecraft.getInstance(), new TextGroupSettingsScreen(parent, groupId));
    }

    private void confirmDelete() {
        Minecraft mc = Minecraft.getInstance();
        ApiCompat.setScreen(mc, new ConfirmScreen(confirmed -> {
            if (confirmed) {
                parent.deleteTextGroup(groupId);
                ApiCompat.setScreen(mc, parent);
            } else {
                ApiCompat.setScreen(mc, this);
            }
        },
                Text.literal(scrollMode ? "删除滚动容器" : "删除轮播容器"),
                Text.literal("将删除该容器内的所有文本行，删除后不可恢复。"),
                Text.literal("确定删除"),
                Text.literal("取消")));
    }

    private static String formatSeconds(int ms) {
        if (ms % 1000 == 0) return Integer.toString(ms / 1000);
        return String.format(java.util.Locale.ROOT, "%.1f", ms / 1000.0);
    }

    private static String formatNumber(float value) {
        if (Math.abs(value - Math.round(value)) < 1e-4f) return Integer.toString(Math.round(value));
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    private String directionLabel() {
        return "方向：" + TextDisplayScreen.scrollDirectionLabel(direction);
    }

    private int parseIntervalMs() {
        try {
            float seconds = Float.parseFloat(intervalField.getText().trim());
            return TextLineData.clampGroupInterval(Math.round(seconds * 1000f));
        } catch (NumberFormatException e) {
            return parent.getGroupIntervalMs(groupId);
        }
    }

    private float parseWidth() {
        try {
            return TextLineData.clampContainerWidth(Float.parseFloat(widthField.getText().trim()));
        } catch (NumberFormatException e) {
            return parent.getGroupContainerWidth(groupId);
        }
    }

    private float parseSpeed() {
        try {
            return TextLineData.clampScrollSpeed(Float.parseFloat(speedField.getText().trim()));
        } catch (NumberFormatException e) {
            return parent.getGroupScrollSpeed(groupId);
        }
    }

    private void applyFields() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) name = parent.getGroupName(groupId);
        if (scrollMode) {
            parent.applyGroupSettings(groupId, name, parent.getGroupIntervalMs(groupId));
            parent.applyScrollContainerSettings(groupId, parseWidth(), parseSpeed(), direction);
        } else {
            parent.applyGroupSettings(groupId, name, parseIntervalMs());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client == null) return;

        context.fill(0, 0, this.width, this.height, 0x80000000);
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA333333);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFCCCCCC);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, panelX + panelWidth / 2,
                panelY + PADDING, 0xFF66FFCC);

        int labelY = nameRowY + 5;
        context.drawText(this.textRenderer, Text.literal("容器名称"), contentX, labelY, 0xFFCCCCCC, false);
        if (scrollMode) {
            labelY = widthRowY + 5;
            context.drawText(this.textRenderer, Text.literal("容器宽度"), contentX, labelY, 0xFFCCCCCC, false);
            context.drawText(this.textRenderer, Text.literal("沿滚动方向长度（1/16 格）"),
                    contentX + LABEL_W + Math.min(80, contentW - LABEL_W) + 6, labelY + 1, 0xFF999999, false);
            labelY = speedRowY + 5;
            context.drawText(this.textRenderer, Text.literal("滚动速度"), contentX, labelY, 0xFFCCCCCC, false);
            context.drawText(this.textRenderer, Text.literal("格/秒"),
                    contentX + LABEL_W + Math.min(80, contentW - LABEL_W) + 6, labelY + 1, 0xFF999999, false);
            labelY = directionRowY + 5;
            context.drawText(this.textRenderer, Text.literal("滚动方向"), contentX, labelY, 0xFFCCCCCC, false);
            context.drawText(this.textRenderer, Text.literal("沿滚动方向从第一行开始排布；其余方向的坐标每一行独立"),
                    contentX, hintY, 0xFF999999, false);
        } else {
            labelY = intervalRowY + 5;
            context.drawText(this.textRenderer, Text.literal("切换时间"), contentX, labelY, 0xFFCCCCCC, false);
            context.drawText(this.textRenderer, Text.literal("秒（每行停留时间）"),
                    contentX + LABEL_W + Math.min(80, contentW - LABEL_W) + 6, labelY + 1, 0xFF999999, false);
        }

        int total = lineTexts().size();
        context.drawText(this.textRenderer, Text.literal(scrollMode
                        ? "容器内容（" + total + " 行，按顺序拼接滚动）"
                        : "容器内文本行（" + total + " 行，按顺序循环切换）"),
                contentX, previewY - 11, 0xFF66FFCC, false);
        List<String> texts = lineTexts();
        int shown = Math.min(MAX_PREVIEW_ROWS, texts.size());
        for (int i = 0; i < shown; i++) {
            String label = texts.get(i);
            if (label.length() > 34) label = label.substring(0, 33) + "…";
            context.drawText(this.textRenderer, Text.literal((i + 1) + ". " + label),
                    contentX + 4, previewY + i * 12, 0xFFDDDDDD, false);
        }
        if (texts.size() > MAX_PREVIEW_ROWS) {
            context.drawText(this.textRenderer, Text.literal("… 其余 " + (texts.size() - MAX_PREVIEW_ROWS) + " 行"),
                    contentX + 4, previewY + shown * 12, 0xFF999999, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == Keys.ESCAPE) {
            ApiCompat.setScreen(Minecraft.getInstance(), parent);
            return true;
        }
        if (keyCode == Keys.ENTER) {
            applyFields();
            ApiCompat.setScreen(Minecraft.getInstance(), parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPauseCompat() {
        return false;
    }
}
