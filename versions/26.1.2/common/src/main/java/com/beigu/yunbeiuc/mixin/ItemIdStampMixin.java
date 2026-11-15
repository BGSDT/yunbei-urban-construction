package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.api.registry.RegistrationContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Minecraft 26.x requires Item.Properties to carry the registry id before construction; the shared registration helpers stamp it here for every item.
@Mixin(Item.class)
public abstract class ItemIdStampMixin {

    @Inject(method = "<init>", at = @At("HEAD"))
    private static void yunbeiuc$stampItemId(Item.Properties properties, CallbackInfo ci) {
        String name = RegistrationContext.currentItem();
        if (name == null || properties == null) {
            return;
        }
        try {
            properties.setId(ResourceKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(YunbeiUrbanConstruction.MOD_ID, name)));
        } catch (Throwable ignored) {
        }
    }
}
