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
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.expressway1 = Expressway.fromName(nbt.getString("expressway1"));
        this.text1 = nbt.getString("text1");
        this.expresswayNumber1 = nbt.getString("expresswayNumber1");
        this.logoType1 = nbt.contains("logoType1") ? nbt.getString("logoType1").replace("provicial", "provincial") : legacyLogoType1();
        this.direction1 = SignCompassDirection.fromName(nbt.getString("direction1"), SignCompassDirection.NORTH);
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("text1", this.text1);
        nbt.putString("expresswayNumber1", this.expresswayNumber1);
        nbt.putString("logoType1", this.logoType1);
        nbt.putString("direction1", this.direction1.getName());
        super.saveAdditionalCompat(nbt);
    }


    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean direction6 = blockStateForDefaults().getBlock() == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_6.get();
        float centerX = direction6 ? -4.5f : 4.5f;
        float directionLogoX = direction6 ? 8.5f : -8.5f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", centerX, 7f, 0.85f));
        lines.add(SignTextLinesHelper.centered("济南", centerX, -7f, 0.05f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.centeredWithZ("G3", centerX, 6.5f, 0.06f, 0xFFFFFF, 0.002f, "b"));
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
            case "direction1" -> direction1 == null ? null : direction1.getName();
            case "logo1" -> logoType1;
            default -> null;
        };
    }

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

    private boolean narrowLogo() {
        String digits = expresswayNumber1 == null ? "" : expresswayNumber1.replaceAll("[^0-9]", "");
        return expresswayNumber1 != null && expresswayNumber1.matches(".*\\d.*") && digits.length() == 1;
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
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
