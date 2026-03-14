package com.beigu.yunbeiuc.block.custom.sign.abandoned.data;

import net.minecraft.util.StringIdentifiable;

public enum SignNoEntryForVehicles implements StringIdentifiable {
    SIGN_NO_NON_MOTOR_VEHICLES("sign_no_non_motor_vehicles"),
    SIGN_NO_SMALL_PASSENGER_CAR("sign_no_small_passenger_car"),
    SIGN_NO_HAZARDOUS_MATERIALS_TRANSPORT_VEHICLE("sign_no_hazardous_materials_transport_vehicle"),
    SIGN_NO_TRICYCLE("sign_no_tricycle"),
    SIGN_NO_ALL("sign_no_all"),
    SIGN_NO_TRAILER("sign_no_trailer"),
    SIGN_NO_PEDESTRIAN("sign_no_pedestrian"),
    SIGN_NO_RICKSHAW("sign_no_rickshaw"),
    SIGN_NO_ENTRY("sign_no_entry"),
    SIGN_NO_TRUCK("sign_no_truck"),
    SIGN_NO_MOTORCYCLE("sign_no_motorcycle"),
    SIGN_NO_LARGE_BUS("sign_no_large_bus"),
    SIGN_NO_ANIMAL_DRAWN_CART("sign_no_animal_drawn_cart"),
    SIGN_NO_HUMAN_POWERED_PASSENGER_TRICYCLE("sign_no_human_powered_passenger_tricycle"),
    SIGN_NO_ELECTRIC_VEHICLE("sign_no_electric_vehicle"),
    SIGN_NO_THREE_WHEELED_VEHICLE("sign_no_three_wheeled_vehicle"),
    SIGN_NO_MOTOR_VEHICLES("sign_no_motor_vehicles"),
    SIGN_NO_TRACTOR("sign_no_tractor"),
    SIGN_NO_HUMAN_POWERED_CARGO_TRICYCLE("sign_no_human_powered_cargo_tricycle"),
    SIGN_NO_PARKING("sign_no_parking"),
    SIGN_NO_PARKING_LONG_TIME("sign_no_parking_long_time");

    private final String name;

    SignNoEntryForVehicles(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}