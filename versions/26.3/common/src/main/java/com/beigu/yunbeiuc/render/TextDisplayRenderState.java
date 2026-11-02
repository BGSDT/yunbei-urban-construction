package com.beigu.yunbeiuc.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

/**
 * 26.x splits block entity rendering into "extract" and "submit" phases, so the renderer needs a
 * state object to carry the data between them.
 *
 * <p>The text display renderers read a lot of behaviour off the block entity itself (transforms,
 * z-offset, text lines), and the entity is still loaded for the whole frame, so the state simply
 * keeps a reference instead of copying every field.</p>
 */
public class TextDisplayRenderState extends BlockEntityRenderState {
    private Object entity;
    private float partialTick;

    public void set(Object entity, float partialTick) {
        this.entity = entity;
        this.partialTick = partialTick;
    }

    @SuppressWarnings("unchecked")
    public <T> T entity() {
        return (T) entity;
    }

    public float partialTick() {
        return partialTick;
    }
}
