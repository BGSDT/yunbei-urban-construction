package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.item.custom.PhaseWand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 手持本模组魔杖时让 MTR 4 也开启轨道高亮：注入 RenderRails#isHoldingRailRelated。
// 类名两版不同（4.0.x org.mtr.mod.render / 4.1 org.mtr.render），参数类型编译期不可见 → 用 @Coerce，
// 两个 targets 都列上，由 YunbeiucMixinPlugin 按「类是否真的存在」筛掉不存在的那个。

@Mixin(targets = {"org.mtr.render.RenderRails", "org.mtr.mod.render.RenderRails"}, remap = false)
public class RenderRailsRailHighlightMixin {

    @Inject(method = "isHoldingRailRelated", at = @At("HEAD"), cancellable = true, require = 0)
    private static void yunbeiuc$railPhaseWandHighlight(@Coerce Object clientPlayer,
                                                        CallbackInfoReturnable<Boolean> cir) {
        if (PhaseWand.isHoldingWandCached()) {
            PhaseWand.noteWandHighlight();
            cir.setReturnValue(true);
        }
    }
}
