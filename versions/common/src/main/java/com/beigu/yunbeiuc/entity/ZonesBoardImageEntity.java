package com.beigu.yunbeiuc.entity;

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
        // 新放置的方块实体不会走 readNbt，构造时即按原渲染代码布局生成默认文本行
        ensureDefaultTextLines();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.text1 = nbt.getString("text1");
        this.image = BoardImage.fromName(nbt.getString("image"));
        this.andX = nbt.getFloat("andX");
        this.andY = nbt.getFloat("andY");
        this.andScale = nbt.getFloat("andScale");
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("image", this.image.getName());
        nbt.putFloat("andX", this.andX);
        nbt.putFloat("andY", this.andY);
        nbt.putFloat("andScale", this.andScale);
        super.saveAdditional(nbt);
    }

    /**
     * 按原 ZonesBoardImageEntityRenderer 的固定布局生成默认文本行（先 logo 后文本，文本覆盖在 logo 之上）：
     * renderLogo(image, andX, 4 + andY, size)——纹理由 {logo} 占位符按 image 枚举选择，
     *             size = (EXPRESSWAY ? 1 : 0.75) + andScale，Z 为渲染器基準（POLE_L -1.75 / POLE_H -1.79 / NORMAL -1.43）；
     * renderBoardText(text1, andX, 4 + andY)——居中锚点，Z 比 logo 靠前 0.01（centeredWithZ 0.01f），
     *             scale = (EXPRESSWAY ? 0.02 : 0.03) + andScale * 0.02，
     *             颜色 RED → 0xFFFFFF、EXPRESSWAY → 0x2D9B47、其余 0x000000。
     * 原渲染器按 NBT 字段 andX/andY/andScale 动态布局，迁移后在生成默认行时取其当前值（新放置均为 0），
     * 之后行偏移/字号由 TextLines UI 直接编辑，字段保留用于旧存档读取与公共 API 兼容。
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean expressway = image == BoardImage.EXPRESSWAY;
        float logoSize = (expressway ? 1f : 0.75f) + andScale;
        float textScale = (expressway ? 0.02f : 0.03f) + andScale * 0.02f;
        int color = expressway ? 0x2D9B47 : (image == BoardImage.RED ? 0xFFFFFF : 0x000000);
        float x = andX;
        float y = 4f + andY;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.logo("yunbeiuc:textures/block/sign/{logo}.png", x, y, logoSize));
        lines.add(SignTextLinesHelper.centeredWithZ("{text1}", x, y, textScale, color, 0.01f));
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
            // 对应原 renderLogo：按 image 枚举选择纹理文件名段
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
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
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
