package com.beigu.yunbeiuc.api.block;

import com.mojang.datafixers.types.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.BiFunction;

/**
 * BlockEntityType.Builder.of 的形参类型 BlockEntityType.BlockEntitySupplier 在官方 mappings 中是
 * private（只有 Forge 补丁会把它公开），因此编译期无法传入实现。这里一并按“签名”反射查找，
 * 不依赖任何方法名字符串，从而不受各加载器符号重映射影响；且只在注册阶段调用一次。
 */
final class BlockEntityTypeFactory {
    private BlockEntityTypeFactory() {}

    static <T extends BlockEntity> BlockEntityType<T> create(BiFunction<BlockPos, BlockState, T> supplier, Block... blocks) {
        try {
            Class<?> supplierType = null;
            for (Class<?> nested : BlockEntityType.class.getDeclaredClasses()) {
                if (!nested.isInterface()) {
                    continue;
                }
                for (Method candidate : nested.getMethods()) {
                    Class<?>[] parameters = candidate.getParameterTypes();
                    if (parameters.length == 2 && parameters[0] == BlockPos.class
                            && parameters[1] == BlockState.class) {
                        supplierType = nested;
                        break;
                    }
                }
                if (supplierType != null) {
                    break;
                }
            }
            if (supplierType == null) {
                throw new IllegalStateException("BlockEntityType supplier interface not found");
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
            Class<?> builderClass = BlockEntityType.Builder.class;
            Method factoryMethod = null;
            for (Method candidate : builderClass.getDeclaredMethods()) {
                Class<?>[] parameters = candidate.getParameterTypes();
                if (parameters.length == 2 && parameters[0] == supplierType && parameters[1] == Block[].class) {
                    factoryMethod = candidate;
                    break;
                }
            }
            if (factoryMethod == null) {
                throw new IllegalStateException("BlockEntityType.Builder factory method not found");
            }
            factoryMethod.setAccessible(true);
            Object builder = factoryMethod.invoke(null, factory, (Object) blocks);
            Method build = null;
            for (Method candidate : builder.getClass().getDeclaredMethods()) {
                Class<?>[] parameters = candidate.getParameterTypes();
                if (parameters.length == 1 && parameters[0] == Type.class) {
                    build = candidate;
                    break;
                }
            }
            if (build == null) {
                throw new IllegalStateException("BlockEntityType.Builder#build not found");
            }
            build.setAccessible(true);
            @SuppressWarnings("unchecked")
            BlockEntityType<T> created = (BlockEntityType<T>) build.invoke(builder, new Object[]{null});
            return created;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to create block entity type", exception);
        }
    }
}
