package com.beigu.yunbeiuc.entity.custom;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.entity.RoadConstructionBarrierBlueEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class RoadConstructionBarrierBlueModel extends GeoModel<RoadConstructionBarrierBlueEntity> {
    @Override
    public Identifier getModelResource(RoadConstructionBarrierBlueEntity animatable) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"geo/road_construction_barrier_blue.geo.json");
    }

    @Override
    public Identifier getTextureResource(RoadConstructionBarrierBlueEntity animatable) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"textures/block/road_construction_barrier_blue.png");
    }

    @Override
    public Identifier getAnimationResource(RoadConstructionBarrierBlueEntity animatable) {
        return null;
    }
}
