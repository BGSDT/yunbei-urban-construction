package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SignExpresswayEntranceAdvance4Entity extends CustomSignBlockEntity {
    private String logo1 = "national_logo_1";
    private String dir1 = "south";
    private SignCompassDirection direction1 = SignCompassDirection.SOUTH;
    private Expressway expressway1 = Expressway.NATIONAL;
    private String expresswayNumber1 = "";

    public SignExpresswayEntranceAdvance4Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_4_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.logo1 = NbtCompat.getString(nbt, "logoType1");
        this.dir1 = NbtCompat.getString(nbt, "direction1");
        this.direction1 = SignCompassDirection.fromName(NbtCompat.getString(nbt, "direction1"), SignCompassDirection.EAST);
        this.expressway1 = Expressway.fromName(NbtCompat.getString(nbt, "expressway1"));
        this.logo1 = nbt.contains("logoType1") ? NbtCompat.getString(nbt, "logoType1").replace("provicial", "provincial") : legacyLogoType1();
        if (!CustomSignBlockEntity.hasTextLinesData(nbt) && !hasCustomizedTextLines()) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("direction1", this.direction1.getName());
        nbt.putString("expressway1", this.expressway1.getName());
        nbt.putString("logoType1", this.logo1);
        nbt.putString("direction1", this.dir1);
        super.saveAdditionalCompat(nbt);
    }

    private String legacyLogoType1() {
            boolean hasDigits = expresswayNumber1 != null && expresswayNumber1.matches(".*\\d.*");
            String digits = expresswayNumber1 == null ? "" : expresswayNumber1.replaceAll("[^0-9]", "");
            boolean oneDigit = hasDigits && digits.length() == 1;
            String kind = expressway1 == Expressway.PROVINCIAL ? "provincial" : "national";
            return kind + "_logo_" + (oneDigit ? "2" : "1");
        }

    public enum Expressway {
        NATIONAL("national"),
        PROVINCIAL("provincial");

        private final String name;

        Expressway(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Expressway fromName(String name) {
            for (Expressway dir : values()) {
                if (dir.name.equals(name)) {
                    return dir;
                }
            }
            return NATIONAL;
        }
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{logo1}.png", -3f, 7f, 0.65f));
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/sign_expressway_{dir1}.png", 6f, 7f, 0.4f));
        lines.add(SignTextLinesHelper.centered("梅州方向", 0f, -2f, 0.035f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.centeredWithZ("G25", -3f, 6.5f, 0.045f, 0xFFFFFF, 0.002f, "b"));
        setTextLines(lines);
    }

    public String getLogo1() {
        return logo1;
    }

    public void setLogo1(String logo1) {
        this.logo1 = logo1;
        markDirtyAndUpdate();
    }

    public String getDir1() {
        return dir1;
    }

    public void setDir1(String dir1) {
        this.dir1 = dir1;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "logo1" -> logo1;
            case "dir1" -> dir1;
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo1" -> List.of(new FieldOptionGroup("高速Logo", "logo1", List.of(
                    opt("国家高速公路（2位数）", "national_logo_1", logo1),
                    opt("国家高速公路（1位数）", "national_logo_2", logo1),
                    opt("省级高速公路（2位数）", "provincial_logo_1", logo1),
                    opt("省级高速公路（1位数）", "provincial_logo_2", logo1))));
            case "dir1" -> List.of(new FieldOptionGroup("方向", "dir1", List.of(
                    opt("北（N）", "north", dir1),
                    opt("南（S）", "south", dir1),
                    opt("西（W）", "west", dir1),
                    opt("东（E）", "east", dir1))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "logo1" -> setLogo1(value);
            case "dir1" -> setDir1(value);
            default -> { }
        }
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }
}
