package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.screen.AbstractOptionListWidget;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

/**
 * 红绿灯方向列表条目图标：把 {@link TrafficLightsBlockEntity.DirectionType} 映射到对应的
 * 红色状态贴图（复用 {@code textures/block/lights/*_red.png}），用于替代原先的纯色色块。
 */
public final class TrafficLightsDirectionIcons {
    private static final Map<TrafficLightsBlockEntity.DirectionType, AbstractOptionListWidget.Icon> ICONS = new EnumMap<>(TrafficLightsBlockEntity.DirectionType.class);

    static {
        register(TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE, "straight_red", 64, 64);
        register(TrafficLightsBlockEntity.DirectionType.STRAIGHT_ARROW, "straight_arrow_yellow", 32, 32);
        register(TrafficLightsBlockEntity.DirectionType.LEFT_TURN, "left_turn_red", 32, 32);
        register(TrafficLightsBlockEntity.DirectionType.RIGHT_TURN, "right_turn_yellow", 32, 32);
        register(TrafficLightsBlockEntity.DirectionType.TURN_AROUND, "turn_around_green", 64, 64);
        register(TrafficLightsBlockEntity.DirectionType.NON_MOTOR_VEHICLES, "non_motor_vehicles_red", 64, 64);
        register(TrafficLightsBlockEntity.DirectionType.NON_MOTOR_VEHICLES_LEFT_TURN, "non_motor_vehicles_left_turn_yellow", 64, 64);
        register(TrafficLightsBlockEntity.DirectionType.NON_MOTOR_VEHICLES_RIGHT_TURN, "non_motor_vehicles_right_turn_green", 64, 64);
        register(TrafficLightsBlockEntity.DirectionType.COLOR_FLASH, "straight_yellow", 64, 64);
        register(TrafficLightsBlockEntity.DirectionType.SLOW_FLASH, "slow_yellow", 64, 64);
    }

    private static void register(TrafficLightsBlockEntity.DirectionType type, String fileName, int width, int height) {
        Identifier texture = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/" + fileName + ".png");
        ICONS.put(type, new AbstractOptionListWidget.Icon(texture, width, height));
    }

    public static AbstractOptionListWidget.Icon get(TrafficLightsBlockEntity.DirectionType type) {
        return ICONS.get(type);
    }

    private TrafficLightsDirectionIcons() {
    }
}
