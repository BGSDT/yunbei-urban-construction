package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ZonesBoardImageEntity extends CustomSignBlockEntity {
    private String text1 = "";
    private BoardImage image = BoardImage.RED;
    private float andX;
    private float andY;
    private float andScale;

    public ZonesBoardImageEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONES_BOARD_IMAGE_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.text1 = nbt.getString("text1");
        this.image = BoardImage.fromName(nbt.getString("image"));
        this.andX = nbt.getFloat("andX");
        this.andY = nbt.getFloat("andY");
        this.andScale = nbt.getFloat("andScale");
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("image", this.image.getName());
        nbt.putFloat("andX", this.andX);
        nbt.putFloat("andY", this.andY);
        nbt.putFloat("andScale", this.andScale);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean expressway = image == BoardImage.EXPRESSWAY;
        float logoSize = (expressway ? 1f : 0.75f) + andScale;
        float textScale = (expressway ? 0.02f : 0.03f) + andScale * 0.02f;
        int color = expressway ? 0x2D9B47 : (image == BoardImage.RED ? 0xFFFFFF : 0x000000);
        float x = andX;
        float y = 4f + andY;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/{logo}.png", x, y, logoSize, 0f));
        lines.add(SignTextLinesHelper.centeredWithZ("{text1}", x, y, textScale, color, 0.01f, "a"));
        setTextLines(lines);
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }

    public BoardImage getImage() {
        return image;
    }

    public void setImage(BoardImage image) {
        this.image = image;
        markDirtyAndUpdate();
    }

    public float getAndX() {
        return andX;
    }

    public void setAndX(float andX) {
        this.andX = andX;
        markDirtyAndUpdate();
    }

    public float getAndY() {
        return andY;
    }

    public void setAndY(float andY) {
        this.andY = andY;
        markDirtyAndUpdate();
    }

    public float getAndScale() { return andScale; }
    public void setAndScale(float andScale) {
        this.andScale = andScale;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "logo" -> switch (image) {
                case RED -> "sign_red_number_logo";
                case YELLOW -> "sign_yellow_number_logo";
                case WHITE -> "sign_white_number_logo";
                case EXPRESSWAY -> "sign_expressway_logo";
            };
            default -> null;
        };
    }

    @Override
    public List<FieldOptionGroup> getFieldOptions(String placeholderKey) {
        return switch (placeholderKey) {
            case "logo" -> List.of(new FieldOptionGroup("底色", "image", List.of(
                    opt("国道", "red", image.getName()),
                    opt("省道", "yellow", image.getName()),
                    opt("县、乡道", "white", image.getName()),
                    opt("快速路", "expressway", image.getName()))));
            default -> null;
        };
    }

    @Override
    public void applyFieldOption(String field, String value) {
        switch (field) {
            case "image" -> setImage(BoardImage.fromName(value));
        }
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }

    public enum BoardImage {
        RED("red"),
        YELLOW("yellow"),
        WHITE("white"),
        EXPRESSWAY("expressway");

        private final String name;

        BoardImage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static BoardImage fromName(String name) {
            for (BoardImage img : values()) {
                if (img.name.equals(name)) {
                    return img;
                }
            }
            return RED;
        }
    }
}
