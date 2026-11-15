package com.beigu.yunbeiuc.api;

import com.beigu.yunbeiuc.api.ApiCompat;

import net.minecraft.client.renderer.MultiBufferSource;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// Small version-neutral wrappers for entity / interaction APIs that 26.x renamed.
public final class ApiCompat {
    private ApiCompat() {
    }

    public static Level level(Entity entity) { return entity.level(); }

    public static void message(Player player, Component text, boolean overlay) {
        player.sendSystemMessage(text);
    }

    public static InteractionResult sidedSuccess(boolean clientSide) {
        return clientSide ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    public static net.minecraft.network.chat.FontDescription font(com.beigu.yunbeiuc.api.resource.Id id) {
        return new net.minecraft.network.chat.FontDescription.Resource(
                com.beigu.yunbeiuc.api.resource.IdAdapter.mc(id));
    }

    public static net.minecraft.client.renderer.rendertype.RenderType text(net.minecraft.resources.Identifier id) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.text(id);
    }

    public static net.minecraft.client.renderer.rendertype.RenderType textSeeThrough(net.minecraft.resources.Identifier id) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.textSeeThrough(id);
    }

    public static net.minecraft.client.renderer.rendertype.RenderType entityCutout(net.minecraft.resources.Identifier id) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(id);
    }

    public static void enableBlend() { }

    public static void disableBlend() { }

    public static void defaultBlendFunc() { }

    public static void enableDepthTest() { }

    public static void disableDepthTest() { }

    public static void enableCull() { }

    public static void disableCull() { }

    public enum UseResult {
        SUCCESS,
        PASS,
        FAIL
    }

    public static net.minecraft.client.renderer.texture.DynamicTexture dynamicTexture(
            com.mojang.blaze3d.platform.NativeImage image) {
        return new net.minecraft.client.renderer.texture.DynamicTexture(() -> "yunbeiuc-dynamic", image);
    }

    public static boolean mouseClicked(net.minecraft.client.gui.components.events.GuiEventListener widget,
                                       double mouseX, double mouseY, int button) {
        return widget.mouseClicked(
                new net.minecraft.client.input.MouseButtonEvent(mouseX, mouseY,
                        new net.minecraft.client.input.MouseButtonInfo(button + 1, 0)), false);
    }

    public static void setPixel(com.mojang.blaze3d.platform.NativeImage image, int x, int y, int abgr) {
        image.setPixelABGR(x, y, abgr);
    }

    public static int maxBuildHeight(net.minecraft.world.level.LevelReader level) {
        return level.getMaxY();
    }

    public static net.minecraft.server.level.ServerLevel serverLevel(net.minecraft.server.level.ServerPlayer player) {
        return player.level();
    }

    public static net.minecraft.client.gui.Font font(
            net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context ctx) {
        return ctx.font();
    }

    public static net.minecraft.world.phys.Vec3 cameraPos(net.minecraft.client.Camera camera) {
        return camera.position();
    }

    public static void lineWidth(float width) {
    }

    public static int getPixel(com.mojang.blaze3d.platform.NativeImage image, int x, int y) {
        return image.getPixel(x, y);
    }

    public static net.minecraft.client.renderer.rendertype.RenderType lines() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.lines();
    }

    public static net.minecraft.client.gui.screens.Screen currentScreen(net.minecraft.client.Minecraft client) {
        return lastScreen;
    }

    public static net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource(net.minecraft.client.Minecraft client) {
        return new com.beigu.yunbeiuc.render.DeferredGeometry();
    }

    public static void openUri(String url) {
        com.mojang.blaze3d.Blaze3D.openUri(java.net.URI.create(url));
    }

    public static void endBatch(net.minecraft.client.renderer.MultiBufferSource buffers) {
    }

    public static float cameraYRot(net.minecraft.client.Camera camera) {
        return camera.yRot();
    }

    private static net.minecraft.client.gui.screens.Screen lastScreen;

    public static void setScreen(net.minecraft.client.Minecraft client,
                                 net.minecraft.client.gui.screens.Screen screen) {
        lastScreen = screen;
        client.setScreenAndShow(screen);
    }

    public static final net.minecraft.world.level.material.PushReaction PUSH_BLOCK =
            net.minecraft.world.level.material.PushReaction.IMMOVEABLE;
}
