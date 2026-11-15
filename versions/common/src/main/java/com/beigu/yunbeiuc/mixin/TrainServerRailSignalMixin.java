package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.signal.mtr.RailSignalHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 把红绿灯相位接入 MTR 3/YMTR 的列车判定：注入 mtr.data.TrainServer#isRailBlocked。

@Pseudo
@Mixin(targets = "mtr.data.TrainServer", remap = false)
public class TrainServerRailSignalMixin {

    @Inject(method = "isRailBlocked", at = @At("HEAD"), cancellable = true, require = 0)
    private void yunbeiuc$railSignalBlock(int checkIndex, CallbackInfoReturnable<Boolean> cir) {
        if (RailSignalHook.isBlocked(this, checkIndex)) {
            cir.setReturnValue(true);
        }
    }
}
