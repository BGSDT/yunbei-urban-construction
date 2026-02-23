package com.beigu.yunbeiuc.item;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.item.custom.CrashBarrierConcreteItem;
import com.beigu.yunbeiuc.item.custom.DebugTool;
import com.beigu.yunbeiuc.item.custom.RoadConstructionBarrierBlueItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item CRASH_BARRIER_CONCRETE_ITEM = registerItem("crash_barrier_concrete", new CrashBarrierConcreteItem(ModBlocks.CRASH_BARRIER_CONCRETE, new Item.Settings()));
    public static final Item ROAD_CONSTRUCTION_BARRIER_BLUE_ITEM = registerItem("road_construction_barrier_blue", new RoadConstructionBarrierBlueItem(ModBlocks.ROAD_CONSTRUCTION_BARRIER_BLUE, new Item.Settings()));

    public static final Item DEBUG_TOOL = registerItem("debug_tool", new DebugTool(new Item.Settings()));
    
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(YunbeiUrbanConstruction.MOD_ID, name), item);
    }

    public static void registerItems() {

    }
}
