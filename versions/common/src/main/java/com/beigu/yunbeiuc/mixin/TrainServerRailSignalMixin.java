package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.signal.mtr.RailSignalHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 把红绿灯相位接入 MTR 的列车判定（可选集成）。
 *
 * <p>MTR 的 {@code TrainServer#isRailBlocked(int)} 返回 true 即表示前方受阻，
 * 列车会把停车点设到该段之前并制动。这里在方法头部插入本模组的判定：
 * 若前方轨道已被「轨道相位魔杖」绑定到红绿灯相位组、且该相位当前为红灯，
 * 则返回 true 拦停列车。
 *
 * <p>目标类用字符串 {@code targets} 指定、且 {@code remap = false}
 * （{@code TrainServer} 是 MTR 自己的类，不是 Minecraft 类，名字不参与映射），
 * 因此本类**不需要**在编译期依赖 MTR。
 *
 * <p>当未安装 MTR 时，由 {@link YunbeiucMixinPlugin} 让本 mixin 不被应用，
 * 从而不会因为找不到目标类而报错。
 */
@Mixin(targets = "mtr.data.TrainServer", remap = false)
public class TrainServerRailSignalMixin {

    @Inject(method = "isRailBlocked", at = @At("HEAD"), cancellable = true, require = 0)
    private void yunbeiuc$railSignalBlock(int checkIndex, CallbackInfoReturnable<Boolean> cir) {
        if (RailSignalHook.isBlocked(this, checkIndex)) {
            cir.setReturnValue(true);
        }
    }
}
