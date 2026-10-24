#!/usr/bin/env bash
# 修复 MTR 4.0.5 的 1.16.5 Fabric 包在开发环境下的重映射冲突。
#
# 现象：:fabric:runClient 启动即失败，日志里是
#   Mapping target name conflicts detected:
#   METHODs org/mtr/mapping/mapper/TickableSoundInstanceExtension/[isRelative, net/minecraft/class_1113/method_4787]()Z -> isRelative
#   There were unfixable conflicts.  /  Failed to remap mods!
#
# 原因：这个接口同时声明了 isRelative()（named 名）和 method_4787()（intermediary 名，default 桥接）。
# 开发环境要把 jar 从 intermediary 重映射到 named，两者都变成 isRelative()，TinyRemapper 无法修复。
# 正式游戏跑 intermediary，不需要重映射，所以只在 dev 环境出现。
#
# 处理：删掉 method_4787() 这个桥接方法。重映射后所有对它的引用都落到 isRelative()，语义不变。
# 只改本地 dev 用的 jar，不要拿去发布。
#
# 用法：./patch-mtr-jar.sh [jar 路径]
#   不带参数时默认处理 ../run/mods/MTR-fabric-4.0.5+1.16.5.jar

set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
JAR="${1:-$HERE/../run/mods/MTR-fabric-4.0.5+1.16.5.jar}"

if [ ! -f "$JAR" ]; then
    echo "找不到 jar: $JAR" >&2
    exit 1
fi

JDK="${JAVA_HOME:-C:/Program Files/Java/jdk-17}"

if "$JDK/bin/javap" -p -classpath "$JAR" org.mtr.mapping.mapper.TickableSoundInstanceExtension 2>/dev/null \
        | grep -q "method_4787"; then
    :
else
    echo "已经处理过，无需重复打补丁：$JAR"
    exit 0
fi

ASM="$(ls -1 "$HOME"/.gradle/caches/modules-2/files-2.1/org.ow2.asm/asm/*/*/asm-*.jar 2>/dev/null \
    | grep -v sources | grep -v javadoc | sort -V | tail -1)"
if [ -z "$ASM" ]; then
    echo "在 Gradle 缓存里找不到 ASM，请先跑一次任意 Gradle 构建" >&2
    exit 1
fi

ASM_WIN="$(cygpath -w "$ASM" 2>/dev/null || echo "$ASM")"
BUILD_DIR="$HERE/build"
mkdir -p "$BUILD_DIR"
BUILD_WIN="$(cygpath -w "$BUILD_DIR" 2>/dev/null || echo "$BUILD_DIR")"

"$JDK/bin/javac" -encoding UTF-8 -cp "$ASM_WIN" -d "$BUILD_WIN" "$HERE/StripBridgeMethod.java"

TMP_OUT="${JAR%.jar}.patched.jar"
"$JDK/bin/java" -cp "$ASM_WIN;$BUILD_WIN" StripBridgeMethod "$JAR" "$TMP_OUT" \
    org/mtr/mapping/mapper/TickableSoundInstanceExtension method_4787 "()Z"

mv "$TMP_OUT" "$JAR"
rm -rf "$BUILD_DIR"

# 让 Fabric Loader 忘掉旧的运行时重映射结果，否则可能用到之前失败/不完整的缓存
rm -rf "$(dirname "$JAR")/../.fabric"

echo "已修复：$JAR"
