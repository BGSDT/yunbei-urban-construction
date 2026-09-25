package dev.architectury.registry.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/** 1.16.5 bridge for Architectury's pre-dev package name. */
public final class DeferredRegister<T> implements Iterable<RegistrySupplier<T>> {
    private final me.shedaniel.architectury.registry.DeferredRegister<T> delegate;
    private final List<RegistrySupplier<T>> entries = new ArrayList<>();

    private DeferredRegister(me.shedaniel.architectury.registry.DeferredRegister<T> delegate) {
        this.delegate = delegate;
    }

    public static <T> DeferredRegister<T> create(String modId, ResourceKey<Registry<T>> key) {
        return new DeferredRegister<>(me.shedaniel.architectury.registry.DeferredRegister.create(modId, key));
    }

    public <R extends T> RegistrySupplier<R> register(String id, Supplier<? extends R> factory) {
        RegistrySupplier<R> entry = new RegistrySupplier<>(delegate.register(id, factory));
        entries.add(cast(entry));
        return entry;
    }

    public <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> factory) {
        RegistrySupplier<R> entry = new RegistrySupplier<>(delegate.register(id, factory));
        entries.add(cast(entry));
        return entry;
    }

    @SuppressWarnings("unchecked")
    private RegistrySupplier<T> cast(RegistrySupplier<? extends T> entry) {
        return (RegistrySupplier<T>) entry;
    }

    public void register() { delegate.register(); }
    @Override public Iterator<RegistrySupplier<T>> iterator() { return entries.iterator(); }
}
