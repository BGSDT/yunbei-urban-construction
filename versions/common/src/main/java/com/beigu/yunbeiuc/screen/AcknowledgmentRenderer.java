package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.text.Text;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public final class AcknowledgmentRenderer {

    private AcknowledgmentRenderer() {
    }

    public static final class Entry {
        public final Component label;
        public final String url;
        public final Component buttonText;

        public Entry(Component label, String url, Component buttonText) {
            this.label = label;
            this.url = url;
            this.buttonText = buttonText;
        }

        public Entry(Component label, String url) {
            this(label, url, null);
        }

        public Entry(Component label) {
            this(label, null, null);
        }
    }

    public static final class Block {
        public final Component title;
        public final List<Component> introLines = new ArrayList<>();
        public final List<Entry> entries = new ArrayList<>();
        public Component quoteTitle;
        public final List<Component> quoteLines = new ArrayList<>();

        public Block(Component title) {
            this.title = title;
        }
    }

    public static int render(DrawContext ctx, Font tr, int mx, int my,
                              int paneW, int startY, int clipTop, int clipBottom) {
        PatternAndFontOverlay.clearLastHoveredUrl();

        int navW = SidebarState.getEffectiveWidth();
        int curY = startY;

        curY = renderBigTitle(ctx, tr, paneW, navW, curY, clipTop, clipBottom);

        List<Block> blocks = buildBlocks();
        for (Block block : blocks) {
            curY = renderBlock(ctx, tr, mx, my, paneW, navW, curY, clipTop, clipBottom, block);
        }

        return curY + 20;
    }

    public static int getContentHeight(int paneW, Font tr) {
        int h = estimateBigTitleHeight(tr, paneW);
        for (Block block : buildBlocks()) {
            h += estimateBlockHeight(tr, paneW, block);
        }
        return h + 20;
    }

    private static List<Block> buildBlocks() {
        List<Block> blocks = new ArrayList<>();

        Block ack = new Block(Text.translatable("yunbeiuc.gui.ack.title"));
        ack.introLines.add(Text.translatable("yunbeiuc.gui.ack.intro"));

        ack.entries.add(new Entry(
                Text.translatable("yunbeiuc.gui.ack.mod.ocelotsign.name"),
                "https://www.mcmod.cn/class/26158.html",
                Text.translatable("yunbeiuc.gui.ack.link.mcmod")));
        ack.entries.add(new Entry(
                Text.translatable("yunbeiuc.gui.ack.mod.ocelotsign.repo"),
                "https://github.com/Creeper-Cola123/OcelotSignMod-Minecraft",
                Text.translatable("yunbeiuc.gui.ack.link.github")));
        blocks.add(ack);

        Block decl = new Block(Text.translatable("yunbeiuc.gui.ack.decl.title"));
        decl.entries.add(new Entry(Text.translatable("yunbeiuc.gui.ack.decl.yunbeiuc.name")));
        decl.entries.add(new Entry(Text.translatable("yunbeiuc.gui.ack.decl.yunbeiuc.license")));
        decl.entries.add(new Entry(Text.translatable("yunbeiuc.gui.ack.decl.yunbeiuc.repo")));
        decl.entries.add(new Entry(Text.translatable("yunbeiuc.gui.ack.decl.yunbeiuc.note")));

        decl.quoteTitle = Text.translatable("yunbeiuc.gui.ack.quote.title");
        decl.quoteLines.add(Text.translatable("yunbeiuc.gui.ack.quote.ocelot"));
        blocks.add(decl);

        return blocks;
    }

    private static int renderBigTitle(DrawContext ctx, Font tr, int paneW, int navW,
                                       int curY, int clipTop, int clipBottom) {
        Component title = Text.translatable("yunbeiuc.gui.ack.page_title");
        int titleW = tr.width(title);
        int titleX = navW + (paneW - titleW) / 2;
        if (curY + 22 >= clipTop && curY <= clipBottom) {
            ctx.drawText(tr, title, titleX, curY, UIConstants.CLR_HEADING, false);
        }
        curY += 24;

        if (curY + 4 >= clipTop && curY <= clipBottom) {
            ctx.fill(navW + 60, curY, navW + paneW - 60, curY + 2, UIConstants.CLR_CONTENT_DIVIDER);
        }
        curY += 14;
        return curY;
    }

    private static int estimateBigTitleHeight(Font tr, int paneW) {
        Component title = Text.translatable("yunbeiuc.gui.ack.page_title");
        List<FormattedCharSequence> lines = tr.split(title, paneW - 60);
        return lines.size() * 22 + 2 + 14;
    }

    private static int renderBlock(DrawContext ctx, Font tr, int mx, int my,
                                    int paneW, int navW, int curY,
                                    int clipTop, int clipBottom, Block block) {
        if (curY + 22 >= clipTop && curY <= clipBottom) {
            ctx.drawText(tr, block.title, navW + 24, curY, UIConstants.CLR_ACCENT, false);
        }
        curY += 24;

        for (Component t : block.introLines) {
            curY = renderWrappedParagraph(ctx, tr, t, navW + 24, paneW - 48,
                    curY, clipTop, clipBottom, UIConstants.CLR_HOME_BODY, 13, 6);
            curY += 8;
        }

        if (!block.entries.isEmpty()) {
            curY += 4;
            curY = renderEntryCard(ctx, tr, mx, my, paneW, navW, curY, clipTop, clipBottom, block.entries);
        }

        if (block.quoteTitle != null || !block.quoteLines.isEmpty()) {
            curY += 6;
            curY = renderQuote(ctx, tr, paneW, navW, curY, clipTop, clipBottom, block);
        }

        curY += 24;
        return curY;
    }

    private static int estimateBlockHeight(Font tr, int paneW, Block block) {
        int h = 24;
        for (Component t : block.introLines) {
            List<FormattedCharSequence> lines = tr.split(t, paneW - 48);
            h += lines.size() * 13 + 6 + 8;
        }
        if (!block.entries.isEmpty()) {
            h += 4;
            h += estimateEntriesHeight(tr, paneW, block.entries);
        }
        if (block.quoteTitle != null || !block.quoteLines.isEmpty()) {
            h += 6;
            h += estimateQuoteHeight(tr, paneW, block);
        }
        h += 24;
        return h;
    }

    private static int renderEntryCard(DrawContext ctx, Font tr, int mx, int my,
                                        int paneW, int navW, int curY,
                                        int clipTop, int clipBottom, List<Entry> entries) {
        int blockX = navW + 24;
        int blockW = paneW - 48;
        int totalH = estimateEntriesHeight(tr, paneW, entries);

        if (curY + totalH >= clipTop && curY <= clipBottom) {
            ctx.fill(blockX, curY, blockX + blockW, curY + totalH, UIConstants.CLR_HOME_CARD_BG);
            ctx.drawBorder(blockX, curY, blockW, totalH, UIConstants.CLR_HOME_CARD_STROKE);
            ctx.fill(blockX, curY, blockX + 4, curY + totalH, UIConstants.CLR_ACCENT);
        }

        int y = curY + 8;
        for (Entry e : entries) {
            y = renderEntry(ctx, tr, mx, my, blockX, blockW, y, clipTop, clipBottom, e);
            y += 6;
        }
        return curY + totalH;
    }

    private static int renderEntry(DrawContext ctx, Font tr, int mx, int my,
                                    int blockX, int blockW, int curY,
                                    int clipTop, int clipBottom, Entry entry) {
        int pad = 6;
        int innerX = blockX + pad + 4;
        int innerW = blockW - pad * 2 - 8;

        List<FormattedCharSequence> labelLines = tr.split(entry.label, innerW - 8);
        int labelH = labelLines.size() * 12;
        int entryH = labelH + pad * 2;
        if (entry.url != null && !entry.url.isEmpty()) {
            entryH += 24 + 4;
        }

        if (curY + entryH >= clipTop && curY <= clipBottom) {
            for (int i = 0; i < labelLines.size(); i++) {
                ctx.drawText(tr, labelLines.get(i), innerX, curY + pad + i * 12,
                        UIConstants.CLR_HOME_BODY, false);
            }
            if (entry.url != null && !entry.url.isEmpty()) {
                int btnY = curY + pad + labelH + 4;
                renderUrlButton(ctx, tr, mx, my, innerX, btnY, innerW - 8,
                        entry.url, entry.buttonText, clipTop, clipBottom);
            }
        }
        return curY + entryH;
    }

    private static int estimateEntriesHeight(Font tr, int paneW, List<Entry> entries) {
        int blockW = paneW - 48;
        int pad = 6;
        int innerW = blockW - pad * 2 - 8;
        int total = 16;
        for (Entry e : entries) {
            List<FormattedCharSequence> labelLines = tr.split(e.label, innerW - 8);
            int labelH = labelLines.size() * 12;
            int entryH = labelH + pad * 2;
            if (e.url != null && !e.url.isEmpty()) {
                entryH += 24 + 4;
            }
            total += entryH + 6;
        }
        return total;
    }

    private static void renderUrlButton(DrawContext ctx, Font tr, int mx, int my,
                                         int x, int y, int w, String url,
                                         Component customText, int clipTop, int clipBottom) {
        Component display = customText != null ? customText : Text.literal(simplifyUrl(url));
        int btnW = Math.max(40, w);
        int minH = 22;

        List<FormattedCharSequence> wrapped = tr.split(display, btnW - 16);
        int btnH = Math.max(minH, wrapped.size() * 10 + 6);

        boolean visible = y + btnH >= clipTop && y <= clipBottom;
        boolean hov = visible && LayoutHelper.isMouseInRect(mx, my, x, y, btnW, btnH);

        if (visible) {
            int bg = hov ? UIConstants.CLR_ACTION_HOVER : UIConstants.CLR_ACTION_FILL;
            int stroke = hov ? UIConstants.CLR_ACCENT : UIConstants.CLR_ACTION_STROKE;
            ctx.fill(x, y, x + btnW, y + btnH, bg);
            ctx.drawBorder(x, y, btnW, btnH, stroke);

            int clr = hov ? UIConstants.CLR_LINK_PRESSED : UIConstants.CLR_LINK;
            int txtY = y + (btnH - wrapped.size() * 10) / 2;
            for (int i = 0; i < wrapped.size(); i++) {
                int lw = tr.width(wrapped.get(i));
                ctx.drawText(tr, wrapped.get(i), x + (btnW - lw) / 2, txtY + i * 10, clr, false);
            }
        }
        if (hov) {
            PatternAndFontOverlay.setLastHoveredUrl(url);
        }
    }

    private static String simplifyUrl(String url) {
        if (url == null || url.isEmpty()) return "";
        String trimmed = url;
        if (trimmed.startsWith("https://")) trimmed = trimmed.substring(8);
        else if (trimmed.startsWith("http://")) trimmed = trimmed.substring(7);
        if (trimmed.length() > 32) {
            return trimmed.substring(0, 29) + "...";
        }
        return trimmed;
    }

    private static int renderQuote(DrawContext ctx, Font tr, int paneW, int navW,
                                    int curY, int clipTop, int clipBottom, Block block) {
        int blockX = navW + 24;
        int blockW = paneW - 48;
        int pad = 10;
        int innerX = blockX + pad + 4;
        int innerW = blockW - pad * 2 - 8;

        int totalH = estimateQuoteHeight(tr, paneW, block);

        if (curY + totalH >= clipTop && curY <= clipBottom) {
            ctx.fill(blockX, curY, blockX + blockW, curY + totalH, UIConstants.CLR_INFO_BG);
            ctx.fill(blockX, curY, blockX + 4, curY + totalH, UIConstants.CLR_ACCENT);

            int y = curY + pad;
            if (block.quoteTitle != null) {
                ctx.drawText(tr, block.quoteTitle, innerX, y, UIConstants.CLR_ACCENT, false);
                y += 16 + 6;
            }
            for (Component t : block.quoteLines) {
                List<FormattedCharSequence> lines = tr.split(t, innerW - 8);
                for (FormattedCharSequence line : lines) {
                    ctx.drawText(tr, line, innerX, y, UIConstants.CLR_HOME_BODY, false);
                    y += 13;
                }
                y += 4;
            }
        }
        return curY + totalH;
    }

    private static int estimateQuoteHeight(Font tr, int paneW, Block block) {
        int blockW = paneW - 48;
        int pad = 10;
        int innerW = blockW - pad * 2 - 8;
        int h = pad;
        if (block.quoteTitle != null) h += 16 + 6;
        for (Component t : block.quoteLines) {
            List<FormattedCharSequence> lines = tr.split(t, innerW - 8);
            h += lines.size() * 13 + 4;
        }
        return h;
    }

    private static int renderWrappedParagraph(DrawContext ctx, Font tr, Component text,
                                               int x, int maxWidth, int curY,
                                               int clipTop, int clipBottom,
                                               int color, int lineHeight, int paraGap) {
        String raw = text.getString();
        String[] paragraphs = raw.split("\n");
        for (int p = 0; p < paragraphs.length; p++) {
            List<FormattedCharSequence> lines = tr.split(Text.literal(paragraphs[p]), maxWidth);
            for (int i = 0; i < lines.size(); i++) {
                if (curY + lineHeight >= clipTop && curY <= clipBottom) {
                    ctx.drawText(tr, lines.get(i), x, curY, color, false);
                }
                curY += lineHeight;
            }
            if (p < paragraphs.length - 1) curY += paraGap;
        }
        return curY;
    }
}
