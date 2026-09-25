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

public final class HomepageRenderer {
    private static final Identifier HEADER_TEX = new Identifier("yunbeiuc", "textures/gui/header.png");
    private static float cachedAspect = -1f;
    private static boolean texExists = false;
    private static boolean texChecked = false;

    private HomepageRenderer() {
    }

    public static int render(DrawContext ctx, TextRenderer tr, int mx, int my,
                              int paneW, int startY, int clipTop, int clipBottom) {
        int y = startY;
        int navW = SidebarState.getEffectiveWidth();
        int pad = 16;
        int contentW = paneW - pad * 2;
        int contentX = navW + pad;

        int hdrH = (int) (paneW * getAspect());
        if (hdrH > 0) {
            if (y + hdrH >= clipTop && y <= clipBottom) {
                ctx.drawTexture(HEADER_TEX, navW, y, 0, 0, paneW, hdrH, paneW, hdrH);
            }
            y += hdrH + 6;
        }

        y = drawTitleBar(ctx, tr, contentX, y, contentW, clipTop, clipBottom);
        y += 6;

        y = drawIntroSection(ctx, tr, contentX, y, contentW, clipTop, clipBottom);
        y += 6;

        y = drawProjectCard(ctx, tr, mx, my, contentX, y, contentW, clipTop, clipBottom);

        return y + 20;
    }

    public static int getContentHeight(int paneW, TextRenderer tr) {
        int h = 0;
        int pad = 16;
        int contentW = paneW - pad * 2;

        int hdrH = (int) (paneW * getAspect());
        if (hdrH > 0) h += hdrH + 6;

        h += 40;
        h += 6;

        h += 18;
        String[] introKeys = {
                "yunbeiuc.gui.homepage.intro.p1",
                "yunbeiuc.gui.homepage.intro.p2",
                "yunbeiuc.gui.homepage.intro.p3"
        };
        for (String key : introKeys) {
            Text t = Text.translatable(key);
            List<OrderedText> lines = tr.wrapLines(t, contentW - 16);
            h += lines.size() * 13 + 6;
        }
        h += 8;

        h += 18 + 4 + 36 + 8;

        return h;
    }

    private static int drawTitleBar(DrawContext ctx, TextRenderer tr, int x, int y, int w, int clipTop, int clipBottom) {
        int panelH = 40;
        if (y + panelH < clipTop || y > clipBottom) return y + panelH;

        ctx.fill(x, y, x + w, y + panelH, UIConstants.CLR_HOME_CARD_BG);
        ctx.drawBorder(x, y, w, panelH, UIConstants.CLR_HOME_CARD_STROKE);
        ctx.fill(x, y, x + 4, y + panelH, UIConstants.CLR_ACCENT);

        Text title = Text.translatable("yunbeiuc.gui.homepage.title");
        ctx.drawText(tr, title, x + 14, y + 8, UIConstants.CLR_HEADING, false);

        Text subtitle = Text.translatable("yunbeiuc.gui.homepage.welcome");
        ctx.drawText(tr, subtitle, x + 14, y + 24, UIConstants.CLR_HOME_DESC, false);

        return y + panelH;
    }

    private static int drawIntroSection(DrawContext ctx, TextRenderer tr, int x, int y, int w, int clipTop, int clipBottom) {
        Text sectionTitle = Text.translatable("yunbeiuc.gui.homepage.section.intro");
        if (y + 18 >= clipTop && y <= clipBottom) {
            ctx.drawText(tr, sectionTitle, x, y, UIConstants.CLR_HEADING, false);
        }
        y += 18;

        String[] introKeys = {
                "yunbeiuc.gui.homepage.intro.p1",
                "yunbeiuc.gui.homepage.intro.p2",
                "yunbeiuc.gui.homepage.intro.p3"
        };

        for (String key : introKeys) {
            Text t = Text.translatable(key);
            List<OrderedText> lines = tr.wrapLines(t, w - 16);
            for (OrderedText line : lines) {
                if (y + 13 >= clipTop && y <= clipBottom) {
                    ctx.drawText(tr, line, x + 8, y, UIConstants.CLR_HOME_BODY, false);
                }
                y += 13;
            }
            y += 6;
        }

        return y + 4;
    }

    private static int drawProjectCard(DrawContext ctx, TextRenderer tr, int mx, int my,
                                        int x, int y, int w, int clipTop, int clipBottom) {
        int cardH = 36;
        if (y + cardH < clipTop || y > clipBottom) return y + cardH;

        // 卡片本身不是按钮：整卡不随鼠标悬停变化，仅卡片内的链接有交互反馈
        ctx.fill(x, y, x + w, y + cardH, UIConstants.CLR_HOME_CARD_BG);
        ctx.drawBorder(x, y, w, cardH, UIConstants.CLR_HOME_CARD_STROKE);
        ctx.fill(x, y, x + 4, y + cardH, UIConstants.CLR_ACCENT);

        Text cardTitle = Text.translatable("yunbeiuc.gui.homepage.yunbeiuc.title");
        ctx.drawText(tr, cardTitle, x + 14, y + 4, UIConstants.CLR_HEADING, false);

        Text repoLink = Text.translatable("yunbeiuc.gui.homepage.repo.label");
        int linkY = y + 18;
        int linkW = w - 28;
        List<OrderedText> linkLines = tr.wrapLines(repoLink, linkW - 8);
        int linkH = linkLines.size() * 10 + 8;

        boolean linkHov = LayoutHelper.isMouseInRect(mx, my, x + 14, linkY, linkW, linkH);
        ctx.fill(x + 14, linkY, x + 14 + linkW, linkY + linkH, linkHov ? UIConstants.CLR_ACTION_HOVER : UIConstants.CLR_ACTION_FILL);
        ctx.drawBorder(x + 14, linkY, linkW, linkH, linkHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_ACTION_STROKE);

        int clr = linkHov ? UIConstants.CLR_LINK_PRESSED : UIConstants.CLR_LINK;
        int txtY = linkY + 4;
        for (OrderedText line : linkLines) {
            int lw = tr.getWidth(line);
            ctx.drawText(tr, line, x + 14 + (linkW - lw) / 2, txtY, clr, false);
            txtY += 10;
        }

        if (linkHov) PatternAndFontOverlay.setLastHoveredUrl("https://github.com/BGSDT/yunbei-urban-construction");

        return y + cardH;
    }

    public static boolean openUrl(String url) {
        if (url != null && !url.isEmpty()) {
            Util.getOperatingSystem().open(url);
            return true;
        }
        return false;
    }

    private static float getAspect() {
        if (!texChecked) {
            texChecked = true;
            try {
                Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(HEADER_TEX);
                if (res.isPresent()) {
                    try (InputStream in = res.get().getInputStream()) {
                        try (NativeImage img = NativeImage.read(in)) {
                            if (img.getWidth() > 0) {
                                cachedAspect = (float) img.getHeight() / (float) img.getWidth();
                                texExists = true;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return texExists && cachedAspect > 0 ? cachedAspect : 0f;
    }
}
