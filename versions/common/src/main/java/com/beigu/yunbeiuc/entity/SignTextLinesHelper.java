package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextAlignment;
import com.beigu.yunbeiuc.util.GlobalFontSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

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
 *
 * ABC 交通字体：
 * - centered/left/right/centeredWithZ 末尾的 abcFont 参数取值为 "a" / "b" / "c"，决定该行走哪种 ABC 字体；
 * - ABC 字体行会把文本直接包成 -json {"text":"...","font":"yunbeiuc:traf_sign_font_?"} 写入
 *   TextLineData.text 并随 NBT 固化（UI/存档可见），渲染端仅按 -json 指令解析，不做任何临时包装；
 * - 本类不做取值校验（按约定只填 a/b/c），非法值不会触发包装（该行走原版字体路径）；
 * - logo() 生成的是 -texture 纹理指令行，不参与字体选择，故不接收该参数；
 * - 全局设置（GlobalFontSettings，全局覆盖）：
 *   原版uniform → 忽略行内 abcFont，全部走原版字体（不包 -json）；
 *   A/B/C 型 → 强制使用对应字体，忽略行内 abcFont；
 *   路牌自适应 → 使用行内 abcFont（即方块自带布局的字体），未填或非法值兜底 A 型；
 *   后四种（含自适应）新生成的默认行一律取消加粗（bold 随行写入 NBT，已放置方块不受影响）；
 * - 走 ABC 字体时新生成的 ABC 字体行 y 位置自动下移 1（单位 1/16，y 向上为正），补偿 ABC 字体基线偏移。
 */
public final class SignTextLinesHelper {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    /** ABC 字体行的 y 位置下移量（单位 1/16，y 向上为正）：补偿 ABC 字体的基线/字形上偏。 */
    private static final float ABC_FONT_Y_OFFSET = 1.0f;

    private SignTextLinesHelper() {}

    /** 对应 renderCenteredText：锚点为文本中心；abcFont 取 "a" / "b" / "c" */
    public static TextLineData centered(String text, float andX, float andY, float scale, int color, String abcFont) {
        TextLineData line = base(text, andX, andY, scale, color, abcFont);
        line.setAlignment(TextAlignment.CENTER_CENTER);
        return line;
    }

    /** 对应 renderLeftAlignedText：锚点为文本左边缘；abcFont 取 "a" / "b" / "c" */
    public static TextLineData left(String text, float andX, float andY, float scale, int color, String abcFont) {
        TextLineData line = base(text, andX, andY, scale, color, abcFont);
        line.setAlignment(TextAlignment.LEFT_CENTER);
        return line;
    }

    /** 对应 renderRightAlignedText：锚点为文本右边缘；abcFont 取 "a" / "b" / "c" */
    public static TextLineData right(String text, float andX, float andY, float scale, int color, String abcFont) {
        TextLineData line = base(text, andX, andY, scale, color, abcFont);
        line.setAlignment(TextAlignment.RIGHT_CENTER);
        return line;
    }

    /** 对应 renderTexture / renderTextWithCustomZ 组合：带独立 z 偏移的居中文本；abcFont 取 "a" / "b" / "c" */
    public static TextLineData centeredWithZ(String text, float andX, float andY, float scale, int color, float zOffsetDelta, String abcFont) {
        TextLineData line = centered(text, andX, andY, scale, color, abcFont);
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
        // 系统默认布局行：删除时 UI 需二次确认（即使占位符被用户改写）
        line.setBuiltin(true);
        return line;
    }

    private static TextLineData base(String text, float andX, float andY, float scale, int color, String abcFont) {
        TextLineData line = new TextLineData(text);
        line.setXOffset(andX);
        line.setYOffset(andY);
        line.setZOffset(0);
        line.setColor(color);
        line.setAlignment(TextAlignment.CENTER_CENTER);
        // ABC 交通字体归属由全局设置决定，行参数只是「路牌自适应」模式的取值来源：
        // - 原版uniform → 忽略行参数，全部走原版字体（不包 -json）；
        // - A/B/C 型 → 强制该字体，忽略行参数；
        // - 路牌自适应 → 用行参数（方块自带布局所选的字体）。
        String effFont = resolveFont(abcFont);
        // ABC 字体行直接把文本包成 -json 指令行写入 TextLineData.text（随 NBT 固化，UI/存档可见），
        // 而非在渲染端临时包装；占位符 {字段} 在渲染时先展开再解析 JSON。
        if (!effFont.isEmpty()) {
            line.setText("-json " + wrapJson(text, fontId(effFont)));
            // ABC 字体行：y 位置自动下移（单位 1/16，y 向上为正），补偿 ABC 字体的基线/字形上偏
            line.setYOffset(andY - ABC_FONT_Y_OFFSET);
        }
        // 走 ABC 字体时新生成的默认行一律取消加粗（ABC 字体本身含字重，避免原版 bold 二次加粗发糊）；
        // 原版uniform 模式保持原渲染器行为（bold）。bold 随行写入 NBT，故已放置方块不受全局设置影响。
        line.setBold(!GlobalFontSettings.isAbcMode());
        line.setFontSize(scale * 20f);
        // ABC 交通字体标识："a" / "b" / "c"
        line.setAbcFont(effFont);
        // 系统默认布局行：删除时 UI 需二次确认（即使占位符被用户改写）
        line.setBuiltin(true);
        return line;
    }

    /**
     * 对界面新建的文本行套用全局字体设置。
     *
     * <p>与 {@link #base} 使用同一套规则与同一偏移量（{@link #ABC_FONT_Y_OFFSET}）：
     * 原版uniform 时清空 abcFont、走原版字体；A/B/C 型时套用该字体；
     * 路牌自适应时套用 {@code signAbcFont}（当前方块自带布局的字体，见 {@link #resolveFont}）。
     *
     * <p>文本本身是 {@code -} 开头的指令行（{@code -texture}/{@code -rect}/{@code -json} 等）时不改写，
     * 否则会破坏图案指令与已套用的字体指令。
     *
     * @param line        界面新建的文本行
     * @param signAbcFont 当前方块自带布局记录的字体（"a"/"b"/"c"，未知填 "a"），仅路牌自适应模式使用
     * @return 传入的行（便于链式使用）
     */
    public static TextLineData applyGlobalFontSetting(TextLineData line, String signAbcFont) {
        if (line == null) {
            return null;
        }
        String effFont = resolveFont(signAbcFont);
        if (effFont.isEmpty()) {
            line.setAbcFont("");
            return line;
        }
        String current = line.getText() == null ? "" : line.getText();
        if (current.trim().startsWith("-")) {
            return line;
        }
        line.setText("-json " + wrapJson(current, fontId(effFont)));
        line.setYOffset(line.getYOffset() - ABC_FONT_Y_OFFSET);
        // ABC 字体本身含字重，避免原版 bold 二次加粗发糊
        line.setBold(false);
        line.setAbcFont(effFont);
        return line;
    }

    /**
     * 按全局设置解析本行实际使用的 ABC 字体。
     *
     * @param lineAbcFont 行内记录的字体（"a"/"b"/"c"），仅路牌自适应模式参与决策
     * @return "a"/"b"/"c"；空串表示走原版uniform（不包裹 -json）
     */
    private static String resolveFont(String lineAbcFont) {
        return switch (GlobalFontSettings.getMode()) {
            case VANILLA -> "";
            case ABC_A -> "a";
            case ABC_B -> "b";
            case ABC_C -> "c";
            case ADAPTIVE -> normalizeFont(lineAbcFont);
        };
    }

    /** 非法/缺失的字体标识统一兜底 A 型 */
    private static String normalizeFont(String font) {
        return "b".equals(font) || "c".equals(font) ? font : "a";
    }

    /** effFont（"a"/"b"/"c"）→ ABC 交通字体 Identifier 字符串；非法值兜底 A 型 */
    private static String fontId(String effFont) {
        return switch (effFont) {
            case "b" -> "yunbeiuc:traf_sign_font_b";
            case "c" -> "yunbeiuc:traf_sign_font_c";
            default -> "yunbeiuc:traf_sign_font_a";
        };
    }

    /** 用 Gson 序列化 JSON 段，文本内的引号/反斜杠自动转义；占位符 {字段} 保持原样待渲染时展开 */
    private static String wrapJson(String text, String fontId) {
        JsonObject obj = new JsonObject();
        obj.addProperty("text", text);
        obj.addProperty("font", fontId);
        return GSON.toJson(obj);
    }
}
