package com.beigu.yunbeiuc;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.RoadBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import com.beigu.yunbeiuc.item.ModItemGroups;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.signal.mtr.MtrVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class
YunbeiUrbanConstruction {
    public static final String MOD_ID = "yunbeiuc";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        MunicipalBlocks.init();
        RoadBlocks.init();
        SignBlocks.init();
        ModItemGroups.init();
        ModItems.init();
        ModBlockEntities.init();
        ModMessages.registerC2SPackets();
        ModMessages.registerS2CPackets();

        LOGGER.info("[yunbeiuc] MTR 联动：{}", MtrVersion.detect().displayName());
    }
}
