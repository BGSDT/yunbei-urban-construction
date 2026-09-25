package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.util.PatternResourceLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;

/**
 * 图案与字体分类注册中心
 *
 * <p>只负责定义分类结构与内置素材清单；纹理扫描、自定义图案/字体 JSON 等
 * 资源包加载逻辑由 {@link PatternResourceLoader} 承担。
 *
 * @see PatternAndFontOverlay
 * @see PatternResourceLoader
 */
public final class PatternRegistry {

    /** 内置标志纹理根目录。 */
    private static final String SIGN_TEXTURE_PATH = "textures/block/sign/";

    /** 基本图案：车道箭头、导向箭头，以及禁令/指示标志中的转向箭头（WHITELIST 精确匹配文件名）。 */
    private static final String[] BASIC_PATTERN_TEXTURES = {
            "sign_guide_lane_arrow_left_turn.png",
            "sign_guide_lane_arrow_right_turn.png",
            "sign_guide_lane_arrow_straight.png",
            "sign_guide_lane_arrow_straight_left_turn.png",
            "sign_guide_lane_arrow_straight_left_turn_around.png",
            "sign_guide_lane_arrow_straight_right_turn.png",
            "sign_expressway_arrow_left.png",
            "sign_expressway_exit_lane_arrow.png",
            "sign_expressway_right_side_median_strip.png",
            "sign_expressway_straight.png",
            "zones_board_arrow_left.png",
            "zones_board_arrow_left_right.png",
            "zones_board_arrow_left_turn.png",
            "zones_board_arrow_straight.png",
            "sign_no_left_right_turn.png",
            "sign_no_left_turn.png",
            "sign_no_right_turn.png",
            "sign_no_single_left_turn_around.png",
            "sign_no_straight.png",
            "sign_no_straight_left_turn.png",
            "sign_no_straight_right_turn.png",
            "sign_indication_left.png",
            "sign_indication_left_right_turn.png",
            "sign_indication_left_turn.png",
            "sign_indication_left_turn_around.png",
            "sign_indication_right.png",
            "sign_indication_right_turn.png",
            "sign_indication_single_left_turn_around.png",
            "sign_indication_straight.png",
            "sign_indication_straight_inverted.png",
            "sign_indication_straight_left_turn.png",
            "sign_indication_straight_right_turn.png"
    };

    /** 归入禁令标识的散装禁令标志（前缀匹配，从「告示标识及其他」移出）。 */
    private static final String[] NO_SIGN_EXTRA = {
            "sign_charging",
            "sign_check",
            "sign_port_check",
            "sign_wrong_way",
            "sign_stop",
            "sign_yield_to_oncoming_traffic",
            "sign_slow",
            "sign_yield"
    };

    /** 从指路·一般道路移入设施标识的纹理（前缀匹配，取完整文件名即可精确命中单个文件）。 */
    private static final String[] GUIDE_FACILITY_EXTRA = {
            "sign_guide_identification_airport",
            "sign_guide_identification_ferry_crossing",
            "sign_guide_identification_first_aid_station"
    };

    /** 不加入图案库的一般道路纹理（精确文件名，从指路标识·一般道路中剔除）。 */
    private static final String[] GENERAL_EXCLUDED = {
            "sign_guide_distance_to_tunnel_exit_3.png",
            "sign_guide_distance_to_tunnel_exit_4.png",
            "sign_expressway_naming_number_1.png",
            "sign_expressway_naming_number_2.png",
            "sign_guide_lane_indicator_1.png"
    };

    /** 路口预告中文件名不带 sign_guide_direction 前缀的纹理，需单独按前缀纳入。 */
    private static final String INTERSECTION_ADVANCE_WARNING = "sign_guide_intersection_advance_warning";

    /** 留在「交叉路口预告」的路口预告纹理，一般道路需排除。 */
    private static final String INTERSECTION_ADVANCE_WARNING_5 = "sign_guide_intersection_advance_warning_5";

    /** 从「交叉路口预告」移入「一般道路」的纹理前缀。 */
    private static final String GUIDE_DIRECTION_ROAD_NAME = "sign_guide_direction_road_name";

    /** 从「交叉路口预告」移入「一般道路」的武汉路口预告纹理（精确文件名）。 */
    private static final String[] GUIDE_DIRECTION_WUHAN = {
            "sign_guide_intersection_advance_warning_1_wuhan_left.png",
            "sign_guide_intersection_advance_warning_1_wuhan_straight.png"
    };

    /**
     * 「交叉路口预告」素材在一般道路中的排除前缀（sign_guide_direction 的第二层分类）。
     *
     * <p>不能直接排除 {@code sign_guide_direction} 整段：排除优先于主过滤器，
     * 会把要移入一般道路的 {@code sign_guide_direction_road_name_*} 一并剔掉，
     * 故逐个列出第二层分类前缀。新增同类素材时需同步补这里。
     */
    private static final String[] GUIDE_DIRECTION_SUB_PREFIXES = {
            "sign_guide_direction_3pathsign",
            "sign_guide_direction_4pathsign",
            "sign_guide_direction_arrow",
            "sign_guide_direction_exits",
            "sign_guide_direction_interchange",
            "sign_guide_direction_roundabout"
    };

    /** 不加入图案库的纹理前缀（从「告示标识及其他」中剔除）。 */
    private static final String[] EXCLUDED_TEXTURES = {
            "sign_gray",
            "sign_red",
            "sign_unit_metre",
            "sign_fluorescence",
            "sign_water",
            "sign_toilet"
    };

    /** 底图图案：高速/普通道路编号背景（WHITELIST 精确匹配文件名）。 */
    private static final String[] BACKGROUND_PATTERN_TEXTURES = {
            "sign_expressway_logo.png",
            "sign_expressway_national_logo_1.png",
            "sign_expressway_national_logo_2.png",
            "sign_expressway_provincial_logo_1.png",
            "sign_expressway_provincial_logo_2.png",
            "sign_ordinary_municipal_road_logo_1.png",
            "sign_ordinary_municipal_road_logo_2.png",
            "sign_white_number_logo.png",
            "sign_red_number_logo.png",
            "sign_yellow_number_logo.png",
            "sign_circle.png"
    };

    private PatternRegistry() {
    }

    /**
     * 注册内置图案/字体分类并构建纹理缓存。幂等，仅首次调用生效。
     */
    public static void registerBuiltInPatterns() {
        if (PatternAndFontOverlay.isDataLoaded) return;

        // ==================== 图案 ====================
        PatternAndFontOverlay.H2Category patternCategory =
                new PatternAndFontOverlay.H2Category(Text.translatable("yunbeiuc.gui.tabs.patterns"));

        // 内置指令说明页（由 YunbeiUCIntegration 渲染）
        PatternAndFontOverlay.H3Category insertGuide =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.yunbeiuc_builtin"));
        patternCategory.addSubCategory(insertGuide);

        // 交通标志：按文件名前缀切分内置标志纹理
        PatternAndFontOverlay.H3Category trafficSigns =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.traffic_signs"));
        addSignSections(trafficSigns);
        patternCategory.addSubCategory(trafficSigns);

        // 自定义资源包图案
        PatternAndFontOverlay.H3Category customPatterns =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.custom_resource_pack"));
        customPatterns.headerText = Text.translatable("yunbeiuc.gui.sections.custom_patterns.desc");
        customPatterns.addSection(new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections.custom_patterns"),
                Text.literal(""), new Identifier("yunbeiuc", "patterns/"))
                .setCustomJsonPath("yunbeiuc:patterns/custom_patterns.json"));
        patternCategory.addSubCategory(customPatterns);

        // ==================== 字体 ====================
        PatternAndFontOverlay.H2Category fontCategory =
                new PatternAndFontOverlay.H2Category(Text.translatable("yunbeiuc.gui.tabs.fonts"));

        PatternAndFontOverlay.H3Category builtInFonts =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.builtin"));
        builtInFonts.addSection(new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections.builtin_fonts"),
                Text.translatable("yunbeiuc.gui.sections.builtin_fonts.desc"),
                new Identifier("yunbeiuc", "font/"))
                .setFontMode()
                .addFontItem("yunbeiuc:traf_sign_font_a", Text.translatable("yunbeiuc.gui.fonts.traf_sign_font_a"))
                .addFontItem("yunbeiuc:traf_sign_font_b", Text.translatable("yunbeiuc.gui.fonts.traf_sign_font_b"))
                .addFontItem("yunbeiuc:traf_sign_font_c", Text.translatable("yunbeiuc.gui.fonts.traf_sign_font_c")));
        fontCategory.addSubCategory(builtInFonts);

        PatternAndFontOverlay.H3Category customFonts =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.custom_resource_pack"));
        customFonts.headerText = Text.translatable("yunbeiuc.gui.sections.custom_fonts.desc");
        customFonts.addSection(new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections.custom_fonts"),
                Text.literal(""), new Identifier("yunbeiuc", "fonts/"))
                .setFontMode()
                .setCustomJsonPath("yunbeiuc:fonts/custom_fonts.json"));
        fontCategory.addSubCategory(customFonts);

        PatternAndFontOverlay.REGISTRY.add(patternCategory);
        PatternAndFontOverlay.REGISTRY.add(fontCategory);

        setDefaultSelection();
        // 资源包加载（纹理扫描 + 自定义 JSON）已迁至 util 包
        PatternResourceLoader.loadAllFromResourceManager();

        PatternAndFontOverlay.isDataLoaded = true;
    }

    // 设置默认选中第一个叶子 H3 节点
    private static void setDefaultSelection() {
        if (PatternAndFontOverlay.REGISTRY.isEmpty()) return;
        PatternAndFontOverlay.selectedH2 = PatternAndFontOverlay.REGISTRY.get(0);
        if (!PatternAndFontOverlay.selectedH2.subCategories.isEmpty()) {
            PatternAndFontOverlay.selectedH3 = findFirstLeafH3(PatternAndFontOverlay.selectedH2.subCategories.get(0));
        }
    }

    // 添加内置交通标志分区
    // 顺序：基本图案 → 底图图案 → 设施图案 → 禁令 → 指示 → 警告 → 指路 → 旅游区 → 告示及其他
    private static void addSignSections(PatternAndFontOverlay.H3Category parent) {
        // 1. 基本图案：各类箭头（车道箭头、导向箭头，以及禁令/指示标志中的转向箭头）
        parent.addSection(new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections.sign_basic"),
                Text.translatable("yunbeiuc.gui.sections.sign_basic.desc"),
                new Identifier("yunbeiuc", SIGN_TEXTURE_PATH))
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.WHITELIST, BASIC_PATTERN_TEXTURES));

        // 2. 底图图案：车道底图 + 高速/普通道路编号背景
        parent.addSection(new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections.sign_background"),
                Text.translatable("yunbeiuc.gui.sections.sign_background.desc"),
                new Identifier("yunbeiuc", SIGN_TEXTURE_PATH))
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.WHITELIST, BACKGROUND_PATTERN_TEXTURES));

        // 3. 禁令标识：禁令 + 限速/限重/限高/限宽 + 从告示移入的散装禁令标志
        //    排除 sign_notice（"sign_no" 前缀会连带匹配 sign_notice_*，那是告示标识的内容）
        //    转向箭头已归入基本图案，此处一并剔除
        addPrefixSection(parent, "sign_no", mergeNames(
                new String[]{"sign_no", "sign_speed", "sign_min", "sign_weight", "sign_height", "sign_alex"},
                NO_SIGN_EXTRA))
                .setExtensionExclude("sign_notice")
                .setExtensionExcludeNames(fromList(BASIC_PATTERN_TEXTURES, "sign_no_"));

        // 4. 指示标识（转向箭头已归入基本图案，此处剔除）
        addPrefixSection(parent, "sign_indication", "sign_indication")
                .setExtensionExcludeNames(fromList(BASIC_PATTERN_TEXTURES, "sign_indication_"));

        // 5. 警告标识
        addPrefixSection(parent, "sign_warning", "sign_warning");

        // 6. 指路标识：父容器，下分四个子分区，父与子各自可独立收起
        PatternAndFontOverlay.H4Section guideSection = newSignSection("sign_guide").enableChildSections();

        // 6.1 一般道路：指路标志中除路口预告、设施标识、路边设施、车道箭头之外的部分
        //     （GENERAL_EXCLUDED 中的几个按需求不加入图案库）
        //     移入：sign_guide_direction_road_name_*、武汉路口预告、sign_guide_intersection_warning_*
        //     移出：交叉路口预告的其余素材（按第二层前缀逐个排除，见 GUIDE_DIRECTION_SUB_PREFIXES）
        guideSection.addChildSection(newSignSection("sign_guide_general")
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX, "sign_guide")
                .setExtensionExclude(mergeNames(
                        new String[]{"sign_guide_roadside_facility", "sign_guide_facility",
                                "sign_guide_impassable_road",
                                INTERSECTION_ADVANCE_WARNING_5},
                        GUIDE_DIRECTION_SUB_PREFIXES,
                        GUIDE_FACILITY_EXTRA))
                .setExtensionExcludeNames(mergeNames(
                        fromList(BASIC_PATTERN_TEXTURES, "sign_guide_lane_arrow"),
                        GENERAL_EXCLUDED)));

        // 6.2 交叉路口预告：sign_guide_direction_* 以及单独移入的路口预告图案
        //     road_name 与武汉路口预告已移入一般道路，此处排除
        guideSection.addChildSection(newSignSection("sign_guide_direction")
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX,
                        "sign_guide_direction", INTERSECTION_ADVANCE_WARNING)
                .setExtensionExclude(GUIDE_DIRECTION_ROAD_NAME)
                .setExtensionExcludeNames(GUIDE_DIRECTION_WUHAN));

        // 6.3 城市快速路和高速公路：sign_expressway_*
        //     （编号背景→底图图案，箭头→基本图案，此处均剔除）
        guideSection.addChildSection(newSignSection("sign_guide_expressway")
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX, "sign_expressway")
                .setExtensionExcludeNames(mergeNames(
                        fromList(BASIC_PATTERN_TEXTURES, "sign_expressway_"),
                        fromList(BACKGROUND_PATTERN_TEXTURES, "sign_expressway_"))));

        // 6.4 设施标识：sign_guide_facility_* + 不通行道路 + 指定移入的识别标志
        guideSection.addChildSection(newSignSection("sign_guide_facility")
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX, mergeNames(
                        new String[]{"sign_guide_facility", "sign_guide_impassable_road"},
                        GUIDE_FACILITY_EXTRA)));

        parent.addSection(guideSection);

        // 7. 旅游区标识：sign_travel_* 前缀
        addPrefixSection(parent, "sign_tourist", "sign_travel");

        // 8. 告示标识：sign_notice_*
        addPrefixSection(parent, "sign_notice", "sign_notice");

        // 9. 辅助标识及其他：其余散装标志
        //    排除上面已归类的全部前缀；sign_single（单图标志）、zones_board（区域信息板）按需求不使用，一并排除
        //    zones_board 中的箭头已归入基本图案，走各自分类的白名单，不受此处排除影响
        parent.addSection(newSignSection("sign_assist_others")
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX_EXCLUDE, mergeNames(
                        new String[]{"sign_warning", "sign_no", "sign_indication", "sign_guide", "sign_expressway",
                                "sign_speed", "sign_min", "sign_weight", "sign_height", "sign_alex",
                                "sign_lane", "sign_ordinary", "sign_single", "sign_travel", "zones_board",
                                "sign_notice"},
                        NO_SIGN_EXTRA, EXCLUDED_TEXTURES))
                .setExtensionExcludeNames(fromList(BACKGROUND_PATTERN_TEXTURES,
                        "sign_white_number_logo", "sign_red_number_logo", "sign_yellow_number_logo",
                        "sign_circle")));
    }

    // 构造一个标志分区（标题/描述取自 yunbeiuc.gui.sections.<sectionKey>[.desc]，描述键留空则不显示描述行）
    private static PatternAndFontOverlay.H4Section newSignSection(String sectionKey) {
        return new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections." + sectionKey),
                Text.translatable("yunbeiuc.gui.sections." + sectionKey + ".desc"),
                new Identifier("yunbeiuc", SIGN_TEXTURE_PATH));
    }

    // 添加一个按文件名前缀过滤的分区
    private static PatternAndFontOverlay.H4Section addPrefixSection(PatternAndFontOverlay.H3Category parent,
                                                                    String sectionKey, String... prefixes) {
        PatternAndFontOverlay.H4Section section = newSignSection(sectionKey)
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX, prefixes);
        parent.addSection(section);
        return section;
    }

    // 从指定清单中取出匹配前缀的素材，供后续分类排除，避免同一素材在多个分类里重复出现
    private static String[] fromList(String[] source, String... prefixes) {
        return Arrays.stream(source)
                .filter(fileName -> Arrays.stream(prefixes).anyMatch(fileName::startsWith))
                .toArray(String[]::new);
    }

    // 合并多个文件名数组
    private static String[] mergeNames(String[]... arrays) {
        return Arrays.stream(arrays).flatMap(Arrays::stream).toArray(String[]::new);
    }

    // 递归寻找首个叶子 H3 节点
    private static PatternAndFontOverlay.H3Category findFirstLeafH3(PatternAndFontOverlay.H3Category h3) {
        if (h3.subCategories.isEmpty()) {
            return h3;
        }
        return findFirstLeafH3(h3.subCategories.get(0));
    }
}
