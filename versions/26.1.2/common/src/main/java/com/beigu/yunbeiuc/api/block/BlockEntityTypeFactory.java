package com.beigu.yunbeiuc.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.function.BiFunction;

// Minecraft 26.x removed BlockEntityType.Builder; this factory calls the private
final class BlockEntityTypeFactory {
    private BlockEntityTypeFactory() {}

    @SuppressWarnings("unchecked")
    static <T extends BlockEntity> BlockEntityType<T> create(BiFunction<BlockPos, BlockState, T> supplier, Block... blocks) {
        try {
            Constructor<?> constructor = null;
            Class<?> supplierType = null;
            for (Constructor<?> candidate : BlockEntityType.class.getDeclaredConstructors()) {
                Class<?>[] parameters = candidate.getParameterTypes();
                if (parameters.length == 2 && parameters[0].isInterface() && parameters[1] == Set.class) {
                    constructor = candidate;
                    supplierType = parameters[0];
                    break;
                }
            }
            if (constructor == null || supplierType == null) {
                throw new IllegalStateException("BlockEntityType(BlockEntitySupplier, Set) constructor not found");
            }

            Object factory = Proxy.newProxyInstance(supplierType.getClassLoader(), new Class<?>[]{supplierType},
                    (proxy, method, arguments) -> {
                        if (method.getDeclaringClass() == Object.class) {
                            switch (method.getName()) {
                                case "toString": return "YunbeiUCBlockEntitySupplier";
                                case "hashCode": return System.identityHashCode(proxy);
                                case "equals": return proxy == arguments[0];
                                default: return null;
                            }
                        }
                        if (arguments != null && arguments.length == 2
                                && arguments[0] instanceof BlockPos && arguments[1] instanceof BlockState) {
                            return supplier.apply((BlockPos) arguments[0], (BlockState) arguments[1]);
                        }
                        return null;
                    });

            constructor.setAccessible(true);
            return (BlockEntityType<T>) constructor.newInstance(factory, Set.of(blocks));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to create block entity type", exception);
        }
    }
}
