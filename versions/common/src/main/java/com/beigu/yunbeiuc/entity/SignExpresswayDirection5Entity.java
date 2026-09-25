package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayDirection5Entity extends CustomSignBlockEntity {
    private Expressway expressway1 = Expressway.NATIONAL;
    private String text1 = "";
    private String expresswayNumber1 = "";
    private String logoType1 = "national_logo_2";
    private SignCompassDirection direction1 = SignCompassDirection.SOUTH;

    public SignExpresswayDirection5Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_DIRECTION_5_ENTITY.get(), pos, state);
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.text1 = nbt.getString("text1");
        this.expresswayNumber1 = nbt.getString("expresswayNumber1");
        // 旧存档兼容：无 logoType1 键时按原逻辑（国/省道 × 编号位数）推导初始值
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1").replace("provicial", "provincial") : legacyLogoType1();
        this.direction1 = SignCompassDirection.fromName(nbt.getString("direction1"), SignCompassDirection.NORTH);
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("expresswayNumber1", this.expresswayNumber1);
        nbt.putString("logoType1", this.logoType1);
        nbt.putString("direction1", this.direction1.getName());
        super.saveAdditional(nbt);
    }

    /**
     * 按原 SignExpresswayDirection5EntityRenderer 的固定布局生成默认文本行（同一实体/渲染器服务
     * SIGN_EXPRESSWAY_DIRECTION_5 与 SIGN_EXPRESSWAY_DIRECTION_6 两个方块，X 坐标镜像）：
     * renderExpresswayLogo(expressway1, ±4.5, 7, size 0.85) → -texture 行，纹理由 {logo1} 占位符选择（logoType1 四选一枚举字段）；
     * renderCenteredText(text1, ±4.5, -7, 0.05)；
     * renderExpresswayText(expresswayNumber1, ±4.5, 6.5, 0.06, zOffsetDelta 0.002)；
     * renderDirectionLogo(direction1, ∓8.5, 8.5, size 0.5) → -texture 行，纹理由 {direction1} 占位符（north/east/south/west）选择。
     * 原单位编号 +1px 自动 X 偏移为动态布局逻辑，默认行不含。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        // DIRECTION_6 为 DIRECTION_5 的镜像布局（logo/文本在左，方向箭头在右）
        boolean direction6 = getBlockState().getBlock() == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_6.get();
        float centerX = direction6 ? -4.5f : 4.5f;
        float directionLogoX = direction6 ? 8.5f : -8.5f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", centerX, 7f, 0.85f));
        lines.add(SignTextLinesHelper.centered("济南", centerX, -7f, 0.05f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centeredWithZ("G3", centerX, 6.5f, 0.06f, 0xFFFFFF, 0.002f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{direction1}.png", directionLogoX, 8.5f, 0.5f));
        setTextLines(lines);
    }

    public Expressway getExpressway1() { return expressway1; }
    public void setExpressway1(Expressway expressway1) {
        this.expressway1 = expressway1;
        markDirtyAndUpdate();
    }
    public String getText1() { return text1; }
    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public String getExpresswayNumber1() { return expresswayNumber1; }
    public void setExpresswayNumber1(String expresswayNumber1) {
        this.expresswayNumber1 = expresswayNumber1;
        markDirtyAndUpdate();
    }
    public String getLogoType1() { return logoType1; }
    public void setLogoType1(String logoType1) {
        this.logoType1 = logoType1;
        markDirtyAndUpdate();
    }
    public SignCompassDirection getDirection1() { return direction1; }
    public void setDirection1(SignCompassDirection direction1) {
        this.direction1 = direction1;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "expresswayNumber1" -> expresswayNumber1;
            // 对应原 renderDirectionLogo：方向箭头纹理名（north/east/south/west）
            case "direction1" -> direction1 == null ? null : direction1.getName();
            // 高速盾牌 logo 纹理：national/provincial × logo_1(宽)/logo_2(窄) 四选一枚举字段
            case "logo1" -> logoType1;
            default -> null;
        };
    }

    // 旧存档兼容推导：原 {logo1} 的派生逻辑——国道/省道 × 编号位数（1 位数字用窄版 logo_2，其余 logo_1）
    private String legacyLogoType1() {
        String kind = expressway1 == Expressway.PROVINCIAL ? "provincial" : "national";
        return kind + "_logo_" + (narrowLogo() ? "2" : "1");
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1" -> List.of(new FieldOptionGroup("高速Logo", "logoType1", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logoType1),
                    opt("国家高速公路（1位数）", "national_logo_2", logoType1),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logoType1),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logoType1))));
            case "direction1" -> List.of(new FieldOptionGroup("方向", "direction1", List.of(
                    opt("北（N）", "north", direction1.getName()),
                    opt("南（S）", "south", direction1.getName()),
                    opt("西（W）", "west", direction1.getName()),
                    opt("东（E）", "east", direction1.getName()))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logoType1" -> setLogoType1(value);
            case "direction1" -> setDirection1(SignCompassDirection.fromName(value, SignCompassDirection.NORTH));
        }
    }

    // 原 renderExpresswayLogo：编号为 1 位数字时用窄版 logo_2，其余用 logo_1
    private boolean narrowLogo() {
        String digits = expresswayNumber1 == null ? "" : expresswayNumber1.replaceAll("[^0-9]", "");
        return expresswayNumber1 != null && expresswayNumber1.matches(".*\\d.*") && digits.length() == 1;
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), VersionServices.blocks().updateAll());
        }
    }

    public enum Expressway {
        NATIONAL("national"),
        PROVINCIAL("provincial");

        private final String name;
        Expressway(String name) { this.name = name; }
        public String getName() { return name; }
        public static Expressway fromName(String name) {
            for (Expressway dir : values()) {
                if (dir.name.equals(name)) return dir;
            }
            return NATIONAL;
        }
    }
}
