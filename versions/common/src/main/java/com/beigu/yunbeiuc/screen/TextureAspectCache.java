package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 纹理原始像素尺寸缓存。
 *
 * <p>GUI 里所有 {@code blit} 重载都会按固定的「图集尺寸」把 regionWidth/regionHeight 换算成 UV。
 * 若把源纹理的宽高直接当图集尺寸传入，或按默认的 256×256 传入，都会让 UV 越界，
 * 结果就是只显示出图片的一小块（比例截取不正确、被拉伸）。
 *
 * <p>本类统一读取 PNG 的真实像素尺寸，供缩略图按原始宽高比等比绘制。
 *
 * <p>缓存以 {@link ResourceLocation} 为键；资源包重载时调用 {@link #clear()} 失效。
 */
public final class TextureAspectCache {

    /** 记录单个纹理的原始尺寸。 */
    public static final class Size {
        public final int width;
        public final int height;

        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        /** 宽高比（宽 / 高），尺寸非法时返回 1。 */
        public float aspect() {
            return height > 0 ? (float) width / (float) height : 1f;
        }
    }

    private static final Map<ResourceLocation, Size> CACHE = new ConcurrentHashMap<>();

    /** 读取失败时的占位（正方形），避免反复重试打日志。 */
    private static final Size UNKNOWN = new Size(0, 0);

    private TextureAspectCache() {
    }

    /**
     * 获取纹理原始尺寸；读取失败返回 {@code null}。
     *
     * <p>结果会被缓存，重复调用不会重复读盘。
     */
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

    /**
     * 按源纹理真实尺寸绘制，等比缩放到 {@code boxW × boxH} 内并居中。
     *
     * <p>这是修复「比例截取不对」的关键：把真实宽高同时作为
     * regionWidth/regionHeight 与 textureWidth/textureHeight 传入，
     * 保证 UV 恰好覆盖整张图（0..1），不再越界，也不拉伸变形。
     *
     * @param ctx    绘制上下文
     * @param texId  纹理
     * @param x      目标区域左上角 X
     * @param y      目标区域左上角 Y
     * @param boxW   目标区域宽
     * @param boxH   目标区域高
     * @return 实际绘制的宽高，读取失败返回 {@code null}（调用方自行降级）
     */
    public static int[] drawFitted(DrawContext ctx, ResourceLocation texId,
                                   int x, int y, int boxW, int boxH) {
        Size size = get(texId);
        if (size == null || size.width <= 0 || size.height <= 0) return null;

        float scale = Math.min((float) boxW / size.width, (float) boxH / size.height);
        int drawW = Math.max(1, Math.round(size.width * scale));
        int drawH = Math.max(1, Math.round(size.height * scale));

        int drawX = x + (boxW - drawW) / 2;
        int drawY = y + (boxH - drawH) / 2;

        // 真实宽高既作 region 也作 texture 尺寸，UV 精确为 0..1，不截取不拉伸
        // 形参顺序为 (tex, x, y, width, height, u, v, regionW, regionH, texW, texH)
        ctx.drawTexture(texId, drawX, drawY, drawW, drawH, 0, 0, size.width, size.height,
                size.width, size.height);

        return new int[]{drawX, drawY, drawW, drawH};
    }

    /** 资源包重载后清空缓存。 */
    public static void clear() {
        CACHE.clear();
    }

    // 读取 PNG 头部得到像素尺寸
    // 注意：不能直接用 manager.getResource(...)，其返回类型跨版本不一致
    // （1.16.5/1.17.1/1.18.2 为 Resource，1.19+ 为 Optional<Resource>），
    // 必须走 VersionServices.resources().openIfPresent 这层跨版本抽象。
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
            // 纹理缺失或格式异常：交由调用方降级处理
        }
        return null;
    }
}
