# -*- coding: utf-8 -*-
import io, re, os

os.chdir(r'D:\yunbei-urban-construction\versions\1.20.1\common\src\main\java\com\beigu\yunbeiuc')

ENTITY_TPL = '''package com.beigu.yunbeiuc.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class @NAME@Entity extends CustomSignBlockEntity {
@FIELDS@

    public @NAME@Entity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.@CONST@.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
@READS@
        // 旧存档兼容：无 TextLines 键时按固定字段的默认布局生成动态文本行
        if (!nbt.contains("TextLines")) {
            ensureDefaultTextLines();
        }
    }

@DEFAULT_LINES@
@GETTERS_SETTERS@
    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
}
'''

RENDERER_TPL = '''package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.@BLOCK@;
import com.beigu.yunbeiuc.entity.@NAME@Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class @NAME@EntityRenderer extends AbstractTextDisplayEntityRenderer<@NAME@Entity> {

    public @NAME@EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, @NAME@Entity entity) {
        Direction facing = entity.getCachedState().get(@BLOCK@.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(@NAME@Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(@BLOCK@.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
'''

def field(n): return '    private String %s = "";' % n
def read(n): return '        this.%s = nbt.getString("%s");' % (n, n)

def gs_str(n):
    cap = n[0].upper() + n[1:]
    return ('    public String get%s() { return %s; }\n'
            '    public void set%s(String %s) {\n'
            '        this.%s = %s;\n'
            '        markDirtyAndUpdate();\n'
            '    }\n') % (cap, n, cap, n, n, n)

def make_entity(name, const, fields, reads, default_lines_code, getters):
    content = ENTITY_TPL
    content = content.replace('@NAME@', name).replace('@CONST@', const)
    content = content.replace('@FIELDS@', '\n'.join(fields))
    content = content.replace('@READS@', '\n'.join(reads))
    content = content.replace('@DEFAULT_LINES@', default_lines_code)
    content = content.replace('@GETTERS_SETTERS@', '\n'.join(getters))
    io.open('entity/%sEntity.java' % name, 'w', encoding='utf-8', newline='\n').write(content)

def make_renderer(name, block):
    content = RENDERER_TPL.replace('@NAME@', name).replace('@BLOCK@', block)
    io.open('render/%sEntityRenderer.java' % name, 'w', encoding='utf-8', newline='\n').write(content)

def patch_dispatch(block_file, screen_name):
    p = 'block/custom/sign/%s.java' % block_file
    src = io.open(p, encoding='utf-8').read()
    old_call = 'MinecraftClient.getInstance().setScreen(new %sScreen(pos));' % screen_name
    new_call = ('BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(pos);\n'
                '        if (blockEntity instanceof CustomSignBlockEntity signEntity) {\n'
                '            MinecraftClient.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TextDisplayScreen(signEntity));\n'
                '        }')
    assert old_call in src, 'dispatch not found in %s' % p
    src = src.replace(old_call, new_call)
    src = re.sub(r'import com\.beigu\.yunbeiuc\.screen\.%sScreen;\n' % screen_name,
                 'import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;\n', src)
    io.open(p, 'w', encoding='utf-8', newline='\n').write(src)

T_HELPER = '    private static String t(String s) { return s == null ? "" : s; }\n'

# ---- 1. SignExpresswayRoadName ----
make_entity('SignExpresswayRoadName', 'SIGN_EXPRESSWAY_ROAD_NAME_ENTITY', [field('text1')], [read('text1')],
'''    /**
     * 按原 SignExpresswayRoadNameEntityRenderer 的固定布局生成默认文本行：
     * renderCenteredText(text1, 0, 0, 0.045, 0xFFFFFF)
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered(text1 == null ? "" : text1, 0f, 0f, 0.045f, 0xFFFFFF));
        setTextLines(lines);
    }
''', [gs_str('text1')])
make_renderer('SignExpresswayRoadName', 'SignExpresswayRoadName')
patch_dispatch('SignExpresswayRoadName', 'SignExpresswayRoadName')

# ---- 2. SignExpresswayDirection1（x 按方块分支）----
make_entity('SignExpresswayDirection1', 'SIGN_EXPRESSWAY_DIRECTION_1_ENTITY', [field('text1')], [read('text1')],
'''    /**
     * 按原 SignExpresswayDirection1EntityRenderer 的固定布局生成默认文本行：
     * renderCenteredText(text1, ±4.5, 0, 0.05, 0xFFFFFF)，x 按方块（DIRECTION_1→4.5 / DIRECTION_2→-4.5）
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        float x = getCachedState().getBlock() == com.beigu.yunbeiuc.block.SignBlocks.SIGN_EXPRESSWAY_DIRECTION_2.get() ? -4.5f : 4.5f;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered(text1 == null ? "" : text1, x, 0f, 0.05f, 0xFFFFFF));
        setTextLines(lines);
    }
''', [gs_str('text1')])
make_renderer('SignExpresswayDirection1', 'SignExpresswayDirection1')
patch_dispatch('SignExpresswayDirection1', 'SignExpresswayDirection1')

# ---- 3. SignExpresswayEntranceAdvance7 ----
make_entity('SignExpresswayEntranceAdvance7', 'SIGN_EXPRESSWAY_ENTRANCE_ADVANCE_7_ENTITY',
    [field('text1'), field('text2'), field('text3')],
    [read('text1'), read('text2'), read('text3')],
'''    /**
     * 按原 SignExpresswayEntranceAdvance7EntityRenderer 的固定布局生成默认文本行
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered(t(text1), 0f, 8f, 0.035f, 0x2D9B47));
        lines.add(SignTextLinesHelper.centered(t(text2), -7f, -2f, 0.035f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered(t(text3), 7f, -2f, 0.035f, 0xFFFFFF));
        setTextLines(lines);
    }

''' + T_HELPER, [gs_str('text1'), gs_str('text2'), gs_str('text3')])
make_renderer('SignExpresswayEntranceAdvance7', 'SignExpresswayEntranceAdvance7')
patch_dispatch('SignExpresswayEntranceAdvance7', 'SignExpresswayEntranceAdvance7')

# ---- 4. SignExpresswayDistanceFromLocation3 ----
make_entity('SignExpresswayDistanceFromLocation3', 'SIGN_EXPRESSWAY_DISTANCE_FROM_LOCATION_3_ENTITY',
    [field('text1'), field('text2')],
    [read('text1'), read('text2')],
'''    /**
     * 按原 SignExpresswayDistanceFromLocation3EntityRenderer 的固定布局生成默认文本行：
     * 第二行为 text2 + "个出口"，保持默认显示一致
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered(t(text1), 0f, 5.5f, 0.045f, 0xFFFFFF));
        lines.add(SignTextLinesHelper.centered(t(text2) + "个出口", 0f, -5.5f, 0.045f, 0xFFFFFF));
        setTextLines(lines);
    }

''' + T_HELPER, [gs_str('text1'), gs_str('text2')])
make_renderer('SignExpresswayDistanceFromLocation3', 'SignExpresswayDistanceFromLocation3')
patch_dispatch('SignExpresswayDistanceFromLocation3', 'SignExpresswayDistanceFromLocation3')

# ---- 5. ZonesBoardTimeRange1 ----
make_entity('ZonesBoardTimeRange1', 'ZONES_BOARD_TIME_RANGE_1_ENTITY',
    [field('time1'), field('time2')],
    [read('time1'), read('time2')],
'''    /**
     * 按原 ZonesBoardTimeRange1EntityRenderer 的固定布局生成默认文本行：
     * renderCenteredText(time1 + "-" + time2, 0, 5.5, 0.02, 0x000000)
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered(t(time1) + "-" + t(time2), 0f, 5.5f, 0.02f, 0x000000));
        setTextLines(lines);
    }

''' + T_HELPER, [gs_str('time1'), gs_str('time2')])
make_renderer('ZonesBoardTimeRange1', 'ZonesBoardTimeRange1')
patch_dispatch('ZonesBoardTimeRange1', 'ZonesBoardTimeRange1')

# ---- 6. ZonesBoardTimeRange2 ----
make_entity('ZonesBoardTimeRange2', 'ZONES_BOARD_TIME_RANGE_2_ENTITY',
    [field('time1'), field('time2'), field('time3'), field('time4')],
    [read('time1'), read('time2'), read('time3'), read('time4')],
'''    /**
     * 按原 ZonesBoardTimeRange2EntityRenderer 的固定布局生成默认文本行
     */
    private void ensureDefaultTextLines() {
        if (!getTextLines().isEmpty()) return;
        List<TextLineData> lines = new ArrayList<>();
        lines.add(SignTextLinesHelper.centered(t(time1) + "-" + t(time2), 0f, 6f, 0.02f, 0x000000));
        lines.add(SignTextLinesHelper.centered(t(time3) + "-" + t(time4), 0f, 3f, 0.02f, 0x000000));
        setTextLines(lines);
    }

''' + T_HELPER, [gs_str('time1'), gs_str('time2'), gs_str('time3'), gs_str('time4')])
make_renderer('ZonesBoardTimeRange2', 'ZonesBoardTimeRange2')
patch_dispatch('ZonesBoardTimeRange2', 'ZonesBoardTimeRange2')

print('batch 2 done: 6 entities + 6 renderers + 6 dispatches')
