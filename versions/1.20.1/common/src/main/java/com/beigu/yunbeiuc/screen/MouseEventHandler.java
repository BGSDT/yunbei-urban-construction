package com.beigu.yunbeiuc.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.List;

/**
 * 图案与字体选择器鼠标事件处理器 — 海燕蓝主题
 *
 * @see PatternAndFontOverlay
 * @see GridRenderer
 */
public final class MouseEventHandler {
    private MouseEventHandler() {
    }

    public static boolean mouseClicked(double mx, double my, int button) {
        if (!PatternAndFontOverlay.isVisible || button != 0) return false;

        int winW = LayoutHelper.getScreenWidth();
        int winH = LayoutHelper.getScreenHeight();
        int clipTop = UIConstants.TOP_BAR_HEIGHT + 1;
        int clipBottom = winH - UIConstants.BOTTOM_BAR_HEIGHT;
        int clipH = clipBottom - clipTop;
        int navW = SidebarState.getEffectiveWidth();
        int paneW = winW - navW;

        // 内容区滚动条拖动
        if (mx > navW && PatternAndFontOverlay.maxScrollY > 20) {
            if (handlePaneScrollbarClick(mx, my, winW, clipTop, clipH)) {
                return true;
            }
        }

        // 导航栏滚动条拖动
        if (mx <= navW && PatternAndFontOverlay.maxSidebarScrollY > 20) {
            if (handleNavScrollbarClick(mx, my, winH)) {
                return true;
            }
        }

        // 导航栏折叠按钮（左上角 22x22）
        if (mx <= navW && my <= UIConstants.TOP_BAR_HEIGHT) {
            if (LayoutHelper.isMouseInRect(mx, my, 4, 5, 22, 22)) {
                SidebarState.toggleCollapse();
                return true;
            }
        }

        // 导航栏点击
        if (mx <= navW) {
            return handleNavClick(mx, my);
        }

        // 内容区点击
        if (my >= clipTop && my <= clipBottom) {
            return handlePaneClick(mx, my, winW, winH, paneW, clipTop, clipBottom);
        }

        // 关闭按钮
        int closeBtnX = navW + (paneW - UIConstants.CLOSE_BTN_WIDTH) / 2;
        int closeBtnY = winH - 38;
        if (LayoutHelper.isMouseInRect(mx, my, closeBtnX, closeBtnY, UIConstants.CLOSE_BTN_WIDTH, UIConstants.CLOSE_BTN_HEIGHT)) {
            PatternAndFontOverlay.isVisible = false;
            return true;
        }

        return false;
    }

    // 内容区滚动条点击
    private static boolean handlePaneScrollbarClick(double mx, double my, int winW, int clipTop, int clipH) {
        int navW = SidebarState.getEffectiveWidth();
        int thumbW = UIConstants.THUMB_WIDTH;
        int sbX = winW - thumbW - 3;
        int sbY = clipTop + 2;
        int trackH = clipH - 4;

        float trackRatio = (float) clipH / (float) (clipH + PatternAndFontOverlay.maxScrollY);
        int thumbH = Math.max(UIConstants.THUMB_MIN_SIZE, (int) (trackH * trackRatio));
        float scrollRatio = (float) PatternAndFontOverlay.scrollY / (float) PatternAndFontOverlay.maxScrollY;
        int thumbY = sbY + (int) ((trackH - thumbH) * scrollRatio);

        if (LayoutHelper.isMouseInRect(mx, my, sbX, clipTop, thumbW, clipH)) {
            PatternAndFontOverlay.isDraggingMainScrollbar = true;
            PatternAndFontOverlay.dragStartMouseY = my;
            PatternAndFontOverlay.dragStartScrollY = PatternAndFontOverlay.scrollY;

            if (!LayoutHelper.isMouseInRect(mx, my, sbX, thumbY, thumbW, thumbH)) {
                float newRatio = (float) (my - sbY - thumbH / 2.0f) / (float) (trackH - thumbH);
                PatternAndFontOverlay.scrollY = MathHelper.clamp(newRatio * PatternAndFontOverlay.maxScrollY, 0, PatternAndFontOverlay.maxScrollY);
                PatternAndFontOverlay.dragStartScrollY = PatternAndFontOverlay.scrollY;
            }
            return true;
        }
        return false;
    }

    // 导航栏滚动条点击
    private static boolean handleNavScrollbarClick(double mx, double my, int winH) {
        int navW = SidebarState.getEffectiveWidth();
        int thumbW = UIConstants.THUMB_WIDTH;
        int navClipTop = UIConstants.TOP_BAR_HEIGHT;
        int navClipH = winH - UIConstants.TOP_BAR_HEIGHT - UIConstants.BOTTOM_BAR_HEIGHT;
        int sbX = navW - thumbW - 3;
        int sbY = navClipTop + 2;
        int trackH = navClipH - 4;

        float trackRatio = (float) navClipH / (float) (navClipH + PatternAndFontOverlay.maxSidebarScrollY);
        int thumbH = Math.max(UIConstants.THUMB_MIN_SIZE, (int) (trackH * trackRatio));
        float scrollRatio = (float) PatternAndFontOverlay.sidebarScrollY / (float) PatternAndFontOverlay.maxSidebarScrollY;
        int thumbY = sbY + (int) ((trackH - thumbH) * scrollRatio);

        if (LayoutHelper.isMouseInRect(mx, my, sbX, navClipTop, thumbW, navClipH)) {
            PatternAndFontOverlay.isDraggingSidebarScrollbar = true;
            PatternAndFontOverlay.dragStartMouseY = my;
            PatternAndFontOverlay.dragStartSidebarScrollY = PatternAndFontOverlay.sidebarScrollY;

            if (!LayoutHelper.isMouseInRect(mx, my, sbX, thumbY, thumbW, thumbH)) {
                float newRatio = (float) (my - sbY - thumbH / 2.0f) / (float) (trackH - thumbH);
                PatternAndFontOverlay.sidebarScrollY = MathHelper.clamp(newRatio * PatternAndFontOverlay.maxSidebarScrollY, 0, PatternAndFontOverlay.maxSidebarScrollY);
                PatternAndFontOverlay.dragStartSidebarScrollY = PatternAndFontOverlay.sidebarScrollY;
            }
            return true;
        }
        return false;
    }

    // 导航栏点击（卡片分组式）
    private static boolean handleNavClick(double mx, double my) {
        int navW = SidebarState.getEffectiveWidth();
        int curY = UIConstants.TOP_BAR_HEIGHT + UIConstants.NAV_PAD - (int) PatternAndFontOverlay.sidebarScrollY;

        if (SidebarState.isCollapsed()) {
            int rowH = UIConstants.NAV_ICON_ROW_H;

            // 主页
            if (LayoutHelper.isMouseInRect(mx, my, 0, curY, navW, rowH)) {
                PatternAndFontOverlay.selectSidebarTop(PatternAndFontOverlay.NAV_TOP_HOME);
                return true;
            }
            curY += rowH;

            for (PatternAndFontOverlay.H2Category h2 : PatternAndFontOverlay.REGISTRY) {
                if (LayoutHelper.isMouseInRect(mx, my, 0, curY, navW, rowH)) {
                    PatternAndFontOverlay.clearSidebarTop();
                    h2.isExpanded = true;
                    PatternAndFontOverlay.selectedH2 = h2;
                    if (!h2.subCategories.isEmpty()) {
                        PatternAndFontOverlay.selectedH3 = findFirstLeafH3(h2.subCategories.get(0));
                    }
                    PatternAndFontOverlay.isHomeSelected = false;
                    PatternAndFontOverlay.scrollY = 0;
                    return true;
                }
                curY += rowH;
            }
        } else {
            int cardX = UIConstants.NAV_PAD;
            int cardW = navW - UIConstants.NAV_PAD * 2;

            // 主页卡片
            if (LayoutHelper.isMouseInRect(mx, my, cardX, curY, cardW, UIConstants.CARD_HEADER_H)) {
                PatternAndFontOverlay.selectSidebarTop(PatternAndFontOverlay.NAV_TOP_HOME);
                return true;
            }
            curY += UIConstants.CARD_HEADER_H + UIConstants.CARD_GAP;

            // H2 分组卡片
            TextRenderer tr = MinecraftClient.getInstance().textRenderer;
            for (PatternAndFontOverlay.H2Category h2 : PatternAndFontOverlay.REGISTRY) {
                int headerH = UIConstants.CARD_HEADER_H;

                // 头部点击：折叠/展开
                if (LayoutHelper.isMouseInRect(mx, my, cardX, curY, cardW, headerH)) {
                    PatternAndFontOverlay.clearSidebarTop();
                    h2.isExpanded = !h2.isExpanded;
                    if (h2.isExpanded) PatternAndFontOverlay.selectedH2 = h2;
                    return true;
                }

                int bodyY = curY + headerH;

                // 子项胶囊点击
                if (h2.isExpanded && !h2.subCategories.isEmpty()) {
                    bodyY += UIConstants.CARD_INNER_TOP;
                    int inset = UIConstants.CARD_ITEM_INSET;
                    int pillX = cardX + inset;
                    int pillW = cardW - inset * 2;
                    int pillH = UIConstants.CARD_ITEM_H - 4;

                    for (PatternAndFontOverlay.H3Category h3 : h2.subCategories) {
                        if (LayoutHelper.isMouseInRect(mx, my, pillX, bodyY, pillW, pillH)) {
                            PatternAndFontOverlay.clearSidebarTop();
                            PatternAndFontOverlay.selectedH2 = h2;
                            PatternAndFontOverlay.selectedH3 = h3;
                            PatternAndFontOverlay.isHomeSelected = false;
                            PatternAndFontOverlay.scrollY = 0;
                            return true;
                        }
                        bodyY += UIConstants.CARD_ITEM_H;
                    }
                }

                curY += LayoutHelper.calculateH2CardHeight(h2, tr) + UIConstants.CARD_GAP;
            }
        }
        return true;
    }

    // 内容区点击
    private static boolean handlePaneClick(double mx, double my, int winW, int winH,
                                           int paneW, int clipTop, int clipBottom) {
        int navW = SidebarState.getEffectiveWidth();
        int contentY = clipTop + 15 - (int) PatternAndFontOverlay.scrollY;

        if (PatternAndFontOverlay.isHomeSelected) {
            return handleHomeClick();
        } else if (PatternAndFontOverlay.selectedH3 != null) {
            if (PatternAndFontOverlay.isYunbeiucBuiltinSelected()) {
                if (YunbeiUCIntegration.handleClick((int) mx, (int) my, winW, winH, navW)) {
                    return true;
                }
            }
            return handleSectionClick(mx, my, winW, paneW, contentY, clipTop, clipBottom);
        }
        return false;
    }

    // 主页链接点击
    private static boolean handleHomeClick() {
        String url = PatternAndFontOverlay.getLastHoveredUrl();
        if (url != null) {
            Util.getOperatingSystem().open(url);
            return true;
        }
        return false;
    }

    // 分区内容点击
    private static boolean handleSectionClick(double mx, double my, int winW,
                                              int paneW, int contentY, int clipTop, int clipBottom) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        PatternAndFontOverlay.H3Category selH3 = PatternAndFontOverlay.selectedH3;

        int curY = contentY;

        // 分类说明横幅
        if (selH3.headerText != null) {
            int descMaxW = paneW - 48;
            int lines = tr.wrapLines(selH3.headerText, descMaxW).size();
            curY += lines * 12 + 12 + 16;
        }

        boolean[] consumed = new boolean[1];
        for (int secIdx = 0; secIdx < selH3.sections.size(); secIdx++) {
            curY = handleSectionNodeClick(mx, my, winW, paneW, clipTop, clipBottom,
                    selH3.sections.get(secIdx), curY, 0, consumed);
            if (consumed[0]) return true;
            curY += (secIdx < selH3.sections.size() - 1) ? 16 : 8;
        }
        return false;
    }

    /**
     * 处理单个分区（含子分区）的点击，返回推进后的 y。
     *
     * <p>命中时置 {@code consumed[0]} 为 true 并立即返回；推进规则与
     * {@code PatternAndFontOverlay.paintSectionNode} 保持一致。
     *
     * @param indent 子分区缩进层级，顶层为 0
     */
    private static int handleSectionNodeClick(double mx, double my, int winW, int paneW,
                                              int clipTop, int clipBottom,
                                              PatternAndFontOverlay.H4Section section,
                                              int curY, int indent, boolean[] consumed) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int navW = SidebarState.getEffectiveWidth();
        int indentPx = indent * 14;
        int secX = navW + 16 + indentPx;
        int secMaxW = paneW - 32 - indentPx;

        // 标题行点击：展开/收起该类型（与 paintSectionNode 的标题行区域一致）
        if (LayoutHelper.isMouseInRect(mx, my, secX, curY, secMaxW, 20)) {
            section.isExpanded = !section.isExpanded;
            consumed[0] = true;
            return curY + 20;
        }
        curY += 20; // 标题行

        // 收起的分区：跳过描述/子分区/标签栏/内容
        if (!section.isExpanded) return curY;

        PatternAndFontOverlay.H4Section eff = LayoutHelper.effective(section);

        // 描述文字
        if (eff.description != null && !eff.description.getString().isEmpty()) {
            int descMaxW = secMaxW - 10;
            curY += tr.wrapLines(eff.description, descMaxW).size() * 12 + 4;
        }

        // 子分区：递归处理
        if (section.useChildSections && !section.childSections.isEmpty()) {
            for (int i = 0; i < section.childSections.size(); i++) {
                curY = handleSectionNodeClick(mx, my, winW, paneW, clipTop, clipBottom,
                        section.childSections.get(i), curY, indent + 1, consumed);
                if (consumed[0]) return curY;
                if (i < section.childSections.size() - 1) curY += 10;
            }
            return curY + 4;
        }

        // 标签栏点击
        if (section.useSubfolders && !section.subFolders.isEmpty()) {
            int tabW = UIConstants.FILTER_PILL_WIDTH;
            int tabH = UIConstants.FILTER_PILL_HEIGHT;
            int gap = UIConstants.FILTER_PILL_SPACING;
            int totalW = section.subFolders.size() * tabW + (section.subFolders.size() - 1) * gap;
            int startX = winW - 24 - totalW;

            for (int i = 0; i < section.subFolders.size(); i++) {
                int tx = startX + i * (tabW + gap);
                if (LayoutHelper.isMouseInRect(mx, my, tx, curY, tabW, tabH)) {
                    section.activeTabIndex = i;
                    consumed[0] = true;
                    return curY;
                }
            }
            curY += tabH + 8;
        }

        if (section.useStyles && !section.subSections.isEmpty()) {
            int tabW = UIConstants.FILTER_PILL_WIDTH;
            int tabH = UIConstants.FILTER_PILL_HEIGHT;
            int gap = UIConstants.FILTER_PILL_SPACING;
            int totalW = section.subSections.size() * tabW + (section.subSections.size() - 1) * gap;
            int startX = winW - 24 - totalW;

            for (int i = 0; i < section.subSections.size(); i++) {
                int tx = startX + i * (tabW + gap);
                if (LayoutHelper.isMouseInRect(mx, my, tx, curY, tabW, tabH)) {
                    section.activeStyleIndex = i;
                    consumed[0] = true;
                    return curY;
                }
            }
            curY += tabH + 8;
        }

        // 内容点击
        if (eff.isFontMode) {
            int idx = GridRenderer.getFontItemIndex(mx, my, paneW, curY, clipTop, clipBottom, eff.fontItems.size());
            if (idx >= 0) {
                PatternAndFontOverlay.FontItem item = eff.fontItems.get(idx);
                insertText(String.format(eff.fontInsertTemplate, item.fontId));
                consumed[0] = true;
                return curY;
            }
            int cardH = UIConstants.FONT_CARD_H + UIConstants.FONT_CARD_GAP;
            curY += !eff.fontItems.isEmpty() ? eff.fontItems.size() * cardH : 30;
        } else if (eff.isWhitelistMode) {
            if (!eff.whitelistItems.isEmpty()) {
                int idx = GridRenderer.getGridItemIndex(mx, my, paneW, curY, clipTop, clipBottom, eff.whitelistItems.size());
                if (idx >= 0) {
                    PatternAndFontOverlay.WhitelistPatternItem item = eff.whitelistItems.get(idx);
                    if (!item.insertContent.isEmpty()) {
                        insertText(item.insertContent);
                    } else {
                        insertTex(item.textureId);
                    }
                    consumed[0] = true;
                    return curY;
                }
                int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
                int rows = (int) Math.ceil((double) eff.whitelistItems.size() / cols);
                curY += rows * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y) - UIConstants.CELL_GAP_Y + 12;
            } else {
                curY += 30;
            }
        } else {
            String tabKey = eff.useSubfolders && !eff.subFolders.isEmpty()
                    ? eff.subFolders.get(eff.activeTabIndex).dirName : "root";
            var textures = eff.cachedTextures.get(tabKey);

            if (textures != null && !textures.isEmpty()) {
                int idx = GridRenderer.getGridItemIndex(mx, my, paneW, curY, clipTop, clipBottom, textures.size());
                if (idx >= 0) {
                    insertTex(textures.get(idx));
                    consumed[0] = true;
                    return curY;
                }
                int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
                int rows = (int) Math.ceil((double) textures.size() / cols);
                curY += rows * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y) - UIConstants.CELL_GAP_Y;
            } else {
                curY += 30;
            }
        }

        return curY;
    }

    public static void mouseScrolled(double mx, double my, double amount) {
        if (!PatternAndFontOverlay.isVisible) return;

        int navW = SidebarState.getEffectiveWidth();
        if (mx > navW && PatternAndFontOverlay.maxScrollY > 20) {
            PatternAndFontOverlay.scrollY -= amount * UIConstants.WHEEL_STEP;
            PatternAndFontOverlay.scrollY = MathHelper.clamp(PatternAndFontOverlay.scrollY, 0, PatternAndFontOverlay.maxScrollY);
        } else if (mx <= navW && PatternAndFontOverlay.maxSidebarScrollY > 20) {
            PatternAndFontOverlay.sidebarScrollY -= amount * UIConstants.WHEEL_STEP;
            PatternAndFontOverlay.sidebarScrollY = MathHelper.clamp(PatternAndFontOverlay.sidebarScrollY, 0, PatternAndFontOverlay.maxSidebarScrollY);
        }
    }

    public static boolean mouseReleased(double mx, double my, int button) {
        if (!PatternAndFontOverlay.isVisible || button != 0) return false;
        PatternAndFontOverlay.isDraggingMainScrollbar = false;
        PatternAndFontOverlay.isDraggingSidebarScrollbar = false;
        return true;
    }

    private static void insertTex(Identifier id) {
        PatternAndFontOverlay.insertToCurrentLine("-texture " + id);
    }

    private static void insertText(String text) {
        PatternAndFontOverlay.insertToCurrentLine(text);
    }

    private static PatternAndFontOverlay.H3Category findFirstLeafH3(PatternAndFontOverlay.H3Category h3) {
        if (h3.subCategories.isEmpty()) {
            return h3;
        }
        return findFirstLeafH3(h3.subCategories.get(0));
    }
}