package com.beigu.yunbeiuc.screen;

/**
 * 海燕蓝主题 UI 常量定义
 *
 * <p>配色方案：以纯净蓝色（#1560C0 系列）为主色调，搭配深蓝导航栏与浅蓝灰内容背景。
 */
public final class UIConstants {
    private UIConstants() {
    }

    // 侧边栏尺寸
    public static final int SIDEBAR_FULL_WIDTH = 168;
    public static final int SIDEBAR_COLLAPSED_WIDTH = 44;
    public static final int TOP_BAR_HEIGHT = 32;
    public static final int BOTTOM_BAR_HEIGHT = 48;
    public static final int THUMB_WIDTH = 6;
    public static final int THUMB_MIN_SIZE = 18;

    // ==================== 卡片分组式侧边栏 ====================
    /** 侧边栏外边距 */
    public static final int NAV_PAD = 8;
    /** 分组卡片圆角半径 */
    public static final int CARD_RADIUS = 7;
    /** 分组卡片头部高度 */
    public static final int CARD_HEADER_H = 26;
    /** 分组卡片内胶囊项高度 */
    public static final int CARD_ITEM_H = 20;
    /** 分组卡片内胶囊项左右内缩 */
    public static final int CARD_ITEM_INSET = 6;
    /** 卡片之间的垂直间距 */
    public static final int CARD_GAP = 6;
    /** 卡片内部子项区与头部的间距 */
    public static final int CARD_INNER_TOP = 3;
    /** 卡片内部子项区底部留白 */
    public static final int CARD_INNER_BOTTOM = 4;
    /** 选中态左侧竖条宽度 */
    public static final int NAV_ACCENT_BAR_W = 3;

    // 收起模式图标
    public static final int NAV_ICON_SIZE = 32;
    public static final int NAV_ICON_ROW_H = 34;

    // 图案格子
    public static final int CELL_SIZE = 36;
    public static final int CELL_GAP_X = 12;
    public static final int CELL_GAP_Y = 18;

    // 字体卡片
    public static final int FONT_CARD_H = 44;
    public static final int FONT_CARD_GAP = 6;

    // 筛选标签
    public static final int FILTER_PILL_WIDTH = 42;
    public static final int FILTER_PILL_HEIGHT = 16;
    public static final int FILTER_PILL_SPACING = 5;

    // 底部操作按钮
    public static final int CLOSE_BTN_WIDTH = 160;
    public static final int CLOSE_BTN_HEIGHT = 24;

    // 列表项高度
    public static final int LIST_ITEM_HEIGHT = 27;

    // 滚动步进
    public static final int WHEEL_STEP = 18;

    // ==================== 海燕蓝配色 ====================

    // 侧边栏底（与其它界面统一的原版面板风：半透明深灰底 + 浅灰边框）
    public static final int CLR_NAV_BG = 0xAA333333;
    public static final int CLR_NAV_TOPBAR = 0xAA333333;
    public static final int CLR_NAV_DIVIDER = 0xFFCCCCCC;
    public static final int CLR_NAV_BORDER = 0xFFCCCCCC;
    public static final int CLR_NAV_ITEM_HOVER = 0x40FFFFFF;
    public static final int CLR_NAVItemSelected = 0xFF555577;
    public static final int CLR_NAVTextPri = 0xFFFFFFFF;
    public static final int CLR_NAVTextSec = 0xFFAAAAAA;

    // 分组卡片
    public static final int CLR_CARD_BG = 0xAA333333;
    public static final int CLR_CARD_BORDER = 0xFFCCCCCC;
    public static final int CLR_CARD_BORDER_HOVER = 0xFFFFFFFF;
    public static final int CLR_CARD_HEADER_BG = 0xAA3F3F3F;
    public static final int CLR_CARD_HEADER_BG_HOVER = 0xAA4A4A4A;

    // 卡片内胶囊项
    public static final int CLR_NAV_PILL = 0x00000000;
    public static final int CLR_NAV_PILL_HOVER = 0xAA4A4A4A;
    public static final int CLR_NAV_PILL_SEL = 0xFF555577;

    // 主内容区
    public static final int CLR_CONTENT_BG = 0xAA333333;
    public static final int CLR_CONTENT_TOPBAR = 0xAA333333;
    public static final int CLR_CONTENT_DIVIDER = 0xFFCCCCCC;
    public static final int CLR_CONTENT_BORDER = 0xFFCCCCCC;

    // 滚动条（圆角）
    public static final int CLR_TRACK = 0x30FFFFFF;
    public static final int CLR_SLIDER = 0xFFAAAAAA;
    public static final int CLR_SLIDER_HOVER = 0xFFCCCCCC;

    // 标题与强调色
    public static final int CLR_HEADING = 0xFFFFFFFF;
    public static final int CLR_ACCENT = 0xFF66FFCC;
    public static final int CLR_MUTED = 0xFFAAAAAA;

    // 分类标题
    public static final int CLR_CATEGORY_LABEL = 0xFFAAAAAA;
    public static final int CLR_BODY_TEXT = 0xFFDDDDDD;
    public static final int CLR_CELL_OUTLINE = 0xFFCCCCCC;
    public static final int CLR_CELL_FILL = 0xAA444444;

    // 按钮
    public static final int CLR_BTN_FILL = 0xAA444444;
    public static final int CLR_BTN_HOVER = 0xAA5A5A5A;
    public static final int CLR_BTN_STROKE = 0xFFCCCCCC;
    public static final int CLR_BTN_LABEL = 0xFFFFFFFF;

    // 操作按钮（插入等）
    public static final int CLR_ACTION_FILL = 0xAA444444;
    public static final int CLR_ACTION_HOVER = 0xAA5A5A5A;
    public static final int CLR_ACTION_STROKE = 0xFFCCCCCC;

    // 链接
    public static final int CLR_LINK = 0xFF66FFCC;
    public static final int CLR_LINK_PRESSED = 0xFF44DDAA;

    // 主页
    public static final int CLR_HOME_TITLE = 0xFFFFFFFF;
    public static final int CLR_HOME_DESC = 0xFFAAAAAA;
    public static final int CLR_HOME_BODY = 0xFFDDDDDD;
    public static final int CLR_HOME_CARD_BG = 0xAA333333;
    public static final int CLR_HOME_CARD_STROKE = 0xFFCCCCCC;
    public static final int CLR_HOME_CARD_ACCENT = 0xFF66FFCC;

    // 信息框
    public static final int CLR_INFO_BG = 0xAA2A2A2A;

    // 层级偏移
    public static final int Z_LAYER = 500;

    // ==================== 动态宽度查询 ====================

    /** 侧边栏当前实际宽度（展开/收起）。 */
    public static int currentSidebarWidth() {
        return SidebarState.isCollapsed() ? SIDEBAR_COLLAPSED_WIDTH : SIDEBAR_FULL_WIDTH;
    }
}