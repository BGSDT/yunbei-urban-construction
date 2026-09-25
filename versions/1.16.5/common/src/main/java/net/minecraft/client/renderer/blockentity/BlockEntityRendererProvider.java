package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.gui.Font;

/** Compatibility shape matching the provider context introduced after 1.16.5. */
public final class BlockEntityRendererProvider {
    private BlockEntityRendererProvider() {}

    public static final class Context {
        private final BlockEntityRenderDispatcher dispatcher;

        public Context(BlockEntityRenderDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public Font getFont() {
            return dispatcher.getFont();
        }

        public BlockEntityRenderDispatcher dispatcher() {
            return dispatcher;
        }
    }
}
