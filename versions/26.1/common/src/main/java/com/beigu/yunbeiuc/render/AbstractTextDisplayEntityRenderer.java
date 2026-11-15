package com.beigu.yunbeiuc.render;

import net.minecraft.client.renderer.MultiBufferSource;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.resources.Identifier;
import com.mojang.math.Axis;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Locale;

public abstract class AbstractTextDisplayEntityRenderer<T extends CustomSignBlockEntity>
        implements BlockEntityRenderer<T, TextDisplayRenderState> {
    protected static final int FULL_BRIGHT = 0xF000F0;

    protected final Font textRenderer;
    private int gizmoLineIndex;

    private final List<PendingText> pendingTexts = new java.util.ArrayList<>();

    private record PendingText(Matrix4f matrix, float x, float y, Component text, boolean shadow,
                               int color, int outlineColor, int light) {}

    protected AbstractTextDisplayEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.textRenderer = ctx.font();
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
        if (cameraState != null && cameraState.projectionMatrix != null && cameraState.viewRotationMatrix != null) {
            TextGizmo.setProjectionView(new Matrix4f(cameraState.projectionMatrix).mul(cameraState.viewRotationMatrix));
        }
        DeferredGeometry deferred = new DeferredGeometry();
        pendingTexts.clear();
        render(entity, state.partialTick(), matrices, deferred, state.lightCoords, OverlayTexture.NO_OVERLAY);
        deferred.submit(collector);
        submitPendingTexts(collector);
    }

    public final void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        applyTransforms(matrices, entity);
        Matrix4f baseFrame = new Matrix4f(matrices.last().pose());
        float zOffset = getZOffset(entity);
        int effectiveLight = entity.isGlowingText() ? FULL_BRIGHT : light;

        renderFixedContent(entity, matrices, vertexConsumers, tickDelta, light, overlay, zOffset);

        List<CustomSignBlockEntity.TextLineData> lines = entity.getTextLines();
        int editingIndex = entity.getEditingLineIndex();
        int editingMode = entity.getEditingGizmoMode();
        int gizmoMode = editingIndex >= 0 ? editingMode : -1;
        if (editingIndex >= 0 || editingMode == -2) TextGizmo.beginFrameRects();
        boolean groupEditing = editingIndex >= 0 || editingMode == -2;
        for (int i = 0; i < lines.size(); i++) {
            if (!groupEditing && !SignTextGroupRuntime.isRenderableAt(lines, i)) continue;
            gizmoLineIndex = i;
            renderTextLine(entity, matrices, vertexConsumers, effectiveLight, overlay, zOffset, lines.get(i), i == editingIndex, gizmoMode, baseFrame);
        }

        matrices.popPose();
    }

    protected abstract void applyTransforms(PoseStack matrices, T entity);

    protected abstract float getZOffset(T entity);

    protected void renderFixedContent(T entity, PoseStack matrices, MultiBufferSource vertexConsumers,
                                      float tickDelta, int light, int overlay, float zOffset) {
    }

    private boolean textureExists(Identifier id) {
        try {
            return Minecraft.getInstance().getResourceManager().getResource(id).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    private void renderTextLine(T entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, float zOffset, CustomSignBlockEntity.TextLineData lineData, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        if (lineData.getText().isEmpty()) return;
        String text = lineData.getText().trim();

        if (text.startsWith("-rect")) {
            String[] parts = text.split("\\s+");
            if (parts.length >= 3) {
                try {
                    float w = Float.parseFloat(parts[1]);
                    float h = Float.parseFloat(parts[2]);
                    renderRect(matrices, vertexConsumers, zOffset, lineData, w, h, editing, gizmoMode, baseFrame);
                    return;
                } catch (NumberFormatException ignored) {}
            }
        }

        if (text.startsWith("-texture")) {
            String[] parts = text.split("\\s+", 4);
            if (parts.length >= 2) {
                Identifier textureId = Identifier.tryParse(entity.resolvePlaceholders(parts[1]).trim().toLowerCase(Locale.ROOT));
                if (textureId != null && textureId.getPath().endsWith(".png") && textureExists(textureId)) {
                    float xShift = 0f;
                    if (parts.length >= 3) {
                        try { xShift = Float.parseFloat(entity.resolvePlaceholders(parts[2]).trim()); }
                        catch (NumberFormatException ignored) {}
                    }
                    float rotZ = 0f;
                    if (parts.length >= 4) {
                        try { rotZ = Float.parseFloat(entity.resolvePlaceholders(parts[3]).trim()); }
                        catch (NumberFormatException ignored) {}
                    }
                    CustomSignBlockEntity.TextLineData shifted = lineData;
                    if (xShift != 0f || rotZ != 0f) {
                        shifted = lineData.copy();
                        if (xShift != 0f) shifted.setXOffset(lineData.getXOffset() + xShift);
                        if (rotZ != 0f) shifted.setRotZ(lineData.getRotZ() + rotZ);
                    }
                    renderTexture(matrices, vertexConsumers, light, overlay, zOffset, shifted, textureId, editing, gizmoMode, baseFrame);
                }
                return;
            }
        }

        if (text.startsWith("-json")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length >= 2) {
                try {
                    Component jsonText = parseJsonComponent(entity.resolvePlaceholders(parts[1]));
                    if (jsonText != null) {
                        renderJsonText(matrices, vertexConsumers, light, zOffset, lineData, jsonText, editing, gizmoMode, baseFrame);
                        return;
                    }
                } catch (Exception ignored) {}
            }
        }

        renderText(entity, matrices, vertexConsumers, light, zOffset, lineData, editing, gizmoMode, baseFrame);
    }

    private void applyRotation(PoseStack matrices, CustomSignBlockEntity.TextLineData lineData) {
        if (lineData.getRotX() != 0) matrices.mulPose(new org.joml.Matrix4f().rotation(Axis.XP.rotationDegrees(lineData.getRotX())));
        if (lineData.getRotY() != 0) matrices.mulPose(new org.joml.Matrix4f().rotation(Axis.YP.rotationDegrees(lineData.getRotY())));
        if (lineData.getRotZ() != 0) matrices.mulPose(new org.joml.Matrix4f().rotation(Axis.ZP.rotationDegrees(lineData.getRotZ())));
    }

    private void renderJsonText(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float zOffset, CustomSignBlockEntity.TextLineData lineData, Component jsonText, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.pushPose();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);
        applyRotation(matrices, lineData);
        Matrix4f gizmoFrame = editing && gizmoMode >= 0 ? new Matrix4f(matrices.last().pose()) : null;
        matrices.scale(baseScale * lineData.getScaleX(), -baseScale * lineData.getScaleY(), baseScale * lineData.getScaleZ());

        MutableComponent renderText = jsonText.copy();
        if (renderText.getStyle().getFont() == null) {
            renderText = renderText.copy().withStyle(s -> s.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "uniform"))));
        }
        if (lineData.isBold()) renderText = renderText.copy().withStyle(s -> s.withBold(true));
        if (lineData.isItalic()) renderText = renderText.copy().withStyle(s -> s.withItalic(true));
        if (lineData.isUnderline()) renderText = renderText.copy().withStyle(s -> s.withUnderlined(true));

        int textWidth = this.textRenderer.width(renderText);
        int textHeight = this.textRenderer.lineHeight;

        float renderX = switch (lineData.getAlignment().hAlign) {
            case 0 -> 0;
            case 2 -> -textWidth;
            default -> -textWidth / 2.0f;
        };
        float renderY = switch (lineData.getAlignment().vAlign) {
            case 0 -> 0;
            case 2 -> -textHeight;
            default -> -textHeight / 2.0f;
        };

        drawStyledText(renderText, renderX, renderY, lineData, matrices.last().pose(), vertexConsumers, light);

        if (editing) renderEditingOutline(vertexConsumers, matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize(), lineData.getScaleX(), lineData.getScaleY(), 0x00FFFF);
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, renderX, renderY, textWidth, textHeight, 1f, 1f);
        if (gizmoFrame != null) TextGizmo.updateAndRender(gizmoMode, baseFrame, gizmoFrame, lineData, zOffset, textWidth / 2f * baseScale, textHeight / 2f * baseScale, vertexConsumers);

        matrices.popPose();
    }

    private void renderText(T entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light, float zOffset, CustomSignBlockEntity.TextLineData lineData, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.pushPose();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);
        applyRotation(matrices, lineData);
        Matrix4f gizmoFrame = editing && gizmoMode >= 0 ? new Matrix4f(matrices.last().pose()) : null;
        matrices.scale(baseScale * lineData.getScaleX(), -baseScale * lineData.getScaleY(), baseScale * lineData.getScaleZ());

        Style style = Style.EMPTY
                .withBold(lineData.isBold())
                .withItalic(lineData.isItalic())
                .withUnderlined(lineData.isUnderline())
                .withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "uniform")));

        Component renderText = Text.literal(entity.resolvePlaceholders(lineData.getText())).setStyle(style);
        int textWidth = this.textRenderer.width(renderText);
        int textHeight = this.textRenderer.lineHeight;

        float renderX = switch (lineData.getAlignment().hAlign) {
            case 0 -> 0;
            case 2 -> -textWidth;
            default -> -textWidth / 2.0f;
        };
        float renderY = switch (lineData.getAlignment().vAlign) {
            case 0 -> 0;
            case 2 -> -textHeight;
            default -> -textHeight / 2.0f;
        };

        drawStyledText(renderText, renderX, renderY, lineData, matrices.last().pose(), vertexConsumers, light);

        if (editing) renderEditingOutline(vertexConsumers, matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize(), lineData.getScaleX(), lineData.getScaleY(), 0x00FFFF);
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, renderX, renderY, textWidth, textHeight, 1f, 1f);
        if (gizmoFrame != null) TextGizmo.updateAndRender(gizmoMode, baseFrame, gizmoFrame, lineData, zOffset, textWidth / 2f * baseScale, textHeight / 2f * baseScale, vertexConsumers);

        matrices.popPose();
    }

    private void drawStyledText(Component renderText, float x, float y, CustomSignBlockEntity.TextLineData lineData, Matrix4f matrix, MultiBufferSource vertexConsumers, int light) {
        int textLight = lineData.isGlowing() ? FULL_BRIGHT : light;
        int textColor = 0xFF000000 | (lineData.getColor() & 0xFFFFFF);
        int outlineColor = lineData.isOutline() ? (0xFF000000 | (resolveOutlineColor(lineData) & 0xFFFFFF)) : 0;
        pendingTexts.add(new PendingText(new Matrix4f(matrix), x, y, renderText, lineData.isShadow(),
                textColor, outlineColor, textLight));
    }

    private void submitPendingTexts(SubmitNodeCollector collector) {
        for (PendingText pending : pendingTexts) {
            PoseStack pose = new PoseStack();
            pose.last().pose().set(pending.matrix());
            collector.submitText(pose, pending.x(), pending.y(), pending.text().getVisualOrderText(),
                    pending.shadow(), Font.DisplayMode.NORMAL, pending.light(), pending.color(), 0,
                    pending.outlineColor());
        }
        pendingTexts.clear();
    }
    private void renderRect(PoseStack matrices, MultiBufferSource vertexConsumers, float zOffset, CustomSignBlockEntity.TextLineData lineData, float width, float height, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.pushPose();
        float centerX = lineData.getXOffset() / 16f;
        float centerY = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;
        float scale = lineData.getFontSize();
        float halfW = width / 16f / 2f * scale * lineData.getScaleX();
        float halfH = height / 16f / 2f * scale * lineData.getScaleY();

        float offsetX = switch (lineData.getAlignment().hAlign) {
            case 0 -> halfW;
            case 2 -> -halfW;
            default -> 0;
        };
        float offsetY = switch (lineData.getAlignment().vAlign) {
            case 0 -> -halfH;
            case 2 -> halfH;
            default -> 0;
        };

        matrices.translate(centerX + offsetX, centerY + offsetY, zPos);
        applyRotation(matrices, lineData);

        int color = lineData.getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) == 0 ? 255 : (color >> 24) & 0xFF;

        Matrix4f matrix = matrices.last().pose();

        VertexConsumer fill = vertexConsumers.getBuffer(RenderTypes.debugQuads());
        fill.addVertex(matrix, -halfW, -halfH, 0).setColor(r, g, b, a);
        fill.addVertex(matrix, halfW, -halfH, 0).setColor(r, g, b, a);
        fill.addVertex(matrix, halfW, halfH, 0).setColor(r, g, b, a);
        fill.addVertex(matrix, -halfW, halfH, 0).setColor(r, g, b, a);

        if (lineData.isOutline()) {
            flushBatch(vertexConsumers);
            renderEditingOutline(vertexConsumers, matrices, -halfW, -halfH, halfW * 2, halfH * 2, 0.25f, 1f, 1f, resolveOutlineColor(lineData));
        }

        if (editing) renderEditingOutline(vertexConsumers, matrices, -halfW, -halfH, halfW * 2, halfH * 2, 1f, 1f, 1f, 0x00FFFF);
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, -halfW, -halfH, halfW * 2, halfH * 2, 1f, 1f);
        if (editing && gizmoMode >= 0) {
            TextGizmo.updateAndRender(gizmoMode, baseFrame, new Matrix4f(matrices.last().pose()), lineData,
                    zOffset, width / 16f / 2f * lineData.getFontSize(), height / 16f / 2f * lineData.getFontSize(), vertexConsumers);
        }

        matrices.popPose();
    }

    private void renderTexture(PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, float zOffset, CustomSignBlockEntity.TextLineData lineData, Identifier textureId, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.pushPose();

        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);

        float imageSize = 0.4f * lineData.getFontSize();
        float halfWidth = imageSize / 2f * lineData.getScaleX();
        float halfHeight = imageSize / 2f * lineData.getScaleY();

        float offsetX = switch (lineData.getAlignment().hAlign) {
            case 0 -> halfWidth;
            case 2 -> -halfWidth;
            default -> 0;
        };
        float offsetY = switch (lineData.getAlignment().vAlign) {
            case 0 -> -halfHeight;
            case 2 -> halfHeight;
            default -> 0;
        };

        matrices.translate(offsetX, offsetY, 0);
        applyRotation(matrices, lineData);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderTypes.entityCutout(textureId));
        Matrix4f positionMatrix = matrices.last().pose();

        consumer.addVertex(positionMatrix, -halfWidth, -halfHeight, 0).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(matrices.last(), 0, 0, 1);
        consumer.addVertex(positionMatrix, halfWidth, -halfHeight, 0).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(matrices.last(), 0, 0, 1);
        consumer.addVertex(positionMatrix, halfWidth, halfHeight, 0).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(matrices.last(), 0, 0, 1);
        consumer.addVertex(positionMatrix, -halfWidth, halfHeight, 0).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(matrices.last(), 0, 0, 1);

        if (lineData.isOutline()) {
            flushBatch(vertexConsumers);
            renderEditingOutline(vertexConsumers, matrices, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 0.25f, 1f, 1f, resolveOutlineColor(lineData));
        }

        if (editing) renderEditingOutline(vertexConsumers, matrices, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 1f, 1f, 1f, 0x00FFFF);
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 1f, 1f);
        if (editing && gizmoMode >= 0) {
            TextGizmo.updateAndRender(gizmoMode, baseFrame, new Matrix4f(matrices.last().pose()), lineData,
                    zOffset, imageSize / 2f, imageSize / 2f, vertexConsumers);
        }

        matrices.popPose();
    }

    private static void flushBatch(MultiBufferSource vertexConsumers) {
    }
    private static int resolveOutlineColor(CustomSignBlockEntity.TextLineData lineData) {
        int outline = lineData.getOutlineColor();
        if (outline == -1) return 0x000000;
        int outlineRgb = outline & 0xFFFFFF;
        int textRgb = lineData.getColor() & 0xFFFFFF;
        if (outlineRgb != textRgb) return outlineRgb;
        int r = (textRgb >> 16) & 0xFF, g = (textRgb >> 8) & 0xFF, b = textRgb & 0xFF;
        int luma = (r * 299 + g * 587 + b * 114) / 1000;
        return luma >= 128 ? 0x000000 : 0xFFFFFF;
    }
    private void renderEditingOutline(MultiBufferSource vertexConsumers, PoseStack matrices, float left, float top, float w, float h, float unitScale, float scaleX, float scaleY, int rgb) {
        float px = 0.25f / 16f / Math.max(1e-5f, unitScale);
        float thicknessX = px / Math.max(0.01f, scaleX);
        float thicknessY = px / Math.max(0.01f, scaleY);
        float marginX = thicknessX;
        float marginY = thicknessY;
        float x0 = left - marginX, y0 = top - marginY, x1 = left + w + marginX, y1 = top + h + marginY;

        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        int a = 255;
        Matrix4f matrix = matrices.last().pose();

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderTypes.debugQuads());
        addOutlineBar(buffer, matrix, x0, y0, x1, y0 + thicknessY, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y1 - thicknessY, x1, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y0, x0 + thicknessX, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x1 - thicknessX, y0, x1, y1, r, g, b, a);
    }

    private void addOutlineBar(VertexConsumer buffer, Matrix4f matrix, float x0, float y0, float x1, float y1, int r, int g, int b, int a) {
        buffer.addVertex(matrix, x0, y0, 0).setColor(r, g, b, a);
        buffer.addVertex(matrix, x1, y0, 0).setColor(r, g, b, a);
        buffer.addVertex(matrix, x1, y1, 0).setColor(r, g, b, a);
        buffer.addVertex(matrix, x0, y1, 0).setColor(r, g, b, a);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    private static Component parseJsonComponent(String json) {
        try {
            net.minecraft.core.RegistryAccess access = Minecraft.getInstance().level != null
                    ? Minecraft.getInstance().level.registryAccess()
                    : net.minecraft.core.RegistryAccess.EMPTY;
            com.mojang.serialization.DynamicOps<com.google.gson.JsonElement> ops =
                    net.minecraft.resources.RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE, access);
            return net.minecraft.network.chat.ComponentSerialization.CODEC
                    .parse(ops, com.google.gson.JsonParser.parseString(json))
                    .result()
                    .orElse(null);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
