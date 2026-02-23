package com.beigu.yunbeiuc.block.custom.sign.data;

import net.minecraft.util.StringIdentifiable;

public enum SignGuideIntersectionAdvanceWarning implements StringIdentifiable {
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1("sign_guide_intersection_advance_warning_1"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2("sign_guide_intersection_advance_warning_2"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_NORTH("sign_guide_intersection_advance_warning_3_north"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_SOUTH("sign_guide_intersection_advance_warning_3_south"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_EAST("sign_guide_intersection_advance_warning_3_east"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_WEST("sign_guide_intersection_advance_warning_3_west"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4_NORTH("sign_guide_intersection_advance_warning_4_north"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4_SOUTH("sign_guide_intersection_advance_warning_4_south"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4_EAST("sign_guide_intersection_advance_warning_4_east"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4_WEST("sign_guide_intersection_advance_warning_4_west"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5("sign_guide_intersection_advance_warning_5"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6("sign_guide_intersection_advance_warning_6"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7("sign_guide_intersection_advance_warning_7"),
    SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_8("sign_guide_intersection_advance_warning_8");

    private final String name;

    SignGuideIntersectionAdvanceWarning(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}