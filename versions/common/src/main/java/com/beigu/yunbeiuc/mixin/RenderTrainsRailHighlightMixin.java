package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.item.custom.PhaseWand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 手持本模组魔杖（普通魔杖或链接魔杖）时让 MTR 3/YMTR 也开启轨道高亮：
// 注入 mtr.render.RenderTrains#isHoldingRailRelated。targets 用字符串 + remap=false，编译期不依赖 MTR。

@Pseudo
@Mixin(targets = "mtr.render.RenderTrains", remap = false)
public class RenderTrainsRailHighlightMixin {

    @Inject(method = "isHoldingRailRelated", at = @At("HEAD"), cancellable = true, require = 0)
    private static void yunbeiuc$railPhaseWandHighlight(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (PhaseWand.isHoldingRailWand(player)) {
            cir.setReturnValue(true);
        }
    }
}
