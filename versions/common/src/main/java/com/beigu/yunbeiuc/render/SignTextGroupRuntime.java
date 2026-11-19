package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * 「轮播容器 / 滚动容器」的运行期逻辑。
 *
 * <p>轮播容器：组内文本行按停留时间循环显示其中一行。</p>
 * <p>滚动容器：组内文本行拼接成一条内容，在容器宽度内按速度/方向循环滚动。
 * 容器位置由组内第一行决定，组内各行的位置都是相对容器的坐标。</p>
 */
public final class SignTextGroupRuntime {

    private SignTextGroupRuntime() {}

    /** 1 个字体像素对应的偏移单位（偏移 16 = 1 格，字体 1 像素 = 0.05 * 字号 格）。 */
    public static final float UNITS_PER_FONT_PIXEL = 0.8f;
    /** 滚动内容里相邻两行之间的间距（偏移单位）。 */
    public static final float SCROLL_ITEM_GAP = 16f;
    /** 字体行高（像素）。 */
    public static final float FONT_LINE_HEIGHT_PX = 9f;
    /** 图片行渲染出来的边长（格）。 */
    private static final float TEXTURE_SIZE_BLOCKS = 0.4f;
    /** 同一行在窗口里最多重复绘制的份数（内容比窗口短时用重复份数铺满）。 */
    private static final int MAX_SCROLL_COPIES = 24;

    /** 一组文本行的渲染坐标（已换成相对偏移单位）。 */
    public static final class Placement {
        private final boolean visible;
        private final float x, y, z;

        public Placement(boolean visible, float x, float y, float z) {
            this.visible = visible;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public boolean visible() { return visible; }
        public float x() { return x; }
        public float y() { return y; }
        public float z() { return z; }
    }

    public static int groupStart(List<TextLineData> lines, int index) {
        if (lines == null || index < 0 || index >= lines.size()) return -1;
        int groupId = lines.get(index).getGroupId();
        if (groupId <= 0) return -1;
        int start = index;
        while (start > 0 && lines.get(start - 1).getGroupId() == groupId) start--;
        return start;
    }

    public static int groupEnd(List<TextLineData> lines, int index) {
        if (lines == null || index < 0 || index >= lines.size()) return -1;
        int groupId = lines.get(index).getGroupId();
        if (groupId <= 0) return -1;
        int end = index;
        while (end + 1 < lines.size() && lines.get(end + 1).getGroupId() == groupId) end++;
        return end;
    }

    public static boolean isScrollGroupAt(List<TextLineData> lines, int index) {
        int start = groupStart(lines, index);
        return start >= 0 && lines.get(start).getContainerMode() == TextLineData.CONTAINER_SCROLL;
    }

    public static int activeIndex(List<TextLineData> lines, int index, long nowMs) {
        int start = groupStart(lines, index);
        if (start < 0) return index;
        if (lines.get(start).getContainerMode() == TextLineData.CONTAINER_SCROLL) return index;
        int end = groupEnd(lines, index);
        int count = end - start + 1;
        if (count <= 1) return start;
        int interval = Math.max(TextLineData.MIN_GROUP_INTERVAL_MS, lines.get(start).getGroupIntervalMs());
        long slot = Math.floorDiv(Math.max(0L, nowMs), (long) interval);
        int offset = (int) Math.floorMod(slot, (long) count);
        return start + offset;
    }

    public static boolean isRenderableAt(List<TextLineData> lines, int index) {
        return isRenderableAt(lines, index, System.currentTimeMillis());
    }

    public static boolean isRenderableAt(List<TextLineData> lines, int index, long nowMs) {
        if (lines == null || index < 0 || index >= lines.size()) return false;
        if (lines.get(index).getGroupId() <= 0) return true;
        return activeIndex(lines, index, nowMs) == index;
    }

    public static boolean isActiveInGroup(List<TextLineData> lines, int index) {
        if (lines == null || index < 0 || index >= lines.size()) return false;
        if (lines.get(index).getGroupId() <= 0) return false;
        return activeIndex(lines, index, System.currentTimeMillis()) == index;
    }

    /** 返回 null 表示该行不在滚动容器内；返回空列表表示当前帧该行不可见。 */
    public static List<Placement> scrollPlacements(List<TextLineData> lines, int index, long nowMs,
                                                   UnaryOperator<String> resolve,
                                                   ToIntFunction<String> widthPx) {
        int start = groupStart(lines, index);
        if (start < 0) return null;
        TextLineData leader = lines.get(start);
        if (leader.getContainerMode() != TextLineData.CONTAINER_SCROLL) return null;
        int end = groupEnd(lines, index);
        TextLineData line = lines.get(index);
        boolean isLeader = index == start;

        float window = leader.getContainerWidth();
        // 速度以「格/秒」存储，换算成偏移单位（1 格 = 16 单位）。
        float speed = leader.getScrollSpeed() * 16f;
        int dir = leader.getScrollDirection();
        boolean horizontal = dir == TextLineData.SCROLL_LEFT || dir == TextLineData.SCROLL_RIGHT;
        // 向右/向上为正方向：内容整体沿正方向平移，反之沿负方向平移。
        boolean increasing = dir == TextLineData.SCROLL_RIGHT || dir == TextLineData.SCROLL_UP;

        int count = end - start + 1;
        float[] sizes = new float[count];
        float contentLength = 0f;
        for (int k = start; k <= end; k++) {
            TextLineData member = lines.get(k);
            float size = horizontal ? measureWidth(resolve, widthPx, member) : measureHeight(resolve, member);
            sizes[k - start] = size;
            contentLength += size;
            if (k < end) contentLength += SCROLL_ITEM_GAP;
        }

        // 每行在容器内容里的起点（沿滚动方向，方向为正）。
        float itemStart = 0f;
        for (int k = start; k < index; k++) itemStart += sizes[k - start] + SCROLL_ITEM_GAP;
        float itemSize = sizes[index - start];

        // 内容按「内容长度 + 行间距」为一个循环周期重复，保证首尾衔接处位置连续。
        float loop = contentLength + SCROLL_ITEM_GAP;
        if (loop <= 1e-3f) loop = 1f;
        double travelled = Math.max(0L, nowMs) / 1000.0 * speed;
        float phase = (float) (travelled % loop);
        if (phase < 0f) phase += loop;

        // 只有「滚动方向」上的位置由容器首行决定（首行即容器起点），
        // 其余方向（水平滚动时的 Y/Z、垂直滚动时的 X/Z）每一行都用自己的坐标，
        // 这样改一行的位置不会牵动组里其他文本。
        float relX = line.getXOffset();
        float relY = line.getYOffset();
        float relZ = line.getZOffset();
        float alongRel = isLeader ? 0f : (horizontal ? relX : relY);

        // 正方向滚动时内容从窗口负侧进入，负方向滚动时从窗口正侧进入；
        // 之后按 phase 匀速平移，循环时整体位移一个 loop，重复份数让位移不可见。
        float base = (increasing ? -window / 2f : window / 2f)
                + itemStart + alongRel
                + (increasing ? phase : -phase);

        float anchorX = leader.getXOffset();
        float anchorY = leader.getYOffset();
        int hAlign = line.getAlignment().hAlign;
        int vAlign = line.getAlignment().vAlign;

        float halfWindow = window / 2f;
        int firstCopy = (int) Math.ceil((-halfWindow - itemSize - base) / loop);
        int lastCopy = (int) Math.floor((halfWindow - base) / loop);
        if (lastCopy - firstCopy + 1 > MAX_SCROLL_COPIES) {
            lastCopy = firstCopy + MAX_SCROLL_COPIES - 1;
        }

        List<Placement> out = new ArrayList<>(Math.max(0, lastCopy - firstCopy + 1));
        for (int copy = firstCopy; copy <= lastCopy; copy++) {
            float boxStart = base + copy * loop;
            // 盒子 [boxStart, boxStart + itemSize] 与窗口 [-halfWindow, +halfWindow] 相交才绘制。
            if (boxStart >= halfWindow || boxStart + itemSize <= -halfWindow) continue;

            float x, y;
            if (horizontal) {
                // 滚动方向用自动排版坐标；垂直方向直接用这一行自己的 Y。
                x = anchorX + boxStart + alignX(hAlign, itemSize);
                y = relY;
            } else {
                x = relX;
                y = anchorY + boxStart + alignY(vAlign, itemSize);
            }
            out.add(new Placement(true, x, y, relZ));
        }
        return out.isEmpty() ? Collections.emptyList() : out;
    }

    /**
     * 编辑预览用的静态坐标：按组内顺序把内容沿正方向排开，不做窗口裁剪、不随时间移动。
     * 返回 null 表示该行不在滚动容器内。
     */
    public static Placement editPlacement(List<TextLineData> lines, int index,
                                          UnaryOperator<String> resolve, ToIntFunction<String> widthPx) {
        int start = groupStart(lines, index);
        if (start < 0) return null;
        TextLineData leader = lines.get(start);
        if (leader.getContainerMode() != TextLineData.CONTAINER_SCROLL) return null;
        int end = groupEnd(lines, index);
        TextLineData line = lines.get(index);
        boolean isLeader = index == start;

        int dir = leader.getScrollDirection();
        boolean horizontal = dir == TextLineData.SCROLL_LEFT || dir == TextLineData.SCROLL_RIGHT;
        float itemStart = 0f;
        float itemSize = 0f;
        for (int k = start; k <= end; k++) {
            TextLineData member = lines.get(k);
            float size = horizontal ? measureWidth(resolve, widthPx, member) : measureHeight(resolve, member);
            if (k < index) itemStart += size + SCROLL_ITEM_GAP;
            if (k == index) itemSize = size;
        }

        float relX = line.getXOffset();
        float relY = line.getYOffset();
        float relZ = line.getZOffset();
        float alongRel = isLeader ? 0f : (horizontal ? relX : relY);
        float boxStart = itemStart + alongRel;

        float anchorX = leader.getXOffset();
        float anchorY = leader.getYOffset();
        int hAlign = line.getAlignment().hAlign;
        int vAlign = line.getAlignment().vAlign;
        float x, y;
        if (horizontal) {
            x = anchorX + boxStart + alignX(hAlign, itemSize);
            y = relY;
        } else {
            x = relX;
            y = anchorY + boxStart + alignY(vAlign, itemSize);
        }
        return new Placement(true, x, y, relZ);
    }

    private static float measureWidth(UnaryOperator<String> resolve, ToIntFunction<String> widthPx, TextLineData line) {
        String text = resolvedLineText(resolve, line);
        if (text == null) return 0f;
        String trimmed = text.trim();
        float fontSize = line.getFontSize();
        float scaleX = Math.abs(line.getScaleX());
        if (trimmed.startsWith("-rect")) {
            float width = parseCommandSize(trimmed, 1);
            if (!Float.isNaN(width)) return Math.abs(width) * fontSize * scaleX;
        } else if (trimmed.startsWith("-texture")) {
            // 图片行实际画出来的是固定边长（0.4 * 字号 格）的正方形，不能把指令文本当宽度。
            return TEXTURE_SIZE_BLOCKS * 16f * fontSize * scaleX;
        }
        int px = widthPx == null ? 0 : widthPx.applyAsInt(text);
        return px * UNITS_PER_FONT_PIXEL * fontSize * scaleX;
    }

    private static float measureHeight(UnaryOperator<String> resolve, TextLineData line) {
        String text = resolvedLineText(resolve, line);
        String trimmed = text == null ? "" : text.trim();
        float fontSize = line.getFontSize();
        float scaleY = Math.abs(line.getScaleY());
        if (trimmed.startsWith("-rect")) {
            float height = parseCommandSize(trimmed, 2);
            if (!Float.isNaN(height)) return Math.abs(height) * fontSize * scaleY;
        } else if (trimmed.startsWith("-texture")) {
            return TEXTURE_SIZE_BLOCKS * 16f * fontSize * scaleY;
        }
        return FONT_LINE_HEIGHT_PX * UNITS_PER_FONT_PIXEL * fontSize * scaleY;
    }

    private static String resolvedLineText(UnaryOperator<String> resolve, TextLineData line) {
        String text = line.getText();
        if (text == null || text.isEmpty()) return null;
        String resolved = resolve == null ? text : resolve.apply(text);
        return resolved == null || resolved.isEmpty() ? null : resolved;
    }

    /** 从「-指令 参数…」里取第 index 个参数（-rect 里 1=宽、2=高）；解析失败返回 NaN。 */
    private static float parseCommandSize(String text, int index) {
        String[] parts = text.split("\\s+");
        if (parts.length <= index) return Float.NaN;
        try {
            return Float.parseFloat(parts[index]);
        } catch (NumberFormatException e) {
            return Float.NaN;
        }
    }

    /** 水平对齐下，盒子的起点坐标到文本锚点的距离。 */
    private static float alignX(int hAlign, float width) {
        if (hAlign == 0) return 0f;
        if (hAlign == 2) return width;
        return width / 2f;
    }

    /** 垂直对齐下，盒子的底边坐标到文本锚点的距离。 */
    private static float alignY(int vAlign, float height) {
        if (vAlign == 0) return height;
        if (vAlign == 2) return 0f;
        return height / 2f;
    }
}
