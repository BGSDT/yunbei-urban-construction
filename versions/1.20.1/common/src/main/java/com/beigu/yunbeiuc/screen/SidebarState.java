package com.beigu.yunbeiuc.screen;

/**
 * 侧边栏导航状态管理
 *
 * <p>控制导航栏的展开/收起状态以及导航项的互斥选中逻辑。
 *
 * @see PatternAndFontOverlay
 */
public final class SidebarState {

    /** 无选中项 */
    public static final int NAV_NONE = -1;
    /** 主页导航项 */
    public static final int NAV_HOME = 0;

    private static boolean collapsed = false;

    private SidebarState() {
    }

    /**
     * 判断侧边栏是否处于收起状态。
     */
    public static boolean isCollapsed() {
        return collapsed;
    }

    /**
     * 切换侧边栏展开/收起状态。
     */
    public static void toggleCollapse() {
        collapsed = !collapsed;
    }

    /**
     * 设置侧边栏展开/收起状态。
     */
    public static void setCollapsed(boolean value) {
        collapsed = value;
    }

    /**
     * 获取当前侧边栏的有效宽度。
     */
    public static int getEffectiveWidth() {
        return collapsed ? UIConstants.SIDEBAR_COLLAPSED_WIDTH : UIConstants.SIDEBAR_FULL_WIDTH;
    }

    /**
     * 选中导航栏顶层项并强制互斥。
     *
     * <p>清空滚动位置，清理分类选中状态。
     *
     * @param which 顶层项索引
     */
    public static void selectNavTop(int which) {
        PatternAndFontOverlay.navSelection = which;
        PatternAndFontOverlay.isHomeSelected = (which == NAV_HOME);
        if (which != NAV_NONE) {
            PatternAndFontOverlay.selectedH2 = null;
            PatternAndFontOverlay.selectedH3 = null;
        }
        PatternAndFontOverlay.scrollY = 0;
    }

    /**
     * 选中分类时清空顶层导航标志。
     */
    public static void clearNavTop() {
        PatternAndFontOverlay.navSelection = NAV_NONE;
        PatternAndFontOverlay.isHomeSelected = false;
    }

    /**
     * 防御性自愈：确保顶层项与分类选中状态互斥。
     *
     * <p>每帧渲染前调用。若 selectedH2/H3 与顶层项同时存在，优先保留顶层项。
     */
    public static void enforceNavMutualExclusion() {
        if (PatternAndFontOverlay.isHomeSelected) {
            PatternAndFontOverlay.selectedH2 = null;
            PatternAndFontOverlay.selectedH3 = null;
        } else if (PatternAndFontOverlay.navSelection == NAV_HOME
                && PatternAndFontOverlay.selectedH2 == null
                && PatternAndFontOverlay.selectedH3 == null) {
            PatternAndFontOverlay.isHomeSelected = true;
        }
    }
}
