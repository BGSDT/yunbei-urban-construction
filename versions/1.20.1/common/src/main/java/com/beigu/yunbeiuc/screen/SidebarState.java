package com.beigu.yunbeiuc.screen;

/**
 * 侧边栏顶层项互斥选择逻辑
 *
 * <p>本界面只保留「主页」一个顶层项，其余内容（图案、字体）均为 H2 分类，
 * 因此互斥逻辑简化为「顶层项与 H2/H3 选中状态二者只存其一」。
 *
 * @see PatternAndFontOverlay
 */
public final class SidebarState {

    /** 无选中项 */
    public static final int SIDEBAR_NONE = -1;
    /** 主页 */
    public static final int SIDEBAR_HOME = 0;

    private SidebarState() {
    }

    /**
     * 统一设置侧边栏顶层项并强制互斥。
     *
     * <p>清空滚动位置，清理 H2/H3 选中状态。
     *
     * @param which 顶层项索引
     */
    public static void selectSidebarTop(int which) {
        PatternAndFontOverlay.sidebarSelection = which;
        PatternAndFontOverlay.isHomeSelected = (which == SIDEBAR_HOME);
        if (which != SIDEBAR_NONE) {
            PatternAndFontOverlay.selectedH2 = null;
            PatternAndFontOverlay.selectedH3 = null;
        }
        PatternAndFontOverlay.scrollY = 0;
    }

    /**
     * 选中 H2/H3 分类时清空顶层项标志。
     */
    public static void clearSidebarTop() {
        PatternAndFontOverlay.sidebarSelection = SIDEBAR_NONE;
        PatternAndFontOverlay.isHomeSelected = false;
    }

    /**
     * 防御性自愈：确保顶层项与分类选中状态互斥。
     *
     * <p>每帧渲染前调用。若 selectedH2/H3 与顶层项同时存在，优先保留顶层项。
     */
    public static void enforceSidebarMutualExclusion() {
        if (PatternAndFontOverlay.isHomeSelected) {
            PatternAndFontOverlay.selectedH2 = null;
            PatternAndFontOverlay.selectedH3 = null;
        } else if (PatternAndFontOverlay.sidebarSelection == SIDEBAR_HOME
                && PatternAndFontOverlay.selectedH2 == null
                && PatternAndFontOverlay.selectedH3 == null) {
            // 顶层项索引存在但标志位丢失（异常状态）时按索引补回
            PatternAndFontOverlay.isHomeSelected = true;
        }
    }
}
