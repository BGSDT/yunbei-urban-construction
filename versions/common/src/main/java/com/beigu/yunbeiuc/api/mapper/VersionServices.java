package com.beigu.yunbeiuc.api.mapper;

import com.beigu.yunbeiuc.api.VersionAdapter;
import com.beigu.yunbeiuc.api.block.BlockPlatform;
import com.beigu.yunbeiuc.api.gui.GuiPlatform;
import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.placeholder.PlaceholderResolver;
import com.beigu.yunbeiuc.api.text.TextPlatform;
import com.beigu.yunbeiuc.api.registry.RegistryPlatform;
import com.beigu.yunbeiuc.api.item.CreativeTabPlatform;
import com.beigu.yunbeiuc.api.resource.ResourcePlatform;

/** Entry point for APIs whose Minecraft signatures differ between versions. */
public final class VersionServices {
    private static final String IMPLEMENTATION = "com.beigu.yunbeiuc.api.VersionAdapterImpl";
    private static final VersionAdapter ADAPTER = loadAdapter();

    private VersionServices() {}

    private static VersionAdapter loadAdapter() {
        try {
            return (VersionAdapter) Class.forName(IMPLEMENTATION).getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | ClassCastException exception) {
            throw new ExceptionInInitializerError(
                    "Missing or invalid version adapter " + IMPLEMENTATION + ": " + exception);
        }
    }

    public static GuiPlatform gui() {
        return ADAPTER.gui();
    }

    public static BlockPlatform blocks() {
        return ADAPTER.blocks();
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
