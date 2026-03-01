package com.beigu.yunbeiuc;

import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.block.custom.poles.flag.FlagLoader;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import com.beigu.yunbeiuc.item.ModItemGroups;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.network.ModMessages;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YunbeiUrbanConstruction implements ModInitializer {
	public static final String MOD_ID = "yunbeiuc";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");


        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModMessages.registerC2SPackets();
        ModMessages.registerS2CPackets();
        ModItemGroups.registerGroups();
        ModItems.registerItems();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return new Identifier(MOD_ID, "flag_loader");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        FlagLoader.loadFlags(manager);
                    }
                }
        );
	}
}