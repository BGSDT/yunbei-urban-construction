package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.item.custom.PhaseWand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让手持「魔杖」时也享受 MTR 的轨道高亮（可选集成，仅客户端）。
 *
 * <p>MTR 的 {@code RenderTrains#isHoldingRailRelated(Player)} 决定
 * {@code renderColors}，为 true 时轨道会改用 {@code rail_preview} 纹理、
 * 按 {@code railType.color} 着色，并额外画出信号机与单向箭头——也就是玩家眼里
 * 「轨道中间那条高亮线」。MTR 只对节点修改器 / 信号灯 / 平台等原版工具开启，
 * 因此这里把本模组的魔杖也算进去，玩家才能看清自己瞄准的是哪条轨道。
 *
 * <p>轨道几何本身由 {@code Rail#getPosition} 描述，服务端射线检测
 * （{@code RailRaycast}）用的是同一套中心线，所见即所得。
 *
 * <p>与 {@link TrainServerRailSignalMixin} 同样用 {@code targets} 字符串 +
 * {@code remap = false}，因此不需要在编译期依赖 MTR；未装 MTR 时由
 * {@link YunbeiucMixinPlugin} 让本 mixin 不被应用。
 */
@Mixin(targets = "mtr.render.RenderTrains", remap = false)
public class RenderTrainsRailHighlightMixin {

    @Inject(method = "isHoldingRailRelated", at = @At("HEAD"), cancellable = true, require = 0)
    private static void yunbeiuc$railPhaseWandHighlight(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        if (player.getMainHandItem().getItem() instanceof PhaseWand
                || player.getOffhandItem().getItem() instanceof PhaseWand) {
            PhaseWand.noteWandHighlight();
            cir.setReturnValue(true);
        }
    }
}
