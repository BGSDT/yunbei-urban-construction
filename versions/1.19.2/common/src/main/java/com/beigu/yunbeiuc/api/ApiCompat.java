package com.beigu.yunbeiuc.api;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// Small version-neutral wrappers for APIs that 26.x renamed.
public final class ApiCompat {
    private ApiCompat() {
    }

    public static Level level(Entity entity) { return entity.level; }

    public static void message(Player player, Component text, boolean overlay) {
        player.displayClientMessage(text, overlay);
    }

    public static InteractionResult sidedSuccess(boolean clientSide) {
        return InteractionResult.sidedSuccess(clientSide);
    }

    public static ResourceLocation font(com.beigu.yunbeiuc.api.resource.Id id) {
        return com.beigu.yunbeiuc.api.resource.IdAdapter.mc(id);
    }

    public static RenderType text(ResourceLocation id) {
        return RenderType.text(id);
    }

    public static RenderType textSeeThrough(ResourceLocation id) {
        return RenderType.textSeeThrough(id);
    }

    public static RenderType entityCutout(ResourceLocation id) {
        return RenderType.entityCutout(id);
    }

    public static void enableBlend() { com.mojang.blaze3d.systems.RenderSystem.enableBlend(); }

    public static void disableBlend() { com.mojang.blaze3d.systems.RenderSystem.disableBlend(); }

    public static void defaultBlendFunc() { com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc(); }

    public static void enableDepthTest() { com.mojang.blaze3d.systems.RenderSystem.enableDepthTest(); }

    public static void disableDepthTest() { com.mojang.blaze3d.systems.RenderSystem.disableDepthTest(); }

    public static void enableCull() { com.mojang.blaze3d.systems.RenderSystem.enableCull(); }

    public static void disableCull() { com.mojang.blaze3d.systems.RenderSystem.disableCull(); }

    public enum UseResult {
        SUCCESS,
        PASS,
        FAIL
    }

    public static DynamicTexture dynamicTexture(
            com.mojang.blaze3d.platform.NativeImage image) {
        return new DynamicTexture(image);
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
        return player.getLevel();
    }

    public static Font font(
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

    public static RenderType lines() {
        return RenderType.lines();
    }

    public static net.minecraft.client.gui.screens.Screen currentScreen(net.minecraft.client.Minecraft client) {
        return client.screen;
    }

    public static net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource(net.minecraft.client.Minecraft client) {
        return client.renderBuffers().bufferSource();
    }

    public static void openUri(String url) {
        net.minecraft.Util.getPlatform().openUri(url);
    }

    public static void endBatch(MultiBufferSource buffers) {
        if (buffers instanceof MultiBufferSource.BufferSource queued) {
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
