package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideConfirmation1Entity extends CustomSignBlockEntity {
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";
    private String length1 = "";
    private String length2 = "";
    private String length3 = "";

    public SignGuideConfirmation1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_CONFIRMATION_1_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.text1 = nbt.getString("text1");
        this.text2 = nbt.getString("text2");
        this.text3 = nbt.getString("text3");
        this.length1 = nbt.getString("length1");
        this.length2 = nbt.getString("length2");
        this.length3 = nbt.getString("length3");
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        nbt.putString("text2", this.text2);
        nbt.putString("text3", this.text3);
        nbt.putString("length1", this.length1);
        nbt.putString("length2", this.length2);
        nbt.putString("length3", this.length3);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        boolean main = blockStateForDefaults().getBlock() == SignBlocks.SIGN_GUIDE_CONFIRMATION_1.get();
        float textX = main ? -17f : -15f;
        float lengthX = main ? 13f : 11f;
        float unitX = main ? 17f : 15f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.left("如皋", textX, 9f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("海安", textX, 0f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.left("盐城", textX, -9f, 0.04f, 0xFFFFFF, "a"));
        lines.add(SignTextLinesHelper.right("5", lengthX, 9f, 0.04f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("40", lengthX, 0f, 0.04f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("105", lengthX, -9f, 0.04f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("km", unitX, 8.5f, 0.025f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("km", unitX, -0.5f, 0.025f, 0xFFFFFF, "b"));
        lines.add(SignTextLinesHelper.right("km", unitX, -9.5f, 0.025f, 0xFFFFFF, "b"));
        setTextLines(lines);
    }

    public String getText1() {
        return text1;
    }
    public String getText2() {
        return text2;
    }
    public String getText3() {
        return text3;
    }
    public String getLength1() {
        return length1;
    }
    public String getLength2() {
        return length2;
    }
    public String getLength3() {
        return length3;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }
    public void setText2(String text2) {
        this.text2 = text2;
        markDirtyAndUpdate();
    }
    public void setText3(String text3) {
        this.text3 = text3;
        markDirtyAndUpdate();
    }
    public void setLength1(String length1) {
        this.length1 = length1;
        markDirtyAndUpdate();
    }
    public void setLength2(String length2) {
        this.length2 = length2;
        markDirtyAndUpdate();
    }
    public void setLength3(String length3) {
        this.length3 = length3;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
            case "text2" -> text2;
            case "text3" -> text3;
            case "length1" -> length1;
            case "length2" -> length2;
            case "length3" -> length3;
            default -> null;
        };
    }

    private void markDirtyAndUpdate() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, blockStateForDefaults(), blockStateForDefaults(), VersionServices.blocks().updateAll());
        }
    }
}
