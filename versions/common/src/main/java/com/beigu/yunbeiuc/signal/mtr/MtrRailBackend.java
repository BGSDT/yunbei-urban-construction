package com.beigu.yunbeiuc.signal.mtr;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

// MTR 轨道几何的版本后端：遍历轨道表、按两端节点取轨道、取长度、沿中心线取点。
// 实现全走反射，编译期不依赖 MTR；未装对应版本时返回空/null，绝不抛异常。

public interface MtrRailBackend {

    interface RailVisitor {
        void accept(BlockPos start, BlockPos end, Object rail);
    }

    MtrVersion version();

    void forEachClientRail(RailVisitor visitor);

    Object findClientRail(BlockPos start, BlockPos end);

    double length(Object rail);

    Vec3 positionAt(Object rail, double distance);

    default boolean forEachServerRail(Level level, RailVisitor visitor) {
        return false;
    }

    static MtrRailBackend create() {
        switch (MtrVersion.detect()) {
            case MTR4:
                return new Mtr4RailBackend();
            case MTR3:
                return new Mtr3RailBackend();
            default:
                return null;
        }
    }
}
