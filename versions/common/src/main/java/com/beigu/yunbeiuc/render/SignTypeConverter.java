package com.beigu.yunbeiuc.render;

/**
 * 标志牌类型转换器
 * 将各个实体的 Type 枚举转换为 SignType
 */
public class SignTypeConverter {

    /**
     * 通用转换方法
     * 通过枚举名称匹配来转换
     */
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
