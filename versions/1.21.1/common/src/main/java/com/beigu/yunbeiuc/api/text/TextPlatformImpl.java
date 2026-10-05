package com.beigu.yunbeiuc.api.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class TextPlatformImpl implements TextPlatform {
    @Override
    public MutableComponent literal(String value) {
        return Component.literal(value);
    }

    @Override
    public MutableComponent translatable(String key, Object... arguments) {
        return Component.translatable(key, arguments);
    }

    @Override
    public MutableComponent empty() {
        return Component.empty();
    }
    @Override
    public net.minecraft.network.chat.Component fromLegacyJson(String json) {
        return ClientJson.parse(json);
    }

    /*
     * 客户端专属解析放在独立的嵌套类里：专用服务端只会加载 TextPlatformImpl 本身
     * （方块 / 物品注册会用到 Text.literal / Text.translatable），不会走到这里，
     * 于是也不会去链接 net.minecraft.client.Minecraft。
     */
    private static final class ClientJson {
        private static Component parse(String json) {
            net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
            net.minecraft.core.HolderLookup.Provider provider =
                    client.level != null ? client.level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
            return Component.Serializer.fromJsonLenient(json, provider);
        }
    }
}