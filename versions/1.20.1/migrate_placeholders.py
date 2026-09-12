# -*- coding: utf-8 -*-
import io, os, re

os.chdir(r'D:\yunbei-urban-construction\versions\1.20.1\common\src\main\java\com\beigu\yunbeiuc\entity')

# (file, [(old_text_expr, new_text_expr)], placeholder_keys)
JOBS = [
    ('ZonesBoard1Entity.java',
     [('SignTextLinesHelper.centered(text1 == null ? "" : text1, 0f, 0f, 0.04f, color)',
       'SignTextLinesHelper.centered("{text1}", 0f, 0f, 0.04f, color)')],
     ['text1']),
    ('SignExpresswayRoadNameEntity.java',
     [('SignTextLinesHelper.centered(text1 == null ? "" : text1, 0f, 0f, 0.045f, 0xFFFFFF)',
       'SignTextLinesHelper.centered("{text1}", 0f, 0f, 0.045f, 0xFFFFFF)')],
     ['text1']),
    ('SignExpresswayDirection1Entity.java',
     [('SignTextLinesHelper.centered(text1 == null ? "" : text1, x, 0f, 0.05f, 0xFFFFFF)',
       'SignTextLinesHelper.centered("{text1}", x, 0f, 0.05f, 0xFFFFFF)')],
     ['text1']),
    ('SignExpresswayEntranceAdvance7Entity.java',
     [('SignTextLinesHelper.centered(t(text1), 0f, 8f, 0.035f, 0x2D9B47)',
       'SignTextLinesHelper.centered("{text1}", 0f, 8f, 0.035f, 0x2D9B47)'),
      ('SignTextLinesHelper.centered(t(text2), -7f, -2f, 0.035f, 0xFFFFFF)',
       'SignTextLinesHelper.centered("{text2}", -7f, -2f, 0.035f, 0xFFFFFF)'),
      ('SignTextLinesHelper.centered(t(text3), 7f, -2f, 0.035f, 0xFFFFFF)',
       'SignTextLinesHelper.centered("{text3}", 7f, -2f, 0.035f, 0xFFFFFF)')],
     ['text1', 'text2', 'text3']),
    ('SignExpresswayDistanceFromLocation3Entity.java',
     [('SignTextLinesHelper.centered(t(text1), 0f, 5.5f, 0.045f, 0xFFFFFF)',
       'SignTextLinesHelper.centered("{text1}", 0f, 5.5f, 0.045f, 0xFFFFFF)'),
      ('SignTextLinesHelper.centered(t(text2) + "个出口", 0f, -5.5f, 0.045f, 0xFFFFFF)',
       'SignTextLinesHelper.centered("{text2}个出口", 0f, -5.5f, 0.045f, 0xFFFFFF)')],
     ['text1', 'text2']),
    ('ZonesBoardTimeRange1Entity.java',
     [('SignTextLinesHelper.centered(t(time1) + "-" + t(time2), 0f, 5.5f, 0.02f, 0x000000)',
       'SignTextLinesHelper.centered("{time1}-{time2}", 0f, 5.5f, 0.02f, 0x000000)')],
     ['time1', 'time2']),
    ('ZonesBoardTimeRange2Entity.java',
     [('SignTextLinesHelper.centered(t(time1) + "-" + t(time2), 0f, 6f, 0.02f, 0x000000)',
       'SignTextLinesHelper.centered("{time1}-{time2}", 0f, 6f, 0.02f, 0x000000)'),
      ('SignTextLinesHelper.centered(t(time3) + "-" + t(time4), 0f, 3f, 0.02f, 0x000000)',
       'SignTextLinesHelper.centered("{time3}-{time4}", 0f, 3f, 0.02f, 0x000000)')],
     ['time1', 'time2', 'time3', 'time4']),
]

def placeholder_method(keys):
    cases = '\n'.join('            case "%s" -> %s;' % (k, k) for k in keys)
    return ('    @Override\n'
            '    public String getPlaceholderValue(String key) {\n'
            '        return switch (key) {\n'
            + cases + '\n'
            '            default -> null;\n'
            '        };\n'
            '    }\n\n')

for fname, replacements, keys in JOBS:
    src = io.open(fname, encoding='utf-8').read()
    changed = False
    for old, new in replacements:
        if old in src:
            src = src.replace(old, new)
            changed = True
        else:
            print('WARN: expr not found in %s: %s' % (fname, old[:60]))
    if 'getPlaceholderValue' not in src:
        anchor = '    private void markDirtyAndUpdate()'
        assert anchor in src, 'anchor not found in ' + fname
        src = src.replace(anchor, placeholder_method(keys) + anchor)
        changed = True
    if changed:
        io.open(fname, 'w', encoding='utf-8', newline='\n').write(src)
        print('patched', fname)

print('placeholder migration done')
