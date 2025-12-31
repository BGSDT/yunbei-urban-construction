package com.beigu.yunbeiuc.entity.custom;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.RoadRailingsIron;
import com.beigu.yunbeiuc.block.custom.data.RoadRailingsIronType;
import com.beigu.yunbeiuc.entity.RoadRailingsIronEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class RoadRailingsIronModel extends GeoModel<RoadRailingsIronEntity> {

    @Override
    public Identifier getModelResource(RoadRailingsIronEntity roadRailingsIronEntity) {
        if (roadRailingsIronEntity == null || roadRailingsIronEntity.getWorld() == null) {
            return new Identifier(YunbeiUrbanConstruction.MOD_ID, "geo/road_railings_iron_pole.geo.json");
        }

        BlockState state = roadRailingsIronEntity.getWorld().getBlockState(roadRailingsIronEntity.getPos());
        if (state.getBlock() instanceof RoadRailingsIron) {
            RoadRailingsIronType type = state.get(RoadRailingsIron.ROAD_RAILINGS_IRON);
            String geoName = type == null ? "road_railings_iron_pole" : type.asString();
            return new Identifier(YunbeiUrbanConstruction.MOD_ID, "geo/" + geoName + ".geo.json");
        }

        return new Identifier(YunbeiUrbanConstruction.MOD_ID, "geo/road_railings_iron_pole.geo.json");
    }

    @Override
    public Identifier getTextureResource(RoadRailingsIronEntity animatable) {
        return new Identifier(YunbeiUrbanConstruction.MOD_ID,"textures/block/road_railings_iron.png");
    }

    @Override
    public Identifier getAnimationResource(RoadRailingsIronEntity animatable) {
        return null;
    }
}
