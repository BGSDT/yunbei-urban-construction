package com.beigu.yunbeiuc.api.render;

/**
 * Version-neutral geometry sink.
 *
 * <p>26.3 removed {@code GeometrySink}; geometry is submitted through
 * {@code SubmitNodeCollector#submitCustomGeometry} instead, so the shared renderers only talk to
 * this interface.</p>
 */
public interface GeometrySink {
    com.mojang.blaze3d.vertex.VertexConsumer getBuffer(
            net.minecraft.client.renderer.rendertype.RenderType type);
}
