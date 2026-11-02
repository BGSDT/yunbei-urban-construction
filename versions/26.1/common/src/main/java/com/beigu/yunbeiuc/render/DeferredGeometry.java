package com.beigu.yunbeiuc.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.beigu.yunbeiuc.api.render.GeometrySink;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects immediate-mode vertex writes and replays them through
 * {@link SubmitNodeCollector#submitCustomGeometry} at the end of the frame.
 *
 * <p>26.x block entity renderers no longer receive a {@code GeometrySink}; geometry has to be
 * submitted to the collector instead. The existing drawing code in this package still writes
 * vertices the old way, so it draws into this adapter and the adapter forwards everything to the
 * collector in one go. Vertices are captured in world/camera space (the matrix handed to
 * {@code addVertex} is already applied), therefore the geometry is replayed against an identity
 * pose.</p>
 */
public final class DeferredGeometry implements GeometrySink {
    private static final PoseStack IDENTITY = new PoseStack();

    private final Map<RenderType, Batch> batches = new LinkedHashMap<>();

    @Override
    public VertexConsumer getBuffer(RenderType type) {
        return batches.computeIfAbsent(type, key -> new Batch()).consumer;
    }

    public void submit(SubmitNodeCollector collector) {
        for (Map.Entry<RenderType, Batch> entry : batches.entrySet()) {
            Batch batch = entry.getValue();
            if (batch.size == 0) {
                continue;
            }
            collector.submitCustomGeometry(IDENTITY, entry.getKey(), (pose, consumer) -> batch.replay(consumer));
        }
        batches.clear();
    }

    /** One RenderType worth of vertices, stored as flat arrays so replay is allocation free. */
    private static final class Batch {
        private float[] positions = new float[3 * 256];
        private int[] colors = new int[256];
        private float[] uvs = new float[2 * 256];
        private int[] overlays = new int[256];
        private int[] lights = new int[256];
        private float[] normals = new float[3 * 256];
        private boolean[] hasNormal = new boolean[256];
        private int size;

        private int cursor; // vertex being written
        private boolean inVertex;

        private final VertexConsumer consumer = new VertexConsumer() {
            @Override
            public VertexConsumer addVertex(float x, float y, float z) {
                ensure(size + 1);
                positions[size * 3] = x;
                positions[size * 3 + 1] = y;
                positions[size * 3 + 2] = z;
                cursor = size;
                size++;
                inVertex = true;
                return this;
            }

            @Override
            public VertexConsumer setColor(int red, int green, int blue, int alpha) {
                colors[cursor] = (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
                return this;
            }

            @Override
            public VertexConsumer setColor(int color) {
                colors[cursor] = color;
                return this;
            }

            @Override
            public VertexConsumer setUv(float u, float v) {
                uvs[cursor * 2] = u;
                uvs[cursor * 2 + 1] = v;
                return this;
            }

            @Override
            public VertexConsumer setUv1(int u, int v) {
                overlays[cursor] = (u & 0xFFFF) << 16 | (v & 0xFFFF);
                return this;
            }

            @Override
            public VertexConsumer setUv2(int u, int v) {
                lights[cursor] = (u & 0xFFFF) << 16 | (v & 0xFFFF);
                return this;
            }

            @Override
            public VertexConsumer setNormal(float x, float y, float z) {
                normals[cursor * 3] = x;
                normals[cursor * 3 + 1] = y;
                normals[cursor * 3 + 2] = z;
                hasNormal[cursor] = true;
                return this;
            }

            @Override
            public VertexConsumer setLineWidth(float width) {
                // not part of the replay contract
                return this;
            }
        };

        private void ensure(int capacity) {
            if (capacity * 3 <= positions.length) {
                return;
            }
            int grown = Math.max(capacity, positions.length / 3) * 2;
            positions = java.util.Arrays.copyOf(positions, grown * 3);
            normals = java.util.Arrays.copyOf(normals, grown * 3);
            colors = java.util.Arrays.copyOf(colors, grown);
            uvs = java.util.Arrays.copyOf(uvs, grown * 2);
            overlays = java.util.Arrays.copyOf(overlays, grown);
            lights = java.util.Arrays.copyOf(lights, grown);
            hasNormal = java.util.Arrays.copyOf(hasNormal, grown);
        }

        private void replay(VertexConsumer out) {
            for (int i = 0; i < size; i++) {
                out.addVertex(positions[i * 3], positions[i * 3 + 1], positions[i * 3 + 2]);
                out.setColor(colors[i]);
                out.setUv(uvs[i * 2], uvs[i * 2 + 1]);
                out.setUv1((overlays[i] >> 16) & 0xFFFF, overlays[i] & 0xFFFF);
                out.setUv2((lights[i] >> 16) & 0xFFFF, lights[i] & 0xFFFF);
                if (hasNormal[i]) {
                    out.setNormal(normals[i * 3], normals[i * 3 + 1], normals[i * 3 + 2]);
                }
            }
        }
    }

    /** Convenience for the renderers: apply the current pose and write a vertex. */
    public static void vertex(VertexConsumer consumer, PoseStack matrices, float x, float y, float z) {
        Matrix4f matrix = matrices.last().pose();
        consumer.addVertex(matrix, x, y, z);
    }
}
