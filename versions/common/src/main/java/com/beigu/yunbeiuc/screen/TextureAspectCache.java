package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TextureAspectCache {

    public static final class Size {
        public final int width;
        public final int height;

        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public float aspect() {
            return height > 0 ? (float) width / (float) height : 1f;
        }
    }

    private static final Map<ResourceLocation, Size> CACHE = new ConcurrentHashMap<>();

    private static final Size UNKNOWN = new Size(0, 0);

    private TextureAspectCache() {
    }

    public static Size get(ResourceLocation texture) {
        if (texture == null) return null;

        Size cached = CACHE.get(texture);
        if (cached != null) {
            return cached == UNKNOWN ? null : cached;
        }

        Size size = read(texture);
        CACHE.put(texture, size != null ? size : UNKNOWN);
        return size;
    }

    public static int[] drawFitted(DrawContext ctx, ResourceLocation texId,
                                   int x, int y, int boxW, int boxH) {
        Size size = get(texId);
        if (size == null || size.width <= 0 || size.height <= 0) return null;

        float scale = Math.min((float) boxW / size.width, (float) boxH / size.height);
        int drawW = Math.max(1, Math.round(size.width * scale));
        int drawH = Math.max(1, Math.round(size.height * scale));

        int drawX = x + (boxW - drawW) / 2;
        int drawY = y + (boxH - drawH) / 2;

        ctx.drawTexture(texId, drawX, drawY, drawW, drawH, 0, 0, size.width, size.height,
                size.width, size.height);

        return new int[]{drawX, drawY, drawW, drawH};
    }

    public static void clear() {
        CACHE.clear();
    }

    private static Size read(ResourceLocation texture) {
        try {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            try (InputStream in = VersionServices.resources().openIfPresent(manager, texture)) {
                if (in == null) return null;
                try (NativeImage img = NativeImage.read(in)) {
                    if (img.getWidth() > 0 && img.getHeight() > 0) {
                        return new Size(img.getWidth(), img.getHeight());
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
