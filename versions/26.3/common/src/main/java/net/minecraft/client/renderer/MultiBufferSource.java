package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;

// 26.3 removed the vanilla immediate-mode MultiBufferSource (BufferUploader / Tesselator / Font#drawInBatch),
public interface MultiBufferSource {

    VertexConsumer getBuffer(RenderType type);

    interface BufferSource extends MultiBufferSource {
        void endBatch();
    }
}
