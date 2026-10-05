#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# 一键发布：对 versions/ 下每个 MC 版本依次执行 publishModrinth
# （每个版本根上的该任务会把自己那套 fabric / forge / neoforge jar 上传到 Modrinth）
#
# 用法:
#   ./publish-all.sh                    发布全部版本
#   ./publish-all.sh 1.20.1 1.21.1      只发布指定版本
#   ./publish-all.sh -x 1.20.1          排除指定版本（可重复，也支持 -x 1.20.1,1.21.1）
#   ./publish-all.sh -s                 跳过已成功发布的版本（见 .publish-state）
#   ./publish-all.sh -r                 清空发布记录（.publish-state）后退出
#   ./publish-all.sh -k                 某个版本失败后继续发布其余版本
#   ./publish-all.sh -n                 干跑（只打印将执行的命令，不发布）
#   ./publish-all.sh -c                 发布前先执行 clean
#   ./publish-all.sh -l                 列出可发布版本 + 各自需要的 JDK + 发布记录
#   ./publish-all.sh -h                 显示帮助
#
# 发布记录:
#   每个版本发布成功后会往仓库根目录 .publish-state 追加一行 "<MC版本> <mod_version>"。
#   带 -s 时，mod_version 与记录一致的版本会被跳过；mod_version 变了（如 26w40b → 26w40c）
#   则视为未发布，仍会正常发布（记录不会因为改了版本号而误跳过新版本）。
#   典型用法：中途某个版本构建失败 → 修好后执行 ./publish-all.sh -s -k，
#   只会重跑失败 / 未发布的那些版本。
#
# 常用组合:
#   ./publish-all.sh -s -k              断点续发（跳过已发布，失败也继续）
#   ./publish-all.sh -s -k -x 1.20.1    跳过已发布，并额外排除 1.20.1
#
# 常驻排除（不用每次打 -x）:
#   仓库根目录 publish-skip.txt，一行一个 MC 版本（# 开头为注释，也支持逗号分隔），
#   每次运行都会自动排除里面列出的版本。
#   例：文件里写一行 1.16.5 → 以后每次发布都自动跳过 1.16.5；
#   想重新发布它，把那行删掉（或前面加 #）即可。
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
STATE_FILE="$SCRIPT_DIR/.publish-state"
SKIP_FILE="$SCRIPT_DIR/publish-skip.txt"

keep_going=0
dry_run=0
list_only=0
do_clean=0
skip_published=0
reset_state=0
wanted=()
excludes=()

usage() {
    cat <<'EOF'
用法: ./publish-all.sh [版本...] [-x 版本] [-s] [-r] [-k] [-n] [-c] [-l] [-h]

  版本...     只发布这些版本（默认全部）
  -x 版本     排除该版本（可重复，也支持 -x 1.20.1,1.21.1）
  -s          跳过已成功发布的版本（读 .publish-state，且 mod_version 一致才跳）
  -r          清空发布记录后退出
  -k          某个版本失败后继续发布其余版本
  -n          干跑，只打印将执行的命令
  -c          发布前先执行 clean
  -l          列出可发布版本 + 各自需要的 JDK + 发布记录
  -h          显示本帮助

常驻排除: 在仓库根目录 publish-skip.txt 里一行写一个版本（如 1.16.5），
          以后每次运行都会自动跳过它，不用每次打 -x。
断点续发: ./publish-all.sh -s -k

JDK 可用环境变量覆盖: JAVA_HOME_17 / JAVA_HOME_21 ...
EOF
}

while [ $# -gt 0 ]; do
    case "$1" in
        -x|--exclude)
            if [ $# -lt 2 ]; then
                echo "错误: $1 后面需要跟一个版本号，例如 -x 1.20.1" >&2
                exit 2
            fi
            shift
            IFS=',' read -r -a _ex <<< "$1"
            for _e in "${_ex[@]}"; do
                [ -n "$_e" ] && excludes+=("$_e")
            done
            ;;
        -s|--skip-published) skip_published=1 ;;
        -r|--reset-state)    reset_state=1 ;;
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

# ---- publish-skip.txt：常驻排除清单（一行一个版本，# 开头为注释，也支持逗号分隔） ----
if [ -f "$SKIP_FILE" ]; then
    while IFS= read -r _line || [ -n "$_line" ]; do
        _line="${_line%%#*}"                      # 去掉行内注释
        _line="$(printf '%s' "${_line%$'\r'}" | tr -d '[:space:]')"   # 去空白与 CR
        [ -n "$_line" ] || continue
        IFS=',' read -r -a _parts <<< "$_line"
        for _p in "${_parts[@]}"; do
            [ -n "$_p" ] && excludes+=("$_p")
        done
    done < "$SKIP_FILE"
fi

if [ "$reset_state" = 1 ]; then
    if [ -f "$STATE_FILE" ]; then
        rm -f "$STATE_FILE"
        echo "已清空发布记录: $STATE_FILE"
    else
        echo "发布记录不存在，无需清空: $STATE_FILE"
    fi
    exit 0
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

# ======================= 发布记录 =======================

# 某版本的 mod_version（读 gradle.properties，取不到则为空）
mod_version_of() {
    local gp="$VERSIONS_DIR/$1/gradle.properties"
    [ -f "$gp" ] || return 0
    sed -n 's/^[[:space:]]*mod_version[[:space:]]*=[[:space:]]*//p' "$gp" \
        | head -n 1 | tr -d '\r'
}

# 记录键: "<MC版本> <mod_version>"；mod_version 取不到时退化为 "<MC版本>"
state_key_of() {
    local mv
    mv="$(mod_version_of "$1")"
    if [ -n "$mv" ]; then printf '%s %s' "$1" "$mv"; else printf '%s' "$1"; fi
}

# 该版本是否已按「当前 mod_version」成功发布过
# 逐行比较并容忍 CRLF（.bat 写出的记录是 CRLF）
state_has() {
    [ -f "$STATE_FILE" ] || return 1
    local want line
    want="$(state_key_of "$1")"
    while IFS= read -r line; do
        [ "${line%$'\r'}" = "$want" ] && return 0
    done < "$STATE_FILE"
    return 1
}

# 记录一次成功发布（按 key 去重）
state_add() {
    state_has "$1" && return 0
    printf '%s\n' "$(state_key_of "$1")" >> "$STATE_FILE"
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
        printf '%-9s %-9s %-8s %s\n' "版本" "Java" "发布记录" "JDK"
        for v in "${versions[@]}"; do
            maj="$(required_major_of "$v")"
            jdk="$(resolve_jdk "$maj" 2>/dev/null)" || true
            mark="未发布"
            state_has "$v" && mark="已发布"
            printf '%-9s %-9s %-8s %s\n' "$v" "$maj" "$mark" "${jdk:-（未找到，将沿用当前 JAVA_HOME）}"
        done
        echo
        echo "发布记录文件: $STATE_FILE"
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

# ---- 排除指定版本（-x） ----
if [ ${#excludes[@]} -gt 0 ]; then
    kept=()
    for v in "${versions[@]}"; do
        hit=0
        for e in "${excludes[@]}"; do
            [ "$v" = "$e" ] && hit=1
        done
        if [ "$hit" = 1 ]; then
            echo "已排除: $v"
        else
            kept+=("$v")
        fi
    done
    if [ ${#kept[@]} -gt 0 ]; then versions=("${kept[@]}"); else versions=(); fi
fi

# ---- 跳过已成功发布的版本（-s） ----
skipped_published=()
if [ "$skip_published" = 1 ]; then
    kept=()
    for v in "${versions[@]}"; do
        if state_has "$v"; then
            echo "已发布，跳过: $v  （记录: $(state_key_of "$v")）"
            skipped_published+=("$v")
        else
            kept+=("$v")
        fi
    done
    if [ ${#kept[@]} -gt 0 ]; then versions=("${kept[@]}"); else versions=(); fi
fi

if [ ${#versions[@]} -eq 0 ]; then
    if [ ${#skipped_published[@]} -gt 0 ]; then
        echo "没有需要发布的版本：其余版本都已在 .publish-state 中记录（用 -r 清空记录，或去掉 -s）。"
        exit 0
    fi
    echo "没有可发布的版本（检查 $VERSIONS_DIR 下是否有含 gradlew 的目录）。" >&2
    exit 1
fi

gradle_args=()
[ "$do_clean" = 1 ] && gradle_args+=("clean")
gradle_args+=("$TASK")

echo
echo "将发布 ${#versions[@]} 个版本: ${versions[*]}"
[ ${#skipped_published[@]} -gt 0 ] && echo "（已跳过已发布: ${skipped_published[*]}）"
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
        state_add "$v"
        echo "     已记入 $(basename "$STATE_FILE")（$(state_key_of "$v")）"
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
    echo "修好后可直接续发: ./publish-all.sh -s -k（已成功发布的版本会自动跳过）" >&2
    exit 1
fi
echo "==================== 结果 ===================="
echo "全部版本发布完成。"
[ ${#skipped_published[@]} -gt 0 ] && echo "（已跳过已发布: ${skipped_published[*]}）"
