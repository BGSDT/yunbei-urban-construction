package com.beigu.yunbeiuc.api.gui;

/** Version-neutral rendering operations for text, textures and primitives. */
public interface RenderPlatform<C> {
    void drawText(C context, String text, float x, float y, int color, boolean shadow);
    void drawTexture(C context, String textureId, int x, int y, int width, int height);
    void fill(C context, int left, int top, int right, int bottom, int color);
}
