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

/** Minecraft 1.20.1 implementation loaded by the shared VersionServices facade. */
public final class VersionAdapterImpl implements VersionAdapter {
    private static final BlockPlatform BLOCKS = new BlockPlatformImpl();
    private static final BlockEntityPlatform BLOCK_ENTITIES = new BlockEntityPlatformImpl();
    private static final PlaceholderResolver PLACEHOLDERS = key -> key;
    private static final TextPlatform TEXT = new TextPlatformImpl();
    private static final RegistryPlatform REGISTRIES = new RegistryPlatformImpl();
    private static final CreativeTabPlatform CREATIVE_TABS = new CreativeTabPlatformImpl();
    private static final ResourcePlatform RESOURCES = new ResourcePlatformImpl();

    /*
     * 客户端专属实现放在嵌套 Holder 里：专用服务端永远不会执行到这里，
     * Holder 的 <clinit> 也就不会触发，RenderPlatformImpl / GuiPlatformImpl
     * 因此不会被加载。这两个类引用了 com.mojang.blaze3d.* 与 net.minecraft.client.*，
     * NeoForge 的 RuntimeDistCleaner 在服务端会直接抛
     * "Attempted to load class ... for invalid dist DEDICATED_SERVER"，
     * 导致整个模组加载失败（ExceptionInInitializerError）。
     */
    private static final class ClientPlatforms {
        private static final GuiPlatform GUI = new GuiPlatformImpl();
        private static final RenderPlatform RENDER = new RenderPlatformImpl();
    }

    @Override
    public String minecraftVersion() {
        return "1.20.1";
    }

    @Override public BlockPlatform blocks() { return BLOCKS; }
    @Override public BlockEntityPlatform blockEntities() { return BLOCK_ENTITIES; }

    @Override
    public GuiPlatform gui() {
        return ClientPlatforms.GUI;
    }

    @Override
    public RenderPlatform render() {
        return ClientPlatforms.RENDER;
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
