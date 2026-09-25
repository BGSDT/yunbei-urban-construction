package dev.architectury.registry.registries;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/** 1.16.5 bridge for Architectury's pre-dev package name. */
public final class RegistrySupplier<T> implements Supplier<T> {
    private final me.shedaniel.architectury.registry.RegistrySupplier<T> delegate;

    RegistrySupplier(me.shedaniel.architectury.registry.RegistrySupplier<T> delegate) {
        this.delegate = delegate;
    }

    @Override public T get() { return delegate.get(); }
    public ResourceLocation getRegistryId() { return delegate.getRegistryId(); }
    public ResourceLocation getId() { return delegate.getId(); }
    public boolean isPresent() { return delegate.isPresent(); }
    public T getOrNull() { return delegate.getOrNull(); }
    public Optional<T> toOptional() { return delegate.toOptional(); }
    public void ifPresent(Consumer<? super T> consumer) { delegate.ifPresent(consumer); }
    public Stream<T> stream() { return delegate.stream(); }
    public T orElse(T value) { return delegate.orElse(value); }
    public T orElseGet(Supplier<? extends T> supplier) { return delegate.orElseGet(supplier); }
}
