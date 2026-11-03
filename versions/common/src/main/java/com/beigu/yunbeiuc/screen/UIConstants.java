package com.beigu.yunbeiuc.screen;

public final class UIConstants {
    private UIConstants() {
    }

    public static final int SIDEBAR_FULL_WIDTH = 168;
    public static final int SIDEBAR_COLLAPSED_WIDTH = 44;
    public static final int TOP_BAR_HEIGHT = 32;
    public static final int BOTTOM_BAR_HEIGHT = 48;
    public static final int THUMB_WIDTH = 6;
    public static final int THUMB_MIN_SIZE = 18;

    public static final int NAV_PAD = 8;
    public static final int CARD_RADIUS = 7;
    public static final int CARD_HEADER_H = 26;
    public static final int CARD_ITEM_H = 20;
    public static final int CARD_ITEM_INSET = 6;
    public static final int CARD_SUB_INDENT = 10;
    public static final int CARD_GAP = 6;
    public static final int CARD_INNER_TOP = 3;
    public static final int CARD_INNER_BOTTOM = 4;
    public static final int NAV_ACCENT_BAR_W = 3;

    public static final int NAV_ICON_SIZE = 32;
    public static final int NAV_ICON_ROW_H = 34;

    public static final int CELL_SIZE = 36;
    public static final int CELL_GAP_X = 12;
    public static final int CELL_GAP_Y = 18;

    public static final int FONT_CARD_H = 44;
    public static final int FONT_CARD_GAP = 6;

    public static final int FILTER_PILL_WIDTH = 42;
    public static final int FILTER_PILL_HEIGHT = 16;
    public static final int FILTER_PILL_SPACING = 5;

    public static final int LABEL_BTN_HEIGHT = 20;
    public static final int LABEL_BTN_PAD_X = 10;
    public static final int LABEL_BTN_GAP_X = 8;
    public static final int LABEL_BTN_GAP_Y = 8;

    public static final int CLOSE_BTN_WIDTH = 160;
    public static final int CLOSE_BTN_HEIGHT = 24;

    public static final int LIST_ITEM_HEIGHT = 27;

    public static final int WHEEL_STEP = 18;

    public static final int CLR_NAV_BG = 0xAA333333;
    public static final int CLR_NAV_TOPBAR = 0xAA333333;
    public static final int CLR_NAV_DIVIDER = 0xFFCCCCCC;
    public static final int CLR_NAV_BORDER = 0xFFCCCCCC;
    public static final int CLR_NAV_ITEM_HOVER = 0x40FFFFFF;
    public static final int CLR_NAVItemSelected = 0xFF555577;
    public static final int CLR_NAVTextPri = 0xFFFFFFFF;
    public static final int CLR_NAVTextSec = 0xFFAAAAAA;

    public static final int CLR_CARD_BG = 0xAA333333;
    public static final int CLR_CARD_BORDER = 0xFFCCCCCC;
    public static final int CLR_CARD_BORDER_HOVER = 0xFFFFFFFF;
    public static final int CLR_CARD_HEADER_BG = 0xAA3F3F3F;
    public static final int CLR_CARD_HEADER_BG_HOVER = 0xAA4A4A4A;

    public static final int CLR_NAV_PILL = 0x00000000;
    public static final int CLR_NAV_PILL_HOVER = 0xAA4A4A4A;
    public static final int CLR_NAV_PILL_SEL = 0xFF555577;

    public static final int CLR_CONTENT_BG = 0xAA333333;
    public static final int CLR_CONTENT_TOPBAR = 0xAA333333;
    public static final int CLR_CONTENT_DIVIDER = 0xFFCCCCCC;
    public static final int CLR_CONTENT_BORDER = 0xFFCCCCCC;

    public static final int CLR_TRACK = 0x30FFFFFF;
    public static final int CLR_SLIDER = 0xFFAAAAAA;
    public static final int CLR_SLIDER_HOVER = 0xFFCCCCCC;

    public static final int CLR_HEADING = 0xFFFFFFFF;
    public static final int CLR_ACCENT = 0xFF66FFCC;
    public static final int CLR_MUTED = 0xFFAAAAAA;

    public static final int CLR_CATEGORY_LABEL = 0xFFAAAAAA;
    public static final int CLR_BODY_TEXT = 0xFFDDDDDD;
    public static final int CLR_CELL_OUTLINE = 0xFFCCCCCC;
    public static final int CLR_CELL_FILL = 0xAA444444;

    public static final int CLR_BTN_FILL = 0xAA444444;
    public static final int CLR_BTN_HOVER = 0xAA5A5A5A;
    public static final int CLR_BTN_STROKE = 0xFFCCCCCC;
    public static final int CLR_BTN_LABEL = 0xFFFFFFFF;

    public static final int CLR_ACTION_FILL = 0xAA444444;
    public static final int CLR_ACTION_HOVER = 0xAA5A5A5A;
    public static final int CLR_ACTION_STROKE = 0xFFCCCCCC;

    public static final int CLR_LINK = 0xFF66FFCC;
    public static final int CLR_LINK_PRESSED = 0xFF44DDAA;

    public static final int CLR_HOME_TITLE = 0xFFFFFFFF;
    public static final int CLR_HOME_DESC = 0xFFAAAAAA;
    public static final int CLR_HOME_BODY = 0xFFDDDDDD;
    public static final int CLR_HOME_CARD_BG = 0xAA333333;
    public static final int CLR_HOME_CARD_STROKE = 0xFFCCCCCC;
    public static final int CLR_HOME_CARD_ACCENT = 0xFF66FFCC;

    public static final int CLR_INFO_BG = 0xAA2A2A2A;

    public static final int Z_LAYER = 500;

    public static int currentSidebarWidth() {
        return SidebarState.isCollapsed() ? SIDEBAR_COLLAPSED_WIDTH : SIDEBAR_FULL_WIDTH;
    }
}
