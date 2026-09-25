package dev.architectury.platform;

import java.nio.file.Path;

/** 1.16.5 bridge for Architectury's pre-dev package name. */
public final class Platform {
    private Platform() {}
    public static Path getGameFolder() { return me.shedaniel.architectury.platform.Platform.getGameFolder(); }
    public static Path getConfigFolder() { return me.shedaniel.architectury.platform.Platform.getConfigFolder(); }
    public static Path getModsFolder() { return me.shedaniel.architectury.platform.Platform.getModsFolder(); }
    public static boolean isFabric() { return me.shedaniel.architectury.platform.Platform.isFabric(); }
    public static boolean isForge() { return me.shedaniel.architectury.platform.Platform.isForge(); }
    public static boolean isModLoaded(String id) { return me.shedaniel.architectury.platform.Platform.isModLoaded(id); }
    public static boolean isDevelopmentEnvironment() { return me.shedaniel.architectury.platform.Platform.isDevelopmentEnvironment(); }
}
