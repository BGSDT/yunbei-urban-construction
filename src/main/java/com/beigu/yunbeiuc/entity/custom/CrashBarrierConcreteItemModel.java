package com.beigu.yunbeiuc.entity.custom;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.entity.CrashBarrierConcreteEntity;
import com.beigu.yunbeiuc.item.custom.CrashBarrierConcreteItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CrashBarrierConcreteItemModel extends GeoModel<CrashBarrierConcreteItem> {
    @Override
    public Identifier getModelResource(CrashBarrierConcreteItem animatable) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"geo/crash_barrier_concrete.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrashBarrierConcreteItem animatable) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"textures/block/crash_barrier_concrete.png");
    }

    @Override
    public Identifier getAnimationResource(CrashBarrierConcreteItem animatable) {
        return null;
    }
}
