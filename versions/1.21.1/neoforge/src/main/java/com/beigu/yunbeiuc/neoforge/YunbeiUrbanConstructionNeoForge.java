package com.beigu.yunbeiuc.neoforge;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.neoforge.client.YunbeiUrbanConstructionNeoForgeClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(YunbeiUrbanConstruction.MOD_ID)
public final class YunbeiUrbanConstructionNeoForge {
    public YunbeiUrbanConstructionNeoForge(IEventBus modEventBus) {
        YunbeiUrbanConstruction.init();


        modEventBus.addListener(YunbeiUrbanConstructionNeoForgeClient::onClientSetup);
    }
}
