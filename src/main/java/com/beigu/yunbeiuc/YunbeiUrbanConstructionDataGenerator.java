package com.beigu.yunbeiuc;

import com.beigu.yunbeiuc.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class YunbeiUrbanConstructionDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		fabricDataGenerator.addProvider(ModBlockTagsProvider::new);
		fabricDataGenerator.addProvider(ModItemTagsProvider::new);
		fabricDataGenerator.addProvider(ModEnUsLangProvider::new);
		fabricDataGenerator.addProvider(ModLootTablesProvider::new);
		fabricDataGenerator.addProvider(ModModelsProvider::new);
		fabricDataGenerator.addProvider(ModRecipesProvider::new);
		fabricDataGenerator.addProvider(ModZhCnLangProvider::new);
	}
}
