package com.beigu.yunbeiuc.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 图案与字体选择器 — 海燕蓝主题 UI
 *
 * <p>界面结构：左侧导航栏（卡片分组式，可收起）+ 右侧内容区。
 *
 * @see PatternRegistry
 * @see SidebarState
 * @see HomepageRenderer
 */
public final class PatternAndFontOverlay {

    // ==================== 数据模型 ====================

    public enum FilterMode {
        WHITELIST,
        BLACKLIST,
        PREFIX,
        PREFIX_EXCLUDE,
        NONE
    }

    public static class SubFolderDef {
        public final String dirName;
        public final Text displayName;

        public SubFolderDef(String dirName, Text displayName) {
            this.dirName = dirName;
            this.displayName = displayName;
        }
    }

    public static class WhitelistPatternItem {
        public final Identifier textureId;
        public final String insertContent;
        public final Text displayName;

        public WhitelistPatternItem(Identifier textureId, String insertContent, Text displayName) {
            this.textureId = textureId;
            this.insertContent = insertContent;
            this.displayName = displayName;
        }
    }

    public static class FontItem {
        public final String fontId;
        public final Text displayName;

        public FontItem(String fontId, Text displayName) {
            this.fontId = fontId;
            this.displayName = displayName;
        }
    }

    public static class H4Section {
        public final Text title;
        public final Text description;
        public final Identifier basePath;

        public boolean useSubfolders = false;
        public final List<SubFolderDef> subFolders = new ArrayList<>();

        public boolean useStyles = false;
        public final List<H4Section> subSections = new ArrayList<>();
        public int activeStyleIndex = 0;
        public boolean isExpanded = true;

        /** 子分区（树形嵌套，父分区只作容器、可整体收起；与 useStyles 的标签切换不同） */
        public boolean useChildSections = false;
        public final List<H4Section> childSections = new ArrayList<>();

        public FilterMode extFilterMode = FilterMode.NONE;
        public final List<String> extFilterList = new ArrayList<>();
        /** 附加排除前缀：主过滤器通过后，文件名命中任一前缀即剔除（默认空，不影响原有过滤行为） */
        public final List<String> extExcludePrefixes = new ArrayList<>();
        /** 附加精确排除：主过滤器通过后，文件名与之完全相同即剔除（默认空） */
        public final List<String> extExcludeNames = new ArrayList<>();

        public int activeTabIndex = 0;
        public final java.util.Map<String, List<Identifier>> cachedTextures = new java.util.HashMap<>();

        public boolean isWhitelistMode = false;
        public final List<WhitelistPatternItem> whitelistItems = new ArrayList<>();

        public boolean isFontMode = false;
        public final List<FontItem> fontItems = new ArrayList<>();
        public String fontInsertTemplate = "-json {\"font\":\"%s\",\"text\":\"XXX\"}";

        public String customJsonPath = "";

        public H4Section(Text title, Text description, Identifier basePath) {
            this.title = title;
            this.description = description;
            this.basePath = basePath;
        }

        public H4Section enableSubfolders() {
            this.useSubfolders = true;
            return this;
        }

        public H4Section addSubFolder(String dirName, Text displayName) {
            this.subFolders.add(new SubFolderDef(dirName, displayName));
            return this;
        }

        public H4Section enableStyles() {
            this.useStyles = true;
            return this;
        }

        /** 开启子分区模式：本分区作为容器，内容全部由 {@link #addChildSection} 添加的子分区承载。 */
        public H4Section enableChildSections() {
            this.useChildSections = true;
            return this;
        }

        public H4Section addChildSection(H4Section child) {
            this.childSections.add(child);
            return this;
        }

        public H4Section addStyle(H4Section styleSection) {
            this.subSections.add(styleSection);
            return this;
        }

        public H4Section setExtensionFilter(FilterMode mode, String... exts) {
            this.extFilterMode = mode;
            this.extFilterList.clear();
            for (String ext : exts) this.extFilterList.add(ext);
            return this;
        }

        /** 在主过滤器之上追加排除前缀（例如"sign_guide 全部，但去掉 sign_guide_roadside_facility"）。 */
        public H4Section setExtensionExclude(String... prefixes) {
            this.extExcludePrefixes.clear();
            for (String prefix : prefixes) this.extExcludePrefixes.add(prefix);
            return this;
        }

        /** 在主过滤器之上追加精确排除的文件名（含扩展名），用于剔除已归入其它分类的素材。 */
        public H4Section setExtensionExcludeNames(String... fileNames) {
            this.extExcludeNames.clear();
            for (String fileName : fileNames) this.extExcludeNames.add(fileName);
            return this;
        }

        public H4Section setWhitelistMode() {
            this.isWhitelistMode = true;
            return this;
        }

        public H4Section addWhitelistItem(Identifier textureId, String insertContent, Text displayName) {
            this.whitelistItems.add(new WhitelistPatternItem(textureId, insertContent, displayName));
            return this;
        }

        public H4Section setFontMode() {
            this.isFontMode = true;
            return this;
        }

        public H4Section addFontItem(String fontId, Text displayName) {
            this.fontItems.add(new FontItem(fontId, displayName));
            return this;
        }

        public H4Section setFontInsertTemplate(String template) {
            this.fontInsertTemplate = template;
            return this;
        }

        public H4Section setCustomJsonPath(String path) {
            this.customJsonPath = path;
            return this;
        }
    }

    public static class H3Category {
        public final Text title;
        public Text headerText = null;
        public final List<H3Category> subCategories = new ArrayList<>();
        public final List<H4Section> sections = new ArrayList<>();
        public boolean isExpanded = true;

        public H3Category(Text title) {
            this.title = title;
        }

        public H3Category addSubCategory(H3Category sub) {
            this.subCategories.add(sub);
            return this;
        }

        public H3Category addSection(H4Section section) {
            this.sections.add(section);
            return this;
        }
    }

    public static class H2Category {
        public final Text title;
        public final List<H3Category> subCategories = new ArrayList<>();
        public boolean isExpanded = true;

        public H2Category(Text title) {
            this.title = title;
        }

        public H2Category addSubCategory(H3Category sub) {
            this.subCategories.add(sub);
            return this;
        }
    }

    // ==================== 全局状态 ====================

    public static final List<H2Category> REGISTRY = new ArrayList<>();

    public static H2Category selectedH2 = null;
    public static H3Category selectedH3 = null;

    public static boolean isVisible = false;
    public static double scrollY = 0;
    public static double maxScrollY = 0;

    public static double sidebarScrollY = 0;
    public static double maxSidebarScrollY = 0;

    public static boolean isDraggingMainScrollbar = false;
    public static boolean isDraggingSidebarScrollbar = false;

    public static double dragStartMouseY = 0;
    public static double dragStartScrollY = 0;
    public static double dragStartSidebarScrollY = 0;

    public static boolean isHomeSelected = false;

    public static int navSelection = 0;
    public static final int NAV_TOP_NONE = SidebarState.NAV_NONE;
    public static final int NAV_TOP_HOME = SidebarState.NAV_HOME;

    public static boolean isDataLoaded = false;

    // ==================== 导航逻辑代理 ====================

    public static void resetForReload() {
        REGISTRY.clear();
        isDataLoaded = false;
        selectedH2 = null;
        selectedH3 = null;
    }

    public static void closeOverlay() {
        isVisible = false;
        targetScreen = null;
    }

    public static void selectSidebarTop(int which) {
        SidebarState.selectNavTop(which);
    }

    public static void clearSidebarTop() {
        SidebarState.clearNavTop();
    }

    public static boolean isYunbeiucBuiltinSelected() {
        if (selectedH3 == null) return false;
        String key = Text.translatable("yunbeiuc.gui.categories.yunbeiuc_builtin").getString();
        return selectedH3.title.getString().equals(key);
    }

    // ==================== 主渲染入口 ====================

    public static void render(DrawContext ctx, int mouseX, int mouseY) {
        if (!isVisible) return;

        YunbeiUCIntegration.clearHovered();
        clearLastHoveredUrl();

        int winW = LayoutHelper.getScreenWidth();
        int winH = LayoutHelper.getScreenHeight();
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int navW = SidebarState.getEffectiveWidth();
        int paneW = winW - navW;

        int clipTop = UIConstants.TOP_BAR_HEIGHT + 1;
        int clipBottom = winH - UIConstants.BOTTOM_BAR_HEIGHT;
        int clipHeight = clipBottom - clipTop;
        int navClipH = winH - UIConstants.TOP_BAR_HEIGHT - UIConstants.BOTTOM_BAR_HEIGHT;

        PatternRegistry.registerBuiltInPatterns();

        SidebarState.enforceNavMutualExclusion();

        refreshScrollBounds(tr, paneW, navClipH, clipHeight);
        processScrollbarDrag(mouseY, clipHeight, navClipH);

        ctx.getMatrices().push();
        ctx.getMatrices().translate(0.0f, 0.0f, UIConstants.Z_LAYER);

        paintNavPanel(ctx, tr, mouseX, mouseY, navW, navClipH);
        paintContentPane(ctx, tr, mouseX, mouseY, winW, winH, paneW, clipTop, clipBottom);

        // 内容区边框（与侧边栏同风格；左边界与侧边栏右边框共用，不重复绘制）
        ctx.fill(navW, 0, winW, 1, UIConstants.CLR_CONTENT_BORDER);
        ctx.fill(navW, winH - 1, winW, winH, UIConstants.CLR_CONTENT_BORDER);
        ctx.fill(winW - 1, 0, winW, winH, UIConstants.CLR_CONTENT_BORDER);

        paintCloseButton(ctx, tr, mouseX, mouseY, winW, paneW, winH);

        ctx.getMatrices().pop();
    }

    private static void refreshScrollBounds(TextRenderer tr, int paneW, int navClipH, int clipHeight) {
        int totalNavH = UIConstants.TOP_BAR_HEIGHT + UIConstants.NAV_PAD
                + LayoutHelper.getHomeCardHeight() + UIConstants.CARD_GAP;
        for (H2Category h2 : REGISTRY) {
            totalNavH += LayoutHelper.calculateH2CardHeight(h2, tr) + UIConstants.CARD_GAP;
        }
        totalNavH += UIConstants.NAV_PAD;
        maxSidebarScrollY = Math.max(0, totalNavH - navClipH);
        sidebarScrollY = MathHelper.clamp(sidebarScrollY, 0, maxSidebarScrollY);

        int totalPaneH = LayoutHelper.getTotalMainContentHeight(paneW, tr);
        maxScrollY = Math.max(0, totalPaneH - clipHeight);
        scrollY = MathHelper.clamp(scrollY, 0, maxScrollY);
    }

    private static void processScrollbarDrag(double mouseY, int clipHeight, int navClipH) {
        if (isDraggingMainScrollbar && maxScrollY > 0) {
            float ratio = (float) clipHeight / (float) (clipHeight + maxScrollY);
            int thumb = Math.max(UIConstants.THUMB_MIN_SIZE, (int) (clipHeight * ratio));
            int track = clipHeight - thumb;
            if (track > 0) {
                double delta = ((mouseY - dragStartMouseY) / track) * maxScrollY;
                scrollY = MathHelper.clamp(dragStartScrollY + delta, 0, maxScrollY);
            }
        }

        if (isDraggingSidebarScrollbar && maxSidebarScrollY > 0) {
            float ratio = (float) navClipH / (float) (navClipH + maxSidebarScrollY);
            int thumb = Math.max(UIConstants.THUMB_MIN_SIZE, (int) (navClipH * ratio));
            int track = navClipH - thumb;
            if (track > 0) {
                double delta = ((mouseY - dragStartMouseY) / track) * maxSidebarScrollY;
                sidebarScrollY = MathHelper.clamp(dragStartSidebarScrollY + delta, 0, maxSidebarScrollY);
            }
        }
    }

    // ==================== 导航栏渲染（卡片分组式） ====================

    private static void paintNavPanel(DrawContext ctx, TextRenderer tr, int mx, int my, int navW, int clipH) {
        int winH = LayoutHelper.getScreenHeight();

        // 背景
        ctx.fill(0, 0, navW, winH, UIConstants.CLR_NAV_BG);
        ctx.fill(0, 0, navW, UIConstants.TOP_BAR_HEIGHT, UIConstants.CLR_NAV_TOPBAR);
        ctx.fill(0, UIConstants.TOP_BAR_HEIGHT - 1, navW, UIConstants.TOP_BAR_HEIGHT, UIConstants.CLR_NAV_DIVIDER);

        // 顶栏：收起按钮 + 标题
        boolean isToggleHover = LayoutHelper.isMouseInRect(mx, my, 4, 5, 22, 22);
        if (isToggleHover) {
            ctx.fill(4, 5, 26, 27, UIConstants.CLR_NAV_ITEM_HOVER);
        }
        drawHamburger(ctx, 9, 10, UIConstants.CLR_NAVTextPri);

        if (!SidebarState.isCollapsed()) {
            Text navTitle = Text.translatable("yunbeiuc.gui.sidebar.title");
            ctx.drawText(tr, navTitle, 34, (UIConstants.TOP_BAR_HEIGHT - 8) / 2, UIConstants.CLR_NAVTextPri, false);
        }

        ctx.enableScissor(0, UIConstants.TOP_BAR_HEIGHT, navW, winH - UIConstants.BOTTOM_BAR_HEIGHT);

        int curY = UIConstants.TOP_BAR_HEIGHT + UIConstants.NAV_PAD - (int) sidebarScrollY;

        if (SidebarState.isCollapsed()) {
            // ---- 收起模式：图标方块 ----
            curY = paintCollapsedIcon(ctx, tr, mx, my, navW, curY, "\u2302", isHomeSelected);
            for (H2Category h2 : REGISTRY) {
                String abbr = getH2Abbreviation(h2);
                boolean active = !isHomeSelected && selectedH2 == h2;
                curY = paintCollapsedIcon(ctx, tr, mx, my, navW, curY, abbr, active);
            }
        } else {
            // ---- 展开模式：主页卡片 ----
            curY = paintHomeCard(ctx, tr, mx, my, navW, curY);
            curY += UIConstants.CARD_GAP;

            // ---- H2 分组卡片 ----
            for (H2Category h2 : REGISTRY) {
                curY = paintH2Card(ctx, tr, mx, my, navW, curY, h2);
                curY += UIConstants.CARD_GAP;
            }
        }

        ctx.disableScissor();

        LayoutHelper.renderScrollbar(ctx, 0, UIConstants.TOP_BAR_HEIGHT, navW,
                clipH, sidebarScrollY, maxSidebarScrollY, clipH, mx, my);

        // 侧边栏边框：最后绘制，压在滚动条与内容之上
        ctx.drawBorder(0, 0, navW, winH, UIConstants.CLR_NAV_BORDER);
    }

    // 汉堡图标
    private static void drawHamburger(DrawContext ctx, int x, int y, int color) {
        ctx.fill(x, y, x + 14, y + 2, color);
        ctx.fill(x, y + 5, x + 14, y + 7, color);
        ctx.fill(x, y + 10, x + 14, y + 12, color);
    }

    // 主页卡片
    private static int paintHomeCard(DrawContext ctx, TextRenderer tr,
                                     int mx, int my, int navW, int curY) {
        int x = UIConstants.NAV_PAD;
        int w = navW - UIConstants.NAV_PAD * 2;
        int h = UIConstants.CARD_HEADER_H;

        boolean hov = LayoutHelper.isMouseInRect(mx, my, x, curY, w, h);
        int bg = isHomeSelected ? UIConstants.CLR_NAVItemSelected
                : (hov ? UIConstants.CLR_CARD_HEADER_BG_HOVER : UIConstants.CLR_CARD_HEADER_BG);
        int border = isHomeSelected ? UIConstants.CLR_ACCENT : UIConstants.CLR_CARD_BORDER;

        // 直角卡片：直接铺底 + 描边
        ctx.fill(x, curY, x + w, curY + h, bg);
        ctx.drawBorder(x, curY, w, h, border);

        // 左侧强调竖条（选中态）
        if (isHomeSelected) {
            ctx.fill(x + 1, curY + 6, x + 1 + UIConstants.NAV_ACCENT_BAR_W, curY + h - 6,
                    UIConstants.CLR_ACCENT);
        }

        int iconX = x + 12;
        int iconY = curY + (h - 8) / 2;
        // 房子图标（用像素块拼）
        ctx.fill(iconX + 3, iconY, iconX + 5, iconY + 2, UIConstants.CLR_NAVTextPri);
        ctx.fill(iconX + 1, iconY + 2, iconX + 7, iconY + 4, UIConstants.CLR_NAVTextPri);
        ctx.fill(iconX + 1, iconY + 4, iconX + 7, iconY + 8, UIConstants.CLR_NAVTextPri);

        Text label = Text.translatable("yunbeiuc.gui.sidebar.home");
        int txtClr = isHomeSelected ? 0xFFFFFFFF : (hov ? UIConstants.CLR_NAVTextPri : UIConstants.CLR_NAVTextSec);
        ctx.drawText(tr, label, iconX + 16, curY + (h - 8) / 2, txtClr, false);

        return curY + h;
    }

    // H2 分组卡片
    private static int paintH2Card(DrawContext ctx, TextRenderer tr,
                                   int mx, int my, int navW, int curY, H2Category h2) {
        int x = UIConstants.NAV_PAD;
        int w = navW - UIConstants.NAV_PAD * 2;
        int headerH = UIConstants.CARD_HEADER_H;

        int totalH = LayoutHelper.calculateH2CardHeight(h2, tr);

        // 卡片底（整卡背景，直角）
        ctx.fill(x, curY, x + w, curY + totalH, UIConstants.CLR_CARD_BG);
        ctx.drawBorder(x, curY, w, totalH, UIConstants.CLR_CARD_BORDER);

        // 头部（单独一层底色，与整卡顶部对齐）
        boolean headerHov = LayoutHelper.isMouseInRect(mx, my, x, curY, w, headerH);
        int headerBg = headerHov ? UIConstants.CLR_CARD_HEADER_BG_HOVER : UIConstants.CLR_CARD_HEADER_BG;
        ctx.fill(x + 1, curY + 1, x + w - 1, curY + headerH, headerBg);

        // 箭头
        String arrow = h2.isExpanded ? "\u25BE" : "\u25B8";
        ctx.drawText(tr, arrow, x + 10, curY + (headerH - 8) / 2,
                headerHov ? UIConstants.CLR_NAVTextPri : UIConstants.CLR_NAVTextSec, false);

        // 标题
        ctx.drawText(tr, h2.title.getString(), x + 24, curY + (headerH - 8) / 2,
                UIConstants.CLR_NAVTextPri, false);

        // 计数徽章
        int count = h2.subCategories.size();
        if (count > 0 && h2.isExpanded) {
            String cs = String.valueOf(count);
            int cw = tr.getWidth(cs) + 8;
            int badgeX = x + w - cw - 10;
            int badgeY = curY + (headerH - 14) / 2;
            ctx.fill(badgeX, badgeY, badgeX + cw, badgeY + 14, 0x40FFFFFF);
            ctx.drawText(tr, cs, badgeX + 4, badgeY + 3, UIConstants.CLR_NAVTextSec, false);
        }

        int bodyY = curY + headerH;

        // 子项胶囊
        if (h2.isExpanded && !h2.subCategories.isEmpty()) {
            bodyY += UIConstants.CARD_INNER_TOP;
            for (H3Category h3 : h2.subCategories) {
                paintH3Pill(ctx, tr, mx, my, x, w, bodyY, h3);
                bodyY += UIConstants.CARD_ITEM_H;
            }
        }

        return curY + totalH;
    }

    // H3 胶囊项
    private static void paintH3Pill(DrawContext ctx, TextRenderer tr,
                                    int mx, int my, int cardX, int cardW, int y, H3Category h3) {
        int inset = UIConstants.CARD_ITEM_INSET;
        int px = cardX + inset;
        int pw = cardW - inset * 2;
        int ph = UIConstants.CARD_ITEM_H - 4;

        boolean isSelected = !isHomeSelected && selectedH3 == h3;
        boolean isHov = LayoutHelper.isMouseInRect(mx, my, px, y, pw, ph);

        if (isSelected) {
            ctx.fill(px, y, px + pw, y + ph, UIConstants.CLR_NAV_PILL_SEL);
            // 左侧强调竖条
            ctx.fill(px + 2, y + 5, px + 2 + UIConstants.NAV_ACCENT_BAR_W, y + ph - 5,
                    UIConstants.CLR_ACCENT);
        } else if (isHov) {
            ctx.fill(px, y, px + pw, y + ph, UIConstants.CLR_NAV_PILL_HOVER);
        }

        int txtClr = isSelected ? 0xFFFFFFFF
                : (isHov ? UIConstants.CLR_NAVTextPri : UIConstants.CLR_NAVTextSec);
        ctx.drawText(tr, h3.title.getString(), px + 12, y + (ph - 8) / 2, txtClr, false);
    }

    // 收起模式图标方块
    private static int paintCollapsedIcon(DrawContext ctx, TextRenderer tr,
                                          int mx, int my, int navW, int curY,
                                          String iconText, boolean isSelected) {
        int btnSize = UIConstants.NAV_ICON_SIZE;
        int btnX = (navW - btnSize) / 2;
        int rowH = UIConstants.NAV_ICON_ROW_H;

        boolean hov = LayoutHelper.isMouseInRect(mx, my, 0, curY, navW, rowH);
        int bg = isSelected ? UIConstants.CLR_NAVItemSelected
                : (hov ? UIConstants.CLR_CARD_HEADER_BG_HOVER : 0x00000000);
        if (bg != 0x00000000) {
            ctx.fill(btnX, curY + 3, btnX + btnSize, curY + 3 + btnSize, bg);
        }

        int iconClr = isSelected ? 0xFFFFFFFF : (hov ? UIConstants.CLR_NAVTextPri : UIConstants.CLR_NAVTextSec);
        int iw = tr.getWidth(iconText);
        ctx.drawText(tr, iconText, btnX + (btnSize - iw) / 2, curY + 3 + (btnSize - 8) / 2, iconClr, false);

        return curY + rowH;
    }

    private static String getH2Abbreviation(H2Category h2) {
        String title = h2.title.getString();
        return title.length() >= 2 ? title.substring(0, 2) : title;
    }

    // ==================== 内容区渲染 ====================

    private static void paintContentPane(DrawContext ctx, TextRenderer tr, int mx, int my,
                                         int winW, int winH, int paneW, int clipTop, int clipBottom) {
        int navW = SidebarState.getEffectiveWidth();

        ctx.fill(navW, 0, winW, winH, UIConstants.CLR_CONTENT_BG);
        ctx.fill(navW, 0, winW, UIConstants.TOP_BAR_HEIGHT, UIConstants.CLR_CONTENT_TOPBAR);
        ctx.fill(navW, UIConstants.TOP_BAR_HEIGHT - 1, winW, UIConstants.TOP_BAR_HEIGHT, UIConstants.CLR_CONTENT_DIVIDER);

        Text paneTitle = isHomeSelected
                ? Text.translatable("yunbeiuc.gui.sidebar.home")
                : (selectedH3 != null ? selectedH3.title : Text.literal(""));
        int titleW = tr.getWidth(paneTitle);
        ctx.drawTextWithShadow(tr, paneTitle, navW + (paneW - titleW) / 2, (UIConstants.TOP_BAR_HEIGHT - 8) / 2, UIConstants.CLR_NAVTextPri);

        ctx.enableScissor(navW, clipTop, winW, clipBottom);

        int contentY = clipTop + 15 - (int) scrollY;

        if (isHomeSelected) {
            HomepageRenderer.render(ctx, tr, mx, my, paneW, contentY, clipTop, clipBottom);
        } else if (PatternAndFontOverlay.selectedH3 != null) {
            if (isYunbeiucBuiltinSelected()) {
                YunbeiUCIntegration.render(ctx, tr, winW, winH, mx, my, navW);
                ctx.disableScissor();
                return;
            }
            paintSectionBody(ctx, tr, mx, my, winW, paneW, contentY, clipTop, clipBottom);
        }

        ctx.disableScissor();

        LayoutHelper.renderScrollbar(ctx, navW, clipTop, winW - navW,
                clipBottom - clipTop, scrollY, maxScrollY, clipBottom - clipTop, mx, my);
    }

    // ==================== 分区内容渲染 ====================

    private static void paintSectionBody(DrawContext ctx, TextRenderer tr, int mx, int my,
                                         int winW, int paneW, int contentY, int clipTop, int clipBottom) {
        int navW = SidebarState.getEffectiveWidth();
        H3Category h3 = selectedH3;

        // 分类说明文字（整行蓝色底色条）
        if (h3.headerText != null) {
            int descMaxW = paneW - 48;
            List<OrderedText> wrapped = tr.wrapLines(h3.headerText, descMaxW);
            int bannerH = wrapped.size() * 12 + 12;
            int bannerX = navW + 16;

            if (contentY + bannerH >= clipTop && contentY <= clipBottom) {
                ctx.fill(bannerX, contentY, bannerX + descMaxW, contentY + bannerH, UIConstants.CLR_ACCENT);
                for (int i = 0; i < wrapped.size(); i++) {
                    ctx.drawText(tr, wrapped.get(i), bannerX + 8, contentY + 6 + i * 12, 0xFFFFFFFF, false);
                }
            }
            contentY += bannerH + 16;
        }

        // 遍历每个分区
        for (int secIdx = 0; secIdx < h3.sections.size(); secIdx++) {
            contentY = paintSectionNode(ctx, tr, mx, my, winW, paneW, clipTop, clipBottom,
                    h3.sections.get(secIdx), contentY, 0);

            // ---- 分区间隔线 ----
            if (secIdx < h3.sections.size() - 1) {
                contentY += 6;
                if (contentY >= clipTop && contentY <= clipBottom) {
                    ctx.fill(navW + 24, contentY, navW + paneW - 24, contentY + 1, UIConstants.CLR_CONTENT_DIVIDER);
                }
                contentY += 10;
            } else {
                contentY += 8;
            }
        }
    }

    /**
     * 渲染单个分区（含其子分区），返回推进后的 contentY。
     *
     * <p>分区标题行始终可点击收起；{@code useChildSections} 的分区自身不承载内容，
     * 而是按 {@code indent} 逐层缩进渲染子分区，每个子分区各自独立折叠。
     *
     * @param indent 子分区缩进层级，顶层为 0
     */
    private static int paintSectionNode(DrawContext ctx, TextRenderer tr, int mx, int my,
                                        int winW, int paneW, int clipTop, int clipBottom,
                                        H4Section section, int contentY, int indent) {
        int navW = SidebarState.getEffectiveWidth();
        H4Section eff = LayoutHelper.effective(section);
        int indentPx = indent * 14;
        int secX = navW + 16 + indentPx;
        int secMaxW = paneW - 32 - indentPx;
        int titleY = contentY;
        boolean expanded = section.isExpanded;

        // 描述行先解析出来：左侧装饰竖条要一直延伸到底，与介绍文字底边对齐
        List<OrderedText> descLines = null;
        int descMaxW = secMaxW - 10;
        if (expanded && eff.description != null && !eff.description.getString().isEmpty()) {
            descLines = tr.wrapLines(eff.description, descMaxW);
        }
        // 竖条底边：收起或无描述时只包住标题行，展开且有描述时包住标题行 + 全部描述行
        int accentBottom = titleY + 16;
        if (descLines != null && !descLines.isEmpty()) {
            accentBottom = titleY + 20 + descLines.size() * 12;
        }

        if (titleY + accentBottom >= clipTop && titleY <= clipBottom) {
            ctx.fill(secX, titleY + 2, secX + 3, accentBottom, UIConstants.CLR_ACCENT);
        }
        if (titleY + 18 >= clipTop && titleY <= clipBottom) {
            boolean titleHov = LayoutHelper.isMouseInRect(mx, my, secX, titleY, secMaxW, 20);
            String arrow = expanded ? "▾" : "▸";
            ctx.drawText(tr, arrow, secX + 8, titleY + 4,
                    titleHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_MUTED, false);
            ctx.drawText(tr, eff.title, secX + 20, titleY + 4, UIConstants.CLR_HEADING, false);
        }
        contentY += 20;

        // 收起时只保留标题行，描述/子分区/内容全部隐藏
        if (expanded) {
            // ---- 分区描述（小字灰色） ----
            if (descLines != null) {
                for (int i = 0; i < descLines.size(); i++) {
                    if (contentY + 12 >= clipTop && contentY <= clipBottom) {
                        ctx.drawText(tr, descLines.get(i), secX + 10, contentY, UIConstants.CLR_MUTED, false);
                    }
                    contentY += 12;
                }
                contentY += 4;
            }

            if (section.useChildSections && !section.childSections.isEmpty()) {
                // ---- 子分区：递归渲染，逐层缩进 ----
                for (int i = 0; i < section.childSections.size(); i++) {
                    contentY = paintSectionNode(ctx, tr, mx, my, winW, paneW, clipTop, clipBottom,
                            section.childSections.get(i), contentY, indent + 1);
                    if (i < section.childSections.size() - 1) contentY += 10;
                }
                contentY += 4;
            } else {
                // ---- 筛选标签栏 ----
                if (section.useSubfolders && !section.subFolders.isEmpty()) {
                    contentY = paintTabBar(ctx, tr, mx, my, winW, section.subFolders, section.activeTabIndex, contentY);
                }

                if (section.useStyles && !section.subSections.isEmpty()) {
                    List<SubFolderDef> styleTabs = new ArrayList<>();
                    for (H4Section child : section.subSections) {
                        styleTabs.add(new SubFolderDef(child.title.getString(), child.title));
                    }
                    contentY = paintTabBar(ctx, tr, mx, my, winW, styleTabs, section.activeStyleIndex, contentY);
                }

                // ---- 内容网格/列表 ----
                contentY = paintSectionItems(ctx, tr, mx, my, paneW, clipTop, clipBottom, eff, contentY);
            }
        }
        return contentY;
    }

    private static int paintTabBar(DrawContext ctx, TextRenderer tr, int mx, int my,
                                   int winW, List<SubFolderDef> items, int activeIdx, int curY) {
        int tabW = UIConstants.FILTER_PILL_WIDTH;
        int tabH = UIConstants.FILTER_PILL_HEIGHT;
        int gap = UIConstants.FILTER_PILL_SPACING;
        int totalW = items.size() * tabW + (items.size() - 1) * gap;
        int startX = winW - 24 - totalW;

        for (int i = 0; i < items.size(); i++) {
            SubFolderDef def = items.get(i);
            int tx = startX + i * (tabW + gap);

            if (curY + tabH < UIConstants.TOP_BAR_HEIGHT + 1 || curY > LayoutHelper.getScreenHeight() - UIConstants.BOTTOM_BAR_HEIGHT) continue;

            boolean isHov = LayoutHelper.isMouseInRect(mx, my, tx, curY, tabW, tabH);
            boolean isActive = (activeIdx == i);

            int bg;
            int txtClr;
            if (isActive) {
                bg = UIConstants.CLR_ACCENT;
                txtClr = 0xFFFFFFFF;
            } else if (isHov) {
                bg = UIConstants.CLR_BTN_HOVER;
                txtClr = UIConstants.CLR_BTN_LABEL;
            } else {
                bg = UIConstants.CLR_BTN_FILL;
                txtClr = UIConstants.CLR_MUTED;
            }

            ctx.fill(tx, curY, tx + tabW, curY + tabH, bg);

            String name = def.displayName.getString();
            List<OrderedText> lines = tr.wrapLines(Text.literal(name), tabW - 4);
            int totalTxtH = lines.size() * tr.fontHeight;
            int txtY = curY + (tabH - totalTxtH) / 2;

            for (int li = 0; li < lines.size(); li++) {
                OrderedText line = lines.get(li);
                int lw = tr.getWidth(line);
                int lx = tx + (tabW - lw) / 2;
                ctx.drawText(tr, line, lx, txtY + li * tr.fontHeight, txtClr, false);
            }
        }
        return curY + tabH + 8;
    }

    private static int paintSectionItems(DrawContext ctx, TextRenderer tr, int mx, int my,
                                         int paneW, int clipTop, int clipBottom,
                                         H4Section section, int curY) {
        int navW = SidebarState.getEffectiveWidth();
        if (section.isFontMode) {
            return curY + GridRenderer.renderFontList(ctx, tr, mx, my, paneW, clipTop, clipBottom,
                    section.fontItems, navW + 24, curY);
        } else if (section.isWhitelistMode) {
            return curY + GridRenderer.renderWhitelistGrid(ctx, tr, mx, my, paneW, clipTop, clipBottom,
                    section.whitelistItems, navW + 24, curY);
        } else {
            String tabKey = section.useSubfolders && !section.subFolders.isEmpty()
                    ? section.subFolders.get(section.activeTabIndex).dirName : "root";
            var textures = section.cachedTextures.get(tabKey);
            return curY + GridRenderer.renderCachedTextureGrid(ctx, tr, mx, my, paneW, clipTop, clipBottom,
                    textures != null ? textures : List.of(), navW + 24, curY);
        }
    }

    // 关闭按钮
    private static void paintCloseButton(DrawContext ctx, TextRenderer tr, int mx, int my,
                                         int winW, int paneW, int winH) {
        int navW = SidebarState.getEffectiveWidth();
        int btnX = navW + (paneW - UIConstants.CLOSE_BTN_WIDTH) / 2;
        int btnY = winH - 38;
        boolean hov = LayoutHelper.isMouseInRect(mx, my, btnX, btnY, UIConstants.CLOSE_BTN_WIDTH, UIConstants.CLOSE_BTN_HEIGHT);

        int bg = hov ? UIConstants.CLR_BTN_HOVER : UIConstants.CLR_BTN_FILL;
        ctx.fill(btnX, btnY, btnX + UIConstants.CLOSE_BTN_WIDTH, btnY + UIConstants.CLOSE_BTN_HEIGHT, bg);
        ctx.drawBorder(btnX, btnY, UIConstants.CLOSE_BTN_WIDTH, UIConstants.CLOSE_BTN_HEIGHT, UIConstants.CLR_BTN_STROKE);

        Text label = Text.translatable("yunbeiuc.gui.button.back");
        int lw = tr.getWidth(label);
        ctx.drawText(tr, label, btnX + (UIConstants.CLOSE_BTN_WIDTH - lw) / 2, btnY + 7, UIConstants.CLR_BTN_LABEL, false);
    }

    // ==================== 插入代理 ====================

    public static TextDisplayScreen targetScreen = null;

    public static void insertToCurrentLine(String text) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextDisplayScreen target = null;
        if (client.currentScreen instanceof TextDisplayScreen screen) {
            target = screen;
        } else if (client.currentScreen instanceof PatternAndFontBlankScreen && targetScreen != null) {
            target = targetScreen;
        }
        if (target != null) {
            target.insertPatternContent(text);
        }
        isVisible = false;
    }

    // ==================== 事件代理 ====================

    public static boolean mouseClicked(double mouseX, double mouseY, int button) {
        return MouseEventHandler.mouseClicked(mouseX, mouseY, button);
    }

    public static void mouseScrolled(double mouseX, double mouseY, double amount) {
        MouseEventHandler.mouseScrolled(mouseX, mouseY, amount);
    }

    public static boolean mouseReleased(double mouseX, double mouseY, int button) {
        return MouseEventHandler.mouseReleased(mouseX, mouseY, button);
    }

    // ==================== URL 悬停状态 ====================

    private static String lastHoveredUrl = null;

    public static String getLastHoveredUrl() {
        return lastHoveredUrl;
    }

    public static void setLastHoveredUrl(String url) {
        lastHoveredUrl = url;
    }

    public static void clearLastHoveredUrl() {
        lastHoveredUrl = null;
    }

    public static boolean openHomepageLink(String url) {
        if (url != null && !url.isEmpty()) {
            Util.getOperatingSystem().open(url);
            return true;
        }
        return false;
    }
}