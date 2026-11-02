package com.beigu.yunbeiuc.api;

import com.beigu.yunbeiuc.api.ApiCompat;

import com.beigu.yunbeiuc.api.render.GeometrySink;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Small version-neutral wrappers for entity / interaction APIs that 26.x renamed.
 */
public final class ApiCompat {
    private ApiCompat() {
    }

    public static Level level(Entity entity) { return ApiCompat.level(entity); }

    public static void message(Player player, Component text, boolean overlay) {
        ApiCompat.message(player, text, overlay);
    }

    public static InteractionResult sidedSuccess(boolean clientSide) {
        return ApiCompat.sidedSuccess(clientSide);
    }

    public static net.minecraft.resources.ResourceLocation font(com.beigu.yunbeiuc.api.resource.Id id) {
        return com.beigu.yunbeiuc.api.resource.IdAdapter.mc(id);
    }

    public static net.minecraft.client.renderer.RenderType text(net.minecraft.resources.ResourceLocation id) {
        return net.minecraft.client.renderer.ApiCompat.text(id);
    }

    public static net.minecraft.client.renderer.RenderType textSeeThrough(net.minecraft.resources.ResourceLocation id) {
        return net.minecraft.client.renderer.ApiCompat.textSeeThrough(id);
    }

    public static net.minecraft.client.renderer.RenderType entityCutout(net.minecraft.resources.ResourceLocation id) {
        return net.minecraft.client.renderer.ApiCompat.entityCutout(id);
    }

    // 26.x folds blend/depth/cull state into the render pipeline, so these become no-ops there.
    public static void enableBlend() { com.mojang.blaze3d.systems.ApiCompat.enableBlend(); }

    public static void disableBlend() { com.mojang.blaze3d.systems.ApiCompat.disableBlend(); }

    public static void defaultBlendFunc() { com.mojang.blaze3d.systems.ApiCompat.defaultBlendFunc(); }

    public static void enableDepthTest() { com.mojang.blaze3d.systems.ApiCompat.enableDepthTest(); }

    public static void disableDepthTest() { com.mojang.blaze3d.systems.ApiCompat.disableDepthTest(); }

    public static void enableCull() { com.mojang.blaze3d.systems.ApiCompat.enableCull(); }

    public static void disableCull() { com.mojang.blaze3d.systems.ApiCompat.disableCull(); }

    /** Version-neutral result of an item use, mapped to the platform type in {@link #use}. */
    public enum UseResult {
        SUCCESS,
        PASS,
        FAIL
    }

    public static net.minecraft.client.renderer.texture.DynamicTexture dynamicTexture(
            com.mojang.blaze3d.platform.NativeImage image) {
        return new net.minecraft.client.renderer.texture.DynamicTexture(image);
    }

    public static boolean mouseClicked(net.minecraft.client.gui.components.events.GuiEventListener widget,
                                       double mouseX, double mouseY, int button) {
        return widget.mouseClicked(mouseX, mouseY, button);
    }

    public static void setPixel(com.mojang.blaze3d.platform.NativeImage image, int x, int y, int abgr) {
        image.setPixelRGBA(x, y, abgr);
    }

    public static int maxBuildHeight(net.minecraft.world.level.LevelReader level) {
        return level.getMaxBuildHeight();
    }

    public static net.minecraft.server.level.ServerLevel serverLevel(net.minecraft.server.level.ServerPlayer player) {
        return player.serverLevel();
    }

    public static net.minecraft.client.gui.Font font(
            net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context ctx) {
        return ctx.getFont();
    }

    public static net.minecraft.world.phys.Vec3 cameraPos(net.minecraft.client.Camera camera) {
        return camera.getPosition();
    }

    public static void lineWidth(float width) {
        com.mojang.blaze3d.systems.RenderSystem.lineWidth(width);
    }

    public static int getPixel(com.mojang.blaze3d.platform.NativeImage image, int x, int y) {
        return image.getPixelRGBA(x, y);
    }

    public static net.minecraft.client.renderer.RenderType lines() {
        return net.minecraft.client.renderer.RenderType.lines();
    }

    public static void endBatch(com.beigu.yunbeiuc.api.render.GeometrySink buffers) {
        if (buffers instanceof com.beigu.yunbeiuc.api.render.GeometrySink queued) {
            queued.endBatch();
        }
    }

    public static float cameraYRot(net.minecraft.client.Camera camera) {
        return camera.getYRot();
    }

    public static void setScreen(net.minecraft.client.Minecraft client,
                                 net.minecraft.client.gui.screens.Screen screen) {
        client.setScreen(screen);
    }

    public static final net.minecraft.world.level.material.PushReaction PUSH_BLOCK =
            net.minecraft.world.level.material.PushReaction.BLOCK;
}
