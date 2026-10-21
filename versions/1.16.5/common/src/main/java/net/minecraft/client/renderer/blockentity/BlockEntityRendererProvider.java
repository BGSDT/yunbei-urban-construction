package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public final class BlockEntityRendererProvider {
    private BlockEntityRendererProvider() {}

    public static final class Context {
        private final BlockEntityRenderDispatcher dispatcher;

        public Context(BlockEntityRenderDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

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
