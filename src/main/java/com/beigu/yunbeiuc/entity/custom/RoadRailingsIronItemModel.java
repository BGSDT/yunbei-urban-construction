package com.beigu.yunbeiuc.entity.custom;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.entity.RoadRailingsIronEntity;
import com.beigu.yunbeiuc.item.custom.RoadRailingsIronItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class RoadRailingsIronItemModel extends GeoModel<RoadRailingsIronItem> {

    @Override
    public Identifier getModelResource(RoadRailingsIronItem animatable) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"geo/road_railings_iron_pole.geo.json");
    }

    @Override
    public Identifier getTextureResource(RoadRailingsIronItem animatable) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"textures/block/road_railings_iron.png");
    }

    @Override
    public Identifier getAnimationResource(RoadRailingsIronItem animatable) {
        return null;
    }
}
