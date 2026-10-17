package com.beigu.yunbeiuc.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin 配置插件：让 MTR 相关的 mixin 只在 MTR 存在时才应用。
 *
 * <p>本模组对 MTR 是**可选**依赖。若未安装 MTR，直接应用
 * {@link TrainServerRailSignalMixin} 会因找不到目标类而报错，
 * 所以在这里按类名把该 mixin 关掉；其余 mixin 不受影响。
 *
 * <p>MTR 是否存在用<b>类路径资源查找</b>判断，避免依赖 Fabric / Forge / Architectury
 * 任一平台的加载器 API（插件加载时机很早），也避免加载任何类。
 */
public class YunbeiucMixinPlugin implements IMixinConfigPlugin {

    /** 懒检测并缓存：{@code null} = 还没判断过。 */
    private Boolean mtrPresent;

    @Override
    public void onLoad(String mixinPackage) {
        // 检测推迟到 shouldApplyMixin（此时模组 jar 一定已在类加载器上）。
    }

    /**
     * MTR 是否在类路径上。
     *
     * <p><b>这里只能做资源查找，绝对不能 {@code Class.forName} 目标类。</b>
     * {@code shouldApplyMixin} 是在「正在转换 mtr.data.TrainServer」的过程中被调用的，
     * 此刻再去加载同一个类会触发嵌套加载，把该类提前定义掉；等 Mixin 真正要注入时
     * 类已经加载完毕，注入就永远失效了——表现为「魔杖能绑轨道，但列车照样闯红灯」。
     * 资源查找不会定义任何类，因此没有这个副作用。
     *
     * <p>探测本身出错时**默认按「在场」处理**：误判为在场最多多一条 WARN 日志，
     * 误判为不在场却会让整个联动静默失效，两种代价不对称。
     */
    private boolean isMtrPresent() {
        if (mtrPresent == null) {
            boolean found = true;
            try {
                final ClassLoader loader = YunbeiucMixinPlugin.class.getClassLoader();
                if (loader != null
                        && loader.getResource("mtr/data/TrainServer.class") == null
                        && loader.getResource("mtr.mixins.json") == null) {
                    found = false;
                }
            } catch (Throwable t) {
                found = true;
            }
            mtrPresent = found;
        }
        return mtrPresent;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    /**
     * 只对「目标是 MTR 的类」的 mixin 做存在性把关，其余 mixin 一律照常应用。
     *
     * <p>按<b>目标类名</b>前缀判断（而不是按 mixin 类名），以后新增任何 MTR 集成 mixin
     * 都不需要再改这里。
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (targetClassName != null && targetClassName.startsWith("mtr.")) {
            return isMtrPresent();
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
