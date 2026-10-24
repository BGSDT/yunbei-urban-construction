package com.beigu.yunbeiuc;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.RoadBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import com.beigu.yunbeiuc.item.ModItemGroups;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.network.ModMessages;
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
        // 本模组没有 S2C 通道（与 yunzhu 分支一致）：客户端所需状态由方块实体更新包下发，
        // 轨道相位界面由客户端本地打开。服务端绝不注册任何 S2C 接收器。
    }
}
