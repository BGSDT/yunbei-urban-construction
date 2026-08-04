package com.beigu.yunbeiuc.block.custom.road;

/**
 * 自动直角标线道路方块。
 * <p>
 * 自动连接逻辑见父类 {@link RoadWithAutoLine}，本类仅指定自动连接类型为直角（RIGHT_ANGLE），
 */
public class RoadWithAutoRightangleLine extends RoadWithAutoLine {

    public RoadWithAutoRightangleLine(Settings settings) {
        super(settings, RoadAutoLineType.RIGHT_ANGLE);
    }
}
