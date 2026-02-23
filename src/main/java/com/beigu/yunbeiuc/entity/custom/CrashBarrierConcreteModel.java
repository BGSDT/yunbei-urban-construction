package com.beigu.yunbeiuc.entity.custom;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.entity.CrashBarrierConcreteEntity;
import com.beigu.yunbeiuc.block.custom.CrashBarrierConcrete;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CrashBarrierConcreteModel extends GeoModel<CrashBarrierConcreteEntity> {

    @Override
    public Identifier getModelResource(CrashBarrierConcreteEntity crashBarrierConcreteEntity) {
        if (crashBarrierConcreteEntity == null || crashBarrierConcreteEntity.getWorld() == null) {
            return new Identifier(YunbeiUrbanConstruction.MOD_ID, "geo/crash_barrier_concrete_right.geo.json");
        }

        BlockState state = crashBarrierConcreteEntity.getWorld().getBlockState(crashBarrierConcreteEntity.getPos());
        if (state.getBlock() instanceof CrashBarrierConcrete) {
            CrashBarrierConcrete.CrashBarrierConcreteType type = state.get(CrashBarrierConcrete.CRASH_BARRIER_CONCRETE);
            String geoName = type == null ? "crash_barrier_concrete_right" : type.asString();
            return new Identifier(YunbeiUrbanConstruction.MOD_ID, "geo/" + geoName + ".geo.json");
        }

        return new Identifier(YunbeiUrbanConstruction.MOD_ID, "geo/crash_barrier_concrete_right.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrashBarrierConcreteEntity crashBarrierConcreteEntity) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"textures/block/crash_barrier_concrete.png");
    }

    @Override
    public Identifier getAnimationResource(CrashBarrierConcreteEntity crashBarrierConcreteEntity) {
        return null;
    }
}
