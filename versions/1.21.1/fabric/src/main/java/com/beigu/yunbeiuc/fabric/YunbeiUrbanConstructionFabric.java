package com.beigu.yunbeiuc.fabric;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.network.ChatCommandHandler;
import com.beigu.yunbeiuc.render.font.CustomFontManager;
import com.beigu.yunbeiuc.render.json.FlagLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public final class YunbeiUrbanConstructionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YunbeiUrbanConstruction.init();
        ModCreativeTabEntries.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ChatCommandHandler.register(dispatcher);
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return Identifier.of(YunbeiUrbanConstruction.MOD_ID, "flag_loader");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        FlagLoader.loadFlags(manager);
                        CustomFontManager.getInstance().onResourceReload();
                    }
                }
        );
    }
}
