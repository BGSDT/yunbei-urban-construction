package com.beigu.yunbeiuc.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.beigu.yunbeiuc.api.render.GeometrySink;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Base class for the mod's block entity renderers.
 *
 * <p>26.x split rendering into an extract phase and a submit phase and dropped the
 * {@code GeometrySink} parameter. The shared renderers still implement the classic
 * {@code render(entity, tickDelta, poseStack, buffers, light, overlay)} method, so this base class
 * bridges the two: the entity is carried in the render state, and on submit the classic method draws
 * into a {@link DeferredGeometry} whose geometry is then handed to the {@link SubmitNodeCollector}.</p>
 */
public abstract class BlockEntityRendererCompat<T extends BlockEntity>
        implements BlockEntityRenderer<T, TextDisplayRenderState> {

    protected BlockEntityRendererCompat(BlockEntityRendererProvider.Context context) {
    }

    protected BlockEntityRendererCompat() {
    }

    @Override
    public TextDisplayRenderState createRenderState() {
        return new TextDisplayRenderState();
    }

    @Override
    public void extractRenderState(T entity, TextDisplayRenderState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        state.set(entity, partialTick);
    }

    @Override
    public void submit(TextDisplayRenderState state, PoseStack matrices, SubmitNodeCollector collector,
                       CameraRenderState cameraState) {
        T entity = state.entity();
        if (entity == null) {
            return;
        }
        DeferredGeometry deferred = new DeferredGeometry();
        render(entity, state.partialTick(), matrices, deferred, state.lightCoords, OverlayTexture.NO_OVERLAY);
        deferred.submit(collector);
    }

    public abstract void render(T entity, float tickDelta, PoseStack matrices,
                                GeometrySink vertexConsumers, int light, int overlay);
}
