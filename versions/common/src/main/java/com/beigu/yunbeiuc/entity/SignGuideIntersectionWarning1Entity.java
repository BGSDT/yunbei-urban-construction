package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.block.SignBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SignGuideIntersectionWarning1Entity extends CustomSignBlockEntity {
    private String text1 = "";

    public SignGuideIntersectionWarning1Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_WARNING_1_ENTITY.get(), pos, state);
        ensureDefaultTextLines();
    }

    @Override
    public void loadCompat(CompoundTag nbt) {
        super.loadCompat(nbt);
        this.text1 = nbt.getString("text1");
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

    @Override
    public void saveAdditionalCompat(CompoundTag nbt) {
        nbt.putString("text1", this.text1);
        super.saveAdditionalCompat(nbt);
    }

    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        Block block = blockStateForDefaults().getBlock();
        List<TextLineData> lines = new ArrayList<>();
        if (block == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_1.get() || block == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_6.get()) {
            lines.add(SignTextLinesHelper.centered("南京路", 0f, 0f, 0.035f, 0xFFFFFF, "a"));
        } else if (block == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_2.get()) {
            lines.add(SignTextLinesHelper.centered("福宁路", -2.5f, 0f, 0.035f, 0xFFFFFF, "a"));
        } else if (block == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_3.get()) {
            lines.add(SignTextLinesHelper.centered("西土城路", 0f, 0f, 0.035f, 0xFFFFFF, "a"));
        } else if (block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_1.get() || block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_2.get() || block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_3.get()) {
            lines.add(SignTextLinesHelper.right("1", 5f, 0f, 0.045f, 0x275aa8, "b"));
            lines.add(SignTextLinesHelper.right("km", 9f, -0.5f, 0.03f, 0x275aa8, "b"));
        } else if (block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_4.get() || block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_5.get() || block == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_6.get()) {
            lines.add(SignTextLinesHelper.right("1", -0.5f, -4f, 0.045f, 0x275aa8, "b"));
            lines.add(SignTextLinesHelper.right("km", 3.5f, -4.5f, 0.03f, 0x275aa8, "b"));
        } else if (block == SignBlocks.SIGN_GUIDE_ODOMETER.get()) {
            lines.add(SignTextLinesHelper.centered("220", 0f, 3f, 0.055f, 0xFFFFFF, "b"));
        }
        setTextLines(lines);
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        markDirtyAndUpdate();
    }

    @Override
    public String getPlaceholderValue(String key) {
        return switch (key) {
            case "text1" -> text1;
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
