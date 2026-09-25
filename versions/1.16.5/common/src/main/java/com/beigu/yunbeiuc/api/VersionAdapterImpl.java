package com.beigu.yunbeiuc.api;

import com.beigu.yunbeiuc.api.gui.GuiPlatform;
import com.beigu.yunbeiuc.api.gui.GuiPlatformImpl;
import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.gui.RenderPlatformImpl;
import com.beigu.yunbeiuc.api.placeholder.PlaceholderResolver;

/** Minecraft 1.18.2 implementation loaded by the shared VersionServices facade. */
public final class VersionAdapterImpl implements VersionAdapter {
    private static final GuiPlatform GUI = new GuiPlatformImpl();
    private static final RenderPlatform RENDER = new RenderPlatformImpl();
    private static final PlaceholderResolver PLACEHOLDERS = key -> key;

    @Override
    public String minecraftVersion() {
        return "1.18.2";
    }

    @Override
    public GuiPlatform gui() {
        return GUI;
    }

    @Override
    public RenderPlatform render() {
        return RENDER;
    }

    @Override
    public PlaceholderResolver placeholders() {
        return PLACEHOLDERS;
    }
}
