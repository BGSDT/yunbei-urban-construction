package com.beigu.yunbeiuc.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

// 26.x splits block entity rendering into extract/submit phases; this state carries data between
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
