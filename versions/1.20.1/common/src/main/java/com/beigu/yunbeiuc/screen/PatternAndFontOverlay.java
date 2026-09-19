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
 * 图案与字体选择界面的 UI 渲染与交互组合入口
 *
 * <p>界面只保留三个内容：主页、图案、字体。渲染在 {@link TextDisplayScreen} 之上，
 * 由该屏幕转发渲染与鼠标/键盘事件。
 *
 * @see PatternRegistry
 * @see SidebarState
 * @see YunbeiUCIntegration
 * @see HomepageRenderer
 */
public final class PatternAndFontOverlay {

    // ==================== 数据类 ====================

    public enum FilterMode {
        /** 白名单：仅保留列表中的文件名 */
        WHITELIST,
        /** 黑名单：排除列表中的文件名 */
        BLACKLIST,
        /** 前缀白名单：仅保留以列表中任一前缀开头的文件名 */
        PREFIX,
        /** 前缀黑名单：排除以列表中任一前缀开头的文件名 */
        PREFIX_EXCLUDE,
        /** 不过滤 */
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

    /**
     * 字体条目。
     */
    public static class FontItem {
        public final String fontId;
        public final Text displayName;

        public FontItem(String fontId, Text displayName) {
            this.fontId = fontId;
            this.displayName = displayName;
        }
    }

    /**
     * H4 级分区，承载一组图案/字体的展示配置。
     */
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

        public FilterMode extFilterMode = FilterMode.NONE;
        public final List<String> extFilterList = new ArrayList<>();

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

    /**
     * H3 级分类。
     */
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

    /**
     * H2 级分类。
     */
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

    // ==================== 公开状态字段 ====================

    /** 分类注册列表。 */
    public static final List<H2Category> REGISTRY = new ArrayList<>();

    /** 当前选中的 H2 分类。 */
    public static H2Category selectedH2 = null;
    /** 当前选中的 H3 分类。 */
    public static H3Category selectedH3 = null;

    /** 浮层是否可见。 */
    public static boolean isVisible = false;
    /** 主区域滚动位置。 */
    public static double scrollY = 0;
    /** 主区域最大滚动位置。 */
    public static double maxScrollY = 0;

    /** 侧边栏滚动位置。 */
    public static double sidebarScrollY = 0;
    /** 侧边栏最大滚动位置。 */
    public static double maxSidebarScrollY = 0;

    /** 主滚动条是否正在拖动。 */
    public static boolean isDraggingMainScrollbar = false;
    /** 侧边栏滚动条是否正在拖动。 */
    public static boolean isDraggingSidebarScrollbar = false;

    /** 拖动起始鼠标 Y 坐标。 */
    public static double dragStartMouseY = 0;
    /** 拖动起始主区域滚动位置。 */
    public static double dragStartScrollY = 0;
    /** 拖动起始侧边栏滚动位置。 */
    public static double dragStartSidebarScrollY = 0;

    /** 主页是否选中。 */
    public static boolean isHomeSelected = false;

    /** 侧边栏选中索引。 */
    public static int sidebarSelection = 0;
    /** 侧边栏顶层项：无。 */
    public static final int SIDEBAR_TOP_NONE = SidebarState.SIDEBAR_NONE;
    /** 侧边栏顶层项：主页。 */
    public static final int SIDEBAR_TOP_HOME = SidebarState.SIDEBAR_HOME;

    /** 注册中心数据是否已加载。 */
    public static boolean isDataLoaded = false;

    // ==================== 互斥逻辑转发 ====================

    /**
     * 清除注册表数据，准备重新加载。
     * <p>在资源包刷新时调用，清空所有缓存数据以便重新构建。
     */
    public static void resetForReload() {
        REGISTRY.clear();
        isDataLoaded = false;
        selectedH2 = null;
        selectedH3 = null;
    }

    /**
     * 关闭浮层并清理目标界面引用。
     */
    public static void closeOverlay() {
        isVisible = false;
        targetScreen = null;
    }

    /**
     * 选中侧边栏顶层项。
     *
     * @param which 顶层项索引
     */
    public static void selectSidebarTop(int which) {
        SidebarState.selectSidebarTop(which);
    }

    /**
     * 清除侧边栏顶层选中状态。
     */
    public static void clearSidebarTop() {
        SidebarState.clearSidebarTop();
    }

    /**
     * 当前选中的 H3 是否为「云北城建内置」插入方式说明页。
     *
     * @return 是则返回 {@code true}
     */
    public static boolean isYunbeiucBuiltinSelected() {
        if (selectedH3 == null) return false;
        String key = Text.translatable("yunbeiuc.gui.categories.yunbeiuc_builtin").getString();
        return selectedH3.title.getString().equals(key);
    }

    // ==================== 主渲染入口 ====================

    /**
     * 渲染图案与字体选择浮层。
     *
     * @param context 绘制上下文
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     */
    public static void render(DrawContext context, int mouseX, int mouseY) {
        if (!isVisible) return;

        YunbeiUCIntegration.clearHovered();
        // 每帧清空悬停 URL，防止残留
        clearLastHoveredUrl();

        int width = LayoutHelper.getScreenWidth();
        int height = LayoutHelper.getScreenHeight();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int mainWidth = width - UIConstants.SIDEBAR_WIDTH;

        int scrollWindowStartY = UIConstants.HEADER_HEIGHT + 1;
        int scrollWindowEndY = height - UIConstants.FOOTER_HEIGHT;
        int scrollWindowHeight = scrollWindowEndY - scrollWindowStartY;
        int sidebarScrollWindowHeight = height - UIConstants.HEADER_HEIGHT - UIConstants.FOOTER_HEIGHT;

        PatternRegistry.registerBuiltInPatterns();

        // 防御性自愈
        SidebarState.enforceSidebarMutualExclusion();

        updateScrollValues(textRenderer, mainWidth, sidebarScrollWindowHeight, scrollWindowHeight);
        handleScrollbarDragging(mouseX, mouseY, scrollWindowHeight, sidebarScrollWindowHeight);

        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, UIConstants.LAYER_Z_OFFSET);

        renderSidebar(context, textRenderer, mouseX, mouseY, sidebarScrollWindowHeight);
        renderMainArea(context, textRenderer, mouseX, mouseY, width, height, mainWidth, scrollWindowStartY, scrollWindowEndY);

        renderReturnButton(context, textRenderer, mouseX, mouseY, width, mainWidth, height);

        context.getMatrices().pop();
    }

    // 更新滚动位置最大值与钳位
    private static void updateScrollValues(TextRenderer textRenderer, int mainWidth, int sidebarScrollWindowHeight, int scrollWindowHeight) {
        // 侧边栏顶层固定 1 项（主页）
        int totalSidebarHeight = UIConstants.HEADER_HEIGHT + 12
                + LayoutHelper.getSidebarTopItemHeight(Text.translatable("yunbeiuc.gui.sidebar.home"), textRenderer);
        for (H2Category h2 : REGISTRY) {
            String h2Prefix = h2.isExpanded ? "[-] " : "[+] ";
            totalSidebarHeight += LayoutHelper.getCategoryHeight(h2.title, h2Prefix, 12, textRenderer);
            if (h2.isExpanded) {
                for (H3Category h3 : h2.subCategories) {
                    totalSidebarHeight += LayoutHelper.calculateH3Height(h3, 24, textRenderer);
                }
            }
        }
        maxSidebarScrollY = Math.max(0, totalSidebarHeight - sidebarScrollWindowHeight);
        sidebarScrollY = MathHelper.clamp(sidebarScrollY, 0, maxSidebarScrollY);

        int totalMainHeight = LayoutHelper.getTotalMainContentHeight(mainWidth, textRenderer);
        maxScrollY = Math.max(0, totalMainHeight - scrollWindowHeight);
        scrollY = MathHelper.clamp(scrollY, 0, maxScrollY);
    }

    // 处理滚动条拖动
    private static void handleScrollbarDragging(double mouseX, double mouseY, int scrollWindowHeight, int sidebarScrollWindowHeight) {
        if (isDraggingMainScrollbar && maxScrollY > 0) {
            float trackRatio = (float) scrollWindowHeight / (float) (scrollWindowHeight + maxScrollY);
            int thumbHeight = Math.max(UIConstants.SCROLLBAR_MIN_HEIGHT, (int) (scrollWindowHeight * trackRatio));
            int trackRange = scrollWindowHeight - thumbHeight;
            if (trackRange > 0) {
                double scrollDelta = ((mouseY - dragStartMouseY) / trackRange) * maxScrollY;
                scrollY = MathHelper.clamp(dragStartScrollY + scrollDelta, 0, maxScrollY);
            }
        }

        if (isDraggingSidebarScrollbar && maxSidebarScrollY > 0) {
            float trackRatio = (float) sidebarScrollWindowHeight / (float) (sidebarScrollWindowHeight + maxSidebarScrollY);
            int thumbHeight = Math.max(UIConstants.SCROLLBAR_MIN_HEIGHT, (int) (sidebarScrollWindowHeight * trackRatio));
            int trackRange = sidebarScrollWindowHeight - thumbHeight;
            if (trackRange > 0) {
                double scrollDelta = ((mouseY - dragStartMouseY) / trackRange) * maxSidebarScrollY;
                sidebarScrollY = MathHelper.clamp(dragStartSidebarScrollY + scrollDelta, 0, maxSidebarScrollY);
            }
        }
    }

    // 渲染侧边栏
    private static void renderSidebar(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY, int sidebarScrollWindowHeight) {
        context.fill(0, 0, UIConstants.SIDEBAR_WIDTH, LayoutHelper.getScreenHeight(), UIConstants.COLOR_SIDEBAR_BG);
        context.fill(0, 0, UIConstants.SIDEBAR_WIDTH, UIConstants.HEADER_HEIGHT, UIConstants.COLOR_SIDEBAR_HEADER);
        context.fill(0, UIConstants.HEADER_HEIGHT - 1, UIConstants.SIDEBAR_WIDTH, UIConstants.HEADER_HEIGHT, UIConstants.COLOR_SIDEBAR_BORDER);

        Text sidebarTitle = Text.translatable("yunbeiuc.gui.sidebar.title");
        int titleWidth = textRenderer.getWidth(sidebarTitle);
        context.drawText(textRenderer, sidebarTitle, (UIConstants.SIDEBAR_WIDTH - titleWidth) / 2, (UIConstants.HEADER_HEIGHT - 8) / 2, 0xFFFFFFFF, false);

        context.enableScissor(0, UIConstants.HEADER_HEIGHT, UIConstants.SIDEBAR_WIDTH, LayoutHelper.getScreenHeight() - UIConstants.HEADER_HEIGHT);

        int currentY = UIConstants.HEADER_HEIGHT + 12 - (int) sidebarScrollY;

        // 主页
        Text homeText = Text.translatable("yunbeiuc.gui.sidebar.home");
        currentY = renderSidebarTopItem(context, textRenderer, mouseX, mouseY, homeText, currentY, isHomeSelected);

        // H2 分类
        for (H2Category h2 : REGISTRY) {
            String prefix = h2.isExpanded ? "[-] " : "[+] ";
            int itemHeight = LayoutHelper.getCategoryHeight(h2.title, prefix, 12, textRenderer);
            boolean hoverH2 = LayoutHelper.isMouseInRect(mouseX, mouseY, 0, currentY, UIConstants.SIDEBAR_WIDTH, itemHeight);

            List<OrderedText> lines = textRenderer.wrapLines(Text.literal(prefix + h2.title.getString()), UIConstants.SIDEBAR_WIDTH - 20);
            int textY = currentY + (itemHeight - lines.size() * 10) / 2 + 1;

            for (int i = 0; i < lines.size(); i++) {
                context.drawText(textRenderer, lines.get(i), 12, textY + i * 10, hoverH2 ? 0xFFFFFFFF : UIConstants.COLOR_H2_TEXT, true);
            }
            currentY += itemHeight;

            if (h2.isExpanded) {
                for (H3Category h3 : h2.subCategories) {
                    currentY = LayoutHelper.renderH3CategoryDynamic(context, textRenderer, mouseX, mouseY, h3, 24, currentY);
                }
            }
        }

        context.disableScissor();

        LayoutHelper.renderScrollbar(context, 0, UIConstants.HEADER_HEIGHT, UIConstants.SIDEBAR_WIDTH,
                sidebarScrollWindowHeight, sidebarScrollY, maxSidebarScrollY, sidebarScrollWindowHeight, mouseX, mouseY);
    }

    // 渲染侧边栏顶层项
    private static int renderSidebarTopItem(DrawContext context, TextRenderer textRenderer,
                                           double mouseX, double mouseY,
                                           Text text, int currentY, boolean isSelected) {
        int maxWidth = UIConstants.SIDEBAR_WIDTH - 24;
        if (maxWidth < 20) maxWidth = 20;
        List<OrderedText> lines = textRenderer.wrapLines(text, maxWidth);
        int itemHeight = Math.max(UIConstants.DOC_LIST_ITEM_HEIGHT, lines.size() * 10 + 6);

        boolean isHover = LayoutHelper.isMouseInRect(mouseX, mouseY, 0, currentY, UIConstants.SIDEBAR_WIDTH, itemHeight);
        if (isSelected) {
            context.fill(0, currentY, UIConstants.SIDEBAR_WIDTH, currentY + itemHeight, UIConstants.COLOR_H3_BG_SELECTED);
        }
        int textColor = isSelected ? 0xFFFFFFFF : (isHover ? 0xFFFFFFFF : UIConstants.COLOR_H3_TEXT);
        int textY = currentY + (itemHeight - lines.size() * 10) / 2 + 1;
        for (int i = 0; i < lines.size(); i++) {
            context.drawText(textRenderer, lines.get(i), 12, textY + i * 10, textColor, false);
        }
        return currentY + itemHeight;
    }

    // 渲染主区域
    private static void renderMainArea(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                                       int width, int height, int mainWidth, int scrollWindowStartY, int scrollWindowEndY) {
        context.fill(UIConstants.SIDEBAR_WIDTH, 0, width, height, UIConstants.COLOR_MAIN_BG);
        context.fill(UIConstants.SIDEBAR_WIDTH, 0, width, UIConstants.HEADER_HEIGHT, UIConstants.COLOR_MAIN_HEADER);
        context.fill(UIConstants.SIDEBAR_WIDTH, UIConstants.HEADER_HEIGHT, width, UIConstants.HEADER_HEIGHT + 1, UIConstants.COLOR_MAIN_BORDER);

        Text currentTitle = isHomeSelected
                ? Text.translatable("yunbeiuc.gui.sidebar.home")
                : (selectedH3 != null ? selectedH3.title : Text.literal(""));
        int h2Width = textRenderer.getWidth(currentTitle);
        context.drawTextWithShadow(textRenderer, currentTitle, UIConstants.SIDEBAR_WIDTH + (mainWidth - h2Width) / 2, (UIConstants.HEADER_HEIGHT - 8) / 2, 0xFFFFFFFF);

        context.enableScissor(UIConstants.SIDEBAR_WIDTH, scrollWindowStartY, width, scrollWindowEndY);

        int contentStartY = scrollWindowStartY + 15 - (int) scrollY;

        if (isHomeSelected) {
            HomepageRenderer.render(context, textRenderer, mouseX, mouseY, mainWidth, contentStartY, scrollWindowStartY, scrollWindowEndY);
        } else if (selectedH3 != null) {
            if (isYunbeiucBuiltinSelected()) {
                YunbeiUCIntegration.render(context, textRenderer, width, height, mouseX, mouseY, UIConstants.SIDEBAR_WIDTH);
                context.disableScissor();
                return;
            }
            renderSectionContent(context, textRenderer, mouseX, mouseY, width, mainWidth, contentStartY, scrollWindowStartY, scrollWindowEndY);
        }

        context.disableScissor();

        LayoutHelper.renderScrollbar(context, UIConstants.SIDEBAR_WIDTH, scrollWindowStartY, width - UIConstants.SIDEBAR_WIDTH,
                scrollWindowEndY - scrollWindowStartY, scrollY, maxScrollY, scrollWindowEndY - scrollWindowStartY, mouseX, mouseY);
    }

    // 渲染分区内容
    private static void renderSectionContent(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                                             int width, int mainWidth, int contentStartY, int scrollWindowStartY, int scrollWindowEndY) {
        int currentContentY = contentStartY;
        H3Category h3 = selectedH3;

        if (h3.headerText != null) {
            renderHeaderText(context, textRenderer, h3.headerText, mainWidth, currentContentY);
            int lines = textRenderer.wrapLines(h3.headerText, mainWidth - 48).size();
            currentContentY += lines * 12 + 16 + 15;
        }

        // 字体渲染警告框（与 headerText 性质相同，只在最顶部出现一次）
        if (LayoutHelper.hasAnyFontSection(h3)) {
            int warningBoxTopY = currentContentY + 8;
            currentContentY = warningBoxTopY + renderWarningBox(context, textRenderer, mainWidth, warningBoxTopY,
                    scrollWindowStartY, scrollWindowEndY);
        }

        for (H4Section section : h3.sections) {
            H4Section effectiveSection = LayoutHelper.effective(section);

            context.drawText(textRenderer, effectiveSection.title, UIConstants.SIDEBAR_WIDTH + 24, currentContentY, UIConstants.COLOR_SECTION_TITLE, false);
            currentContentY += 12;

            context.drawText(textRenderer, effectiveSection.description, UIConstants.SIDEBAR_WIDTH + 24, currentContentY, UIConstants.COLOR_DESC_TEXT, false);
            List<OrderedText> descLines = textRenderer.wrapLines(effectiveSection.description, mainWidth - 48);
            currentContentY += descLines.size() * 12 + 10;

            // 子文件夹筛选按钮
            if (section.useSubfolders && !section.subFolders.isEmpty()) {
                renderFilterButtons(context, textRenderer, mouseX, mouseY, width, section.subFolders, section.activeTabIndex, currentContentY);
                currentContentY += 20;
            }

            // 样式切换按钮
            if (section.useStyles && !section.subSections.isEmpty()) {
                List<SubFolderDef> styleTabs = new ArrayList<>();
                for (H4Section child : section.subSections) {
                    styleTabs.add(new SubFolderDef(child.title.getString(), child.title));
                }
                renderFilterButtons(context, textRenderer, mouseX, mouseY, width, styleTabs, section.activeStyleIndex, currentContentY);
                currentContentY += 20;
            }

            if (!section.useSubfolders && !section.useStyles) {
                currentContentY += 20;
            }

            currentContentY = renderSectionItems(context, textRenderer, mouseX, mouseY, mainWidth, scrollWindowStartY, scrollWindowEndY, effectiveSection, currentContentY);
            currentContentY += 12;
        }
    }

    // 渲染头部说明文本
    private static void renderHeaderText(DrawContext context, TextRenderer textRenderer, Text headerText, int mainWidth, int currentY) {
        int descWidth = mainWidth - 48;
        List<OrderedText> wrappedLines = textRenderer.wrapLines(headerText, descWidth);
        int totalDescHeight = wrappedLines.size() * 12 + 16;

        context.fill(UIConstants.SIDEBAR_WIDTH + 20, currentY + 2, UIConstants.SIDEBAR_WIDTH + 28 + descWidth, currentY + 2 + totalDescHeight, UIConstants.COLOR_HEADER_BG_HELP);

        for (int i = 0; i < wrappedLines.size(); i++) {
            context.drawText(textRenderer, wrappedLines.get(i), UIConstants.SIDEBAR_WIDTH + 24, currentY + 10 + i * 12, UIConstants.COLOR_HEADER_TEXT, false);
        }
    }

    /**
     * 渲染字体渲染兼容性警告框。
     *
     * @param context 绘制上下文
     * @param textRenderer 文本渲染器
     * @param mainWidth 主区域宽度
     * @param currentY 当前 Y 坐标
     * @param scrollWindowStartY 滚动窗口起始 Y
     * @param scrollWindowEndY 滚动窗口结束 Y
     * @return 警告框总高度（含尾部间距）
     */
    private static int renderWarningBox(DrawContext context, TextRenderer textRenderer, int mainWidth, int currentY,
                                       int scrollWindowStartY, int scrollWindowEndY) {
        Text warningTitle = Text.translatable("yunbeiuc.gui.sections.font_rendering_warning.title");
        Text warningText = Text.translatable("yunbeiuc.gui.sections.font_rendering_warning");

        int boxX = UIConstants.SIDEBAR_WIDTH + 20;
        int boxWidth = mainWidth - 40;
        int paddingX = 8;
        int paddingY = 8;
        int titleHeight = 14;
        int lineHeight = 12;
        int gap = 4;

        List<OrderedText> textLines = textRenderer.wrapLines(warningText, boxWidth - paddingX * 2);
        int textHeight = textLines.size() * lineHeight;
        int totalBoxHeight = paddingY + titleHeight + gap + textHeight + paddingY;

        if (currentY + totalBoxHeight >= scrollWindowStartY && currentY <= scrollWindowEndY) {
            // 背景
            context.fill(boxX, currentY, boxX + boxWidth, currentY + totalBoxHeight, UIConstants.COLOR_HEADER_BG_WARNING);
            // 顶部边框
            context.fill(boxX, currentY, boxX + boxWidth, currentY + 1, UIConstants.COLOR_WARNING_BAR);
            // 底部边框
            context.fill(boxX, currentY + totalBoxHeight - 1, boxX + boxWidth, currentY + totalBoxHeight, UIConstants.COLOR_WARNING_BAR);
            // 右侧边框
            context.fill(boxX + boxWidth - 1, currentY, boxX + boxWidth, currentY + totalBoxHeight, UIConstants.COLOR_WARNING_BAR);
            // 左侧橙色竖线
            context.fill(boxX, currentY, boxX + 3, currentY + totalBoxHeight, UIConstants.COLOR_WARNING_BAR);

            // 标题
            context.drawText(textRenderer, warningTitle, boxX + paddingX + 8, currentY + paddingY, UIConstants.COLOR_WARNING_TEXT, false);

            // 内容
            int textY = currentY + paddingY + titleHeight + gap;
            for (int i = 0; i < textLines.size(); i++) {
                context.drawText(textRenderer, textLines.get(i), boxX + paddingX + 8, textY + i * lineHeight, UIConstants.COLOR_HEADER_TEXT, false);
            }
        }

        return totalBoxHeight + 8;
    }

    // 渲染筛选按钮
    private static void renderFilterButtons(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                                           int width, List<SubFolderDef> items, int activeIndex, int currentY) {
        int filterAreaWidth = items.size() * UIConstants.FILTER_BUTTON_WIDTH
                + (items.size() - 1) * UIConstants.FILTER_BUTTON_GAP;
        int filterStartX = width - 24 - filterAreaWidth;

        for (int i = 0; i < items.size(); i++) {
            SubFolderDef subDef = items.get(i);
            int bx = filterStartX + i * (UIConstants.FILTER_BUTTON_WIDTH + UIConstants.FILTER_BUTTON_GAP);

            if (currentY + UIConstants.FILTER_BUTTON_HEIGHT < UIConstants.HEADER_HEIGHT + 1 || currentY > LayoutHelper.getScreenHeight() - UIConstants.FOOTER_HEIGHT) continue;

            boolean isFltHover = LayoutHelper.isMouseInRect(mouseX, mouseY, bx, currentY, UIConstants.FILTER_BUTTON_WIDTH, UIConstants.FILTER_BUTTON_HEIGHT);
            boolean isActive = (activeIndex == i);

            int bgColor = isActive ? 0xFFAAAAAA : (isFltHover ? UIConstants.COLOR_BTN_BG_HOVER : UIConstants.COLOR_BTN_BG);
            int borderColor = isActive ? 0xFF000000 : UIConstants.COLOR_BTN_BORDER;

            context.fill(bx, currentY, bx + UIConstants.FILTER_BUTTON_WIDTH, currentY + UIConstants.FILTER_BUTTON_HEIGHT, bgColor);
            context.drawBorder(bx, currentY, UIConstants.FILTER_BUTTON_WIDTH, UIConstants.FILTER_BUTTON_HEIGHT, borderColor);

            if (isActive) {
                context.fill(bx, currentY + UIConstants.FILTER_BUTTON_HEIGHT - 2,
                        bx + UIConstants.FILTER_BUTTON_WIDTH, currentY + UIConstants.FILTER_BUTTON_HEIGHT,
                        0xFF00AAFF);
            }

            String tabName = subDef.displayName.getString();
            int textColor = isActive ? 0xFF000000 : UIConstants.COLOR_BTN_TEXT;
            List<OrderedText> lines = textRenderer.wrapLines(Text.literal(tabName), UIConstants.FILTER_BUTTON_WIDTH - 4);
            int totalTextHeight = lines.size() * textRenderer.fontHeight;
            int textStartY = currentY + (UIConstants.FILTER_BUTTON_HEIGHT - totalTextHeight) / 2;

            for (int lineIdx = 0; lineIdx < lines.size(); lineIdx++) {
                OrderedText line = lines.get(lineIdx);
                int lineWidth = textRenderer.getWidth(line);
                int textX = bx + (UIConstants.FILTER_BUTTON_WIDTH - lineWidth) / 2;
                context.drawText(textRenderer, line, textX, textStartY + lineIdx * textRenderer.fontHeight, textColor, false);
            }
        }
    }

    // 渲染分区条目
    private static int renderSectionItems(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                                         int mainWidth, int scrollWindowStartY, int scrollWindowEndY,
                                         H4Section section, int currentY) {
        if (section.isFontMode) {
            return currentY + GridRenderer.renderFontList(context, textRenderer, mouseX, mouseY, mainWidth, scrollWindowStartY, scrollWindowEndY,
                    section.fontItems, UIConstants.SIDEBAR_WIDTH + 30, currentY);
        } else if (section.isWhitelistMode) {
            return currentY + GridRenderer.renderWhitelistGrid(context, textRenderer, mouseX, mouseY, mainWidth, scrollWindowStartY, scrollWindowEndY,
                    section.whitelistItems, UIConstants.SIDEBAR_WIDTH + 24, currentY);
        } else {
            String tabKey = section.useSubfolders && !section.subFolders.isEmpty()
                    ? section.subFolders.get(section.activeTabIndex).dirName : "root";
            var textures = section.cachedTextures.get(tabKey);
            return currentY + GridRenderer.renderCachedTextureGrid(context, textRenderer, mouseX, mouseY, mainWidth, scrollWindowStartY, scrollWindowEndY,
                    textures != null ? textures : List.of(), UIConstants.SIDEBAR_WIDTH + 24, currentY);
        }
    }

    // 渲染返回按钮
    private static void renderReturnButton(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                                          int width, int mainWidth, int height) {
        int returnBtnX = UIConstants.SIDEBAR_WIDTH + (mainWidth - UIConstants.RETURN_BUTTON_WIDTH) / 2;
        int returnBtnY = height - 35;
        boolean hoverRet = LayoutHelper.isMouseInRect(mouseX, mouseY, returnBtnX, returnBtnY, UIConstants.RETURN_BUTTON_WIDTH, UIConstants.RETURN_BUTTON_HEIGHT);

        int bgColor = hoverRet ? UIConstants.COLOR_BTN_BG_HOVER : UIConstants.COLOR_BTN_BG;
        context.fill(returnBtnX, returnBtnY, returnBtnX + UIConstants.RETURN_BUTTON_WIDTH, returnBtnY + UIConstants.RETURN_BUTTON_HEIGHT, bgColor);
        context.drawBorder(returnBtnX, returnBtnY, UIConstants.RETURN_BUTTON_WIDTH, UIConstants.RETURN_BUTTON_HEIGHT, UIConstants.COLOR_BTN_BORDER);

        Text returnText = Text.translatable("yunbeiuc.gui.button.back");
        int rtw = textRenderer.getWidth(returnText);
        context.drawText(textRenderer, returnText, returnBtnX + (UIConstants.RETURN_BUTTON_WIDTH - rtw) / 2, returnBtnY + 7, UIConstants.COLOR_BTN_TEXT, false);
    }

    // ==================== 插入代理 ====================

    /**
     * 打开浮层时记录的目标编辑界面。
     *
     * <p>浮层直接叠加在 {@link TextDisplayScreen} 上时当前屏幕即为目标；
     * 由 {@link PatternAndFontBlankScreen} 承载浮层时用本字段把内容写回原编辑界面。
     */
    public static TextDisplayScreen targetScreen = null;

    /**
     * 将内容写入当前正在编辑的文本行，并关闭浮层。
     *
     * <p>找不到目标编辑界面时仅关闭浮层，不写入。
     *
     * @param text 要写入的文本（可为 -texture / -rect / -json 指令）
     */
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

    /**
     * 鼠标点击事件处理。
     *
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     * @param button 鼠标按钮
     * @return 是否处理了事件
     */
    public static boolean mouseClicked(double mouseX, double mouseY, int button) {
        return MouseEventHandler.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 鼠标滚轮事件处理。
     *
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     * @param amount 滚动量
     */
    public static void mouseScrolled(double mouseX, double mouseY, double amount) {
        MouseEventHandler.mouseScrolled(mouseX, mouseY, amount);
    }

    /**
     * 鼠标释放事件处理。
     *
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     * @param button 鼠标按钮
     * @return 是否处理了事件
     */
    public static boolean mouseReleased(double mouseX, double mouseY, int button) {
        return MouseEventHandler.mouseReleased(mouseX, mouseY, button);
    }

    // ==================== URL 悬停状态 ====================

    /** 上一次悬停的 URL。 */
    private static String lastHoveredUrl = null;

    /**
     * 获取上一次悬停的 URL。
     *
     * @return 悬停的 URL
     */
    public static String getLastHoveredUrl() {
        return lastHoveredUrl;
    }

    /**
     * 设置上一次悬停的 URL。
     *
     * @param url 悬停的 URL
     */
    public static void setLastHoveredUrl(String url) {
        lastHoveredUrl = url;
    }

    /**
     * 清除上一次悬停的 URL。
     */
    public static void clearLastHoveredUrl() {
        lastHoveredUrl = null;
    }

    /**
     * 打开主页链接。
     *
     * @param url 链接地址
     * @return 是否成功打开
     */
    public static boolean openHomepageLink(String url) {
        if (url != null && !url.isEmpty()) {
            Util.getOperatingSystem().open(url);
            return true;
        }
        return false;
    }
}
