package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;

import java.util.List;

// 「文本列组」的运行期切换逻辑。
public final class SignTextGroupRuntime {

    private SignTextGroupRuntime() {}

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

    public static int activeIndex(List<TextLineData> lines, int index, long nowMs) {
        int start = groupStart(lines, index);
        if (start < 0) return index;
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
}
