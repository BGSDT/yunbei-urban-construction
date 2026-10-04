#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# 一键发布：对 versions/ 下每个 MC 版本依次执行 publishModrinth
# （每个版本根上的该任务会把自己那套 fabric / forge / neoforge jar 上传到 Modrinth）
#
# 用法:
#   ./publish-all.sh                    发布全部版本
#   ./publish-all.sh 1.20.1 1.21.1      只发布指定版本
#   ./publish-all.sh -k                 某个版本失败后继续发布其余版本
#   ./publish-all.sh -n                 干跑（只打印将执行的命令，不发布）
#   ./publish-all.sh -c                 发布前先执行 clean
#   ./publish-all.sh -l                 列出可发布版本 + 各自需要的 JDK
#   ./publish-all.sh -h                 显示帮助
#
# JDK:
#   脚本会读每个版本 build.gradle 里的 options.release 自动选 JDK
#   （1.16.5~1.20.1 → 17，1.21.1 → 21），并在常见安装目录里自动查找。
#   可用环境变量强制指定：JAVA_HOME_17 / JAVA_HOME_21 / ...
#
# 依赖: 仓库根目录 modrinth.properties 里的 token / project_id 配置正确
# ---------------------------------------------------------------------------
set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VERSIONS_DIR="$SCRIPT_DIR/versions"
TASK="publishModrinth"

keep_going=0
dry_run=0
list_only=0
do_clean=0
wanted=()

usage() {
    cat <<'EOF'
用法: ./publish-all.sh [版本...] [-k] [-n] [-c] [-l] [-h]

  版本...   只发布这些版本（默认全部）
  -k        某个版本失败后继续发布其余版本
  -n        干跑，只打印将执行的命令
  -c        发布前先执行 clean
  -l        列出可发布版本 + 各自需要的 JDK
  -h        显示本帮助

JDK 可用环境变量覆盖: JAVA_HOME_17 / JAVA_HOME_21 ...
EOF
}

while [ $# -gt 0 ]; do
    case "$1" in
        -k|--keep-going) keep_going=1 ;;
        -n|--dry-run)    dry_run=1 ;;
        -l|--list)       list_only=1 ;;
        -c|--clean)      do_clean=1 ;;
        -h|--help)       usage; exit 0 ;;
        -*)              echo "未知参数: $1（用 -h 查看帮助）" >&2; exit 2 ;;
        *)               wanted+=("$1") ;;
    esac
    shift
done

if [ ! -d "$VERSIONS_DIR" ]; then
    echo "找不到 versions 目录: $VERSIONS_DIR" >&2
    exit 1
fi

# ======================= JDK 选择 =======================

to_posix() { cygpath -u "$1" 2>/dev/null || printf '%s' "$1"; }

# 读 JDK 的 release 文件，输出主版本号（如 17 / 21）
java_major_of() {
    local home rel v
    [ -n "${1:-}" ] || return 1
    home="$(to_posix "$1")"
    rel="$home/release"
    [ -f "$rel" ] || return 1
    v="$(sed -n 's/^JAVA_VERSION="\([^"]*\)".*/\1/p' "$rel" | head -n 1)"
    [ -n "$v" ] || return 1
    case "$v" in
        1.*) printf '%s' "$v" | cut -d. -f2 ;;
        *)   printf '%s' "$v" | cut -d. -f1 ;;
    esac
}

# 某个版本需要的 Java 主版本：读 build.gradle 的 options.release，取不到默认 17
required_major_of() {
    local ver="$1"
    local bg="$VERSIONS_DIR/$ver/build.gradle"
    local m=""
    if [ -f "$bg" ]; then
        m="$(grep -o 'options\.release *= *[0-9]\+' "$bg" 2>/dev/null \
             | grep -o '[0-9]\+' | sort -n | tail -n 1)"
        if [ -z "$m" ]; then
            m="$(grep -o 'JavaVersion\.VERSION_[0-9]\+' "$bg" 2>/dev/null \
                 | grep -o '[0-9]\+' | sort -n | tail -n 1)"
        fi
    fi
    [ -n "$m" ] || m=17
    printf '%s' "$m"
}

# 常见 JDK 安装根目录（Windows 上遍历盘符，兼顾 C: 与 D: 等）
jdk_roots() {
    local L la
    for L in c d e f g h i j k l m n o p q r s t u v w x y z; do
        [ -d "/$L/Program Files/Java" ] && printf '%s\n' "/$L/Program Files/Java"
        [ -d "/$L/Program Files/Eclipse Adoptium" ] && printf '%s\n' "/$L/Program Files/Eclipse Adoptium"
    done
    [ -d "$HOME/.jdks" ] && printf '%s\n' "$HOME/.jdks"
    [ -d "$HOME/.gradle/jdks" ] && printf '%s\n' "$HOME/.gradle/jdks"
    if [ -n "${LOCALAPPDATA:-}" ]; then
        la="$(to_posix "$LOCALAPPDATA")/Programs/Eclipse Adoptium"
        [ -d "$la" ] && printf '%s\n' "$la"
    fi
    return 0
}

# 在常见根目录下找一个主版本匹配的 JDK，输出其 POSIX 路径
find_jdk_for() {
    local want="$1" root cand maj
    while IFS= read -r root; do
        [ -n "$root" ] || continue
        for cand in "$root"/*/; do
            [ -d "$cand" ] || continue
            if [ ! -x "$cand/bin/java" ] && [ ! -f "$cand/bin/java.exe" ]; then
                continue
            fi
            maj="$(java_major_of "$cand")" || continue
            if [ "$maj" = "$want" ]; then
                printf '%s' "${cand%/}"
                return 0
            fi
        done
    done < <(jdk_roots)
    return 1
}

declare -A JDK_CACHE=()

# 解析某主版本对应的 JAVA_HOME；找不到时回退到继承的 JAVA_HOME 并返回 1
resolve_jdk() {
    local want="$1" var cand home
    if [ -n "${JDK_CACHE[$want]+x}" ]; then
        printf '%s' "${JDK_CACHE[$want]}"
        [ -n "${JDK_CACHE[$want]}" ] && return 0 || return 1
    fi

    # 1) 环境变量 JAVA_HOME_<major>
    var="JAVA_HOME_${want}"
    cand="${!var:-}"
    if [ -n "$cand" ] && [ -d "$(to_posix "$cand")" ]; then
        cand="$(to_posix "$cand")"
        JDK_CACHE[$want]="$cand"; printf '%s' "$cand"; return 0
    fi

    # 2) JAVA_HOME 若主版本正好匹配
    if [ -n "${JAVA_HOME:-}" ] && [ "$(java_major_of "$JAVA_HOME" 2>/dev/null)" = "$want" ]; then
        home="$(to_posix "$JAVA_HOME")"
        JDK_CACHE[$want]="$home"; printf '%s' "$home"; return 0
    fi

    # 3) 自动查找
    home="$(find_jdk_for "$want")" || home=""
    if [ -n "$home" ]; then
        JDK_CACHE[$want]="$home"; printf '%s' "$home"; return 0
    fi

    # 4) 兜底：沿用当前 JAVA_HOME
    home="$(to_posix "${JAVA_HOME:-}")"
    JDK_CACHE[$want]="$home"
    printf '%s' "$home"
    return 1
}

run_gradle() {
    local dir="$1" jdk="$2"; shift 2
    if [ -n "$jdk" ]; then
        ( cd "$dir" && JAVA_HOME="$jdk" ./gradlew "$@" )
    else
        ( cd "$dir" && ./gradlew "$@" )
    fi
}

# ======================= 版本列表 =======================

versions=()
while IFS= read -r d; do
    [ -n "$d" ] || continue
    if [ -f "$d/gradlew" ]; then
        versions+=("$(basename "$d")")
    fi
done < <(find "$VERSIONS_DIR" -mindepth 1 -maxdepth 1 -type d 2>/dev/null | sort -V)

if [ "$list_only" = 1 ]; then
    if [ ${#versions[@]} -gt 0 ]; then
        for v in "${versions[@]}"; do
            maj="$(required_major_of "$v")"
            jdk="$(resolve_jdk "$maj" 2>/dev/null)" || true
            printf '%-8s Java %-3s %s\n' "$v" "$maj" "${jdk:-（未找到，将沿用当前 JAVA_HOME）}"
        done
    fi
    exit 0
fi

if [ ${#wanted[@]} -gt 0 ]; then
    selected=()
    for w in "${wanted[@]}"; do
        ok=0
        for v in "${versions[@]}"; do
            [ "$v" = "$w" ] && ok=1
        done
        if [ "$ok" = 1 ]; then
            selected+=("$w")
        else
            echo "跳过未知版本: $w" >&2
        fi
    done
    versions=("${selected[@]}")
fi

if [ ${#versions[@]} -eq 0 ]; then
    echo "没有可发布的版本（检查 $VERSIONS_DIR 下是否有含 gradlew 的目录）。" >&2
    exit 1
fi

gradle_args=()
[ "$do_clean" = 1 ] && gradle_args+=("clean")
gradle_args+=("$TASK")

echo
echo "将发布 ${#versions[@]} 个版本: ${versions[*]}"
[ "$do_clean" = 1 ] && echo "（发布前会先 clean）"
[ "$dry_run" = 1 ] && echo "（干跑：不会真正上传）"
echo

failed=()
for v in "${versions[@]}"; do
    dir="$VERSIONS_DIR/$v"
    maj="$(required_major_of "$v")"
    jdk="$(resolve_jdk "$maj")" || true

    echo "==================== $v ===================="
    if [ -n "$jdk" ]; then
        echo "JDK: $jdk   (需要 Java $maj)"
    else
        echo "[警告] 没找到 Java $maj 的 JDK，将沿用当前 JAVA_HOME；可设置 JAVA_HOME_${maj} 指定。" >&2
    fi

    if [ "$dry_run" = 1 ]; then
        echo "[dry-run] (cd \"$dir\" && JAVA_HOME=\"$jdk\" ./gradlew ${gradle_args[*]})"
        continue
    fi

    if run_gradle "$dir" "$jdk" "${gradle_args[@]}"; then
        echo "---- $v 完成 ----"
    else
        rc=$?
        echo "!!!! $v 失败 (exit=$rc) !!!!" >&2
        failed+=("$v")
        if [ "$keep_going" != 1 ]; then
            echo "[中止] 遇到失败即停止（加 -k 可继续）。" >&2
            break
        fi
    fi
done

echo
if [ ${#failed[@]} -gt 0 ]; then
    echo "==================== 结果 ===================="
    echo "失败: ${failed[*]}" >&2
    exit 1
fi
echo "==================== 结果 ===================="
echo "全部版本发布完成。"
