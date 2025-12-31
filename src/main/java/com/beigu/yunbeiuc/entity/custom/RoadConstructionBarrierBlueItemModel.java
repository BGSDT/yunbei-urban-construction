package com.beigu.yunbeiuc.entity.custom;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.item.custom.RoadConstructionBarrierBlueItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class RoadConstructionBarrierBlueItemModel extends GeoModel<RoadConstructionBarrierBlueItem> {

    @Override
    public Identifier getModelResource(RoadConstructionBarrierBlueItem animatable) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"geo/road_construction_barrier_blue.geo.json");
    }

    @Override
    public Identifier getTextureResource(RoadConstructionBarrierBlueItem animatable) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"textures/block/road_construction_barrier_blue.png");
    }

    @Override
    public Identifier getAnimationResource(RoadConstructionBarrierBlueItem animatable) {
        return null;
    }
}
