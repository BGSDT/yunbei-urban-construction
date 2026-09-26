package com.beigu.yunbeiuc.fabric;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.util.CustomFontManager;
import com.beigu.yunbeiuc.util.FlagLoader;
import com.beigu.yunbeiuc.util.TrafficLightsPatternPresetLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public final class YunbeiUrbanConstructionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YunbeiUrbanConstruction.init();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "flag_loader");
                    }

                    @Override
                    public void onResourceManagerReload(net.minecraft.server.packs.resources.ResourceManager manager) {
                        FlagLoader.loadFlags(manager);
                        TrafficLightsPatternPresetLoader.loadPresets(manager);
                        CustomFontManager.getInstance().onResourceReload();
                    }
                }
        );
    }
}
