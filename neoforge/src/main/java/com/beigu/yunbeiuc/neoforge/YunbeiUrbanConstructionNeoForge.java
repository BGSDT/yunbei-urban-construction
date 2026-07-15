package com.beigu.yunbeiuc.neoforge;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.neoforge.client.YunbeiUrbanConstructionNeoForgeClient;
import com.beigu.yunbeiuc.network.ChatCommandHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;

@Mod(YunbeiUrbanConstruction.MOD_ID)
public final class YunbeiUrbanConstructionNeoForge {
    @SuppressWarnings("removal")
    public YunbeiUrbanConstructionNeoForge(IEventBus modEventBus) {
        YunbeiUrbanConstruction.init();

        modEventBus.addListener(ModCreativeTabEntries::onBuildCreativeModeTabContents);

        modEventBus.addListener(YunbeiUrbanConstructionNeoForgeClient::onClientSetup);
        modEventBus.addListener(YunbeiUrbanConstructionNeoForgeClient::onRegisterClientReloadListeners);

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ChatCommandHandler.register(event.getDispatcher());
    }
}
