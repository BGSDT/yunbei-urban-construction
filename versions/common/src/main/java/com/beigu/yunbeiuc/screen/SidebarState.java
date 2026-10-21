package com.beigu.yunbeiuc.screen;

public final class SidebarState {

    public static final int NAV_NONE = -1;
    public static final int NAV_HOME = 0;

    private static boolean collapsed = false;

    private SidebarState() {
    }

    public static boolean isCollapsed() {
        return collapsed;
    }

    public static void toggleCollapse() {
        collapsed = !collapsed;
    }

    public static void setCollapsed(boolean value) {
        collapsed = value;
    }

    public static int getEffectiveWidth() {
        return collapsed ? UIConstants.SIDEBAR_COLLAPSED_WIDTH : UIConstants.SIDEBAR_FULL_WIDTH;
    }

    public static void selectNavTop(int which) {
        PatternAndFontOverlay.navSelection = which;
        PatternAndFontOverlay.isHomeSelected = (which == NAV_HOME);
        if (which != NAV_NONE) {
            PatternAndFontOverlay.selectedH2 = null;
            PatternAndFontOverlay.selectedH3 = null;
        }
        PatternAndFontOverlay.scrollY = 0;
    }

    public static void clearNavTop() {
        PatternAndFontOverlay.navSelection = NAV_NONE;
        PatternAndFontOverlay.isHomeSelected = false;
    }

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
