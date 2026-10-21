package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.text.Text;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class GridRenderer {
    private GridRenderer() {
    }

    public static int renderWhitelistGrid(DrawContext ctx, Font tr,
                                          double mx, double my,
                                          int paneW, int clipTop, int clipBottom,
                                          List<PatternAndFontOverlay.WhitelistPatternItem> items,
                                          int startX, int startY) {
        if (items.isEmpty()) {
            ctx.drawText(tr, Text.translatable("yunbeiuc.gui.no_images"),
                    startX, startY, UIConstants.CLR_MUTED, false);
            return 30;
        }

        int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
        int gridW = cols * UIConstants.CELL_SIZE + (cols - 1) * UIConstants.CELL_GAP_X;
        int gridX = SidebarState.getEffectiveWidth() + (paneW - gridW) / 2;

        for (int i = 0; i < items.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int tx = gridX + col * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X);
            int ty = startY + row * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y);

            if (ty + UIConstants.CELL_SIZE < clipTop || ty > clipBottom) continue;

            PatternAndFontOverlay.WhitelistPatternItem item = items.get(i);
            drawTile(ctx, tx, ty, item.textureId, mx, my);
        }

        int rows = (int) Math.ceil((double) items.size() / cols);
        return rows * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y) - UIConstants.CELL_GAP_Y;
    }

    public static int renderCachedTextureGrid(DrawContext ctx, Font tr,
                                              double mx, double my,
                                              int paneW, int clipTop, int clipBottom,
                                              List<ResourceLocation> textures,
                                              int startX, int startY) {
        if (textures == null || textures.isEmpty()) {
            ctx.drawText(tr, Text.translatable("yunbeiuc.gui.no_images"),
                    startX, startY, UIConstants.CLR_MUTED, false);
            return 30;
        }

        int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
        int gridW = cols * UIConstants.CELL_SIZE + (cols - 1) * UIConstants.CELL_GAP_X;
        int gridX = SidebarState.getEffectiveWidth() + (paneW - gridW) / 2;

        for (int i = 0; i < textures.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int tx = gridX + col * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X);
            int ty = startY + row * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y);

            if (ty + UIConstants.CELL_SIZE < clipTop || ty > clipBottom) continue;

            drawTile(ctx, tx, ty, textures.get(i), mx, my);
        }

        int rows = (int) Math.ceil((double) textures.size() / cols);
        return rows * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y) - UIConstants.CELL_GAP_Y;
    }

    private static void drawTile(DrawContext ctx, int x, int y, ResourceLocation texId,
                                 double mx, double my) {
        int s = UIConstants.CELL_SIZE;
        boolean isHov = LayoutHelper.isMouseInRect(mx, my, x, y, s, s);

        ctx.fill(x, y, x + s, y + s, isHov ? UIConstants.CLR_BTN_HOVER : UIConstants.CLR_CELL_FILL);
        ctx.drawBorder(x, y, s, s, isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_CELL_OUTLINE);

        int pad = 4;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int[] fitted = TextureAspectCache.drawFitted(ctx, texId,
                x + pad, y + pad, s - pad * 2, s - pad * 2);
        if (fitted == null) {
        }

        if (isHov) {
            ctx.fill(x, y, x + s, y + 3, UIConstants.CLR_ACCENT);
        }
    }

    public static int renderFontList(DrawContext ctx, Font tr,
                                     double mx, double my,
                                     int paneW, int clipTop, int clipBottom,
                                     List<PatternAndFontOverlay.FontItem> fontItems,
                                     int startX, int startY) {
        if (fontItems.isEmpty()) {
            ctx.drawText(tr, Text.translatable("yunbeiuc.gui.no_fonts"),
                    startX, startY, UIConstants.CLR_MUTED, false);
            return 30;
        }

        int cardW = paneW - 60;
        int cardX = SidebarState.getEffectiveWidth() + 30;
        int cardH = UIConstants.FONT_CARD_H;
        int gap = UIConstants.FONT_CARD_GAP;
        int accentW = 4;

        for (int i = 0; i < fontItems.size(); i++) {
            PatternAndFontOverlay.FontItem item = fontItems.get(i);
            int cy = startY + i * (cardH + gap);

            if (cy + cardH < clipTop || cy > clipBottom) continue;

            boolean isHov = LayoutHelper.isMouseInRect(mx, my, cardX, cy, cardW, cardH);

            int bg = isHov ? UIConstants.CLR_BTN_HOVER : UIConstants.CLR_HOME_CARD_BG;
            ctx.fill(cardX, cy, cardX + cardW, cy + cardH, bg);
            ctx.drawBorder(cardX, cy, cardW, cardH,
                    isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_HOME_CARD_STROKE);

            ctx.fill(cardX, cy, cardX + accentW, cy + cardH, UIConstants.CLR_ACCENT);

            String name = item.displayName.getString();
            ctx.drawText(tr, name, cardX + accentW + 10, cy + 6,
                    UIConstants.CLR_HEADING, false);

            Component preview = buildFontPreview(item.fontId);
            ctx.drawText(tr, preview, cardX + accentW + 10, cy + 22,
                    UIConstants.CLR_BODY_TEXT, false);

            Component tag = Text.translatable("yunbeiuc.gui.button.insert");
            int tagW = tr.width(tag) + 12;
            int tagX = cardX + cardW - tagW - 8;
            int tagY = cy + (cardH - 14) / 2;
            ctx.fill(tagX, tagY, tagX + tagW, tagY + 14,
                    isHov ? UIConstants.CLR_ACTION_HOVER : UIConstants.CLR_ACTION_FILL);
            ctx.drawBorder(tagX, tagY, tagW, 14,
                    isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_ACTION_STROKE);
            ctx.drawText(tr, tag, tagX + 6, tagY + 3,
                    UIConstants.CLR_BTN_LABEL, false);
        }

        return fontItems.size() * (cardH + gap) - gap;
    }

    private static Component buildFontPreview(String fontId) {
        Component preview = Text.literal("AaBbCc 123");
        if (fontId == null || fontId.isEmpty()) {
            return preview;
        }
        try {
            int colon = fontId.indexOf(':');
            String namespace = colon >= 0 ? fontId.substring(0, colon) : "minecraft";
            String path = colon >= 0 ? fontId.substring(colon + 1) : fontId;
            ResourceLocation id = createResourceLocation(namespace, path);
            return preview.copy().withStyle(Style.EMPTY.withFont(id));
        } catch (Throwable t) {
            return preview;
        }
    }

    private static ResourceLocation createResourceLocation(String namespace, String path)
            throws ReflectiveOperationException {
        try {
            return (ResourceLocation) ResourceLocation.class
                    .getMethod("fromNamespaceAndPath", String.class, String.class)
                    .invoke(null, namespace, path);
        } catch (NoSuchMethodException ignored) {
        }
        return (ResourceLocation) ResourceLocation.class
                .getConstructor(String.class, String.class)
                .newInstance(namespace, path);
    }

    public static int getGridItemIndex(double mx, double my,
                                       int paneW, int startY, int clipTop, int clipBottom,
                                       int itemCount) {
        if (my < clipTop || my > clipBottom) return -1;

        int cols = Math.max(1, (paneW - 48) / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X));
        int gridW = cols * UIConstants.CELL_SIZE + (cols - 1) * UIConstants.CELL_GAP_X;
        int gridX = SidebarState.getEffectiveWidth() + (paneW - gridW) / 2;

        int relX = (int) mx - gridX;
        int relY = (int) my - startY;

        if (relX < 0 || relY < 0) return -1;

        int col = relX / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X);
        int row = relY / (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y);

        if (col >= cols) return -1;

        int tileX = col * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_X);
        int tileY = row * (UIConstants.CELL_SIZE + UIConstants.CELL_GAP_Y);
        if (relX - tileX > UIConstants.CELL_SIZE) return -1;
        if (relY - tileY > UIConstants.CELL_SIZE) return -1;

        int idx = row * cols + col;
        if (idx >= itemCount) return -1;

        return idx;
    }

    public static int getFontItemIndex(double mx, double my, int paneW,
                                       int startY, int clipTop, int clipBottom,
                                       int itemCount) {
        if (my < clipTop || my > clipBottom) return -1;

        int cardW = paneW - 60;
        int cardX = SidebarState.getEffectiveWidth() + 30;
        int cardH = UIConstants.FONT_CARD_H;
        int gap = UIConstants.FONT_CARD_GAP;

        int relY = (int) my - startY;
        int idx = relY / (cardH + gap);

        if (idx < 0 || idx >= itemCount) return -1;

        int cy = startY + idx * (cardH + gap);
        if (!LayoutHelper.isMouseInRect(mx, my, cardX, cy, cardW, cardH)) return -1;

        return idx;
    }

    public static final class LabeledLayout {
        public final int[] rects;
        public final int totalHeight;

        LabeledLayout(int[] rects, int totalHeight) {
            this.rects = rects;
            this.totalHeight = totalHeight;
        }
    }

    public static LabeledLayout layoutLabeled(Font tr, int paneW, int startX, int startY,
                                              List<PatternAndFontOverlay.LabeledItem> items) {
        int avail = Math.max(1, paneW - 48);
        int n = items.size();
        int[] rects = new int[n * 4];

        int rowH = UIConstants.LABEL_BTN_HEIGHT;
        int relX = 0;
        int y = startY;

        for (int i = 0; i < n; i++) {
            Component label = items.get(i).displayName;
            int w = Math.min(avail, tr.width(label) + UIConstants.LABEL_BTN_PAD_X * 2);

            if (relX > 0 && relX + w > avail) {
                relX = 0;
                y += rowH + UIConstants.LABEL_BTN_GAP_Y;
            }

            rects[i * 4] = startX + relX;
            rects[i * 4 + 1] = y;
            rects[i * 4 + 2] = w;
            rects[i * 4 + 3] = rowH;

            relX += w + UIConstants.LABEL_BTN_GAP_X;
        }

        return new LabeledLayout(rects, n == 0 ? 0 : (y - startY) + rowH);
    }

    public static int labeledHeight(Font tr, int paneW, List<PatternAndFontOverlay.LabeledItem> items) {
        if (items == null || items.isEmpty()) return 30;
        return layoutLabeled(tr, paneW, 0, 0, items).totalHeight;
    }

    public static int renderLabeledGrid(DrawContext ctx, Font tr,
                                        double mx, double my,
                                        int paneW, int clipTop, int clipBottom,
                                        List<PatternAndFontOverlay.LabeledItem> items,
                                        int startX, int startY) {
        if (items == null || items.isEmpty()) {
            ctx.drawText(tr, Text.translatable("yunbeiuc.gui.no_images"),
                    startX, startY, UIConstants.CLR_MUTED, false);
            return 30;
        }

        LabeledLayout layout = layoutLabeled(tr, paneW, startX, startY, items);

        for (int i = 0; i < items.size(); i++) {
            int x = layout.rects[i * 4];
            int y = layout.rects[i * 4 + 1];
            int w = layout.rects[i * 4 + 2];
            int h = layout.rects[i * 4 + 3];

            if (y + h < clipTop || y > clipBottom) continue;

            boolean isHov = LayoutHelper.isMouseInRect(mx, my, x, y, w, h);

            ctx.fill(x, y, x + w, y + h, isHov ? UIConstants.CLR_BTN_HOVER : UIConstants.CLR_BTN_FILL);
            ctx.drawBorder(x, y, w, h, isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_BTN_STROKE);

            Component label = items.get(i).displayName;
            int lw = tr.width(label);
            ctx.drawText(tr, label, x + (w - lw) / 2, y + (h - 8) / 2,
                    isHov ? UIConstants.CLR_ACCENT : UIConstants.CLR_BTN_LABEL, false);
        }

        return layout.totalHeight;
    }

    public static int getLabeledItemIndex(Font tr, double mx, double my, int paneW,
                                          int startY, int clipTop, int clipBottom,
                                          List<PatternAndFontOverlay.LabeledItem> items) {
        if (items == null || items.isEmpty()) return -1;
        if (my < clipTop || my > clipBottom) return -1;

        int startX = SidebarState.getEffectiveWidth() + 24;
        LabeledLayout layout = layoutLabeled(tr, paneW, startX, startY, items);

        for (int i = 0; i < items.size(); i++) {
            if (LayoutHelper.isMouseInRect(mx, my,
                    layout.rects[i * 4], layout.rects[i * 4 + 1],
                    layout.rects[i * 4 + 2], layout.rects[i * 4 + 3])) {
                return i;
            }
        }
        return -1;
    }
}
