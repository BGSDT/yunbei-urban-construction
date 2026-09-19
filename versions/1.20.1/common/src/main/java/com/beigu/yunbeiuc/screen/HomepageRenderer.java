package com.beigu.yunbeiuc.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * 主页（图案与字体选择界面首页）渲染逻辑
 *
 * @see PatternAndFontOverlay
 */
public final class HomepageRenderer {

    /** 主页头图资源 ID。资源不存在时不绘制头图（高度按 0 计算），避免缺失纹理报错。 */
    private static final Identifier HEADER_IMAGE = new Identifier("yunbeiuc", "textures/image/bg1.png");
    /** 头图真实宽高比（高/宽），首次读取后缓存。 */
    private static float cachedHeaderAspectRatio = -1f;
    /** 头图是否存在，首次读取后缓存。 */
    private static boolean headerImageExists = false;
    /** 头图是否已检查过。 */
    private static boolean headerImageChecked = false;

    private HomepageRenderer() {
    }

    /**
     * 渲染主页完整内容。
     *
     * @param context 绘制上下文
     * @param textRenderer 文本渲染器
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     * @param mainWidth 主区域宽度
     * @param contentStartY 内容起始 Y 坐标
     * @param scrollWindowStartY 滚动窗口起始 Y 坐标
     * @param scrollWindowEndY 滚动窗口结束 Y 坐标
     * @return 内容总高度（像素）
     */
    public static int render(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                              int mainWidth, int contentStartY, int scrollWindowStartY, int scrollWindowEndY) {
        int currentY = contentStartY;

        // 绘制头图
        int headerImageHeight = (int) (mainWidth * getHeaderAspectRatio());
        if (headerImageHeight > 0) {
            int headerImageX = UIConstants.SIDEBAR_WIDTH;
            int headerImageY = currentY;
            if (headerImageY + headerImageHeight >= scrollWindowStartY && headerImageY <= scrollWindowEndY) {
                context.drawTexture(HEADER_IMAGE, headerImageX, headerImageY, 0, 0, mainWidth, headerImageHeight, mainWidth, headerImageHeight);
            }
            currentY += headerImageHeight + 10;
        }

        // 主标题
        Text title = Text.translatable("yunbeiuc.gui.homepage.title");
        int titleWidth = textRenderer.getWidth(title);
        context.drawText(textRenderer, title, UIConstants.SIDEBAR_WIDTH + (mainWidth - titleWidth) / 2, currentY, 0x0066CC, false);
        currentY += 26;

        // 标题下划线
        int dividerY = currentY;
        context.fill(UIConstants.SIDEBAR_WIDTH + 40, dividerY, LayoutHelper.getScreenWidth() - 40, dividerY + 2, UIConstants.COLOR_HOMEPAGE_DIVIDER);
        currentY += 20;

        // 副标题
        Text welcome = Text.translatable("yunbeiuc.gui.homepage.welcome");
        int welcomeWidth = textRenderer.getWidth(welcome);
        context.drawText(textRenderer, welcome, UIConstants.SIDEBAR_WIDTH + (mainWidth - welcomeWidth) / 2, currentY, UIConstants.COLOR_HOMEPAGE_SUBTITLE, false);
        currentY += 28;

        // 提示文字
        currentY = renderHintText(context, textRenderer, mainWidth, currentY, scrollWindowStartY, scrollWindowEndY);

        // Introduction 章节
        currentY += 10;
        currentY = renderIntroSection(context, textRenderer, mainWidth, currentY, scrollWindowStartY, scrollWindowEndY);

        // Project Links 章节
        currentY = renderLinksSection(context, textRenderer, mouseX, mouseY, mainWidth, currentY, scrollWindowStartY, scrollWindowEndY);

        // 免责声明
        currentY = renderDisclaimerSection(context, textRenderer, mainWidth, currentY, scrollWindowStartY, scrollWindowEndY);

        currentY += 20;
        return currentY;
    }

    /**
     * 获取主页内容总高度，用于滚动计算（与 render 一一对应）。
     *
     * @param mainWidth 主区域宽度
     * @param textRenderer 文本渲染器
     * @return 内容总高度（像素）
     */
    public static int getContentHeight(int mainWidth, TextRenderer textRenderer) {
        int height = 0;

        // 头图
        int headerImageHeight = (int) (mainWidth * getHeaderAspectRatio());
        if (headerImageHeight > 0) {
            height += headerImageHeight + 10;
        }

        // 主标题
        height += 26;

        // 标题下划线
        height += 20;

        // 副标题
        height += 28;

        // 提示文字块
        Text hintText = Text.translatable("yunbeiuc.gui.homepage.hint");
        List<OrderedText> hintLines = textRenderer.wrapLines(hintText, mainWidth - 40);
        int hintBgHeight = hintLines.size() * 12 + 10;
        height += hintBgHeight + 12;

        // Introduction 章节
        height += 10;
        height += 22; // 章节标题

        int introLineHeight = 15;
        int introParaGap = 20;
        String[] introKeys = {
                "yunbeiuc.gui.homepage.intro.p1",
                "yunbeiuc.gui.homepage.intro.p2",
                "yunbeiuc.gui.homepage.intro.p3"
        };
        for (int i = 0; i < introKeys.length; i++) {
            Text t = Text.translatable(introKeys[i]);
            List<OrderedText> lines = textRenderer.wrapLines(t, mainWidth - 40);
            height += lines.size() * introLineHeight;
            if (i < introKeys.length - 1) {
                height += introParaGap;
            }
        }
        height += 45; // 末段后的尾部间距

        // Project Links 章节
        height += 20; // 分割线间距
        height += 5;  // 分割线到标题间距
        height += 22; // 章节标题

        // 三列卡片
        int columnWidth = (mainWidth - 60) / 3;
        int padding = 12;
        int fixedCardHeight1 = calculateCardHeight(textRenderer, columnWidth, padding,
                "https://github.com/BGSDT/yunbei-urban-construction",
                "https://bgsdt.github.io");
        int fixedCardHeight2 = calculateCardHeight(textRenderer, columnWidth, padding,
                "https://github.com/BGSDT/ocelotsignyunbei",
                "https://bgsdt.github.io");
        int fixedCardHeight3 = calculateCardHeight(textRenderer, columnWidth, padding,
                "https://github.com/Creeper-Cola123/ocelotsign-minecraft",
                "https://creeper-cola123.github.io/OcelotSignMod_Docs/");
        int fixedCardHeight = Math.max(Math.max(fixedCardHeight1, fixedCardHeight2), fixedCardHeight3);
        height += fixedCardHeight;
        height += 25; // 卡片底部间距

        // 免责声明
        height += 10;
        height += 22; // 章节标题
        Text disclaimerP1 = Text.translatable("yunbeiuc.gui.homepage.disclaimer.p1");
        List<OrderedText> disclaimerLines = textRenderer.wrapLines(disclaimerP1, mainWidth - 40);
        height += disclaimerLines.size() * 15;
        height += 20; // 段后间距
        height += 20; // renderDisclaimerSection 返回尾部
        height += 20; // render 末尾间距

        return height;
    }

    /**
     * 渲染提示文本区域。
     */
    private static int renderHintText(DrawContext context, TextRenderer textRenderer,
                                      int mainWidth, int currentY, int scrollWindowStartY, int scrollWindowEndY) {
        Text hintText = Text.translatable("yunbeiuc.gui.homepage.hint");
        int hintPaddingY = 5;
        int hintX = UIConstants.SIDEBAR_WIDTH + 20;
        int hintMaxWidth = mainWidth - 40;
        List<OrderedText> hintLines = textRenderer.wrapLines(hintText, hintMaxWidth);
        int hintBgHeight = hintLines.size() * 12 + hintPaddingY * 2;
        if (currentY + hintBgHeight >= scrollWindowStartY && currentY <= scrollWindowEndY) {
            context.fill(hintX, currentY, hintX + hintMaxWidth, currentY + hintBgHeight, UIConstants.COLOR_HEADER_BG_HELP);
            int textY = currentY + hintPaddingY + (hintBgHeight - hintLines.size() * 12) / 2;
            for (int i = 0; i < hintLines.size(); i++) {
                int lineW = textRenderer.getWidth(hintLines.get(i));
                context.drawText(textRenderer, hintLines.get(i), hintX + (hintMaxWidth - lineW) / 2, textY + i * 12, UIConstants.COLOR_HEADER_TEXT, false);
            }
        }
        return currentY + hintBgHeight + 12;
    }

    /**
     * 渲染介绍章节。
     */
    private static int renderIntroSection(DrawContext context, TextRenderer textRenderer,
                                          int mainWidth, int currentY, int scrollWindowStartY, int scrollWindowEndY) {
        Text introTitle = Text.translatable("yunbeiuc.gui.homepage.section.intro");
        context.drawText(textRenderer, introTitle, UIConstants.SIDEBAR_WIDTH + 20, currentY, UIConstants.COLOR_HOMEPAGE_SECTION_TITLE, false);
        currentY += 22;

        int introX = UIConstants.SIDEBAR_WIDTH + 20;
        int introMaxWidth = mainWidth - 40;
        int introLineHeight = 15;
        int introParaGap = 20;

        Text introP1 = Text.translatable("yunbeiuc.gui.homepage.intro.p1");
        currentY = renderTextBlock(context, textRenderer, introP1, introX, introMaxWidth, currentY, scrollWindowStartY, scrollWindowEndY, UIConstants.COLOR_HOMEPAGE_BODY, introLineHeight);
        currentY += introParaGap;

        Text introP2 = Text.translatable("yunbeiuc.gui.homepage.intro.p2");
        currentY = renderTextBlock(context, textRenderer, introP2, introX, introMaxWidth, currentY, scrollWindowStartY, scrollWindowEndY, UIConstants.COLOR_HOMEPAGE_BODY, introLineHeight);
        currentY += introParaGap;

        Text introP3 = Text.translatable("yunbeiuc.gui.homepage.intro.p3");
        currentY = renderTextBlock(context, textRenderer, introP3, introX, introMaxWidth, currentY, scrollWindowStartY, scrollWindowEndY, UIConstants.COLOR_HOMEPAGE_BODY, introLineHeight);

        currentY += 45;
        return currentY;
    }

    private static int renderLinksSection(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                                          int mainWidth, int currentY, int scrollWindowStartY, int scrollWindowEndY) {
        // 分割线
        int sectionDividerY = currentY;
        context.fill(UIConstants.SIDEBAR_WIDTH + 40, sectionDividerY, LayoutHelper.getScreenWidth() - 40, sectionDividerY + 1, UIConstants.COLOR_HOMEPAGE_DIVIDER);
        currentY += 20;

        // 章节标题
        currentY += 5;
        Text linksTitle = Text.translatable("yunbeiuc.gui.homepage.section.links");
        context.drawText(textRenderer, linksTitle, UIConstants.SIDEBAR_WIDTH + 20, currentY, UIConstants.COLOR_HOMEPAGE_SECTION_TITLE, false);
        currentY += 22;

        // 三列卡片布局
        int columnWidth = (mainWidth - 60) / 3;
        int leftColumnX = UIConstants.SIDEBAR_WIDTH + 20;
        int centreColumnX = leftColumnX + columnWidth + 10;
        int rightColumnX = centreColumnX + columnWidth + 10;
        int cardStartY = currentY;

        int padding = 12;

        // 计算三张卡片的最大高度，确保统一
        int fixedCardHeight1 = calculateCardHeight(textRenderer, columnWidth, padding,
                "https://github.com/BGSDT/yunbei-urban-construction",
                "https://bgsdt.github.io");

        int fixedCardHeight2 = calculateCardHeight(textRenderer, columnWidth, padding,
                "https://github.com/BGSDT/ocelotsignyunbei",
                "https://bgsdt.github.io");

        int fixedCardHeight3 = calculateCardHeight(textRenderer, columnWidth, padding,
                "https://github.com/Creeper-Cola123/ocelotsign-minecraft",
                "https://creeper-cola123.github.io/OcelotSignMod_Docs/");

        int fixedCardHeight = Math.max(Math.max(fixedCardHeight1, fixedCardHeight2), fixedCardHeight3);

        int leftCardEndY = renderCard(context, textRenderer, mouseX, mouseY,
                leftColumnX, cardStartY, columnWidth,
                Text.translatable("yunbeiuc.gui.homepage.yunbeiuc.title"),
                "https://github.com/BGSDT/yunbei-urban-construction",
                "https://bgsdt.github.io",
                scrollWindowStartY, scrollWindowEndY, fixedCardHeight);

        int centreCardEndY = renderCard(context, textRenderer, mouseX, mouseY,
                centreColumnX, cardStartY, columnWidth,
                Text.translatable("yunbeiuc.gui.homepage.ocelotsignyunbei.title"),
                "https://github.com/BGSDT/ocelotsignyunbei",
                "https://bgsdt.github.io",
                scrollWindowStartY, scrollWindowEndY, fixedCardHeight);

        int rightCardEndY = renderCard(context, textRenderer, mouseX, mouseY,
                rightColumnX, cardStartY, columnWidth,
                Text.translatable("yunbeiuc.gui.homepage.ocelotsign.title"),
                "https://github.com/Creeper-Cola123/ocelotsign-minecraft",
                "https://creeper-cola123.github.io/OcelotSignMod_Docs/",
                scrollWindowStartY, scrollWindowEndY, fixedCardHeight);

        return Math.max(Math.max(leftCardEndY, centreCardEndY), rightCardEndY) + 25;
    }

    /**
     * 渲染免责声明章节。
     */
    private static int renderDisclaimerSection(DrawContext context, TextRenderer textRenderer,
                                               int mainWidth, int currentY, int scrollWindowStartY, int scrollWindowEndY) {
        currentY += 10;
        Text disclaimerTitle = Text.translatable("yunbeiuc.gui.homepage.disclaimer.title");
        context.drawText(textRenderer, disclaimerTitle, UIConstants.SIDEBAR_WIDTH + 20, currentY, UIConstants.COLOR_HOMEPAGE_SECTION_TITLE, false);
        currentY += 22;

        int disclaimerX = UIConstants.SIDEBAR_WIDTH + 20;
        int disclaimerMaxWidth = mainWidth - 40;
        int disclaimerLineHeight = 15;
        int disclaimerParaGap = 20;

        Text disclaimerP1 = Text.translatable("yunbeiuc.gui.homepage.disclaimer.p1");
        currentY = renderTextBlock(context, textRenderer, disclaimerP1, disclaimerX, disclaimerMaxWidth, currentY, scrollWindowStartY, scrollWindowEndY, UIConstants.COLOR_HOMEPAGE_BODY, disclaimerLineHeight);
        currentY += disclaimerParaGap;

        return currentY + 20;
    }

    /**
     * 渲染文本块（支持多段落换行）。
     */
    private static int renderTextBlock(DrawContext context, TextRenderer textRenderer, Text text,
                                       int x, int maxWidth, int currentY,
                                       int scrollWindowStartY, int scrollWindowEndY, int textColor, int lineHeight) {
        String rawText = text.getString();
        String[] paragraphs = rawText.split("\n\n");
        for (int p = 0; p < paragraphs.length; p++) {
            String paraText = paragraphs[p];
            Text para = Text.literal(paraText);
            List<OrderedText> lines = textRenderer.wrapLines(para, maxWidth);
            for (int i = 0; i < lines.size(); i++) {
                if (currentY + lineHeight >= scrollWindowStartY && currentY <= scrollWindowEndY) {
                    context.drawText(textRenderer, lines.get(i), x, currentY, textColor, false);
                }
                currentY += lineHeight;
            }
            if (p < paragraphs.length - 1) {
                currentY += lineHeight;
                if (currentY >= scrollWindowStartY && currentY - lineHeight <= scrollWindowEndY) {
                    context.drawText(textRenderer, Text.literal(" "), x, currentY - lineHeight, textColor, false);
                }
            }
        }
        return currentY;
    }

    /**
     * 计算链接卡片的高度。
     */
    private static int calculateCardHeight(TextRenderer textRenderer, int width, int padding,
                                            String repoUrl, String docUrl) {
        int titleHeight = 14;
        Text repoName = getShortLinkText(repoUrl);
        Text docName = getShortLinkText(docUrl);
        int repoLines = Math.max(1, textRenderer.wrapLines(repoName, width - padding * 2 - 16).size());
        int docLines = Math.max(1, textRenderer.wrapLines(docName, width - padding * 2 - 16).size());
        int repoBtnHeight = repoLines * 10 + 8;
        int docBtnHeight = docLines * 10 + 8;
        int linkAreaHeight = repoBtnHeight + docBtnHeight + 12;
        return padding + titleHeight + 12 + linkAreaHeight + padding;
    }

    /**
     * 渲染单个链接卡片。
     */
    private static int renderCard(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                                   int x, int currentY, int width,
                                   Text teamTitle, String repoUrl, String docUrl,
                                   int scrollWindowStartY, int scrollWindowEndY, int fixedCardHeight) {
        int padding = 12;
        int cardHeight = fixedCardHeight;

        if (currentY + cardHeight >= scrollWindowStartY && currentY <= scrollWindowEndY) {
            context.fill(x, currentY, x + width, currentY + cardHeight, UIConstants.COLOR_HOMEPAGE_CARD_BG);
            context.fill(x, currentY, x + 3, currentY + cardHeight, UIConstants.COLOR_HOMEPAGE_CARD_TITLE);
            context.fill(x, currentY + cardHeight - 1, x + width, currentY + cardHeight, UIConstants.COLOR_HOMEPAGE_CARD_BORDER);
            context.fill(x, currentY, x + width, currentY + 1, UIConstants.COLOR_HOMEPAGE_CARD_BORDER);
            context.fill(x + width - 1, currentY, x + width, currentY + cardHeight, UIConstants.COLOR_HOMEPAGE_CARD_BORDER);
        }

        int cardY = currentY + padding;

        if (cardY + 14 >= scrollWindowStartY && cardY <= scrollWindowEndY) {
            context.drawText(textRenderer, teamTitle, x + padding, cardY, UIConstants.COLOR_HOMEPAGE_CARD_TITLE, false);
        }
        cardY += 14;
        cardY += 12;

        int btnMaxWidth = width - padding * 2;

        cardY = renderLinkButton(context, textRenderer, mouseX, mouseY, x + padding, cardY, btnMaxWidth, repoUrl, scrollWindowStartY, scrollWindowEndY);
        cardY = renderLinkButton(context, textRenderer, mouseX, mouseY, x + padding, cardY, btnMaxWidth, docUrl, scrollWindowStartY, scrollWindowEndY);

        return currentY + cardHeight;
    }

    /**
     * 渲染链接按钮。
     */
    private static int renderLinkButton(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY,
                                         int cardX, int currentY, int cardWidth, String url,
                                         int scrollWindowStartY, int scrollWindowEndY) {
        Text linkText = getShortLinkText(url);
        int btnWidth = cardWidth;
        int lineHeight = 10;

        List<OrderedText> wrappedLines = textRenderer.wrapLines(linkText, btnWidth - 16);
        int actualBtnHeight = wrappedLines.size() * lineHeight + 8;

        int textWidth = textRenderer.getWidth(wrappedLines.get(0));
        int textX = cardX + (btnWidth - textWidth) / 2;
        int textY = currentY + (actualBtnHeight - wrappedLines.size() * lineHeight) / 2;

        boolean isVisible = currentY + actualBtnHeight >= scrollWindowStartY && currentY <= scrollWindowEndY;
        boolean isHover = isVisible && LayoutHelper.isMouseInRect(mouseX, mouseY, cardX, currentY, btnWidth, actualBtnHeight);

        if (isVisible) {
            int bgColor = isHover ? UIConstants.COLOR_BTN_BG_HOVER : UIConstants.COLOR_BTN_BG;
            context.fill(cardX, currentY, cardX + btnWidth, currentY + actualBtnHeight, bgColor);
            context.drawBorder(cardX, currentY, btnWidth, actualBtnHeight, UIConstants.COLOR_BTN_BORDER);

            int textColor = isHover ? 0xFF004499 : UIConstants.COLOR_LINK_NORMAL;
            int yOffset = 0;
            for (OrderedText line : wrappedLines) {
                int lineW = textRenderer.getWidth(line);
                context.drawText(textRenderer, line, cardX + (btnWidth - lineW) / 2, textY + yOffset, textColor, false);
                yOffset += lineHeight;
            }
        }

        if (isHover) {
            PatternAndFontOverlay.setLastHoveredUrl(url);
        }

        return currentY + actualBtnHeight + 6;
    }

    /**
     * 获取 URL 的短显示文本。
     *
     * <p>对已知域名返回国际化标签，其他 URL 截取路径部分并省略过长内容。
     *
     * @param url 原始 URL
     * @return 简化后的显示文本
     */
    public static Text getShortLinkText(String url) {
        if (url == null || url.isEmpty()) {
            return Text.empty();
        }
        if (url.contains("github.com")) {
            return Text.translatable("yunbeiuc.gui.homepage.repo.label");
        }
        if (url.contains("github.io")) {
            return Text.translatable("yunbeiuc.gui.homepage.doc.label");
        }
        try {
            String path = url.substring(url.indexOf("/", 8));
            if (path.length() > 25) {
                String[] parts = path.split("/");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < Math.min(3, parts.length); i++) {
                    if (parts[i].length() > 0) {
                        if (sb.length() > 0) sb.append("/");
                        sb.append(parts[i]);
                    }
                }
                if (sb.length() > 20) {
                    return Text.literal(sb.substring(0, 17) + "...");
                }
                return Text.literal(sb.toString());
            }
            return Text.literal(path);
        } catch (Exception e) {
            return Text.translatable("yunbeiuc.gui.homepage.doc.label");
        }
    }

    /**
     * 使用系统默认浏览器打开 URL。
     *
     * @param url 要打开的 URL
     * @return 若成功打开返回 {@code true}
     */
    public static boolean openUrl(String url) {
        if (url != null && !url.isEmpty()) {
            Util.getOperatingSystem().open(url);
            return true;
        }
        return false;
    }

    /**
     * 获取头图真实宽高比（高/宽），按图片本身比例显示，避免固定比例导致的拉伸变形。
     * <p>资源不存在时返回 0（不绘制头图）。
     *
     * @return 头图高度 / 宽度；资源缺失时为 0
     */
    private static float getHeaderAspectRatio() {
        if (!headerImageChecked) {
            headerImageChecked = true;
            try {
                Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(HEADER_IMAGE);
                if (resource.isPresent()) {
                    try (InputStream in = resource.get().getInputStream()) {
                        try (NativeImage image = NativeImage.read(in)) {
                            if (image.getWidth() > 0) {
                                cachedHeaderAspectRatio = (float) image.getHeight() / (float) image.getWidth();
                                headerImageExists = true;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return headerImageExists && cachedHeaderAspectRatio > 0 ? cachedHeaderAspectRatio : 0f;
    }
}
