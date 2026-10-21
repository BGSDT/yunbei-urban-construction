package com.beigu.yunbeiuc.render;

public class SignTypeConverter {

    public static SignType convert(Enum<?> type) {
        if (type == null) {
            return SignType.NORMAL;
        }

        String name = type.name();
        return switch (name) {
            case "POLE_L" -> SignType.POLE_L;
            case "POLE_H" -> SignType.POLE_H;
            case "NORMAL" -> SignType.NORMAL;
            default -> SignType.NORMAL;
        };
    }
}
