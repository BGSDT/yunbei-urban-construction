package dev.architectury.platform.forge;

import net.minecraftforge.eventbus.api.IEventBus;

/** 1.16.5 bridge for Architectury's pre-dev package name. */
public final class EventBuses {
    private EventBuses() {}
    public static void registerModEventBus(String modId, IEventBus eventBus) {
        me.shedaniel.architectury.platform.forge.EventBuses.registerModEventBus(modId, eventBus);
    }
}
