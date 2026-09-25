package com.beigu.yunbeiuc.api;

import com.beigu.yunbeiuc.api.block.BlockPlatform;
import com.beigu.yunbeiuc.api.gui.GuiPlatform;
import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.placeholder.PlaceholderResolver;
import com.beigu.yunbeiuc.api.text.TextPlatform;
import com.beigu.yunbeiuc.api.registry.RegistryPlatform;
import com.beigu.yunbeiuc.api.item.CreativeTabPlatform;
import com.beigu.yunbeiuc.api.resource.ResourcePlatform;

/** Complete per-Minecraft-version service boundary. */
public interface VersionAdapter {
    String minecraftVersion();
    BlockPlatform blocks();
    GuiPlatform gui();
    RenderPlatform render();
    PlaceholderResolver placeholders();
    TextPlatform text();
    RegistryPlatform registries();
    CreativeTabPlatform creativeTabs();
    ResourcePlatform resources();
}
