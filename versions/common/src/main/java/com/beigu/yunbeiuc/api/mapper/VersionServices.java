package com.beigu.yunbeiuc.api.mapper;

import com.beigu.yunbeiuc.api.VersionAdapter;
import com.beigu.yunbeiuc.api.VersionAdapterImpl;
import com.beigu.yunbeiuc.api.block.BlockPlatform;
import com.beigu.yunbeiuc.api.block.BlockEntityPlatform;
import com.beigu.yunbeiuc.api.gui.GuiPlatform;
import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.placeholder.PlaceholderResolver;
import com.beigu.yunbeiuc.api.text.TextPlatform;
import com.beigu.yunbeiuc.api.registry.RegistryPlatform;
import com.beigu.yunbeiuc.api.item.CreativeTabPlatform;
import com.beigu.yunbeiuc.api.resource.ResourcePlatform;

public final class VersionServices {
    private static final VersionAdapter ADAPTER = new VersionAdapterImpl();

    private VersionServices() {}

    public static GuiPlatform gui() {
        return ADAPTER.gui();
    }

    public static BlockPlatform blocks() {
        return ADAPTER.blocks();
    }

    public static BlockEntityPlatform blockEntities() {
        return ADAPTER.blockEntities();
    }

    public static RenderPlatform render() {
        return ADAPTER.render();
    }

    public static PlaceholderResolver placeholders() {
        return ADAPTER.placeholders();
    }

    public static TextPlatform text() {
        return ADAPTER.text();
    }

    public static RegistryPlatform registries() {
        return ADAPTER.registries();
    }

    public static CreativeTabPlatform creativeTabs() {
        return ADAPTER.creativeTabs();
    }

    public static ResourcePlatform resources() {
        return ADAPTER.resources();
    }

    public static String minecraftVersion() {
        return ADAPTER.minecraftVersion();
    }
}
