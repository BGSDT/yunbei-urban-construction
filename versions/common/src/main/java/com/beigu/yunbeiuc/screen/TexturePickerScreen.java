package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.ApiCompat;
import com.beigu.yunbeiuc.api.input.Keys;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.resource.Id;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// 「图片」选择界面：先选命名空间（原版 / 本模组 / 其它模组与资源包），
public class TexturePickerScreen extends Screen {

    private static final String PNG = ".png";

    private static final int PAD = 10;
    private static final int HEADER_H = 18;
    private static final int BREADCRUMB_H = 16;
    private static final int ROW_H = 16;
    private static final int SCROLL_W = 8;
    private static final int LIST_W = 240;
    private static final int PREVIEW = 96;
    private static final int FOOTER_H = 22;

    private final TextDisplayScreen parent;

    private final Map<String, List<String>> namespaceTextures = new LinkedHashMap<>();
    private final List<String> namespaces = new ArrayList<>();

    private boolean namespaceChosen = false;
    private String currentNamespace = "minecraft";
    private String currentDir = "textures/";

    private String selectedPath = "";
    private int scroll = 0;
    private int hoverRow = -1;

    private final List<String> entries = new ArrayList<>();
    private final Map<String, Boolean> entryIsDir = new HashMap<>();
    private final Map<String, Integer> entrySubCount = new HashMap<>();

    private int panelX, panelY, panelW, panelH;
    private int nsTileW, nsTileH, nsCols, nsRowsPerPage;
    private int nsPage = 0;

    public TexturePickerScreen(TextDisplayScreen parent) {
        super(Text.literal("选择图片"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        if (this.client == null) return;
        scanAssets();
        panelW = Math.min(this.width - 20, 620);
        panelH = Math.min(this.height - 20, 320);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
        if (!namespaceChosen) {
            selectedPath = namespaces.contains("yunbeiuc") ? "yunbeiuc" : (namespaces.isEmpty() ? "" : namespaces.get(0));
        }
        rebuildEntries();
    }

    private void scanAssets() {
        namespaceTextures.clear();
        namespaces.clear();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        try {
            namespaces.addAll(VersionServices.resources().listNamespaces(manager));
        } catch (Throwable ignored) {
        }
        for (String ns : new ArrayList<>(namespaces)) {
            namespaceTextures.put(ns, new ArrayList<>());
        }
        try {
            List<Id> all = VersionServices.resources().listResources(manager, "textures/",
                    id -> id.path().endsWith(PNG));
            for (Id id : all) {
                if (!id.path().endsWith(PNG)) continue;
                namespaceTextures.computeIfAbsent(id.namespace(), k -> {
                    if (!namespaces.contains(id.namespace())) namespaces.add(id.namespace());
                    return new ArrayList<>();
                }).add(id.path());
            }
        } catch (Throwable ignored) {
        }
        for (String ns : namespaceTextures.keySet()) {
            Collections.sort(namespaceTextures.get(ns));
        }
        namespaces.removeIf(ns -> ns == null || ns.isEmpty() || ns.equals("realms"));
        namespaces.sort((a, b) -> {
            int c = Integer.compare(preferenceRank(a), preferenceRank(b));
            if (c != 0) return c;
            int byCount = Integer.compare(texturesOf(b).size(), texturesOf(a).size());
            return byCount != 0 ? byCount : a.compareTo(b);
        });
    }

    private static int preferenceRank(String ns) {
        if (ns.equals("yunbeiuc")) return 0;
        if (ns.equals("minecraft")) return 1;
        return 2;
    }

    private List<String> texturesOf(String ns) {
        List<String> list = namespaceTextures.get(ns);
        return list == null ? Collections.emptyList() : list;
    }

    private void rebuildEntries() {
        entries.clear();
        entryIsDir.clear();
        entrySubCount.clear();
        if (!namespaceChosen) {
            scroll = 0;
            return;
        }
        Map<String, Integer> dirCounts = new TreeMap<>();
        for (String p : texturesOf(currentNamespace)) {
            if (!p.startsWith(currentDir)) continue;
            String rest = p.substring(currentDir.length());
            int slash = rest.indexOf('/');
            if (slash < 0) continue;
            String dir = rest.substring(0, slash);
            dirCounts.merge(dir, 1, Integer::sum);
        }
        entries.addAll(dirCounts.keySet());
        for (String d : dirCounts.keySet()) {
            entryIsDir.put(d, true);
            entrySubCount.put(d, dirCounts.get(d));
        }
        for (String p : texturesOf(currentNamespace)) {
            if (!p.startsWith(currentDir)) continue;
            String rest = p.substring(currentDir.length());
            if (rest.indexOf('/') >= 0) continue;
            entries.add(rest);
            entryIsDir.put(rest, false);
        }
        clampScroll();
        if (selectedPath.isEmpty() && !entries.isEmpty()) hoverRow = -1;
    }

    private List<String> breadcrumbs() {
        List<String> out = new ArrayList<>();
        out.add(currentNamespace);
        String[] parts = currentDir.split("/");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            sb.append(part).append('/');
            out.add(part);
        }
        return out;
    }

    private void drillInto(String dir) {
        currentDir = currentDir + dir + "/";
        selectedPath = "";
        scroll = 0;
        rebuildEntries();
    }

    private void goUp() {
        if ("textures/".equals(currentDir)) {
            namespaceChosen = false;
            selectedPath = currentNamespace;
        } else {
            String trimmed = currentDir.substring(0, currentDir.length() - 1);
            int slash = trimmed.lastIndexOf('/');
            currentDir = slash >= 0 ? trimmed.substring(0, slash + 1) : "textures/";
        }
        selectedPath = "";
        scroll = 0;
        rebuildEntries();
    }

    private void goToCrumb(int index) {
        if (index == 0) {
            namespaceChosen = false;
            selectedPath = "";
        } else {
            String[] parts = currentDir.split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < index && i < parts.length; i++) {
                if (parts[i].isEmpty()) continue;
                sb.append(parts[i]).append('/');
            }
            currentDir = sb.length() == 0 ? "textures/" : sb.toString();
        }
        scroll = 0;
        rebuildEntries();
    }

    private int listX() { return panelX + PAD; }
    private int listY() { return panelY + PAD + HEADER_H + BREADCRUMB_H + 4; }
    private int listW() { return Math.min(LIST_W, panelW - PAD * 3 - 150); }
    private int listH() { return panelH - (listY() - panelY) - FOOTER_H - PAD; }
    private int previewX() { return listX() + listW() + PAD; }
    private int previewW() { return panelX + panelW - PAD - previewX(); }

    private int visibleRows() { return Math.max(1, listH() / ROW_H); }

    private void clampScroll() {
        int max = Math.max(0, entries.size() - visibleRows());
        scroll = Math.max(0, Math.min(scroll, max));
    }

    private int rowAt(double mouseX, double mouseY) {
        if (mouseX < listX() || mouseX >= listX() + listW()) return -1;
        int idx = (int) ((mouseY - listY()) / ROW_H) + scroll;
        if (mouseY < listY() || mouseY >= listY() + listH()) return -1;
        return idx >= 0 && idx < entries.size() ? idx : -1;
    }

    private String currentFullPath() {
        return currentNamespace + ":" + currentDir + selectedPath;
    }

    private boolean canConfirm() {
        return namespaceChosen && !selectedPath.isEmpty() && Boolean.FALSE.equals(entryIsDir.get(selectedPath));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client == null) return;

        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xAA333333);
        context.drawBorder(panelX, panelY, panelW, panelH, 0xFFCCCCCC);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, panelX + panelW / 2, panelY + 4, 0xFF66FFCC);

        if (!namespaceChosen) {
            renderNamespaces(context, mouseX, mouseY);
        } else {
            renderBrowser(context, mouseX, mouseY);
        }

        int btnW = 60;
        int btnY = panelY + panelH - PAD - FOOTER_H;
        renderFooter(context, mouseX, mouseY, btnW, btnY);
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderFooter(DrawContext context, int mouseX, int mouseY, int btnW, int btnY) {
        boolean canConfirm = canConfirm();
        int confirmX = panelX + panelW - PAD - btnW;
        int cancelX = confirmX - btnW - 6;
        drawFooterButton(context, "确定", confirmX, btnY, btnW, canConfirm, mouseX, mouseY);
        drawFooterButton(context, "取消", cancelX, btnY, btnW, true, mouseX, mouseY);
        String tip = canConfirm ? currentFullPath() : "先选命名空间，再逐级选择图片";
        context.drawText(this.textRenderer, Text.literal(tip), panelX + PAD,
                btnY + 6, canConfirm ? 0xFF88CCFF : 0xFF888888, false);
    }

    private int footerConfirmX() { return panelX + panelW - PAD - 60; }
    private int footerCancelX() { return footerConfirmX() - 66; }
    private int footerBtnY() { return panelY + panelH - PAD - FOOTER_H; }

    private void drawFooterButton(DrawContext context, String label, int x, int y, int w, boolean active,
                                  int mouseX, int mouseY) {
        boolean hover = active && mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + FOOTER_H;
        int fill = !active ? 0xFF2A2A33 : (hover ? 0xFF4A4A70 : 0xFF3A3A50);
        context.fill(x, y, x + w, y + FOOTER_H, fill);
        context.drawBorder(x, y, w, FOOTER_H, active ? 0xFFCCCCCC : 0xFF555566);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(label), x + w / 2, y + (FOOTER_H - 8) / 2,
                active ? 0xFFFFFFFF : 0xFF777788);
    }

    private void renderNamespaces(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, Text.literal("第 1 步：选择命名空间（模组 / 资源包 / 原版）"),
                listX(), panelY + PAD + HEADER_H + 2, 0xFFCCCCCC, false);

        int gridY = panelY + PAD + HEADER_H * 2 + 4;
        int gridW = panelW - PAD * 2;
        nsTileW = 130;
        nsCols = Math.max(1, gridW / (nsTileW + 6));
        nsTileH = 18;
        int gridH = panelH - (gridY - panelY) - FOOTER_H - PAD;
        nsRowsPerPage = Math.max(1, gridH / (nsTileH + 4));        int perPage = Math.max(1, nsCols * nsRowsPerPage);
        int maxPage = Math.max(0, (namespaces.size() - 1) / perPage);
        nsPage = Math.max(0, Math.min(nsPage, maxPage));

        int totalW = nsCols * nsTileW + (nsCols - 1) * 6;
        int startX = panelX + (panelW - totalW) / 2;
        for (int i = 0; i < perPage; i++) {
            int idx = nsPage * perPage + i;
            if (idx >= namespaces.size()) break;
            String ns = namespaces.get(idx);
            int col = i % nsCols, row = i / nsCols;
            int x = startX + col * (nsTileW + 6);
            int y = gridY + row * (nsTileH + 4);
            boolean hover = mouseX >= x && mouseX < x + nsTileW && mouseY >= y && mouseY < y + nsTileH;
            context.fill(x, y, x + nsTileW, y + nsTileH, hover ? 0xFF4A4A70 : 0xFF2E2E3E);
            context.drawBorder(x, y, nsTileW, nsTileH, hover ? 0xFF66FFCC : 0xFF66667A);
            int count = texturesOf(ns).size();
            context.drawText(this.textRenderer, Text.literal(ns), x + 6, y + 5, 0xFFEEEEEE, false);
            String cnt = count + "";
            context.drawText(this.textRenderer, Text.literal(cnt),
                    x + nsTileW - 6 - this.textRenderer.width(Text.literal(cnt)), y + 5,
                    count > 0 ? 0xFF88FF88 : 0xFF888888, false);
        }
        if (maxPage > 0) {
            String page = (nsPage + 1) + "/" + (maxPage + 1) + "  （滚轮翻页）";
            context.drawText(this.textRenderer, Text.literal(page),
                    panelX + panelW - PAD - this.textRenderer.width(Text.literal(page)),
                    panelY + PAD + HEADER_H + 2, 0xFF888888, false);
        }
    }

    private void renderBrowser(DrawContext context, int mouseX, int mouseY) {
        int cx = listX();
        int cy = panelY + PAD + HEADER_H + 2;
        List<String> crumbs = breadcrumbs();
        String full = String.join(" / ", crumbs);
        context.drawText(this.textRenderer, Text.literal(full), cx, cy, 0xFF66FFCC, false);
        int upX = panelX + panelW - PAD - 56;
        drawFooterButton(context, "◀ 上一级", upX, cy - 3, 56, true, mouseX, mouseY);

        int lx = listX(), ly = listY(), lw = listW(), lh = listH();
        context.fill(lx, ly, lx + lw, ly + lh, 0x55222233);
        context.drawBorder(lx, ly, lw, lh, 0xFF66667A);
        hoverRow = rowAt(mouseX, mouseY);
        context.enableScissor(lx, ly, lx + lw, ly + lh);
        int rows = visibleRows();
        for (int i = 0; i < rows; i++) {
            int idx = scroll + i;
            if (idx >= entries.size()) break;
            String name = entries.get(idx);
            boolean isDir = Boolean.TRUE.equals(entryIsDir.get(name));
            boolean selected = !isDir && name.equals(selectedPath);
            boolean hover = idx == hoverRow;
            int y = ly + i * ROW_H;
            if (selected || hover) {
                context.fill(lx + 1, y, lx + lw - 1, y + ROW_H, selected ? 0xFF3A5A6A : 0x55336688);
            }
            String label = (isDir ? "[文件夹] " : "") + name;
            int color = isDir ? 0xFFFFD966 : 0xFFDDDDDD;
            if (selected) color = 0xFF66FFCC;
            context.drawText(this.textRenderer, Text.literal(label), lx + 5, y + 4, color, false);
            String right = isDir ? ("文件夹 · " + entrySubCount.getOrDefault(name, 0)) : "";
            if (!right.isEmpty()) {
                context.drawText(this.textRenderer, Text.literal(right),
                        lx + lw - 6 - this.textRenderer.width(Text.literal(right)), y + 4, 0xFF999999, false);
            }
        }
        if (entries.isEmpty()) {
            String empty = "该目录下没有图片";
            context.drawText(this.textRenderer, Text.literal(empty),
                    lx + (lw - this.textRenderer.width(Text.literal(empty))) / 2, ly + lh / 2 - 4, 0xFF888888, false);
        }
        context.disableScissor();

        int max = Math.max(0, entries.size() - rows);
        if (max > 0) {
            int trackH = lh - 4;
            int thumbH = Math.max(10, trackH * rows / Math.max(1, entries.size()));
            int thumbY = ly + 2 + (trackH - thumbH) * scroll / max;
            context.fill(lx + lw - SCROLL_W - 2, ly + 2, lx + lw - SCROLL_W, ly + lh - 2, 0x55000000);
            context.fill(lx + lw - SCROLL_W - 2, thumbY, lx + lw - SCROLL_W, thumbY + thumbH, 0xFFCCCCCC);
        }

        int px = previewX(), py = listY(), pw = previewW();
        context.fill(px, py, px + pw, py + listH(), 0x55222233);
        context.drawBorder(px, py, pw, listH(), 0xFF66667A);
        context.drawText(this.textRenderer, Text.literal("预览"), px + 6, py + 5, 0xFF66FFCC, false);
        if (!selectedPath.isEmpty() && !Boolean.TRUE.equals(entryIsDir.get(selectedPath))) {
            Id id = VersionServices.resources().create(currentNamespace, currentDir + selectedPath);
            int boxW = Math.min(PREVIEW, pw - 20);
            int boxH = Math.min(PREVIEW, listH() - 60);
            TextureAspectCache.Size size = TextureAspectCache.get(id);
            if (size != null) {
                TextureAspectCache.drawFitted(context, id, px + (pw - boxW) / 2, py + 20, boxW, boxH);
                String dim = size.width + " × " + size.height;
                context.drawText(this.textRenderer, Text.literal(dim),
                        px + (pw - this.textRenderer.width(Text.literal(dim))) / 2,
                        py + 20 + boxH + 4, 0xFFAAAAAA, false);
            } else {
                context.drawText(this.textRenderer, Text.literal("（图片加载失败）"), px + 6, py + 26, 0xFFFF8888, false);
            }
            String file = selectedPath;
            if (this.textRenderer.width(Text.literal(file)) > pw - 12) {
                while (file.length() > 3 && this.textRenderer.width(Text.literal(file)) > pw - 18) {
                    file = file.substring(1);
                }
                file = "…" + file;
            }
            context.drawText(this.textRenderer, Text.literal(file), px + 6, py + listH() - 12, 0xFFCCCCCC, false);
        } else {
            context.drawText(this.textRenderer, Text.literal("点选右侧列表里的图片"), px + 6, py + 26, 0xFF888888, false);
            context.drawText(this.textRenderer, Text.literal("文件夹请先点进去"), px + 6, py + 38, 0xFF888888, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int by = footerBtnY();
            if (mouseX >= footerCancelX() && mouseX < footerCancelX() + 60 && mouseY >= by && mouseY < by + FOOTER_H) {
                ApiCompat.setScreen(Minecraft.getInstance(), parent);
                return true;
            }
            boolean canConfirm = canConfirm();
            if (mouseX >= footerConfirmX() && mouseX < footerConfirmX() + 60 && mouseY >= by && mouseY < by + FOOTER_H) {
                if (canConfirm) confirmSelection();
                return true;
            }
        }

        if (!namespaceChosen) {
            int perPage = Math.max(1, nsCols * nsRowsPerPage);
            int gridY = panelY + PAD + HEADER_H * 2 + 4;
            int totalW = nsCols * nsTileW + (nsCols - 1) * 6;
            int startX = panelX + (panelW - totalW) / 2;
            for (int i = 0; i < perPage; i++) {
                int idx = nsPage * perPage + i;
                if (idx >= namespaces.size()) break;
                int col = i % nsCols, row = i / nsCols;
                int x = startX + col * (nsTileW + 6);
                int y = gridY + row * (nsTileH + 4);
                if (mouseX >= x && mouseX < x + nsTileW && mouseY >= y && mouseY < y + nsTileH) {
                    currentNamespace = namespaces.get(idx);
                    currentDir = "textures/";
                    namespaceChosen = true;
                    selectedPath = "";
                    scroll = 0;
                    rebuildEntries();
                    return true;
                }
            }
            return true;
        }

        int cy = panelY + PAD + HEADER_H + 2;
        int upX = panelX + panelW - PAD - 56;
        if (mouseX >= upX && mouseX < upX + 56 && mouseY >= cy - 3 && mouseY < cy - 3 + FOOTER_H) {
            goUp();
            return true;
        }

        int row = rowAt(mouseX, mouseY);
        if (row >= 0) {
            String name = entries.get(row);
            if (Boolean.TRUE.equals(entryIsDir.get(name))) {
                drillInto(name);
            } else {
                selectedPath = name;
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void confirmSelection() {
        String fullId = currentNamespace + ":" + currentDir + selectedPath;
        pendingTexture = fullId;
        ApiCompat.setScreen(Minecraft.getInstance(), parent);
    }

    public static String pendingTexture = null;

    @Override
    public boolean mouseScrolledCompat(double mouseX, double mouseY, double amount) {
        if (!namespaceChosen) {
            int perPage = Math.max(1, nsCols * nsRowsPerPage);
            int maxPage = Math.max(0, (namespaces.size() - 1) / perPage);
            nsPage = Math.max(0, Math.min(maxPage, nsPage + (amount < 0 ? 1 : -1)));
            return true;
        }
        scroll += amount < 0 ? 1 : -1;
        clampScroll();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isTextInputFocusedCompat()) return super.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == Keys.ESCAPE) {
            ApiCompat.setScreen(Minecraft.getInstance(), parent);
            return true;
        }
        if (keyCode == 259) {
            goUp();
            return true;
        }
        if (keyCode == Keys.ENTER) {
            if (canConfirm()) confirmSelection();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPauseCompat() {
        return false;
    }
}
