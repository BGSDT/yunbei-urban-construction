package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;
/**
 * 自动直角标线道路方块。
 * <p>
 * 自动连接逻辑见父类 {@link RoadWithAutoLine}，本类仅指定自动连接类型为直角（RIGHT_ANGLE），
 */
public class RoadSlabWithAutoRightangleLine extends RoadSlabWithAutoLine {

    public RoadSlabWithAutoRightangleLine(BlockBehaviour.Properties properties) {
        super(properties, RoadWithAutoLine.RoadAutoLineType.RIGHT_ANGLE);
    }
}
