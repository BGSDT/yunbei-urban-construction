package com.beigu.yunbeiuc.screen;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.util.PatternResourceLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public final class PatternRegistry {

    private static final String SIGN_TEXTURE_PATH = "textures/block/sign/";

    private static final String ASSEMBLED_TEXTURE_PATH = "textures/block/sign_assembled/";

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

    private static final String[] GUIDE_FACILITY_EXTRA = {
            "sign_guide_identification_airport",
            "sign_guide_identification_ferry_crossing",
            "sign_guide_identification_first_aid_station"
    };

    private static final String[] GENERAL_EXCLUDED = {
            "sign_guide_distance_to_tunnel_exit_3.png",
            "sign_guide_distance_to_tunnel_exit_4.png",
            "sign_expressway_naming_number_1.png",
            "sign_expressway_naming_number_2.png",
            "sign_guide_lane_indicator_1.png"
    };

    private static final String INTERSECTION_ADVANCE_WARNING = "sign_guide_intersection_advance_warning";

    private static final String INTERSECTION_ADVANCE_WARNING_5 = "sign_guide_intersection_advance_warning_5";

    private static final String GUIDE_DIRECTION_ROAD_NAME = "sign_guide_direction_road_name";

    private static final String[] GUIDE_DIRECTION_WUHAN = {
            "sign_guide_intersection_advance_warning_1_wuhan_left.png",
            "sign_guide_intersection_advance_warning_1_wuhan_straight.png"
    };

    private static final String[] GUIDE_DIRECTION_SUB_PREFIXES = {
            "sign_guide_direction_3pathsign",
            "sign_guide_direction_4pathsign",
            "sign_guide_direction_arrow",
            "sign_guide_direction_exits",
            "sign_guide_direction_interchange",
            "sign_guide_direction_roundabout"
    };

    private static final String[] EXCLUDED_TEXTURES = {
            "sign_gray",
            "sign_red",
            "sign_unit_metre",
            "sign_fluorescence",
            "sign_water",
            "sign_toilet"
    };

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

    public static void registerBuiltInPatterns() {
        if (PatternAndFontOverlay.isDataLoaded) return;

        PatternAndFontOverlay.H2Category patternCategory =
                new PatternAndFontOverlay.H2Category(Text.translatable("yunbeiuc.gui.tabs.patterns"));

        PatternAndFontOverlay.H3Category insertGuide =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.yunbeiuc_builtin"));
        patternCategory.addSubCategory(insertGuide);

        PatternAndFontOverlay.H3Category trafficSigns =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.traffic_signs"));
        trafficSigns.headerText = Text.translatable("yunbeiuc.gui.sections.traffic_signs.desc");

        PatternAndFontOverlay.H3Category splitPatterns =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.split_patterns"));
        addSignSections(splitPatterns);
        trafficSigns.addSubCategory(splitPatterns);

        PatternAndFontOverlay.H3Category assembledPatterns =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.assembled_patterns"));
        addAssembledSections(assembledPatterns);
        trafficSigns.addSubCategory(assembledPatterns);

        patternCategory.addSubCategory(trafficSigns);

        PatternAndFontOverlay.H3Category patternSets =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.pattern_sets"));
        addPatternSetSections(patternSets);
        patternCategory.addSubCategory(patternSets);

        PatternAndFontOverlay.H3Category customPatterns =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.custom_resource_pack"));
        customPatterns.headerText = Text.translatable("yunbeiuc.gui.sections.custom_patterns.desc");
        patternCategory.addSubCategory(customPatterns);

        PatternAndFontOverlay.H2Category fontCategory =
                new PatternAndFontOverlay.H2Category(Text.translatable("yunbeiuc.gui.tabs.fonts"));

        PatternAndFontOverlay.H3Category builtInFonts =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.builtin"));
        builtInFonts.addSection(new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections.builtin_fonts"),
                Text.translatable("yunbeiuc.gui.sections.builtin_fonts.desc"),
                VersionServices.resources().create("yunbeiuc", "font/"))
                .setFontMode()
                .addFontItem("yunbeiuc:traf_sign_font_a", Text.translatable("yunbeiuc.gui.fonts.traf_sign_font_a"))
                .addFontItem("yunbeiuc:traf_sign_font_b", Text.translatable("yunbeiuc.gui.fonts.traf_sign_font_b"))
                .addFontItem("yunbeiuc:traf_sign_font_c", Text.translatable("yunbeiuc.gui.fonts.traf_sign_font_c")));
        fontCategory.addSubCategory(builtInFonts);

        PatternAndFontOverlay.H3Category customFonts =
                new PatternAndFontOverlay.H3Category(Text.translatable("yunbeiuc.gui.categories.custom_resource_pack"));
        customFonts.headerText = Text.translatable("yunbeiuc.gui.sections.custom_fonts.desc");
        fontCategory.addSubCategory(customFonts);

        PatternAndFontOverlay.REGISTRY.add(patternCategory);
        PatternAndFontOverlay.REGISTRY.add(fontCategory);

        setDefaultSelection();
        PatternResourceLoader.loadAllFromResourceManager();

        PatternAndFontOverlay.isDataLoaded = true;
    }

    private static void setDefaultSelection() {
        if (PatternAndFontOverlay.REGISTRY.isEmpty()) return;
        PatternAndFontOverlay.selectedH2 = PatternAndFontOverlay.REGISTRY.get(0);
        if (!PatternAndFontOverlay.selectedH2.subCategories.isEmpty()) {
            PatternAndFontOverlay.selectedH3 = findFirstLeafH3(PatternAndFontOverlay.selectedH2.subCategories.get(0));
        }
    }

    private static void addSignSections(PatternAndFontOverlay.H3Category parent) {
        parent.addSection(new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections.sign_basic"),
                Text.translatable("yunbeiuc.gui.sections.sign_basic.desc"),
                VersionServices.resources().create("yunbeiuc", SIGN_TEXTURE_PATH))
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.WHITELIST, BASIC_PATTERN_TEXTURES));

        parent.addSection(new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections.sign_background"),
                Text.translatable("yunbeiuc.gui.sections.sign_background.desc"),
                VersionServices.resources().create("yunbeiuc", SIGN_TEXTURE_PATH))
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.WHITELIST, BACKGROUND_PATTERN_TEXTURES));

        addPrefixSection(parent, "sign_no", mergeNames(
                new String[]{"sign_no", "sign_speed", "sign_min", "sign_weight", "sign_height", "sign_alex"},
                NO_SIGN_EXTRA))
                .setExtensionExclude("sign_notice")
                .setExtensionExcludeNames(fromList(BASIC_PATTERN_TEXTURES, "sign_no_"));

        addPrefixSection(parent, "sign_indication", "sign_indication")
                .setExtensionExcludeNames(fromList(BASIC_PATTERN_TEXTURES, "sign_indication_"));

        addPrefixSection(parent, "sign_warning", "sign_warning");

        PatternAndFontOverlay.H4Section guideSection = newSignSection("sign_guide").enableChildSections();

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

        guideSection.addChildSection(newSignSection("sign_guide_direction")
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX,
                        "sign_guide_direction", INTERSECTION_ADVANCE_WARNING)
                .setExtensionExclude(GUIDE_DIRECTION_ROAD_NAME)
                .setExtensionExcludeNames(GUIDE_DIRECTION_WUHAN));

        guideSection.addChildSection(newSignSection("sign_guide_expressway")
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX, "sign_expressway")
                .setExtensionExcludeNames(mergeNames(
                        fromList(BASIC_PATTERN_TEXTURES, "sign_expressway_"),
                        fromList(BACKGROUND_PATTERN_TEXTURES, "sign_expressway_"))));

        guideSection.addChildSection(newSignSection("sign_guide_facility")
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX, mergeNames(
                        new String[]{"sign_guide_facility", "sign_guide_impassable_road"},
                        GUIDE_FACILITY_EXTRA)));

        parent.addSection(guideSection);

        addPrefixSection(parent, "sign_tourist", "sign_travel");

        addPrefixSection(parent, "sign_notice", "sign_notice");

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

    private static void addAssembledSections(PatternAndFontOverlay.H3Category parent) {
        addAssembledSection(parent, "assembled_prohibition", "sign_prohibition");
        addAssembledSection(parent, "assembled_indication", "sign_indication");
        addAssembledSection(parent, "assembled_warning", "sign_warning");
    }

    private static void addAssembledSection(PatternAndFontOverlay.H3Category parent,
                                            String sectionKey, String... prefixes) {
        parent.addSection(new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections." + sectionKey),
                Text.translatable("yunbeiuc.gui.sections." + sectionKey + ".desc"),
                VersionServices.resources().create("yunbeiuc", ASSEMBLED_TEXTURE_PATH))
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX, prefixes));
    }

    private static PatternAndFontOverlay.H4Section newSignSection(String sectionKey) {
        return new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.sections." + sectionKey),
                Text.translatable("yunbeiuc.gui.sections." + sectionKey + ".desc"),
                VersionServices.resources().create("yunbeiuc", SIGN_TEXTURE_PATH));
    }

    private static PatternAndFontOverlay.H4Section addPrefixSection(PatternAndFontOverlay.H3Category parent,
                                                                    String sectionKey, String... prefixes) {
        PatternAndFontOverlay.H4Section section = newSignSection(sectionKey)
                .setExtensionFilter(PatternAndFontOverlay.FilterMode.PREFIX, prefixes);
        parent.addSection(section);
        return section;
    }

    private static String[] fromList(String[] source, String... prefixes) {
        return Arrays.stream(source)
                .filter(fileName -> Arrays.stream(prefixes).anyMatch(fileName::startsWith))
                .toArray(String[]::new);
    }

    private static String[] mergeNames(String[]... arrays) {
        return Arrays.stream(arrays).flatMap(Arrays::stream).toArray(String[]::new);
    }

    private static void addPatternSetSections(PatternAndFontOverlay.H3Category parent) {
        parent.addSection(newPatternSetSection("expressway_logo")
                .addMatchTitle("高速Logo")
                .addLabeledItem(signTexture("sign_expressway_national_logo_1.png"), "", setLabel("ew_nat_1"))
                .addLabeledItem(signTexture("sign_expressway_national_logo_2.png"), "", setLabel("ew_nat_2"))
                .addLabeledItem(signTexture("sign_expressway_provincial_logo_1.png"), "", setLabel("ew_prov_1"))
                .addLabeledItem(signTexture("sign_expressway_provincial_logo_2.png"), "", setLabel("ew_prov_2")));

        parent.addSection(newPatternSetSection("board_color")
                .addMatchTitle("底色")
                .addLabeledItem(signTexture("sign_red_number_logo.png"), "", setLabel("board_red"))
                .addLabeledItem(signTexture("sign_yellow_number_logo.png"), "", setLabel("board_yellow"))
                .addLabeledItem(signTexture("sign_white_number_logo.png"), "", setLabel("board_white"))
                .addLabeledItem(signTexture("sign_expressway_logo.png"), "", setLabel("board_expressway")));

        parent.addSection(newPatternSetSection("municipal_logo")
                .addMatchTitle("普通公路Logo")
                .addLabeledItem(signTexture("sign_ordinary_municipal_road_logo_1.png"), "", setLabel("mun_3"))
                .addLabeledItem(signTexture("sign_ordinary_municipal_road_logo_2.png"), "", setLabel("mun_4")));

        parent.addSection(newPatternSetSection("lane_arrow")
                .addMatchTitle("箭头")
                .addLabeledItem(signTexture("sign_guide_lane_arrow_left_turn.png"), "", setLabel("lane_left_turn"))
                .addLabeledItem(signTexture("sign_guide_lane_arrow_straight.png"), "", setLabel("lane_straight"))
                .addLabeledItem(signTexture("sign_guide_lane_arrow_right_turn.png"), "", setLabel("lane_right_turn"))
                .addLabeledItem(signTexture("sign_guide_lane_arrow_straight_left_turn.png"), "", setLabel("lane_sl_turn"))
                .addLabeledItem(signTexture("sign_guide_lane_arrow_straight_right_turn.png"), "", setLabel("lane_sr_turn"))
                .addLabeledItem(signTexture("sign_guide_lane_arrow_straight_left_turn_around.png"), "", setLabel("lane_uturn")));

        parent.addSection(newPatternSetSection("turn_direction")
                .addMatchTitle("转向")
                .addLabeledItem(signTexture("sign_indication_left.png"), "", setLabel("turn_left"))
                .addLabeledItem(signTexture("sign_indication_straight.png"), "", setLabel("turn_straight"))
                .addLabeledItem(signTexture("sign_indication_right.png"), "", setLabel("turn_right")));

        parent.addSection(newPatternSetSection("compass")
                .addMatchTitle("方向")
                .addLabeledItem(signTexture("sign_expressway_north.png"), "", setLabel("compass_n"))
                .addLabeledItem(signTexture("sign_expressway_south.png"), "", setLabel("compass_s"))
                .addLabeledItem(signTexture("sign_expressway_west.png"), "", setLabel("compass_w"))
                .addLabeledItem(signTexture("sign_expressway_east.png"), "", setLabel("compass_e")));
    }

    public static PatternAndFontOverlay.H3Category getPatternSetCategory() {
        registerBuiltInPatterns();
        String want = Text.translatable("yunbeiuc.gui.categories.pattern_sets").getString();
        for (PatternAndFontOverlay.H2Category h2 : PatternAndFontOverlay.REGISTRY) {
            for (PatternAndFontOverlay.H3Category h3 : h2.subCategories) {
                if (h3.title.getString().equals(want)) return h3;
            }
        }
        return null;
    }

    private static PatternAndFontOverlay.H4Section newPatternSetSection(String key) {
        return new PatternAndFontOverlay.H4Section(
                Text.translatable("yunbeiuc.gui.pattern_sets." + key),
                Text.translatable("yunbeiuc.gui.pattern_sets." + key + ".desc"),
                VersionServices.resources().create("yunbeiuc", SIGN_TEXTURE_PATH))
                .setLabeledMode();
    }

    private static ResourceLocation signTexture(String fileName) {
        return VersionServices.resources().create("yunbeiuc", SIGN_TEXTURE_PATH + fileName);
    }

    private static Component setLabel(String key) {
        return Text.translatable("yunbeiuc.gui.pattern_sets.opt." + key);
    }

    private static PatternAndFontOverlay.H3Category findFirstLeafH3(PatternAndFontOverlay.H3Category h3) {
        if (h3.subCategories.isEmpty()) {
            return h3;
        }
        return findFirstLeafH3(h3.subCategories.get(0));
    }
}
