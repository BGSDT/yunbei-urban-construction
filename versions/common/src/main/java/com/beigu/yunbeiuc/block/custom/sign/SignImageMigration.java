package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity.TextLineData;
import com.beigu.yunbeiuc.entity.SignTextLinesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;

import java.util.ArrayList;
import java.util.List;

public final class SignImageMigration {

    public static void ensure(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }
        ensure(level, pos, level.getBlockState(pos));
    }

    public static void ensure(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }
        if (!(state.getBlock() instanceof SignImageBlock block)) {
            return;
        }
        SignImageDefinitions.Def def = block.definition();
        if (!supports(def, state)) {
            return;
        }
        BlockEntity raw = VersionServices.blocks().ensureBlockEntity(level, pos);
        if (!(raw instanceof CustomSignBlockEntity be)) {
            return;
        }
        if (apply(be, def)) {
            be.notifyTextChanged();
        }
    }

    public static void migrate(ProtoChunk chunk) {
        if (chunk == null) {
            return;
        }
        LevelChunkSection[] sections = chunk.getSections();
        int minY = VersionServices.blocks().chunkMinY(chunk);
        int baseX = chunk.getPos().getMinBlockX();
        int baseZ = chunk.getPos().getMinBlockZ();
        for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = sections[i];
            if (section == null || VersionServices.blocks().isSectionEmpty(section)) {
                continue;
            }
            for (int ly = 0; ly < 16; ly++) {
                for (int lz = 0; lz < 16; lz++) {
                    for (int lx = 0; lx < 16; lx++) {
                        BlockState state = section.getBlockState(lx, ly, lz);
                        if (!(state.getBlock() instanceof SignImageBlock block)) {
                            continue;
                        }
                        SignImageDefinitions.Def def = block.definition();
                        if (!supports(def, state)) {
                            continue;
                        }
                        BlockPos pos = new BlockPos(baseX + lx, minY + i * 16 + ly, baseZ + lz);
                        BlockEntity existing = chunk.getBlockEntity(pos);
                        if (existing != null) {
                            if (existing instanceof CustomSignBlockEntity be && apply(be, def)) {
                                be.setChanged();
                            }
                            continue;
                        }
                        BlockEntity created = block.createBlockEntityCompat(pos, state);
                        if (!(created instanceof CustomSignBlockEntity be)) {
                            continue;
                        }
                        if (apply(be, def)) {
                            VersionServices.blocks().setProtoChunkBlockEntity(chunk, created);
                        }
                    }
                }
            }
        }
    }

    private static boolean supports(SignImageDefinitions.Def def, BlockState state) {
        return def != null && def.types().contains(state.getValue(CustomSignTypeBlock.TYPE));
    }

    private static boolean apply(CustomSignBlockEntity be, SignImageDefinitions.Def def) {
        if (be.hasCustomizedTextLines()) {
            return false;
        }
        List<TextLineData> current = be.getTextLines();
        if (!current.isEmpty()) {
            for (TextLineData line : current) {
                if (!line.isBuiltin()) {
                    return false;
                }
            }
            if (matches(current, def.lines())) {
                return false;
            }
            current.clear();
        }
        List<TextLineData> lines = new ArrayList<>();
        float z = 0f;
        for (SignImageDefinitions.Line line : def.lines()) {
            TextLineData data = create(line);
            data.setZOffset(z);
            z += 0.01f;
            lines.add(data);
        }
        be.getTextLines().addAll(lines);
        be.setChanged();
        return true;
    }

    private static TextLineData create(SignImageDefinitions.Line line) {
        if (line.isRect()) {
            return SignTextLinesHelper.rect(line.rectW(), line.rectH(), line.x(), line.y(), line.rotZ(), line.color());
        }
        if (line.isText()) {
            TextLineData data = SignTextLinesHelper.centered(line.value(), line.x(), line.y(), line.size(), line.color(), "a");
            data.setScaleX(line.scaleX());
            return data;
        }
        return SignTextLinesHelper.logo(line.value(), line.x(), line.y(), line.size(), line.rotZ());
    }

    private static boolean matches(List<TextLineData> current, List<SignImageDefinitions.Line> defs) {
        if (current.size() != defs.size()) {
            return false;
        }
        for (int i = 0; i < defs.size(); i++) {
            TextLineData line = current.get(i);
            TextLineData expected = create(defs.get(i));
            if (!line.getText().equals(expected.getText())) {
                return false;
            }
            if (Math.abs(line.getXOffset() - expected.getXOffset()) > 1e-3f
                    || Math.abs(line.getYOffset() - expected.getYOffset()) > 1e-3f
                    || Math.abs(line.getFontSize() - expected.getFontSize()) > 1e-4f
                    || Math.abs(line.getScaleX() - expected.getScaleX()) > 1e-3f
                    || Math.abs(line.getRotZ() - expected.getRotZ()) > 1e-3f) {
                return false;
            }
        }
        return true;
    }

    private SignImageMigration() {
    }
}
