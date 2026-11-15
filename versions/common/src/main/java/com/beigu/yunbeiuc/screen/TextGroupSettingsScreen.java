package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.ApiCompat;

import com.beigu.yunbeiuc.api.input.Keys;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;

import java.util.List;

// 「文本列组」设置页面：设置组名、每行停留时间（切换时间），并可以增删组内文本行。
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

    private TextFieldWidget nameField;
    private TextFieldWidget intervalField;

    private int panelX, panelY, panelWidth, panelHeight;
    private int contentX, contentW;
    private int nameRowY, intervalRowY, previewY, actionRowY, confirmRowY;

    public TextGroupSettingsScreen(TextDisplayScreen parent, int groupId) {
        super(Text.literal("文本列组设置"));
        this.parent = parent;
        this.groupId = groupId;
    }

    @Override
    protected void init() {
        super.init();

        panelWidth = Math.max(PANEL_MIN_WIDTH, Math.min(PANEL_MAX_WIDTH, this.width - 40));
        contentW = panelWidth - PADDING * 2;
        int previewRows = Math.min(MAX_PREVIEW_ROWS, Math.max(1, lineTexts().size()));
        panelHeight = PADDING + TITLE_H + 10
                + ROW_H + ROW_GAP
                + ROW_H + ROW_GAP
                + 12 + previewRows * 12 + 8
                + ROW_H + ROW_GAP
                + ROW_H + PADDING;
        panelX = Math.max(6, (this.width - panelWidth) / 2);
        panelY = Math.max(6, (this.height - panelHeight) / 2);
        contentX = panelX + PADDING;

        int y = panelY + PADDING + TITLE_H + 10;
        nameRowY = y; y += ROW_H + ROW_GAP;
        intervalRowY = y; y += ROW_H + ROW_GAP;
        previewY = y + 12; y = previewY + previewRows * 12 + 8;
        actionRowY = y; y += ROW_H + ROW_GAP;
        confirmRowY = y;

        int fieldX = contentX + LABEL_W;
        int fieldW = contentW - LABEL_W;

        nameField = new TextFieldWidget(this.textRenderer, fieldX, nameRowY, fieldW, 18, Text.literal("组名"));
        nameField.setMaxLength(64);
        nameField.setText(parent.getGroupName(groupId));
        this.addDrawableChild(nameField);

        intervalField = new TextFieldWidget(this.textRenderer, fieldX, intervalRowY, Math.min(80, fieldW), 18, Text.literal("切换时间"));
        intervalField.setMaxLength(12);
        intervalField.setText(formatSeconds(parent.getGroupIntervalMs(groupId)));
        this.addDrawableChild(intervalField);

        int halfW = (contentW - ROW_GAP) / 2;

        ButtonWidget addLine = ButtonWidget.builderCompat(Text.literal("添加文本行"), b -> {
            applyFields();
            parent.addLineToGroup(groupId);
            reopen();
        }).dimensions(contentX, actionRowY, halfW, ROW_H).build();
        this.addDrawableChild(addLine);

        ButtonWidget deleteGroup = ButtonWidget.builderCompat(Text.literal("删除整组"), b -> confirmDelete())
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
                Text.literal("删除文本列组"),
                Text.literal("将删除该组内的所有文本行，删除后不可恢复。"),
                Text.literal("确定删除"),
                Text.literal("取消")));
    }

    private static String formatSeconds(int ms) {
        if (ms % 1000 == 0) return Integer.toString(ms / 1000);
        return String.format(java.util.Locale.ROOT, "%.1f", ms / 1000.0);
    }

    private int parseIntervalMs() {
        try {
            float seconds = Float.parseFloat(intervalField.getText().trim());
            return TextLineData.clampGroupInterval(Math.round(seconds * 1000f));
        } catch (NumberFormatException e) {
            return parent.getGroupIntervalMs(groupId);
        }
    }

    private void applyFields() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) name = parent.getGroupName(groupId);
        parent.applyGroupSettings(groupId, name, parseIntervalMs());
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
        context.drawText(this.textRenderer, Text.literal("组名"), contentX, labelY, 0xFFCCCCCC, false);
        labelY = intervalRowY + 5;
        context.drawText(this.textRenderer, Text.literal("切换时间"), contentX, labelY, 0xFFCCCCCC, false);
        context.drawText(this.textRenderer, Text.literal("秒（每行停留时间）"),
                contentX + LABEL_W + Math.min(80, contentW - LABEL_W) + 6, labelY + 1, 0xFF999999, false);

        int total = lineTexts().size();
        context.drawText(this.textRenderer, Text.literal("组内文本行（" + total + " 行，按顺序循环切换）"),
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
