package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.DrawContext;

/** Shared 1.21 screen background rendering that never draws over GUI controls. */
final class ScreenRenderUtils {
    private ScreenRenderUtils() {
    }

    static void renderBackground(DrawContext context, int width, int height) {
        // Screen#renderBackground applies Minecraft 1.21's post-process blur.  These
        // screens draw several custom layers outside vanilla's widget pass, so the
        // blur can be composited between those layers.  A translucent shade keeps
        // the world readable without allowing a later blur pass to cover controls.
        context.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);
    }
}
