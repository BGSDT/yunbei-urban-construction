package com.beigu.yunbeiuc.forge;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.forge.client.YunbeiUrbanConstructionForgeClient;
import com.beigu.yunbeiuc.network.ChatCommandHandler;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(YunbeiUrbanConstruction.MOD_ID)
public final class YunbeiUrbanConstructionForge {
    public YunbeiUrbanConstructionForge() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(YunbeiUrbanConstruction.MOD_ID, modEventBus);
        YunbeiUrbanConstruction.init();

        modEventBus.register(ModCreativeTabEntries.class);

        modEventBus.addListener(YunbeiUrbanConstructionForgeClient::onClientSetup);

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ChatCommandHandler.register(event.getDispatcher());
    }
}
