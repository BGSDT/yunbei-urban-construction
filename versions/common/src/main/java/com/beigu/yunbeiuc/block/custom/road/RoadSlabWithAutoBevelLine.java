package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;
/**
 * 自动斜角标线道路方块。
 * <p>
 * 自动连接逻辑见父类 {@link RoadWithAutoLine}，本类仅指定自动连接类型为斜线（BEVEL），
 */
public class RoadSlabWithAutoBevelLine extends RoadSlabWithAutoLine {

    public RoadSlabWithAutoBevelLine(BlockBehaviour.Properties properties) {
        super(properties, RoadWithAutoLine.RoadAutoLineType.BEVEL);
    }
}
