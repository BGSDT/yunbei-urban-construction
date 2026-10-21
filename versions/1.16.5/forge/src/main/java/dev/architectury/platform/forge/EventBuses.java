package dev.architectury.platform.forge;

import net.minecraftforge.eventbus.api.IEventBus;

public final class EventBuses {
    private EventBuses() {}
    public static void registerModEventBus(String modId, IEventBus eventBus) {
        me.shedaniel.architectury.platform.forge.EventBuses.registerModEventBus(modId, eventBus);
    }
}
