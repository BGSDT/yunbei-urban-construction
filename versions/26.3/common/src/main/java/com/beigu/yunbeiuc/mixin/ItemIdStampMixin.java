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

/**
 * Minecraft 26.x requires {@code Item.Properties} to carry the registry id before the item is
 * constructed, otherwise {@code Item.Properties.itemIdOrThrow()} throws {@code Item id not set}.
 *
 * <p>The shared registration helpers publish the registry name through
 * {@link RegistrationContext} while the deferred supplier runs, so the id can be stamped here for
 * every item the mod creates (custom items, plain items and {@code BlockItem}s alike).</p>
 */
@Mixin(Item.class)
public abstract class ItemIdStampMixin {

    // Must be static: the injection point sits before super() in Item's own constructor.
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
            // Never let id stamping break item construction; the vanilla error message is clearer.
        }
    }
}
