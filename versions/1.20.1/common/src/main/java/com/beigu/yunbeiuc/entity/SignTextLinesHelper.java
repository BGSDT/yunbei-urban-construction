package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextAlignment;

/**
 * 将原固定渲染体系的布局参数换算为 TextLineData 的工具类。
 *
 * 换算关系（保证渲染位置与原渲染器逐像素一致）：
 * - AbstractTextDisplayEntityRenderer 的 baseScale = 0.05 * fontSize，
 *   故 fontSize = 原渲染 scale * 20（如 0.04f → 0.8）；
 * - TextLineData 的 xOffset/yOffset 与原 andX/andY 同坐标系（单位 1/16 像素，y 向上为正）；
 * - 原渲染器文本均为 bold，生成默认行时同步置 bold；
 * - 原 renderCenteredText/renderLeftAlignedText/renderRightAlignedText 的锚点语义
 *   与 TextAlignment 的 CENTER/LEFT/RIGHT 居中行完全一致；
 * - renderTexture 的 size 与 AbstractTextDisplayEntityRenderer 的 imageSize = 0.4 * fontSize
 *   对应，故 fontSize = size / 0.4；
 * - renderTextWithCustomZ / renderExpresswayText 的 zOffsetDelta（方块单位）
 *   需乘以 16 存入 TextLineData.zOffset（该类以 1/16 为单位）。
 */
public final class SignTextLinesHelper {

    private SignTextLinesHelper() {}

    /** 对应 renderCenteredText：锚点为文本中心 */
    public static TextLineData centered(String text, float andX, float andY, float scale, int color) {
        TextLineData line = base(text, andX, andY, scale, color);
        line.setAlignment(TextAlignment.CENTER_CENTER);
        return line;
    }

    /** 对应 renderLeftAlignedText：锚点为文本左边缘 */
    public static TextLineData left(String text, float andX, float andY, float scale, int color) {
        TextLineData line = base(text, andX, andY, scale, color);
        line.setAlignment(TextAlignment.LEFT_CENTER);
        return line;
    }

    /** 对应 renderRightAlignedText：锚点为文本右边缘 */
    public static TextLineData right(String text, float andX, float andY, float scale, int color) {
        TextLineData line = base(text, andX, andY, scale, color);
        line.setAlignment(TextAlignment.RIGHT_CENTER);
        return line;
    }

    /** 对应 renderTexture / renderTextWithCustomZ 组合：带独立 z 偏移的居中文本 */
    public static TextLineData centeredWithZ(String text, float andX, float andY, float scale, int color, float zOffsetDelta) {
        TextLineData line = centered(text, andX, andY, scale, color);
        line.setZOffset(zOffsetDelta * 16f);
        return line;
    }

    /** 对应 renderTexture：以 -texture 指令行表示 logo，路径支持 {字段名} 占位符（按枚举/编号选纹理），锚点为纹理中心 */
    public static TextLineData logo(String texturePath, float andX, float andY, float size) {
        TextLineData line = new TextLineData("-texture " + texturePath);
        line.setXOffset(andX);
        line.setYOffset(andY);
        line.setZOffset(0);
        line.setColor(0xFFFFFF);
        line.setAlignment(TextAlignment.CENTER_CENTER);
        line.setBold(false);
        line.setFontSize(size / 0.4f);
        return line;
    }

    private static TextLineData base(String text, float andX, float andY, float scale, int color) {
        TextLineData line = new TextLineData(text);
        line.setXOffset(andX);
        line.setYOffset(andY);
        line.setZOffset(0);
        line.setColor(color);
        line.setAlignment(TextAlignment.CENTER_CENTER);
        // 原渲染器文本一律 bold
        line.setBold(true);
        line.setFontSize(scale * 20f);
        return line;
    }
}
