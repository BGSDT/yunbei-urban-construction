package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public final class TextGizmo {
    public static final int MODE_POSITION = 0;
    public static final int MODE_ROTATION = 1;
    public static final int MODE_SCALE = 2;

    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;
    public static final int ROT_X = 10;
    public static final int ROT_Y = 11;
    public static final int ROT_Z = 12;
    public static final int CORNER_TL = 20;
    public static final int CORNER_TR = 21;
    public static final int CORNER_BR = 22;
    public static final int CORNER_BL = 23;
    public static final int EDGE_TOP = 30;
    public static final int EDGE_RIGHT = 31;
    public static final int EDGE_BOTTOM = 32;
    public static final int EDGE_LEFT = 33;

    public static final int KIND_AXIS = 0;
    public static final int KIND_ROT = 1;
    public static final int KIND_CORNER = 2;
    public static final int KIND_EDGE = 3;

    private static final float PICK_RADIUS_SQ = 144f;
    private static final long FRESH_MS = 500L;
    private static final float GIZMO_SIZE = 0.5f;

    public static int hoverId = -1;
    public static int grabId = -1;

    private static Matrix4f frame;
    private static Matrix4f frameInv;
    private static Matrix4f baseFrameInv;
    private static Matrix4f proj;
    private static float bs = 1f;
    private static float halfWpx;
    private static float halfHpx;
    private static float halfWbBlocks;
    private static float halfHbBlocks;
    private static float offPxX;
    private static float offPxY;
    private static float offPxZ;
    private static long updateTime;

    private static final List<H> HANDLES = new ArrayList<>();

    private static final List<LineRect> LINE_RECTS = new ArrayList<>();
    private static boolean rectsActive = false;
    private static long rectsTime = 0L;

    private static final class LineRect {
        final int index;
        final float minX, minY, maxX, maxY, depth;

        LineRect(int index, float minX, float minY, float maxX, float maxY, float depth) {
            this.index = index; this.minX = minX; this.minY = minY; this.maxX = maxX; this.maxY = maxY; this.depth = depth;
        }
    }

    public static void beginFrameRects() {
        LINE_RECTS.clear();
        rectsActive = true;
        rectsTime = System.currentTimeMillis();
    }

    public static void clearRects() {
        LINE_RECTS.clear();
        rectsActive = false;
    }

    private TextGizmo() {}

    private static final class H {
        final int id;
        final int kind;
        final int axis;
        final float lx;
        final float ly;

        H(int id, int kind, int axis, float lx, float ly) {
            this.id = id; this.kind = kind; this.axis = axis; this.lx = lx; this.ly = ly;
        }
    }

    public static void clear() {
        HANDLES.clear();
        frame = null;
        frameInv = null;
        baseFrameInv = null;
        proj = null;
        hoverId = -1;
        grabId = -1;
        clearRects();
    }

    public static boolean isFresh() {
        return frame != null && System.currentTimeMillis() - updateTime < FRESH_MS;
    }

    public static int handleKind(int id) {
        if (id < 0) return -1;
        if (id < 10) return KIND_AXIS;
        if (id < 20) return KIND_ROT;
        if (id < 30) return KIND_CORNER;
        return KIND_EDGE;
    }

    public static int handleAxis(int id) {
        return switch (id) {
            case AXIS_X, ROT_X, EDGE_LEFT, EDGE_RIGHT -> 0;
            case AXIS_Y, ROT_Y, EDGE_TOP, EDGE_BOTTOM -> 1;
            default -> 2;
        };
    }

    public static float halfWidthPx() { return halfWpx; }

    public static float halfHeightPx() { return halfHpx; }

    public static float halfWidthBlocks() { return halfWbBlocks; }

    public static float halfHeightBlocks() { return halfHbBlocks; }

    public static void updateAndRender(int mode, Matrix4f baseFrame, Matrix4f lineFrame, TextLineData d, float zOffsetBlocks, float halfWBlocks, float halfHBlocks) {
        Matrix4f pm = RenderSystem.getProjectionMatrix();
        if (pm == null || d == null || lineFrame == null || baseFrame == null) return;
        bs = 0.05f * d.getFontSize();
        if (bs <= 1e-6f) return;
        frame = new Matrix4f(lineFrame);
        try {
            frameInv = new Matrix4f(lineFrame);
            baseFrameInv = new Matrix4f(baseFrame);
            if (!frameInv.invert() || !baseFrameInv.invert()) throw new IllegalStateException("Non-invertible text transform");
        } catch (RuntimeException e) {
            frameInv = null;
            baseFrameInv = null;
        }
        proj = new Matrix4f(pm);
        halfWpx = halfWBlocks / bs;
        halfHpx = halfHBlocks / bs;
        halfWbBlocks = halfWBlocks;
        halfHbBlocks = halfHBlocks;
        offPxX = d.getXOffset();
        offPxY = d.getYOffset();
        offPxZ = d.getZOffset();
        updateTime = System.currentTimeMillis();

        HANDLES.clear();
        switch (mode) {
            case MODE_SCALE -> {
                HANDLES.add(new H(CORNER_TL, KIND_CORNER, 0, -halfWpx, -halfHpx));
                HANDLES.add(new H(CORNER_TR, KIND_CORNER, 0, halfWpx, -halfHpx));
                HANDLES.add(new H(CORNER_BR, KIND_CORNER, 0, halfWpx, halfHpx));
                HANDLES.add(new H(CORNER_BL, KIND_CORNER, 0, -halfWpx, halfHpx));
                HANDLES.add(new H(EDGE_TOP, KIND_EDGE, 1, 0f, -halfHpx));
                HANDLES.add(new H(EDGE_BOTTOM, KIND_EDGE, 1, 0f, halfHpx));
                HANDLES.add(new H(EDGE_LEFT, KIND_EDGE, 0, -halfWpx, 0f));
                HANDLES.add(new H(EDGE_RIGHT, KIND_EDGE, 0, halfWpx, 0f));
            }
            case MODE_POSITION -> {
                HANDLES.add(new H(AXIS_X, KIND_AXIS, 0, 0f, 0f));
                HANDLES.add(new H(AXIS_Y, KIND_AXIS, 1, 0f, 0f));
                HANDLES.add(new H(AXIS_Z, KIND_AXIS, 2, 0f, 0f));
            }
            case MODE_ROTATION -> {
                HANDLES.add(new H(ROT_X, KIND_ROT, 0, 0f, 0f));
                HANDLES.add(new H(ROT_Y, KIND_ROT, 1, 0f, 0f));
                HANDLES.add(new H(ROT_Z, KIND_ROT, 2, 0f, 0f));
            }
            default -> {
                return;
            }
        }

        draw(mode, d);
    }

    public static int pick(double mx, double my, float sx, float sy, float sz) {
        if (!isFresh() || HANDLES.isEmpty()) return -1;
        int best = -1;
        float bestD = PICK_RADIUS_SQ;
        for (H h : HANDLES) {
            float dd = Float.MAX_VALUE;
            switch (h.kind) {
                case KIND_AXIS -> {
                    Vector3f c = center();
                    Vector3f tip = new Vector3f(c).add(new Vector3f(axisDirView(h.axis)).mul(GIZMO_SIZE));
                    float[] a = projectGui(c.x, c.y, c.z);
                    float[] bq = projectGui(tip.x, tip.y, tip.z);
                    if (a != null && bq != null) dd = distSegSq(mx, my, a[0], a[1], bq[0], bq[1]);
                }
                case KIND_ROT -> {
                    Vector3f c = center();
                    Vector3f n = axisDirView(h.axis);
                    Vector3f b1 = perp(n);
                    Vector3f b2 = new Vector3f(n).cross(b1);
                    int seg = 40;
                    for (int i = 0; i < seg; i++) {
                        Vector3f p = ringPoint(c, b1, b2, GIZMO_SIZE, (float) (i * Math.PI * 2.0 / seg));
                        float[] g = projectGui(p.x, p.y, p.z);
                        if (g == null) continue;
                        float dx = (float) mx - g[0], dy = (float) my - g[1];
                        float d2 = dx * dx + dy * dy;
                        if (d2 < dd) dd = d2;
                    }
                }
                default -> {
                    Vector3f p = transformPosition(frame, new Vector3f(h.lx * bs * sx, -h.ly * bs * sy, 0f));
                    float[] g = projectGui(p.x, p.y, p.z);
                    if (g != null) {
                        float dx = (float) mx - g[0], dy = (float) my - g[1];
                        dd = dx * dx + dy * dy;
                    }
                }
            }
            if (dd < bestD) {
                bestD = dd;
                best = h.id;
            }
        }
        return best;
    }

    public static Float axisDragValue(int axis, double mx, double my) {
        if (!isFresh() || baseFrameInv == null) return null;
        Vector4f[] ray = viewRayPoints(mx, my);
        if (ray == null) return null;
        Vector4f ob4 = transform(baseFrameInv, new Vector4f(ray[0].x, ray[0].y, ray[0].z, 1f));
        if (Math.abs(ob4.w) < 1e-9f) return null;
        ob4.div(ob4.w);
        Vector3f db = transformDirection(baseFrameInv, new Vector3f(ray[1].x - ray[0].x, ray[1].y - ray[0].y, ray[1].z - ray[0].z));
        if (db.lengthSquared() < 1e-12f) return null;
        db.normalize();
        Vector3f u = transformDirection(baseFrameInv, axisDirView(axis));
        if (u.lengthSquared() < 1e-10f) return null;
        u.normalize();
        Vector3f cb = new Vector3f(offPxX / 16f, offPxY / 16f, offPxZ / 16f);
        Vector3f w0 = new Vector3f(ob4.x, ob4.y, ob4.z).sub(cb);
        float b = db.dot(u);
        float dd = db.dot(w0);
        float e = u.dot(w0);
        float denom = 1f - b * b;
        if (Math.abs(denom) < 1e-3f) return null;
        float t = (e - b * dd) / denom;
        Vector3f target = new Vector3f(u).mul(t).add(cb);
        float tau = target.dot(u) * 16f;
        return Math.max(-2048f, Math.min(2048f, tau));
    }

    public static Float rotationDragAngle(int axis, double mx, double my) {
        if (!isFresh()) return null;
        Vector4f[] ray = viewRayPoints(mx, my);
        if (ray == null) return null;
        Vector3f o = new Vector3f(ray[0].x, ray[0].y, ray[0].z);
        Vector3f dir = new Vector3f(ray[1].x - ray[0].x, ray[1].y - ray[0].y, ray[1].z - ray[0].z);
        if (dir.lengthSquared() < 1e-12f) return null;
        dir.normalize();
        Vector3f c = center();
        Vector3f n = axisDirView(axis);
        float dn = dir.dot(n);
        if (Math.abs(dn) < 1e-4f) return null;
        Vector3f co = new Vector3f(c).sub(o);
        float t = co.dot(n) / dn;
        Vector3f v = new Vector3f(dir).mul(t).sub(co);
        if (v.lengthSquared() < 1e-10f) return null;
        Vector3f b1 = perp(n);
        Vector3f b2 = new Vector3f(n).cross(b1);
        return (float) Math.toDegrees(Math.atan2(v.dot(b2), v.dot(b1)));
    }

    public static float[] scaleDragPoint(double mx, double my) {
        if (!isFresh() || frameInv == null) return null;
        Vector4f[] ray = viewRayPoints(mx, my);
        if (ray == null) return null;
        Vector3f dv = new Vector3f(ray[1].x - ray[0].x, ray[1].y - ray[0].y, ray[1].z - ray[0].z);
        if (dv.lengthSquared() < 1e-12f) return null;
        dv.normalize();
        Vector4f of = transform(frameInv, new Vector4f(ray[0].x, ray[0].y, ray[0].z, 1f));
        if (Math.abs(of.w) < 1e-9f) return null;
        of.div(of.w);
        Vector3f df = transformDirection(frameInv, new Vector3f(dv));
        if (Math.abs(df.z) < 1e-6f) return null;
        float t = -of.z / df.z;
        return new float[]{of.x + t * df.x, of.y + t * df.y};
    }

    private static void draw(int mode, TextLineData d) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buf = tessellator.getBuilder();
        buf.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f m = new Matrix4f();
        m.setIdentity();
        Vector3f center = center();

        if (mode == MODE_POSITION || mode == MODE_ROTATION) {
            if (mode == MODE_POSITION) {
                axis(buf, m, center, axisDirView(0), GIZMO_SIZE, AXIS_X, 253, 48, 67);
                axis(buf, m, center, axisDirView(1), GIZMO_SIZE, AXIS_Y, 38, 236, 69);
                axis(buf, m, center, axisDirView(2), GIZMO_SIZE, AXIS_Z, 45, 94, 232);
                disc(buf, m, center, 0.035f, 255, 255, 255, 210);
            } else {
                ring(buf, m, center, axisDirView(0), GIZMO_SIZE, ROT_X, 253, 48, 67);
                ring(buf, m, center, axisDirView(1), GIZMO_SIZE, ROT_Y, 38, 236, 69);
                ring(buf, m, center, axisDirView(2), GIZMO_SIZE, ROT_Z, 45, 94, 232);
            }
        } else if (mode == MODE_SCALE) {
            for (H h : HANDLES) {
                Vector3f p = transformPosition(frame, new Vector3f(h.lx * bs * d.getScaleX(), -h.ly * bs * d.getScaleY(), 0f));
                boolean hot = h.id == hoverId || h.id == grabId;
                disc(buf, m, p, hot ? 0.065f : 0.05f, 255, hot ? 255 : 214, hot ? 90 : 40, 255);
            }
        }

        BufferUploader.end(buf);

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static Vector4f[] viewRayPoints(double mx, double my) {
        if (proj == null) return null;
        var win = Minecraft.getInstance().getWindow();
        if (win.getGuiScaledWidth() <= 0 || win.getGuiScaledHeight() <= 0) return null;
        float ndcX = (float) mx / win.getGuiScaledWidth() * 2f - 1f;
        float ndcY = 1f - (float) my / win.getGuiScaledHeight() * 2f;
        try {
            Matrix4f invP = new Matrix4f(proj);
            if (!invP.invert()) return null;
            Vector4f n = transform(invP, new Vector4f(ndcX, ndcY, -1f, 1f));
            Vector4f f = transform(invP, new Vector4f(ndcX, ndcY, 1f, 1f));
            if (Math.abs(n.w) < 1e-9f || Math.abs(f.w) < 1e-9f) return null;
            n.div(n.w);
            f.div(f.w);
            return new Vector4f[]{n, f};
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static Vector3f center() {
        return transformPosition(frame, new Vector3f());
    }

    public static void addLineRect(Matrix4f viewMatrix, int index, float lx0, float ly0, float lw, float lh, float unitX, float unitY) {
        if (!rectsActive || proj == null) return;
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, depth = 0f;
        for (int k = 0; k < 4; k++) {
            float lx = lx0 + (k & 1) * lw;
            float ly = ly0 + ((k >> 1) & 1) * lh;
            Vector3f v = transformPosition(viewMatrix, new Vector3f(lx * unitX, ly * unitY, 0f));
            Vector4f p = new Vector4f(v.x, v.y, v.z, 1f);
            transformInPlace(proj, p);
            if (p.w < 0.05f) return;
            depth += p.w;
            var win = Minecraft.getInstance().getWindow();
            float gx = (p.x / p.w * 0.5f + 0.5f) * win.getGuiScaledWidth();
            float gy = (0.5f - p.y / p.w * 0.5f) * win.getGuiScaledHeight();
            minX = Math.min(minX, gx);
            minY = Math.min(minY, gy);
            maxX = Math.max(maxX, gx);
            maxY = Math.max(maxY, gy);
        }
        LINE_RECTS.add(new LineRect(index, minX - 1f, minY - 1f, maxX + 1f, maxY + 1f, depth / 4f));
    }

    public static int pickLine(double mx, double my) {
        if (!rectsActive || LINE_RECTS.isEmpty()) return -1;
        if (System.currentTimeMillis() - rectsTime > FRESH_MS) return -1;
        int best = -1;
        float bestDepth = Float.MAX_VALUE;
        for (LineRect r : LINE_RECTS) {
            if (mx >= r.minX && mx <= r.maxX && my >= r.minY && my <= r.maxY && r.depth < bestDepth) {
                bestDepth = r.depth;
                best = r.index;
            }
        }
        return best;
    }

    private static float[] projectGui(float vx, float vy, float vz) {
        if (proj == null) return null;
        Vector4f p = new Vector4f(vx, vy, vz, 1f);
        transformInPlace(proj, p);
        if (p.w < 0.05f) return null;
        var win = Minecraft.getInstance().getWindow();
        return new float[]{
                (p.x / p.w * 0.5f + 0.5f) * win.getGuiScaledWidth(),
                (0.5f - p.y / p.w * 0.5f) * win.getGuiScaledHeight()
        };
    }

    private static float distSegSq(double px, double py, float ax, float ay, float bx, float by) {
        float abx = bx - ax, aby = by - ay;
        float lenSq = abx * abx + aby * aby;
        float t = lenSq < 1e-9f ? 0f : (float) (((px - ax) * abx + (py - ay) * aby) / lenSq);
        t = Math.max(0f, Math.min(1f, t));
        float dx = (float) px - (ax + t * abx), dy = (float) py - (ay + t * aby);
        return dx * dx + dy * dy;
    }

    private static Vector3f axisDirView(int axis) {
        Vector3f v = switch (axis) {
            case 0 -> new Vector3f(1f, 0f, 0f);
            case 1 -> new Vector3f(0f, 1f, 0f);
            default -> new Vector3f(0f, 0f, 1f);
        };
        transformDirection(frame, v);
        return v.normalize();
    }

    private static void axis(BufferBuilder buf, Matrix4f m, Vector3f c, Vector3f dir, float len, int id, int r, int g, int b) {
        boolean hot = id == hoverId || id == grabId;
        Vector3f tip = new Vector3f(dir).mul(len).add(c);
        ribbon(buf, m, c, tip, 0.25f / 16f, r, g, b, hot ? 255 : 215);
        disc(buf, m, tip, hot ? 0.075f : 0.055f, r, g, b, 255);
    }

    private static void ring(BufferBuilder buf, Matrix4f m, Vector3f c, Vector3f n, float radius, int id, int r, int g, int b) {
        boolean hot = id == hoverId || id == grabId;
        float th = 0.25f / 16f;
        Vector3f b1 = perp(n);
        Vector3f b2 = new Vector3f(n).cross(b1);
        arc(buf, m, c, b1, b2, radius, 0f, (float) Math.PI, th, hot ? 255 : r, hot ? 255 : g, hot ? 120 : b, 255);
        arc(buf, m, c, b1, b2, radius, (float) Math.PI, (float) (Math.PI * 2), th, hot ? 225 : 150, hot ? 225 : 150, hot ? 225 : 150, hot ? 230 : 175);
    }

    private static void arc(BufferBuilder buf, Matrix4f m, Vector3f c, Vector3f b1, Vector3f b2, float radius, float a0, float a1, float th, int r, int g, int b, int a) {
        int seg = 24;
        Vector3f prev = ringPoint(c, b1, b2, radius, a0);
        for (int i = 1; i <= seg; i++) {
            Vector3f p = ringPoint(c, b1, b2, radius, a0 + (a1 - a0) * i / seg);
            ribbon(buf, m, prev, p, th, r, g, b, a);
            prev = p;
        }
    }

    private static Vector3f ringPoint(Vector3f c, Vector3f b1, Vector3f b2, float radius, float a) {
        return new Vector3f(b1).mul((float) Math.cos(a) * radius)
                .add(new Vector3f(b2).mul((float) Math.sin(a) * radius))
                .add(c);
    }

    private static Vector3f perp(Vector3f n) {
        Vector3f a = Math.abs(n.y) < 0.9f ? new Vector3f(0f, 1f, 0f) : new Vector3f(1f, 0f, 0f);
        Vector3f p = new Vector3f(n).cross(a);
        if (p.lengthSquared() < 1e-8f) p.set(1f, 0f, 0f);
        else p.normalize();
        return p;
    }

    private static void ribbon(BufferBuilder buf, Matrix4f m, Vector3f p0, Vector3f p1, float th, int r, int g, int b, int a) {
        Vector3f dir = new Vector3f(p1).sub(p0);
        if (dir.lengthSquared() < 1e-12f) return;
        dir.normalize();
        Vector3f mid = new Vector3f(p0).add(p1).mul(0.5f);
        Vector3f viewDir = mid.lengthSquared() < 1e-10f ? new Vector3f(0f, 0f, -1f) : mid.normalize();
        Vector3f side = new Vector3f(dir).cross(viewDir);
        if (side.lengthSquared() < 1e-10f) side.set(perp(dir));
        else side.normalize();
        side.mul(th);
        Vector3f a0 = new Vector3f(p0).sub(side);
        Vector3f a1 = new Vector3f(p0).add(side);
        Vector3f b0 = new Vector3f(p1).sub(side);
        Vector3f b1 = new Vector3f(p1).add(side);
        tri(buf, m, a0, b0, b1, r, g, b, a);
        tri(buf, m, a0, b1, a1, r, g, b, a);
    }

    private static void disc(BufferBuilder buf, Matrix4f m, Vector3f c, float radius, int r, int g, int b, int a) {
        Vector3f vd = new Vector3f(c);
        if (vd.lengthSquared() < 1e-10f) vd.set(0f, 0f, -1f);
        else vd.normalize();
        Vector3f e1 = perp(vd);
        Vector3f e2 = new Vector3f(vd).cross(e1);
        int seg = 14;
        Vector3f prev = new Vector3f(e1).mul(radius);
        for (int i = 1; i <= seg; i++) {
            float an = (float) (i * Math.PI * 2.0 / seg);
            Vector3f cur = new Vector3f(e1).mul((float) Math.cos(an) * radius)
                    .add(new Vector3f(e2).mul((float) Math.sin(an) * radius));
            tri(buf, m, c, new Vector3f(c).add(prev), new Vector3f(c).add(cur), r, g, b, a);
            prev = cur;
        }
    }

    private static void tri(BufferBuilder buf, Matrix4f m, Vector3f p0, Vector3f p1, Vector3f p2, int r, int g, int b, int a) {
        vert(buf, m, p0, r, g, b, a);
        vert(buf, m, p1, r, g, b, a);
        vert(buf, m, p2, r, g, b, a);
    }

    private static void vert(BufferBuilder buf, Matrix4f m, Vector3f p, int r, int g, int b, int a) {
        buf.vertex(m, p.x, p.y, p.z).color(r, g, b, a).endVertex();
    }

    private static Vector4f transform(Matrix4f matrix, Vector4f value) {
        transformInPlace(matrix, value);
        return value;
    }

    private static void transformInPlace(Matrix4f matrix, Vector4f value) {
        com.mojang.math.Vector4f result = new com.mojang.math.Vector4f(value.x, value.y, value.z, value.w);
        result.transform(matrix);
        value.x = result.x();
        value.y = result.y();
        value.z = result.z();
        value.w = result.w();
    }

    private static Vector3f transformPosition(Matrix4f matrix, Vector3f value) {
        Vector4f result = transform(matrix, new Vector4f(value.x, value.y, value.z, 1f));
        return new Vector3f(result.x / result.w, result.y / result.w, result.z / result.w);
    }

    private static Vector3f transformDirection(Matrix4f matrix, Vector3f value) {
        Vector4f result = transform(matrix, new Vector4f(value.x, value.y, value.z, 0f));
        return value.set(result.x, result.y, result.z);
    }

    private static final class Vector4f {
        float x, y, z, w;

        Vector4f(float x, float y, float z, float w) {
            this.x = x; this.y = y; this.z = z; this.w = w;
        }

        void div(float value) {
            x /= value; y /= value; z /= value; w /= value;
        }
    }

    private static final class Vector3f {
        float x, y, z;

        Vector3f() { this(0f, 0f, 0f); }
        Vector3f(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
        Vector3f(Vector3f other) { this(other.x, other.y, other.z); }

        Vector3f set(float x, float y, float z) { this.x = x; this.y = y; this.z = z; return this; }
        Vector3f set(Vector3f other) { return set(other.x, other.y, other.z); }
        Vector3f add(Vector3f other) { x += other.x; y += other.y; z += other.z; return this; }
        Vector3f sub(Vector3f other) { x -= other.x; y -= other.y; z -= other.z; return this; }
        Vector3f mul(float value) { x *= value; y *= value; z *= value; return this; }
        float dot(Vector3f other) { return x * other.x + y * other.y + z * other.z; }
        float lengthSquared() { return dot(this); }
        Vector3f normalize() { float length = (float) Math.sqrt(lengthSquared()); return length > 0f ? mul(1f / length) : this; }
        Vector3f cross(Vector3f other) {
            float newX = y * other.z - z * other.y;
            float newY = z * other.x - x * other.z;
            float newZ = x * other.y - y * other.x;
            return set(newX, newY, newZ);
        }
    }
}
