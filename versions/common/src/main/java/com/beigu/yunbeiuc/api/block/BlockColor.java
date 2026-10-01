package com.beigu.yunbeiuc.api.block;

/**
 * Version-neutral block map colour.
 *
 * <p>Minecraft 1.16.5-1.19.4 expose {@code net.minecraft.world.level.material.MaterialColor};
 * 1.20+ replaced it with {@code net.minecraft.world.level.material.MapColor}. The two classes are
 * mutually exclusive across versions, so the shared tree cannot reference either one directly and
 * uses this enum instead. Every version maps it onto its own type in {@code BlockPlatformImpl}.
 *
 * <p>The constant names are identical in both Minecraft types.
 */
public enum BlockColor {
    NONE,
    GRASS,
    SAND,
    WOOL,
    FIRE,
    ICE,
    METAL,
    PLANT,
    SNOW,
    CLAY,
    DIRT,
    STONE,
    WATER,
    WOOD,
    QUARTZ,
    COLOR_ORANGE,
    COLOR_MAGENTA,
    COLOR_LIGHT_BLUE,
    COLOR_YELLOW,
    COLOR_LIGHT_GREEN,
    COLOR_PINK,
    COLOR_GRAY,
    COLOR_LIGHT_GRAY,
    COLOR_CYAN,
    COLOR_PURPLE,
    COLOR_BLUE,
    COLOR_BROWN,
    COLOR_GREEN,
    COLOR_RED,
    COLOR_BLACK,
    GOLD,
    DIAMOND,
    LAPIS,
    EMERALD,
    PODZOL,
    NETHER,
    TERRACOTTA_WHITE,
    TERRACOTTA_ORANGE,
    TERRACOTTA_MAGENTA,
    TERRACOTTA_LIGHT_BLUE,
    TERRACOTTA_YELLOW,
    TERRACOTTA_LIGHT_GREEN,
    TERRACOTTA_PINK,
    TERRACOTTA_GRAY,
    TERRACOTTA_LIGHT_GRAY,
    TERRACOTTA_CYAN,
    TERRACOTTA_PURPLE,
    TERRACOTTA_BLUE,
    TERRACOTTA_BROWN,
    TERRACOTTA_GREEN,
    TERRACOTTA_RED,
    TERRACOTTA_BLACK,
    CRIMSON_NYLIUM,
    CRIMSON_STEM,
    CRIMSON_HYPHAE,
    WARPED_NYLIUM,
    WARPED_STEM,
    WARPED_HYPHAE,
    WARPED_WART_BLOCK
}
