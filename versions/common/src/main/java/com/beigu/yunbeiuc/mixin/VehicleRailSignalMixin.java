package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.signal.mtr.RailSignalHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 把红绿灯相位接入 MTR 4 的列车判定：注入 org.mtr.core.data.Vehicle#checkAndBlockSignal。
// targets 用字符串 + remap=false，编译期不依赖 MTR；未装 MTR 时由 YunbeiucMixinPlugin 关闭本 mixin。

@Mixin(targets = "org.mtr.core.data.Vehicle", remap = false)
public class VehicleRailSignalMixin {

    @Inject(method = "checkAndBlockSignal", at = @At("HEAD"), cancellable = true, require = 0)
    private void yunbeiuc$railSignalBlock(int index, @Coerce Object vehiclePositions,
                                          boolean reserve, boolean checkCurrentOnly,
                                          CallbackInfoReturnable<Boolean> cir) {
        if (RailSignalHook.isBlockedVehicle(this, index)) {
            cir.setReturnValue(true);
        }
    }
}
