package com.beigu.yunbeiuc;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.RoadBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import com.beigu.yunbeiuc.item.ModItemGroups;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.network.ModMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class
YunbeiUrbanConstruction {
    public static final String MOD_ID = "yunbeiuc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // First register blocks, then items (which depend on blocks via BlockItem)
        MunicipalBlocks.init();
        RoadBlocks.init();
        SignBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModItemGroups.init();
        ModMessages.registerC2SPackets();

        LOGGER.info("Yunbei Urban Construction has finished loading");
    }
}
