package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.item.custom.PhaseWand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 手持本模组魔杖时让 MTR 4 也开启轨道高亮：注入 RenderRails#isHoldingRailRelated。

@Pseudo
@Mixin(targets = {"org.mtr.render.RenderRails", "org.mtr.mod.render.RenderRails"}, remap = false)
public class RenderRailsRailHighlightMixin {

    @Inject(method = "isHoldingRailRelated", at = @At("HEAD"), cancellable = true, require = 0)
    private static void yunbeiuc$railPhaseWandHighlight(@Coerce Object clientPlayer,
                                                        CallbackInfoReturnable<Boolean> cir) {
        if (PhaseWand.isHoldingWandCached()) {
            cir.setReturnValue(true);
        }
    }
}
