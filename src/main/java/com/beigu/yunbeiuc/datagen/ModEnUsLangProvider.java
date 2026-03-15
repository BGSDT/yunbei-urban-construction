package com.beigu.yunbeiuc.datagen;

import com.beigu.yunbeiuc.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModEnUsLangProvider extends FabricLanguageProvider {
    public ModEnUsLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput,"en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.WAND,"Wand");
        translationBuilder.add(ModItems.TREE_WAND,"Tree Wand");
        translationBuilder.add("item.yunbeiuc.tree_wand.tooltip","Right-click on grass or dirt to plant an oak tree");
        translationBuilder.add("item.yunbeiuc.tree_wand.success","Planted an oak sapling!");
        translationBuilder.add("item.yunbeiuc.tree_wand.planted_sapling","Planted an oak sapling!");
        translationBuilder.add("item.yunbeiuc.tree_wand.failed","Cannot generate an oak tree here!");
        translationBuilder.add("item.yunbeiuc.tree_wand.no_space","Not enough space to plant a tree!");
        translationBuilder.add("item.yunbeiuc.tree_wand.invalid_block","Cannot plant a tree on this block!");
        translationBuilder.add(ModItems.WATER_WAND,"Water Wand");
        translationBuilder.add("item.yunbeiuc.water_wand.tooltip","Right-click within the range of 3 * 3 * 3 to replace with water source");
        translationBuilder.add("item.yunbeiuc.water_wand.success","Successfully replaced with water source!");
        translationBuilder.add(ModItems.ROTATED_WAND,"Rotated Wand");
        translationBuilder.add("item.yunbeiuc.rotated_wand.tooltip","Right-click on the block to rotate it 90 degrees clockwise");



        translationBuilder.add("itemGroup.yunbeiuc_rb_group","云北城建 | 道路方块");
        translationBuilder.add("itemGroup.yunbeiuc_sings_group","云北城建 | 道路标识");
    }
}
