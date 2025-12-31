package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.custom.CrashBarrierConcreteItemModel;
import com.beigu.yunbeiuc.entity.custom.RoadRailingsIronItemModel;
import com.beigu.yunbeiuc.item.custom.CrashBarrierConcreteItem;
import com.beigu.yunbeiuc.item.custom.RoadRailingsIronItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RoadRailingsIronItemRenderer extends GeoItemRenderer<RoadRailingsIronItem> {

    public RoadRailingsIronItemRenderer() {
        super(new RoadRailingsIronItemModel());
    }
}
