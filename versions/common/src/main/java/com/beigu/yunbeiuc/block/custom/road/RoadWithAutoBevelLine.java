package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;
/**
 * 自动斜角标线道路方块。
 * <p>
 * 自动连接逻辑见父类 {@link RoadWithAutoLine}，本类仅指定自动连接类型为斜线（BEVEL），
 */
public class RoadWithAutoBevelLine extends RoadWithAutoLine {

    public RoadWithAutoBevelLine(BlockBehaviour.Properties properties) {
        super(properties, RoadAutoLineType.BEVEL);
    }
}
