package com.beigu.yunbeiuc;

import com.beigu.yunbeiuc.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class YunbeiUrbanConstructionDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModBlockTagsProvider::new);
		pack.addProvider(ModItemTagsProvider::new);
		pack.addProvider(ModEnUsLangProvider::new);
		pack.addProvider(ModLootTablesProvider::new);
		pack.addProvider(ModModelsProvider::new);
		pack.addProvider(ModRecipesProvider::new);
		pack.addProvider(ModZhCnLangProvider::new);
	}
}
