package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class HomepageRenderer {
    private static final ResourceLocation HEADER_TEX = VersionServices.resources().create("yunbeiuc", "textures/gui/header.png");

    private HomepageRenderer() {
    }

    public static int render(DrawContext ctx, Font tr, int mx, int my,
                              int paneW, int startY, int clipTop, int clipBottom) {
        int y = startY;
        int navW = SidebarState.getEffectiveWidth();
        int pad = 16;
        int contentW = paneW - pad * 2;
        int contentX = navW + pad;

        int hdrH = (int) (paneW * getAspect());
        if (hdrH > 0) {
            if (y + hdrH >= clipTop && y <= clipBottom) {
                // 真实像素尺寸既作 region 也作 texture 尺寸，UV 恰好覆盖整张图。
                // 原先传的是 (paneW, hdrH) 自洽尺寸，一旦 PNG 实际比例与
                // getAspect() 推算结果不符（或缓存陈旧），UV 就会越界，
                // 表现为只显示出图片的一小块、比例不对。
                TextureAspectCache.Size size = TextureAspectCache.get(HEADER_TEX);
                if (size != null) {
                    ctx.drawTexture(HEADER_TEX, navW, y, paneW, hdrH, 0, 0,
                            size.width, size.height, size.width, size.height);
                }
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

    public static int getContentHeight(int paneW, Font tr) {
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
            Component t = Text.translatable(key);
            List<FormattedCharSequence> lines = tr.split(t, contentW - 16);
            h += lines.size() * 13 + 6;
        }
        h += 8;

        h += 18 + 4 + 36 + 8;

        return h;
    }

    private static int drawTitleBar(DrawContext ctx, Font tr, int x, int y, int w, int clipTop, int clipBottom) {
        int panelH = 40;
        if (y + panelH < clipTop || y > clipBottom) return y + panelH;

        ctx.fill(x, y, x + w, y + panelH, UIConstants.CLR_HOME_CARD_BG);
        ctx.drawBorder(x, y, w, panelH, UIConstants.CLR_HOME_CARD_STROKE);
        ctx.fill(x, y, x + 4, y + panelH, UIConstants.CLR_ACCENT);

        Component title = Text.translatable("yunbeiuc.gui.homepage.title");
        ctx.drawText(tr, title, x + 14, y + 8, UIConstants.CLR_HEADING, false);

        Component subtitle = Text.translatable("yunbeiuc.gui.homepage.welcome");
        ctx.drawText(tr, subtitle, x + 14, y + 24, UIConstants.CLR_HOME_DESC, false);

        return y + panelH;
    }

    private static int drawIntroSection(DrawContext ctx, Font tr, int x, int y, int w, int clipTop, int clipBottom) {
        Component sectionTitle = Text.translatable("yunbeiuc.gui.homepage.section.intro");
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
            Component t = Text.translatable(key);
            List<FormattedCharSequence> lines = tr.split(t, w - 16);
            for (FormattedCharSequence line : lines) {
                if (y + 13 >= clipTop && y <= clipBottom) {
                    ctx.drawText(tr, line, x + 8, y, UIConstants.CLR_HOME_BODY, false);
                }
                y += 13;
            }
            y += 6;
        }

        return y + 4;
    }

    private static int drawProjectCard(DrawContext ctx, Font tr, int mx, int my,
                                        int x, int y, int w, int clipTop, int clipBottom) {
        int cardH = 36;
        if (y + cardH < clipTop || y > clipBottom) return y + cardH;

        // 卡片本身不是按钮：整卡不随鼠标悬停变化，仅卡片内的链接有交互反馈
        ctx.fill(x, y, x + w, y + cardH, UIConstants.CLR_HOME_CARD_BG);
        ctx.drawBorder(x, y, w, cardH, UIConstants.CLR_HOME_CARD_STROKE);
        ctx.fill(x, y, x + 4, y + cardH, UIConstants.CLR_ACCENT);

        Component cardTitle = Text.translatable("yunbeiuc.gui.homepage.yunbeiuc.title");
        ctx.drawText(tr, cardTitle, x + 14, y + 4, UIConstants.CLR_HEADING, false);

        Component repoLink = Text.translatable("yunbeiuc.gui.homepage.repo.label");
        int linkY = y + 18;
        int linkW = w - 28;
        List<FormattedCharSequence> linkLines = tr.split(repoLink, linkW - 8);
        int linkH = linkLines.size() * 10 + 8;

        boolean linkHov = LayoutHelper.isMouseInRect(mx, my, x + 14, linkY, linkW, linkH);
        ctx.fill(x + 14, linkY, x + 14 + linkW, linkY + linkH, linkHov ? UIConstants.CLR_ACTION_HOVER : UIConstants.CLR_ACTION_FILL);
        ctx.drawBorder(x + 14, linkY, linkW, linkH, linkHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_ACTION_STROKE);

        int clr = linkHov ? UIConstants.CLR_LINK_PRESSED : UIConstants.CLR_LINK;
        int txtY = linkY + 4;
        for (FormattedCharSequence line : linkLines) {
            int lw = tr.width(line);
            ctx.drawText(tr, line, x + 14 + (linkW - lw) / 2, txtY, clr, false);
            txtY += 10;
        }

        if (linkHov) PatternAndFontOverlay.setLastHoveredUrl("https://github.com/BGSDT/yunbei-urban-construction");

        return y + cardH;
    }

    public static boolean openUrl(String url) {
        if (url != null && !url.isEmpty()) {
            Util.getPlatform().openUri(url);
            return true;
        }
        return false;
    }

    /** 头部横幅的宽高比（高 / 宽）；纹理缺失时返回 0 表示不绘制。 */
    private static float getAspect() {
        TextureAspectCache.Size size = TextureAspectCache.get(HEADER_TEX);
        if (size == null || size.width <= 0 || size.height <= 0) return 0f;
        return (float) size.height / (float) size.width;
    }
}
