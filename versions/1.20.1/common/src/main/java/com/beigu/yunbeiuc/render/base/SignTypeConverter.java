package com.beigu.yunbeiuc.render.base;

/**
 * 标志牌类型转换器
 * 将各个实体的 Type 枚举转换为基类的 SignType
 */
public class SignTypeConverter {

    /**
     * 通用转换方法
     * 通过枚举名称匹配来转换
     */
    public static BaseSignRenderer.SignType convert(Enum<?> type) {
        if (type == null) {
            return BaseSignRenderer.SignType.NORMAL;
        }

        String name = type.name();
        return switch (name) {
            case "POLE_L" -> BaseSignRenderer.SignType.POLE_L;
            case "POLE_H" -> BaseSignRenderer.SignType.POLE_H;
            case "NORMAL" -> BaseSignRenderer.SignType.NORMAL;
            default -> BaseSignRenderer.SignType.NORMAL;
        };
    }
}
