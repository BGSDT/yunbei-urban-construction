package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

/** Compatibility shape matching the provider context introduced after 1.16.5. */
public final class BlockEntityRendererProvider {
    private BlockEntityRendererProvider() {}

    public static final class Context {
        private final BlockEntityRenderDispatcher dispatcher;

        public Context(BlockEntityRenderDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        /**
         * 1.16.5 的 {@code BlockEntityRenderDispatcher.font} 只在每帧 {@code prepare(...)} 时赋值，
         * 渲染器构造期取到的是 null。这里回退到全局字体，保证调用方拿不到 null；
         * 但真正稳妥的做法仍是在使用处按需获取（见各渲染器的 {@code textRenderer()}）。
         */
        public Font getFont() {
            Font font = dispatcher.getFont();
            if (font != null) return font;
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft != null ? minecraft.font : null;
        }

        public BlockEntityRenderDispatcher dispatcher() {
            return dispatcher;
        }
    }
}
