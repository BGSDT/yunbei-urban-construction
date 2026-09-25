package com.beigu.yunbeiuc.api;

import com.beigu.yunbeiuc.api.gui.GuiPlatform;
import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.placeholder.PlaceholderResolver;

/** Complete per-Minecraft-version service boundary. */
public interface VersionAdapter {
    String minecraftVersion();
    GuiPlatform gui();
    RenderPlatform render();
    PlaceholderResolver placeholders();
}
