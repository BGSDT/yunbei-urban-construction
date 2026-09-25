package com.beigu.yunbeiuc.api.mapper;

import com.beigu.yunbeiuc.api.gui.GuiPlatform;
import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.placeholder.PlaceholderResolver;

/** Service registry populated by each version module during initialization. */
public final class VersionServices {
    private static GuiPlatform gui;
    private static RenderPlatform<?> render;
    private static PlaceholderResolver placeholders;

    private VersionServices() {}

    public static void install(GuiPlatform guiPlatform, RenderPlatform<?> renderPlatform) {
        gui = guiPlatform;
        render = renderPlatform;
    }

    public static void installPlaceholders(PlaceholderResolver resolver) {
        placeholders = resolver;
    }

    public static GuiPlatform gui() {
        if (gui == null) throw new IllegalStateException("Version GUI services have not been installed");
        return gui;
    }

    public static RenderPlatform<?> render() {
        if (render == null) throw new IllegalStateException("Version render services have not been installed");
        return render;
    }

    public static PlaceholderResolver placeholders() {
        if (placeholders == null) throw new IllegalStateException("Version placeholder services have not been installed");
        return placeholders;
    }
}
