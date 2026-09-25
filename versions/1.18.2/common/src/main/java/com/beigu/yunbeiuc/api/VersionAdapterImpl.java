package com.beigu.yunbeiuc.api;

import com.beigu.yunbeiuc.api.block.BlockPlatform;
import com.beigu.yunbeiuc.api.block.BlockEntityPlatform;
import com.beigu.yunbeiuc.api.block.BlockEntityPlatformImpl;
import com.beigu.yunbeiuc.api.block.BlockPlatformImpl;
import com.beigu.yunbeiuc.api.gui.GuiPlatform;
import com.beigu.yunbeiuc.api.gui.GuiPlatformImpl;
import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.gui.RenderPlatformImpl;
import com.beigu.yunbeiuc.api.placeholder.PlaceholderResolver;
import com.beigu.yunbeiuc.api.text.TextPlatform;
import com.beigu.yunbeiuc.api.text.TextPlatformImpl;
import com.beigu.yunbeiuc.api.registry.RegistryPlatform;
import com.beigu.yunbeiuc.api.registry.RegistryPlatformImpl;
import com.beigu.yunbeiuc.api.item.CreativeTabPlatform;
import com.beigu.yunbeiuc.api.item.CreativeTabPlatformImpl;
import com.beigu.yunbeiuc.api.resource.ResourcePlatform;
import com.beigu.yunbeiuc.api.resource.ResourcePlatformImpl;

/** Minecraft 1.18.2 implementation loaded by the shared VersionServices facade. */
public final class VersionAdapterImpl implements VersionAdapter {
    private static final BlockPlatform BLOCKS = new BlockPlatformImpl();
    private static final BlockEntityPlatform BLOCK_ENTITIES = new BlockEntityPlatformImpl();
    private static final GuiPlatform GUI = new GuiPlatformImpl();
    private static final RenderPlatform RENDER = new RenderPlatformImpl();
    private static final PlaceholderResolver PLACEHOLDERS = key -> key;
    private static final TextPlatform TEXT = new TextPlatformImpl();
    private static final RegistryPlatform REGISTRIES = new RegistryPlatformImpl();
    private static final CreativeTabPlatform CREATIVE_TABS = new CreativeTabPlatformImpl();
    private static final ResourcePlatform RESOURCES = new ResourcePlatformImpl();

    @Override
    public String minecraftVersion() {
        return "1.18.2";
    }

    @Override public BlockPlatform blocks() { return BLOCKS; }
    @Override public BlockEntityPlatform blockEntities() { return BLOCK_ENTITIES; }

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

    @Override public TextPlatform text() { return TEXT; }
    @Override public RegistryPlatform registries() { return REGISTRIES; }
    @Override public CreativeTabPlatform creativeTabs() { return CREATIVE_TABS; }
    @Override public ResourcePlatform resources() { return RESOURCES; }
}
